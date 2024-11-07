package com.example.jardinenfantmobile.model;

import java.io.Serializable;

public class Enfant implements Serializable {
    private String nom;
    private int age;
    private String niveauClasse;

    // Constructeur
    public Enfant(String nom, int age, String niveauClasse) {
        this.nom = nom;
        this.age = age;
        this.niveauClasse = niveauClasse;
    }

    // Getters et setters
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getNiveauClasse() { return niveauClasse; }
    public void setNiveauClasse(String niveauClasse) { this.niveauClasse = niveauClasse; }

    @Override
    public String toString() {
        return "Enfant{" +
                "nom='" + nom + '\'' +
                ", age=" + age +
                ", niveauClasse='" + niveauClasse + '\'' +
                '}';
    }
}
