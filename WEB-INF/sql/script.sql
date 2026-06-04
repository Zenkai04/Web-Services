DROP TABLE IF EXISTS utilisateur CASCADE;
DROP TABLE IF EXISTS membre_de CASCADE;
DROP TABLE IF EXISTS canal CASCADE;
DROP TABLE IF EXISTS message CASCADE;

CREATE TABLE utilisateur (
                             idUtilisateur INT PRIMARY KEY,
                             pseudo VARCHAR(255) UNIQUE NOT NULL,
                             email VARCHAR(255) UNIQUE NOT NULL,
                             motDePasseHash VARCHAR(255) NOT NULL,
                             date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE canal (
                       idCanal INT PRIMARY KEY,
                       idAdmin INT REFERENCES utilisateur(idUtilisateur),
                       nom VARCHAR(255) NOT NULL,
                       description VARCHAR(255),
                       typeCanal VARCHAR(50) CHECK (typeCanal IN ('public', 'privé')),
                       slug VARCHAR(255),
                       date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE membre_de (
                           idUtilisateur INT REFERENCES utilisateur(idUtilisateur),
                           idCanal INT REFERENCES canal(idCanal),
                           dateAdhesion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           PRIMARY KEY (idUtilisateur, idCanal) -- Évite qu'un utilisateur rejoigne 2 fois le même canal
);

CREATE TABLE message (
                         idMessage INT PRIMARY KEY,
                         idUtilisateur INT REFERENCES utilisateur(idUtilisateur),
                         idCanal INT REFERENCES canal(idCanal),
                         contenu TEXT,
                         date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         dateModification TIMESTAMP
);