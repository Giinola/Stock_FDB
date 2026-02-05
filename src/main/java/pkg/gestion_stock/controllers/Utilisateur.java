package pkg.gestion_stock.controllers;


import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


public class Utilisateur {
    private Long id;
    private String username;
    private String password; // Hash BCrypt
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private boolean actif;
    private LocalDateTime dateCreation;
    private LocalDateTime dernierLogin;
    private int tentativesEchec;
    private boolean compteVerrouille;
    private Set<Role> roles;

    // Constructeurs
    public Utilisateur() {
        this.actif = true;
        this.dateCreation = LocalDateTime.now();
        this.tentativesEchec = 0;
        this.compteVerrouille = false;
        this.roles = new HashSet<>();
    }

    public Utilisateur(String username, String password) {
        this();
        this.username = username;
        this.password = password;
    }

    // Getters et Setters
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

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    // Méthodes utilitaires


    public boolean hasRole(String roleName) {
        return roles.stream()
                .anyMatch(role -> role.getNom().equals(roleName));
    }


    public boolean hasPermission(String permissionName) {
        return roles.stream()
                .flatMap(role -> role.getPermissions().stream())
                .anyMatch(perm -> perm.getNom().equals(permissionName));
    }


    public String getNomComplet() {
        if (nom != null && prenom != null) {
            return prenom + " " + nom;
        } else if (nom != null) {
            return nom;
        } else if (prenom != null) {
            return prenom;
        }
        return username;
    }


    public void incrementerTentativesEchec() {
        this.tentativesEchec++;
        if (this.tentativesEchec >= 5) {
            this.compteVerrouille = true;
        }
    }


    public void reinitialiserTentativesEchec() {
        this.tentativesEchec = 0;
    }

    @Override
    public String toString() {
        return "Utilisateur{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", actif=" + actif +
                '}';
    }
}
}
