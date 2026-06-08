package dao;

import dto.Canal;
import dto.Utilisateur;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MembreCanalDAOJDBC implements MembreCanalDAO {

    private final DbConnectionManager dbManager;

    public MembreCanalDAOJDBC(DbConnectionManager dbManager) {
        this.dbManager = dbManager;
    }

    @Override
    public List<Utilisateur> findByCanal(int idCanal) {
        String sql = "SELECT u.* FROM utilisateur u JOIN membre_de m ON u.idUtilisateur = m.idUtilisateur WHERE m.idCanal = ?";
        List<Utilisateur> utilisateurs = new ArrayList<>();

        try (Connection conn = dbManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCanal);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    utilisateurs.add(mapResultSetToUtilisateur(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return utilisateurs;
    }

    @Override
    public List<Canal> findByUtilisateur(int idUtilisateur) {
        String sql = "SELECT c.* FROM canal c JOIN membre_de m ON c.idCanal = m.idCanal WHERE m.idUtilisateur = ?";
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

    @Override
    public boolean isMembre(int idUtilisateur, int idCanal) {
        String sql = "SELECT 1 FROM membre_de WHERE idUtilisateur = ? AND idCanal = ?";

        try (Connection conn = dbManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUtilisateur);
            stmt.setInt(2, idCanal);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean addMembre(int idUtilisateur, int idCanal) {
        String sql = "INSERT INTO membre_de (idUtilisateur, idCanal) VALUES (?, ?)";

        try (Connection conn = dbManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUtilisateur);
            stmt.setInt(2, idCanal);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean removeMembre(int idUtilisateur, int idCanal) {
        String sql = "DELETE FROM membre_de WHERE idUtilisateur = ? AND idCanal = ?";

        try (Connection conn = dbManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUtilisateur);
            stmt.setInt(2, idCanal);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private Utilisateur mapResultSetToUtilisateur(ResultSet rs) throws SQLException {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setIdUtilisateur(rs.getInt("idUtilisateur"));
        utilisateur.setPseudo(rs.getString("pseudo"));
        utilisateur.setEmail(rs.getString("email"));
        utilisateur.setMotDePasseHash(rs.getString("motDePasseHash"));
        utilisateur.setDateCreation(rs.getTimestamp("dateCreation"));
        return utilisateur;
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
