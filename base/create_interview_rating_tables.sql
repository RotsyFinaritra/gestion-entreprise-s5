-- Script pour créer les tables de notation des entretiens
-- Exécuter ce script dans votre base de données PostgreSQL après avoir créé les autres tables

-- Table pour les sections de notation par poste
CREATE TABLE IF NOT EXISTS section_note_entretien (
    id_section SERIAL PRIMARY KEY,
    id_poste BIGINT NOT NULL,
    nom_section VARCHAR(100) NOT NULL,
    description TEXT,
    note_max INTEGER NOT NULL DEFAULT 20,
    ordre_affichage INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Clé étrangère
    CONSTRAINT fk_section_note_poste FOREIGN KEY (id_poste) 
        REFERENCES poste (id_poste) ON DELETE CASCADE,
    
    -- Contraintes
    CONSTRAINT check_note_max_positive CHECK (note_max > 0),
    CONSTRAINT check_ordre_positive CHECK (ordre_affichage > 0),
    
    -- Index unique pour éviter les doublons
    CONSTRAINT uk_section_poste_nom UNIQUE (id_poste, nom_section)
);

-- Table pour les notes d'entretien par section
CREATE TABLE IF NOT EXISTS note_entretien (
    id_note SERIAL PRIMARY KEY,
    id_entretien BIGINT NOT NULL,
    id_section BIGINT NOT NULL,
    note_obtenue DECIMAL(5,2) NOT NULL,
    commentaire TEXT,
    date_evaluation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Clés étrangères
    CONSTRAINT fk_note_entretien FOREIGN KEY (id_entretien) 
        REFERENCES entretien (id_entretien) ON DELETE CASCADE,
    CONSTRAINT fk_note_section FOREIGN KEY (id_section) 
        REFERENCES section_note_entretien (id_section) ON DELETE CASCADE,
    
    -- Contraintes
    CONSTRAINT check_note_positive CHECK (note_obtenue >= 0),
    
    -- Index unique pour éviter les doublons
    CONSTRAINT uk_entretien_section UNIQUE (id_entretien, id_section)
);

-- Commentaires sur les colonnes
COMMENT ON TABLE section_note_entretien IS 'Sections d''évaluation configurables par poste';
COMMENT ON COLUMN section_note_entretien.nom_section IS 'Nom de la section (ex: Compétences techniques, Savoir-être, etc.)';
COMMENT ON COLUMN section_note_entretien.description IS 'Description détaillée de ce qui est évalué dans cette section';
COMMENT ON COLUMN section_note_entretien.note_max IS 'Note maximale possible pour cette section';
COMMENT ON COLUMN section_note_entretien.ordre_affichage IS 'Ordre d''affichage des sections dans le formulaire';

COMMENT ON TABLE note_entretien IS 'Notes attribuées lors des entretiens par section';
COMMENT ON COLUMN note_entretien.note_obtenue IS 'Note obtenue par le candidat pour cette section';
COMMENT ON COLUMN note_entretien.commentaire IS 'Commentaire détaillé de l''évaluateur';
COMMENT ON COLUMN note_entretien.date_evaluation IS 'Date et heure de l''évaluation';

-- Index pour améliorer les performances
CREATE INDEX IF NOT EXISTS idx_section_note_poste ON section_note_entretien (id_poste);
CREATE INDEX IF NOT EXISTS idx_section_note_ordre ON section_note_entretien (id_poste, ordre_affichage);
CREATE INDEX IF NOT EXISTS idx_note_entretien ON note_entretien (id_entretien);
CREATE INDEX IF NOT EXISTS idx_note_section ON note_entretien (id_section);
CREATE INDEX IF NOT EXISTS idx_note_date ON note_entretien (date_evaluation);

-- Données de test (optionnel - décommentez si vous voulez des exemples)
/*
-- Exemples de sections de notation pour différents postes
-- Ajustez les IDs selon votre base de données

-- Sections pour développeur (supposons id_poste = 1)
INSERT INTO section_note_entretien (id_poste, nom_section, description, note_max, ordre_affichage) VALUES 
(1, 'Compétences Techniques', 'Évaluation des connaissances en programmation et technologies', 20, 1),
(1, 'Résolution de Problèmes', 'Capacité à analyser et résoudre des problèmes complexes', 20, 2),
(1, 'Communication', 'Clarté d''expression et capacité d''écoute', 20, 3),
(1, 'Motivation', 'Intérêt pour le poste et l''entreprise', 20, 4),
(1, 'Savoir-être', 'Attitude professionnelle et capacité de travail en équipe', 20, 5);

-- Sections pour commercial (supposons id_poste = 2)
INSERT INTO section_note_entretien (id_poste, nom_section, description, note_max, ordre_affichage) VALUES 
(2, 'Expérience Commerciale', 'Expérience et réussites dans le domaine commercial', 20, 1),
(2, 'Techniques de Vente', 'Maîtrise des méthodes et outils de vente', 20, 2),
(2, 'Relation Client', 'Capacité à établir et maintenir des relations clients', 20, 3),
(2, 'Présentation', 'Capacité de présentation et charisme', 20, 4),
(2, 'Objectifs', 'Orientation résultats et gestion des objectifs', 20, 5);

-- Sections génériques (supposons id_poste = 3)
INSERT INTO section_note_entretien (id_poste, nom_section, description, note_max, ordre_affichage) VALUES 
(3, 'Compétences Métier', 'Maîtrise des compétences spécifiques au poste', 20, 1),
(3, 'Expérience', 'Adéquation de l''expérience avec le poste', 20, 2),
(3, 'Communication', 'Qualité de la communication verbale et non verbale', 20, 3),
(3, 'Motivation', 'Motivation pour le poste et l''entreprise', 20, 4);
*/

-- Trigger pour mettre à jour automatiquement updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_section_note_entretien_updated_at 
    BEFORE UPDATE ON section_note_entretien 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_note_entretien_updated_at 
    BEFORE UPDATE ON note_entretien 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Vue pour faciliter les requêtes avec moyennes
CREATE OR REPLACE VIEW v_entretien_notes AS
SELECT 
    e.id_entretien,
    e.id_candidat,
    e.id_offre,
    c.prenom,
    c.nom,
    c.email,
    o.mission,
    p.nom as poste_nom,
    e.date_heure_entretien,
    e.statut,
    COALESCE(AVG(ne.note_obtenue), 0) as moyenne_generale,
    COUNT(ne.id_note) as nombre_notes,
    MAX(ne.date_evaluation) as derniere_evaluation
FROM entretien e
    LEFT JOIN candidat c ON e.id_candidat = c.id_candidat
    LEFT JOIN offre o ON e.id_offre = o.id_offre
    LEFT JOIN poste p ON o.id_poste = p.id_poste
    LEFT JOIN note_entretien ne ON e.id_entretien = ne.id_entretien
GROUP BY e.id_entretien, e.id_candidat, e.id_offre, c.prenom, c.nom, c.email, 
         o.mission, p.nom, e.date_heure_entretien, e.statut;

COMMENT ON VIEW v_entretien_notes IS 'Vue simplifiée pour afficher les entretiens avec leurs moyennes de notes';

-- Afficher le résultat de la création
SELECT 'Tables de notation des entretiens créées avec succès!' as message;
