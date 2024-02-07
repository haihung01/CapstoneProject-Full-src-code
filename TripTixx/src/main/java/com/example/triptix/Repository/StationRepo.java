package com.example.triptix.Repository;

import com.example.triptix.Model.Station;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StationRepo extends JpaRepository<Station, Integer> {
    @Query(value = "SELECT id_station FROM triptix.station where province = ?1", nativeQuery = true)
    List<Integer> findIdStationByProvince(String province);
    Page<Station> findByNameContaining(String name, Pageable pageable);

    @Query("Select t.idStation from Station t where t.address like %?1%")
    List<Integer> findIdStationByAddressContaining(String address);

    @Query("Select t from Station t where t.province like ?1")
    Page<Station> findByProvinceContaining2(String province, Pageable pageable);

    List<Station> findByAddressContaining(String address, Pageable pageable);

    @Query("Select t from Station t where t.idStation = ?1")
    Station findByIdStation(int idStation);

    @Query("SELECT t from Station t order by t.province")
    Page<Station> findAllOrderyByProvince(Pageable pageable);

    Page<Station> findByNameContainingAndProvinceContaining(String name, String province, Pageable pageable);
}
