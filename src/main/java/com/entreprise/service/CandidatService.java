package com.entreprise.service;

import com.entreprise.model.*;
import com.entreprise.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CandidatService {
    
    @Autowired
    private CandidatRepository candidatRepository;
    
    @Autowired
    private OffreRepository offreRepository;
    
    @Autowired
    private GenreRepository genreRepository;
    
    @Autowired
    private FormationRepository formationRepository;
    
    @Autowired
    private CompetanceRepository competanceRepository;
    
    @Autowired
    private FormationCandidatRepository formationCandidatRepository;
    
    @Autowired
    private CandidatCompetanceRepository candidatCompetanceRepository;
    
    @Autowired
    private FileUploadService fileUploadService;
    
    public List<Candidat> findAll() {
        return candidatRepository.findAll();
    }
    
    public Optional<Candidat> findById(Long id) {
        return candidatRepository.findById(id);
    }
    
    public Candidat save(Candidat candidat) {
        return candidatRepository.save(candidat);
    }
    
    public void deleteById(Long id) {
        candidatRepository.deleteById(id);
    }
    
    public List<Candidat> findByOffreId(Long idOffre) {
        return candidatRepository.findByOffreIdOffre(idOffre);
    }
    
    @Transactional
    public Candidat createCandidature(Long offreId, String prenom, String nom, String email, 
                                    String tel, String adresse, String dateNaissance, Long genreId,
                                    List<Long> formationIds, List<Long> competanceIds, 
                                    String messageMotivation, MultipartFile photo) {
        
        // Créer le candidat
        Candidat candidat = new Candidat();
        candidat.setPrenom(prenom);
        candidat.setNom(nom);
        candidat.setEmail(email);
        candidat.setTel(tel);
        candidat.setAdresse(adresse);
        candidat.setDateDepot(LocalDate.now());
        
        // Associer l'offre
        offreRepository.findById(offreId).ifPresent(candidat::setOffre);
        
        // Associer le genre si fourni
        if (genreId != null) {
            genreRepository.findById(genreId).ifPresent(candidat::setGenre);
        }
        
        // Parser et associer la date de naissance si fournie
        if (dateNaissance != null && !dateNaissance.trim().isEmpty()) {
            try {
                candidat.setDateNaissance(LocalDate.parse(dateNaissance));
            } catch (Exception e) {
                // Log l'erreur mais continue l'enregistrement
                System.err.println("Erreur parsing date de naissance: " + e.getMessage());
            }
        }
        
        // Sauvegarder le candidat d'abord pour obtenir l'ID
        final Candidat savedCandidat = candidatRepository.save(candidat);
        
        // Gérer l'upload de la photo si fournie
        if (photo != null && !photo.isEmpty()) {
            try {
                System.out.println("Tentative d'upload de photo pour candidat ID: " + savedCandidat.getIdCandidat());
                String candidatPrefix = "candidat_" + savedCandidat.getIdCandidat();
                String photoPath = fileUploadService.saveImageFile(photo, candidatPrefix);
                System.out.println("Photo sauvegardée avec le chemin: " + photoPath);
                savedCandidat.setImage(photoPath);
                candidatRepository.save(savedCandidat); // Sauvegarder à nouveau avec le chemin de l'image
                System.out.println("Chemin de l'image enregistré dans la base: " + photoPath);
            } catch (Exception e) {
                System.err.println("Erreur lors de l'upload de la photo: " + e.getMessage());
                e.printStackTrace();
                // On continue l'enregistrement même si l'upload échoue
            }
        } else {
            System.out.println("Aucune photo fournie pour le candidat ID: " + savedCandidat.getIdCandidat());
        }
        
        // Associer les formations
        if (formationIds != null && !formationIds.isEmpty()) {
            for (Long formationId : formationIds) {
                formationRepository.findById(formationId).ifPresent(formation -> {
                    FormationCandidat fc = new FormationCandidat(formation, savedCandidat);
                    formationCandidatRepository.save(fc);
                });
            }
        }
        
        // Associer les compétences
        if (competanceIds != null && !competanceIds.isEmpty()) {
            for (Long competanceId : competanceIds) {
                competanceRepository.findById(competanceId).ifPresent(competance -> {
                    CandidatCompetance cc = new CandidatCompetance(savedCandidat, competance);
                    candidatCompetanceRepository.save(cc);
                });
            }
        }
        
        return savedCandidat;
    }
}
