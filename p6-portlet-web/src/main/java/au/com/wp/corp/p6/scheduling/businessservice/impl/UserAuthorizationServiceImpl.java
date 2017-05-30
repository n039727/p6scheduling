/**
 * 
 */
package au.com.wp.corp.p6.scheduling.businessservice.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import au.com.wp.corp.p6.scheduling.auth.UserRoleExtractor;
import au.com.wp.corp.p6.scheduling.businessservice.UserAuthorizationService;
import au.com.wp.corp.p6.scheduling.dao.FunctionAccessDAO;
import au.com.wp.corp.p6.scheduling.dto.UserAuthorizationDTO;
import au.com.wp.corp.p6.scheduling.model.FunctionAccess;

/**
 * @author N039603
 *
 */
@Service
public class UserAuthorizationServiceImpl implements UserAuthorizationService {
	
	private static final Logger logger = LoggerFactory.getLogger(UserAuthorizationServiceImpl.class);

	@Autowired
	private FunctionAccessDAO functionAccessDAO;
	
	@Autowired
	private UserRoleExtractor userRoleExtractor;

	/* (non-Javadoc)
	 * @see au.com.wp.corp.p6.businessservice.UserAuthorizationService#getAccess(java.lang.String)
	 */
	@Transactional
	@Override
	public List<UserAuthorizationDTO> getAccess(String userName) {
		logger.debug("User name : {} ", userName);
		String roleName = userRoleExtractor.extract(userName);
		logger.debug("Input role name>>>{}", roleName);
		List<FunctionAccess> accesses = functionAccessDAO.getAccess(roleName);
		List<UserAuthorizationDTO> userAuthorizationDTOs = new ArrayList<UserAuthorizationDTO>();
		if(accesses != null){
			logger.debug("Number of functions returned>>>{}", accesses.size());
			for(FunctionAccess access:accesses){
				UserAuthorizationDTO userAuthorizationDTO = new UserAuthorizationDTO();
				userAuthorizationDTO.setFunctionName(access.getPortalFunction().getFuncNam());
				if(access.getWriteFlg().equalsIgnoreCase("Y")){
					userAuthorizationDTO.setAccess(Boolean.TRUE);
				}
				else{
					userAuthorizationDTO.setAccess(Boolean.FALSE);
				}
				userAuthorizationDTOs.add(userAuthorizationDTO);
			}
		}
		return userAuthorizationDTOs;
	}

}
