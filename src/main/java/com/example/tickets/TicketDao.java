package com.example.tickets;

import com.example.tickets.bean.*;
import com.example.tickets.enums.CardType;
import com.example.tickets.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class TicketDao
{
  private NamedParameterJdbcTemplate template;

  @Autowired
  public TicketDao(NamedParameterJdbcTemplate template)
  {
    this.template = template;
  }

  public void registration(UserRegistrationDetail detail)
  {
    String sql = "" +
        " INSERT INTO users(firstName,      " +
        "                   lastName,       " +
        "                   email,          " +
        "                   password,       " +
        "                   role,           " +
        "                   username,       " +
        "                   typeOfCard)     " +
        " VALUES(:firstName,                " +
        "        :lastName,                 " +
        "        :email,                    " +
        "        :password,                 " +
        "        :role,                     " +
        "        :username,                 " +
        "        :typeOfCard)               ";

    MapSqlParameterSource source = new MapSqlParameterSource()
        .addValue("firstName", detail.getFirstName())
        .addValue("lastName", detail.getLastName())
        .addValue("email", detail.getEmail())
        .addValue("password", detail.getPassword())
        .addValue("role", detail.getRole().toString())
        .addValue("username", detail.getUsername())
        .addValue("typeOfCard", detail.getTypeOfCard().toString());

    template.update(sql, source);
  }

  public List<Town> loadAvailableDestinations()
  {
    String sql = "" +
        "SELECT town1, town2, distance FROM train";
    return template.query(sql, new MapSqlParameterSource(), (rs, rowNum) ->
        Town.builder()
            .town1(rs.getString("town1"))
            .town2(rs.getString("town2"))
            .distance(rs.getInt("distance"))
            .build());
  }

  public void addAvailableDestination(DestinationInfo destinationInfo)
  {
    String sql = "" +
        " INSERT INTO train (distance, town1, town2, initialPrice)" +
        "VALUES (:distance, town1, town2, initialPrice)";

    MapSqlParameterSource source = new MapSqlParameterSource()
        .addValue("distance", destinationInfo.getDistance())
        .addValue("town1", destinationInfo.getTown1())
        .addValue("town2", destinationInfo.getTown2())
        .addValue("initialPrice", destinationInfo.getInitialPrice());

    template.update(sql, source);
  }

  public BigDecimal getPriceOfTicket(String townFrom, String townTo)
  {
    String sql = "" +
        " SELECT initialPrice       " +
        "   FROM train              " +
        " WHERE town1 = :townFrom   ";
    if (null != townTo && !"".equals(townTo)) {
      sql += "   AND town2 = :townTo      ";
    }

    MapSqlParameterSource source = new MapSqlParameterSource()
        .addValue("townFrom", townFrom)
        .addValue("townTo", townTo);

    try {
      return template.queryForObject(sql, source, BigDecimal.class);
    }
    catch (EmptyResultDataAccessException e) {
      return BigDecimal.ZERO;
    }
  }

  public void reservation(Long userId, LocalDateTime startDate, LocalDateTime endDate,
                          BigDecimal priceWithDiscount, Long trainId)
  {
    String sql = "" +
        " INSERT INTO ticket (user_id,                                      " +
        "                     startDate,                                    " +
        "                     endDate,                                      " +
        "                     dateAfterSevenDays,                           " +
        "                     is_pays,                                      " +
        "                     priceWithDiscount,                            " +
        "                     train_id)                                     " +
        " VALUES(:user_id,                                                  " +
        "        :startDate,                                                " +
        "        :endDate,                                                  " +
        "        date_add(:startDate,Interval 7 day),                       " +
        "        'N',                                                       " +
        "        :priceWithDiscount,                                        " +
        "        :train_id)                                                 ";

    MapSqlParameterSource source = new MapSqlParameterSource()
        .addValue("user_id", userId)
        .addValue("startDate", startDate)
        .addValue("endDate", endDate)
        .addValue("priceWithDiscount", priceWithDiscount)
        .addValue("train_id", trainId);

    template.update(sql, source);
  }

  public List<Ticket> loadAllReservationsOfUser(Long userId)
  {
    String sql = "" +
        " SELECT tik.id AS id,                        " +
        " t.initialPrice AS price,                    " +
        " tik.startDate AS startDate,                 " +
        " t.distance AS distance,                     " +
        " t.town1 AS town1,                           " +
        " t.town2 AS town2                            " +
        "FROM ticket tik                              " +
        "         JOIN train t on tik.train_id = t.id " +
        "WHERE is_pays = 'N'                          " +
        "  AND user_id = :userId                      ";

    return template.query(sql, new MapSqlParameterSource("userId", userId), (rs, rowNum) -> Ticket
        .builder()
        .id(rs.getLong("id"))
        .price(rs.getBigDecimal("price"))
        .startDate(rs.getObject("startDate", LocalDate.class))
        .distance(rs.getInt("distance"))
        .town1(rs.getString("town1"))
        .town2(rs.getString("town2"))
        .build());
  }

  public void payTicket(Long id)
  {
    // update tickets is_pay -> Y
    String sql = "" +
        " UPDATE ticket set is_pays = 'Y' WHERE id = :id";

    template.update(sql, new MapSqlParameterSource("id", id));
  }

  public void refactorReservation(Long idOfReservation, String day1, String month1, String day2, String month2)
  {
    StringBuilder sql = new StringBuilder();
    sql.append(
        "  update ticket                                                                " +
            "  set                                                                      " +
            " startDate=date_add(startDate,Interval :month1 month),                     " +
            " dateAfterSevenDays = date_add(dateAfterSevenDays,Interval :month1 month), " +
            " startDate=date_add(startDate,Interval :day1 day),                         " +
            " dateAfterSevenDays = date_add(dateAfterSevenDays,Interval :day1 day)      ");
    if (null != month2 && !"".equals(month2)) {
      sql.append("    ,endDate=date_add(endDate,Interval :month2 month) ");
    }
    if (null != day2 && !"".equals(day2)) {
      sql.append("    ,endDate=date_add(endDate,Interval :day2 day) ");
    }
    sql.append(" where id = :idOfReservation ");

    MapSqlParameterSource source = new MapSqlParameterSource()
        .addValue("idOfReservation", idOfReservation)
        .addValue("day1", day1)
        .addValue("month1", month1)
        .addValue("day2", day2)
        .addValue("month2", month2);

    template.update(sql.toString(), source);

  }

  public List<Date> getStartDate(Long userId)
  {
    String sql = "" +
        " SELECT id,                                           " +
        "        startDate,                                    " +
        "        dateAfterSevenDays                            " +
        "   FROM ticket                                        " +
        " WHERE is_pays = 'N'                                  " +
        "   AND user_id = :userId                              ";

    return template.query(sql, new MapSqlParameterSource("userId", userId), (rs, rowNum) -> Date
        .builder()
        .id(rs.getLong("id"))
        .startDate(rs.getObject("startDate", LocalDate.class))
        .dateAfterSevenDays(rs.getObject("dateAfterSevenDays", LocalDate.class))
        .build());
  }

  public void removeOldTicket(Long id)
  {
    String sql = "" +
        " DELETE FROM ticket  " +
        " WHERE id = :id      ";

    template.update(sql, new MapSqlParameterSource("id", id));
  }

  public String getTypeOfPayReservation(Long idOfReservation)
  {
    String sql = "" +
        " SELECT is_pays FROM ticket where id = :idOfReservation";
    MapSqlParameterSource source = new MapSqlParameterSource("idOfReservation", idOfReservation);

    try {
      return template.queryForObject(sql, source, String.class);
    }
    catch (EmptyResultDataAccessException e) {
      return null;
    }
  }

  public void removeTicketReservation(Long idOfReservation)
  {
    String sql = "" +
        "DELETE FROM ticket WHERE id = :idOfReservation ";

    MapSqlParameterSource source = new MapSqlParameterSource("idOfReservation", idOfReservation);

    template.update(sql, source);
  }

  public List<UserInformation> loadUsers()
  {
    String sql = "" +
        "SELECT id,firstName, lastName, email, role, username, typeOfCard FROM users";

    return template.query(sql, new MapSqlParameterSource(), (rs, rowNum) -> UserInformation
        .builder()
        .userId(rs.getLong("id"))
        .firstName(rs.getString("firstName"))
        .lastName(rs.getString("lastName"))
        .email(rs.getString("email"))
        .role(rs.getString("role"))
        .username(rs.getString("username"))
        .typeOfCard(rs.getString("typeOfCard"))
        .build());
  }

  public String getUser(Long userId)
  {
    String sql = "" +
        "SELECT role FROM users WHERE id = :userId";

    try {
      return template.queryForObject(sql, new MapSqlParameterSource("userId", userId), String.class);
    }
    catch (EmptyResultDataAccessException e) {
      return "";
    }
  }


  public void refactorUserProfile(Long userId, String email)
  {
    String sql = "" +
        " UPDATE users set email = :email where id = :userId";
    MapSqlParameterSource source = new MapSqlParameterSource()
        .addValue("userId", userId)
        .addValue("email", email);
    template.update(sql, source);
  }

  public Optional<UserRegistrationDetail> findByUsername(String username)
  {
    String sql = "" +
        "SELECT id,                     " +
        "       firstName,              " +
        "       lastName,               " +
        "       email,                  " +
        "       password,               " +
        "       role,                   " +
        "       username,               " +
        "       typeOfCard              " +
        "   FROM USERS                  " +
        " WHERE USERNAME = :username    ";

    SqlParameterSource parameters = new MapSqlParameterSource()
        .addValue("username", username);

    try {
      return template.queryForObject(sql, parameters, (rs, rowNum) ->
          Optional.of(UserRegistrationDetail
              .builder()
              .id(rs.getLong("id"))
              .firstName(rs.getString("firstName"))
              .lastName(rs.getString("lastName"))
              .email(rs.getString("email"))
              .password(rs.getString("password"))
              .role(Role.valueOf(rs.getString("role")))
              .username(rs.getString("username"))
              .typeOfCard(CardType.valueOf(rs.getString("typeOfCard")))
              .build()
          ));
    }
    catch (EmptyResultDataAccessException ex) {
      return Optional.empty();
    }
  }

  public Boolean loadTowns(String town1, String town2)
  {
    String sql = "" +
        " SELECT COUNT(1) FROM train WHERE town1 = :town1 " +
        " AND town2 = :town2 ";

    MapSqlParameterSource source = new MapSqlParameterSource()
        .addValue("town1", town1)
        .addValue("town2", town2);

    try {
      return template.queryForObject(sql, source, Boolean.class);
    }
    catch (EmptyResultDataAccessException e) {
      return false;
    }
  }
}
