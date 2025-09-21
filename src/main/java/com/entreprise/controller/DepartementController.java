package com.entreprise.controller;

import com.entreprise.model.DemandeOffre;
import com.entreprise.model.Local;
import com.entreprise.model.Formation;
import com.entreprise.model.Competance;
import com.entreprise.model.Poste;
import com.entreprise.model.User;
import com.entreprise.service.DemandeOffreService;
import com.entreprise.service.LocalService;
import com.entreprise.service.FormationService;
import com.entreprise.service.CompetanceService;
import com.entreprise.service.PosteService;
import com.entreprise.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
            model.addAttribute("activeSection", "postes");
            
            // Ajouter les listes de compétences et formations
            model.addAttribute("competences", competanceService.findAll());
            model.addAttribute("formations", formationService.findAll());
            
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
            
            // Ajouter les listes de compétences et formations
            model.addAttribute("competences", competanceService.findAll());
            model.addAttribute("formations", formationService.findAll());
            
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
    public String sauvegarderDemande(@ModelAttribute DemandeOffre demandeOffre, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!userService.isDepartement(session)) {
            redirectAttributes.addFlashAttribute("error", "Accès refusé.");
            return "redirect:/login";
        }

        Optional<User> currentUser = userService.getCurrentUser(session);
        if (currentUser.isPresent()) {
            User departement = currentUser.get();
            demandeOffre.setDepartement(departement);
            
            try {
                demandeOffreService.save(demandeOffre);
                redirectAttributes.addFlashAttribute("success", 
                    "Demande d'offre envoyée avec succès ! Elle sera traitée par l'équipe RH.");
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
            List<Competance> competences = competanceService.findAll();
            
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
        
        try {
            competanceService.save(competence);
            redirectAttributes.addFlashAttribute("success", "Compétence enregistrée avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'enregistrement : " + e.getMessage());
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
            List<Formation> formations = formationService.findAll();
            
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
        
        try {
            formationService.save(formation);
            redirectAttributes.addFlashAttribute("success", "Formation enregistrée avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'enregistrement : " + e.getMessage());
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
}