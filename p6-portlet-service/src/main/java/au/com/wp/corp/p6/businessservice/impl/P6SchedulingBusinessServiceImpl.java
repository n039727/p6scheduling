package au.com.wp.corp.p6.businessservice.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.transaction.Transactional;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import au.com.wp.corp.p6.businessservice.P6SchedulingBusinessService;
import au.com.wp.corp.p6.dataservice.ExecutionPackageDao;
import au.com.wp.corp.p6.dataservice.TaskDAO;
import au.com.wp.corp.p6.dataservice.TodoDAO;
import au.com.wp.corp.p6.dataservice.WorkOrderDAO;
import au.com.wp.corp.p6.dto.ExecutionPackageDTO;
import au.com.wp.corp.p6.dto.TaskDTO;
import au.com.wp.corp.p6.dto.ToDoAssignment;
import au.com.wp.corp.p6.dto.ToDoItem;
import au.com.wp.corp.p6.dto.UserTokenRequest;
import au.com.wp.corp.p6.dto.ViewToDoStatus;
import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.dto.WorkOrderSearchRequest;
import au.com.wp.corp.p6.exception.P6BusinessException;
import au.com.wp.corp.p6.exception.P6DataAccessException;
import au.com.wp.corp.p6.model.ActivitySearchRequest;
import au.com.wp.corp.p6.model.ExecutionPackage;
import au.com.wp.corp.p6.model.Task;
import au.com.wp.corp.p6.model.TodoAssignment;
import au.com.wp.corp.p6.model.TodoTemplate;
import au.com.wp.corp.p6.utils.DateUtils;
import au.com.wp.corp.p6.wsclient.cleint.P6WSClient;

@Service
public class P6SchedulingBusinessServiceImpl implements P6SchedulingBusinessService {

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

	private static String ACTIONED_Y = "Y";
	private static String ACTIONED_N = "N";

	@Autowired
	UserTokenRequest userTokenRequest;
	
	@Autowired
	P6WSClient p6wsClient;
	
	
	@Override
	public List<WorkOrder> retrieveWorkOrders(WorkOrderSearchRequest input) throws P6BusinessException{
		logger.info("input date # {} ", input.getFromDate());
		ActivitySearchRequest searchRequest = new ActivitySearchRequest();
		searchRequest.setCrewList(input.getCrewList());
		searchRequest.setPlannedStartDate(dateUtils.convertDate(input.getFromDate()));
		List<WorkOrder> workOrders = p6wsClient.searchWorkOrder(searchRequest);
		logger.info("list of work orders from P6# {}", workOrders);
		return workOrders;

	}
 
	@Override
	public List<WorkOrder> search(WorkOrderSearchRequest input) throws P6BusinessException {
		logger.debug("User logged in as ======================================={}",userTokenRequest.getUserPrincipal());
		List<WorkOrder> listWOData = retrieveWorkOrders(input);
		Map<String,WorkOrder> mapOfExecutionPkgWO = new HashMap<>();
		List<WorkOrder> ungroupedWorkorders = new ArrayList<>();
		for (WorkOrder workOrder : listWOData) {
			List<String> workOrderNamesinGroup = new ArrayList<>();
			if (workOrder.getWorkOrders() != null) {
				for (String workOrderId : workOrder.getWorkOrders()) {
					Task dbTask = workOrderDAO.fetch(workOrderId);
					logger.debug("Rerieved task in db for the the given workder in String array {}",workOrderId);
					dbTask = dbTask == null ? new Task() : dbTask;
					dbTask.setSchdDt(dateUtils.toDateFromYYYY_MM_DD(workOrder.getScheduleDate()));
					updateTaskAgainstExecutionPackage(dbTask.getExecutionPackage());
					if (dbTask.getExecutionPackage() != null) {
						logger.debug("Execution package obtained ===={}",dbTask.getExecutionPackage());
						String dbWOExecPkg = dbTask.getExecutionPackage().getExctnPckgNam();
							if (mapOfExecutionPkgWO.containsKey(dbWOExecPkg)) {
								WorkOrder workOrdersalreadyinGroup = mapOfExecutionPkgWO.get(dbWOExecPkg);
								if(!workOrdersalreadyinGroup.getWorkOrders().contains(workOrderId)){
									workOrdersalreadyinGroup.getWorkOrders().add(workOrderId);
								}
							} else {
								WorkOrder workOrderNew = prepareWorkOrder(workOrder,dbTask.getExecutionPackage(),dbTask.getActioned());
								mapOfExecutionPkgWO.put(dbWOExecPkg, workOrderNew);
							}
					}else{
						//create separate work order list
						WorkOrder workOrderNew = new WorkOrder();
						workOrderNew.setWorkOrders(workOrder.getWorkOrders());
						workOrderNew.setCrewNames(workOrder.getCrewNames());
						workOrderNew.setScheduleDate(dateUtils.convertDateDDMMYYYY(workOrder.getScheduleDate()));
						workOrderNew.setActioned(dbTask.getActioned());
						workOrderNamesinGroup.add(dbTask.getTaskId());
						ungroupedWorkorders.add(workOrderNew);
					}
				}
			}
		}
		logger.debug("final grouped work orders size {}",mapOfExecutionPkgWO.values().size());
		logger.debug("final grouped work orders = {}",mapOfExecutionPkgWO.values());
		List<WorkOrder> workorders = new ArrayList<> (mapOfExecutionPkgWO.values());
		workorders.addAll(ungroupedWorkorders);
		return workorders;
	}


