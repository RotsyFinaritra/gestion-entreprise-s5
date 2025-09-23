-- Active: 1749052170815@@127.0.0.1@5432@entreprise
-- Script de données pour PostgreSQL
-- Données réelles pour le système de recrutement

-- Vider les tables (ordre important pour les contraintes de clé étrangère)
TRUNCATE TABLE reponse_candidat CASCADE;
TRUNCATE TABLE status_candidat CASCADE;
TRUNCATE TABLE candidat_competance CASCADE;
TRUNCATE TABLE formation_candidat CASCADE;
TRUNCATE TABLE candidat CASCADE;
TRUNCATE TABLE reponse_question CASCADE;
TRUNCATE TABLE question CASCADE;
TRUNCATE TABLE poste_competance CASCADE;
TRUNCATE TABLE poste_formation CASCADE;
TRUNCATE TABLE profil CASCADE;
TRUNCATE TABLE offre CASCADE;
TRUNCATE TABLE competance CASCADE;
TRUNCATE TABLE formation CASCADE;
TRUNCATE TABLE poste CASCADE;
TRUNCATE TABLE local CASCADE;
TRUNCATE TABLE genre CASCADE;
TRUNCATE TABLE status CASCADE;

-- Réinitialiser les séquences
ALTER SEQUENCE IF EXISTS genre_id_sexe_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS status_id_status_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS local_id_local_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS poste_id_poste_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS formation_id_formation_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS competance_id_competance_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS offre_id_offre_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS profil_id_profil_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS poste_formation_id_poste_formation_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS poste_competance_id_poste_competance_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS question_id_question_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS reponse_question_id_reponse_question_seq RESTART WITH 1;

-- ===================================================
-- DONNÉES DE BASE
-- ===================================================

-- Genres
INSERT INTO genre (nom) VALUES 
('Homme'),
('Femme'),
('Autre');

-- Status
INSERT INTO status (nom) VALUES 
('Candidature reçue'),
('En cours d''examen'),
('Pré-sélectionné'),
('Invité Test 1'),
('Pass Test 1'),
('Échec Test 1'),
('Invité Test 2'),
('Pass Test 2'),
('Échec Test 2'),
('Invité entretien'),
('Pass entretien'),
('Échec entretien'),
('Recruté'),
('Rejeté'),
('Abandon');

-- Locaux
INSERT INTO local (nom) VALUES 
('Antananarivo'),
('Antsirabe'),
('Toamasina'),
('Mahajanga'),
('Fianarantsoa'),
('Antsiranana'),
('Toliara'),
('Morondava'),
('Remote');

-- ===================================================
-- POSTES ET LEURS DONNÉES ASSOCIÉES
-- ===================================================

-- Postes
INSERT INTO poste (nom) VALUES 
('Développeur Full Stack'),
('Data Scientist'),
('Chef de Projet Digital'),
('Designer UX/UI'),
('Administrateur Système');

-- ===================================================
-- FORMATIONS
-- ===================================================

-- Formations pour Développeur Full Stack
INSERT INTO formation (nom) VALUES 
-- Full Stack
('Licence Informatique'),
('Master Génie Logiciel'),
('DUT Informatique'),
('École d''Ingénieur en Informatique'),
('Certification Full Stack Development'),
-- Data Science
('Master Data Science'),
('Master Statistiques'),
('Formation Machine Learning'),
('Certification Data Analytics'),
('Master Intelligence Artificielle'),
-- Chef de Projet
('Master Management de Projet'),
('Certification PMP'),
('MBA Digital'),
('Master Informatique avec spécialisation projet'),
('Formation Agile & Scrum'),
-- UX/UI
('Master Design Graphique'),
('Formation UX Design'),
('École d''Art Numérique'),
('Certification Adobe Creative Suite'),
('Master Communication Visuelle'),
-- Administrateur Système
('Master Réseaux et Systèmes'),
('Certification Microsoft Azure'),
('Formation Linux Administration'),
('Master Sécurité Informatique'),
('Certification AWS Solutions Architect');

-- ===================================================
-- COMPÉTENCES
-- ===================================================

INSERT INTO competance (nom) VALUES 
-- Full Stack (1-5)
('JavaScript/TypeScript'),
('React.js / Angular'),
('Node.js / Express'),
('Bases de données (SQL/NoSQL)'),
('API REST / GraphQL'),
-- Data Science (6-10)
('Python / R'),
('Machine Learning'),
('SQL / Big Data'),
('Statistiques / Probabilités'),
('Visualisation de données'),
-- Chef de Projet (11-15)
('Gestion de projet Agile'),
('Leadership d''équipe'),
('Planification stratégique'),
('Communication client'),
('Outils de gestion (Jira, Trello)'),
-- UX/UI (16-20)
('Design Thinking'),
('Prototypage (Figma, Sketch)'),
('Tests utilisateur'),
('HTML/CSS'),
('Accessibilité web'),
-- Admin Système (21-25)
('Administration Linux/Windows'),
('Virtualisation (VMware, Docker)'),
('Sécurité informatique'),
('Scripting (Bash, PowerShell)'),
('Cloud Computing (AWS, Azure)');

-- ===================================================
-- ASSOCIATIONS POSTE-FORMATION
-- ===================================================

-- Développeur Full Stack
INSERT INTO poste_formation (id_poste, id_formation, niveau, description) VALUES 
(1, 1, 'BAC+3', 'Formation de base en informatique'),
(1, 2, 'BAC+5', 'Spécialisation en génie logiciel'),
(1, 3, 'BAC+2', 'Formation technique courte'),
(1, 4, 'BAC+5', 'Formation complète en ingénierie'),
(1, 5, 'Certification', 'Certification professionnelle');

-- Data Scientist
INSERT INTO poste_formation (id_poste, id_formation, niveau, description) VALUES 
(2, 6, 'BAC+5', 'Formation spécialisée en data science'),
(2, 7, 'BAC+5', 'Base statistique solide'),
(2, 8, 'Formation', 'Compétences ML pratiques'),
(2, 9, 'Certification', 'Analyse de données'),
(2, 10, 'BAC+5', 'Intelligence artificielle avancée');

-- Chef de Projet Digital
INSERT INTO poste_formation (id_poste, id_formation, niveau, description) VALUES 
(3, 11, 'BAC+5', 'Gestion de projet avancée'),
(3, 12, 'Certification', 'Standard international PMP'),
(3, 13, 'BAC+5', 'Management digital'),
(3, 14, 'BAC+5', 'Double compétence tech/management'),
(3, 15, 'Formation', 'Méthodologies agiles');

-- Designer UX/UI
INSERT INTO poste_formation (id_poste, id_formation, niveau, description) VALUES 
(4, 16, 'BAC+5', 'Design graphique et numérique'),
(4, 17, 'Formation', 'UX Design spécialisé'),
(4, 18, 'BAC+3', 'Art numérique et interfaces'),
(4, 19, 'Certification', 'Maîtrise outils Adobe'),
(4, 20, 'BAC+5', 'Communication visuelle avancée');

-- Administrateur Système
INSERT INTO poste_formation (id_poste, id_formation, niveau, description) VALUES 
(5, 21, 'BAC+5', 'Réseaux et systèmes'),
(5, 22, 'Certification', 'Cloud Microsoft Azure'),
(5, 23, 'Formation', 'Administration Linux avancée'),
(5, 24, 'BAC+5', 'Sécurité des systèmes'),
(5, 25, 'Certification', 'Architecture cloud AWS');

-- ===================================================
-- ASSOCIATIONS POSTE-COMPÉTENCE
-- ===================================================

-- Développeur Full Stack
INSERT INTO poste_competance (id_poste, id_competance) VALUES 
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5);

-- Data Scientist
INSERT INTO poste_competance (id_poste, id_competance) VALUES 
(2, 6), (2, 7), (2, 8), (2, 9), (2, 10);

-- Chef de Projet Digital
INSERT INTO poste_competance (id_poste, id_competance) VALUES 
(3, 11), (3, 12), (3, 13), (3, 14), (3, 15);

