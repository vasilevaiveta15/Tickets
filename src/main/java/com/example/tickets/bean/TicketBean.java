package com.example.tickets.bean;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Setter
@Getter
@Builder
public class TicketBean implements Serializable
{
  private Long tiketId;
  private Long userId;
}
