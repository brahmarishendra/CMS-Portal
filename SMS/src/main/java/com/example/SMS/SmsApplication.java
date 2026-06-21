package com.example.SMS;

// POJO classes (Student, Staff, Users, Transport) are located in com.example.SMS.pojo
import com.example.SMS.pojo.Student;
import com.example.SMS.pojo.Staff;
import com.example.SMS.datasource.repository.jpa.StudentRepository;
import com.example.SMS.datasource.repository.jpa.StaffRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan
@EntityScan(basePackages = "com.example.SMS")
public class SmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmsApplication.class, args);
    }

    @Bean
    public CommandLineRunner demoData(StudentRepository studentRepository, StaffRepository staffRepository) {
        return args -> {
            // 1. Seed Student sample record if empty
            if (studentRepository.count() == 0) {
                Student student = new Student();
                // '1L' explicitly defines the number literal 1 as a Long data type (64-bit)
                student.setId(1L);
                student.setFirstName("Admin");
                student.setLastName("One");
                student.setEmail("[EMAIL_ADDRESS]");
                student.setPassword("password123");
                student.setClasName("12");
                student.setSection("A");
                student.setGender("Male");
                studentRepository.save(student);
            }

            // 2. Seed Staff sample record if empty
            if (staffRepository.count() == 0) {
                Staff staff = new Staff();
                staff.Simpledata(); // Populates with "Sri Rama" sample data
                staffRepository.save(staff);
            }
        };
    }
}