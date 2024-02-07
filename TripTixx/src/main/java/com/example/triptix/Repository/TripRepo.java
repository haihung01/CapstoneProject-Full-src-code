package com.example.triptix.Repository;

import com.example.triptix.Model.Trip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripRepo extends JpaRepository<Trip, Integer> {

    List<Trip> findByRepeatCycle(String repeatCycle);

    @Query(value = "SELECT * FROM triptix.trip where (id_vehicle = ?1 or id_driver = ?2) and departure_date like ?3 and ( ( admin_check = 'PENDING' and status = 'READY' ) or ( admin_check = 'ACCEPTED' and status = 'READY' ) or ( admin_check = 'ACCEPTED' and status = 'RUNNING' ))", nativeQuery = true)
    List<Trip> findByBusAndDriver(int idBus , int idDriver, String dayRun);

    boolean existsByRepeatCycle(String idRepeatCycle);

    @Query("select t from Trip t order by case when t.status = 'RUNNING' then 1 when t.status = 'READY' then 2 when t.status = 'FINISHED' then 3 when t.status = 'CANCELED' then 4 else 5 end")
    Page<Trip> findAllByOrderStatus(Pageable pageable) ;

    @Query("SELECT t FROM Trip t WHERE t.route.idRoute = :routeId AND DATE_FORMAT(t.departureDate, '%Y-%m-%d') like %:startTime% AND t.status like :status AND t.adminCheck like :adminCheck")
    Page<Trip> findByRouteAndStartTimeContainingAndStatusAndAdminCheck(int routeId, String startTime, String status, String adminCheck, Pageable pageable);

    @Query("SELECT t.idTrip FROM Trip t WHERE t.route.idRoute = :routeId AND DATE_FORMAT(t.departureDate, '%d-%m-%Y') like %:startTime% AND t.status like :status AND t.adminCheck like :adminCheck")
    List<Integer> findIdTripByRouteAndStartTimeContainingAndStatusAndAdminCheck(int routeId, String startTime, String status, String adminCheck, Pageable pageable);


    @Query("SELECT t FROM Trip t WHERE t.route.idRoute = :routeId AND DATE_FORMAT(t.departureDate, '%Y-%m-%d') like %:startTime% AND t.status like :status")
    Page<Trip> findByRouteAndStartTimeContainingAndStatus(int routeId, String startTime, String status, Pageable pageable);

    @Query("SELECT t FROM Trip t WHERE t.route.idRoute = :routeId AND DATE_FORMAT(t.departureDate, '%Y-%m-%d')  like %:startTime% AND t.adminCheck like :adminCheck")
    Page<Trip> findByRouteAndStartTimeContainingAndAdminCheck(int routeId, String startTime, String adminCheck, Pageable pageable);

    @Query("SELECT t FROM Trip t WHERE t.route.idRoute = :routeId AND t.status like :status AND t.adminCheck like :adminCheck")
    Page<Trip> findByRouteAndStatusAndAdminCheck(int routeId, String status, String adminCheck, Pageable pageable);

    @Query("SELECT t FROM Trip t WHERE t.route.idRoute = :routeId AND DATE_FORMAT(t.departureDate, '%Y-%m-%d') like %:startTime%")
    Page<Trip> findByRouteAndStartTimeContaining(int routeId, String startTime, Pageable pageable);

    @Query("SELECT t FROM Trip t WHERE t.route.idRoute = :routeId AND t.status like :status")
    Page<Trip> findByRouteAndStatus(int routeId, String status, Pageable pageable);

    @Query("SELECT t FROM Trip t WHERE t.route.idRoute = :routeId AND t.adminCheck like :adminCheck")
    Page<Trip> findByRouteAndAdminCheck(int routeId, String adminCheck, Pageable pageable);

    @Query("SELECT t FROM Trip t WHERE DATE_FORMAT(t.departureDate, '%Y-%m-%d') like %:startTime% AND t.status like :status")
    Page<Trip> findByStartTimeContainingAndStatus(String startTime, String status, Pageable pageable);

    @Query("SELECT t FROM Trip t WHERE DATE_FORMAT(t.departureDate, '%Y-%m-%d') like %:startTime% AND t.adminCheck like :adminCheck")
    Page<Trip> findByStartTimeContainingAndAdminCheck(String startTime, String adminCheck, Pageable pageable);

    @Query("SELECT t FROM Trip t WHERE t.status like :status AND t.adminCheck like :adminCheck")
    Page<Trip> findByStatusAndAdminCheck(String status, String adminCheck, Pageable pageable);

    @Query("SELECT t FROM Trip t WHERE DATE_FORMAT(t.departureDate, '%Y-%m-%d') like %:startTime%")
    Page<Trip> findByStartTimeContaining(String startTime, Pageable pageable);

    @Query("select  t from Trip t where t.route.idRoute = ?1")
    Page<Trip> findByRoute(int routeId, Pageable pageable);

    @Query("Select t from Trip t where t.status = ?1")
    Page<Trip> findByStatus(String status, Pageable pageable);

    @Query("Select t from Trip t where t.adminCheck like :adminCheck")
    Page<Trip> findByAdminCheck(String adminCheck, Pageable pageable);

    @Query("Select t from Trip t where t.driver.idUserSystem = :driverId and t.status in (:status) and DATE_FORMAT(t.departureDate, '%Y-%m-%d') like  %:startTime%  and t.adminCheck like 'ACCEPTED'")
    Page<Trip> findByIdDriverAndStatusAndStartTimeAndAdminCheckACCEPT(int driverId, List<String> status , String startTime, Pageable pageable);

    @Query("Select t from Trip t where t.driver.idUserSystem = :driverId  and DATE_FORMAT(t.departureDate, '%Y-%m-%d') like  %:startTime% and t.adminCheck like 'ACCEPTED'")
    Page<Trip> findByIdDriverAndStartTimeAndAdminCheckACCEPT(int driverId, String startTime, Pageable pageable);

    @Query("Select t from Trip t where t.driver.idUserSystem = ?1 and t.status in (?2) and t.adminCheck like 'ACCEPTED'")
    Page<Trip> findByIdDriverAndStatusAndAdminCheckACCEPT(int driverId, List<String> status, Pageable pageable);

    @Query("select  t from  Trip t where t.driver.idUserSystem = ?1 and t.adminCheck like 'ACCEPTED'")
    Page<Trip> findByIdDriverAndAdminCheckACCEPT(int driverId, Pageable pageable);

    @Query("select t from Trip t where t.driver.idUserSystem = ?1 and t.adminCheck like 'ACCEPTED' and t.status in ('FINISHED', 'CANCELED') order by t.departureDate desc ")
    Page<Trip> findTripFinishAndCancelOfDriverById(Integer driverId, Pageable pageable);

    @Query("select t from Trip t where t.driver.idUserSystem = ?1 and t.adminCheck like 'ACCEPTED' and t.status in ('READY', 'RUNNING') order by t.departureDate")
    Page<Trip> findTripReadyOfDriverById(Integer driverId, Pageable pageable);

//    @Query("select  t from Trip t where t.staff.idUserSystem = ?1")
//    @Query(value = "select * from triptix.trip t where id_staff = ?1 order by case when t.Status = 'READY' then 1 when t.Status = 'RUNNING' then 2 when t.Status = 'FINISHED' then 3 when t.Status = 'CANCELED' then 4 else 5 end;", nativeQuery = true)
    @Query("select  t from Trip t where t.staff.idUserSystem = ?1 order by case when t.status = 'READY' then 1 when t.status = 'RUNNING' then 2 when t.status = 'FINISHED' then 3 when t.status = 'CANCELED' then 4 else 5 end")
    Page<Trip> findByIdStaff(int idStaff, Pageable pageable) ;

    @Query("select  t from Trip t where t.staff.idUserSystem = ?1 and t.adminCheck like ?2")
    Page<Trip> findByIdStaffAndAdminCheck(int idStaff, String adminCheck, Pageable pageable);

    @Query("select t from Trip t where DATE_FORMAT(t.endDate, '%d-%m-%Y') like :end and t.status like :status")
//    @Query(value = "select * from triptix.trip where DATE_FORMAT(end_date, '%d-%m-%Y') like ?1 and status like ?2", nativeQuery = true)
    List<Trip> getListIdTripRunning(String end, String status);

    @Query("select t from Trip t where DATE_FORMAT(t.departureDate, '%d-%m-%Y %H:%i') like :startTime and t.status like :status and t.adminCheck like 'ACCEPTED'")
    List<Trip> getListIdTripRunningNow(String startTime, String status);

    @Query("select t from Trip t where DATE_FORMAT(t.departureDate, '%d-%m-%Y') like :startTime and t.status like 'READY' and t.adminCheck like 'ACCEPTED'")
    List<Trip> findListTripRunByDay(String startTime);

    @Query(value = "SELECT id_customer FROM triptix.booking \n" +
            "where id_trip in \n" +
            "\t(select id_trip from triptix.trip where date_format(departure_date, '%d-%m-%Y %H:%i') like ?1 and status like 'READY' and admin_check like 'ACCEPTED')\n" +
            " and id_customer is not null", nativeQuery = true)
    List<Integer> getListIdCustomerTripByDepartureDate(String format);

    @Query(value = "SELECT * FROM triptix.trip \n" +
            "where id_route in (SELECT id_route FROM triptix.route where id_start_province_city = ?1 and id_end_province_city = ?2)\n" +
            "and admin_check = 'ACCEPTED' and status = 'READY'\n" +
            "and date_format(departure_date, '%d-%m-%Y') like ?3\n" +
            "order by departure_date", nativeQuery = true)
    Page<Trip> search(String codeDeparturePoint, String codeDestination, String formattedDate, Pageable pageable);

    @Query(value = "SELECT id_trip FROM triptix.trip \n" +
            "where id_route in (SELECT id_route FROM triptix.route where id_start_province_city = ?1 and id_end_province_city = ?2)\n" +
            "and admin_check = 'ACCEPTED' and status = 'READY'\n" +
            "and date_format(departure_date, '%d-%m-%Y') like ?3\n" +
            "order by departure_date", nativeQuery = true)
    Page<Integer> searchReturnIdTrip(String codeDeparturePoint, String codeDestination, String formattedDate, Pageable pageable);

    @Query(value = "SELECT count(*) FROM triptix.ticket where id_trip = ?1 and status not like 'CANCELED'", nativeQuery = true)
    Integer getTotalCustomer(int idTrip);

    @Query(value = "SELECT avarage_star, departure_date, repeat_cycle, admin_check FROM triptix.trip where id_trip in (97952, 40094)", nativeQuery = true)
    List<List<Object>> testFindSomeAttribute();

//    @Query(value = "SELECT id_trip FROM triptix.trip where admin_check = 'PENDING' group by repeat_cycle;", nativeQuery = true)
    @Query(value = "SELECT id_trip, repeat_cycle FROM triptix.trip where admin_check = 'PENDING' and ( id_trip = repeat_cycle or repeat_cycle is null) order by departure_date;", nativeQuery = true)
    List<Integer> findIdTripPendingForAdminAccept();

    @Query("SELECT t FROM Trip t WHERE t.idTrip IN (:listIdTripPendingForAdminAccept)")
    Page<Trip> findByListIDTrip(List<Integer> listIdTripPendingForAdminAccept, Pageable pageable);

    @Query(value = "SELECT id_trip, departure_date FROM triptix.trip where repeat_cycle = ?1 order by departure_date;", nativeQuery = true)
    List<List<Object>> findIdTripAndDepartureDateByRepeatCycle(String repeatCycle);
}
