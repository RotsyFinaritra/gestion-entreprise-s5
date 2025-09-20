package com.entreprise.controller;

import com.entreprise.model.Profil;
import com.entreprise.service.ProfilService;
import com.entreprise.service.PosteService;
import com.entreprise.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profils")
public class ProfilController {
    
    @Autowired
    private ProfilService profilService;
    
    @Autowired
    private PosteService posteService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping
    public String listProfils(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        // Vérifier si l'utilisateur est connecté
        String username = (String) session.getAttribute("username");
        String role = (String) session.getAttribute("role");
        
        if (username == null) {
            redirectAttributes.addFlashAttribute("error", "Vous devez être connecté pour accéder à cette page");
            return "redirect:/login";
        }
        
        // Vérifier si l'utilisateur est admin
        if (!"admin".equalsIgnoreCase(role)) {
            redirectAttributes.addFlashAttribute("error", "Accès refusé. Seuls les administrateurs peuvent accéder à cette page");
            return "redirect:/login";
        }
        
        model.addAttribute("profils", profilService.findAll());
        model.addAttribute("activeSection", "profils");
        return "profil/list";
    }
    
    @GetMapping("/new")
    public String showCreateForm(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        // Vérifier si l'utilisateur est admin
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Accès refusé. Seuls les administrateurs peuvent accéder à cette page");
            return "redirect:/login";
        }
        
        model.addAttribute("profil", new Profil());
        model.addAttribute("postes", posteService.findAll());
        model.addAttribute("activeSection", "profils");
        return "profil/form";
    }
    
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        // Vérifier si l'utilisateur est admin
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Accès refusé. Seuls les administrateurs peuvent accéder à cette page");
            return "redirect:/login";
        }
        
        return profilService.findById(id)
            .map(profil -> {
                model.addAttribute("profil", profil);
                model.addAttribute("postes", posteService.findAll());
                model.addAttribute("activeSection", "profils");
                return "profil/form";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("error", "Profil introuvable");
                return "redirect:/profils";
            });
    }
    
    @PostMapping
    public String saveProfil(@ModelAttribute Profil profil, HttpSession session, RedirectAttributes redirectAttributes) {
        // Vérifier si l'utilisateur est admin
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Accès refusé. Seuls les administrateurs peuvent effectuer cette action");
            return "redirect:/login";
        }
        
        try {
            profilService.save(profil);
            redirectAttributes.addFlashAttribute("success", "Profil enregistré avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'enregistrement: " + e.getMessage());
        }
        return "redirect:/profils";
    }
    
    @GetMapping("/delete/{id}")
    public String deleteProfil(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        // Vérifier si l'utilisateur est admin
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Accès refusé. Seuls les administrateurs peuvent effectuer cette action");
            return "redirect:/login";
        }
        
        try {
            profilService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Profil supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression: " + e.getMessage());
        }
        return "redirect:/profils";
    }
    
    // API pour obtenir les profils par poste (pour AJAX)
    @GetMapping("/by-poste/{posteId}")
    @ResponseBody
    public java.util.List<Profil> getProfilsByPoste(@PathVariable Long posteId) {
        return profilService.findByPosteId(posteId);
    }
}
