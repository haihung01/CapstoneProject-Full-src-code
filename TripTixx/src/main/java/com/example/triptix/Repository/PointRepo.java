//package com.example.triptix.Repository;
//
//import com.example.triptix.Model.Point;
//import com.example.triptix.Model.key.PointKey;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//@Repository
//public interface PointRepo extends JpaRepository<Point, PointKey> {
//    @Query(value = "SELECT route_id_route FROM btb.point where province_city_id_province = ?2 and type = 'END' \n" +
//            "INTERSECT\n" +
//            "SELECT route_id_route FROM btb.point where (province_city_id_province = ?1 and type = 'START')", nativeQuery = true)
//    List<Integer> findBycodeDeparturePointAndcodeDestination(String codeDeparturePoint, String codeDestination, Pageable pageable);
//
//    List<Point> findByProvinceCityIdProvinceAndType(String codeProvinceCity, String type, Pageable pageable);
//}