package com.example.triptix.DTO.Route;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RouteDTOviewInTrip {
    private int idRoute;

    private String name;

    private String departurePoint;

    private String destination;

    private List<StationInRouteDTOview> listStationInRoute;

    private List<TicketTypeDTOview> listTicketType;
}
