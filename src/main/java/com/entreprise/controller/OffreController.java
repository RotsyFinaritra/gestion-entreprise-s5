package com.entreprise.controller;

import com.entreprise.model.Offre;
import com.entreprise.service.OffreService;
import com.entreprise.service.PosteService;
import com.entreprise.service.UserService;

import jakarta.servlet.http.HttpSession;

import com.entreprise.service.FormationService;
import com.entreprise.service.LocalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;

@Controller
@RequestMapping("/offres")
public class OffreController {

    @Autowired
    private OffreService offreService;

    @Autowired
    private PosteService posteService;

    @Autowired
    private FormationService formationService;

    @Autowired
    private LocalService localService;
    
    @Autowired
    private UserService userService;

    @GetMapping
    public String listOffres(Model model, RedirectAttributes redirectAttributes,
            HttpSession session) {
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error",
                    "Accès refusé. Seuls les administrateurs peuvent accéder à cette page");
            return "redirect:/login";
        }
        model.addAttribute("offres", offreService.findAll());
        return "offre/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model, RedirectAttributes redirectAttributes,
            HttpSession session) {
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error",
                    "Accès refusé. Seuls les administrateurs peuvent accéder à cette page");
            return "redirect:/login";
        }
        model.addAttribute("offre", new Offre());
        model.addAttribute("postes", posteService.findAll());
        model.addAttribute("formations", formationService.findAll());
        model.addAttribute("locals", localService.findAll());
        return "offre/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes,
            HttpSession session) {
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error",
                    "Accès refusé. Seuls les administrateurs peuvent accéder à cette page");
            return "redirect:/login";
        }
        return offreService.findById(id)
                .map(offre -> {
                    model.addAttribute("offre", offre);
                    model.addAttribute("postes", posteService.findAll());
                    model.addAttribute("formations", formationService.findAll());
                    model.addAttribute("locals", localService.findAll());
                    return "offre/form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Offre introuvable");
                    return "redirect:/offres";
                });
    }

    @PostMapping
    public String saveOffre(@ModelAttribute Offre offre, RedirectAttributes redirectAttributes,
            HttpSession session) {
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error",
                    "Accès refusé. Seuls les administrateurs peuvent accéder à cette page");
            return "redirect:/login";
        }
        try {
            if (offre.getDateCreation() == null) {
                offre.setDateCreation(LocalDate.now());
            }
            offreService.save(offre);
            redirectAttributes.addFlashAttribute("success", "Offre enregistrée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'enregistrement");
        }
        return "redirect:/offres";
    }

    @GetMapping("/delete/{id}")
    public String deleteOffre(@PathVariable Long id, RedirectAttributes redirectAttributes,
            HttpSession session) {
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error",
                    "Accès refusé. Seuls les administrateurs peuvent accéder à cette page");
            return "redirect:/login";
        }
        try {
            offreService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Offre supprimée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression");
        }
        return "redirect:/offres";
    }
}
