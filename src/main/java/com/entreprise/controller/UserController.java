package com.entreprise.controller;

import com.entreprise.model.User;
import com.entreprise.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    // Afficher la liste des utilisateurs
    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("pageTitle", "Gestion des Utilisateurs");
        model.addAttribute("pageDescription", "Liste des utilisateurs du système");
        model.addAttribute("activeSection", "users");
        return "user/list";
    }
    
    // Afficher le formulaire de création
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("pageTitle", "Nouvel Utilisateur");
        model.addAttribute("pageDescription", "Créer un nouvel utilisateur");
        model.addAttribute("activeSection", "users");
        return "user/form";
    }
    
    // Traiter la création d'un utilisateur
    @PostMapping
    public String create(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        try {
            // Vérifier si le nom d'utilisateur existe déjà
            if (userService.existsByUsername(user.getUsername())) {
                redirectAttributes.addFlashAttribute("error", "Ce nom d'utilisateur existe déjà !");
                return "redirect:/users/new";
            }
            
            userService.save(user);
            redirectAttributes.addFlashAttribute("success", "Utilisateur créé avec succès !");
            return "redirect:/users";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la création : " + e.getMessage());
            return "redirect:/users/new";
        }
    }
    
    // Afficher le formulaire d'édition
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        var userOptional = userService.findById(id);
        if (userOptional.isPresent()) {
            model.addAttribute("user", userOptional.get());
            model.addAttribute("pageTitle", "Modifier Utilisateur");
            model.addAttribute("pageDescription", "Modifier les informations de l'utilisateur");
            model.addAttribute("activeSection", "users");
            return "user/form";
        } else {
            redirectAttributes.addFlashAttribute("error", "Utilisateur non trouvé !");
            return "redirect:/users";
        }
    }
    
    // Traiter la modification d'un utilisateur
    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id, @ModelAttribute User user, RedirectAttributes redirectAttributes) {
        try {
            var existingUserOptional = userService.findById(id);
            if (existingUserOptional.isPresent()) {
                User existingUser = existingUserOptional.get();
                
                // Vérifier si le nouveau username n'existe pas déjà (sauf pour l'utilisateur actuel)
                if (!existingUser.getUsername().equals(user.getUsername()) && 
                    userService.existsByUsername(user.getUsername())) {
                    redirectAttributes.addFlashAttribute("error", "Ce nom d'utilisateur existe déjà !");
                    return "redirect:/users/edit/" + id;
                }
                
                existingUser.setUsername(user.getUsername());
                existingUser.setPassword(user.getPassword());
                existingUser.setRole(user.getRole());
                
                userService.update(existingUser);
                redirectAttributes.addFlashAttribute("success", "Utilisateur modifié avec succès !");
                return "redirect:/users";
            } else {
                redirectAttributes.addFlashAttribute("error", "Utilisateur non trouvé !");
                return "redirect:/users";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la modification : " + e.getMessage());
            return "redirect:/users/edit/" + id;
        }
    }
    
    // Supprimer un utilisateur
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            var userOptional = userService.findById(id);
            if (userOptional.isPresent()) {
                userService.deleteById(id);
                redirectAttributes.addFlashAttribute("success", "Utilisateur supprimé avec succès !");
            } else {
                redirectAttributes.addFlashAttribute("error", "Utilisateur non trouvé !");
            }
            return "redirect:/users";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression : " + e.getMessage());
            return "redirect:/users";
        }
    }
    
    // Afficher les détails d'un utilisateur
    @GetMapping("/view/{id}")
    public String view(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        var userOptional = userService.findById(id);
        if (userOptional.isPresent()) {
            model.addAttribute("user", userOptional.get());
            model.addAttribute("pageTitle", "Détails Utilisateur");
            model.addAttribute("pageDescription", "Informations détaillées de l'utilisateur");
            model.addAttribute("activeSection", "users");
            return "user/view";
        } else {
            redirectAttributes.addFlashAttribute("error", "Utilisateur non trouvé !");
            return "redirect:/users";
        }
    }
}
