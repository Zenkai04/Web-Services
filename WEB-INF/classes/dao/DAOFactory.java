package dao;

public class DAOFactory {

    private static DAOFactory instance;

    private final DbConnectionManager dbManager;

    private DAOFactory() {
        this.dbManager = DbConnectionManager.getInstance();
    }

    public static synchronized DAOFactory getInstance() {
        if (instance == null) {
            instance = new DAOFactory();
        }
        return instance;
    }

    public UtilisateurDAO getUtilisateurDAO() {
        return new UtilisateurDAOJDBC(dbManager);
    }

    public CanalDAO getCanalDAO() {
        return new CanalDAOJDBC(dbManager);
    }

    public MessageDAO getMessageDAO() {
        return new MessageDAOJDBC(dbManager);
    }

    public MembreCanalDAO getMembreCanalDAO() {
        return new MembreCanalDAOJDBC(dbManager);
    }
}