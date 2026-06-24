package practice4;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductService {
    private final String dbUrl;

    public ProductService(String dbUrl) {
        this.dbUrl = dbUrl;
        initDatabase();
    }

    private void initDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS products (" +
                "id INTEGER PRIMARY KEY, " +
                "name TEXT NOT NULL, " +
                "category TEXT, " +
                "quantity INTEGER, " +
                "price REAL" +
                ");";
        try (Connection conn = DriverManager.getConnection(dbUrl);
                Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void create(Product p) {
        String sql = "INSERT INTO products (id, name, category, quantity, price) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(dbUrl);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, p.getId());
            pstmt.setString(2, p.getName());
            pstmt.setString(3, p.getCategory());
            pstmt.setInt(4, p.getQuantity());
            pstmt.setDouble(5, p.getPrice());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(dbUrl);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Product p) {
        String sql = "UPDATE products SET name = ?, category = ?, quantity = ?, price = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(dbUrl);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, p.getName());
            pstmt.setString(2, p.getCategory());
            pstmt.setInt(3, p.getQuantity());
            pstmt.setDouble(4, p.getPrice());
            pstmt.setInt(5, p.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Product getById(int id) {
        String sql = "SELECT * FROM products WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(dbUrl);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToProduct(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Product> search(ProductFilter filter) {
        List<Product> products = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM products WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (filter.name != null) {
            sql.append(" AND LOWER(name) LIKE ?");
            params.add("%" + filter.name.toLowerCase() + "%");
        }
        if (filter.category != null) {
            sql.append(" AND LOWER(category) = ?");
            params.add(filter.category.toLowerCase());
        }
        if (filter.minQty != null) {
            sql.append(" AND quantity >= ?");
            params.add(filter.minQty);
        }
        if (filter.maxQty != null) {
            sql.append(" AND quantity <= ?");
            params.add(filter.maxQty);
        }
        if (filter.minPrice != null) {
            sql.append(" AND price >= ?");
            params.add(filter.minPrice);
        }
        if (filter.maxPrice != null) {
            sql.append(" AND price <= ?");
            params.add(filter.maxPrice);
        }

        int safePage = Math.max(1, filter.page);
        int safeSize = Math.max(1, filter.size);
        sql.append(" LIMIT ? OFFSET ?");
        params.add(safeSize);
        params.add((safePage - 1) * safeSize);

        try (Connection conn = DriverManager.getConnection(dbUrl);
                PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    products.add(mapRowToProduct(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public boolean existsByName(String name) {
        String sql = "SELECT 1 FROM products WHERE LOWER(name) = ? LIMIT 1";
        try (Connection conn = DriverManager.getConnection(dbUrl);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name.toLowerCase());
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Product mapRowToProduct(ResultSet rs) throws SQLException {
        return new Product(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("category"),
                rs.getInt("quantity"),
                rs.getDouble("price"));
    }
}