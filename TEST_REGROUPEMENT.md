# Test du regroupement des compétences et formations par département

## Instructions de test

### Prérequis
1. L'application doit être démarrée (`mvn spring-boot:run`)
2. Exécuter le script SQL de migration si nécessaire :
```sql
-- Dans PostgreSQL
ALTER TABLE competance ADD COLUMN IF NOT EXISTS departement_id BIGINT;
ALTER TABLE formation ADD COLUMN IF NOT EXISTS departement_id BIGINT;

-- Ajouter les contraintes FK
ALTER TABLE competance 
ADD CONSTRAINT IF NOT EXISTS fk_competance_departement 
FOREIGN KEY (departement_id) REFERENCES "user"(id_user);

ALTER TABLE formation 
ADD CONSTRAINT IF NOT EXISTS fk_formation_departement 
FOREIGN KEY (departement_id) REFERENCES "user"(id_user);
```

### Tests à effectuer

#### 1. Test d'accès département
- [ ] Se connecter en tant que département
- [ ] Accéder à `/departement/competences`
- [ ] Vérifier que le message indique : "Ces compétences sont spécifiques à votre département : [NOM_DEPT]"
- [ ] Créer une nouvelle compétence
- [ ] Vérifier qu'elle est bien associée au département

#### 2. Test de filtrage
- [ ] Créer des compétences avec 2 départements différents
- [ ] Se connecter avec le département A
- [ ] Vérifier qu'on ne voit que ses compétences
- [ ] Se connecter avec le département B
- [ ] Vérifier qu'on ne voit que ses compétences

#### 3. Test formations
- [ ] Répéter les mêmes tests pour `/departement/formations`

#### 4. Test création de postes
- [ ] Se connecter en tant que département
- [ ] Accéder à `/departement/postes/nouveau`
- [ ] Vérifier que seules les compétences/formations du département apparaissent

#### 5. Test admin (doit continuer à fonctionner)
- [ ] Se connecter en tant qu'admin
- [ ] Accéder aux pages de gestion des postes
- [ ] Vérifier qu'on voit toutes les compétences/formations

### Résultats attendus

✅ **Succès** si :
- Chaque département ne voit que ses compétences/formations
- La création associe automatiquement au département connecté
- Les admins voient toujours tout
- Aucune erreur lors de l'utilisation

❌ **Échec** si :
- Un département voit les données d'autres départements
- Erreurs lors de la création/modification
- Problèmes d'affichage dans les formulaires

### URLs de test
- Département : `http://localhost:8080/departement/competences`
- Département : `http://localhost:8080/departement/formations`
- Département : `http://localhost:8080/departement/postes/nouveau`
- Admin : `http://localhost:8080/postes/new`

### Base de données
Vérifier les données avec :
```sql
-- Voir les compétences par département
SELECT c.nom as competence, u.nom_departement as departement 
FROM competance c 
LEFT JOIN "user" u ON c.departement_id = u.id_user;

-- Voir les formations par département  
SELECT f.nom as formation, u.nom_departement as departement
FROM formation f
LEFT JOIN "user" u ON f.departement_id = u.id_user;
```
