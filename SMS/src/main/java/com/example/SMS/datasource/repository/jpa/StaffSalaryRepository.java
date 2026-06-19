package com.example.SMS.datasource.repository.jpa;

import com.example.SMS.datasource.entitymodels.models.StaffSalary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repository Interface
// Location: src/main/java/com/example/SMS/datasource/repository/jpa/StaffSalaryRepository.java
@Repository
public interface StaffSalaryRepository extends JpaRepository<StaffSalary, String> {
}
