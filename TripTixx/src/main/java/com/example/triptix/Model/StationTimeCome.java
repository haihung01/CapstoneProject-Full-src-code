package com.example.triptix.Model;

import com.example.triptix.Model.key.StationTimeComeKey;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Time;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class StationTimeCome {
    @EmbeddedId
    private StationTimeComeKey stationTimeComeKey;

    private Time timeCome;

    @ManyToOne
    @MapsId("idStationInRoute")
    private StationInRoute stationInRoute;

    @ManyToOne
    @MapsId("idTrip") //map vs tên biến trong key (ko phải tên column trong key)
    private Trip trip;
}
