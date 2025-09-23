package com.entreprise.controller;

import com.entreprise.model.DemandeOffre;
import com.entreprise.model.Local;
import com.entreprise.model.Formation;
import com.entreprise.model.Competance;
import com.entreprise.model.Poste;
import com.entreprise.model.User;
import com.entreprise.model.StatutDemande;
import com.entreprise.model.Entretien;
import com.entreprise.model.NoteEntretien;
import com.entreprise.model.SectionNoteEntretien;
import com.entreprise.service.DemandeOffreService;
import com.entreprise.service.LocalService;
import com.entreprise.service.FormationService;
import com.entreprise.service.CompetanceService;
import com.entreprise.service.PosteService;
import com.entreprise.service.UserService;
import com.entreprise.service.StatutDemandeService;
import com.entreprise.service.EntretienService;
import com.entreprise.service.NoteEntretienService;
import com.entreprise.service.SectionNoteEntretienService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/departement")
public class DepartementController {

    @Autowired
    private UserService userService;

    @Autowired
    private PosteService posteService;
    
    @Autowired
    private DemandeOffreService demandeOffreService;
    
    @Autowired
    private LocalService localService;
    
    @Autowired
    private FormationService formationService;
    
    @Autowired
    private CompetanceService competanceService;

    @Autowired
    private StatutDemandeService statutDemandeService;
    
    @Autowired
    private EntretienService entretienService;
    
    @Autowired
    private NoteEntretienService noteEntretienService;
    
    @Autowired
    private SectionNoteEntretienService sectionNoteEntretienService;

    /**
     * Méthode utilitaire pour récupérer le département depuis la session
     */
    private User getDepartementFromSession(HttpSession session) {
        if (!userService.isDepartement(session)) {
            return null;
        }
        
        Optional<User> currentUser = userService.getCurrentUser(session);
        return currentUser.orElse(null);
    }

    // Tableau de bord du département
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        // Vérifier si l'utilisateur est connecté et est un département
        if (!userService.isDepartement(session)) {
            redirectAttributes.addFlashAttribute("error", "Accès refusé. Vous devez être connecté en tant que département.");
            return "redirect:/login";
        }

