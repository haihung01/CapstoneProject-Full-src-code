package com.example.triptix.Repository;

import com.example.triptix.Model.ProvinceCity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProvinceCityRepo extends JpaRepository<ProvinceCity, String> {
    List<ProvinceCity> findByType(String type, Pageable pageable);

    @Query("SELECT p FROM ProvinceCity p WHERE p.idProvince not like '%-%'")
    List<ProvinceCity> find63ProvinceInVN(Pageable pageable);

    @Query("SELECT p FROM ProvinceCity p WHERE p.name = ?1")
    ProvinceCity findByName(String name);
}
