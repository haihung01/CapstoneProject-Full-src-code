package com.example.triptix.DTO.Booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RouteDTOviewInBooking {
    private int idRoute;

    private String name;

    private String departurePoint;

    private String destination;

    private String status;

    private int idAdmin;
}
