package com.magasin.securite;

import org.mindrot.jbcrypt.BCrypt;

/** Hachage et verification de mots de passe via BCrypt. */
public final class GestionnaireMotDePasse {

    private static final int COUT = 12;

    private GestionnaireMotDePasse() {}

    public static String hacher(String motDePasseClair) {
        if (motDePasseClair == null || motDePasseClair.isEmpty()) {
            throw new IllegalArgumentException("Mot de passe vide");
        }
        return BCrypt.hashpw(motDePasseClair, BCrypt.gensalt(COUT));
    }

    public static boolean verifier(String motDePasseClair, String hashStocke) {
        if (motDePasseClair == null || hashStocke == null || hashStocke.isEmpty()) {
            return false;
        }
        try {
            return BCrypt.checkpw(motDePasseClair, hashStocke);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
