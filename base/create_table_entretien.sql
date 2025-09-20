-- Script pour créer la table entretien
-- Exécuter ce script dans votre base de données PostgreSQL

CREATE TABLE IF NOT EXISTS entretien (
    id_entretien SERIAL PRIMARY KEY,
    id_candidat BIGINT NOT NULL,
    id_offre BIGINT NOT NULL,
    date_envoi_mail DATE NOT NULL,
    date_heure_entretien TIMESTAMP NOT NULL,
    duree_entretien INTEGER NOT NULL, -- en minutes
    statut VARCHAR(50) NOT NULL DEFAULT 'programmé', -- programmé, confirmé, terminé, annulé
    lieu_entretien VARCHAR(255),
    commentaire TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Clés étrangères
    CONSTRAINT fk_entretien_candidat FOREIGN KEY (id_candidat) 
        REFERENCES candidat (id_candidat) ON DELETE CASCADE,
    CONSTRAINT fk_entretien_offre FOREIGN KEY (id_offre) 
        REFERENCES offre (id_offre) ON DELETE CASCADE,
    
    -- Contraintes
    CONSTRAINT chk_statut CHECK (statut IN ('programmé', 'confirmé', 'terminé', 'annulé')),
    CONSTRAINT chk_duree CHECK (duree_entretien > 0 AND duree_entretien <= 480) -- max 8h
);

-- Index pour améliorer les performances
CREATE INDEX IF NOT EXISTS idx_entretien_candidat ON entretien(id_candidat);
CREATE INDEX IF NOT EXISTS idx_entretien_offre ON entretien(id_offre);
CREATE INDEX IF NOT EXISTS idx_entretien_date ON entretien(date_heure_entretien);
CREATE INDEX IF NOT EXISTS idx_entretien_statut ON entretien(statut);

-- Trigger pour mettre à jour updated_at automatiquement
CREATE OR REPLACE FUNCTION update_entretien_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_entretien_updated_at
    BEFORE UPDATE ON entretien
    FOR EACH ROW
    EXECUTE FUNCTION update_entretien_updated_at();

-- Commentaires pour la documentation
COMMENT ON TABLE entretien IS 'Table des entretiens planifiés pour les candidats admis';
COMMENT ON COLUMN entretien.id_entretien IS 'Identifiant unique de l''entretien';
COMMENT ON COLUMN entretien.id_candidat IS 'Référence vers le candidat';
COMMENT ON COLUMN entretien.id_offre IS 'Référence vers l''offre d''emploi';
COMMENT ON COLUMN entretien.date_envoi_mail IS 'Date d''envoi de l''invitation par email';
COMMENT ON COLUMN entretien.date_heure_entretien IS 'Date et heure programmées de l''entretien';
COMMENT ON COLUMN entretien.duree_entretien IS 'Durée prévue de l''entretien en minutes';
COMMENT ON COLUMN entretien.statut IS 'Statut de l''entretien: programmé, confirmé, terminé, annulé';
COMMENT ON COLUMN entretien.lieu_entretien IS 'Lieu où se déroulera l''entretien';
COMMENT ON COLUMN entretien.commentaire IS 'Commentaires additionnels ou notes';

-- Exemples de données de test (optionnel)
-- Décommentez les lignes suivantes si vous voulez des données de test

/*
-- Insérer quelques exemples d'entretiens (ajustez les IDs selon votre base)
INSERT INTO entretien (id_candidat, id_offre, date_envoi_mail, date_heure_entretien, duree_entretien, statut, lieu_entretien, commentaire)
VALUES 
    (1, 1, CURRENT_DATE, CURRENT_DATE + INTERVAL '3 days' + TIME '09:00:00', 45, 'programmé', 'Salle de réunion A', 'Premier entretien technique'),
    (2, 1, CURRENT_DATE, CURRENT_DATE + INTERVAL '3 days' + TIME '10:00:00', 45, 'programmé', 'Salle de réunion A', 'Entretien RH'),
    (3, 1, CURRENT_DATE, CURRENT_DATE + INTERVAL '4 days' + TIME '14:00:00', 60, 'confirmé', 'Bureau du manager', 'Entretien avec le manager direct');
*/
