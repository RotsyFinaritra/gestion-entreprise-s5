package com.entreprise.controller;

import com.entreprise.model.Offre;
import com.entreprise.model.Competance;
import com.entreprise.model.Formation;
import com.entreprise.model.Candidat;
import com.entreprise.model.Profil;
import com.entreprise.service.OffreService;
import com.entreprise.service.PosteService;
import com.entreprise.service.FormationService;
import com.entreprise.service.LocalService;
import com.entreprise.service.CompetanceService;
import com.entreprise.service.PosteCompetanceService;
import com.entreprise.service.PosteFormationService;
import com.entreprise.service.CandidatService;
import com.entreprise.service.ProfilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/client")
public class ClientController {
    
    @Autowired
    private OffreService offreService;
    
    @Autowired
    private PosteService posteService;
    
    @Autowired
    private FormationService formationService;
    
    @Autowired
    private LocalService localService;
    
    @Autowired
    private CompetanceService competanceService;
    
    @Autowired
    private PosteCompetanceService posteCompetanceService;
    
    @Autowired
    private PosteFormationService posteFormationService;
    
    @Autowired
    private CandidatService candidatService;
    
    @Autowired
    private ProfilService profilService;
    
    @GetMapping
    public String clientHome(Model model) {
        // Afficher les dernières offres (limitées à 6)
        List<Offre> offresRecentes = offreService.findAll()
            .stream()
            .filter(offre -> offre.getDateFin() == null || offre.getDateFin().isAfter(LocalDate.now()))
            .sorted((o1, o2) -> o2.getDatePublication().compareTo(o1.getDatePublication()))
            .limit(6)
            .collect(Collectors.toList());
        
        model.addAttribute("offresRecentes", offresRecentes);
        model.addAttribute("totalOffres", offreService.findAll().size());
        model.addAttribute("activeSection", "client-home");
        return "client/home";
    }
    