-- Designer UX/UI
INSERT INTO poste_competance (id_poste, id_competance) VALUES 
(4, 16), (4, 17), (4, 18), (4, 19), (4, 20);

-- Administrateur Système
INSERT INTO poste_competance (id_poste, id_competance) VALUES 
(5, 21), (5, 22), (5, 23), (5, 24), (5, 25);

-- ===================================================
-- PROFILS POUR CHAQUE POSTE
-- ===================================================

-- Développeur Full Stack
INSERT INTO profil (id_poste, description) VALUES 
(1, 'Développeur junior avec 1-2 ans d''expérience, maîtrise des bases JavaScript et frameworks modernes'),
(1, 'Développeur confirmé avec 3-5 ans d''expérience, expertise Full Stack et bonnes pratiques de développement'),
(1, 'Développeur senior avec 5+ ans d''expérience, leadership technique et architecture d''applications complexes'),
(1, 'Développeur spécialisé frontend avec expertise React/Angular et design system'),
(1, 'Développeur spécialisé backend avec expertise Node.js, bases de données et APIs RESTful');

-- Data Scientist
INSERT INTO profil (id_poste, description) VALUES 
(2, 'Data Scientist junior avec formation académique solide en statistiques et premiers projets ML'),
(2, 'Data Scientist confirmé avec 3-5 ans d''expérience en analyse prédictive et business intelligence'),
(2, 'Data Scientist senior avec expertise en ML/AI et capacité à diriger des projets data complexes'),
(2, 'Spécialiste Machine Learning avec focus sur les algorithmes avancés et deep learning'),
(2, 'Data Analyst avec expertise en visualisation et communication des insights business');

-- Chef de Projet Digital
INSERT INTO profil (id_poste, description) VALUES 
(3, 'Chef de projet junior avec 2-3 ans d''expérience en gestion de projets web/mobile'),
(3, 'Chef de projet confirmé avec expertise Agile/Scrum et gestion d''équipes multidisciplinaires'),
(3, 'Chef de projet senior avec expérience en transformation digitale et projets stratégiques'),
(3, 'Product Owner avec expertise en définition produit et collaboration étroite avec les équipes de développement'),
(3, 'Scrum Master certifié avec expertise en coaching d''équipes et amélioration continue');

-- Designer UX/UI
INSERT INTO profil (id_poste, description) VALUES 
(4, 'Designer UX/UI junior avec portfolio créatif et maîtrise des outils de design modernes'),
(4, 'Designer UX confirmé avec expertise en recherche utilisateur et tests d''usabilité'),
(4, 'Designer UI senior avec expertise en design systems et cohérence visuelle multi-supports'),
(4, 'UX Researcher spécialisé dans l''analyse comportementale et l''optimisation d''expérience'),
(4, 'Designer produit avec vision globale UX/UI et collaboration étroite avec les équipes techniques');

-- Administrateur Système
INSERT INTO profil (id_poste, description) VALUES 
(5, 'Administrateur système junior avec bonnes bases Linux/Windows et motivation d''apprentissage'),
(5, 'Administrateur confirmé avec expertise en virtualisation et gestion d''infrastructures moyennes'),
(5, 'Administrateur senior avec expertise cloud et capacité à concevoir des architectures complexes'),
(5, 'Spécialiste sécurité avec focus sur la protection des systèmes et la conformité'),
(5, 'DevOps Engineer avec expertise en automatisation et intégration continue');

-- ===================================================
-- QUESTIONS ET RÉPONSES
-- ===================================================

-- Questions pour JavaScript/TypeScript (compétence 1)
INSERT INTO question (id_competance, enonce, note) VALUES 
(1, 'Quelle est la différence principale entre var, let et const en JavaScript ?', 5),
(1, 'Comment fonctionne le hoisting en JavaScript ?', 4),
(1, 'Qu''est-ce qu''une closure en JavaScript et donnez un exemple d''utilisation ?', 5),
(1, 'Expliquez la différence entre == et === en JavaScript', 3),
(1, 'Comment gérer l''asynchrone en JavaScript (callbacks, promises, async/await) ?', 5);

-- Questions pour React.js/Angular (compétence 2)
INSERT INTO question (id_competance, enonce, note) VALUES 
(2, 'Expliquez le cycle de vie d''un composant React', 4),
(2, 'Quelle est la différence entre state et props dans React ?', 3),
(2, 'Comment optimiser les performances d''une application React ?', 5),
(2, 'Qu''est-ce que le Virtual DOM et comment fonctionne-t-il ?', 4),
(2, 'Expliquez les hooks en React (useState, useEffect, etc.)', 4);

-- Questions pour Node.js/Express (compétence 3)
INSERT INTO question (id_competance, enonce, note) VALUES 
(3, 'Comment gérer les middlewares dans Express.js ?', 4),
(3, 'Expliquez l''event loop de Node.js', 5),
(3, 'Comment sécuriser une API REST avec Node.js ?', 5),
(3, 'Quelle est la différence entre require() et import en Node.js ?', 3),
(3, 'Comment gérer les erreurs dans une application Node.js ?', 4);

-- Questions pour Bases de données (compétence 4)
INSERT INTO question (id_competance, enonce, note) VALUES 
(4, 'Expliquez les différents types de relations en base de données', 3),
(4, 'Quelle est la différence entre SQL et NoSQL ?', 4),
(4, 'Comment optimiser les performances d''une requête SQL ?', 5),
(4, 'Qu''est-ce qu''une transaction et les propriétés ACID ?', 4),
(4, 'Expliquez les différents types d''index en base de données', 4);

-- Questions pour API REST/GraphQL (compétence 5)
INSERT INTO question (id_competance, enonce, note) VALUES 
(5, 'Quels sont les principes de base d''une API REST ?', 4),
(5, 'Expliquez les codes de statut HTTP les plus courants', 3),
(5, 'Quelle est la différence entre REST et GraphQL ?', 5),
(5, 'Comment versionner une API REST ?', 4),
(5, 'Qu''est-ce que CORS et comment le gérer ?', 4);

-- Questions pour Python/R (compétence 6)
INSERT INTO question (id_competance, enonce, note) VALUES 
(6, 'Expliquez les différences entre les listes, tuples et dictionnaires en Python', 3),
(6, 'Comment gérer les exceptions en Python ?', 3),
(6, 'Qu''est-ce qu''un décorateur en Python et comment l''utiliser ?', 5),
(6, 'Expliquez la différence entre les méthodes de classe et les méthodes statiques', 4),
(6, 'Comment optimiser les performances d''un script Python ?', 5);

-- Questions pour Machine Learning (compétence 7)
INSERT INTO question (id_competance, enonce, note) VALUES 
(7, 'Expliquez la différence entre apprentissage supervisé et non supervisé', 4),
(7, 'Qu''est-ce que le surapprentissage (overfitting) et comment l''éviter ?', 5),
(7, 'Quels sont les principaux algorithmes de classification ?', 4),
(7, 'Comment évaluer les performances d''un modèle de machine learning ?', 4),
(7, 'Expliquez le principe de la validation croisée', 4);

-- Questions pour SQL/Big Data (compétence 8)
INSERT INTO question (id_competance, enonce, note) VALUES 
(8, 'Expliquez les différents types de jointures SQL', 4),
(8, 'Qu''est-ce que le Big Data et ses caractéristiques (3V) ?', 3),
(8, 'Comment optimiser les requêtes sur de gros volumes de données ?', 5),
(8, 'Quelle est la différence entre OLTP et OLAP ?', 4),
(8, 'Expliquez les principes d''un data warehouse', 4);

-- Questions pour Statistiques (compétence 9)
INSERT INTO question (id_competance, enonce, note) VALUES 
(9, 'Expliquez la différence entre corrélation et causalité', 4),
(9, 'Qu''est-ce qu''un test d''hypothèse et comment l''interpréter ?', 5),
(9, 'Expliquez les mesures de tendance centrale et de dispersion', 3),
(9, 'Qu''est-ce que la loi normale et ses propriétés ?', 4),
(9, 'Comment interpréter un intervalle de confiance ?', 4);

