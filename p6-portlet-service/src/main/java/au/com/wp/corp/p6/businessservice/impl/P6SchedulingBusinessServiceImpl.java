package au.com.wp.corp.p6.businessservice.impl;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import au.com.wp.corp.p6.businessservice.P6SchedulingBusinessService;
import au.com.wp.corp.p6.businessservice.dto.TaskDTO;
import au.com.wp.corp.p6.dataservice.TaskDAO;
import au.com.wp.corp.p6.dto.ToDoItems;
import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.dto.WorkOrderSerachInput;
import au.com.wp.corp.p6.model.Task;

@Service
public class P6SchedulingBusinessServiceImpl implements P6SchedulingBusinessService {

	private Map<String, WorkOrder> mapStorage = null;

	@Autowired
	TaskDAO taskDAO;
	
	@PostConstruct
	public void initData() {
		System.out.println("Initializing Datastore..");
		mapStorage = new ConcurrentHashMap<String, WorkOrder>();
		WorkOrder workOrder1 = new WorkOrder();
		workOrder1.setLeadCrew("MOST1");
		workOrder1.setScheduleDate(String.valueOf(Date.valueOf(LocalDate.now())));
		workOrder1.setWorkOrders(Arrays.asList(new String[] { "Y6UIOP97" }));

		ToDoItems toDoItems = new ToDoItems();
		toDoItems.setToDoName("ENAR");
		toDoItems.setWorkOrders(workOrder1.getWorkOrders());
		workOrder1.setToDoItems(Arrays.asList(new ToDoItems[] { toDoItems }));

		mapStorage.put(workOrder1.getWorkOrders().get(0), workOrder1);

		WorkOrder workOrder2 = new WorkOrder();
		workOrder2.setLeadCrew("MOST2");
		workOrder2.setScheduleDate(String.valueOf(Date.valueOf(LocalDate.now())));
		workOrder2.setWorkOrders(Arrays.asList(new String[] { "Y6UIOP67" }));

		toDoItems = new ToDoItems();
		toDoItems.setToDoName("ESA");
		toDoItems.setWorkOrders(workOrder2.getWorkOrders());
		workOrder2.setToDoItems(Arrays.asList(new ToDoItems[] { toDoItems }));

		mapStorage.put(workOrder2.getWorkOrders().get(0), workOrder2);

		WorkOrder workOrder3 = new WorkOrder();
		workOrder3.setLeadCrew("MOST3");
		workOrder3.setScheduleDate(String.valueOf(Date.valueOf(LocalDate.now())));
		workOrder3.setWorkOrders(Arrays.asList(new String[] { "Y6UIOP87" }));
		/* this code will be replaced will the actual P6 Service call */
		mapStorage.put(workOrder3.getWorkOrders().get(0), workOrder3);
	}

	public List<WorkOrder> retrieveWorkOrders(WorkOrderSerachInput input) {
		List<WorkOrder> workOrders = new ArrayList<WorkOrder>();
		/* this code will be replaced will the actual P6 Service call */
		if (mapStorage == null || mapStorage.size() < 1) {
			WorkOrder workOrder1 = new WorkOrder();
			workOrder1.setExecutionPackage("test execution 1");
			workOrder1.setLeadCrew("MOST1");
			workOrder1.setScheduleDate(String.valueOf(Date.valueOf(LocalDate.now())));
			workOrder1.setWorkOrders(Arrays.asList(new String[] { "Y6UIOP67", "Y6UIOP70", "Y6UIOP97" }));

			ToDoItems toDoItems = new ToDoItems();
			toDoItems.setToDoName("ENAR");
			toDoItems.setWorkOrders(workOrder1.getWorkOrders());

			workOrder1.setToDoItems(Arrays.asList(new ToDoItems[] { toDoItems }));

			workOrders.add(workOrder1);
			WorkOrder workOrder2 = new WorkOrder();
			workOrder2.setExecutionPackage("test execution 1");
			workOrder2.setLeadCrew("MOST1");
			workOrder2.setScheduleDate(String.valueOf(Date.valueOf(LocalDate.now())));
			workOrder2.setWorkOrders(Arrays.asList(new String[] { "Y6UIOP67", "Y6UIOP70", "Y6UIOP97" }));

			workOrders.add(workOrder2);

			WorkOrder workOrder3 = new WorkOrder();
			workOrder3.setExecutionPackage("test execution 1");
			workOrder3.setLeadCrew("MOST1");
			workOrder3.setScheduleDate(String.valueOf(Date.valueOf(LocalDate.now())));
			workOrder3.setWorkOrders(Arrays.asList(new String[] { "Y6UIOP67", "Y6UIOP70", "Y6UIOP97" }));

			workOrders.add(workOrder3);
		} else {
			workOrders = new ArrayList<WorkOrder>(mapStorage.values());
		}
		/* this code will be replaced will the actual P6 Service call */
		return workOrders;

	}

	public List<WorkOrder> retrieveJobs(WorkOrderSerachInput input) {

		return new ArrayList<WorkOrder>(mapStorage.values());

	}

	public List<WorkOrder> saveWorkOrder(WorkOrder workOrder) {
		System.out.println(workOrder);

		if (mapStorage.containsKey(workOrder.getWorkOrders().get(0))) {
			mapStorage.put(workOrder.getWorkOrders().get(0), workOrder);
		}

		List<WorkOrder> listofJobs = new ArrayList<WorkOrder>(mapStorage.values());

		return listofJobs;
	}
	
	@Transactional
	public List<TaskDTO> listTasks() {
		
		List<Task> taskList = taskDAO.listTasks();
		List<TaskDTO> tasks = new ArrayList<TaskDTO>();
		for (Task task2 : taskList) {
			TaskDTO taskDTO = new TaskDTO();			
			taskDTO.setTaskId(task2.getTaskId());
			taskDTO.setExecutionPackageId(task2.getExecutionPackage().getExctnPckgId());
			taskDTO.setDepotId(task2.getDepotId());
			taskDTO.setCrewId(task2.getCrewId());
			taskDTO.setLeadCrewId(task2.getLeadCrewId());
			taskDTO.setMatrlReqRef(task2.getMatrlReqRef());
			tasks.add(taskDTO);
		}
		return tasks;
	}

}
