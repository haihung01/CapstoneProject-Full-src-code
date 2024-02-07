package com.example.triptix.Model;

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
public class Route {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idRoute;

    @ManyToOne
    @JoinColumn(name = "idAdmin")
    private UserSystem admin;

    @ManyToOne
    @JoinColumn(name = "idStartProvinceCity")
    private ProvinceCity startProvinceCity;

    @ManyToOne
    @JoinColumn(name = "idEndProvinceCity")
    private ProvinceCity endProvinceCity;

    private String name;

    private Date createdDate;

    private Date updatedDate;

    private String status;  //status (active/ deactive)

    @OneToMany(mappedBy = "route")
    private List<StationInRoute> stationInRoutes;

    @OneToMany(mappedBy = "route")
    private List<Trip> trips;

    @OneToMany(mappedBy = "route")
    private List<TicketType> ticketTypes;
}
