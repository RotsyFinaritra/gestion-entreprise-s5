# Configuration alternative pour générer automatiquement les tables
# Remplacez temporairement dans application.properties:

# AVANT (configuration actuelle)
spring.jpa.hibernate.ddl-auto=none

# APRÈS (pour générer les tables)
spring.jpa.hibernate.ddl-auto=update

# Après avoir démarré l'application une fois pour créer les tables,
# vous pouvez remettre la configuration à "none" pour éviter
# les modifications accidentelles du schéma en production.

# Note: Cette méthode est pratique pour le développement mais 
# la création manuelle via SQL est recommandée en production.