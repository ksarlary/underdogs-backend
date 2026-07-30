---
status: "accepted"
date: 2026-07-28
decision-makers: KONOVALOVA Sofia, TANCRAY Ibtissam, KASSI Florian
---

# Hébergement sur une VM Azure plutôt que sur un cluster Kubernetes

## Context and Problem Statement

L'application doit être déployée sur un cloud, accessible en HTTPS sur un nom de domaine, avec des
livrables sous forme de conteneurs et des ressources provisionnées par de l'IaC.

La stack compte sept conteneurs : frontend, backend, worker, deux bases PostgreSQL, Keycloak et une
file de messages. Le projet est mené par des étudiants sans budget, avec pour seule ressource les
offres du GitHub Student Pack.

## Considered Options

* VM unique sous Docker Compose, provisionnée par Terraform
* Kubernetes managé (DigitalOcean Kubernetes, puis équivalents)
* Conteneurs serverless (Google Cloud Run + Pub/Sub)

## Decision Outcome

Chosen option: **VM unique sous Docker Compose sur Azure**, provisionnée par Terraform, avec Caddy en
reverse proxy pour le TLS automatique.

Trois facteurs ont tranché. Le crédit DigitalOcean du Student Pack a été retiré au 31 juillet 2026,
supprimant l'option la moins chère pour Kubernetes. Azure for Students offre 100 $ sur douze mois
sans carte bancaire, seule offre du pack dans ce cas. Et le `compose.yaml` de développement existait
déjà, ce qui a réduit le travail de mise en production à l'ajout d'un reverse proxy.

Conséquence assumée : pas de haute disponibilité, la VM est un point de défaillance unique. C'est
acceptable pour un projet évalué sur l'IaC et l'architecture, pas sur la résilience.

## Pros and Cons of the Options

### VM unique sous Docker Compose

* Avantage, car la configuration de développement est réutilisable presque telle quelle.
* Avantage, car le coût est prévisible et unique : une seule ressource facturée.
* Avantage, car `terraform destroy` supprime toute l'infrastructure d'une commande.
* Neutre, car l'exploitation reste manuelle sans orchestrateur.
* Inconvénient, car aucune haute disponibilité ni redémarrage automatique sur un autre nœud.
* Inconvénient, car la montée en charge impose de redimensionner la VM.

### Kubernetes managé

* Avantage, car il apporte l'auto-réparation, le scaling horizontal et les déploiements progressifs.
* Avantage, car c'est la compétence la plus valorisée sur le marché.
* Neutre, car les manifestes représentent un volume de configuration comparable au Terraform.
* Inconvénient, car le coût est structurellement supérieur : nœuds, load balancer, base managée.
* Inconvénient, car aucune de ces capacités n'est requise par le sujet.

### Cloud Run + Pub/Sub

* Avantage, car natif conteneur, avec un free tier suffisant pour notre charge.
* Avantage, car la mise à l'échelle jusqu'à zéro élimine le coût en période creuse.
* Neutre, car il impose des services sans état, ce que respecte déjà notre architecture.
* Inconvénient, car il aurait fallu réécrire la couche file des deux côtés, SQS n'existant pas chez
  Google.
* Inconvénient, car Keycloak met vingt à quarante secondes à démarrer : garder une instance chaude
  fait sortir du gratuit, et sans elle la première connexion est inacceptable.
