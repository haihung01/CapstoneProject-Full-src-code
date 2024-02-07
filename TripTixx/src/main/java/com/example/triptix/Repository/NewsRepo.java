package com.example.triptix.Repository;

import com.example.triptix.Model.ConfigSystem;
import com.example.triptix.Model.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsRepo extends JpaRepository<News, Integer> {
}
