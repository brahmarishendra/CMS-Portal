package com.example.SMS.datasource.entitymodels.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

@Entity
@Table(name = "marks")
public class Results {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mark_id")
    private int id;
    
    @Column(name = "student_id")
    private long studentId;
    
    private int marks;

    @Column(name = "subject_name")
    private String subject;

    @Column(name = "term_name")
    private String termName = "General";

    private String result;

    public Results() {}

    public Results(long studentId, int marks, String subject, String result) {
        this.studentId = studentId;
        this.marks = marks;
        this.subject = subject;
        this.result = result;
        this.termName = "General";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getStudentId() {
        return studentId;
    }

    public void setStudentId(long studentId) {
        this.studentId = studentId;
    }

    public int getMarks() {
        return marks;
    }

    public void setMarks(int marks) {
        this.marks = marks;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }
}