-- Questions pour Visualisation (compétence 10)
INSERT INTO question (id_competance, enonce, note) VALUES 
(10, 'Quels sont les principes d''une bonne visualisation de données ?', 4),
(10, 'Quand utiliser un graphique en barres vs un graphique en secteurs ?', 3),
(10, 'Comment choisir les couleurs appropriées pour une visualisation ?', 3),
(10, 'Qu''est-ce qu''un dashboard efficace et ses composants ?', 4),
(10, 'Comment éviter les biais dans la présentation des données ?', 5);

-- Questions pour Gestion Agile (compétence 11)
INSERT INTO question (id_competance, enonce, note) VALUES 
(11, 'Expliquez les principes fondamentaux de la méthode Agile', 4),
(11, 'Quelle est la différence entre Scrum et Kanban ?', 4),
(11, 'Expliquez le rôle de chaque membre de l''équipe Scrum', 3),
(11, 'Comment estimer la charge de travail dans un projet Agile ?', 4),
(11, 'Qu''est-ce qu''une rétrospective et comment la mener ?', 4);

-- Questions pour Leadership (compétence 12)
INSERT INTO question (id_competance, enonce, note) VALUES 
(12, 'Comment motiver une équipe lors d''un projet difficile ?', 5),
(12, 'Expliquez les différents styles de leadership', 4),
(12, 'Comment gérer les conflits au sein d''une équipe ?', 5),
(12, 'Qu''est-ce que la délégation efficace ?', 4),
(12, 'Comment donner un feedback constructif ?', 4);

-- Questions pour Planification (compétence 13)
INSERT INTO question (id_competance, enonce, note) VALUES 
(13, 'Comment créer un planning de projet réaliste ?', 4),
(13, 'Qu''est-ce que le chemin critique dans un projet ?', 4),
(13, 'Comment gérer les risques dans un projet ?', 5),
(13, 'Expliquez les différentes phases d''un projet', 3),
(13, 'Comment mesurer l''avancement d''un projet ?', 4);

-- Questions pour Communication (compétence 14)
INSERT INTO question (id_competance, enonce, note) VALUES 
(14, 'Comment présenter un projet technique à des non-techniciens ?', 5),
(14, 'Quels sont les éléments d''une communication efficace ?', 3),
(14, 'Comment gérer une réunion productive ?', 4),
(14, 'Comment rédiger un cahier des charges clair ?', 4),
(14, 'Comment négocier avec un client mécontent ?', 5);

-- Questions pour Outils de gestion (compétence 15)
INSERT INTO question (id_competance, enonce, note) VALUES 
(15, 'Expliquez les fonctionnalités principales de Jira', 3),
(15, 'Comment organiser un projet dans Trello ?', 3),
(15, 'Quelle est la différence entre un Epic et une User Story ?', 4),
(15, 'Comment configurer un workflow dans un outil de gestion ?', 4),
(15, 'Comment mesurer la vélocité d''une équipe ?', 4);

-- Questions pour Design Thinking (compétence 16)
INSERT INTO question (id_competance, enonce, note) VALUES 
(16, 'Expliquez les 5 étapes du Design Thinking', 4),
(16, 'Comment mener une session d''idéation créative ?', 4),
(16, 'Qu''est-ce qu''un persona utilisateur et comment le créer ?', 4),
(16, 'Comment définir un problème design à résoudre ?', 4),
(16, 'Expliquez l''importance de l''empathie dans le design', 5);

-- Questions pour Prototypage (compétence 17)
INSERT INTO question (id_competance, enonce, note) VALUES 
(17, 'Quelle est la différence entre wireframe, mockup et prototype ?', 4),
(17, 'Comment choisir entre Figma, Sketch et Adobe XD ?', 3),
(17, 'Qu''est-ce qu''un prototype haute fidélité vs basse fidélité ?', 3),
(17, 'Comment tester un prototype avec des utilisateurs ?', 5),
(17, 'Expliquez les avantages du prototypage rapide', 4);

-- Questions pour Tests utilisateur (compétence 18)
INSERT INTO question (id_competance, enonce, note) VALUES 
(18, 'Comment préparer et mener un test utilisateur ?', 5),
(18, 'Quelle est la différence entre test d''utilisabilité et test A/B ?', 4),
(18, 'Comment analyser et interpréter les résultats d''un test utilisateur ?', 4),
(18, 'Qu''est-ce que le guerrilla testing ?', 3),
(18, 'Comment définir des métriques d''utilisabilité ?', 4);

-- Questions pour HTML/CSS (compétence 19)
INSERT INTO question (id_competance, enonce, note) VALUES 
(19, 'Expliquez la différence entre display: block, inline et inline-block', 3),
(19, 'Comment créer une mise en page responsive ?', 4),
(19, 'Qu''est-ce que Flexbox et Grid CSS ?', 4),
(19, 'Comment optimiser le CSS pour les performances ?', 4),
(19, 'Expliquez les principes du CSS modulaire (BEM, SMACSS)', 5);

-- Questions pour Accessibilité (compétence 20)
INSERT INTO question (id_competance, enonce, note) VALUES 
(20, 'Quels sont les principes WCAG pour l''accessibilité web ?', 4),
(20, 'Comment rendre un site accessible aux malvoyants ?', 5),
(20, 'Qu''est-ce que l''attribut alt et son importance ?', 3),
(20, 'Comment tester l''accessibilité d''un site web ?', 4),
(20, 'Expliquez l''importance du contraste des couleurs', 4);

-- Questions pour Administration Linux (compétence 21)
INSERT INTO question (id_competance, enonce, note) VALUES 
(21, 'Expliquez la structure des répertoires Linux (/etc, /var, /usr)', 3),
(21, 'Comment gérer les permissions de fichiers sous Linux ?', 4),
(21, 'Qu''est-ce qu''un processus daemon et comment le gérer ?', 4),
(21, 'Comment configurer un serveur web Apache/Nginx ?', 5),
(21, 'Expliquez la différence entre hard link et symbolic link', 4);

-- Questions pour Virtualisation (compétence 22)
INSERT INTO question (id_competance, enonce, note) VALUES 
(22, 'Quelle est la différence entre virtualisation et containerisation ?', 4),
(22, 'Comment fonctionne Docker et ses avantages ?', 4),
(22, 'Qu''est-ce que VMware vSphere et ses composants ?', 4),
(22, 'Comment orchestrer des containers avec Kubernetes ?', 5),
(22, 'Expliquez les concepts de Dockerfile et Docker Compose', 4);

-- Questions pour Sécurité (compétence 23)
INSERT INTO question (id_competance, enonce, note) VALUES 
(23, 'Quels sont les principaux types d''attaques informatiques ?', 4),
(23, 'Comment sécuriser un serveur Linux ?', 5),
(23, 'Qu''est-ce qu''un firewall et comment le configurer ?', 4),
(23, 'Expliquez les principes de la cryptographie (symétrique/asymétrique)', 5),
(23, 'Comment mettre en place une politique de sauvegarde sécurisée ?', 4);

-- Questions pour Scripting (compétence 24)
INSERT INTO question (id_competance, enonce, note) VALUES 
(24, 'Comment automatiser des tâches avec des scripts Bash ?', 4),
(24, 'Quelle est la différence entre Bash et PowerShell ?', 3),
(24, 'Comment gérer les erreurs dans un script ?', 4),
(24, 'Expliquez l''utilisation des variables d''environnement', 3),
(24, 'Comment planifier l''exécution de scripts (cron, tâches planifiées) ?', 4);

-- Questions pour Cloud Computing (compétence 25)
INSERT INTO question (id_competance, enonce, note) VALUES 
(25, 'Expliquez les différents modèles de cloud (IaaS, PaaS, SaaS)', 4),
(25, 'Quels sont les avantages du cloud computing ?', 3),
(25, 'Comment choisir entre AWS, Azure et Google Cloud ?', 4),
(25, 'Qu''est-ce que l''auto-scaling et comment l''implémenter ?', 5),
(25, 'Comment sécuriser une infrastructure cloud ?', 5);

-- ===================================================
-- RÉPONSES AUX QUESTIONS (3 par question, 1 seule vraie)
-- ===================================================

