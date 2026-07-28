# Infrastructure Underdogs

Une VM Azure exécutant la stack complète en conteneurs, derrière Caddy qui gère le TLS
automatiquement. Terraform provisionne la VM, le réseau et le DNS ; `docker-compose.prod.yml`
déploie les conteneurs.

Les trois services sont exposés sous **une seule origine**, ce qui supprime toute question de CORS :

| Chemin | Service |
| --- | --- |
| `/` | frontend |
| `/api/*` | backend |
| `/auth/*` | Keycloak |

## 1. Provisionner la VM

```bash
az login
az account show --query id -o tsv     # subscription_id

cd infra
cp prod.tfvars.example prod.tfvars    # renseigner subscription_id et dns_label

terraform init
terraform plan  -var-file=prod.tfvars
terraform apply -var-file=prod.tfvars

terraform output site_address
```

`dns_label` doit être unique dans la région : si `terraform apply` échoue sur ce point, changez-le.

## 2. Déployer la stack

```bash
ssh underdogs@<fqdn>
sudo mkdir -p /opt/underdogs && sudo chown $USER /opt/underdogs
cd /opt/underdogs
git clone https://github.com/ksarlary/underdogs-backend.git .
cd infra

cp .env.example .env                  # renseigner SITE_ADDRESS et les mots de passe

echo $GHCR_TOKEN | docker login ghcr.io -u <user> --password-stdin
docker compose -f docker-compose.prod.yml up -d
docker compose -f docker-compose.prod.yml logs -f
```

Le `docker login` n'est nécessaire que si les packages GHCR sont privés. Les rendre publics dans
les paramètres du package évite cette étape.

Comptez deux à trois minutes au premier démarrage : Keycloak importe le realm et Flyway applique
les migrations.

## 3. Configurer le client Keycloak

Le realm est importé avec des URL de développement. Dans la console d'administration
(`https://<fqdn>/auth`), client `underdogs-frontend`, remplacer :

- **Valid redirect URIs** : `https://<fqdn>/*`
- **Web origins** : `https://<fqdn>`
- **Root URL** et **Home URL** : `https://<fqdn>`

Sans cette étape, la connexion échoue avec `Invalid parameter: redirect_uri`.

Le realm importé n'a pas de serveur SMTP configuré : la réinitialisation de mot de passe restera
inopérante tant qu'un SMTP (Brevo, Mailtrap) n'est pas renseigné dans Realm settings → Email.

## 4. Reconstruire le frontend pour la production

Les variables Vite sont figées au build. Dans les *repository variables* du dépôt frontend :

| Variable | Valeur |
| --- | --- |
| `VITE_API_BASE_URL` | *(vide)* |
| `VITE_KEYCLOAK_URL` | `https://<fqdn>/auth` |
| `VITE_KEYCLOAK_REALM` | `underdogs` |
| `VITE_KEYCLOAK_CLIENT_ID` | `underdogs-frontend` |

`VITE_API_BASE_URL` vide est volontaire : le frontend appelle `/api/v1/...` en relatif, donc sur la
même origine.

## 5. Mettre à jour une image

```bash
cd /opt/underdogs/infra
docker compose -f docker-compose.prod.yml pull
docker compose -f docker-compose.prod.yml up -d
```

## Maîtriser la consommation du crédit

Une `Standard_B2s` consomme le crédit étudiant en environ trois mois. Deux leviers :

```bash
az vm deallocate --resource-group rg-underdogs-prod --name vm-underdogs-prod   # arrête la facturation compute
az vm start      --resource-group rg-underdogs-prod --name vm-underdogs-prod

terraform destroy -var-file=prod.tfvars                                        # supprime tout
```

Une VM simplement éteinte depuis l'OS continue d'être facturée : il faut `deallocate`.

Pensez à créer une alerte de budget dans le portail Azure, sous Cost Management.

## Environnements

`dev.tfvars` et `prod.tfvars` déploient deux stacks isolées, chacune avec son groupe de ressources
et son FQDN. Utiliser un workspace Terraform par environnement pour ne pas mélanger les états :

```bash
terraform workspace new prod
terraform workspace select prod
terraform apply -var-file=prod.tfvars
```
