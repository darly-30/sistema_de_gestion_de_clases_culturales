package edu.unac.services;


import edu.unac.domain.CulturalClass;
import edu.unac.repository.CulturalClassRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CulturalClassService {

    private final CulturalClassRepository classRepository;

    public CulturalClassService(CulturalClassRepository classRepository) {
        this.classRepository = classRepository;
    }

    public List<CulturalClass> getAll() {
        return classRepository.findAll();
    }

    public Optional<CulturalClass> getById(Long id) {
        return classRepository.findById(id);
    }

    public CulturalClass create(CulturalClass culturalClass) {
        validateNameUniqueness(culturalClass);
        validateDates(culturalClass);
        return classRepository.save(culturalClass);
    }

    public CulturalClass update(Long id, CulturalClass updated) {
        CulturalClass existing = classRepository.findById(id).orElseThrow();
        if (!existing.getName().equals(updated.getName())) {
            validateNameUniqueness(updated);
        }
        validateDates(updated);
        existing.setName(updated.getName());
        existing.setCategory(updated.getCategory());
        existing.setMaxCapacity(updated.getMaxCapacity());
        existing.setStartDateTime(updated.getStartDateTime());
        existing.setEndDateTime(updated.getEndDateTime());
        existing.setAvailable(updated.isAvailable());
        return classRepository.save(existing);
    }

    public void delete(Long id) {
        CulturalClass cls = classRepository.findById(id).orElseThrow();
        if (!cls.getEnrollments().isEmpty()) {
            throw new IllegalStateException("Class has enrolled students");
        }
        classRepository.delete(cls);
    }

    private void validateNameUniqueness(CulturalClass cls) {
        classRepository.findByNameAndTimeRange(cls.getName(), cls.getStartDateTime(), cls.getEndDateTime())
                .ifPresent(existing -> {
                    throw new IllegalStateException("Class with same name and time already exists");
                });
    }

    private void validateDates(CulturalClass cls) {
        if (cls.getEndDateTime() <= cls.getStartDateTime()) {
            throw new IllegalArgumentException("End time must be after start time");
        }
    }
}