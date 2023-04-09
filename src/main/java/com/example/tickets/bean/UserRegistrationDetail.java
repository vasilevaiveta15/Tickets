package com.example.tickets.bean;

import com.example.tickets.enums.CardType;
import com.example.tickets.enums.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Transient;
import org.springframework.security.core.GrantedAuthority;

@Getter
@Setter
@Builder
public class UserRegistrationDetail implements GrantedAuthority
{
  private Long     id;
  private String   firstName;
  private String   lastName;
  private String   email;
  private String password;
  private Role   role;
  private CardType typeOfCard;
  private String username;

  @Override
  @Transient
  public String getAuthority()
  {
    return "ROLE_" + role.toString();
  }
}
