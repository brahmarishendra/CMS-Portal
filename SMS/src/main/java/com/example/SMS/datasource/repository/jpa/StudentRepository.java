package com.example.SMS.datasource.repository.jpa;

import com.example.SMS.pojo.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

// Repository interface for Database operations on the students table.
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByEmail(String email);
    List<Student> findAll();
}
