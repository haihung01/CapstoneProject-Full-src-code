package com.example.triptix.Service;


import com.example.triptix.DTO.Payment.GuestBookingDTO;
import com.example.triptix.DTO.ResponseObject;

import javax.servlet.http.HttpServletRequest;

public interface PaymentService {
    ResponseObject<?> createPayment(HttpServletRequest req, int amount_param, int idCustomer);
    ResponseObject<?> createPayment2(HttpServletRequest req, int amount_param, int idCustomer);
    ResponseObject<?> bookingGuest(HttpServletRequest req, GuestBookingDTO guestBookingDTO);
    ResponseObject<?> resultTransaction(String vnp_Amount, String vnp_BankCode, String vnp_OrderInfo,  String vnp_PayDate, String vnp_ResponseCode);
    ResponseObject<?> resultPayment(String status);
}
