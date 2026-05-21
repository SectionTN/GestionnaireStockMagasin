package com.magasin.vue;

import com.magasin.modele.Produit;
import com.magasin.util.GestionnaireErreurs;
import com.magasin.vue.composants.BoutonPrimaire;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.atomic.AtomicReference;

/** Formulaire modal pour ajouter/modifier un produit. */
public final class DialogueProduit {

    private DialogueProduit() {}

    public static Produit afficher(Component parent, Produit existant) {
        Window proprietaire = SwingUtilities.getWindowAncestor(parent);
        JDialog dialogue = new JDialog(proprietaire,
                existant == null ? "Ajouter un produit" : "Modifier le produit",
                JDialog.ModalityType.APPLICATION_MODAL);
        dialogue.setSize(440, 420);
        dialogue.setLocationRelativeTo(parent);

        JTextField champNom = new JTextField(existant == null ? "" : existant.getNom());
        JTextField champQte = new JTextField(existant == null ? "" : String.valueOf(existant.getQuantite()));
        JTextField champPrix = new JTextField(existant == null ? "" : String.valueOf(existant.getPrix()));
        JTextField champFour = new JTextField(existant == null ? "" : existant.getFournisseur());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.gridy = 0; g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1; g.insets = new java.awt.Insets(6, 0, 6, 0);

        ajouterLigne(form, g, "Nom", champNom);
        ajouterLigne(form, g, "Quantite", champQte);
        ajouterLigne(form, g, "Prix", champPrix);
        ajouterLigne(form, g, "Fournisseur", champFour);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        actions.setBackground(Color.WHITE);
        JButton annuler = new JButton("Annuler");
        BoutonPrimaire valider = new BoutonPrimaire(existant == null ? "Ajouter" : "Enregistrer");
        actions.add(annuler);
        actions.add(valider);

        dialogue.setLayout(new BorderLayout());
        dialogue.add(form, BorderLayout.CENTER);
        dialogue.add(actions, BorderLayout.SOUTH);

        final AtomicReference<Produit> resultat = new AtomicReference<>();

        annuler.addActionListener(e -> dialogue.dispose());
        valider.addActionListener(e -> {
            try {
                String nom = champNom.getText().trim();
                if (nom.isEmpty()) throw new IllegalArgumentException("Le nom est obligatoire.");
                int qte = Integer.parseInt(champQte.getText().trim());
                if (qte < 0) throw new IllegalArgumentException("La quantite doit etre positive.");
                double prix = Double.parseDouble(champPrix.getText().trim().replace(",", "."));
                if (prix < 0) throw new IllegalArgumentException("Le prix doit etre positif.");
                String four = champFour.getText().trim();
                Produit p = new Produit(existant == null ? null : existant.getId(),
                        nom, qte, prix, four);
                resultat.set(p);
                dialogue.dispose();
            } catch (NumberFormatException nfe) {
                GestionnaireErreurs.erreur(dialogue, "Quantite ou prix invalide.");
            } catch (IllegalArgumentException iae) {
                GestionnaireErreurs.erreur(dialogue, iae.getMessage());
            }
        });

        dialogue.addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { resultat.set(null); }
        });

        dialogue.setVisible(true);
        return resultat.get();
    }

    private static void ajouterLigne(JPanel form, GridBagConstraints g, String label, JComponent champ) {
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l.setForeground(new Color(0x374151));
        g.gridy++;
        form.add(l, g);
        g.gridy++;
        champ.setPreferredSize(new Dimension(0, 36));
        form.add(champ, g);
    }
}
