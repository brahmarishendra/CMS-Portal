package com.example.SMS.controllers.registration;

import com.example.SMS.pojo.Staff;
import com.example.SMS.pojo.Student;
import com.example.SMS.interfaces.StaffService;
import com.example.SMS.interfaces.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.SMS.datasource.repository.jpa.StaffRepository;
import com.example.SMS.datasource.repository.jpa.StudentRepository;
import java.util.Optional;
import java.util.Base64;
import org.springframework.transaction.annotation.Transactional;

/**
 * REST Controller for Profile Editing operations.
 */
@RestController
@RequestMapping("/api/profile")
@CrossOrigin
@Transactional
public class profile_edit {

    @Autowired
    private StaffService staffService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private StudentRepository studentRepository;

    @PutMapping("/staff/update")
    public ResponseEntity<?> updateStaffProfile(@RequestBody Staff staff,
            @RequestParam(defaultValue = "System") String updatedBy) {
        try {
            Staff updated = staffService.updateStaff(staff, updatedBy);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/student/update")
    public ResponseEntity<?> updateStudentProfile(@RequestBody Student student, @RequestParam(defaultValue = "System") String updatedBy) {
        try {
            Student updated = studentService.updateStudent(student, updatedBy);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/upload-image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file,
            @RequestParam("userId") String userId) {
        try {
            String imageUrl = staffService.uploadImage(file, userId);
            return ResponseEntity.ok(imageUrl);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/get-image/{identifier}")
    public ResponseEntity<?> getProfileImage(@PathVariable String identifier) {
        try {
            String cleanId = identifier.trim();
            // 1. Try finding by Email first
            Optional<Staff> staffByEmail = staffRepository.findByEmail(cleanId);
            if (staffByEmail.isPresent() && staffByEmail.get().getProfileImage() != null && staffByEmail.get().getProfileImage().length > 0) {
                return ResponseEntity.ok("data:image/jpeg;base64," + Base64.getEncoder().encodeToString(staffByEmail.get().getProfileImage()));
            }
            Optional<Student> studentByEmail = studentRepository.findByEmail(cleanId);
            if (studentByEmail.isPresent() && studentByEmail.get().getProfileImage() != null && studentByEmail.get().getProfileImage().length > 0) {
                return ResponseEntity.ok("data:image/jpeg;base64," + Base64.getEncoder().encodeToString(studentByEmail.get().getProfileImage()));
            }

            // 2. Try finding by ID if identifier is numeric
            try {
                Long id = Long.parseLong(cleanId);
                Optional<Staff> staffOpt = staffRepository.findById(id);
                if (staffOpt.isPresent() && staffOpt.get().getProfileImage() != null && staffOpt.get().getProfileImage().length > 0) {
                    return ResponseEntity.ok("data:image/jpeg;base64," + Base64.getEncoder().encodeToString(staffOpt.get().getProfileImage()));
                }
                Optional<Student> studentOpt = studentRepository.findById(id);
                if (studentOpt.isPresent() && studentOpt.get().getProfileImage() != null && studentOpt.get().getProfileImage().length > 0) {
                    return ResponseEntity.ok("data:image/jpeg;base64," + Base64.getEncoder().encodeToString(studentOpt.get().getProfileImage()));
                }
            } catch (NumberFormatException ignored) {}

            return ResponseEntity.ok("");
        } catch (Exception e) {
            return ResponseEntity.ok("");
        }
    }
}

