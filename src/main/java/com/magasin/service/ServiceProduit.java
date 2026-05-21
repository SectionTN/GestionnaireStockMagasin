package com.magasin.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.magasin.modele.Produit;
import com.magasin.util.Configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** CRUD sur la table produit. */
public final class ServiceProduit {

    private ServiceProduit() {}

    private static String table() { return Configuration.tableProduit(); }

    public static List<Produit> listerTout() throws AppwriteException {
        JsonArray lignes = ClientAppwrite.listerLignes(table(), Collections.emptyList());
        List<Produit> resultats = new ArrayList<>();
        for (JsonElement el : lignes) {
            resultats.add(depuisJson(el.getAsJsonObject()));
        }
        return resultats;
    }

    public static Produit ajouter(Produit p) throws AppwriteException {
        JsonObject d = versJson(p);
        JsonObject rep = ClientAppwrite.creerLigne(table(), d);
        return depuisJson(rep);
    }

    public static Produit modifier(Produit p) throws AppwriteException {
        if (p.getId() == null) throw new IllegalArgumentException("Id produit manquant");
        JsonObject d = versJson(p);
        JsonObject rep = ClientAppwrite.mettreAJourLigne(table(), p.getId(), d);
        return depuisJson(rep);
    }

    public static void supprimer(String id) throws AppwriteException {
        ClientAppwrite.supprimerLigne(table(), id);
    }

    public static Produit obtenirParId(String id) throws AppwriteException {
        if (id == null || id.isBlank()) return null;
        JsonObject rep = ClientAppwrite.obtenirLigne(table(), id);
        return depuisJson(rep);
    }

    private static JsonObject versJson(Produit p) {
        JsonObject o = new JsonObject();
        o.addProperty("nom", p.getNom());
        o.addProperty("quantite", p.getQuantite());
        o.addProperty("prix", p.getPrix());
        o.addProperty("fournisseur", p.getFournisseur() == null ? "" : p.getFournisseur());
        return o;
    }

    private static Produit depuisJson(JsonObject o) {
        Produit p = new Produit();
        p.setId(extraireId(o));
        p.setNom(optString(o, "nom"));
        p.setQuantite(optInt(o, "quantite"));
        p.setPrix(optDouble(o, "prix"));
        p.setFournisseur(optString(o, "fournisseur"));
        return p;
    }

    static String extraireId(JsonObject o) {
        if (o.has("$id")) return o.get("$id").getAsString();
        if (o.has("id")) return o.get("id").getAsString();
        return null;
    }

    static String optString(JsonObject o, String k) {
        return o.has(k) && !o.get(k).isJsonNull() ? o.get(k).getAsString() : "";
    }

    static int optInt(JsonObject o, String k) {
        return o.has(k) && !o.get(k).isJsonNull() ? o.get(k).getAsInt() : 0;
    }

    static double optDouble(JsonObject o, String k) {
        return o.has(k) && !o.get(k).isJsonNull() ? o.get(k).getAsDouble() : 0.0;
    }
}
