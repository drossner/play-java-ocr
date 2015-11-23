import modules.database.entities.Country;
import modules.database.entities.User;
import org.junit.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import play.mvc.*;
import play.test.*;
import play.libs.F.*;

import static org.junit.Assert.assertEquals;
import static play.test.Helpers.*;

/**
 * Created by FRudi on 20.11.2015.
 */
public class DatabaseTest {

    @PersistenceContext
    EntityManager em;

    @Test
    public void createUserTest(){
        running(fakeApplication(), () -> {
            User temp = new DataCreator().getUser();
            em.persist(temp);

            assertEquals(em.find(User.class, temp.getId()).geteMail(), temp.geteMail());
        });
    }

    public class DataCreator{


        public User getUser(){
            User rc = new User();

            Country c = new Country();
            c.setName("Deutscheland");

            rc.seteMail("test@test.de");
            rc.setCountry(c);
            rc.setPassword("test");

            return rc;
        }

    }
}
