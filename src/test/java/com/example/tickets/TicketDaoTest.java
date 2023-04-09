package com.example.tickets;

import com.example.tickets.bean.UserRegistrationDetail;
import com.example.tickets.enums.CardType;
import com.example.tickets.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class TicketDaoTest
{
  @Autowired
  private TicketDao ticketDao;

  @java.lang.SuppressWarnings("all")
  private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(TicketDaoTest.class);


  @Test
  void wires()
  {
    assertNotNull(ticketDao);
    log.info("TEST - OK");
  }

  /**
   * Link to the Dao {@link TicketDao#registration}
   */
  @Test
  void registration()
  {
    assertDoesNotThrow(() -> ticketDao.registration(UserRegistrationDetail
        .builder()
        .username("Test")
        .typeOfCard(CardType.ELDERLY)
        .role(Role.ADMIN)
        .password("Pass!123")
        .email("Test12@abv.bg")
        .lastName("test")
        .firstName("TestName")
        .build()));

    log.info("TEST - OK");
  }

  /**
   * Link to the Dao {@link TicketDao#loadAvailableDestinations}
   */
  @Test
  void loadAvailableDestinations()
  {
    assertDoesNotThrow(() -> ticketDao.loadAvailableDestinations());
    log.info("TEST - OK");
  }

  /**
   * Link to the Dao {@link TicketDao#getPriceOfTicket}
   */
  @Test
  void getPriceOfTicket_withEmpty_townTo()
  {
    assertDoesNotThrow(() -> ticketDao.getPriceOfTicket("Sofia", ""));
    log.info("TEST - OK");
  }

  /**
   * Link to the Dao {@link TicketDao#getPriceOfTicket}
   */
  @Test
  void getPriceOfTicket()
  {
    assertDoesNotThrow(() -> ticketDao.getPriceOfTicket("Sofia", "Varna"));
    log.info("TEST - OK");
  }

  /**
   * Link to the Dao {@link TicketDao#getPriceOfTicket}
   */
  @Test
  void getPriceOfTicket_with_EmptyResult()
  {
    assertDoesNotThrow(() -> ticketDao.getPriceOfTicket("", ""));
    log.info("TEST - OK");
  }


  /**
   * Link to the Dao {@link TicketDao#reservation}
   */
  @Test
  void reservation()
  {
    assertDoesNotThrow(() -> ticketDao.reservation(1L, LocalDateTime.now(),
        LocalDateTime.parse("2023-06-22T17:38"), BigDecimal.valueOf(50), 1L));
    log.info("TEST - OK");
  }

  /**
   * Link to the Dao {@link TicketDao#payTicket}
   */
  @Test
  void payTicket()
  {
    assertDoesNotThrow(() -> ticketDao.payTicket(1L));
    log.info("TEST - OK");
  }

  /**
   * Link to the Dao {@link TicketDao#refactorReservation}
   */
  @Test
  void refactorReservation_WithEmpty_Month1_And_Day2()
  {
    assertDoesNotThrow(() -> ticketDao.refactorReservation(1L, "1", "5", "", ""));
    log.info("TEST - OK");
  }

  /**
   * Link to the Dao {@link TicketDao#refactorReservation}
   */
  @Test
  void refactorReservation()
  {
    assertDoesNotThrow(() -> ticketDao.refactorReservation(1L, "1", "5", "4", "5"));
    log.info("TEST - OK");
  }

  /**
   * Link to the Dao {@link TicketDao#removeOldTicket}
   */
  @Test
  void removeOldTicket()
  {
    assertDoesNotThrow(() -> ticketDao.removeOldTicket(1L));
    log.info("TEST - OK");
  }

  /**
   * Link to the Dao {@link TicketDao#getTypeOfPayReservation}
   */
  @Test
  void getTypeOfPayReservation()
  {
    assertDoesNotThrow(() -> ticketDao.getTypeOfPayReservation(1L));
    log.info("TEST - OK");
  }

  /**
   * Link to the Dao {@link TicketDao#getTypeOfPayReservation}
   */
  @Test
  void getTypeOfPayReservation_With_EmpthyResult()
  {
    assertDoesNotThrow(() -> ticketDao.getTypeOfPayReservation(null));
    log.info("TEST - OK");
  }

  /**
   * Link to the Dao {@link TicketDao#removeTicketReservation}
   */
  @Test
  void removeTicketReservation()
  {
    assertDoesNotThrow(() -> ticketDao.removeTicketReservation(1L));
    log.info("TEST - OK");
  }

  /**
   * Link to the Dao {@link TicketDao#loadUsers}
   */
  @Test
  void loadUsers()
  {
    assertDoesNotThrow(() -> ticketDao.loadUsers());
    log.info("TEST - OK");
  }

  /**
   * Link to the Dao {@link TicketDao#getUser}
   */
  @Test
  void getUser()
  {
    assertDoesNotThrow(() -> ticketDao.getUser(1L));
    log.info("TEST - OK");
  }

  /**
   * Link to the Dao {@link TicketDao#getUser}
   */
  @Test
  void getUser_With_EmptyResult()
  {
    assertDoesNotThrow(() -> ticketDao.getUser(null));
    log.info("TEST - OK");
  }

  /**
   * Link to the Dao {@link TicketDao#refactorUserProfile}
   */
  @Test
  void refactorUserProfile()
  {
    assertDoesNotThrow(() -> ticketDao.refactorUserProfile(1L, "testmain@abv.bg"));
    log.info("TEST - OK");
  }

  /**
   * Link to the Dao {@link TicketDao#findByUsername}
   */
  @Test
  void findByUsername()
  {
    assertDoesNotThrow(() -> ticketDao.findByUsername("test"));
    log.info("TEST - OK");
  }

  /**
   * Link to the Dao {@link TicketDao#loadTowns}
   */
  @Test
  void loadTowns_With_Empty_Town2()
  {
    assertDoesNotThrow(() -> ticketDao.loadTowns("Sofia", ""));
    log.info("TEST - OK");
  }

  /**
   * Link to the Dao {@link TicketDao#loadTowns}
   */
  @Test
  void loadTowns()
  {
    assertDoesNotThrow(() -> ticketDao.loadTowns("Sofia", "Varna"));
    log.info("TEST - OK");
  }

  /**
   * Link to the Dao {@link TicketDao#loadTowns}
   */
  @Test
  void loadTowns_With_EmptyResult()
  {
    assertDoesNotThrow(() -> ticketDao.loadTowns("", ""));
    log.info("TEST - OK");
  }
}