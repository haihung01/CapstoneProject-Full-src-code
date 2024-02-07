package com.example.triptix.DTO.Booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookingDTOcreate {

    @NotNull(message = "idTrip is required")
    private int idTrip;

    @NotNull(message = "idCustomer is required")
    private int idCustomer;

    @NotNull(message = "code Pick up point is required")
    private int codePickUpPoint;

    @NotNull(message = "code Drop off point is required")
    private int codeDropOffPoint;

    private List<String> seatName;


}

