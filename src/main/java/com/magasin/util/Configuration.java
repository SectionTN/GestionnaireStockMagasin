package com.magasin.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/** Chargeur de configuration depuis config.properties. */
public final class Configuration {

    private static final Properties PROPRIETES = new Properties();
    private static boolean chargee = false;

    private Configuration() {}

    public static synchronized void charger() {
        if (chargee) return;
        try (InputStream flux = Configuration.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (flux == null) {
                throw new IllegalStateException(
                    "Fichier config.properties introuvable dans le classpath");
            }
            PROPRIETES.load(flux);
            chargee = true;
        } catch (IOException e) {
            throw new IllegalStateException("Impossible de lire config.properties", e);
        }
    }

    public static String obtenir(String cle) {
        if (!chargee) charger();
        String valeur = PROPRIETES.getProperty(cle);
        if (valeur == null || valeur.isBlank()) {
            throw new IllegalStateException("Propriete manquante : " + cle);
        }
        return valeur.trim();
    }

    public static String pointTerminaison() { return obtenir("APPWRITE_ENDPOINT"); }
    public static String idProjet()         { return obtenir("APPWRITE_PROJECT_ID"); }
    public static String cleApi()           { return obtenir("APPWRITE_API_KEY"); }
    public static String idBase()           { return obtenir("APPWRITE_DATABASE_ID"); }
    public static String tableProduit()     { return obtenir("TABLE_PRODUIT"); }
    public static String tableFournisseur() { return obtenir("TABLE_FOURNISSEUR"); }
    public static String tableCommande()    { return obtenir("TABLE_COMMANDE"); }
    public static String tableUtilisateur() { return obtenir("TABLE_UTILISATEUR"); }
}
