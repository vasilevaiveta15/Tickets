package com.example.tickets.bean;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
@Builder
public class Ticket implements Serializable
{
  private Long       id;
  private BigDecimal price;
  private LocalDate  startDate;
  private Integer distance;
  private String town1;
  private String town2;

}
