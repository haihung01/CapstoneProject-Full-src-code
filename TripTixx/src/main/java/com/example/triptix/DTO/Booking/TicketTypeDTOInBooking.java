package com.example.triptix.DTO.Booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TicketTypeDTOInBooking {
    private int idTicketType;

    private int idRoute;

    private int idEarlyOnStation;

    private int idLateOffStation;
}
