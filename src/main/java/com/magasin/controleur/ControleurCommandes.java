package com.magasin.controleur;

import com.magasin.modele.Commande;
import com.magasin.modele.Produit;
import com.magasin.service.AppwriteException;
import com.magasin.service.ServiceCommande;
import com.magasin.service.ServiceProduit;

import java.util.List;

public final class ControleurCommandes {

    private ControleurCommandes() {}

    public static List<Commande> listerTout() throws AppwriteException {
        return ServiceCommande.listerTout();
    }

    public static List<Produit> listerProduits() throws AppwriteException {
        return ServiceProduit.listerTout();
    }

    public static Commande ajouter(Commande c) throws AppwriteException {
        return ServiceCommande.ajouter(c);
    }

    public static Commande modifier(Commande c) throws AppwriteException {
        return ServiceCommande.modifier(c);
    }

    public static void supprimer(String id) throws AppwriteException {
        ServiceCommande.supprimer(id);
    }
}
