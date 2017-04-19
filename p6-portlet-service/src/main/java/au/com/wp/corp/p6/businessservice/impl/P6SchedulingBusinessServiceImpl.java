package au.com.wp.corp.p6.businessservice.impl;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import au.com.wp.corp.p6.businessservice.P6SchedulingBusinessService;
import au.com.wp.corp.p6.dataservice.TaskDAO;
import au.com.wp.corp.p6.dataservice.TodoDAO;
import au.com.wp.corp.p6.dataservice.WorkOrderDAO;
import au.com.wp.corp.p6.dto.ExecutionPackageDTO;
import au.com.wp.corp.p6.dto.TaskDTO;
import au.com.wp.corp.p6.dto.ToDoItem;
import au.com.wp.corp.p6.dto.ViewToDoStatus;
import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.dto.WorkOrderSearchInput;
import au.com.wp.corp.p6.model.ExecutionPackage;
import au.com.wp.corp.p6.model.Task;
import au.com.wp.corp.p6.model.TodoAssignment;
import au.com.wp.corp.p6.model.TodoTemplate;

@Service
public class P6SchedulingBusinessServiceImpl implements P6SchedulingBusinessService {

	private Map<String, WorkOrder> mapStorage = null;
	private static final Logger logger = LoggerFactory.getLogger(P6SchedulingBusinessServiceImpl.class);
	@Autowired
	TaskDAO taskDAO;
	@Autowired
	TodoDAO todoDAO;
	@Autowired
	WorkOrderDAO workOrderDAO;

	@PostConstruct
	public void initData() {
		logger.info("Initializing Datastore..");
		mapStorage = new ConcurrentHashMap<String, WorkOrder>();
		WorkOrder workOrder1 = new WorkOrder();
		workOrder1.setLeadCrew("MOST1");
		workOrder1.setScheduleDate(String.valueOf(Date.valueOf(LocalDate.now())));
		workOrder1.setWorkOrders(Arrays.asList(new String[] { "Y6UIOP97" }));

		ToDoItem toDoItems = new ToDoItem();
		toDoItems.setTodoName("ENAR");
		toDoItems.setWorkOrders(workOrder1.getWorkOrders());
		workOrder1.setToDoItems(Arrays.asList(new ToDoItem[] { toDoItems }));

		mapStorage.put(workOrder1.getWorkOrders().get(0), workOrder1);

		WorkOrder workOrder2 = new WorkOrder();
		workOrder2.setLeadCrew("MOST2");
		workOrder2.setScheduleDate(String.valueOf(Date.valueOf(LocalDate.now())));
		workOrder2.setWorkOrders(Arrays.asList(new String[] { "Y6UIOP67" }));

		toDoItems = new ToDoItem();
		toDoItems.setTodoName("ESA");
		toDoItems.setWorkOrders(workOrder2.getWorkOrders());
		workOrder2.setToDoItems(Arrays.asList(new ToDoItem[] { toDoItems }));

		mapStorage.put(workOrder2.getWorkOrders().get(0), workOrder2);

		WorkOrder workOrder3 = new WorkOrder();
		workOrder3.setLeadCrew("MOST3");
		workOrder3.setScheduleDate(String.valueOf(Date.valueOf(LocalDate.now())));
		workOrder3.setWorkOrders(Arrays.asList(new String[] { "Y6UIOP87" }));
		/* this code will be replaced will the actual P6 Service call */
		mapStorage.put(workOrder3.getWorkOrders().get(0), workOrder3);
	}

	public List<WorkOrder> retrieveWorkOrders(WorkOrderSearchInput input) {
		List<WorkOrder> workOrders = new ArrayList<WorkOrder>();
		/* this code will be replaced will the actual P6 Service call */
		WorkOrder workOrder1 = new WorkOrder();
		workOrder1.setExecutionPackage("test execution 1");
		workOrder1.setLeadCrew("MOST1");
		workOrder1.setScheduleDate(String.valueOf(Date.valueOf(LocalDate.now())));
		workOrder1.setWorkOrders(Arrays.asList(new String[] { "Y6UIOP67", "Y6UIOP70", "Y6UIOP97" }));

		ToDoItem toDoItems = new ToDoItem();
		toDoItems.setTodoName("ENAR");
		toDoItems.setWorkOrders(workOrder1.getWorkOrders());

		workOrder1.setToDoItems(Arrays.asList(new ToDoItem[] { toDoItems }));

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

		/* this code will be replaced will the actual P6 Service call */
		return workOrders;

	}

