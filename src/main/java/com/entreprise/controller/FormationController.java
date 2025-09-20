package com.entreprise.controller;

import com.entreprise.model.Formation;
import com.entreprise.service.FormationService;
import com.entreprise.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/formations")
public class FormationController {

    @Autowired
    private FormationService formationService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String listFormations(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error",
                    "Accès refusé. Seuls les administrateurs peuvent accéder à cette page");
            return "redirect:/login";
        }

        model.addAttribute("formations", formationService.findAll());
        model.addAttribute("activeSection", "formations");
        return "formation/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error",
                    "Accès refusé. Seuls les administrateurs peuvent accéder à cette page");
            return "redirect:/login";
        }
        model.addAttribute("formation", new Formation());
        model.addAttribute("activeSection", "formations");
        return "formation/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes,
            HttpSession session) {
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error",
                    "Accès refusé. Seuls les administrateurs peuvent accéder à cette page");
            return "redirect:/login";
        }
        return formationService.findById(id)
                .map(formation -> {
                    model.addAttribute("formation", formation);
                    model.addAttribute("activeSection", "formations");
                    return "formation/form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Formation introuvable");
                    return "redirect:/formations";
                });
    }

    @PostMapping
    public String saveFormation(@ModelAttribute Formation formation, RedirectAttributes redirectAttributes,
            HttpSession session) {
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error",
                    "Accès refusé. Seuls les administrateurs peuvent accéder à cette page");
            return "redirect:/login";
        }
        try {
            formationService.save(formation);
            redirectAttributes.addFlashAttribute("success", "Formation enregistrée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'enregistrement");
        }
        return "redirect:/formations";
    }

    @GetMapping("/delete/{id}")
    public String deleteFormation(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpSession session) {

        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error",
                    "Accès refusé. Seuls les administrateurs peuvent accéder à cette page");
            return "redirect:/login";
        }
        try {
            formationService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Formation supprimée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression");
        }
        return "redirect:/formations";
    }
}
