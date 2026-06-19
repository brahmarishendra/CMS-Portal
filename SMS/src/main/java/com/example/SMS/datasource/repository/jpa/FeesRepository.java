package com.example.SMS.datasource.repository.jpa;

import com.example.SMS.datasource.entitymodels.models.Fees;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeesRepository extends JpaRepository<Fees, Integer> {
}
