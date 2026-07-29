# Déploiement Underdogs — guide de bout en bout

Une VM Azure exécute toute la stack en conteneurs, derrière Caddy qui obtient et renouvelle le
certificat TLS automatiquement. Terraform provisionne la VM, le réseau et le DNS ;
`docker-compose.prod.yml` déploie les conteneurs.

Les trois services sont exposés sous **une seule origine**, ce qui supprime toute question de CORS :

| Chemin | Service |
| --- | --- |
| `/` | frontend |
| `/api/*` | backend |
| `/auth/*` | Keycloak |

Durée totale : environ une heure, dont une bonne part d'attente.

---

## Étape 0 — Prérequis

### 0.1 Les trois images doivent exister sur GHCR

Le déploiement ne construit rien : il tire des images déjà publiées. Vérifiez que les pipelines CD
sont vertes et que les trois packages existent :

| Image | Dépôt |
| --- | --- |
| `ghcr.io/ksarlary/underdogs-api` | underdogs-backend |
| `ghcr.io/ksarlary/underdogs-frontend` | underdogs_frontend |
| `ghcr.io/4l0cks/underdogs-worker` | underdogs-worker |

**Passez les trois packages en public** (page du package → Package settings → Change visibility).
Cela évite d'avoir à gérer un token de lecture sur la VM.

### 0.2 Outils sur votre poste

```powershell
az version
terraform version
dir $env:USERPROFILE\.ssh\*.pub
```

S'il manque quelque chose :

```powershell
winget install Microsoft.AzureCLI
winget install Hashicorp.Terraform
ssh-keygen -t ed25519
```

Après un `winget install`, ouvrez un **nouveau** terminal : le PATH n'est lu qu'au démarrage.

### 0.3 Authentification Azure

```powershell
az login
az account show --query "{nom:name, etat:state, compte:user.name}" -o table
az account show --query id -o tsv
```

Notez l'identifiant d'abonnement retourné par la dernière commande.

---

## Étape 1 — Provisionner l'infrastructure

```powershell
cd C:\Users\kassi\IdeaProjects\underdogs-backend\infra
copy prod.tfvars.example prod.tfvars
notepad prod.tfvars
```

Renseignez :

- `subscription_id` : la valeur notée à l'étape 0.3
- `dns_label` : **unique dans toute la région**, ajoutez-y quelque chose de personnel
- `ssh_public_key_path` : `~/.ssh/id_ed25519.pub` si vous venez de créer une clé ed25519

Puis :

```powershell
terraform init
terraform plan -var-file=prod.tfvars
```

`plan` liste ce qui serait créé sans rien créer ni facturer. Vous devez voir sept ressources :
resource group, virtual network, subnet, public IP, network security group, network interface et
la VM. Relisez avant de continuer.

```powershell
terraform apply -var-file=prod.tfvars
terraform output
```

C'est la seule commande qui engage votre crédit. Notez le `fqdn` : tout le reste en dépend.

---

## Étape 2 — Lancer le rebuild du frontend

À faire **immédiatement**, car c'est le plus long et ça tourne pendant que vous déployez.

Les variables Vite sont figées à la compilation : changer un fichier sur le serveur n'a aucun effet,
il faut reconstruire l'image. Dans le dépôt frontend, *Settings → Secrets and variables → Actions →
Variables* :

| Variable | Valeur |
| --- | --- |
| `VITE_API_BASE_URL` | *(laisser vide)* |
| `VITE_KEYCLOAK_URL` | `https://<fqdn>/auth` |
| `VITE_KEYCLOAK_REALM` | `underdogs` |
| `VITE_KEYCLOAK_CLIENT_ID` | `underdogs-frontend` |
| `VITE_MATCH_RESULT_API_VERSION` | `v1` |

`VITE_API_BASE_URL` vide est volontaire : le frontend appelle `/api/v1/...` en relatif, donc sur la
même origine que lui.

Poussez ensuite un commit sur `main` du dépôt frontend pour déclencher la CI puis la CD.

---

## Étape 3 — Déployer les conteneurs

```powershell
ssh underdogs@<fqdn>
```

Si la connexion est refusée, attendez une minute : cloud-init installe encore Docker.

Sur la VM :

```bash
cd /opt/underdogs
git clone https://github.com/ksarlary/underdogs-backend.git .
cd infra
cp .env.example .env
nano .env
```

Renseignez `SITE_ADDRESS` avec le FQDN **sans `https://`**, et remplacez les quatre `CHANGE_ME` par
de vrais mots de passe.

```bash
docker compose -f docker-compose.prod.yml up -d
docker compose -f docker-compose.prod.yml ps
docker compose -f docker-compose.prod.yml logs -f
```

Comptez deux à trois minutes : Keycloak importe le realm, Flyway applique les migrations, Caddy
demande le certificat. Quittez les logs avec `Ctrl+C` quand tout est stable.

Vérifiez que le TLS fonctionne :

```bash
curl -I https://<fqdn>/api/v1/teams
```

