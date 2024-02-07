package com.example.triptix.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class StationInRoute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idStationInRoute;

    private int orderInRoute;

    private int distance;

    @ManyToOne
    @JoinColumn(name = "idRoute")
    private Route route;

    @ManyToOne
    @JoinColumn(name = "idStation")
    private Station station;

    @OneToMany(mappedBy = "stationInRoute")
    private List<StationTimeCome> stationTimeComes;
}