-- Réponses pour la question 1 (var, let, const)
INSERT INTO reponse_question (id_question, choix, valeur) VALUES 
(1, 'var a une portée fonction, let et const ont une portée de bloc', true),
(1, 'var, let et const ont tous la même portée', false),
(1, 'let ne peut pas être réassigné, var et const peuvent l''être', false);

-- Réponses pour la question 2 (hoisting)
INSERT INTO reponse_question (id_question, choix, valeur) VALUES 
(2, 'Le hoisting remonte les déclarations de variables et fonctions en haut de leur portée', true),
(2, 'Le hoisting n''affecte que les fonctions, pas les variables', false),
(2, 'Le hoisting est uniquement disponible en mode strict', false);

-- Réponses pour la question 3 (closure)
INSERT INTO reponse_question (id_question, choix, valeur) VALUES 
(3, 'Une closure permet à une fonction d''accéder aux variables de sa portée externe même après que cette portée soit fermée', true),
(3, 'Une closure est une fonction qui ne peut être appelée qu''une seule fois', false),
(3, 'Une closure est un type de variable spécial en JavaScript', false);

-- Réponses pour la question 4 (== vs ===)
INSERT INTO reponse_question (id_question, choix, valeur) VALUES 
(4, '== compare avec conversion de type, === compare sans conversion (strict)', true),
(4, '== et === fonctionnent exactement de la même manière', false),
(4, '== est plus rapide que === en termes de performance', false);

-- Réponses pour la question 5 (asynchrone)
INSERT INTO reponse_question (id_question, choix, valeur) VALUES 
(5, 'async/await est la syntaxe moderne recommandée, basée sur les Promises', true),
(5, 'Les callbacks sont toujours la meilleure solution pour l''asynchrone', false),
(5, 'Les Promises et async/await ne peuvent pas être utilisées ensemble', false);

-- Réponses pour la question 6 (cycle de vie React)
INSERT INTO reponse_question (id_question, choix, valeur) VALUES 
(6, 'componentDidMount, componentDidUpdate, componentWillUnmount sont les phases principales', true),
(6, 'React n''a pas de cycle de vie, les composants sont statiques', false),
(6, 'Le cycle de vie ne s''applique qu''aux composants de classe, pas aux hooks', false);

-- Réponses pour la question 7 (state vs props)
INSERT INTO reponse_question (id_question, choix, valeur) VALUES 
(7, 'state est interne au composant et mutable, props sont passées par le parent et immutables', true),
(7, 'state et props sont identiques, juste deux noms différents', false),
(7, 'props peuvent être modifiées par le composant enfant', false);

-- Réponses pour la question 8 (optimisation React)
INSERT INTO reponse_question (id_question, choix, valeur) VALUES 
(8, 'React.memo, useMemo, useCallback et lazy loading sont des techniques d''optimisation', true),
(8, 'React s''optimise automatiquement, aucune action n''est nécessaire', false),
(8, 'Il faut uniquement optimiser les composants de classe', false);

-- Réponses pour la question 9 (Virtual DOM)
INSERT INTO reponse_question (id_question, choix, valeur) VALUES 
(9, 'Le Virtual DOM est une représentation JavaScript du DOM réel, permettant des updates efficaces', true),
(9, 'Le Virtual DOM remplace complètement le DOM du navigateur', false),
(9, 'Le Virtual DOM n''est utilisé que pour les animations', false);

-- Réponses pour la question 10 (hooks React)
INSERT INTO reponse_question (id_question, choix, valeur) VALUES 
(10, 'useState gère l''état local, useEffect gère les effets de bord', true),
(10, 'Les hooks ne peuvent être utilisés que dans les composants de classe', false),
(10, 'useState et useEffect font la même chose', false);

-- Réponses pour Node.js/Express (questions 11-15)
INSERT INTO reponse_question (id_question, choix, valeur) VALUES 
-- Question 11 (middlewares Express)
(11, 'Les middlewares sont des fonctions qui s''exécutent en séquence pour traiter les requêtes', true),
(11, 'Express n''utilise pas de middlewares', false),
(11, 'Les middlewares ne peuvent être utilisés qu''avec GET', false),
-- Question 12 (event loop)
(12, 'L''event loop gère l''exécution asynchrone et les callbacks dans Node.js', true),
(12, 'L''event loop n''existe que dans le navigateur', false),
(12, 'Node.js est synchrone et n''a pas d''event loop', false),
-- Question 13 (sécurité API)
(13, 'JWT, CORS, rate limiting et validation des inputs sont essentiels', true),
(13, 'Une API REST n''a pas besoin de sécurité', false),
(13, 'Il suffit d''utiliser HTTPS pour sécuriser une API', false),
-- Question 14 (require vs import)
(14, 'require est CommonJS (Node.js traditionnel), import est ES6 modules', true),
(14, 'require et import sont identiques', false),
(14, 'import ne fonctionne que dans le navigateur', false),
-- Question 15 (gestion erreurs Node.js)
(15, 'try/catch, error-first callbacks et middleware d''erreur Express', true),
(15, 'Node.js n''a pas de mécanisme de gestion d''erreurs', false),
(15, 'Les erreurs Node.js s''affichent automatiquement dans le navigateur', false);

-- Réponses pour API REST/GraphQL (questions 21-25)
INSERT INTO reponse_question (id_question, choix, valeur) VALUES 
(21, 'REST utilise les verbes HTTP (GET, POST, PUT, DELETE) et est stateless', true),
(21, 'REST ne peut utiliser que le protocole HTTP', false),
(21, 'Une API REST doit obligatoirement retourner du JSON', false),
(22, '200 (OK), 404 (Not Found), 500 (Server Error), 201 (Created), 401 (Unauthorized)', true),
(22, 'Tous les codes HTTP commencent par 2', false),
(22, 'Les codes de statut ne sont pas importants dans une API', false),
(23, 'GraphQL permet de récupérer exactement les données nécessaires en une requête', true),
(23, 'REST est plus moderne que GraphQL', false),
(23, 'GraphQL ne peut pas faire de mutations', false),
(24, 'Versioning par URL, headers ou query parameters selon les besoins', true),
(24, 'Une API ne doit jamais être versionnée', false),
(24, 'Il faut créer une nouvelle API pour chaque version', false),
(25, 'CORS permet aux navigateurs d''accéder à des ressources cross-origin de manière sécurisée', true),
(25, 'CORS n''est nécessaire que pour les API payantes', false),
(25, 'CORS bloque automatiquement toutes les requêtes externes', false);

-- Réponses pour Python/R (questions 26-30)
INSERT INTO reponse_question (id_question, choix, valeur) VALUES 
(26, 'Listes mutables ordonnées, tuples immutables ordonnés, dictionnaires mutables clé-valeur', true),
(26, 'Listes et tuples sont identiques', false),
(26, 'Les dictionnaires ne peuvent contenir que des strings', false),
(27, 'try/except/finally avec des exceptions spécifiques et hiérarchie d''exceptions', true),
(27, 'Python n''a pas de gestion d''exceptions', false),
(27, 'Il faut toujours utiliser except sans spécifier le type', false),
(28, 'Un décorateur modifie ou étend le comportement d''une fonction sans la modifier', true),
(28, 'Les décorateurs ne fonctionnent qu''avec les classes', false),
(28, 'Un décorateur supprime la fonction originale', false),
(29, 'Méthodes de classe reçoivent cls, méthodes statiques n''ont pas de référence automatique', true),
(29, 'Les méthodes de classe et statiques sont identiques', false),
(29, 'Les méthodes statiques ne peuvent pas être appelées', false),
(30, 'Utiliser numpy, pandas, multiprocessing, profiling et algorithmes efficaces', true),
(30, 'Python ne peut pas être optimisé', false),
(30, 'Il faut toujours utiliser des boucles for au lieu de comprehensions', false);

