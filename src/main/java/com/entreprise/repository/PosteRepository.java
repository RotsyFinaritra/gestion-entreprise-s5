package com.entreprise.repository;

import com.entreprise.model.Poste;
import com.entreprise.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PosteRepository extends JpaRepository<Poste, Long> {
    List<Poste> findByDepartement(User departement);
    List<Poste> findByDepartementIdUser(Long departementId);
}
