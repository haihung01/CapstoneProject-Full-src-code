package com.example.triptix.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Time;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Trip {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idTrip;

    @ManyToOne
    @JoinColumn(name = "idRoute")
    private Route route;

    @ManyToOne
    @JoinColumn(name = "idDriver")
    private UserSystem driver;

    @ManyToOne
    @JoinColumn(name = "idVehicle")
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name = "idStaff")
    private UserSystem staff;

    private java.util.Date departureDate;

    private java.util.Date endDate;

    private float avarageStar;

    private String adminCheck;

    private String repeatCycle;

    private java.util.Date createdDate;

    private java.util.Date updatedDate;

    private String status;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL)
//    @JsonIgnore
    private List<Ticket> tickets;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL)
    private List<TicketTypeInTrip> ticketTypeInTrips;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL)
    private List<Booking> bookings;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.REMOVE)
    private List<StationTimeCome> stationTimeComes;

    @Override
    public String toString() {
        return "Trip{" +
                "idTrip=" + idTrip +
                ", route=" + route.getIdRoute() +
                ", driver=" + driver.getIdUserSystem() +
                ", vehicle=" + vehicle.getIdBus() +
                ", staff=" + staff.getIdUserSystem() +
                ", departureDate=" + departureDate +
                ", endDate=" + endDate +
                ", avarageStar=" + avarageStar +
                ", adminCheck='" + adminCheck + '\'' +
                ", repeatCycle='" + repeatCycle + '\'' +
                ", createdDate=" + createdDate +
                ", updatedDate=" + updatedDate +
                ", status='" + status + '\'' +
                ", tickets=" +
                ", ticketTypeInTrips=" +
                ", bookings=" +
                '}';
    }
}
