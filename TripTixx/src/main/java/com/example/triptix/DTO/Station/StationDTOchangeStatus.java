package com.example.triptix.DTO.Station;

import com.example.triptix.Util.ValidData.ValueStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StationDTOchangeStatus {
    private int idStation;


    @ValueStatus
    private String status;
}
