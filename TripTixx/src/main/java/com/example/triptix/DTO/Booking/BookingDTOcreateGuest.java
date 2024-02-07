package com.example.triptix.DTO.Booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookingDTOcreateGuest {
    @NotNull(message = "idTrip is required")
    private int idTrip;

    @NotNull(message = "phone Guest is required")
    private String phoneGuest;

    @NotNull(message = "name Guest is required")
    private String nameGuest;

    @NotNull(message = "email Guest is required")
    private String emailGuest;

    @NotNull(message = "code Pick up point is required")
    private int codePickUpPoint;

    @NotNull(message = "code Drop off point is required")
    private int codeDropOffPoint;

    private List<String> seatName;
}
