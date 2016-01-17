import modules.database.entities.User;
import modules.ldap.LdapController;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.assertTrue;

/**
 * Created by Benedikt Linke on 23.11.15.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LdapTest {

    LdapController lc;
    User temp;

    @Before
    public void SetupTest(){
        lc = new LdapController();
        temp = new LDAPUserCreator().getUser();
    }

    @Test
    public void bCreateUserTest() {
        assertTrue(lc.insert(temp));
    }

    @Test
    public void cSearchUserTest(){
        assertTrue(lc.searchUser(temp.getCmsAccount()));
    }

    @Test
    public void dEditUserTest(){
        //change an Attribute before edit an user
        temp.setCmsPassword("testtest");
        assertTrue(lc.edit(temp));
    }

    @Test
    public void eDeleteUserTest(){
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
