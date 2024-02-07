package com.example.triptix.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idTicket;

    @ManyToOne
    @JoinColumn(name = "idBooking")
    @JsonIgnore
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "idTrip")
    @JsonIgnore
    private Trip trip;

    private String seatName;

    private String ticketCode;

    private String ticketCodeImg;

    private double price;

    private String status;  //(paid/checkin/notcheckin/finsh/cancel/noshow)

    private float star;

    private Date createDate;

    @ManyToOne
    @JoinColumn(name = "idOnStation", nullable = true)
    @JsonIgnore
    private Station onStation;

    @ManyToOne
    @JoinColumn(name = "idOffStation", nullable = true)
    @JsonIgnore
    private Station offStation;

    @ManyToOne
    @JoinColumn(name = "idTicketType", nullable = true)
    @JsonIgnore
    private TicketType ticketType;
}
