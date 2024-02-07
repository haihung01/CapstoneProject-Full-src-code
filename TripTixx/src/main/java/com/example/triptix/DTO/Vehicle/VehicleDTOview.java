package com.example.triptix.DTO.Vehicle;

import com.example.triptix.DTO.Station.StationDTOview;
import com.example.triptix.Model.Station;
import com.example.triptix.Util.ValidData.ValueStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class VehicleDTOview {
    private int idBus;

    private String name;

    private String licensePlates;

    private String type;    //type (limousine, giường)

    private String description; //service ( mô tả dv xe có)

    private short capacity;

    private short floor;    //số tầng của xe

    private String status;

    private String createdDate;

    private String updatedDate;

    private String imgLink;

    private StationDTOview station;
}
