package com.example.triptix.Repository;

import com.example.triptix.Model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepo extends JpaRepository<Booking,Integer> {


    @Query("select  t from Booking t where t.trip.idTrip = ?1 and t.customer.idUserSystem = ?2")
    List<Booking> findByIdTripAndIdCustomer(Integer idTrip, Integer idCustomer);

    @Query("select t from Booking t where t.trip.idTrip = ?1")
    List<Booking> findByIdTrip(Integer idTrip);

    @Query("select t from Booking t where t.customer.idUserSystem = ?1")
    List<Booking> findByIdCustomer(Integer idCustomer);
}
