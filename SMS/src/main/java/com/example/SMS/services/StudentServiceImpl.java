package com.example.SMS.services;

import com.example.SMS.interfaces.StudentService;
import com.example.SMS.pojo.Student;
import com.example.SMS.pojo.Users;
import com.example.SMS.datasource.repository.jpa.StudentRepository;
import com.example.SMS.datasource.repository.jpa.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Service Implementation class for Student operations.
 */
@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Override
    public Student registerStudent(Student student) {
        if (student.getEmail() == null || student.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required for registration!");
        }

        Optional<Student> existingStudent = studentRepository.findByEmail(student.getEmail().trim());
        if (existingStudent.isPresent()) {
            throw new IllegalStateException("A student with this email is already registered!");
        }

        if (usersRepository.findByEmailId(student.getEmail().trim()).isPresent()) {
            throw new IllegalStateException("This email is already registered under another account!");
        }

        if (student.getId() == null || student.getId() == 0) {
            java.time.LocalDate today = java.time.LocalDate.now();
            String monthStr = String.valueOf(today.getMonthValue());
            String dayStr = String.format("%02d", today.getDayOfMonth());
            String yearStr = String.valueOf(today.getYear());
            long baseDate = Long.parseLong(monthStr + dayStr + yearStr);
            
            int sequence = 1000;
            long id = baseDate * 10000 + sequence;
            
            while (studentRepository.existsById(id)) {
                sequence++;
                id = baseDate * 10000 + sequence;
            }
            student.setId(id);
        }

        if (student.getCreatedBy() == null) student.setCreatedBy("System");
        if (student.getUpdatedBy() == null) student.setUpdatedBy("System");
        if (student.getCreatedAt() == null) student.setCreatedAt(java.time.LocalDateTime.now());
        if (student.getUpdatedAt() == null) student.setUpdatedAt(java.time.LocalDateTime.now());

        Student savedStudent = studentRepository.save(student);

        Users user = new Users(savedStudent.getEmail(), savedStudent.getPassword());
        usersRepository.save(user);

        return savedStudent;
    }

    @Override
    public Student updateStudent(Student student, String updatedBy) {
        if (student.getEmail() == null || student.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required for updating student profile!");
        }

        Student existing = null;
        if (student.getId() != null) {
            existing = studentRepository.findById(student.getId()).orElse(null);
        }
        if (existing == null) {
            existing = studentRepository.findByEmail(student.getEmail().trim()).orElse(null);
        }

        if (existing != null) {
            if (student.getFirstName() != null) existing.setFirstName(student.getFirstName());
            if (student.getLastName() != null) existing.setLastName(student.getLastName());
            if (student.getEmail() != null) existing.setEmail(student.getEmail());
            if (student.getPassword() != null) existing.setPassword(student.getPassword());
            if (student.getClasName() != null) existing.setClasName(student.getClasName());
            if (student.getSection() != null) existing.setSection(student.getSection());
            if (student.getGender() != null) existing.setGender(student.getGender());
            existing.setUpdatedBy(updatedBy != null ? updatedBy : "System");
            existing.setUpdatedAt(java.time.LocalDateTime.now());
            student = existing;
        } else {
            student.setUpdatedBy(updatedBy != null ? updatedBy : "System");
            student.setUpdatedAt(java.time.LocalDateTime.now());
        }

        Student savedStudent = studentRepository.save(student);

        if (savedStudent.getEmail() != null && savedStudent.getPassword() != null) {
            Optional<Users> userOpt = usersRepository.findByEmailId(savedStudent.getEmail().trim());
            if (userOpt.isPresent()) {
                Users user = userOpt.get();
                user.setPassword(savedStudent.getPassword());
                usersRepository.save(user);
            }
        }

        return savedStudent;
    }

    @Override
    public Optional<Student> loginStudent(String email, String password) {
        Optional<Student> studentOpt = studentRepository.findByEmail(email);

        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            if (student.getPassword().equals(password)) {
                return Optional.of(student);
            }
        }
        
        return Optional.empty();
    }

    @Override
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }
}
