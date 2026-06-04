package dao;

import dto.Message;
import java.util.List;

public interface MessageDAO {

    List<Message> findAll();

    Message findById(int idMessage);

    List<Message> findByCanal(int idCanal);

    boolean save(Message message);

    boolean update(Message message);

    boolean delete(int idMessage);
}
