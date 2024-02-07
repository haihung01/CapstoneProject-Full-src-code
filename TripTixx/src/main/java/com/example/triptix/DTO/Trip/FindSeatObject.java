package com.example.triptix.DTO.Trip;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FindSeatObject {
    private int idStationPickUp;
    private int idStationDropOff;
    private int idTrip;
    private List<ComboSeatStation> comboSeatStations;
}
