package com.example.triptix.DTO.Transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TransactionDTOview {
    private int idTransaction;

    private int amount;

    private Long dateTimeStamp;

    private String description;

    private String bankCode;
}