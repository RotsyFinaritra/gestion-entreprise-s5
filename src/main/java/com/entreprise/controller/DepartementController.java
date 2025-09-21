package com.entreprise.controller;

import com.entreprise.model.Poste;
import com.entreprise.model.User;
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
            
            return "departement/poste-form";
        } else {
            redirectAttributes.addFlashAttribute("error", "Session invalide.");
            return "redirect:/login";
        }
    }

    // Sauvegarder un nouveau poste
    @PostMapping("/postes/sauvegarder")
    public String sauvegarderPoste(@ModelAttribute Poste poste, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!userService.isDepartement(session)) {
            redirectAttributes.addFlashAttribute("error", "Accès refusé.");
            return "redirect:/login";
        }

        Optional<User> currentUser = userService.getCurrentUser(session);
        if (currentUser.isPresent()) {
            User departement = currentUser.get();
            poste.setDepartement(departement);
            
            try {
                posteService.save(poste);
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
            
            return "departement/poste-form";
        } else {
            redirectAttributes.addFlashAttribute("error", "Poste non trouvé ou session invalide.");
            return "redirect:/departement/postes";
        }
    }

    // Mettre à jour un poste
    @PostMapping("/postes/modifier/{id}")
    public String mettreAJourPoste(@PathVariable Long id, @ModelAttribute Poste poste, HttpSession session, RedirectAttributes redirectAttributes) {
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
                posteService.save(poste);
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
}