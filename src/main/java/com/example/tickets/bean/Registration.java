package com.example.tickets.bean;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@Builder
public class Registration implements Serializable
{
  private String firstName;
  private String lastName;
  private String email;
  private String password;
  private String repeatPassword;
  private String role;
  private String typeOfCard;
  private String username;
}
