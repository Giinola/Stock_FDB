package pkg.gestion_stock.controllers;


import java.util.HashSet;
import java.util.Set;


public class Role {
    private Long id;
    private String nom;
    private String description;
    private Set<Permission> permissions;

    // Constructeurs
    public Role() {
        this.permissions = new HashSet<>();
    }

    public Role(String nom, String description) {
        this();
        this.nom = nom;
        this.description = description;
    }

    // Getters et Setters
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

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    // Méthodes utilitaires


    public void ajouterPermission(Permission permission) {
        this.permissions.add(permission);
    }


    public void retirerPermission(Permission permission) {
        this.permissions.remove(permission);
    }


    public boolean hasPermission(String permissionName) {
        return permissions.stream()
                .anyMatch(p -> p.getNom().equals(permissionName));
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", permissions=" + permissions.size() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return nom != null && nom.equals(role.nom);
    }

    @Override
    public int hashCode() {
        return nom != null ? nom.hashCode() : 0;
    }
}
}
