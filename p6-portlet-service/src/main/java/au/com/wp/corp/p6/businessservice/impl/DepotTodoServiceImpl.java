/**
 * 
 */
package au.com.wp.corp.p6.businessservice.impl;

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
import au.com.wp.corp.p6.dto.ViewToDoStatus;
import au.com.wp.corp.p6.dto.WorkOrderSearchRequest;
import au.com.wp.corp.p6.exception.P6BusinessException;
import au.com.wp.corp.p6.model.ExecutionPackage;
import au.com.wp.corp.p6.model.Task;
import au.com.wp.corp.p6.model.TodoAssignment;
import au.com.wp.corp.p6.utils.DateUtils;

/**
 * @author N039603
 *
 */
@Service
public class DepotTodoServiceImpl implements DepotTodoService {
	
	private static final Logger logger = LoggerFactory.getLogger(P6SchedulingBusinessServiceImpl.class);
	
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
	public ViewToDoStatus fetchDepotTaskForViewToDoStatus(WorkOrderSearchRequest query){
		
		List<Task> tasks = null;
		Map<String,List<au.com.wp.corp.p6.dto.ToDoAssignment>> toDoAssignments = new HashMap<String,List<au.com.wp.corp.p6.dto.ToDoAssignment>>();
		ExecutionPackage executionPackage = null;
		ViewToDoStatus viewToDoStatus = new ViewToDoStatus();
		if (null != query && null != query.getExecPckgName()) {
			executionPackage = executionPackageDao.fetch(query.getExecPckgName());
			tasks = new ArrayList<Task>(executionPackage.getTasks()); 
		} else { 
			tasks = workOrderDAO.fetchWorkOrdersForViewToDoStatus(query); 
		}
		for (Task task : tasks) {
			if (task.getExecutionPackage() != null) {
				viewToDoStatus.setExctnPckgName(task.getExecutionPackage().getExctnPckgNam());
			} else {
				viewToDoStatus.setExctnPckgName("");
			}
			viewToDoStatus.setSchedulingComment(task.getCmts());

			Set<TodoAssignment> toDoEntities = task.getTodoAssignments();

			//List<au.com.wp.corp.p6.dto.ToDoAssignment> assignmentDTOs = new ArrayList<au.com.wp.corp.p6.dto.ToDoAssignment>();
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
					//assignmentDTOs.add(assignmentDTO);
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

}
