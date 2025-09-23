package com.entreprise.service;

import com.entreprise.model.SectionNoteEntretien;
import com.entreprise.model.Poste;
import com.entreprise.model.User;
import com.entreprise.repository.SectionNoteEntretienRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SectionNoteEntretienService {

    @Autowired
    private SectionNoteEntretienRepository sectionRepository;

    public List<SectionNoteEntretien> findAll() {
        return sectionRepository.findAllOrderByPosteAndOrdre();
    }

    public Optional<SectionNoteEntretien> findById(Long id) {
        return sectionRepository.findById(id);
    }

    public List<SectionNoteEntretien> findByPosteId(Long posteId) {
        return sectionRepository.findByPosteIdPosteOrderByOrdreAffichage(posteId);
    }
    
    // Trouver les sections d'un poste
    public List<SectionNoteEntretien> findByPoste(Poste poste) {
        return sectionRepository.findByPosteOrderByOrdreAffichage(poste);
    }

    // Trouver les sections d'un département
    public List<SectionNoteEntretien> findByDepartement(User departement) {
        return sectionRepository.findByDepartement(departement);
    }

    // Trouver les sections d'un département pour un poste spécifique
    public List<SectionNoteEntretien> findByDepartementAndPosteId(User departement, Long posteId) {
        return sectionRepository.findByDepartementAndPosteId(departement, posteId);
    }

    public SectionNoteEntretien save(SectionNoteEntretien section) {
        // Si pas d'ordre défini, le mettre en dernier
        if (section.getOrdreAffichage() == null) {
            if (section.getPoste() != null) {
                Integer nextOrder = sectionRepository.findNextOrdreAffichage(section.getPoste());
                section.setOrdreAffichage(nextOrder);
            } else {
                Long count = sectionRepository.countByPosteIdPoste(section.getPoste().getIdPoste());
                section.setOrdreAffichage(count.intValue() + 1);
            }
        }
        return sectionRepository.save(section);
    }

    public void deleteById(Long id) {
        sectionRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return sectionRepository.existsById(id);
    }
    
    // Compter les sections d'un poste
    public Long countByPoste(Poste poste) {
        return sectionRepository.countByPoste(poste);
    }

    // Vérifier si une section avec ce nom existe déjà pour ce poste
    public boolean existsByPosteAndNomSection(Poste poste, String nomSection) {
        return sectionRepository.existsByPosteAndNomSection(poste, nomSection);
    }

    // Vérifier si une section avec ce nom existe déjà pour ce poste (en excluant la section courante)
    public boolean existsByPosteAndNomSectionAndNotId(Poste poste, String nomSection, Long excludeId) {
        List<SectionNoteEntretien> sections = findByPoste(poste);
        return sections.stream()
                .anyMatch(s -> s.getNomSection().equalsIgnoreCase(nomSection) && 
                              !s.getIdSection().equals(excludeId));
    }
}
