package com.example.triptix.DTO.Booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentTransactionDTOcreate {

    private int idBooking;

    private String phoneGuest;

    private String nameGuest;

    private String emailGuest;

    private Date createDate;

}
