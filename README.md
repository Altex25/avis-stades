# Avis Stades

Application web progressive (PWA) qui rassemble les expériences des supporters dans les stades de football :
coût du déplacement par poste, notes par critère, commentaire et photos, avec modération.

## Organisation du dépôt

| Dossier | Contenu |
| --- | --- |
| `backend/` | API Spring Boot 4.1 (Java 25, Maven), monolithe modulaire vérifié par Spring Modulith |
| `frontend/` | PWA Angular 22 (Tailwind CSS 4, Vitest) |

L'API servira la PWA compilée sur la même origine : le projet se déploie en un seul livrable.

### Modules applicatifs

`users` · `stadiums` · `reviews` · `moderation` · `statistics` · `notifications`

Un module n'utilise un autre module que par son API publique ou en écoutant ses événements.
`ModularityTests` fait échouer le build dès qu'un module accède à l'intérieur d'un autre.

## Branches et versions

| Branche | Rôle |
| --- | --- |
| `develop` | Branche par défaut, déployée en recette |
| `main` | Production |

Chaque ticket part de `develop` sur une branche courte et y revient par pull request.
Une release passe de `develop` à `main` ; un tag `vX.Y.Z` fige alors la version.
Chaque commit tient sur une ligne au format Conventional Commits et cite son ticket, par exemple
`chore: initialize monorepo skeleton (#1)`.

## Prérequis

- JDK 25
- Node.js 24 (voir `.nvmrc` ; `nvm use` sélectionne la bonne version)
- Un moteur Docker (Docker Desktop, OrbStack ou Colima), nécessaire aux tests d'intégration

Maven n'a pas besoin d'être installé : le backend embarque le Maven Wrapper.

## Compiler et tester

```bash
cd backend
./mvnw verify
```

```bash
cd frontend
npm ci
npm run build
npm test -- --watch=false
```

### Intégration continue

Chaque pull request et chaque push sur `develop` ou `main` déclenchent les vérifications suivantes, qui tournent aussi chaque nuit :

| Vérification | Contenu |
| --- | --- |
| Backend | `./mvnw verify` : tests JUnit, Testcontainers, frontières des modules, couverture JaCoCo |
| Frontend | tests Vitest avec couverture, build de production |
| SonarQube Cloud | qualité, duplication et couverture du code, porte qualité |
| CodeQL | analyse de sécurité du code Java et TypeScript |
| Dependency review | refus d'une dépendance ajoutée avec une faille haute ou critique (pull requests) |

Les rapports de couverture sont générés en local par les mêmes commandes : `backend/target/site/jacoco/`
et `frontend/coverage/` (`npm test -- --watch=false --coverage`).

### Tests du backend avec Colima

Testcontainers cherche Docker sur `/var/run/docker.sock`, qui pointe sur macOS vers `~/.docker/run/docker.sock`.
Avec Colima, relier une fois pour toutes ce chemin au socket de Colima (sans Docker Desktop installé) :

```bash
mkdir -p ~/.docker/run && ln -sf ~/.colima/default/docker.sock ~/.docker/run/docker.sock
```

Les tests fonctionnent ensuite depuis le terminal comme depuis l'IDE, sans variable d'environnement.

## Lancer en local

### 1. Démarrer les services

Le dossier `infra/` décrit les services dont l'application a besoin :

| Service | Rôle | Adresse sur le poste |
| --- | --- | --- |
| PostgreSQL 17 + PostGIS 3.6 | Base de données | `localhost:5432` |
| Garage | Stockage des photos, compatible S3 | `http://localhost:3900` |
| Mailpit | Capture des e-mails envoyés | SMTP `localhost:1025`, interface http://localhost:8025 |

Au premier lancement, copier le fichier d'exemple des variables (le fichier `.env` n'est jamais versionné ;
ses valeurs sont réservées au poste local et ne doivent pas être réutilisées ailleurs) :

```bash
cp infra/.env.example infra/.env
```

Puis démarrer les services :

```bash
docker compose -f infra/docker-compose.yml up -d
```

Le conteneur `garage-init` prépare le stockage puis s'arrête : il crée le bucket des photos et sa clé d'accès.
`docker compose -f infra/docker-compose.yml ps -a` doit montrer `postgres` et `mailpit` en état `healthy`,
`garage` en état `Up` et `garage-init` en état `Exited (0)`.

Pour arrêter les services :

```bash
docker compose -f infra/docker-compose.yml down
```

Ajouter `-v` à cette commande supprime aussi les données, pour repartir d'une base et d'un stockage vides.

### 2. Lancer l'API

```bash
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

Le profil `local` lit `infra/.env` et se connecte aux services ci-dessus.
L'API répond sur http://localhost:8080 ; http://localhost:8080/actuator/health doit renvoyer `{"status":"UP"}`.

Dans IntelliJ IDEA, lancer `AvisStadesApplication` avec le profil actif `local`.

### 3. Lancer la PWA

```bash
cd frontend
npm start
```

La PWA est alors disponible sur http://localhost:4200.

## Licence

Tous droits réservés. Le code source est publié pour consultation uniquement ; aucune licence n'est accordée.
