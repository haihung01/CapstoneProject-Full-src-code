package com.example.triptix.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class UserSystem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idUserSystem;

    private String phone;

    private String userName;

    private String fullName;

    private String password;

    private int voucherCoins;

    private String address;

    private Date birthday;

    private String gender;

    private String email;

    private java.util.Date createdDate;

    private String citizenIdentityCard; //cmnd/ cccd

    private String status;  //active, deactive

    private String role;  //DRIVER, CUSTOMER, STAFF, ADMIN

    private String fcmTokenDevide;  //firebase notification

    private int mileStone;

    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL)
//    @JsonIgnore
    private Wallet wallet;

    @OneToMany(mappedBy = "userSystem", cascade = CascadeType.ALL)
//    @JsonIgnore
    private List<Notification> notifications;

    @OneToMany(mappedBy = "staff", cascade = CascadeType.ALL)
//    @JsonIgnore
    private List<News> news;

    @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL)
//    @JsonIgnore
    private List<Trip> tripsDriver;

    @OneToMany(mappedBy = "staff", cascade = CascadeType.ALL)
//    @JsonIgnore
    private List<Trip> tripsStaff;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
//    @JsonIgnore
    private List<Booking> bookings;

    @ManyToOne
    @JoinColumn(name = "idStation", nullable = true)
    private Station station;
}
