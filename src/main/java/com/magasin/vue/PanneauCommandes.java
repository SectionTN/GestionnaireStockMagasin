package com.magasin.vue;

import com.magasin.service.Appwrite;
import com.magasin.vue.composants.UI;

import com.magasin.Application;
import com.magasin.modele.Commande;
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
import java.awt.FlowLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PanneauCommandes extends JPanel {

    private final DefaultTableModel modele = new DefaultTableModel(
            new Object[] { "ID", "Produit", "Quantite", "Date" }, 0) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };
    private final JTable tableau = new UI.Tableau(modele);
    private List<Produit> produitsCache = List.of();
    private final Map<String, String> nomParId = new HashMap<>();

    public PanneauCommandes() {
        setLayout(new BorderLayout());
        setBackground(Application.COULEUR_FOND);
        construire();
        rafraichir();
    }

    private void construire() {
        add(new UI.EnTete("Gestion des commandes",
                "Enregistrer et suivre les commandes en cours et passees"), BorderLayout.NORTH);

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

        UI.BoutonPrimaire bAj = new UI.BoutonPrimaire("Enregistrer");
        UI.BoutonPrimaire bMo = new UI.BoutonPrimaire("Modifier", new Color(0xF3F4F6), new Color(0x111827));
        UI.BoutonPrimaire bSu = new UI.BoutonPrimaire("Supprimer", Application.COULEUR_DANGER, Color.WHITE);
        UI.BoutonPrimaire bRa = new UI.BoutonPrimaire("Rafraichir", new Color(0xF3F4F6), new Color(0x111827));

        bAj.addActionListener(e -> ajouter());
        bMo.addActionListener(e -> modifier());
        bSu.addActionListener(e -> supprimer());
        bRa.addActionListener(e -> rafraichir());

        barre.add(bAj);
        barre.add(bMo);
        barre.add(bSu);
        barre.add(bRa);
        JLabel c = new JLabel();
        c.setForeground(new Color(0x6B7280));
        barre.add(c);
        modele.addTableModelListener(e -> c.setText("  " + modele.getRowCount() + " commande(s)"));
        return barre;
    }

    private void ajouter() {
        if (produitsCache.isEmpty()) {
            GestionnaireErreurs.erreur(this, "Aucun produit disponible. Ajoutez d'abord un produit.");
            return;
        }
        Commande c = DialogueCommande.afficher(this, null, produitsCache);
        if (c != null)
            executer(() -> Appwrite.ajouterCommande(c), "Commande enregistree.");
    }

    private void modifier() {
        Commande cur = selectionnee();
        if (cur == null) {
            GestionnaireErreurs.erreur(this, "Veuillez selectionner une commande.");
            return;
        }
        Commande c = DialogueCommande.afficher(this, cur, produitsCache);
        if (c == null)
            return;
        c.setId(cur.getId());
        executer(() -> Appwrite.modifierCommande(c), "Commande modifiee.");
    }

    private void supprimer() {
        Commande cur = selectionnee();
        if (cur == null) {
            GestionnaireErreurs.erreur(this, "Veuillez selectionner une commande.");
            return;
        }
        String nomProduit = nomParId.getOrDefault(cur.getProduitId(), "(produit inconnu)");
        if (!GestionnaireErreurs.confirmer(this,
                "Supprimer cette commande de " + cur.getQuantite() + " x \"" + nomProduit + "\" ?\n"
                        + "Le stock du produit sera restitue."))
            return;
        executer(() -> {
            Appwrite.supprimerCommande(cur.getId());
            return null;
        }, "Commande supprimee.");
    }

    private Commande selectionnee() {
        int l = tableau.getSelectedRow();
        if (l < 0)
            return null;
        int v = tableau.convertRowIndexToModel(l);
        String id = (String) modele.getValueAt(v, 0);
        String nomProduit = (String) modele.getValueAt(v, 1);
        // recuperer l'id produit depuis le cache
        String prodId = null;
        for (Produit p : produitsCache)
            if (p.getNom().equals(nomProduit)) {
                prodId = p.getId();
                break;
            }
        return new Commande(id, prodId,
                ((Number) modele.getValueAt(v, 2)).intValue(),
                (String) modele.getValueAt(v, 3));
    }

    public void rafraichir() {
        new SwingWorker<Object[], Void>() {
            @Override
            protected Object[] doInBackground() throws Exception {
                List<Produit> prods = Appwrite.listerProduits();
                List<Commande> cmds = Appwrite.listerCommandes();
                return new Object[] { prods, cmds };
            }

            @Override
            @SuppressWarnings("unchecked")
            protected void done() {
                try {
                    Object[] res = get();
                    produitsCache = (List<Produit>) res[0];
                    nomParId.clear();
                    for (Produit p : produitsCache)
                        nomParId.put(p.getId(), p.getNom());

                    List<Commande> cmds = (List<Commande>) res[1];
                    modele.setRowCount(0);
                    for (Commande c : cmds) {
                        String nom = nomParId.getOrDefault(c.getProduitId(), "(produit inconnu)");
                        modele.addRow(new Object[] { c.getId(), nom, c.getQuantite(),
                                formaterDate(c.getDateCommande()) });
                    }
                } catch (Exception ex) {
                    GestionnaireErreurs.erreur(PanneauCommandes.this,
                            "Impossible de charger les commandes", ex.getCause());
                }
            }
        }.execute();
    }

    private static String formaterDate(String date) {
        if (date == null)
            return "";
        String s = date.trim();
        if (s.length() >= 10 && s.charAt(4) == '-' && s.charAt(7) == '-') {
            return s.substring(0, 10);
        }
        return s;
    }

    private interface Act {
        Object executer() throws Exception;
    }

    private void executer(Act a, String ok) {
        new SwingWorker<Object, Void>() {
            @Override
            protected Object doInBackground() throws Exception {
                return a.executer();
            }

            @Override
            protected void done() {
                try {
                    get();
                    SwingUtilities.invokeLater(() -> {
                        rafraichir();
                        GestionnaireErreurs.information(PanneauCommandes.this, ok);
                    });
                } catch (Exception ex) {
                    GestionnaireErreurs.erreur(PanneauCommandes.this,
                            "Operation echouee", ex.getCause());
                }
            }
        }.execute();
    }
}
