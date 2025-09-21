package com.entreprise.repository;

import com.entreprise.model.SectionNoteEntretien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SectionNoteEntretienRepository extends JpaRepository<SectionNoteEntretien, Long> {

    // Trouver les sections par poste
    List<SectionNoteEntretien> findByPosteIdPosteOrderByOrdreAffichage(Long posteId);

    // Trouver toutes les sections ordonnées
    @Query("SELECT s FROM SectionNoteEntretien s ORDER BY s.poste.nom, s.ordreAffichage")
    List<SectionNoteEntretien> findAllOrderByPosteAndOrdre();

    // Compter les sections par poste
    long countByPosteIdPoste(Long posteId);
}
