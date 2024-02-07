package com.example.triptix.DTO.UserSystem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserSystemDTOview {

    private int idUserSystem;

    private String phone;

    private String userName;

    private String fullName;

    private int voucherCoins;

    private int coins;

    private String address;

    private Long birthdayLong;  //timestamp là kiểu  tính bằng miliseconds từ 1/1/1970  (FE no)

    private String gender;

    private String email;

    private Date createdDate;

    private String citizenIdentityCard; //cmnd

    private String status;  //active, deactive

    private String role;  //DRIVER, CUSTOMER, STAFF, ADMIN

    private String fcmTokenDevide;  //firebase notification

    private int mileStone;

    private int belongTo;

    private String nameStationBelong;
}