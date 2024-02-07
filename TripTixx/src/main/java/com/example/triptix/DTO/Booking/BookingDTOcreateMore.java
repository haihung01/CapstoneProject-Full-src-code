package com.example.triptix.DTO.Booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookingDTOcreateMore {
    @NotNull(message = "idTrip is required")
    private int idTrip;

    @NotNull(message = "idCustomer is required")
    private int idCustomer;

    private List<TicketCreate> listTicket;
}
