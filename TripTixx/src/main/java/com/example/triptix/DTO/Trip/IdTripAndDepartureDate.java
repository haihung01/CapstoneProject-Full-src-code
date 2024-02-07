package com.example.triptix.DTO.Trip;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class IdTripAndDepartureDate {
    private int idTrip;
    private Long departureDateLT;
}
