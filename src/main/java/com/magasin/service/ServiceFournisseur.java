package com.magasin.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.magasin.modele.Fournisseur;
import com.magasin.util.Configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.magasin.service.ServiceProduit.extraireId;
import static com.magasin.service.ServiceProduit.optString;

/** CRUD sur la table fournisseur. */
public final class ServiceFournisseur {

    private ServiceFournisseur() {}

    private static String table() { return Configuration.tableFournisseur(); }

    public static List<Fournisseur> listerTout() throws AppwriteException {
        JsonArray lignes = ClientAppwrite.listerLignes(table(), Collections.emptyList());
        List<Fournisseur> r = new ArrayList<>();
        for (JsonElement el : lignes) r.add(depuisJson(el.getAsJsonObject()));
        return r;
    }

    public static Fournisseur ajouter(Fournisseur f) throws AppwriteException {
        JsonObject rep = ClientAppwrite.creerLigne(table(), versJson(f));
        return depuisJson(rep);
    }

    public static Fournisseur modifier(Fournisseur f) throws AppwriteException {
        if (f.getId() == null) throw new IllegalArgumentException("Id fournisseur manquant");
        JsonObject rep = ClientAppwrite.mettreAJourLigne(table(), f.getId(), versJson(f));
        return depuisJson(rep);
    }

    public static void supprimer(String id) throws AppwriteException {
        ClientAppwrite.supprimerLigne(table(), id);
    }

    private static JsonObject versJson(Fournisseur f) {
        JsonObject o = new JsonObject();
        o.addProperty("nom", f.getNom());
        o.addProperty("contact", f.getContact() == null ? "" : f.getContact());
        return o;
    }

    private static Fournisseur depuisJson(JsonObject o) {
        return new Fournisseur(extraireId(o), optString(o, "nom"), optString(o, "contact"));
    }
}
