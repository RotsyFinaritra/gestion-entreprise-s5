package com.entreprise.controller;

import com.entreprise.model.Candidat;
import com.entreprise.model.Formation;
import com.entreprise.model.Status;
import com.entreprise.model.StatusCandidat;
import com.entreprise.model.ReponseCandidat;
import com.entreprise.model.Offre;
import com.entreprise.service.CandidatService;
import com.entreprise.service.StatusService;
import com.entreprise.service.StatusCandidatService;
import com.entreprise.service.Test2Service;
import com.entreprise.service.UserService;
import com.entreprise.service.OffreService;
import com.entreprise.repository.ReponseCandidatRepository;
import com.entreprise.repository.StatusCandidatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.time.LocalDate;
import java.time.Period;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;

@Controller
@RequestMapping("/admin/candidats")
public class AdminCandidatController {

    @Autowired
    private CandidatService candidatService;
    
    @Autowired
    private StatusService statusService;
    
    @Autowired
    private StatusCandidatService statusCandidatService;
    
    @Autowired
    private Test2Service test2Service;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private OffreService offreService;
    
    @Autowired
    private ReponseCandidatRepository reponseCandidatRepository;
    
    @Autowired
    private StatusCandidatRepository statusCandidatRepository;

    @GetMapping
    public String listCandidatsAdmin(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Accès refusé. Seuls les administrateurs peuvent accéder à cette page");
            return "redirect:/login";
        }
        
        List<Candidat> candidats = candidatService.findAll();
        
        // Calculer les âges des candidats pour le template
        Map<Long, Integer> agesMap = candidats.stream()
            .filter(c -> c.getDateNaissance() != null)
            .collect(Collectors.toMap(
                Candidat::getIdCandidat,
                c -> Period.between(c.getDateNaissance(), LocalDate.now()).getYears()
            ));
        
        // Regrouper les candidats par offre (et non par poste)
        Map<String, List<Candidat>> candidatsParOffre = candidats.stream()
            .filter(c -> c.getOffre() != null)
            .collect(Collectors.groupingBy(c -> {
                String posteNom = c.getOffre().getPoste() != null ? c.getOffre().getPoste().getNom() : "Poste non défini";
                String mission = c.getOffre().getMission() != null && !c.getOffre().getMission().trim().isEmpty() 
                    ? " - " + (c.getOffre().getMission().length() > 50 
                        ? c.getOffre().getMission().substring(0, 50) + "..." 
                        : c.getOffre().getMission())
                    : "";
                return posteNom + mission + " (ID: " + c.getOffre().getIdOffre() + ")";
            }));
        
        // Vérifier pour chaque offre si le premier test a été effectué
        Map<Long, Boolean> premierTestEffectueParOffre = candidats.stream()
            .filter(c -> c.getOffre() != null)
            .collect(Collectors.groupingBy(
                c -> c.getOffre().getIdOffre(),
                Collectors.collectingAndThen(
                    Collectors.toList(),
                    candidatsOffre -> candidatsOffre.stream()
                        .anyMatch(c -> c.getStatusCandidats().stream()
                            .anyMatch(sc -> sc.getStatus().getNom().equals("Pass Test 1") || 
                                           sc.getStatus().getNom().equals("Refus Test 1")))
                )
            ));
        
        // Calculer les statistiques
        long totalCandidats = candidats.size();
        long candidatsAvecAge = candidats.stream()
            .filter(c -> c.getDateNaissance() != null)
            .count();
        long candidatsEnAttente = candidats.stream()
            .filter(c -> c.getStatusCandidats().isEmpty() || 
                        c.getStatusCandidats().stream().noneMatch(sc -> 
                            sc.getStatus().getNom().contains("test") || 
                            sc.getStatus().getNom().contains("refus") ||
                            sc.getStatus().getNom().contains("accepté")
                        ))
            .count();
        
        model.addAttribute("candidats", candidats);
        model.addAttribute("candidatsParOffre", candidatsParOffre);
        model.addAttribute("agesMap", agesMap);
        model.addAttribute("premierTestEffectueParOffre", premierTestEffectueParOffre);
        model.addAttribute("totalCandidats", totalCandidats);
        model.addAttribute("candidatsAvecAge", candidatsAvecAge);
        model.addAttribute("candidatsEnAttente", candidatsEnAttente);
        model.addAttribute("activeSection", "admin-candidats");
        
