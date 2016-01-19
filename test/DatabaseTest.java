import modules.database.DatabaseController;
import modules.database.UserController;
import modules.database.entities.Country;
import modules.database.entities.CountryImpl;
import modules.database.entities.DomainObject;
import modules.database.entities.User;
import modules.database.factory.SimpleUserFactory;
import org.junit.*;
import org.junit.rules.ExpectedException;
import play.db.jpa.JPA;
import play.test.WithApplication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 *
 * Created by FRudi on 20.11.2015.
 */
public class DatabaseTest extends WithApplication {

    //User user = Mockito.mock(User.class);
    User user = new User();
    UserController controller = new UserController();

    /**
     * setting up test => insert user in database
     */
    @Before
    public void setup(){
        Country c = new Country();
        c.setCountry(CountryImpl.GERMAN);

        user.seteMail("test@test.de");
        user.setCountry(c);
        user.setPassword("test");

        createUserFromFactory();
    }

    /**
     * create user from factory
     */
    private void createUserFromFactory() {
        try {
            JPA.withTransaction(() -> new SimpleUserFactory().setCountry(user.getCountry().getCountry())
                    .setEmail(user.geteMail())
                    .setPassword(user.getPassword())
                    .persist());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    /**
     * check database if persistation with @simpleuserfactory is working
     */
    @Test
    public void checkCreateFromObject(){
        JPA.withTransaction(() -> assertEquals(user.geteMail(), controller.selectUserFromMail(user).geteMail()));
    }

    /**
     * check database if persistation with @simpleuserfactory is working
     */
    @Test
    public void checkCreateFromString(){
        JPA.withTransaction(() -> assertEquals(user.geteMail(), controller.selectUserFromMail(user.geteMail()).geteMail()));
    }

    /**
     * check if change of password is working
     */
    @Test
    public void checkPasswordChange(){
        user.setPassword("test2");
        JPA.withTransaction(() -> controller.selectUserFromMail(user).setPassword(user.getPassword()));

        JPA.withTransaction(() -> assertEquals(user.getPassword(), controller.selectUserFromMail(user).getPassword()));
    }


    @Rule public ExpectedException thrown= ExpectedException.none();
    /**
     * check nullpointer when false selection
     */
    @Test
    public void checkFalseSelection(){
        thrown.expect(RuntimeException.class );

        JPA.withTransaction(() -> {
            new TestController().selectEntity(User.class, true);
        });
    }

    public class TestController extends DatabaseController<DomainObject, Object>{

    }
}
