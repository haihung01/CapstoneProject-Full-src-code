package com.example.triptix.DTO.Booking;

import com.example.triptix.DTO.EMail.TripSendMail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookingSuccessTripSendMail {
    private String toEmail;
    private String nameRoute;
    private String startTime;
    private String endTime;
    private String totalPrice;
//    private String ListSeat;
//    private int idTrip;
//    private TripSendMail tripSendMail;
}
