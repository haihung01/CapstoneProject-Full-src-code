package com.example.triptix.Repository;

import com.example.triptix.Model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepo extends JpaRepository<Wallet, Integer> {
  
    @Query("select t from Wallet t where t.customer.idUserSystem = ?1")
    Wallet findByIdCustomer(int idUserSystem); 
  
//     @Query("Select t from Wallet t where t.customer.idUserSystem = ?1")
//     Wallet findByIdCustomer (int idCustomer);
  
}
