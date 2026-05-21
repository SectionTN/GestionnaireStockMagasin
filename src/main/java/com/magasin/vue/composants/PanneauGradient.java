package com.magasin.vue.composants;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/** Panneau avec degrade de couleurs. */
public class PanneauGradient extends JPanel {

    private final Color couleurDebut;
    private final Color couleurFin;
    private final boolean diagonal;

    public PanneauGradient(Color debut, Color fin) {
        this(debut, fin, true);
    }

    public PanneauGradient(Color debut, Color fin, boolean diagonal) {
        this.couleurDebut = debut;
        this.couleurFin = fin;
        this.diagonal = diagonal;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();
        GradientPaint gp = diagonal
                ? new GradientPaint(0, 0, couleurDebut, w, h, couleurFin)
                : new GradientPaint(0, 0, couleurDebut, 0, h, couleurFin);
        g2.setPaint(gp);
        g2.fillRect(0, 0, w, h);
        g2.dispose();
        super.paintComponent(g);
    }
}
