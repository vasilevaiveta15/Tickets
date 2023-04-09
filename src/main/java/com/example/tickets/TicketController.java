package com.example.tickets;

import com.example.tickets.bean.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@Validated
@RequestMapping("/api/be/ticket")
public class TicketController
{
  private final TicketService service;

  @Autowired
  public TicketController(TicketService service)
  {
    this.service = service;
  }

  @PostMapping("/registration")
  public void registration(@RequestBody Registration information)
  {
    service.registration(information);
  }

  @GetMapping("/available/destinations")
  public List<Town> loadAvailableDestinations()
  {
    return service.loadAvailableDestinations();
  }


  @PostMapping("/available/destination")
  @PreAuthorize("hasRole('ADMIN')")
  public void addAvailableDestination(@RequestBody DestinationInfo destination)
  {
    service.addAvailableDestination(destination);
  }


  @GetMapping("/price")
  public BigDecimal getPriceOfTicket(@RequestParam @DateTimeFormat(pattern = "HH:mm:ss") LocalTime diapasonTime,
                                     @RequestParam String townFrom,
                                     @RequestParam String townTo,
                                     @RequestParam String wayOfTrip,
                                     @RequestParam boolean isThereChild)
  {
    return service.getPriceOfTicket(diapasonTime, townFrom, townTo, wayOfTrip, isThereChild);
  }

  @PostMapping("/reservation/ticket")
  public void reservation(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime startDate,
                          @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime endDate,
                          @RequestParam BigDecimal priceWithDiscount,
                          @RequestParam Long trainId,
                          @RequestParam int numberOfTickets)
  {
    service.reservation(startDate, endDate, priceWithDiscount, trainId, numberOfTickets);
  }

  @GetMapping("/reservations")
  public List<Ticket> loadAllReservationsOfUser()
  {
    return service.loadAllReservationsOfUser();
  }

  @PatchMapping("/pay/{ticketId}")
  public void payTicket(@PathVariable Long ticketId)
  {
    service.payTicket(ticketId);
  }

  @PatchMapping("/reservation/{idOfReservation}")
  public void refactorReservation(@PathVariable Long idOfReservation,
                                  @RequestParam String day1,
                                  @RequestParam String month1,
                                  @RequestParam(required = false) String day2,
                                  @RequestParam(required = false) String month2)
  {
    service.refactorReservation(idOfReservation, day1, month1, day2, month2);
  }

  @DeleteMapping("/{idOfReservation}")
  public void removeTicketReservation(@PathVariable Long idOfReservation)
  {
    service.removeTicketReservation(idOfReservation);
  }

  //only AMDIN za da vzemem id na potrebitelq i da go postavim v dolniq za refaktor
  @GetMapping("/users")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public List<UserInformation> loadUsers()
  {
    return service.loadUsers();
  }


  //only ADMIN
  @PatchMapping("/refactor/{userId}")
  @PreAuthorize("hasRole('ADMIN')")
  public void refactorUserProfile(@PathVariable Long userId,
                                  @RequestParam String email) //po uslovie ne e qsno zatova admina
  // shte moje da refactorira email
  {
    service.refactorUserProfile(userId, email);
  }
}
