package com.magasin.vue;

import com.magasin.service.Appwrite;
import com.magasin.vue.composants.UI;

import com.magasin.Application;
import com.magasin.modele.Produit;
import com.magasin.util.GestionnaireErreurs;

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
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.List;

/** Panneau de gestion des produits — CRUD complet. */
public class PanneauProduits extends JPanel {

    private final DefaultTableModel modele = new DefaultTableModel(
            new Object[]{"ID", "Nom", "Quantite", "Prix", "Fournisseur"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tableau = new UI.Tableau(modele);

    public PanneauProduits() {
        setLayout(new BorderLayout());
        setBackground(Application.COULEUR_FOND);
        construire();
        rafraichir();
    }

    private void construire() {
        add(new UI.EnTete("Gestion des produits",
                "Ajouter, modifier ou supprimer des articles du stock"),
            BorderLayout.NORTH);

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

        UI.BoutonPrimaire bAjouter = new UI.BoutonPrimaire("Ajouter");
        UI.BoutonPrimaire bModifier = new UI.BoutonPrimaire("Modifier",
                new Color(0xF3F4F6), new Color(0x111827));
        UI.BoutonPrimaire bSupprimer = new UI.BoutonPrimaire("Supprimer",
                Application.COULEUR_DANGER, Color.WHITE);
        UI.BoutonPrimaire bRafraichir = new UI.BoutonPrimaire("Rafraichir",
                new Color(0xF3F4F6), new Color(0x111827));

        bAjouter.addActionListener(e -> ajouter());
        bModifier.addActionListener(e -> modifier());
        bSupprimer.addActionListener(e -> supprimer());
        bRafraichir.addActionListener(e -> rafraichir());

        barre.add(bAjouter);
        barre.add(bModifier);
        barre.add(bSupprimer);
        barre.add(bRafraichir);

        JLabel compteur = new JLabel();
        compteur.setForeground(new Color(0x6B7280));
        barre.add(compteur);

        modele.addTableModelListener(e -> compteur.setText("  " + modele.getRowCount() + " element(s)"));
        return barre;
    }

    private void ajouter() {
        Produit p = DialogueProduit.afficher((Component) this, null);
        if (p == null) return;
        executerService(() -> Appwrite.ajouterProduit(p), "Produit ajoute.");
    }

    private void modifier() {
        Produit p = produitSelectionne();
        if (p == null) {
            GestionnaireErreurs.erreur(this, "Veuillez selectionner un produit.");
            return;
        }
        Produit modifie = DialogueProduit.afficher((Component) this, p);
        if (modifie == null) return;
        modifie.setId(p.getId());
        executerService(() -> Appwrite.modifierProduit(modifie), "Produit modifie.");
    }

    private void supprimer() {
        Produit p = produitSelectionne();
        if (p == null) {
            GestionnaireErreurs.erreur(this, "Veuillez selectionner un produit.");
            return;
        }
        if (!GestionnaireErreurs.confirmer(this,
                "Supprimer definitivement le produit \"" + p.getNom() + "\" ?")) return;
        executerService(() -> { Appwrite.supprimerProduit(p.getId()); return null; }, "Produit supprime.");
    }

    private Produit produitSelectionne() {
        int l = tableau.getSelectedRow();
        if (l < 0) return null;
        int vrai = tableau.convertRowIndexToModel(l);
        return new Produit(
                (String) modele.getValueAt(vrai, 0),
                (String) modele.getValueAt(vrai, 1),
                ((Number) modele.getValueAt(vrai, 2)).intValue(),
                ((Number) modele.getValueAt(vrai, 3)).doubleValue(),
                (String) modele.getValueAt(vrai, 4));
    }

    public void rafraichir() {
        new SwingWorker<List<Produit>, Void>() {
            @Override protected List<Produit> doInBackground() throws Exception {
                return Appwrite.listerProduits();
            }
            @Override protected void done() {
                try {
                    modele.setRowCount(0);
                    for (Produit p : get()) {
                        modele.addRow(new Object[]{p.getId(), p.getNom(),
                                p.getQuantite(), p.getPrix(), p.getFournisseur()});
                    }
                } catch (Exception ex) {
                    GestionnaireErreurs.erreur(PanneauProduits.this,
                            "Impossible de charger les produits", ex.getCause());
                }
            }
        }.execute();
    }

    private interface Action { Object executer() throws Exception; }

    private void executerService(Action action, String messageSucces) {
        new SwingWorker<Object, Void>() {
            @Override protected Object doInBackground() throws Exception { return action.executer(); }
            @Override protected void done() {
                try {
                    get();
                    SwingUtilities.invokeLater(() -> {
                        rafraichir();
                        GestionnaireErreurs.information(PanneauProduits.this, messageSucces);
                    });
                } catch (Exception ex) {
                    GestionnaireErreurs.erreur(PanneauProduits.this,
                            "Operation echouee", ex.getCause());
                }
            }
        }.execute();
    }
}
