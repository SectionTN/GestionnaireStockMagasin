package com.magasin.modele;

public class Utilisateur {

    public static final String ROLE_ADMINISTRATEUR = "administrateur";
    public static final String ROLE_GESTIONNAIRE = "gestionnaire";

    private String id;
    private String username;
    private String password;
    private String role;

    public Utilisateur() {
    }

    public Utilisateur(String id, String username, String password, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean estAdministrateur() {
        return ROLE_ADMINISTRATEUR.equalsIgnoreCase(role);
    }

    @Override
    public String toString() {
        return username + " [" + role + "]";
    }
}
