package com.example.triptix.Repository;

import com.example.triptix.Model.ConfigSystem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface ConfigSystemRepo extends JpaRepository<ConfigSystem, Integer> {

    @Query("select  t from ConfigSystem t where t.idConfigSystem = 13")
    ConfigSystem findByTimeRefund();
    @Query("select  t from ConfigSystem t where t.idConfigSystem = 14")
    ConfigSystem findByTimeCanNotRefund();
    @Query("select  t from ConfigSystem t where t.idConfigSystem = 15")
    ConfigSystem findByConfigSystemTimeCanNotBook();

    @Query("select  t from ConfigSystem t where t.idConfigSystem = 16")
    ConfigSystem findBySeatPerBooking();
    @Query("select  t from ConfigSystem t where t.idConfigSystem = 17")
    ConfigSystem findByPerCancelTicketBeforeTimeCannotRefund();
    @Query("select  t from ConfigSystem t where t.idConfigSystem = 18")
    ConfigSystem findByPerCancelTicket();

    @Query("select t.value from ConfigSystem t where t.idConfigSystem = 3")
    Integer findMileStone1();

    @Query("select t.value from ConfigSystem t where t.idConfigSystem = 4")
    int findMileStone2();

    @Query("select t.value from ConfigSystem t where t.idConfigSystem = 5")
    int findMileStone3();

    @Query("select t.value from ConfigSystem t where t.idConfigSystem = 6")
    int findCrossMileStone();

    @Query("select t.value from ConfigSystem t where t.idConfigSystem = 7")
    int findCoinsMileStone1();

    @Query("select t.value from ConfigSystem t where t.idConfigSystem = 8")
    int findCoinsMileStone2();

    @Query("select t.value from ConfigSystem t where t.idConfigSystem = 9")
    int findCoinsMileStone3();

    @Query("select t.value from ConfigSystem t where t.idConfigSystem = 10")
    int findCoinsCrossMileStone();
}