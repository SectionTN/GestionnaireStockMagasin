package com.magasin.modele;

/** Fournisseur de produits. */
public class Fournisseur {

    private String id;
    private String nom;
    private String contact;

    public Fournisseur() {}

    public Fournisseur(String id, String nom, String contact) {
        this.id = id;
        this.nom = nom;
        this.contact = contact;
    }

    public String getId()       { return id; }
    public String getNom()      { return nom; }
    public String getContact()  { return contact; }

    public void setId(String id)            { this.id = id; }
    public void setNom(String nom)          { this.nom = nom; }
    public void setContact(String contact)  { this.contact = contact; }

    @Override
    public String toString() { return nom; }
}
