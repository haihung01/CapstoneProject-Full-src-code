package com.example.triptix.DTO.Booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookingDTOcreateRound {
    @NotNull(message = "idTrip is required")
    private int idTrip;

    @NotNull(message = "idCustomer is required")
    private int idCustomer;
    @NotNull(message = "code Pick up point is required")
    private int codePickUpPoint;

    @NotNull(message = "code Drop off point is required")
    private int codeDropOffPoint;

    private List<String> seatName;

    @NotNull(message = "idTrip2 is required")
    private int idTrip2;

    @NotNull(message = "code Pick up point 2 is required")
    private int codePickUpPoint2;

    @NotNull(message = "code Drop off point 2 is required")
    private int codeDropOffPoint2;

    private List<String> seatName2;
}