-- Réponses pour Machine Learning (questions 31-35)
INSERT INTO reponse_question (id_question, choix, valeur) VALUES 
(31, 'Supervisé utilise des données labellisées, non supervisé découvre des patterns cachés', true),
(31, 'L''apprentissage supervisé ne nécessite pas de données', false),
(31, 'L''apprentissage non supervisé est plus facile que le supervisé', false),
(32, 'Overfitting : trop spécialisé sur données d''entraînement. Solution : validation, régularisation, plus de données', true),
(32, 'Le surapprentissage améliore toujours les performances', false),
(32, 'Il n''y a pas de solution au surapprentissage', false),
(33, 'Decision Trees, Random Forest, SVM, Naive Bayes, Logistic Regression, Neural Networks', true),
(33, 'Il n''existe qu''un seul algorithme de classification', false),
(33, 'Les algorithmes de classification ne fonctionnent qu''avec des nombres', false),
(34, 'Précision, rappel, F1-score, matrice de confusion, AUC-ROC selon le contexte', true),
(34, 'La précision seule suffit toujours pour évaluer un modèle', false),
(34, 'Un modèle ne peut pas être évalué', false),
(35, 'Division des données en k-folds pour entraînement/validation rotative et estimation robuste', true),
(35, 'La validation croisée n''est utile que pour les petits datasets', false),
(35, 'Il faut toujours utiliser toutes les données pour l''entraînement', false);

-- Réponses pour SQL/Big Data (questions 36-40)
INSERT INTO reponse_question (id_question, choix, valeur) VALUES 
(36, 'INNER, LEFT, RIGHT, FULL OUTER JOIN selon les besoins de données', true),
(36, 'Il n''existe qu''un seul type de jointure', false),
(36, 'Les jointures ralentissent toujours les requêtes', false),
(37, 'Big Data : Volume, Vélocité, Variété. Données massives nécessitant outils spécialisés', true),
(37, 'Big Data ne concerne que les réseaux sociaux', false),
(37, 'Big Data signifie simplement beaucoup de données', false),
(38, 'Partitionnement, index appropriés, requêtes optimisées, hardware adapté', true),
(38, 'Il est impossible d''optimiser les requêtes sur gros volumes', false),
(38, 'Il faut toujours faire des SELECT * sur les gros volumes', false),
(39, 'OLTP : transactions en temps réel, OLAP : analyse et reporting sur données historiques', true),
(39, 'OLTP et OLAP sont identiques', false),
(39, 'OLAP ne peut pas traiter de données récentes', false),
(40, 'Entrepôt centralisé optimisé pour l''analyse avec données historiques structurées', true),
(40, 'Un data warehouse est identique à une base de données normale', false),
(40, 'Les data warehouses ne stockent que des données actuelles', false);

-- Réponses pour Statistiques (questions 41-45)
INSERT INTO reponse_question (id_question, choix, valeur) VALUES 
(41, 'Corrélation mesure la relation, causalité implique qu''une variable influence l''autre', true),
(41, 'Corrélation et causalité sont identiques', false),
(41, 'Une forte corrélation prouve toujours la causalité', false),
(42, 'Test pour valider/rejeter une hypothèse avec p-value et seuil de signification', true),
(42, 'Un test d''hypothèse donne toujours une réponse définitive', false),
(42, 'La p-value n''est pas importante dans un test d''hypothèse', false),
(43, 'Tendance centrale : moyenne, médiane, mode. Dispersion : écart-type, variance, range', true),
(43, 'La moyenne est toujours la meilleure mesure de tendance centrale', false),
(43, 'Il n''y a qu''une seule mesure de dispersion', false),
(44, 'Distribution symétrique en cloche, 68%-95%-99.7% règle, fondamentale en statistiques', true),
(44, 'La loi normale n''existe que dans la théorie', false),
(44, 'Toutes les données suivent obligatoirement une loi normale', false),
(45, 'Fourchette de valeurs avec niveau de confiance pour estimer un paramètre de population', true),
(45, 'Un intervalle de confiance donne la valeur exacte', false),
(45, 'Plus l''intervalle est large, plus il est précis', false);

-- Réponses pour Visualisation (questions 46-50)
INSERT INTO reponse_question (id_question, choix, valeur) VALUES 
(46, 'Clarté, simplicité, pertinence, adaptation à l''audience et au message', true),
(46, 'Plus il y a de couleurs, mieux c''est', false),
(46, 'Les graphiques 3D sont toujours préférables', false),
(47, 'Barres pour comparaisons, secteurs pour proportions d''un tout', true),
(47, 'Les graphiques en secteurs sont toujours meilleurs', false),
(47, 'Il n''y a pas de différence entre ces deux types', false),
(48, 'Contraste suffisant, signification culturelle, accessibilité, cohérence', true),
(48, 'Il faut toujours utiliser les couleurs les plus vives', false),
(48, 'Les couleurs n''ont pas d''impact sur la compréhension', false),
(49, 'KPIs clairs, navigation intuitive, actualisation en temps réel, visuels appropriés', true),
(49, 'Un dashboard doit contenir toutes les données disponibles', false),
(49, 'Les dashboards ne nécessitent pas d''organisation', false),
(50, 'Échelles appropriées, contexte complet, éviter les manipulations visuelles', true),
(50, 'Les biais dans les visualisations sont impossibles à éviter', false),
(50, 'Il n''est pas nécessaire de considérer les biais', false);

-- Réponses pour Gestion Agile (questions 51-55)
INSERT INTO reponse_question (id_question, choix, valeur) VALUES 
(51, 'Individus et interactions, logiciel fonctionnel, collaboration client, adaptation au changement', true),
(51, 'Agile signifie travailler plus vite', false),
(51, 'L''Agile ne nécessite aucune documentation', false),
(52, 'Scrum : sprints fixes et rôles définis, Kanban : flux continu et limite WIP', true),
(52, 'Scrum et Kanban sont identiques', false),
(52, 'Kanban ne peut pas être utilisé en développement', false),
(53, 'Product Owner (vision produit), Scrum Master (facilitation), Development Team (réalisation)', true),
(53, 'Tous les membres d''équipe Scrum ont le même rôle', false),
(53, 'Le Scrum Master code aussi', false),
(54, 'Planning poker, story points, vélocité historique, décomposition des tâches', true),
(54, 'L''estimation est impossible en Agile', false),
(54, 'Il faut toujours estimer en heures exactes', false),
(55, 'Réunion d''amélioration continue : qu''est-ce qui a bien fonctionné, à améliorer, actions', true),
(55, 'Une rétrospective sert à critiquer l''équipe', false),
(55, 'Les rétrospectives ne sont pas utiles', false);

-- Réponses pour Leadership (questions 56-60)
INSERT INTO reponse_question (id_question, choix, valeur) VALUES 
(56, 'Communication transparente, reconnaissance, soutien, objectifs clairs, exemplarité', true),
(56, 'Il faut toujours augmenter la pression', false),
(56, 'La motivation ne peut pas être influencée', false),
(57, 'Directif, participatif, délégatif, transformationnel selon le contexte et l''équipe', true),
(57, 'Il n''existe qu''un seul style de leadership efficace', false),
(57, 'Le style de leadership ne doit jamais changer', false),
(58, 'Écoute active, médiation, recherche de solutions gagnant-gagnant, communication ouverte', true),
(58, 'Les conflits doivent toujours être évités', false),
(58, 'Il faut imposer sa solution en cas de conflit', false),
(59, 'Choisir la bonne personne, définir clairement les attentes, donner autonomie et support', true),
(59, 'Déléguer signifie se débarrasser des tâches ennuyeuses', false),
(59, 'Il ne faut jamais déléguer les tâches importantes', false),
(60, 'Spécifique, bienveillant, axé sur les comportements, actionnable, régulier', true),
(60, 'Le feedback doit toujours être négatif pour être utile', false),
(60, 'Il vaut mieux éviter de donner du feedback', false);

