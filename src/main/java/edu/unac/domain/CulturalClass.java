package edu.unac.domain;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cultural_classes")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)

public class CulturalClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 40)
    private String name;

    @Column(name = "category")
    private Category category;

    @Column(name = "maxCapacity")
    private int maxCapacity;

    @Column(name = "startDatetime")
    private long startDateTime;

    @Column(name = "endDatatime")
    private long endDateTime;

    @Column(name = "available")
    private boolean available = true;

}
