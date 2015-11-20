package modules.database;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * create user psql: http://stackoverflow.com/questions/10861260/how-to-create-user-for-a-db-in-postgresql
 *
 * magic config: https://groups.google.com/forum/#!topic/play-framework/xJsImshqJwM
 *
 * Created by florian on 17.11.15.
 */
public class Controller {

    private static Controller INSTANCE = null;
    private final EntityManager em;

    public static Controller getInstance(){
        if(INSTANCE == null){
            INSTANCE = new Controller();
        }

        return INSTANCE;
    }

    private Controller(){
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("ocr");
        em = emf.createEntityManager();
    }

}
