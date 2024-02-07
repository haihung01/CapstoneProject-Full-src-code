package com.example.triptix.DTO.UserSystem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserSystemDTOviewInTrip {
    private int idUserSystem;
    private String phone;
    private String fullName;
    private String email;
    private String gender;
    private String address;
    private Long birthdayLong;  //timestamp là kiểu  tính bằng miliseconds từ 1/1/1970  (FE no)
}
