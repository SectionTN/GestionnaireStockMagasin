package com.magasin.vue;

import com.magasin.service.Appwrite;
import com.magasin.vue.composants.UI;

import com.magasin.Application;
import com.magasin.modele.Fournisseur;
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
import java.util.List;

public class PanneauFournisseurs extends JPanel {

    private final DefaultTableModel modele = new DefaultTableModel(
            new Object[] { "ID", "Nom", "Contact" }, 0) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };
    private final JTable tableau = new UI.Tableau(modele);

    public PanneauFournisseurs() {
        setLayout(new BorderLayout());
        setBackground(Application.COULEUR_FOND);
        construire();
        rafraichir();
    }

    private void construire() {
        add(new UI.EnTete("Gestion des fournisseurs",
                "Repertoire des partenaires et points de contact"), BorderLayout.NORTH);

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

        UI.BoutonPrimaire bAj = new UI.BoutonPrimaire("Ajouter");
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
        modele.addTableModelListener(e -> c.setText("  " + modele.getRowCount() + " element(s)"));
        return barre;
    }

    private void ajouter() {
        Fournisseur f = DialogueFournisseur.afficher(this, null);
        if (f != null)
            executer(() -> Appwrite.ajouterFournisseur(f), "Fournisseur ajoute.");
    }

    private void modifier() {
        Fournisseur cur = selectionne();
        if (cur == null) {
            GestionnaireErreurs.erreur(this, "Veuillez selectionner un fournisseur.");
            return;
        }
        Fournisseur f = DialogueFournisseur.afficher(this, cur);
        if (f == null)
            return;
        f.setId(cur.getId());
        executer(() -> Appwrite.modifierFournisseur(f), "Fournisseur modifie.");
    }

    private void supprimer() {
        Fournisseur cur = selectionne();
        if (cur == null) {
            GestionnaireErreurs.erreur(this, "Veuillez selectionner un fournisseur.");
            return;
        }
        if (!GestionnaireErreurs.confirmer(this, "Supprimer \"" + cur.getNom() + "\" ?"))
            return;
        executer(() -> {
            Appwrite.supprimerFournisseur(cur.getId());
            return null;
        }, "Fournisseur supprime.");
    }

    private Fournisseur selectionne() {
        int l = tableau.getSelectedRow();
        if (l < 0)
            return null;
        int v = tableau.convertRowIndexToModel(l);
        return new Fournisseur((String) modele.getValueAt(v, 0),
                (String) modele.getValueAt(v, 1), (String) modele.getValueAt(v, 2));
    }

    public void rafraichir() {
        new SwingWorker<List<Fournisseur>, Void>() {
            @Override
            protected List<Fournisseur> doInBackground() throws Exception {
                return Appwrite.listerFournisseurs();
            }

            @Override
            protected void done() {
                try {
                    modele.setRowCount(0);
                    for (Fournisseur f : get()) {
                        modele.addRow(new Object[] { f.getId(), f.getNom(), f.getContact() });
                    }
                } catch (Exception ex) {
                    GestionnaireErreurs.erreur(PanneauFournisseurs.this,
                            "Impossible de charger les fournisseurs", ex.getCause());
                }
            }
        }.execute();
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
                        GestionnaireErreurs.information(PanneauFournisseurs.this, ok);
                    });
                } catch (Exception ex) {
                    GestionnaireErreurs.erreur(PanneauFournisseurs.this,
                            "Operation echouee", ex.getCause());
                }
            }
        }.execute();
    }
}
