/**
 * 
 */
package au.com.wp.corp.p6.integration.util;

import java.io.File;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reloads the properties file if any changes occur
 * 
 * @author N039126
 * @version 1.0
 */
public class P6ReloadablePropertiesReader {
	private static final Logger logger = LoggerFactory.getLogger(P6ReloadablePropertiesReader.class);

	private static PropertiesConfiguration configuration;
	
	private P6ReloadablePropertiesReader(){
		
	}

	/**
	 * 
	 */
	static{
		try {
			final String propFilePath = System.getProperty("properties.dir");
			configuration = new PropertiesConfiguration(propFilePath + File.separator + "p6portal.properties");
			configuration.setDelimiterParsingDisabled(true);
		} catch (ConfigurationException e) {
			logger.debug("An error ocurrs while reading properties file : ", e);
		}
		configuration.setReloadingStrategy(new FileChangedReloadingStrategy());
	}

	public static synchronized String getProperty(final String key) {
		return (String) configuration.getString(key);
	}

}
