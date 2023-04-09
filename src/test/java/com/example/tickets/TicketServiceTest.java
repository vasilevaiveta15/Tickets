package com.example.tickets;

import com.example.tickets.bean.Date;
import com.example.tickets.bean.DestinationInfo;
import com.example.tickets.bean.Registration;
import com.example.tickets.bean.UserRegistrationDetail;
import com.example.tickets.enums.CardType;
import com.example.tickets.enums.Role;
import com.example.tickets.enums.Travel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TicketServiceTest
{

  @Mock
  private TicketDao             ticketDao;
  @Mock
  private Authentication        authentication;
  @Mock
  private SecurityContext       securityContext;
  @Mock
  private BCryptPasswordEncoder passwordEncoder;

  private TicketService ticketService;

  private UserRegistrationDetail testUser1;
  private UserRegistrationDetail testUser2;
  private UserRegistrationDetail testUser3;


  @BeforeEach
  public void beforeClass()
  {
    testUser1 = UserRegistrationDetail
        .builder()
        .id(1L)
        .firstName("Ivan")
        .email("ivan1@gmail.com")
        .username("ivan76")
        .password("abc123ABC")
        .typeOfCard(CardType.ELDERLY)
        .build();
    testUser2 = UserRegistrationDetail
        .builder()
        .firstName("Ivan")
        .email("ivan2@gmail.com")
        .username("ivan76")
        .password("abc123ABC")
        .typeOfCard(CardType.FAMILY)
        .build();
    testUser3 = UserRegistrationDetail
        .builder()
        .firstName("Ivan")
        .email("ivan3@gmail.com")
        .username("ivan76")
        .password("abc123ABC")
        .typeOfCard(CardType.NONE)
        .build();
  }

  @BeforeEach
  public void setUp()
  {
//    initialize mocks before each method
    MockitoAnnotations.openMocks(this);
    ticketService = new TicketService(ticketDao, passwordEncoder);
    authentication = Mockito.mock(Authentication.class);
    securityContext = Mockito.mock(SecurityContext.class);
  }

  /**
   * Link to the Dao {@link TicketService#getPriceOfTicket}
   */
  @Test
  void getPriceOfTicket_TestCalculationWayTripMethod_WithRoundTrip()
  {
    mockUser1Authentication();

    when(ticketDao.loadTowns(anyString(), anyString()))
        .thenReturn(true);

    when(ticketDao.getPriceOfTicket(anyString(), anyString()))
        .thenReturn(BigDecimal.TEN);

    BigDecimal priceOfTicket = ticketService.getPriceOfTicket(LocalTime.parse("17:38:00"), "Sofia", "Varna",
        Travel.ROUND_TRIP.toString(), false);

    verify(ticketDao, times(1)).getPriceOfTicket("Sofia", "Varna");

    assertEquals(BigDecimal.valueOf(13), priceOfTicket);
  }

  /**
   * Link to the Dao {@link TicketService#getPriceOfTicket}
   */
  @Test
  void getPriceOfTicket_ThrowsIllegalArgumentException_IfTheTownsDontExistInDb()
  {
    IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class,
        () -> {
          mockUser1Authentication();

          when(ticketDao.loadTowns(anyString(), anyString()))
              .thenReturn(false);

          ticketService.getPriceOfTicket(LocalTime.now(), "Sofia", "Varna",
              Travel.ONE_WAY_TRIP.toString(), false);

          verify(ticketDao, times(1)).findByUsername(testUser1.getUsername());
        });
    assertEquals("There is no train for your trip to this town!", thrown.getMessage());

  }

  /**
   * Link to the Dao {@link TicketService#getPriceOfTicket}
   */
  @Test
  void getPriceOfTicket_ThrowsIllegalArgumentException_IfTheTravelTypeIsInvalid()
  {
    IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class,
        () -> {
          mockUser1Authentication();

          when(ticketDao.loadTowns(anyString(), anyString()))
              .thenReturn(true);

          ticketService.getPriceOfTicket(LocalTime.now(), "Sofia", "Varna",
              "TRIP_TEST", true);

          verify(ticketDao, times(1)).findByUsername(testUser1.getUsername());
        });
    assertEquals("Invalid way of trip!", thrown.getMessage());
  }

  /**
   * Link to the Dao {@link TicketService#getPriceOfTicket}
   */
  @Test
  void getPriceOfTicket_IfThereIsNoChildAndTheCardIsElderly()
  {
    mockUser1Authentication();

    when(ticketDao.loadTowns(anyString(), anyString()))
        .thenReturn(true);

    when(ticketDao.getPriceOfTicket(anyString(), anyString()))
        .thenReturn(BigDecimal.TEN);

    BigDecimal priceOfTicket = ticketService.getPriceOfTicket(LocalTime.parse("17:38:00"), "Sofia", "Varna",
        Travel.ONE_WAY_TRIP.toString(), false);

    verify(ticketDao, times(1)).getPriceOfTicket("Sofia", "Varna");

    assertEquals(BigDecimal.valueOf(6), priceOfTicket);
  }

  /**
   * Link to the Dao {@link TicketService#getPriceOfTicket}
   */
  @Test
  void getPriceOfTicket_IfThereIsChildAndTheCardIsElderly()
  {
    mockUser1Authentication();

    when(ticketDao.loadTowns(anyString(), anyString()))
        .thenReturn(true);

    when(ticketDao.getPriceOfTicket(anyString(), anyString()))
        .thenReturn(BigDecimal.TEN);

    BigDecimal priceOfTicket = ticketService.getPriceOfTicket(LocalTime.parse("17:38:00"), "Sofia", "Varna",
        Travel.ONE_WAY_TRIP.toString(), true);

    verify(ticketDao, times(1)).getPriceOfTicket("Sofia", "Varna");

    assertEquals(BigDecimal.valueOf(6), priceOfTicket);
  }

  /**
   * Link to the Dao {@link TicketService#getPriceOfTicket}
   */
  @Test
  void getPriceOfTicket_IfThereIsNoChildAndTheCardIsElderlyWithDiapason()
  {
    mockUser1Authentication();

    when(ticketDao.loadTowns(anyString(), anyString()))
        .thenReturn(true);

    when(ticketDao.getPriceOfTicket(anyString(), anyString()))
        .thenReturn(BigDecimal.TEN);

    BigDecimal priceOfTicket = ticketService.getPriceOfTicket(LocalTime.parse("09:38:00"), "Sofia", "Varna",
        Travel.ONE_WAY_TRIP.toString(), false);

    verify(ticketDao, times(1)).getPriceOfTicket("Sofia", "Varna");

    assertEquals(BigDecimal.valueOf(6), priceOfTicket);
  }

  /**
   * Link to the Dao {@link TicketService#getPriceOfTicket}
   */
  @Test
  void getPriceOfTicket_IfThereIsNoChildAndTheCardIsNONEWithDiapason()
  {
    mockUser3Authentication();

    when(ticketDao.loadTowns(anyString(), anyString()))
        .thenReturn(true);

    when(ticketDao.getPriceOfTicket(anyString(), anyString()))
        .thenReturn(BigDecimal.TEN);

    BigDecimal priceOfTicket = ticketService.getPriceOfTicket(LocalTime.parse("09:38:00"), "Sofia", "Varna",
        Travel.ONE_WAY_TRIP.toString(), false);

    verify(ticketDao, times(1)).getPriceOfTicket("Sofia", "Varna");

    assertEquals(BigDecimal.valueOf(9), priceOfTicket);
  }

  /**
   * Link to the Dao {@link TicketService#getPriceOfTicket}
   */
  @Test
  void getPriceOfTicket_IfWeHaveChildAndCardIsFamily()
  {
    mockUser2Authentication();

    when(ticketDao.loadTowns(anyString(), anyString()))
        .thenReturn(true);

    when(ticketDao.getPriceOfTicket(anyString(), anyString()))
        .thenReturn(BigDecimal.TEN);

    BigDecimal priceOfTicket = ticketService.getPriceOfTicket(LocalTime.parse("17:38:00"), "Sofia", "Varna",
        Travel.ONE_WAY_TRIP.toString(), true);

    verify(ticketDao, times(1)).getPriceOfTicket("Sofia", "Varna");

    assertEquals(BigDecimal.valueOf(5), priceOfTicket);
  }

  /**
   * Link to the Dao {@link TicketService#getPriceOfTicket}
   */
  @Test
  void getPriceOfTicket_IfWithoutChildAndWithoutCardAndDiapasonNotInTheRange_ThenNoDiscount()
  {
    mockUser3Authentication();

    when(ticketDao.loadTowns(anyString(), anyString()))
        .thenReturn(true);

    when(ticketDao.getPriceOfTicket(anyString(), anyString()))
        .thenReturn(BigDecimal.TEN);

    BigDecimal priceOfTicket = ticketService.getPriceOfTicket(LocalTime.parse("17:38:00"), "Sofia", "Varna",
        Travel.ONE_WAY_TRIP.toString(), false);

    verify(ticketDao, times(1)).getPriceOfTicket("Sofia", "Varna");

    assertEquals(BigDecimal.valueOf(10), priceOfTicket);
  }

  /**
   * Link to the Dao {@link TicketService#getPriceOfTicket}
   */
  @Test
  void getPriceOfTicket_WithChildAndWIthoutCard()
  {
    mockUser3Authentication();

    when(ticketDao.loadTowns(anyString(), anyString()))
        .thenReturn(true);

    when(ticketDao.getPriceOfTicket(anyString(), anyString()))
        .thenReturn(BigDecimal.TEN);

    BigDecimal priceOfTicket = ticketService.getPriceOfTicket(LocalTime.parse("17:38:00"), "Sofia", "Varna",
        Travel.ONE_WAY_TRIP.toString(), true);

    verify(ticketDao, times(1)).getPriceOfTicket("Sofia", "Varna");

    assertEquals(BigDecimal.valueOf(9), priceOfTicket);
  }

  /**
   * Link to the Dao {@link TicketService#getPriceOfTicket}
   */
  @Test
  void getPriceOfTicket_WithoutChildWitoutCard()
  {
    mockUser1Authentication();

    when(ticketDao.loadTowns(anyString(), anyString()))
        .thenReturn(true);

    when(ticketDao.getPriceOfTicket(anyString(), anyString()))
        .thenReturn(BigDecimal.TEN);

    BigDecimal priceOfTicket = ticketService.getPriceOfTicket(LocalTime.parse("17:38:00"), "Sofia", "Varna",
        Travel.ONE_WAY_TRIP.toString(), false);

    verify(ticketDao, times(1)).getPriceOfTicket("Sofia", "Varna");

    assertEquals(BigDecimal.valueOf(6), priceOfTicket);
  }

  /**
   * Link to the Dao {@link TicketService#reservation}
   */
  @Test
  void reservation()
  {
    mockUser1Authentication();
    doNothing().when(ticketDao).reservation(anyLong(), any(), any(), any(), anyLong());

    ticketService.reservation(LocalDateTime.parse("2023-04-07T00:24:01.230662200"),
        LocalDateTime.parse("2023-04-07T00:24:01.230662200"), BigDecimal.TEN, 1L, 2);

    verify(ticketDao, times(2)).reservation(1L, LocalDateTime.parse("2023-04-07T00:24:01.230662200"),
        LocalDateTime.parse("2023-04-07T00:24:01.230662200"),
        BigDecimal.TEN, 1L);
  }

  /**
   * Link to the Dao {@link TicketService#reservation}
   */
  @Test
  void reservation_WithNumberTicketZero()
  {
    mockUser1Authentication();
    doNothing().when(ticketDao).reservation(anyLong(), any(), any(), any(), anyLong());

    ticketService.reservation(LocalDateTime.parse("2023-04-07T00:24:01.230662200"),
        LocalDateTime.parse("2023-04-07T00:24:01.230662200"), BigDecimal.TEN, 1L, 0);

    verify(ticketDao, times(0)).reservation(1L, LocalDateTime.parse("2023-04-07T00:24:01.230662200"),
        LocalDateTime.parse("2023-04-07T00:24:01.230662200"),
        BigDecimal.TEN, 1L);
  }

  /**
   * Link to the Dao {@link TicketService#registration}
   */
  @Test
  void registration_ThrowsIllegalArgumentException_IfThePasswordsDontMatch()
  {
    IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class,
        () -> {
          Registration information = Registration
              .builder()
              .password("123")
              .repeatPassword("321")
              .build();

          ticketService.registration(information);
        });
    assertEquals("The passwords doesn't match!", thrown.getMessage());

  }

  /**
   * Link to the Dao {@link TicketService#registration}
   */
  @Test
  void registration_ThrowsIllegalArgumentException_IfTheCardTypeIsInvalid()
  {
    IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class,
        () -> {
          Registration information = Registration
              .builder()
              .password("123")
              .repeatPassword("123")
              .typeOfCard("TEST Card type")
              .build();

          ticketService.registration(information);
        });
    assertEquals("Invalid type of card. You choose from ELDERLY, FAMILY and NONE!", thrown.getMessage());
  }

  /**
   * Link to the Dao {@link TicketService#registration}
   */
  @Test
  void registration_ThrowsIllegalArgumentException_IfTheRoleIsInvalid()
  {
    IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class,
        () -> {
          Registration information = Registration
              .builder()
              .password("123")
              .repeatPassword("123")
              .typeOfCard(CardType.FAMILY.toString())
              .role("Test role")
              .build();

          ticketService.registration(information);
        });
    assertEquals("Invalid role!", thrown.getMessage());
  }

  /**
   * Link to the Dao {@link TicketService#registration}
   */
  @Test
  void registration()
  {
    Registration information = Registration
        .builder()
        .firstName("test")
        .lastName("test")
        .password("123")
        .email("test@abv.bg")
        .username("test")
        .repeatPassword("123")
        .typeOfCard(CardType.FAMILY.toString())
        .role(Role.ADMIN.toString())
        .build();

    UserRegistrationDetail detail = UserRegistrationDetail
        .builder()
        .firstName(information.getFirstName())
        .lastName(information.getLastName())
        .password(information.getPassword())
        .email(information.getEmail())
        .username(information.getUsername())
        .typeOfCard(CardType.FAMILY)
        .role(Role.ADMIN)
        .build();

    mockUser1Authentication();
    doNothing().when(ticketDao).registration(detail);

    ticketService.registration(information);
  }

  /**
   * Link to the Dao {@link TicketService#loadAvailableDestinations}
   */
  @Test
  void loadAvailableDestinations()
  {

    when(ticketDao.loadAvailableDestinations()).thenReturn(new ArrayList<>());

    ticketService.loadAvailableDestinations();
  }

  /**
   * Link to the Dao {@link TicketService#addAvailableDestination}
   */
  @Test
  void addAvailableDestination()
  {
    doNothing().when(ticketDao).addAvailableDestination(any());

    ticketService.addAvailableDestination(DestinationInfo.builder().build());
  }

  /**
   * Link to the Dao {@link TicketService#payTicket}]}
   */
  @Test
  void payTicket()
  {
    doNothing().when(ticketDao).payTicket(anyLong());

    ticketService.payTicket(1L);
  }

  /**
   * Link to the Dao {@link TicketService#loadAllReservationsOfUser}]}
   */
  @Test
  void loadAllReservationsOfUser()
  {
    List<Date> dates = new ArrayList<>();
    dates.add(Date
        .builder()
        .dateAfterSevenDays(LocalDate.now())
        .id(1L)
        .startDate(LocalDate.now())
        .build());

    mockUser1Authentication();

    when(ticketDao.getStartDate(1L))
        .thenReturn(dates);

    doNothing().when(ticketDao).removeOldTicket(1L);

    when(ticketDao.loadAllReservationsOfUser(1L))
        .thenReturn(new ArrayList<>());

    ticketService.loadAllReservationsOfUser();
    assertEquals(new ArrayList<>(), ticketService.loadAllReservationsOfUser());
  }

  /**
   * Link to the Dao {@link TicketService#loadAllReservationsOfUser}]}
   */
  @Test
  void loadAllReservationsOfUser_Expired()
  {
    List<Date> dates = new ArrayList<>();
    dates.add(Date
        .builder()
        .dateAfterSevenDays(LocalDate.parse("2015-06-22"))
        .id(1L)
        .startDate(LocalDate.parse("2016-06-22"))
        .build());

    mockUser1Authentication();

    when(ticketDao.getStartDate(1L))
        .thenReturn(dates);

    doNothing().when(ticketDao).removeOldTicket(1L);

    when(ticketDao.loadAllReservationsOfUser(1L))
        .thenReturn(new ArrayList<>());

    ticketService.loadAllReservationsOfUser();
    assertEquals(new ArrayList<>(), ticketService.loadAllReservationsOfUser());
  }


  /**
   * Link to the Dao {@link TicketService#refactorReservation}]}
   */
  @Test
  void refactorReservation()
  {
    doNothing().when(ticketDao).refactorReservation(1L, "1", "2",
        "3", "4");

    ticketService.refactorReservation(1L, "1", "2",
        "3", "4");
  }

  /**
   * Link to the Dao {@link TicketService#removeTicketReservation}]}
   */
  @Test
  void removeTicketReservation()
  {
    when(ticketDao.getTypeOfPayReservation(any()))
        .thenReturn("N");

    doNothing().when(ticketDao).removeTicketReservation(1L);

    ticketService.removeTicketReservation(1L);
  }

  /**
   * Link to the Dao {@link TicketService#removeTicketReservation}
   */
  @Test
  void removeTicketReservation_ThrowsIllegalArgumentException_IfTheReservationIsInvalid()
  {
    IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class,
        () -> {
          when(ticketDao.getTypeOfPayReservation(any()))
              .thenReturn(null);

          doNothing().when(ticketDao).removeTicketReservation(1L);

          ticketService.removeTicketReservation(1L);
        });
    assertEquals("This reservation doesn't exist!", thrown.getMessage());
  }

  /**
   * Link to the Dao {@link TicketService#removeTicketReservation}
   */
  @Test
  void removeTicketReservation_ThrowsIllegalArgumentException_IfTheReservationIsAlreadyPayed()
  {
    IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class,
        () -> {
          when(ticketDao.getTypeOfPayReservation(any()))
              .thenReturn("Y");

          doNothing().when(ticketDao).removeTicketReservation(1L);

          ticketService.removeTicketReservation(1L);
        });
    assertEquals("Sorry you can't remove the reservation, the ticket is already payed!", thrown.getMessage());
  }

  /**
   * Link to the Dao {@link TicketService#refactorUserProfile}
   */
  @Test
  void refactorUserProfile_ThrowsIllegalArgumentException_IfRoleIsEmpty()
  {
    IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class,
        () -> {
          when(ticketDao.getUser(any()))
              .thenReturn("");


          ticketService.refactorUserProfile(1L, "testEmail@abv.bg");
        });
    assertEquals("Invalid user id!", thrown.getMessage());
  }

  /**
   * Link to the Dao {@link TicketService#refactorUserProfile}
   */
  @Test
  void refactorUserProfile_ThrowsIllegalArgumentException_IfRoleIsAdmin()
  {
    IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class,
        () -> {
          when(ticketDao.getUser(any()))
              .thenReturn("ADMIN");

          ticketService.refactorUserProfile(1L, "testEmail@abv.bg");
        });
    assertEquals("Can not refactor ADMIN profile!", thrown.getMessage());
  }

  /**
   * Link to the Dao {@link TicketService#refactorUserProfile}
   */
  @Test
  void refactorUserProfile()
  {
    when(ticketDao.getUser(any()))
        .thenReturn("USER");

    ticketService.refactorUserProfile(1L, "testEmail@abv.bg");
  }

  /**
   * Link to the Dao {@link TicketService#loadUsers}
   */
  @Test
  void loadUsers()
  {
    when(ticketDao.loadUsers())
        .thenReturn(new ArrayList<>());

    ticketService.loadUsers();
  }

  private void mockUser1Authentication()
  {
    when(authentication.getPrincipal())
        .thenReturn(testUser1.getUsername());
    when(securityContext.getAuthentication())
        .thenReturn(authentication);
    when(ticketDao.findByUsername(any(String.class)))
        .thenReturn(java.util.Optional.ofNullable(testUser1));
    SecurityContextHolder.setContext(securityContext);
  }

  private void mockUser2Authentication()
  {
    when(authentication.getPrincipal())
        .thenReturn(testUser2.getUsername());
    when(securityContext.getAuthentication())
        .thenReturn(authentication);
    when(ticketDao.findByUsername(any(String.class)))
        .thenReturn(java.util.Optional.ofNullable(testUser2));
    SecurityContextHolder.setContext(securityContext);
  }


  private void mockUser3Authentication()
  {
    when(authentication.getPrincipal())
        .thenReturn(testUser3.getUsername());
    when(securityContext.getAuthentication())
        .thenReturn(authentication);
    when(ticketDao.findByUsername(any(String.class)))
        .thenReturn(java.util.Optional.ofNullable(testUser3));
    SecurityContextHolder.setContext(securityContext);
  }
}