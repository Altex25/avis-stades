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

### Tests du backend avec Colima

Testcontainers ne détecte pas Colima tout seul. Exporter ces variables avant de lancer les tests :

```bash
export DOCKER_HOST="unix://$HOME/.colima/default/docker.sock"
export TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE=/var/run/docker.sock
```

## Lancer en local

```bash
cd frontend
npm start
```

La PWA est alors disponible sur http://localhost:4200.

Le backend a besoin d'une base PostgreSQL : l'environnement Docker Compose arrive au prochain jalon.
D'ici là, `TestAvisStadesApplication` (dans `backend/src/test`) démarre l'API avec une base jetable en conteneur.

## Licence

Tous droits réservés. Le code source est publié pour consultation uniquement ; aucune licence n'est accordée.
