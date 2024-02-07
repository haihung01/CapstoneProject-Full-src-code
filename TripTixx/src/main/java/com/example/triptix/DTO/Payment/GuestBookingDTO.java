package com.example.triptix.DTO.Payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GuestBookingDTO {
    private String nameGuest;
    private String phoneGuest;
    private String emailGuest;
    private int idTrip;
    private int idOnStation;
    private int idoffStation;
    private List<String> seatName;
    private int amountMoneyToRecharge;
}
