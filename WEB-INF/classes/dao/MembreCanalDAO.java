package dao;

import dto.Utilisateur;
import dto.Canal;
import java.util.List;

public interface MembreCanalDAO {

    List<Utilisateur> findByCanal(int idCanal);

    List<Canal> findByUtilisateur(int idUtilisateur);

    boolean isMembre(int idUtilisateur, int idCanal);

    boolean addMembre(int idUtilisateur, int idCanal);

    boolean removeMembre(int idUtilisateur, int idCanal);
}