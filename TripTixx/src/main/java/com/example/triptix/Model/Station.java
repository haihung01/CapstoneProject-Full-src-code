package com.example.triptix.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idStation;

    private String name;

    private String address;

    private String province;

    private String status;  //active/ deactive

    private String location;    //dùng cho gg map

    @OneToMany(mappedBy = "station")
    private List<StationInRoute> stationInRoutes;

    @OneToMany(mappedBy = "earlyOnStation")
    private List<TicketType> earlyOnTicketTypes;

    @OneToMany(mappedBy = "lateOffStation")
    private List<TicketType> lateOffTicketTypes;

    @OneToMany(mappedBy = "station")
    private List<Vehicle> vehicles;

    @OneToMany(mappedBy = "station")
    private List<UserSystem> drivers;

    @OneToMany(mappedBy = "onStation")
    private List<Ticket> ticketsOnStation;

    @OneToMany(mappedBy = "offStation")
    private List<Ticket> ticketsOffStation;
}
