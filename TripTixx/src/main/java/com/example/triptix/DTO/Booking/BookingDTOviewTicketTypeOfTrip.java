package com.example.triptix.DTO.Booking;

import com.example.triptix.DTO.Route.TicketTypeDTOview;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookingDTOviewTicketTypeOfTrip {
    @NotNull(message = "idTrip is required")
    private int idTrip;

    @NotNull(message = "code Pick up point is required")
    private int codePickUpPoint;

    @NotNull(message = "code Drop off point is required")
    private int codeDropOffPoint;

    private double pricePerSeat;

    private TicketTypeDTOInBooking ticketType;

}
