package com.example.triptix.DTO.Ticket;

import com.example.triptix.DTO.Station.StationDTOview;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TicketDTOview {

    private int idTicket;

    private int idBooking;

    private int idTrip;

    private int idOnStation;

    private int inOffStation;

    private StationDTOview onStation;

    private StationDTOview offStation;

    private int idTicketType;

    private double price;

    private String seatName;

    private String status;

    private String ticketCode;

    private String ticketCodeImg;

    private float star;

    private Long createdDate;

    private TripDTOviewInTicket trip;
}
