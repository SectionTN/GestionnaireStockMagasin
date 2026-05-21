package com.magasin.securite;

import com.magasin.modele.Utilisateur;

public final class SessionUtilisateur {

    private static Utilisateur utilisateurCourant;

    private SessionUtilisateur() {}

    public static void connecter(Utilisateur u) {
        utilisateurCourant = u;
    }

    public static void deconnecter() {
        utilisateurCourant = null;
    }

    public static Utilisateur utilisateurCourant() {
        return utilisateurCourant;
    }

    public static boolean estConnecte() {
        return utilisateurCourant != null;
    }

    public static boolean estAdministrateur() {
        return utilisateurCourant != null && utilisateurCourant.estAdministrateur();
    }
}
