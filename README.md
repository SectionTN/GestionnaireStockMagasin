# Gestionnaire de Stock de Magasin

Application Java Swing pour la gestion du stock d'un magasin avec authentification basée
sur les rôles. Backend : **Appwrite Cloud** via l'API REST **TablesDB**.

## Pile technique

- **Java 17** + **Swing** + **FlatLaf IntelliJ Theme** (apparence moderne)
- **Appwrite Cloud** — base de données via API REST TablesDB
- **Gson** — JSON
- **jBCrypt** — hachage des mots de passe
- **Maven** — build

## Prérequis

1. JDK 17+
2. Maven 3.9+ (`sudo pacman -S maven` sur Arch / `brew install maven` sur macOS)
3. Compte Appwrite Cloud — https://cloud.appwrite.io

## Configuration Appwrite Cloud (une seule fois)

### 1. Créer le projet

1. Se connecter à https://cloud.appwrite.io
2. **Create project** → nom : "Magasin" → noter le **Project ID**
3. **Settings → API keys → Create API Key** :
   - Nom : "magasin-java"
   - Expiration : pas d'expiration (ou choix)
   - Scopes : `tables.read`, `tables.write`, `rows.read`, `rows.write`
   - **Copier la clé API immédiatement** (affichée une seule fois)

### 2. Créer la base de données et les tables

Dans **Databases → TablesDB → Create database** :
- Nom : `magasin_db` → noter le **Database ID**

Créer 4 tables (clic sur **Create table** dans la base) :

#### Table `produit`
| Colonne     | Type    | Taille | Requis |
|-------------|---------|--------|--------|
| nom         | String  | 255    | Oui    |
| quantite    | Integer | —      | Oui    |
| prix        | Double  | —      | Oui    |
| fournisseur | String  | 255    | Non    |

#### Table `fournisseur`
| Colonne | Type   | Taille | Requis |
|---------|--------|--------|--------|
| nom     | String | 255    | Oui    |
| contact | String | 255    | Non    |

#### Table `commande`
| Colonne       | Type    | Taille | Requis |
|---------------|---------|--------|--------|
| produit_id    | String  | 36     | Oui    |
| quantite      | Integer | —      | Oui    |
| date_commande | String  | 32     | Oui    |

#### Table `utilisateur`
| Colonne  | Type   | Taille | Requis | Notes                                          |
|----------|--------|--------|--------|------------------------------------------------|
| username | String | 64     | Oui    | Ajouter un index unique sur cette colonne     |
| password | String | 255    | Oui    | Hash bcrypt (60 caractères)                   |
| role     | Enum   | —      | Oui    | Valeurs : `administrateur`, `gestionnaire`    |

**Permissions** : pour chaque table, dans l'onglet **Settings**, donner accès *Create / Read /
Update / Delete* à `Any` ou utiliser la clé API (notre client envoie `X-Appwrite-Key`).

### 3. Configuration locale

Copier `src/main/resources/config.properties.example` vers `config.properties` et remplir :

```properties
APPWRITE_ENDPOINT=https://cloud.appwrite.io/v1
APPWRITE_PROJECT_ID=ton_project_id
APPWRITE_API_KEY=ta_cle_api
APPWRITE_DATABASE_ID=ton_database_id

TABLE_PRODUIT=produit
TABLE_FOURNISSEUR=fournisseur
TABLE_COMMANDE=commande
TABLE_UTILISATEUR=utilisateur
```

### 4. Créer le premier administrateur

Générer un hash BCrypt pour le mot de passe :

```bash
mvn compile exec:java \
  -Dexec.mainClass=com.magasin.outils.GenererHashAdmin \
  -Dexec.args="motDePasseAdmin"
```

Copier le hash affiché, puis dans la console Appwrite → table `utilisateur` →
**Create row** avec :

- `username` : `admin`
- `password` : (coller le hash bcrypt)
- `role` : `administrateur`

## Compiler et lancer

```bash
mvn clean package
java -jar target/gestionnaire-stock-magasin.jar
```

Ou en mode développement :

```bash
mvn compile exec:java
```

### Réglage HiDPI (écran haute densité)

Par défaut, l'application force `uiScale=2.0` sur Linux. Si l'interface paraît trop grande
ou trop petite, override via une variable d'environnement :

```bash
MAGASIN_UI_SCALE=1.5 java -jar target/gestionnaire-stock-magasin.jar
# ou
java -Dsun.java2d.uiScale=1.0 -jar target/gestionnaire-stock-magasin.jar
```

Valeurs courantes : `1.0` (FullHD standard), `1.5` (laptop HiDPI), `2.0` (4K).

## Fonctionnalités

### Authentification
- Page de connexion (nom d'utilisateur + mot de passe)
- Vérification dans la table `utilisateur`
- Deux rôles : **administrateur** et **gestionnaire**
- Redirection vers le tableau de bord selon le rôle

### Gestion des produits
- Ajouter, modifier, supprimer, lister
- Champs : nom, quantité, prix, fournisseur

### Gestion des fournisseurs
- Ajouter, modifier, supprimer, lister
- Champs : nom, contact

### Gestion des commandes
- Enregistrer, modifier, lister (en cours et passées)
- Lien vers un produit (par ID), quantité, date

### Gestion des utilisateurs (admin uniquement)
- Ajouter, modifier, supprimer, lister
- Mot de passe haché en BCrypt avant stockage

## Structure du projet

```
src/main/java/com/magasin/
├── Application.java          # Point d'entree, init FlatLaf
├── modele/                   # POJOs : Produit, Fournisseur, Commande, Utilisateur
├── service/                  # Client Appwrite + services CRUD
├── securite/                 # BCrypt + session utilisateur
├── vue/                      # Fenetres et panneaux Swing
│   └── composants/           # Composants stylises reutilisables
├── controleur/               # Orchestration vue ↔ service
├── outils/                   # Utilitaires CLI (generateur de hash)
└── util/                     # Configuration, gestion erreurs
```