-- Réponses pour Planification (questions 61-65)
INSERT INTO reponse_question (id_question, choix, valeur) VALUES 
(61, 'Décomposition WBS, estimation réaliste, dépendances, ressources, buffers', true),
(61, 'Il suffit de deviner les durées', false),
(61, 'Un planning ne doit jamais changer', false),
(62, 'Séquence de tâches la plus longue qui détermine la durée minimale du projet', true),
(62, 'Le chemin critique est toujours le plus court', false),
(62, 'Tous les chemins dans un projet sont critiques', false),
(63, 'Identification, évaluation probabilité/impact, plans de mitigation, suivi régulier', true),
(63, 'Les risques ne peuvent pas être anticipés', false),
(63, 'Il ne faut pas s''inquiéter des risques', false),
(64, 'Initiation, planification, exécution, surveillance/contrôle, clôture', true),
(64, 'Un projet n''a qu''une seule phase', false),
(64, 'Les phases ne se chevauchent jamais', false),
(65, 'Indicateurs de performance, jalons, livrables, budget consommé, vélocité', true),
(65, 'L''avancement ne peut pas être mesuré', false),
(65, 'Seul le temps passé compte', false);

-- Réponses pour Communication (questions 66-70)
INSERT INTO reponse_question (id_question, choix, valeur) VALUES 
(66, 'Langage accessible, analogies, bénéfices business, visuels, éviter le jargon technique', true),
(66, 'Il faut utiliser un maximum de termes techniques', false),
(66, 'Les non-techniciens ne peuvent pas comprendre les projets techniques', false),
(67, 'Message clair, écoute active, adaptation à l''audience, feedback, non-verbal cohérent', true),
(67, 'Parler fort suffit pour bien communiquer', false),
(67, 'La communication non-verbale n''est pas importante', false),
(68, 'Ordre du jour, objectifs clairs, timeboxing, participation active, actions de suivi', true),
(68, 'Une réunion productive dure nécessairement longtemps', false),
(68, 'Il ne faut jamais préparer de réunion', false),
(69, 'Contexte, objectifs, exigences fonctionnelles/techniques, contraintes, critères d''acceptation', true),
(69, 'Un cahier des charges doit être le plus court possible', false),
(69, 'Les exigences techniques ne sont pas importantes', false),
(70, 'Écoute empathique, reconnaissance du problème, solutions alternatives, compromis', true),
(70, 'Il faut toujours céder aux demandes du client', false),
(70, 'Un client mécontent ne peut jamais être satisfait', false);

-- Réponses pour Outils de gestion (questions 71-75)
INSERT INTO reponse_question (id_question, choix, valeur) VALUES 
(71, 'Gestion tickets, workflows, sprints, reporting, tableaux de bord personnalisables', true),
(71, 'Jira ne sert qu''à créer des bugs', false),
(71, 'Jira ne peut pas être personnalisé', false),
(72, 'Tableaux Kanban, cartes, listes, équipes, automatisation, intégrations', true),
(72, 'Trello ne peut gérer que des projets personnels', false),
(72, 'Il n''y a qu''une seule façon d''organiser un projet dans Trello', false),
(73, 'Epic : grande fonctionnalité, User Story : besoin utilisateur spécifique', true),
(73, 'Epic et User Story sont identiques', false),
(73, 'Les Epics sont toujours plus petites que les User Stories', false),
(74, 'États, transitions, règles métier, permissions, notifications selon processus', true),
(74, 'Un workflow ne peut pas être personnalisé', false),
(74, 'Tous les projets doivent utiliser le même workflow', false),
(75, 'Points de story complétés par sprint, mesure de productivité et prédictibilité', true),
(75, 'La vélocité mesure uniquement la vitesse de codage', false),
(75, 'Une équipe doit avoir la même vélocité à chaque sprint', false);

-- Réponses pour Design Thinking (questions 76-80)
INSERT INTO reponse_question (id_question, choix, valeur) VALUES 
(76, 'Empathie, Définition, Idéation, Prototype, Test - processus itératif centré utilisateur', true),
(76, 'Le Design Thinking n''a que 3 étapes', false),
(76, 'Les étapes doivent toujours être suivies dans l''ordre strict', false),
(77, 'Brainstorming, brainwriting, SCAMPER, mind mapping, divergence puis convergence', true),
(77, 'L''idéation doit toujours se faire seul', false),
(77, 'Il faut critiquer chaque idée immédiatement', false),
(78, 'Archétype d''utilisateur basé sur recherche : besoins, comportements, motivations, frustrations', true),
(78, 'Un persona est une vraie personne', false),
(78, 'Un seul persona suffit pour tout projet', false),
(79, 'Observation, interviews, empathy maps, problem statement, point de vue utilisateur', true),
(79, 'Le problème est toujours évident dès le début', false),
(79, 'Il ne faut pas impliquer les utilisateurs dans la définition du problème', false),
(80, 'Comprendre les utilisateurs, leurs émotions, contexte, besoins non exprimés', true),
(80, 'L''empathie n''est pas nécessaire en design', false),
(80, 'Il suffit de supposer ce que veulent les utilisateurs', false);

-- Réponses pour Prototypage (questions 81-85)
INSERT INTO reponse_question (id_question, choix, valeur) VALUES 
(81, 'Wireframe : structure, Mockup : design visuel, Prototype : interactions fonctionnelles', true),
(81, 'Ces trois termes désignent la même chose', false),
(81, 'Les wireframes sont toujours en couleur', false),
(82, 'Figma : collaboratif web, Sketch : Mac natif, Adobe XD : Creative Suite intégré', true),
(82, 'Tous ces outils sont identiques', false),
(82, 'Il faut toujours utiliser le plus cher', false),
(83, 'Haute fidélité : proche du final, Basse fidélité : concepts et flux généraux', true),
(83, 'Un prototype haute fidélité est toujours meilleur', false),
(83, 'La fidélité ne dépend que des couleurs utilisées', false),
(84, 'Tests d''utilisabilité, interviews, observation, A/B testing, métriques comportementales', true),
(84, 'Il ne faut pas tester les prototypes', false),
(84, 'Seuls les développeurs peuvent tester un prototype', false),
(85, 'Validation rapide, itération, coût réduit, communication d''idées, réduction risques', true),
(85, 'Le prototypage rapide produit toujours des solutions de mauvaise qualité', false),
(85, 'Il vaut mieux développer directement sans prototype', false);

-- Réponses pour Tests utilisateur (questions 86-90)
INSERT INTO reponse_question (id_question, choix, valeur) VALUES 
(86, 'Objectifs clairs, scénarios réalistes, environnement naturel, observation sans intervention', true),
(86, 'Il faut toujours guider l''utilisateur pendant le test', false),
(86, 'Un test utilisateur ne nécessite aucune préparation', false),
(87, 'Utilisabilité : comportement utilisateur, A/B : comparaison de versions', true),
(87, 'Les tests d''utilisabilité et A/B sont identiques', false),
(87, 'Les tests A/B ne peuvent pas mesurer l''utilisabilité', false),
(88, 'Analyse qualitative/quantitative, identification patterns, recommandations actionnables', true),
(88, 'Il suffit de compter les erreurs pour analyser un test', false),
(88, 'Les résultats d''un test utilisateur sont toujours évidents', false),
(89, 'Tests rapides dans l''environnement naturel, recrutement spontané, coût minimal', true),
(89, 'Le guerrilla testing n''est pas scientifique', false),
(89, 'Il faut un laboratoire spécialisé pour tous les tests', false),
(90, 'Taux de réussite, temps de completion, erreurs, satisfaction, métriques spécifiques', true),
(90, 'L''utilisabilité ne peut pas être mesurée', false),
(90, 'Une seule métrique suffit pour évaluer l''utilisabilité', false);

