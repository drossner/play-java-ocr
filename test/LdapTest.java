import modules.database.entities.Country;
import modules.database.entities.User;
import modules.ldap.LdapController;
import org.junit.Before;
import org.junit.Test;
import play.Logger;

import static org.junit.Assert.assertTrue;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

/**
 * Created by Benedikt Linke on 23.11.15.
 */
public class LdapTest {

    /*
    LdapController lc;

    @Before
    public void setupTest(){
        lc = new LdapController();
    }

    @Test
    public void createUserTest() {
        User temp = new LDAPUserCreator().getUser();

        lc.insert(temp);
        lc.searchUser(temp);
        temp.setCmsPassword("testtest");
        lc.edit(temp);
        assertTrue(lc.delete(temp));
    }

    public class LDAPUserCreator{

        public User getUser(){
            User rc = new User();

            Country c = new Country();
            c.setName("Deutscheland");

            rc.seteMail("test@test.de");
            rc.setCountry(c);
            rc.setCmsPassword("test");
            rc.setCmsAccount("test");

            return rc;
        }

    }
    */
}
