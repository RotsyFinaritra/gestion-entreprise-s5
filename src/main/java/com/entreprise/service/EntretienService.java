package com.entreprise.service;

import com.entreprise.model.Entretien;
import com.entreprise.model.Candidat;
import com.entreprise.model.Offre;
import com.entreprise.repository.EntretienRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class EntretienService {

    @Autowired
    private EntretienRepository entretienRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private CandidatService candidatService;

    @Autowired
    private OffreService offreService;

    public List<Entretien> findAll() {
        return entretienRepository.findAll();
    }

    public Optional<Entretien> findById(Long id) {
        return entretienRepository.findById(id);
    }

    public List<Entretien> findByOffreId(Long offreId) {
        return entretienRepository.findByOffreWithCandidatAndOffre(offreId);
    }

    public List<Entretien> findByCandidatId(Long candidatId) {
        return entretienRepository.findByCandidatIdCandidat(candidatId);
    }

    public Entretien save(Entretien entretien) {
        return entretienRepository.save(entretien);
    }

    public void deleteById(Long id) {
        entretienRepository.deleteById(id);
    }

    /**
     * Planifier automatiquement les entretiens pour une offre
     */
    public List<Entretien> planifierEntretiens(Long offreId, 
                                             Integer dureeEntretien,
                                             LocalDate dateDebut,
                                             List<String> heuresJour,
                                             Integer intervalleMinutes,
                                             List<LocalDate> joursFeries) {
        
        System.out.println("=== PLANIFICATION ENTRETIENS ===");
        System.out.println("Offre ID: " + offreId);
        System.out.println("Durée entretien: " + dureeEntretien + " minutes");
        System.out.println("Date début: " + dateDebut);
        System.out.println("Heures jour: " + heuresJour);
        System.out.println("Intervalle: " + intervalleMinutes + " minutes");
        System.out.println("Jours fériés: " + joursFeries);

        // Récupérer l'offre
        Offre offre = offreService.findById(offreId)
            .orElseThrow(() -> new RuntimeException("Offre introuvable"));

        // Récupérer tous les candidats admis (Pass Test 2) pour cette offre
        List<Candidat> candidatsAdmis = candidatService.findByOffreId(offreId).stream()
            .filter(c -> c.getStatusCandidats().stream()
                .anyMatch(sc -> "Pass Test 2".equals(sc.getStatus().getNom())))
            .collect(Collectors.toList());

        System.out.println("Candidats admis trouvés: " + candidatsAdmis.size());

        if (candidatsAdmis.isEmpty()) {
            throw new RuntimeException("Aucun candidat admis trouvé pour cette offre");
        }

        // Trier les candidats par meilleur score Test 2 (priorité)
        candidatsAdmis.sort((c1, c2) -> {
            // Ici on pourrait récupérer les scores, pour l'instant on trie par ID
            return c1.getIdCandidat().compareTo(c2.getIdCandidat());
        });

        // Parser les heures de travail
        List<CreneauHoraire> creneaux = parseHeuresJour(heuresJour);
        
        // Planifier les entretiens
        List<Entretien> entretiensPlannifies = new ArrayList<>();
        LocalDate dateActuelle = dateDebut;
        int candidatIndex = 0;
        
        while (candidatIndex < candidatsAdmis.size()) {
            // Ignorer les weekends et jours fériés
            if (dateActuelle.getDayOfWeek().getValue() >= 6 || 
                (joursFeries != null && joursFeries.contains(dateActuelle))) {
                dateActuelle = dateActuelle.plusDays(1);
                continue;
            }

            // Planifier pour cette date
            for (CreneauHoraire creneau : creneaux) {
                LocalTime heureActuelle = creneau.heureDebut;
                
                while (heureActuelle.isBefore(creneau.heureFin) && candidatIndex < candidatsAdmis.size()) {
                    LocalDateTime dateHeureEntretien = LocalDateTime.of(dateActuelle, heureActuelle);
                    
                    // Vérifier les conflits
                    LocalDateTime finEntretien = dateHeureEntretien.plusMinutes(dureeEntretien);
                    if (!aConflitHoraire(dateHeureEntretien, finEntretien)) {
                        
                        Candidat candidat = candidatsAdmis.get(candidatIndex);
                        
                        // Créer l'entretien
                        Entretien entretien = new Entretien();
                        entretien.setCandidat(candidat);
                        entretien.setOffre(offre);
                        entretien.setDateEnvoiMail(LocalDate.now());
                        entretien.setDateHeureEntretien(dateHeureEntretien);
                        entretien.setDureeEntretien(dureeEntretien);
                        entretien.setStatut("programmé");
                        entretien.setLieuEntretien("À définir");
                        
                        entretien = save(entretien);
                        entretiensPlannifies.add(entretien);
                        
                        candidatIndex++;
                        System.out.println("Entretien planifié pour " + candidat.getPrenom() + " " + 
                                         candidat.getNom() + " le " + dateHeureEntretien);
                    }
                    
                    // Passer au créneau suivant
                    heureActuelle = heureActuelle.plusMinutes(dureeEntretien + intervalleMinutes);
                }
            }
            
            dateActuelle = dateActuelle.plusDays(1);
        }

        System.out.println("Total entretiens planifiés: " + entretiensPlannifies.size());
        return entretiensPlannifies;
    }

    /**
     * Envoyer les emails d'invitation aux entretiens
     */
    public void envoyerInvitationsEntretiens(List<Entretien> entretiens) {
        System.out.println("=== ENVOI INVITATIONS ENTRETIENS ===");
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm");
        
        for (Entretien entretien : entretiens) {
            try {
                Candidat candidat = entretien.getCandidat();
                String dateFormatee = entretien.getDateHeureEntretien().format(formatter);
                
                String sujet = "Invitation à l'entretien - " + entretien.getOffre().getPoste().getNom();
                
                String contenu = String.format(
                    "Bonjour %s %s,\n\n" +
                    "Félicitations ! Suite à votre excellent résultat au Test 2, nous avons le plaisir de vous inviter à un entretien.\n\n" +
                    "Détails de l'entretien :\n" +
                    "📅 Date et heure : %s\n" +
                    "⏱️ Durée prévue : %d minutes\n" +
                    "📍 Lieu : %s\n" +
                    "💼 Poste : %s\n\n" +
                    "Merci de confirmer votre présence en répondant à cet email.\n\n" +
                    "En cas d'empêchement, contactez-nous au plus vite pour reprogrammer.\n\n" +
                    "Cordialement,\n" +
                    "L'équipe de recrutement",
                    candidat.getPrenom(), candidat.getNom(),
                    dateFormatee,
                    entretien.getDureeEntretien(),
                    entretien.getLieuEntretien() != null ? entretien.getLieuEntretien() : "À confirmer",
                    entretien.getOffre().getPoste().getNom()
                );

                emailService.envoyerEmail(candidat.getEmail(), sujet, contenu);
                
                System.out.println("Email envoyé à " + candidat.getEmail() + " pour entretien le " + dateFormatee);
                
            } catch (Exception e) {
                System.err.println("Erreur envoi email pour entretien " + entretien.getIdEntretien() + ": " + e.getMessage());
            }
        }
    }

    // Classes internes pour la gestion des créneaux
    private static class CreneauHoraire {
        LocalTime heureDebut;
        LocalTime heureFin;
        
        CreneauHoraire(LocalTime debut, LocalTime fin) {
            this.heureDebut = debut;
            this.heureFin = fin;
        }
    }

    private List<CreneauHoraire> parseHeuresJour(List<String> heuresJour) {
        List<CreneauHoraire> creneaux = new ArrayList<>();
        
        for (String creneau : heuresJour) {
            // Format attendu: "08:00-12:00" ou "8h-12h" ou "8h00-12h00"
            String[] parties = creneau.toLowerCase().split("-");
            if (parties.length == 2) {
                try {
                    LocalTime debut = parseHeure(parties[0].trim());
                    LocalTime fin = parseHeure(parties[1].trim());
                    creneaux.add(new CreneauHoraire(debut, fin));
                } catch (Exception e) {
                    System.err.println("Erreur parsing créneau: " + creneau);
                }
            }
        }
        
        return creneaux;
    }

    private LocalTime parseHeure(String heure) {
        // Nettoyer la chaîne
        heure = heure.replaceAll("h", ":").replaceAll("[^0-9:]", "");
        
        if (!heure.contains(":")) {
            heure += ":00";
        }
        
        String[] parties = heure.split(":");
        int heures = Integer.parseInt(parties[0]);
        int minutes = parties.length > 1 ? Integer.parseInt(parties[1]) : 0;
        
        return LocalTime.of(heures, minutes);
    }

    private boolean aConflitHoraire(LocalDateTime debut, LocalDateTime fin) {
        List<Entretien> conflits = entretienRepository.findEntretiensEnConflit(debut, fin);
        return !conflits.isEmpty();
    }

    /**
     * Obtenir les statistiques des entretiens pour une offre
     */
    public Map<String, Object> getStatistiquesEntretiens(Long offreId) {
        Map<String, Object> stats = new HashMap<>();
        
        List<Entretien> entretiens = findByOffreId(offreId);
        
        stats.put("total", entretiens.size());
        stats.put("programmes", entretiens.stream().filter(e -> "programmé".equals(e.getStatut())).count());
        stats.put("confirmes", entretiens.stream().filter(e -> "confirmé".equals(e.getStatut())).count());
        stats.put("termines", entretiens.stream().filter(e -> "terminé".equals(e.getStatut())).count());
        stats.put("annules", entretiens.stream().filter(e -> "annulé".equals(e.getStatut())).count());
        
        return stats;
    }

    /**
     * Annuler un entretien
     */
    public void annulerEntretien(Long entretienId, String raison) {
        Entretien entretien = findById(entretienId)
            .orElseThrow(() -> new RuntimeException("Entretien introuvable"));
        
        entretien.setStatut("annulé");
        entretien.setCommentaire(raison);
        save(entretien);
        
        // Envoyer email d'annulation
        try {
            String sujet = "Annulation entretien - " + entretien.getOffre().getPoste().getNom();
            String contenu = String.format(
                "Bonjour %s %s,\n\n" +
                "Nous vous informons que l'entretien prévu le %s a été annulé.\n\n" +
                "Raison : %s\n\n" +
                "Nous vous recontacterons prochainement pour reprogrammer.\n\n" +
                "Cordialement,\n" +
                "L'équipe de recrutement",
                entretien.getCandidat().getPrenom(),
                entretien.getCandidat().getNom(),
                entretien.getDateHeureEntretien().format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm")),
                raison != null ? raison : "Raisons organisationnelles"
            );
            
            emailService.envoyerEmail(entretien.getCandidat().getEmail(), sujet, contenu);
        } catch (Exception e) {
            System.err.println("Erreur envoi email annulation: " + e.getMessage());
        }
    }
}
