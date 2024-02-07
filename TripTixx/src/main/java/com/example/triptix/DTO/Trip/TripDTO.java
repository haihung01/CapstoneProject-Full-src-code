package com.example.triptix.DTO.Trip;

import com.example.triptix.DTO.Route.RouteDTOview;
import com.example.triptix.DTO.Route.RouteDTOviewInTrip;
import com.example.triptix.DTO.Route.StationInRouteDTOview;
import com.example.triptix.DTO.Route.TicketTypeDTOview;
import com.example.triptix.DTO.Ticket.TicketDTOviewInTrip;
import com.example.triptix.DTO.UserSystem.UserSystemDTOview;
import com.example.triptix.DTO.UserSystem.UserSystemDTOviewInTrip;
import com.example.triptix.DTO.Vehicle.VehicleDTOview;
import com.example.triptix.DTO.Vehicle.VehicleDTOviewInTrip;
import com.example.triptix.Model.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TripDTO {
    private int idTrip;

    private RouteDTOviewInTrip route;

    private UserSystemDTOviewInTrip driver;

    private VehicleDTOviewInTrip vehicle;

    private UserSystemDTOviewInTrip staff;

//    private String departureDateStr;   //format yyyy-MM-dd HH:mm:ss

    private Long departureDateLT;

//    private String endDateStr;

    private Long endDateLT;

    private float avarageStar;

    private String adminCheck;

    private String repeatCycle;

    private String createdDate;

    private String updatedDate;

    private String status;

    private String subTrip; //Vé chặng thuộc chuyến 08:00 12-11-2023 Hồ Chí Minh - Bù Đốp

    private int totalCustomer;

    private List<TicketDTOviewInTrip> tickets;

    private List<String> seatNameBusy;

    private short availableSeat;

    private short bookedSeat;

    private List<IdTripAndDepartureDate> listSchedules;
}