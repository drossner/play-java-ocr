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

    LdapController lc;
    User temp;

    @Before
    public void setupTest(){
        lc = new LdapController();
        temp = new LDAPUserCreator().getUser();
    }

    @Test
    public void createUserTest() {
        assertTrue(lc.insert(temp));
    }

    @Test
    public void searchUserTest(){
        assertTrue(lc.searchUser(temp.getCmsAccount()));
    }

    @Test
    public void editUserTest(){
        //change an Attribute before edit an user
        temp.setCmsPassword("testtest");
        assertTrue(lc.edit(temp));
    }

    @Test
    public void deleteUserTest(){
        assertTrue(lc.delete(temp));
    }

    public class LDAPUserCreator{

        public User getUser(){
            User rc = new User();
            rc.seteMail("test@test.de");
            rc.setCmsPassword("test");
            rc.setCmsAccount("test");

            return rc;
        }

    }

}
