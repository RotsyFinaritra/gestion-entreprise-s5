-- Script de migration pour ajouter les champs âge min/max et nombre de personnes
-- dans la table demande_offre

\c entreprise;

-- Ajouter les nouvelles colonnes à la table demande_offre
ALTER TABLE demande_offre 
ADD COLUMN IF NOT EXISTS age_min INTEGER,
ADD COLUMN IF NOT EXISTS age_max INTEGER,
ADD COLUMN IF NOT EXISTS nbr_personne INTEGER;

-- Ajouter des commentaires pour documenter les colonnes
COMMENT ON COLUMN demande_offre.age_min IS 'Âge minimum requis pour le poste';
COMMENT ON COLUMN demande_offre.age_max IS 'Âge maximum requis pour le poste';
COMMENT ON COLUMN demande_offre.nbr_personne IS 'Nombre de personnes à recruter pour ce poste';

-- Vérifier que les colonnes ont été ajoutées
SELECT column_name, data_type, is_nullable 
FROM information_schema.columns 
WHERE table_name = 'demande_offre' 
AND column_name IN ('age_min', 'age_max', 'nbr_personne')
ORDER BY column_name;

PRINT 'Migration des nouvelles colonnes de demande_offre terminée avec succès!';