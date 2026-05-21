package com.magasin.vue.composants;

import com.magasin.Application;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

/** En-tete de section : titre + sous-titre. */
public class EnTetePanneau extends JPanel {

    public EnTetePanneau(String titre, String sousTitre) {
        super(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));

        JLabel labTitre = new JLabel(titre);
        labTitre.setFont(new Font("Segoe UI", Font.BOLD, 22));
        labTitre.setForeground(Application.COULEUR_TEXTE);

        JLabel labSous = new JLabel(sousTitre);
        labSous.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        labSous.setForeground(new Color(0x6B7280));

        JPanel gauche = new JPanel();
        gauche.setOpaque(false);
        gauche.setLayout(new javax.swing.BoxLayout(gauche, javax.swing.BoxLayout.Y_AXIS));
        labTitre.setAlignmentX(0f);
        labSous.setAlignmentX(0f);
        gauche.add(labTitre);
        gauche.add(labSous);
        add(gauche, BorderLayout.WEST);
    }
}
