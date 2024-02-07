package com.example.triptix.DTO.Ticket;

import com.example.triptix.DTO.Station.StationDTOviewInTrip;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TicketDTOviewCheckIn {

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
    private UserSystemDTOInTicket customer;

}
