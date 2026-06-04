package dao;

import dto.Utilisateur;
import java.util.List;

public interface UtilisateurDAO {

    List<Utilisateur> findAll();

    Utilisateur findById(int idUtilisateur);

    Utilisateur findByPseudo(String pseudo);

    Utilisateur findByEmail(String email);

    boolean save(Utilisateur utilisateur);

    boolean update(Utilisateur utilisateur);

    boolean delete(int idUtilisateur);
}