---
status: "accepted"
date: 2026-07-29
decision-makers: KONOVALOVA Sofia, TANCRAY Ibtissam, KASSI Florian
---

# Montée de Spring Cloud AWS en 4.0.2 via son BOM

## Context and Problem Statement

Le backend utilise `spring-cloud-aws-starter-sqs` pour publier sur la file de demandes et consommer
la file de réponses. La version 3.1.1 était épinglée en dur, aux côtés d'un
`software.amazon.awssdk:sqs` figé en 2.31.77.

Au premier démarrage en production, le contexte Spring s'est effondré :

```
Correct the classpath ... compatible versions of
io.awspring.cloud.autoconfigure.sqs.SqsAutoConfiguration
and org.springframework.boot.context.properties.PropertyMapper
```

Spring Boot 4 a modifié l'API de `PropertyMapper` ; l'autoconfiguration SQS de la branche 3.x appelle
une méthode qui n'existe plus. Toute la chaîne d'autoconfiguration échouait, ce qui masquait
d'autres erreurs en aval.

## Considered Options

* Monter Spring Cloud AWS en 4.x, aligné sur Spring Boot 4
* Redescendre Spring Boot en 3.5.x
* Retirer Spring Cloud AWS au profit du SDK AWS brut

## Decision Outcome

Chosen option: **Spring Cloud AWS 4.0.2**, importé via le BOM `spring-cloud-aws-dependencies` plutôt
qu'épinglé dépendance par dépendance.

La branche 4.x est la première annoncée compatible Spring Boot 4 et Spring Framework 7. La 4.0.2 est
retenue plutôt que la 4.1.0, qui vise Spring Boot 4.1.

Le passage par le BOM répond à un second problème : `software.amazon.awssdk:sqs` était épinglé
manuellement, avec un risque de décalage entre les artefacts du SDK selon ce que la couche awspring
tire par transitivité. Le BOM aligne l'ensemble et supprime les versions en dur.

Aucune adaptation de code n'a été nécessaire : `SqsTemplate.send` et `@SqsListener` sont inchangés
entre les deux versions majeures.

## Pros and Cons of the Options

### Spring Cloud AWS 4.x via le BOM

* Avantage, car c'est la version explicitement alignée sur notre version de Spring Boot.
* Avantage, car le BOM garantit la cohérence entre awspring et les artefacts du SDK AWS.
* Avantage, car les prochaines montées de version se font en changeant une seule ligne.
* Neutre, car c'est un changement de version majeure, avec le risque de rupture d'API qu'il implique.
* Inconvénient, car la branche 4.x est récente, donc moins éprouvée.

### Redescendre Spring Boot en 3.5.x

* Avantage, car l'écosystème y est plus mature et mieux documenté.
* Neutre, car nos usages de Spring restent classiques.
* Inconvénient, car le projet utilise déjà les starters modulaires de Boot 4, qu'il faudrait tous
  renommer.
* Inconvénient, car ce serait reculer volontairement pour contourner un problème isolé.

### SDK AWS brut

* Avantage, car il supprime une couche d'abstraction et donc une source d'incompatibilité.
* Neutre, car le SDK est déjà présent comme dépendance transitive.
* Inconvénient, car il faudrait réimplémenter la boucle de consommation, la désérialisation et
  l'acquittement, que `@SqsListener` fournit.
* Inconvénient, car cela déplacerait le risque plutôt que de le supprimer.
