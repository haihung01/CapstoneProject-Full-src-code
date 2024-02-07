package com.example.triptix.Model.key;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Embeddable
public class StationTimeComeKey implements Serializable {
    @Column(name = "idStationInRoute")
    private int idStationInRoute;

    @Column(name = "idTrip")
    private int idTrip;
}
