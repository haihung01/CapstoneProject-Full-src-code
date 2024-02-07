package com.example.triptix.DTO.Vehicle;

import com.example.triptix.Util.ValidData.ValueStatus;
import com.example.triptix.Util.ValidData.ValueType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class VehicleDTOchangeStatus {
    private int idBus;

    @ValueStatus
    private String status;
}
