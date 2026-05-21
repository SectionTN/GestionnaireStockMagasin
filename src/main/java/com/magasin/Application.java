package com.magasin;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.magasin.util.Configuration;
import com.magasin.util.GestionnaireErreurs;
import com.magasin.vue.FenetreConnexion;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;

/** Point d'entree de l'application Gestionnaire de Stock de Magasin. */
public final class Application {

    public static final Color COULEUR_ACCENT = new Color(0x2D6CDF);
    public static final Color COULEUR_SUCCES = new Color(0x28A745);
    public static final Color COULEUR_DANGER = new Color(0xDC3545);
    public static final Color COULEUR_FOND = new Color(0xF5F7FB);
    public static final Color COULEUR_TEXTE = new Color(0x1F2937);
    public static final Color COULEUR_BARRE_LAT = new Color(0x1E293B);

    private Application() {
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            try {
                initialiserApparence();
                Configuration.charger();
                new FenetreConnexion().setVisible(true);
            } catch (Exception e) {
                GestionnaireErreurs.erreur(null,
                        "Echec du demarrage de l'application", e);
                System.exit(1);
            }
        });
    }

    private static double detecterEchelle() {
        String env = System.getenv("MAGASIN_UI_SCALE");
        if (env != null && !env.isBlank()) {
            try {
                return Double.parseDouble(env);
            } catch (Exception ignore) {
            }
        }
        String gdk = System.getenv("GDK_SCALE");
        if (gdk != null && !gdk.isBlank()) {
            try {
                return Double.parseDouble(gdk);
            } catch (Exception ignore) {
            }
        }
        String os = System.getProperty("os.name", "").toLowerCase();
        if (os.contains("linux"))
            return 2.0;
        return 1.0;
    }

    private static void initialiserApparence() throws Exception {
        UIManager.put("Button.arc", 12);
        UIManager.put("Component.arc", 10);
        UIManager.put("TextComponent.arc", 10);
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.thumbInsets", new java.awt.Insets(2, 2, 2, 2));
        UIManager.put("ProgressBar.arc", 999);
        UIManager.put("Component.focusWidth", 1);
        UIManager.put("Component.innerFocusWidth", 1);
        UIManager.put("Component.focusColor", COULEUR_ACCENT);
        UIManager.put("Component.accentColor", COULEUR_ACCENT);
        UIManager.put("TitlePane.unifiedBackground", true);
        UIManager.put("Table.rowHeight", 32);
        UIManager.put("Table.showHorizontalLines", true);
        UIManager.put("Table.intercellSpacing", new java.awt.Dimension(0, 0));
        UIManager.put("TabbedPane.tabHeight", 36);
        UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 13));

        FlatIntelliJLaf.setup();
    }
}
