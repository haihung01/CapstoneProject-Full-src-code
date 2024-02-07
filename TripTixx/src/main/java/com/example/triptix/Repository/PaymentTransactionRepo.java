package com.example.triptix.Repository;

import com.example.triptix.Model.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentTransactionRepo extends JpaRepository<PaymentTransaction, Integer> {
}
