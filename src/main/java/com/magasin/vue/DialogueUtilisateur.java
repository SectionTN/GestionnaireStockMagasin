package com.magasin.vue;

import com.magasin.service.Appwrite;
import com.magasin.vue.composants.UI;

import com.magasin.modele.Utilisateur;
import com.magasin.util.GestionnaireErreurs;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
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

/** Formulaire utilisateur. Renvoie un tableau [Utilisateur, motDePasseClairOuVide]. */
public final class DialogueUtilisateur {

    public static final class Resultat {
        public final Utilisateur utilisateur;
        public final String motDePasseClair; // null si non modifie
        public Resultat(Utilisateur u, String mdp) { this.utilisateur = u; this.motDePasseClair = mdp; }
    }

    private DialogueUtilisateur() {}

    public static Resultat afficher(Component parent, Utilisateur existant) {
        Window w = SwingUtilities.getWindowAncestor(parent);
        JDialog d = new JDialog(w, existant == null ? "Ajouter un utilisateur" : "Modifier l'utilisateur",
                JDialog.ModalityType.APPLICATION_MODAL);
        d.setSize(460, 380);
        d.setLocationRelativeTo(parent);

        JTextField champUser = new JTextField(existant == null ? "" : existant.getUsername());
        JPasswordField champMdp = new JPasswordField();
        champMdp.putClientProperty("JTextField.placeholderText",
                existant == null ? "Mot de passe" : "Laisser vide pour conserver");
        JComboBox<String> comboRole = new JComboBox<>(new String[]{
                Utilisateur.ROLE_ADMINISTRATEUR, Utilisateur.ROLE_GESTIONNAIRE });
        if (existant != null) comboRole.setSelectedItem(existant.getRole());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.gridy = 0; g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1; g.insets = new java.awt.Insets(6, 0, 6, 0);

        ligne(form, g, "Nom d'utilisateur", champUser);
        ligne(form, g, "Mot de passe", champMdp);
        ligne(form, g, "Role", comboRole);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        actions.setBackground(Color.WHITE);
        JButton annuler = new JButton("Annuler");
        UI.BoutonPrimaire valider = new UI.BoutonPrimaire(existant == null ? "Ajouter" : "Enregistrer");
        actions.add(annuler); actions.add(valider);

        d.setLayout(new BorderLayout());
        d.add(form, BorderLayout.CENTER);
        d.add(actions, BorderLayout.SOUTH);

        AtomicReference<Resultat> ref = new AtomicReference<>();
        annuler.addActionListener(e -> d.dispose());
        valider.addActionListener(e -> {
            String user = champUser.getText().trim();
            String mdp = new String(champMdp.getPassword());
            String role = (String) comboRole.getSelectedItem();

            if (user.isEmpty()) { GestionnaireErreurs.erreur(d, "Le nom d'utilisateur est obligatoire."); return; }
            if (existant == null && mdp.isEmpty()) {
                GestionnaireErreurs.erreur(d, "Le mot de passe est obligatoire pour un nouvel utilisateur."); return;
            }
            Utilisateur u = new Utilisateur(
                    existant == null ? null : existant.getId(),
                    user, existant == null ? null : existant.getPassword(), role);
            ref.set(new Resultat(u, mdp.isEmpty() ? null : mdp));
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
