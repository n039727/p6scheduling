/**
 * 
 */
package au.com.wp.corp.p6.scheduling.naming;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;

import au.com.wp.corp.p6.ad.connector.ActiveDirectoryConnector;

/**
 * @author N039126
 *
 */
public class ActiveDirectoryTest {

	/**
	 * @param args
	 * @throws NamingException 
	 */
	public static void main(String[] args) throws NamingException {

		ActiveDirectoryConnector connector = new ActiveDirectoryConnector("n039126","XXXXXXX","ads.westernpower.com.au");
		NamingEnumeration<SearchResult> result = connector.searchUser("n039049", "username", null);
		
		if(result.hasMore()) {
			SearchResult rs= (SearchResult)result.next();
			Attributes attrs = rs.getAttributes();
			String temp = attrs.get("samaccountname").toString();
			System.out.println("Username	: " + temp.substring(temp.indexOf(":")+1));
			temp = attrs.get("givenname").toString();
			System.out.println("Name         : " + temp.substring(temp.indexOf(":")+1));
			temp = attrs.get("mail").toString();
			System.out.println("Email ID	: " + temp.substring(temp.indexOf(":")+1));
			temp = attrs.get("cn").toString();
			System.out.println("Display Name : " + temp.substring(temp.indexOf(":")+1) + "\n\n"); 
		} else  {
			System.out.println("No search result found!");
		}

		//Closing LDAP Connection
		connector.closeLdapConnection();
	}

}
