package com.example.triptix.DTO.Ticket;

import com.example.triptix.DTO.Route.RouteDTOviewInTrip;
import com.example.triptix.DTO.UserSystem.UserSystemDTOviewInTrip;
import com.example.triptix.DTO.Vehicle.VehicleDTOview;
import com.example.triptix.DTO.Vehicle.VehicleDTOviewInTrip;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TripDTOviewInTicket {

    private int idTrip;

    private int idRoute;

    private String nameRoute;

    private int idDriver;

    private int idVehicle;

    private int idStaff;

    private Long departureDate;   //format yyyy-MM-dd HH:mm:ss

    private Long endDate;

    private float avarageStar;

    private String adminCheck;

    private String status;

    private UserSystemDTOInTicket driverDTO;

    private VehicleDTOview vehicleDTO;

}
