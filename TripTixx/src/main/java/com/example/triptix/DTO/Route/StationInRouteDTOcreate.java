package com.example.triptix.DTO.Route;

import com.example.triptix.DTO.Station.StationDTOview;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StationInRouteDTOcreate {


    private int orderInRoute;

    private int idStation;

    @Min(value = 0, message = "Khoảng cách từ trạm 1 tới trạm 2 ít nhất 0km")
    private int distance;


}
