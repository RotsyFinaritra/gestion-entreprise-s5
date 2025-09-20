# Système de Planification d'Entretiens - Guide d'Utilisation

## Vue d'ensemble

Le système de planification d'entretiens permet aux administrateurs de planifier automatiquement les entretiens pour les candidats qui ont réussi le Test 2 (statut "Pass Test 2").

## Fonctionnalités principales

### 1. Page des candidats admis par offre
- **URL** : `/admin/candidats/admis/{offreId}`
- **Accès** : Via le bouton "Planifier Entretiens" dans la page de classement Test 2
- **Affiche** :
  - Liste des candidats admis (Pass Test 2)
  - Statistiques des entretiens déjà planifiés
  - Formulaire de planification automatique

### 2. Planification automatique
Le système planifie automatiquement les entretiens selon les paramètres suivants :

#### Paramètres de planification :
- **Durée d'entretien** : 15-120 minutes (recommandé : 30-60 min)
- **Date de début** : À partir de quelle date commencer
- **Créneaux horaires** : Plusieurs créneaux par jour possibles
  - Format accepté : `8h-12h`, `08:00-12:00`, `8h00-12h00`
  - Exemple : matin `8h-12h` et après-midi `13h-17h`
- **Intervalle** : Temps de pause entre chaque entretien (0-60 min)
- **Jours fériés** : Dates à éviter (optionnel)

#### Logique de planification :
1. **Priorité** : Les candidats sont triés par performance (meilleur score en premier)
2. **Exclusions automatiques** : Les weekends sont automatiquement exclus
3. **Gestion des conflits** : Évite les créneaux déjà occupés
4. **Répartition** : Distribue les entretiens sur plusieurs jours selon les créneaux

### 3. Envoi automatique d'emails
Après planification, le système :
- Envoie automatiquement un email d'invitation à chaque candidat
- Inclut la date, l'heure, la durée et les détails de l'entretien
- Demande une confirmation de présence

## Structure de la base de données

### Table `entretien`
```sql
CREATE TABLE entretien (
    id_entretien SERIAL PRIMARY KEY,
    id_candidat BIGINT NOT NULL,
    id_offre BIGINT NOT NULL,
    date_envoi_mail DATE NOT NULL,
    date_heure_entretien TIMESTAMP NOT NULL,
    duree_entretien INTEGER NOT NULL,
    statut VARCHAR(50) NOT NULL DEFAULT 'programmé',
    lieu_entretien VARCHAR(255),
    commentaire TEXT
);
```

### Statuts possibles :
- `programmé` : Entretien planifié, invitation envoyée
- `confirmé` : Candidat a confirmé sa présence
- `terminé` : Entretien réalisé
- `annulé` : Entretien annulé

## Workflow complet

### Étape 1 : Traitement des admissions Test 2
1. Aller sur `/admin/candidats/classement/{offreId}`
2. Utiliser le formulaire "Traitement des Admissions"
3. Définir les critères (note minimum ou nombre de candidats)
4. Valider → Les candidats passent en statut "Pass Test 2"

### Étape 2 : Planification des entretiens
1. Cliquer sur "Planifier les Entretiens" 
2. Accéder à `/admin/candidats/admis/{offreId}`
3. Remplir le formulaire de planification :
   - Durée : `30` minutes
   - Date début : `2024-09-25`
   - Créneaux : `8h-12h` et `13h-17h`
   - Intervalle : `15` minutes
   - Jours fériés : (optionnel)
4. Valider → Planification automatique + envoi des emails

### Étape 3 : Suivi des entretiens
- La page affiche tous les entretiens planifiés
- Statuts en temps réel
- Possibilité de voir les détails de chaque candidat

## Exemple concret

Pour 5 candidats admis avec les paramètres :
- Durée : 45 minutes
- Créneaux : 8h-12h et 13h-17h  
- Intervalle : 15 minutes
- Date début : 25/09/2024

**Planification résultante :**
- Candidat 1 : 25/09/2024 08:00-08:45
- Candidat 2 : 25/09/2024 09:00-09:45  
- Candidat 3 : 25/09/2024 10:00-10:45
- Candidat 4 : 25/09/2024 13:00-13:45
- Candidat 5 : 25/09/2024 14:00-14:45

## Contenu de l'email d'invitation

```
Bonjour [Prénom] [Nom],

Félicitations ! Suite à votre excellent résultat au Test 2, nous avons le plaisir de vous inviter à un entretien.

Détails de l'entretien :
📅 Date et heure : 25/09/2024 à 08:00
⏱️ Durée prévue : 45 minutes
📍 Lieu : À définir
💼 Poste : Développeur Full Stack

Merci de confirmer votre présence en répondant à cet email.

En cas d'empêchement, contactez-nous au plus vite pour reprogrammer.

Cordialement,
L'équipe de recrutement
```

## Avantages du système

1. **Automatisation complète** : Plus besoin de planifier manuellement
2. **Gestion des conflits** : Évite les doublons d'horaires
3. **Communication automatique** : Emails envoyés instantanément
4. **Flexibilité** : Créneaux personnalisables par entreprise
5. **Traçabilité** : Historique complet des entretiens
6. **Par offre** : Planification séparée pour chaque offre d'emploi

## Points importants

- ⚠️ **Prérequis** : Les candidats doivent avoir le statut "Pass Test 2"
- 🔄 **Temps réel** : Les planifications sont visibles immédiatement
- 📧 **Configuration email** : Vérifier la configuration SMTP
- 🗓️ **Weekends exclus** : Automatiquement ignorés
- 🚫 **Jours fériés** : À définir manuellement si nécessaire

## Dépannage

### Problème : Aucun candidat admis
- Vérifier que des candidats ont le statut "Pass Test 2"
- Utiliser d'abord le formulaire de traitement des admissions

### Problème : Erreur de planification
- Vérifier les créneaux horaires (format correct)
- S'assurer qu'il y a assez de créneaux disponibles
- Contrôler les dates (pas dans le passé)

### Problème : Emails non envoyés
- Vérifier la configuration SMTP dans `application.properties`
- Contrôler les logs de l'application pour les erreurs d'envoi

## Extensions futures possibles

1. **Confirmation en ligne** : Page web pour confirmer l'entretien
2. **Calendrier intégré** : Vue calendrier des entretiens
3. **Notifications** : Rappels automatiques par email
4. **Évaluation** : Formulaire d'évaluation post-entretien
5. **Reprogrammation** : Interface pour modifier les créneaux
6. **Visioconférence** : Intégration avec des outils comme Zoom/Teams
