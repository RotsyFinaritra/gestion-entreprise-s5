package com.entreprise.controller;

import com.entreprise.model.Question;
import com.entreprise.service.QuestionService;
import com.entreprise.service.UserService;

import jakarta.servlet.http.HttpSession;

import com.entreprise.service.CompetanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/questions")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CompetanceService competanceService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String listQuestions(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error",
                    "Accès refusé. Seuls les administrateurs peuvent accéder à cette page");
            return "redirect:/login";
        }
        model.addAttribute("questions", questionService.findAll());
        model.addAttribute("activeSection", "questions");
        return "question/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error",
                    "Accès refusé. Seuls les administrateurs peuvent accéder à cette page");
            return "redirect:/login";
        }

        model.addAttribute("question", new Question());
        model.addAttribute("competances", competanceService.findAll());
        return "question/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes,
            HttpSession session) {
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error",
                    "Accès refusé. Seuls les administrateurs peuvent accéder à cette page");
            return "redirect:/login";
        }

        return questionService.findById(id)
                .map(question -> {
                    model.addAttribute("question", question);
                    model.addAttribute("competances", competanceService.findAll());
                    return "question/form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Question introuvable");
                    return "redirect:/questions";
                });
    }

    @PostMapping
    public String saveQuestion(@ModelAttribute Question question, RedirectAttributes redirectAttributes,
            HttpSession session) {
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error",
                    "Accès refusé. Seuls les administrateurs peuvent accéder à cette page");
            return "redirect:/login";
        }

        try {
            questionService.save(question);
            redirectAttributes.addFlashAttribute("success", "Question enregistrée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'enregistrement");
        }
        return "redirect:/questions";
    }

    @GetMapping("/delete/{id}")
    public String deleteQuestion(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpSession session) {
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error",
                    "Accès refusé. Seuls les administrateurs peuvent accéder à cette page");
            return "redirect:/login";
        }

        try {
            questionService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Question supprimée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression");
        }
        return "redirect:/questions";
    }
}
