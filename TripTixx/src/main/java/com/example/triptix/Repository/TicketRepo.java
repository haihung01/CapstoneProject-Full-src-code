package com.example.triptix.Repository;

import com.example.triptix.Model.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepo extends JpaRepository<Ticket, Integer> {
  
   @Query("Select t from Ticket t where t.seatName like ?1 and t.trip.idTrip = ?2")
    Ticket findBySeatNameAndIdTrip(String seatName, int idTrip);


   @Query("select t from Ticket t where t.ticketCode = ?1")
    List<Ticket> findByTicketCode(String code);

    @Query("select t from Ticket t where t.booking.idBooking = ?1")
    List<Ticket> findByIdBooking(int idBooking);

    @Query("select t from Ticket t where t.status = 'FINISHED' or t.status = 'NO_SHOW'")
    List<Ticket> findAllByBookingStatusFinish();

    @Query(value = "SELECT  *  FROM triptix.ticket\n" +
            "join triptix.trip on triptix.trip.id_trip = triptix.ticket.id_trip\n" +
            "WHERE triptix.ticket.status IN ('FINISHED', 'NO_SHOW')\n" +
            "and triptix.trip.departure_date like :queryTime", nativeQuery = true)
    List<Ticket> findAllByTicketStatusFinishAndTimeRange(String queryTime);

    @Query(value = "select triptix.booking.id_customer, CAST(SUM(triptix.ticket.price) as char) as total_spent\n" +
            "from triptix.ticket\n" +
            "join triptix.booking on triptix.ticket.id_booking = triptix.booking.id_booking\n" +
            "where triptix.ticket.status != 'CANCELED'\n" +
            "group by triptix.booking.id_customer\n" +
            "order by SUM(triptix.ticket.price) desc limit 10"
            , nativeQuery = true)
    List<Object[]> getTop10CustomersByTotalSpent();


    @Query(value = "SELECT * FROM triptix.ticket\n" +
            "join triptix.booking on triptix.booking.id_booking = triptix.ticket.id_booking" , nativeQuery = true)
    List<Ticket> findAll1();

    @Query("select  t from Ticket t where t.trip.idTrip = ?1 and t.booking.idBooking = ?2 and t.status = ?3")
    List<Ticket> findByTrip_IdTripAndBooking_IdBookingAndStatus(Integer idTrip, Integer idBooking, String status);

    @Query("select t from Ticket t where t.trip.idTrip = ?1 and t.booking.idBooking = ?2 order by t.trip.departureDate")
    Page<Ticket> findByTrip_IdTripAndBooking_IdBooking(Integer idTrip, Integer idBooking, Pageable pageable);


    @Query("select t from Ticket t where t.trip.idTrip = ?1 and t.status = ?2")
    List<Ticket> findByTrip_IdTripAndStatus(Integer idTrip, String status);

    @Query("select t from Ticket t where t.booking.idBooking = ?1 and t.status = ?2 ")
    List<Ticket> findByBooking_IdBookingAndStatus(Integer idBooking, String status);

    @Query("select t from Ticket t where t.trip.idTrip = ?1 order by t.trip.departureDate")
    Page<Ticket> findByTrip_IdTrip(Integer idTrip, Pageable pageable);

    @Query("select t from Ticket t where t.booking.idBooking = ?1 order by t.trip.departureDate")
    Page<Ticket> findByBooking_IdBooking(Integer idBooking, Pageable pageable);

    @Query("select t from Ticket t where t.status = ?1 order by t.trip.departureDate")
    Page<Ticket> findByStatus(String status, Pageable pageable);
  
    @Query(value = "SELECT seat_name FROM triptix.ticket where id_trip = ?1 and id_on_station = ?2 and id_off_station = ?3 and status = 'PAID'", nativeQuery = true)
    List<String> findSeatNameBusyByIdTripAndIdCoupleStation(int idTrip, Integer staionOn, Integer stationOff);

    @Query("select t from Ticket t where t.trip.idTrip = ?1 and t.booking.idBooking = ?2 and t.status = ?3 " +
            "and t.booking.customer.idUserSystem = ?4 order by t.trip.departureDate")
    Page<Ticket> findByTrip_IdTripAndBooking_IdBookingAndStatusAndBooking_Customer_IdCustomer(Integer idTrip, Integer idBooking, String status, Integer idCustomer, Pageable pageable);

    @Query("select t from Ticket t where t.trip.idTrip = ?1 " +
            "and t.booking.idBooking = ?2 and t.booking.customer.idUserSystem = ?3" +
            "order by t.trip.departureDate")
    Page<Ticket> findByTrip_IdTripAndBooking_IdBookingAndBooking_Customer_IdCustomer(Integer idTrip, Integer idBooking, Integer idCustomer, Pageable pageable);

    @Query("select t from Ticket t where t.trip.idTrip = ?1 and t.status = ?2 " +
            "and t.booking.customer.idUserSystem = ?3 order by t.trip.departureDate")
    Page<Ticket> findByTrip_IdTripAndStatusAndBooking_Customer_IdCustomer(Integer idTrip, String status, Integer idCustomer, Pageable pageable);

    @Query("select t from Ticket t where t.booking.idBooking = ?1 and t.status = ?2" +
            " and t.booking.customer.idUserSystem = ?3 order by t.trip.departureDate")
    Page<Ticket> findByBooking_IdBookingAndStatusAndBooking_Customer_IdCustomer(Integer idBooking, String status, Integer idCustomer, Pageable pageable);


    @Query("select t from Ticket t where t.trip.idTrip = ?1 and t.booking.customer.idUserSystem = ?2 order by t.trip.departureDate")
    Page<Ticket> findByTrip_IdTripAndBooking_Customer_IdCustomer(Integer idTrip, Integer idCustomer, Pageable pageable);

    @Query("select t from Ticket t where t.booking.idBooking = ?1 and t.booking.customer.idUserSystem = ?2 order by t.trip.departureDate")
    Page<Ticket> findByBooking_IdBookingAndBooking_Customer_IdCustomer(Integer idBooking, Integer idCustomer, Pageable pageable);


    @Query("select t from Ticket t where t.status = ?1 and t.booking.customer.idUserSystem = ?2 order by t.trip.departureDate")
    Page<Ticket> findByStatusAndBooking_Customer_IdCustomer(String status, Integer idCustomer, Pageable pageable);


    @Query("select t from Ticket t where t.booking.customer.idUserSystem = ?1 order by t.trip.departureDate")
    Page<Ticket> findByBooking_Customer_IdCustomer(Integer idCustomer, Pageable pageable);

    @Query("select t from Ticket t order by t.trip.departureDate")
    Page<Ticket> findAllOrderByDepartureDate(Pageable pageable);

   @Query("select t from Ticket t where t.trip.idTrip = ?1 and t.ticketCode = ?2")
   Ticket findByIdTripAndTicketCode(Integer idTrip, String ticketCode);

   @Query(value = "SELECT count(*) FROM triptix.ticket where id_booking in (SELECT id_booking FROM triptix.booking where id_customer = ?1) and status not like 'CANCELED'", nativeQuery = true)
   Integer findAllTicketCustomerBuy(int idUserSystem);

   @Query(value = "SELECT COUNT(*) FROM triptix.ticket where id_trip = ?1 and star > 0", nativeQuery = true)
   Integer findByIdTripAndCountTicket(int idTrip);
}
