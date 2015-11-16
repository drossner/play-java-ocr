package database;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * code from: https://github.com/ldaume/play-hibernate/blob/master/build.sbt
 *
 * Created by FRudi on 17.11.2015.
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
