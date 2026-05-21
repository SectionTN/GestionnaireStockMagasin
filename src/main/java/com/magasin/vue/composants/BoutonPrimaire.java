package com.magasin.vue.composants;

import com.magasin.Application;

import javax.swing.JButton;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;

/** Bouton style primaire (fond accent, texte blanc, coins arrondis). */
public class BoutonPrimaire extends JButton {

    public BoutonPrimaire(String texte) {
        super(texte);
        appliquerStyle(Application.COULEUR_ACCENT, Color.WHITE);
    }

    public BoutonPrimaire(String texte, Color fond, Color textePremier) {
        super(texte);
        appliquerStyle(fond, textePremier);
    }

    private void appliquerStyle(Color fond, Color premier) {
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(true);
        setBackground(fond);
        setForeground(premier);
        setFont(getFont().deriveFont(Font.BOLD, 13f));
        setBorder(new EmptyBorder(10, 22, 10, 22));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        putClientProperty("JButton.buttonType", "roundRect");
    }
}
