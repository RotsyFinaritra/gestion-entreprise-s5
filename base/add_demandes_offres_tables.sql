-- Script de migration pour ajouter les entités de gestion des demandes d'offres
-- À exécuter sur la base de données PostgreSQL

-- 1. Création de la table statut_demande
CREATE TABLE IF NOT EXISTS statut_demande (
    id_statut_demande SERIAL PRIMARY KEY,
    libelle VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    couleur VARCHAR(20),
    ordre_affichage INTEGER,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    actif BOOLEAN DEFAULT TRUE
);

-- 2. Création de la table demande_offre
CREATE TABLE IF NOT EXISTS demande_offre (
    id_demande_offre SERIAL PRIMARY KEY,
    id_poste INTEGER NOT NULL,
    id_departement INTEGER NOT NULL,
    id_statut_demande INTEGER NOT NULL,
    id_user_traitement INTEGER,
    
    titre_offre VARCHAR(255) NOT NULL,
    description_poste TEXT,
    description_entreprise TEXT,
    type_contrat VARCHAR(50),
    niveau_experience VARCHAR(100),
    salaire_min DECIMAL(10,2),
    salaire_max DECIMAL(10,2),
    localisation VARCHAR(255),
    age_min INTEGER,
    age_max INTEGER,
    nbr_personne INTEGER,
    avantages TEXT,
    date_limite_candidature TIMESTAMP,
    
    priorite VARCHAR(20) DEFAULT 'NORMALE',
    justification TEXT,
    commentaire_departement TEXT,
    commentaire_rh TEXT,
    
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_traitement TIMESTAMP,
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_demande_offre_poste FOREIGN KEY (id_poste) REFERENCES poste(id_poste),
    CONSTRAINT fk_demande_offre_departement FOREIGN KEY (id_departement) REFERENCES users(id_user),
    CONSTRAINT fk_demande_offre_statut FOREIGN KEY (id_statut_demande) REFERENCES statut_demande(id_statut_demande),
    CONSTRAINT fk_demande_offre_user_traitement FOREIGN KEY (id_user_traitement) REFERENCES users(id_user)
);

-- 3. Création de la table statut_offre pour l'historique
CREATE TABLE IF NOT EXISTS statut_offre (
    id_statut_offre SERIAL PRIMARY KEY,
    id_offre INTEGER NOT NULL,
    id_statut_demande INTEGER NOT NULL,
    id_user_modification INTEGER,
    
    date_changement TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    commentaire TEXT,
    motif_changement VARCHAR(255),
    ancien_statut VARCHAR(50),
    nouveau_statut VARCHAR(50),
    
    CONSTRAINT fk_statut_offre_offre FOREIGN KEY (id_offre) REFERENCES offre(id_offre),
    CONSTRAINT fk_statut_offre_statut FOREIGN KEY (id_statut_demande) REFERENCES statut_demande(id_statut_demande),
    CONSTRAINT fk_statut_offre_user FOREIGN KEY (id_user_modification) REFERENCES users(id_user)
);

-- 4. Ajout de la colonne id_demande_offre dans la table offre (relation optionnelle)
ALTER TABLE offre ADD COLUMN IF NOT EXISTS id_demande_offre INTEGER;
ALTER TABLE offre ADD CONSTRAINT  fk_offre_demande_offre FOREIGN KEY (id_demande_offre) REFERENCES demande_offre(id_demande_offre);

-- 5. Insertion des statuts par défaut
INSERT INTO statut_demande (libelle, description, couleur, ordre_affichage) VALUES
    ('EN_ATTENTE', 'Demande en attente de traitement par les RH', 'warning', 1),
    ('ACCEPTE', 'Demande acceptée et offre publiée', 'success', 2),
    ('REFUSE', 'Demande refusée par les RH', 'danger', 3)
ON CONFLICT (libelle) DO NOTHING;

-- 6. Création d'index pour optimiser les performances
CREATE INDEX IF NOT EXISTS idx_demande_offre_departement ON demande_offre(id_departement);
CREATE INDEX IF NOT EXISTS idx_demande_offre_statut ON demande_offre(id_statut_demande);
CREATE INDEX IF NOT EXISTS idx_demande_offre_poste ON demande_offre(id_poste);
CREATE INDEX IF NOT EXISTS idx_demande_offre_date_creation ON demande_offre(date_creation);
CREATE INDEX IF NOT EXISTS idx_demande_offre_priorite ON demande_offre(priorite);

