package com.entreprise.repository;

import com.entreprise.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Rechercher un utilisateur par nom d'utilisateur
    Optional<User> findByUsername(String username);
    
    // Vérifier si un nom d'utilisateur existe déjà
    boolean existsByUsername(String username);
    
    // Rechercher des utilisateurs par rôle
    java.util.List<User> findByRole(String role);
}
