-- Ajouter les colonnes département aux tables competance et formation
ALTER TABLE competance ADD COLUMN id_departement BIGINT;
ALTER TABLE formation ADD COLUMN id_departement BIGINT;

-- Ajouter les contraintes de clé étrangère  
ALTER TABLE competance ADD CONSTRAINT fk_competance_departement FOREIGN KEY (id_departement) REFERENCES users(id_user);
ALTER TABLE formation ADD CONSTRAINT fk_formation_departement FOREIGN KEY (id_departement) REFERENCES users(id_user);

-- Créer les index pour optimiser les requêtes
CREATE INDEX idx_competance_departement ON competance(id_departement);
CREATE INDEX idx_formation_departement ON formation(id_departement);
