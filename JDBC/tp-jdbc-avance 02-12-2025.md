# TP JDBC Avancé avec Maven, MySQL et PostgreSQL

**Travaux Pratiques - Ingénierie Informatique et Réseaux (2ᵉ année)**  
**EMSI Maroc - **

---

## Table des matières

1. [Introduction et architecture JDBC](#partie-1)
2. [Création du projet Maven dans IntelliJ IDEA](#partie-2)
3. [Configuration de Maven et du pom.xml](#partie-3)
4. [Préparation de MySQL pour le TP](#partie-4)
5. [Préparation de PostgreSQL pour le TP](#partie-5)
6. [TP Connexion JDBC : DriverManager et Connection](#partie-6)
7. [TP Requêtes SQL avec Statement et ResultSet](#partie-7)
8. [TP Requêtes paramétrées avec PreparedStatement](#partie-8)
9. [TP Bonus : CallableStatement et mini-DAO](#partie-9)
10. [Conclusion et exercices récapitulatifs](#partie-10)

---

<a name="partie-1"></a>
## Partie 1 : Introduction et architecture JDBC

### Objectifs de cette partie
- Comprendre le rôle et la position de JDBC dans une application Java
- Visualiser l'architecture en couches d'une application utilisant JDBC
- Se familiariser avec les concepts de modèles 2-tiers et 3-tiers
- Situer le contexte du TP dans l'écosystème Java/Base de données

### 1.1 Qu'est-ce que JDBC ?

**JDBC** (Java Database Connectivity) est une API standard du langage Java qui permet aux applications Java de se connecter et d'interagir avec des bases de données relationnelles (MySQL, PostgreSQL, Oracle, SQL Server, etc.). 

JDBC joue le rôle d'**interface unifiée** : vous écrivez du code Java qui utilise l'API JDBC, et selon le driver que vous chargez, votre application peut communiquer avec différents systèmes de gestion de bases de données (SGBD) sans changement majeur de code.

**Avantages de JDBC :**
- **Portabilité** : le même code Java fonctionne avec différents SGBD (à quelques nuances près)
- **Standardisation** : toutes les opérations (connexion, requêtes, transactions) suivent les mêmes interfaces
- **Intégration native** : JDBC fait partie intégrante de la plateforme Java SE

### 1.2 Architecture en couches

Dans une application utilisant JDBC, on distingue généralement plusieurs couches :
<img src="jdbc1.png" style="height:464px;margin-right:432px"/>

```
┌─────────────────────────────────────┐
│   Application Java (votre code)    │
│   (Logique métier, IHM, etc.)      │
└─────────────────┬───────────────────┘
                  │
                  ▼
┌─────────────────────────────────────┐
│         API JDBC (java.sql)         │
│  (Interfaces : Connection,          │
│   Statement, ResultSet, etc.)       │
└─────────────────┬───────────────────┘
                  │
                  ▼
┌─────────────────────────────────────┐
│     Driver JDBC (spécifique SGBD)   │
│  (Ex: mysql-connector-java.jar,     │
│       postgresql.jar)                │
└─────────────────┬───────────────────┘
                  │
                  ▼
┌─────────────────────────────────────┐
│     SGBD (MySQL, PostgreSQL...)     │
│   (Serveur de base de données)      │
└─────────────────────────────────────┘
```

**Explication du schéma :**

1. **Application Java** : c'est votre code métier, vos classes, votre logique applicative
2. **API JDBC** : ensemble d'interfaces standardisées (Connection, Statement, PreparedStatement, ResultSet, etc.)
3. **Driver JDBC** : bibliothèque (JAR) fournie par l'éditeur du SGBD qui implémente l'API JDBC pour communiquer avec le SGBD spécifique
4. **SGBD** : le serveur de base de données (MySQL, PostgreSQL, etc.) qui stocke et gère les données

### 1.3 Modèles d'architecture

#### Modèle 2-tiers (Client-Serveur)

Dans une architecture **2-tiers**, l'application cliente (votre programme Java) se connecte directement au serveur de base de données :

```
┌──────────────────┐         ┌──────────────────┐
│  Client Java     │ ◄─────► │   Serveur BDD    │
│  (Application)   │  JDBC   │ (MySQL/PostgreSQL)│
└──────────────────┘         └──────────────────┘
```

**Caractéristiques :**
- Connexion directe entre le client et la base
- Simple à mettre en œuvre pour les petites applications
- Moins sécurisé (les identifiants de connexion sont dans le code client)
- Scalabilité limitée

#### Modèle 3-tiers (Client-Application Server-Database)

Dans une architecture **3-tiers**, une couche intermédiaire (serveur d'application) gère la logique métier et les accès aux données :

```
┌─────────────┐      ┌──────────────────┐      ┌─────────────┐
│   Client    │ ◄──► │  Serveur App     │ ◄──► │  Serveur    │
│ (Interface) │ HTTP │  (Logique métier)│ JDBC │     BDD     │
└─────────────┘      └──────────────────┘      └─────────────┘
```

**Caractéristiques :**
- Séparation des responsabilités (présentation, logique, données)
- Meilleure sécurité (la BDD n'est pas exposée aux clients)
- Meilleure scalabilité (pool de connexions, load balancing)
- Plus complexe à mettre en place

### 1.4 Lien avec le TP

Dans ce TP, vous allez jouer le rôle du **développeur d'applications Java** qui utilise JDBC pour :

- Vous connecter à deux SGBD différents (MySQL et PostgreSQL)
- Exécuter des requêtes SQL depuis Java
- Manipuler les résultats retournés par la base de données
- Comprendre les bonnes pratiques (PreparedStatement, gestion des ressources, etc.)

Vous travaillerez principalement dans un **modèle 2-tiers** pour des raisons pédagogiques, mais les concepts appris sont directement transposables vers des architectures 3-tiers utilisées en entreprise.

### Questions de réflexion

1. **Pourquoi JDBC utilise-t-il des interfaces plutôt que des classes concrètes ?**
2. **Quels sont les avantages d'utiliser un driver JDBC plutôt que de communiquer directement avec le protocole réseau du SGBD ?**
3. **Dans quel cas préfériez-vous un modèle 2-tiers ? Un modèle 3-tiers ?**

---

<a name="partie-2"></a>
## Partie 2 : Création du projet Maven dans IntelliJ IDEA (TP 1)

### Objectifs de cette partie
- Créer un nouveau projet Maven dans IntelliJ IDEA
- Comprendre la structure standard d'un projet Maven
- Configurer le JDK du projet
- Créer une première classe Java et l'exécuter

### 2.1 Ouverture d'IntelliJ IDEA et création du projet

#### Étape 1 : Lancer IntelliJ IDEA
- Ouvrez IntelliJ IDEA sur votre machine
- Si vous avez déjà des projets ouverts, fermez-les (File → Close Project) pour revenir à l'écran d'accueil

#### Étape 2 : Créer un nouveau projet
1. Cliquez sur **New Project** dans l'écran d'accueil
2. Dans la fenêtre qui s'ouvre :
   - **Generators** : sélectionnez **Maven** dans la liste de gauche
   - **Name** : tapez `TP_JDBC_Avance`
   - **Location** : choisissez un emplacement sur votre disque (par exemple `C:\Users\VotreNom\IdeaProjects\TP_JDBC_Avance`)
   - **JDK** : sélectionnez votre JDK (Java 11 ou supérieur recommandé)
   - **Add sample code** : décochez cette option (nous allons créer notre propre code)
3. Développez la section **Advanced Settings** (en bas de la fenêtre)
   - **GroupId** : tapez `ma.emsi`
   - **ArtifactId** : tapez `tp-jdbc-avance`
   - **Version** : laissez `1.0-SNAPSHOT`
4. Cliquez sur **Create**

RQ: dans le fichier pom.xml on fait /*injection des dependances* ça veut dire ? pour demarrer il faut mlire des fichier de dependances / son role est de nous indiquer qu'elle fichier je doit parametrer
injection sql / invrtion de controle / 1ere qst: quel type de bdd ?

#### Étape 3 : Patienter pendant l'indexation
IntelliJ va créer la structure du projet et indexer les bibliothèques. Attendez que la barre de progression en bas à droite disparaisse.

### 2.2 Découverte de la structure Maven

Une fois le projet créé, vous devriez voir dans le panneau **Project** (à gauche) la structure suivante :

```
TP_JDBC_Avance/
├── src/
│   ├── main/
│   │   ├── java/           ← Votre code source principal
│   │   └── resources/      ← Fichiers de configuration
│   └── test/
│       ├── java/           ← Vos tests unitaires
│       └── resources/
├── pom.xml                 ← Fichier de configuration Maven
└── .idea/                  ← Configuration IntelliJ (ignoré par Git)
```

**Explication des dossiers :**

- **src/main/java** : c'est ici que vous allez créer vos classes Java principales
- **src/main/resources** : fichiers de configuration, fichiers properties, etc.
- **src/test/java** : classes de tests JUnit (nous ne l'utiliserons pas dans ce TP)
- **pom.xml** : fichier central de Maven qui décrit le projet et ses dépendances

### 2.3 Vérification du SDK du projet

#### Étape 1 : Ouvrir les paramètres du projet
- Allez dans **File → Project Structure** (ou appuyez sur `Ctrl+Alt+Shift+S`)
- Dans la section **Project**, vérifiez que :
  - **SDK** : affiche votre JDK (ex: "11" ou "17")
  - **Language level** : correspond à votre version Java

#### Étape 2 : Vérifier les modules
- Allez dans la section **Modules**
- Vous devriez voir votre module `tp-jdbc-avance`
- Vérifiez que les dossiers `src/main/java` et `src/main/resources` sont bien marqués comme **Sources** et **Resources** (ils apparaissent en bleu)

Cliquez sur **OK** pour fermer la fenêtre.

### 2.4 Création de la première classe Main

#### Étape 1 : Créer un package
1. Faites un clic droit sur le dossier **src/main/java**
2. Sélectionnez **New → Package**
3. Tapez `ma.emsi.tp` et validez

#### Étape 2 : Créer la classe Main
1. Faites un clic droit sur le package `ma.emsi.tp`
2. Sélectionnez **New → Java Class**
3. Tapez `Main` et validez

#### Étape 3 : Écrire le code de la classe Main

Tapez le code suivant dans la classe `Main` :

```java
package ma.emsi.tp;

/**
 * Classe principale pour tester le projet Maven et JDBC
 */
public class Main {
    
    public static void main(String[] args) {
        System.out.println("=== Bienvenue dans le TP JDBC Avancé ===");
        System.out.println("Projet Maven initialisé avec succès !");
        System.out.println("Prêt à explorer JDBC avec MySQL et PostgreSQL.");
        
        // Vérification de la version Java
        String javaVersion = System.getProperty("java.version");
        System.out.println("\nVersion Java utilisée : " + javaVersion);
    }
}
```

### 2.5 Configuration et exécution

#### Étape 1 : Créer une Run Configuration
1. Faites un clic droit n'importe où dans le code de la classe `Main`
2. Sélectionnez **Run 'Main.main()'** (ou appuyez sur `Ctrl+Shift+F10`)

IntelliJ va compiler et exécuter votre programme.

#### Étape 2 : Observer la sortie
Dans le panneau **Run** en bas de l'écran, vous devriez voir :

```
=== Bienvenue dans le TP JDBC Avancé ===
Projet Maven initialisé avec succès !
Prêt à explorer JDBC avec MySQL et PostgreSQL.

Version Java utilisée : 23.0.2

Process finished with exit code 0
```

**Félicitations !** Votre projet Maven est opérationnel et prêt pour JDBC.

### 2.6 Enregistrer la Run Configuration (optionnel)

Pour ne pas avoir à recréer la configuration à chaque fois :

1. Allez dans **Run → Edit Configurations...**
2. Vous devriez voir votre configuration `Main` dans la liste
3. Vous pouvez la renommer en `TP JDBC - Main` si vous le souhaitez
4. Cliquez sur **OK**

Désormais, vous pourrez exécuter votre programme en cliquant sur le bouton vert **▶ Run** dans la barre d'outils.

### Questions et exercices

1. **Quelle différence entre un projet Maven et un projet Java simple ?**
   - *Indice : pensez à la gestion des dépendances externes*
   - dans un projet java simple on a pas de dependances externes à gérer contrairement à un projet Maven il sert à 

2. **Exercice** : Modifiez la classe `Main` pour afficher également le nom du système d'exploitation (`System.getProperty("os.name")`)

3. **Exercice** : Créez une deuxième classe `Utils` dans le même package avec une méthode statique `afficherInfosSysteme()` qui affiche diverses propriétés système, puis appelez cette méthode depuis `Main`

---

<a name="partie-3"></a>
## Partie 3 : Configuration de Maven et du pom.xml (TP 2)

### Objectifs de cette partie
- Comprendre le rôle de Maven dans la gestion de projet
- Maîtriser la structure et les principales balises du fichier pom.xml
- Ajouter les dépendances JDBC pour MySQL et PostgreSQL
- Recharger le projet Maven pour intégrer les dépendances

### 3.1 Rappel : Qu'est-ce que Maven ?

**Maven** est un outil de gestion et d'automatisation de construction de projets Java. Il permet principalement de :

- **Gérer les dépendances** : télécharger automatiquement les bibliothèques (JAR) nécessaires depuis des dépôts centraux
- **Standardiser la structure** : tous les projets Maven suivent la même organisation de dossiers
- **Automatiser les tâches** : compilation, tests, packaging (création de JAR/WAR), déploiement
- **Gérer le cycle de vie** : phases prédéfinies (compile, test, package, install, deploy)

**Avantages pour notre TP :**
- Pas besoin de télécharger manuellement les drivers JDBC
- Pas besoin de configurer manuellement le classpath
- Facilité de partage du projet (le `pom.xml` suffit pour recréer l'environnement)

### 3.2 Structure du fichier pom.xml

#### Étape 1 : Ouvrir le fichier pom.xml
Dans le panneau **Project**, double-cliquez sur `pom.xml` à la racine du projet.

Vous devriez voir un fichier similaire à ceci :

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>ma.emsi</groupId>
    <artifactId>tp-jdbc-avance</artifactId>
    <version>1.0-SNAPSHOT</version>

</project>
```

#### Explication des balises principales

| Balise | Description |
|--------|-------------|
| `<modelVersion>` | Version du modèle POM (toujours 4.0.0) |
| `<groupId>` | Identifiant du groupe/organisation (souvent un nom de domaine inversé) |
| `<artifactId>` | Identifiant unique du projet |
| `<version>` | Version du projet (SNAPSHOT = version en développement) |
| `<dependencies>` | Liste des bibliothèques externes nécessaires |
| `<build>` | Configuration du processus de construction (plugins, ressources) |
| `<properties>` | Variables réutilisables dans le POM |

### 3.3 Configuration de la version Java

#### Étape 1 : Ajouter la section properties
Juste après la balise `<version>`, ajoutez :

```xml
<properties>
    <maven.compiler.source>11</maven.compiler.source>
    <maven.compiler.target>11</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
</properties>
```

**Explication :**
- `maven.compiler.source` : version Java du code source
- `maven.compiler.target` : version Java cible pour la compilation
- `project.build.sourceEncoding` : encodage des fichiers (UTF-8 recommandé)

### 3.4 Ajout de la dépendance MySQL

#### Étape 1 : Créer la section dependencies
Après la section `<properties>`, ajoutez :

```xml
<dependencies>
    <!-- Driver JDBC pour MySQL -->
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>8.0.33</version>
    </dependency>
</dependencies>
```

**Explication :**
- `groupId`, `artifactId`, `version` : coordonnées Maven de la bibliothèque
- Maven va télécharger automatiquement le fichier JAR correspondant depuis le dépôt central Maven (https://repo.maven.apache.org)

#### Note sur la version
La version `8.0.33` est une version stable récente. Vous pouvez vérifier les dernières versions sur [mvnrepository.com](https://mvnrepository.com/artifact/mysql/mysql-connector-java).

### 3.5 Ajout de la dépendance PostgreSQL

#### Étape 1 : Ajouter PostgreSQL dans la même section dependencies

Juste après la dépendance MySQL, ajoutez :

```xml
    <!-- Driver JDBC pour PostgreSQL -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <version>42.6.0</version>
    </dependency>
```

Votre section `<dependencies>` complète ressemble maintenant à :

```xml
<dependencies>
    <!-- Driver JDBC pour MySQL -->
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>8.0.33</version>
    </dependency>

    <!-- Driver JDBC pour PostgreSQL -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <version>42.6.0</version>
    </dependency>
</dependencies>
```

### 3.6 Rechargement du projet Maven

#### Étape 1 : Recharger les dépendances
Dès que vous sauvegardez le fichier `pom.xml`, IntelliJ peut afficher une petite icône Maven dans le coin supérieur droit de l'éditeur.

**Méthode 1 : Via l'icône Maven**
- Cliquez sur l'icône 🔄 (Load Maven Changes / Reload All Maven Projects)

**Méthode 2 : Via le panneau Maven**
1. Ouvrez le panneau **Maven** (View → Tool Windows → Maven)
2. Cliquez sur l'icône 🔄 (Reload All Maven Projects) dans la barre d'outils du panneau

#### Étape 2 : Vérifier le téléchargement
Dans le panneau en bas (onglet **Build**), vous devriez voir Maven télécharger les dépendances :

```
Downloading from central: https://repo.maven.apache.org/maven2/mysql/mysql-connector-java/8.0.33/...
Downloaded from central: ...
```

**Note :** Le téléchargement peut prendre quelques secondes selon votre connexion Internet.

#### Étape 3 : Vérifier l'ajout dans External Libraries
1. Dans le panneau **Project**, développez **External Libraries**
2. Vous devriez voir apparaître :
   - `Maven: mysql:mysql-connector-java:8.0.33`
   - `Maven: org.postgresql:postgresql:42.6.0`

**Félicitations !** Les drivers JDBC sont maintenant disponibles dans votre projet.

### 3.7 POM.xml complet pour le TP

Voici le fichier `pom.xml` complet que vous devriez avoir à ce stade :

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>ma.emsi</groupId>
    <artifactId>tp-jdbc-avance</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>11</maven.compiler.source>
        <maven.compiler.target>11</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <!-- Driver JDBC pour MySQL -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.33</version>
        </dependency>

        <!-- Driver JDBC pour PostgreSQL -->
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <version>42.6.0</version>
        </dependency>
    </dependencies>

</project>
```

### 3.8 Comprendre les scopes de dépendance

Maven propose différents **scopes** qui définissent quand une dépendance est utilisée :

| Scope | Description | Exemple d'usage |
|-------|-------------|-----------------|
| **compile** (défaut) | Disponible partout (compilation, tests, exécution) | Drivers JDBC, bibliothèques métier |
| **test** | Uniquement pour les tests | JUnit, Mockito |
| **runtime** | Non nécessaire à la compilation, mais à l'exécution | Drivers JDBC (optionnel) |
| **provided** | Fourni par l'environnement d'exécution | Servlet API (fourni par Tomcat) |

**Dans notre TP**, nous utilisons le scope **compile** (par défaut, pas besoin de le spécifier) car nous allons utiliser les classes des drivers directement dans notre code.

### 🚨 Erreurs fréquentes et débogage

#### Erreur 1 : "Cannot resolve dependency"
**Symptôme** : Maven affiche une erreur rouge dans le `pom.xml`

**Solutions :**
- Vérifiez votre connexion Internet
- Vérifiez l'orthographe des coordonnées Maven (groupId, artifactId, version)
- Essayez de forcer le re-téléchargement : Maven → Reimport (dans le panneau Maven)
- Supprimez le dossier `~/.m2/repository` (cache Maven) et rechargez

#### Erreur 2 : "Project SDK is not defined"
**Symptôme** : Message d'erreur en haut de l'éditeur

**Solution :**
- Allez dans File → Project Structure → Project
- Sélectionnez un SDK dans la liste déroulante
- Si aucun SDK n'apparaît, cliquez sur **Add SDK → Download JDK**

#### Erreur 3 : Les dépendances n'apparaissent pas dans External Libraries
**Solution :**
- Faites un clic droit sur le projet → Maven → Reload Project
- Invalidez les caches : File → Invalidate Caches / Restart

### Questions et exercices

1. **Quelle est la différence entre `groupId` et `artifactId` dans une dépendance Maven ?**

2. **Pourquoi est-il préférable d'utiliser Maven plutôt que de télécharger les JAR manuellement ?**

3. **Exercice** : Ajoutez une troisième dépendance au projet : `slf4j-simple` (pour les logs). Cherchez les coordonnées Maven sur mvnrepository.com et ajoutez-la au `pom.xml`.

---

<a name="partie-4"></a>
## Partie 4 : Préparation de MySQL pour le TP (TP 3 – Partie A)

### Objectifs de cette partie
- Installer et configurer MySQL Server (si ce n'est pas déjà fait)
- Créer une base de données dédiée au TP
- Créer un utilisateur avec les privilèges appropriés
- Créer une table de test "etudiants"
- Comprendre la structure d'une URL de connexion JDBC MySQL

### 4.1 Installation de MySQL (si nécessaire)

#### Si MySQL n'est pas installé sur votre machine

**Option 1 : Installation native**
1. Téléchargez MySQL Community Server depuis [dev.mysql.com/downloads/mysql](https://dev.mysql.com/downloads/mysql/)
2. Lancez l'installateur
3. Choisissez "Developer Default" ou "Server only"
4. Définissez un mot de passe root (notez-le bien !)
5. Terminez l'installation

**Option 2 : Utiliser Docker (recommandé si vous avez Docker)**
```bash
docker run --name mysql-tp-jdbc -e MYSQL_ROOT_PASSWORD=root123 -p 3306:3306 -d mysql:8.0
```

#### Vérification de l'installation
Ouvrez un terminal et tapez :
```bash
mysql --version
```

Vous devriez voir quelque chose comme :
```
mysql  Ver 8.0.33 for Win64 on x86_64
```

### 4.2 Connexion à MySQL

#### Méthode 1 : Via MySQL Workbench (interface graphique)
1. Lancez MySQL Workbench
2. Créez une nouvelle connexion :
   - Connection Name : `TP JDBC Local`
   - Hostname : `localhost`
   - Port : `3306`
   - Username : `root`
   - Password : (votre mot de passe root)
3. Cliquez sur **Test Connection**, puis **OK**
4. Double-cliquez sur la connexion pour l'ouvrir

#### Méthode 2 : Via ligne de commande
Ouvrez un terminal et tapez :
```bash
mysql -u root -p
```
Entrez votre mot de passe root quand demandé.

Vous devriez voir le prompt MySQL :
```
mysql>
```

### 4.3 Création de la base de données

#### Étape 1 : Créer la base de données
Dans MySQL Workbench ou dans le terminal MySQL, exécutez :

```sql
-- Création de la base de données pour le TP
CREATE DATABASE IF NOT EXISTS tp_jdbc
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
```

**Explication :**
- `IF NOT EXISTS` : ne crée la base que si elle n'existe pas déjà
- `CHARACTER SET utf8mb4` : supporte tous les caractères Unicode (y compris émojis)
- `COLLATE utf8mb4_unicode_ci` : règles de tri et comparaison insensibles à la casse

#### Étape 2 : Vérifier la création
```sql
SHOW DATABASES;
```

Vous devriez voir `tp_jdbc` dans la liste.

### 4.4 Création d'un utilisateur dédié

#### Étape 1 : Créer l'utilisateur
Pour des raisons de sécurité, nous allons créer un utilisateur spécifique pour notre application plutôt que d'utiliser root.

```sql
-- Création d'un utilisateur pour l'application
CREATE USER IF NOT EXISTS 'tp_user'@'localhost' 
IDENTIFIED BY 'tp_password123';
```

**Explication :**
- `'tp_user'@'localhost'` : l'utilisateur ne peut se connecter que depuis la machine locale
- `'tp_password123'` : mot de passe (changez-le pour quelque chose de plus sécurisé en production !)

#### Étape 2 : Accorder les privilèges
```sql
-- Accorder tous les privilèges sur la base tp_jdbc
GRANT ALL PRIVILEGES ON tp_jdbc.* TO 'tp_user'@'localhost';

-- Appliquer les changements
FLUSH PRIVILEGES;
```

**Explication :**
- `ALL PRIVILEGES` : SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, etc.
- `tp_jdbc.*` : toutes les tables de la base tp_jdbc
- `FLUSH PRIVILEGES` : recharge les privilèges pour qu'ils soient actifs immédiatement

#### Étape 3 : Vérifier les privilèges
```sql
SHOW GRANTS FOR 'tp_user'@'localhost';
```

Vous devriez voir :
```
GRANT ALL PRIVILEGES ON `tp_jdbc`.* TO `tp_user`@`localhost`
```

### 4.5 Création de la table "etudiants"

#### Étape 1 : Sélectionner la base de données
```sql
USE tp_jdbc;
```

#### Étape 2 : Créer la table
```sql
-- Création de la table etudiants
CREATE TABLE IF NOT EXISTS etudiants (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    date_naissance DATE,
    note_moyenne DECIMAL(4,2),
    date_inscription TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_note CHECK (note_moyenne >= 0 AND note_moyenne <= 20)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

**Explication des colonnes :**
- `id` : clé primaire auto-incrémentée
- `nom`, `prenom` : chaînes de caractères obligatoires
- `email` : unique (pas de doublons) et obligatoire
- `date_naissance` : type DATE (format YYYY-MM-DD)
- `note_moyenne` : décimal avec 2 chiffres après la virgule
- `date_inscription` : horodatage automatique à l'insertion
- `CONSTRAINT chk_note` : contrainte de validation (note entre 0 et 20)

#### Étape 3 : Vérifier la structure
```sql
DESCRIBE etudiants;
```

Ou :
```sql
SHOW CREATE TABLE etudiants;
```

#### Étape 4 : Insérer des données de test
```sql
-- Insertion de quelques étudiants de test
INSERT INTO etudiants (nom, prenom, email, date_naissance, note_moyenne) VALUES
('Alami', 'Fatima', 'f.alami@emsi.ma', '2003-05-15', 15.50),
('Bennani', 'Mohammed', 'm.bennani@emsi.ma', '2002-11-22', 13.75),
('Chakir', 'Amina', 'a.chakir@emsi.ma', '2003-08-30', 16.20),
('Dahane', 'Youssef', 'y.dahane@emsi.ma', '2002-03-10', 12.90),
('El Fassi', 'Sara', 's.elfassi@emsi.ma', '2003-01-25', 17.10);
```

#### Étape 5 : Vérifier l'insertion
```sql
SELECT * FROM etudiants;
```

Vous devriez voir les 5 étudiants insérés.

### 4.6 Comprendre l'URL de connexion JDBC MySQL

Pour se connecter à MySQL via JDBC, nous utiliserons une chaîne de connexion (URL) au format suivant :

```
jdbc:mysql://[host]:[port]/[database]?[paramètres]
```

**Exemple concret pour notre TP :**
```
jdbc:mysql://localhost:3306/tp_jdbc?useSSL=false&serverTimezone=UTC
```

**Décomposition :**
- `jdbc:mysql://` : protocole JDBC pour MySQL
- `localhost` : serveur (127.0.0.1 ou nom d'hôte)
- `3306` : port MySQL par défaut
- `tp_jdbc` : nom de la base de données
- `useSSL=false` : désactive SSL pour les tests locaux (⚠️ à activer en production)
- `serverTimezone=UTC` : définit le fuseau horaire (évite des warnings avec MySQL 8+)

**Autres paramètres utiles :**
```
?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8
```

### 4.7 Test de connexion depuis le terminal

Avant de passer au code Java, testons la connexion avec l'utilisateur créé :

```bash
mysql -u tp_user -ptp_password123 -h localhost tp_jdbc
```

Si vous êtes connecté, tapez :
```sql
SELECT * FROM etudiants;
```

Si vous voyez les données, tout est prêt pour JDBC !

### 🚨 Erreurs fréquentes et débogage

#### Erreur 1 : "Access denied for user 'tp_user'@'localhost'"
**Causes possibles :**
- Mauvais mot de passe
- Utilisateur pas créé correctement
- Privilèges non accordés

**Solution :**
```sql
-- Se connecter en root et vérifier
SELECT User, Host FROM mysql.user WHERE User='tp_user';

-- Si absent, recréer l'utilisateur
DROP USER IF EXISTS 'tp_user'@'localhost';
CREATE USER 'tp_user'@'localhost' IDENTIFIED BY 'tp_password123';
GRANT ALL PRIVILEGES ON tp_jdbc.* TO 'tp_user'@'localhost';
FLUSH PRIVILEGES;
```

#### Erreur 2 : "Unknown database 'tp_jdbc'"
**Solution :**
```sql
-- Vérifier les bases existantes
SHOW DATABASES;

-- Recréer si nécessaire
CREATE DATABASE tp_jdbc;
```

#### Erreur 3 : "Table 'etudiants' already exists"
**Solution :**
```sql
-- Supprimer et recréer
DROP TABLE IF EXISTS etudiants;
-- Puis relancer le CREATE TABLE
```

#### Erreur 4 : Port 3306 déjà utilisé
**Symptôme :** MySQL ne démarre pas

**Solution :**
- Vérifier quel processus utilise le port : `netstat -ano | findstr 3306` (Windows)
- Arrêter MySQL existant : `net stop MySQL80` (Windows)
- Ou changer le port de MySQL dans my.ini/my.cnf

### Questions et exercices

1. **Pourquoi est-il recommandé de créer un utilisateur dédié plutôt que d'utiliser root ?**

2. **Que signifie la contrainte `UNIQUE` sur la colonne email ?**

3. **Exercice** : Ajoutez une colonne `ville VARCHAR(50)` à la table etudiants. Utilisez la commande `ALTER TABLE`.

4. **Exercice** : Écrivez une requête SQL pour afficher uniquement les étudiants ayant une note moyenne supérieure ou égale à 15.

---

<a name="partie-5"></a>
## Partie 5 : Préparation de PostgreSQL pour le TP (TP 3 – Partie B)

### Objectifs de cette partie
- Installer et configurer PostgreSQL Server (si nécessaire)
- Créer une base de données et un utilisateur PostgreSQL
- Créer la même table "etudiants" que pour MySQL
- Identifier les différences entre MySQL et PostgreSQL
- Comprendre l'URL de connexion JDBC PostgreSQL

### 5.1 Installation de PostgreSQL (si nécessaire)

#### Si PostgreSQL n'est pas installé sur votre machine

**Option 1 : Installation native**
1. Téléchargez PostgreSQL depuis [postgresql.org/download](https://www.postgresql.org/download/)
2. Lancez l'installateur
3. Notez bien le mot de passe du superutilisateur `postgres`
4. Port par défaut : `5432`
5. Terminez l'installation

**Option 2 : Utiliser Docker (recommandé)**
```bash
docker run --name postgres-tp-jdbc -e POSTGRES_PASSWORD=postgres123 -p 5432:5432 -d postgres:15
```

#### Vérification de l'installation
```bash
psql --version
```

Résultat attendu :
```
psql (PostgreSQL) 15.3
```

### 5.2 Connexion à PostgreSQL

#### Méthode 1 : Via pgAdmin (interface graphique)
1. Lancez pgAdmin
2. Créez un nouveau serveur :
   - Name : `TP JDBC Local`
   - Host : `localhost`
   - Port : `5432`
   - Username : `postgres`
   - Password : (votre mot de passe postgres)
3. Sauvegardez

#### Méthode 2 : Via ligne de commande (psql)
```bash
psql -U postgres -h localhost
```

Entrez le mot de passe quand demandé.

Vous devriez voir le prompt PostgreSQL :
```
postgres=#
```

### 5.3 Création de la base de données

#### Étape 1 : Créer la base de données
```sql
-- Création de la base de données pour le TP
CREATE DATABASE tp_jdbc
    WITH 
    ENCODING = 'UTF8'
    LC_COLLATE = 'fr_FR.UTF-8'
    LC_CTYPE = 'fr_FR.UTF-8'
    TEMPLATE = template0;
```

**Note :** Si vous avez une erreur avec les locales `fr_FR.UTF-8`, utilisez simplement :
```sql
CREATE DATABASE tp_jdbc
    WITH ENCODING = 'UTF8';
```

#### Étape 2 : Vérifier la création
```sql
\l
```
ou
```sql
SELECT datname FROM pg_database;
```

Vous devriez voir `tp_jdbc` dans la liste.

### 5.4 Création d'un utilisateur dédié

#### Étape 1 : Créer l'utilisateur
```sql
-- Création d'un utilisateur pour l'application
CREATE USER tp_user WITH PASSWORD 'tp_password123';
```

#### Étape 2 : Accorder les privilèges
```sql
-- Accorder tous les privilèges sur la base tp_jdbc
GRANT ALL PRIVILEGES ON DATABASE tp_jdbc TO tp_user;

-- Se connecter à la base tp_jdbc (important !)
\c tp_jdbc

-- Accorder les privilèges sur le schéma public
GRANT ALL ON SCHEMA public TO tp_user;

-- Accorder les privilèges sur les tables futures
ALTER DEFAULT PRIVILEGES IN SCHEMA public 
GRANT ALL ON TABLES TO tp_user;

ALTER DEFAULT PRIVILEGES IN SCHEMA public 
GRANT ALL ON SEQUENCES TO tp_user;
```

**⚠️ Important :** PostgreSQL gère les privilèges différemment de MySQL. Il faut accorder :
1. Les privilèges sur la base de données
2. Les privilèges sur le schéma (généralement `public`)
3. Les privilèges sur les tables

#### Étape 3 : Vérifier les privilèges
```sql
\du
```

Vous devriez voir l'utilisateur `tp_user` dans la liste.

### 5.5 Création de la table "etudiants"

#### Étape 1 : Se connecter à la base tp_jdbc
```sql
\c tp_jdbc
```

#### Étape 2 : Créer la table
```sql
-- Création de la table etudiants (version PostgreSQL)
CREATE TABLE IF NOT EXISTS etudiants (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    date_naissance DATE,
    note_moyenne NUMERIC(4,2),
    date_inscription TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_note CHECK (note_moyenne >= 0 AND note_moyenne <= 20)
);
```

**⚠️ Différences avec MySQL :**
- `SERIAL` au lieu de `INT AUTO_INCREMENT` (pour l'auto-incrémentation)
- `NUMERIC` au lieu de `DECIMAL` (synonymes, mais NUMERIC est plus standard SQL)
- `CURRENT_TIMESTAMP` fonctionne de la même manière

#### Étape 3 : Accorder les privilèges sur la table à tp_user
```sql
-- Important : accorder les droits sur la table créée
GRANT ALL PRIVILEGES ON TABLE etudiants TO tp_user;
GRANT USAGE, SELECT ON SEQUENCE etudiants_id_seq TO tp_user;
```

**Note :** PostgreSQL crée automatiquement une séquence `etudiants_id_seq` pour gérer l'auto-incrémentation de `id`.

#### Étape 4 : Vérifier la structure
```sql
\d etudiants
```

Ou :
```sql
SELECT column_name, data_type, is_nullable 
FROM information_schema.columns 
WHERE table_name = 'etudiants';
```

#### Étape 5 : Insérer des données de test
```sql
-- Insertion de quelques étudiants de test
INSERT INTO etudiants (nom, prenom, email, date_naissance, note_moyenne) VALUES
('Alami', 'Fatima', 'f.alami@emsi.ma', '2003-05-15', 15.50),
('Bennani', 'Mohammed', 'm.bennani@emsi.ma', '2002-11-22', 13.75),
('Chakir', 'Amina', 'a.chakir@emsi.ma', '2003-08-30', 16.20),
('Dahane', 'Youssef', 'y.dahane@emsi.ma', '2002-03-10', 12.90),
('El Fassi', 'Sara', 's.elfassi@emsi.ma', '2003-01-25', 17.10);
```

#### Étape 6 : Vérifier l'insertion
```sql
SELECT * FROM etudiants;
```

### 5.6 Comprendre l'URL de connexion JDBC PostgreSQL

Pour PostgreSQL, l'URL de connexion JDBC a le format suivant :

```
jdbc:postgresql://[host]:[port]/[database]?[paramètres]
```

**Exemple concret pour notre TP :**
```
jdbc:postgresql://localhost:5432/tp_jdbc
```

**Décomposition :**
- `jdbc:postgresql://` : protocole JDBC pour PostgreSQL
- `localhost` : serveur
- `5432` : port PostgreSQL par défaut
- `tp_jdbc` : nom de la base de données

**Paramètres optionnels utiles :**
```
jdbc:postgresql://localhost:5432/tp_jdbc?currentSchema=public&ssl=false
```

### 5.7 Principales différences MySQL vs PostgreSQL

| Aspect | MySQL | PostgreSQL |
|--------|-------|------------|
| **Auto-incrémentation** | `INT AUTO_INCREMENT` | `SERIAL` ou `IDENTITY` |
| **Type décimal** | `DECIMAL(p,s)` | `NUMERIC(p,s)` ou `DECIMAL(p,s)` |
| **Chaînes** | `VARCHAR`, `TEXT` | `VARCHAR`, `TEXT`, `CHAR` |
| **Port par défaut** | 3306 | 5432 |
| **Gestion des privilèges** | Base → Tables | Base → Schéma → Tables |
| **Sensibilité à la casse** | Insensible (par défaut) | Sensible (identifiants en minuscules) |
| **Booléens** | `TINYINT(1)` ou `BOOLEAN` | `BOOLEAN` (vrai type booléen) |
| **Commande d'aide** | `HELP` ou `?` | `\?` |
| **Lister les tables** | `SHOW TABLES;` | `\dt` ou `SELECT * FROM pg_tables;` |

### 5.8 Test de connexion depuis le terminal

Testons la connexion avec l'utilisateur `tp_user` :

```bash
psql -U tp_user -h localhost -d tp_jdbc
```

Entrez le mot de passe : `tp_password123`

Si vous êtes connecté, tapez :
```sql
SELECT * FROM etudiants;
```

Si vous voyez les données, tout est prêt !

### 🚨 Erreurs fréquentes et débogage

#### Erreur 1 : "FATAL: password authentication failed for user 'tp_user'"
**Solution :**
```sql
-- Se connecter en postgres et vérifier
\c postgres postgres
SELECT usename FROM pg_user WHERE usename='tp_user';

-- Si absent, recréer
DROP USER IF EXISTS tp_user;
CREATE USER tp_user WITH PASSWORD 'tp_password123';

-- Redonner les privilèges
\c tp_jdbc
GRANT ALL PRIVILEGES ON DATABASE tp_jdbc TO tp_user;
GRANT ALL ON SCHEMA public TO tp_user;
```

#### Erreur 2 : "ERROR: permission denied for table etudiants"
**Cause :** L'utilisateur n'a pas les droits sur la table

**Solution :**
```sql
-- Se connecter en postgres
\c tp_jdbc postgres

-- Accorder les droits
GRANT ALL PRIVILEGES ON TABLE etudiants TO tp_user;
GRANT USAGE, SELECT ON SEQUENCE etudiants_id_seq TO tp_user;
```

#### Erreur 3 : "FATAL: database 'tp_jdbc' does not exist"
**Solution :**
```sql
-- Lister les bases
\l

-- Créer si nécessaire
CREATE DATABASE tp_jdbc;
```

#### Erreur 4 : "ERROR: relation 'etudiants' already exists"
**Solution :**
```sql
DROP TABLE IF EXISTS etudiants CASCADE;
-- Puis relancer le CREATE TABLE
```

### Questions et exercices

1. **Quelle est la principale différence syntaxique entre MySQL et PostgreSQL pour l'auto-incrémentation ?**

2. **Pourquoi PostgreSQL nécessite-t-il d'accorder des privilèges sur le schéma `public` en plus de la base de données ?**

3. **Exercice** : Écrivez une requête qui fonctionne à la fois sur MySQL et PostgreSQL pour compter le nombre d'étudiants dont le nom commence par 'A'.

4. **Exercice** : Dans PostgreSQL, utilisez la commande `\d etudiants` pour afficher la structure de la table. Identifiez le nom de la séquence créée automatiquement.

---

<a name="partie-6"></a>
## Partie 6 : TP Connexion JDBC : DriverManager et Connection (TP 4)

### Objectifs de cette partie
- Comprendre le rôle de DriverManager et Connection dans JDBC
- Se connecter à MySQL depuis Java
- Se connecter à PostgreSQL depuis Java
- Maîtriser le try-with-resources pour la gestion des ressources
- Diagnostiquer les erreurs de connexion courantes

### 6.1 Rappels théoriques

#### Le DriverManager
`DriverManager` est une classe de l'API JDBC (`java.sql.DriverManager`) qui joue le rôle de **gestionnaire de pilotes**. Son rôle principal :

- Charger et gérer les drivers JDBC disponibles
- Établir une connexion à la base de données via une URL JDBC
- Sélectionner automatiquement le driver approprié selon l'URL

#### L'interface Connection
`Connection` est une interface (`java.sql.Connection`) qui représente une **session avec la base de données**. Elle permet de :

- Créer des instructions SQL (Statement, PreparedStatement, CallableStatement)
- Gérer les transactions (commit, rollback)
- Obtenir des métadonnées sur la base
- Fermer la connexion quand elle n'est plus nécessaire

**⚠️ Important :** Une Connection est une ressource qu'il faut **toujours fermer** après utilisation pour éviter les fuites mémoire et les connexions bloquées.

### 6.2 TP : Connexion à MySQL

#### Étape 1 : Créer le package de connexion
Dans IntelliJ :
1. Faites un clic droit sur `src/main/java/ma/emsi/tp`
2. New → Package
3. Nommez-le `connexion`

#### Étape 2 : Créer la classe TestConnexionMySQL
1. Clic droit sur le package `connexion`
2. New → Java Class
3. Nommez-la `TestConnexionMySQL`

#### Étape 3 : Écrire le code de connexion

Tapez le code suivant (commentaires inclus pour la pédagogie) :

```java
package ma.emsi.tp.connexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe de test pour la connexion JDBC à MySQL
 */
public class TestConnexionMySQL {
    
    // Paramètres de connexion à MySQL
    private static final String URL = "jdbc:mysql://localhost:3306/tp_jdbc";
    private static final String USER = "tp_user";
    private static final String PASSWORD = "tp_password123";
    
    public static void main(String[] args) {
        System.out.println("=== Test de connexion à MySQL ===\n");
        
        // Try-with-resources : la connexion sera automatiquement fermée
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            
            // Si on arrive ici, la connexion a réussi
            System.out.println("✓ Connexion réussie à MySQL !");
            
            // Affichage d'informations sur la connexion
            System.out.println("URL : " + URL);
            System.out.println("Utilisateur : " + USER);
            System.out.println("Base de données : " + connection.getCatalog());
            System.out.println("Driver : " + connection.getMetaData().getDriverName());
            System.out.println("Version du driver : " + connection.getMetaData().getDriverVersion());
            
        } catch (SQLException e) {
            // En cas d'erreur de connexion
            System.err.println("✗ Échec de la connexion à MySQL !");
            System.err.println("Raison : " + e.getMessage());
            System.err.println("Code d'erreur SQL : " + e.getErrorCode());
            e.printStackTrace();
        }
        
        System.out.println("\n=== Fin du test ===");
    }
}
```

**Explication du code :**

1. **Constantes de connexion** : définies en haut pour faciliter les modifications
2. **try-with-resources** : `try (Connection conn = ...)` garantit que la connexion sera fermée automatiquement, même en cas d'exception
3. **DriverManager.getConnection()** : tente d'établir la connexion
4. **connection.getMetaData()** : permet d'obtenir des informations sur la base et le driver
5. **Bloc catch** : capture et affiche les erreurs de connexion

#### Étape 4 : Exécuter le programme
1. Clic droit dans le code → Run 'TestConnexionMySQL.main()'
2. Ou cliquez sur la flèche verte à côté de `public static void main`

**Résultat attendu :**
```
=== Test de connexion à MySQL ===

✓ Connexion réussie à MySQL !
URL : jdbc:mysql://localhost:3306/tp_jdbc
Utilisateur : tp_user
Base de données : tp_jdbc
Driver : MySQL Connector/J
Version du driver : mysql-connector-java-8.0.33

=== Fin du test ===
```

### 6.3 TP : Connexion à PostgreSQL

#### Étape 1 : Créer la classe TestConnexionPostgreSQL
Dans le même package `connexion`, créez `TestConnexionPostgreSQL`.

#### Étape 2 : Écrire le code

```java
package ma.emsi.tp.connexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe de test pour la connexion JDBC à PostgreSQL
 */
public class TestConnexionPostgreSQL {
    
    // Paramètres de connexion à PostgreSQL
    private static final String URL = "jdbc:postgresql://localhost:5432/tp_jdbc";
    private static final String USER = "tp_user";
    private static final String PASSWORD = "tp_password123";
    
    public static void main(String[] args) {
        System.out.println("=== Test de connexion à PostgreSQL ===\n");
        
        // Try-with-resources pour gérer automatiquement la fermeture
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            
            // Connexion réussie
            System.out.println("✓ Connexion réussie à PostgreSQL !");
            
            // Informations sur la connexion
            System.out.println("URL : " + URL);
            System.out.println("Utilisateur : " + USER);
            System.out.println("Base de données : " + connection.getCatalog());
            System.out.println("Schéma actuel : " + connection.getSchema());
            System.out.println("Driver : " + connection.getMetaData().getDriverName());
            System.out.println("Version du driver : " + connection.getMetaData().getDriverVersion());
            System.out.println("Version PostgreSQL : " + connection.getMetaData().getDatabaseProductVersion());
            
        } catch (SQLException e) {
            // Gestion des erreurs
            System.err.println("✗ Échec de la connexion à PostgreSQL !");
            System.err.println("Raison : " + e.getMessage());
            System.err.println("Code d'erreur SQL : " + e.getErrorCode());
            System.err.println("État SQL : " + e.getSQLState());
            e.printStackTrace();
        }
        
        System.out.println("\n=== Fin du test ===");
    }
}
```

**Différences notables avec MySQL :**
- L'URL commence par `jdbc:postgresql://`
- Port par défaut : `5432` au lieu de `3306`
- Méthode `connection.getSchema()` plus pertinente pour PostgreSQL

#### Étape 3 : Exécuter
Lancez le programme de la même manière.

**Résultat attendu :**
```
=== Test de connexion à PostgreSQL ===

✓ Connexion réussie à PostgreSQL !
URL : jdbc:postgresql://localhost:5432/tp_jdbc
Utilisateur : tp_user
Base de données : tp_jdbc
Schéma actuel : public
Driver : PostgreSQL JDBC Driver
Version du driver : 42.6.0
Version PostgreSQL : 15.3

=== Fin du test ===
```

### 6.4 Amélioration : Classe utilitaire de connexion

Pour éviter la duplication de code, créons une classe utilitaire.

#### Étape 1 : Créer la classe ConnexionUtil

```java
package ma.emsi.tp.connexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe utilitaire pour gérer les connexions JDBC
 */
public class ConnexionUtil {
    
    // Configuration MySQL
    private static final String MYSQL_URL = "jdbc:mysql://localhost:3306/tp_jdbc";
    private static final String MYSQL_USER = "tp_user";
    private static final String MYSQL_PASSWORD = "tp_password123";
    
    // Configuration PostgreSQL
    private static final String POSTGRES_URL = "jdbc:postgresql://localhost:5432/tp_jdbc";
    private static final String POSTGRES_USER = "tp_user";
    private static final String POSTGRES_PASSWORD = "tp_password123";
    
    /**
     * Obtenir une connexion à MySQL
     * @return Connection à MySQL
     * @throws SQLException en cas d'erreur de connexion
     */
    public static Connection getConnexionMySQL() throws SQLException {
        return DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD);
    }
    
    /**
     * Obtenir une connexion à PostgreSQL
     * @return Connection à PostgreSQL
     * @throws SQLException en cas d'erreur de connexion
     */
    public static Connection getConnexionPostgreSQL() throws SQLException {
        return DriverManager.getConnection(POSTGRES_URL, POSTGRES_USER, POSTGRES_PASSWORD);
    }
    
    /**
     * Fermer une connexion de manière sécurisée
     * @param connection la connexion à fermer
     */
    public static void fermerConnexion(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Connexion fermée avec succès.");
            } catch (SQLException e) {
                System.err.println("Erreur lors de la fermeture : " + e.getMessage());
            }
        }
    }
    
    /**
     * Tester les deux connexions
     */
    public static void main(String[] args) {
        System.out.println("=== Test des connexions via ConnexionUtil ===\n");
        
        // Test MySQL
        try (Connection connMySQL = getConnexionMySQL()) {
            System.out.println("✓ MySQL : " + connMySQL.getMetaData().getDatabaseProductName());
        } catch (SQLException e) {
            System.err.println("✗ MySQL : " + e.getMessage());
        }
        
        // Test PostgreSQL
        try (Connection connPostgres = getConnexionPostgreSQL()) {
            System.out.println("✓ PostgreSQL : " + connPostgres.getMetaData().getDatabaseProductName());
        } catch (SQLException e) {
            System.err.println("✗ PostgreSQL : " + e.getMessage());
        }
    }
}
```

**Avantages de cette approche :**
- Centralisation de la configuration
- Réutilisabilité du code
- Facilité de maintenance
- Méthode de fermeture sécurisée

### 🚨 Erreurs fréquentes et débogage

#### Erreur 1 : ClassNotFoundException: com.mysql.cj.jdbc.Driver

**Symptôme :**
```
java.lang.ClassNotFoundException: com.mysql.cj.jdbc.Driver
```

**Cause :** Le driver MySQL n'est pas dans le classpath (dépendance Maven manquante ou non chargée)

**Solution :**
1. Vérifiez que la dépendance est bien dans `pom.xml`
2. Rechargez Maven : icône 🔄 dans le panneau Maven
3. Vérifiez dans External Libraries que `mysql-connector-java` est présent

#### Erreur 2 : SQLException: Access denied for user 'tp_user'@'localhost'

**Symptôme :**
```
java.sql.SQLException: Access denied for user 'tp_user'@'localhost' (using password: YES)
```

**Causes possibles :**
- Mauvais mot de passe
- Utilisateur non créé
- Privilèges non accordés

**Solution :**
1. Vérifiez les identifiants dans le code
2. Reconnectez-vous à MySQL/PostgreSQL en ligne de commande avec ces identifiants
3. Recréez l'utilisateur si nécessaire (voir Partie 4 et 5)

#### Erreur 3 : SQLException: Communications link failure

**Symptôme :**
```
com.mysql.cj.jdbc.exceptions.CommunicationsException: Communications link failure
```

**Causes possibles :**
- Le serveur MySQL/PostgreSQL n'est pas démarré
- Mauvais port ou hôte dans l'URL
- Pare-feu bloquant la connexion

**Solution :**
1. Vérifiez que le serveur tourne :
   ```bash
   # MySQL (Windows)
   net start MySQL80
   
   # PostgreSQL (Windows)
   net start postgresql-x64-15
   ```
2. Vérifiez le port avec `netstat -ano | findstr 3306` (MySQL) ou `findstr 5432` (PostgreSQL)
3. Testez la connexion en ligne de commande avant de retester en Java

#### Erreur 4 : SQLException: Unknown database 'tp_jdbc'

**Symptôme :**
```
java.sql.SQLException: Unknown database 'tp_jdbc'
```

**Cause :** La base de données n'existe pas

**Solution :**
```sql
CREATE DATABASE tp_jdbc;
```

#### Erreur 5 : java.sql.SQLTimeoutException: Connection timed out

**Cause :** Le serveur est inaccessible (hôte incorrect, réseau)

**Solution :**
- Vérifiez que `localhost` est correct (essayez `127.0.0.1`)
- Vérifiez que le serveur n'est pas configuré pour n'accepter que certaines IPs

### 6.5 Bonnes pratiques

#### 1. Toujours utiliser try-with-resources
```java
// ✓ BON
try (Connection conn = DriverManager.getConnection(url, user, pwd)) {
    // utiliser conn
} // conn.close() appelé automatiquement

// ✗ MAUVAIS
Connection conn = DriverManager.getConnection(url, user, pwd);
// utiliser conn
conn.close(); // Peut ne jamais être appelé si exception avant
```

#### 2. Ne jamais hardcoder les mots de passe
```java
// ✗ MAUVAIS : mot de passe en dur dans le code
private static final String PASSWORD = "tp_password123";

// ✓ BON : utiliser un fichier de configuration
// Créer src/main/resources/db.properties :
// mysql.url=jdbc:mysql://localhost:3306/tp_jdbc
// mysql.user=tp_user
// mysql.password=tp_password123

Properties props = new Properties();
props.load(new FileInputStream("src/main/resources/db.properties"));
String url = props.getProperty("mysql.url");
String user = props.getProperty("mysql.user");
String password = props.getProperty("mysql.password");
```

#### 3. Gérer proprement les exceptions
```java
// ✓ BON : messages clairs et logging
try (Connection conn = getConnection()) {
    // ...
} catch (SQLException e) {
    System.err.println("Erreur de connexion : " + e.getMessage());
    System.err.println("Code SQL : " + e.getSQLState());
    // En production : logger.error("Erreur de connexion", e);
}
```

### Questions et exercices

1. **Pourquoi utilise-t-on try-with-resources plutôt qu'un simple try-catch-finally ?**

2. **Quelle est la différence entre `e.getMessage()`, `e.getSQLState()` et `e.getErrorCode()` ?**

3. **Exercice** : Créez un fichier `db.properties` dans `src/main/resources` et modifiez `ConnexionUtil` pour lire les paramètres depuis ce fichier.

4. **Exercice** : Ajoutez une méthode `testConnexion(String dbType)` dans `ConnexionUtil` qui prend "mysql" ou "postgresql" en paramètre et teste la connexion correspondante.

---

<a name="partie-7"></a>
## Partie 7 : TP Requêtes SQL avec Statement et ResultSet (TP 5)

### Objectifs de cette partie
- Comprendre les rôles de Statement et ResultSet
- Exécuter des requêtes SELECT
- Parcourir et afficher les résultats
- Comprendre les différences entre executeQuery, executeUpdate et execute
- Créer une application simple de consultation

### 7.1 Rappels théoriques

#### L'interface Statement
`Statement` est une interface qui permet d'exécuter des requêtes SQL **statiques** (sans paramètres) :

```java
Statement stmt = connection.createStatement();
ResultSet rs = stmt.executeQuery("SELECT * FROM etudiants");
```

**Méthodes principales :**
- `executeQuery(String sql)` : pour les SELECT (retourne ResultSet)
- `executeUpdate(String sql)` : pour INSERT, UPDATE, DELETE (retourne int = nombre de lignes affectées)
- `execute(String sql)` : pour tout type de requête (retourne boolean)

#### L'interface ResultSet
`ResultSet` représente l'ensemble des résultats d'une requête SELECT. C'est comme un **curseur** qui pointe sur une ligne à la fois :

```java
while (resultSet.next()) {  // Passe à la ligne suivante
    int id = resultSet.getInt("id");
    String nom = resultSet.getString("nom");
    // ...
}
```

**Méthodes de navigation :**
- `next()` : passe à la ligne suivante (retourne false si fin atteinte)
- `previous()` : ligne précédente (si ResultSet scrollable)
- `first()`, `last()` : première/dernière ligne

**Méthodes de lecture :**
- `getInt(String colonne)` ou `getInt(int index)`
- `getString(...)`, `getDouble(...)`, `getDate(...)`, etc.

### 7.2 TP : Lister tous les étudiants

#### Étape 1 : Créer le package requetes
1. Clic droit sur `src/main/java/ma/emsi/tp`
2. New → Package
3. Nommez-le `requetes`

#### Étape 2 : Créer la classe ListerEtudiants

```java
package ma.emsi.tp.requetes;

import ma.emsi.tp.connexion.ConnexionUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Classe pour lister tous les étudiants de la base
 */
public class ListerEtudiants {
    
    public static void main(String[] args) {
        System.out.println("=== Liste des étudiants (MySQL) ===\n");
        
        // Requête SQL
        String sql = "SELECT * FROM etudiants ORDER BY nom, prenom";
        
        // Try-with-resources pour Connection, Statement et ResultSet
        try (Connection connection = ConnexionUtil.getConnexionMySQL();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            
            // Affichage de l'en-tête
            System.out.println("╔════╦══════════════╦══════════════╦════════════════════════╦═══════════════╦══════════╗");
            System.out.println("║ ID ║     NOM      ║    PRÉNOM    ║         EMAIL          ║   NAISSANCE   ║   NOTE   ║");
            System.out.println("╠════╬══════════════╬══════════════╬════════════════════════╬═══════════════╬══════════╣");
            
            // Parcours des résultats
            int compteur = 0;
            while (resultSet.next()) {
                // Récupération des colonnes
                int id = resultSet.getInt("id");
                String nom = resultSet.getString("nom");
                String prenom = resultSet.getString("prenom");
                String email = resultSet.getString("email");
                java.sql.Date dateNaissance = resultSet.getDate("date_naissance");
                double noteMoyenne = resultSet.getDouble("note_moyenne");
                
                // Affichage formaté
                System.out.printf("║ %-2d ║ %-12s ║ %-12s ║ %-22s ║ %-13s ║ %8.2f ║%n",
                        id, nom, prenom, email, dateNaissance, noteMoyenne);
                
                compteur++;
            }
            
            System.out.println("╚════╩══════════════╩══════════════╩════════════════════════╩═══════════════╩══════════╝");
            System.out.println("\nTotal : " + compteur + " étudiant(s)");
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des étudiants :");
            System.err.println("  Message : " + e.getMessage());
            System.err.println("  Code SQL : " + e.getSQLState());
            e.printStackTrace();
        }
    }
}
```

**Points clés du code :**
1. **Triple try-with-resources** : ferme automatiquement Connection, Statement ET ResultSet
2. **resultSet.next()** : avance le curseur et retourne true si une ligne existe
3. **Getters typés** : `getInt()`, `getString()`, `getDouble()`, `getDate()`
4. **Formatage** : `printf()` pour un affichage tabulaire propre

#### Étape 3 : Exécuter

**Résultat attendu :**
```
=== Liste des étudiants (MySQL) ===

╔════╦══════════════╦══════════════╦════════════════════════╦═══════════════╦══════════╗
║ ID ║     NOM      ║    PRÉNOM    ║         EMAIL          ║   NAISSANCE   ║   NOTE   ║
╠════╬══════════════╬══════════════╬════════════════════════╬═══════════════╬══════════╣
║ 1  ║ Alami        ║ Fatima       ║ f.alami@emsi.ma        ║ 2003-05-15    ║    15.50 ║
║ 2  ║ Bennani      ║ Mohammed     ║ m.bennani@emsi.ma      ║ 2002-11-22    ║    13.75 ║
║ 3  ║ Chakir       ║ Amina        ║ a.chakir@emsi.ma       ║ 2003-08-30    ║    16.20 ║
║ 4  ║ Dahane       ║ Youssef      ║ y.dahane@emsi.ma       ║ 2002-03-10    ║    12.90 ║
║ 5  ║ El Fassi     ║ Sara         ║ s.elfassi@emsi.ma      ║ 2003-01-25    ║    17.10 ║
╚════╩══════════════╩══════════════╩════════════════════════╩═══════════════╩══════════╝

Total : 5 étudiant(s)
```

### 7.3 TP : Recherche avec filtre

#### Créer la classe RechercherEtudiantsParNote

```java
package ma.emsi.tp.requetes;

import ma.emsi.tp.connexion.ConnexionUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Recherche des étudiants ayant une note >= 15
 */
public class RechercherEtudiantsParNote {
    
    public static void main(String[] args) {
        System.out.println("=== Étudiants avec note >= 15 ===\n");
        
        double seuilNote = 15.0;
        String sql = "SELECT nom, prenom, note_moyenne " +
                     "FROM etudiants " +
                     "WHERE note_moyenne >= " + seuilNote + " " +
                     "ORDER BY note_moyenne DESC";
        
        try (Connection connection = ConnexionUtil.getConnexionMySQL();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            
            System.out.println("Étudiants ayant au moins " + seuilNote + "/20 :\n");
            
            while (resultSet.next()) {
                String nom = resultSet.getString("nom");
                String prenom = resultSet.getString("prenom");
                double note = resultSet.getDouble("note_moyenne");
                
                System.out.printf("  - %s %s : %.2f/20%n", prenom, nom, note);
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
```

**⚠️ Attention :** Cette approche (concaténation de valeurs dans la requête) est **dangereuse** et vulnérable aux **injections SQL**. Nous verrons la bonne méthode avec `PreparedStatement` dans la partie suivante.

### 7.4 Les trois méthodes execute

#### executeQuery() - Pour les SELECT
```java
String sql = "SELECT * FROM etudiants";
ResultSet rs = statement.executeQuery(sql);
// Retourne un ResultSet
```

#### executeUpdate() - Pour INSERT, UPDATE, DELETE
```java
String sql = "UPDATE etudiants SET note_moyenne = 18.0 WHERE id = 1";
int nbLignes = statement.executeUpdate(sql);
System.out.println(nbLignes + " ligne(s) modifiée(s)");
// Retourne le nombre de lignes affectées
```

#### execute() - Pour tout type de requête
```java
String sql = "...";
boolean estResultSet = statement.execute(sql);
if (estResultSet) {
    ResultSet rs = statement.getResultSet();
    // Traiter le ResultSet
} else {
    int nbLignes = statement.getUpdateCount();
    // Traiter le nombre de lignes
}
```

**Quand utiliser quoi ?**
- **executeQuery()** : toujours pour SELECT
- **executeUpdate()** : pour INSERT/UPDATE/DELETE/CREATE/DROP
- **execute()** : quand on ne sait pas à l'avance le type de requête (rare)

### 7.5 TP : Statistiques sur les étudiants

#### Créer la classe StatistiquesEtudiants

```java
package ma.emsi.tp.requetes;

import ma.emsi.tp.connexion.ConnexionUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Affiche des statistiques sur les étudiants
 */
public class StatistiquesEtudiants {
    
    public static void main(String[] args) {
        System.out.println("=== Statistiques sur les étudiants ===\n");
        
        String sqlStats = "SELECT " +
                "COUNT(*) as nombre_etudiants, " +
                "AVG(note_moyenne) as moyenne_generale, " +
                "MIN(note_moyenne) as note_min, " +
                "MAX(note_moyenne) as note_max " +
                "FROM etudiants";
        
        try (Connection connection = ConnexionUtil.getConnexionMySQL();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sqlStats)) {
            
            if (rs.next()) {
                int nombre = rs.getInt("nombre_etudiants");
                double moyenne = rs.getDouble("moyenne_generale");
                double noteMin = rs.getDouble("note_min");
                double noteMax = rs.getDouble("note_max");
                
                System.out.println("┌─────────────────────────────────────┐");
                System.out.printf("│ Nombre d'étudiants : %-14d │%n", nombre);
                System.out.printf("│ Moyenne générale   : %-14.2f │%n", moyenne);
                System.out.printf("│ Note minimale      : %-14.2f │%n", noteMin);
                System.out.printf("│ Note maximale      : %-14.2f │%n", noteMax);
                System.out.println("└─────────────────────────────────────┘");
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }
}
```

### 🚨 Erreurs fréquentes et débogage

#### Erreur 1 : SQLException: Column 'xyz' not found

**Cause :** Nom de colonne incorrect dans `resultSet.getString("xyz")`

**Solution :**
- Vérifiez l'orthographe exacte de la colonne
- Utilisez `resultSet.getMetaData().getColumnCount()` pour lister les colonnes disponibles
- Ou utilisez l'index : `resultSet.getString(1)` (commence à 1, pas 0 !)

#### Erreur 2 : SQLException: ResultSet is closed

**Cause :** Tentative d'accès au ResultSet après la fermeture de la connexion

**Solution :**
- Assurez-vous de traiter le ResultSet DANS le bloc try-with-resources
- Ne retournez jamais un ResultSet d'une méthode (il sera fermé)

#### Erreur 3 : SQLException: Before start of result set

**Cause :** Tentative de lecture avant d'appeler `next()`

**Solution :**
```java
// ✗ MAUVAIS
ResultSet rs = statement.executeQuery(sql);
String nom = rs.getString("nom"); // ERREUR : curseur avant la première ligne

// ✓ BON
ResultSet rs = statement.executeQuery(sql);
if (rs.next()) {
    String nom = rs.getString("nom");
}
```

### Questions et exercices

1. **Quelle est la différence entre `executeQuery()` et `executeUpdate()` ?**

2. **Pourquoi ne doit-on jamais concaténer des valeurs directement dans une requête SQL ?**

3. **Exercice** : Créez une classe `CompterEtudiantsParNote` qui affiche le nombre d'étudiants pour chaque tranche de notes :
   - 0-9.99
   - 10-11.99
   - 12-13.99
   - 14-15.99
   - 16-20

4. **Exercice** : Modifiez `ListerEtudiants` pour qu'elle fonctionne avec PostgreSQL. Testez les deux versions
4. **Exercice** : Modifiez `ListerEtudiants` pour qu'elle fonctionne avec PostgreSQL. Testez les deux versions.

---

<a name="partie-8"></a>
## Partie 8 : TP Requêtes paramétrées avec PreparedStatement (TP 6)

### Objectifs de cette partie
- Comprendre les risques d'injection SQL avec Statement
- Maîtriser l'utilisation de PreparedStatement
- Paramétrer correctement des requêtes SQL
- Implémenter des opérations CRUD sécurisées
- Comprendre les avantages en termes de performance

### 8.1 Rappels théoriques : Le problème de l'injection SQL

#### Qu'est-ce qu'une injection SQL ?

C'est une technique d'attaque où un utilisateur malveillant insère du code SQL dans une requête pour modifier son comportement.

**Exemple vulnérable :**
```java
// ⚠️ CODE DANGEREUX - NE JAMAIS FAIRE ÇA
String nom = userInput; // Supposons que l'utilisateur entre: "' OR '1'='1"
String sql = "SELECT * FROM etudiants WHERE nom = '" + nom + "'";
// Résultat: SELECT * FROM etudiants WHERE nom = '' OR '1'='1'
// Cette requête retournera TOUS les étudiants !
```

**Autres attaques possibles :**
```sql
-- Suppression de table
nom = "'; DROP TABLE etudiants; --"

-- Extraction de données sensibles
nom = "' UNION SELECT password FROM users WHERE '1'='1"
```

#### La solution : PreparedStatement

`PreparedStatement` **sépare** le code SQL des données :
- La structure de la requête est compilée en avance
- Les paramètres sont traités comme de simples valeurs, jamais comme du code SQL
- Protection automatique contre les injections

### 8.2 Syntaxe de PreparedStatement

#### Création et paramétrage
```java
// Les ? sont des placeholders (marqueurs de paramètres)
String sql = "SELECT * FROM etudiants WHERE nom = ? AND note_moyenne >= ?";

PreparedStatement pstmt = connection.prepareStatement(sql);

// Définir les paramètres (index commence à 1)
pstmt.setString(1, "Alami");     // Remplace le premier ?
pstmt.setDouble(2, 15.0);         // Remplace le deuxième ?

// Exécuter (SANS passer la requête SQL en paramètre !)
ResultSet rs = pstmt.executeQuery();
```

**Méthodes setXXX() courantes :**
- `setString(index, value)`
- `setInt(index, value)`
- `setDouble(index, value)`
- `setDate(index, value)`
- `setTimestamp(index, value)`
- `setBoolean(index, value)`
- `setNull(index, sqlType)`

### 8.3 TP : Recherche sécurisée par nom

#### Étape 1 : Créer le package prepared
1. Clic droit sur `src/main/java/ma/emsi/tp`
2. New → Package
3. Nommez-le `prepared`

#### Étape 2 : Créer la classe RechercherEtudiantParNom

```java
package ma.emsi.tp.prepared;

import ma.emsi.tp.connexion.ConnexionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * Recherche d'étudiants par nom avec PreparedStatement
 */
public class RechercherEtudiantParNom {
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=== Recherche d'étudiant par nom ===\n");
        System.out.print("Entrez un nom (ou une partie) : ");
        String recherche = scanner.nextLine();
        
        // Requête avec paramètre
        String sql = "SELECT id, nom, prenom, email, note_moyenne " +
                     "FROM etudiants " +
                     "WHERE nom LIKE ? " +
                     "ORDER BY nom, prenom";
        
        try (Connection connection = ConnexionUtil.getConnexionMySQL();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            // Définir le paramètre (avec wildcards pour LIKE)
            pstmt.setString(1, "%" + recherche + "%");
            
            // Exécuter la requête
            try (ResultSet rs = pstmt.executeQuery()) {
                
                System.out.println("\nRésultats de la recherche :\n");
                
                int compteur = 0;
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String nom = rs.getString("nom");
                    String prenom = rs.getString("prenom");
                    String email = rs.getString("email");
                    double note = rs.getDouble("note_moyenne");
                    
                    System.out.printf("[%d] %s %s (%s) - Note: %.2f/20%n",
                            id, prenom, nom, email, note);
                    compteur++;
                }
                
                if (compteur == 0) {
                    System.out.println("Aucun étudiant trouvé.");
                } else {
                    System.out.println("\n" + compteur + " résultat(s).");
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche : " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}
```

**Points clés :**
- `LIKE ?` : le paramètre peut contenir des wildcards `%`
- `setString(1, "%" + recherche + "%")` : ajout des % pour recherche partielle
- Même si l'utilisateur entre `'; DROP TABLE etudiants; --`, cela sera traité comme une simple chaîne de recherche (aucun étudiant ne correspondra, mais pas d'attaque)

#### Étape 3 : Tester
Exécutez et entrez différentes valeurs :
- `Ala` → devrait trouver "Alami"
- `a` → devrait trouver plusieurs étudiants
- `' OR '1'='1` → ne devrait rien trouver (traité comme texte littéral)

### 8.4 TP : Insertion d'un étudiant

#### Créer la classe InsererEtudiant

```java
package ma.emsi.tp.prepared;

import ma.emsi.tp.connexion.ConnexionUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;

/**
 * Insertion d'un nouvel étudiant avec PreparedStatement
 */
public class InsererEtudiant {
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=== Inscription d'un nouvel étudiant ===\n");
        
        // Saisie des informations
        System.out.print("Nom : ");
        String nom = scanner.nextLine();
        
        System.out.print("Prénom : ");
        String prenom = scanner.nextLine();
        
        System.out.print("Email : ");
        String email = scanner.nextLine();
        
        System.out.print("Date de naissance (YYYY-MM-DD) : ");
        String dateStr = scanner.nextLine();
        LocalDate dateNaissance = LocalDate.parse(dateStr);
        
        System.out.print("Note moyenne : ");
        double noteMoyenne = scanner.nextDouble();
        
        // Requête d'insertion
        String sql = "INSERT INTO etudiants (nom, prenom, email, date_naissance, note_moyenne) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection connection = ConnexionUtil.getConnexionMySQL();
             PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            // Définir les paramètres
            pstmt.setString(1, nom);
            pstmt.setString(2, prenom);
            pstmt.setString(3, email);
            pstmt.setDate(4, Date.valueOf(dateNaissance));
            pstmt.setDouble(5, noteMoyenne);
            
            // Exécuter l'insertion
            int nbLignes = pstmt.executeUpdate();
            
            if (nbLignes > 0) {
                System.out.println("\n✓ Étudiant inséré avec succès !");
                
                // Récupérer l'ID auto-généré
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);
                        System.out.println("  ID attribué : " + id);
                    }
                }
            } else {
                System.out.println("\n✗ Échec de l'insertion.");
            }
            
        } catch (SQLIntegrityConstraintViolationException e) {
            System.err.println("\n✗ Erreur : cet email existe déjà !");
        } catch (SQLException e) {
            System.err.println("\n✗ Erreur lors de l'insertion : " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}
```

**Nouveautés :**
- `Statement.RETURN_GENERATED_KEYS` : pour récupérer l'ID auto-généré
- `pstmt.getGeneratedKeys()` : retourne un ResultSet contenant l'ID
- `SQLIntegrityConstraintViolationException` : exception spécifique pour les violations de contraintes (email unique, etc.)
- `Date.valueOf(localDate)` : conversion LocalDate → java.sql.Date

### 8.5 TP : Mise à jour et suppression

#### Créer la classe ModifierEtudiant

```java
package ma.emsi.tp.prepared;

import ma.emsi.tp.connexion.ConnexionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * Modification de la note d'un étudiant
 */
public class ModifierEtudiant {
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=== Modification de la note d'un étudiant ===\n");
        
        System.out.print("ID de l'étudiant : ");
        int id = scanner.nextInt();
        
        System.out.print("Nouvelle note moyenne : ");
        double nouvelleNote = scanner.nextDouble();
        
        String sql = "UPDATE etudiants SET note_moyenne = ? WHERE id = ?";
        
        try (Connection connection = ConnexionUtil.getConnexionMySQL();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setDouble(1, nouvelleNote);
            pstmt.setInt(2, id);
            
            int nbLignes = pstmt.executeUpdate();
            
            if (nbLignes > 0) {
                System.out.println("\n✓ Note mise à jour avec succès !");
                System.out.println("  " + nbLignes + " ligne(s) modifiée(s).");
            } else {
                System.out.println("\n⚠ Aucun étudiant trouvé avec l'ID " + id);
            }
            
        } catch (SQLException e) {
            System.err.println("\n✗ Erreur lors de la mise à jour : " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}
```

#### Créer la classe SupprimerEtudiant

```java
package ma.emsi.tp.prepared;

import ma.emsi.tp.connexion.ConnexionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * Suppression d'un étudiant
 */
public class SupprimerEtudiant {
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=== Suppression d'un étudiant ===\n");
        
        System.out.print("ID de l'étudiant à supprimer : ");
        int id = scanner.nextInt();
        
        System.out.print("Êtes-vous sûr ? (O/N) : ");
        scanner.nextLine(); // Consommer le retour ligne
        String confirmation = scanner.nextLine();
        
        if (!confirmation.equalsIgnoreCase("O")) {
            System.out.println("Suppression annulée.");
            scanner.close();
            return;
        }
        
        String sql = "DELETE FROM etudiants WHERE id = ?";
        
        try (Connection connection = ConnexionUtil.getConnexionMySQL();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            int nbLignes = pstmt.executeUpdate();
            
            if (nbLignes > 0) {
                System.out.println("\n✓ Étudiant supprimé avec succès !");
            } else {
                System.out.println("\n⚠ Aucun étudiant trouvé avec l'ID " + id);
            }
            
        } catch (SQLException e) {
            System.err.println("\n✗ Erreur lors de la suppression : " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}
```

### 8.6 Avantages de PreparedStatement

#### 1. Sécurité
```java
// Aucun risque d'injection SQL
String dangerousInput = "'; DROP TABLE etudiants; --";
pstmt.setString(1, dangerousInput); // Traité comme une simple chaîne
```

#### 2. Performance
```java
// La requête est pré-compilée et peut être réutilisée
PreparedStatement pstmt = conn.prepareStatement("INSERT INTO etudiants (...) VALUES (?, ?, ?)");

for (Etudiant e : listeEtudiants) {
    pstmt.setString(1, e.getNom());
    pstmt.setString(2, e.getPrenom());
    pstmt.setString(3, e.getEmail());
    pstmt.executeUpdate(); // Pas besoin de recompiler la requête
}
```

#### 3. Lisibilité
```java
// ✓ Plus clair
pstmt.setString(1, nom);
pstmt.setDouble(2, note);

// ✗ Moins clair
String sql = "... WHERE nom = '" + nom + "' AND note = " + note;
```

### 🚨 Erreurs fréquentes et débogage

#### Erreur 1 : SQLException: Parameter index out of range

**Cause :** Index de paramètre incorrect (commence à 1, pas 0)

**Solution :**
```java
// ✗ MAUVAIS
pstmt.setString(0, value); // Index commence à 1 !

// ✓ BON
pstmt.setString(1, value);
```

#### Erreur 2 : SQLException: No value specified for parameter X

**Cause :** Oubli de définir un paramètre

**Solution :**
```java
String sql = "INSERT INTO etudiants (nom, prenom, email) VALUES (?, ?, ?)";
PreparedStatement pstmt = conn.prepareStatement(sql);
pstmt.setString(1, nom);
pstmt.setString(2, prenom);
pstmt.setString(3, email); // Ne pas oublier tous les paramètres !
pstmt.executeUpdate();
```

#### Erreur 3 : Passer la requête SQL à executeQuery()

**Solution :**
```java
// ✗ MAUVAIS
pstmt.executeQuery(sql); // Ne jamais passer sql ici

// ✓ BON
pstmt.executeQuery(); // Pas de paramètre
```

#### Erreur 4 : Réutiliser un PreparedStatement sans clearParameters()

**Solution :**
```java
for (int i = 0; i < 10; i++) {
    pstmt.setString(1, "Valeur " + i);
    pstmt.executeUpdate();
    pstmt.clearParameters(); // Optionnel mais recommandé
}
```

### Questions et exercices

1. **Expliquez avec vos propres mots pourquoi PreparedStatement protège contre les injections SQL.**

2. **Quelle est la différence entre `executeUpdate()` et `executeQuery()` dans un PreparedStatement ?**

3. **Exercice** : Créez une classe `RechercherEtudiantsAvances` qui permet de rechercher par :
   - Nom (LIKE)
   - Note minimale
   - Note maximale
   Tous les critères sont optionnels (utilisez des conditions dynamiques).

4. **Exercice** : Créez une classe `InsertionBatch` qui insère 10 étudiants d'un coup en utilisant `addBatch()` et `executeBatch()` de PreparedStatement.

---

<a name="partie-9"></a>
## Partie 9 : TP Bonus : CallableStatement et mini-DAO (TP 7)

### Objectifs de cette partie
- Découvrir CallableStatement pour les procédures stockées (optionnel)
- Structurer le code avec le pattern DAO (Data Access Object)
- Implémenter une classe DAO complète
- Comprendre la séparation des responsabilités

### 9.1 CallableStatement (optionnel)

#### Qu'est-ce qu'une procédure stockée ?

Une **procédure stockée** est un ensemble d'instructions SQL enregistrées dans la base de données et exécutables par nom. Avantages :
- Logique métier centralisée dans la BDD
- Performance (pré-compilée)
- Réutilisabilité

#### Exemple de procédure stockée MySQL

Créez cette procédure dans MySQL :

```sql
DELIMITER //

CREATE PROCEDURE obtenirStatistiquesEtudiants(
    OUT nb_total INT,
    OUT moyenne_generale DECIMAL(4,2)
)
BEGIN
    SELECT COUNT(*), AVG(note_moyenne)
    INTO nb_total, moyenne_generale
    FROM etudiants;
END//

DELIMITER ;
```

#### Appel depuis Java avec CallableStatement

```java
package ma.emsi.tp.callable;

import ma.emsi.tp.connexion.ConnexionUtil;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

public class TestCallableStatement {
    
    public static void main(String[] args) {
        String sql = "{CALL obtenirStatistiquesEtudiants(?, ?)}";
        
        try (Connection conn = ConnexionUtil.getConnexionMySQL();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            // Déclarer les paramètres OUT
            cstmt.registerOutParameter(1, Types.INTEGER);
            cstmt.registerOutParameter(2, Types.DECIMAL);
            
            // Exécuter la procédure
            cstmt.execute();
            
            // Récupérer les résultats
            int nbTotal = cstmt.getInt(1);
            double moyenne = cstmt.getDouble(2);
            
            System.out.println("Statistiques depuis procédure stockée :");
            System.out.println("  Nombre d'étudiants : " + nbTotal);
            System.out.println("  Moyenne générale : " + moyenne);
            
        } catch (SQLException e) {
            System.err.println("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
```

**Note :** CallableStatement est moins courant dans les applications modernes (on préfère souvent la logique métier en Java), mais reste utile pour des bases de données legacy.

### 9.2 Le pattern DAO (Data Access Object)

#### Qu'est-ce qu'un DAO ?

Un **DAO** est une classe qui encapsule tous les accès à la base de données pour une entité donnée. Il sépare :
- **Modèle** (classe Etudiant) : représentation Java de l'entité
- **DAO** (classe EtudiantDAO) : opérations CRUD sur la BDD
- **Logique métier** : utilise le DAO sans connaître SQL

**Avantages :**
- Code plus propre et maintenable
- Réutilisabilité
- Facilite les tests (on peut mocker le DAO)
- Changement de BDD facilité (un seul endroit à modifier)

### 9.3 Création de la classe Etudiant (Modèle)

#### Étape 1 : Créer le package model
New → Package → `ma.emsi.tp.model`

#### Étape 2 : Créer la classe Etudiant

```java
package ma.emsi.tp.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Classe modèle représentant un étudiant
 */
public class Etudiant {
    
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private LocalDate dateNaissance;
    private double noteMoyenne;
    private LocalDateTime dateInscription;
    
    // Constructeur vide
    public Etudiant() {
    }
    
    // Constructeur complet
    public Etudiant(int id, String nom, String prenom, String email, 
                    LocalDate dateNaissance, double noteMoyenne, LocalDateTime dateInscription) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.dateNaissance = dateNaissance;
        this.noteMoyenne = noteMoyenne;
        this.dateInscription = dateInscription;
    }
    
    // Constructeur sans ID (pour les insertions)
    public Etudiant(String nom, String prenom, String email, LocalDate dateNaissance, double noteMoyenne) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.dateNaissance = dateNaissance;
        this.noteMoyenne = noteMoyenne;
    }
    
    // Getters et Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getNom() {
        return nom;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public String getPrenom() {
        return prenom;
    }
    
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public LocalDate getDateNaissance() {
        return dateNaissance;
    }
    
    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }
    
    public double getNoteMoyenne() {
        return noteMoyenne;
    }
    
    public void setNoteMoyenne(double noteMoyenne) {
        this.noteMoyenne = noteMoyenne;
    }
    
    public LocalDateTime getDateInscription() {
        return dateInscription;
    }
    
    public void setDateInscription(LocalDateTime dateInscription) {
        this.dateInscription = dateInscription;
    }
    
    // toString() pour l'affichage
    @Override
    public String toString() {
        return String.format("Etudiant[id=%d, nom=%s, prenom=%s, email=%s, note=%.2f]",
                id, nom, prenom, email, noteMoyenne);
    }
}
```

### 9.4 Création de la classe EtudiantDAO

#### Étape 1 : Créer le package dao
New → Package → `ma.emsi.tp.dao`

#### Étape 2 : Créer la classe EtudiantDAO

```java
package ma.emsi.tp.dao;

import ma.emsi.tp.connexion.ConnexionUtil;
import ma.emsi.tp.model.Etudiant;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des étudiants
 */
public class EtudiantDAO {
    
    /**
     * Sauvegarder un nouvel étudiant
     * @param etudiant l'étudiant à insérer
     * @return l'ID généré, ou -1 en cas d'erreur
     */
    public int save(Etudiant etudiant) {
        String sql = "INSERT INTO etudiants (nom, prenom, email, date_naissance, note_moyenne) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = ConnexionUtil.getConnexionMySQL();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, etudiant.getNom());
            pstmt.setString(2, etudiant.getPrenom());
            pstmt.setString(3, etudiant.getEmail());
            pstmt.setDate(4, Date.valueOf(etudiant.getDateNaissance()));
            pstmt.setDouble(5, etudiant.getNoteMoyenne());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);
                        etudiant.setId(id);
                        return id;
                    }
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion : " + e.getMessage());
            e.printStackTrace();
        }
        
        return -1;
    }
    
    /**
     * Trouver tous les étudiants
     * @return liste de tous les étudiants
     */
    public List<Etudiant> findAll() {
        List<Etudiant> etudiants = new ArrayList<>();
        String sql = "SELECT * FROM etudiants ORDER BY nom, prenom";
        
        try (Connection conn = ConnexionUtil.getConnexionMySQL();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Etudiant etudiant = extraireEtudiant(rs);
                etudiants.add(etudiant);
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération : " + e.getMessage());
            e.printStackTrace();
        }
        
        return etudiants;
    }
    
    /**
     * Trouver un étudiant par son ID
     * @param id l'identifiant
     * @return l'étudiant trouvé, ou null
     */
    public Etudiant findById(int id) {
        String sql = "SELECT * FROM etudiants WHERE id = ?";
        
        try (Connection conn = ConnexionUtil.getConnexionMySQL();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extraireEtudiant(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche : " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Trouver des étudiants par nom (recherche partielle)
     * @param nom le nom à rechercher
     * @return liste des étudiants correspondants
     */
    public List<Etudiant> findByNom(String nom) {
        List<Etudiant> etudiants = new ArrayList<>();
        String sql = "SELECT * FROM etudiants WHERE nom LIKE ? ORDER BY nom, prenom";
        
        try (Connection conn = ConnexionUtil.getConnexionMySQL();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + nom + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    etudiants.add(extraireEtudiant(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche : " + e.getMessage());
            e.printStackTrace();
        }
        
        return etudiants;
    }
    
    /**
     * Mettre à jour un étudiant
     * @param etudiant l'étudiant avec les nouvelles valeurs
     * @return true si mis à jour, false sinon
     */
    public boolean update(Etudiant etudiant) {
        String sql = "UPDATE etudiants SET nom = ?, prenom = ?, email = ?, " +
                     "date_naissance = ?, note_moyenne = ? WHERE id = ?";
        
        try (Connection conn = ConnexionUtil.getConnexionMySQL();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, etudiant.getNom());
            pstmt.setString(2, etudiant.getPrenom());
            pstmt.setString(3, etudiant.getEmail());
            pstmt.setDate(4, Date.valueOf(etudiant.getDateNaissance()));
            pstmt.setDouble(5, etudiant.getNoteMoyenne());
            pstmt.setInt(6, etudiant.getId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour : " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Supprimer un étudiant par son ID
     * @param id l'identifiant de l'étudiant
     * @return true si supprimé, false sinon
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM etudiants WHERE id = ?";
        
        try (Connection conn = ConnexionUtil.getConnexionMySQL();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression : " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Compter le nombre total d'étudiants
     * @return le nombre d'étudiants
     */
    public int count() {
        String sql = "SELECT COUNT(*) FROM etudiants";
        
        try (Connection conn = ConnexionUtil.getConnexionMySQL();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur lors du comptage : " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Méthode utilitaire pour extraire un Etudiant depuis un ResultSet
     * @param rs le ResultSet positionné sur une ligne
     * @return l'objet Etudiant
     * @throws SQLException en cas d'erreur
     */
    private Etudiant extraireEtudiant(ResultSet rs) throws SQLException {
        Etudiant etudiant = new Etudiant();
        etudiant.setId(rs.getInt("id"));
        etudiant.setNom(rs.getString("nom"));
        etudiant.setPrenom(rs.getString("prenom"));
        etudiant.setEmail(rs.getString("email"));
        
        Date dateNaissance = rs.getDate("date_naissance");
        if (dateNaissance != null) {
            etudiant.setDateNaissance(dateNaissance.toLocalDate());
        }
        
        etudiant.setNoteMoyenne(rs.getDouble("note_moyenne"));
        
        Timestamp dateInscription = rs.getTimestamp("date_inscription");
        if (dateInscription != null) {
            etudiant.setDateInscription(dateInscription.toLocalDateTime());
        }
        
        return etudiant;
    }
}
```

**Avantages de cette architecture :**
- Toute la logique SQL est centralisée dans le DAO
- Réutilisabilité : on peut appeler `findAll()` depuis n'importe où
- Maintenance : un seul endroit à modifier si la structure change
- Tests : on peut facilement mocker le DAO

### 9.5 Utilisation du DAO

#### Créer la classe TestEtudiantDAO

```java
package ma.emsi.tp.dao;

import ma.emsi.tp.model.Etudiant;

import java.time.LocalDate;
import java.util.List;

/**
 * Classe de test pour le DAO
 */
public class TestEtudiantDAO {
    
    public static void main(String[] args) {
        EtudiantDAO dao = new EtudiantDAO();
        
        System.out.println("=== Test du DAO Etudiant ===\n");
        
        // 1. Lister tous les étudiants
        System.out.println("1. Liste de tous les étudiants :");
        List<Etudiant> tous = dao.findAll();
        tous.forEach(e -> System.out.println("  - " + e));
        System.out.println("  Total : " + dao.count() + " étudiants\n");
        
        // 2. Recherche par ID
        System.out.println("2. Recherche de l'étudiant ID=1 :");
        Etudiant etudiant1 = dao.findById(1);
        if (etudiant1 != null) {
            System.out.println("  Trouvé : " + etudiant1);
        } else {
            System.out.println("  Non trouvé");
        }
        System.out.println();
        
        // 3. Recherche par nom
        System.out.println("3. Recherche des étudiants dont le nom contient 'a' :");
        List<Etudiant> resultats = dao.findByNom("a");
        resultats.forEach(e -> System.out.println("  - " + e));
        System.out.println();
        
        // 4. Insertion d'un nouvel étudiant
        System.out.println("4. Insertion d'un nouvel étudiant :");
        Etudiant nouveau = new Etudiant(
            "Idrissi",
            "Karim",
            "k.idrissi@emsi.ma",
            LocalDate.of(2003, 7, 20),
            14.80
        );
        int id = dao.save(nouveau);
        if (id > 0) {
            System.out.println("  ✓ Étudiant inséré avec ID : " + id);
        } else {
            System.out.println("  ✗ Échec de l'insertion");
        }
        System.out.println();
        
        // 5. Mise à jour
        System.out.println("5. Mise à jour de la note de l'étudiant ID=1 :");
        if (etudiant1 != null) {
            etudiant1.setNoteMoyenne(16.50);
            boolean success = dao.update(etudiant1);
            if (success) {
                System.out.println("  ✓ Note mise à jour");
                // Vérification
                Etudiant verifie = dao.findById(1);
                System.out.println("  Nouvelle note : " + verifie.getNoteMoyenne());
            } else {
                System.out.println("  ✗ Échec de la mise à jour");
            }
        }
        System.out.println();
        
        // 6. Suppression (optionnel, commenté pour ne pas perdre de données)
        /*
        System.out.println("6. Suppression de l'étudiant ID=" + id + " :");
        boolean deleted = dao.delete(id);
        if (deleted) {
            System.out.println("  ✓ Étudiant supprimé");
        } else {
            System.out.println("  ✗ Échec de la suppression");
        }
        */
        
        System.out.println("=== Fin des tests ===");
    }
}
```

**Résultat attendu :**
```
=== Test du DAO Etudiant ===

1. Liste de tous les étudiants :
  - Etudiant[id=1, nom=Alami, prenom=Fatima, email=f.alami@emsi.ma, note=15.50]
  - Etudiant[id=2, nom=Bennani, prenom=Mohammed, email=m.bennani@emsi.ma, note=13.75]
  - Etudiant[id=3, nom=Chakir, prenom=Amina, email=a.chakir@emsi.ma, note=16.20]
  - Etudiant[id=4, nom=Dahane, prenom=Youssef, email=y.dahane@emsi.ma, note=12.90]
  - Etudiant[id=5, nom=El Fassi, prenom=Sara, email=s.elfassi@emsi.ma, note=17.10]
  Total : 5 étudiants

2. Recherche de l'étudiant ID=1 :
  Trouvé : Etudiant[id=1, nom=Alami, prenom=Fatima, email=f.alami@emsi.ma, note=15.50]

3. Recherche des étudiants dont le nom contient 'a' :
  - Etudiant[id=1, nom=Alami, prenom=Fatima, email=f.alami@emsi.ma, note=15.50]
  - Etudiant[id=3, nom=Chakir, prenom=Amina, email=a.chakir@emsi.ma, note=16.20]
  - Etudiant[id=4, nom=Dahane, prenom=Youssef, email=y.dahane@emsi.ma, note=12.90]

4. Insertion d'un nouvel étudiant :
  ✓ Étudiant inséré avec ID : 6

5. Mise à jour de la note de l'étudiant ID=1 :
  ✓ Note mise à jour
  Nouvelle note : 16.5

=== Fin des tests ===
```

### 9.6 Amélioration : Interface DAO générique (avancé)

Pour aller plus loin, on peut créer une interface générique :

```java
package ma.emsi.tp.dao;

import java.util.List;

/**
 * Interface générique pour les DAO
 * @param <T> le type d'entité
 */
public interface GenericDAO<T> {
    int save(T entity);
    T findById(int id);
    List<T> findAll();
    boolean update(T entity);
    boolean delete(int id);
    int count();
}
```

Puis faire implémenter cette interface par `EtudiantDAO` :

```java
public class EtudiantDAO implements GenericDAO<Etudiant> {
    // ... implémentation
}
```

### 🚨 Erreurs fréquentes et débogage

#### Erreur 1 : NullPointerException lors de l'extraction

**Cause :** Colonne nulle dans la BDD

**Solution :**
```java
// ✓ BON : vérifier les valeurs nulles
Date dateNaissance = rs.getDate("date_naissance");
if (dateNaissance != null) {
    etudiant.setDateNaissance(dateNaissance.toLocalDate());
}
```

#### Erreur 2 : SQLException: Column not found

**Cause :** Nom de colonne incorrect ou SELECT * non synchronisé avec la table

**Solution :**
- Utiliser des SELECT explicites : `SELECT id, nom, prenom FROM ...`
- Vérifier l'orthographe exacte des colonnes

#### Erreur 3 : ID non mis à jour après insertion

**Cause :** Oublier de définir l'ID après génération

**Solution :**
```java
int id = dao.save(etudiant);
// L'ID est maintenant défini dans l'objet etudiant
System.out.println("ID généré : " + etudiant.getId());
```

### Questions et exercices

1. **Quels sont les avantages d'utiliser un DAO plutôt que d'écrire du SQL directement dans les classes métier ?**

2. **Pourquoi la méthode `extraireEtudiant()` est-elle privée et non publique ?**

3. **Exercice** : Ajoutez une méthode `findByNoteMoyenneGreaterThan(double note)` au DAO qui retourne tous les étudiants ayant une note supérieure à la valeur donnée.

4. **Exercice** : Créez une nouvelle entité `Cours` (id, nom, coefficient) avec son DAO correspondant. Implémentez toutes les méthodes CRUD.

5. **Exercice avancé** : Modifiez le DAO pour qu'il accepte un paramètre de type de BDD (MySQL ou PostgreSQL) et adapte automatiquement les requêtes.

---

<a name="partie-10"></a>
## Partie 10 : Conclusion et exercices récapitulatifs

### 10.1 Résumé des compétences acquises

À la fin de ce TP, vous maîtrisez maintenant :

#### ✅ Configuration et environnement
- Créer un projet Maven dans IntelliJ IDEA
- Configurer le fichier `pom.xml` avec des dépendances
- Gérer les drivers JDBC pour MySQL et PostgreSQL

#### ✅ Bases de données
- Préparer MySQL et PostgreSQL pour JDBC
- Créer des utilisateurs et gérer les privilèges
- Comprendre les différences entre les deux SGBD

#### ✅ JDBC - Concepts fondamentaux
- Établir une connexion avec `DriverManager` et `Connection`
- Comprendre l'architecture JDBC (DriverManager → Connection → Statement → ResultSet)
- Utiliser le try-with-resources pour gérer les ressources

#### ✅ Requêtes SQL
- Exécuter des requêtes avec `Statement` et `ResultSet`
- Parcourir et afficher des résultats
- Différencier `executeQuery()`, `executeUpdate()` et `execute()`

#### ✅ Sécurité
- Identifier les risques d'injection SQL
- Utiliser `PreparedStatement` pour sécuriser les requêtes
- Paramétrer correctement les requêtes avec `setString()`, `setInt()`, etc.

#### ✅ Architecture
- Structurer le code avec le pattern DAO
- Créer des classes modèles (entités)
- Séparer les responsabilités (modèle, DAO, logique métier)

#### ✅ Opérations CRUD complètes
- **C**reate : insérer des données avec récupération de l'ID généré
- **R**ead : lire et rechercher des données
- **U**pdate : mettre à jour des enregistrements
- **D**elete : supprimer des enregistrements

### 10.2 Bonnes pratiques apprises

1. **Toujours utiliser try-with-resources** pour fermer automatiquement les ressources
2. **Toujours utiliser PreparedStatement** au lieu de Statement (sécurité + performance)
3. **Centraliser la configuration** dans une classe utilitaire (ConnexionUtil)
4. **Structurer avec le pattern DAO** pour séparer l'accès aux données
5. **Gérer proprement les exceptions** avec des messages clairs
6. **Ne jamais hardcoder les mots de passe** dans le code source

### 10.3 Exercices récapitulatifs

#### Exercice 1 : Nouvelle colonne
**Objectif :** Ajouter une colonne `ville` à la table `etudiants`

**Instructions :**
1. Ajoutez la colonne en SQL :
   ```sql
   ALTER TABLE etudiants ADD COLUMN ville VARCHAR(50);
   ```
2. Modifiez la classe `Etudiant` pour inclure l'attribut `ville`
3. Mettez à jour le DAO (méthodes `save()`, `update()`, `extraireEtudiant()`)
4. Testez l'insertion d'un étudiant avec une ville

#### Exercice 2 : Migration MySQL → PostgreSQL
**Objectif :** Faire fonctionner le même code avec PostgreSQL

**Instructions :**
1. Créez une copie de `ConnexionUtil` qui se connecte à PostgreSQL
2. Modifiez `EtudiantDAO` pour accepter un type de connexion en paramètre
3. Testez les mêmes opérations sur les deux bases
4. Identifiez les éventuelles différences de comportement

#### Exercice 3 : Gestion des erreurs et logs
**Objectif :** Améliorer la gestion des erreurs

**Instructions :**
1. Créez une classe `Logger` qui écrit les erreurs dans un fichier `logs.txt`
2. Remplacez tous les `System.err.println()` par des appels au Logger
3. Ajoutez des niveaux de log (INFO, WARNING, ERROR)
4. Testez en provoquant volontairement des erreurs

#### Exercice 4 : Mini-application de gestion
**Objectif :** Créer une application console interactive

**Instructions :**
Créez une classe `ApplicationGestionEtudiants` avec un menu :
```
=== Gestion des Étudiants ===
1. Lister tous les étudiants
2. Rechercher un étudiant par nom
3. Ajouter un étudiant
4. Modifier la note d'un étudiant
5. Supprimer un étudiant
6. Afficher les statistiques
0. Quitter

Votre choix :
```

Implémentez toutes les fonctionnalités en utilisant le DAO.

#### Exercice 5 : Transactions (avancé)
**Objectif :** Gérer les transactions JDBC

**Instructions :**
1. Désactivez l'auto-commit : `connection.setAutoCommit(false)`
2. Créez une méthode `transfererNote(int idSource, int idDest, double points)` qui :
   - Retire des points à un étudiant
   - Ajoute ces points à un autre étudiant
   - Utilise `commit()` si tout va bien
   - Utilise `rollback()` en cas d'erreur
3. Testez les cas normaux et les cas d'erreur

**Exemple de code :**
```java
Connection conn = null;
try {
    conn = ConnexionUtil.getConnexionMySQL();
    conn.setAutoCommit(false); // Démarrer une transaction
    
    // Opération 1
    PreparedStatement pstmt1 = conn.prepareStatement("UPDATE etudiants SET note_moyenne = note_moyenne - ? WHERE id = ?");
    pstmt1.setDouble(1, points);
    pstmt1.setInt(2, idSource);
    pstmt1.executeUpdate();
    
    // Opération 2
    PreparedStatement pstmt2 = conn.prepareStatement("UPDATE etudiants SET note_moyenne = note_moyenne + ? WHERE id = ?");
    pstmt2.setDouble(1, points);
    pstmt2.setInt(2, idDest);
    pstmt2.executeUpdate();
    
    conn.commit(); // Valider la transaction
    System.out.println("✓ Transaction réussie");
    
} catch (SQLException e) {
    if (conn != null) {
        try {
            conn.rollback(); // Annuler la transaction
            System.out.println("✗ Transaction annulée");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    e.printStackTrace();
} finally {
    if (conn != null) {
        try {
            conn.setAutoCommit(true); // Rétablir l'auto-commit
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
```

#### Exercice 6 : Pool de connexions (avancé)
**Objectif :** Améliorer les performances avec un pool de connexions

**Instructions :**
1. Ajoutez la dépendance HikariCP dans `pom.xml` :
   ```xml
   <dependency>
       <groupId>com.zaxxer</groupId>
       <artifactId>HikariCP</artifactId>
       <version>5.0.1</version>
   </dependency>
   ```
2. Créez une classe `ConnectionPool` qui utilise HikariCP
3. Modifiez `ConnexionUtil` pour utiliser le pool
4. Mesurez les performances avec et sans pool (insertion de 1000 étudiants)

#### Exercice 7 : Tests unitaires (avancé)
**Objectif :** Tester le DAO avec JUnit

**Instructions :**
1. Ajoutez la dépendance JUnit 5 dans `pom.xml`
2. Créez une classe `EtudiantDAOTest` dans `src/test/java`
3. Écrivez des tests pour chaque méthode du DAO
4. Utilisez une base de données de test séparée

### 10.4 Pour aller plus loin

#### Concepts JDBC avancés à explorer
- **Batch Processing** : `addBatch()` et `executeBatch()` pour les insertions en masse
- **Métadonnées** : `DatabaseMetaData` et `ResultSetMetaData` pour l'introspection
- **Types de ResultSet** : scrollable, updatable
- **BLOB et CLOB** : gestion des données binaires et texte volumineux
- **Connection Pooling** : HikariCP, Apache DBCP
- **ORM** : Hibernate, JPA pour abstraire complètement JDBC

#### Frameworks et technologies liés
- **Spring JDBC** : simplification de JDBC avec JdbcTemplate
- **MyBatis** : mapping SQL-objet avec XML ou annotations
- **JPA/Hibernate** : ORM complet, requêtes avec JPQL
- **jOOQ** : génération de code Java typé depuis le schéma SQL

#### Ressources recommandées
- Documentation officielle Oracle JDBC : [docs.oracle.com/javase/tutorial/jdbc](https://docs.oracle.com/javase/tutorial/jdbc/)
- MySQL Connector/J Documentation
- PostgreSQL JDBC Driver Documentation
- Livre : "JDBC API Tutorial and Reference" (très complet)

### 10.5 Checklist finale

Avant de considérer ce TP comme terminé, assurez-vous de pouvoir :

- [ ] Créer un projet Maven dans IntelliJ
- [ ] Ajouter et gérer des dépendances Maven
- [ ] Configurer MySQL et PostgreSQL
- [ ] Établir une connexion JDBC
- [ ] Exécuter des SELECT avec Statement
- [ ] Exécuter des INSERT/UPDATE/DELETE avec PreparedStatement
- [ ] Parcourir un ResultSet
- [ ] Récupérer un ID auto-généré
- [ ] Créer une classe DAO complète
- [ ] Gérer proprement les exceptions SQL
- [ ] Utiliser try-with-resources systématiquement
- [ ] Expliquer les risques d'injection SQL
- [ ] Différencier Statement, PreparedStatement et CallableStatement

### 10.6 Mot de la fin

**Félicitations !** Vous avez terminé ce TP JDBC avancé. Vous disposez maintenant de bases solides pour développer des applications Java qui interagissent avec des bases de données relationnelles.

Les compétences que vous avez acquises sont **fondamentales** dans le développement d'applications d'entreprise. Même si les frameworks modernes (comme Spring ou Hibernate) abstraient une partie de JDBC, comprendre ce qui se passe "sous le capot" vous rendra beaucoup plus efficace en tant que développeur.

**Conseils pour la suite :**
1. **Pratiquez régulièrement** : créez de petits projets pour maintenir vos compétences
2. **Explorez les frameworks** : une fois JDBC maîtrisé, découvrez Spring JDBC et JPA
3. **Lisez du code** : étudiez le code de projets open source utilisant JDBC
4. **Partagez vos connaissances** : expliquer à d'autres renforce votre compréhension

N'oubliez pas : le meilleur moyen d'apprendre est de **pratiquer, se tromper, corriger, et recommencer** !

Bon courage pour vos futurs projets ! 🚀

---

## Annexe : Aide-mémoire JDBC

### Connexion
```java
Connection conn = DriverManager.getConnection(url, user, password);
```

### Statement (requêtes statiques)
```java
Statement stmt = conn.createStatement();
ResultSet rs = stmt.executeQuery("SELECT * FROM table");
int n = stmt.executeUpdate("UPDATE table SET col = val");
```

### PreparedStatement (requêtes paramétrées)
```java
PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM table WHERE id = ?");
pstmt.setInt(1, 42);
ResultSet rs = pstmt.executeQuery();
```

### ResultSet (parcours des résultats)
```java
while (rs.next()) {
    int id = rs.getInt("id");
    String nom = rs.getString("nom");
}
```

### Try-with-resources
```java
try (Connection conn = getConnection();
     PreparedStatement pstmt = conn.prepareStatement(sql);
     ResultSet rs = pstmt.executeQuery()) {
    // Utiliser les ressources
} // Fermeture automatique
```

### Récupération d'ID généré
```java
PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
pstmt.executeUpdate();
ResultSet keys = pstmt.getGeneratedKeys();
if (keys.next()) {
    int id = keys.getInt(1);
}
```

### Transaction
```java
conn.setAutoCommit(false);
try {
    // opérations
    conn.commit();
} catch (SQLException e) {
    conn.rollback();
}
```

---

**Fin du TP JDBC Avancé - EMSI Maroc**# TP JDBC Avancé avec Maven, MySQL et PostgreSQL

**Travaux Pratiques - Ingénierie Informatique et Réseaux (2ᵉ année)**  
**EMSI Maroc - Durée estimée : 6-8 heures**

---

## Table des matières

1. [Introduction et architecture JDBC](#partie-1)
2. [Création du projet Maven dans IntelliJ IDEA](#partie-2)
3. [Configuration de Maven et du pom.xml](#partie-3)
4. [Préparation de MySQL pour le TP](#partie-4)
5. [Préparation de PostgreSQL pour le TP](#partie-5)
6. [TP Connexion JDBC : DriverManager et Connection](#partie-6)
7. [TP Requêtes SQL avec Statement et ResultSet](#partie-7)
8. [TP Requêtes paramétrées avec PreparedStatement](#partie-8)
9. [TP Bonus : CallableStatement et mini-DAO](#partie-9)
10. [Conclusion et exercices récapitulatifs](#partie-10)

---

<a name="partie-1"></a>
## Partie 1 : Introduction et architecture JDBC

### Objectifs de cette partie
- Comprendre le rôle et la position de JDBC dans une application Java
- Visualiser l'architecture en couches d'une application utilisant JDBC
- Se familiariser avec les concepts de modèles 2-tiers et 3-tiers
- Situer le contexte du TP dans l'écosystème Java/Base de données

### 1.1 Qu'est-ce que JDBC ?

**JDBC** (Java Database Connectivity) est une API standard du langage Java qui permet aux applications Java de se connecter et d'interagir avec des bases de données relationnelles (MySQL, PostgreSQL, Oracle, SQL Server, etc.). 

JDBC joue le rôle d'**interface unifiée** : vous écrivez du code Java qui utilise l'API JDBC, et selon le driver que vous chargez, votre application peut communiquer avec différents systèmes de gestion de bases de données (SGBD) sans changement majeur de code.

**Avantages de JDBC :**
- **Portabilité** : le même code Java fonctionne avec différents SGBD (à quelques nuances près)
- **Standardisation** : toutes les opérations (connexion, requêtes, transactions) suivent les mêmes interfaces
- **Intégration native** : JDBC fait partie intégrante de la plateforme Java SE

### 1.2 Architecture en couches

Dans une application utilisant JDBC, on distingue généralement plusieurs couches :

```
┌─────────────────────────────────────┐
│   Application Java (votre code)    │
│   (Logique métier, IHM, etc.)      │
└─────────────────┬───────────────────┘
                  │
                  ▼
┌─────────────────────────────────────┐
│         API JDBC (java.sql)         │
│  (Interfaces : Connection,          │
│   Statement, ResultSet, etc.)       │
└─────────────────┬───────────────────┘
                  │
                  ▼
┌─────────────────────────────────────┐
│     Driver JDBC (spécifique SGBD)   │
│  (Ex: mysql-connector-java.jar,     │
│       postgresql.jar)                │
└─────────────────┬───────────────────┘
                  │
                  ▼
┌─────────────────────────────────────┐
│     SGBD (MySQL, PostgreSQL...)     │
│   (Serveur de base de données)      │
└─────────────────────────────────────┘
```

**Explication du schéma :**

1. **Application Java** : c'est votre code métier, vos classes, votre logique applicative
2. **API JDBC** : ensemble d'interfaces standardisées (Connection, Statement, PreparedStatement, ResultSet, etc.)
3. **Driver JDBC** : bibliothèque (JAR) fournie par l'éditeur du SGBD qui implémente l'API JDBC pour communiquer avec le SGBD spécifique
4. **SGBD** : le serveur de base de données (MySQL, PostgreSQL, etc.) qui stocke et gère les données

### 1.3 Modèles d'architecture

#### Modèle 2-tiers (Client-Serveur)

Dans une architecture **2-tiers**, l'application cliente (votre programme Java) se connecte directement au serveur de base de données :

```
┌──────────────────┐         ┌──────────────────┐
│  Client Java     │ ◄─────► │   Serveur BDD    │
│  (Application)   │  JDBC   │ (MySQL/PostgreSQL)│
└──────────────────┘         └──────────────────┘
```

**Caractéristiques :**
- Connexion directe entre le client et la base
- Simple à mettre en œuvre pour les petites applications
- Moins sécurisé (les identifiants de connexion sont dans le code client)
- Scalabilité limitée

#### Modèle 3-tiers (Client-Application Server-Database)

Dans une architecture **3-tiers**, une couche intermédiaire (serveur d'application) gère la logique métier et les accès aux données :

```
┌─────────────┐      ┌──────────────────┐      ┌─────────────┐
│   Client    │ ◄──► │  Serveur App     │ ◄──► │  Serveur    │
│ (Interface) │ HTTP │  (Logique métier)│ JDBC │     BDD     │
└─────────────┘      └──────────────────┘      └─────────────┘
```

**Caractéristiques :**
- Séparation des responsabilités (présentation, logique, données)
- Meilleure sécurité (la BDD n'est pas exposée aux clients)
- Meilleure scalabilité (pool de connexions, load balancing)
- Plus complexe à mettre en place

### 1.4 Lien avec le TP

Dans ce TP, vous allez jouer le rôle du **développeur d'applications Java** qui utilise JDBC pour :

- Vous connecter à deux SGBD différents (MySQL et PostgreSQL)
- Exécuter des requêtes SQL depuis Java
- Manipuler les résultats retournés par la base de données
- Comprendre les bonnes pratiques (PreparedStatement, gestion des ressources, etc.)

Vous travaillerez principalement dans un **modèle 2-tiers** pour des raisons pédagogiques, mais les concepts appris sont directement transposables vers des architectures 3-tiers utilisées en entreprise.

### Questions de réflexion

1. **Pourquoi JDBC utilise-t-il des interfaces plutôt que des classes concrètes ?**
2. **Quels sont les avantages d'utiliser un driver JDBC plutôt que de communiquer directement avec le protocole réseau du SGBD ?**
3. **Dans quel cas préfériez-vous un modèle 2-tiers ? Un modèle 3-tiers ?**

---

<a name="partie-2"></a>
## Partie 2 : Création du projet Maven dans IntelliJ IDEA (TP 1)

### Objectifs de cette partie
- Créer un nouveau projet Maven dans IntelliJ IDEA
- Comprendre la structure standard d'un projet Maven
- Configurer le JDK du projet
- Créer une première classe Java et l'exécuter

### 2.1 Ouverture d'IntelliJ IDEA et création du projet

#### Étape 1 : Lancer IntelliJ IDEA
- Ouvrez IntelliJ IDEA sur votre machine
- Si vous avez déjà des projets ouverts, fermez-les (File → Close Project) pour revenir à l'écran d'accueil

#### Étape 2 : Créer un nouveau projet
1. Cliquez sur **New Project** dans l'écran d'accueil
2. Dans la fenêtre qui s'ouvre :
   - **Generators** : sélectionnez **Maven** dans la liste de gauche
   - **Name** : tapez `TP_JDBC_Avance`
   - **Location** : choisissez un emplacement sur votre disque (par exemple `C:\Users\VotreNom\IdeaProjects\TP_JDBC_Avance`)
   - **JDK** : sélectionnez votre JDK (Java 11 ou supérieur recommandé)
   - **Add sample code** : décochez cette option (nous allons créer notre propre code)
3. Développez la section **Advanced Settings** (en bas de la fenêtre)
   - **GroupId** : tapez `ma.emsi`
   - **ArtifactId** : tapez `tp-jdbc-avance`
   - **Version** : laissez `1.0-SNAPSHOT`
4. Cliquez sur **Create**

#### Étape 3 : Patienter pendant l'indexation
IntelliJ va créer la structure du projet et indexer les bibliothèques. Attendez que la barre de progression en bas à droite disparaisse.

### 2.2 Découverte de la structure Maven

Une fois le projet créé, vous devriez voir dans le panneau **Project** (à gauche) la structure suivante :

```
TP_JDBC_Avance/
├── src/
│   ├── main/
│   │   ├── java/           ← Votre code source principal
│   │   └── resources/      ← Fichiers de configuration
│   └── test/
│       ├── java/           ← Vos tests unitaires
│       └── resources/
├── pom.xml                 ← Fichier de configuration Maven
└── .idea/                  ← Configuration IntelliJ (ignoré par Git)
```

**Explication des dossiers :**

- **src/main/java** : c'est ici que vous allez créer vos classes Java principales
- **src/main/resources** : fichiers de configuration, fichiers properties, etc.
- **src/test/java** : classes de tests JUnit (nous ne l'utiliserons pas dans ce TP)
- **pom.xml** : fichier central de Maven qui décrit le projet et ses dépendances

### 2.3 Vérification du SDK du projet

#### Étape 1 : Ouvrir les paramètres du projet
- Allez dans **File → Project Structure** (ou appuyez sur `Ctrl+Alt+Shift+S`)
- Dans la section **Project**, vérifiez que :
  - **SDK** : affiche votre JDK (ex: "11" ou "17")
  - **Language level** : correspond à votre version Java

#### Étape 2 : Vérifier les modules
- Allez dans la section **Modules**
- Vous devriez voir votre module `tp-jdbc-avance`
- Vérifiez que les dossiers `src/main/java` et `src/main/resources` sont bien marqués comme **Sources** et **Resources** (ils apparaissent en bleu)

Cliquez sur **OK** pour fermer la fenêtre.

### 2.4 Création de la première classe Main

#### Étape 1 : Créer un package
1. Faites un clic droit sur le dossier **src/main/java**
2. Sélectionnez **New → Package**
3. Tapez `ma.emsi.tp` et validez

#### Étape 2 : Créer la classe Main
1. Faites un clic droit sur le package `ma.emsi.tp`
2. Sélectionnez **New → Java Class**
3. Tapez `Main` et validez

#### Étape 3 : Écrire le code de la classe Main

Tapez le code suivant dans la classe `Main` :

```java
package ma.emsi.tp;

/**
 * Classe principale pour tester le projet Maven et JDBC
 */
public class Main {
    
    public static void main(String[] args) {
        System.out.println("=== Bienvenue dans le TP JDBC Avancé ===");
        System.out.println("Projet Maven initialisé avec succès !");
        System.out.println("Prêt à explorer JDBC avec MySQL et PostgreSQL.");
        
        // Vérification de la version Java
        String javaVersion = System.getProperty("java.version");
        System.out.println("\nVersion Java utilisée : " + javaVersion);
    }
}
```

### 2.5 Configuration et exécution

#### Étape 1 : Créer une Run Configuration
1. Faites un clic droit n'importe où dans le code de la classe `Main`
2. Sélectionnez **Run 'Main.main()'** (ou appuyez sur `Ctrl+Shift+F10`)

IntelliJ va compiler et exécuter votre programme.

#### Étape 2 : Observer la sortie
Dans le panneau **Run** en bas de l'écran, vous devriez voir :

```
=== Bienvenue dans le TP JDBC Avancé ===
Projet Maven initialisé avec succès !
Prêt à explorer JDBC avec MySQL et PostgreSQL.

Version Java utilisée : 11.0.15

Process finished with exit code 0
```

**Félicitations !** Votre projet Maven est opérationnel et prêt pour JDBC.

### 2.6 Enregistrer la Run Configuration (optionnel)

Pour ne pas avoir à recréer la configuration à chaque fois :

1. Allez dans **Run → Edit Configurations...**
2. Vous devriez voir votre configuration `Main` dans la liste
3. Vous pouvez la renommer en `TP JDBC - Main` si vous le souhaitez
4. Cliquez sur **OK**

Désormais, vous pourrez exécuter votre programme en cliquant sur le bouton vert **▶ Run** dans la barre d'outils.

### Questions et exercices

1. **Quelle différence entre un projet Maven et un projet Java simple ?**
   - *Indice : pensez à la gestion des dépendances externes*

2. **Exercice** : Modifiez la classe `Main` pour afficher également le nom du système d'exploitation (`System.getProperty("os.name")`)

3. **Exercice** : Créez une deuxième classe `Utils` dans le même package avec une méthode statique `afficherInfosSysteme()` qui affiche diverses propriétés système, puis appelez cette méthode depuis `Main`

---

<a name="partie-3"></a>
## Partie 3 : Configuration de Maven et du pom.xml (TP 2)

### Objectifs de cette partie
- Comprendre le rôle de Maven dans la gestion de projet
- Maîtriser la structure et les principales balises du fichier pom.xml
- Ajouter les dépendances JDBC pour MySQL et PostgreSQL
- Recharger le projet Maven pour intégrer les dépendances

### 3.1 Rappel : Qu'est-ce que Maven ?

**Maven** est un outil de gestion et d'automatisation de construction de projets Java. Il permet principalement de :

- **Gérer les dépendances** : télécharger automatiquement les bibliothèques (JAR) nécessaires depuis des dépôts centraux
- **Standardiser la structure** : tous les projets Maven suivent la même organisation de dossiers
- **Automatiser les tâches** : compilation, tests, packaging (création de JAR/WAR), déploiement
- **Gérer le cycle de vie** : phases prédéfinies (compile, test, package, install, deploy)

**Avantages pour notre TP :**
- Pas besoin de télécharger manuellement les drivers JDBC
- Pas besoin de configurer manuellement le classpath
- Facilité de partage du projet (le `pom.xml` suffit pour recréer l'environnement)

### 3.2 Structure du fichier pom.xml

#### Étape 1 : Ouvrir le fichier pom.xml
Dans le panneau **Project**, double-cliquez sur `pom.xml` à la racine du projet.

Vous devriez voir un fichier similaire à ceci :

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>ma.emsi</groupId>
    <artifactId>tp-jdbc-avance</artifactId>
    <version>1.0-SNAPSHOT</version>

</project>
```

#### Explication des balises principales

| Balise | Description |
|--------|-------------|
| `<modelVersion>` | Version du modèle POM (toujours 4.0.0) |
| `<groupId>` | Identifiant du groupe/organisation (souvent un nom de domaine inversé) |
| `<artifactId>` | Identifiant unique du projet |
| `<version>` | Version du projet (SNAPSHOT = version en développement) |
| `<dependencies>` | Liste des bibliothèques externes nécessaires |
| `<build>` | Configuration du processus de construction (plugins, ressources) |
| `<properties>` | Variables réutilisables dans le POM |

### 3.3 Configuration de la version Java

#### Étape 1 : Ajouter la section properties
Juste après la balise `<version>`, ajoutez :

```xml
<properties>
    <maven.compiler.source>11</maven.compiler.source>
    <maven.compiler.target>11</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
</properties>
```

**Explication :**
- `maven.compiler.source` : version Java du code source
- `maven.compiler.target` : version Java cible pour la compilation
- `project.build.sourceEncoding` : encodage des fichiers (UTF-8 recommandé)

### 3.4 Ajout de la dépendance MySQL

#### Étape 1 : Créer la section dependencies
Après la section `<properties>`, ajoutez :

```xml
<dependencies>
    <!-- Driver JDBC pour MySQL -->
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>8.0.33</version>
    </dependency>
</dependencies>
```

**Explication :**
- `groupId`, `artifactId`, `version` : coordonnées Maven de la bibliothèque
- Maven va télécharger automatiquement le fichier JAR correspondant depuis le dépôt central Maven (https://repo.maven.apache.org)

#### Note sur la version
La version `8.0.33` est une version stable récente. Vous pouvez vérifier les dernières versions sur [mvnrepository.com](https://mvnrepository.com/artifact/mysql/mysql-connector-java).

### 3.5 Ajout de la dépendance PostgreSQL

#### Étape 1 : Ajouter PostgreSQL dans la même section dependencies

Juste après la dépendance MySQL, ajoutez :

```xml
    <!-- Driver JDBC pour PostgreSQL -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <version>42.6.0</version>
    </dependency>
```

Votre section `<dependencies>` complète ressemble maintenant à :

```xml
<dependencies>
    <!-- Driver JDBC pour MySQL -->
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>8.0.33</version>
    </dependency>

    <!-- Driver JDBC pour PostgreSQL -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <version>42.6.0</version>
    </dependency>
</dependencies>
```

### 3.6 Rechargement du projet Maven

#### Étape 1 : Recharger les dépendances
Dès que vous sauvegardez le fichier `pom.xml`, IntelliJ peut afficher une petite icône Maven dans le coin supérieur droit de l'éditeur.

**Méthode 1 : Via l'icône Maven**
- Cliquez sur l'icône 🔄 (Load Maven Changes / Reload All Maven Projects)

**Méthode 2 : Via le panneau Maven**
1. Ouvrez le panneau **Maven** (View → Tool Windows → Maven)
2. Cliquez sur l'icône 🔄 (Reload All Maven Projects) dans la barre d'outils du panneau

#### Étape 2 : Vérifier le téléchargement
Dans le panneau en bas (onglet **Build**), vous devriez voir Maven télécharger les dépendances :

```
Downloading from central: https://repo.maven.apache.org/maven2/mysql/mysql-connector-java/8.0.33/...
Downloaded from central: ...
```

**Note :** Le téléchargement peut prendre quelques secondes selon votre connexion Internet.

#### Étape 3 : Vérifier l'ajout dans External Libraries
1. Dans le panneau **Project**, développez **External Libraries**
2. Vous devriez voir apparaître :
   - `Maven: mysql:mysql-connector-java:8.0.33`
   - `Maven: org.postgresql:postgresql:42.6.0`

**Félicitations !** Les drivers JDBC sont maintenant disponibles dans votre projet.

### 3.7 POM.xml complet pour le TP

Voici le fichier `pom.xml` complet que vous devriez avoir à ce stade :

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>ma.emsi</groupId>
    <artifactId>tp-jdbc-avance</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>11</maven.compiler.source>
        <maven.compiler.target>11</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <!-- Driver JDBC pour MySQL -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.33</version>
        </dependency>

        <!-- Driver JDBC pour PostgreSQL -->
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <version>42.6.0</version>
        </dependency>
    </dependencies>

</project>
```

### 3.8 Comprendre les scopes de dépendance

Maven propose différents **scopes** qui définissent quand une dépendance est utilisée :

| Scope | Description | Exemple d'usage |
|-------|-------------|-----------------|
| **compile** (défaut) | Disponible partout (compilation, tests, exécution) | Drivers JDBC, bibliothèques métier |
| **test** | Uniquement pour les tests | JUnit, Mockito |
| **runtime** | Non nécessaire à la compilation, mais à l'exécution | Drivers JDBC (optionnel) |
| **provided** | Fourni par l'environnement d'exécution | Servlet API (fourni par Tomcat) |

**Dans notre TP**, nous utilisons le scope **compile** (par défaut, pas besoin de le spécifier) car nous allons utiliser les classes des drivers directement dans notre code.

### 🚨 Erreurs fréquentes et débogage

#### Erreur 1 : "Cannot resolve dependency"
**Symptôme** : Maven affiche une erreur rouge dans le `pom.xml`

**Solutions :**
- Vérifiez votre connexion Internet
- Vérifiez l'orthographe des coordonnées Maven (groupId, artifactId, version)
- Essayez de forcer le re-téléchargement : Maven → Reimport (dans le panneau Maven)
- Supprimez le dossier `~/.m2/repository` (cache Maven) et rechargez

#### Erreur 2 : "Project SDK is not defined"
**Symptôme** : Message d'erreur en haut de l'éditeur

**Solution :**
- Allez dans File → Project Structure → Project
- Sélectionnez un SDK dans la liste déroulante
- Si aucun SDK n'apparaît, cliquez sur **Add SDK → Download JDK**

#### Erreur 3 : Les dépendances n'apparaissent pas dans External Libraries
**Solution :**
- Faites un clic droit sur le projet → Maven → Reload Project
- Invalidez les caches : File → Invalidate Caches / Restart

### Questions et exercices

1. **Quelle est la différence entre `groupId` et `artifactId` dans une dépendance Maven ?**

2. **Pourquoi est-il préférable d'utiliser Maven plutôt que de télécharger les JAR manuellement ?**

3. **Exercice** : Ajoutez une troisième dépendance au projet : `slf4j-simple` (pour les logs). Cherchez les coordonnées Maven sur mvnrepository.com et ajoutez-la au `pom.xml`.

---

<a name="partie-4"></a>
## Partie 4 : Préparation de MySQL pour le TP (TP 3 – Partie A)

### Objectifs de cette partie
- Installer et configurer MySQL Server (si ce n'est pas déjà fait)
- Créer une base de données dédiée au TP
- Créer un utilisateur avec les privilèges appropriés
- Créer une table de test "etudiants"
- Comprendre la structure d'une URL de connexion JDBC MySQL

### 4.1 Installation de MySQL (si nécessaire)

#### Si MySQL n'est pas installé sur votre machine

**Option 1 : Installation native**
1. Téléchargez MySQL Community Server depuis [dev.mysql.com/downloads/mysql](https://dev.mysql.com/downloads/mysql/)
2. Lancez l'installateur
3. Choisissez "Developer Default" ou "Server only"
4. Définissez un mot de passe root (notez-le bien !)
5. Terminez l'installation

**Option 2 : Utiliser Docker (recommandé si vous avez Docker)**
```bash
docker run --name mysql-tp-jdbc -e MYSQL_ROOT_PASSWORD=root123 -p 3306:3306 -d mysql:8.0
```

#### Vérification de l'installation
Ouvrez un terminal et tapez :
```bash
mysql --version
```

Vous devriez voir quelque chose comme :
```
mysql  Ver 8.0.33 for Win64 on x86_64
```

### 4.2 Connexion à MySQL

#### Méthode 1 : Via MySQL Workbench (interface graphique)
1. Lancez MySQL Workbench
2. Créez une nouvelle connexion :
   - Connection Name : `TP JDBC Local`
   - Hostname : `localhost`
   - Port : `3306`
   - Username : `root`
   - Password : (votre mot de passe root)
3. Cliquez sur **Test Connection**, puis **OK**
4. Double-cliquez sur la connexion pour l'ouvrir

#### Méthode 2 : Via ligne de commande
Ouvrez un terminal et tapez :
```bash
mysql -u root -p
```
Entrez votre mot de passe root quand demandé.

Vous devriez voir le prompt MySQL :
```
mysql>
```

### 4.3 Création de la base de données

#### Étape 1 : Créer la base de données
Dans MySQL Workbench ou dans le terminal MySQL, exécutez :

```sql
-- Création de la base de données pour le TP
CREATE DATABASE IF NOT EXISTS tp_jdbc
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
```

**Explication :**
- `IF NOT EXISTS` : ne crée la base que si elle n'existe pas déjà
- `CHARACTER SET utf8mb4` : supporte tous les caractères Unicode (y compris émojis)
- `COLLATE utf8mb4_unicode_ci` : règles de tri et comparaison insensibles à la casse

#### Étape 2 : Vérifier la création
```sql
SHOW DATABASES;
```

Vous devriez voir `tp_jdbc` dans la liste.

### 4.4 Création d'un utilisateur dédié

#### Étape 1 : Créer l'utilisateur
Pour des raisons de sécurité, nous allons créer un utilisateur spécifique pour notre application plutôt que d'utiliser root.

```sql
-- Création d'un utilisateur pour l'application
CREATE USER IF NOT EXISTS 'tp_user'@'localhost' 
IDENTIFIED BY 'tp_password123';
```

**Explication :**
- `'tp_user'@'localhost'` : l'utilisateur ne peut se connecter que depuis la machine locale
- `'tp_password123'` : mot de passe (changez-le pour quelque chose de plus sécurisé en production !)

#### Étape 2 : Accorder les privilèges
```sql
-- Accorder tous les privilèges sur la base tp_jdbc
GRANT ALL PRIVILEGES ON tp_jdbc.* TO 'tp_user'@'localhost';

-- Appliquer les changements
FLUSH PRIVILEGES;
```

**Explication :**
- `ALL PRIVILEGES` : SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, etc.
- `tp_jdbc.*` : toutes les tables de la base tp_jdbc
- `FLUSH PRIVILEGES` : recharge les privilèges pour qu'ils soient actifs immédiatement

#### Étape 3 : Vérifier les privilèges
```sql
SHOW GRANTS FOR 'tp_user'@'localhost';
```

Vous devriez voir :
```
GRANT ALL PRIVILEGES ON `tp_jdbc`.* TO `tp_user`@`localhost`
```

### 4.5 Création de la table "etudiants"

#### Étape 1 : Sélectionner la base de données
```sql
USE tp_jdbc;
```

#### Étape 2 : Créer la table
```sql
-- Création de la table etudiants
CREATE TABLE IF NOT EXISTS etudiants (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    date_naissance DATE,
    note_moyenne DECIMAL(4,2),
    date_inscription TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_note CHECK (note_moyenne >= 0 AND note_moyenne <= 20)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

**Explication des colonnes :**
- `id` : clé primaire auto-incrémentée
- `nom`, `prenom` : chaînes de caractères obligatoires
- `email` : unique (pas de doublons) et obligatoire
- `date_naissance` : type DATE (format YYYY-MM-DD)
- `note_moyenne` : décimal avec 2 chiffres après la virgule
- `date_inscription` : horodatage automatique à l'insertion
- `CONSTRAINT chk_note` : contrainte de validation (note entre 0 et 20)

#### Étape 3 : Vérifier la structure
```sql
DESCRIBE etudiants;
```

Ou :
```sql
SHOW CREATE TABLE etudiants;
```

#### Étape 4 : Insérer des données de test
```sql
-- Insertion de quelques étudiants de test
INSERT INTO etudiants (nom, prenom, email, date_naissance, note_moyenne) VALUES
('Alami', 'Fatima', 'f.alami@emsi.ma', '2003-05-15', 15.50),
('Bennani', 'Mohammed', 'm.bennani@emsi.ma', '2002-11-22', 13.75),
('Chakir', 'Amina', 'a.chakir@emsi.ma', '2003-08-30', 16.20),
('Dahane', 'Youssef', 'y.dahane@emsi.ma', '2002-03-10', 12.90),
('El Fassi', 'Sara', 's.elfassi@emsi.ma', '2003-01-25', 17.10);
```

#### Étape 5 : Vérifier l'insertion
```sql
SELECT * FROM etudiants;
```

Vous devriez voir les 5 étudiants insérés.

### 4.6 Comprendre l'URL de connexion JDBC MySQL

Pour se connecter à MySQL via JDBC, nous utiliserons une chaîne de connexion (URL) au format suivant :

```
jdbc:mysql://[host]:[port]/[database]?[paramètres]
```

**Exemple concret pour notre TP :**
```
jdbc:mysql://localhost:3306/tp_jdbc?useSSL=false&serverTimezone=UTC
```

**Décomposition :**
- `jdbc:mysql://` : protocole JDBC pour MySQL
- `localhost` : serveur (127.0.0.1 ou nom d'hôte)
- `3306` : port MySQL par défaut
- `tp_jdbc` : nom de la base de données
- `useSSL=false` : désactive SSL pour les tests locaux (⚠️ à activer en production)
- `serverTimezone=UTC` : définit le fuseau horaire (évite des warnings avec MySQL 8+)

**Autres paramètres utiles :**
```
?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8
```

### 4.7 Test de connexion depuis le terminal

Avant de passer au code Java, testons la connexion avec l'utilisateur créé :

```bash
mysql -u tp_user -ptp_password123 -h localhost tp_jdbc
```

Si vous êtes connecté, tapez :
```sql
SELECT * FROM etudiants;
```

Si vous voyez les données, tout est prêt pour JDBC !

### 🚨 Erreurs fréquentes et débogage

#### Erreur 1 : "Access denied for user 'tp_user'@'localhost'"
**Causes possibles :**
- Mauvais mot de passe
- Utilisateur pas créé correctement
- Privilèges non accordés

**Solution :**
```sql
-- Se connecter en root et vérifier
SELECT User, Host FROM mysql.user WHERE User='tp_user';

-- Si absent, recréer l'utilisateur
DROP USER IF EXISTS 'tp_user'@'localhost';
CREATE USER 'tp_user'@'localhost' IDENTIFIED BY 'tp_password123';
GRANT ALL PRIVILEGES ON tp_jdbc.* TO 'tp_user'@'localhost';
FLUSH PRIVILEGES;
```

#### Erreur 2 : "Unknown database 'tp_jdbc'"
**Solution :**
```sql
-- Vérifier les bases existantes
SHOW DATABASES;

-- Recréer si nécessaire
CREATE DATABASE tp_jdbc;
```

#### Erreur 3 : "Table 'etudiants' already exists"
**Solution :**
```sql
-- Supprimer et recréer
DROP TABLE IF EXISTS etudiants;
-- Puis relancer le CREATE TABLE
```

#### Erreur 4 : Port 3306 déjà utilisé
**Symptôme :** MySQL ne démarre pas

**Solution :**
- Vérifier quel processus utilise le port : `netstat -ano | findstr 3306` (Windows)
- Arrêter MySQL existant : `net stop MySQL80` (Windows)
- Ou changer le port de MySQL dans my.ini/my.cnf

### Questions et exercices

1. **Pourquoi est-il recommandé de créer un utilisateur dédié plutôt que d'utiliser root ?**

2. **Que signifie la contrainte `UNIQUE` sur la colonne email ?**

3. **Exercice** : Ajoutez une colonne `ville VARCHAR(50)` à la table etudiants. Utilisez la commande `ALTER TABLE`.

4. **Exercice** : Écrivez une requête SQL pour afficher uniquement les étudiants ayant une note moyenne supérieure ou égale à 15.

---

<a name="partie-5"></a>
## Partie 5 : Préparation de PostgreSQL pour le TP (TP 3 – Partie B)

### Objectifs de cette partie
- Installer et configurer PostgreSQL Server (si nécessaire)
- Créer une base de données et un utilisateur PostgreSQL
- Créer la même table "etudiants" que pour MySQL
- Identifier les différences entre MySQL et PostgreSQL
- Comprendre l'URL de connexion JDBC PostgreSQL

### 5.1 Installation de PostgreSQL (si nécessaire)

#### Si PostgreSQL n'est pas installé sur votre machine

**Option 1 : Installation native**
1. Téléchargez PostgreSQL depuis [postgresql.org/download](https://www.postgresql.org/download/)
2. Lancez l'installateur
3. Notez bien le mot de passe du superutilisateur `postgres`
4. Port par défaut : `5432`
5. Terminez l'installation

**Option 2 : Utiliser Docker (recommandé)**
```bash
docker run --name postgres-tp-jdbc -e POSTGRES_PASSWORD=postgres123 -p 5432:5432 -d postgres:15
```

#### Vérification de l'installation
```bash
psql --version
```

Résultat attendu :
```
psql (PostgreSQL) 15.3
```

### 5.2 Connexion à PostgreSQL

#### Méthode 1 : Via pgAdmin (interface graphique)
1. Lancez pgAdmin
2. Créez un nouveau serveur :
   - Name : `TP JDBC Local`
   - Host : `localhost`
   - Port : `5432`
   - Username : `postgres`
   - Password : (votre mot de passe postgres)
3. Sauvegardez

#### Méthode 2 : Via ligne de commande (psql)
```bash
psql -U postgres -h localhost
```

Entrez le mot de passe quand demandé.

Vous devriez voir le prompt PostgreSQL :
```
postgres=#
```

### 5.3 Création de la base de données

#### Étape 1 : Créer la base de données
```sql
-- Création de la base de données pour le TP
CREATE DATABASE tp_jdbc
    WITH 
    ENCODING = 'UTF8'
    LC_COLLATE = 'fr_FR.UTF-8'
    LC_CTYPE = 'fr_FR.UTF-8'
    TEMPLATE = template0;
```

**Note :** Si vous avez une erreur avec les locales `fr_FR.UTF-8`, utilisez simplement :
```sql
CREATE DATABASE tp_jdbc
    WITH ENCODING = 'UTF8';
```

#### Étape 2 : Vérifier la création
```sql
\l
```
ou
```sql
SELECT datname FROM pg_database;
```

Vous devriez voir `tp_jdbc` dans la liste.

### 5.4 Création d'un utilisateur dédié

#### Étape 1 : Créer l'utilisateur
```sql
-- Création d'un utilisateur pour l'application
CREATE USER tp_user WITH PASSWORD 'tp_password123';
```

#### Étape 2 : Accorder les privilèges
```sql
-- Accorder tous les privilèges sur la base tp_jdbc
GRANT ALL PRIVILEGES ON DATABASE tp_jdbc TO tp_user;

-- Se connecter à la base tp_jdbc (important !)
\c tp_jdbc

-- Accorder les privilèges sur le schéma public
GRANT ALL ON SCHEMA public TO tp_user;

-- Accorder les privilèges sur les tables futures
ALTER DEFAULT PRIVILEGES IN SCHEMA public 
GRANT ALL ON TABLES TO tp_user;

ALTER DEFAULT PRIVILEGES IN SCHEMA public 
GRANT ALL ON SEQUENCES TO tp_user;
```

**⚠️ Important :** PostgreSQL gère les privilèges différemment de MySQL. Il faut accorder :
1. Les privilèges sur la base de données
2. Les privilèges sur le schéma (généralement `public`)
3. Les privilèges sur les tables

#### Étape 3 : Vérifier les privilèges
```sql
\du
```

Vous devriez voir l'utilisateur `tp_user` dans la liste.

### 5.5 Création de la table "etudiants"

#### Étape 1 : Se connecter à la base tp_jdbc
```sql
\c tp_jdbc
```

#### Étape 2 : Créer la table
```sql
-- Création de la table etudiants (version PostgreSQL)
CREATE TABLE IF NOT EXISTS etudiants (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    date_naissance DATE,
    note_moyenne NUMERIC(4,2),
    date_inscription TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_note CHECK (note_moyenne >= 0 AND note_moyenne <= 20)
);
```

**⚠️ Différences avec MySQL :**
- `SERIAL` au lieu de `INT AUTO_INCREMENT` (pour l'auto-incrémentation)
- `NUMERIC` au lieu de `DECIMAL` (synonymes, mais NUMERIC est plus standard SQL)
- `CURRENT_TIMESTAMP` fonctionne de la même manière

#### Étape 3 : Accorder les privilèges sur la table à tp_user
```sql
-- Important : accorder les droits sur la table créée
GRANT ALL PRIVILEGES ON TABLE etudiants TO tp_user;
GRANT USAGE, SELECT ON SEQUENCE etudiants_id_seq TO tp_user;
```

**Note :** PostgreSQL crée automatiquement une séquence `etudiants_id_seq` pour gérer l'auto-incrémentation de `id`.

#### Étape 4 : Vérifier la structure
```sql
\d etudiants
```

Ou :
```sql
SELECT column_name, data_type, is_nullable 
FROM information_schema.columns 
WHERE table_name = 'etudiants';
```

#### Étape 5 : Insérer des données de test
```sql
-- Insertion de quelques étudiants de test
INSERT INTO etudiants (nom, prenom, email, date_naissance, note_moyenne) VALUES
('Alami', 'Fatima', 'f.alami@emsi.ma', '2003-05-15', 15.50),
('Bennani', 'Mohammed', 'm.bennani@emsi.ma', '2002-11-22', 13.75),
('Chakir', 'Amina', 'a.chakir@emsi.ma', '2003-08-30', 16.20),
('Dahane', 'Youssef', 'y.dahane@emsi.ma', '2002-03-10', 12.90),
('El Fassi', 'Sara', 's.elfassi@emsi.ma', '2003-01-25', 17.10);
```

#### Étape 6 : Vérifier l'insertion
```sql
SELECT * FROM etudiants;
```

### 5.6 Comprendre l'URL de connexion JDBC PostgreSQL

Pour PostgreSQL, l'URL de connexion JDBC a le format suivant :

```
jdbc:postgresql://[host]:[port]/[database]?[paramètres]
```

**Exemple concret pour notre TP :**
```
jdbc:postgresql://localhost:5432/tp_jdbc
```

**Décomposition :**
- `jdbc:postgresql://` : protocole JDBC pour PostgreSQL
- `localhost` : serveur
- `5432` : port PostgreSQL par défaut
- `tp_jdbc` : nom de la base de données

**Paramètres optionnels utiles :**
```
jdbc:postgresql://localhost:5432/tp_jdbc?currentSchema=public&ssl=false
```

### 5.7 Principales différences MySQL vs PostgreSQL

| Aspect | MySQL | PostgreSQL |
|--------|-------|------------|
| **Auto-incrémentation** | `INT AUTO_INCREMENT` | `SERIAL` ou `IDENTITY` |
| **Type décimal** | `DECIMAL(p,s)` | `NUMERIC(p,s)` ou `DECIMAL(p,s)` |
| **Chaînes** | `VARCHAR`, `TEXT` | `VARCHAR`, `TEXT`, `CHAR` |
| **Port par défaut** | 3306 | 5432 |
| **Gestion des privilèges** | Base → Tables | Base → Schéma → Tables |
| **Sensibilité à la casse** | Insensible (par défaut) | Sensible (identifiants en minuscules) |
| **Booléens** | `TINYINT(1)` ou `BOOLEAN` | `BOOLEAN` (vrai type booléen) |
| **Commande d'aide** | `HELP` ou `?` | `\?` |
| **Lister les tables** | `SHOW TABLES;` | `\dt` ou `SELECT * FROM pg_tables;` |

### 5.8 Test de connexion depuis le terminal

Testons la connexion avec l'utilisateur `tp_user` :

```bash
psql -U tp_user -h localhost -d tp_jdbc
```

Entrez le mot de passe : `tp_password123`

Si vous êtes connecté, tapez :
```sql
SELECT * FROM etudiants;
```

Si vous voyez les données, tout est prêt !

### 🚨 Erreurs fréquentes et débogage

#### Erreur 1 : "FATAL: password authentication failed for user 'tp_user'"
**Solution :**
```sql
-- Se connecter en postgres et vérifier
\c postgres postgres
SELECT usename FROM pg_user WHERE usename='tp_user';

-- Si absent, recréer
DROP USER IF EXISTS tp_user;
CREATE USER tp_user WITH PASSWORD 'tp_password123';

-- Redonner les privilèges
\c tp_jdbc
GRANT ALL PRIVILEGES ON DATABASE tp_jdbc TO tp_user;
GRANT ALL ON SCHEMA public TO tp_user;
```

#### Erreur 2 : "ERROR: permission denied for table etudiants"
**Cause :** L'utilisateur n'a pas les droits sur la table

**Solution :**
```sql
-- Se connecter en postgres
\c tp_jdbc postgres

-- Accorder les droits
GRANT ALL PRIVILEGES ON TABLE etudiants TO tp_user;
GRANT USAGE, SELECT ON SEQUENCE etudiants_id_seq TO tp_user;
```

#### Erreur 3 : "FATAL: database 'tp_jdbc' does not exist"
**Solution :**
```sql
-- Lister les bases
\l

-- Créer si nécessaire
CREATE DATABASE tp_jdbc;
```

#### Erreur 4 : "ERROR: relation 'etudiants' already exists"
**Solution :**
```sql
DROP TABLE IF EXISTS etudiants CASCADE;
-- Puis relancer le CREATE TABLE
```

### Questions et exercices

1. **Quelle est la principale différence syntaxique entre MySQL et PostgreSQL pour l'auto-incrémentation ?**

2. **Pourquoi PostgreSQL nécessite-t-il d'accorder des privilèges sur le schéma `public` en plus de la base de données ?**

3. **Exercice** : Écrivez une requête qui fonctionne à la fois sur MySQL et PostgreSQL pour compter le nombre d'étudiants dont le nom commence par 'A'.

4. **Exercice** : Dans PostgreSQL, utilisez la commande `\d etudiants` pour afficher la structure de la table. Identifiez le nom de la séquence créée automatiquement.

---

<a name="partie-6"></a>
## Partie 6 : TP Connexion JDBC : DriverManager et Connection (TP 4)

### Objectifs de cette partie
- Comprendre le rôle de DriverManager et Connection dans JDBC
- Se connecter à MySQL depuis Java
- Se connecter à PostgreSQL depuis Java
- Maîtriser le try-with-resources pour la gestion des ressources
- Diagnostiquer les erreurs de connexion courantes

### 6.1 Rappels théoriques

#### Le DriverManager
`DriverManager` est une classe de l'API JDBC (`java.sql.DriverManager`) qui joue le rôle de **gestionnaire de pilotes**. Son rôle principal :

- Charger et gérer les drivers JDBC disponibles
- Établir une connexion à la base de données via une URL JDBC
- Sélectionner automatiquement le driver approprié selon l'URL

#### L'interface Connection
`Connection` est une interface (`java.sql.Connection`) qui représente une **session avec la base de données**. Elle permet de :

- Créer des instructions SQL (Statement, PreparedStatement, CallableStatement)
- Gérer les transactions (commit, rollback)
- Obtenir des métadonnées sur la base
- Fermer la connexion quand elle n'est plus nécessaire

**⚠️ Important :** Une Connection est une ressource qu'il faut **toujours fermer** après utilisation pour éviter les fuites mémoire et les connexions bloquées.

### 6.2 TP : Connexion à MySQL

#### Étape 1 : Créer le package de connexion
Dans IntelliJ :
1. Faites un clic droit sur `src/main/java/ma/emsi/tp`
2. New → Package
3. Nommez-le `connexion`

#### Étape 2 : Créer la classe TestConnexionMySQL
1. Clic droit sur le package `connexion`
2. New → Java Class
3. Nommez-la `TestConnexionMySQL`

#### Étape 3 : Écrire le code de connexion

Tapez le code suivant (commentaires inclus pour la pédagogie) :

```java
package ma.emsi.tp.connexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe de test pour la connexion JDBC à MySQL
 */
public class TestConnexionMySQL {
    
    // Paramètres de connexion à MySQL
    private static final String URL = "jdbc:mysql://localhost:3306/tp_jdbc";
    private static final String USER = "tp_user";
    private static final String PASSWORD = "tp_password123";
    
    public static void main(String[] args) {
        System.out.println("=== Test de connexion à MySQL ===\n");
        
        // Try-with-resources : la connexion sera automatiquement fermée
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            
            // Si on arrive ici, la connexion a réussi
            System.out.println("✓ Connexion réussie à MySQL !");
            
            // Affichage d'informations sur la connexion
            System.out.println("URL : " + URL);
            System.out.println("Utilisateur : " + USER);
            System.out.println("Base de données : " + connection.getCatalog());
            System.out.println("Driver : " + connection.getMetaData().getDriverName());
            System.out.println("Version du driver : " + connection.getMetaData().getDriverVersion());
            
        } catch (SQLException e) {
            // En cas d'erreur de connexion
            System.err.println("✗ Échec de la connexion à MySQL !");
            System.err.println("Raison : " + e.getMessage());
            System.err.println("Code d'erreur SQL : " + e.getErrorCode());
            e.printStackTrace();
        }
        
        System.out.println("\n=== Fin du test ===");
    }
}
```

**Explication du code :**

1. **Constantes de connexion** : définies en haut pour faciliter les modifications
2. **try-with-resources** : `try (Connection conn = ...)` garantit que la connexion sera fermée automatiquement, même en cas d'exception
3. **DriverManager.getConnection()** : tente d'établir la connexion
4. **connection.getMetaData()** : permet d'obtenir des informations sur la base et le driver
5. **Bloc catch** : capture et affiche les erreurs de connexion

#### Étape 4 : Exécuter le programme
1. Clic droit dans le code → Run 'TestConnexionMySQL.main()'
2. Ou cliquez sur la flèche verte à côté de `public static void main`

**Résultat attendu :**
```
=== Test de connexion à MySQL ===

✓ Connexion réussie à MySQL !
URL : jdbc:mysql://localhost:3306/tp_jdbc
Utilisateur : tp_user
Base de données : tp_jdbc
Driver : MySQL Connector/J
Version du driver : mysql-connector-java-8.0.33

=== Fin du test ===
```

### 6.3 TP : Connexion à PostgreSQL

#### Étape 1 : Créer la classe TestConnexionPostgreSQL
Dans le même package `connexion`, créez `TestConnexionPostgreSQL`.

#### Étape 2 : Écrire le code

```java
package ma.emsi.tp.connexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe de test pour la connexion JDBC à PostgreSQL
 */
public class TestConnexionPostgreSQL {
    
    // Paramètres de connexion à PostgreSQL
    private static final String URL = "jdbc:postgresql://localhost:5432/tp_jdbc";
    private static final String USER = "tp_user";
    private static final String PASSWORD = "tp_password123";
    
    public static void main(String[] args) {
        System.out.println("=== Test de connexion à PostgreSQL ===\n");
        
        // Try-with-resources pour gérer automatiquement la fermeture
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            
            // Connexion réussie
            System.out.println("✓ Connexion réussie à PostgreSQL !");
            
            // Informations sur la connexion
            System.out.println("URL : " + URL);
            System.out.println("Utilisateur : " + USER);
            System.out.println("Base de données : " + connection.getCatalog());
            System.out.println("Schéma actuel : " + connection.getSchema());
            System.out.println("Driver : " + connection.getMetaData().getDriverName());
            System.out.println("Version du driver : " + connection.getMetaData().getDriverVersion());
            System.out.println("Version PostgreSQL : " + connection.getMetaData().getDatabaseProductVersion());
            
        } catch (SQLException e) {
            // Gestion des erreurs
            System.err.println("✗ Échec de la connexion à PostgreSQL !");
            System.err.println("Raison : " + e.getMessage());
            System.err.println("Code d'erreur SQL : " + e.getErrorCode());
            System.err.println("État SQL : " + e.getSQLState());
            e.printStackTrace();
        }
        
        System.out.println("\n=== Fin du test ===");
    }
}
```

**Différences notables avec MySQL :**
- L'URL commence par `jdbc:postgresql://`
- Port par défaut : `5432` au lieu de `3306`
- Méthode `connection.getSchema()` plus pertinente pour PostgreSQL

#### Étape 3 : Exécuter
Lancez le programme de la même manière.

**Résultat attendu :**
```
=== Test de connexion à PostgreSQL ===

✓ Connexion réussie à PostgreSQL !
URL : jdbc:postgresql://localhost:5432/tp_jdbc
Utilisateur : tp_user
Base de données : tp_jdbc
Schéma actuel : public
Driver : PostgreSQL JDBC Driver
Version du driver : 42.6.0
Version PostgreSQL : 15.3

=== Fin du test ===
```

### 6.4 Amélioration : Classe utilitaire de connexion

Pour éviter la duplication de code, créons une classe utilitaire.

#### Étape 1 : Créer la classe ConnexionUtil

```java
package ma.emsi.tp.connexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe utilitaire pour gérer les connexions JDBC
 */
public class ConnexionUtil {
    
    // Configuration MySQL
    private static final String MYSQL_URL = "jdbc:mysql://localhost:3306/tp_jdbc";
    private static final String MYSQL_USER = "tp_user";
    private static final String MYSQL_PASSWORD = "tp_password123";
    
    // Configuration PostgreSQL
    private static final String POSTGRES_URL = "jdbc:postgresql://localhost:5432/tp_jdbc";
    private static final String POSTGRES_USER = "tp_user";
    private static final String POSTGRES_PASSWORD = "tp_password123";
    
    /**
     * Obtenir une connexion à MySQL
     * @return Connection à MySQL
     * @throws SQLException en cas d'erreur de connexion
     */
    public static Connection getConnexionMySQL() throws SQLException {
        return DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD);
    }
    
    /**
     * Obtenir une connexion à PostgreSQL
     * @return Connection à PostgreSQL
     * @throws SQLException en cas d'erreur de connexion
     */
    public static Connection getConnexionPostgreSQL() throws SQLException {
        return DriverManager.getConnection(POSTGRES_URL, POSTGRES_USER, POSTGRES_PASSWORD);
    }
    
    /**
     * Fermer une connexion de manière sécurisée
     * @param connection la connexion à fermer
     */
    public static void fermerConnexion(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Connexion fermée avec succès.");
            } catch (SQLException e) {
                System.err.println("Erreur lors de la fermeture : " + e.getMessage());
            }
        }
    }
    
    /**
     * Tester les deux connexions
     */
    public static void main(String[] args) {
        System.out.println("=== Test des connexions via ConnexionUtil ===\n");
        
        // Test MySQL
        try (Connection connMySQL = getConnexionMySQL()) {
            System.out.println("✓ MySQL : " + connMySQL.getMetaData().getDatabaseProductName());
        } catch (SQLException e) {
            System.err.println("✗ MySQL : " + e.getMessage());
        }
        
        // Test PostgreSQL
        try (Connection connPostgres = getConnexionPostgreSQL()) {
            System.out.println("✓ PostgreSQL : " + connPostgres.getMetaData().getDatabaseProductName());
        } catch (SQLException e) {
            System.err.println("✗ PostgreSQL : " + e.getMessage());
        }
    }
}
```

**Avantages de cette approche :**
- Centralisation de la configuration
- Réutilisabilité du code
- Facilité de maintenance
- Méthode de fermeture sécurisée

### 🚨 Erreurs fréquentes et débogage

#### Erreur 1 : ClassNotFoundException: com.mysql.cj.jdbc.Driver

**Symptôme :**
```
java.lang.ClassNotFoundException: com.mysql.cj.jdbc.Driver
```

**Cause :** Le driver MySQL n'est pas dans le classpath (dépendance Maven manquante ou non chargée)

**Solution :**
1. Vérifiez que la dépendance est bien dans `pom.xml`
2. Rechargez Maven : icône 🔄 dans le panneau Maven
3. Vérifiez dans External Libraries que `mysql-connector-java` est présent

#### Erreur 2 : SQLException: Access denied for user 'tp_user'@'localhost'

**Symptôme :**
```
java.sql.SQLException: Access denied for user 'tp_user'@'localhost' (using password: YES)
```

**Causes possibles :**
- Mauvais mot de passe
- Utilisateur non créé
- Privilèges non accordés

**Solution :**
1. Vérifiez les identifiants dans le code
2. Reconnectez-vous à MySQL/PostgreSQL en ligne de commande avec ces identifiants
3. Recréez l'utilisateur si nécessaire (voir Partie 4 et 5)

#### Erreur 3 : SQLException: Communications link failure

**Symptôme :**
```
com.mysql.cj.jdbc.exceptions.CommunicationsException: Communications link failure
```

**Causes possibles :**
- Le serveur MySQL/PostgreSQL n'est pas démarré
- Mauvais port ou hôte dans l'URL
- Pare-feu bloquant la connexion

**Solution :**
1. Vérifiez que le serveur tourne :
   ```bash
   # MySQL (Windows)
   net start MySQL80
   
   # PostgreSQL (Windows)
   net start postgresql-x64-15
   ```
2. Vérifiez le port avec `netstat -ano | findstr 3306` (MySQL) ou `findstr 5432` (PostgreSQL)
3. Testez la connexion en ligne de commande avant de retester en Java

#### Erreur 4 : SQLException: Unknown database 'tp_jdbc'

**Symptôme :**
```
java.sql.SQLException: Unknown database 'tp_jdbc'
```

**Cause :** La base de données n'existe pas

**Solution :**
```sql
CREATE DATABASE tp_jdbc;
```

#### Erreur 5 : java.sql.SQLTimeoutException: Connection timed out

**Cause :** Le serveur est inaccessible (hôte incorrect, réseau)

**Solution :**
- Vérifiez que `localhost` est correct (essayez `127.0.0.1`)
- Vérifiez que le serveur n'est pas configuré pour n'accepter que certaines IPs

### 6.5 Bonnes pratiques

#### 1. Toujours utiliser try-with-resources
```java
// ✓ BON
try (Connection conn = DriverManager.getConnection(url, user, pwd)) {
    // utiliser conn
} // conn.close() appelé automatiquement

// ✗ MAUVAIS
Connection conn = DriverManager.getConnection(url, user, pwd);
// utiliser conn
conn.close(); // Peut ne jamais être appelé si exception avant
```

#### 2. Ne jamais hardcoder les mots de passe
```java
// ✗ MAUVAIS : mot de passe en dur dans le code
private static final String PASSWORD = "tp_password123";

// ✓ BON : utiliser un fichier de configuration
// Créer src/main/resources/db.properties :
// mysql.url=jdbc:mysql://localhost:3306/tp_jdbc
// mysql.user=tp_user
// mysql.password=tp_password123

Properties props = new Properties();
props.load(new FileInputStream("src/main/resources/db.properties"));
String url = props.getProperty("mysql.url");
String user = props.getProperty("mysql.user");
String password = props.getProperty("mysql.password");
```

#### 3. Gérer proprement les exceptions
```java
// ✓ BON : messages clairs et logging
try (Connection conn = getConnection()) {
    // ...
} catch (SQLException e) {
    System.err.println("Erreur de connexion : " + e.getMessage());
    System.err.println("Code SQL : " + e.getSQLState());
    // En production : logger.error("Erreur de connexion", e);
}
```

### Questions et exercices

1. **Pourquoi utilise-t-on try-with-resources plutôt qu'un simple try-catch-finally ?**

2. **Quelle est la différence entre `e.getMessage()`, `e.getSQLState()` et `e.getErrorCode()` ?**

3. **Exercice** : Créez un fichier `db.properties` dans `src/main/resources` et modifiez `ConnexionUtil` pour lire les paramètres depuis ce fichier.

4. **Exercice** : Ajoutez une méthode `testConnexion(String dbType)` dans `ConnexionUtil` qui prend "mysql" ou "postgresql" en paramètre et teste la connexion correspondante.

---

<a name="partie-7"></a>
## Partie 7 : TP Requêtes SQL avec Statement et ResultSet (TP 5)

### Objectifs de cette partie
- Comprendre les rôles de Statement et ResultSet
- Exécuter des requêtes SELECT
- Parcourir et afficher les résultats
- Comprendre les différences entre executeQuery, executeUpdate et execute
- Créer une application simple de consultation

### 7.1 Rappels théoriques

#### L'interface Statement
`Statement` est une interface qui permet d'exécuter des requêtes SQL **statiques** (sans paramètres) :

```java
Statement stmt = connection.createStatement();
ResultSet rs = stmt.executeQuery("SELECT * FROM etudiants");
```

**Méthodes principales :**
- `executeQuery(String sql)` : pour les SELECT (retourne ResultSet)
- `executeUpdate(String sql)` : pour INSERT, UPDATE, DELETE (retourne int = nombre de lignes affectées)
- `execute(String sql)` : pour tout type de requête (retourne boolean)

#### L'interface ResultSet
`ResultSet` représente l'ensemble des résultats d'une requête SELECT. C'est comme un **curseur** qui pointe sur une ligne à la fois :

```java
while (resultSet.next()) {  // Passe à la ligne suivante
    int id = resultSet.getInt("id");
    String nom = resultSet.getString("nom");
    // ...
}
```

**Méthodes de navigation :**
- `next()` : passe à la ligne suivante (retourne false si fin atteinte)
- `previous()` : ligne précédente (si ResultSet scrollable)
- `first()`, `last()` : première/dernière ligne

**Méthodes de lecture :**
- `getInt(String colonne)` ou `getInt(int index)`
- `getString(...)`, `getDouble(...)`, `getDate(...)`, etc.

### 7.2 TP : Lister tous les étudiants

#### Étape 1 : Créer le package requetes
1. Clic droit sur `src/main/java/ma/emsi/tp`
2. New → Package
3. Nommez-le `requetes`

#### Étape 2 : Créer la classe ListerEtudiants

```java
package ma.emsi.tp.requetes;

import ma.emsi.tp.connexion.ConnexionUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Classe pour lister tous les étudiants de la base
 */
public class ListerEtudiants {
    
    public static void main(String[] args) {
        System.out.println("=== Liste des étudiants (MySQL) ===\n");
        
        // Requête SQL
        String sql = "SELECT * FROM etudiants ORDER BY nom, prenom";
        
        // Try-with-resources pour Connection, Statement et ResultSet
        try (Connection connection = ConnexionUtil.getConnexionMySQL();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            
            // Affichage de l'en-tête
            System.out.println("╔════╦══════════════╦══════════════╦════════════════════════╦═══════════════╦══════════╗");
            System.out.println("║ ID ║     NOM      ║    PRÉNOM    ║         EMAIL          ║   NAISSANCE   ║   NOTE   ║");
            System.out.println("╠════╬══════════════╬══════════════╬════════════════════════╬═══════════════╬══════════╣");
            
            // Parcours des résultats
            int compteur = 0;
            while (resultSet.next()) {
                // Récupération des colonnes
                int id = resultSet.getInt("id");
                String nom = resultSet.getString("nom");
                String prenom = resultSet.getString("prenom");
                String email = resultSet.getString("email");
                java.sql.Date dateNaissance = resultSet.getDate("date_naissance");
                double noteMoyenne = resultSet.getDouble("note_moyenne");
                
                // Affichage formaté
                System.out.printf("║ %-2d ║ %-12s ║ %-12s ║ %-22s ║ %-13s ║ %8.2f ║%n",
                        id, nom, prenom, email, dateNaissance, noteMoyenne);
                
                compteur++;
            }
            
            System.out.println("╚════╩══════════════╩══════════════╩════════════════════════╩═══════════════╩══════════╝");
            System.out.println("\nTotal : " + compteur + " étudiant(s)");
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des étudiants :");
            System.err.println("  Message : " + e.getMessage());
            System.err.println("  Code SQL : " + e.getSQLState());
            e.printStackTrace();
        }
    }
}
```

**Points clés du code :**
1. **Triple try-with-resources** : ferme automatiquement Connection, Statement ET ResultSet
2. **resultSet.next()** : avance le curseur et retourne true si une ligne existe
3. **Getters typés** : `getInt()`, `getString()`, `getDouble()`, `getDate()`
4. **Formatage** : `printf()` pour un affichage tabulaire propre

#### Étape 3 : Exécuter

**Résultat attendu :**
```
=== Liste des étudiants (MySQL) ===

╔════╦══════════════╦══════════════╦════════════════════════╦═══════════════╦══════════╗
║ ID ║     NOM      ║    PRÉNOM    ║         EMAIL          ║   NAISSANCE   ║   NOTE   ║
╠════╬══════════════╬══════════════╬════════════════════════╬═══════════════╬══════════╣
║ 1  ║ Alami        ║ Fatima       ║ f.alami@emsi.ma        ║ 2003-05-15    ║    15.50 ║
║ 2  ║ Bennani      ║ Mohammed     ║ m.bennani@emsi.ma      ║ 2002-11-22    ║    13.75 ║
║ 3  ║ Chakir       ║ Amina        ║ a.chakir@emsi.ma       ║ 2003-08-30    ║    16.20 ║
║ 4  ║ Dahane       ║ Youssef      ║ y.dahane@emsi.ma       ║ 2002-03-10    ║    12.90 ║
║ 5  ║ El Fassi     ║ Sara         ║ s.elfassi@emsi.ma      ║ 2003-01-25    ║    17.10 ║
╚════╩══════════════╩══════════════╩════════════════════════╩═══════════════╩══════════╝

Total : 5 étudiant(s)
```

### 7.3 TP : Recherche avec filtre

#### Créer la classe RechercherEtudiantsParNote

```java
package ma.emsi.tp.requetes;

import ma.emsi.tp.connexion.ConnexionUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Recherche des étudiants ayant une note >= 15
 */
public class RechercherEtudiantsParNote {
    
    public static void main(String[] args) {
        System.out.println("=== Étudiants avec note >= 15 ===\n");
        
        double seuilNote = 15.0;
        String sql = "SELECT nom, prenom, note_moyenne " +
                     "FROM etudiants " +
                     "WHERE note_moyenne >= " + seuilNote + " " +
                     "ORDER BY note_moyenne DESC";
        
        try (Connection connection = ConnexionUtil.getConnexionMySQL();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            
            System.out.println("Étudiants ayant au moins " + seuilNote + "/20 :\n");
            
            while (resultSet.next()) {
                String nom = resultSet.getString("nom");
                String prenom = resultSet.getString("prenom");
                double note = resultSet.getDouble("note_moyenne");
                
                System.out.printf("  - %s %s : %.2f/20%n", prenom, nom, note);
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
```

**⚠️ Attention :** Cette approche (concaténation de valeurs dans la requête) est **dangereuse** et vulnérable aux **injections SQL**. Nous verrons la bonne méthode avec `PreparedStatement` dans la partie suivante.

### 7.4 Les trois méthodes execute

#### executeQuery() - Pour les SELECT
```java
String sql = "SELECT * FROM etudiants";
ResultSet rs = statement.executeQuery(sql);
// Retourne un ResultSet
```

#### executeUpdate() - Pour INSERT, UPDATE, DELETE
```java
String sql = "UPDATE etudiants SET note_moyenne = 18.0 WHERE id = 1";
int nbLignes = statement.executeUpdate(sql);
System.out.println(nbLignes + " ligne(s) modifiée(s)");
// Retourne le nombre de lignes affectées
```

#### execute() - Pour tout type de requête
```java
String sql = "...";
boolean estResultSet = statement.execute(sql);
if (estResultSet) {
    ResultSet rs = statement.getResultSet();
    // Traiter le ResultSet
} else {
    int nbLignes = statement.getUpdateCount();
    // Traiter le nombre de lignes
}
```

**Quand utiliser quoi ?**
- **executeQuery()** : toujours pour SELECT
- **executeUpdate()** : pour INSERT/UPDATE/DELETE/CREATE/DROP
- **execute()** : quand on ne sait pas à l'avance le type de requête (rare)

### 7.5 TP : Statistiques sur les étudiants

#### Créer la classe StatistiquesEtudiants

```java
package ma.emsi.tp.requetes;

import ma.emsi.tp.connexion.ConnexionUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Affiche des statistiques sur les étudiants
 */
public class StatistiquesEtudiants {
    
    public static void main(String[] args) {
        System.out.println("=== Statistiques sur les étudiants ===\n");
        
        String sqlStats = "SELECT " +
                "COUNT(*) as nombre_etudiants, " +
                "AVG(note_moyenne) as moyenne_generale, " +
                "MIN(note_moyenne) as note_min, " +
                "MAX(note_moyenne) as note_max " +
                "FROM etudiants";
        
        try (Connection connection = ConnexionUtil.getConnexionMySQL();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sqlStats)) {
            
            if (rs.next()) {
                int nombre = rs.getInt("nombre_etudiants");
                double moyenne = rs.getDouble("moyenne_generale");
                double noteMin = rs.getDouble("note_min");
                double noteMax = rs.getDouble("note_max");
                
                System.out.println("┌─────────────────────────────────────┐");
                System.out.printf("│ Nombre d'étudiants : %-14d │%n", nombre);
                System.out.printf("│ Moyenne générale   : %-14.2f │%n", moyenne);
                System.out.printf("│ Note minimale      : %-14.2f │%n", noteMin);
                System.out.printf("│ Note maximale      : %-14.2f │%n", noteMax);
                System.out.println("└─────────────────────────────────────┘");
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }
}
```

### 🚨 Erreurs fréquentes et débogage

#### Erreur 1 : SQLException: Column 'xyz' not found

**Cause :** Nom de colonne incorrect dans `resultSet.getString("xyz")`

**Solution :**
- Vérifiez l'orthographe exacte de la colonne
- Utilisez `resultSet.getMetaData().getColumnCount()` pour lister les colonnes disponibles
- Ou utilisez l'index : `resultSet.getString(1)` (commence à 1, pas 0 !)

#### Erreur 2 : SQLException: ResultSet is closed

**Cause :** Tentative d'accès au ResultSet après la fermeture de la connexion

**Solution :**
- Assurez-vous de traiter le ResultSet DANS le bloc try-with-resources
- Ne retournez jamais un ResultSet d'une méthode (il sera fermé)

#### Erreur 3 : SQLException: Before start of result set

**Cause :** Tentative de lecture avant d'appeler `next()`

**Solution :**
```java
// ✗ MAUVAIS
ResultSet rs = statement.executeQuery(sql);
String nom = rs.getString("nom"); // ERREUR : curseur avant la première ligne

// ✓ BON
ResultSet rs = statement.executeQuery(sql);
if (rs.next()) {
    String nom = rs.getString("nom");
}
```

### Questions et exercices

1. **Quelle est la différence entre `executeQuery()` et `executeUpdate()` ?**

2. **Pourquoi ne doit-on jamais concaténer des valeurs directement dans une requête SQL ?**

3. **Exercice** : Créez une classe `CompterEtudiantsParNote` qui affiche le nombre d'étudiants pour chaque tranche de notes :
   - 0-9.99
   - 10-11.99
   - 12-13.99
   - 14-15.99
   - 16-20

4. **Exercice** : Modifiez `ListerEtudiants` pour qu'elle fonctionne avec PostgreSQL. Testez les deux versions
