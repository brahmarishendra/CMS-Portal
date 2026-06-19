package com.example.SMS.services;

import com.example.SMS.interfaces.StaffService;
import com.example.SMS.pojo.Staff;
import com.example.SMS.pojo.Users;
import com.example.SMS.datasource.repository.jpa.StaffRepository;
import com.example.SMS.datasource.repository.jpa.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * Service Implementation class for Staff operations.
 */
@Service
public class StaffServiceImpl implements StaffService {

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private UsersRepository usersRepository;

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

        Staff savedStaff = staffRepository.save(staff);

        Users user = new Users(savedStaff.getEmail(), savedStaff.getPassword());
        usersRepository.save(user);

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
}
