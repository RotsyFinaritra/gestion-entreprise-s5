package com.entreprise.repository;

import com.entreprise.model.PosteCompetance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PosteCompetanceRepository extends JpaRepository<PosteCompetance, Long> {
    List<PosteCompetance> findByPosteIdPoste(Long idPoste);
    List<PosteCompetance> findByCompetanceIdCompetance(Long idCompetance);
    boolean existsByPosteIdPosteAndCompetanceIdCompetance(Long idPoste, Long idCompetance);
    void deleteByPosteIdPosteAndCompetanceIdCompetance(Long idPoste, Long idCompetance);
}
