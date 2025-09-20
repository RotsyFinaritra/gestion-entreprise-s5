package com.entreprise.controller;

import com.entreprise.model.Local;
import com.entreprise.service.LocalService;
import com.entreprise.service.UserService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/locals")
public class LocalController {

    @Autowired
    private LocalService localService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String listLocals(Model model, RedirectAttributes redirectAttributes, HttpSession session) {
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error",
                    "Accès refusé. Seuls les administrateurs peuvent accéder à cette page");
            return "redirect:/login";
        }
        model.addAttribute("locals", localService.findAll());
        model.addAttribute("activeSection", "locals");
        return "local/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model, RedirectAttributes redirectAttributes, HttpSession session) {
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error",
                    "Accès refusé. Seuls les administrateurs peuvent accéder à cette page");
            return "redirect:/login";
        }
        model.addAttribute("local", new Local());
        model.addAttribute("activeSection", "locals");
        return "local/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes,
            HttpSession session) {
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error",
                    "Accès refusé. Seuls les administrateurs peuvent accéder à cette page");
            return "redirect:/login";
        }
        return localService.findById(id)
                .map(local -> {
                    model.addAttribute("local", local);
                    model.addAttribute("activeSection", "locals");
                    return "local/form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Localisation introuvable");
                    return "redirect:/locals";
                });
    }

    @PostMapping
    public String saveLocal(@ModelAttribute Local local, RedirectAttributes redirectAttributes, HttpSession session) {
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error",
                    "Accès refusé. Seuls les administrateurs peuvent accéder à cette page");
            return "redirect:/login";
        }
        try {
            localService.save(local);
            redirectAttributes.addFlashAttribute("success", "Localisation enregistrée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'enregistrement");
        }
        return "redirect:/locals";
    }

    @GetMapping("/delete/{id}")
    public String deleteLocal(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpSession session) {
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error",
                    "Accès refusé. Seuls les administrateurs peuvent accéder à cette page");
            return "redirect:/login";
        }
        try {
            localService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Localisation supprimée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression");
        }
        return "redirect:/locals";
    }
}
