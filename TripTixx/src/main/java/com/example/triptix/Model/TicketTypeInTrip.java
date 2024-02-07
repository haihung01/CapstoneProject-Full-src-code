package com.example.triptix.Model;

import com.example.triptix.Model.key.TicketTypeInTripKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class TicketTypeInTrip {
    @EmbeddedId
    private TicketTypeInTripKey ticketTypeInTripKey;

    private double price;

    @ManyToOne
    @MapsId("idTicketType")
    private TicketType ticketType;

    @ManyToOne
    @MapsId("idTrip")
    private Trip trip;
}
