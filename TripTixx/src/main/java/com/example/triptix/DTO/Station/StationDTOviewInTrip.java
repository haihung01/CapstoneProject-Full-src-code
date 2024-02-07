package com.example.triptix.DTO.Station;

import com.example.triptix.Util.ValidData.ValueStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StationDTOviewInTrip {
    private int idStation;

    private String name;

    private String address;

    private String province;
}
