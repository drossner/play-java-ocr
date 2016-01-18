import modules.database.UserController;
import modules.database.entities.Country;
import modules.database.entities.CountryImpl;
import modules.database.entities.User;
import modules.database.factory.SimpleUserFactory;
import org.junit.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.*;
import play.test.*;
import play.libs.F.*;

import static org.junit.Assert.assertEquals;
import static play.test.Helpers.*;
import static org.mockito.*;

/**
 * Created by FRudi on 20.11.2015.
 */
public class DatabaseTest {

    User user = Mockito.mock(User.class);
    UserController controller = new UserController();

    @Before
    public void setup(){
        Country c = new Country();
        c.setCountry(CountryImpl.GERMAN);

        user.seteMail("test@test.de");
        user.setCountry(c);
        user.setPassword("test");
    }

    @Test
    public void createUserTest(){
        running(fakeApplication(), () -> {
            JPA.withTransaction(this::create);
        });
    }

    private void create() {
        new SimpleUserFactory().setCountry(user.getCountry().getCountry())
                .setEmail(user.geteMail())
                .setPassword(user.getPassword())
                .persist();

        assertEquals(user.geteMail(), controller.selectUserFromMail(user).geteMail());
    }
}
