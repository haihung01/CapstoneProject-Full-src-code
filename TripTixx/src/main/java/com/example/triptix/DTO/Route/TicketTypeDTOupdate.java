package com.example.triptix.DTO.Route;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TicketTypeDTOupdate {

    private int idTicketType;

    private String name;

    private int idEarlyOnStation;

    private int idLateOffStation;


    @Min(value = 0,message = "Giá tối thiểu là 0đ")
    private double defaultPrice;
}
