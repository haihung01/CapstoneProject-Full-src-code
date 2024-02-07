package com.example.triptix.DTO.Ticket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TicketDTOListPotentialCustomer {
    int top;

    String email;

    String nameCustomer;

    String totalPriceUsed;
}
