-- Script SQL pour ajouter la relation département aux compétences et formations
-- Date: 23 septembre 2025

-- Ajouter la colonne departement_id à la table competance
ALTER TABLE competance ADD COLUMN IF NOT EXISTS departement_id BIGINT;

-- Ajouter la contrainte de clé étrangère pour competance
ALTER TABLE competance 
ADD CONSTRAINT IF NOT EXISTS fk_competance_departement 
FOREIGN KEY (departement_id) REFERENCES "user"(id_user);

-- Ajouter la colonne departement_id à la table formation
ALTER TABLE formation ADD COLUMN IF NOT EXISTS departement_id BIGINT;

-- Ajouter la contrainte de clé étrangère pour formation
ALTER TABLE formation 
ADD CONSTRAINT IF NOT EXISTS fk_formation_departement 
FOREIGN KEY (departement_id) REFERENCES "user"(id_user);

-- Optionnel : Assigner toutes les compétences existantes à un département par défaut
-- (Remplacez 1 par l'ID du département souhaité ou commentez cette ligne)
-- UPDATE competance SET departement_id = 1 WHERE departement_id IS NULL;

-- Optionnel : Assigner toutes les formations existantes à un département par défaut  
-- (Remplacez 1 par l'ID du département souhaité ou commentez cette ligne)
-- UPDATE formation SET departement_id = 1 WHERE departement_id IS NULL;

-- Créer un index pour améliorer les performances des requêtes par département
CREATE INDEX IF NOT EXISTS idx_competance_departement ON competance(departement_id);
CREATE INDEX IF NOT EXISTS idx_formation_departement ON formation(departement_id);

-- Afficher les informations sur les changements
SELECT 'Modifications apportées:' as info;
SELECT 'Colonne departement_id ajoutée à la table competance' as modification;
SELECT 'Colonne departement_id ajoutée à la table formation' as modification;
SELECT 'Contraintes de clés étrangères créées' as modification;
SELECT 'Index créés pour améliorer les performances' as modification;
