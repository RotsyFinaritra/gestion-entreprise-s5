package com.entreprise.service;

import com.entreprise.model.*;
import com.entreprise.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.annotation.PostConstruct;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class Test2Service {

    @Value("${test2.questions-par-test:4}")
    private int questionsParTest;
    
    @Value("${test2.seuil-reussite:60}")
    private int seuilReussite;

    @Autowired
    private CandidatService candidatService;
    
    @Autowired
    private QuestionRepository questionRepository;
    
    @Autowired
    private ReponseQuestionRepository reponseQuestionRepository;
    
    @Autowired
    private ReponseCandidatRepository reponseCandidatRepository;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private StatusService statusService;
    
    @Autowired
    private StatusCandidatService statusCandidatService;
    
    @Autowired
    private StatusRepository statusRepository;
    
    @Autowired
    private StatusCandidatRepository statusCandidatRepository;

    public int envoyerInvitationsTest2(Long offreId) {
        System.out.println("=== ENVOI INVITATIONS TEST 2 ===");
        System.out.println("Offre ID: " + offreId);
        
        // Récupérer tous les candidats qui ont passé le test 1 pour cette offre
        List<Candidat> candidatsPasseTest1 = candidatService.findByOffreId(offreId).stream()
            .filter(c -> c.getStatusCandidats().stream()
                .anyMatch(sc -> sc.getStatus().getNom().equals("Pass Test 1")))
            .collect(Collectors.toList());
        
        System.out.println("Candidats trouvés qui ont passé Test 1: " + candidatsPasseTest1.size());
        
        int invitationsEnvoyees = 0;
        
        // Envoyer un email à chaque candidat (seulement s'il n'a pas déjà été invité)
        for (Candidat candidat : candidatsPasseTest1) {
            // Vérifier si le candidat a déjà reçu une invitation Test 2
            boolean dejaInvite = candidat.getStatusCandidats().stream()
                .anyMatch(sc -> sc.getStatus().getNom().equals("Invité Test 2") ||
                               sc.getStatus().getNom().equals("Test 2 Terminé") ||
                               sc.getStatus().getNom().equals("Pass Test 2") ||
                               sc.getStatus().getNom().equals("Echec Test 2"));
            
            if (dejaInvite) {
                System.out.println("Candidat " + candidat.getPrenom() + " " + candidat.getNom() + " a déjà reçu une invitation Test 2 - IGNORÉ");
                continue;
            }
            
            System.out.println("Envoi email à: " + candidat.getPrenom() + " " + candidat.getNom());
            emailService.envoyerLienTest2(candidat);
            invitationsEnvoyees++;
            
            // Créer le statut "Invité Test 2"
            Status statusInviteTest2 = statusService.findByNom("Invité Test 2")
                .orElseGet(() -> {
                    Status newStatus = new Status("Invité Test 2");
                    return statusService.save(newStatus);
                });
            
            // Utiliser la date actuelle avec une seconde de plus pour s'assurer que ce soit le plus récent
            LocalDate dateModification = LocalDate.now();
            StatusCandidat statusCandidat = new StatusCandidat(statusInviteTest2, candidat, dateModification);
            
            System.out.println("=== CRÉATION STATUT 'Invité Test 2' ===");
            System.out.println("Candidat: " + candidat.getPrenom() + " " + candidat.getNom() + " (ID: " + candidat.getIdCandidat() + ")");
            System.out.println("Date: " + dateModification);
            statusCandidatService.save(statusCandidat);
            System.out.println("Statut sauvegardé avec succès");
            
            // Vérifier le nouveau statut actuel
            String nouveauStatut = getStatutActuelCandidat(candidat);
            System.out.println("Nouveau statut actuel: '" + nouveauStatut + "'");
            System.out.println("========================================");
        }
        
        System.out.println("Invitations Test 2 terminées - " + invitationsEnvoyees + " nouvelles invitations envoyées sur " + candidatsPasseTest1.size() + " candidat(s) éligibles");
        System.out.println("================================");
        
        return invitationsEnvoyees;
    }
    
    public List<Question> getQuestionsForCandidat(Long candidatId) {
        Candidat candidat = candidatService.findById(candidatId).orElse(null);
        if (candidat == null || candidat.getOffre() == null || candidat.getOffre().getPoste() == null) {
            return List.of();
        }
        
        // Récupérer les compétences requises pour le poste
        List<Competance> competencesRequises = candidat.getOffre().getPoste().getPosteCompetances().stream()
            .map(pc -> pc.getCompetance())
            .collect(Collectors.toList());
        
        // Récupérer toutes les questions pour ces compétences
        List<Question> toutesLesQuestions = questionRepository.findByCompetanceIn(competencesRequises);
        
        System.out.println("=== SÉLECTION QUESTIONS ALÉATOIRES ===");
        System.out.println("Total questions disponibles: " + toutesLesQuestions.size());
        System.out.println("Questions à sélectionner: " + questionsParTest);
        
        // Sélectionner aléatoirement un nombre limité de questions
        if (toutesLesQuestions.size() <= questionsParTest) {
            System.out.println("Toutes les questions sélectionnées (pas assez de questions disponibles)");
            System.out.println("=====================================");
            return toutesLesQuestions;
        }
        
        // Mélanger la liste et prendre les N premières
        List<Question> questionsAleatoires = new ArrayList<>(toutesLesQuestions);
        Collections.shuffle(questionsAleatoires);
        List<Question> questionsSelectionnees = questionsAleatoires.subList(0, questionsParTest);
        
        System.out.println("Questions sélectionnées aléatoirement:");
        for (Question q : questionsSelectionnees) {
            System.out.println("  - ID: " + q.getIdQuestion() + " | Énoncé: " + q.getEnonce());
        }
        System.out.println("=====================================");
        
        return questionsSelectionnees;
    }
    
    public Map<Long, List<ReponseQuestion>> getReponsesForQuestions(List<Question> questions) {
        return questions.stream()
            .collect(Collectors.toMap(
                Question::getIdQuestion,
                question -> reponseQuestionRepository.findByQuestion(question)
            ));
    }
    
    public void sauvegarderReponses(Long candidatId, Map<Long, Long> reponsesCandidatMap, String token) {
        System.out.println("=== SAUVEGARDE RÉPONSES CANDIDAT ===");
        System.out.println("Candidat ID: " + candidatId);
        System.out.println("Token: " + token);
        System.out.println("Nombre de réponses: " + reponsesCandidatMap.size());
        
        // Vérifier le token avec plus de debugging
        boolean tokenValide = emailService.isTokenValid(token, candidatId);
        System.out.println("Token valide: " + tokenValide);
        
        if (!tokenValide) {
            // Debug du token
            try {
                String[] parts = token.split("_");
                System.out.println("Token parts: " + java.util.Arrays.toString(parts));
                if (parts.length >= 1) {
                    Long tokenCandidatId = Long.parseLong(parts[0]);
                    System.out.println("Token candidat ID: " + tokenCandidatId + " vs fourni: " + candidatId);
                }
                if (parts.length >= 2) {
                    long timestamp = Long.parseLong(parts[1]);
                    long currentTime = System.currentTimeMillis();
                    long ageHours = (currentTime - timestamp) / (60 * 60 * 1000);
                    System.out.println("Token âge: " + ageHours + " heures");
                }
            } catch (Exception e) {
                System.err.println("Erreur parsing token: " + e.getMessage());
            }
            throw new IllegalArgumentException("Token invalide ou expiré");
        }
        
        Candidat candidat = candidatService.findById(candidatId).orElse(null);
        if (candidat == null) {
            throw new IllegalArgumentException("Candidat non trouvé");
        }
        
        // Sauvegarder chaque réponse
        int reponsesEnregistrees = 0;
        for (Map.Entry<Long, Long> entry : reponsesCandidatMap.entrySet()) {
            Long questionId = entry.getKey();
            Long reponseQuestionId = entry.getValue();
            
            System.out.println("Traitement - Question ID: " + questionId + ", Réponse ID: " + reponseQuestionId);
            
            Question question = questionRepository.findById(questionId).orElse(null);
            ReponseQuestion reponseQuestion = reponseQuestionRepository.findById(reponseQuestionId).orElse(null);
            
            if (question != null && reponseQuestion != null) {
                // Vérifier si une réponse existe déjà pour cette question et ce candidat
                ReponseCandidat existante = reponseCandidatRepository.findByQuestionIdAndCandidatId(questionId, candidatId);
                if (existante != null) {
                    // Mettre à jour la réponse existante
                    existante.setReponseQuestion(reponseQuestion);
                    reponseCandidatRepository.save(existante);
                    System.out.println("  -> Réponse mise à jour pour question " + questionId);
                } else {
                    // Créer une nouvelle réponse
                    ReponseCandidat reponseCandidat = new ReponseCandidat(question, reponseQuestion, candidat);
                    reponseCandidatRepository.save(reponseCandidat);
                    System.out.println("  -> Nouvelle réponse enregistrée pour question " + questionId);
                }
                reponsesEnregistrees++;
            } else {
                System.err.println("  -> Erreur: Question ou ReponseQuestion introuvable (Question ID: " + questionId + ", Réponse ID: " + reponseQuestionId + ")");
            }
        }
        
        System.out.println("Total réponses enregistrées: " + reponsesEnregistrees);
        System.out.println("=====================================");
        
        // Calculer le score et assigner le statut "Test 2 Terminé"
        calculerResultatTest2(candidat);
        assignerStatutTest2Termine(candidat);
    }
    
    private void assignerStatutTest2Termine(Candidat candidat) {
        try {
            // Assigner le statut "Test 2 Terminé"
            Status statusTest2Termine = statusRepository.findByNom("Test 2 Terminé")
                .orElseThrow(() -> new RuntimeException("Statut 'Test 2 Terminé' non trouvé"));
            
            StatusCandidat statusCandidat = new StatusCandidat();
            statusCandidat.setCandidat(candidat);
            statusCandidat.setStatus(statusTest2Termine);
            statusCandidat.setDateModification(LocalDate.now());
            
            statusCandidatRepository.save(statusCandidat);
            System.out.println("Statut 'Test 2 Terminé' assigné au candidat " + candidat.getIdCandidat());
        } catch (Exception e) {
            System.err.println("Erreur lors de l'assignation du statut 'Test 2 Terminé': " + e.getMessage());
        }
    }
    
    public double calculerResultatTest2(Candidat candidat) {
        System.out.println("=== CALCUL RÉSULTAT TEST 2 ===");
        System.out.println("Candidat: " + candidat.getPrenom() + " " + candidat.getNom() + " (ID: " + candidat.getIdCandidat() + ")");
        
        // Récupérer toutes les réponses du candidat pour ce test
        List<ReponseCandidat> reponsesCandidat = reponseCandidatRepository.findByCandidatId(candidat.getIdCandidat());
        if (reponsesCandidat.isEmpty()) {
            System.out.println("Aucune réponse trouvée pour ce candidat");
            return 0.0;
        }
        
        int totalPoints = 0;
        int pointsObtenus = 0;
        
        System.out.println("Évaluation des réponses:");
        for (ReponseCandidat rc : reponsesCandidat) {
            Question question = rc.getQuestion();
            ReponseQuestion reponseQuestion = rc.getReponseQuestion();
            
            totalPoints += question.getNote();
            
            if (reponseQuestion.getValeur() != null && reponseQuestion.getValeur()) {
                pointsObtenus += question.getNote();
                System.out.println("  ✓ Question: \"" + question.getEnonce() + "\" - BONNE réponse (+" + question.getNote() + " points)");
            } else {
                System.out.println("  ✗ Question: \"" + question.getEnonce() + "\" - MAUVAISE réponse (0 points)");
            }
        }
        
        // Calculer le pourcentage
        double pourcentage = totalPoints > 0 ? ((double) pointsObtenus / totalPoints) * 100 : 0;
        
        System.out.println("Score final: " + pointsObtenus + "/" + totalPoints + " points (" + Math.round(pourcentage) + "%)");
        System.out.println("Note calculée, en attente de décision administrative");
        System.out.println("==============================");
        
        return pourcentage;
    }
    
    public double calculerNoteSur20(Candidat candidat) {
        double scorePercentage = calculerResultatTest2(candidat);
        return (scorePercentage * 20) / 100.0;
    }
    
    public Map<String, Object> getResultatCompletTest2(Candidat candidat) {
        // Récupérer toutes les réponses du candidat pour ce test
        List<ReponseCandidat> reponsesCandidat = reponseCandidatRepository.findByCandidatId(candidat.getIdCandidat());
        
        int totalPoints = 0;
        int pointsObtenus = 0;
        int nombreQuestions = reponsesCandidat.size();
        int bonnesReponses = 0;
        
        for (ReponseCandidat rc : reponsesCandidat) {
            Question question = rc.getQuestion();
            ReponseQuestion reponseQuestion = rc.getReponseQuestion();
            
            totalPoints += question.getNote();
            
            if (reponseQuestion.getValeur() != null && reponseQuestion.getValeur()) {
                pointsObtenus += question.getNote();
                bonnesReponses++;
            }
        }
        
        double pourcentage = totalPoints > 0 ? ((double) pointsObtenus / totalPoints) * 100 : 0;
        double noteSur20 = (pourcentage * 20) / 100.0;
        
        Map<String, Object> resultat = new HashMap<>();
        resultat.put("pourcentage", pourcentage);
        resultat.put("noteSur20", Math.round(noteSur20 * 100.0) / 100.0);
        resultat.put("pointsObtenus", pointsObtenus);
        resultat.put("totalPoints", totalPoints);
        resultat.put("nombreQuestions", nombreQuestions);
        resultat.put("bonnesReponses", bonnesReponses);
        resultat.put("mauvaisesReponses", nombreQuestions - bonnesReponses);
        resultat.put("reussi", pourcentage >= seuilReussite);
        
        return resultat;
    }
    
    public boolean candidatADejaPasseTest2(Long candidatId) {
        return candidatService.findById(candidatId)
            .map(candidat -> candidat.getStatusCandidats().stream()
                .anyMatch(sc -> sc.getStatus().getNom().equals("Pass Test 2") || 
                               sc.getStatus().getNom().equals("Refus Test 2")))
            .orElse(false);
    }
    
    public String getStatutActuelCandidat(Candidat candidat) {
        if (candidat.getStatusCandidats() == null || candidat.getStatusCandidats().isEmpty()) {
            return "En attente";
        }
        
        System.out.println("=== RECHERCHE STATUT ACTUEL ===");
        System.out.println("Candidat: " + candidat.getPrenom() + " " + candidat.getNom() + " (ID: " + candidat.getIdCandidat() + ")");
        System.out.println("Nombre de statuts: " + candidat.getStatusCandidats().size());
        
        // Afficher tous les statuts pour debug
        candidat.getStatusCandidats().stream()
            .sorted((sc1, sc2) -> {
                int dateComp = sc2.getDateModification().compareTo(sc1.getDateModification());
                if (dateComp == 0) {
                    // Si même date, trier par ID décroissant (le plus récent d'abord)
                    return sc2.getIdStatusCandidat().compareTo(sc1.getIdStatusCandidat());
                }
                return dateComp;
            })
            .forEach(sc -> {
                System.out.println("  - Statut: '" + sc.getStatus().getNom() + "' | Date: " + sc.getDateModification() + " | ID: " + sc.getIdStatusCandidat());
            });
        
        // Récupérer le statut le plus récent
        String statutActuel = candidat.getStatusCandidats().stream()
            .max((sc1, sc2) -> {
                int dateComp = sc1.getDateModification().compareTo(sc2.getDateModification());
                if (dateComp == 0) {
                    // Si même date, prendre celui avec l'ID le plus élevé (plus récent)
                    return sc1.getIdStatusCandidat().compareTo(sc2.getIdStatusCandidat());
                }
                return dateComp;
            })
            .map(sc -> sc.getStatus().getNom())
            .orElse("En attente");
            
        System.out.println("Statut retenu: '" + statutActuel + "'");
        System.out.println("===============================");
        
        return statutActuel;
    }
    
    public Long validateToken(String token) {
        try {
            String[] parts = token.split("_");
            if (parts.length != 3) {
                System.out.println("Token invalide - format incorrect: " + token);
                return null;
            }
            
            Long candidatId = Long.parseLong(parts[0]);
            long timestamp = Long.parseLong(parts[1]);
            int hash = Integer.parseInt(parts[2]);
            
            // Recalculer le hash pour vérifier
            int expectedHash = Objects.hash(candidatId, timestamp, "TEST2_SECRET");
            if (hash != expectedHash) {
                System.out.println("Token invalide - hash incorrect");
                return null;
            }
            
            System.out.println("Token validé avec succès pour candidat ID: " + candidatId);
            return candidatId;
        } catch (Exception e) {
            System.out.println("Erreur validation token: " + e.getMessage());
            return null;
        }
    }
    
    public void sauvegarderReponsesProgression(Long candidatId, Map<String, String> reponses) {
        System.out.println("=== SAUVEGARDE PROGRESSION CANDIDAT ===");
        System.out.println("Candidat ID: " + candidatId);
        System.out.println("Nombre de réponses: " + reponses.size());
        
        try {
            // Convertir les réponses du format String vers le format attendu
            Map<Long, Long> reponsesConverties = new HashMap<>();
            
            for (Map.Entry<String, String> entry : reponses.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                
                // Extraire l'ID de la question (format: "question_X")
                if (key.startsWith("question_") && !value.isEmpty()) {
                    try {
                        Long questionId = Long.parseLong(key.substring(9));
                        Long reponseId = Long.parseLong(value);
                        reponsesConverties.put(questionId, reponseId);
                        System.out.println("Question " + questionId + " -> Réponse " + reponseId);
                    } catch (NumberFormatException e) {
                        System.err.println("Erreur conversion: " + key + " = " + value);
                    }
                }
            }
            
            // Sauvegarder avec la méthode existante
            if (!reponsesConverties.isEmpty()) {
                sauvegarderReponses(candidatId, reponsesConverties, "PROGRESSION");
                System.out.println("Progression sauvegardée avec succès");
            }
            
        } catch (Exception e) {
            System.err.println("Erreur sauvegarde progression: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("======================================");
    }

    public int getSeuilReussite() {
        return seuilReussite;
    }
    
    public void traiterAdmissionsTest2(String critere, Double noteMinimum, Integer nombreCandidats) {
        System.out.println("=== TRAITEMENT ADMISSIONS TEST 2 ===");
        System.out.println("Critère: " + critere);
        System.out.println("Note minimum: " + noteMinimum);
        System.out.println("Nombre candidats: " + nombreCandidats);
        
        // Récupérer tous les candidats ayant terminé le Test 2
        Optional<Status> statusOpt = statusRepository.findByNom("Test 2 Terminé");
        if (!statusOpt.isPresent()) {
            System.out.println("Statut 'Test 2 Terminé' non trouvé");
            return;
        }
        
        Status statusTest2Termine = statusOpt.get();
        List<StatusCandidat> statusCandidats = statusCandidatRepository.findByStatusIdStatus(statusTest2Termine.getIdStatus());
        List<Candidat> candidatsTest2Termine = new ArrayList<>();
        for (StatusCandidat sc : statusCandidats) {
            candidatsTest2Termine.add(sc.getCandidat());
        }
        System.out.println("Nombre de candidats ayant terminé le Test 2: " + candidatsTest2Termine.size());
        
        if (candidatsTest2Termine.isEmpty()) {
            System.out.println("Aucun candidat à traiter");
            return;
        }
        
        // Calculer les scores et créer la liste avec résultats
        List<ResultatTest2> resultats = new ArrayList<>();
        for (Candidat candidat : candidatsTest2Termine) {
            Map<String, Object> resultatComplet = getResultatCompletTest2(candidat);
            double noteSur20 = (Double) resultatComplet.get("noteSur20");
            
            ResultatTest2 resultat = new ResultatTest2();
            resultat.setCandidat(candidat);
            resultat.setNoteSur20(noteSur20);
            resultat.setPourcentage((Double) resultatComplet.get("pourcentage"));
            resultat.setNombreBonnesReponses((Integer) resultatComplet.get("bonnesReponses"));
            resultat.setNombreTotalQuestions((Integer) resultatComplet.get("nombreQuestions"));
            
            resultats.add(resultat);
        }
        
        // Trier par note décroissante
        resultats.sort((r1, r2) -> Double.compare(r2.getNoteSur20(), r1.getNoteSur20()));
        
        // Déterminer qui est admis selon le critère
        Set<Long> candidatsAdmis = new HashSet<>();
        
        if ("moyenne".equals(critere) && noteMinimum != null) {
            // Critère: note minimum
            for (ResultatTest2 resultat : resultats) {
                if (resultat.getNoteSur20() >= noteMinimum) {
                    candidatsAdmis.add(resultat.getCandidat().getIdCandidat());
                }
            }
            System.out.println("Candidats admis avec note >= " + noteMinimum + ": " + candidatsAdmis.size());
        } else if ("top".equals(critere) && nombreCandidats != null) {
            // Critère: top N candidats
            int limite = Math.min(nombreCandidats, resultats.size());
            for (int i = 0; i < limite; i++) {
                candidatsAdmis.add(resultats.get(i).getCandidat().getIdCandidat());
            }
            System.out.println("Top " + limite + " candidats admis");
        } else if ("combiné".equals(critere) && noteMinimum != null && nombreCandidats != null) {
            // Critère combiné: note minimum ET parmi les top N
            int limite = Math.min(nombreCandidats, resultats.size());
            for (int i = 0; i < limite; i++) {
                ResultatTest2 resultat = resultats.get(i);
                if (resultat.getNoteSur20() >= noteMinimum) {
                    candidatsAdmis.add(resultat.getCandidat().getIdCandidat());
                }
            }
            System.out.println("Candidats admis (top " + nombreCandidats + " avec note >= " + noteMinimum + "): " + candidatsAdmis.size());
        }
        
        // Assigner les statuts finaux
        for (ResultatTest2 resultat : resultats) {
            Candidat candidat = resultat.getCandidat();
            boolean estAdmis = candidatsAdmis.contains(candidat.getIdCandidat());
            String nomStatut = estAdmis ? "Pass Test 2" : "Echec Test 2";
            
            try {
                Status status = statusRepository.findByNom(nomStatut)
                    .orElseThrow(() -> new RuntimeException("Statut '" + nomStatut + "' non trouvé"));
                
                StatusCandidat statusCandidat = new StatusCandidat();
                statusCandidat.setCandidat(candidat);
                statusCandidat.setStatus(status);
                statusCandidat.setDateModification(LocalDate.now());
                
                statusCandidatRepository.save(statusCandidat);
                System.out.println("Candidat " + candidat.getIdCandidat() + " (" + candidat.getPrenom() + " " + candidat.getNom() + "): " + nomStatut + " (Note: " + resultat.getNoteSur20() + "/20)");
            } catch (Exception e) {
                System.err.println("Erreur lors de l'assignation du statut pour le candidat " + candidat.getIdCandidat() + ": " + e.getMessage());
            }
        }
        
        System.out.println("==========================================");
    }
    
    public void traiterAdmissionsTest2ParPoste(Long posteId, String critere, Double noteMinimum, Integer nombreCandidats) {
        System.out.println("=== TRAITEMENT ADMISSIONS TEST 2 PAR POSTE ===");
        System.out.println("Poste ID: " + posteId);
        System.out.println("Critère: " + critere);
        System.out.println("Note minimum: " + noteMinimum);
        System.out.println("Nombre candidats: " + nombreCandidats);
        
        // Récupérer tous les candidats ayant terminé le Test 2 pour ce poste spécifique
        Optional<Status> statusOpt = statusRepository.findByNom("Test 2 Terminé");
        if (!statusOpt.isPresent()) {
            System.out.println("Statut 'Test 2 Terminé' non trouvé");
            return;
        }
        
        Status statusTest2Termine = statusOpt.get();
        List<StatusCandidat> statusCandidats = statusCandidatRepository.findByStatusIdStatus(statusTest2Termine.getIdStatus());
        
        // Filtrer les candidats pour ce poste spécifique
        List<Candidat> candidatsTest2TerminePoste = new ArrayList<>();
        for (StatusCandidat sc : statusCandidats) {
            Candidat candidat = sc.getCandidat();
            if (candidat.getOffre() != null && 
                candidat.getOffre().getPoste() != null && 
                candidat.getOffre().getPoste().getIdPoste().equals(posteId)) {
                candidatsTest2TerminePoste.add(candidat);
            }
        }
        System.out.println("Nombre de candidats ayant terminé le Test 2 pour ce poste: " + candidatsTest2TerminePoste.size());
        
        if (candidatsTest2TerminePoste.isEmpty()) {
            System.out.println("Aucun candidat à traiter pour ce poste");
            return;
        }
        
        // Calculer les scores et créer la liste avec résultats
        List<ResultatTest2> resultats = new ArrayList<>();
        for (Candidat candidat : candidatsTest2TerminePoste) {
            Map<String, Object> resultatComplet = getResultatCompletTest2(candidat);
            double noteSur20 = (Double) resultatComplet.get("noteSur20");
            
            ResultatTest2 resultat = new ResultatTest2();
            resultat.setCandidat(candidat);
            resultat.setNoteSur20(noteSur20);
            resultat.setPourcentage((Double) resultatComplet.get("pourcentage"));
            resultat.setNombreBonnesReponses((Integer) resultatComplet.get("bonnesReponses"));
            resultat.setNombreTotalQuestions((Integer) resultatComplet.get("nombreQuestions"));
            
            resultats.add(resultat);
        }
        
        // Trier par note décroissante
        resultats.sort((r1, r2) -> Double.compare(r2.getNoteSur20(), r1.getNoteSur20()));
        
        // Déterminer qui est admis selon le critère
        Set<Long> candidatsAdmis = new HashSet<>();
        
        if ("moyenne".equals(critere) && noteMinimum != null) {
            // Critère: note minimum
            for (ResultatTest2 resultat : resultats) {
                if (resultat.getNoteSur20() >= noteMinimum) {
                    candidatsAdmis.add(resultat.getCandidat().getIdCandidat());
                }
            }
            System.out.println("Candidats admis avec note >= " + noteMinimum + " pour ce poste: " + candidatsAdmis.size());
        } else if ("top".equals(critere) && nombreCandidats != null) {
            // Critère: top N candidats
            int limite = Math.min(nombreCandidats, resultats.size());
            for (int i = 0; i < limite; i++) {
                candidatsAdmis.add(resultats.get(i).getCandidat().getIdCandidat());
            }
            System.out.println("Top " + limite + " candidats admis pour ce poste");
        } else if ("combiné".equals(critere) && noteMinimum != null && nombreCandidats != null) {
            // Critère combiné: note minimum ET parmi les top N
            int limite = Math.min(nombreCandidats, resultats.size());
            for (int i = 0; i < limite; i++) {
                ResultatTest2 resultat = resultats.get(i);
                if (resultat.getNoteSur20() >= noteMinimum) {
                    candidatsAdmis.add(resultat.getCandidat().getIdCandidat());
                }
            }
            System.out.println("Candidats admis (top " + nombreCandidats + " avec note >= " + noteMinimum + ") pour ce poste: " + candidatsAdmis.size());
        }
        
        // Assigner les statuts finaux
        for (ResultatTest2 resultat : resultats) {
            Candidat candidat = resultat.getCandidat();
            boolean estAdmis = candidatsAdmis.contains(candidat.getIdCandidat());
            String nomStatut = estAdmis ? "Pass Test 2" : "Echec Test 2";
            
            try {
                Status status = statusRepository.findByNom(nomStatut)
                    .orElseThrow(() -> new RuntimeException("Statut '" + nomStatut + "' non trouvé"));
                
                StatusCandidat statusCandidat = new StatusCandidat();
                statusCandidat.setCandidat(candidat);
                statusCandidat.setStatus(status);
                statusCandidat.setDateModification(LocalDate.now());
                
                statusCandidatRepository.save(statusCandidat);
                System.out.println("Candidat " + candidat.getIdCandidat() + " (" + candidat.getPrenom() + " " + candidat.getNom() + "): " + nomStatut + " (Note: " + resultat.getNoteSur20() + "/20)");
            } catch (Exception e) {
                System.err.println("Erreur lors de l'assignation du statut pour le candidat " + candidat.getIdCandidat() + ": " + e.getMessage());
            }
        }
        
        System.out.println("============================================");
    }

    // Classe interne pour faciliter le tri
    private static class ResultatTest2 {
        private Candidat candidat;
        private double noteSur20;
        private double pourcentage;
        private int nombreBonnesReponses;
        private int nombreTotalQuestions;
        
        // Getters et setters
        public Candidat getCandidat() { return candidat; }
        public void setCandidat(Candidat candidat) { this.candidat = candidat; }
        public double getNoteSur20() { return noteSur20; }
        public void setNoteSur20(double noteSur20) { this.noteSur20 = noteSur20; }
        public double getPourcentage() { return pourcentage; }
        public void setPourcentage(double pourcentage) { this.pourcentage = pourcentage; }
        public int getNombreBonnesReponses() { return nombreBonnesReponses; }
        public void setNombreBonnesReponses(int nombreBonnesReponses) { this.nombreBonnesReponses = nombreBonnesReponses; }
        public int getNombreTotalQuestions() { return nombreTotalQuestions; }
        public void setNombreTotalQuestions(int nombreTotalQuestions) { this.nombreTotalQuestions = nombreTotalQuestions; }
    }
    
    @PostConstruct
    public void initializeStatuses() {
        try {
            // Créer le statut "Test 2 Terminé" s'il n'existe pas
            statusRepository.findByNom("Test 2 Terminé")
                .orElseGet(() -> {
                    Status status = new Status();
                    status.setNom("Test 2 Terminé");
                    return statusRepository.save(status);
                });
            
            // Créer le statut "Pass Test 2" s'il n'existe pas
            statusRepository.findByNom("Pass Test 2")
                .orElseGet(() -> {
                    Status status = new Status();
                    status.setNom("Pass Test 2");
                    return statusRepository.save(status);
                });
                
            // Créer le statut "Echec Test 2" s'il n'existe pas
            statusRepository.findByNom("Echec Test 2")
                .orElseGet(() -> {
                    Status status = new Status();
                    status.setNom("Echec Test 2");
                    return statusRepository.save(status);
                });
                
            System.out.println("Statuts Test 2 initialisés");
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation des statuts: " + e.getMessage());
        }
    }
}
