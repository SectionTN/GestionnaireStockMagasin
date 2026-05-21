package com.magasin.vue;

import com.magasin.vue.composants.UI;

import com.magasin.modele.Commande;
import com.magasin.modele.Produit;
import com.magasin.util.GestionnaireErreurs;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public final class DialogueCommande {

    private DialogueCommande() {
    }

    public static Commande afficher(Component parent, Commande existant, List<Produit> produits) {
        Window w = SwingUtilities.getWindowAncestor(parent);
        JDialog d = new JDialog(w, existant == null ? "Enregistrer une commande" : "Modifier la commande",
                JDialog.ModalityType.APPLICATION_MODAL);
        d.setSize(460, 380);
        d.setLocationRelativeTo(parent);

        DefaultComboBoxModel<Produit> modeleCombo = new DefaultComboBoxModel<>();
        for (Produit p : produits)
            modeleCombo.addElement(p);
        JComboBox<Produit> combo = new JComboBox<>(modeleCombo);
        if (existant != null) {
            for (Produit p : produits) {
                if (p.getId().equals(existant.getProduitId())) {
                    combo.setSelectedItem(p);
                    break;
                }
            }
        }

        JTextField champQte = new JTextField(existant == null ? "" : String.valueOf(existant.getQuantite()));
        JTextField champDate = new JTextField(existant == null
                ? LocalDate.now().format(DateTimeFormatter.ISO_DATE)
                : extraireDateISO(existant.getDateCommande()));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.gridy = 0;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;
        g.insets = new java.awt.Insets(6, 0, 6, 0);

        ligne(form, g, "Produit", combo);
        ligne(form, g, "Quantite", champQte);
        ligne(form, g, "Date (AAAA-MM-JJ)", champDate);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        actions.setBackground(Color.WHITE);
        JButton annuler = new JButton("Annuler");
        UI.BoutonPrimaire valider = new UI.BoutonPrimaire(existant == null ? "Enregistrer" : "Mettre a jour");
        actions.add(annuler);
        actions.add(valider);

        d.setLayout(new BorderLayout());
        d.add(form, BorderLayout.CENTER);
        d.add(actions, BorderLayout.SOUTH);

        AtomicReference<Commande> ref = new AtomicReference<>();
        annuler.addActionListener(e -> d.dispose());
        valider.addActionListener(e -> {
            try {
                Produit p = (Produit) combo.getSelectedItem();
                if (p == null)
                    throw new IllegalArgumentException("Selectionnez un produit.");
                int qte = Integer.parseInt(champQte.getText().trim());
                if (qte <= 0)
                    throw new IllegalArgumentException("Quantite doit etre > 0.");
                String dateBrute = champDate.getText().trim();
                String date = extraireDateISO(dateBrute);
                LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
                Commande c = new Commande(
                        existant == null ? null : existant.getId(),
                        p.getId(), qte, date);
                ref.set(c);
                d.dispose();
            } catch (NumberFormatException nfe) {
                GestionnaireErreurs.erreur(d, "Quantite invalide.");
            } catch (java.time.format.DateTimeParseException dtpe) {
                GestionnaireErreurs.erreur(d, "Format de date invalide (AAAA-MM-JJ attendu).");
            } catch (IllegalArgumentException iae) {
                GestionnaireErreurs.erreur(d, iae.getMessage());
            }
        });

        d.setVisible(true);
        return ref.get();
    }

    private static String extraireDateISO(String entree) {
        if (entree == null)
            return "";
        String s = entree.trim();
        if (s.length() >= 10 && s.charAt(4) == '-' && s.charAt(7) == '-') {
            return s.substring(0, 10);
        }
        return s;
    }

    private static void ligne(JPanel form, GridBagConstraints g, String label, JComponent c) {
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l.setForeground(new Color(0x374151));
        g.gridy++;
        form.add(l, g);
        g.gridy++;
        c.setPreferredSize(new Dimension(0, 36));
        form.add(c, g);
    }
}
