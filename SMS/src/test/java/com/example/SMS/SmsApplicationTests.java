package com.example.SMS;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Optional;

import com.example.SMS.interfaces.StudentService;
import com.example.SMS.interfaces.StaffService;
import com.example.SMS.controllers.auth.AuthController;
import com.example.SMS.controllers.user.UsersController;
import com.example.SMS.pojo.Student;
import com.example.SMS.pojo.Staff;
import com.example.SMS.pojo.Users;
import com.example.SMS.datasource.entitycustomclasses.customClasses.SignupRequest;
import com.example.SMS.datasource.entitycustomclasses.customClasses.AuthRequest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SmsApplicationTests {

    @Autowired
    private StudentService studentService;

    @Autowired
    private StaffService staffService;

    @Autowired
    private AuthController authController;

    @Autowired
    private UsersController usersController;

    @Test
    void testStudentServiceRegisterAndLogin() {
        // Unique email for test
        String testEmail = "jane.smith.test_" + System.currentTimeMillis() + "@example.com";
        
        Student student = new Student();
        student.setFirstName("Jane");
        student.setLastName("Smith");
        student.setEmail(testEmail);
        student.setPassword("studentPass123");

        // Test Registration
        Student registered = studentService.registerStudent(student);
        assertNotNull(registered.getId());
        assertEquals("Jane", registered.getFirstName());

        // Test Login Success
        Optional<Student> loggedIn = studentService.loginStudent(testEmail, "studentPass123");
        assertTrue(loggedIn.isPresent());
        assertEquals("Jane", loggedIn.get().getFirstName());

        // Test Login Failure
        Optional<Student> loginFail = studentService.loginStudent(testEmail, "wrong_password");
        assertFalse(loginFail.isPresent());
    }

    @Test
    void testStaffServiceRegisterAndLogin() {
        // Unique email for test
        String testEmail = "sri.rama.test_" + System.currentTimeMillis() + "@example.com";
        
        Staff staff = new Staff();
        staff.setFirstName("Sri");
        staff.setLastName("Rama");
        staff.setEmail(testEmail);
        staff.setPhoneNumber("9876543210");
        staff.setPassword("staffPass2025");

        // Test Registration
        Staff registered = staffService.registerStaff(staff);
        assertNotNull(registered.getId());
        assertEquals("Sri", registered.getFirstName());

        // Test Login Success
        Optional<Staff> loggedIn = staffService.loginStaff(testEmail, "staffPass2025");
        assertTrue(loggedIn.isPresent());
        assertEquals("Sri", loggedIn.get().getFirstName());

        // Test Login Failure
        Optional<Staff> loginFail = staffService.loginStaff(testEmail, "wrong_password");
        assertFalse(loginFail.isPresent());
    }

    @Test
    void testAuthControllerSignupAndLoginDirectly() {
        String testEmail = "auth.controller.test_" + System.currentTimeMillis() + "@example.com";
        
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setFirstName("Jane");
        signupRequest.setLastName("Smith");
        signupRequest.setEmail(testEmail);
        signupRequest.setPassword("studentPass123");
        signupRequest.setRole("STUDENT");

        // Test /api/auth/signup logic
        ResponseEntity<?> signupResponse = authController.signup(signupRequest);
        assertEquals(HttpStatus.CREATED, signupResponse.getStatusCode());
        assertNotNull(signupResponse.getBody());
        assertTrue(signupResponse.getBody() instanceof Student);
        Student studentResult = (Student) signupResponse.getBody();
        assertEquals(testEmail, studentResult.getEmail());

        AuthRequest loginRequest = new AuthRequest();
        loginRequest.setEmail(testEmail);
        loginRequest.setPassword("studentPass123");
        loginRequest.setRole("STUDENT");

        // Test /api/auth/login logic
        ResponseEntity<?> loginResponse = authController.login(loginRequest);
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        assertNotNull(loginResponse.getBody());
        assertTrue(loginResponse.getBody() instanceof Student);
        Student loginResult = (Student) loginResponse.getBody();
        assertEquals(testEmail, loginResult.getEmail());
    }

    @Test
    void testUsersControllerSigninDirectly() {
        String testEmail = "users.controller.test_" + System.currentTimeMillis() + "@example.com";
        
        // 1. Register a student via service (should sync to users table)
        Student student = new Student();
        student.setFirstName("Jane");
        student.setLastName("Smith");
        student.setEmail(testEmail);
        student.setPassword("studentPass123");
        studentService.registerStudent(student);

        // 2. Perform sign-in via UsersController
        Users loginRequest = new Users();
        loginRequest.setEmailId(testEmail);
        loginRequest.setPasswordHash("studentPass123");

        ResponseEntity<?> response = usersController.signin(loginRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Users);
        
        Users authUser = (Users) response.getBody();
        assertEquals(testEmail, authUser.getEmailId());
        assertEquals("STUDENT", authUser.getRole());
        assertEquals("Jane Smith", authUser.getUsername());
    }
}
