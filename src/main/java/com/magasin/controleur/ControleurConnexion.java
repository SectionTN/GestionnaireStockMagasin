package com.magasin.controleur;

import com.magasin.modele.Utilisateur;
import com.magasin.securite.SessionUtilisateur;
import com.magasin.service.ServiceUtilisateur;
import com.magasin.vue.FenetreConnexion;
import com.magasin.vue.FenetrePrincipale;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import java.util.function.Consumer;

/** Coordonne la connexion : validation, appel service, ouverture fenetre principale. */
public final class ControleurConnexion {

    private ControleurConnexion() {}

    public static void connecter(FenetreConnexion fenetre,
                                  String username,
                                  String motDePasse,
                                  Consumer<String> auxErreurs) {
        if (username == null || username.isBlank()) {
            auxErreurs.accept("Veuillez saisir un nom d'utilisateur.");
            return;
        }
        if (motDePasse == null || motDePasse.isEmpty()) {
            auxErreurs.accept("Veuillez saisir un mot de passe.");
            return;
        }

        new SwingWorker<Utilisateur, Void>() {
            @Override
            protected Utilisateur doInBackground() throws Exception {
                return ServiceUtilisateur.authentifier(username, motDePasse);
            }

            @Override
            protected void done() {
                try {
                    Utilisateur u = get();
                    if (u == null) {
                        auxErreurs.accept("Identifiants incorrects.");
                        return;
                    }
                    SessionUtilisateur.connecter(u);
                    SwingUtilities.invokeLater(() -> {
                        fenetre.dispose();
                        new FenetrePrincipale().setVisible(true);
                    });
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    auxErreurs.accept("Echec : " + cause.getMessage());
                }
            }
        }.execute();
    }
}
