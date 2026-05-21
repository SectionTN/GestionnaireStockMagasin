package com.magasin.controleur;

import com.magasin.modele.Produit;
import com.magasin.service.AppwriteException;
import com.magasin.service.ServiceProduit;

import java.util.List;

/** Couche d'orchestration entre la vue Produits et le service. */
public final class ControleurProduits {

    private ControleurProduits() {}

    public static List<Produit> listerTout() throws AppwriteException {
        return ServiceProduit.listerTout();
    }

    public static Produit ajouter(Produit p) throws AppwriteException {
        return ServiceProduit.ajouter(p);
    }

    public static Produit modifier(Produit p) throws AppwriteException {
        return ServiceProduit.modifier(p);
    }

    public static void supprimer(String id) throws AppwriteException {
        ServiceProduit.supprimer(id);
    }
}
