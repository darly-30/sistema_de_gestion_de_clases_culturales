package edu.unac.services;

import edu.unac.repositories.EnrollmentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import edu.unac.domain.CulturalClass;
import edu.unac.dto.CreateCulturalDto;
import edu.unac.dto.UpdateCulturalDto;
import edu.unac.repositories.CulturalClassRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Slf4j
@Service
public class CulturalClassService {

    private final CulturalClassRepository classRepository;
    private final EnrollmentRepository enrollmentRepository;

    public CulturalClassService(CulturalClassRepository classRepository, EnrollmentRepository enrollmentRepository) {
        this.classRepository = classRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    public CulturalClass create(CreateCulturalDto dto) {
        List<CulturalClass> existing = classRepository.findByName(dto.name());
        for (CulturalClass c : existing) {
            boolean overlap = dto.startDateTime() < c.getEndDateTime() && c.getStartDateTime() < dto.endDateTime();
            if (overlap) {
                throw new IllegalStateException("Class with same name and overlapping schedule exists");
            }
        }

        if (dto.name() == null || dto.name().length() < 3) {
            throw new IllegalArgumentException("Name must be at least 3 characters");
        }
        if (dto.maxCapacity() <= 0) {
            throw new IllegalArgumentException("Max capacity must be greater than zero");
        }
        if (dto.endDateTime() <= dto.startDateTime()) {
            throw new IllegalArgumentException("End datetime must be after start datetime");
        }

        CulturalClass cls = CulturalClass.builder()
                .name(dto.name())
                .category(dto.category())
                .maxCapacity(dto.maxCapacity())
                .startDateTime(dto.startDateTime())
                .endDateTime(dto.endDateTime())
                .available(true)
                .build();

        return classRepository.save(cls);
    }

    public CulturalClass update(Long id, UpdateCulturalDto dto) {
        CulturalClass cls = classRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Class not found"));

        cls.setName(dto.name());
        cls.setCategory(dto.category());
        cls.setMaxCapacity(dto.maxCapacity());
        cls.setStartDateTime(dto.startDateTime());
        cls.setEndDateTime(dto.endDateTime());
        cls.setAvailable(dto.available());

        return classRepository.save(cls);
    }

    public List<CulturalClass> findAll() {
        return classRepository.findAll();
    }

    public CulturalClass findById(Long id) {
        return classRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Class not found"));
    }

    public void delete(Long id) {
        int enrollmentsCount = enrollmentRepository.countByCulturalClassId(id);
        if (enrollmentsCount > 0) {
            throw new IllegalStateException("Cannot delete class with active enrollments");
        }
        classRepository.deleteById(id);
    }
}

