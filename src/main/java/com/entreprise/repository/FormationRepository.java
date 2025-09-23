package com.entreprise.repository;

import com.entreprise.model.Formation;
import com.entreprise.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FormationRepository extends JpaRepository<Formation, Long> {
    List<Formation> findByDepartement(User departement);
    List<Formation> findByDepartementIdUser(Long departementId);
}
