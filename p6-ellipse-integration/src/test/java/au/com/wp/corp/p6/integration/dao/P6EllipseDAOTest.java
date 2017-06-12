/**
 * 
 */
package au.com.wp.corp.p6.integration.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import au.com.wp.corp.p6.integration.dao.P6EllipseDAOImpl;
import au.com.wp.corp.p6.integration.dao.P6PortalDAOImpl;
import au.com.wp.corp.p6.integration.dao.mapper.P6EllipseMapper;
import au.com.wp.corp.p6.integration.dao.mapper.P6PortalMapper;
import au.com.wp.corp.p6.integration.dto.P6ProjWorkgroupDTO;
import au.com.wp.corp.p6.integration.exception.P6DataAccessException;
import au.com.wp.corp.p6.integration.util.CacheManager;

/**
 * @author N039126
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/servlet-context.xml" })
public class P6EllipseDAOTest {
	
	@InjectMocks
	P6EllipseDAOImpl p6EllipseDAO;
	
	
	
	@InjectMocks
	P6PortalDAOImpl p6PortalDAO;
	
	@Mock
	P6PortalMapper p6PortalDataMapper;
	
	
	@Mock
	P6EllipseMapper ellipseDataMapper;
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testReadProjectResourceMapping() throws P6DataAccessException {
		
		thrown.expect(P6DataAccessException.class);
		
		Mockito.when(p6PortalDataMapper.getProjectResourceMappingList()).thenThrow(P6DataAccessException.class);
		List<P6ProjWorkgroupDTO> projects = p6PortalDAO.getProjectResourceMappingList();
		Assert.assertNotNull(projects);
	}

	
	@Test
	public void testReadElipseWorkorderDetails() throws P6DataAccessException {
		thrown.expect(P6DataAccessException.class);
		List<P6ProjWorkgroupDTO> value = new ArrayList<>();
		P6ProjWorkgroupDTO proj = new P6ProjWorkgroupDTO();
		
		proj.setPrimaryResourceId("MOMT1");
		proj.setPrimaryResourceObjectId(1234);
		proj.setPrimaryResourceYN("Y");
		proj.setProjectObjectId(267339);
		value.add(proj);
		
		Mockito.when(p6PortalDataMapper.getProjectResourceMappingList()).thenReturn(value);
		
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
			e.printStackTrace();
		}
		
		final List<String> workgroupList = new ArrayList<>();
		
		final Set<String> keys = CacheManager.getProjectWorkgroupListMap().keySet(); 
		for ( String key : keys){
			workgroupList.addAll(CacheManager.getProjectWorkgroupListMap().get(key));
		}
		
		Mockito.when(ellipseDataMapper.readElipseWorkorderDetails(workgroupList)).thenThrow(P6DataAccessException.class);
		
		
		Assert.assertNotNull(p6EllipseDAO.readElipseWorkorderDetails(workgroupList));
	}

	
	
	
}
