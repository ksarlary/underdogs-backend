---
status: "accepted"
date: 2026-07-28
decision-makers: KONOVALOVA Sofia, TANCRAY Ibtissam, KASSI Florian
---

# Exposition des services sous une origine unique plutôt que par sous-domaines

## Context and Problem Statement

Trois services doivent être joignables depuis le navigateur : le frontend, l'API et Keycloak. Le
frontend s'authentifie auprès de Keycloak puis appelle l'API avec le jeton obtenu.

La question est de savoir comment les exposer publiquement, sachant que toute séparation d'origine
déclenche les règles CORS du navigateur et impose de les configurer de façon cohérente côté Spring
Security et côté client Keycloak.

## Considered Options

* Origine unique, routage par chemin derrière un reverse proxy
* Un sous-domaine par service, avec configuration CORS explicite

## Decision Outcome

Chosen option: **origine unique avec routage par chemin**. Caddy sert le frontend à la racine, dirige
`/api/*` vers le backend et `/auth/*` vers Keycloak.

Deux constats ont rendu ce choix évident. Les contrôleurs exposaient déjà tous leurs endpoints sous
`/api/v1` et `/api/v2`, et le client HTTP du frontend construit ses URL en relatif lorsque
`VITE_API_BASE_URL` est vide. Aucune requête n'est donc cross-origin, et la question CORS disparaît
au lieu d'être résolue.

Keycloak est configuré avec `KC_HTTP_RELATIVE_PATH=/auth` et un `KC_HOSTNAME` incluant ce chemin,
conformément à sa documentation pour un déploiement derrière un proxy sur sous-chemin.

## Pros and Cons of the Options

### Origine unique, routage par chemin

* Avantage, car aucune configuration CORS n'est nécessaire : il n'y a pas de requête cross-origin.
* Avantage, car un seul certificat TLS et un seul enregistrement DNS suffisent.
* Avantage, car le frontend n'a aucune URL absolue en dur : changer de domaine ne le recompile pas.
* Neutre, car les services restent isolés, seul le point d'entrée est mutualisé.
* Inconvénient, car Keycloak derrière un sous-chemin demande une configuration précise et peu
  documentée.
* Inconvénient, car un découpage futur par sous-domaines imposerait de revoir le routage.

### Un sous-domaine par service

* Avantage, car chaque service est adressable et déplaçable indépendamment.
* Avantage, car c'est la configuration Keycloak la plus courante, donc la mieux documentée.
* Neutre, car les certificats sont gérés automatiquement dans les deux cas.
* Inconvénient, car il faut maintenir les origines autorisées à trois endroits : Spring Security, le
  client Keycloak et le proxy.
* Inconvénient, car toute erreur de configuration ne se manifeste qu'à l'exécution, dans le
  navigateur, avec des messages peu explicites.
