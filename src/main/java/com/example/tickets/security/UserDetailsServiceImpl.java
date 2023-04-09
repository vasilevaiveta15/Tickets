package com.example.tickets.security;

import com.example.tickets.TicketDao;
import com.example.tickets.bean.UserRegistrationDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService
{
  @Autowired
  private TicketDao ticketDao;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
  {
    UserRegistrationDetail user = ticketDao.findByUsername(username)
        .orElseThrow(() -> new IllegalArgumentException("No such user found!"));
    ;


    return org.springframework.security.core.userdetails.User
        .withUsername(user.getUsername())
        .password(user.getPassword())
        .roles(user.getRole().name())
        .build();
  }
}
