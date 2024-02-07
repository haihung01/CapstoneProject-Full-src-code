package com.example.triptix.DTO.Booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserSystemDTOviewInBooking {

    private int idUserSystem;

    private String phone;

    private String userName;

    private String fullName;

    private String gender;

    private String email;

    private String role;  //DRIVER, CUSTOMER, STAFF, ADMIN

}
