package edu.unac.repositories;

import com.example.cultural.domain.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudentName(String studentName);
    List<Enrollment> findByCulturalClassId(Long classId);
}
//hola//