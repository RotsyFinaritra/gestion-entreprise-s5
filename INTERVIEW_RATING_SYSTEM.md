# Système de Notation des Entretiens - Documentation

## Vue d'ensemble
Le système de notation des entretiens permet aux administrateurs de :
- Créer des sections d'évaluation personnalisées par poste
- Noter les candidats lors des entretiens selon ces sections
- Consulter les résultats et statistiques d'évaluations

## Composants créés

### 1. Entités JPA

#### SectionNoteEntretien.java
- Représente les sections d'évaluation configurées par poste
- Champs : `nomSection`, `description`, `noteMax`, `ordreAffichage`
- Relation : Liée à `Poste` (Many-to-One)

#### NoteEntretien.java
- Stocke les notes attribuées lors des entretiens
- Champs : `noteObtenue`, `commentaire`, `dateEvaluation`
- Relations : Liée à `Entretien` et `SectionNoteEntretien`

### 2. Repositories

#### SectionNoteEntretienRepository.java
- `findByPosteIdPosteOrderByOrdreAffichage()` : Récupère les sections par poste, triées
- `findMaxOrdreByPoste()` : Trouve l'ordre maximum pour un poste

#### NoteEntretienRepository.java
- `findByEntretienIdEntretien()` : Notes d'un entretien
- `calculateMoyenneByEntretien()` : Calcul de moyenne
- `findByOffreId()` : Notes par offre

### 3. Services

#### SectionNoteEntretienService.java
- Gestion CRUD des sections
- Attribution automatique de l'ordre d'affichage
- Validation des données

#### NoteEntretienService.java
- Sauvegarde et calcul des notes
- Calcul de moyennes et statistiques
- Gestion des notes par entretien/section

### 4. Contrôleurs

#### SectionNotationController.java
- `/sections-notation/*` : CRUD des sections de notation
- Routes : list, new, edit, save, delete

#### NoteEntretienController.java
- `/note-entretien/*` : Formulaires de notation des entretiens
- `/note-entretien/form/{entretienId}` : Formulaire de notation
- `/admin/notes-entretien/*` : Liste des résultats

#### EntretienController.java
- `/entretiens/*` : Gestion générale des entretiens
- Vue d'ensemble avec moyennes de notation
- Integration avec le système de notation

### 5. Templates Thymeleaf

#### Sections de notation
- `sections-notation/list.html` : Liste des sections par poste
- `sections-notation/form.html` : Formulaire CRUD sections

#### Notation des entretiens
- `note-entretien/form.html` : Formulaire de notation complet
- `note-entretien/liste_resultats.html` : Résultats par offre
- `note-entretien/resultats.html` : Vue détaillée des résultats

#### Gestion des entretiens
- `entretien/list.html` : Liste générale des entretiens
- Intégration dans `admin/candidats/admis-offre.html` avec boutons "Noter"

### 6. Base de données

#### Script SQL : `base/create_interview_rating_tables.sql`
```sql
-- Tables créées :
CREATE TABLE section_note_entretien (...)
CREATE TABLE note_entretien (...)

-- Vue pour faciliter les requêtes :
CREATE VIEW v_entretien_notes AS (...)
```

## Workflow d'utilisation

### 1. Configuration des sections (Admin)
1. Aller dans "Sections Notation" via le menu admin
2. Sélectionner un poste
3. Créer les sections d'évaluation (ex: "Compétences techniques", "Savoir-être")
4. Définir la note maximale et l'ordre d'affichage

### 2. Notation des entretiens (Admin)
1. Depuis "Gestion Entretiens" ou la liste des candidats admis
2. Cliquer sur le bouton "Noter" d'un entretien
3. Remplir le formulaire avec les notes par section
4. Ajouter des commentaires si nécessaire
5. Valider la notation

### 3. Consultation des résultats (Admin)
1. "Résultats Entretiens" : Vue d'ensemble par offre
2. Voir les moyennes générales et par section
3. Exporter ou analyser les données d'évaluation

## Fonctionnalités avancées

### Calculs automatiques
- Moyenne générale par entretien
- Moyennes par section
- Statistiques par offre
- Classements des candidats

### Intégration système
- Navigation cohérente dans l'interface admin
- Liens directs entre entretiens, candidats et notations
- Gestion des autorisations (admin uniquement)

### Validation et sécurité
- Vérification des notes dans les limites définies
- Protection contre les doublons (un entretien = une note par section)
- Validation des données côté serveur

## Installation et déploiement

### 1. Base de données
```bash
# Exécuter le script de création des tables
psql -h localhost -U username -d database_name -f base/create_interview_rating_tables.sql
```

### 2. Application
- Toutes les classes Java sont prêtes
- Templates Thymeleaf intégrés
- Configuration Spring Boot automatique

### 3. Navigation
- Liens ajoutés dans `layout.html`
- Sections accessibles depuis le menu admin
- Boutons "Noter" ajoutés sur les cartes d'entretien

## Exemple d'utilisation

1. **Créer des sections pour "Développeur Java"** :
   - Compétences techniques (20 pts)
   - Résolution de problèmes (20 pts)
   - Communication (20 pts)
   - Motivation (20 pts)

2. **Noter un entretien** :
   - Compétences techniques : 16/20 + "Très bon niveau en Spring Boot"
   - Résolution de problèmes : 18/20 + "Approche méthodique"
   - Communication : 14/20 + "Peut améliorer l'expression orale"
   - Motivation : 17/20 + "Très motivé pour le poste"

3. **Résultat automatique** :
   - Moyenne générale : 16.25/20
   - Visible dans les listes d'entretiens et résultats
   - Utilisable pour le classement final

## URLs importantes

- `/sections-notation` : Gestion des sections
- `/entretiens` : Liste des entretiens
- `/note-entretien/form/{id}` : Noter un entretien
- `/admin/notes-entretien/liste-resultats` : Résultats par offre
- `/admin/candidats/admis/{offreId}` : Entretiens d'une offre (avec boutons Noter)

Le système est maintenant complet et opérationnel !
