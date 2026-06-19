package com.example.SMS.interfaces;

import com.example.SMS.pojo.Student;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for Student operations.
 */
public interface StudentService {
    Student registerStudent(Student student);
    Optional<Student> loginStudent(String email, String password);
    List<Student> getAllStudents();
}
