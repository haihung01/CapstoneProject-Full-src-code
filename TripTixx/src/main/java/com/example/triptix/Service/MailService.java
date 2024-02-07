package com.example.triptix.Service;


import com.example.triptix.DTO.EMail.TripSendMail;
import com.example.triptix.DTO.ResponseObject;

public interface MailService {
    ResponseObject<?> sendMailOTP(String toEmail);
    ResponseObject<?> sendMailBookingSuccess(String toEmail, String ListSeat, int idTrip, TripSendMail tripSendMail);

    ResponseObject<?> sendMailBookingSuccessTwo(String toEmail, String name, String startTime, String endTime, String price);
    ResponseObject<?> sendMaiRegisterSuccess(String toEmail, String username, String password, String role);
    ResponseObject<?> sendMailCancelTrip(String toEmail, String departurePoint, String destination, String date);
}
