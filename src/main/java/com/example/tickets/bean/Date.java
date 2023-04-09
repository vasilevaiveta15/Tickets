package com.example.tickets.bean;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Setter
@Getter
@Builder
public class Date implements Serializable
{
  Long id;
  private LocalDate startDate;
  private LocalDate dateAfterSevenDays;
}
