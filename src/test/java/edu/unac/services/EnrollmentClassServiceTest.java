package edu.unac.services;

import static org.junit.jupiter.api.Assertions.*;

import edu.unac.domain.Category;
import edu.unac.domain.CulturalClass;
import edu.unac.domain.Enrollment;
import edu.unac.dto.CreateCulturalDto;
import edu.unac.dto.UpdateCulturalDto;
import edu.unac.repositories.CulturalClassRepository;
import edu.unac.repositories.EnrollmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class EnrollmentClassServiceTest {

    private EnrollmentRepository enrollmentRepository;
    private CulturalClassRepository classRepository;
    private EnrollmentClassService service;
    @BeforeEach
    void setup() {
        classRepository = mock(CulturalClassRepository.class);
        enrollmentRepository = mock(EnrollmentRepository.class);
        service = new EnrollmentClassService(enrollmentRepository, classRepository);
    }
    @Test
    void classIsFull() {
        CulturalClass cls = CulturalClass.builder().id(1L).name("Baile").category(Category.DANCE).maxCapacity(1).startDateTime(1L).endDateTime(2L).available(true).build();
        when(enrollmentRepository.countByCulturalClassId(1L)).thenReturn(1);

        when(classRepository.findById(cls.getId())).thenReturn(Optional.of(cls));

        assertThrows(IllegalStateException.class, () -> service.create("Pedro", 1L, 1L));
    }
    @Test
    void studentHasConflictingClassTimes() {
        CulturalClass newClass = CulturalClass.builder()
                .id(1L).name("Teatro")
                .startDateTime(1000L)
                .endDateTime(2000L)
                .available(true)
                .maxCapacity(10)
                .build();

        CulturalClass existingClass = CulturalClass.builder()
                .id(2L).name("Pintura")
                .startDateTime(1500L) // Se cruza con newClass
                .endDateTime(2500L)
                .available(true)
                .maxCapacity(10)
                .build();

        Enrollment enrollment = new Enrollment();
        enrollment.setCulturalClass(existingClass);

        when(classRepository.findById(1L)).thenReturn(Optional.of(newClass));
        when(enrollmentRepository.countByCulturalClassId(1L)).thenReturn(0);
        when(enrollmentRepository.findByStudentName("Pedro")).thenReturn(List.of(enrollment));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> service.create("Pedro", 1L, 12345L));
        assertEquals("Time conflict with another class", exception.getMessage());
    }
    @Test
    void studentHasNoConflictWithOtherClasses() {
        CulturalClass newClass = CulturalClass.builder().id(1L).name("Teatro").startDateTime(3000L).endDateTime(4000L).available(true).maxCapacity(10).build();
        CulturalClass existingClass = CulturalClass.builder().id(2L).name("Pintura")
                .startDateTime(1000L) // No se cruza
                .endDateTime(2000L)
                .available(true)
                .maxCapacity(10)
                .build();

        Enrollment enrollment = new Enrollment();
        enrollment.setCulturalClass(existingClass);

        when(classRepository.findById(1L)).thenReturn(Optional.of(newClass));
        when(enrollmentRepository.countByCulturalClassId(1L)).thenReturn(0);
        when(enrollmentRepository.findByStudentName("Pedro")).thenReturn(List.of(enrollment));

        when(enrollmentRepository.save(any())).thenReturn(new Enrollment());

        Enrollment result = service.create("Pedro", 1L, 12345L);
        assertNotNull(result);
        verify(enrollmentRepository).save(any());
    }

    @Test
    void successfulEnrollment() {
        CulturalClass cls = CulturalClass.builder()
                .id(1L)
                .name("Danza")
                .available(true)
                .maxCapacity(2)
                .startDateTime(1L)
                .endDateTime(2L)
                .build();

        when(classRepository.findById(1L)).thenReturn(Optional.of(cls));
        when(enrollmentRepository.countByCulturalClassId(1L)).thenReturn(1);
        when(enrollmentRepository.findByStudentName("Juan")).thenReturn(List.of());

        Enrollment saved = new Enrollment();
        when(enrollmentRepository.save(any())).thenReturn(saved);

        Enrollment result = service.create("Juan", 1L, 1L);

        assertNotNull(result);
        verify(enrollmentRepository).save(any());
    }

    @Test
    void enrollmentFailsIfClassNotFound() {
        when(classRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> service.create("Ana", 1L, System.currentTimeMillis()));
    }
    @Test
    void cancelFailsIfClassEnded() {
        CulturalClass cls = CulturalClass.builder()
                .endDateTime(System.currentTimeMillis() - 1000)
                .build();

        Enrollment enrollment = new Enrollment();
        enrollment.setCulturalClass(cls);

        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));

        assertThrows(IllegalStateException.class, () -> service.cancel(1L));
    }
    @Test
    void cancelSuccessBeforeClassEnd() {
        CulturalClass cls = CulturalClass.builder()
                .endDateTime(System.currentTimeMillis() + 100000)
                .build();

        Enrollment enrollment = new Enrollment();
        enrollment.setCulturalClass(cls);

        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));

        service.cancel(1L);


        verify(enrollmentRepository).delete(enrollment);
    }
    @Test
    void findAllEnrollments(){
        Enrollment enrollment1 = new Enrollment();
        enrollment1.setId(1L);
        enrollment1.setStudentName("Juan");

        Enrollment enrollment2 = new Enrollment();
        enrollment2.setId(2L);
        enrollment2.setStudentName("Maria");
        when(enrollmentRepository.findAll()).thenReturn(List.of(enrollment1, enrollment2));
        List<Enrollment> result = service.findAll();

        assertEquals(2, result.size());

    }
    @Test
    void findEnrollmentById(){
        Enrollment enrollment = new Enrollment();
        enrollment.setId(1L);
        enrollment.setStudentName("Juan");
        enrollment.setClassId(2L);

        when(enrollmentRepository.findByCulturalClassId(2L)).thenReturn(List.of(enrollment));

        List<Enrollment> result = service.getByClassId(2L);

        assertEquals(1, result.size());
    }

//Nuevas 
    @Test
    void enrollmentFailsIfClassIsNotAvailable() {
        CulturalClass cls = CulturalClass.builder()
                .id(1L)
                .name("Pintura")
                .available(false) // No disponible
                .startDateTime(System.currentTimeMillis() / 1000 + 1000)
                .endDateTime(System.currentTimeMillis() / 1000 + 2000)
                .maxCapacity(5)
                .build();

        when(classRepository.findById(1L)).thenReturn(Optional.of(cls));

        assertThrows(IllegalStateException.class, () -> service.create("Pedro", 1L, 1L));
    }


    @Test
    void enrollmentFailsIfClassAlreadyStarted() {
        CulturalClass cls = CulturalClass.builder()
                .id(1L)
                .name("Música")
                .available(true)
                .startDateTime(System.currentTimeMillis() / 1000 - 1000)
                .endDateTime(System.currentTimeMillis() / 1000 + 1000)
                .maxCapacity(5)
                .build();

        when(classRepository.findById(1L)).thenReturn(Optional.of(cls));

        assertThrows(IllegalStateException.class, () -> service.create("Pedro", 1L, 1L));
    }



}