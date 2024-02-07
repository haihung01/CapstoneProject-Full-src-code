package com.example.triptix.DTO.Booking;

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
public class TicketDTOview {

    private int idTicket;

    private int idTicketType;

    private int idBooking;

    private int idTrip;

    private Date createdDate;

    private double price;

    private String seatName;

    private String status;

    private String ticketCode;

    private String ticketCodeImg;

    private float star;

    private int idOnStation;

    private int inOffStation;

}
