/**
 * 
 */
package au.com.wp.corp.p6.scheduling.businessservice.impl;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import au.com.wp.corp.p6.ad.connector.ActiveDirectoryConnector;
import au.com.wp.corp.p6.scheduling.businessservice.UserAuthorizationService;
import au.com.wp.corp.p6.scheduling.dao.FunctionAccessDAO;
import au.com.wp.corp.p6.scheduling.dto.UserAuthorizationDTO;
import au.com.wp.corp.p6.scheduling.model.FunctionAccess;

/**
 * @author N039603
 *
 */
@Service
@PropertySource("file:/${properties.dir}/p6portal.properties")
public class UserAuthorizationServiceImpl implements UserAuthorizationService {

	private static final Logger logger = LoggerFactory.getLogger(UserAuthorizationServiceImpl.class);

	@Value("${LDAP_HOST_NAME}")
	private String ldapHostName;

	@Value("${LDAP_USER_PRINCIPAL}")
	private String ldapUserPrincipal;

	@Value("${LDAP_USER_CREDENTIAL}")
	private String ldapUserCredential;
	
	@Autowired
	private FunctionAccessDAO functionAccessDAO;


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * au.com.wp.corp.p6.businessservice.UserAuthorizationService#getAccess(java
	 * .lang.String)
	 */
	@Transactional
	@Override
	public List<UserAuthorizationDTO> getAccess(List<String> roleNames) {
		logger.debug("Input role name>>>{}", roleNames);
		List<FunctionAccess> accesses = functionAccessDAO.getAccess(roleNames);
		List<UserAuthorizationDTO> userAuthorizationDTOs = new ArrayList<UserAuthorizationDTO>();
		if (accesses != null) {
			logger.debug("Number of functions returned>>>{}", accesses.size());
			for (FunctionAccess access : accesses) {
				UserAuthorizationDTO userAuthorizationDTO = new UserAuthorizationDTO();
				userAuthorizationDTO.setFunctionName(access.getPortalFunction().getFuncNam());
				if (access.getWriteFlg().equalsIgnoreCase("Y")) {
					userAuthorizationDTO.setAccess(Boolean.TRUE);
				} else {
					userAuthorizationDTO.setAccess(Boolean.FALSE);
				}
				userAuthorizationDTOs.add(userAuthorizationDTO);
			}
		}
		return userAuthorizationDTOs;
	}

	@Override
	public String getUserName(String nId) {
		String userName = nId ;
		ActiveDirectoryConnector adConnector = new ActiveDirectoryConnector(ldapUserPrincipal, ldapUserCredential, ldapHostName);
		try {
			NamingEnumeration<SearchResult> result = adConnector.searchUser(nId,
					ActiveDirectoryConnector.SEARCH_BY_USER_NAME, null);
			if (result.hasMore()) {
				SearchResult rs = (SearchResult) result.next();
				Attributes attrs = rs.getAttributes();
				String temp = attrs.get("givenname").toString();
				userName = temp.substring(temp.indexOf(":") + 1);
			}
		} catch (NamingException e) {

		} finally {

			// Closing LDAP Connection
			adConnector.closeLdapConnection();
		}
		
		return userName;
	}

}
