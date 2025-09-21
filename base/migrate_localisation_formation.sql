-- Migration de demande_offre : remplacer localisation par id_local et ajouter id_formation
-- Date: 21 septembre 2025

-- Étape 1: Ajouter les nouvelles colonnes
ALTER TABLE demande_offre 
ADD COLUMN id_local INTEGER,
ADD COLUMN id_formation INTEGER;

-- Étape 2: Ajouter les contraintes de clés étrangères
ALTER TABLE demande_offre 
ADD CONSTRAINT fk_demande_offre_local 
    FOREIGN KEY (id_local) REFERENCES local(id_local),
ADD CONSTRAINT fk_demande_offre_formation 
    FOREIGN KEY (id_formation) REFERENCES formation(id_formation);

-- Étape 3: Migration des données existantes (si nécessaire)
-- Essayer de mapper les localisations textuelles vers les locaux existants
UPDATE demande_offre 
SET id_local = (
    SELECT l.id_local 
    FROM local l 
    WHERE LOWER(l.nom) = LOWER(demande_offre.localisation)
    LIMIT 1
)
WHERE localisation IS NOT NULL 
AND EXISTS (
    SELECT 1 
    FROM local l 
    WHERE LOWER(l.nom) = LOWER(demande_offre.localisation)
);

-- Étape 4: Supprimer l'ancienne colonne localisation
ALTER TABLE demande_offre DROP COLUMN localisation;

-- Vérification des données
SELECT 
    d.id_demande_offre,
    d.titre_offre,
    l.nom as nom_local,
    f.nom as nom_formation
FROM demande_offre d
LEFT JOIN local l ON d.id_local = l.id_local
LEFT JOIN formation f ON d.id_formation = f.id_formation
ORDER BY d.id_demande_offre;

-- Afficher les contraintes créées
SELECT 
    conname as constraint_name,
    contype as constraint_type
FROM pg_constraint 
WHERE conrelid = 'demande_offre'::regclass 
AND conname LIKE 'fk_demande_offre_%';