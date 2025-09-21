-- Migration pour ajouter la gestion des départements
-- Transformation de User en entité département + liaison avec Poste

-- 1. Ajouter les colonnes département à la table users
ALTER TABLE users 
ADD COLUMN nom_departement VARCHAR(100),
ADD COLUMN description_departement TEXT,
ADD COLUMN email_departement VARCHAR(100);

-- 2. Ajouter la colonne département dans la table poste
ALTER TABLE poste 
ADD COLUMN id_departement BIGINT;

-- 3. Ajouter la contrainte de clé étrangère
ALTER TABLE poste 
ADD CONSTRAINT fk_poste_departement 
FOREIGN KEY (id_departement) REFERENCES users(id_user);

-- 4. Créer un index pour optimiser les requêtes
CREATE INDEX idx_poste_departement ON poste(id_departement);

-- 5. Insérer des données de test pour les départements
INSERT INTO users (username, password, role, nom_departement, description_departement, email_departement) VALUES
('dept_informatique', '$2a$10$hashedpassword1', 'DEPARTEMENT', 'Département Informatique', 'Responsable du développement des systèmes d''information et des nouvelles technologies', 'informatique@entreprise.com'),
('dept_rh', '$2a$10$hashedpassword2', 'DEPARTEMENT', 'Département Ressources Humaines', 'Gestion du personnel, recrutement et développement des compétences', 'rh@entreprise.com'),
('dept_marketing', '$2a$10$hashedpassword3', 'DEPARTEMENT', 'Département Marketing', 'Stratégie marketing, communication et développement commercial', 'marketing@entreprise.com'),
('dept_finance', '$2a$10$hashedpassword4', 'DEPARTEMENT', 'Département Finance', 'Gestion financière, comptabilité et contrôle de gestion', 'finance@entreprise.com'),
('dept_operations', '$2a$10$hashedpassword5', 'DEPARTEMENT', 'Département Opérations', 'Gestion des opérations, logistique et production', 'operations@entreprise.com');

-- 6. Associer les postes existants aux départements
UPDATE poste SET id_departement = (SELECT id_user FROM users WHERE username = 'dept_informatique') 
WHERE nom IN ('Développeur Full Stack', 'Administrateur Système');

UPDATE poste SET id_departement = (SELECT id_user FROM users WHERE username = 'dept_informatique') 
WHERE nom = 'Data Scientist';

UPDATE poste SET id_departement = (SELECT id_user FROM users WHERE username = 'dept_rh') 
WHERE nom = 'Chef de Projet Digital';

UPDATE poste SET id_departement = (SELECT id_user FROM users WHERE username = 'dept_marketing') 
WHERE nom = 'Designer UX/UI';

-- 7. Mettre à jour les offres existantes si nécessaire
-- Les offres gardent leur référence au poste, qui maintenant a un département

-- 8. Créer un utilisateur admin RH pour tester
INSERT INTO users (username, password, role, nom_departement, description_departement, email_departement) VALUES
('admin_rh', '$2a$10$hashedpassword_admin', 'RH', 'Service RH Central', 'Administration centrale des ressources humaines', 'admin.rh@entreprise.com');

COMMIT;