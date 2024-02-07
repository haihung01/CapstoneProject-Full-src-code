package com.example.triptix.Repository;


import com.example.triptix.Model.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleRepo  extends JpaRepository<Vehicle, Integer> {
    Page<Vehicle> findByType(String type, Pageable pageable);


    boolean existsByLicensePlates(String licensePlates);
}
