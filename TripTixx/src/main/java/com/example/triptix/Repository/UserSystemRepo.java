package com.example.triptix.Repository;

import com.example.triptix.Model.UserSystem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserSystemRepo extends JpaRepository<UserSystem, Integer> {
    UserSystem findByUserName(String userName);

    @Query("Select t from UserSystem t where t.role like 'ROLE_ADMIN'")
    UserSystem findByRoleAdmin();
  
    @Query("Select t from UserSystem t where t.role != 'ROLE_ADMIN'")
    Page<UserSystem> findAllNotIncludeAdmin(Pageable pageable);

    Page<UserSystem> findByRole(String role, Pageable pageable);

    UserSystem findByPhoneAndRole(String phone, String role);

    UserSystem findByEmailAndRole(String email, String role);

    UserSystem findByCitizenIdentityCardAndRole(String citizenIdentityCard, String role);

    @Query(value = "SELECT * FROM triptix.user_system where DATE_FORMAT(birthday, '%m-%d') like ?1 and role = ?3 and status = ?2", nativeQuery = true)
    List<UserSystem> findByBirthdayAndStatusAndRole(String birthday, String status, String role);


    @Query("select  t from UserSystem t where t.idUserSystem = ?1 and t.role like 'ROLE_STAFF' ")
    UserSystem findByIdStaff(int idStaff);

    @Query("select  t from UserSystem t where t.idUserSystem = ?1 and t.role = 'ROLE_DRIVER'")
    UserSystem findByIdDriver(int idDriver);

    @Query("Select t from UserSystem t where t.idUserSystem = ?1 and t.role in ('ROLE_CUSTOMER','ROLE_STAFF')")
    UserSystem findByIdCustomerAndStaff(int id);

    @Query("select t from UserSystem t where t.idUserSystem = ?1 and t.role = 'ROLE_CUSTOMER'")
    UserSystem findByIdCustomer(int idUserSystem);

    @Query("select  t from UserSystem  t where t.idUserSystem = ?1")
    UserSystem findByIdCustomerNOTROLE(int idUserSystem);
}
