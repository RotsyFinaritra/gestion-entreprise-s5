-- Script de test pour vérifier le système de notation des entretiens
-- Insérer des données de test après avoir créé les tables principales

-- Assurez-vous d'avoir au moins un poste existant
-- Exemple : INSERT INTO poste (nom) VALUES ('Développeur Java') ON CONFLICT DO NOTHING;

-- Insérer des sections de test pour un poste (ajustez l'id_poste selon votre base)
INSERT INTO section_note_entretien (id_poste, nom_section, description, note_max, ordre_affichage) 
VALUES 
    (1, 'Compétences Techniques', 'Évaluation des connaissances en programmation et technologies requises', 20, 1),
    (1, 'Résolution de Problèmes', 'Capacité à analyser et résoudre des problèmes complexes', 20, 2),
    (1, 'Communication', 'Clarté d''expression et capacité d''écoute', 20, 3),
    (1, 'Motivation et Savoir-être', 'Intérêt pour le poste et attitude professionnelle', 20, 4)
ON CONFLICT (id_poste, nom_section) DO NOTHING;

-- Vérifier les données insérées
SELECT 
    p.nom as poste_nom,
    sne.nom_section,
    sne.description,
    sne.note_max,
    sne.ordre_affichage
FROM section_note_entretien sne
JOIN poste p ON p.id_poste = sne.id_poste
ORDER BY p.nom, sne.ordre_affichage;

-- Afficher les statistiques
SELECT 
    'Tables créées avec succès!' as message,
    COUNT(*) as nombre_sections
FROM section_note_entretien;
