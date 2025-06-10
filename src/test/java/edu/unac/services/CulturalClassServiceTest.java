package edu.unac.services;

import edu.unac.domain.Category;
import edu.unac.domain.CulturalClass;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CulturalClassServiceTest {
    private CulturalClassRepository classRepository;
    private EnrollmentRepository enrollmentRepository;
    private CulturalClassService service;

    @BeforeEach
    void setup() {
        classRepository = mock(CulturalClassRepository.class);
        enrollmentRepository = mock(EnrollmentRepository.class);
        service = new CulturalClassService(classRepository, enrollmentRepository);
    }

    @Test
    void testCreateValidClass() {
        CreateCulturalDto dto = new CreateCulturalDto("Taller de Danza", Category.DANCE, 20, 1000L, 2000L);
        when(classRepository.findByName("Taller de Danza")).thenReturn(Collections.emptyList());

        CulturalClass saved = CulturalClass.builder()
                .id(1L)
                .name(dto.name())
                .category(dto.category())
                .maxCapacity(dto.maxCapacity())
                .startDateTime(dto.startDateTime())
                .endDateTime(dto.endDateTime())
                .available(true)
                .build();

        when(classRepository.save(Mockito.any(CulturalClass.class))).thenReturn(saved);

        CulturalClass result = service.create(dto);
        assertNotNull(result);
        assertEquals("Taller de Danza", result.getName());
    }

    @Test
    void testCreateWithShortNameThrowsException() {
        CreateCulturalDto dto = new CreateCulturalDto("AB", Category.DANCE, 10, 1000L, 2000L);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.create(dto));
        assertEquals("Name must be at least 3 characters", ex.getMessage());
    }

    @Test
    void testCreateWithOverlappingScheduleThrowsException() {
        CreateCulturalDto dto = new CreateCulturalDto("Yoga", Category.DANCE, 20, 1000L, 2000L);
        CulturalClass existing = CulturalClass.builder().startDateTime(1500L).endDateTime(2500L).name("Yoga").build();

        when(classRepository.findByName("Yoga")).thenReturn(List.of(existing));

        assertThrows(IllegalStateException.class, () -> service.create(dto));
    }

    @Test
    void testUpdateExistingClass() {
        Long id = 1L;
        UpdateCulturalDto dto = new UpdateCulturalDto("Pintura", Category.DANCE, 25, 1000L, 2000L, true);
        CulturalClass existing = CulturalClass.builder().id(id).name("Old").category(Category.DANCE).maxCapacity(10).startDateTime(1L).endDateTime(2L).available(false).build();

        when(classRepository.findById(id)).thenReturn(Optional.of(existing));
        when(classRepository.save(existing)).thenReturn(existing);

        CulturalClass updated = service.update(id, dto);

        assertEquals("Pintura", updated.getName());
        assertEquals(25, updated.getMaxCapacity());
    }

    @Test
    void testUpdateNonExistingClassThrowsException() {
        when(classRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.update(99L,
                new UpdateCulturalDto("Pintura", Category.DANCE, 25, 1000L, 2000L, true)));
    }

    @Test
    void testFindByIdReturnsClass() {
        CulturalClass cls = CulturalClass.builder().id(1L).name("Baile").build();
        when(classRepository.findById(1L)).thenReturn(Optional.of(cls));

        CulturalClass found = service.findById(1L);
        assertEquals("Baile", found.getName());
    }

    @Test
    void testFindByIdThrowsWhenNotFound() {
        when(classRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.findById(2L));
    }

    @Test
    void testFindAllReturnsList() {
        List<CulturalClass> list = List.of(
                CulturalClass.builder().name("Teatro").build(),
                CulturalClass.builder().name("Pintura").build()
        );
        when(classRepository.findAll()).thenReturn(list);

        List<CulturalClass> result = service.findAll();
        assertEquals(2, result.size());
    }

    @Test
    void deleteWithoutEnrollmentsDeletesSuccessfully() {
        when(enrollmentRepository.countByCulturalClassId(1L)).thenReturn(0);
        assertDoesNotThrow(() -> service.delete(1L));
        verify(classRepository).deleteById(1L);
    }

    @Test
    void deleteWithEnrollments() {
        when(enrollmentRepository.countByCulturalClassId(1L)).thenReturn(5);

        assertThrows(IllegalStateException.class, () -> service.delete(1L));
    }
    @Test
    void nameIsIncorrect() {
        CreateCulturalDto dto = new CreateCulturalDto(null, Category.DANCE, 20, 1000L, 2000L);

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> service.create(dto));
        assertEquals("Name must be at least 3 characters", illegalArgumentException.getMessage());
    }
    @Test
    void maxCapacityIsIncorrect() {
        CreateCulturalDto dto = new CreateCulturalDto("Taller de Danza", Category.DANCE, 0, 1000L, 2000L);

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> service.create(dto));
        assertEquals("Max capacity must be greater than zero", illegalArgumentException.getMessage());
    }
    @Test
    void startDateTimeIsIncorrect() {
        CreateCulturalDto dto = new CreateCulturalDto("Taller de Danza", Category.DANCE, 20, 2000L, 1000L);

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> service.create(dto));
        assertEquals("End datetime must be after start datetime", illegalArgumentException.getMessage());
    }
    @Test
    void ifClassIsOverlapped() {
        CreateCulturalDto dto = new CreateCulturalDto("Taller de Danza", Category.DANCE, 20, 1000L, 2000L);
        CulturalClass existing = CulturalClass.builder().startDateTime(1500L).endDateTime(2500L).name("Taller de Danza").build();

        when(classRepository.findByName("Taller de Danza")).thenReturn(List.of(existing));

        IllegalStateException illegalStateException = assertThrows(IllegalStateException.class, () -> service.create(dto));
        assertEquals("Class with same name and overlapping schedule exists", illegalStateException.getMessage());
    }
    @Test
    void ifClassIsNotOverlappedThenItShouldCreateSuccessfully() {
        CreateCulturalDto dto = new CreateCulturalDto("Taller de Danza", Category.DANCE, 20, 1000L, 2000L);
        CulturalClass existing = CulturalClass.builder()
                .startDateTime(3000L)
                .endDateTime(4000L)
                .name("Taller de Danza")
                .build(); // No hay solapamiento

        when(classRepository.findByName("Taller de Danza")).thenReturn(List.of(existing));
        when(classRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        CulturalClass created = service.create(dto);
        assertNotNull(created);
        assertEquals("Taller de Danza", created.getName());
    }

}