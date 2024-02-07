package com.example.triptix.DTO.Ticket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TicketDTOCancelupdate {

    @NotNull(message = "id Ticket is required")
    private int idTicket;

}
