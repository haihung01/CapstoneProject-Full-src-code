package com.example.triptix.Repository;

import com.example.triptix.DTO.Route.TicketTypeDTOcreate;
import com.example.triptix.Model.Route;
import com.example.triptix.Model.Ticket;
import com.example.triptix.Model.TicketType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketTypeRepo extends JpaRepository<TicketType, Integer> {

    @Query("select t from TicketType t where t.route.idRoute = ?1")
    List<TicketType> findByRoute(int idRoute);


    @Query(value = "SELECT * \n" +
            "FROM triptix.ticket_type\n" +
            "JOIN triptix.ticket_type_in_trip ON triptix.ticket_type_in_trip.ticket_type_id_ticket_type = triptix.ticket_type.id_ticket_type\n" +
            "JOIN triptix.trip ON triptix.ticket_type_in_trip.trip_id_trip = triptix.trip.id_trip\n" +
            "WHERE triptix.trip.id_trip = ?1 AND triptix.trip.id_route = ?2;", nativeQuery = true)
    List<TicketType> findByIdTripAndIdRoute(int idTrip, int idRoute);



    @Query("SELECT t FROM TicketType t " +
            "JOIN TicketTypeInTrip ttt ON ttt.ticketType.idTicketType = t.idTicketType " +
            "JOIN Trip tr ON ttt.trip.idTrip = tr.idTrip " +
            "WHERE tr.idTrip = ?1 AND tr.route.idRoute = ?2")
    List<TicketType> findByIdTripAndIdRoute1(int idTrip, int idRoute);
}
