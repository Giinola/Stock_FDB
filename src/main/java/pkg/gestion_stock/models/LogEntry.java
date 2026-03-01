package pkg.gestion_stock.models;

public class LogEntry {
    private String date;
    private String username;
    private String action;
    private String tableCible;
    private String detail;

    public LogEntry(String date, String username, String action, String tableCible, String detail) {
        this.date = date;
        this.username = username;
        this.action = action;
        this.tableCible = tableCible;
        this.detail = detail;
    }

    public String getDate()       { return date; }
    public String getUsername()   { return username; }
    public String getAction()     { return action; }
    public String getTableCible() { return tableCible; }
    public String getDetail()     { return detail; }
}
