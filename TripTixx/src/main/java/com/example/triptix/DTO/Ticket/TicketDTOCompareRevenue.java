package com.example.triptix.DTO.Ticket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TicketDTOCompareRevenue {

    double totalAmountPriceToday;
    int numberOfTicketToday;
    double totalAmountPriceYesterday;
    int numberOfTicketYesterday;
    String comparisonResult;
}
