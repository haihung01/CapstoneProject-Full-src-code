package com.example.triptix.DTO.Booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BookingResult {
    private String msg;
    private BookingSuccessTripSendMail data;
}
