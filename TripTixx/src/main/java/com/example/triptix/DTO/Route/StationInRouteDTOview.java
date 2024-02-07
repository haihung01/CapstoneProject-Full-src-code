package com.example.triptix.DTO.Route;

import com.example.triptix.DTO.Station.StationDTOview;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StationInRouteDTOview {

    private int idStationInRoute;

    private int orderInRoute;

    private int idStation;

    private int distance;

    private String timeCome;

    private StationDTOview station;

}
