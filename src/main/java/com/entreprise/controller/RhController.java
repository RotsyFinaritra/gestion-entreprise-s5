package com.entreprise.controller;

import com.entreprise.model.*;
import com.entreprise.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/rh")
public class RhController {

    @Autowired
    private DemandeOffreService demandeOffreService;

    @Autowired
    private StatutDemandeService statutDemandeService;

    @Autowired
    private UserService userService;

    @Autowired
    private OffreService offreService;

    /**
     * Affiche le tableau de bord RH avec les demandes en attente
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Récupérer les statistiques des demandes
        List<DemandeOffre> demandesEnAttente = demandeOffreService.findDemandesEnAttente();
        List<DemandeOffre> demandesAcceptees = demandeOffreService.findDemandesAcceptees();
        List<DemandeOffre> demandesRefusees = demandeOffreService.findDemandesRefusees();

        model.addAttribute("pageTitle", "Tableau de bord RH");
        model.addAttribute("activeSection", "dashboard");
        model.addAttribute("demandesEnAttente", demandesEnAttente);
        model.addAttribute("demandesAcceptees", demandesAcceptees);
        model.addAttribute("demandesRefusees", demandesRefusees);
        model.addAttribute("nbEnAttente", demandesEnAttente.size());
        model.addAttribute("nbAcceptees", demandesAcceptees.size());
        model.addAttribute("nbRefusees", demandesRefusees.size());

        return "rh/dashboard";
    }

    /**
     * Affiche la liste de toutes les demandes d'offres
     */
    @GetMapping("/demandes")
    public String listeDemandes(
            @RequestParam(value = "statut", required = false) String statut,
            Model model) {

        List<DemandeOffre> demandes;
        
        if (statut != null && !statut.isEmpty()) {
            if ("EN_ATTENTE".equals(statut.toUpperCase())) {
                demandes = demandeOffreService.findDemandesEnAttente();
            } else if ("ACCEPTE".equals(statut.toUpperCase())) {
                demandes = demandeOffreService.findDemandesAcceptees();
            } else if ("REFUSE".equals(statut.toUpperCase())) {
                demandes = demandeOffreService.findDemandesRefusees();
            } else {
                demandes = demandeOffreService.findAll();
            }
        } else {
            demandes = demandeOffreService.findAll();
        }

        // Calculer les statistiques
        long nbEnAttente = demandes.stream().filter(d -> d.isEnAttente()).count();
        long nbAcceptees = demandes.stream().filter(d -> d.isAcceptee()).count();
        long nbRefusees = demandes.stream().filter(d -> d.isRefusee()).count();

        // Récupérer tous les statuts pour le filtre
        List<StatutDemande> statuts = statutDemandeService.findAll();

        model.addAttribute("pageTitle", "Gestion des demandes d'offres");
        model.addAttribute("activeSection", "demandes");
        model.addAttribute("demandes", demandes);
        model.addAttribute("statuts", statuts);
        model.addAttribute("statutFiltre", statut);
        model.addAttribute("nbEnAttente", nbEnAttente);
        model.addAttribute("nbAcceptees", nbAcceptees);
        model.addAttribute("nbRefusees", nbRefusees);

        return "rh/demandes";
    }

    /**
     * Affiche les détails d'une demande d'offre
     */
    @GetMapping("/demandes/{id}")
    public String detailDemande(@PathVariable Long id, Model model) {
        Optional<DemandeOffre> demandeOpt = demandeOffreService.findById(id);
        
        if (!demandeOpt.isPresent()) {
            return "redirect:/rh/demandes?error=Demande introuvable";
        }

        DemandeOffre demande = demandeOpt.get();
        model.addAttribute("pageTitle", "Détails de la demande #" + id);
        model.addAttribute("activeSection", "demandes");
        model.addAttribute("demande", demande);

        return "rh/demande-detail";
    }

