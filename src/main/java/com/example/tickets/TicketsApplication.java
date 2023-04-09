package com.example.tickets;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@SpringBootApplication
@Component
public class TicketsApplication
{
  public static void main(String[] args)
  {
    SpringApplication.run(TicketsApplication.class, args);
  }
}
