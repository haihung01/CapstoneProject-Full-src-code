package com.example.triptix.Repository;

import com.example.triptix.Model.StationInRoute;
import org.checkerframework.framework.qual.QualifierArgument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StationInRouteRepo extends JpaRepository<StationInRoute, Integer> {
    @Query("select  t from StationInRoute  t where t.route.idRoute = ?1 order by  t.orderInRoute")
    List<StationInRoute> findByRoute(int idRoute);

    @Query("Select t from StationInRoute t where t.station.idStation = ?1 and t.route.idRoute = ?2")
    StationInRoute findByIdStationAndRoute(int earlyOn, int idRoute);

    @Query("select t from StationInRoute t where t.route.idRoute = ?1 and t.station.idStation = ?2")
    StationInRoute findByRouteAndIdStation(int idRoute, int idStation);

    @Query("Select t from StationInRoute t where t.station.idStation = ?1 and t.route.idRoute = ?2")
    StationInRoute findByIdStationAndIdRoute(int idStation, int idRoute);
  
//    @Query(value = "SELECT station_id_station FROM triptix.station_in_route \n" +
//            "where route_id_route in (SELECT id_route FROM triptix.trip where id_trip = ?1) \n" +
//            "order by order_in_route", nativeQuery = true)
    @Query("select t.station.idStation from StationInRoute t where t.route.idRoute in (select t1.route.idRoute from Trip t1 where t1.idTrip = ?1) " +
            "order by t.orderInRoute")
    List<Integer> findListIdStationAscByIdTrip(int idTrip);

    @Query(value = "SELECT order_in_route FROM triptix.station_in_route where id_route = ?1 and id_station = ?2", nativeQuery = true)
    Integer findOrderByIdRouteAndIdStation(Integer idRouteIncludeBothStation, Integer integer);
  
}
