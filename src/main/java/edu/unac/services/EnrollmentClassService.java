package edu.unac.services;

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

    public Enrollment create(String studentName, Long classId, Long enrollmentDateTime) {

        try {
            System.out.println("Holaaaa EnrollmentService");
            CulturalClass cls = classRepository.findById(classId).orElseThrow();
            System.out.println("Despues Linea 26");
            System.out.println("cls.getStartDateTime() = " + cls.getStartDateTime());
            System.out.println("System.currentTimeMillis() = " + System.currentTimeMillis());
            System.out.println("cls.isAvailable() = " + cls.isAvailable());

          /*  if (!cls.isAvailable() || (cls.getStartDateTime() * 1000) <= System.currentTimeMillis()) {
                throw new IllegalStateException("Class is not available or has already started");
            }*/

            System.out.println("Paso de linea 31 ");

            int enrolledCount = enrollmentRepository.countByCulturalClassId(cls.getId());
            if (enrolledCount >= cls.getMaxCapacity()) {
                throw new IllegalStateException("Class is full");
            }
            System.out.println("Paso de linea 36 ");

            List<Enrollment> studentEnrollments = enrollmentRepository.findByStudentName(studentName);
            for (Enrollment e : studentEnrollments) {
                CulturalClass other = e.getCulturalClass();
                if (cls.getStartDateTime() < other.getEndDateTime() && other.getStartDateTime() < cls.getEndDateTime()) {
                    throw new IllegalStateException("Time conflict with another class");
                }
            }
            System.out.println("Paso de linea 45 ");

            System.out.println("Paso de largoooooooooooo");
            Enrollment enrollment = new Enrollment();
            enrollment.setStudentName(studentName);
            enrollment.setEnrollmentDateTime(enrollmentDateTime);
            enrollment.setCulturalClass(cls);
            System.out.println("Paso de linea 52 ");

            return enrollmentRepository.save(enrollment);
        } catch (Exception e) {

            System.out.println(e+"EROOOOOOOOOOOOO");
            throw e;
        }
    }

    public void cancel(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId).orElseThrow();
        if (enrollment.getCulturalClass().getEndDateTime() <= System.currentTimeMillis()) {
            throw new IllegalStateException("Cannot cancel after class has ended");
        }
        enrollmentRepository.delete(enrollment);
    }

    public List<Enrollment> findAll() {
        return enrollmentRepository.findAll();
    }
    public List<Enrollment> getByClassId(Long classId) {
        return enrollmentRepository.findByCulturalClassId(classId);
    }
}

