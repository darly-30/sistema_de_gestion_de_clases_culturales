package edu.unac.services;
//hola//
import edu.unac.domain.CulturalClass;
import edu.unac.domain.Enrollment;
import edu.unac.repositories.CulturalClassRepository;
import edu.unac.repositories.EnrollmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnrollmentClassService {

    private final EnrollmentRepository enrollmentRepository;
    private final CulturalClassRepository classRepository;

    public EnrollmentClassService(EnrollmentRepository enrollmentRepository, CulturalClassRepository classRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.classRepository = classRepository;
    }

    public Enrollment enroll(String studentName, Long classId, Long enrollmentDateTime) {
        CulturalClass cls = classRepository.findById(classId).orElseThrow();

        if (!cls.isAvailable() || cls.getStartDateTime() <= System.currentTimeMillis()) {
            throw new IllegalStateException("Class is not available or has already started");
        }

        int enrolledCount = enrollmentRepository.countByCulturalClassId(cls.getId());
        if (enrolledCount >= cls.getMaxCapacity()) {
        throw new IllegalStateException("Class is full");
        }

        List<Enrollment> studentEnrollments = enrollmentRepository.findByStudentName(studentName);
        for (Enrollment e : studentEnrollments) {
            CulturalClass other = e.getCulturalClass();
            if (cls.getStartDateTime() < other.getEndDateTime() && other.getStartDateTime() < cls.getEndDateTime()) {
                throw new IllegalStateException("Time conflict with another class");
            }
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudentName(studentName);
        enrollment.setEnrollmentDateTime(enrollmentDateTime);
        enrollment.setCulturalClass(cls);
        return enrollmentRepository.save(enrollment);
    }

    public void cancel(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId).orElseThrow();
        if (enrollment.getCulturalClass().getEndDateTime() <= System.currentTimeMillis()) {
            throw new IllegalStateException("Cannot cancel after class has ended");
        }
        enrollmentRepository.delete(enrollment);
    }

    public List<Enrollment> getByClassId(Long classId) {
        return enrollmentRepository.findByCulturalClassId(classId);
    }
}