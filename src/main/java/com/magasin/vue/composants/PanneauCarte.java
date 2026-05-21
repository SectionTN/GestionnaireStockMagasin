package com.magasin.vue.composants;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/** Panneau "carte" avec coins arrondis et legere ombre. */
public class PanneauCarte extends JPanel {

    private final int rayon;
    private final Color couleurFond;

    public PanneauCarte() {
        this(18, Color.WHITE);
    }

    public PanneauCarte(int rayon, Color fond) {
        this.rayon = rayon;
        this.couleurFond = fond;
        setOpaque(false);
        setBorder(new EmptyBorder(24, 28, 24, 28));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // ombre
        g2.setColor(new Color(0, 0, 0, 18));
        g2.fillRoundRect(4, 6, getWidth() - 8, getHeight() - 8, rayon, rayon);
        // fond
        g2.setColor(couleurFond);
        g2.fillRoundRect(0, 0, getWidth() - 6, getHeight() - 6, rayon, rayon);
        g2.dispose();
        super.paintComponent(g);
    }
}
