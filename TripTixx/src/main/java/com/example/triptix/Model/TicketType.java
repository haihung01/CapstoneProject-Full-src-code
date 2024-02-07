package com.example.triptix.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class TicketType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idTicketType;

    private String name;

    private double defaultPrice;

    @ManyToOne
    @JoinColumn(name = "idRoute")
    private Route route;

    @ManyToOne
    @JoinColumn(name = "idEarlyOnStation")
    private Station earlyOnStation;

    @ManyToOne
    @JoinColumn(name = "idLateOffStation")
    private Station lateOffStation;

    @OneToMany(mappedBy = "ticketType")
    private List<TicketTypeInTrip> ticketTypeInTrips;

    @OneToMany(mappedBy = "ticketType")
    private List<Ticket> tickets;
}