	private WorkOrder prepareWorkOrder(WorkOrder workOrder, ExecutionPackage executionPackage, String actioned) {
		WorkOrder workOrderNew = new WorkOrder();
		if (executionPackage != null) {
			Set<Task> tasks = executionPackage.getTasks();
			List<String> workOrderNamesinGroup = new ArrayList<String>();

			workOrderNew.setWorkOrders(workOrder.getWorkOrders());
			workOrderNew.setScheduleDate(dateUtils.convertDateDDMMYYYY(workOrder.getScheduleDate()));
			workOrderNew.setCrewNames(workOrder.getCrewNames());
			workOrderNew.setLeadCrew(workOrder.getLeadCrew());
			workOrderNew.setActioned(actioned);

			workOrderNew.setExctnPckgName(executionPackage.getExctnPckgNam());
			for (Task dbTask : tasks) {
				workOrderNamesinGroup.add(dbTask.getTaskId());
			}
			workOrderNew.setWorkOrders(workOrderNamesinGroup);
		}
		return workOrderNew;

	}

	/**
	 * Updates to remove task from execution package.
	 * @param executionPackage
	 * @throws P6DataAccessException
	 */
	private void updateTaskAgainstExecutionPackage(ExecutionPackage executionPackage) throws P6DataAccessException {
		if (executionPackage != null) {
			Set<Task> tasks = executionPackage.getTasks();
			StringBuilder crewPresent = new StringBuilder();
			if (tasks != null) {
				for (Iterator<Task> iterator = tasks.iterator(); iterator.hasNext();) {
					Task taskAttahced = (Task) iterator.next();
				
					Date plannedStartDate = taskAttahced.getSchdDt();
					String crewAssignedToTask = taskAttahced.getCrewId();
					logger.debug("crew assigned to this task{}", crewAssignedToTask);
					String executionPackageName = executionPackage.getExctnPckgNam();
					String[] strNames = StringUtils.split(executionPackageName, "_");
					if (strNames != null) {
						Date dateOfExectnPkg = dateUtils.toDateFromDD_MM_YYYY(strNames[0]);
						logger.debug("planned start date {} for task {}", plannedStartDate, taskAttahced.getTaskId());
						logger.debug("date {} for package  {}", dateOfExectnPkg, executionPackageName);
						if (dateOfExectnPkg.compareTo(plannedStartDate) != 0) {
							logger.debug("removing task {} from execution package", taskAttahced.getTaskId(),
									executionPackageName);
							iterator.remove();
							taskAttahced.setExecutionPackage(null);
						} else {
							crewPresent.append(crewAssignedToTask);
						}

					}

				}
				if (!StringUtils.contains(crewPresent, executionPackage.getLeadCrewId())) {
					executionPackage.setLeadCrewId("");
					//there are no crew assigned field
				}
				executionPackageDao.createOrUpdateExecPackage(executionPackage);
			}
		}
			
		
	}

