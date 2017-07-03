/**
 * 
 */
package au.com.wp.corp.p6.integration.wsclient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import au.com.wp.corp.p6.integration.business.P6EllipseIntegrationService;
import au.com.wp.corp.p6.integration.dto.P6ActivityDTO;
import au.com.wp.corp.p6.integration.dto.P6ProjWorkgroupDTO;
import au.com.wp.corp.p6.integration.exception.P6BusinessException;
import au.com.wp.corp.p6.integration.util.CacheManager;
import au.com.wp.corp.p6.integration.util.EllipseReadParameter;
import au.com.wp.corp.p6.integration.util.P6ReloadablePropertiesReader;
import au.com.wp.corp.p6.integration.wsclient.cleint.impl.P6WSClientImpl;

/**
 * @author N039126
 *
 */
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@PowerMockIgnore({"javax.management.*"})
@ContextConfiguration(locations = { "/servlet-context.xml" })
@PrepareForTest(P6ReloadablePropertiesReader.class)
public class P6WSClientIntegrationTest {

	private List<P6ActivityDTO> p6Activities = null;

	
	@Rule
    public PowerMockRule rule = new PowerMockRule();
	
	@Autowired
	P6WSClientImpl p6WsclientImpl;

	@Autowired
	P6EllipseIntegrationService p6serviceImpl;

	private final List<String> workgroupList = new ArrayList<>();

	@Before
	public void setup() throws P6BusinessException {
		p6serviceImpl.readUDFTypeMapping();
		p6serviceImpl.readProjectWorkgroupMapping();

		PowerMockito.mockStatic(P6ReloadablePropertiesReader.class);
		PowerMockito.when(P6ReloadablePropertiesReader.getProperty("INTEGRATION_RUN_STARTEGY")).thenReturn("INDIVIDUAL");

		final String integrationRunStartegy = P6ReloadablePropertiesReader.getProperty("INTEGRATION_RUN_STARTEGY");

		if (null == integrationRunStartegy || integrationRunStartegy.isEmpty()) {
			throw new P6BusinessException("INTEGRATION_RUN_STARTEGY can't be null");
		}

		if (integrationRunStartegy.equals(EllipseReadParameter.ALL.name())) {
			final Set<String> keys = CacheManager.getProjectWorkgroupListMap().keySet();
			for (String key : keys) {
				workgroupList.addAll(CacheManager.getProjectWorkgroupListMap().get(key));
			}
		} else if (integrationRunStartegy.equals(EllipseReadParameter.INDIVIDUAL.name())) {
			final Set<String> keys = CacheManager.getProjectWorkgroupListMap().keySet();
			for (String key : keys) {
				workgroupList.addAll(CacheManager.getProjectWorkgroupListMap().get(key));
			}

		}
	}

	@Test
	public void test_5_readResource() throws P6BusinessException {


		Map<String, Integer> projWorkgroupDTOs = p6WsclientImpl.readResources();

		Map<String, P6ProjWorkgroupDTO> resourceMap = CacheManager.getP6ProjectWorkgroupMap();

	}

}
