package edu.unac.controller;
//hola//
import com.example.cultural.domain.*;
import com.example.cultural.service.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/classes")
public class CulturalClassController {

    private final CulturalClassService service;

    public CulturalClassController(CulturalClassService service) {
        this.service = service;
    }

    @GetMapping
    public List<CulturalClass> getAllClasses() {
        return service.getAllClasses();
    }

    @PostMapping
    public ResponseEntity<CulturalClass> createClass(@RequestBody CulturalClass cls) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createClass(cls));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CulturalClass> updateClass(@PathVariable Long id, @RequestBody CulturalClass cls) {
        return ResponseEntity.ok(service.updateClass(id, cls));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteClass(@PathVariable Long id) {
        try {
            service.deleteClass(id);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/{id}/enroll")
    public ResponseEntity<?> enrollStudent(@PathVariable Long id, @RequestBody Map<String, Object> req) {
        try {
            String name = (String) req.get("studentName");
            Long time = ((Number) req.get("enrollmentDateTime")).longValue();
            return ResponseEntity.status(HttpStatus.CREATED).body(service.enrollStudent(id, name, time));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @GetMapping("/{id}/students")
    public List<Enrollment> getStudents(@PathVariable Long id) {
        return service.getEnrollmentsByClassId(id);
    }

    @DeleteMapping("/enrollments/{id}")
    public ResponseEntity<?> cancelEnrollment(@PathVariable Long id) {
        try {
            service.cancelEnrollment(id);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}