    /**
     * Accepte une demande d'offre
     */
    @PostMapping("/demandes/{id}/accepter")
    public String accepterDemande(
            @PathVariable Long id,
            @RequestParam(value = "commentaire", required = false) String commentaire,
            RedirectAttributes redirectAttributes) {

        try {
            // Pour l'instant, on utilise un utilisateur fixe, à remplacer par l'authentification
            Optional<User> utilisateurRhOpt = userService.findByUsername("admin"); // À modifier
            if (!utilisateurRhOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Utilisateur RH non trouvé");
                return "redirect:/rh/demandes";
            }
            User utilisateurRh = utilisateurRhOpt.get();
            Optional<DemandeOffre> demandeOpt = demandeOffreService.findById(id);

            if (!demandeOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Demande introuvable");
                return "redirect:/rh/demandes";
            }

            DemandeOffre demande = demandeOpt.get();

            // Vérifier que la demande est en attente
            if (!demande.isEnAttente()) {
                redirectAttributes.addFlashAttribute("error", "Cette demande a déjà été traitée");
                return "redirect:/rh/demandes/" + id;
            }

            // Accepter la demande
            StatutDemande statutAccepte = statutDemandeService.getStatutAccepte();
            demande.setStatutDemande(statutAccepte);
            demande.setUserTraitement(utilisateurRh);
            demande.setDateTraitement(LocalDateTime.now());
            demande.setCommentaireRh(commentaire);

            demandeOffreService.save(demande);

            // Créer automatiquement l'offre d'emploi
            creerOffreDepuisDemande(demande);

            redirectAttributes.addFlashAttribute("success", 
                "Demande acceptée avec succès. L'offre d'emploi a été créée automatiquement.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Erreur lors de l'acceptation de la demande : " + e.getMessage());
        }

        return "redirect:/rh/demandes/" + id;
    }

    /**
     * Refuse une demande d'offre
     */
    @PostMapping("/demandes/{id}/refuser")
    public String refuserDemande(
            @PathVariable Long id,
            @RequestParam(value = "commentaire", required = true) String commentaire,
            RedirectAttributes redirectAttributes) {

        try {
            // Pour l'instant, on utilise un utilisateur fixe, à remplacer par l'authentification
            Optional<User> utilisateurRhOpt = userService.findByUsername("admin"); // À modifier
            if (!utilisateurRhOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Utilisateur RH non trouvé");
                return "redirect:/rh/demandes";
            }
            User utilisateurRh = utilisateurRhOpt.get();
            Optional<DemandeOffre> demandeOpt = demandeOffreService.findById(id);

            if (!demandeOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Demande introuvable");
                return "redirect:/rh/demandes";
            }

            DemandeOffre demande = demandeOpt.get();

            // Vérifier que la demande est en attente
            if (!demande.isEnAttente()) {
                redirectAttributes.addFlashAttribute("error", "Cette demande a déjà été traitée");
                return "redirect:/rh/demandes/" + id;
            }

            // Refuser la demande
            StatutDemande statutRefuse = statutDemandeService.getStatutRefuse();
            demande.setStatutDemande(statutRefuse);
            demande.setUserTraitement(utilisateurRh);
            demande.setDateTraitement(LocalDateTime.now());
            demande.setCommentaireRh(commentaire);

            demandeOffreService.save(demande);

            redirectAttributes.addFlashAttribute("success", "Demande refusée avec succès.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Erreur lors du refus de la demande : " + e.getMessage());
        }

        return "redirect:/rh/demandes/" + id;
    }

    /**
     * Crée automatiquement une offre d'emploi à partir d'une demande acceptée
     */
    private void creerOffreDepuisDemande(DemandeOffre demande) {
        try {
            Offre offre = new Offre();
            
            // Copier les informations de base de la demande vers l'offre
            offre.setMission(demande.getDescriptionPoste());
            offre.setDateCreation(LocalDateTime.now().toLocalDate());
            offre.setDatePublication(LocalDateTime.now().toLocalDate());
            
            // Associer le poste si disponible
            if (demande.getPoste() != null) {
                offre.setPoste(demande.getPoste());
            }
            
            // Associer la demande d'offre
            offre.setDemandeOffre(demande);

            // Sauvegarder l'offre
            offreService.save(offre);

        } catch (Exception e) {
            // Log l'erreur mais ne pas faire échouer l'acceptation de la demande
            System.err.println("Erreur lors de la création automatique de l'offre : " + e.getMessage());
        }
    }
}