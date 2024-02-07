package com.example.triptix.DTO.Ticket;

import com.example.triptix.DTO.Station.StationDTOview;
import com.example.triptix.DTO.Station.StationDTOviewInTrip;
import com.example.triptix.Model.Booking;
import com.example.triptix.Model.Station;
import com.example.triptix.Model.TicketType;
import com.example.triptix.Model.Trip;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TicketDTOviewInTrip {
    private int idTicket;

    private int idBooking;

    private String seatName;

    private String ticketCode;

    private String ticketCodeImg;

    private double price;

    private String status;  //(paid/checkin/notcheckin/finsh/cancel/noshow)

    private float star;

    private Date createDate;

    private StationDTOviewInTrip onStation;

    private StationDTOviewInTrip offStation;

    private String fullName;

    private String phone;

    private String email;
}
