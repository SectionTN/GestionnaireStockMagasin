package com.magasin.controleur;

import com.magasin.modele.Fournisseur;
import com.magasin.service.AppwriteException;
import com.magasin.service.ServiceFournisseur;

import java.util.List;

public final class ControleurFournisseurs {

    private ControleurFournisseurs() {}

    public static List<Fournisseur> listerTout() throws AppwriteException {
        return ServiceFournisseur.listerTout();
    }
    public static Fournisseur ajouter(Fournisseur f) throws AppwriteException {
        return ServiceFournisseur.ajouter(f);
    }
    public static Fournisseur modifier(Fournisseur f) throws AppwriteException {
        return ServiceFournisseur.modifier(f);
    }
    public static void supprimer(String id) throws AppwriteException {
        ServiceFournisseur.supprimer(id);
    }
}
