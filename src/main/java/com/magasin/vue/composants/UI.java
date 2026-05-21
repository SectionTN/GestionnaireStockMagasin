package com.magasin.vue.composants;

import com.magasin.Application;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Composants Swing stylises reutilises dans toute l'interface.
 *   - BoutonPrimaire : bouton accentue avec coins arrondis
 *   - Gradient       : panneau a degrade de couleurs
 *   - Carte          : panneau avec coins arrondis et ombre
 *   - Tableau        : JTable stylise avec rendu alterne
 *   - EnTete         : titre + sous-titre uniformes
 */
public final class UI {

    private UI() {}

    // ----------------------------------------------------------------
    // BoutonPrimaire
    // ----------------------------------------------------------------
    public static class BoutonPrimaire extends JButton {
        public BoutonPrimaire(String texte) {
            this(texte, Application.COULEUR_ACCENT, Color.WHITE);
        }
        public BoutonPrimaire(String texte, Color fond, Color premier) {
            super(texte);
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

    // ----------------------------------------------------------------
    // Gradient
    // ----------------------------------------------------------------
    public static class Gradient extends JPanel {
        private final Color debut, fin;
        public Gradient(Color debut, Color fin) {
            this.debut = debut;
            this.fin = fin;
            setOpaque(false);
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(new GradientPaint(0, 0, debut, getWidth(), getHeight(), fin));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ----------------------------------------------------------------
    // Carte
    // ----------------------------------------------------------------
    public static class Carte extends JPanel {
        private final int rayon;
        private final Color fond;
        public Carte() { this(18, Color.WHITE); }
        public Carte(int rayon, Color fond) {
            this.rayon = rayon;
            this.fond = fond;
            setOpaque(false);
            setBorder(new EmptyBorder(24, 28, 24, 28));
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0, 0, 0, 18));
            g2.fillRoundRect(4, 6, getWidth() - 8, getHeight() - 8, rayon, rayon);
            g2.setColor(fond);
            g2.fillRoundRect(0, 0, getWidth() - 6, getHeight() - 6, rayon, rayon);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ----------------------------------------------------------------
    // Tableau stylise
    // ----------------------------------------------------------------
    public static class Tableau extends JTable {
        public Tableau(TableModel modele) {
            super(modele);
            setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            setRowHeight(34);
            setShowGrid(false);
            setIntercellSpacing(new Dimension(0, 0));
            setFillsViewportHeight(true);
            setFont(new Font("Segoe UI", Font.PLAIN, 13));

            JTableHeader entete = getTableHeader();
            entete.setFont(new Font("Segoe UI", Font.BOLD, 13));
            entete.setBackground(new Color(0xF3F4F6));
            entete.setForeground(new Color(0x374151));
            entete.setPreferredSize(new Dimension(0, 38));
            entete.setReorderingAllowed(false);

            setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
                @Override public Component getTableCellRendererComponent(JTable t, Object v,
                        boolean sel, boolean foc, int r, int c) {
                    Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                    if (sel) {
                        comp.setBackground(new Color(0xE0E7FF));
                        comp.setForeground(new Color(0x1E40AF));
                    } else {
                        comp.setBackground(r % 2 == 0 ? Color.WHITE : new Color(0xF9FAFB));
                        comp.setForeground(new Color(0x1F2937));
                    }
                    setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                    return comp;
                }
            });
        }
    }

    // ----------------------------------------------------------------
    // EnTete (titre + sous-titre)
    // ----------------------------------------------------------------
    public static class EnTete extends JPanel {
        public EnTete(String titre, String sousTitre) {
            super(new BorderLayout());
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));
            JLabel t = new JLabel(titre);
            t.setFont(new Font("Segoe UI", Font.BOLD, 22));
            t.setForeground(Application.COULEUR_TEXTE);
            JLabel s = new JLabel(sousTitre);
            s.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            s.setForeground(new Color(0x6B7280));
            JPanel gauche = new JPanel();
            gauche.setOpaque(false);
            gauche.setLayout(new javax.swing.BoxLayout(gauche, javax.swing.BoxLayout.Y_AXIS));
            t.setAlignmentX(0f); s.setAlignmentX(0f);
            gauche.add(t); gauche.add(s);
            add(gauche, BorderLayout.WEST);
        }
    }
}
