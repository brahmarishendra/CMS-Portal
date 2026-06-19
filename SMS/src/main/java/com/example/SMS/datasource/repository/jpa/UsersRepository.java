package com.example.SMS.datasource.repository.jpa;

import com.example.SMS.pojo.Users;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * UsersRepository interface for database operations on the users credentials table.
 */
@Repository
public interface UsersRepository extends CrudRepository<Users, Integer> {
    Optional<Users> findByEmailId(String emailId);
}