CREATE INDEX IF NOT EXISTS idx_statut_offre_offre ON statut_offre(id_offre);
CREATE INDEX IF NOT EXISTS idx_statut_offre_date ON statut_offre(date_changement);

CREATE INDEX IF NOT EXISTS idx_statut_demande_libelle ON statut_demande(libelle);
CREATE INDEX IF NOT EXISTS idx_statut_demande_actif ON statut_demande(actif);

-- 7. Ajout de contraintes de validation
ALTER TABLE demande_offre ADD CONSTRAINT chk_priorite 
    CHECK (priorite IN ('BASSE', 'NORMALE', 'HAUTE', 'URGENTE'));

ALTER TABLE demande_offre ADD CONSTRAINT  chk_type_contrat 
    CHECK (type_contrat IN ('CDI', 'CDD', 'STAGE', 'ALTERNANCE', 'FREELANCE') OR type_contrat IS NULL);

ALTER TABLE demande_offre ADD CONSTRAINT chk_niveau_experience 
    CHECK (niveau_experience IN ('DEBUTANT', 'JUNIOR', 'SENIOR', 'EXPERT') OR niveau_experience IS NULL);

ALTER TABLE demande_offre ADD CONSTRAINT chk_salaire_coherence 
    CHECK (salaire_min IS NULL OR salaire_max IS NULL OR salaire_min <= salaire_max);

-- 8. Fonction pour mettre à jour automatiquement date_modification
CREATE OR REPLACE FUNCTION update_date_modification()
RETURNS TRIGGER AS $$
BEGIN
    NEW.date_modification = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- 9. Trigger pour la mise à jour automatique de date_modification
DROP TRIGGER IF EXISTS tr_demande_offre_update_date_modification ON demande_offre;
CREATE TRIGGER tr_demande_offre_update_date_modification
    BEFORE UPDATE ON demande_offre
    FOR EACH ROW
    EXECUTE FUNCTION update_date_modification();

-- 10. Commentaires sur les tables
COMMENT ON TABLE statut_demande IS 'Table des statuts possibles pour les demandes d''offres (EN_ATTENTE, ACCEPTE, REFUSE)';
COMMENT ON TABLE demande_offre IS 'Table des demandes d''offres envoyées par les départements aux RH';
COMMENT ON TABLE statut_offre IS 'Table d''historique des changements de statut des offres publiées';

COMMENT ON COLUMN demande_offre.priorite IS 'Priorité de la demande: BASSE, NORMALE, HAUTE, URGENTE';
COMMENT ON COLUMN demande_offre.type_contrat IS 'Type de contrat proposé: CDI, CDD, STAGE, ALTERNANCE, FREELANCE';
COMMENT ON COLUMN demande_offre.niveau_experience IS 'Niveau d''expérience requis: DEBUTANT, JUNIOR, SENIOR, EXPERT';
COMMENT ON COLUMN demande_offre.justification IS 'Justification du besoin de recrutement par le département';

-- 11. Vues utiles pour les rapports
CREATE OR REPLACE VIEW v_demandes_en_attente AS
SELECT 
    d.id_demande_offre,
    d.titre_offre,
    p.nom as nom_poste,
    u.nom_departement,
    d.priorite,
    d.date_creation,
    d.justification
FROM demande_offre d
JOIN poste p ON d.id_poste = p.id_poste
JOIN users u ON d.id_departement = u.id_user
JOIN statut_demande sd ON d.id_statut_demande = sd.id_statut_demande
WHERE sd.libelle = 'EN_ATTENTE'
ORDER BY 
    CASE d.priorite 
        WHEN 'URGENTE' THEN 1
        WHEN 'HAUTE' THEN 2
        WHEN 'NORMALE' THEN 3
        WHEN 'BASSE' THEN 4
    END,
    d.date_creation ASC;

CREATE OR REPLACE VIEW v_statistiques_demandes AS
SELECT 
    u.nom_departement,
    COUNT(*) as total_demandes,
    COUNT(CASE WHEN sd.libelle = 'EN_ATTENTE' THEN 1 END) as en_attente,
    COUNT(CASE WHEN sd.libelle = 'ACCEPTE' THEN 1 END) as acceptees,
    COUNT(CASE WHEN sd.libelle = 'REFUSE' THEN 1 END) as refusees
FROM demande_offre d
JOIN users u ON d.id_departement = u.id_user
JOIN statut_demande sd ON d.id_statut_demande = sd.id_statut_demande
GROUP BY u.id_user, u.nom_departement
ORDER BY total_demandes DESC;

-- Message de confirmation
SELECT 'Migration des tables de gestion des demandes d''offres terminée avec succès!' as message;