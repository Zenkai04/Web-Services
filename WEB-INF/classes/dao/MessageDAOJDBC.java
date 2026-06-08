package dao;

import dto.Message;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MessageDAOJDBC implements MessageDAO {

    private final DbConnectionManager dbManager;

    public MessageDAOJDBC(DbConnectionManager dbManager) {
        this.dbManager = dbManager;
    }
    
    @Override
    public List<Message> findAll() {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM message";
        try (Connection conn = dbManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                messages.add(mapResultSetToMessage(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    @Override
    public Message findById(int idMessage) {
        Message message = null;
        String sql = "SELECT * FROM message WHERE \"idMessage\" = ?";
        try (Connection conn = dbManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idMessage);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    message = mapResultSetToMessage(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return message;
    }

    @Override
    public List<Message> findByCanal(int idCanal) {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM message WHERE \"idCanal\" = ?";
        try (Connection conn = dbManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCanal);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    messages.add(mapResultSetToMessage(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    @Override
    public boolean save(Message message) {
        String sql = "INSERT INTO message (\"idUtilisateur\", \"idCanal\", contenu, \"dateCreation\", \"dateModification\") "
                + "VALUES (?, ?, ?, COALESCE(?, CURRENT_TIMESTAMP), ?)";
        try (Connection conn = dbManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, message.getIdUtilisateur());
            stmt.setInt(2, message.getIdCanal());
            stmt.setString(3, message.getContenu());
            stmt.setTimestamp(4, message.getDateCreation());
            stmt.setTimestamp(5, message.getDateModification());
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        message.setIdMessage(keys.getInt(1));
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
    public boolean update(Message message) {
        String sql = "UPDATE message SET contenu = ?, \"dateModification\" = ? WHERE \"idMessage\" = ?";
        try (Connection conn = dbManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, message.getContenu());
            stmt.setTimestamp(2, message.getDateModification());
            stmt.setInt(3, message.getIdMessage());
            int rowsAffected = stmt.executeUpdate();    
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(int idMessage) {
        String sql = "DELETE FROM message WHERE \"idMessage\" = ?";
        try (Connection conn = dbManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idMessage);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Message mapResultSetToMessage(ResultSet rs) throws SQLException {
        Message message = new Message();
        message.setIdMessage(rs.getInt("idMessage"));
        message.setIdUtilisateur(rs.getInt("idUtilisateur"));
        message.setIdCanal(rs.getInt("idCanal"));
        message.setContenu(rs.getString("contenu"));
        message.setDateCreation(rs.getTimestamp("dateCreation"));
        message.setDateModification(rs.getTimestamp("dateModification"));
        return message;
    }
}
