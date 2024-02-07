package com.example.triptix.Repository;

import com.example.triptix.Model.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface NotificationRepo extends JpaRepository<Notification, Integer> {
    @Query("select t from Notification t where t.userSystem.idUserSystem = ?1")
    List<Notification> findByIdUserSystem(int idUserSystem, Pageable pageable);

    @Transactional
    @Modifying
    @Query("update Notification t set t.seen = 1 where t.userSystem.idUserSystem = ?1")
    int updateSeen(int idUser);
}