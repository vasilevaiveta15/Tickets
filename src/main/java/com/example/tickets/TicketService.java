package com.example.tickets;

import com.example.tickets.bean.*;
import com.example.tickets.enums.CardType;
import com.example.tickets.enums.Role;
import com.example.tickets.enums.Travel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional
public class TicketService
{
  private       TicketDao             dao;
  private final BCryptPasswordEncoder passwordEncoder;


  @Autowired
  public TicketService(TicketDao dao, BCryptPasswordEncoder passwordEncoder)
  {
    this.dao = dao;
    this.passwordEncoder = passwordEncoder;
  }

  public void registration(Registration information)
  {
    UserRegistrationDetail detail = UserRegistrationDetail
        .builder()
        .firstName(information.getFirstName())
        .lastName(information.getLastName())
        .password(information.getPassword())
        .email(information.getEmail())
        .username(information.getUsername())
        .build();

    if (!information.getPassword().equalsIgnoreCase(information.getRepeatPassword())) {
      throw new IllegalArgumentException("The passwords doesn't match!");
    }

    detail.setPassword(passwordEncoder.encode(detail.getPassword()));

    checkForValidTypeOfCard(information.getTypeOfCard());
    detail.setTypeOfCard(CardType.valueOf(information.getTypeOfCard()));

    if (!(Role.ADMIN.toString().equals(information.getRole())
        && !Role.USER.toString().equals(information.getRole()))) {
      throw new IllegalArgumentException("Invalid role!");
    }

    detail.setRole(Role.valueOf(information.getRole()));

    dao.registration(detail);
  }

  public List<Town> loadAvailableDestinations()
  {
    return dao.loadAvailableDestinations(); //shte pokazvame samo vuzmojnite gradovete v koito vlaka shte putyva bez cenite im
  }

  public void addAvailableDestination(DestinationInfo destinationInfo)
  {

    dao.addAvailableDestination(destinationInfo);
  }


  public BigDecimal getPriceOfTicket(LocalTime diapasonTime, String townFrom,
                                     String townTo, String wayOfTrip, boolean isThereChild)
  {
    //check towns if exist in database
    boolean isExist = dao.loadTowns(townFrom, townTo);
    if (!isExist) {
      throw new IllegalArgumentException("There is no train for your trip to this town!");
    }

    if (!Travel.ONE_WAY_TRIP.toString().equals(wayOfTrip) && !Travel.ROUND_TRIP.toString().equals(wayOfTrip)) {
      throw new IllegalArgumentException("Invalid way of trip!");
    }


    String typeOfCard = getCurrentLoggedUser().getTypeOfCard().toString();

    BigDecimal priceOfTicket = dao.getPriceOfTicket(townFrom, townTo);
    priceOfTicket = calculationWayTrip(wayOfTrip, priceOfTicket);

    return getDiscountForTickets(priceOfTicket, diapasonTime, isThereChild, typeOfCard);
  }

  private BigDecimal calculationWayTrip(String wayOfTrip, BigDecimal priceOfTicket)
  {
    if (Travel.ROUND_TRIP.toString().equals(wayOfTrip)) {
      return priceOfTicket.multiply(BigDecimal.valueOf(2));
    }
    return priceOfTicket;
  }

  public void payTicket(Long ticketId)
  {
    dao.payTicket(ticketId);
  }

  public void reservation(LocalDateTime startDate, LocalDateTime endDate,
                          BigDecimal priceWithDiscount, Long trainId, int numberOfTickets)
  {
    Long userId = getCurrentLoggedUser().getId();
    for (int i = 0; i < numberOfTickets; i++) {
      dao.reservation(userId, startDate, endDate, priceWithDiscount, trainId);
    }
  }

  public List<Ticket> loadAllReservationsOfUser()
  {
    UserRegistrationDetail user = getCurrentLoggedUser();
    List<Date> dates = dao.getStartDate(user.getId());
    int compareValue;
    for (Date d : dates) {
      compareValue = d.getDateAfterSevenDays().compareTo(d.getStartDate());
      if (compareValue < 0) {
        dao.removeOldTicket(d.getId());
      }
    }
    return dao.loadAllReservationsOfUser(user.getId());
  }

  public void refactorReservation(Long idOfReservation, String day1, String month1, String day2, String month2)
  {
    dao.refactorReservation(idOfReservation, day1, month1, day2, month2);
  }

