package com.example.triptix.Repository;

import com.example.triptix.Model.TicketTypeInTrip;
import com.example.triptix.Model.key.TicketTypeInTripKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketTypeInTripRepo extends JpaRepository<TicketTypeInTrip, TicketTypeInTripKey> {
    @Query("Select t from TicketTypeInTrip t where t.ticketType.idTicketType = ?1")
    TicketTypeInTrip findByIdTicketType(int idTicketType);

    @Query("select t from TicketTypeInTrip t where t.trip.idTrip = ?1")
    List<TicketTypeInTrip> findByIdTrip(int idTrip);
}
