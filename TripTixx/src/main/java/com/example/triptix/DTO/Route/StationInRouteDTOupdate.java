package com.example.triptix.DTO.Route;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StationInRouteDTOupdate {

    private int idStationInRoute;

    private int orderInRoute;

    private int idStation;

    @Min(value = 0, message = "Khoảng cách từ trạm 1 tới trạm 2 ít nhất 0km")
    private int distance;

}
