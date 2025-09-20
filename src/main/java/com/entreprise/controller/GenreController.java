package com.entreprise.controller;

import com.entreprise.model.Genre;
import com.entreprise.service.GenreService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.entreprise.service.UserService;

@Controller
@RequestMapping("/genres")
public class GenreController {

    @Autowired
    private GenreService genreService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String listGenres(Model model, RedirectAttributes redirectAttributes, HttpSession session) {
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error",
                    "Accès refusé. Seuls les administrateurs peuvent accéder à cette page");
            return "redirect:/login";
        }
        model.addAttribute("genres", genreService.findAll());
        model.addAttribute("activeSection", "genres");
        return "genre/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model, RedirectAttributes redirectAttributes, HttpSession session) {
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error",
                    "Accès refusé. Seuls les administrateurs peuvent accéder à cette page");
            return "redirect:/login";
        }
        model.addAttribute("genre", new Genre());
        model.addAttribute("activeSection", "genres");
        return "genre/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes,
            HttpSession session) {
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error",
                    "Accès refusé. Seuls les administrateurs peuvent accéder à cette page");
            return "redirect:/login";
        }
        return genreService.findById(id)
                .map(genre -> {
                    model.addAttribute("genre", genre);
                    model.addAttribute("activeSection", "genres");
                    return "genre/form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Genre introuvable");
                    return "redirect:/genres";
                });
    }

    @PostMapping
    public String saveGenre(@ModelAttribute Genre genre, RedirectAttributes redirectAttributes, HttpSession session) {
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error",
                    "Accès refusé. Seuls les administrateurs peuvent accéder à cette page");
            return "redirect:/login";
        }
        try {
            genreService.save(genre);
            redirectAttributes.addFlashAttribute("success", "Genre enregistré avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'enregistrement");
        }
        return "redirect:/genres";
    }

    @GetMapping("/delete/{id}")
    public String deleteGenre(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpSession session) {
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error",
                    "Accès refusé. Seuls les administrateurs peuvent accéder à cette page");
            return "redirect:/login";
        }
        try {
            genreService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Genre supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression");
        }
        return "redirect:/genres";
    }
}
