package com.entreprise.controller;

import com.entreprise.model.ReponseQuestion;
import com.entreprise.service.ReponseQuestionService;
import com.entreprise.service.UserService;

import jakarta.servlet.http.HttpSession;

import com.entreprise.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/reponses")
public class ReponseQuestionController {

    @Autowired
    private ReponseQuestionService reponseQuestionService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String listReponses(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error",
                    "Accès refusé. Seuls les administrateurs peuvent accéder à cette page");
            return "redirect:/login";
        }
        model.addAttribute("reponses", reponseQuestionService.findAll());
        return "reponse/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error",
                    "Accès refusé. Seuls les administrateurs peuvent accéder à cette page");
            return "redirect:/login";
        }
        model.addAttribute("reponse", new ReponseQuestion());
        model.addAttribute("questions", questionService.findAll());
        return "reponse/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes,
            HttpSession session) {
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error",
                    "Accès refusé. Seuls les administrateurs peuvent accéder à cette page");
            return "redirect:/login";
        }
        return reponseQuestionService.findById(id)
                .map(reponse -> {
                    model.addAttribute("reponse", reponse);
                    model.addAttribute("questions", questionService.findAll());
                    return "reponse/form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Réponse introuvable");
                    return "redirect:/reponses";
                });
    }

    @PostMapping
    public String saveReponse(@ModelAttribute ReponseQuestion reponse, RedirectAttributes redirectAttributes,
            HttpSession session) {
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error",
                    "Accès refusé. Seuls les administrateurs peuvent accéder à cette page");
            return "redirect:/login";
        }
        try {
            reponseQuestionService.save(reponse);
            redirectAttributes.addFlashAttribute("success", "Réponse enregistrée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'enregistrement");
        }
        return "redirect:/reponses";
    }

    @GetMapping("/delete/{id}")
    public String deleteReponse(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpSession session) {
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error",
                    "Accès refusé. Seuls les administrateurs peuvent accéder à cette page");
            return "redirect:/login";
        }
        try {
            reponseQuestionService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Réponse supprimée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression");
        }
        return "redirect:/reponses";
    }
}