    @GetMapping("/offres")
    public String listOffres(Model model,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "9") int size,
                            @RequestParam(required = false) Long posteId,
                            @RequestParam(required = false) Long formationId,
                            @RequestParam(required = false) Long localId,
                            @RequestParam(required = false) String search) {
        
        // Récupérer toutes les offres actives
        List<Offre> toutesOffres = offreService.findAll()
            .stream()
            .filter(offre -> offre.getDateFin() == null || offre.getDateFin().isAfter(LocalDate.now()))
            .collect(Collectors.toList());
        
        // Filtrer par critères
        if (posteId != null) {
            toutesOffres = toutesOffres.stream()
                .filter(offre -> offre.getPoste() != null && offre.getPoste().getIdPoste().equals(posteId))
                .collect(Collectors.toList());
        }
        
        if (formationId != null) {
            toutesOffres = toutesOffres.stream()
                .filter(offre -> offre.getFormation() != null && offre.getFormation().getIdFormation().equals(formationId))
                .collect(Collectors.toList());
        }
        
        if (localId != null) {
            toutesOffres = toutesOffres.stream()
                .filter(offre -> offre.getLocal() != null && offre.getLocal().getIdLocal().equals(localId))
                .collect(Collectors.toList());
        }
        
        if (search != null && !search.trim().isEmpty()) {
            String searchLower = search.toLowerCase().trim();
            toutesOffres = toutesOffres.stream()
                .filter(offre -> 
                    (offre.getPoste() != null && offre.getPoste().getNom().toLowerCase().contains(searchLower)) ||
                    (offre.getMission() != null && offre.getMission().toLowerCase().contains(searchLower)) ||
                    (offre.getLocal() != null && offre.getLocal().getNom().toLowerCase().contains(searchLower))
                )
                .collect(Collectors.toList());
        }
        
        // Trier par date de publication (plus récent en premier)
        toutesOffres.sort((o1, o2) -> o2.getDatePublication().compareTo(o1.getDatePublication()));
        
        // Pagination manuelle
        int totalElements = toutesOffres.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, totalElements);
        
        List<Offre> offresPage = toutesOffres.subList(startIndex, endIndex);
        
        model.addAttribute("offres", offresPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalElements", totalElements);
        model.addAttribute("size", size);
        
        // Données pour les filtres
        model.addAttribute("postes", posteService.findAll());
        model.addAttribute("formations", formationService.findAll());
        model.addAttribute("locals", localService.findAll());
        
        // Conserver les paramètres de recherche
        model.addAttribute("selectedPosteId", posteId);
        model.addAttribute("selectedFormationId", formationId);
        model.addAttribute("selectedLocalId", localId);
        model.addAttribute("searchQuery", search);
        
        model.addAttribute("activeSection", "client-offres");
        return "client/offres";
    }
    
    @GetMapping("/offres/{id}")
    public String detailOffre(@PathVariable Long id, Model model) {
        offreService.findById(id).ifPresentOrElse(
            offre -> {
                model.addAttribute("offre", offre);
                
                // Calculer les jours restants
                if (offre.getDateFin() != null) {
                    long joursRestants = Period.between(LocalDate.now(), offre.getDateFin()).getDays();
                    model.addAttribute("joursRestants", joursRestants);
                }
                
                // Offres similaires (même poste, différent ID)
                if (offre.getPoste() != null) {
                    List<Offre> offresSimilaires = offreService.findAll()
                        .stream()
                        .filter(o -> !o.getIdOffre().equals(id))
                        .filter(o -> o.getPoste() != null && o.getPoste().getIdPoste().equals(offre.getPoste().getIdPoste()))
                        .filter(o -> o.getDateFin() == null || o.getDateFin().isAfter(LocalDate.now()))
                        .limit(3)
                        .collect(Collectors.toList());
                    model.addAttribute("offresSimilaires", offresSimilaires);
                    
                    // Récupérer les compétences requises pour ce poste
                    List<Competance> competancesPoste = posteCompetanceService.findByPosteId(offre.getPoste().getIdPoste())
                        .stream()
                        .map(pc -> pc.getCompetance())
                        .collect(Collectors.toList());
                    model.addAttribute("competances", competancesPoste);
                    
                    // Récupérer les formations requises pour ce poste
                    List<Formation> formationsPoste = posteFormationService.findByPosteId(offre.getPoste().getIdPoste())
                        .stream()
                        .map(pf -> pf.getFormation())
                        .collect(Collectors.toList());
                    model.addAttribute("formations", formationsPoste);
                    
                    // Récupérer les profils requis pour ce poste
                    List<Profil> profilsPoste = profilService.findByPosteId(offre.getPoste().getIdPoste());
                    model.addAttribute("profils", profilsPoste);
                    model.addAttribute("nombreProfils", profilsPoste.size());
                }
            },
            () -> model.addAttribute("error", "Offre non trouvée")
        );
        
        model.addAttribute("activeSection", "client");
        return "client/detail_offre";
    }
    
    @GetMapping("/postuler/{offreId}")
    public String postuler(@PathVariable Long offreId, Model model) {
        offreService.findById(offreId).ifPresentOrElse(
            offre -> {
                model.addAttribute("offre", offre);
                
                // Récupérer les formations et compétences spécifiques au poste
                if (offre.getPoste() != null) {
                    // Formations requises pour ce poste
                    List<Formation> formationsPoste = posteFormationService.findByPosteId(offre.getPoste().getIdPoste())
                        .stream()
                        .map(pf -> pf.getFormation())
                        .collect(Collectors.toList());
                    model.addAttribute("formations", formationsPoste);
                    
                    // Compétences requises pour ce poste
                    List<Competance> competancesPoste = posteCompetanceService.findByPosteId(offre.getPoste().getIdPoste())
                        .stream()
                        .map(pc -> pc.getCompetance())
                        .collect(Collectors.toList());
                    model.addAttribute("competances", competancesPoste);
                } else {
                    // Si pas de poste défini, listes vides
                    model.addAttribute("formations", List.of());
                    model.addAttribute("competances", List.of());
                }
                
                model.addAttribute("activeSection", "client");
            },
            () -> model.addAttribute("error", "Offre non trouvée")
        );
        
        return "client/postuler";
    }
    
    @PostMapping("/submit-candidature")
    public String submitCandidature(@RequestParam Long offreId,
                                   @RequestParam String prenom,
                                   @RequestParam String nom,
                                   @RequestParam String email,
                                   @RequestParam String tel,
                                   @RequestParam(required = false) String adresse,
                                   @RequestParam(required = false) String dateNaissance,
                                   @RequestParam(required = false) Long genreId,
                                   @RequestParam(required = false) List<Long> formationIds,
                                   @RequestParam(required = false) List<Long> competanceIds,
                                   @RequestParam(required = false) String messageMotivation,
                                   @RequestParam(required = false) MultipartFile photo,
                                   @RequestParam(required = false) MultipartFile cv,
                                   @RequestParam(required = false) MultipartFile lettreMotivation,
                                   RedirectAttributes redirectAttributes) {
        try {
            // Debug : afficher les informations sur la photo
            if (photo != null && !photo.isEmpty()) {
                System.out.println("Photo reçue : " + photo.getOriginalFilename());
                System.out.println("Taille : " + photo.getSize() + " bytes");
                System.out.println("Type : " + photo.getContentType());
            } else {
                System.out.println("Aucune photo fournie ou photo vide");
            }
            
            // Créer la candidature avec toutes les associations
            Candidat candidat = candidatService.createCandidature(
                offreId, prenom, nom, email, tel, adresse, dateNaissance, genreId,
                formationIds, competanceIds, messageMotivation, photo
            );
            
            // Log des données pour debug
            System.out.println("Candidature créée pour: " + prenom + " " + nom);
            System.out.println("Formations sélectionnées: " + formationIds);
            System.out.println("Compétences sélectionnées: " + competanceIds);
            
            redirectAttributes.addFlashAttribute("success", 
                "Votre candidature a été enregistrée avec succès ! Référence: #" + candidat.getIdCandidat() + 
                ". Nous vous contacterons bientôt à l'adresse " + email + ".");
            return "redirect:/client/offres/" + offreId + "?candidature=success";
            
        } catch (Exception e) {
            e.printStackTrace(); // Pour debug
            redirectAttributes.addFlashAttribute("error", 
                "Une erreur est survenue lors de l'enregistrement de votre candidature : " + e.getMessage());
            return "redirect:/client/postuler/" + offreId;
        }
    }
}
