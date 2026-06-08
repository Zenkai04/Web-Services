DROP TABLE IF EXISTS membre_de CASCADE;
DROP TABLE IF EXISTS message CASCADE;
DROP TABLE IF EXISTS canal CASCADE;
DROP TABLE IF EXISTS utilisateur CASCADE;

CREATE TABLE utilisateur (
    "idUtilisateur" INT PRIMARY KEY,
    pseudo VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    "motDePasseHash" VARCHAR(255) NOT NULL,
    "dateCreation" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE canal (
    "idCanal" INT PRIMARY KEY,
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
    "idMessage" INT PRIMARY KEY,
    "idUtilisateur" INT REFERENCES utilisateur("idUtilisateur"),
    "idCanal" INT REFERENCES canal("idCanal"),
    contenu TEXT NOT NULL,
    "dateCreation" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "dateModification" TIMESTAMP
);

INSERT INTO utilisateur ("idUtilisateur", pseudo, email, "motDePasseHash") VALUES
    (1, 'alice', 'alice@email.com', '$2b$12$e0MYzX1W/HlX8Y9K...'),
    (2, 'bob', 'bob@email.com', '$2b$12$b6RzQ9P2LmNqWvTx...'),
    (3, 'charlie', 'charlie@email.com', '$2b$12$f8KjH7M1OpQrStUv...');

INSERT INTO canal ("idCanal", "idAdmin", nom, description, "typeCanal", slug) VALUES
    (10, 1, 'General', 'Discussion ouverte a toute l''equipe', 'public', 'general'),
    (20, 2, 'Projet Secret', 'Espace confidentiel pour la direction', 'privé', 'projet-secret');

INSERT INTO membre_de ("idUtilisateur", "idCanal") VALUES
    (1, 10),
    (2, 10),
    (3, 10),
    (1, 20),
    (2, 20);

INSERT INTO message ("idMessage", "idUtilisateur", "idCanal", contenu) VALUES
    (1, 1, 10, 'Bonjour tout le monde et bienvenue sur le canal general !'),
    (2, 2, 10, 'Salut Alice ! Ravi de voir que l''application avance bien.'),
    (3, 3, 10, 'Hello a tous ! Super ce nouveau systeme de messagerie.'),
    (4, 1, 10, 'Merci Charlie. N''hesite pas a remonter des bugs ici si tu en trouves.'),
    (5, 3, 10, 'Est-ce que ce canal est visible par les invites externes ?'),
    (6, 2, 10, 'Oui Charlie, comme c''est un canal public, tous les inscrits y ont acces.'),
    (7, 2, 20, 'Salut Alice, j''ai cree ce canal prive pour qu''on parle du budget.'),
    (8, 1, 20, 'Parfait Bob. Est-ce que tu as bien verifie que Charlie n''a pas acces ?'),
    (9, 2, 20, 'Je confirme, il n''est pas dans la table membre_de pour ce canal.'),
    (10, 1, 20, 'Super, on est tranquilles pour bosser. Je t''envoie les chiffres cet apres-midi.');
