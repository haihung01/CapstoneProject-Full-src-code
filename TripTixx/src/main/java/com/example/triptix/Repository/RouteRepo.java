package com.example.triptix.Repository;

import com.example.triptix.Model.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteRepo extends JpaRepository<Route, Integer> {
    @Query("select t from Route t order by t.name")
    List<Route> findAllOrderByName();

//    @Query(value = "(SELECT id_route FROM triptix.station_in_route where id_station in ?1 group by id_route)\n" +
//            "union distinct\n" + //cộng, dồn data của 2 table vào vs nhau, vs đk data 2 table giả có các cột giống nhau
//            "(SELECT id_route FROM triptix.station_in_route where id_station in ?2 group by id_route)", nativeQuery = true)
    @Query(value = "select id_route\n" +
            "from (\n" +
            "(SELECT id_route FROM triptix.station_in_route where id_station in ?1 group by id_route)\n" +
            "union all\n" +     //gộp hết lại
            "(SELECT id_route FROM triptix.station_in_route where id_station in ?2 group by id_route)\n" +
            ") as tmp\n" +
            "group by id_route \n" +
            "having count(*) > 1", nativeQuery = true)
    List<Integer> findIdRouteIncludeBothStation(List<Integer> listIdStationDeparture, List<Integer> listIdStationDestination);

    @Query("select  t from Route t where t.startProvinceCity.idProvince = ?1 and t.endProvinceCity.idProvince = ?2 and t.name = ?3 order by t.name")
    List<Route> findByCodeDepartureAndCodeEndAndName(String codeDepartureDate, String codeEndDate, String name);

    @Query("select t from Route t where t.startProvinceCity.idProvince  = ?1 and t.name =?2 order by t.name")
    List<Route> findByCodeDepartureAndName(String codeDepartureDate, String name);

    @Query("select t from Route t where t.endProvinceCity = ?1 and t.name = ?2 order by t.name")
    List<Route> findByCodeEndDateAndName(String codeEndDate, String name);

    @Query("select t from Route t where t.startProvinceCity.idProvince = ?1 and t.endProvinceCity.idProvince =?2 order by t.name")
    List<Route> findByCodeDepartureAndEndDate(String codeDepartureDate, String codeEndDate);

    @Query("select t from Route t where t.startProvinceCity.idProvince = ?1 order by t.name")
    List<Route> findByCodeDeparture(String codeDepartureDate);

    @Query("select t from Route t where t.endProvinceCity.idProvince = ?1 order by t.name")
    List<Route> findByCodeEndDate(String codeEndDate);

    @Query("select t from Route t where t.name = ?1 order by t.name")
    List<Route> findByName(String name);
}
