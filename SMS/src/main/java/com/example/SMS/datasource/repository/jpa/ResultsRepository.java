package com.example.SMS.datasource.repository.jpa;

import com.example.SMS.datasource.entitymodels.models.Results;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ResultsRepository extends JpaRepository<Results, Integer> {
    List<Results> findByStudentId(long studentId);
}
