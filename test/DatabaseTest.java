import controllers.security.OcrPermission;
import controllers.security.OcrRole;
import modules.database.UserController;
import modules.database.entities.Country;
import modules.database.entities.CountryImpl;
import modules.database.entities.User;
import modules.database.factory.SimpleUserFactory;
import org.junit.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.rules.ExpectedException;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.*;
import play.test.*;
import play.libs.F.*;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static play.test.Helpers.*;

/**
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
    }

    /**
     * delete user after test
     */
    @After
    public void delete(){
        JPA.withTransaction(() -> controller.deleteObject(user));
    }

    /**
     * check database if persistation with @simpleuserfactory is working
     */
    public void checkCreate(){
        System.out.println("email selection from object: ");
        assertEquals(user.geteMail(), controller.selectUserFromMail(user).geteMail());
        System.out.println("email selection from string: ");
        assertEquals(user.geteMail(), controller.selectUserFromMail(user.geteMail()).geteMail());
    }

    /**
     * check if change of password is working
     */
    @Test
    public void checkPasswordChange(){
        JPA.withTransaction(() -> {
            user.setPassword("test2");
        });
        assertEquals(user.getPassword(), controller.selectUserFromMail(user).getPassword());
    }

    /**
     *check user creation
     */
    @Test
    public void checkUserCreation(){
        JPA.withTransaction(() -> {
            controller.persistUser(user);
        });

        checkCreate();
    }

    /**
     * check user creation with factory
     */
    @Test
    private void checkCreateUserFromFactory() {
        new SimpleUserFactory().setCountry(user.getCountry().getCountry())
                .setEmail(user.geteMail())
                .setPassword(user.getPassword())
                .persist();
        checkCreate();
    }

    @Rule public ExpectedException thrown= ExpectedException.none();

    /**
     * check nullpointer when false selection
     */
    @Test
    public void checkFalseSelection(){
        thrown.expect(NullPointerException.class );
        thrown.expectMessage("selection must be unique database object parameter");


    }
}
