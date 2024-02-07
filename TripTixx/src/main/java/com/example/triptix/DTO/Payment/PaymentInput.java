package com.example.triptix.DTO.Payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentInput {
    @NotNull(message = "idCustomer is required")
    private int idCustomer;

    @Min(value = 10000, message = "amount must be at least 10.000 vnđ")
    private int amount;
}
