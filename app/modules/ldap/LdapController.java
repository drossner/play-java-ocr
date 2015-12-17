package modules.ldap;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Hashtable;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Singleton;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import modules.database.entities.User;
import play.Logger;

/**
 * Created by Benedikt Linke on 23.11.15.
 */
@Singleton
public class LdapController {

    public static final String INITIAL_CONTEXT_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
    public static final String PROVIDER_URL = "ldap://v22015042759824376.yourvserver.net:389/";
    public static final String BASE_DN = "dc=somuchocr,dc=iisys,dc=net";
    public static final String SECURITY_PRINCIPAL = "cn=admin";
    public static final String SECURITY_CREDENTIALS = "slapd101";

    private Hashtable<String, String> env = new Hashtable<String, String>();

    public LdapController() {
        try {
            env.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
            env.put(Context.PROVIDER_URL, PROVIDER_URL + BASE_DN);
            env.put(Context.SECURITY_PRINCIPAL, SECURITY_PRINCIPAL +", "+ BASE_DN);
            env.put(Context.SECURITY_CREDENTIALS, SECURITY_CREDENTIALS);
        } catch (Exception e) {
            Logger.info("Faild", e);
        }
    }


    public boolean insert(User user) {
        try {
            DirContext dctx = new InitialDirContext(env);
            Attributes matchAttrs = new BasicAttributes(true);
            matchAttrs.put(new BasicAttribute("uid", user.getCmsAccount()));
            matchAttrs.put(new BasicAttribute("cn", user.getCmsAccount()));
            matchAttrs.put(new BasicAttribute("sn", user.getCmsAccount()));
            matchAttrs.put(new BasicAttribute("givenname", user.getCmsAccount()));
            matchAttrs.put(new BasicAttribute("mail", user.geteMail()));
            matchAttrs.put(new BasicAttribute("userpassword", user.getCmsPassword()));
            matchAttrs.put(new BasicAttribute("objectclass", "top"));
            matchAttrs.put(new BasicAttribute("objectclass", "person"));
            matchAttrs.put(new BasicAttribute("objectclass", "organizationalPerson"));
            matchAttrs.put(new BasicAttribute("objectclass", "inetorgperson"));

            String name = "cn=" + user.getCmsAccount() + ",ou=groups";
            InitialDirContext iniDirContext = (InitialDirContext) dctx;
            iniDirContext.bind(name, dctx, matchAttrs);

            return true;
        } catch (Exception e) {
            Logger.info("Could not insert a user", e);

            return false;
        }
    }

    public boolean edit(User user) {
        try {

            DirContext ctx = new InitialDirContext(env);
            ModificationItem[] mods = new ModificationItem[1];
            Attribute mod0 = new BasicAttribute("userpassword", user.getCmsPassword());
            mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, mod0);

            ctx.modifyAttributes("cn=" + user.getCmsAccount() + ",ou=groups", mods);

            Logger.info("success editing user: "+user.getCmsAccount());
            return true;
        } catch (Exception e) {
            Logger.info("Could not edit user", e);
            return false;
        }
    }

    public boolean delete(User user) {
        try {

            DirContext ctx = new InitialDirContext(env);
            ctx.destroySubcontext("cn=" + user.getCmsAccount() + ",ou=groups");
            Logger.info("success deleting user:  "+user.getCmsAccount());
            return true;
        } catch (Exception e) {
            Logger.info("Could not delete User", e);
            return false;
        }
    }

    public boolean searchUser(String username) {
        try {

            DirContext ctx = new InitialDirContext(env);
            String base = "ou=groups";

            SearchControls sc = new SearchControls();
            sc.setSearchScope(SearchControls.SUBTREE_SCOPE);

            String filter = "(&(objectclass=person)(cn="+username+"))";

            NamingEnumeration results = ctx.search(base, filter, sc);


            /*while (results.hasMore()) {
                SearchResult sr = (SearchResult) results.next();
                Attributes attrs = sr.getAttributes();

                Attribute attr = attrs.get("cn");
                if(attr != null)
                    Logger.info("record found "+attr.get());
            }*/
            ctx.close();
            return results.hasMore();
        } catch (Exception e) {
            Logger.info("record not found",e);
            return false;
        }
    }



    private String digestMd5(final String password) {
        String base64;
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            try {
                digest.update(password.getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String test = new String(digest.digest());
            Logger.info("Test: " + test);
            Base64.Encoder encoder = Base64.getEncoder();
            base64 = encoder.encodeToString(digest.digest());

        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        Logger.info(base64);
        return "{MD5}" + base64;
    }

    private String encryptLdapPassword(String password) throws Exception {
        byte[] keyData = (password).getBytes();
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyData, "Blowfish");
        Cipher cipher = Cipher.getInstance("Blowfish");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        byte[] hasil = cipher.doFinal(password.getBytes());
        String rc = "{CRYPT}"+ Base64.getEncoder().encodeToString(hasil);
        rc= "{MD5}0lW/bn6RBGiEWKTO/rAgyg==";
        Logger.info(rc);
        return rc;
    }

}