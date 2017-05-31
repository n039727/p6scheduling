/**
 * 
 */
package au.com.wp.corp.p6.businessservice.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import au.com.wp.corp.p6.businessservice.DepotTodoService;
import au.com.wp.corp.p6.dataservice.ExecutionPackageDao;
import au.com.wp.corp.p6.dataservice.TodoDAO;
import au.com.wp.corp.p6.dataservice.WorkOrderDAO;
import au.com.wp.corp.p6.dto.ToDoAssignment;
import au.com.wp.corp.p6.dto.ToDoItem;
import au.com.wp.corp.p6.dto.ViewToDoStatus;
import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.dto.WorkOrderSearchRequest;
import au.com.wp.corp.p6.exception.P6BusinessException;
import au.com.wp.corp.p6.exception.P6DataAccessException;
import au.com.wp.corp.p6.model.ExecutionPackage;
import au.com.wp.corp.p6.model.Task;
import au.com.wp.corp.p6.model.TodoAssignment;
import au.com.wp.corp.p6.model.TodoTemplate;
import au.com.wp.corp.p6.utils.DateUtils;

/**
 * @author N039603
 *
 */
@Service
public class DepotTodoServiceImpl implements DepotTodoService {
	
	private static final Logger logger = LoggerFactory.getLogger(DepotTodoServiceImpl.class);
	
	@Autowired
	WorkOrderDAO workOrderDAO;
	
	@Autowired
	private ExecutionPackageDao executionPackageDao;
	
	@Autowired
	TodoDAO todoDAO;
	
	@Autowired
	DateUtils dateUtils;
	
	/* (non-Javadoc)
	 * @see au.com.wp.corp.p6.businessservice.DepotTodoService#fetchDepotTaskForViewToDoStatus(au.com.wp.corp.p6.dto.WorkOrderSearchRequest)
	 */
	@Transactional
	@Override
	public ViewToDoStatus fetchDepotTaskForViewToDoStatus(WorkOrderSearchRequest query) throws P6DataAccessException{
		
		List<Task> tasks = null;
		Map<String,List<au.com.wp.corp.p6.dto.ToDoAssignment>> toDoAssignments = new HashMap<String,List<au.com.wp.corp.p6.dto.ToDoAssignment>>();
		ExecutionPackage executionPackage = null;
		ViewToDoStatus viewToDoStatus = new ViewToDoStatus();
		if (null != query && (null != query.getExecPckgName() && (!"".equals(query.getExecPckgName())))) {
			executionPackage = executionPackageDao.fetch(query.getExecPckgName());
			tasks = new ArrayList<Task>(executionPackage.getTasks()); 
		} else { 
			tasks = workOrderDAO.fetchWorkOrdersForViewToDoStatus(query); 
		}
		for (Task task : tasks) {
			if (task.getExecutionPackage() != null) {
				viewToDoStatus.setExctnPckgName(task.getExecutionPackage().getExctnPckgNam());
				viewToDoStatus.setSchedulingComment(task.getExecutionPackage().getExecSchdlrCmt());
				viewToDoStatus.setDeportComment(task.getExecutionPackage().getExecDeptCmt());
			} else {
				viewToDoStatus.setExctnPckgName("");
				viewToDoStatus.setSchedulingComment(task.getCmts());
				viewToDoStatus.setDeportComment(task.getCmts());
			}

			Set<TodoAssignment> toDoEntities = task.getTodoAssignments();

			if (null != toDoEntities) {
				logger.debug("Size of ToDoAssignment for task>>>{}", toDoEntities.size());
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
					assignmentDTO.setTypeId(todoDAO.getTypeId(toDoName));
					
					if (toDoAssignments.containsKey(toDoName)) { 
						logger.debug("retrived todoname =={} and size of AssignmentDto {}", toDoName,
								toDoAssignments.get(toDoName)
										.size());
						toDoAssignments.get(toDoName).add(assignmentDTO);
						
					} else {
						logger.debug("adding  todoname =={} and AssignmentDto {}", toDoName,
								workOrderId);
						List<au.com.wp.corp.p6.dto.ToDoAssignment> assignments = new ArrayList<au.com.wp.corp.p6.dto.ToDoAssignment>();
						assignments.add(assignmentDTO);
						toDoAssignments.put(toDoName, assignments);
					}
				}
			}
			
		}
		Map<String, ToDoAssignment> todoMap = new HashMap<String, ToDoAssignment>();
		toDoAssignments.forEach((todoName, assignments) -> {
			   logger.debug("Merging for ToDo name {}, total assignments records count {}", todoName,
					   assignments.size());
				ToDoAssignment groupedTodoAssignment = groupTodoAssinmentRecord(assignments);
				String todo = groupedTodoAssignment.getToDoName();
				logger.debug("Adding to merged records for todo {} , merged {}", todo,
						groupedTodoAssignment.getWorkOrders().toArray());
				todoMap.put(todo, groupedTodoAssignment);
			});
		
