package dao;

import dto.Canal;
import java.util.List;

public interface CanalDAO {

    List<Canal> findAll();

    Canal findById(int idCanal);

    Canal findBySlug(String slug);

    boolean save(Canal canal);

    boolean update(Canal canal);
}