-- Réponses pour HTML/CSS (questions 91-95)
INSERT INTO reponse_question (id_question, choix, valeur) VALUES 
(91, 'Block : largeur 100%, Inline : largeur contenu, Inline-block : largeur contenu mais propriétés block', true),
(91, 'Ces trois valeurs sont identiques', false),
(91, 'Display n''affecte que la couleur des éléments', false),
(92, 'Media queries, grilles flexibles, images adaptatives, mobile-first approach', true),
(92, 'Le responsive design n''est nécessaire que pour les mobiles', false),
(92, 'Une seule taille d''écran suffit pour tous les appareils', false),
(93, 'Flexbox : layouts 1D (lignes/colonnes), Grid : layouts 2D (grilles complexes)', true),
(93, 'Flexbox et Grid CSS font exactement la même chose', false),
(93, 'Grid CSS ne peut créer que des grilles carrées', false),
(94, 'Minification, sprites, critical CSS, lazy loading, optimisation sélecteurs', true),
(94, 'Le CSS n''a pas d''impact sur les performances', false),
(94, 'Plus il y a de CSS, mieux c''est', false),
(95, 'BEM : Block Element Modifier, SMACSS : catégories CSS, organisation maintenable', true),
(95, 'Il n''y a pas de bonnes pratiques pour organiser le CSS', false),
(95, 'Le CSS modulaire est plus lent', false);

-- Réponses pour Accessibilité (questions 96-100)
INSERT INTO reponse_question (id_question, choix, valeur) VALUES 
(96, 'Perceptible, Utilisable, Compréhensible, Robuste - standards d''accessibilité web', true),
(96, 'WCAG ne concerne que les développeurs', false),
(96, 'L''accessibilité web n''a pas de standards officiels', false),
(97, 'Lecteurs d''écran, alt text, structure sémantique, navigation clavier, contrastes', true),
(97, 'Les malvoyants ne peuvent pas utiliser internet', false),
(97, 'Il suffit d''augmenter la taille de police', false),
(98, 'Description alternative des images pour technologies d''assistance', true),
(98, 'L''attribut alt est uniquement décoratif', false),
(98, 'Toutes les images doivent avoir le même alt text', false),
(99, 'Outils automatisés, tests manuels, lecteurs d''écran, navigation clavier', true),
(99, 'L''accessibilité ne peut pas être testée', false),
(99, 'Seuls les experts peuvent tester l''accessibilité', false),
(100, 'Ratio 4.5:1 minimum, lisibilité, inclusion, conformité légale', true),
(100, 'Le contraste n''affecte que l''esthétique', false),
(100, 'Tous les contrastes sont acceptables', false);

-- Réponses pour Administration Linux (questions 101-105)
INSERT INTO reponse_question (id_question, choix, valeur) VALUES 
(101, '/etc (config), /var (données variables), /usr (programmes utilisateur), /home (utilisateurs)', true),
(101, 'Tous les répertoires Linux ont la même fonction', false),
(101, 'L''organisation des répertoires est aléatoire sous Linux', false),
(102, 'chmod (permissions), chown (propriétaire), umask, rwx pour user/group/other', true),
(102, 'Tous les fichiers ont les mêmes permissions sous Linux', false),
(102, 'Les permissions ne sont importantes que pour les fichiers secrets', false),
(103, 'Processus d''arrière-plan, systemctl/service, logs, démarrage automatique', true),
(103, 'Un daemon est un utilisateur spécial', false),
(103, 'Les daemons ne peuvent fonctionner que le jour', false),
(104, 'Configuration virtual hosts, modules, SSL, performance, sécurité', true),
(104, 'Apache et Nginx sont identiques', false),
(104, 'Un serveur web fonctionne sans configuration', false),
(105, 'Hard link : même inode, Symbolic link : pointeur vers chemin', true),
(105, 'Les deux types de liens sont identiques', false),
(105, 'Les liens ne fonctionnent qu''avec des fichiers texte', false);

-- Réponses pour Virtualisation (questions 106-110)
INSERT INTO reponse_question (id_question, choix, valeur) VALUES 
(106, 'Virtualisation : OS complets, Conteneurisation : isolation processus, ressources partagées', true),
(106, 'Docker et VMware font exactement la même chose', false),
(106, 'La conteneurisation nécessite plus de ressources', false),
(107, 'Isolation, portabilité, déploiement rapide, gestion dépendances, microservices', true),
(107, 'Docker ne sert qu''au développement', false),
(107, 'Les conteneurs sont moins sécurisés que les VMs', false),
(108, 'Hyperviseur, vCenter, ESXi, gestion centralisée, haute disponibilité', true),
(108, 'VMware ne peut virtualiser que Windows', false),
(108, 'vSphere est uniquement pour les petites entreprises', false),
(109, 'Orchestration automatisée, scaling, service discovery, load balancing, self-healing', true),
(109, 'Kubernetes est uniquement pour Docker', false),
(109, 'L''orchestration n''est utile que pour les gros projets', false),
(110, 'Dockerfile : instructions build, Compose : multi-conteneurs, environnements', true),
(110, 'Dockerfile et Docker Compose sont identiques', false),
(110, 'On ne peut utiliser qu''un seul conteneur avec Docker', false);

-- Réponses pour Sécurité (questions 111-115)
INSERT INTO reponse_question (id_question, choix, valeur) VALUES 
(111, 'Malware, phishing, DDoS, injection SQL, social engineering, ransomware', true),
(111, 'Il n''existe qu''un seul type d''attaque informatique', false),
(111, 'Les attaques ne visent que les grandes entreprises', false),
(112, 'Mises à jour, pare-feu, authentification forte, monitoring, principe moindre privilège', true),
(112, 'Un serveur Linux est sécurisé par défaut', false),
(112, 'La sécurité n''est nécessaire que pour les serveurs web', false),
(113, 'Filtrage trafic réseau, règles entrantes/sortantes, zones de sécurité, monitoring', true),
(113, 'Un firewall bloque automatiquement toutes les menaces', false),
(113, 'Les firewalls ne sont utiles que pour les entreprises', false),
(114, 'Symétrique : même clé, Asymétrique : paire clés publique/privée', true),
(114, 'Tous les algorithmes de cryptographie sont identiques', false),
(114, 'La cryptographie ralentit trop les systèmes', false),
(115, 'Stratégie 3-2-1, chiffrement, tests restauration, rétention, géolocalisation', true),
(115, 'Une seule sauvegarde suffit toujours', false),
(115, 'Les sauvegardes ne nécessitent pas de tests', false);

-- Réponses pour Scripting (questions 116-120)
INSERT INTO reponse_question (id_question, choix, valeur) VALUES 
(116, 'Shebang, boucles, conditions, fonctions, cron jobs, variables', true),
(116, 'Bash ne peut pas automatiser de tâches', false),
(116, 'Les scripts Bash ne fonctionnent que manuellement', false),
(117, 'Bash : Unix/Linux natif, PowerShell : Windows avec objets .NET', true),
(117, 'Bash et PowerShell sont identiques', false),
(117, 'PowerShell ne fonctionne que sur Windows', false),
(118, 'Try/catch, codes de retour, validation entrées, logging, exit codes appropriés', true),
(118, 'Les erreurs dans les scripts ne peuvent pas être gérées', false),
(118, 'Il faut ignorer toutes les erreurs dans les scripts', false),
(119, 'PATH, HOME, USER, variables système et utilisateur, export, persistance', true),
(119, 'Les variables d''environnement ne servent à rien', false),
(119, 'Chaque script doit redéfinir toutes ses variables', false),
(120, 'Cron (Linux), Tâches planifiées (Windows), syntax crontab, monitoring execution', true),
(120, 'Les scripts ne peuvent s''exécuter que manuellement', false),
(120, 'La planification n''est pas fiable', false);

-- Réponses pour Cloud Computing (questions 121-125)
INSERT INTO reponse_question (id_question, choix, valeur) VALUES 
(121, 'IaaS : infrastructure, PaaS : plateforme développement, SaaS : logiciel complet', true),
(121, 'Tous les modèles cloud offrent les mêmes services', false),
(121, 'SaaS nécessite de gérer l''infrastructure', false),
(122, 'Élasticité, coûts variables, accessibilité, maintenance réduite, innovation rapide', true),
(122, 'Le cloud est toujours plus cher', false),
(122, 'Le cloud n''est pas sécurisé', false),
(123, 'Besoins spécifiques, coûts, expertise équipe, intégrations, conformité', true),
(123, 'AWS est toujours le meilleur choix', false),
(123, 'Tous les fournisseurs cloud sont identiques', false),
(124, 'Ajustement automatique capacité selon demande, métriques, seuils, économies', true),
(124, 'L''auto-scaling ne fonctionne qu''avec AWS', false),
(124, 'Il faut toujours maintenir la capacité maximale', false),
(125, 'IAM, chiffrement, réseaux privés, audits, conformité, principe moindre privilège', true),
(125, 'Le cloud est automatiquement sécurisé', false),
(125, 'La sécurité cloud n''est pas de la responsabilité du client', false);

