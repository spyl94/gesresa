Développement d’un système de réservation
===========

Projet de Base de Données niveau L3 proposé par J.-M. Busca.
Sujet (résumé)
=====

Il s’agit de développer en Java une application de réservation de places de spectacle s’appuyant sur une 
base de données Oracle, et de mesurer ses performances dans différents cas de figure.

Le script SQL creabase.sql permet de créer ou recréer la base de données et la peupler.

Le script SQL contraintes.sql permet d’ajouter a posteriori des contraintes au schéma de la base défini par le script creabase.sql.

L’application, développée en Java, permet à un client de réserver une ou plusieurs places pour une 
représentation donnée. Elle offre les fonctionnalités suivantes :
- lister les représentations d’un spectacle situées entre deux dates, en donnant pour chacune des 
représentations les différents tarifs définis,
- lister les places d’une représentation, en donnant pour chacune son numéro, son statut 
(libre/réservé) et son tarif,
- réserver une ou plusieurs places pour une représentation, en spécifiant soit leur numéros, soit 
leur tarif.

L’application doit bien entendu prendre en charge le fait que plusieurs clients peuvent effectuer des 
consultations et réservations de façon concurrente. En particulier, une même place ne doit pas être 
attribuée à plusieurs clients.
