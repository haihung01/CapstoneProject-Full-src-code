package com.example.triptix.Repository;

import com.example.triptix.Model.StationTimeCome;
import com.example.triptix.Model.key.StationTimeComeKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Time;
import java.util.List;

@Repository
public interface StationTimeComeRepo extends JpaRepository<StationTimeCome, StationTimeComeKey> {
    @Query("select s.timeCome from StationTimeCome s where s.stationInRoute.idStationInRoute in " +
            "(select s1.idStationInRoute from StationInRoute s1 where s1.route.idRoute = ?1 and s1.station.idStation = ?2) " +
            "and s.trip.idTrip = ?3")
    Time findTimeComeByIdStationAndIdTrip(int idROute, int idStation, int idTrip);
    @Query("select s.timeCome from StationTimeCome s where s.stationInRoute.idStationInRoute = ?1 and s.trip.idTrip = ?2")
    Time findByIdStationTimeCome(int idStationInRoute, int idTrip);

    @Query("select s from StationTimeCome s where s.trip.idTrip = ?1")
    List<StationTimeCome> findByTripIdTrip(int idTrip);
}