	@Override
	@Transactional
	public List<TaskDTO> listTasks() throws P6BusinessException {
		List<Task> taskList = taskDAO.listTasks();
		List<TaskDTO> tasks = new ArrayList<>();
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
		List<ToDoItem> toDos = new ArrayList<>();
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
			toDos.add(item);
		}
		return toDos;
	}

	@Override
	public ViewToDoStatus fetchWorkOrdersForViewToDoStatus(WorkOrderSearchRequest query) {

		List<Task> tasks = null;
		//Map<String, ViewToDoStatus> taskIdWOMap = new HashMap<String, ViewToDoStatus>();
		Map<String,List<au.com.wp.corp.p6.dto.ToDoAssignment>> mapOfToDoIdWorkOrders = new HashMap<String,List<au.com.wp.corp.p6.dto.ToDoAssignment>>();
		ExecutionPackage executionPackage = null;
		ViewToDoStatus returnedVal = new ViewToDoStatus();
		if (null != query && null != query.getExecPckgName()) {
			executionPackage = executionPackageDao.fetch(query.getExecPckgName());
			tasks = new ArrayList<Task>(executionPackage.getTasks()); //multiple tasks
		} else { 
			tasks = workOrderDAO.fetchWorkOrdersForViewToDoStatus(query); //single task
		}
		for (Task task : tasks) {
			String key = null;
			if (task.getExecutionPackage() != null) {
				key = task.getExecutionPackage().getExctnPckgNam();
				returnedVal.setExctnPckgName(key);
			} else {
				key = task.getTaskId();
				returnedVal.setExctnPckgName("");
			}

			
			
			logger.debug("Key for fetch todo >>>{}", key);
			
			returnedVal.setSchedulingComment(task.getCmts());
			
			// TO DO
			Set<TodoAssignment> toDoEntities = task.getTodoAssignments();

			List<au.com.wp.corp.p6.dto.ToDoAssignment> assignmentDTOs = new ArrayList<au.com.wp.corp.p6.dto.ToDoAssignment>();
			if (null != toDoEntities) {
				logger.debug("Size of ToDoAssignment for task>>>{}", toDoEntities.size());
			}
			logger.debug("task id for each entry {}",task.getTaskId());
			for (TodoAssignment assignment : toDoEntities) {
				au.com.wp.corp.p6.dto.ToDoAssignment assignmentDTO = new au.com.wp.corp.p6.dto.ToDoAssignment();
				String workOrderId = task.getTaskId();
				long todoId = assignment.getTodoAssignMentPK().getTodoId().longValue();
				String toDoName = todoDAO.getToDoName(todoId);
				logger.debug("work order associated to each todo {} {}",toDoName,workOrderId);
				assignmentDTO.setComment(assignment.getCmts());
				if (null != assignment.getReqdByDt()) {
					assignmentDTO.setReqByDate(dateUtils.toStringDD_MM_YYYY(assignment.getReqdByDt()));
				}
				assignmentDTO.setStatus(assignment.getStat());
				assignmentDTO.setSupportingDoc(assignment.getSuprtngDocLnk());
				assignmentDTO.setWorkOrderId(workOrderId);
				assignmentDTO.setToDoName(toDoName);
				if (mapOfToDoIdWorkOrders.containsKey(toDoName)) { 
					/*	global map update against each todo across all tasks */
					logger.debug("retrived todoname =={} as key in global map with  list of AssignmentDto {}", toDoName,
							mapOfToDoIdWorkOrders.get(toDoName)
									.size());
					mapOfToDoIdWorkOrders.get(toDoName).add(assignmentDTO);
					
				} else {
					/*new entry added for this todo*/
					logger.debug("added  todoname =={} as key in global map with  entry  of AssignmentDto {}", toDoName,
							workOrderId);
					List<au.com.wp.corp.p6.dto.ToDoAssignment> assignments = new ArrayList<au.com.wp.corp.p6.dto.ToDoAssignment>();
					assignments.add(assignmentDTO);
					/*List<String> listOfWorkOrders = new ArrayList<String>();
					listOfWorkOrders.add(workOrderId)*/;
					mapOfToDoIdWorkOrders.put(toDoName, assignments);
				}
				assignmentDTOs.add(assignmentDTO);
			}
			/*if (status.getTodoAssignments() == null) {
				status.setTodoAssignments(new ArrayList<ToDoAssignment>());
			}
			status.getTodoAssignments().addAll(assignmentDTOs);
			logger.debug("Size of ToDoAssignment for task>>>{}", status.getTodoAssignments().size());
			
			//toDoStatuses.add(status);*/
		}
		Map<String,ToDoAssignment> mapOfGroupedTodoRecord = getGroupedTodowithWorkOrders(mapOfToDoIdWorkOrders);
		List<ToDoAssignment> listOfTodoAssignments = new ArrayList<ToDoAssignment>(mapOfGroupedTodoRecord.values());
		returnedVal.setTodoAssignments(listOfTodoAssignments);
		return returnedVal; 
	}

	/**
	 * Method to go through all the view to status's TodoAssignment entry 
	 * and update the list of workorders
	 * @param values
	 * @param mapOfToDoIdWorkOrders
	 */
	private Map<String,ToDoAssignment> getGroupedTodowithWorkOrders(Map<String, List<ToDoAssignment>> mapOfToDoIdWorkOrders) {

		Map<String, ToDoAssignment> todoMap = new HashMap<String, ToDoAssignment>();
		mapOfToDoIdWorkOrders.forEach((todoName, assignments) -> {
		   logger.debug("Merging for ToDo name {}, total assignments records count {}", todoName,
				   assignments.size());
			ToDoAssignment groupedTodoAssignment = groupTodoAssinmentRecord(assignments);
			String todo = groupedTodoAssignment.getToDoName();
			logger.debug("Adding to merged records for todo {} , merged {}", todo,
					groupedTodoAssignment.getWorkOrders().toArray());
			todoMap.put(todo, groupedTodoAssignment);
		});
			
		return todoMap;
	}
	/**
	 * To merge multiple ToDo records in single Todo records 
	 * for display
	 * @param assignments
	 * @return
	 */
	private ToDoAssignment groupTodoAssinmentRecord(List<ToDoAssignment> assignments) {
		ToDoAssignment singleMergedTodo = new ToDoAssignment();
		Set<String> workOrders = new HashSet<>();
		Set<String> requiredByDate = new HashSet<>();
		Set<String> status = new HashSet<String>();
		Set<String> comments = new HashSet<String>();
		Set<String> supDocLinks = new HashSet<String>();
		//String toDoName ;
		logger.debug("before starting loop for assignments size = {}",assignments);
		/*for (Iterator<ToDoAssignment> iterator = assignments.iterator(); iterator.hasNext();) {
			ToDoAssignment toDoAssignment = (ToDoAssignment) iterator.next();
			String toDoName = toDoAssignment.getToDoName();
			logger.debug("todo name {}",toDoName);
			singleMergedTodo.setToDoName(toDoName); //single todo name
			logger.debug("Grouping for ToDo = {}",toDoName);
			workOrders.add(toDoAssignment.getWorkOrderId());
			logger.debug("workOrder for this todo = {}",toDoAssignment.getWorkOrderId());
			String reqByDate = toDoAssignment.getReqByDate() == null ? "" :toDoAssignment.getReqByDate();
			String strStatus = toDoAssignment.getStatus() == null ? "": toDoAssignment.getStatus();
			boolean isNotSameReqByDate = requiredByDate.add(reqByDate);
			logger.debug("isSameReqByDate to be added for this todo {}",isNotSameReqByDate);
			boolean isNotSameStatus = status.add(strStatus);
			logger.debug("isNotSameStatus to be added for this todo {}",isNotSameStatus);
			comments.add(toDoAssignment.getComment()==null?"":toDoAssignment.getComment());
			supDocLinks.add(toDoAssignment.getSupportingDoc()==null?"":toDoAssignment.getSupportingDoc());
		}*/
		assignments.forEach(toDoAssignment->{
		String toDoName = toDoAssignment.getToDoName();
			logger.debug("todo name {}",toDoName);
			singleMergedTodo.setToDoName(toDoName); //single todo name
			logger.debug("Grouping for ToDo = {}",toDoName);
			workOrders.add(toDoAssignment.getWorkOrderId());
			logger.debug("workOrder for this todo = {}",toDoAssignment.getWorkOrderId());
			String reqByDate = toDoAssignment.getReqByDate() == null ? "" :toDoAssignment.getReqByDate();
			String strStatus = toDoAssignment.getStatus() == null ? "": toDoAssignment.getStatus();
			boolean isNotSameReqByDate = requiredByDate.add(reqByDate);
			logger.debug("isSameReqByDate to be added for this todo {}",isNotSameReqByDate);
			boolean isNotSameStatus = status.add(strStatus);
			logger.debug("isNotSameStatus to be added for this todo {}",isNotSameStatus);
			comments.add(toDoAssignment.getComment()==null?"":toDoAssignment.getComment());
			supDocLinks.add(toDoAssignment.getSupportingDoc()==null?"":toDoAssignment.getSupportingDoc());
		});
		
		singleMergedTodo.setWorkOrders(Arrays.asList(workOrders.toArray(new String[workOrders.size()])));
		if((requiredByDate.size() > 1) && (status.size() > 1)){
			singleMergedTodo.setReqByDate("");
			singleMergedTodo.setStatus("");
		}
		
		singleMergedTodo.setReqByDate(requiredByDate.iterator().next());
		singleMergedTodo.setStatus(status.iterator().next());
		if (ArrayUtils.isNotEmpty(comments.toArray(new String[comments.size()]))){
			singleMergedTodo.setComment(StringUtils.join(comments.toArray(new String[comments.size()]),","));
		}
		if (ArrayUtils.isNotEmpty(supDocLinks.toArray(new String[supDocLinks.size()]))){
			singleMergedTodo.setSupportingDoc(StringUtils.join(supDocLinks.toArray(new String[supDocLinks.size()]),","));
		}
		
		return singleMergedTodo;
	}

	/*
	 * @Override public WorkOrder saveToDo(WorkOrder workOrder) {
	 * todoDAO.saveToDos(workOrder); return workOrder; }
	 */

	@Override
	public WorkOrder saveToDo(WorkOrder workOrder) throws P6BusinessException {

		if (workOrder == null)
			throw new IllegalArgumentException("Work Order canot be null");

		if (workOrder.getWorkOrders() != null) {
			for (String workOrderId : workOrder.getWorkOrders()) {
				Task task = prepareTaskFromWorkOrderId(workOrderId, workOrder);
				if(null != task.getExecutionPackage()){
					logger.debug("task.getExecutionPackage()>> {}", task.getExecutionPackage().getActioned());
				}
				workOrderDAO.saveTask(task);
			}
		}

		return workOrder;
	}

	private Task prepareTaskFromWorkOrderId(String workOrderId, WorkOrder workOrder) throws P6BusinessException {
		Task dbTask = workOrderDAO.fetch(workOrderId);
		Task updatedTask = prepareTaskBean(dbTask, workOrder,workOrderId);
		prepareToDoAssignmentList(updatedTask, workOrder);
		return updatedTask;
	}

	private void prepareToDoAssignmentList(Task updatedTask, WorkOrder workOrder) {
		
		if (updatedTask == null)
			return;
		
		if (null != updatedTask.getTodoAssignments()) {
			for (Iterator<TodoAssignment> itr = updatedTask.getTodoAssignments().iterator(); itr.hasNext();) {
				TodoAssignment todo = itr.next();
				boolean found = false;
				logger.debug("todo.getTodoId(): " + todo.getTodoAssignMentPK().getTodoId());
				for (Iterator<ToDoItem> itrToDo = workOrder.getToDoItems().iterator(); itrToDo.hasNext();) {
					ToDoItem item = itrToDo.next();
					logger.debug("item.getTodoName(): " + item.getToDoName());
					if (item.getToDoName().equals(todoDAO.getToDoName(todo.getTodoAssignMentPK().getTodoId().longValue())) 
																										// check
																										// on
																										// long
																										// ID
							&& item.getWorkOrders().contains(updatedTask.getTaskId())) {
						item.getWorkOrders().remove(updatedTask.getTaskId());
						if (item.getWorkOrders().isEmpty()) {
							itrToDo.remove();
						}
						found = true;
						break;
					}
				}
				if (!found) {
					itr.remove();
				}
			}
		}	
		
		if (null != workOrder && null != workOrder.getToDoItems()) {
			Set<TodoAssignment> todos = new HashSet<>();
			// Set the new values
			for (Iterator<ToDoItem> itrToDo = workOrder.getToDoItems().iterator(); itrToDo.hasNext();) {
				ToDoItem item = itrToDo.next();
				if (null != item.getWorkOrders() && item.getWorkOrders().contains(updatedTask.getTaskId())) {
					TodoAssignment todoAssignment = new TodoAssignment();
					todoAssignment.getTodoAssignMentPK().setTask(updatedTask);
					//todoAssignment.setExecutionPackage(updatedTask.getExecutionPackage());
					logger.debug("Todo id for #{} - {}", item.getToDoName(), todoDAO.getToDoId(item.getToDoName()));
					todoAssignment.getTodoAssignMentPK().setTodoId(todoDAO.getToDoId(item.getToDoName()));
					todos.add(todoAssignment);
				}
			}
			if (updatedTask.getTodoAssignments() == null) {
				updatedTask.setTodoAssignments(new HashSet<TodoAssignment>());
			}
			updatedTask.getTodoAssignments().addAll(todos);
		}
	
		logger.debug("After merging to do assignments size: " + updatedTask.getTodoAssignments());
		logger.debug("After merging to do assignments: " + updatedTask.getTodoAssignments());
		
	}

	private Task prepareTaskBean(Task dbTask, WorkOrder workOrder, String workOrderId) {
		// create new Task if not there in DB
		if (dbTask == null) {
			dbTask = new Task();
			dbTask.setTaskId(workOrderId);
			
		}

		dbTask.setCmts(workOrder.getSchedulingToDoComment());
		dbTask.setCrewId(workOrder.getCrewNames());
		dbTask.setLeadCrewId(workOrder.getLeadCrew());
		java.util.Date scheduleDate = null;
		if(null != workOrder.getScheduleDate()){
			scheduleDate = dateUtils.toDateFromDD_MM_YYYY(workOrder.getScheduleDate());
		}
		dbTask.setSchdDt(scheduleDate);
		dbTask.setDepotId(workOrder.getDepotId());
		dbTask.setMatrlReqRef(workOrder.getMeterialReqRef());
		if ( null != workOrder.getExctnPckgName()){
			ExecutionPackage executionPackage = executionPackageDao.fetch(workOrder.getExctnPckgName());
			if(null != executionPackage){
				executionPackage.setActioned(ACTIONED_Y);
				dbTask.setExecutionPackage(executionPackage); 
				dbTask.setActioned(ACTIONED_N);
			}
			else{
				dbTask.setActioned(ACTIONED_Y);
			}
			logger.debug("Execution Package {}", workOrder.getExctnPckgName());
		}
		else{
			dbTask.setActioned(ACTIONED_Y);
		}
		return dbTask;
	}

	public ExecutionPackageDTO saveExecutionPackage(ExecutionPackageDTO executionPackageDTO)
			throws P6BusinessException {
		executionPackageDTO.setExctnPckgName(getCurrentDateTimeMS());
		executionPackageDTO = executionPackageDao.saveExecutionPackage(executionPackageDTO);
		return executionPackageDTO;
	}


	@Override
	public List<WorkOrder> fetchWorkOrdersForAddUpdateToDo(WorkOrderSearchRequest query) {

		List<Task> tasks = null;
		ExecutionPackage executionPackage = null;
		logger.debug("ExecPckgName >>>>{}", query.getExecPckgName());
		logger.debug("WorkOrderId >>>>{}", query.getWorkOrderId());
		if (null != query && null != query.getExecPckgName()) {
			executionPackage = executionPackageDao.fetch(query.getExecPckgName());
			tasks = new ArrayList<Task>(executionPackage.getTasks());
		} else {
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
			if (!StringUtils.isEmpty(executionPkg) && workOrderMap.containsKey(executionPkg)) {
				workOrder = workOrderMap.get(executionPkg);
				workOrder.setCrewNames(workOrder.getCrewNames() + "," + task.getCrewId());
				workOrder.getWorkOrders().add(task.getTaskId());
				toDoMap = workOrderToDoMap.get(executionPkg);
			} else {
				workOrder = new WorkOrder();
				if (!StringUtils.isEmpty(executionPkg))
					workOrder.setExctnPckgName(executionPkg);
				else
					executionPkg = task.getTaskId();
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
					Long todoId = todo.getTodoAssignMentPK().getTodoId().longValue();
					if (toDoMap.containsKey(todoId)) {
						toDoMap.get(todoId).getWorkOrders().add(todo.getTodoAssignMentPK().getTask().getTaskId());
					} else {
						ToDoItem item = new ToDoItem();
						item.setTodoId(String.valueOf(todoId));
						item.setToDoName(todoDAO.getToDoName(todoId));
						List<String> workOrders = new ArrayList<String>();
						workOrders.add(todo.getTodoAssignMentPK().getTask().getTaskId());
						item.setWorkOrders(workOrders);
						toDoMap.put(todoId, item);
					}
				}
				workOrderToDoMap.put(executionPkg, toDoMap);
			}
		}

		List<WorkOrder> workOrders = new ArrayList<WorkOrder>(workOrderMap.values());
		for (WorkOrder workOrder : workOrders) {
			String executionPkg = workOrder.getExctnPckgName();
			if (StringUtils.isEmpty(executionPkg)) {
				executionPkg = workOrder.getWorkOrders().get(0);
			}
			workOrder.setToDoItems(new ArrayList<ToDoItem>(workOrderToDoMap.get(executionPkg).values()));
		}
		return workOrders;
	}

	private String getCurrentDateTimeMS() {
		java.util.Date dNow = new java.util.Date();
		SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy-hhmmssMs");
		String datetime = ft.format(dNow);
		return datetime;
	}

	@Override
	public ViewToDoStatus saveViewToDoStatus(ViewToDoStatus workOrder) throws P6BusinessException {

		if (workOrder != null) {

			List<Task> taskList = new ArrayList<Task>();
			if (!StringUtils.isEmpty(workOrder.getExctnPckgName())
					&& !workOrder.getExctnPckgName().equals("PKG1")) {
				ExecutionPackage pkg = executionPackageDao.fetch(workOrder.getExctnPckgName());
				taskList.addAll(pkg.getTasks());
			} else {
				taskList.add(workOrderDAO.fetch(workOrder.getWorkOrders().get(0)));
			}

			for (Task task : taskList) {
				for (TodoAssignment todo : task.getTodoAssignments()) {
					for (au.com.wp.corp.p6.dto.ToDoAssignment assignmentDTO : workOrder.getTodoAssignments()) {
						
						if (!StringUtils.isEmpty(assignmentDTO.getToDoName())
								&& todoDAO.getToDoId(assignmentDTO.getToDoName()) != null
								&& todoDAO.getToDoId(assignmentDTO.getToDoName()).longValue() == todo.getTodoAssignMentPK().getTodoId()
										.longValue()
								&& assignmentDTO.getWorkOrders().contains(task.getTaskId())) {

							try {
								mergeToDoAssignment(todo, assignmentDTO);
							} catch (ParseException e) {
								logger.error("Parsing date failed: ", e);
							}
							
						}
					}
				}
				workOrderDAO.saveTask(task);
			}
		}
		return workOrder;
	}

	private void mergeToDoAssignment(TodoAssignment assignment, ToDoAssignment assignmentDTO) throws ParseException {
		if(!"".equalsIgnoreCase(assignmentDTO.getReqByDate())){
			assignment.setReqdByDt(dateUtils.toDateFromDD_MM_YYYY(assignmentDTO.getReqByDate()));
		}
		assignment.setCmts(assignmentDTO.getComment());
		assignment.setStat(assignmentDTO.getStatus());
		assignment.setSuprtngDocLnk(assignmentDTO.getSupportingDoc());

	}

}
