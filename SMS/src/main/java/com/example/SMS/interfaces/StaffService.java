package com.example.SMS.interfaces;

import com.example.SMS.pojo.Staff;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for Staff ("stuff") operations.
 */
public interface StaffService {
    Staff registerStaff(Staff staff);
    Optional<Staff> loginStaff(String email, String password);
    List<Staff> getAllStaff();
}
