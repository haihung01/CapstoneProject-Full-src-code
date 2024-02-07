package com.example.triptix.DTO.UserSystem;

import com.example.triptix.Util.ValidData.SexGender.ValueSexGender;
import com.example.triptix.Util.ValidData.Status.ValueStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserSystemDTOupdateTimeStamp2 {

    private int idUserSystem;

    @NotBlank(message = "phone is required")
    @Pattern(regexp = "\\d{10}", message = "Invalid phone number format, must be 10 numbers")
    private String phone;

    @NotBlank(message = "fullName is required")
    @Size(min = 5, message = "fullName must be at least 5 characters long")
    private String fullName;

    @NotBlank(message = "address is required")
    private String address;

    @NotBlank(message = "birthday is required")
    @Pattern(regexp = "^(\\d{2}-\\d{1,2}-\\d{4})$", message = "Invalid time format. Please use 'dd-M-yyyy'")
    private String sinhnhat;

    @NotBlank(message = "sex is required")
    @ValueSexGender
    private String gender;

    @NotBlank(message = "email is required")
    @Email
    private String email;

    @Nullable
    @Pattern(regexp = "\\d{9,12}", message = "CMND/CCCD (CitizenIdentityCard) must be at between 9 and 12 numbers")
    private String citizenIdentityCard; //cmnd

    @ValueStatus
    @JsonIgnore
    private String status;  //active, deactive

    private int belongTo;
}
