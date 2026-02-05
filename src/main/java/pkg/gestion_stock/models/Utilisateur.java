package pkg.gestion_stock.models;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class Utilisateur {
    private Long id;
    private String username;
    private String password;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private boolean actif;
    private LocalDateTime dateCreation;
    private LocalDateTime dernierLogin;
    private int tentativesEchec;
    private boolean compteVerrouille;

    public Utilisateur() {
    }

    public Utilisateur(String username, String password) {
        this.username = username;
        this.password = password;
        this.actif = true;
        this.dateCreation = LocalDateTime.now();
        this.tentativesEchec = 0;
        this.compteVerrouille = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDateTime getDernierLogin() {
        return dernierLogin;
    }

    public void setDernierLogin(LocalDateTime dernierLogin) {
        this.dernierLogin = dernierLogin;
    }

    public int getTentativesEchec() {
        return tentativesEchec;
    }

    public void setTentativesEchec(int tentativesEchec) {
        this.tentativesEchec = tentativesEchec;
    }

    public boolean isCompteVerrouille() {
        return compteVerrouille;
    }

    public void setCompteVerrouille(boolean compteVerrouille) {
        this.compteVerrouille = compteVerrouille;
    }
}
