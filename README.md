# Web-Services

## Initialisation et Lancement du Frontend

Le dossier `frontend` contient l'application cliente en React. Pour des raisons d'optimisation, les dépendances (`node_modules`) ne sont pas partagées sur Git.

Pour installer et lancer le projet sur votre machine :

### Prérequis
* Avoir **Node.js** installé sur votre système.

---

### Étapes d'installation

#### 1. Se déplacer dans le dossier frontend
Ouvrez votre terminal et naviguez dans le sous-dossier dédié au front :
```bash
cd frontend
```

#### 2. Configuration des variables d'environnement
Le projet utilise une variable d'environnement pour connaître l'adresse de l'API Tomcat.

1. Copiez le fichier exemple mis à disposition :
    * **Sous Windows (Invite de commandes) :**
      ```cmd
      copy .env.example .env.local
      ```
    * **Sous Mac / Linux / Git Bash :**
      ```bash
      cp .env.example .env.local
      ```
2. Ouvrez le nouveau fichier `.env.local` fraîchement créé et **ajustez l'URL** pour qu'elle corresponde exactement à l'adresse locale de votre Tomcat. Exemple :
   ```properties
   VITE_API_URL=http://localhost:8080/web_services_temp_war_exploded
   ```

#### 3. Installer les dépendances
Générez le dossier `node_modules` local en téléchargeant les paquets requis via npm :
```bash
npm install
```

---

### Démarrage en mode développement

Une fois l'installation terminée, lancez le serveur de développement Vite :
```bash
npm run dev
```

Un lien s'affichera dans votre terminal (généralement `http://localhost:5173`). Cliquez dessus pour ouvrir l'application dans votre navigateur.