package com.example.SMS.services;

import com.example.SMS.interfaces.StaffService;
import com.example.SMS.pojo.Staff;
import com.example.SMS.pojo.Users;
import com.example.SMS.datasource.repository.jpa.StaffRepository;
import com.example.SMS.datasource.repository.jpa.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Service Implementation class for Staff operations.
 */
@Service
@Transactional
public class StaffServiceImpl implements StaffService {

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private com.example.SMS.datasource.repository.jpa.StudentRepository studentRepository;

    @Override
    public Staff registerStaff(Staff staff) {
        if (staff.getEmail() == null || staff.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required for staff registration!");
        }

        Optional<Staff> existingStaff = staffRepository.findByEmail(staff.getEmail().trim());
        if (existingStaff.isPresent()) {
            throw new IllegalStateException("A staff member with this email is already registered!");
        }

        if (usersRepository.findByEmailId(staff.getEmail().trim()).isPresent()) {
            throw new IllegalStateException("This email is already registered under another account!");
        }

        if (staff.getCreatedBy() == null) staff.setCreatedBy(staff.getLastName());
        if (staff.getUpdatedBy() == null) staff.setUpdatedBy(staff.getLastName());
        if (staff.getCreatedAt() == null) staff.setCreatedAt(java.time.LocalDateTime.now());
        if (staff.getUpdatedAt() == null) staff.setUpdatedAt(java.time.LocalDateTime.now());

        Staff savedStaff = staffRepository.save(staff);

        Users user = new Users(savedStaff.getEmail(), savedStaff.getPassword());
        usersRepository.save(user);

        return savedStaff;
    }

    @Override
    public Staff updateStaff(Staff staff, String updatedBy) {
        if (staff.getEmail() == null || staff.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required for updating staff profile!");
        }

        Staff existing = null;
        if (staff.getId() != null) {
            existing = staffRepository.findById(staff.getId()).orElse(null);
        }
        if (existing == null) {
            existing = staffRepository.findByEmail(staff.getEmail().trim()).orElse(null);
        }

        if (existing != null) {
            if (staff.getFirstName() != null) existing.setFirstName(staff.getFirstName());
            if (staff.getLastName() != null) existing.setLastName(staff.getLastName());
            if (staff.getEmail() != null) existing.setEmail(staff.getEmail());
            if (staff.getPassword() != null) existing.setPassword(staff.getPassword());
            if (staff.getPhoneNumber() != null) existing.setPhoneNumber(staff.getPhoneNumber());
            if (staff.getAddress() != null) existing.setAddress(staff.getAddress());
            existing.setUpdatedBy(updatedBy != null ? updatedBy : "System");
            existing.setUpdatedAt(java.time.LocalDateTime.now());
            staff = existing;
        } else {
            staff.setUpdatedBy(updatedBy != null ? updatedBy : "System");
            staff.setUpdatedAt(java.time.LocalDateTime.now());
        }

        Staff savedStaff = staffRepository.save(staff);

        if (savedStaff.getEmail() != null && savedStaff.getPassword() != null) {
            Optional<Users> userOpt = usersRepository.findByEmailId(savedStaff.getEmail().trim());
            if (userOpt.isPresent()) {
                Users u = userOpt.get();
                u.setPassword(savedStaff.getPassword());
                usersRepository.save(u);
            }
        }

        return savedStaff;
    }

    @Override
    public Optional<Staff> loginStaff(String email, String password) {
        Optional<Staff> staffOpt = staffRepository.findByEmail(email);

        if (staffOpt.isPresent()) {
            Staff staff = staffOpt.get();
            if (staff.getPassword().equals(password)) {
                return Optional.of(staff);
            }
        }

        return Optional.empty();
    }

    @Override
    public List<Staff> getAllStaff() {
        return staffRepository.findAll();
    }

    @Override
    public String uploadImage(org.springframework.web.multipart.MultipartFile file, String userId) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Cannot upload an empty file!");
        }
        try {
            byte[] bytes = file.getBytes();
            String cleanId = userId.trim();

            Optional<Staff> staffOpt = staffRepository.findByEmail(cleanId);
            if (!staffOpt.isPresent()) {
                try {
                    staffOpt = staffRepository.findById(Long.parseLong(cleanId));
                } catch (Exception ignored) {}
            }

            if (staffOpt.isPresent()) {
                Staff staff = staffOpt.get();
                staff.setProfileImage(bytes);
                staffRepository.save(staff);
            } else {
                Optional<com.example.SMS.pojo.Student> studentOpt = studentRepository.findByEmail(cleanId);
                if (!studentOpt.isPresent()) {
                    try {
                        studentOpt = studentRepository.findById(Long.parseLong(cleanId));
                    } catch (Exception ignored) {}
                }
                if (studentOpt.isPresent()) {
                    com.example.SMS.pojo.Student student = studentOpt.get();
                    student.setProfileImage(bytes);
                    studentRepository.save(student);
                }
            }

            return "data:image/jpeg;base64," + java.util.Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            throw new RuntimeException("Error saving profile image to database: " + e.getMessage());
        }
    }
}
