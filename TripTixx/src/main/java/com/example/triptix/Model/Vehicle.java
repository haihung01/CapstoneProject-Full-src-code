package com.example.triptix.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Vehicle {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idBus;

    private String name;

    @Column(unique = true)
    private String licensePlates;

    private String type;    //type (limousine, giường, ghế)

    private String description; //service ( mô tả dv xe có)

    private short capacity;

    private short floor;    //số tầng của xe

    private String createdDate;

    private String updatedDate;

    private String status;

    private String imageLink;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL)
    private List<Trip> trips;

    @ManyToOne
    @JoinColumn(name = "idStation", nullable = false)
    private Station station;
}
