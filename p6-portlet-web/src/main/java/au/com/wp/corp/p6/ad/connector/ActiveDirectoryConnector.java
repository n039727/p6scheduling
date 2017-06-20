/**
 * 
 */
package au.com.wp.corp.p6.ad.connector;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author n039126
 *
 */
public class ActiveDirectoryConnector {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(ActiveDirectoryConnector.class);

	// required private variables
	private Properties properties;
	private DirContext dirContext;
	private SearchControls searchCtls;
	private String[] returnAttributes = { "sAMAccountName", "givenName", "cn", "mail" };
	private String domainBase;
	private String baseFilter = "(&((&(objectCategory=Person)(objectClass=User)))";
	
	public static final String SEARCH_BY_USER_NAME="username";
	
	public static final String SEARCH_BY_EMAIL="email";

	/**
	 * constructor with parameter for initializing a LDAP context
	 * 
	 * @param username
	 *            a {@link java.lang.String} object - username to establish a
	 *            LDAP connection
	 * @param password
	 *            a {@link java.lang.String} object - password to establish a
	 *            LDAP connection
	 * @param domainController
	 *            a {@link java.lang.String} object - domain controller name for
	 *            LDAP connection
	 */
	public ActiveDirectoryConnector(String username, String password, String domainController) {
		properties = new Properties();
		properties.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		properties.put(Context.PROVIDER_URL, "LDAP://" + domainController);
		properties.put(Context.SECURITY_PRINCIPAL, username + "@" + domainController);
		properties.put(Context.SECURITY_CREDENTIALS, password);

		// initializing active directory LDAP connection
		try {
			dirContext = new InitialDirContext(properties);
		} catch (NamingException e) {
			logger.error("an error occurs while getting initial context  ", e );
		}
		
		logger.debug("Active directory connector created: {}", dirContext);

		// default domain base for search
		domainBase = getDomainBase(domainController);

		// initializing search controls
		searchCtls = new SearchControls();
		searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		searchCtls.setReturningAttributes(returnAttributes);
	}

	/**
	 * search the Active directory by username/email id for given search base
	 * 
	 * @param searchValue
	 *            a {@link java.lang.String} object - search value used for AD
	 *            search for eg. username or email
	 * @param searchBy
	 *            a {@link java.lang.String} object - scope of search by
	 *            username or by email id
	 * @param searchBase
	 *            a {@link java.lang.String} object - search base value for
	 *            scope tree for eg. DC=myjeeva,DC=com
	 * @return search result a {@link javax.naming.NamingEnumeration} object -
	 *         active directory search result
	 * @throws NamingException
	 */
	public NamingEnumeration<SearchResult> searchUser(String searchValue, String searchBy, String searchBase)
			throws NamingException {
		String filter = getFilter(searchValue, searchBy);
		String base = (null == searchBase) ? domainBase : getDomainBase(searchBase); 
		NamingEnumeration<SearchResult> result = this.dirContext.search(base, filter, this.searchCtls);
		
		logger.debug("User searched: {}" , result);
		return result;
	}

	/**
	 * closes the LDAP connection with Domain controller
	 */
	public void closeLdapConnection() {
		try {
			if (dirContext != null)
				dirContext.close();
		} catch (NamingException e) {
			logger.error("an error occurs while closing ldap connectiont  ", e );
		}
	}

	/**
	 * active directory filter string value
	 * 
	 * @param searchValue
	 *            a {@link java.lang.String} object - search value of
	 *            username/email id for active directory
	 * @param searchBy
	 *            a {@link java.lang.String} object - scope of search by
	 *            username or email id
	 * @return a {@link java.lang.String} object - filter string
	 */
	private String getFilter(String searchValue, String searchBy) {
		String filter = this.baseFilter;
		if (searchBy.equals(SEARCH_BY_EMAIL)) {
			filter += "(mail=" + searchValue + "))";
		} else if (searchBy.equals(SEARCH_BY_USER_NAME)) {
			filter += "(samaccountname=" + searchValue + "))";
		}
		logger.debug("Filter: {}", filter);
		return filter;
	}

	/**
	 * creating a domain base value from domain controller name
	 * 
	 * @param base
	 *            a {@link java.lang.String} object - name of the domain
	 *            controller
	 * @return a {@link java.lang.String} object - base name for eg.
	 *         DC=myjeeva,DC=com
	 */
	private static String getDomainBase(String base) {
		char[] namePair = base.toUpperCase().toCharArray();
		String dn = "DC=";
		for (int i = 0; i < namePair.length; i++) {
			if (namePair[i] == '.') {
				dn += ",DC=" + namePair[++i];
			} else {
				dn += namePair[i];
			}
		}
		logger.info("dn :  {} ", dn);
		return dn;
	}

}
