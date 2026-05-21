package com.magasin.vue;

import com.magasin.service.Appwrite;
import com.magasin.vue.composants.UI;

import com.magasin.modele.Fournisseur;
import com.magasin.util.GestionnaireErreurs;

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
import java.util.concurrent.atomic.AtomicReference;

public final class DialogueFournisseur {

    private DialogueFournisseur() {}

    public static Fournisseur afficher(Component parent, Fournisseur existant) {
        Window w = SwingUtilities.getWindowAncestor(parent);
        JDialog d = new JDialog(w, existant == null ? "Ajouter un fournisseur" : "Modifier le fournisseur",
                JDialog.ModalityType.APPLICATION_MODAL);
        d.setSize(420, 280);
        d.setLocationRelativeTo(parent);

        JTextField champNom = new JTextField(existant == null ? "" : existant.getNom());
        JTextField champContact = new JTextField(existant == null ? "" : existant.getContact());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.gridy = 0; g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1; g.insets = new java.awt.Insets(6, 0, 6, 0);

        ligne(form, g, "Nom", champNom);
        ligne(form, g, "Contact", champContact);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        actions.setBackground(Color.WHITE);
        JButton annuler = new JButton("Annuler");
        UI.BoutonPrimaire valider = new UI.BoutonPrimaire(existant == null ? "Ajouter" : "Enregistrer");
        actions.add(annuler);
        actions.add(valider);

        d.setLayout(new BorderLayout());
        d.add(form, BorderLayout.CENTER);
        d.add(actions, BorderLayout.SOUTH);

        AtomicReference<Fournisseur> ref = new AtomicReference<>();
        annuler.addActionListener(e -> d.dispose());
        valider.addActionListener(e -> {
            String nom = champNom.getText().trim();
            if (nom.isEmpty()) { GestionnaireErreurs.erreur(d, "Le nom est obligatoire."); return; }
            Fournisseur f = new Fournisseur(
                    existant == null ? null : existant.getId(),
                    nom, champContact.getText().trim());
            ref.set(f);
            d.dispose();
        });

        d.setVisible(true);
        return ref.get();
    }

    private static void ligne(JPanel form, GridBagConstraints g, String label, JComponent c) {
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l.setForeground(new Color(0x374151));
        g.gridy++; form.add(l, g);
        g.gridy++;
        c.setPreferredSize(new Dimension(0, 36));
        form.add(c, g);
    }
}
