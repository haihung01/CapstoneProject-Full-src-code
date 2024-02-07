package com.example.triptix.DTO.Trip;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TripDTOupdateDriverAndBus {
    private int idTrip;
    @NotNull(message = "id Driver is required")
    private int idDriver;

    @NotNull(message = "id Bus is required")
    private int idBus;
}
