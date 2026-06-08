DROP TABLE IF EXISTS membre_de CASCADE;
DROP TABLE IF EXISTS message CASCADE;
DROP TABLE IF EXISTS canal CASCADE;
DROP TABLE IF EXISTS utilisateur CASCADE;

CREATE TABLE utilisateur (
    "idUtilisateur" SERIAL PRIMARY KEY,
    pseudo VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    "motDePasseHash" VARCHAR(255) NOT NULL,
    "dateCreation" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE canal (
    "idCanal" SERIAL PRIMARY KEY,
    "idAdmin" INT REFERENCES utilisateur("idUtilisateur"),
    nom VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    "typeCanal" VARCHAR(50) CHECK ("typeCanal" IN ('public', 'privé')),
    slug VARCHAR(255),
    "dateCreation" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE membre_de (
    "idUtilisateur" INT REFERENCES utilisateur("idUtilisateur"),
    "idCanal" INT REFERENCES canal("idCanal"),
    "dateAdhesion" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY ("idUtilisateur", "idCanal")
);

CREATE TABLE message (
    "idMessage" SERIAL PRIMARY KEY,
    "idUtilisateur" INT REFERENCES utilisateur("idUtilisateur"),
    "idCanal" INT REFERENCES canal("idCanal"),
    contenu TEXT NOT NULL,
    "dateCreation" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "dateModification" TIMESTAMP
);

INSERT INTO utilisateur (pseudo, email, "motDePasseHash") VALUES
    ('alice', 'alice@email.com', '$2b$12$e0MYzX1W/HlX8Y9K...'),
    ('bob', 'bob@email.com', '$2b$12$b6RzQ9P2LmNqWvTx...'),
    ('charlie', 'charlie@email.com', '$2b$12$f8KjH7M1OpQrStUv...');

INSERT INTO canal ("idAdmin", nom, description, "typeCanal", slug) VALUES
    (1, 'Général', 'Discussion ouverte à toute l''équipe', 'public', 'general'),
    (2, 'Projet Secret', 'Espace confidentiel pour la direction', 'privé', 'projet-secret');

INSERT INTO membre_de ("idUtilisateur", "idCanal") VALUES
    (1, 1),
    (2, 1),
    (3, 1),
    (1, 2),
    (2, 2);

INSERT INTO message ("idUtilisateur", "idCanal", contenu) VALUES
    (1, 1, 'Bonjour tout le monde et bienvenue sur le canal général !'),
    (2, 1, 'Salut Alice ! Ravi de voir que l''application avance bien.'),
    (3, 1, 'Hello à tous ! Super ce nouveau système de messagerie.'),
    (1, 1, 'Merci Charlie. N''hésite pas à remonter des bugs ici si tu en trouves.'),
    (3, 1, 'Est-ce que ce canal est visible par les invités externes ?'),
    (2, 1, 'Oui Charlie, comme c''est un canal public, tous les inscrits y ont accès.'),
    (2, 2, 'Salut Alice, j''ai créé ce canal privé pour qu''on parle du budget.'),
    (1, 2, 'Parfait Bob. Est-ce que tu as bien vérifié que Charlie n''a pas accès ?'),
    (2, 2, 'Je confirme, il n''est pas dans la table membre_de pour ce canal.'),
    (1, 2, 'Super, on est tranquilles pour bosser. Je t''envoie les chiffres cet après-midi.');
