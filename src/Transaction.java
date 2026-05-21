import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Transaction {
    public static class TransactionRecord {
        private final int id;
        private final String dateTime;
        private final String type;
        private final double amount;
        private final String account;
        private final String category;
        private final String description;
        private final String mode;

        public TransactionRecord(int id, String dateTime, String type, double amount,
                String account, String category, String description, String mode) {
            this.id = id;
            this.dateTime = dateTime;
            this.type = type;
            this.amount = amount;
            this.account = account;
            this.category = category;
            this.description = description;
            this.mode = mode;
        }

        public int getId() {
            return id;
        }

        public String getDateTime() {
            return dateTime;
        }

        public String getType() {
            return type;
        }

        public double getAmount() {
            return amount;
        }

        public String getAccount() {
            return account;
        }

        public String getCategory() {
            return category;
        }

        public String getDescription() {
            return description;
        }

        public String getMode() {
            return mode;
        }
    }

    public static void addTransaction(String type, double amount, String account,
            String category, String description, String mode) throws Exception {
        String query = "INSERT INTO transactions (type, amount, account, category, description, mode) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DBconnection.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, type);
            ps.setDouble(2, amount);
            ps.setString(3, account);
            ps.setString(4, category);
            ps.setString(5, description);
            ps.setString(6, mode);
            ps.executeUpdate();
        }
    }

    public static List<TransactionRecord> getAllTransactions() throws Exception {
        List<TransactionRecord> transactions = new ArrayList<>();
        String query = "SELECT * FROM transactions ORDER BY id DESC";

        try (Connection con = DBconnection.getConnection();
                PreparedStatement ps = con.prepareStatement(query);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                transactions.add(readRecord(rs));
            }
        }

        return transactions;
    }

    public static List<TransactionRecord> searchTransactions(String keyword) throws Exception {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllTransactions();
        }

        List<TransactionRecord> transactions = new ArrayList<>();
        String query = "SELECT * FROM transactions WHERE account LIKE ? OR category LIKE ? OR description LIKE ? OR mode LIKE ? OR type LIKE ? ORDER BY id DESC";
        String value = "%" + keyword.trim() + "%";

        try (Connection con = DBconnection.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
            for (int i = 1; i <= 5; i++) {
                ps.setString(i, value);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    transactions.add(readRecord(rs));
                }
            }
        }

        return transactions;
    }

    public static List<TransactionRecord> filterTransactions(String type, String account,
            String category, String description, String mode) throws Exception {
        List<TransactionRecord> transactions = new ArrayList<>();
        List<String> values = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM transactions WHERE 1=1");

        addExactFilter(query, values, "type", type);
        addExactFilter(query, values, "account", account);
        addExactFilter(query, values, "category", category);
        addLikeFilter(query, values, "description", description);
        addExactFilter(query, values, "mode", mode);
        query.append(" ORDER BY id DESC");

        try (Connection con = DBconnection.getConnection();
                PreparedStatement ps = con.prepareStatement(query.toString())) {
            for (int i = 0; i < values.size(); i++) {
                ps.setString(i + 1, values.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    transactions.add(readRecord(rs));
                }
            }
        }

        return transactions;
    }

    public static void updateTransaction(int id, String type, double amount, String account,
            String category, String description, String mode) throws Exception {
        String query = "UPDATE transactions SET type = ?, amount = ?, account = ?, category = ?, description = ?, mode = ? WHERE id = ?";

        try (Connection con = DBconnection.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, type);
            ps.setDouble(2, amount);
            ps.setString(3, account);
            ps.setString(4, category);
            ps.setString(5, description);
            ps.setString(6, mode);
            ps.setInt(7, id);

            if (ps.executeUpdate() == 0) {
                throw new SQLException("No record found with ID " + id);
            }
        }
    }

    public static void deleteTransaction(int id) throws Exception {
        String query = "DELETE FROM transactions WHERE id = ?";

        try (Connection con = DBconnection.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, id);

            if (ps.executeUpdate() == 0) {
                throw new SQLException("No record found with ID " + id);
            }
        }
    }

    public static double getTotalAmount(String type) throws Exception {
        boolean hasType = type != null && !type.trim().isEmpty() && !"All".equalsIgnoreCase(type.trim());
        String query = hasType
                ? "SELECT SUM(amount) FROM transactions WHERE type = ?"
                : "SELECT SUM(amount) FROM transactions";

        try (Connection con = DBconnection.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
            if (hasType) {
                ps.setString(1, type.trim());
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        }

        return 0;
    }

    public static double getFilteredTotal(String type, String account,
            String category, String description, String mode) throws Exception {
        List<String> values = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT SUM(amount) FROM transactions WHERE 1=1");

        addExactFilter(query, values, "type", type);
        addExactFilter(query, values, "account", account);
        addExactFilter(query, values, "category", category);
        addLikeFilter(query, values, "description", description);
        addExactFilter(query, values, "mode", mode);

        try (Connection con = DBconnection.getConnection();
                PreparedStatement ps = con.prepareStatement(query.toString())) {
            for (int i = 0; i < values.size(); i++) {
                ps.setString(i + 1, values.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        }

        return 0;
    }

    private static void addExactFilter(StringBuilder query, List<String> values, String column, String value) {
        if (hasFilterValue(value)) {
            query.append(" AND ").append(column).append(" = ?");
            values.add(value.trim());
        }
    }

    private static void addLikeFilter(StringBuilder query, List<String> values, String column, String value) {
        if (hasFilterValue(value)) {
            query.append(" AND ").append(column).append(" LIKE ?");
            values.add("%" + value.trim() + "%");
        }
    }

    private static boolean hasFilterValue(String value) {
        return value != null
                && !value.trim().isEmpty()
                && !"All".equalsIgnoreCase(value.trim());
    }

    private static TransactionRecord readRecord(ResultSet rs) throws SQLException {
        return new TransactionRecord(
                rs.getInt("id"),
                rs.getString("date_time"),
                rs.getString("type"),
                rs.getDouble("amount"),
                rs.getString("account"),
                rs.getString("category"),
                rs.getString("description"),
                rs.getString("mode"));
    }

    public static void add(Scanner sc) {
        try {
            Connection con = DBconnection.getConnection();

            System.out.print("Type (income/expense): ");
            String type = sc.next();

            System.out.print("Amount: ");
            double amount = sc.nextDouble();

            System.out.print("Account: ");
            String account = sc.next();

            System.out.print("Category: ");
            String category = sc.next();

            sc.nextLine();

            System.out.print("Description: ");
            String description = sc.nextLine();

            System.out.print("Mode (offline/online): ");
            String mode = sc.next();

            String query = "INSERT INTO transactions (type, amount, account, category, description, mode) VALUES (?, ?, ?, ?, ?, ?)";

            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, type);
            ps.setDouble(2, amount);
            ps.setString(3, account);
            ps.setString(4, category);
            ps.setString(5, description);
            ps.setString(6, mode);

            ps.executeUpdate();

            System.out.println("Transaction added!");

        } catch (Exception e) {
            System.out.println(e);
        }
        finally{
            System.out.print("Account updates");
        }

    
    }

    public static void view(Scanner sc){
        try{
            Connection con = DBconnection.getConnection();
            System.out.println("===== EXPENSE VIEWER =====");
            System.out.println("1. View ALL Data");
            System.out.println("2. Filter by CATEGORY (Food, Rent, etc.)");
            System.out.println("3. Filter by MODE (Online/Offline)");
            System.out.println("4. Amount GREATER than X");
            System.out.println("5. Amount LESS than X");
            System.out.println("6. DATE Range (Start to End)");
            System.out.println("7. Search by ACCOUNT or DESCRIPTION");
            System.out.println("8. Filter by type (income/expense)");
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();
            sc.nextLine(); // Clear buffer

            String sql = "";
            PreparedStatement pstmt = null;

            // --- STEP 1: Define the Query based on choice ---
            if (choice == 1) {
                sql = "SELECT * FROM transactions";
                pstmt = con.prepareStatement(sql);
            } 
            else if (choice == 2) {
                sql = "SELECT * FROM transactions WHERE CATEGORY = ?";
                pstmt = con.prepareStatement(sql);
                System.out.print("Enter Category: ");
                pstmt.setString(1, sc.nextLine());
            } 
            else if (choice == 3) {
                sql = "SELECT * FROM transactions WHERE MODE = ?";
                pstmt = con.prepareStatement(sql);
                System.out.print("Enter Mode: ");
                pstmt.setString(1, sc.nextLine());
            }
            else if (choice == 4) {
                sql = "SELECT * FROM transactions WHERE AMOUNT > ?";
                pstmt = con.prepareStatement(sql);
                System.out.print("Enter Minimum Amount: ");
                pstmt.setDouble(1, sc.nextDouble());
            }
            else if (choice == 5) {
                sql = "SELECT * FROM transactions WHERE AMOUNT < ?";
                pstmt = con.prepareStatement(sql);
                System.out.print("Enter Maximum Amount: ");
                pstmt.setDouble(1, sc.nextDouble());
            }
            else if (choice == 6) {
                sql = "SELECT * FROM transactions WHERE DATE BETWEEN ? AND ?";
                pstmt = con.prepareStatement(sql);
                System.out.print("Start Date (YYYY-MM-DD): ");
                pstmt.setString(1, sc.nextLine());
                System.out.print("End Date (YYYY-MM-DD): ");
                pstmt.setString(2, sc.nextLine());
            }
            else if (choice == 7) {
                sql = "SELECT * FROM transactions WHERE ACCOUNT = ? OR DESCRIPTION LIKE ?";
                pstmt = con.prepareStatement(sql);
                System.out.print("Enter Account or Keyword: ");
                String input = sc.nextLine();
                pstmt.setString(1, input);
                pstmt.setString(2, "%" + input + "%");
            }
            else if (choice == 8){
                sql = "SELECT * FROM transactions WHERE type = ?";
                pstmt = con.prepareStatement(sql);
                System.out.print("Enter type: ");
                pstmt.setString(1, sc.nextLine());
            }

            // --- STEP 2: Execute and Print ---
            ResultSet rs = pstmt.executeQuery();
            System.out.println("\nID | DATE_TIME | TYPE | AMOUNT | ACCOUNT | CATEGORY | MODE");
            System.out.println("----------------------------------------------------------");
            
            while (rs.next()) {
                System.out.printf("%d | %s | %s | %.2f | %s | %s | %s%n", 
                    rs.getInt("ID"),
                    rs.getString("DATE_TIME"),
                    rs.getString("TYPE"), 
                    rs.getDouble("AMOUNT"), 
                    rs.getString("ACCOUNT"),
                    rs.getString("CATEGORY"), 
                    rs.getString("MODE"));
            }

        } catch (Exception e) {
            System.out.println("Connection Error: " + e.getMessage());
        }
    }

    public static void summation(Scanner sc) throws Exception {
        try {
                Connection conn = DBconnection.getConnection();    
                System.out.println("\n--- Calculate Total Spending ---");
                System.out.println("1. Total by Category (Salary, Food , shopping)");
                System.out.println("2. Total by Mode (ONLINE/OFFLINE)");
                System.out.println("3. Total by Account (BOB/DCB)");
                System.out.println("4. Total by type (income/expense)");
                System.out.println("5. Custom Filter (Category + Mode + Account + Type + Description)");
                System.out.print("Choice: ");
                int choice = sc.nextInt();
                sc.nextLine(); 

                String sql = "";
                if (choice == 1) sql = "SELECT SUM(AMOUNT) FROM transactions WHERE CATEGORY = ?";
                else if (choice == 2) sql = "SELECT SUM(AMOUNT) FROM transactions WHERE MODE = ?";
                else if (choice == 3) sql = "SELECT SUM(AMOUNT) FROM transactions WHERE ACCOUNT = ?";
                else if (choice == 4) sql = "SELECT SUM(AMOUNT) FROM transactions WHERE TYPE = ?";

                if (choice ==5){
                    StringBuilder query = new StringBuilder("SELECT SUM(amount) FROM transactions WHERE 1=1");

                    System.out.print("Category (Enter to skip): ");
                    String category = sc.nextLine();

                    System.out.print("Mode (Enter to skip): ");
                    String mode = sc.nextLine();

                    System.out.print("Account (Enter to skip): ");
                    String account = sc.nextLine();

                    System.out.print("Type (Enter to skip): ");
                    String type = sc.nextLine();

                    System.out.print("Description (Enter to skip): ");
                    String description = sc.nextLine();

                    if (!category.isEmpty()) query.append(" AND CATEGORY = ?");
                    if (!mode.isEmpty()) query.append(" AND MODE = ?");
                    if (!account.isEmpty()) query.append(" AND ACCOUNT = ?");
                    if (!type.isEmpty()) query.append(" AND TYPE = ?");
                    if (!description.isEmpty()) query.append(" AND DESCRIPTION = ?");

                    PreparedStatement pstmt = conn.prepareStatement(query.toString());

                    int index = 1;

                    if (!category.isEmpty()) pstmt.setString(index++, category);
                    if (!mode.isEmpty()) pstmt.setString(index++, mode);
                    if (!account.isEmpty()) pstmt.setString(index++, account);
                    if (!type.isEmpty()) pstmt.setString(index++, type);
                    if (!description.isEmpty()) pstmt.setString(index++, description);

                    ResultSet rs = pstmt.executeQuery();

                    if (rs.next()) {
                        System.out.println(">>> Total Spent: Rs " + rs.getDouble(1));
                    }

                } else {
                    PreparedStatement pstmt = conn.prepareStatement(sql);

                    System.out.print("Enter filter value: ");
                    pstmt.setString(1, sc.nextLine());

                    ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    System.out.println(">>> Total Spent: Rs " + rs.getDouble(1));
                }
            }
    

        }catch(SQLException e){
            System.out.println("Summation Error: " + e.getMessage());
        }
    }

    public static void deleteData(Scanner sc) throws Exception {
        Connection conn = DBconnection.getConnection();

        System.out.print("Enter ID to delete: ");
        int id = sc.nextInt();
        sc.nextLine();

        String query = "DELETE FROM transactions WHERE id = ?";

        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setInt(1, id);

        int rows = pstmt.executeUpdate();

        if (rows > 0) {
            System.out.println("Data deleted successfully");
        } else {
            System.out.println("No record found");
        }
    }

    public static void updateData(Scanner sc) throws Exception {

        Connection conn = DBconnection.getConnection();

        System.out.print("Enter ID to update: ");
        int id = sc.nextInt();
        sc.nextLine(); // clear buffer

        System.out.println("What do you want to update?");
        System.out.println("1. Category");
        System.out.println("2. Mode");
        System.out.println("3. Account");
        System.out.println("4. Type");
        System.out.println("5. Description");
        System.out.println("6. Amount");

        int choice = sc.nextInt();
        sc.nextLine();

        String column = "";

        switch (choice) {
            case 1: column = "category"; break;
            case 2: column = "mode"; break;
            case 3: column = "account"; break;
            case 4: column = "type"; break;
            case 5: column = "description"; break;
            case 6: column = "amount"; break;
            default:
                System.out.println("Invalid choice");
                return;
        }

        System.out.print("Enter new value: ");

        String query = "UPDATE transactions SET " + column + " = ? WHERE id = ?";
        PreparedStatement pstmt = conn.prepareStatement(query);

        // Handle amount separately because it is numeric.
        if (column.equals("amount")) {
            double newAmount = sc.nextDouble();
            sc.nextLine();
            pstmt.setDouble(1, newAmount);
        } else {
            String newValue = sc.nextLine();
            pstmt.setString(1, newValue);
        }

        pstmt.setInt(2, id);

        int rows = pstmt.executeUpdate();

        if (rows > 0) {
            System.out.println("Data updated successfully");
        } else {
            System.out.println("No record found with this ID");
        }
    }
}        
    