	public List<WorkOrder> retrieveJobs(WorkOrderSearchInput input) {
		for (WorkOrder workOrder : mapStorage.values()) {
			List<TodoAssignment> list = todoDAO.fetchToDosByWorkOrder(workOrder);
			List<ToDoItem> toDoItems = new ArrayList<ToDoItem>();

			for (TodoAssignment todoTemplate : list) {
				ToDoItem toDoItem = new ToDoItem();
				toDoItem.setTodoName(todoTemplate.getTodoTemplate().getTodoNam());
				toDoItem.setWorkOrders(workOrder.getWorkOrders());
				toDoItems.add(toDoItem);
			}
			workOrder.setToDoItems(toDoItems);
			WorkOrderSearchInput workOrderSearchInput = new WorkOrderSearchInput();
			workOrderSearchInput.setWorkOrderId(workOrder.getWorkOrders().get(0));
			List<Task> taskList = workOrderDAO.fetchWorkOrdersForViewToDoStatus(workOrderSearchInput);
			for (Task task : taskList)
				workOrder.setSchedulingToDoComment(task.getCmts());
			mapStorage.put(workOrder.getWorkOrders().get(0), workOrder);
		}
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

	@Override
	public List<ToDoItem> fetchToDos() {

		List<TodoTemplate> toDoTemplateList = todoDAO.fetchAllToDos();
		List<ToDoItem> toDos = new ArrayList<ToDoItem>();
		for (TodoTemplate toDo : toDoTemplateList) {
			ToDoItem item = new ToDoItem();
			item.setCrtdTs(toDo.getCrtdTs().toString());
			item.setCrtdUsr(toDo.getCrtdUsr());
			if (null != toDo.getLstUpdtdTs()) {
				item.setLstUpdtdTs(toDo.getLstUpdtdTs().toString());
			}
			item.setLstUpdtdUsr(toDo.getLstUpdtdUsr());
			item.setTmpltDesc(toDo.getTmpltDesc());
			item.setTmpltId(String.valueOf(toDo.getTmpltId()));
			item.setTodoName(toDo.getTodoNam());
			// TODO populate work order
			toDos.add(item);
		}
		return toDos;
	}

	@Override
	public List<ViewToDoStatus> fetchWorkOrdersForViewToDoStatus(WorkOrderSearchInput query) {

		List<Task> tasks = workOrderDAO.fetchWorkOrdersForViewToDoStatus(query);
		List<ViewToDoStatus> toDoStatuses = new ArrayList<ViewToDoStatus>();

		for (Task task : tasks) {
			ViewToDoStatus status = new ViewToDoStatus();
			status.setCrewAssigned(task.getCrewId());
			// TODO to decide the user to populate the comment
			status.setDeportComment(task.getCmts());
			if (null != task.getExecutionPackage()) {
				status.setExecutionPackage(task.getExecutionPackage().getExctnPckgNam());
			}
			status.setLeadCrew(task.getLeadCrewId());

			status.setScheduleDate(task.getSchdDt().toString());
			// TODO
			// status.setSchedulingComment(schedulingComment);
			// status.setWorkOrders(workOrders);
			Set<TodoAssignment> toDoEntities = task.getTodoAssignments();

			if (null != task.getSchdDt()) {
				status.setScheduleDate(task.getSchdDt().toString());
			}
			// TODO

			status.setWorkOrders(task.getTaskId());

			List<au.com.wp.corp.p6.dto.ToDoAssignment> assignmentDTOs = new ArrayList<au.com.wp.corp.p6.dto.ToDoAssignment>();
			if (null != toDoEntities) {
				logger.debug("Size of ToDoAssignment for task>>>{}", toDoEntities.size());
			}
			for (TodoAssignment assignment : toDoEntities) {
				au.com.wp.corp.p6.dto.ToDoAssignment assignmentDTO = new au.com.wp.corp.p6.dto.ToDoAssignment();
				assignmentDTO.setComment(assignment.getCmts());
				if (null != assignment.getReqdByDt()) {
					assignmentDTO.setReqByDate(assignment.getReqdByDt().toString());
				}
				assignmentDTO.setStatus(assignment.getStat());
				assignmentDTO.setSupportingDoc(assignment.getSuprtngDocLnk());
				if (null != assignment.getTodoTemplate()) {
					assignmentDTO.setToDoName(assignment.getTodoTemplate().getTodoNam());
				}
				assignmentDTOs.add(assignmentDTO);
			}
			status.setTodoAssignments(assignmentDTOs);
			toDoStatuses.add(status);
		}
		return toDoStatuses;
	}

	@Override
	public WorkOrder saveToDo(WorkOrder workOrder) {
		todoDAO.saveToDos(workOrder);
		return workOrder;
	}

	public ExecutionPackageDTO saveExecutionPackage(ExecutionPackageDTO executionPackageDTO) {
		executionPackageDTO = taskDAO.saveExecutionPackage(executionPackageDTO);
		return executionPackageDTO;
	}

	public List<ExecutionPackageDTO> fetchExecutionPackageList() {
		// TODO Auto-generated method stub
		return null;
	}

}
