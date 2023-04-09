package com.example.tickets.bean;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
@Setter
@Getter
@Builder
public class Town implements Serializable
{
  private String town1;
  private String town2;
  private Integer distance;
}
