package com.example.triptix.Repository;

import com.example.triptix.Model.SpecialDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecialDayRepo extends JpaRepository<SpecialDay, Integer> {
    SpecialDay findByDate(String date);
}