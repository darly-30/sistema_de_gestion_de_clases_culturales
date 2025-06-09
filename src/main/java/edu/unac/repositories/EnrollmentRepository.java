package edu.unac.repositories;

import edu.unac.domain.CulturalClass;
import edu.unac.domain.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface    EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findByStudentName(String studentName);

    List<Enrollment> findByCulturalClassId(Long classId);
    List<CulturalClass> findByName(String name);
    int countByCulturalClassId(Long culturalClassId);

}
//hola//