		//Map<String,ToDoAssignment> mapOfGroupedTodoRecord = getGroupedTodowithWorkOrders(mapOfToDoIdWorkOrders);
		List<ToDoAssignment> listOfTodoAssignments = new ArrayList<ToDoAssignment>(todoMap.values());
		viewToDoStatus.setTodoAssignments(listOfTodoAssignments);
		return viewToDoStatus; 
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
		logger.debug("before starting loop for assignments size = {}",assignments);
		for (Iterator<ToDoAssignment> iterator = assignments.iterator(); iterator.hasNext();) {
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
		}
		
		singleMergedTodo.setWorkOrders(Arrays.asList(workOrders.toArray(new String[workOrders.size()])));
		singleMergedTodo.setTypeId(todoDAO.getTypeId(singleMergedTodo.getToDoName()));
		if(requiredByDate.size() > 1){
			singleMergedTodo.setReqByDate("");
			
		}
		if(status.size() > 1){
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
	
	/* (non-Javadoc)
	 * @see au.com.wp.corp.p6.businessservice.DepotTodoService#UpdateDepotToDo(au.com.wp.corp.p6.dto.ViewToDoStatus)
	 */
	@Transactional
	@Override
	public ViewToDoStatus UpdateDepotToDo(ViewToDoStatus workOrder) throws P6BusinessException{
		if (workOrder != null) {

			List<Task> taskList = new ArrayList<Task>();
			if (!StringUtils.isEmpty(workOrder.getExctnPckgName())) {
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
		if(null != assignmentDTO.getReqByDate() && !"".equals(assignmentDTO.getReqByDate())){
			assignment.setReqdByDt(dateUtils.toDateFromDD_MM_YYYY(assignmentDTO.getReqByDate()));
		}
		assignment.setCmts(assignmentDTO.getComment());
		assignment.setStat(assignmentDTO.getStatus());
		assignment.setSuprtngDocLnk(assignmentDTO.getSupportingDoc());

	}
	
	@Override
	@Transactional
	public WorkOrder saveDepotToDo(WorkOrder workOrder) throws P6BusinessException {

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
	
	private Task prepareTaskBean(Task dbTask, WorkOrder workOrder, String workOrderId) {
		// create new Task if not there in DB
		if (dbTask == null) {
			logger.debug("Task don't exists in portal DB..");
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
		if (!org.springframework.util.StringUtils.isEmpty(workOrder.getExctnPckgName())){
			ExecutionPackage executionPackage = executionPackageDao.fetch(workOrder.getExctnPckgName());
			if(null != executionPackage){
				executionPackage.setExecSchdlrCmt(workOrder.getExecutionPkgComment());
				dbTask.setExecutionPackage(executionPackage);
			}
			logger.debug("Execution Package {}", workOrder.getExctnPckgName());
		}
		return dbTask;
	}
	
	private void prepareToDoAssignmentList(Task updatedTask, WorkOrder workOrder) throws P6BusinessException {
		
		logger.debug("inside prepareToDoAssignmentList");
		if (updatedTask == null){
			return;
		}
		List<ToDoItem> requestToDos = workOrder.getToDoItems();
		Set<TodoAssignment> newToDos =  new HashSet<>();
		Set<TodoAssignment> deleteToDos =  new HashSet<>();
		ToDoItem reqToDoNeedsToDeAdded = null;
		Set<TodoAssignment> dBToDos = null;
		if(null != requestToDos && ! requestToDos.isEmpty()){
			logger.debug("requestToDos is not null");
			Set<TodoAssignment> existingToDos =  new HashSet<>();
			TodoAssignment dbToDo = null;
			for (ToDoItem reqToDo : requestToDos){
				boolean isExists = false;
				logger.debug("inside reqToDo for loop");
				if (null != updatedTask.getTodoAssignments() && !updatedTask.getTodoAssignments().isEmpty()) {
					logger.debug("updatedTask.getTodoAssignments() is not null");
					dBToDos= updatedTask.getTodoAssignments();
					for (Iterator<TodoAssignment> itr = dBToDos.iterator(); itr.hasNext();) {
						dbToDo = itr.next();
						logger.debug("inside dBToDos for loop");
						if (reqToDo.getToDoName().equals(todoDAO.getToDoName(dbToDo.getTodoAssignMentPK().getTodoId().longValue())) 
								&& reqToDo.getWorkOrders().contains(updatedTask.getTaskId())) {
							logger.debug("Todo in request exists in DB #{} ", reqToDo.getToDoName());
							isExists = true;
							break;
						}
						else{
							reqToDoNeedsToDeAdded = reqToDo;
						}
					}
					if(isExists){
						existingToDos.add(dbToDo);
					}
					else{
						if (reqToDoNeedsToDeAdded.getWorkOrders().contains(updatedTask.getTaskId())) {
							logger.debug("Todo in request do not exists in DB, needs to be added  #{} ", reqToDoNeedsToDeAdded.getToDoName());
							BigDecimal todoId = todoDAO.getToDoId(reqToDo.getToDoName());
							if(null == todoId){
								//create new TODO
								logger.debug("Todo Id is null and hence creating new  #{} ", todoId);
								TodoTemplate newToDo = addTodo(reqToDo);
								todoId = new BigDecimal(newToDo.getTodoId());
							}
							TodoAssignment todoAssignment = new TodoAssignment();
							todoAssignment.getTodoAssignMentPK().setTask(updatedTask);
							todoAssignment.getTodoAssignMentPK().setTodoId(todoId);
							newToDos.add(todoAssignment);
						}
					}
				}
				else{					
					logger.debug("No todo assignment exists for the task, adding new =={}", reqToDo.getToDoName());
					BigDecimal todoId = todoDAO.getToDoId(reqToDo.getToDoName());
					if(null == todoId){
						//create new TODO
						TodoTemplate newToDo = addTodo(reqToDo);
						todoId = new BigDecimal(newToDo.getTodoId());
					}
					TodoAssignment todoAssignment = new TodoAssignment();
					todoAssignment.getTodoAssignMentPK().setTask(updatedTask);
					todoAssignment.getTodoAssignMentPK().setTodoId(todoId);
					newToDos.add(todoAssignment);
				}
			}
			if(null == updatedTask.getTodoAssignments()){
				updatedTask.setTodoAssignments(new HashSet<TodoAssignment>());
			}
			
			if(null != dBToDos){
				for (TodoAssignment allDbToDo : dBToDos){
					if(existingToDos.add(allDbToDo)){
						//delete from DB
						logger.debug("TODO to be deleted from DB=={}", allDbToDo.getTodoAssignMentPK().getTodoId());
						deleteToDos.add(allDbToDo);
					}
				}
				for (TodoAssignment deleteDbToDo : deleteToDos){
					updatedTask.getTodoAssignments().remove(deleteDbToDo);
				}
			}
			updatedTask.getTodoAssignments().addAll(newToDos);
		}
		else{
			dBToDos= updatedTask.getTodoAssignments();
			if(null != dBToDos){
				
				for (TodoAssignment allDbToDo : dBToDos){
						logger.debug("TODO to be deleted from DB=={}", allDbToDo.getTodoAssignMentPK().getTodoId());
						deleteToDos.add(allDbToDo);
				}
				for (TodoAssignment deleteDbToDo : deleteToDos){
					updatedTask.getTodoAssignments().remove(deleteDbToDo);
				}
			}
		}
		

		logger.debug("After merging to do assignments size: " + updatedTask.getTodoAssignments());
		logger.debug("After merging to do assignments: " + updatedTask.getTodoAssignments());
		
		/*if (updatedTask == null)
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
				//to persist new user define TODO
				BigDecimal todoId = todoDAO.getToDoId(item.getToDoName());
				if(null == todoId){
					//create new TODO
					TodoTemplate newToDo = addTodo(item);
					//todoId = newToDo.getTodoId();
					todoId = new BigDecimal(newToDo.getId().getTodoId());
				}
				if (null != item.getWorkOrders() && item.getWorkOrders().contains(updatedTask.getTaskId())) {
					TodoAssignment todoAssignment = new TodoAssignment();
					todoAssignment.getTodoAssignMentPK().setTask(updatedTask);
					logger.debug("Todo id for #{} - {}", item.getToDoName(), todoId);
					todoAssignment.getTodoAssignMentPK().setTodoId(todoId);
					todos.add(todoAssignment);
				}
			}
			if (updatedTask.getTodoAssignments() == null) {
				updatedTask.setTodoAssignments(new HashSet<TodoAssignment>());
			}
			updatedTask.getTodoAssignments().addAll(todos);
		}
	
		logger.debug("After merging to do assignments size: " + updatedTask.getTodoAssignments());
		logger.debug("After merging to do assignments: " + updatedTask.getTodoAssignments());*/
		
	}
	
	private TodoTemplate addTodo(ToDoItem item) throws P6BusinessException {
		logger.debug("inside addTodo ");
		TodoTemplate todoTemplate = new TodoTemplate();
		todoTemplate.setCrtdTs(new Timestamp(System.currentTimeMillis()));
		todoTemplate.setCrtdUsr("N039603");
		todoTemplate.setLstUpdtdTs(new Timestamp(System.currentTimeMillis()));
		todoTemplate.setLstUpdtdUsr("N039603");
		todoTemplate.setTmpltDesc(item.getToDoName());
		todoTemplate.setTodoNam(item.getToDoName());
		todoTemplate.setTypId(new BigDecimal(2));
		todoTemplate.setTmpltId(2);
		
		todoDAO.createToDo(todoTemplate);
		logger.debug("ToDo Id and Template Id>>>{} , {]", todoTemplate.getTodoId(), todoTemplate.getTmpltId());
		return todoTemplate;
	}
	
	@Override
	@Transactional
	public List<WorkOrder> fetchDepotTaskForAddUpdateToDo(WorkOrderSearchRequest query) throws P6BusinessException {

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
				if (!StringUtils.isEmpty(executionPkg)){
					workOrder.setExctnPckgName(executionPkg);
					workOrder.setExecutionPkgComment(executionPackage.getExecSchdlrCmt());
				}
				
				else{
					executionPkg = task.getTaskId();
				}
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
						String toDoName = todoDAO.getToDoName(todoId);
						item.setToDoName(toDoName);
						item.setTypeId(todoDAO.getTypeId(toDoName));
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

}
