package com.example.tickets.bean;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Setter
@Getter
@Builder
public class DestinationInfo implements Serializable
{
  private Long       distance;
  private String     town1;
  private String     town2;
  private BigDecimal initialPrice;

}
