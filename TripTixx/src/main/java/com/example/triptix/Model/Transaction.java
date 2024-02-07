package com.example.triptix.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idTransaction;

    @ManyToOne
    @JoinColumn(name = "idWallet", nullable = true)
    private Wallet wallet;

    private int amount;

    private Date date;

    private String description;

    private String bankCode;

    @ManyToOne
    @JoinColumn(name = "idPaymentTransaction", nullable = true)
    private PaymentTransaction paymentTransaction;
}
