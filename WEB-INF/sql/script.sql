DROP TABLE IF EXISTS utilisateur CASCADE;
DROP TABLE IF EXISTS membre_de CASCADE;
DROP TABLE IF EXISTS canal CASCADE;
DROP TABLE IF EXISTS message CASCADE;

CREATE TABLE utilisateur (
    idUtilisateur INT PRIMARY KEY,
    pseudo VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    motDePasseHash VARCHAR(255) NOT NULL,
    dateCreation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE canal (
    idCanal INT PRIMARY KEY,
    idAdmin INT REFERENCES utilisateur(idUtilisateur),
    nom VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    typeCanal VARCHAR(50) CHECK (typeCanal IN ('public', 'privé')),
    slug VARCHAR(255),
    dateCreation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
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
    contenu TEXT NOT NULL,
    dateCreation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    dateModification TIMESTAMP
);

-- ============================================================================
-- ALIMENTATION DES UTILISATEURS
-- ============================================================================
INSERT INTO utilisateur (idUtilisateur, pseudo, email, motDePasseHash) VALUES
    (1, 'alice', 'alice@email.com', '$2b$12$e0MYzX1W/HlX8Y9K...'),
    (2, 'bob', 'bob@email.com', '$2b$12$b6RzQ9P2LmNqWvTx...'),
    (3, 'charlie', 'charlie@email.com', '$2b$12$f8KjH7M1OpQrStUv...');

-- ============================================================================
-- ALIMENTATION DES CANAUX
-- ============================================================================
INSERT INTO canal (idCanal, idAdmin, nom, description, typeCanal, slug) VALUES
    (10, 1, 'Général', 'Discussion ouverte à toute l''équipe', 'public', 'general'),
    (20, 2, 'Projet Secret', 'Espace confidentiel pour la direction', 'privé', 'projet-secret');

-- ============================================================================
-- ALIMENTATION DES MEMBRES
-- ============================================================================
-- Tout le monde est sur le canal général
INSERT INTO membre_de (idUtilisateur, idCanal) VALUES (1, 10);
INSERT INTO membre_de (idUtilisateur, idCanal) VALUES (2, 10);
INSERT INTO membre_de (idUtilisateur, idCanal) VALUES (3, 10);

-- Seuls Alice et Bob sont sur le canal privé
INSERT INTO membre_de (idUtilisateur, idCanal) VALUES (1, 20);
INSERT INTO membre_de (idUtilisateur, idCanal) VALUES (2, 20);

-- ============================================================================
-- ALIMENTATION DES MESSAGES
-- ============================================================================

-- Fil de discussion sur le canal PUBLIC
INSERT INTO message (idMessage, idUtilisateur, idCanal, contenu) VALUES
    (1, 1, 10, 'Bonjour tout le monde et bienvenue sur le canal général !'),
    (2, 2, 10, 'Salut Alice ! Ravi de voir que l''application avance bien.'),
    (3, 3, 10, 'Hello à tous ! Super ce nouveau système de messagerie.'),
    (4, 1, 10, 'Merci Charlie. N''hésite pas à remonter des bugs ici si tu en trouves.'),
    (5, 3, 10, 'Est-ce que ce canal est visible par les invités externes ?'),
    (6, 2, 10, 'Oui Charlie, comme c''est un canal "public", tous les inscrits y ont accès.');

-- Fil de discussion sur le canal PRIVÉ
INSERT INTO message (idMessage, idUtilisateur, idCanal, contenu) VALUES
    (7, 2, 20, 'Salut Alice, j''ai créé ce canal privé pour qu''on parle du budget.'),
    (8, 1, 20, 'Parfait Bob. Est-ce que tu as bien vérifié que Charlie n''a pas accès ?'),
    (9, 2, 20, 'Je confirme, il n''est pas dans la table membre_de pour ce canal.'),
    (10, 1, 20, 'Super, on est tranquilles pour bosser. Je t''envoie les chiffres cet après-midi.');