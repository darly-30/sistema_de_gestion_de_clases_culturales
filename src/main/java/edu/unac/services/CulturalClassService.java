package edu.unac.services;

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

    private final CulturalClassRepository repository;

    public CulturalClassService(CulturalClassRepository repository) {
        this.repository = repository;
    }

    public CulturalClass create(CreateCulturalDto dto) {
        log.info("Creating CulturalClass: {}", dto);

        validateName(dto.name());
        validateCapacity(dto.maxCapacity());
        validateDates(dto.startDateTime(), dto.endDateTime());

        CulturalClass culturalClass = CulturalClass.builder()
                .name(dto.name())
                .category(dto.category())
                .maxCapacity(dto.maxCapacity())
                .startDateTime(dto.startDateTime()) // LONG
                .endDateTime(dto.endDateTime())     // LONG
                .available(true)
                .build();

        return repository.save(culturalClass);
    }

    public List<CulturalClass> findAll() {
        return repository.findAll();
    }

    public CulturalClass findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cultural class not found"));
    }

    public CulturalClass update(Long id, UpdateCulturalDto dto) {
        CulturalClass current = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cultural class not found"));

        validateNameForUpdate(dto.name(), id);
        validateCapacity(dto.maxCapacity());
        validateDates(dto.startDateTime(), dto.endDateTime());

        CulturalClass updated = current.toBuilder()
                .name(dto.name())
                .category(dto.category())
                .maxCapacity(dto.maxCapacity())
                .startDateTime(dto.startDateTime())
                .endDateTime(dto.endDateTime())
                .available(dto.available())
                .build();

        return repository.save(updated);
    }

    public void delete(Long id) {
        CulturalClass toDelete = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cultural class not found with ID: " + id));

        if (!toDelete.isAvailable()) {
            throw new IllegalArgumentException("Cannot delete a class that is not available");
        }

        repository.delete(toDelete);
    }

    // Validations

    private void validateName(String name) {
        if (name == null || name.length() < 3) {
            throw new IllegalArgumentException("Name must be at least 3 characters");
        }
        if (repository.existsByName(name)) {
            throw new IllegalArgumentException("Name must be unique");
        }
    }

    private void validateNameForUpdate(String name, Long id) {
        if (name == null || name.length() < 3) {
            throw new IllegalArgumentException("Name must be at least 3 characters");
        }

        if (repository.existsByName(name) && !repository.findById(id).get().getName().equals(name)) {
            throw new IllegalArgumentException("Name must be unique");
        }
    }

    private void validateCapacity(int maxCapacity) {
        if (maxCapacity <= 0) {
            throw new IllegalArgumentException("Max capacity must be greater than 0");
        }
    }

    private void validateDates(Long startEpoch, Long endEpoch) {
        if (startEpoch == null || endEpoch == null) {
            throw new IllegalArgumentException("Start and end dates are required");
        }

        LocalDateTime start = LocalDateTime.ofEpochSecond(startEpoch, 0, ZoneOffset.UTC);
        LocalDateTime end = LocalDateTime.ofEpochSecond(endEpoch, 0, ZoneOffset.UTC);

        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("End date must be after start date");
        }
    }
}

