package com.example.triptix.DTO.UserSystem;

import com.example.triptix.Util.ValidData.Role.ValueRole;
import com.example.triptix.Util.ValidData.SexGender.ValueSexGender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserSystemDTOcreateTimeStamp {

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

    @NotBlank(message = "address is required")
    private String address;

    private Long birthdayTimeStamp;

    @NotBlank(message = "sex is required")
    @ValueSexGender
    private String gender;

    @Column(unique = true)
    @NotBlank(message = "email is required")
    @Email
    private String email;

    @Nullable
    @Pattern(regexp = "\\d{10,15}", message = "CMND/CCCD (CitizenIdentityCard) must be at between 10 and 15 numbers")
    private String citizenIdentityCard;     //dành cho staff, driver

    @ValueRole
    private String role;  //DRIVER, CUSTOMER, STAFF, ADMIN

    private int belongTo;    //chứa idStation
}