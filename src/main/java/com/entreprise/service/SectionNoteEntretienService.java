package com.entreprise.service;

import com.entreprise.model.SectionNoteEntretien;
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

    public SectionNoteEntretien save(SectionNoteEntretien section) {
        // Si pas d'ordre défini, le mettre en dernier
        if (section.getOrdreAffichage() == null) {
            Long count = sectionRepository.countByPosteIdPoste(section.getPoste().getIdPoste());
            section.setOrdreAffichage(count.intValue() + 1);
        }
        return sectionRepository.save(section);
    }

    public void deleteById(Long id) {
        sectionRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return sectionRepository.existsById(id);
    }
}
