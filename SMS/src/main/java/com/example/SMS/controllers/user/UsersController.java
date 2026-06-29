package com.example.SMS.controllers.user;

import com.example.SMS.pojo.Users;
import com.example.SMS.pojo.Student;
import com.example.SMS.pojo.Staff;
import com.example.SMS.datasource.repository.jpa.StudentRepository;
import com.example.SMS.datasource.repository.jpa.StaffRepository;
import com.example.SMS.datasource.repository.jpa.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller for handling operations on combined users dashboard and user sign-in.
 */
@RestController
@RequestMapping("/api/all-users")
public class UsersController {

    @Autowired
    private StudentRepository studentRepository;    

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private UsersRepository usersRepository;

    @GetMapping
    public List<Users> getAllUsers() {
        List<Users> combinedUsers = new ArrayList<>();

        List<Student> students = studentRepository.findAll();
        if (students != null) {
            for (Student student : students) {
                Users u = new Users();
                String fullName = "";
                if (student.getFirstName() != null) {
                    fullName += student.getFirstName();
                }
                if (student.getLastName() != null) {
                    if (!fullName.isEmpty()) {
                        fullName += " ";
                    }
                    fullName += student.getLastName();
                }

                u.setUsername(fullName.isEmpty() ? "Unknown Student" : fullName);
                u.setEmail(student.getEmail());
                u.setPhoneNumber(null);
                u.setRole("STUDENT");
                combinedUsers.add(u);
            }
        }

        List<Staff> staffMembers = staffRepository.findAll();
        if (staffMembers != null) {
            for (Staff staff : staffMembers) {
                Users u = new Users();
                String fullName = "";
                if (staff.getFirstName() != null) {
                    fullName += staff.getFirstName();
                }
                if (staff.getLastName() != null) {
                    if (!fullName.isEmpty()) {
                        fullName += " ";
                    }
                    fullName += staff.getLastName();
                }

                u.setUsername(fullName.isEmpty() ? "Unknown Staff" : fullName);
                u.setEmail(staff.getEmail());
                u.setPhoneNumber(staff.getPhoneNumber());
                u.setRole("STAFF");
                combinedUsers.add(u);
            }
        }

        return combinedUsers;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody Users loginRequest) {
        String email = loginRequest.getEmailId() != null ? loginRequest.getEmailId().trim() : "";
        String password = loginRequest.getPasswordHash() != null ? loginRequest.getPasswordHash() : "";

        if (email.isEmpty() || password.isEmpty()) {
            return ResponseEntity.badRequest().body("Email and Password are required!");
        }

        Optional<Users> userOpt = usersRepository.findByEmailId(email);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }

        Users dbUser = userOpt.get();

        if (!dbUser.getPasswordHash().equals(password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }

        String selectedRole = loginRequest.getRole() != null ? loginRequest.getRole().trim().toUpperCase() : "";

        if (selectedRole.equals("STAFF") || selectedRole.equals("STUFF")) {
            Optional<Staff> staffOpt = staffRepository.findByEmail(email);
            if (staffOpt.isPresent()) {
                Staff staff = staffOpt.get();
                if (staff.getPhoneNumber() != null && !staff.getPhoneNumber().trim().isEmpty()) {
                    String fullName = (staff.getFirstName() != null ? staff.getFirstName() : "") + " " +
                                       (staff.getLastName() != null ? staff.getLastName() : "");
                    dbUser.setUsername(fullName.trim().isEmpty() ? "Staff Member" : fullName.trim());
                    dbUser.setRole("STAFF");
                    dbUser.setPhoneNumber(staff.getPhoneNumber());
                    dbUser.setDomainId(staff.getId());
                    if (staff.getProfileImage() != null && staff.getProfileImage().length > 0) {
                        dbUser.setProfileImage("data:image/jpeg;base64," + java.util.Base64.getEncoder().encodeToString(staff.getProfileImage()));
                    }
                    return ResponseEntity.ok(dbUser);
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Staff member does not have a registered phone number!");
                }
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password for Staff role!");
        } else if (selectedRole.equals("STUDENT")) {
            Optional<Student> studentOpt = studentRepository.findByEmail(email);
            if (studentOpt.isPresent()) {
                Student student = studentOpt.get();
                String fullName = (student.getFirstName() != null ? student.getFirstName() : "") + " " +
                                   (student.getLastName() != null ? student.getLastName() : "");
                dbUser.setUsername(fullName.trim().isEmpty() ? "Student" : fullName.trim());
                dbUser.setRole("STUDENT");
                dbUser.setDomainId(student.getId());
                if (student.getProfileImage() != null && student.getProfileImage().length > 0) {
                    dbUser.setProfileImage("data:image/jpeg;base64," + java.util.Base64.getEncoder().encodeToString(student.getProfileImage()));
                }
                return ResponseEntity.ok(dbUser);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password for Student role!");
        } else {

            // Default fallback for frontend update user data with domainId
            // to fetch user data with domainId

            Optional<Student> studentOpt = studentRepository.findByEmail(email);
            if (studentOpt.isPresent()) {
                Student student = studentOpt.get();
                String fullName = (student.getFirstName() != null ? student.getFirstName() : "") + " " +
                                   (student.getLastName() != null ? student.getLastName() : "");
                dbUser.setUsername(fullName.trim().isEmpty() ? "Student" : fullName.trim());
                dbUser.setRole("STUDENT");
                dbUser.setDomainId(student.getId());
                if (student.getProfileImage() != null && student.getProfileImage().length > 0) {
                    dbUser.setProfileImage("data:image/jpeg;base64," + java.util.Base64.getEncoder().encodeToString(student.getProfileImage()));
                }
                return ResponseEntity.ok(dbUser);
            }
            Optional<Staff> staffOpt = staffRepository.findByEmail(email);
            if (staffOpt.isPresent()) {
                Staff staff = staffOpt.get();
                if (staff.getPhoneNumber() != null && !staff.getPhoneNumber().trim().isEmpty()) {
                    String fullName = (staff.getFirstName() != null ? staff.getFirstName() : "") + " " +
                                       (staff.getLastName() != null ? staff.getLastName() : "");
                    dbUser.setUsername(fullName.trim().isEmpty() ? "Staff Member" : fullName.trim());
                    dbUser.setRole("STAFF");
                    dbUser.setPhoneNumber(staff.getPhoneNumber());
                    dbUser.setDomainId(staff.getId());
                    if (staff.getProfileImage() != null && staff.getProfileImage().length > 0) {
                        dbUser.setProfileImage("data:image/jpeg;base64," + java.util.Base64.getEncoder().encodeToString(staff.getProfileImage()));
                    }
                    return ResponseEntity.ok(dbUser);
                }
            }
        }

        dbUser.setUsername("User");
        dbUser.setRole("USER");
        return ResponseEntity.ok(dbUser);
    }
}
