package com.entreprise.repository;

import com.entreprise.model.Competance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompetanceRepository extends JpaRepository<Competance, Long> {
}
