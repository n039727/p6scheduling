package au.com.wp.corp.p6.businessservice.impl;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import au.com.wp.corp.p6.businessservice.P6SchedulingBusinessService;
import au.com.wp.corp.p6.dataservice.ExecutionPackageDao;
import au.com.wp.corp.p6.dataservice.TaskDAO;
import au.com.wp.corp.p6.dataservice.TodoDAO;
import au.com.wp.corp.p6.dataservice.WorkOrderDAO;
import au.com.wp.corp.p6.dto.ExecutionPackageDTO;
import au.com.wp.corp.p6.dto.TaskDTO;
import au.com.wp.corp.p6.dto.ToDoAssignment;
import au.com.wp.corp.p6.dto.ToDoItem;
import au.com.wp.corp.p6.dto.ViewToDoStatus;
import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.dto.WorkOrderSearchRequest;
import au.com.wp.corp.p6.exception.P6BusinessException;
import au.com.wp.corp.p6.mock.CreateP6MockData;
import au.com.wp.corp.p6.model.ExecutionPackage;
import au.com.wp.corp.p6.model.Task;
import au.com.wp.corp.p6.model.TodoAssignment;
import au.com.wp.corp.p6.model.TodoTemplate;
import au.com.wp.corp.p6.utils.DateUtils;


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
	
	@Autowired
	DateUtils dateUtils;
	
	@Autowired
	private ExecutionPackageDao executionPackageDao;

	
	@Autowired
	CreateP6MockData mockData;
	
	
	@PostConstruct
	public void initData() {
		logger.info("Initializing Datastore..");
		mapStorage = new ConcurrentHashMap<String, WorkOrder>();
		WorkOrder workOrder1 = new WorkOrder();
		workOrder1.setLeadCrew("MOST1");
		workOrder1.setScheduleDate(String.valueOf(Date.valueOf(LocalDate.now())));
		workOrder1.setWorkOrders(Arrays.asList(new String[] { "Y6UIOP97" }));

		/*ToDoItem toDoItems = new ToDoItem();
		toDoItems.setTodoName("ENAR");
		toDoItems.setWorkOrders(workOrder1.getWorkOrders());
		workOrder1.setToDoItems(Arrays.asList(new ToDoItem[] { toDoItems }));*/

		mapStorage.put(workOrder1.getWorkOrders().get(0), workOrder1);

		WorkOrder workOrder2 = new WorkOrder();
		workOrder2.setLeadCrew("MOST2");
		workOrder2.setScheduleDate(String.valueOf(Date.valueOf(LocalDate.now())));
		workOrder2.setWorkOrders(Arrays.asList(new String[] { "Y6UIOP67" }));

		/*toDoItems = new ToDoItem();
		toDoItems.setTodoName("ESA");
		toDoItems.setWorkOrders(workOrder2.getWorkOrders());
		workOrder2.setToDoItems(Arrays.asList(new ToDoItem[] { toDoItems }));*/

		mapStorage.put(workOrder2.getWorkOrders().get(0), workOrder2);

		WorkOrder workOrder3 = new WorkOrder();
		workOrder3.setLeadCrew("MOST3");
		workOrder3.setScheduleDate(String.valueOf(Date.valueOf(LocalDate.now())));
		workOrder3.setWorkOrders(Arrays.asList(new String[] { "Y6UIOP87" }));
		/* this code will be replaced will the actual P6 Service call */
		mapStorage.put(workOrder3.getWorkOrders().get(0), workOrder3);
	}

	public List<WorkOrder> retrieveWorkOrders(WorkOrderSearchRequest input) {
		List<WorkOrder> workOrders = new ArrayList<WorkOrder>();
		/* this code will be replaced will the actual P6 Service call */
		WorkOrder workOrder1 = new WorkOrder();
		workOrder1.setExecutionPackage("test execution 1");
		workOrder1.setLeadCrew("MOST1");
		workOrder1.setScheduleDate(String.valueOf(Date.valueOf(LocalDate.now())));
		workOrder1.setWorkOrders(Arrays.asList(new String[] { "Y6UIOP67", "Y6UIOP70", "Y6UIOP97" }));

		ToDoItem toDoItems = new ToDoItem();
		toDoItems.setToDoName("ENAR");
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
	
	@Override
	public List<WorkOrder> search(WorkOrderSearchRequest input) {
		return mockData.search(input);
	}

	public List<WorkOrder> retrieveJobs(WorkOrderSearchRequest input) {
		/*for (WorkOrder workOrder : mapStorage.values()) {
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
		}*/
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
	public List<TaskDTO> listTasks() throws P6BusinessException {

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
			item.setToDoName(toDo.getTodoNam());
			// TODO populate work order
			toDos.add(item);
		}
		return toDos;
	}

	@Override
	public List<ViewToDoStatus> fetchWorkOrdersForViewToDoStatus(WorkOrderSearchRequest query) {

		List<Task> tasks = workOrderDAO.fetchWorkOrdersForViewToDoStatus(query);
		List<ViewToDoStatus> toDoStatuses = new ArrayList<ViewToDoStatus>();
		Map<String, ViewToDoStatus> taskIdWOMap = new HashMap<String, ViewToDoStatus>();
		for (Task task : tasks) {
			ViewToDoStatus status = null;
			String key = null;
			if (task.getExecutionPackage() != null
					&& "PKG1".equals(task.getExecutionPackage().getExctnPckgNam())) {
				key = task.getExecutionPackage().getExctnPckgNam();
			} else {
				key = task.getTaskId();
			}
			
			if (!taskIdWOMap.containsKey(key)) {
				taskIdWOMap.put(key, new ViewToDoStatus());
			}
			
			status = taskIdWOMap.get(key);
			
			if (status.getWorkOrders() == null) {
				status.setWorkOrders(new ArrayList<String>());
			}
			status.getWorkOrders().add(task.getTaskId());
			
			if (status.getCrewAssigned() == null) {
				status.setCrewAssigned(new ArrayList<String>());
			}
			status.getCrewAssigned().add(task.getCrewId());
			
			status.setSchedulingComment(task.getCmts());
			
			if (task.getExecutionPackage() != null
					&& "PKG1".equals(task.getExecutionPackage().getExctnPckgNam())) {
				status.setExecutionPackage(task.getExecutionPackage().getExctnPckgNam());
			}
			
			status.setLeadCrew(task.getLeadCrewId());

			if (null != task.getSchdDt()) {
				status.setScheduleDate(dateUtils.toStringYYYY_MM_DD(task.getSchdDt()));
			}

			
			// TODO
			Set<TodoAssignment> toDoEntities = task.getTodoAssignments();

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
				assignmentDTO.setWorkOrderId(task.getTaskId());
				assignmentDTO.setToDoName(todoDAO.getToDoName(assignment.getTodoId().longValue()));
				assignmentDTOs.add(assignmentDTO);
			}
			status.setTodoAssignments(assignmentDTOs);
			toDoStatuses.add(status);
		}
		return toDoStatuses;
	}

	/*@Override
	public WorkOrder saveToDo(WorkOrder workOrder) {
		todoDAO.saveToDos(workOrder);
		return workOrder;
	}*/
	
	@Override
	public WorkOrder saveToDo(WorkOrder workOrder) {
		
		if (workOrder == null)
			throw new IllegalArgumentException("Work Order canot be null");
		
		if (workOrder.getWorkOrders() != null) {
			for (String workOrderId: workOrder.getWorkOrders()) {
				Task task = prepareTaskFromWorkOrderId(workOrderId, workOrder);
				workOrderDAO.saveTask(task);
			}
		}
		
		
		return workOrder;
	}

	
	private Task prepareTaskFromWorkOrderId(String workOrderId, WorkOrder workOrder) {
		Task dbTask = workOrderDAO.fetch(workOrderId);
		Task updatedTask = prepareTaskBean(dbTask, workOrder);
		prepareToDoAssignmentList(updatedTask, workOrder);
		return updatedTask;
	}

	private void prepareToDoAssignmentList(Task updatedTask, WorkOrder workOrder) {
		
		for (Iterator<TodoAssignment> itr = updatedTask.getTodoAssignments().iterator(); itr.hasNext(); ){
			TodoAssignment todo = itr.next();
			boolean found = false;
			System.out.println("todo.getTodoId(): " + todo.getTodoId());
			for (Iterator<ToDoItem> itrToDo = workOrder.getToDoItems().iterator(); itrToDo.hasNext();) {
				ToDoItem item = itrToDo.next();
				System.out.println("item.getTodoName(): " + item.getToDoName());
				if (item.getToDoName().equals(todoDAO.getToDoName(todo.getTodoId().longValue())) // TODO check on long ID
						&& item.getWorkOrders().contains(updatedTask.getTaskId())) {
					itrToDo.remove();
					found = true;
					break;
				} 
			}
			if (!found) {
				itr.remove();
			}
		}
		
		// Set the new values
		for (Iterator<ToDoItem> itrToDo = workOrder.getToDoItems().iterator(); itrToDo.hasNext();) {
			ToDoItem item = itrToDo.next();
			TodoAssignment todoAssignment = new TodoAssignment();
			todoAssignment.setTask(updatedTask);
			todoAssignment.setExecutionPackage(updatedTask.getExecutionPackage());
			todoAssignment.setTodoId(todoDAO.getToDoId(item.getToDoName()));
			updatedTask.getTodoAssignments().add(todoAssignment);
		}
		
		System.out.println("After merging to do assignments size: " + updatedTask.getTodoAssignments().size());
		System.out.println("After merging to do assignments: " + updatedTask.getTodoAssignments());
	}

	private Task prepareTaskBean(Task dbTask, WorkOrder workOrder) {
		// create new Task if not there in DB
		if (dbTask == null) {
			dbTask = new Task();
		}
		
		dbTask.setCmts(workOrder.getSchedulingToDoComment());
		dbTask.setCrewId(workOrder.getCrewNames());
		dbTask.setLeadCrewId(workOrder.getLeadCrew());
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-mm-yyyy");
		java.util.Date scheduleDate = null;
		try {
			scheduleDate = simpleDateFormat.parse(workOrder.getScheduleDate());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dbTask.setSchdDt(scheduleDate);
		dbTask.setDepotId(workOrder.getDepotId());
		dbTask.setMatrlReqRef(workOrder.getMeterialReqRef());
		dbTask.setExecutionPackage(executionPackageDao.fetch(StringUtils.isEmpty(workOrder.getExecutionPackage()) ? "PKG1" :
			workOrder.getExecutionPackage()));
		logger.debug("Execution Package {}", workOrder.getExecutionPackage());
		return dbTask;
	}

	public ExecutionPackageDTO saveExecutionPackage(ExecutionPackageDTO executionPackageDTO) throws P6BusinessException {
		executionPackageDTO.setExctnPckgNam(getCurrentDateTimeMS());
		executionPackageDTO = executionPackageDao.saveExecutionPackage(executionPackageDTO);
		return executionPackageDTO;
	}

	public List<ExecutionPackageDTO> fetchExecutionPackageList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<WorkOrder> fetchWorkOrdersForAddUpdateToDo(WorkOrderSearchRequest query) {
		
		List<Task> tasks = null;
		ExecutionPackage executionPackage = null;
		logger.debug("ExecPckgName >>>>{}", query.getExecPckgName());
		logger.debug("WorkOrderId >>>>{}", query.getWorkOrderId());
		if(null != query && null != query.getExecPckgName()){
			executionPackage = executionPackageDao.fetch(query.getExecPckgName());
			tasks = new ArrayList<Task>(executionPackage.getTasks());
		}
		else{
			tasks = workOrderDAO.fetchWorkOrdersForViewToDoStatus(query);
		}
		
		Map<String, WorkOrder> workOrderMap = new HashMap<String, WorkOrder>();
		Map<String, Map<Long, ToDoItem>> workOrderToDoMap = new HashMap<String, Map<Long, ToDoItem>>();

		for (Task task : tasks) {
			
			String executionPkg = null;
			if (task.getExecutionPackage() != null) {
				executionPkg = task.getExecutionPackage().getExctnPckgNam();
			}
			
			WorkOrder workOrder = null;
			Map<Long, ToDoItem> toDoMap = null;
			if (!"PKG1".equals(executionPkg) && workOrderMap.containsKey(executionPkg)) {
				workOrder = workOrderMap.get(executionPkg);
				workOrder.setCrewNames(workOrder.getCrewNames() + "," + task.getCrewId());
				workOrder.getWorkOrders().add(task.getTaskId());
				toDoMap = workOrderToDoMap.get(executionPkg);
			} else {
				workOrder = new WorkOrder();
				if (!"PKG1".equals(executionPkg))
					workOrder.setExecutionPackage(executionPkg);
				else 
					executionPkg = task.getTaskId();
				// TODO to decide the user to populate the comment
				List<String> workOrders = new ArrayList<String>();
				workOrders.add(task.getTaskId());
				workOrder.setWorkOrders(workOrders);
				workOrder.setLeadCrew(task.getLeadCrewId());
				workOrder.setCrewNames(task.getCrewId());
				workOrder.setScheduleDate(task.getSchdDt().toString());
				workOrder.setSchedulingToDoComment(task.getCmts());
				toDoMap = new HashMap<Long, ToDoItem>();
				workOrderMap.put(executionPkg, workOrder);
			}
			
			if (task.getTodoAssignments() != null) {
				for (TodoAssignment todo : task.getTodoAssignments()) {
					Long todoId = todo.getTodoId().longValue();
					if (toDoMap.containsKey(todoId)) {
						toDoMap.get(todoId).getWorkOrders().add(todo.getTask().getTaskId());
					} else {
						ToDoItem item = new ToDoItem();
						item.setTodoId(String.valueOf(todoId));
						item.setToDoName(todoDAO.getToDoName(todoId));
						List<String> workOrders = new ArrayList<String>();
						workOrders.add(todo.getTask().getTaskId());
						item.setWorkOrders(workOrders);
						toDoMap.put(todoId, item);
					}
				}
				workOrderToDoMap.put(executionPkg, toDoMap);
			}
		}
		
		List<WorkOrder> workOrders = new ArrayList<WorkOrder>(workOrderMap.values());
		for (WorkOrder workOrder : workOrders) {
			String executionPkg = workOrder.getExecutionPackage();
			if (StringUtils.isEmpty(executionPkg)) {
				executionPkg = workOrder.getWorkOrders().get(0);
			}
			workOrder.setToDoItems(new ArrayList<ToDoItem>(workOrderToDoMap.get(executionPkg).values()));
		}
		return workOrders;
	}
	
	private  String getCurrentDateTimeMS() {
		java.util.Date dNow = new java.util.Date();
        SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy-hhmmssMs");
        String datetime = ft.format(dNow);
        return datetime;
    }

	@Override
	public ViewToDoStatus saveViewToDoStatus(ViewToDoStatus workOrder) {
		
		if (workOrder != null) {
			
			List<Task> taskList = new ArrayList<Task>();
			if (!StringUtils.isEmpty(workOrder.getExecutionPackage())
					&& !workOrder.getExecutionPackage().equals("PKG1")) {
				ExecutionPackage pkg = executionPackageDao.fetch(workOrder.getExecutionPackage());
				taskList.addAll(pkg.getTasks());
			} else {
				taskList.add(workOrderDAO.fetch(workOrder.getWorkOrders().get(0)));
			}
			
			for (Task task : taskList) {
				for (TodoAssignment todo : task.getTodoAssignments()) {
					for (au.com.wp.corp.p6.dto.ToDoAssignment assignmentDTO : workOrder.getTodoAssignments()) {
						if (!StringUtils.isEmpty(assignmentDTO.getToDoName())
								&& todoDAO.getToDoId(assignmentDTO.getToDoName()) != null
								&& todoDAO.getToDoId(assignmentDTO.getToDoName()).longValue() == todo.getTodoId().longValue()
								&& task.getTaskId().equals(assignmentDTO.getWorkOrderId())) {
							
							try {
								mergeToDoAssignment(todo, assignmentDTO);
							} catch (ParseException e) {
								logger.error("Parsing date failed: ", e);
							}
							continue;
						}
					}
				}
				workOrderDAO.saveTask(task);
			}
		}
		return workOrder;
	} 

	private void mergeToDoAssignment(TodoAssignment assignment, ToDoAssignment assignmentDTO) throws ParseException {
		assignment.setReqdByDt(dateUtils.toDateFromYYYY_MM_DD(assignmentDTO.getReqByDate()));
		assignment.setCmts(assignmentDTO.getComment());
		assignment.setStat(assignmentDTO.getStatus());
		assignment.setSuprtngDocLnk(assignmentDTO.getSupportingDoc());
		
	}

}
