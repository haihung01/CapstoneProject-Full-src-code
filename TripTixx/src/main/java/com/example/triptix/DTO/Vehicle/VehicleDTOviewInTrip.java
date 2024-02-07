package com.example.triptix.DTO.Vehicle;

import com.example.triptix.DTO.Station.StationDTOview;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class VehicleDTOviewInTrip {
    private int idBus;

    private String name;

    private String licensePlates;

    private String type;    //type (limousine, giường)

    private String description; //service ( mô tả dv xe có)

    private short capacity;

    private short floor;    //số tầng của xe

    private String imgLink;
}
