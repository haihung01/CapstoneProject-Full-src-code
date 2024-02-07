package com.example.triptix.DTO.Ticket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TicketDTOChangeSeat {
    private int idTicket;

    private String seatName;

}
