package edu.unac.dto;

import lombok.Builder;
import edu.unac.domain.Category;

@Builder
public record UpdateCulturalDto(
        String name,
        Category category,
        int maxCapacity,
        Long startDateTime,
        Long endDateTime,
        boolean available
) {}
