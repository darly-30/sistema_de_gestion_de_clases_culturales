package edu.unac.repositories;


import edu.unac.domain.CulturalClass;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CulturalClassRepository extends JpaRepository<CulturalClass, Long>{
    List<CulturalClass> findByName(String name);
    boolean existsByName(String name);
}

