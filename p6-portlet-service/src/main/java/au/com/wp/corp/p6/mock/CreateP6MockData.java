/**
 * 
 */
package au.com.wp.corp.p6.mock;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.dto.WorkOrderSearchRequest;

/**
 * @author n039126
 *
 */
@Component
public class CreateP6MockData {

	private List<WorkOrder> loadMockData() {
		ObjectMapper mapper = new ObjectMapper();
		final String baseDir = System.getProperty("properties.dir");
		InputStream stream = null;
		List<WorkOrder> workOrders = null;
		try {
			final File file = new File(baseDir + File.separator + "p6_mock_data.json");
			stream = new FileInputStream(file);
			workOrders = mapper.readValue(stream,
					mapper.getTypeFactory().constructCollectionType(List.class, WorkOrder.class));
		} catch (IOException e) {

		}

		return workOrders;
	}

	private String getCrewNames(List<String> crewNames) {
		String crewName = "";
		if (null != crewNames)
			for (String crew : crewNames) {
				crewName = crewName + (crewName.isEmpty() ? "" : ",") + crew;
			}
		return crewName;
	}

	public List<WorkOrder> search(WorkOrderSearchRequest request) {
		
		List<WorkOrder> workOrders = new ArrayList<>();
		List<WorkOrder> _workOrders = loadMockData();
		if ( request == null)
			return _workOrders;
			
		final String crewNames = getCrewNames(request.getCrewList());
		for (WorkOrder workOrder : _workOrders) {
			if (workOrder.getCrewNames().contains(crewNames)
					&& convertDate(request.getFromDate()).equalsIgnoreCase(workOrder.getScheduleDate())) {
				workOrders.add(workOrder);
			}
		}
		return workOrders;

	}

	private String convertDate ( String date )  {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		Date d = new Date();
		try {
			d = sdf.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		sdf = new SimpleDateFormat("dd/MM/yyyy");
		return sdf.format(d);
	}
}
