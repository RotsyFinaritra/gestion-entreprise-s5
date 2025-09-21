-- Ajout de données de test pour les locaux et formations
-- Date: 21 septembre 2025

-- Ajout de locaux de test
INSERT INTO local (nom) VALUES 
('Antananarivo'),
('Fianarantsoa'),
('Mahajanga'),
('Toamasina'),
('Antsirabe'),
('Télétravail')
ON CONFLICT (nom) DO NOTHING;

-- Ajout de formations de test
INSERT INTO formation (nom) VALUES 
('Informatique'),
('Gestion'),
('Marketing'),
('Comptabilité'),
('Ressources Humaines'),
('Ingénierie'),
('Commerce'),
('Finance'),
('Droit'),
('Communication')
ON CONFLICT (nom) DO NOTHING;

-- Vérification des données ajoutées
SELECT 'Locaux disponibles:' as type, id_local as id, nom FROM local
UNION ALL
SELECT 'Formations disponibles:' as type, id_formation as id, nom FROM formation
ORDER BY type, id;