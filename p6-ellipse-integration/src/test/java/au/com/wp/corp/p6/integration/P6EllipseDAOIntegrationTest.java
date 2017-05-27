/**
 * 
 */
package au.com.wp.corp.p6.integration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import au.com.wp.corp.p6.dao.P6EllipseDAO;
import au.com.wp.corp.p6.dao.P6PortalDAO;
import au.com.wp.corp.p6.dto.P6ProjWorkgroupDTO;
import au.com.wp.corp.p6.exception.P6DataAccessException;
import au.com.wp.corp.p6.util.CacheManager;

/**
 * @author N039126
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/servlet-context.xml" })
public class P6EllipseDAOIntegrationTest {
	
	@Autowired
	P6EllipseDAO p6EllipseDAO;
	
	@Autowired
	P6PortalDAO p6PortalDAO;
	
	
	
	@Test
	public void testReadElipseWorkorderDetails() throws P6DataAccessException {
		
		Map<String, P6ProjWorkgroupDTO> projectWorkgroupMap = CacheManager.getP6ProjectWorkgroupMap();

		Map<String, List<String>> projectWorkgroupListMap = CacheManager.getProjectWorkgroupListMap();

		try {
			List<String> projects = null;
			for (P6ProjWorkgroupDTO projectWG : p6PortalDAO.getProjectResourceMappingList()) {
				projectWorkgroupMap.put(projectWG.getPrimaryResourceId(), projectWG);

				if (null == projectWorkgroupListMap.get(projectWG.getProjectName())) {
					projects = new ArrayList<>();
					projectWorkgroupListMap.put(projectWG.getProjectName(), projects);
				} else {
					projects = projectWorkgroupListMap.get(projectWG.getProjectName());
				}
				projects.add(projectWG.getPrimaryResourceId() != null ? projectWG.getPrimaryResourceId()
						: projectWG.getSchedulerinbox());

			}
		} catch (P6DataAccessException e) {

		}
		
		final List<String> workgroupList = new ArrayList<>();
		
		final Set<String> keys = CacheManager.getProjectWorkgroupListMap().keySet(); 
		for ( String key : keys){
			workgroupList.addAll(CacheManager.getProjectWorkgroupListMap().get(key));
		}
		
		System.out.println(p6EllipseDAO.readElipseWorkorderDetails(workgroupList));
	}

}