-- Pour les questions de base de données (questions 16-20)
INSERT INTO reponse_question (id_question, choix, valeur) VALUES 
(16, 'Un-à-un, un-à-plusieurs et plusieurs-à-plusieurs sont les types de relations', true),
(16, 'Il n''existe qu''un seul type de relation en base de données', false),
(16, 'Les relations ne sont nécessaires que dans NoSQL', false),
(17, 'SQL est relationnel avec schéma fixe, NoSQL est flexible sans schéma strict', true),
(17, 'SQL et NoSQL sont identiques', false),
(17, 'NoSQL ne peut pas gérer de grandes quantités de données', false),
(18, 'Index, optimisation des jointures, requêtes sélectives et pagination', true),
(18, 'Les requêtes SQL ne peuvent pas être optimisées', false),
(18, 'Il faut éviter les index pour optimiser les performances', false),
(19, 'ACID : Atomicité, Cohérence, Isolation, Durabilité pour les transactions', true),
(19, 'Une transaction ne peut contenir qu''une seule opération', false),
(19, 'Les propriétés ACID ne s''appliquent qu''à Oracle', false),
(20, 'Index clustered, non-clustered, unique, composite selon les besoins', true),
(20, 'Il n''existe qu''un seul type d''index', false),
(20, 'Les index ralentissent toujours les performances', false);

-- ===================================================
-- OFFRES D'EMPLOI
-- ===================================================

INSERT INTO offre (id_poste, id_formation, id_local, mission, nbr_personne, age_min, age_max, date_creation, date_publication, date_fin) VALUES 
-- Développeur Full Stack
(1, 2, 1, 'Développement d''applications web modernes avec React/Node.js pour nos clients fintech. Participation à l''architecture technique et mentorat des juniors.', 2, 23, 35, CURRENT_DATE - INTERVAL '10 days', CURRENT_DATE - INTERVAL '8 days', CURRENT_DATE + INTERVAL '30 days'),

(1, 1, 9, 'Création d''une plateforme e-commerce innovante. Travail en remote avec équipe internationale. Stack moderne : React, TypeScript, Node.js, PostgreSQL.', 1, 25, 40, CURRENT_DATE - INTERVAL '15 days', CURRENT_DATE - INTERVAL '12 days', CURRENT_DATE + INTERVAL '25 days'),

(1, 4, 2, 'Lead Developer pour startup AgriTech. Développement d''IoT et applications mobiles pour l''agriculture. Responsabilités d''architecture et de formation équipe.', 1, 28, 45, CURRENT_DATE - INTERVAL '5 days', CURRENT_DATE - INTERVAL '3 days', CURRENT_DATE + INTERVAL '45 days'),
-- Data Scientist
(2, 6, 1, 'Analyse prédictive pour optimiser la supply chain. Développement de modèles ML pour la prévision de demande et optimisation logistique.', 1, 25, 40, CURRENT_DATE - INTERVAL '12 days', CURRENT_DATE - INTERVAL '10 days', CURRENT_DATE + INTERVAL '35 days'),

(2, 7, 3, 'Data Scientist pour secteur bancaire. Détection de fraudes, scoring crédit et analyse comportementale. Environnement Python/R avec AWS.', 2, 26, 42, CURRENT_DATE - INTERVAL '8 days', CURRENT_DATE - INTERVAL '6 days', CURRENT_DATE + INTERVAL '20 days'),
-- Chef de Projet Digital
(3, 11, 1, 'Pilotage de la transformation digitale d''une grande entreprise manufacturière. Gestion de projets complexes avec budgets multimillionnaires.', 1, 30, 50, CURRENT_DATE - INTERVAL '20 days', CURRENT_DATE - INTERVAL '18 days', CURRENT_DATE + INTERVAL '15 days'),

(3, 13, 4, 'Chef de projet pour développement d''applications mobiles B2B. Management d''équipe agile de 8 personnes. Expertise Scrum/Kanban requise.', 1, 28, 45, CURRENT_DATE - INTERVAL '7 days', CURRENT_DATE - INTERVAL '5 days', CURRENT_DATE + INTERVAL '40 days'),
-- Designer UX/UI
(4, 16, 1, 'Lead UX Designer pour refonte complète de notre plateforme SaaS. Recherche utilisateur, prototypage et design system. Équipe créative de 5 personnes.', 1, 26, 38, CURRENT_DATE - INTERVAL '14 days', CURRENT_DATE - INTERVAL '12 days', CURRENT_DATE + INTERVAL '28 days'),

(4, 17, 9, 'UX/UI Designer senior remote pour agence digitale. Projets variés (e-commerce, applications mobiles). Portfolio créatif et expérience client requis.', 1, 24, 35, CURRENT_DATE - INTERVAL '6 days', CURRENT_DATE - INTERVAL '4 days', CURRENT_DATE + INTERVAL '50 days'),
-- Administrateur Système
(5, 21, 1, 'Admin Système senior pour infrastructure cloud hybride. Migration AWS, sécurisation et automatisation. Environnement DevOps avec Kubernetes.', 1, 28, 45, CURRENT_DATE - INTERVAL '18 days', CURRENT_DATE - INTERVAL '15 days', CURRENT_DATE + INTERVAL '22 days'),

(5, 22, 5, 'Administrateur Système Junior pour PME en croissance. Formation assurée sur technologies modernes. Opportunité d''évolution dans équipe technique dynamique.', 1, 22, 30, CURRENT_DATE - INTERVAL '9 days', CURRENT_DATE - INTERVAL '7 days', CURRENT_DATE + INTERVAL '60 days'),
-- Offres urgentes avec dates de fin proches
(1, 5, 6, 'URGENT - Développeur Full Stack pour mission courte (3 mois). Remplacement congé maternité. Démarrage immédiat sur projet critique e-gouvernement.', 1, 25, 40, CURRENT_DATE - INTERVAL '3 days', CURRENT_DATE - INTERVAL '2 days', CURRENT_DATE + INTERVAL '5 days'),

(2, 8, 7, 'Data Scientist freelance pour audit algorithmes IA. Mission ponctuelle de 2 mois. Expertise éthique IA et explicabilité des modèles requise.', 1, 30, 50, CURRENT_DATE - INTERVAL '4 days', CURRENT_DATE - INTERVAL '2 days', CURRENT_DATE + INTERVAL '8 days');

-- ===================================================
-- VÉRIFICATION DES DONNÉES
-- ===================================================

-- Requêtes de vérification (à exécuter pour contrôler)
-- SELECT COUNT(*) FROM poste; -- Devrait retourner 5
-- SELECT COUNT(*) FROM competance; -- Devrait retourner 25 (5 par poste)
-- SELECT COUNT(*) FROM formation; -- Devrait retourner 25 (5 par poste)
-- SELECT COUNT(*) FROM poste_competance; -- Devrait retourner 25
-- SELECT COUNT(*) FROM poste_formation; -- Devrait retourner 25
-- SELECT COUNT(*) FROM profil; -- Devrait retourner 25 (5 par poste)
-- SELECT COUNT(*) FROM question; -- Devrait retourner 125 (5 par compétence)
-- SELECT COUNT(*) FROM reponse_question; -- Devrait retourner 375 (3 par question)
-- SELECT COUNT(*) FROM offre; -- Devrait retourner 13

-- Vérifier qu'il y a exactement une bonne réponse par question
-- SELECT id_question, COUNT(*) as total, SUM(CASE WHEN valeur = true THEN 1 ELSE 0 END) as bonnes_reponses
-- FROM reponse_question 
-- GROUP BY id_question 
-- HAVING SUM(CASE WHEN valeur = true THEN 1 ELSE 0 END) != 1;
-- -- Cette requête ne devrait retourner aucune ligne

COMMIT;
