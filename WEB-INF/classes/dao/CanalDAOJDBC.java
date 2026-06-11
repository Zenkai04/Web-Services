package dao;

import dto.Canal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * IMPLEMENTATION JDBC - Persistance des canaux.
 *
 * Responsabilites :
 * - Executer les requetes SQL associees aux canaux.
 * - Transformer les lignes SQL en DTO Canal.
 * - Retourner des booleens simples aux controleurs pour indiquer le succes.
 */
public class CanalDAOJDBC implements CanalDAO {

    private final DbConnectionManager dbManager;

    /**
     * Recoit le gestionnaire de connexion partage par la DAOFactory.
     */
    public CanalDAOJDBC(DbConnectionManager dbManager) {
        this.dbManager = dbManager;
    }

    /**
     * Charge tous les canaux de la base, sans appliquer les regles d'acces REST.
     */
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

    /**
     * Charge les canaux relies a un utilisateur par la table d'association membre_de.
     */
    @Override
    public List<Canal> findByUtilisateurId(int idUtilisateur) {
        String sql = "SELECT c.* FROM canal c "
                + "JOIN membre_de m ON c.\"idCanal\" = m.\"idCanal\" "
                + "WHERE m.\"idUtilisateur\" = ?";
        List<Canal> canaux = new ArrayList<>();
        try (Connection conn = dbManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUtilisateur);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    canaux.add(mapResultSetToCanal(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return canaux;
    }

    /**
     * Recherche un canal unique a partir de son identifiant.
     */
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

    /**
     * Recherche un canal unique a partir de son slug.
     */
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

    /**
     * Insere un canal puis recupere la cle primaire generee.
     */
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

    /**
     * Met a jour les champs modifiables du canal.
     */
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

    /**
     * Convertit une ligne SQL en objet Canal exploitable par l'API.
     */
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
