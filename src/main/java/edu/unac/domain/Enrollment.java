package edu.unac.domain;


import jakarta.persistence.*;

@Entity
public class Enrollment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String studentName;
    private Long enrollmentDateTime; // epoch millis

    @ManyToOne
    @JoinColumn(name = "cultural_class_id")
    private CulturalClass culturalClass;

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public Long getEnrollmentDateTime() {
        return enrollmentDateTime;
    }

    public void setEnrollmentDateTime(Long enrollmentDateTime) {
        this.enrollmentDateTime = enrollmentDateTime;
    }

    public CulturalClass getCulturalClass() {
        return culturalClass;
    }

    public void setCulturalClass(CulturalClass culturalClass) {
        this.culturalClass = culturalClass;
    }
}
