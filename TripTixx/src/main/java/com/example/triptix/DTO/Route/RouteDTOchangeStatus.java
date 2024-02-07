package com.example.triptix.DTO.Route;

import com.example.triptix.Util.ValidData.ValueStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.pl.REGON;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RouteDTOchangeStatus {
    private int idRoute;

    @NotBlank(message = "Không để trống trạng thái.")
    @ValueStatus
    private String status;
}
