package dao;

/**
 * FABRIQUE DAO - Point d'entree unique vers les acces aux donnees.
 *
 * Responsabilites :
 * - Fournir les implementations JDBC aux controleurs.
 * - Partager le meme DbConnectionManager entre toutes les DAO.
 * - Eviter que les servlets instancient directement les classes JDBC.
 */
public class DAOFactory {

    private static DAOFactory instance;

    private final DbConnectionManager dbManager;

    private DAOFactory() {
        this.dbManager = DbConnectionManager.getInstance();
    }

    /**
     * Retourne l'instance unique de la fabrique.
     */
    public static synchronized DAOFactory getInstance() {
        if (instance == null) {
            instance = new DAOFactory();
        }
        return instance;
    }

    /**
     * Fournit la DAO des utilisateurs.
     */
    public UtilisateurDAO getUtilisateurDAO() {
        return new UtilisateurDAOJDBC(dbManager);
    }

    /**
     * Fournit la DAO des canaux.
     */
    public CanalDAO getCanalDAO() {
        return new CanalDAOJDBC(dbManager);
    }

    /**
     * Fournit la DAO des messages.
     */
    public MessageDAO getMessageDAO() {
        return new MessageDAOJDBC(dbManager);
    }

    /**
     * Fournit la DAO de l'association utilisateurs-canaux.
     */
    public MembreCanalDAO getMembreCanalDAO() {
        return new MembreCanalDAOJDBC(dbManager);
    }
}
