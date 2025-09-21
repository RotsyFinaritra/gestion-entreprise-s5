package com.entreprise.service;

import com.entreprise.model.User;
import com.entreprise.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    // Créer un nouvel utilisateur
    public User save(User user) {
        return userRepository.save(user);
    }
    
    // Récupérer tous les utilisateurs
    public List<User> findAll() {
        return userRepository.findAll();
    }
    
    // Récupérer un utilisateur par ID
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    // Récupérer un utilisateur par nom d'utilisateur
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    // Vérifier si un nom d'utilisateur existe
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    // Récupérer les utilisateurs par rôle
    public List<User> findByRole(String role) {
        return userRepository.findByRole(role);
    }
    
    // Mettre à jour un utilisateur
    public User update(User user) {
        return userRepository.save(user);
    }
    
    // Supprimer un utilisateur par ID
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
    
    // Supprimer un utilisateur
    public void delete(User user) {
        userRepository.delete(user);
    }
    
    // Compter le nombre total d'utilisateurs
    public long count() {
        return userRepository.count();
    }
    
    // Vérifier si l'utilisateur en session est un administrateur
    public boolean isAdmin(jakarta.servlet.http.HttpSession session) {
        String username = (String) session.getAttribute("username");
        String role = (String) session.getAttribute("role");
        return username != null && "admin".equalsIgnoreCase(role);
    }
    
    // === MÉTHODES SPÉCIFIQUES AUX DÉPARTEMENTS ===
    
    // Récupérer tous les départements
    public List<User> findAllDepartements() {
        return userRepository.findByRole("DEPARTEMENT");
    }
    
    // Vérifier si l'utilisateur est un département
    public boolean isDepartement(User user) {
        return user != null && "DEPARTEMENT".equalsIgnoreCase(user.getRole());
    }
    
    // Vérifier si l'utilisateur en session est un département
    public boolean isDepartement(jakarta.servlet.http.HttpSession session) {
        String role = (String) session.getAttribute("role");
        return "DEPARTEMENT".equalsIgnoreCase(role);
    }
    
    // Récupérer l'utilisateur connecté depuis la session
    public Optional<User> getCurrentUser(jakarta.servlet.http.HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username != null) {
            return findByUsername(username);
        }
        return Optional.empty();
    }
}