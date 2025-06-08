package edu.unac.repositories;


import edu.unac.domain.CulturalClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CulturalClassRepository extends JpaRepository<CulturalClass, Long>{
    Optional<CulturalClassRepository> findByName(String name);

    boolean existsByName(String name);
}

