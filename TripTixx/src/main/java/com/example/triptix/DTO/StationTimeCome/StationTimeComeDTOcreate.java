package com.example.triptix.DTO.StationTimeCome;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StationTimeComeDTOcreate {
    private int idStationInRoute;
    private int idTrip;
    private Time timeCome;
}
