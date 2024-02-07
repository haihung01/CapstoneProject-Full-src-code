package com.example.triptix.DTO.Booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class VehicleDTOviewInBooking {
    private int idBus;

    private String name;

    private String licensePlates;

    private String type;    //type (limousine, giường)

    private String description; //service ( mô tả dv xe có)

    private short capacity;

    private short floor;    //số tầng của xe

    private String status;

    private String imgLink;
}
