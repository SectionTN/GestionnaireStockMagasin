package com.magasin.modele;

/** Commande d'un produit. dateCommande = ISO-8601 (yyyy-MM-dd). */
public class Commande {

    private String id;
    private String produitId;
    private int quantite;
    private String dateCommande;

    public Commande(String id, String produitId, int quantite, String dateCommande) {
        this.id = id;
        this.produitId = produitId;
        this.quantite = quantite;
        this.dateCommande = dateCommande;
    }

    public String getId() {
        return id;
    }

    public String getProduitId() {
        return produitId;
    }

    public int getQuantite() {
        return quantite;
    }

    public String getDateCommande() {
        return dateCommande;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setProduitId(String produitId) {
        this.produitId = produitId;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public void setDateCommande(String dateCommande) {
        this.dateCommande = dateCommande;
    }
}
