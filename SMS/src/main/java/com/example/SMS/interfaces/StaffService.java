package com.example.SMS.interfaces;

import com.example.SMS.pojo.Staff;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for Staff ("stuff") operations.
 */
public interface StaffService {
    Staff registerStaff(Staff staff);
    Staff updateStaff(Staff staff, String updatedBy);
    Optional<Staff> loginStaff(String email, String password);
    List<Staff> getAllStaff();
    String uploadImage(MultipartFile file, String userId);
}
