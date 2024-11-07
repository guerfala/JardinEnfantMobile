package com.example.jardinenfantmobile.model;

import java.io.Serializable;

public class Parent implements Serializable {
    private String nom;
    private String prenom;
    private String adresse;
    private String telephone;
    private String email;
    private double montantAPayer;
    private Enfant enfant;  // Ajout de l'enfant

    // Constructeur avec l'enfant
    public Parent(String nom, String prenom, String adresse, String telephone, String email, double montantAPayer, Enfant enfant) {
        this.nom = nom;
        this.prenom = prenom;
        this.adresse = adresse;
        this.telephone = telephone;
        this.email = email;
        this.montantAPayer = montantAPayer;
        this.enfant = enfant;
    }

    // Constructeur sans enfant
    public Parent(String nom, String prenom, String adresse, String telephone, String email, double montantAPayer) {
        this.nom = nom;
        this.prenom = prenom;
        this.adresse = adresse;
        this.telephone = telephone;
        this.email = email;
        this.montantAPayer = montantAPayer;
        this.enfant = null; // Enfant est laissé null
    }

    // Getters et setters pour tous les champs, y compris 'enfant'
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public double getMontantAPayer() { return montantAPayer; }
    public void setMontantAPayer(double montantAPayer) { this.montantAPayer = montantAPayer; }

    public Enfant getEnfant() { return enfant; }
    public void setEnfant(Enfant enfant) { this.enfant = enfant; }

    @Override
    public String toString() {
        return "Parent{" +
                "nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", adresse='" + adresse + '\'' +
                ", telephone='" + telephone + '\'' +
                ", email='" + email + '\'' +
                ", montantAPayer=" + montantAPayer +
                '}';
    }
}
