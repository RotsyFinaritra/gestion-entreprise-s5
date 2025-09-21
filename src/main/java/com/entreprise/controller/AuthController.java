package com.entreprise.controller;

import com.entreprise.model.User;
import com.entreprise.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/login")
    public String showLogin(Model model) {
        model.addAttribute("pageTitle", "Connexion");
        model.addAttribute("pageDescription", "Connectez-vous à votre compte");
        return "login";
    }
    
    @PostMapping("/login")
    public String processLogin(@RequestParam String username, 
                             @RequestParam String password,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        
        // Vérifier les identifiants
        var userOptional = userService.findByUsername(username);
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Simple vérification du mot de passe (à améliorer avec hash)
            if (password.equals(user.getPassword())) {
                // Enregistrer l'utilisateur dans la session
                session.setAttribute("user", user);
                session.setAttribute("userId", user.getIdUser());
                session.setAttribute("username", user.getUsername());
                session.setAttribute("role", user.getRole());
                
                redirectAttributes.addFlashAttribute("success", "Connexion réussie ! Bienvenue " + user.getUsername());
                
                // Redirection selon le rôle
                if ("DEPARTEMENT".equalsIgnoreCase(user.getRole())) {
                    return "redirect:/departement/dashboard";
                } else if ("RH".equalsIgnoreCase(user.getRole()) || "admin".equalsIgnoreCase(user.getRole())) {
                    return "redirect:/";
                } else {
                    return "redirect:/client"; // Pour les autres rôles
                }
            }
        }
        
        redirectAttributes.addFlashAttribute("error", "Nom d'utilisateur ou mot de passe incorrect");
        return "redirect:/login";
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        // Nettoyer la session
        session.removeAttribute("user");
        session.removeAttribute("userId");
        session.removeAttribute("username");
        session.removeAttribute("role");
        session.invalidate();
        
        redirectAttributes.addFlashAttribute("success", "Vous avez été déconnecté avec succès");
        return "redirect:/login";
    }
}
