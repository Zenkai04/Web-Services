package dao;

import dto.Canal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CanalDAOJDBC implements CanalDAO {

    private final DbConnectionManager dbManager;

    public CanalDAOJDBC(DbConnectionManager dbManager) {
        this.dbManager = dbManager;
    }

    @Override
    public List<Canal> findAll() {
        String sql = "SELECT * FROM canal";
        List<Canal> canals = new ArrayList<>();
        try (Connection conn = dbManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                canals.add(mapResultSetToCanal(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return canals;
    }

    @Override
    public Canal findById(int idCanal) {
        String sql = "SELECT * FROM canal WHERE \"idCanal\" = ?";
        try (Connection conn = dbManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCanal);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCanal(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Canal findBySlug(String slug) {
        String sql = "SELECT * FROM canal WHERE slug = ?";
        try (Connection conn = dbManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, slug);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCanal(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean save(Canal canal) {
        String sql = "INSERT INTO canal (\"idAdmin\", nom, description, \"typeCanal\", slug, \"dateCreation\") "
                + "VALUES (?, ?, ?, ?, ?, COALESCE(?, CURRENT_TIMESTAMP))";
        try (Connection conn = dbManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, canal.getIdAdmin());
            stmt.setString(2, canal.getNom());
            stmt.setString(3, canal.getDescription());
            stmt.setString(4, canal.getTypeCanal());
            stmt.setString(5, canal.getSlug());
            stmt.setTimestamp(6, canal.getDateCreation());
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        canal.setIdCanal(keys.getInt(1));
                    }
                }
            }

            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Canal canal) {
        String sql = "UPDATE canal SET \"idAdmin\" = ?, nom = ?, description = ?, \"typeCanal\" = ?, slug = ?, "
                + "\"dateCreation\" = COALESCE(?, \"dateCreation\") WHERE \"idCanal\" = ?";
        try (Connection conn = dbManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, canal.getIdAdmin());
            stmt.setString(2, canal.getNom());
            stmt.setString(3, canal.getDescription());
            stmt.setString(4, canal.getTypeCanal());
            stmt.setString(5, canal.getSlug());
            stmt.setTimestamp(6, canal.getDateCreation());
            stmt.setInt(7, canal.getIdCanal());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(int idCanal) {
        String sqlMessages = "DELETE FROM message WHERE \"idCanal\" = ?";
        String sqlMembres = "DELETE FROM membre_de WHERE \"idCanal\" = ?";
        String sqlCanal = "DELETE FROM canal WHERE \"idCanal\" = ?";

        try (Connection conn = dbManager.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmtMessages = conn.prepareStatement(sqlMessages);
                PreparedStatement stmtMembres = conn.prepareStatement(sqlMembres);
                PreparedStatement stmtCanal = conn.prepareStatement(sqlCanal)) {

                stmtMessages.setInt(1, idCanal);
                stmtMessages.executeUpdate();

                stmtMembres.setInt(1, idCanal);
                stmtMembres.executeUpdate();

                stmtCanal.setInt(1, idCanal);
                int rowsAffected = stmtCanal.executeUpdate();

                if (rowsAffected > 0) {
                    conn.commit();
                    return true;
                } else {
                    conn.rollback();
                    return false;
                }

            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Canal mapResultSetToCanal(ResultSet rs) throws SQLException {
        Canal canal = new Canal();
        canal.setIdCanal(rs.getInt("idCanal"));
        canal.setIdAdmin(rs.getInt("idAdmin"));
        canal.setNom(rs.getString("nom"));
        canal.setDescription(rs.getString("description"));
        canal.setTypeCanal(rs.getString("typeCanal"));
        canal.setSlug(rs.getString("slug"));
        canal.setDateCreation(rs.getTimestamp("dateCreation"));
        return canal;
    }
}
