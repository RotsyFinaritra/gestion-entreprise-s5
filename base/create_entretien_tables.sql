-- Création des tables pour les entretiens et notes d'entretien
-- Date: 21 septembre 2025

-- Table section_note_entretien
CREATE TABLE IF NOT EXISTS section_note_entretien (
    id_section SERIAL PRIMARY KEY,
    id_poste INTEGER NOT NULL,
    nom_section VARCHAR(255) NOT NULL,
    description TEXT,
    note_max INTEGER NOT NULL,
    ordre_affichage INTEGER,
    CONSTRAINT fk_section_note_entretien_poste 
        FOREIGN KEY (id_poste) REFERENCES poste(id_poste)
);

-- Table note_entretien (si elle n'existe pas déjà)
CREATE TABLE IF NOT EXISTS note_entretien (
    id_note SERIAL PRIMARY KEY,
    id_entretien INTEGER NOT NULL,
    id_section INTEGER NOT NULL,
    note_obtenue DOUBLE PRECISION,
    commentaire TEXT,
    date_evaluation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_note_entretien_entretien 
        FOREIGN KEY (id_entretien) REFERENCES entretien(id_entretien),
    CONSTRAINT fk_note_entretien_section 
        FOREIGN KEY (id_section) REFERENCES section_note_entretien(id_section)
);

-- Index pour améliorer les performances
CREATE INDEX IF NOT EXISTS idx_section_note_entretien_poste ON section_note_entretien(id_poste);
CREATE INDEX IF NOT EXISTS idx_section_note_entretien_ordre ON section_note_entretien(ordre_affichage);
CREATE INDEX IF NOT EXISTS idx_note_entretien_entretien ON note_entretien(id_entretien);
CREATE INDEX IF NOT EXISTS idx_note_entretien_section ON note_entretien(id_section);

-- Données de test pour section_note_entretien
INSERT INTO section_note_entretien (id_poste, nom_section, description, note_max, ordre_affichage) VALUES
(1, 'Compétences techniques', 'Évaluation des compétences techniques du candidat', 20, 1),
(1, 'Communication', 'Capacité de communication et expression', 10, 2),
(1, 'Motivation', 'Motivation pour le poste et l''entreprise', 10, 3),
(1, 'Expérience professionnelle', 'Adéquation de l''expérience avec le poste', 15, 4)
ON CONFLICT DO NOTHING;

-- Vérification des tables créées
SELECT 'Tables créées:' as info;
\dt+ section_note_entretien;
\dt+ note_entretien;

-- Vérification des données
SELECT 
    s.id_section,
    s.nom_section,
    s.note_max,
    s.ordre_affichage,
    p.nom as nom_poste
FROM section_note_entretien s
LEFT JOIN poste p ON s.id_poste = p.id_poste
ORDER BY s.ordre_affichage;