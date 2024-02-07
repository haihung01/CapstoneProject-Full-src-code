package com.example.triptix.Model.key;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Embeddable
public class TicketTypeInTripKey implements Serializable {
    @Column(name = "idTicketType")
    private int idTicketType;

    @Column(name = "idTrip")
    private int idTrip;
}
