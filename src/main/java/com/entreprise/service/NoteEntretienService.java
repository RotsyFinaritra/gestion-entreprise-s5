package com.entreprise.service;

import com.entreprise.model.NoteEntretien;
import com.entreprise.repository.NoteEntretienRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class NoteEntretienService {

    @Autowired
    private NoteEntretienRepository noteRepository;

    public List<NoteEntretien> findAll() {
        return noteRepository.findAll();
    }

    public Optional<NoteEntretien> findById(Long id) {
        return noteRepository.findById(id);
    }

    public List<NoteEntretien> findByEntretienId(Long entretienId) {
        return noteRepository.findByEntretienIdEntretienOrderBySectionOrdreAffichage(entretienId);
    }

    public List<NoteEntretien> findByCandidatId(Long candidatId) {
        return noteRepository.findByCandidatId(candidatId);
    }

    public List<NoteEntretien> findByOffreId(Long offreId) {
        return noteRepository.findByOffreId(offreId);
    }

    public NoteEntretien save(NoteEntretien note) {
        note.setDateEvaluation(LocalDateTime.now());
        return noteRepository.save(note);
    }

    public void deleteById(Long id) {
        noteRepository.deleteById(id);
    }

    /**
     * Sauvegarder plusieurs notes à la fois
     */
    public List<NoteEntretien> saveAll(List<NoteEntretien> notes) {
        notes.forEach(note -> note.setDateEvaluation(LocalDateTime.now()));
        return noteRepository.saveAll(notes);
    }

    /**
     * Calculer la moyenne des notes d'un entretien
     */
    public Double calculateMoyenneByEntretien(Long entretienId) {
        Double moyenne = noteRepository.calculateMoyenneByEntretien(entretienId);
        return moyenne != null ? Math.round(moyenne * 100.0) / 100.0 : 0.0;
    }

    /**
     * Calculer le total des notes d'un entretien
     */
    public Double calculateTotalByEntretien(Long entretienId) {
        Double total = noteRepository.calculateTotalByEntretien(entretienId);
        return total != null ? total : 0.0;
    }

    /**
     * Obtenir les notes groupées par section pour un entretien
     */
    public Map<Long, NoteEntretien> getNotesMapByEntretien(Long entretienId) {
        List<NoteEntretien> notes = findByEntretienId(entretienId);
        return notes.stream()
                .collect(Collectors.toMap(
                    note -> note.getSection().getIdSection(),
                    note -> note,
                    (existing, replacement) -> replacement
                ));
    }

    /**
     * Vérifier si une note existe pour un entretien et une section
     */
    public Optional<NoteEntretien> findByEntretienAndSection(Long entretienId, Long sectionId) {
        return noteRepository.findByEntretienIdEntretienAndSectionIdSection(entretienId, sectionId);
    }

    /**
     * Calculer les moyennes pour plusieurs entretiens
     */
    public Map<Long, Double> calculateMoyennesForMultipleEntretiens(List<Long> entretienIds) {
        Map<Long, Double> moyennes = new HashMap<>();
        
        for (Long entretienId : entretienIds) {
            Double moyenne = calculateMoyenneByEntretien(entretienId);
            moyennes.put(entretienId, moyenne);
        }
        
        return moyennes;
    }
}
