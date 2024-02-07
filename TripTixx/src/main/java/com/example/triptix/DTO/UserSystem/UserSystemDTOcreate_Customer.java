package com.example.triptix.DTO.UserSystem;

import com.example.triptix.Util.ValidData.SexGender.ValueSexGender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserSystemDTOcreate_Customer {

    @Column(unique = true)
    @NotBlank(message = "phone is required")
    @Pattern(regexp = "\\d{10}", message = "Invalid phone number format, must be 10 numbers")
    private String phone;

    @NotBlank(message = "user name is required")
    @Size(min = 5, message = "user name must be at least 5 characters long")
    private String userName;

    @NotBlank(message = "fullName is required")
    @Size(min = 5, message = "fullName must be at least 5 characters long")
    private String fullName;

    @NotBlank(message = "password is required")
    @Size(min = 5, message = "password must be at least 5 characters long")
    private String password;

//    private int voucherCoins;

    @NotBlank(message = "address is required")
    private String address;

    private Long birthdayLong;

    @NotBlank(message = "sex is required")
    @ValueSexGender
    private String gender;

    @Column(unique = true)
    @NotBlank(message = "email is required")
    @Email
    private String email;

//    private java.util.Date createdDate;

//    private String citizenIdentityCard; //cmnd/ cccd

//    private String status;  //active, deactive

//    private String role;  //DRIVER, CUSTOMER, STAFF, ADMIN

//    private String fcmTokenDevide;  //firebase notification

//    private int mileStone;
}
