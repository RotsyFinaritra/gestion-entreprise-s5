package com.entreprise.repository;

import com.entreprise.model.Competance;
import com.entreprise.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CompetanceRepository extends JpaRepository<Competance, Long> {
    List<Competance> findByDepartement(User departement);
    List<Competance> findByDepartementIdUser(Long departementId);
}
