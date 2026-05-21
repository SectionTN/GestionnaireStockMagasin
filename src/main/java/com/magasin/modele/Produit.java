package com.magasin.modele;

/** Produit du magasin. id = $id Appwrite (string). */
public class Produit {

    private String id;
    private String nom;
    private int quantite;
    private double prix;
    private String fournisseur;

    public Produit() {
    }

    public Produit(String id, String nom, int quantite, double prix, String fournisseur) {
        this.id = id;
        this.nom = nom;
        this.quantite = quantite;
        this.prix = prix;
        this.fournisseur = fournisseur;
    }

    public String getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public int getQuantite() {
        return quantite;
    }

    public double getPrix() {
        return prix;
    }

    public String getFournisseur() {
        return fournisseur;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public void setFournisseur(String fournisseur) {
        this.fournisseur = fournisseur;
    }

    @Override
    public String toString() {
        return nom + " (qte=" + quantite + ", prix=" + prix + ")";
    }
}
