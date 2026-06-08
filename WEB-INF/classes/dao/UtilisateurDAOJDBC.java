package dao;

import dto.Utilisateur;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;

public class UtilisateurDAOJDBC implements UtilisateurDAO {

    private final DbConnectionManager dbManager;

    public UtilisateurDAOJDBC(DbConnectionManager dbManager) {
        this.dbManager = dbManager;
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

    @Override
    public List<Utilisateur> findAll() {
        String sql = "SELECT * FROM utilisateur";
        List<Utilisateur> utilisateurs = new ArrayList<>();

        try (Connection conn = dbManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                utilisateurs.add(mapResultSetToUtilisateur(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return utilisateurs;
    }

    @Override
    public Utilisateur findById(int idUtilisateur) {
        String sql = "SELECT * FROM utilisateur WHERE \"idUtilisateur\" = ?";

        try (Connection conn = dbManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUtilisateur);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUtilisateur(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Utilisateur findByPseudo(String pseudo) {
        String sql = "SELECT * FROM utilisateur WHERE pseudo = ?";
        try (Connection conn = dbManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, pseudo);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUtilisateur(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Utilisateur findByEmail(String email) {
        String sql = "SELECT * FROM utilisateur WHERE email = ?";
        try (Connection conn = dbManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUtilisateur(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean save(Utilisateur utilisateur) {
        String sql = "INSERT INTO utilisateur (pseudo, email, \"motDePasseHash\", \"dateCreation\") "
                + "VALUES (?, ?, ?, COALESCE(?, CURRENT_TIMESTAMP))";
        try (Connection conn = dbManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, utilisateur.getPseudo());
            stmt.setString(2, utilisateur.getEmail());
            stmt.setString(3, utilisateur.getMotDePasseHash());
            stmt.setTimestamp(4, utilisateur.getDateCreation());
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        utilisateur.setIdUtilisateur(keys.getInt(1));
                    }
                }
            }

            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(Utilisateur utilisateur) {
        String sql = "UPDATE utilisateur SET pseudo = ?, email = ?, \"motDePasseHash\" = ?, "
                + "\"dateCreation\" = COALESCE(?, \"dateCreation\") WHERE \"idUtilisateur\" = ?";
        try (Connection conn = dbManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, utilisateur.getPseudo());
            stmt.setString(2, utilisateur.getEmail());
            stmt.setString(3, utilisateur.getMotDePasseHash());
            stmt.setTimestamp(4, utilisateur.getDateCreation());
            stmt.setInt(5, utilisateur.getIdUtilisateur());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(int idUtilisateur) {
        String sql = "DELETE FROM utilisateur WHERE \"idUtilisateur\" = ?";
        try (Connection conn = dbManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUtilisateur);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }    
}
