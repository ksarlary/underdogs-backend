---
status: "accepted"
date: 2026-07-29
decision-makers: KONOVALOVA Sofia, TANCRAY Ibtissam, KASSI Florian
---

# Migrations Flyway exécutées comme étape dédiée du déploiement

## Context and Problem Statement

Le schéma est décrit par des migrations Flyway et Hibernate est configuré en `ddl-auto: validate`,
ce qui impose que les tables existent avant la création de l'`EntityManagerFactory`.

Au premier déploiement, l'application a échoué sur `Schema validation: missing table [bets]` alors
que la base était vide et les migrations présentes dans le jar. Le rapport d'évaluation des
conditions de Spring Boot indique pourtant `FlywayAutoConfiguration matched` : le bean
`flywayInitializer` est bien créé.

Le problème n'est donc pas l'activation mais l'ordonnancement. Spring Boot 3 embarquait dans
`FlywayAutoConfiguration` une classe forçant l'`EntityManagerFactory` à dépendre de l'initialiseur
Flyway. Elle n'apparaît plus dans le rapport depuis la modularisation des autoconfigurations en
Spring Boot 4 : Hibernate valide le schéma avant que Flyway n'ait migré.

## Considered Options

* Conteneur Flyway dédié, exécuté avant le backend
* Rétablir l'ordonnancement dans l'application
* Passer Hibernate en `ddl-auto: none`

## Decision Outcome

Chosen option: **conteneur Flyway dédié**. Un service `migrate` utilisant l'image officielle monte
les fichiers SQL depuis le dépôt et s'exécute avant le backend, qui attend
`service_completed_successfully`.

Au-delà du contournement, ce choix transforme les migrations en **étape explicite du déploiement**
plutôt qu'en effet de bord du démarrage applicatif. Le schéma est garanti à jour avant qu'une seule
instance ne démarre, ce qui reste vrai si plusieurs instances du backend démarrent en parallèle.

Effet de bord appréciable : une nouvelle migration ne nécessite pas de reconstruire l'image, un
`git pull` sur la machine hôte suffit.

## Pros and Cons of the Options

### Conteneur Flyway dédié

* Avantage, car il ne dépend d'aucun détail interne de Spring Boot.
* Avantage, car l'échec d'une migration bloque le déploiement au lieu de laisser démarrer une
  application sur un schéma incohérent.
* Avantage, car il reste correct avec plusieurs instances du backend.
* Neutre, car il ajoute un service au fichier compose.
* Inconvénient, car les migrations existent à deux endroits : dans le jar et montées depuis le
  dépôt, avec un risque de divergence si l'un des deux n'est pas à jour.

### Rétablir l'ordonnancement dans l'application

* Avantage, car tout resterait dans l'application, sans service supplémentaire.
* Neutre, car cela suppose de déclarer soi-même un post-processeur de dépendance de beans.
* Inconvénient, car il faut s'appuyer sur des classes internes dont le paquet a changé entre les
  versions majeures — c'est précisément ce qui a cassé.
* Inconvénient, car le problème réapparaîtrait à la prochaine montée de version.

### Hibernate en `ddl-auto: none`

* Avantage, car c'est une seule ligne de configuration.
* Neutre, car les migrations finiraient par s'exécuter, la validation ne bloquant plus le démarrage.
* Inconvénient, car on perd le filet de sécurité qui détecte une divergence entre les entités et le
  schéma.
* Inconvénient, car l'application accepterait de démarrer sur une base incomplète, l'erreur
  n'apparaissant qu'à la première requête.
