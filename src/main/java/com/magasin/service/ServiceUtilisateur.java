package com.magasin.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.magasin.modele.Utilisateur;
import com.magasin.util.Configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.magasin.service.ServiceProduit.extraireId;
import static com.magasin.service.ServiceProduit.optString;

/** CRUD + authentification sur la table utilisateur. */
public final class ServiceUtilisateur {

    private ServiceUtilisateur() {}

    private static String table() { return Configuration.tableUtilisateur(); }

    public static List<Utilisateur> listerTout() throws AppwriteException {
        JsonArray lignes = ClientAppwrite.listerLignes(table(), Collections.emptyList());
        List<Utilisateur> r = new ArrayList<>();
        for (JsonElement el : lignes) r.add(depuisJson(el.getAsJsonObject()));
        return r;
    }

    public static Utilisateur trouverParUsername(String username) throws AppwriteException {
        for (Utilisateur u : listerTout()) {
            if (u.getUsername() != null && u.getUsername().equalsIgnoreCase(username)) {
                return u;
            }
        }
        return null;
    }

    public static Utilisateur authentifier(String username, String motDePasse) throws AppwriteException {
        Utilisateur u = trouverParUsername(username);
        if (u == null || !motDePasse.equals(u.getPassword())) return null;
        return u;
    }

    public static Utilisateur ajouter(String username, String motDePasse, String role) throws AppwriteException {
        JsonObject d = new JsonObject();
        d.addProperty("username", username);
        d.addProperty("password", motDePasse);
        d.addProperty("role", role);
        return depuisJson(ClientAppwrite.creerLigne(table(), d));
    }

    public static Utilisateur modifier(Utilisateur u, String nouveauMotDePasse) throws AppwriteException {
        if (u.getId() == null) throw new IllegalArgumentException("Id utilisateur manquant");
        JsonObject d = new JsonObject();
        d.addProperty("username", u.getUsername());
        d.addProperty("password",
                (nouveauMotDePasse != null && !nouveauMotDePasse.isBlank()) ? nouveauMotDePasse : u.getPassword());
        d.addProperty("role", u.getRole());
        return depuisJson(ClientAppwrite.mettreAJourLigne(table(), u.getId(), d));
    }

    public static void supprimer(String id) throws AppwriteException {
        ClientAppwrite.supprimerLigne(table(), id);
    }

    private static Utilisateur depuisJson(JsonObject o) {
        return new Utilisateur(
                extraireId(o),
                optString(o, "username"),
                optString(o, "password"),
                optString(o, "role"));
    }
}
