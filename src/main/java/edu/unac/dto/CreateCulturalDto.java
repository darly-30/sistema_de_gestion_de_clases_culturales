package edu.unac.dto;

import lombok.Builder;
import edu.unac.domain.Category;

@Builder
public record CreateCulturalDto (
        String name,
        Category category,
        int maxCapacity,
        Long startDateTime,
        Long endDateTime
){}
