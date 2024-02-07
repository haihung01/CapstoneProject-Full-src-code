package com.example.triptix.Model;

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
public class PaymentTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idPaymentTransaction;

    private String phoneGuest;

    private String nameGuest;

    private String emailGuest;

    private Date createDate;

    @OneToOne
    @JoinColumn(name = "idBooking")
    private Booking booking;

    @OneToMany(mappedBy = "paymentTransaction", cascade = CascadeType.ALL)
    private List<Transaction> transactions;
}
