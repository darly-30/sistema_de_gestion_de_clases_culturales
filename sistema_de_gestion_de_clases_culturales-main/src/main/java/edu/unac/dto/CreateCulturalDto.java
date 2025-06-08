package edu.unac.dto;

import lombok.Builder;

import  org.example.domain.Category;
@Builder
public record CreateCulturalDto (
        String name,
        Category category,
        int maxCapacity,
        Long startDateTime,
        Long endDateTime
){}
//hola//