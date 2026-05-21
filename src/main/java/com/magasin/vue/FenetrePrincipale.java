package com.magasin.vue;

import com.magasin.Application;
import com.magasin.modele.Utilisateur;
import com.magasin.securite.SessionUtilisateur;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

/** Fenetre principale avec barre laterale + zone de contenu. */
public class FenetrePrincipale extends JFrame {

    private static final String CLE_PRODUITS     = "PRODUITS";
    private static final String CLE_FOURNISSEURS = "FOURNISSEURS";
    private static final String CLE_COMMANDES    = "COMMANDES";
    private static final String CLE_UTILISATEURS = "UTILISATEURS";

    private final CardLayout dispositionCartes = new CardLayout();
    private final JPanel zoneContenu = new JPanel(dispositionCartes);
    private final List<JToggleButton> boutonsBarre = new ArrayList<>();

    public FenetrePrincipale() {
        super("Gestionnaire de Stock de Magasin");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension ecran = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        int l = Math.min(1280, (int) (ecran.width * 0.85));
        int h = Math.min(800, (int) (ecran.height * 0.85));
        setSize(l, h);
        setMinimumSize(new Dimension(Math.min(900, l), Math.min(600, h)));
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        construire();
    }

    private void construire() {
        setLayout(new BorderLayout());
        add(construireBarreLaterale(), BorderLayout.WEST);
        add(zoneContenu, BorderLayout.CENTER);

        zoneContenu.setBackground(Application.COULEUR_FOND);
        zoneContenu.setBorder(new EmptyBorder(24, 24, 24, 24));

        zoneContenu.add(new PanneauProduits(), CLE_PRODUITS);
        zoneContenu.add(new PanneauFournisseurs(), CLE_FOURNISSEURS);
        zoneContenu.add(new PanneauCommandes(), CLE_COMMANDES);
        if (SessionUtilisateur.estAdministrateur()) {
            zoneContenu.add(new PanneauUtilisateurs(), CLE_UTILISATEURS);
        }

        afficherCarte(CLE_PRODUITS);
        if (!boutonsBarre.isEmpty()) boutonsBarre.get(0).setSelected(true);
    }

    private JPanel construireBarreLaterale() {
        JPanel barre = new JPanel();
        barre.setLayout(new BoxLayout(barre, BoxLayout.Y_AXIS));
        barre.setBackground(Application.COULEUR_BARRE_LAT);
        barre.setPreferredSize(new Dimension(240, 0));
        barre.setBorder(new EmptyBorder(24, 16, 16, 16));

        JLabel titre = new JLabel("Magasin Pro");
        titre.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titre.setForeground(Color.WHITE);
        titre.setAlignmentX(0f);
        titre.setBorder(new EmptyBorder(0, 6, 24, 0));
        barre.add(titre);

        ButtonGroup groupe = new ButtonGroup();

        ajouterEntreeNav(barre, groupe, "Produits",     CLE_PRODUITS);
        ajouterEntreeNav(barre, groupe, "Fournisseurs", CLE_FOURNISSEURS);
        ajouterEntreeNav(barre, groupe, "Commandes",    CLE_COMMANDES);
        if (SessionUtilisateur.estAdministrateur()) {
            ajouterEntreeNav(barre, groupe, "Utilisateurs", CLE_UTILISATEURS);
        }

        barre.add(Box.createVerticalGlue());
        barre.add(construirePiedDePage());
        return barre;
    }

    private void ajouterEntreeNav(JPanel barre, ButtonGroup groupe, String texte, String cle) {
        JToggleButton b = new JToggleButton(texte);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setAlignmentX(0f);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        b.setBorder(new EmptyBorder(10, 16, 10, 16));
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setForeground(new Color(0xCBD5E1));
        b.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.putClientProperty("JButton.buttonType", "borderless");

        b.addItemListener(e -> {
            if (b.isSelected()) {
                b.setOpaque(true);
                b.setContentAreaFilled(true);
                b.setBackground(new Color(0x334155));
                b.setForeground(Color.WHITE);
                b.setFont(new Font("Segoe UI", Font.BOLD, 14));
                afficherCarte(cle);
            } else {
                b.setOpaque(false);
                b.setContentAreaFilled(false);
                b.setForeground(new Color(0xCBD5E1));
                b.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            }
        });

        groupe.add(b);
        boutonsBarre.add(b);
        barre.add(b);
        barre.add(Box.createVerticalStrut(4));
    }

    private JPanel construirePiedDePage() {
        Utilisateur u = SessionUtilisateur.utilisateurCourant();
        JPanel pied = new JPanel(new GridBagLayout());
        pied.setOpaque(false);
        pied.setAlignmentX(0f);
        pied.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel nom = new JLabel(u == null ? "—" : u.getUsername());
        nom.setForeground(Color.WHITE);
        nom.setFont(new Font("Segoe UI", Font.BOLD, 13));
        pied.add(nom, gbc);

        gbc.gridy = 1;
        JLabel role = new JLabel(u == null ? "" : ("Role : " + u.getRole()));
        role.setForeground(new Color(0x94A3B8));
        role.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        pied.add(role, gbc);

        gbc.gridy = 2; gbc.insets = new java.awt.Insets(12, 0, 0, 0);
        JButton deconnexion = new JButton("Se deconnecter");
        deconnexion.setBackground(new Color(0x475569));
        deconnexion.setForeground(Color.WHITE);
        deconnexion.setFocusPainted(false);
        deconnexion.setBorderPainted(false);
        deconnexion.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        deconnexion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        deconnexion.addActionListener(e -> seDeconnecter());
        pied.add(deconnexion, gbc);

        JPanel cadre = new JPanel();
        cadre.setLayout(new BoxLayout(cadre, BoxLayout.Y_AXIS));
        cadre.setOpaque(false);
        cadre.setAlignmentX(0f);
        cadre.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0x334155)));
        cadre.add(Box.createVerticalStrut(16));
        cadre.add(pied);
        return cadre;
    }

    private void afficherCarte(String cle) {
        dispositionCartes.show(zoneContenu, cle);
    }

    private void seDeconnecter() {
        SessionUtilisateur.deconnecter();
        dispose();
        new FenetreConnexion().setVisible(true);
    }
}
