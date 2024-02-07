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
public class StationDTOview {

    private int idStation;
    @NotBlank(message =  "Station name is requried")
    @Size(min = 5, message = "Station name must be at least 5 characters long")
    private String name;

    @NotBlank(message =  "Address is requried")
    @Size(min = 5, message = "Address must be at least 5 characters long")
    private String address;
    @NotBlank(message =  "Province is requried")
    @Size(min = 5, message = "Province must be at least 5 characters long")
    private String province;
    @NotBlank(message = "Status is required")
    private String location;    //dùng cho gg map
    @ValueStatus
    private String status;  //active/ deactive
}
