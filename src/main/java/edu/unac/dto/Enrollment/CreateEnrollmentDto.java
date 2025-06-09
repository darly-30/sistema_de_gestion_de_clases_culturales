package edu.unac.dto.Enrollment;


import lombok.Builder;

@Builder
public record CreateEnrollmentDto(
        String studentName,
        Long classId,
        Long enrollmentDateTime
) {}