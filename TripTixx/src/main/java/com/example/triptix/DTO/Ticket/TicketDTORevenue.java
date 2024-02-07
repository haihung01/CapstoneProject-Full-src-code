package com.example.triptix.DTO.Ticket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TicketDTORevenue {
    private int idTicket;

    private int idTrip;

    private String date;

    private double totalPrice;

    private double totalTicket;

    private short star;
}
