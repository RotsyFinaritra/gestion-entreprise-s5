package com.entreprise.controller;

import com.entreprise.model.Competance;
import com.entreprise.service.CompetanceService;
import com.entreprise.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/competances")
public class CompetanceController {
    
    @Autowired
    private CompetanceService competanceService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping
    public String listCompetances(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
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
        
        model.addAttribute("competances", competanceService.findAll());
        model.addAttribute("activeSection", "competances");
        return "competance/list";
    }
    
    @GetMapping("/new")
    public String showCreateForm(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        // Vérifier si l'utilisateur est admin
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Accès refusé. Seuls les administrateurs peuvent accéder à cette page");
            return "redirect:/login";
        }
        
        model.addAttribute("competance", new Competance());
        model.addAttribute("activeSection", "competances");
        return "competance/form";
    }
    
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        // Vérifier si l'utilisateur est admin
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Accès refusé. Seuls les administrateurs peuvent accéder à cette page");
            return "redirect:/login";
        }
        
        return competanceService.findById(id)
            .map(competance -> {
                model.addAttribute("competance", competance);
                return "competance/form";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("error", "Compétence introuvable");
                return "redirect:/competances";
            });
    }
    
    @PostMapping
    public String saveCompetance(@ModelAttribute Competance competance, HttpSession session, RedirectAttributes redirectAttributes) {
        // Vérifier si l'utilisateur est admin
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Accès refusé. Seuls les administrateurs peuvent effectuer cette action");
            return "redirect:/login";
        }
        
        try {
            competanceService.save(competance);
            redirectAttributes.addFlashAttribute("success", "Compétence enregistrée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'enregistrement");
        }
        return "redirect:/competances";
    }
    
    @GetMapping("/delete/{id}")
    public String deleteCompetance(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        // Vérifier si l'utilisateur est admin
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Accès refusé. Seuls les administrateurs peuvent effectuer cette action");
            return "redirect:/login";
        }
        
        try {
            competanceService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Compétence supprimée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression");
        }
        return "redirect:/competances";
    }
}