        return "admin/candidats/list";
    }

    @PostMapping("/premier-test/{offreId}")
    public String procederPremierTest(@PathVariable Long offreId, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Accès refusé. Seuls les administrateurs peuvent accéder à cette page");
            return "redirect:/login";
        }
        
        try {
            // Récupérer tous les candidats pour cette offre
            List<Candidat> candidats = candidatService.findByOffreId(offreId);
            
            if (candidats.isEmpty()) {
                redirectAttributes.addFlashAttribute("warning", "Aucun candidat trouvé pour cette offre");
                return "redirect:/admin/candidats";
            }
            
            // Récupérer l'offre pour les critères d'âge
            Candidat premierCandidat = candidats.get(0);
            if (premierCandidat.getOffre() == null) {
                redirectAttributes.addFlashAttribute("error", "Offre non trouvée");
                return "redirect:/admin/candidats";
            }
            
            Integer ageMin = premierCandidat.getOffre().getAgeMin();
            Integer ageMax = premierCandidat.getOffre().getAgeMax();
            
            if (ageMin == null && ageMax == null) {
                redirectAttributes.addFlashAttribute("warning", "Aucun critère d'âge défini pour cette offre");
                return "redirect:/admin/candidats";
            }
            
            // Récupérer les statuts
            Status statusPassTest1 = statusService.findByNom("Pass Test 1")
                .orElseGet(() -> {
                    Status newStatus = new Status("Pass Test 1");
                    return statusService.save(newStatus);
                });
                
            Status statusRefusTest1 = statusService.findByNom("Refus Test 1")
                .orElseGet(() -> {
                    Status newStatus = new Status("Refus Test 1");
                    return statusService.save(newStatus);
                });
            
            int candidatsAcceptes = 0;
            int candidatsRefuses = 0;
            int candidatsSansAge = 0;
            int candidatsSansCompetences = 0;
            int candidatsSansFormation = 0;
            
            // Traiter chaque candidat
            for (Candidat candidat : candidats) {
                boolean estAccepte = true;
                String raisonRefus = "";
                
                // 1. Vérifier l'âge
                if (candidat.getDateNaissance() == null) {
                    candidatsSansAge++;
                    estAccepte = false;
                    raisonRefus += "Âge non renseigné; ";
                } else {
                    int age = Period.between(candidat.getDateNaissance(), LocalDate.now()).getYears();
                    
                    // Vérifier l'âge minimum
                    if (ageMin != null && age < ageMin) {
                        estAccepte = false;
                        raisonRefus += "Âge insuffisant (" + age + " < " + ageMin + "); ";
                    }
                    
                    // Vérifier l'âge maximum
                    if (ageMax != null && age > ageMax) {
                        estAccepte = false;
                        raisonRefus += "Âge trop élevé (" + age + " > " + ageMax + "); ";
                    }
                }
                
                // 2. Vérifier les compétences
                if (premierCandidat.getOffre().getPoste() != null && 
                    premierCandidat.getOffre().getPoste().getPosteCompetances() != null &&
                    !premierCandidat.getOffre().getPoste().getPosteCompetances().isEmpty()) {
                    
                    // Récupérer les compétences requises pour l'offre
                    var competencesRequises = premierCandidat.getOffre().getPoste().getPosteCompetances().stream()
                        .map(pc -> pc.getCompetance())
                        .collect(Collectors.toSet());
                    
                    // Récupérer les compétences du candidat
                    var competencesCandidat = candidat.getCandidatCompetances() != null ? 
                        candidat.getCandidatCompetances().stream()
                            .map(cc -> cc.getCompetance())
                            .collect(Collectors.toSet()) : 
                        Collections.emptySet();
                    
                    if (competencesCandidat.isEmpty()) {
                        candidatsSansCompetences++;
                        estAccepte = false;
                        raisonRefus += "Aucune compétence renseignée; ";
                    } else {
                        // Vérifier s'il y a au moins une compétence en commun
                        boolean aUneCompetenceRequise = competencesRequises.stream()
                            .anyMatch(competencesCandidat::contains);
                        
                        if (!aUneCompetenceRequise) {
                            estAccepte = false;
                            raisonRefus += "Aucune compétence requise trouvée; ";
                        }
                    }
                }
                
                // 3. Vérifier les formations requises par le poste
                if (premierCandidat.getOffre().getPoste() != null && 
                    premierCandidat.getOffre().getPoste().getPosteFormations() != null &&
                    !premierCandidat.getOffre().getPoste().getPosteFormations().isEmpty()) {
                    
                    if (candidat.getFormationCandidats() == null || candidat.getFormationCandidats().isEmpty()) {
                        candidatsSansFormation++;
                        estAccepte = false;
                        raisonRefus += "Formation non renseignée; ";
                    } else {
                        // Récupérer toutes les formations requises par le poste
                        List<Formation> formationsRequises = premierCandidat.getOffre().getPoste().getPosteFormations().stream()
                            .map(pf -> pf.getFormation())
                            .collect(Collectors.toList());
                        
                        // Récupérer les formations du candidat
                        List<Formation> formationsCandidat = candidat.getFormationCandidats().stream()
                            .map(fc -> fc.getFormation())
                            .collect(Collectors.toList());
                        
                        // Vérifier si le candidat a au moins une formation requise
                        boolean aAuMoinsUneFormationRequise = formationsRequises.stream()
                            .anyMatch(formationsCandidat::contains);
                        
                        if (!aAuMoinsUneFormationRequise) {
                            estAccepte = false;
                            String formationsCandidatNoms = formationsCandidat.stream()
                                .map(Formation::getNom)
                                .collect(Collectors.joining(", "));
                            String formationsRequisesNoms = formationsRequises.stream()
                                .map(Formation::getNom)
                                .collect(Collectors.joining(", "));
                            raisonRefus += "Formation non compatible (a: " + formationsCandidatNoms + 
                                ", requis: " + formationsRequisesNoms + "); ";
                        }
                    }
                }
                
                // Créer le statut candidat avec détails
                Status statusAAppliquer = estAccepte ? statusPassTest1 : statusRefusTest1;
                StatusCandidat statusCandidat = new StatusCandidat(statusAAppliquer, candidat, LocalDate.now());
                
                // Ajouter les détails du refus si nécessaire
                if (!estAccepte && !raisonRefus.isEmpty()) {
                    // On pourrait ajouter un champ commentaire dans StatusCandidat pour stocker la raison
                    // Pour l'instant, on garde juste le statut
                }
                
                statusCandidatService.save(statusCandidat);
                
                if (estAccepte) {
                    candidatsAcceptes++;
                } else {
                    candidatsRefuses++;
                }
            }
            
            String message = String.format("Premier test terminé ! " +
                "✅ Candidats acceptés: %d | " +
                "❌ Refusés: %d | " +
                "📅 Sans âge: %d | " + 
                "🎯 Sans compétences: %d | " +
                "🎓 Sans formation: %d",
                candidatsAcceptes, candidatsRefuses, candidatsSansAge, candidatsSansCompetences, candidatsSansFormation);
                
            redirectAttributes.addFlashAttribute("success", message);
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors du premier test: " + e.getMessage());
        }
        
        return "redirect:/admin/candidats";
    }

    @GetMapping("/detail/{id}")
    public String detailCandidatAdmin(@PathVariable Long id, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Accès refusé. Seuls les administrateurs peuvent accéder à cette page");
            return "redirect:/login";
        }
        
        return candidatService.findById(id)
            .map(candidat -> {
                model.addAttribute("candidat", candidat);
                
                // Calculer l'âge si date de naissance disponible
                if (candidat.getDateNaissance() != null) {
                    int age = Period.between(candidat.getDateNaissance(), LocalDate.now()).getYears();
                    model.addAttribute("age", age);
                }
                
                // Vérifier la compatibilité avec l'offre
                if (candidat.getOffre() != null) {
                    Integer ageMin = candidat.getOffre().getAgeMin();
                    Integer ageMax = candidat.getOffre().getAgeMax();
                    
                    // Compatibilité d'âge
                    if (candidat.getDateNaissance() != null && (ageMin != null || ageMax != null)) {
                        int age = Period.between(candidat.getDateNaissance(), LocalDate.now()).getYears();
                        boolean compatibleAge = true;
                        
                        if (ageMin != null && age < ageMin) {
                            compatibleAge = false;
                        }
                        if (ageMax != null && age > ageMax) {
                            compatibleAge = false;
                        }
                        
                        model.addAttribute("compatibleAge", compatibleAge);
                        model.addAttribute("ageRequis", 
                            (ageMin != null && ageMax != null) ? ageMin + "-" + ageMax + " ans" :
                            (ageMin != null) ? ageMin + "+ ans" :
                            (ageMax != null) ? "Moins de " + ageMax + " ans" : "Non défini"
                        );
                    }
                    
                    // Compatibilité des compétences
                    if (candidat.getOffre().getPoste() != null &&
                        candidat.getOffre().getPoste().getPosteCompetances() != null && 
                        !candidat.getOffre().getPoste().getPosteCompetances().isEmpty()) {
                        
                        var competencesRequises = candidat.getOffre().getPoste().getPosteCompetances().stream()
                            .map(pc -> pc.getCompetance())
                            .collect(Collectors.toList());
                        
                        var competencesCandidat = candidat.getCandidatCompetances() != null ? 
                            candidat.getCandidatCompetances().stream()
                                .map(cc -> cc.getCompetance())
                                .collect(Collectors.toList()) : 
                            Collections.emptyList();
                        
                        // Compétences en commun
                        var competencesCommunes = competencesCandidat.stream()
                            .filter(competencesRequises::contains)
                            .collect(Collectors.toList());
                        
                        // Compétences manquantes
                        var competencesManquantes = competencesRequises.stream()
                            .filter(cr -> !competencesCandidat.contains(cr))
                            .collect(Collectors.toList());
                        
                        model.addAttribute("competencesRequises", competencesRequises);
                        model.addAttribute("competencesCandidat", competencesCandidat);
                        model.addAttribute("competencesCommunes", competencesCommunes);
                        model.addAttribute("competencesManquantes", competencesManquantes);
                        model.addAttribute("compatibleCompetences", !competencesCommunes.isEmpty());
                    }
                    
                    // Compatibilité des formations
                    if (candidat.getOffre().getPoste() != null && 
                        candidat.getOffre().getPoste().getPosteFormations() != null &&
                        !candidat.getOffre().getPoste().getPosteFormations().isEmpty()) {
                        
                        // Récupérer toutes les formations requises par le poste
                        var formationsRequises = candidat.getOffre().getPoste().getPosteFormations().stream()
                            .map(pf -> pf.getFormation())
                            .collect(Collectors.toList());
                        
                        var formationsCandidat = candidat.getFormationCandidats() != null ? 
                            candidat.getFormationCandidats().stream()
                                .map(fc -> fc.getFormation())
                                .collect(Collectors.toList()) : 
                            Collections.emptyList();
                        
                        // Le candidat est compatible s'il a au moins une formation requise
                        boolean aAuMoinsUneFormationRequise = formationsRequises.stream()
                            .anyMatch(formationsCandidat::contains);
                        
                        // Trouver les formations communes
                        var formationsCommunes = formationsRequises.stream()
                            .filter(formationsCandidat::contains)
                            .collect(Collectors.toList());
                        
                        model.addAttribute("formationsRequises", formationsRequises);
                        model.addAttribute("formationsCandidat", formationsCandidat);
                        model.addAttribute("formationsCommunes", formationsCommunes);
                        model.addAttribute("compatibleFormation", aAuMoinsUneFormationRequise);
                    }
                }
                
                model.addAttribute("activeSection", "admin-candidats");
                return "admin/candidats/detail";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("error", "Candidat introuvable");
                return "redirect:/admin/candidats";
            });
    }

    @GetMapping("/by-offre/{offreId}")
    public String listCandidatsByOffre(@PathVariable Long offreId, 
                                     @RequestParam(value = "status", required = false) String statusFilter,
                                     Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Accès refusé. Seuls les administrateurs peuvent accéder à cette page");
            return "redirect:/login";
        }
        
        // Récupérer les candidats pour cette offre
        List<Candidat> candidats = candidatService.findByOffreId(offreId);
        
        if (candidats.isEmpty()) {
            redirectAttributes.addFlashAttribute("warning", "Aucun candidat trouvé pour cette offre");
            return "redirect:/admin/candidats";
        }
        
        // Calculer les âges des candidats
        Map<Long, Integer> agesMap = candidats.stream()
            .filter(c -> c.getDateNaissance() != null)
            .collect(Collectors.toMap(
                Candidat::getIdCandidat,
                c -> Period.between(c.getDateNaissance(), LocalDate.now()).getYears()
            ));
        
        // Filtrer par statut si spécifié
        if (statusFilter != null && !statusFilter.isEmpty() && !statusFilter.equals("tous")) {
            candidats = candidats.stream()
                .filter(c -> c.getStatusCandidats().stream()
                    .anyMatch(sc -> sc.getStatus().getNom().toLowerCase().contains(statusFilter.toLowerCase())))
                .collect(Collectors.toList());
        }
        
        // Récupérer l'offre pour les informations
        Candidat premierCandidat = candidatService.findByOffreId(offreId).get(0);
        
        // Vérifier si le premier test a déjà été effectué
        boolean premierTestEffectue = candidatService.findByOffreId(offreId).stream()
            .anyMatch(c -> c.getStatusCandidats().stream()
                .anyMatch(sc -> sc.getStatus().getNom().equals("Pass Test 1") || 
                               sc.getStatus().getNom().equals("Refus Test 1")));
        
        // Récupérer tous les statuts pour le filtre
        List<Status> tousLesStatuts = statusService.findAll();
        
        // Statistiques par statut
        Map<String, Long> statistiquesStatuts = candidatService.findByOffreId(offreId).stream()
            .flatMap(c -> c.getStatusCandidats().stream())
            .collect(Collectors.groupingBy(
                sc -> sc.getStatus().getNom(),
                Collectors.counting()
            ));
        
        model.addAttribute("candidats", candidats);
        model.addAttribute("agesMap", agesMap);
        model.addAttribute("offre", premierCandidat.getOffre());
        model.addAttribute("statusFilter", statusFilter);
        model.addAttribute("tousLesStatuts", tousLesStatuts);
        model.addAttribute("statistiquesStatuts", statistiquesStatuts);
        model.addAttribute("premierTestEffectue", premierTestEffectue);
        model.addAttribute("activeSection", "admin-candidats");
        
        return "admin/candidats/by-offre";
    }
    
    @PostMapping("/envoyer-test2/{offreId}")
    public String envoyerInvitationsTest2(@PathVariable Long offreId, 
                                         RedirectAttributes redirectAttributes) {
        try {
            int invitationsEnvoyees = test2Service.envoyerInvitationsTest2(offreId);
            if (invitationsEnvoyees > 0) {
                redirectAttributes.addFlashAttribute("success", 
                    invitationsEnvoyees + " invitation(s) Test 2 envoyée(s) avec succès!");
            } else {
                redirectAttributes.addFlashAttribute("info", 
                    "Aucune nouvelle invitation envoyée. Tous les candidats éligibles ont déjà reçu une invitation Test 2.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'envoi des invitations: " + e.getMessage());
        }
        
        return "redirect:/admin/candidats/by-offre/" + offreId;
    }

    @GetMapping("/classement")
    public String afficherClassementTest2(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Accès refusé. Seuls les administrateurs peuvent accéder à cette page");
            return "redirect:/login";
        }
        
        try {
            System.out.println("=== PAGE CLASSEMENT TEST 2 ADMIN ===");
            
            // Récupérer tous les candidats ayant fait le Test 2 (tous les statuts)
            List<String> statutsTest2 = Arrays.asList("Test 2 Terminé", "Pass Test 2", "Echec Test 2");
            List<Candidat> candidatsTest2 = new ArrayList<>();
            Map<Long, String> statutsActuels = new HashMap<>();
            
            for (String nomStatut : statutsTest2) {
                Status status = statusService.findByNom(nomStatut).orElse(null);
                if (status != null) {
                    List<StatusCandidat> statusCandidats = statusCandidatRepository.findByStatusIdStatus(status.getIdStatus());
                    for (StatusCandidat sc : statusCandidats) {
                        Candidat candidat = sc.getCandidat();
                        if (!candidatsTest2.contains(candidat)) {
                            candidatsTest2.add(candidat);
                        }
                        // Garder le statut le plus récent pour chaque candidat
                        String statutActuel = statutsActuels.get(candidat.getIdCandidat());
                        if (statutActuel == null) {
                            statutsActuels.put(candidat.getIdCandidat(), nomStatut);
                        } else {
                            // Prioriser Pass Test 2 et Echec Test 2 sur Test 2 Terminé
                            if ("Test 2 Terminé".equals(statutActuel) && 
                                ("Pass Test 2".equals(nomStatut) || "Echec Test 2".equals(nomStatut))) {
                                statutsActuels.put(candidat.getIdCandidat(), nomStatut);
                            }
                        }
                    }
                }
            }
            
            System.out.println("Nombre total de candidats Test 2: " + candidatsTest2.size());
            
            // Grouper les candidats par poste
            Map<String, List<Map<String, Object>>> resultatsParPoste = new LinkedHashMap<>();
            
            for (Candidat candidat : candidatsTest2) {
                String nomPoste = candidat.getOffre().getPoste().getNom();
                
                Map<String, Object> resultatComplet = test2Service.getResultatCompletTest2(candidat);
                resultatComplet.put("candidat", candidat);
                resultatComplet.put("statutActuel", statutsActuels.get(candidat.getIdCandidat()));
                
                resultatsParPoste.computeIfAbsent(nomPoste, k -> new ArrayList<>()).add(resultatComplet);
            }
            
            // Trier les candidats par note dans chaque poste et ajouter le rang
            for (Map.Entry<String, List<Map<String, Object>>> entry : resultatsParPoste.entrySet()) {
                List<Map<String, Object>> resultatsPoste = entry.getValue();
                
                // Trier par note décroissante
                resultatsPoste.sort((r1, r2) -> {
                    Double note1 = (Double) r1.get("noteSur20");
                    Double note2 = (Double) r2.get("noteSur20");
                    return Double.compare(note2, note1);
                });
                
                // Ajouter le rang pour ce poste
                for (int i = 0; i < resultatsPoste.size(); i++) {
                    resultatsPoste.get(i).put("rang", i + 1);
                }
            }
            
            // Calculer des statistiques globales
            List<Double> toutesLesNotes = resultatsParPoste.values().stream()
                .flatMap(List::stream)
                .map(r -> (Double) r.get("noteSur20"))
                .collect(Collectors.toList());
                
            if (!toutesLesNotes.isEmpty()) {
                double moyenneGenerale = toutesLesNotes.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
                double noteMax = toutesLesNotes.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
                double noteMin = toutesLesNotes.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
                
                model.addAttribute("moyenneGenerale", Math.round(moyenneGenerale * 100.0) / 100.0);
                model.addAttribute("noteMax", noteMax);
                model.addAttribute("noteMin", noteMin);
            }
            
            // Calculer les statistiques pour le template
            long totalCandidats = candidatsTest2.size();
            long termine = statutsActuels.values().stream().mapToLong(s -> "Test 2 Terminé".equals(s) ? 1 : 0).sum();
            long admis = statutsActuels.values().stream().mapToLong(s -> "Pass Test 2".equals(s) ? 1 : 0).sum();
            long refuses = statutsActuels.values().stream().mapToLong(s -> "Echec Test 2".equals(s) ? 1 : 0).sum();
            
            Map<String, Long> statistiques = new HashMap<>();
            statistiques.put("totalCandidats", totalCandidats);
            statistiques.put("termine", termine);
            statistiques.put("admis", admis);
            statistiques.put("refuses", refuses);
            
            // Transformer resultatsParPoste en candidatsParPoste pour être compatible avec le template
            Map<Object, List<Object>> candidatsParPoste = new LinkedHashMap<>();
            for (Map.Entry<String, List<Map<String, Object>>> entry : resultatsParPoste.entrySet()) {
                String nomPoste = entry.getKey();
                List<Map<String, Object>> resultats = entry.getValue();
                
                // Créer un objet poste fictif pour le template
                Map<String, Object> poste = new HashMap<>();
                poste.put("nom", nomPoste);
                poste.put("id", nomPoste.hashCode()); // ID fictif
                
                // Créer une offre fictive
                if (!resultats.isEmpty()) {
                    Candidat premierCandidat = (Candidat) resultats.get(0).get("candidat");
                    Map<String, Object> offre = new HashMap<>();
                    offre.put("id", premierCandidat.getOffre().getIdOffre());
                    offre.put("titre", "Offre pour " + nomPoste);
                    poste.put("offre", offre);
                }
                
                // Transformer les résultats en format attendu par le template
                List<Object> candidatsTransformes = new ArrayList<>();
                for (Map<String, Object> resultat : resultats) {
                    Map<String, Object> candidatTransforme = new HashMap<>();
                    candidatTransforme.put("candidat", resultat.get("candidat"));
                    candidatTransforme.put("nombreBonnesReponses", resultat.get("bonnesReponses"));
                    candidatTransforme.put("nombreTotalQuestions", resultat.get("nombreQuestions"));
                    candidatTransforme.put("pourcentage", resultat.get("pourcentage"));
                    candidatTransforme.put("statutTest2", resultat.get("statutActuel"));
                    candidatTransforme.put("dateTest", new Date()); // Date fictive pour le moment
                    candidatsTransformes.add(candidatTransforme);
                }
                
                candidatsParPoste.put(poste, candidatsTransformes);
            }
            
            model.addAttribute("statistiques", statistiques);
            model.addAttribute("candidatsParPoste", candidatsParPoste);
            
            // Récupérer toutes les offres uniques des candidats Test 2
            Set<Map<String, Object>> offresUniques = new HashSet<>();
            for (Candidat candidat : candidatsTest2) {
                Map<String, Object> offre = new HashMap<>();
                offre.put("id", candidat.getOffre().getIdOffre());
                offre.put("titre", "Offre pour " + candidat.getOffre().getPoste().getNom());
                offresUniques.add(offre);
            }
            model.addAttribute("offres", new ArrayList<>(offresUniques));
            model.addAttribute("nombreCandidats", candidatsTest2.size());
            
            return "admin/candidats/classement-test2";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors du calcul du classement: " + e.getMessage());
            return "redirect:/admin/candidats";
        }
    }
    
    @PostMapping("/traiter-admissions")
    public String traiterAdmissions(@RequestParam String critere,
                                  @RequestParam(required = false) Double noteMinimum,
                                  @RequestParam(required = false) Integer nombreCandidats,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Accès refusé. Seuls les administrateurs peuvent effectuer cette action");
            return "redirect:/login";
        }
        
        try {
            System.out.println("=== TRAITEMENT ADMISSIONS ===");
            System.out.println("Critère: " + critere);
            System.out.println("Note minimum: " + noteMinimum);
            System.out.println("Nombre candidats: " + nombreCandidats);
            
            test2Service.traiterAdmissionsTest2(critere, noteMinimum, nombreCandidats);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Les admissions ont été traitées avec succès !");
            
        } catch (Exception e) {
            System.err.println("Erreur traitement admissions: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Erreur lors du traitement des admissions: " + e.getMessage());
        }
        
        return "redirect:/admin/candidats";
    }
    
    @PostMapping("/traiter-admissions-poste")
    public String traiterAdmissionsParPoste(@RequestParam String critere,
                                          @RequestParam Long posteId,
                                          @RequestParam(required = false) Double noteMinimum,
                                          @RequestParam(required = false) Integer nombreCandidats,
                                          HttpSession session,
                                          RedirectAttributes redirectAttributes) {
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Accès refusé. Seuls les administrateurs peuvent effectuer cette action");
            return "redirect:/login";
        }
        
        try {
            System.out.println("=== TRAITEMENT ADMISSIONS PAR POSTE ===");
            System.out.println("Poste ID: " + posteId);
            System.out.println("Critère: " + critere);
            System.out.println("Note minimum: " + noteMinimum);
            System.out.println("Nombre candidats: " + nombreCandidats);
            
            test2Service.traiterAdmissionsTest2ParPoste(posteId, critere, noteMinimum, nombreCandidats);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Les admissions ont été traitées avec succès pour ce poste !");
            
        } catch (Exception e) {
            System.err.println("Erreur traitement admissions par poste: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Erreur lors du traitement des admissions: " + e.getMessage());
        }
        
        return "redirect:/admin/candidats/classement-test2";
    }
    
    @PostMapping("/traiter-admissions-offre/{offreId}")
    public String traiterAdmissionsParOffre(@PathVariable Long offreId,
                                           @RequestParam String critere,
                                           @RequestParam(required = false) Double noteMinimum,
                                           @RequestParam(required = false) Integer nombreCandidats,
                                           HttpSession session,
                                           RedirectAttributes redirectAttributes) {
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Accès refusé. Seuls les administrateurs peuvent effectuer cette action");
            return "redirect:/login";
        }
        
        try {
            System.out.println("=== TRAITEMENT ADMISSIONS POUR OFFRE " + offreId + " ===");
            System.out.println("Critère: " + critere);
            System.out.println("Note minimum: " + noteMinimum);
            System.out.println("Nombre candidats: " + nombreCandidats);
            
            // Utiliser une nouvelle méthode du service qui traite seulement une offre
            int candidatsTraites = test2Service.traiterAdmissionsTest2ParOffre(offreId, critere, noteMinimum, nombreCandidats);
            
            if (candidatsTraites > 0) {
                redirectAttributes.addFlashAttribute("success", 
                    candidatsTraites + " candidat(s) traité(s) avec succès pour cette offre !");
            } else {
                redirectAttributes.addFlashAttribute("info", 
                    "Aucun candidat à traiter selon les critères spécifiés.");
            }
            
        } catch (Exception e) {
            System.err.println("Erreur traitement admissions offre: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", 
                "Erreur lors du traitement des admissions: " + e.getMessage());
        }
        
        return "redirect:/admin/candidats/classement/" + offreId;
    }
    
    @GetMapping("/detail-test2/{id}")
    public String detailTest2Candidat(@PathVariable Long id, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Accès refusé. Seuls les administrateurs peuvent accéder à cette page");
            return "redirect:/login";
        }
        
        try {
            Optional<Candidat> candidatOpt = candidatService.findById(id);
            if (!candidatOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Candidat introuvable");
                return "redirect:/admin/candidats";
            }
            
            Candidat candidat = candidatOpt.get();
            
            // Vérifier si le candidat a passé le Test 2
            boolean aPasseTest2 = candidat.getStatusCandidats().stream()
                .anyMatch(sc -> sc.getStatus().getNom().contains("Test 2"));
            
            if (!aPasseTest2) {
                redirectAttributes.addFlashAttribute("warning", "Ce candidat n'a pas encore passé le Test 2");
                return "redirect:/admin/candidats/detail/" + id;
            }
            
            // Obtenir les résultats détaillés
            Map<String, Object> resultats = test2Service.getResultatCompletTest2(candidat);
            
            // Récupérer les réponses détaillées pour affichage
            List<ReponseCandidat> reponsesCandidat = reponseCandidatRepository.findByCandidatId(candidat.getIdCandidat());
            
            model.addAttribute("candidat", candidat);
            model.addAttribute("resultats", resultats);
            model.addAttribute("reponsesCandidat", reponsesCandidat);
            model.addAttribute("seuilReussite", test2Service.getSeuilReussite());
            
            return "admin/candidats/detail-test2";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors du chargement des détails: " + e.getMessage());
            return "redirect:/admin/candidats";
        }
    }
    
    @GetMapping("/classement/{offreId}")
    public String afficherClassementTest2ParOffre(@PathVariable Long offreId, 
                                                Model model, 
                                                HttpSession session, 
                                                RedirectAttributes redirectAttributes) {
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Accès refusé. Seuls les administrateurs peuvent accéder à cette page");
            return "redirect:/login";
        }
        
        try {
            System.out.println("=== PAGE CLASSEMENT TEST 2 POUR OFFRE " + offreId + " ===");
            
            // Récupérer l'offre
            Offre offre = offreService.findById(offreId).orElse(null);
            if (offre == null) {
                redirectAttributes.addFlashAttribute("error", "Offre introuvable");
                return "redirect:/admin/candidats";
            }
            
            // Récupérer tous les candidats de cette offre ayant fait le Test 2
            List<String> statutsTest2 = Arrays.asList("Test 2 Terminé", "Pass Test 2", "Echec Test 2");
            List<Candidat> candidatsTest2 = new ArrayList<>();
            Map<Long, String> statutsActuels = new HashMap<>();
            
            for (String nomStatut : statutsTest2) {
                Status status = statusService.findByNom(nomStatut).orElse(null);
                if (status != null) {
                    List<StatusCandidat> statusCandidats = statusCandidatRepository.findByStatusIdStatus(status.getIdStatus());
                    for (StatusCandidat sc : statusCandidats) {
                        Candidat candidat = sc.getCandidat();
                        // Filtrer uniquement les candidats de cette offre
                        if (candidat.getOffre() != null && candidat.getOffre().getIdOffre().equals(offreId)) {
                            if (!candidatsTest2.contains(candidat)) {
                                candidatsTest2.add(candidat);
                            }
                            // Garder le statut le plus récent pour chaque candidat
                            String statutActuel = statutsActuels.get(candidat.getIdCandidat());
                            if (statutActuel == null) {
                                statutsActuels.put(candidat.getIdCandidat(), nomStatut);
                            } else {
                                // Prioriser Pass Test 2 et Echec Test 2 sur Test 2 Terminé
                                if ("Test 2 Terminé".equals(statutActuel) && 
                                    ("Pass Test 2".equals(nomStatut) || "Echec Test 2".equals(nomStatut))) {
                                    statutsActuels.put(candidat.getIdCandidat(), nomStatut);
                                }
                            }
                        }
                    }
                }
            }
            
            System.out.println("Nombre de candidats Test 2 pour l'offre " + offreId + ": " + candidatsTest2.size());
            
            // Créer la liste des résultats
            List<Map<String, Object>> resultats = new ArrayList<>();
            
            for (Candidat candidat : candidatsTest2) {
                Map<String, Object> resultatComplet = test2Service.getResultatCompletTest2(candidat);
                resultatComplet.put("candidat", candidat);
                resultatComplet.put("statutActuel", statutsActuels.get(candidat.getIdCandidat()));
                resultats.add(resultatComplet);
            }
            
            // Trier les candidats par note décroissante
            resultats.sort((r1, r2) -> {
                Double note1 = (Double) r1.get("noteSur20");
                Double note2 = (Double) r2.get("noteSur20");
                return Double.compare(note2, note1);
            });
            
            // Ajouter le rang
            for (int i = 0; i < resultats.size(); i++) {
                resultats.get(i).put("rang", i + 1);
            }
            
            // Calculer des statistiques pour cette offre
            List<Double> notes = resultats.stream()
                .map(r -> (Double) r.get("noteSur20"))
                .collect(Collectors.toList());
                
            if (!notes.isEmpty()) {
                double moyenneOffre = notes.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
                double noteMax = notes.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
                double noteMin = notes.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
                
                model.addAttribute("moyenneOffre", Math.round(moyenneOffre * 100.0) / 100.0);
                model.addAttribute("noteMax", noteMax);
                model.addAttribute("noteMin", noteMin);
            }
            
            // Calculer les statistiques pour le template
            long totalCandidats = candidatsTest2.size();
            long termine = statutsActuels.values().stream().mapToLong(s -> "Test 2 Terminé".equals(s) ? 1 : 0).sum();
            long admis = statutsActuels.values().stream().mapToLong(s -> "Pass Test 2".equals(s) ? 1 : 0).sum();
            long refuses = statutsActuels.values().stream().mapToLong(s -> "Echec Test 2".equals(s) ? 1 : 0).sum();
            
            Map<String, Long> statistiques = new HashMap<>();
            statistiques.put("totalCandidats", totalCandidats);
            statistiques.put("termine", termine);
            statistiques.put("admis", admis);
            statistiques.put("refuses", refuses);
            
            // Calculer les âges des candidats
            Map<Long, Integer> agesMap = candidatsTest2.stream()
                .filter(c -> c.getDateNaissance() != null)
                .collect(Collectors.toMap(
                    Candidat::getIdCandidat,
                    c -> Period.between(c.getDateNaissance(), LocalDate.now()).getYears()
                ));
            
            model.addAttribute("offre", offre);
            model.addAttribute("resultats", resultats);
            model.addAttribute("statistiques", statistiques);
            model.addAttribute("agesMap", agesMap);
            model.addAttribute("nombreCandidats", candidatsTest2.size());
            
            return "admin/candidats/classement-test2-offre";
            
        } catch (Exception e) {
            System.err.println("Erreur classement Test 2 par offre: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Erreur lors du calcul du classement: " + e.getMessage());
            return "redirect:/admin/candidats/by-offre/" + offreId;
        }
    }
}
