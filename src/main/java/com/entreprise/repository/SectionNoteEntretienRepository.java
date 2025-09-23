package com.entreprise.repository;

import com.entreprise.model.SectionNoteEntretien;
import com.entreprise.model.Poste;
import com.entreprise.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SectionNoteEntretienRepository extends JpaRepository<SectionNoteEntretien, Long> {

    // Trouver les sections par poste
    List<SectionNoteEntretien> findByPosteIdPosteOrderByOrdreAffichage(Long posteId);
    
    // Trouver les sections d'un poste spécifique, triées par ordre d'affichage
    List<SectionNoteEntretien> findByPosteOrderByOrdreAffichage(Poste poste);

    // Trouver toutes les sections ordonnées
    @Query("SELECT s FROM SectionNoteEntretien s ORDER BY s.poste.nom, s.ordreAffichage")
    List<SectionNoteEntretien> findAllOrderByPosteAndOrdre();
    
    // Trouver les sections d'entretien pour un département
    @Query("SELECT s FROM SectionNoteEntretien s WHERE s.poste.departement = :departement ORDER BY s.poste.nom, s.ordreAffichage")
    List<SectionNoteEntretien> findByDepartement(@Param("departement") User departement);
    
    // Trouver les sections d'entretien d'un département avec un poste spécifique
    @Query("SELECT s FROM SectionNoteEntretien s WHERE s.poste.departement = :departement AND s.poste.idPoste = :posteId ORDER BY s.ordreAffichage")
    List<SectionNoteEntretien> findByDepartementAndPosteId(@Param("departement") User departement, @Param("posteId") Long posteId);

    // Compter les sections par poste
    long countByPosteIdPoste(Long posteId);
    
    // Compter les sections d'un poste
    Long countByPoste(Poste poste);
    
    // Vérifier si une section avec ce nom existe déjà pour ce poste
    boolean existsByPosteAndNomSection(Poste poste, String nomSection);
    
    // Trouver le prochain ordre d'affichage pour un poste
    @Query("SELECT COALESCE(MAX(s.ordreAffichage), 0) + 1 FROM SectionNoteEntretien s WHERE s.poste = :poste")
    Integer findNextOrdreAffichage(@Param("poste") Poste poste);
}
