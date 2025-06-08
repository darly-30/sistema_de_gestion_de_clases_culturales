package edu.unac.domain;
//hola//

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cultural_classes")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class    CulturalClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 40)
    private String name;

    @Column(name = "category")
    private Category category;

    @Column(name = "maxCapacity")
    private int maxCapacity;

    @Column(name= "startDatetime")
    private long startDateTime;

    @Column(name = "endDatatime")
    private long endDateTime;

    private boolean available = true;

}
