package pkg.gestion_stock.service;

public class SessionManager {
    private static SessionManager instance;
    private String username;
    private boolean isLoggedIn = false;

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void login(String username) {
        this.username = username;
        this.isLoggedIn = true;
        System.out.println("Session creee pour : " + username);
    }

    public void logout() {
        this.username = null;
        this.isLoggedIn = false;
        System.out.println("Session fermee");
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public String getUsername() {
        return username;
    }
}
