package com.entreprise.controller;

import com.entreprise.model.Poste;
import com.entreprise.service.PosteService;
import com.entreprise.service.UserService;
import com.entreprise.service.CompetanceService;
import com.entreprise.service.FormationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@RequestMapping("/postes")
public class PosteController {
    
    @Autowired
    private PosteService posteService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private CompetanceService competanceService;
    
    @Autowired
    private FormationService formationService;
    
    @GetMapping
    public String listPostes(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Accès refusé. Seuls les administrateurs peuvent accéder à cette page");
            return "redirect:/login";
        }
        
        model.addAttribute("postes", posteService.findAll());
        model.addAttribute("activeSection", "postes");
        return "poste/list";
    }
    
    @GetMapping("/new")
    public String showCreateForm(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Accès refusé. Seuls les administrateurs peuvent accéder à cette page");
            return "redirect:/login";
        }
        
        model.addAttribute("poste", new Poste());
        model.addAttribute("departements", userService.findAllDepartements());
        model.addAttribute("competences", competanceService.findAll());
        model.addAttribute("formations", formationService.findAll());
        model.addAttribute("activeSection", "postes");
        return "poste/form";
    }
    
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Accès refusé. Seuls les administrateurs peuvent accéder à cette page");
            return "redirect:/login";
        }
        
        return posteService.findById(id)
            .map(poste -> {
                model.addAttribute("poste", poste);
                model.addAttribute("departements", userService.findAllDepartements());
                model.addAttribute("competences", competanceService.findAll());
                model.addAttribute("formations", formationService.findAll());
                model.addAttribute("activeSection", "postes");
                return "poste/form";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("error", "Poste introuvable");
                return "redirect:/postes";
            });
    }
    
    @PostMapping
    public String savePoste(@ModelAttribute Poste poste, 
                           @RequestParam(value = "competencesIds", required = false) List<Long> competencesIds,
                           @RequestParam(value = "formationsIds", required = false) List<Long> formationsIds,
                           HttpSession session, 
                           RedirectAttributes redirectAttributes) {
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Accès refusé. Seuls les administrateurs peuvent effectuer cette action");
            return "redirect:/login";
        }
        
        try {
            Poste savedPoste = posteService.save(poste);
            
            // Gérer les associations avec les compétences
            if (competencesIds != null && !competencesIds.isEmpty()) {
                posteService.associateCompetences(savedPoste.getIdPoste(), competencesIds);
            }
            
            // Gérer les associations avec les formations
            if (formationsIds != null && !formationsIds.isEmpty()) {
                posteService.associateFormations(savedPoste.getIdPoste(), formationsIds);
            }
            
            redirectAttributes.addFlashAttribute("success", "Poste enregistré avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'enregistrement : " + e.getMessage());
        }
        return "redirect:/postes";
    }
    
    @GetMapping("/delete/{id}")
    public String deletePoste(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Accès refusé. Seuls les administrateurs peuvent effectuer cette action");
            return "redirect:/login";
        }
        
        try {
            posteService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Poste supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression");
        }
        return "redirect:/postes";
    }
}
