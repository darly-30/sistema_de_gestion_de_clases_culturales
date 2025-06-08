package edu.unac.domain;

public class enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3)
    private String studentName;

    @NotNull
    private Long enrollmentDateTime;

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    private CulturalClass culturalClass;
}
}
//hola//