---

## Étape 4 — Configurer le client Keycloak

Le realm est importé avec des URL de développement. **Sans cette étape, la connexion échoue.**

Ouvrez `https://<fqdn>/auth`, connectez-vous avec `KEYCLOAK_ADMIN_USERNAME` et
`KEYCLOAK_ADMIN_PASSWORD` du `.env`. Realm `underdogs` → Clients → `underdogs-frontend` :

| Champ | Valeur |
| --- | --- |
| Root URL | `https://<fqdn>` |
| Home URL | `https://<fqdn>` |
| Valid redirect URIs | `https://<fqdn>/*` |
| Web origins | `https://<fqdn>` |

Enregistrez.

Profitez-en pour changer le mot de passe d'`admin_user` (Users → admin_user → Credentials) : le
realm l'importe avec `password123`, et ce Keycloak est désormais public.

---

## Étape 5 — Récupérer le frontend reconstruit

Quand la CD du frontend est verte :

```bash
cd /opt/underdogs/infra
docker compose -f docker-compose.prod.yml pull frontend
docker compose -f docker-compose.prod.yml up -d frontend
```

---

## Étape 6 — Vérifier

1. `https://<fqdn>` affiche le frontend, cadenas TLS valide.
2. La liste des matchs et des tournois se charge sans être connecté.
3. Le bouton de connexion redirige vers Keycloak sur `/auth`.
4. Connexion avec `underdogs_fan` — le retour sur le site se fait authentifié.
5. Placer un pari fonctionne.
6. Résolution asynchrone, connecté en `admin_user` : `POST /api/v1/bets/resolution`. Puis
   `docker compose logs worker` doit montrer le traitement, et le statut des paris doit changer.
7. Le versionnement d'API se démontre en rebuildant le frontend avec
   `VITE_MATCH_RESULT_API_VERSION=v2`, **sans redéployer le backend**.

---

## Exploitation

### Mettre à jour la production

Le déploiement est automatisé par le workflow `Deploy` (`.github/workflows/deploy.yml`). Il se
déclenche à la fin de la CD sur `main`, et peut aussi être lancé à la main depuis l'onglet Actions
via *Run workflow*.

Le workflow se connecte en SSH, aligne le dépôt de la VM sur `origin/main`, attend que le registre
serve bien l'image du commit demandé — c'est ce qui évite de déployer l'image précédente quand la CD
n'a pas fini — relance les conteneurs, puis vérifie que l'API répond 200.

Trois secrets sont requis dans *Settings → Secrets and variables → Actions* :

| Secret | Valeur |
| --- | --- |
| `VM_HOST` | le FQDN de la VM |
| `VM_USER` | `underdogs` |
| `VM_SSH_KEY` | le contenu de la clé privée SSH, en entier |

En cas de besoin, le déploiement manuel reste possible :

```bash
cd /opt/underdogs/underdogs-backend/infra
git pull
docker compose -f docker-compose.prod.yml pull
docker compose -f docker-compose.prod.yml up -d
```

### Maîtriser la consommation du crédit

Une `Standard_B2s` consomme environ 100 $ en trois mois. Une VM éteinte depuis l'OS **continue
d'être facturée** : il faut la désallouer.

```powershell
az vm deallocate --resource-group rg-underdogs-prod --name vm-underdogs-prod
az vm start      --resource-group rg-underdogs-prod --name vm-underdogs-prod
terraform destroy -var-file=prod.tfvars
```

Créez une alerte de budget dans le portail, sous *Cost Management → Budgets*.

### Deux environnements

`dev.tfvars` et `prod.tfvars` déploient deux stacks isolées, chacune avec son groupe de ressources
et son FQDN. Un workspace par environnement évite de mélanger les états :

```powershell
terraform workspace new prod
terraform workspace select prod
terraform apply -var-file=prod.tfvars
```

---

## Dépannage

| Symptôme | Cause | Correctif |
| --- | --- | --- |
| `terraform apply` : `RequestDisallowedByPolicy` ou quota | Restrictions du tenant de l'école | Changer `location` ou `vm_size` |
| `terraform apply` : le label DNS existe déjà | `dns_label` pris | En choisir un autre |
| `docker compose` : `denied` / `unauthorized` | Packages GHCR privés | Les passer en public, ou `docker login ghcr.io` |
| Pas de HTTPS, Caddy en erreur | Port 80 injoignable pour le challenge | Vérifier les règles du NSG et que le FQDN résout |
| `Invalid parameter: redirect_uri` | Étape 4 non faite | Corriger les URL du client Keycloak |
| Boucle de redirection à la connexion | `KC_HOSTNAME` / `KC_HTTP_RELATIVE_PATH` | Vérifier les deux dans `docker-compose.prod.yml` |
| Le backend redémarre en boucle | Variable d'environnement manquante | `docker compose logs backend` |
| Le front appelle `localhost` | Image construite avant l'étape 2 | Refaire l'étape 5 |
