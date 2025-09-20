package com.entreprise.repository;

import com.entreprise.model.PosteFormation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PosteFormationRepository extends JpaRepository<PosteFormation, Long> {
    List<PosteFormation> findByPosteIdPoste(Long idPoste);
    List<PosteFormation> findByFormationIdFormation(Long idFormation);
    boolean existsByPosteIdPosteAndFormationIdFormation(Long idPoste, Long idFormation);
    void deleteByPosteIdPosteAndFormationIdFormation(Long idPoste, Long idFormation);
}
