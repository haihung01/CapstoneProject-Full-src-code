package com.example.triptix.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idBooking;

    @ManyToOne
    @JoinColumn(name = "idCustomer", nullable = true)
    private UserSystem customer;

    @ManyToOne
    @JoinColumn(name = "idTrip", nullable = true)
    private Trip trip;

    private double totalPrice;

    private short totalTicket;

    private Date createdDate;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    private List<Ticket> tickets;

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL)
//    @JsonIgnore
    private PaymentTransaction paymentTransaction;
}
