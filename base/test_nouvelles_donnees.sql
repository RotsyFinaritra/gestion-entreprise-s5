-- Test des nouveaux champs age_min, age_max, nbr_personne dans demande_offre

-- Mise à jour de quelques demandes existantes avec les nouveaux champs
UPDATE demande_offre 
SET age_min = 25, age_max = 35, nbr_personne = 3 
WHERE id_demande_offre = 1;

UPDATE demande_offre 
SET age_min = 30, age_max = 45, nbr_personne = 2 
WHERE id_demande_offre = 2;

UPDATE demande_offre 
SET age_min = 22, age_max = 30, nbr_personne = 5 
WHERE id_demande_offre = 3;

-- Insertion d'une nouvelle demande avec tous les champs
INSERT INTO demande_offre (
    titre_offre, 
    description_poste, 
    justification, 
    priorite, 
    type_contrat, 
    salaire_min, 
    salaire_max, 
    niveau_experience, 
    localisation,
    age_min,
    age_max,
    nbr_personne,
    avantages, 
    description_entreprise, 
    commentaire_departement,
    date_creation, 
    date_limite_candidature, 
    id_poste, 
    id_departement, 
    id_statut_demande
) VALUES (
    'Développeur Full Stack Senior', 
    'Développement d''applications web modernes avec React et Spring Boot', 
    'Expansion de l''équipe technique pour de nouveaux projets', 
    'HAUTE', 
    'CDI', 
    45000, 
    65000, 
    'SENIOR', 
    'Antananarivo',
    28,
    40,
    2,
    'Télétravail partiel, formations continues, prime de performance', 
    'Entreprise leader dans le développement logiciel', 
    'Poste urgent - équipe en sous-effectif',
    CURRENT_TIMESTAMP, 
    CURRENT_TIMESTAMP + INTERVAL '30 days', 
    1, 
    1, 
    1
);

-- Vérification des données
SELECT 
    id_demande_offre,
    titre_offre,
    age_min,
    age_max,
    nbr_personne,
    localisation,
    priorite
FROM demande_offre 
WHERE age_min IS NOT NULL OR age_max IS NOT NULL OR nbr_personne IS NOT NULL
ORDER BY id_demande_offre;