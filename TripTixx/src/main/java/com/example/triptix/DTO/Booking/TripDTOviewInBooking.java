package com.example.triptix.DTO.Booking;

import com.example.triptix.DTO.Route.RouteDTOview;
import com.example.triptix.DTO.Vehicle.VehicleDTOview;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TripDTOviewInBooking {
    private int idTrip;

    private int idRoute;

    private int idStaff;

    private int idDriver;

    private int idVehicle;

    private Date departureDate;

    private Date endDate;

    private String adminCheck;

    private String status;

    private RouteDTOviewInBooking route;

    private VehicleDTOview vehicle;

}
