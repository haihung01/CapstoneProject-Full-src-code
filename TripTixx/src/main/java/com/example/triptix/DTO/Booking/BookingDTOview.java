package com.example.triptix.DTO.Booking;

import com.example.triptix.DTO.Trip.TripDTOview;
import com.example.triptix.DTO.UserSystem.UserSystemDTOview;
import com.example.triptix.Model.PaymentTransaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookingDTOview {
    private int idBooking;

    private int idTrip;

    private int idCustomer;

    private double totalPrice;

    private short totalTicket;

    private Date createdDate;

    private UserSystemDTOviewInBooking customer;

    private TripDTOviewInBooking trip;

    private List<TicketDTOview> listTicket;

    private PaymentTransactionDTOcreate paymentTransaction;


}
