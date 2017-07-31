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
import au.com.wp.corp.p6.businessservice.IExecutionPackageService;
import au.com.wp.corp.p6.dataservice.ExecutionPackageDao;
import au.com.wp.corp.p6.dataservice.TodoDAO;
import au.com.wp.corp.p6.dataservice.WorkOrderDAO;
import au.com.wp.corp.p6.dto.ExecutionPackageDTO;
import au.com.wp.corp.p6.dto.ToDoAssignment;
import au.com.wp.corp.p6.dto.ToDoItem;
import au.com.wp.corp.p6.dto.UserTokenRequest;
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
    private IExecutionPackageService executionPackageService;
	
	@Autowired
	TodoDAO todoDAO;
	
	@Autowired
	DateUtils dateUtils;
	
	@Autowired
	UserTokenRequest userTokenRequest;
	
	private static String ACTIONED_Y = "Y";
	private static String ACTIONED_N = "N";
	
	/* (non-Javadoc)
	 * @see au.com.wp.corp.p6.businessservice.DepotTodoService#fetchDepotTaskForViewToDoStatus(au.com.wp.corp.p6.dto.WorkOrderSearchRequest)
	 */
	@Transactional
	@Override
	public ViewToDoStatus fetchDepotTaskForViewToDoStatus(WorkOrderSearchRequest query) throws P6DataAccessException{
		
		List<Task> tasks;
		Map<String,List<au.com.wp.corp.p6.dto.ToDoAssignment>> toDoAssignments = new HashMap();
		ExecutionPackage executionPackage;
		ViewToDoStatus viewToDoStatus = new ViewToDoStatus();
		if (null != query && (null != query.getExecPckgName() && (!"".equals(query.getExecPckgName())))) {
			executionPackage = executionPackageDao.fetch(query.getExecPckgName());
			tasks = new ArrayList(executionPackage.getTasks()); 
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
				viewToDoStatus.setSchedulingComment(task.getSchdlrCmt());
				viewToDoStatus.setDeportComment(task.getDeptCmt());
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
						List<au.com.wp.corp.p6.dto.ToDoAssignment> assignments = new ArrayList();
						assignments.add(assignmentDTO);
						toDoAssignments.put(toDoName, assignments);
					}
				}
			}
			
		}
		Map<String, ToDoAssignment> todoMap = new HashMap();
		toDoAssignments.forEach((todoName, assignments) -> {
			   logger.debug("Merging for ToDo name {}, total assignments records count {}", todoName,
					   assignments.size());
				ToDoAssignment groupedTodoAssignment = groupTodoAssinmentRecord(assignments);
				String todo = groupedTodoAssignment.getToDoName();
				logger.debug("Adding to merged records for todo {} , merged {}", todo,
						groupedTodoAssignment.getWorkOrders().toArray());
				todoMap.put(todo, groupedTodoAssignment);
			});
		
		List<ToDoAssignment> listOfTodoAssignments = new ArrayList(todoMap.values());
		viewToDoStatus.setTodoAssignments(listOfTodoAssignments);
		return viewToDoStatus; 
	}
	
	/**
	 * To merge multiple ToDo records in single to-do records 
	 * for display
	 * @param assignments
	 * @return
	 */
	private ToDoAssignment groupTodoAssinmentRecord(List<ToDoAssignment> assignments) {
		ToDoAssignment singleMergedTodo = new ToDoAssignment();
		Set<String> workOrders = new HashSet<>();
		Set<String> requiredByDate = new HashSet<>();
		Set<String> status = new HashSet();
		Set<String> comments = new HashSet();
		Set<String> supDocLinks = new HashSet();
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

			List<Task> taskList = new ArrayList();
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
		List<ExecutionPackageDTO> executionPackageDTOList = new ArrayList();
		ExecutionPackageDTO executionPackageDTO = null;
		List<WorkOrder> workOrderList = new ArrayList<>();
		if (workOrder.getWorkOrders() != null) {
			for (String workOrderId : workOrder.getWorkOrders()) {
				WorkOrder wo = new WorkOrder();
				Task task = prepareTaskFromWorkOrderId(workOrderId, workOrder);
				if(null != task.getExecutionPackage()){
					logger.debug("task.getExecutionPackage()>> {}", task.getExecutionPackage().getExctnPckgNam());
					executionPackageDTO = new ExecutionPackageDTO();
					executionPackageDTO.setExctnPckgName(task.getExecutionPackage().getExctnPckgNam());
					workOrderList.add(wo);
				}
				workOrderDAO.saveTask(task);
			}
			if(executionPackageDTO != null){
				executionPackageDTO.setWorkOrders(workOrderList);
				executionPackageDTOList.add(executionPackageDTO);
				executionPackageService.setExecutionPackageDTDOFoP6(executionPackageDTOList);
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
		dbTask.setSchdlrCmt(workOrder.getSchedulingToDoComment());
		dbTask.setDeptCmt(workOrder.getDepotToDoComment());
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
				executionPackage.setActioned(ACTIONED_Y);
				executionPackage.setExecDeptCmt(workOrder.getDepotToDoComment());
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
		String  userName = "";
		if(userTokenRequest != null && userTokenRequest.getUserPrincipal() != null){
			userName = userTokenRequest.getUserPrincipal();
		}
		dbTask.setCrtdUsr(userName);
		dbTask.setLstUpdtdUsr(userName);
		return dbTask;
	}
	
	private void prepareToDoAssignmentList(Task updatedTask, WorkOrder workOrder) throws P6BusinessException {

		logger.debug("inside prepareToDoAssignmentList");
		if (updatedTask == null) {
			return;
		}
		List<ToDoItem> requestToDos = workOrder.getToDoItems();
		Set<TodoAssignment> newToDos = new HashSet<>();
		Set<TodoAssignment> deleteToDos = new HashSet<>();
		Set<TodoAssignment> dBToDos = updatedTask.getTodoAssignments();
		if (null != requestToDos && !requestToDos.isEmpty()) {

			logger.debug("requestToDos is not null");
			for (ToDoItem reqToDo : requestToDos) {
				if (reqToDo.getWorkOrders().contains(updatedTask.getTaskId())) {
					TodoAssignment todoAssignment = new TodoAssignment();
					todoAssignment.getTodoAssignMentPK().setTask(updatedTask);
					BigDecimal todoId = todoDAO.getToDoId(reqToDo.getToDoName());
					if (null == todoId) {
						// create new TODO
						logger.debug("Todo Id is null and hence creating new  #{} ", todoId);
						TodoTemplate newToDo = addTodo(reqToDo);
						todoId = new BigDecimal(newToDo.getTodoId());
					}
					todoAssignment.getTodoAssignMentPK().setTodoId(todoId);
					newToDos.add(todoAssignment);
				}
			}
			if (null != dBToDos && !dBToDos.isEmpty()) {
				logger.debug("updatedTask.getTodoAssignments() is not null");
				updatedTask.getTodoAssignments().retainAll(newToDos);
				updatedTask.getTodoAssignments().addAll(newToDos);
			} else {
				if (updatedTask.getTodoAssignments() != null) {
					updatedTask.getTodoAssignments().addAll(newToDos);
				} else {
					// this is only required for JUNIT
					updatedTask.setTodoAssignments(newToDos);
				}
			}
			//updatedTask.setActioned(ACTIONED_Y);
		} else {
			dBToDos = updatedTask.getTodoAssignments();
			if (null != dBToDos) {

				for (TodoAssignment allDbToDo : dBToDos) {
					logger.debug("TODO to be deleted from DB=={}", allDbToDo.getTodoAssignMentPK().getTodoId());
					deleteToDos.add(allDbToDo);
				}
				for (TodoAssignment deleteDbToDo : deleteToDos) {
					updatedTask.getTodoAssignments().remove(deleteDbToDo);
				}
			}
			//updatedTask.setActioned(ACTIONED_N);
		}

		logger.debug("After merging to do assignments size: " + updatedTask.getTodoAssignments());
	}
	
	private TodoTemplate addTodo(ToDoItem item) throws P6BusinessException {
		logger.debug("inside addTodo ");
		String  userName = "";
		if(userTokenRequest != null && userTokenRequest.getUserPrincipal() != null){
			userName = userTokenRequest.getUserPrincipal();
		}
		TodoTemplate todoTemplate = new TodoTemplate();
		todoTemplate.setCrtdTs(new Timestamp(System.currentTimeMillis()));
		todoTemplate.setCrtdUsr(userName);
		todoTemplate.setLstUpdtdTs(new Timestamp(System.currentTimeMillis()));
		todoTemplate.setLstUpdtdUsr(userName);
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

		List<Task> tasks;
		ExecutionPackage executionPackage = null;
		logger.debug("ExecPckgName >>>>{}", query.getExecPckgName());
		logger.debug("WorkOrderId >>>>{}", query.getWorkOrderId());
		if (null != query && null != query.getExecPckgName()) {
			executionPackage = executionPackageDao.fetch(query.getExecPckgName());
			tasks = new ArrayList(executionPackage.getTasks());
		} else {
			tasks = workOrderDAO.fetchWorkOrdersForViewToDoStatus(query);
		}

		Map<String, WorkOrder> workOrderMap = new HashMap();
		Map<String, Map<Long, ToDoItem>> workOrderToDoMap = new HashMap();

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
					workOrder.setDepotToDoComment(executionPackage.getExecDeptCmt());
				}
				
				else{
					executionPkg = task.getTaskId();
				}
				List<String> workOrders = new ArrayList();
				workOrders.add(task.getTaskId());
				workOrder.setWorkOrders(workOrders);
				workOrder.setLeadCrew(task.getLeadCrewId());
				workOrder.setCrewNames(task.getCrewId());
				workOrder.setScheduleDate(task.getSchdDt().toString());
				workOrder.setSchedulingToDoComment(task.getSchdlrCmt());
				if (StringUtils.isEmpty(workOrder.getDepotToDoComment())) {
					workOrder.setDepotToDoComment(task.getDeptCmt());
				}
				toDoMap = new HashMap();
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
						List<String> workOrders = new ArrayList();
						workOrders.add(todo.getTodoAssignMentPK().getTask().getTaskId());
						item.setWorkOrders(workOrders);
						toDoMap.put(todoId, item);
					}
				}
				workOrderToDoMap.put(executionPkg, toDoMap);
			}
		}

		List<WorkOrder> workOrders = new ArrayList(workOrderMap.values());
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
