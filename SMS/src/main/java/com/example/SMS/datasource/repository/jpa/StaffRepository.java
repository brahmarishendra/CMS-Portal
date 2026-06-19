package com.example.SMS.datasource.repository.jpa;

import com.example.SMS.pojo.Staff;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StaffRepository extends CrudRepository<Staff, Long> {
    Optional<Staff> findByEmail(String email);
    List<Staff> findAll();
}
