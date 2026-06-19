package com.example.SMS.services;

import com.example.SMS.interfaces.StudentService;
import com.example.SMS.pojo.Student;
import com.example.SMS.pojo.Users;
import com.example.SMS.datasource.repository.jpa.StudentRepository;
import com.example.SMS.datasource.repository.jpa.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * Service Implementation class for Student operations.
 */
@Service
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

        Student savedStudent = studentRepository.save(student);

        Users user = new Users(savedStudent.getEmail(), savedStudent.getPassword());
        usersRepository.save(user);

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
