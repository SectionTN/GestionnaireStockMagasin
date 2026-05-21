package com.magasin.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.magasin.modele.Commande;
import com.magasin.modele.Produit;
import com.magasin.util.Configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.magasin.service.ServiceProduit.extraireId;
import static com.magasin.service.ServiceProduit.optInt;
import static com.magasin.service.ServiceProduit.optString;

/**
 * CRUD sur la table commande.
 * Lors de l'ajout ou de la modification, le stock du produit est verifie
 * puis ajuste automatiquement. Si la quantite demandee depasse le stock,
 * une AppwriteException est levee avec un message explicite.
 */
public final class ServiceCommande {

    private ServiceCommande() {}

    private static String table() { return Configuration.tableCommande(); }

    public static List<Commande> listerTout() throws AppwriteException {
        JsonArray lignes = ClientAppwrite.listerLignes(table(), Collections.emptyList());
        List<Commande> r = new ArrayList<>();
        for (JsonElement el : lignes) r.add(depuisJson(el.getAsJsonObject()));
        return r;
    }

    public static Commande obtenirParId(String id) throws AppwriteException {
        if (id == null || id.isBlank()) return null;
        JsonObject rep = ClientAppwrite.obtenirLigne(table(), id);
        return depuisJson(rep);
    }

    /**
     * Enregistre une nouvelle commande. Le stock du produit lie est verifie
     * puis decremente de la quantite commandee.
     */
    public static Commande ajouter(Commande c) throws AppwriteException {
        Produit p = ServiceProduit.obtenirParId(c.getProduitId());
        if (p == null) {
            throw new AppwriteException("Produit introuvable.", -1);
        }
        if (c.getQuantite() > p.getQuantite()) {
            throw new AppwriteException(
                    "Stock insuffisant pour \"" + p.getNom() + "\". "
                    + "Disponible : " + p.getQuantite()
                    + ", demande : " + c.getQuantite() + ".", -1);
        }
        p.setQuantite(p.getQuantite() - c.getQuantite());
        ServiceProduit.modifier(p);

        JsonObject rep = ClientAppwrite.creerLigne(table(), versJson(c));
        return depuisJson(rep);
    }

    /**
     * Met a jour une commande existante. Selon les changements :
     *   - meme produit : on calcule la difference et on l'applique au stock
     *   - produit different : on rembourse l'ancien produit et on debite le nouveau
     * Si le stock devient insuffisant, on annule l'operation.
     */
    public static Commande modifier(Commande nouvelle) throws AppwriteException {
        if (nouvelle.getId() == null) {
            throw new IllegalArgumentException("Id commande manquant");
        }
        Commande ancienne = obtenirParId(nouvelle.getId());
        if (ancienne == null) {
            throw new AppwriteException("Commande introuvable.", -1);
        }

        if (ancienne.getProduitId() != null
                && ancienne.getProduitId().equals(nouvelle.getProduitId())) {
            int delta = nouvelle.getQuantite() - ancienne.getQuantite();
            if (delta != 0) {
                Produit p = ServiceProduit.obtenirParId(nouvelle.getProduitId());
                if (p == null) {
                    throw new AppwriteException("Produit introuvable.", -1);
                }
                if (delta > 0 && p.getQuantite() < delta) {
                    throw new AppwriteException(
                            "Stock insuffisant pour \"" + p.getNom() + "\". "
                            + "Disponible : " + p.getQuantite()
                            + ", supplement requis : " + delta + ".", -1);
                }
                p.setQuantite(p.getQuantite() - delta);
                ServiceProduit.modifier(p);
            }
        } else {
            Produit ancien = ServiceProduit.obtenirParId(ancienne.getProduitId());
            Produit nouveau = ServiceProduit.obtenirParId(nouvelle.getProduitId());
            if (nouveau == null) {
                throw new AppwriteException("Produit introuvable.", -1);
            }
            if (nouveau.getQuantite() < nouvelle.getQuantite()) {
                throw new AppwriteException(
                        "Stock insuffisant pour \"" + nouveau.getNom() + "\". "
                        + "Disponible : " + nouveau.getQuantite()
                        + ", demande : " + nouvelle.getQuantite() + ".", -1);
            }
            if (ancien != null) {
                ancien.setQuantite(ancien.getQuantite() + ancienne.getQuantite());
                ServiceProduit.modifier(ancien);
            }
            nouveau.setQuantite(nouveau.getQuantite() - nouvelle.getQuantite());
            ServiceProduit.modifier(nouveau);
        }

        JsonObject rep = ClientAppwrite.mettreAJourLigne(table(), nouvelle.getId(), versJson(nouvelle));
        return depuisJson(rep);
    }

    private static JsonObject versJson(Commande c) {
        JsonObject o = new JsonObject();
        o.addProperty("produit_id", c.getProduitId());
        o.addProperty("quantite", c.getQuantite());
        o.addProperty("date_commande", c.getDateCommande());
        return o;
    }

    private static Commande depuisJson(JsonObject o) {
        return new Commande(
                extraireId(o),
                optString(o, "produit_id"),
                optInt(o, "quantite"),
                optString(o, "date_commande"));
    }
}
