    package ru.volkov.cw.dao;

    import ru.volkov.cw.DatabaseConfig;
    import ru.volkov.cw.model.Product;

    import java.sql.*;
    import java.util.ArrayList;
    import java.util.List;

    public class ProductDAO {

        public List<Product> getAllProducts() {
            List<Product> products = new ArrayList<>();
            String sql = "SELECT * FROM public.product ORDER BY id ASC";

            try (Connection conn = DatabaseConfig.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    Product product = new Product(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("article_number"),
                            rs.getBigDecimal("price"),
                            rs.getString("characteristic"),
                            rs.getBigDecimal("discount"),
                            rs.getBigDecimal("vat"),
                            rs.getInt("brand_id"),
                            rs.getInt("unit_id"),
                            rs.getObject("category_id", Integer.class),
                            rs.getObject("subcategory_id", Integer.class),
                            rs.getBytes("image")
                    );
                    products.add(product);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return products;
        }

        public boolean addProduct(Product product) {
            String sql = "INSERT INTO public.product (name, article_number, price, characteristic, discount, vat, brand_id, unit_id, category_id, subcategory_id, image) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, product.getName());
                pstmt.setString(2, product.getArticleNumber());
                pstmt.setBigDecimal(3, product.getPrice());
                pstmt.setString(4, product.getCharacteristic());
                pstmt.setBigDecimal(5, product.getDiscount());
                pstmt.setBigDecimal(6, product.getVat());

                if (product.getBrandId() > 0) pstmt.setInt(7, product.getBrandId());
                else pstmt.setNull(7, Types.INTEGER);

                if (product.getUnitId() > 0) pstmt.setInt(8, product.getUnitId());
                else pstmt.setNull(8, Types.INTEGER);

                if (product.getCategoryId() != null && product.getCategoryId() > 0) pstmt.setInt(9, product.getCategoryId());
                else pstmt.setNull(9, Types.INTEGER);

                if (product.getSubcategoryId() != null && product.getSubcategoryId() > 0) pstmt.setInt(10, product.getSubcategoryId());
                else pstmt.setNull(10, Types.INTEGER);

                pstmt.setBytes(11, product.getImage());
                int affectedRows = pstmt.executeUpdate();
                return affectedRows > 0;

            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

        public boolean updateProduct(Product product) {
            String sql = "UPDATE public.product SET name = ?, article_number = ?, price = ?, " +
                    "characteristic = ?, discount = ?, vat = ?, brand_id = ?, unit_id = ?, " +
                    "category_id = ?, subcategory_id = ?, image = ? WHERE id = ?";

            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, product.getName());
                pstmt.setString(2, product.getArticleNumber());
                pstmt.setBigDecimal(3, product.getPrice());
                pstmt.setString(4, product.getCharacteristic());
                pstmt.setBigDecimal(5, product.getDiscount());
                pstmt.setBigDecimal(6, product.getVat());

                if (product.getBrandId() > 0) pstmt.setInt(7, product.getBrandId());
                else pstmt.setNull(7, Types.INTEGER);

                if (product.getUnitId() > 0) pstmt.setInt(8, product.getUnitId());
                else pstmt.setNull(8, Types.INTEGER);

                if (product.getCategoryId() != null && product.getCategoryId() > 0) pstmt.setInt(9, product.getCategoryId());
                else pstmt.setNull(9, Types.INTEGER);

                if (product.getSubcategoryId() != null && product.getSubcategoryId() > 0) pstmt.setInt(10, product.getSubcategoryId());
                else pstmt.setNull(10, Types.INTEGER);

                pstmt.setBytes(11, product.getImage());
                pstmt.setInt(12, product.getId());

                int affectedRows = pstmt.executeUpdate();
                return affectedRows > 0;

            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

        public boolean deleteProduct(int id) {
            String sql = "DELETE FROM public.product WHERE id = ?";

            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, id);
                int affectedRows = pstmt.executeUpdate();
                return affectedRows > 0;

            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

        public String generateNewArticle(int categoryId) {
            String sql = "SELECT public.generate_article(?)";
            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, categoryId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString(1);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return "ERR-ERR-000000";
        }
    }