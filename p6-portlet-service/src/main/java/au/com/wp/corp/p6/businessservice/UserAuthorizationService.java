package au.com.wp.corp.p6.businessservice;

import java.util.List;

import au.com.wp.corp.p6.dto.UserAuthorizationDTO;

public interface UserAuthorizationService {
	
	public List<UserAuthorizationDTO> getAccess(String roleName);

}
