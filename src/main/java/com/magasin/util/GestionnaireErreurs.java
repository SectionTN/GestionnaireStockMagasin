package com.magasin.util;

import javax.swing.JOptionPane;
import java.awt.Component;

public final class GestionnaireErreurs {

    private GestionnaireErreurs() {}

    public static void erreur(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    public static void erreur(Component parent, String message, Throwable cause) {
        String detail = cause == null ? message : message + "\n\nDetail : " + cause.getMessage();
        JOptionPane.showMessageDialog(parent, detail, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    public static void information(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean confirmer(Component parent, String message) {
        int r = JOptionPane.showConfirmDialog(parent, message, "Confirmation",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return r == JOptionPane.YES_OPTION;
    }
}
