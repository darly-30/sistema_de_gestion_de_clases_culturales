package edu.unac.controller;



import edu.unac.domain.Enrollment;
import edu.unac.dto.Enrollment.CreateEnrollmentDto;
import edu.unac.services.EnrollmentClassService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentClassController {

    private final EnrollmentClassService enrollmentService;

    public EnrollmentClassController(EnrollmentClassService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Enrollment create(@RequestBody CreateEnrollmentDto dto) {
        try {
            return enrollmentService.enroll(dto.studentName(), dto.classId(), dto.enrollmentDateTime());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            enrollmentService.cancel(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/class/{classId}")
    public List<Enrollment> getByClassId(@PathVariable Long classId) {
        return enrollmentService.getByClassId(classId);
    }
}
