package modules.ldap;

import java.util.Hashtable;
import java.security.MessageDigest;
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
import sun.misc.BASE64Encoder;

/**
 * Created by Benedikt Linke on 23.11.15.
 */
public class LdapController {

    public static final String INITIAL_CONTEXT_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";


    public static final String PROVIDER_URL = "ldap://v22015042759824376.yourvserver.net:389/";
    public static final String BASE_DN = "dc=somuchocr,dc=iisys,dc=de";
    public static final String SECURITY_PRINCIPAL = "cn=manager";
    public static final String SECURITY_CREDENTIALS = "slapd101&";

    private Hashtable<String, String> env = new Hashtable<String, String>();

    public LdapController() {
        try {
            env.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
            env.put(Context.PROVIDER_URL, PROVIDER_URL + BASE_DN);
            env.put(Context.SECURITY_PRINCIPAL, SECURITY_PRINCIPAL +", "+ BASE_DN);
            env.put(Context.SECURITY_CREDENTIALS, SECURITY_CREDENTIALS);
        } catch (Exception e) {
            System.out.println("Faild");
        }
    }


    public boolean insert(User user) {
        try {
            DirContext dctx = new InitialDirContext(env);
            Attributes matchAttrs = new BasicAttributes(true);
            matchAttrs.put(new BasicAttribute("uid", user.getFirstName()));
            matchAttrs.put(new BasicAttribute("cn", user.getName()));
            matchAttrs.put(new BasicAttribute("sn", user.getLastName()));
            matchAttrs.put(new BasicAttribute("givenname", user.getFirstName()));
            matchAttrs.put(new BasicAttribute("mail", user.getEmail()));
            matchAttrs.put(new BasicAttribute("userpassword", encryptLdapPassword("MD5", user.getPassword())));
            matchAttrs.put(new BasicAttribute("objectclass", "top"));
            matchAttrs.put(new BasicAttribute("objectclass", "person"));
            matchAttrs.put(new BasicAttribute("objectclass", "organizationalPerson"));
            matchAttrs.put(new BasicAttribute("objectclass", "inetorgperson"));

            String name = "cn=" + user.getName() + ",ou=groups";
            InitialDirContext iniDirContext = (InitialDirContext) dctx;
            iniDirContext.bind(name, dctx, matchAttrs);

            return true;
        } catch (Exception e) {
            System.out.println("Could not insert a Person");

            return false;
        }
    }

    public boolean edit(User user) {
        try {

            DirContext ctx = new InitialDirContext(env);
            ModificationItem[] mods = new ModificationItem[1];
            Attribute mod0 = new BasicAttribute("userpassword", encryptLdapPassword("SHA", user.getPassword()));
            mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, mod0);

            ctx.modifyAttributes("cn=" + user.getName() + ",ou=group", mods);

            System.out.println("success editing "+user.getName());
            return true;
        } catch (Exception e) {
            System.out.println("Could not edit Person");
            return false;
        }
    }

    public boolean delete(User user) {
        try {

            DirContext ctx = new InitialDirContext(env);
            ctx.destroySubcontext("cn=" + user.getName() + ",ou=group");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean searchUser(User user) {
        try {

            DirContext ctx = new InitialDirContext(env);
            String base = "ou=groups";

            SearchControls sc = new SearchControls();
            sc.setSearchScope(SearchControls.SUBTREE_SCOPE);

            String filter = "(&(objectclass=person)(cn="+user.getName()+"))";

            NamingEnumeration results = ctx.search(base, filter, sc);


            while (results.hasMore()) {
                SearchResult sr = (SearchResult) results.next();
                Attributes attrs = sr.getAttributes();

                Attribute attr = attrs.get("cn");
                if(attr != null)
                    System.out.println("record found "+attr.get());
            }
            ctx.close();

            return true;
        } catch (Exception e) {
            System.out.println("record not found");
            return false;
        }
    }

    private String encryptLdapPassword(String algorithm, String _password) {
        String sEncrypted = _password;
        if ((_password != null) && (_password.length() > 0)) {
            boolean bMD5 = algorithm.equalsIgnoreCase("MD5");
            boolean bSHA = algorithm.equalsIgnoreCase("SHA")
                    || algorithm.equalsIgnoreCase("SHA1")
                    || algorithm.equalsIgnoreCase("SHA-1");
            if (bSHA || bMD5) {
                String sAlgorithm = "MD5";
                if (bSHA) {
                    sAlgorithm = "SHA";
                }
                try {
                    MessageDigest md = MessageDigest.getInstance(sAlgorithm);
                    md.update(_password.getBytes("UTF-8"));
                    sEncrypted = "{" + sAlgorithm + "}" + (new BASE64Encoder()).encode(md.digest());
                } catch (Exception e) {
                    sEncrypted = null;
                }
            }
        }
        return sEncrypted;
    }
}