# Ibtihale Meftah     4IIR G2

 # Titre du projet : application Online Shopping 
 
# déscriptif du projet :

Ce projet modélise un système complet de commerce en ligne. Il couvre toutes les opérations essentielles qu’un site d’achat doit gérer, depuis la création du compte utilisateur jusqu’à la validation du paiement et le suivi de la commande.

## 1. Gestion des utilisateurs

Le système gère différents types d’utilisateurs.
Chaque utilisateur possède :

des informations personnelles: un état (actif, bloqué, banni)

un compte client associé
Cet état permet de contrôler l’accès aux fonctionnalités.
Un utilisateur bloqué ne peut plus passer de commandes.
Un utilisateur banni ne peut plus accéder au site.

## 2. Comptes clients

Chaque utilisateur possède un compte client qui contient :

-ses informations de contact

-son adresse

-son historique de commandes

-ses moyens de paiement enregistrés

## 3. Panier d’achat

Chaque utilisateur peut ajouter des produits dans un panier d’achat.
Le panier contient plusieurs produits avec :

la quantité souhaitée

le prix unitaire

le total calculé automatiquement

Le panier peut être modifié avant le passage de commande :

ajout de produits

suppression

modification de quantité

## 4. Commande (Order)

Une commande est créée à partir du panier.
Elle regroupe :

les produits choisis

leur quantité

les prix

les taxes éventuelles

le montant total

Une commande suit un cycle de vie défini par son statut :

nouvelle, en préparation, expédiée, livrée, fermée, annulée

Ce cycle permet au client et au vendeur de suivre l’avancement de la livraison.

## 5. Lignes de commande (LineItem)

Chaque commande est composée de LineItems.
Chaque LineItem représente :

un produit, la quantité achetée, le sous-total correspondant

Cette structure permet d’avoir plusieurs produits dans une seule commande.

## 6. Paiement

Chaque commande est liée à un paiement.
Le paiement contient :

le montant, le mode de paiement (carte, PayPal, etc.), la date, l’état du paiement (réussi, échoué, en attente)

Sans paiement validé, la commande ne passe pas en expédition.

## 7. Gestion du statut utilisateur

Le modèle prévoit également la gestion de l'état des utilisateurs :

-Actif : peut tout faire

-Bloqué : accès limité, pas de commande

-Banni : accès totalement retiré

Ce mécanisme assure un contrôle de sécurité et de conformité.

# diagramme de classe 

<img width="724" height="700" alt="12-class-diagram-online-shopping" src="https://github.com/user-attachments/assets/464dc8ec-fcbe-4cf6-aaa5-cffbbcb0c3b0" />

