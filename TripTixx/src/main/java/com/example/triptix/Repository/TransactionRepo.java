package com.example.triptix.Repository;

import com.example.triptix.Model.Transaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepo extends JpaRepository<Transaction, Integer> {
    @Query("select o from Transaction o where o.wallet.customer.idUserSystem = ?1")
    List<Transaction> findAllByCustomerId(int idCustomer, Pageable pageable);
}
