package com.entreprise.controller.admin;

import com.entreprise.model.Entretien;
import com.entreprise.service.EntretienService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/calendrier-entretiens")
public class CalendrierEntretiensController {

    @Autowired
    private EntretienService entretienService;

    /**
     * Affiche la page du calendrier des entretiens
     */
    @GetMapping
    public String afficherCalendrier(Model model) {
        model.addAttribute("activeSection", "calendrier-entretiens");
        model.addAttribute("pageTitle", "Calendrier des Entretiens");
        return "admin/calendrier_entretiens/calendrier";
    }

    /**
     * API pour récupérer les événements du calendrier au format JSON
     */
    @GetMapping("/api/events")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getCalendarEvents(
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end) {
        
        try {
            List<Entretien> entretiens = entretienService.findAll();
            
            List<Map<String, Object>> events = entretiens.stream()
                .filter(entretien -> entretien.getDateHeureEntretien() != null)
                .map(this::convertToCalendarEvent)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * API pour récupérer les détails d'un entretien
     */
    @GetMapping("/api/entretien/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getEntretienDetails(@PathVariable Long id) {
        try {
            Entretien entretien = entretienService.findById(id)
                .orElseThrow(() -> new RuntimeException("Entretien non trouvé"));
            
            Map<String, Object> details = new HashMap<>();
            details.put("id", entretien.getIdEntretien());
            details.put("candidat", entretien.getCandidat().getPrenom() + " " + entretien.getCandidat().getNom());
            details.put("email", entretien.getCandidat().getEmail());
            details.put("poste", entretien.getCandidat().getOffre().getPoste().getNom());
            details.put("offre", "Offre #" + entretien.getCandidat().getOffre().getIdOffre());
            details.put("lieu", entretien.getLieuEntretien());
            details.put("dateHeure", entretien.getDateHeureEntretien().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            details.put("statut", entretien.getStatut());
            
            return ResponseEntity.ok(details);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Convertit un entretien en événement de calendrier
     */
    private Map<String, Object> convertToCalendarEvent(Entretien entretien) {
        Map<String, Object> event = new HashMap<>();
        
        event.put("id", entretien.getIdEntretien());
        event.put("title", entretien.getCandidat().getPrenom() + " " + entretien.getCandidat().getNom());
        event.put("start", entretien.getDateHeureEntretien().toString());
        
        // Utiliser la durée de l'entretien ou 60 minutes par défaut
        int dureeMinutes = entretien.getDureeEntretien() != null ? entretien.getDureeEntretien() : 60;
        event.put("end", entretien.getDateHeureEntretien().plusMinutes(dureeMinutes).toString());
        
        // Couleur selon le statut
        String color = getColorByStatus(entretien.getStatut());
        event.put("backgroundColor", color);
        event.put("borderColor", color);
        
        // Informations additionnelles
        event.put("extendedProps", Map.of(
            "candidat", entretien.getCandidat().getPrenom() + " " + entretien.getCandidat().getNom(),
            "email", entretien.getCandidat().getEmail(),
            "poste", entretien.getCandidat().getOffre().getPoste().getNom(),
            "lieu", entretien.getLieuEntretien() != null ? entretien.getLieuEntretien() : "Non spécifié",
            "statut", entretien.getStatut()
        ));
        
        return event;
    }

    /**
     * Retourne une couleur selon le statut de l'entretien
     */
    private String getColorByStatus(String statut) {
        if (statut == null) return "#6c757d";
        
        switch (statut.toLowerCase()) {
            case "planifié":
            case "planifie":
                return "#007bff"; // Bleu
            case "en cours":
                return "#ffc107"; // Jaune
            case "terminé":
            case "termine":
                return "#28a745"; // Vert
            case "annulé":
            case "annule":
                return "#dc3545"; // Rouge
            case "reporté":
            case "reporte":
                return "#fd7e14"; // Orange
            default:
                return "#6c757d"; // Gris
        }
    }
}