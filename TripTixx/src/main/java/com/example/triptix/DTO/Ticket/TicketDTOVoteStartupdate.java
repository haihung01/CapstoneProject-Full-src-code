package com.example.triptix.DTO.Ticket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TicketDTOVoteStartupdate {
    @NotNull(message = "id Ticket is required.")
    private int idTicket;
    @Min(value = 1, message = "average Star must be greater than 1")
    @Max(value = 5, message = "average Star must be lower than 5")
    private float star;
}
