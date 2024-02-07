package com.example.triptix.DTO.Transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TransactionDTocreate {
    private int amount;

    private Date date;

    private String description;

    private String bankCode;
}
