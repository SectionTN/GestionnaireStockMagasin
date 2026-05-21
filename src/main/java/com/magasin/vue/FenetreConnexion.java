package com.magasin.vue;

import com.magasin.Application;
import com.magasin.controleur.ControleurConnexion;
import com.magasin.vue.composants.BoutonPrimaire;
import com.magasin.vue.composants.PanneauCarte;
import com.magasin.vue.composants.PanneauGradient;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

/** Fenetre de connexion stylisee. */
public class FenetreConnexion extends JFrame {

    private final JTextField champUsername = new JTextField();
    private final JPasswordField champMotDePasse = new JPasswordField();
    private final JLabel etiquetteMessage = new JLabel(" ");

    public FenetreConnexion() {
        super("Gestionnaire de Stock — Connexion");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Taille adaptative : 70% de l'ecran logique, plafonnee
        Dimension ecran = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        int l = Math.min(960, (int) (ecran.width * 0.75));
        int h = Math.min(620, (int) (ecran.height * 0.80));
        setSize(l, h);
        setMinimumSize(new Dimension(Math.min(700, l), Math.min(500, h)));
        setLocationRelativeTo(null);
        construire();
    }

    private void construire() {
        JPanel racine = new JPanel(new GridBagLayout());
        racine.setBackground(Application.COULEUR_FOND);

        JPanel conteneur = new JPanel(new BorderLayout());
        conteneur.setPreferredSize(new Dimension(820, 460));
        conteneur.setOpaque(false);

        conteneur.add(construirePanneauIllustration(), BorderLayout.WEST);
        conteneur.add(construirePanneauFormulaire(), BorderLayout.CENTER);

        racine.add(conteneur);
        setContentPane(racine);
    }

    private JPanel construirePanneauIllustration() {
        PanneauGradient pg = new PanneauGradient(
                new Color(0x2D6CDF), new Color(0x6A4DF2));
        pg.setPreferredSize(new Dimension(380, 460));
        pg.setLayout(new BoxLayout(pg, BoxLayout.Y_AXIS));
        pg.setBorder(BorderFactory.createEmptyBorder(48, 40, 48, 40));

        JLabel titre = new JLabel("Magasin Pro");
        titre.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titre.setForeground(Color.WHITE);
        titre.setAlignmentX(0f);

        JLabel sous = new JLabel("<html>Gestion intelligente<br>de votre stock</html>");
        sous.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        sous.setForeground(new Color(255, 255, 255, 220));
        sous.setAlignmentX(0f);
        sous.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        JLabel pied = new JLabel("<html>Authentifiez-vous pour acceder<br>au tableau de bord.</html>");
        pied.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pied.setForeground(new Color(255, 255, 255, 180));
        pied.setAlignmentX(0f);

        pg.add(titre);
        pg.add(sous);
        pg.add(Box.createVerticalGlue());
        pg.add(pied);
        return pg;
    }

    private JPanel construirePanneauFormulaire() {
        PanneauCarte carte = new PanneauCarte(20, Color.WHITE);
        carte.setLayout(new BoxLayout(carte, BoxLayout.Y_AXIS));
        carte.setBorder(BorderFactory.createEmptyBorder(48, 48, 48, 48));

        JLabel titre = new JLabel("Bienvenue");
        titre.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titre.setForeground(Application.COULEUR_TEXTE);
        titre.setAlignmentX(0f);

        JLabel desc = new JLabel("Connectez-vous a votre compte");
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        desc.setForeground(new Color(0x6B7280));
        desc.setAlignmentX(0f);
        desc.setBorder(BorderFactory.createEmptyBorder(4, 0, 28, 0));

        JLabel labUser = etiquette("Nom d'utilisateur");
        champUsername.putClientProperty("JTextField.placeholderText", "ex. admin");
        champUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        champUsername.setAlignmentX(0f);
        champUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel labPass = etiquette("Mot de passe");
        champMotDePasse.putClientProperty("JTextField.placeholderText", "••••••••");
        champMotDePasse.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        champMotDePasse.setAlignmentX(0f);
        champMotDePasse.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        BoutonPrimaire bouton = new BoutonPrimaire("Se connecter");
        bouton.setAlignmentX(0f);
        bouton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        bouton.addActionListener(e -> tenterConnexion());

        etiquetteMessage.setForeground(Application.COULEUR_DANGER);
        etiquetteMessage.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        etiquetteMessage.setAlignmentX(0f);
        etiquetteMessage.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        // Entree clavier valide aussi
        champMotDePasse.addActionListener(e -> tenterConnexion());

        carte.add(titre);
        carte.add(desc);
        carte.add(labUser);
        carte.add(Box.createVerticalStrut(6));
        carte.add(champUsername);
        carte.add(Box.createVerticalStrut(16));
        carte.add(labPass);
        carte.add(Box.createVerticalStrut(6));
        carte.add(champMotDePasse);
        carte.add(Box.createVerticalStrut(24));
        carte.add(bouton);
        carte.add(etiquetteMessage);
        carte.add(Box.createVerticalGlue());

        JLabel astuce = new JLabel("Roles : administrateur ou gestionnaire");
        astuce.setForeground(new Color(0x9CA3AF));
        astuce.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        astuce.setAlignmentX(0f);
        astuce.setHorizontalAlignment(SwingConstants.LEFT);
        carte.add(astuce);

        return carte;
    }

    private JLabel etiquette(String texte) {
        JLabel l = new JLabel(texte);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l.setForeground(new Color(0x374151));
        l.setAlignmentX(0f);
        return l;
    }

    private void tenterConnexion() {
        String u = champUsername.getText().trim();
        String p = new String(champMotDePasse.getPassword());
        etiquetteMessage.setText(" ");
        ControleurConnexion.connecter(this, u, p, message -> {
            etiquetteMessage.setText(message);
            etiquetteMessage.setForeground(Application.COULEUR_DANGER);
        });
    }

    public void afficherMessage(String message, boolean succes) {
        etiquetteMessage.setText(message);
        etiquetteMessage.setForeground(succes ? Application.COULEUR_SUCCES : Application.COULEUR_DANGER);
    }
}
