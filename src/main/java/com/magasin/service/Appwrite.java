package com.magasin.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.magasin.modele.Commande;
import com.magasin.modele.Fournisseur;
import com.magasin.modele.Produit;
import com.magasin.modele.Utilisateur;
import com.magasin.util.Configuration;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Couche unique d'acces a Appwrite Cloud (API TablesDB) :
 *   - methodes HTTP de bas niveau (creer, lister, modifier, supprimer une ligne)
 *   - methodes CRUD par entite (produits, fournisseurs, commandes, utilisateurs)
 *   - logique metier des commandes (verification + ajustement du stock)
 */
public final class Appwrite {

    // ----------------------------------------------------------------
    // Exception
    // ----------------------------------------------------------------
    public static class Exception extends java.lang.Exception {
        private final int codeHttp;
        public Exception(String message, int codeHttp) { super(message); this.codeHttp = codeHttp; }
        public int getCodeHttp() { return codeHttp; }
    }

    // ----------------------------------------------------------------
    // Client HTTP
    // ----------------------------------------------------------------
    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15)).build();

    private Appwrite() {}

    private static HttpRequest.Builder requeteBase(String chemin) {
        return HttpRequest.newBuilder()
                .uri(URI.create(Configuration.pointTerminaison() + chemin))
                .timeout(Duration.ofSeconds(30))
                .header("X-Appwrite-Project", Configuration.idProjet())
                .header("X-Appwrite-Key", Configuration.cleApi())
                .header("Content-Type", "application/json")
                .header("Accept", "application/json");
    }

    private static String basePourTable(String tableId) {
        return "/tablesdb/" + Configuration.idBase() + "/tables/" + tableId + "/rows";
    }

    private static JsonObject envoyer(HttpRequest req) throws Exception {
        try {
            HttpResponse<String> rep = HTTP.send(req, BodyHandlers.ofString(StandardCharsets.UTF_8));
            int code = rep.statusCode();
            String corps = rep.body();
            if (code >= 200 && code < 300) {
                if (corps == null || corps.isEmpty()) return new JsonObject();
                JsonElement el = JsonParser.parseString(corps);
                return el.isJsonObject() ? el.getAsJsonObject() : new JsonObject();
            }
            String msg = "Erreur HTTP " + code;
            try {
                JsonObject err = JsonParser.parseString(corps).getAsJsonObject();
                if (err.has("message")) msg += " — " + err.get("message").getAsString();
            } catch (java.lang.Exception ignore) { msg += " — " + corps; }
            throw new Exception(msg, code);
        } catch (Exception e) { throw e; }
          catch (java.lang.Exception e) { throw new Exception("Echec reseau : " + e.getMessage(), -1); }
    }

    private static JsonArray listerLignes(String tableId) throws Exception {
        HttpRequest req = requeteBase(basePourTable(tableId)).GET().build();
        JsonObject rep = envoyer(req);
        if (rep.has("rows") && rep.get("rows").isJsonArray())      return rep.getAsJsonArray("rows");
        if (rep.has("documents") && rep.get("documents").isJsonArray()) return rep.getAsJsonArray("documents");
        return new JsonArray();
    }

    private static JsonObject creerLigne(String tableId, JsonObject donnees) throws Exception {
        JsonObject corps = new JsonObject();
        corps.addProperty("rowId", "unique()");
        corps.add("data", donnees);
        return envoyer(requeteBase(basePourTable(tableId))
                .POST(BodyPublishers.ofString(corps.toString(), StandardCharsets.UTF_8)).build());
    }

    private static JsonObject mettreAJourLigne(String tableId, String rowId, JsonObject donnees) throws Exception {
        JsonObject corps = new JsonObject();
        corps.add("data", donnees);
        return envoyer(requeteBase(basePourTable(tableId) + "/" + rowId)
                .method("PATCH", BodyPublishers.ofString(corps.toString(), StandardCharsets.UTF_8)).build());
    }

    private static void supprimerLigne(String tableId, String rowId) throws Exception {
        envoyer(requeteBase(basePourTable(tableId) + "/" + rowId).DELETE().build());
    }

    private static JsonObject obtenirLigne(String tableId, String rowId) throws Exception {
        return envoyer(requeteBase(basePourTable(tableId) + "/" + rowId).GET().build());
    }

    // ----------------------------------------------------------------
    // Helpers JSON
    // ----------------------------------------------------------------
    private static String extraireId(JsonObject o) {
        if (o.has("$id")) return o.get("$id").getAsString();
        if (o.has("id"))  return o.get("id").getAsString();
        return null;
    }
    private static String optStr(JsonObject o, String k) {
        return o.has(k) && !o.get(k).isJsonNull() ? o.get(k).getAsString() : "";
    }
    private static int    optInt(JsonObject o, String k) {
        return o.has(k) && !o.get(k).isJsonNull() ? o.get(k).getAsInt() : 0;
    }
    private static double optDbl(JsonObject o, String k) {
        return o.has(k) && !o.get(k).isJsonNull() ? o.get(k).getAsDouble() : 0.0;
    }

    // ----------------------------------------------------------------
    // PRODUIT
    // ----------------------------------------------------------------
    public static List<Produit> listerProduits() throws Exception {
        List<Produit> r = new ArrayList<>();
        for (JsonElement el : listerLignes(Configuration.tableProduit()))
            r.add(produitDepuisJson(el.getAsJsonObject()));
        return r;
    }
    public static Produit obtenirProduit(String id) throws Exception {
        if (id == null || id.isBlank()) return null;
        return produitDepuisJson(obtenirLigne(Configuration.tableProduit(), id));
    }
    public static Produit ajouterProduit(Produit p) throws Exception {
        return produitDepuisJson(creerLigne(Configuration.tableProduit(), produitVersJson(p)));
    }
    public static Produit modifierProduit(Produit p) throws Exception {
        return produitDepuisJson(mettreAJourLigne(Configuration.tableProduit(), p.getId(), produitVersJson(p)));
    }
    public static void supprimerProduit(String id) throws Exception {
        supprimerLigne(Configuration.tableProduit(), id);
    }
    private static JsonObject produitVersJson(Produit p) {
        JsonObject o = new JsonObject();
        o.addProperty("nom", p.getNom());
        o.addProperty("quantite", p.getQuantite());
        o.addProperty("prix", p.getPrix());
        o.addProperty("fournisseur", p.getFournisseur() == null ? "" : p.getFournisseur());
        return o;
    }
    private static Produit produitDepuisJson(JsonObject o) {
        return new Produit(extraireId(o), optStr(o, "nom"), optInt(o, "quantite"),
                optDbl(o, "prix"), optStr(o, "fournisseur"));
    }

    // ----------------------------------------------------------------
    // FOURNISSEUR
    // ----------------------------------------------------------------
    public static List<Fournisseur> listerFournisseurs() throws Exception {
        List<Fournisseur> r = new ArrayList<>();
        for (JsonElement el : listerLignes(Configuration.tableFournisseur()))
            r.add(fournisseurDepuisJson(el.getAsJsonObject()));
        return r;
    }
    public static Fournisseur ajouterFournisseur(Fournisseur f) throws Exception {
        return fournisseurDepuisJson(creerLigne(Configuration.tableFournisseur(), fournisseurVersJson(f)));
    }
    public static Fournisseur modifierFournisseur(Fournisseur f) throws Exception {
        return fournisseurDepuisJson(mettreAJourLigne(Configuration.tableFournisseur(), f.getId(), fournisseurVersJson(f)));
    }
    public static void supprimerFournisseur(String id) throws Exception {
        supprimerLigne(Configuration.tableFournisseur(), id);
    }
    private static JsonObject fournisseurVersJson(Fournisseur f) {
        JsonObject o = new JsonObject();
        o.addProperty("nom", f.getNom());
        o.addProperty("contact", f.getContact() == null ? "" : f.getContact());
        return o;
    }
    private static Fournisseur fournisseurDepuisJson(JsonObject o) {
        return new Fournisseur(extraireId(o), optStr(o, "nom"), optStr(o, "contact"));
    }

    // ----------------------------------------------------------------
    // COMMANDE (avec gestion du stock)
    // ----------------------------------------------------------------
    public static List<Commande> listerCommandes() throws Exception {
        List<Commande> r = new ArrayList<>();
        for (JsonElement el : listerLignes(Configuration.tableCommande()))
            r.add(commandeDepuisJson(el.getAsJsonObject()));
        return r;
    }
    public static Commande obtenirCommande(String id) throws Exception {
        if (id == null || id.isBlank()) return null;
        return commandeDepuisJson(obtenirLigne(Configuration.tableCommande(), id));
    }
    public static Commande ajouterCommande(Commande c) throws Exception {
        Produit p = obtenirProduit(c.getProduitId());
        if (p == null) throw new Exception("Produit introuvable.", -1);
        if (c.getQuantite() > p.getQuantite()) {
            throw new Exception("Stock insuffisant pour \"" + p.getNom() + "\". "
                    + "Disponible : " + p.getQuantite() + ", demande : " + c.getQuantite() + ".", -1);
        }
        p.setQuantite(p.getQuantite() - c.getQuantite());
        modifierProduit(p);
        return commandeDepuisJson(creerLigne(Configuration.tableCommande(), commandeVersJson(c)));
    }
    public static Commande modifierCommande(Commande nouvelle) throws Exception {
        Commande ancienne = obtenirCommande(nouvelle.getId());
        if (ancienne == null) throw new Exception("Commande introuvable.", -1);

        if (ancienne.getProduitId() != null && ancienne.getProduitId().equals(nouvelle.getProduitId())) {
            int delta = nouvelle.getQuantite() - ancienne.getQuantite();
            if (delta != 0) {
                Produit p = obtenirProduit(nouvelle.getProduitId());
                if (p == null) throw new Exception("Produit introuvable.", -1);
                if (delta > 0 && p.getQuantite() < delta) {
                    throw new Exception("Stock insuffisant pour \"" + p.getNom() + "\". "
                            + "Disponible : " + p.getQuantite() + ", supplement requis : " + delta + ".", -1);
                }
                p.setQuantite(p.getQuantite() - delta);
                modifierProduit(p);
            }
        } else {
            Produit ancien  = obtenirProduit(ancienne.getProduitId());
            Produit nouveau = obtenirProduit(nouvelle.getProduitId());
            if (nouveau == null) throw new Exception("Produit introuvable.", -1);
            if (nouveau.getQuantite() < nouvelle.getQuantite()) {
                throw new Exception("Stock insuffisant pour \"" + nouveau.getNom() + "\". "
                        + "Disponible : " + nouveau.getQuantite() + ", demande : " + nouvelle.getQuantite() + ".", -1);
            }
            if (ancien != null) {
                ancien.setQuantite(ancien.getQuantite() + ancienne.getQuantite());
                modifierProduit(ancien);
            }
            nouveau.setQuantite(nouveau.getQuantite() - nouvelle.getQuantite());
            modifierProduit(nouveau);
        }
        return commandeDepuisJson(mettreAJourLigne(Configuration.tableCommande(), nouvelle.getId(), commandeVersJson(nouvelle)));
    }
    public static void supprimerCommande(String id) throws Exception {
        Commande c = obtenirCommande(id);
        if (c == null) throw new Exception("Commande introuvable.", -1);
        Produit p = obtenirProduit(c.getProduitId());
        if (p != null) {
            p.setQuantite(p.getQuantite() + c.getQuantite());
            modifierProduit(p);
        }
        supprimerLigne(Configuration.tableCommande(), id);
    }
    private static JsonObject commandeVersJson(Commande c) {
        JsonObject o = new JsonObject();
        o.addProperty("produit_id", c.getProduitId());
        o.addProperty("quantite", c.getQuantite());
        o.addProperty("date_commande", c.getDateCommande());
        return o;
    }
    private static Commande commandeDepuisJson(JsonObject o) {
        return new Commande(extraireId(o), optStr(o, "produit_id"),
                optInt(o, "quantite"), optStr(o, "date_commande"));
    }

    // ----------------------------------------------------------------
    // UTILISATEUR
    // ----------------------------------------------------------------
    public static List<Utilisateur> listerUtilisateurs() throws Exception {
        List<Utilisateur> r = new ArrayList<>();
        for (JsonElement el : listerLignes(Configuration.tableUtilisateur()))
            r.add(utilisateurDepuisJson(el.getAsJsonObject()));
        return r;
    }
    public static Utilisateur obtenirUtilisateur(String id) throws Exception {
        if (id == null || id.isBlank()) return null;
        return utilisateurDepuisJson(obtenirLigne(Configuration.tableUtilisateur(), id));
    }
    public static Utilisateur authentifier(String username, String motDePasse) throws Exception {
        for (Utilisateur u : listerUtilisateurs()) {
            if (u.getUsername() != null
                    && u.getUsername().equalsIgnoreCase(username)
                    && motDePasse.equals(u.getPassword())) return u;
        }
        return null;
    }
    public static Utilisateur ajouterUtilisateur(String username, String motDePasse, String role) throws Exception {
        JsonObject d = new JsonObject();
        d.addProperty("username", username);
        d.addProperty("password", motDePasse);
        d.addProperty("role", role);
        return utilisateurDepuisJson(creerLigne(Configuration.tableUtilisateur(), d));
    }
    public static Utilisateur modifierUtilisateur(Utilisateur u, String nouveauMotDePasse) throws Exception {
        JsonObject d = new JsonObject();
        d.addProperty("username", u.getUsername());
        d.addProperty("password",
                (nouveauMotDePasse != null && !nouveauMotDePasse.isBlank()) ? nouveauMotDePasse : u.getPassword());
        d.addProperty("role", u.getRole());
        return utilisateurDepuisJson(mettreAJourLigne(Configuration.tableUtilisateur(), u.getId(), d));
    }
    public static void supprimerUtilisateur(String id) throws Exception {
        supprimerLigne(Configuration.tableUtilisateur(), id);
    }
    private static Utilisateur utilisateurDepuisJson(JsonObject o) {
        return new Utilisateur(extraireId(o), optStr(o, "username"), optStr(o, "password"), optStr(o, "role"));
    }
}
