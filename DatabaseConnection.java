import java.sql.*;

public class DatabaseConnection {

    private static final String URL = "jdbc:sqlite:guild_system.db";

    // ================= CONNECTION =================

    public static Connection getConnection() throws SQLException {
        ensureDriverLoaded();
        return DriverManager.getConnection(URL);
    }

    private static void ensureDriverLoaded() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC driver not found. Ensure the JAR is in the classpath.", e);
        }
    }

    // ================= INITIALIZATION =================

    public static void initializeDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS transactions ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "adventurer_name TEXT,"
                    + "facility_name TEXT,"
                    + "original_amount REAL,"
                    + "vat_applied REAL,"
                    + "discount_applied REAL,"
                    + "final_amount REAL,"
                    + "transaction_date DATETIME DEFAULT CURRENT_TIMESTAMP)");

            stmt.execute("CREATE TABLE IF NOT EXISTS adventurers ("
                    + "id TEXT PRIMARY KEY,"
                    + "name TEXT,"
                    + "age INTEGER,"
                    + "rank TEXT,"
                    + "classType TEXT,"
                    + "password TEXT,"
                    + "xp INTEGER,"
                    + "gold INTEGER)");

            stmt.execute("CREATE TABLE IF NOT EXISTS quests ("
                    + "id TEXT PRIMARY KEY,"
                    + "name TEXT,"
                    + "xp INTEGER,"
                    + "gold INTEGER,"
                    + "requiredRank TEXT,"
                    + "status TEXT)");

            stmt.execute("CREATE TABLE IF NOT EXISTS parties ("
                    + "id TEXT PRIMARY KEY,"
                    + "leader_id TEXT,"
                    + "member_ids TEXT)");

            System.out.println("Database initialized successfully.");

        } catch (SQLException e) {
            System.out.println("CRITICAL: Failed to initialize database.");
            System.out.println("Reason: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ================= TRANSACTION LOGGING =================

    public static void logTransaction(String advName, String facility, double orig, double vat, double disc, double fin) {
        String sql = "INSERT INTO transactions (adventurer_name, facility_name, original_amount, vat_applied, discount_applied, final_amount) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, advName);
            pstmt.setString(2, facility);
            pstmt.setDouble(3, orig);
            pstmt.setDouble(4, vat);
            pstmt.setDouble(5, disc);
            pstmt.setDouble(6, fin);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("ERROR: Failed to log transaction.");
            System.out.println("Adventurer: " + advName + ", Facility: " + facility);
            System.out.println("Reason: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ================= REPORTING =================

    public static void printIncomeStatement() {
        System.out.println("\n=== GUILD HALL INCOME STATEMENT ===");

        String sql = "SELECT SUM(original_amount) as total_rev, "
                + "SUM(vat_applied) as total_vat, "
                + "SUM(discount_applied) as total_disc, "
                + "SUM(final_amount) as net_income FROM transactions";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next() && rs.getDouble("total_rev") > 0) {
                System.out.printf("Total Gross Revenue:   %.2f Gold\n", rs.getDouble("total_rev"));
                System.out.printf("Total VAT Collected:   %.2f Gold\n", rs.getDouble("total_vat"));
                System.out.printf("Total Discounts Given: -%.2f Gold\n", rs.getDouble("total_disc"));
                System.out.println("-----------------------------------");
                System.out.printf("NET GUILD INCOME:      %.2f Gold\n", rs.getDouble("net_income"));
            } else {
                System.out.println("No financial transactions recorded yet.");
            }

        } catch (SQLException e) {
            System.out.println("ERROR: Failed to generate income statement.");
            System.out.println("Reason: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
