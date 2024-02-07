package com.example.triptix.DTO.Trip;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class TicketTypeInTripDTOcreate {

    private int idTicketType;

    /*private int idTrip;*/

    private double price;

}
