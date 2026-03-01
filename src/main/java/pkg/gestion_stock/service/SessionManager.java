package pkg.gestion_stock.service;

public class SessionManager {
    private static SessionManager instance;
    private String username;
    private String role;
    private boolean isLoggedIn = false;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) instance = new SessionManager();
        return instance;
    }

    public void login(String username, String role) {
        this.username = username;
        this.role = role;
        this.isLoggedIn = true;
        System.out.println("Session creee pour : " + username + " | Role : " + role);
    }

    public void logout() {
        this.username = null;
        this.role = null;
        this.isLoggedIn = false;
        System.out.println("Session fermee");
    }

    public boolean isLoggedIn() { return isLoggedIn; }
    public String getUsername() { return username; }
    public String getRole()     { return role; }

    // RBAC - seuls MANAGER et OWNER peuvent gérer les utilisateurs
    public boolean canManageUsers() {
        return "OWNER".equals(role) || "MANAGER".equals(role);
    }
}


