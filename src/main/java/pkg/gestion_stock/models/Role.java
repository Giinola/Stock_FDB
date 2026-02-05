package pkg.gestion_stock.models;

import java.util.HashSet;
import java.util.Set;

public class Role {
    private Long id;
    private String nom;
    private String description;

    public Role() {
    }

    public Role(String nom, String description) {
        this.nom = nom;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}