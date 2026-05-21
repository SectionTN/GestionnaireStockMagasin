package com.magasin.outils;

import com.magasin.securite.GestionnaireMotDePasse;

/**
 * Utilitaire CLI : genere un hash BCrypt pour le mot de passe d'un admin.
 * Usage : mvn exec:java -Dexec.mainClass=com.magasin.outils.GenererHashAdmin -Dexec.args="motdepasse"
 */
public final class GenererHashAdmin {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage : GenererHashAdmin <motDePasseClair>");
            System.exit(1);
        }
        String hash = GestionnaireMotDePasse.hacher(args[0]);
        System.out.println("Hash BCrypt :");
        System.out.println(hash);
        System.out.println();
        System.out.println("Copier ce hash dans la colonne 'password' de la table 'utilisateur'.");
    }
}