        Optional<User> currentUser = userService.getCurrentUser(session);
        if (currentUser.isPresent()) {
            User departement = currentUser.get();
            List<Poste> postesDuDepartement = posteService.findByDepartement(departement);
            
            model.addAttribute("departement", departement);
            model.addAttribute("postes", postesDuDepartement);
            model.addAttribute("nombrePostes", postesDuDepartement.size());
            model.addAttribute("pageTitle", "Tableau de bord - " + departement.getNomDepartement());
            model.addAttribute("pageDescription", "Gestion des postes de votre département");
            model.addAttribute("activeSection", "dashboard");
            
            return "departement/dashboard";
        } else {
            redirectAttributes.addFlashAttribute("error", "Session invalide. Veuillez vous reconnecter.");
            return "redirect:/login";
        }
    }

    // Liste des postes du département
    @GetMapping("/postes")
    public String listPostes(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!userService.isDepartement(session)) {
            redirectAttributes.addFlashAttribute("error", "Accès refusé.");
            return "redirect:/login";
        }

        Optional<User> currentUser = userService.getCurrentUser(session);
        if (currentUser.isPresent()) {
            User departement = currentUser.get();
            List<Poste> postes = posteService.findByDepartement(departement);
            
            model.addAttribute("departement", departement);
            model.addAttribute("postes", postes);
            model.addAttribute("pageTitle", "Mes Postes - " + departement.getNomDepartement());
            model.addAttribute("pageDescription", "Liste des postes de votre département");
            model.addAttribute("activeSection", "postes");
            
            return "departement/postes";
        } else {
            redirectAttributes.addFlashAttribute("error", "Session invalide.");
            return "redirect:/login";
        }
    }

    // Formulaire de création d'un nouveau poste
    @GetMapping("/postes/nouveau")
    public String nouveauPoste(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!userService.isDepartement(session)) {
            redirectAttributes.addFlashAttribute("error", "Accès refusé.");
            return "redirect:/login";
        }

        Optional<User> currentUser = userService.getCurrentUser(session);
        if (currentUser.isPresent()) {
            User departement = currentUser.get();
            Poste nouveauPoste = new Poste();
            nouveauPoste.setDepartement(departement);
            
            model.addAttribute("poste", nouveauPoste);
            model.addAttribute("departement", departement);
            model.addAttribute("pageTitle", "Nouveau Poste - " + departement.getNomDepartement());
            model.addAttribute("pageDescription", "Créer un nouveau poste pour votre département");
            model.addAttribute("activeSection", "nouveau-poste");
            
            // Ajouter les listes de compétences et formations du département
            model.addAttribute("competences", competanceService.findByDepartement(departement));
            model.addAttribute("formations", formationService.findByDepartement(departement));
            
            return "departement/poste-form";
        } else {
            redirectAttributes.addFlashAttribute("error", "Session invalide.");
            return "redirect:/login";
        }
    }

    // Sauvegarder un nouveau poste
    @PostMapping("/postes/sauvegarder")
    public String sauvegarderPoste(@ModelAttribute Poste poste, 
                                   @RequestParam(value = "competencesIds", required = false) List<Long> competencesIds,
                                   @RequestParam(value = "formationsIds", required = false) List<Long> formationsIds,
                                   HttpSession session, 
                                   RedirectAttributes redirectAttributes) {
        if (!userService.isDepartement(session)) {
            redirectAttributes.addFlashAttribute("error", "Accès refusé.");
            return "redirect:/login";
        }

        Optional<User> currentUser = userService.getCurrentUser(session);
        if (currentUser.isPresent()) {
            User departement = currentUser.get();
            poste.setDepartement(departement);
            
            try {
                // Sauvegarder le poste
                Poste savedPoste = posteService.save(poste);
                
                // Associer les compétences si elles sont sélectionnées
                if (competencesIds != null && !competencesIds.isEmpty()) {
                    posteService.associateCompetences(savedPoste.getIdPoste(), competencesIds);
                }
                
                // Associer les formations si elles sont sélectionnées
                if (formationsIds != null && !formationsIds.isEmpty()) {
                    posteService.associateFormations(savedPoste.getIdPoste(), formationsIds);
                }
                
                redirectAttributes.addFlashAttribute("success", "Poste créé avec succès !");
                return "redirect:/departement/postes";
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Erreur lors de la création du poste : " + e.getMessage());
                return "redirect:/departement/postes/nouveau";
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Session invalide.");
            return "redirect:/login";
        }
    }

    // Formulaire d'édition d'un poste
    @GetMapping("/postes/modifier/{id}")
    public String modifierPoste(@PathVariable Long id, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!userService.isDepartement(session)) {
            redirectAttributes.addFlashAttribute("error", "Accès refusé.");
            return "redirect:/login";
        }

        Optional<User> currentUser = userService.getCurrentUser(session);
        Optional<Poste> posteOpt = posteService.findById(id);
        
        if (currentUser.isPresent() && posteOpt.isPresent()) {
            User departement = currentUser.get();
            Poste poste = posteOpt.get();
            
            // Vérifier que le poste appartient bien au département connecté
            if (!poste.getDepartement().getIdUser().equals(departement.getIdUser())) {
                redirectAttributes.addFlashAttribute("error", "Vous ne pouvez modifier que les postes de votre département.");
                return "redirect:/departement/postes";
            }
            
            model.addAttribute("poste", poste);
            model.addAttribute("departement", departement);
            model.addAttribute("pageTitle", "Modifier Poste - " + poste.getNom());
            model.addAttribute("pageDescription", "Modifier le poste : " + poste.getNom());
            model.addAttribute("activeSection", "postes");
            
            // Ajouter les listes de compétences et formations du département
            model.addAttribute("competences", competanceService.findByDepartement(departement));
            model.addAttribute("formations", formationService.findByDepartement(departement));
            
            // Ajouter les IDs des compétences et formations déjà associées
            List<Long> competencesIds = poste.getPosteCompetances().stream()
                .map(pc -> pc.getCompetance().getIdCompetance())
                .collect(java.util.stream.Collectors.toList());
            List<Long> formationsIds = poste.getPosteFormations().stream()
                .map(pf -> pf.getFormation().getIdFormation())
                .collect(java.util.stream.Collectors.toList());
            
            model.addAttribute("selectedCompetencesIds", competencesIds);
            model.addAttribute("selectedFormationsIds", formationsIds);
            
            return "departement/poste-form";
        } else {
            redirectAttributes.addFlashAttribute("error", "Poste non trouvé ou session invalide.");
            return "redirect:/departement/postes";
        }
    }

    // Mettre à jour un poste
    @PostMapping("/postes/modifier/{id}")
    public String mettreAJourPoste(@PathVariable Long id, 
                                   @ModelAttribute Poste poste,
                                   @RequestParam(value = "competencesIds", required = false) List<Long> competencesIds,
                                   @RequestParam(value = "formationsIds", required = false) List<Long> formationsIds,
                                   HttpSession session, 
                                   RedirectAttributes redirectAttributes) {
        if (!userService.isDepartement(session)) {
            redirectAttributes.addFlashAttribute("error", "Accès refusé.");
            return "redirect:/login";
        }

        Optional<User> currentUser = userService.getCurrentUser(session);
        if (currentUser.isPresent()) {
            User departement = currentUser.get();
            poste.setIdPoste(id);
            poste.setDepartement(departement);
            
            try {
                // Sauvegarder le poste
                Poste savedPoste = posteService.save(poste);
                
                // Mettre à jour les associations de compétences
                if (competencesIds != null && !competencesIds.isEmpty()) {
                    posteService.associateCompetences(savedPoste.getIdPoste(), competencesIds);
                } else {
                    // Si aucune compétence sélectionnée, supprimer toutes les associations
                    posteService.associateCompetences(savedPoste.getIdPoste(), new java.util.ArrayList<>());
                }
                
                // Mettre à jour les associations de formations
                if (formationsIds != null && !formationsIds.isEmpty()) {
                    posteService.associateFormations(savedPoste.getIdPoste(), formationsIds);
                } else {
                    // Si aucune formation sélectionnée, supprimer toutes les associations
                    posteService.associateFormations(savedPoste.getIdPoste(), new java.util.ArrayList<>());
                }
                
                redirectAttributes.addFlashAttribute("success", "Poste modifié avec succès !");
                return "redirect:/departement/postes";
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Erreur lors de la modification : " + e.getMessage());
                return "redirect:/departement/postes/modifier/" + id;
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Session invalide.");
            return "redirect:/login";
        }
    }

    // Créer un profil requis pour un recrutement (à envoyer aux RH)
    @GetMapping("/postes/{id}/profil-requis")
    public String creerProfilRequis(@PathVariable Long id, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!userService.isDepartement(session)) {
            redirectAttributes.addFlashAttribute("error", "Accès refusé.");
            return "redirect:/login";
        }

        Optional<User> currentUser = userService.getCurrentUser(session);
        Optional<Poste> posteOpt = posteService.findById(id);
        
        if (currentUser.isPresent() && posteOpt.isPresent()) {
            User departement = currentUser.get();
            Poste poste = posteOpt.get();
            
            // Vérifier que le poste appartient au département
            if (!poste.getDepartement().getIdUser().equals(departement.getIdUser())) {
                redirectAttributes.addFlashAttribute("error", "Ce poste n'appartient pas à votre département.");
                return "redirect:/departement/postes";
            }
            
            model.addAttribute("poste", poste);
            model.addAttribute("departement", departement);
            model.addAttribute("pageTitle", "Créer Profil Requis");
            model.addAttribute("pageDescription", "Définir le profil requis pour le poste : " + poste.getNom());
            model.addAttribute("activeSection", "postes");
            
            return "departement/profil-requis-form";
        } else {
            redirectAttributes.addFlashAttribute("error", "Poste non trouvé.");
            return "redirect:/departement/postes";
        }
    }
    
    // ===== GESTION DES DEMANDES D'OFFRES =====
    
    /**
     * Affiche la liste des demandes d'offres du département
     */
    @GetMapping("/demandes")
    public String listDemandes(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!userService.isDepartement(session)) {
            redirectAttributes.addFlashAttribute("error", "Accès refusé.");
            return "redirect:/login";
        }

        Optional<User> currentUser = userService.getCurrentUser(session);
        if (currentUser.isPresent()) {
            User departement = currentUser.get();
            List<DemandeOffre> demandes = demandeOffreService.findByDepartement(departement);
            
            model.addAttribute("departement", departement);
            model.addAttribute("demandes", demandes);
            model.addAttribute("pageTitle", "Demandes aux RH - " + departement.getNomDepartement());
            model.addAttribute("pageDescription", "Liste de vos demandes d'offres d'emploi");
            model.addAttribute("activeSection", "demandes-rh");
            
            return "departement/demandes";
        } else {
            redirectAttributes.addFlashAttribute("error", "Session invalide.");
            return "redirect:/login";
        }
    }
    
    /**
     * Affiche le formulaire de nouvelle demande d'offre
     */
    @GetMapping("/demandes/nouvelle")
    public String nouvelleDemande(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!userService.isDepartement(session)) {
            redirectAttributes.addFlashAttribute("error", "Accès refusé.");
            return "redirect:/login";
        }

        Optional<User> currentUser = userService.getCurrentUser(session);
        if (currentUser.isPresent()) {
            User departement = currentUser.get();
            List<Poste> postes = posteService.findByDepartement(departement);
            
            if (postes.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Vous devez d'abord créer des postes avant de demander une offre.");
                return "redirect:/departement/postes/nouveau";
            }
            
            DemandeOffre nouvelleDemande = new DemandeOffre();
            
            // Récupérer tous les locaux et formations
            List<Local> locaux = localService.findAll();
            List<Formation> formations = formationService.findAll();
            
            model.addAttribute("demandeOffre", nouvelleDemande);
            model.addAttribute("departement", departement);
            model.addAttribute("postes", postes);
            model.addAttribute("locaux", locaux);
            model.addAttribute("formations", formations);
            model.addAttribute("pageTitle", "Nouvelle Demande d'Offre - " + departement.getNomDepartement());
            model.addAttribute("pageDescription", "Demander aux RH de publier une offre d'emploi");
            model.addAttribute("activeSection", "demandes-rh");
            
            return "departement/demande-offre-form";
        } else {
            redirectAttributes.addFlashAttribute("error", "Session invalide.");
            return "redirect:/login";
        }
    }
    
    /**
     * Sauvegarde une nouvelle demande d'offre
     */
    @PostMapping("/demandes/sauvegarder")
    public String sauvegarderDemande(
            @ModelAttribute DemandeOffre demandeOffre, 
            HttpSession session, 
            RedirectAttributes redirectAttributes) {
        
        if (!userService.isDepartement(session)) {
            redirectAttributes.addFlashAttribute("error", "Accès refusé.");
            return "redirect:/login";
        }

        Optional<User> currentUser = userService.getCurrentUser(session);
        if (currentUser.isPresent()) {
            User departement = currentUser.get();
            
            try {
                // Configurer les informations de base de la demande
                demandeOffre.setDepartement(departement);
                demandeOffre.setDateCreation(LocalDateTime.now());
                demandeOffre.setDateModification(LocalDateTime.now());
                
                // Définir le statut initial si pas déjà défini
                if (demandeOffre.getStatutDemande() == null) {
                    // Récupérer le statut "EN_ATTENTE" depuis le service
                    StatutDemande statutEnAttente = statutDemandeService.getStatutEnAttente();
                    if (statutEnAttente != null) {
                        demandeOffre.setStatutDemande(statutEnAttente);
                    }
                }

                // Récupérer et associer le poste sélectionné
                if (demandeOffre.getPoste() != null && demandeOffre.getPoste().getIdPoste() != null) {
                    Optional<Poste> posteOpt = posteService.findById(demandeOffre.getPoste().getIdPoste());
                    if (posteOpt.isPresent()) {
                        demandeOffre.setPoste(posteOpt.get());
                    } else {
                        redirectAttributes.addFlashAttribute("error", "Poste sélectionné introuvable");
                        return "redirect:/departement/demandes/nouvelle";
                    }
                }

                // Récupérer et associer le local sélectionné si spécifié
                if (demandeOffre.getLocal() != null && demandeOffre.getLocal().getIdLocal() != null) {
                    Optional<Local> localOpt = localService.findById(demandeOffre.getLocal().getIdLocal());
                    if (localOpt.isPresent()) {
                        demandeOffre.setLocal(localOpt.get());
                    } else {
                        redirectAttributes.addFlashAttribute("error", "Local sélectionné introuvable");
                        return "redirect:/departement/demandes/nouvelle";
                    }
                } else {
                    demandeOffre.setLocal(null);
                }

                // Récupérer et associer la formation sélectionnée si spécifiée
                if (demandeOffre.getFormation() != null && demandeOffre.getFormation().getIdFormation() != null) {
                    Optional<Formation> formationOpt = formationService.findById(demandeOffre.getFormation().getIdFormation());
                    if (formationOpt.isPresent()) {
                        demandeOffre.setFormation(formationOpt.get());
                    } else {
                        redirectAttributes.addFlashAttribute("error", "Formation sélectionnée introuvable");
                        return "redirect:/departement/demandes/nouvelle";
                    }
                } else {
                    demandeOffre.setFormation(null);
                }

                // Valider les données obligatoires
                if (demandeOffre.getTitreOffre() == null || demandeOffre.getTitreOffre().trim().isEmpty()) {
                    redirectAttributes.addFlashAttribute("error", "Le titre de l'offre est obligatoire");
                    return "redirect:/departement/demandes/nouvelle";
                }

                if (demandeOffre.getDescriptionPoste() == null || demandeOffre.getDescriptionPoste().trim().isEmpty()) {
                    redirectAttributes.addFlashAttribute("error", "La description du poste est obligatoire");
                    return "redirect:/departement/demandes/nouvelle";
                }

                // Sauvegarder la demande
                demandeOffreService.save(demandeOffre);
                
                String messageSucces = String.format(
                    "Demande d'offre \"%s\" envoyée avec succès ! Elle sera traitée par l'équipe RH.", 
                    demandeOffre.getTitreOffre()
                );
                
                redirectAttributes.addFlashAttribute("success", messageSucces);
                return "redirect:/departement/demandes";
                
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", 
                    "Erreur lors de l'envoi de la demande : " + e.getMessage());
                return "redirect:/departement/demandes/nouvelle";
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Session invalide.");
            return "redirect:/login";
        }
    }
    
    /**
     * Affiche les détails d'une demande
     */
    @GetMapping("/demandes/{id}")
    public String detailsDemande(@PathVariable Long id, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!userService.isDepartement(session)) {
            redirectAttributes.addFlashAttribute("error", "Accès refusé.");
            return "redirect:/login";
        }

        Optional<User> currentUser = userService.getCurrentUser(session);
        Optional<DemandeOffre> demandeOpt = demandeOffreService.findById(id);
        
        if (currentUser.isPresent() && demandeOpt.isPresent()) {
            User departement = currentUser.get();
            DemandeOffre demande = demandeOpt.get();
            
            // Vérifier que la demande appartient bien au département connecté
            if (!demande.getDepartement().getIdUser().equals(departement.getIdUser())) {
                redirectAttributes.addFlashAttribute("error", "Cette demande n'appartient pas à votre département.");
                return "redirect:/departement/demandes";
            }
            
            model.addAttribute("demande", demande);
            model.addAttribute("departement", departement);
            model.addAttribute("pageTitle", "Détails Demande - " + demande.getTitreOffre());
            model.addAttribute("pageDescription", "Détails de votre demande d'offre");
            model.addAttribute("activeSection", "demandes-rh");
            
            return "departement/demande-details";
        } else {
            redirectAttributes.addFlashAttribute("error", "Demande non trouvée.");
            return "redirect:/departement/demandes";
        }
    }
    
    // ===============================
    // GESTION DES COMPÉTENCES
    // ===============================
    
    /**
     * Liste des compétences du département
     */
    @GetMapping("/competences")
    public String listCompetences(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!userService.isDepartement(session)) {
            redirectAttributes.addFlashAttribute("error", "Accès refusé.");
            return "redirect:/login";
        }

        Optional<User> currentUser = userService.getCurrentUser(session);
        if (currentUser.isPresent()) {
            User departement = currentUser.get();
            List<Competance> competences = competanceService.findByDepartement(departement);
            
            model.addAttribute("competences", competences);
            model.addAttribute("departement", departement);
            model.addAttribute("pageTitle", "Gestion des Compétences - " + departement.getNomDepartement());
            model.addAttribute("pageDescription", "Gérer les compétences requises pour vos postes");
            model.addAttribute("activeSection", "competences");
            
            return "departement/competences-list";
        }
        
        redirectAttributes.addFlashAttribute("error", "Erreur de session.");
        return "redirect:/login";
    }
    
    /**
     * Formulaire de création d'une compétence
     */
    @GetMapping("/competences/nouvelle")
    public String nouvelleCompetence(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!userService.isDepartement(session)) {
            redirectAttributes.addFlashAttribute("error", "Accès refusé.");
            return "redirect:/login";
        }

        Optional<User> currentUser = userService.getCurrentUser(session);
        if (currentUser.isPresent()) {
            User departement = currentUser.get();
            Competance competence = new Competance();
            
            model.addAttribute("competence", competence);
            model.addAttribute("departement", departement);
            model.addAttribute("pageTitle", "Nouvelle Compétence - " + departement.getNomDepartement());
            model.addAttribute("pageDescription", "Ajouter une nouvelle compétence");
            model.addAttribute("activeSection", "competences");
            
            return "departement/competence-form";
        }
        
        redirectAttributes.addFlashAttribute("error", "Erreur de session.");
        return "redirect:/login";
    }
    
    /**
     * Sauvegarde d'une compétence
     */
    @PostMapping("/competences/sauvegarder")
    public String sauvegarderCompetence(@ModelAttribute Competance competence, 
                                      HttpSession session, 
                                      RedirectAttributes redirectAttributes) {
        if (!userService.isDepartement(session)) {
            redirectAttributes.addFlashAttribute("error", "Accès refusé.");
            return "redirect:/login";
        }
        
        Optional<User> currentUser = userService.getCurrentUser(session);
        if (currentUser.isPresent()) {
            User departement = currentUser.get();
            competence.setDepartement(departement);
            
            try {
                competanceService.save(competence);
                redirectAttributes.addFlashAttribute("success", "Compétence enregistrée avec succès !");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Erreur lors de l'enregistrement : " + e.getMessage());
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Session invalide.");
        }
        
        return "redirect:/departement/competences";
    }
    
    /**
     * Modifier une compétence
     */
    @GetMapping("/competences/modifier/{id}")
    public String modifierCompetence(@PathVariable Long id, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!userService.isDepartement(session)) {
            redirectAttributes.addFlashAttribute("error", "Accès refusé.");
            return "redirect:/login";
        }

        Optional<User> currentUser = userService.getCurrentUser(session);
        if (currentUser.isPresent()) {
            User departement = currentUser.get();
            Optional<Competance> competenceOpt = competanceService.findById(id);
            
            if (competenceOpt.isPresent()) {
                model.addAttribute("competence", competenceOpt.get());
                model.addAttribute("departement", departement);
                model.addAttribute("pageTitle", "Modifier Compétence - " + competenceOpt.get().getNom());
                model.addAttribute("pageDescription", "Modifier les informations de la compétence");
                model.addAttribute("activeSection", "competences");
                
                return "departement/competence-form";
            } else {
                redirectAttributes.addFlashAttribute("error", "Compétence non trouvée.");
                return "redirect:/departement/competences";
            }
        }
        
        redirectAttributes.addFlashAttribute("error", "Erreur de session.");
        return "redirect:/login";
    }
    
    /**
     * Supprimer une compétence
     */
    @GetMapping("/competences/supprimer/{id}")
    public String supprimerCompetence(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpSession session) {
        if (!userService.isDepartement(session)) {
            redirectAttributes.addFlashAttribute("error", "Accès refusé.");
            return "redirect:/login";
        }
        
        try {
            competanceService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Compétence supprimée avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Impossible de supprimer cette compétence : " + e.getMessage());
        }
        
        return "redirect:/departement/competences";
    }
    
    // ===============================
    // GESTION DES FORMATIONS
    // ===============================
    
    /**
     * Liste des formations du département
     */
    @GetMapping("/formations")
    public String listFormations(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!userService.isDepartement(session)) {
            redirectAttributes.addFlashAttribute("error", "Accès refusé.");
            return "redirect:/login";
        }

        Optional<User> currentUser = userService.getCurrentUser(session);
        if (currentUser.isPresent()) {
            User departement = currentUser.get();
            List<Formation> formations = formationService.findByDepartement(departement);
            
            model.addAttribute("formations", formations);
            model.addAttribute("departement", departement);
            model.addAttribute("pageTitle", "Gestion des Formations - " + departement.getNomDepartement());
            model.addAttribute("pageDescription", "Gérer les formations requises pour vos postes");
            model.addAttribute("activeSection", "formations");
            
            return "departement/formations-list";
        }
        
        redirectAttributes.addFlashAttribute("error", "Erreur de session.");
        return "redirect:/login";
    }
    
    /**
     * Formulaire de création d'une formation
     */
    @GetMapping("/formations/nouvelle")
    public String nouvelleFormation(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!userService.isDepartement(session)) {
            redirectAttributes.addFlashAttribute("error", "Accès refusé.");
            return "redirect:/login";
        }

        Optional<User> currentUser = userService.getCurrentUser(session);
        if (currentUser.isPresent()) {
            User departement = currentUser.get();
            Formation formation = new Formation();
            
            model.addAttribute("formation", formation);
            model.addAttribute("departement", departement);
            model.addAttribute("pageTitle", "Nouvelle Formation - " + departement.getNomDepartement());
            model.addAttribute("pageDescription", "Ajouter une nouvelle formation");
            model.addAttribute("activeSection", "formations");
            
            return "departement/formation-form";
        }
        
        redirectAttributes.addFlashAttribute("error", "Erreur de session.");
        return "redirect:/login";
    }
    
    /**
     * Sauvegarde d'une formation
     */
    @PostMapping("/formations/sauvegarder")
    public String sauvegarderFormation(@ModelAttribute Formation formation, 
                                     HttpSession session, 
                                     RedirectAttributes redirectAttributes) {
        if (!userService.isDepartement(session)) {
            redirectAttributes.addFlashAttribute("error", "Accès refusé.");
            return "redirect:/login";
        }
        
        Optional<User> currentUser = userService.getCurrentUser(session);
        if (currentUser.isPresent()) {
            User departement = currentUser.get();
            formation.setDepartement(departement);
            
            try {
                formationService.save(formation);
                redirectAttributes.addFlashAttribute("success", "Formation enregistrée avec succès !");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Erreur lors de l'enregistrement : " + e.getMessage());
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Session invalide.");
        }
        
        return "redirect:/departement/formations";
    }
    
    /**
     * Modifier une formation
     */
    @GetMapping("/formations/modifier/{id}")
    public String modifierFormation(@PathVariable Long id, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!userService.isDepartement(session)) {
            redirectAttributes.addFlashAttribute("error", "Accès refusé.");
            return "redirect:/login";
        }

        Optional<User> currentUser = userService.getCurrentUser(session);
        if (currentUser.isPresent()) {
            User departement = currentUser.get();
            Optional<Formation> formationOpt = formationService.findById(id);
            
            if (formationOpt.isPresent()) {
                model.addAttribute("formation", formationOpt.get());
                model.addAttribute("departement", departement);
                model.addAttribute("pageTitle", "Modifier Formation - " + formationOpt.get().getNom());
                model.addAttribute("pageDescription", "Modifier les informations de la formation");
                model.addAttribute("activeSection", "formations");
                
                return "departement/formation-form";
            } else {
                redirectAttributes.addFlashAttribute("error", "Formation non trouvée.");
                return "redirect:/departement/formations";
            }
        }
        
        redirectAttributes.addFlashAttribute("error", "Erreur de session.");
        return "redirect:/login";
    }
    
    /**
     * Supprimer une formation
     */
    @GetMapping("/formations/supprimer/{id}")
    public String supprimerFormation(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpSession session) {
        if (!userService.isDepartement(session)) {
            redirectAttributes.addFlashAttribute("error", "Accès refusé.");
            return "redirect:/login";
        }
        
        try {
            formationService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Formation supprimée avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Impossible de supprimer cette formation : " + e.getMessage());
        }
        
        return "redirect:/departement/formations";
    }
    
    // ===============================
    // GESTION DES NOTATIONS D'ENTRETIENS
    // ===============================
    
    /**
     * Liste des entretiens du département pour notation
     */
    @GetMapping("/entretiens")
    public String listEntretiensDepartement(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!userService.isDepartement(session)) {
            redirectAttributes.addFlashAttribute("error", "Accès refusé.");
            return "redirect:/login";
        }

        Optional<User> currentUser = userService.getCurrentUser(session);
        if (currentUser.isPresent()) {
            User departement = currentUser.get();
            
            // Récupérer tous les entretiens pour les postes du département
            List<Entretien> entretiensDepartement = entretienService.findByDepartement(departement);
            
            // Calculer les moyennes des notes pour chaque entretien
            java.util.Map<Long, Double> moyennesNotes = new java.util.HashMap<>();
            for (Entretien entretien : entretiensDepartement) {
                Double moyenne = noteEntretienService.calculateMoyenneByEntretien(entretien.getIdEntretien());
                moyennesNotes.put(entretien.getIdEntretien(), moyenne);
            }
            
            model.addAttribute("entretiens", entretiensDepartement);
            model.addAttribute("moyennesNotes", moyennesNotes);
            model.addAttribute("departement", departement);
            model.addAttribute("pageTitle", "Entretiens - " + departement.getNomDepartement());
            model.addAttribute("pageDescription", "Noter les entretiens pour vos postes");
            model.addAttribute("activeSection", "entretiens");
            
            return "departement/entretiens-list";
        }
        
        redirectAttributes.addFlashAttribute("error", "Erreur de session.");
        return "redirect:/login";
    }
    
    /**
     * Formulaire de notation d'un entretien par le département
     */
    @GetMapping("/entretiens/noter/{entretienId}")
    public String noterEntretien(@PathVariable Long entretienId, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!userService.isDepartement(session)) {
            redirectAttributes.addFlashAttribute("error", "Accès refusé.");
            return "redirect:/login";
        }

        Optional<User> currentUser = userService.getCurrentUser(session);
        Optional<Entretien> entretienOpt = entretienService.findById(entretienId);
        
        if (currentUser.isPresent() && entretienOpt.isPresent()) {
            User departement = currentUser.get();
            Entretien entretien = entretienOpt.get();
            
            // Vérifier que l'entretien concerne un poste du département
            if (!entretien.getOffre().getPoste().getDepartement().getIdUser().equals(departement.getIdUser())) {
                redirectAttributes.addFlashAttribute("error", "Vous ne pouvez noter que les entretiens pour vos postes.");
                return "redirect:/departement/entretiens";
            }
            
            // Récupérer les sections de notation pour le poste
            List<SectionNoteEntretien> sections = sectionNoteEntretienService.findByPosteId(
                entretien.getOffre().getPoste().getIdPoste()
            );
            
            if (sections.isEmpty()) {
                redirectAttributes.addFlashAttribute("warning", 
                    "Aucune section de notation n'est configurée pour ce poste. Contactez l'administration.");
                return "redirect:/departement/entretiens";
            }
            
            // Récupérer les notes existantes
            java.util.Map<Long, NoteEntretien> notesExistantes = noteEntretienService.getNotesMapByEntretien(entretienId);
            Double moyenneActuelle = noteEntretienService.calculateMoyenneByEntretien(entretienId);
            
            model.addAttribute("entretien", entretien);
            model.addAttribute("sections", sections);
            model.addAttribute("notesExistantes", notesExistantes);
            model.addAttribute("moyenneActuelle", moyenneActuelle);
            model.addAttribute("departement", departement);
            model.addAttribute("pageTitle", "Noter l'entretien");
            model.addAttribute("pageDescription", "Évaluation de " + 
                entretien.getCandidat().getPrenom() + " " + entretien.getCandidat().getNom());
            model.addAttribute("activeSection", "entretiens");
            
            return "departement/entretien-notation";
        }
        
        redirectAttributes.addFlashAttribute("error", "Entretien non trouvé.");
        return "redirect:/departement/entretiens";
    }
    
    /**
     * Sauvegarder les notes d'un entretien par le département
     */
    @PostMapping("/entretiens/sauvegarder-notes/{entretienId}")
    public String sauvegarderNotesEntretien(@PathVariable Long entretienId,
                                           @RequestParam java.util.Map<String, String> allParams,
                                           HttpSession session,
                                           RedirectAttributes redirectAttributes) {
        if (!userService.isDepartement(session)) {
            redirectAttributes.addFlashAttribute("error", "Accès refusé.");
            return "redirect:/login";
        }

        try {
            Optional<User> currentUser = userService.getCurrentUser(session);
            Optional<Entretien> entretienOpt = entretienService.findById(entretienId);
            
            if (currentUser.isEmpty() || entretienOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Données invalides.");
                return "redirect:/departement/entretiens";
            }
            
            User departement = currentUser.get();
            Entretien entretien = entretienOpt.get();
            
            // Vérifier que l'entretien concerne un poste du département
            if (!entretien.getOffre().getPoste().getDepartement().getIdUser().equals(departement.getIdUser())) {
                redirectAttributes.addFlashAttribute("error", "Vous ne pouvez noter que les entretiens pour vos postes.");
                return "redirect:/departement/entretiens";
            }
            
            int notesTraitees = 0;
            
            // Traiter chaque note
            for (java.util.Map.Entry<String, String> entry : allParams.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                
                if (key.startsWith("note_") && !value.isEmpty()) {
                    try {
                        // Extraire l'ID de la section
                        Long sectionId = Long.parseLong(key.substring(5)); // Enlever "note_"
                        Double noteValue = Double.parseDouble(value);
                        String commentaire = allParams.get("commentaire_" + sectionId);
                        
                        Optional<SectionNoteEntretien> sectionOpt = sectionNoteEntretienService.findById(sectionId);
                        if (sectionOpt.isEmpty()) continue;
                        
                        SectionNoteEntretien section = sectionOpt.get();
                        
                        // Vérifier que la note ne dépasse pas le maximum
                        if (noteValue > section.getNoteMax()) {
                            redirectAttributes.addFlashAttribute("error", 
                                "La note pour '" + section.getNomSection() + "' ne peut pas dépasser " + section.getNoteMax());
                            return "redirect:/departement/entretiens/noter/" + entretienId;
                        }
                        
                        if (noteValue < 0) {
                            redirectAttributes.addFlashAttribute("error", 
                                "La note pour '" + section.getNomSection() + "' ne peut pas être négative");
                            return "redirect:/departement/entretiens/noter/" + entretienId;
                        }

                        // Créer ou mettre à jour la note
                        NoteEntretien note = new NoteEntretien();
                        note.setEntretien(entretien);
                        note.setSection(section);
                        note.setNoteObtenue(noteValue);
                        note.setCommentaire(commentaire != null ? commentaire.trim() : "");
                        
                        noteEntretienService.save(note);
                        notesTraitees++;

                    } catch (NumberFormatException e) {
                        redirectAttributes.addFlashAttribute("error", 
                            "Note invalide. Veuillez saisir un nombre valide.");
                        return "redirect:/departement/entretiens/noter/" + entretienId;
                    }
                }
            }

            if (notesTraitees > 0) {
                // Mettre à jour le statut de l'entretien à "terminé" si ce n'est pas déjà fait
                if (!"terminé".equals(entretien.getStatut())) {
                    entretien.setStatut("terminé");
                    entretienService.save(entretien);
                }
                
                redirectAttributes.addFlashAttribute("success", 
                    "Notation sauvegardée avec succès ! " + notesTraitees + " note(s) enregistrée(s).");
            } else {
                redirectAttributes.addFlashAttribute("warning", 
                    "Aucune note n'a été saisie.");
                return "redirect:/departement/entretiens/noter/" + entretienId;
            }
            
            return "redirect:/departement/entretiens";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Erreur lors de la sauvegarde : " + e.getMessage());
            return "redirect:/departement/entretiens/noter/" + entretienId;
        }
    }
    
    /**
     * Détail d'un entretien avec les notes attribuées
     */
    @GetMapping("/entretiens/{entretienId}/detail")
    public String detailEntretien(@PathVariable Long entretienId, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!userService.isDepartement(session)) {
            redirectAttributes.addFlashAttribute("error", "Accès refusé.");
            return "redirect:/login";
        }

        Optional<User> currentUser = userService.getCurrentUser(session);
        Optional<Entretien> entretienOpt = entretienService.findById(entretienId);
        
        if (currentUser.isPresent() && entretienOpt.isPresent()) {
            User departement = currentUser.get();
            Entretien entretien = entretienOpt.get();
            
            // Vérifier que l'entretien concerne un poste du département
            if (!entretien.getOffre().getPoste().getDepartement().getIdUser().equals(departement.getIdUser())) {
                redirectAttributes.addFlashAttribute("error", "Accès non autorisé à cet entretien.");
                return "redirect:/departement/entretiens";
            }
            
            // Récupérer les sections et notes
            List<SectionNoteEntretien> sections = sectionNoteEntretienService.findByPosteId(
                entretien.getOffre().getPoste().getIdPoste()
            );
            List<NoteEntretien> notes = noteEntretienService.findByEntretienId(entretienId);
            java.util.Map<Long, NoteEntretien> notesMap = noteEntretienService.getNotesMapByEntretien(entretienId);
            Double moyenne = noteEntretienService.calculateMoyenneByEntretien(entretienId);

            model.addAttribute("entretien", entretien);
            model.addAttribute("sections", sections);
            model.addAttribute("notes", notes);
            model.addAttribute("notesMap", notesMap);
            model.addAttribute("moyenne", moyenne);
            model.addAttribute("departement", departement);
            model.addAttribute("pageTitle", "Détail entretien - " + entretien.getCandidat().getPrenom() + " " + entretien.getCandidat().getNom());
            model.addAttribute("pageDescription", "Résultats de l'évaluation");
            model.addAttribute("activeSection", "entretiens");
            
            return "departement/entretien-detail";
        }
        
        redirectAttributes.addFlashAttribute("error", "Entretien non trouvé.");
        return "redirect:/departement/entretiens";
    }
    
    // ==================== GESTION DES SECTIONS D'ENTRETIEN ====================
    
    /**
     * Afficher la liste des sections d'entretien du département
     */
    @GetMapping("/sections-entretien")
    public String listSectionsEntretien(HttpSession session, Model model) {
        User departement = getDepartementFromSession(session);
        if (departement == null) {
            return "redirect:/login";
        }

        List<SectionNoteEntretien> sections = sectionNoteEntretienService.findByDepartement(departement);
        List<Poste> postes = posteService.findByDepartement(departement);
        
        model.addAttribute("sections", sections);
        model.addAttribute("postes", postes);
        model.addAttribute("pageTitle", "Sections d'Entretien");
        model.addAttribute("pageDescription", "Gérer les critères d'évaluation des entretiens");
        model.addAttribute("activeSection", "sections-entretien");
        
        return "departement/sections-entretien-list";
    }
    
    /**
     * Afficher le formulaire de création d'une section d'entretien
     */
    @GetMapping("/sections-entretien/nouveau")
    public String nouveauSectionEntretien(@RequestParam(required = false) Long posteId, 
                                         HttpSession session, Model model) {
        User departement = getDepartementFromSession(session);
        if (departement == null) {
            return "redirect:/login";
        }

        List<Poste> postes = posteService.findByDepartement(departement);
        SectionNoteEntretien section = new SectionNoteEntretien();
        
        // Si un poste est spécifié, le sélectionner par défaut
        if (posteId != null) {
            Optional<Poste> posteOpt = posteService.findById(posteId);
            if (posteOpt.isPresent() && posteOpt.get().getDepartement().equals(departement)) {
                section.setPoste(posteOpt.get());
            }
        }
        
        model.addAttribute("section", section);
        model.addAttribute("postes", postes);
        model.addAttribute("pageTitle", "Nouvelle Section d'Entretien");
        model.addAttribute("pageDescription", "Ajouter un nouveau critère d'évaluation");
        model.addAttribute("activeSection", "sections-entretien");
        
        return "departement/section-entretien-form";
    }
    
    /**
     * Traiter la création d'une section d'entretien
     */
    @PostMapping("/sections-entretien/nouveau")
    public String creerSectionEntretien(@ModelAttribute SectionNoteEntretien section,
                                       @RequestParam Long posteId,
                                       HttpSession session, 
                                       RedirectAttributes redirectAttributes) {
        User departement = getDepartementFromSession(session);
        if (departement == null) {
            return "redirect:/login";
        }

        try {
            // Vérifier que le poste appartient au département
            Optional<Poste> posteOpt = posteService.findById(posteId);
            if (posteOpt.isEmpty() || !posteOpt.get().getDepartement().equals(departement)) {
                redirectAttributes.addFlashAttribute("error", "Poste non trouvé ou non autorisé.");
                return "redirect:/departement/sections-entretien";
            }
            
            Poste poste = posteOpt.get();
            section.setPoste(poste);
            
            // Vérifier l'unicité du nom de section pour ce poste
            if (sectionNoteEntretienService.existsByPosteAndNomSection(poste, section.getNomSection())) {
                redirectAttributes.addFlashAttribute("error", 
                    "Une section avec ce nom existe déjà pour ce poste.");
                return "redirect:/departement/sections-entretien/nouveau?posteId=" + posteId;
            }
            
            // Valider les données
            if (section.getNomSection() == null || section.getNomSection().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Le nom de la section est requis.");
                return "redirect:/departement/sections-entretien/nouveau?posteId=" + posteId;
            }
            
            if (section.getNoteMax() == null || section.getNoteMax() <= 0) {
                redirectAttributes.addFlashAttribute("error", "La note maximale doit être positive.");
                return "redirect:/departement/sections-entretien/nouveau?posteId=" + posteId;
            }
            
            sectionNoteEntretienService.save(section);
            redirectAttributes.addFlashAttribute("success", 
                "Section d'entretien créée avec succès pour le poste " + poste.getNom());
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Erreur lors de la création de la section : " + e.getMessage());
        }
        
        return "redirect:/departement/sections-entretien";
    }
    
    /**
     * Afficher le formulaire de modification d'une section d'entretien
     */
    @GetMapping("/sections-entretien/modifier/{id}")
    public String modifierSectionEntretien(@PathVariable Long id, 
                                          HttpSession session, Model model,
                                          RedirectAttributes redirectAttributes) {
        User departement = getDepartementFromSession(session);
        if (departement == null) {
            return "redirect:/login";
        }

        Optional<SectionNoteEntretien> sectionOpt = sectionNoteEntretienService.findById(id);
        if (sectionOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Section non trouvée.");
            return "redirect:/departement/sections-entretien";
        }
        
        SectionNoteEntretien section = sectionOpt.get();
        
        // Vérifier que la section appartient au département
        if (!section.getPoste().getDepartement().equals(departement)) {
            redirectAttributes.addFlashAttribute("error", "Section non autorisée.");
            return "redirect:/departement/sections-entretien";
        }
        
        List<Poste> postes = posteService.findByDepartement(departement);
        
        model.addAttribute("section", section);
        model.addAttribute("postes", postes);
        model.addAttribute("pageTitle", "Modifier Section d'Entretien");
        model.addAttribute("pageDescription", "Modifier les critères d'évaluation");
        model.addAttribute("activeSection", "sections-entretien");
        
        return "departement/section-entretien-form";
    }
    
    /**
     * Traiter la modification d'une section d'entretien
     */
    @PostMapping("/sections-entretien/modifier/{id}")
    public String sauvegarderSectionEntretien(@PathVariable Long id,
                                             @ModelAttribute SectionNoteEntretien section,
                                             @RequestParam Long posteId,
                                             HttpSession session,
                                             RedirectAttributes redirectAttributes) {
        User departement = getDepartementFromSession(session);
        if (departement == null) {
            return "redirect:/login";
        }

        try {
            // Vérifier que la section existe et appartient au département
            Optional<SectionNoteEntretien> existingSectionOpt = sectionNoteEntretienService.findById(id);
            if (existingSectionOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Section non trouvée.");
                return "redirect:/departement/sections-entretien";
            }
            
            SectionNoteEntretien existingSection = existingSectionOpt.get();
            if (!existingSection.getPoste().getDepartement().equals(departement)) {
                redirectAttributes.addFlashAttribute("error", "Section non autorisée.");
                return "redirect:/departement/sections-entretien";
            }
            
            // Vérifier que le nouveau poste appartient au département
            Optional<Poste> posteOpt = posteService.findById(posteId);
            if (posteOpt.isEmpty() || !posteOpt.get().getDepartement().equals(departement)) {
                redirectAttributes.addFlashAttribute("error", "Poste non trouvé ou non autorisé.");
                return "redirect:/departement/sections-entretien";
            }
            
            Poste poste = posteOpt.get();
            
            // Vérifier l'unicité du nom de section pour ce poste (en excluant la section courante)
            if (sectionNoteEntretienService.existsByPosteAndNomSectionAndNotId(poste, section.getNomSection(), id)) {
                redirectAttributes.addFlashAttribute("error", 
                    "Une autre section avec ce nom existe déjà pour ce poste.");
                return "redirect:/departement/sections-entretien/modifier/" + id;
            }
            
            // Valider les données
            if (section.getNomSection() == null || section.getNomSection().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Le nom de la section est requis.");
                return "redirect:/departement/sections-entretien/modifier/" + id;
            }
            
            if (section.getNoteMax() == null || section.getNoteMax() <= 0) {
                redirectAttributes.addFlashAttribute("error", "La note maximale doit être positive.");
                return "redirect:/departement/sections-entretien/modifier/" + id;
            }
            
            // Mettre à jour les données
            existingSection.setPoste(poste);
            existingSection.setNomSection(section.getNomSection());
            existingSection.setDescription(section.getDescription());
            existingSection.setNoteMax(section.getNoteMax());
            existingSection.setOrdreAffichage(section.getOrdreAffichage());
            
            sectionNoteEntretienService.save(existingSection);
            redirectAttributes.addFlashAttribute("success", "Section d'entretien modifiée avec succès.");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Erreur lors de la modification : " + e.getMessage());
        }
        
        return "redirect:/departement/sections-entretien";
    }
    
    /**
     * Supprimer une section d'entretien
     */
    @PostMapping("/sections-entretien/supprimer/{id}")
    public String supprimerSectionEntretien(@PathVariable Long id,
                                           HttpSession session,
                                           RedirectAttributes redirectAttributes) {
        User departement = getDepartementFromSession(session);
        if (departement == null) {
            return "redirect:/login";
        }

        try {
            Optional<SectionNoteEntretien> sectionOpt = sectionNoteEntretienService.findById(id);
            if (sectionOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Section non trouvée.");
                return "redirect:/departement/sections-entretien";
            }
            
            SectionNoteEntretien section = sectionOpt.get();
            
            // Vérifier que la section appartient au département
            if (!section.getPoste().getDepartement().equals(departement)) {
                redirectAttributes.addFlashAttribute("error", "Section non autorisée.");
                return "redirect:/departement/sections-entretien";
            }
            
            String nomSection = section.getNomSection();
            String nomPoste = section.getPoste().getNom();
            
            sectionNoteEntretienService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", 
                "Section '" + nomSection + "' supprimée du poste '" + nomPoste + "'.");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Erreur lors de la suppression : " + e.getMessage());
        }
        
        return "redirect:/departement/sections-entretien";
    }
    
    /**
     * Afficher les sections d'entretien pour un poste spécifique
     */
    @GetMapping("/postes/{posteId}/sections-entretien")
    public String sectionsEntretienPoste(@PathVariable Long posteId,
                                        HttpSession session, Model model,
                                        RedirectAttributes redirectAttributes) {
        User departement = getDepartementFromSession(session);
        if (departement == null) {
            return "redirect:/login";
        }

        Optional<Poste> posteOpt = posteService.findById(posteId);
        if (posteOpt.isEmpty() || !posteOpt.get().getDepartement().equals(departement)) {
            redirectAttributes.addFlashAttribute("error", "Poste non trouvé ou non autorisé.");
            return "redirect:/departement/sections-entretien";
        }
        
        Poste poste = posteOpt.get();
        List<SectionNoteEntretien> sections = sectionNoteEntretienService.findByPoste(poste);
        
        model.addAttribute("poste", poste);
        model.addAttribute("sections", sections);
        model.addAttribute("pageTitle", "Sections d'Entretien - " + poste.getNom());
        model.addAttribute("pageDescription", "Critères d'évaluation pour le poste " + poste.getNom());
        model.addAttribute("activeSection", "sections-entretien");
        
        return "departement/sections-entretien-poste";
    }
}