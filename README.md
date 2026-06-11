# Web-Services - Journal de bord

Application de messagerie organisee autour de canaux publics et prives.

Le projet est compose d'un backend Java Jakarta EE deploye sur Tomcat 11, d'une API REST securisee par token JWT, d'une couche JDBC PostgreSQL et d'un frontend React/Vite.

## Auteurs

| Etudiant | Contributions principales |
| --- | --- |
| NADIR Ayoub | Conception backend, API REST, generation des tokens JWT, securisation des mots de passe, structure DAO/JDBC. |
| LASNE Tanguy | Frontend React, gestion des sessions par token cote client, invalidation locale lors de la deconnexion, creation de la base de donnees a partir de la conception. |

## Fonctionnalites

- Inscription publique d'un utilisateur.
- Connexion avec generation d'un token JWT.
- Hachage des mots de passe cote backend.
- Reponses utilisateur publiques sans exposition du hash.
- Canaux publics et prives.
- Ajout automatique des utilisateurs aux canaux publics.
- Gestion des membres de canaux.
- Envoi, modification et suppression de messages.
- Controle d'acces par token et par regles metier.
- Frontend React consommant l'API REST.

## Architecture

```text
Web-Services/
|-- WEB-INF/
|   |-- classes/
|   |   |-- controleur/     Servlets REST Jakarta
|   |   |-- dao/            Interfaces DAO et implementations JDBC
|   |   |-- dto/            Objets de transfert JSON
|   |   |-- filtre/         Filtre CORS
|   |   `-- utils/          JWT et mots de passe
|   |-- lib/                Dependances Java externes
|   `-- sql/script.sql      Script de creation de la base
|-- frontend/               Application React/Vite
|-- MCD/                    Modele conceptuel de donnees
|-- db.properties           Configuration locale de la base
|-- db_exemple.properties   Exemple de configuration
`-- compile.bat             Compilation backend et redemarrage Tomcat
```

## Backend Java

### Controleurs REST

| Classe | Route | Role |
| --- | --- | --- |
| `AuthRestAPI` | `/auth/*` | Connexion et generation du token JWT. |
| `UtilisateurRestAPI` | `/utilisateurs/*` | Inscription, consultation et modification des utilisateurs. |
| `CanalRestAPI` | `/canaux/*` | Canaux, membres et messages rattaches a un canal. |
| `MessageRestAPI` | `/messages/*` | Consultation globale autorisee et modification de messages. |
| `SecuredServelet` | classe parente | Verification centralisee du header `Authorization: Bearer`. |

### Couche DAO

| Interface | Implementation JDBC | Donnees gerees |
| --- | --- | --- |
| `UtilisateurDAO` | `UtilisateurDAOJDBC` | Comptes utilisateurs. |
| `CanalDAO` | `CanalDAOJDBC` | Canaux publics/prives. |
| `MessageDAO` | `MessageDAOJDBC` | Messages. |
| `MembreCanalDAO` | `MembreCanalDAOJDBC` | Association utilisateurs-canaux. |

`DAOFactory` fournit les DAO aux controleurs. `DbConnectionManager` charge `db.properties` et ouvre les connexions PostgreSQL.

### DTO principaux

| DTO | Role |
| --- | --- |
| `Utilisateur` | Version complete cote backend, contient le hash du mot de passe. |
| `UtilisateurPublic` | Version exposee en JSON, sans mot de passe. |
| `LoginRequest` | Corps de requete pour la connexion. |
| `AuthResponse` | Token JWT, duree de validite et profil public. |
| `Canal` | Representation d'un canal. |
| `Message` | Representation d'un message. |
| `APIMessage` | Reponse JSON simple avec un message texte. |

## Endpoints REST

Les routes securisees attendent le header suivant :

```http
Authorization: Bearer <token>
```

### Authentification

| Methode | Endpoint | Acces | Description |
| --- | --- | --- | --- |
| `POST` | `/auth/login` | Public | Connecte un utilisateur et renvoie un token JWT. |
| `GET` / `PUT` / `DELETE` | `/auth/*` | Refuse | Renvoie `405 Method Not Allowed`. |

### Utilisateurs

| Methode | Endpoint | Acces | Description |
| --- | --- | --- | --- |
| `POST` | `/utilisateurs` | Public | Cree un utilisateur. |
| `GET` | `/utilisateurs` | Token requis | Liste les utilisateurs publics. |
| `GET` | `/utilisateurs/{id}` | Token requis | Recupere un utilisateur public. |
| `GET` | `/utilisateurs/{id}/canaux` | Utilisateur concerne | Liste les canaux de l'utilisateur connecte. |
| `PUT` | `/utilisateurs/{id}` | Utilisateur concerne | Modifie son compte. |
| `DELETE` | `/utilisateurs/*` | Refuse | Renvoie `405 Method Not Allowed`. |

### Canaux

| Methode | Endpoint | Acces | Description |
| --- | --- | --- | --- |
| `GET` | `/canaux` | Token requis | Liste les canaux visibles. |
| `POST` | `/canaux` | Token requis | Cree un canal. |
| `GET` | `/canaux/{id}` | Membre ou canal public | Recupere un canal. |
| `PUT` | `/canaux/{id}` | Admin du canal | Modifie un canal. |
| `GET` | `/canaux/{id}/messages` | Membre ou canal public | Liste les messages du canal. |
| `POST` | `/canaux/{id}/messages` | Membre du canal | Cree un message dans le canal. |
| `DELETE` | `/canaux/{id}/messages/{idMessage}` | Auteur ou admin | Supprime un message dans son canal. |
| `GET` | `/canaux/{id}/membres` | Membre ou canal public | Liste les membres du canal. |
| `POST` | `/canaux/{id}/membres` | Admin du canal | Ajoute un membre. |
| `DELETE` | `/canaux/{id}/membres/{idUtilisateur}` | Admin ou soi-meme | Retire un membre. |

### Messages

| Methode | Endpoint | Acces | Description |
| --- | --- | --- | --- |
| `GET` | `/messages` | Token requis | Liste les messages visibles par l'utilisateur. |
| `GET` | `/messages/{id}` | Acces au canal | Recupere un message visible. |
| `PUT` | `/messages/{id}` | Auteur du message | Modifie le contenu d'un message. |
| `POST` | `/messages` | Refuse | Les messages doivent etre crees depuis un canal. |
| `DELETE` | `/messages/{id}` | Refuse | La suppression se fait via `/canaux/{idCanal}/messages/{idMessage}`. |

## Securite

- Les mots de passe ne sont pas stockes en clair.
- `PasswordUtils` gere le hachage et la verification.
- `JwtManager` genere et verifie les tokens JWT.
- `SecuredServelet` centralise la verification du header `Authorization`.
- `UtilisateurPublic` evite d'exposer le hash du mot de passe dans les reponses JSON.
- Le frontend supprime le token stocke localement lors de la deconnexion.

## Configuration de la base de donnees

Le backend lit les informations de connexion dans `db.properties`.

Exemple :

```properties
DB_URL=jdbc:postgresql://localhost:5432/nom_base
DB_USER=postgres
DB_PASSWORD=mot_de_passe
```

Un modele est fourni dans `db_exemple.properties`.

Le script SQL principal est disponible ici :

```text
WEB-INF/sql/script.sql
```

Il cree les tables :

- `utilisateur`
- `canal`
- `membre_de`
- `message`

## Compilation et lancement backend

Prerequis :

- Java JDK installe.
- Tomcat 11 installe.
- PostgreSQL accessible.
- Les dependances presentes dans `WEB-INF/lib`.

Compilation manuelle depuis `WEB-INF/classes` :

```bat
javac -cp ".;..\lib\*;C:\Program Files\Apache Software Foundation\Tomcat 11.0\lib\*" -d . controleur\*.java dao\*.java dto\*.java filtre\*.java utils\*.java
```

Le projet contient aussi un script Windows :

```bat
compile.bat
```

Ce script compile les fichiers Java puis redemarre le service `Tomcat11`.

## Frontend React

Le dossier `frontend` contient l'application cliente.

Prerequis :

- Node.js
- npm

Installation :

```bash
cd frontend
npm install
```

Configuration de l'URL backend dans `.env.local` :

```properties
VITE_API_URL=http://localhost:8080/Web-Services
```

Lancement en developpement :

```bash
npm run dev
```

Build de production :

```bash
npm run build
```

## Notes de fonctionnement

- L'inscription est publique.
- Les autres operations sensibles utilisent le token JWT.
- Un canal public ajoute automatiquement les utilisateurs existants.
- Un nouvel utilisateur est ajoute automatiquement aux canaux publics.
- Un message n'existe pas fonctionnellement hors d'un canal.
- La suppression d'un message passe donc par le controleur des canaux.
- La deconnexion est geree cote frontend par suppression du token local.

## Etat du projet

Le backend, la securisation, les DTO publics et les regles metier principales sont en place.
Le frontend consomme l'API avec gestion de session par token.
La documentation du code a ete ajoutee directement dans les fichiers Java et React.
