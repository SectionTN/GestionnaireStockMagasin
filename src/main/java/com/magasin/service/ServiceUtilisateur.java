package com.magasin.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.magasin.modele.Utilisateur;
import com.magasin.securite.GestionnaireMotDePasse;
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
        // Filtre cote client (liste petite pour application d'examen).
        // Evite les soucis de syntaxe de requete TablesDB.
        for (Utilisateur u : listerTout()) {
            if (u.getUsername() != null && u.getUsername().equalsIgnoreCase(username)) {
                return u;
            }
        }
        return null;
    }

    /** Verifie identifiants, retourne l'utilisateur si OK, null sinon. */
    public static Utilisateur authentifier(String username, String motDePasseClair) throws AppwriteException {
        Utilisateur u = trouverParUsername(username);
        if (u == null) return null;
        if (!GestionnaireMotDePasse.verifier(motDePasseClair, u.getPassword())) return null;
        return u;
    }

    /** Ajoute un utilisateur. motDePasseClair sera hache. */
    public static Utilisateur ajouter(String username, String motDePasseClair, String role) throws AppwriteException {
        JsonObject d = new JsonObject();
        d.addProperty("username", username);
        d.addProperty("password", GestionnaireMotDePasse.hacher(motDePasseClair));
        d.addProperty("role", role);
        JsonObject rep = ClientAppwrite.creerLigne(table(), d);
        return depuisJson(rep);
    }

    /**
     * Modifie un utilisateur. Si nouveauMotDePasseClair != null/blank, le mot de passe
     * est rehache et remplace. Sinon, hashStocke est conserve (passer u.getPassword()).
     */
    public static Utilisateur modifier(Utilisateur u, String nouveauMotDePasseClair) throws AppwriteException {
        if (u.getId() == null) throw new IllegalArgumentException("Id utilisateur manquant");
        JsonObject d = new JsonObject();
        d.addProperty("username", u.getUsername());
        if (nouveauMotDePasseClair != null && !nouveauMotDePasseClair.isBlank()) {
            d.addProperty("password", GestionnaireMotDePasse.hacher(nouveauMotDePasseClair));
        } else {
            d.addProperty("password", u.getPassword());
        }
        d.addProperty("role", u.getRole());
        JsonObject rep = ClientAppwrite.mettreAJourLigne(table(), u.getId(), d);
        return depuisJson(rep);
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
