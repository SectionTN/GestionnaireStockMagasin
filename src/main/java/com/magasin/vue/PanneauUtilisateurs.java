package com.magasin.vue;

import com.magasin.Application;
import com.magasin.modele.Utilisateur;
import com.magasin.securite.SessionUtilisateur;
import com.magasin.service.ServiceUtilisateur;
import com.magasin.util.GestionnaireErreurs;
import com.magasin.vue.composants.BoutonPrimaire;
import com.magasin.vue.composants.EnTetePanneau;
import com.magasin.vue.composants.TableurStylise;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.util.List;

/** Gestion des utilisateurs — reserve aux administrateurs. */
public class PanneauUtilisateurs extends JPanel {

    private final DefaultTableModel modele = new DefaultTableModel(
            new Object[]{"ID", "Nom d'utilisateur", "Role"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tableau = new TableurStylise(modele);

    public PanneauUtilisateurs() {
        setLayout(new BorderLayout());
        setBackground(Application.COULEUR_FOND);
        construire();
        rafraichir();
    }

    private void construire() {
        add(new EnTetePanneau("Gestion des utilisateurs",
                "Comptes et roles (administrateur ou gestionnaire)"), BorderLayout.NORTH);

        JPanel centre = new JPanel(new BorderLayout());
        centre.setBackground(Color.WHITE);
        centre.setBorder(BorderFactory.createLineBorder(new Color(0xE5E7EB)));
        centre.add(construireBarreActions(), BorderLayout.NORTH);
        JScrollPane sp = new JScrollPane(tableau);
        sp.setBorder(BorderFactory.createEmptyBorder());
        centre.add(sp, BorderLayout.CENTER);
        add(centre, BorderLayout.CENTER);
    }

    private JPanel construireBarreActions() {
        JPanel barre = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 12));
        barre.setBackground(Color.WHITE);
        barre.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        BoutonPrimaire bAj = new BoutonPrimaire("Ajouter");
        BoutonPrimaire bMo = new BoutonPrimaire("Modifier", new Color(0xF3F4F6), new Color(0x111827));
        BoutonPrimaire bSu = new BoutonPrimaire("Supprimer", Application.COULEUR_DANGER, Color.WHITE);
        BoutonPrimaire bRa = new BoutonPrimaire("Rafraichir", new Color(0xF3F4F6), new Color(0x111827));

        bAj.addActionListener(e -> ajouter());
        bMo.addActionListener(e -> modifier());
        bSu.addActionListener(e -> supprimer());
        bRa.addActionListener(e -> rafraichir());

        barre.add(bAj); barre.add(bMo); barre.add(bSu); barre.add(bRa);
        JLabel c = new JLabel(); c.setForeground(new Color(0x6B7280)); barre.add(c);
        modele.addTableModelListener(e -> c.setText("  " + modele.getRowCount() + " utilisateur(s)"));
        return barre;
    }

    private void ajouter() {
        DialogueUtilisateur.Resultat r = DialogueUtilisateur.afficher(this, null);
        if (r == null) return;
        executer(() -> ServiceUtilisateur.ajouter(
                r.utilisateur.getUsername(), r.motDePasseClair, r.utilisateur.getRole()),
                "Utilisateur ajoute.");
    }

    private void modifier() {
        Utilisateur sel = selectionne();
        if (sel == null) { GestionnaireErreurs.erreur(this, "Veuillez selectionner un utilisateur."); return; }
        // Recuperer le hash courant pour conserver le mot de passe si non modifie
        try {
            Utilisateur complet = ServiceUtilisateur.trouverParUsername(sel.getUsername());
            if (complet != null) sel = complet;
        } catch (Exception ex) { /* ignore — on continuera avec sel partiel */ }

        DialogueUtilisateur.Resultat r = DialogueUtilisateur.afficher(this, sel);
        if (r == null) return;
        Utilisateur u = r.utilisateur;
        executer(() -> ServiceUtilisateur.modifier(u, r.motDePasseClair), "Utilisateur modifie.");
    }

    private void supprimer() {
        Utilisateur sel = selectionne();
        if (sel == null) { GestionnaireErreurs.erreur(this, "Veuillez selectionner un utilisateur."); return; }
        Utilisateur courant = SessionUtilisateur.utilisateurCourant();
        if (courant != null && courant.getId().equals(sel.getId())) {
            GestionnaireErreurs.erreur(this, "Vous ne pouvez pas supprimer votre propre compte.");
            return;
        }
        if (!GestionnaireErreurs.confirmer(this,
                "Supprimer definitivement \"" + sel.getUsername() + "\" ?")) return;
        executer(() -> { ServiceUtilisateur.supprimer(sel.getId()); return null; }, "Utilisateur supprime.");
    }

    private Utilisateur selectionne() {
        int l = tableau.getSelectedRow();
        if (l < 0) return null;
        int v = tableau.convertRowIndexToModel(l);
        return new Utilisateur(
                (String) modele.getValueAt(v, 0),
                (String) modele.getValueAt(v, 1),
                null,
                (String) modele.getValueAt(v, 2));
    }

    public void rafraichir() {
        new SwingWorker<List<Utilisateur>, Void>() {
            @Override protected List<Utilisateur> doInBackground() throws Exception {
                return ServiceUtilisateur.listerTout();
            }
            @Override protected void done() {
                try {
                    modele.setRowCount(0);
                    for (Utilisateur u : get()) {
                        modele.addRow(new Object[]{u.getId(), u.getUsername(), u.getRole()});
                    }
                } catch (Exception ex) {
                    GestionnaireErreurs.erreur(PanneauUtilisateurs.this,
                            "Impossible de charger les utilisateurs", ex.getCause());
                }
            }
        }.execute();
    }

    private interface Act { Object executer() throws Exception; }

    private void executer(Act a, String ok) {
        new SwingWorker<Object, Void>() {
            @Override protected Object doInBackground() throws Exception { return a.executer(); }
            @Override protected void done() {
                try { get();
                    SwingUtilities.invokeLater(() -> {
                        rafraichir();
                        GestionnaireErreurs.information(PanneauUtilisateurs.this, ok);
                    });
                } catch (Exception ex) {
                    GestionnaireErreurs.erreur(PanneauUtilisateurs.this,
                            "Operation echouee", ex.getCause());
                }
            }
        }.execute();
    }
}
