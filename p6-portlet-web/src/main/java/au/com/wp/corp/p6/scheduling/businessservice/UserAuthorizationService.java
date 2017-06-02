package au.com.wp.corp.p6.scheduling.businessservice;

import java.util.List;

import au.com.wp.corp.p6.scheduling.dto.UserAuthorizationDTO;

public interface UserAuthorizationService {
	
	public List<UserAuthorizationDTO> getAccess(List<String> roleNames);

}
