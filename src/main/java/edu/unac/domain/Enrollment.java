package edu.unac.domain;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long classId;
    private String studentName;
    private Long enrollmentDateTime; // epoch millis

    @ManyToOne
    @JoinColumn(name = "cultural_class_id")
    private CulturalClass culturalClass;

    }