  public void removeTicketReservation(Long idOfReservation)
  {
    //check if the reservation if pay throw exception that can't remove the reservation
    String isReservationPay = dao.getTypeOfPayReservation(idOfReservation);
    if (null == isReservationPay) {
      throw new IllegalArgumentException("This reservation doesn't exist!");
    }

    if ("Y".equals(isReservationPay)) {
      throw new IllegalArgumentException("Sorry you can't remove the reservation, the ticket is already payed!");
    }
    dao.removeTicketReservation(idOfReservation); //da premahnem i ot join table
  }

  public List<UserInformation> loadUsers()
  {
    return dao.loadUsers();
  }

  public void refactorUserProfile(Long userId, String email)
  {
    //Ako potrebitelq e ADMIN ne moje da bude refactoriran
    String role = dao.getUser(userId);
    if (role.isEmpty()) {
      throw new IllegalArgumentException("Invalid user id!");
    }
    else if (Role.ADMIN.toString().equalsIgnoreCase(role)) {
      throw new IllegalArgumentException("Can not refactor ADMIN profile!");
    }
    dao.refactorUserProfile(userId, email);
  }

  private BigDecimal getDiscountForTickets(BigDecimal priceOfTicket, LocalTime diapasonTime, boolean isThereChild,
                                           String typeOfCard)
  {
    BigDecimal discount1 = BigDecimal.ZERO;
    //1 DISCOUNT
    //between 7:30 and 9:30 sutrin i 16:00 do 19:30 sledobed cenata ostava sushtata
    //mejdu 9 i 30 i 16:00 i sled 19:30 poluchavame 5% otstupka
    if (diapasonTime.isAfter(LocalTime.parse("09:35:00"))
        && diapasonTime.isBefore(LocalTime.parse("16:00:00"))
        || diapasonTime.isAfter(LocalTime.parse("19:30:00"))
    ) {

      discount1 = priceOfTicket.multiply(BigDecimal.valueOf(5)).divide(BigDecimal.valueOf(100), RoundingMode.CEILING);
    }


    //ako ima dete{
    //2 DISCOUNT
    //zavisimost ot vida karta poluchavame otstupka vinagi
    //proverka s enuma
    //ako imame semeina jelezoputna karta poluchavame na vseki bilet 50%
    // ako nqmame nikakva  poluchavame 10%
    // ako pritejavame karta za hora nad 60 godini poluchavame 34% otstupka
    // (tuk imame dete s nas) vuv vsichki sluchai
    BigDecimal discount2 = BigDecimal.ZERO;
    if (isThereChild) {
      if (CardType.FAMILY.toString().equals(typeOfCard)) {
        discount2 = priceOfTicket.multiply(BigDecimal.valueOf(50)).divide(BigDecimal.valueOf(100), RoundingMode.CEILING);
      }
      else if (CardType.ELDERLY.toString().equals(typeOfCard)) {
        discount2 = priceOfTicket.multiply(BigDecimal.valueOf(34)).divide(BigDecimal.valueOf(100), RoundingMode.CEILING);
      }
      else if (CardType.NONE.toString().equals(typeOfCard)) {
        discount2 = priceOfTicket.multiply(BigDecimal.valueOf(10)).divide(BigDecimal.valueOf(100), RoundingMode.CEILING);
      }
    } // + tazi za vuzrastniq chovek nad 60 karta dori da nqma dete s nas po uslovie s deteto e sled tova napisano
    else {
      if (CardType.ELDERLY.toString().equals(typeOfCard)) {
        discount2 = priceOfTicket.multiply(BigDecimal.valueOf(34)).divide(BigDecimal.valueOf(100), RoundingMode.CEILING);
      }
    }

    //ako nqma dete s nas ostava vuzmojnostta za discount samo ot hour diapazone ili tazi za vuzrastniq chovek nad 60 karta


    //sravnqvame discount ot  2te vuzmojni otstupki kato vzimame nai golqmata
    if (discount2.compareTo(discount1) >= 0) {
      return priceOfTicket.subtract(discount2);
    }
    return priceOfTicket.subtract(discount1);
  }


  private void checkForValidTypeOfCard(String typeOfCard)
  {
    if (!CardType.ELDERLY.toString().equalsIgnoreCase(typeOfCard) &&
        !CardType.FAMILY.toString().equalsIgnoreCase(typeOfCard) &&
        !CardType.NONE.toString().equalsIgnoreCase(typeOfCard)
    ) {
      throw new IllegalArgumentException("Invalid type of card. You choose from ELDERLY, FAMILY and NONE!");
    }
  }

  public UserRegistrationDetail getCurrentLoggedUser()
  {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String principalUsername = "";
    if (principal instanceof UserDetails) {
      principalUsername = ((UserDetails) principal).getUsername();
    }
    return dao.findByUsername(principalUsername).orElseThrow(() -> new IllegalArgumentException("Invalid username"));
  }

}
