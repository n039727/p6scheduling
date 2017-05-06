package au.com.wp.corp.p6.dataservice.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import au.com.wp.corp.p6.dataservice.TodoDAO;
import au.com.wp.corp.p6.dto.ToDoItem;
import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.model.ExecutionPackage;
import au.com.wp.corp.p6.model.Task;
import au.com.wp.corp.p6.model.TodoAssignment;
import au.com.wp.corp.p6.model.TodoTemplate;

@Repository
public class TodoDAOImpl implements TodoDAO { 
	
	private static final Logger logger = LoggerFactory.getLogger(TodoDAO.class);
	@Autowired 
	SessionFactory sessionFactory;
	
	private volatile Map<Long, TodoTemplate> toDoMap = null;
	private volatile Map<String, TodoTemplate> toDoNameMap = null;
	private Object lock = new Object();
	
	
	@Transactional
	public List<TodoTemplate> fetchAllToDos() {

		if (toDoMap == null) {
			synchronized (lock) {
				if (toDoMap == null) {
					@SuppressWarnings("unchecked")
					List<TodoTemplate> listToDo = (List<TodoTemplate>) sessionFactory.getCurrentSession()
							.createCriteria(TodoTemplate.class)
							.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
					toDoMap = new HashMap<Long, TodoTemplate>();
					toDoNameMap = new HashMap<String, TodoTemplate>();
					for (TodoTemplate todo:listToDo) {
						toDoMap.put(todo.getTodoId().longValue(), todo);
						toDoNameMap.put(todo.getTodoNam(), todo);
					}
				}
			}
		}

		return new ArrayList<TodoTemplate>(toDoMap.values());
		
	}
	/**
	 * 
	 */
	@Transactional
	public WorkOrder saveToDos(WorkOrder workOrder) {
		logger.info("Entering method saveToDos");
		if (workOrder != null) {

			Map<String, List<ToDoItem>> mapOfTaskToDo = new HashMap<String, List<ToDoItem>>();
			// get ToDoItems in the WorkOrder
			List<ToDoItem> toDoItems = workOrder.getToDoItems();
			if (toDoItems != null && toDoItems.size() > 0) {
				// loop on each todo names in the list to create map of
				// task<>todo
				for (ToDoItem toDoItem : toDoItems) {
					// Get Linked work orders on each ToDO
					List<String> workOrdersInTodo = toDoItem.getWorkOrders();

					if (workOrdersInTodo != null && workOrdersInTodo.size() > 0)
						// loop on each linked WO under each ToDo
						for (String workInTodo : workOrdersInTodo) {
							logger.debug("workInTodo {}", workInTodo);
							if (mapOfTaskToDo.containsKey(workInTodo)) {
								logger.debug("Map has entry for work order {}", workInTodo);
								mapOfTaskToDo.get(workInTodo).add(toDoItem);
							} else {
								logger.debug("Map does not have entry for work order {}, so entering one", workInTodo);
								List<ToDoItem> toDoItemsList = new ArrayList<ToDoItem>();
								toDoItemsList.add(toDoItem);
								mapOfTaskToDo.put(workInTodo, toDoItemsList);
							}
						}
				}
			}

			saveOrUpdateTaskandTodo(mapOfTaskToDo, workOrder);
		}
		return workOrder;
	}
	/**
	 * 
	 * @param mapOfTaskToDo
	 * @param workOrder
	 */
	private void saveOrUpdateTaskandTodo(Map<String, List<ToDoItem>> mapOfTaskToDo, WorkOrder workOrder) {

		if (workOrder != null) {
			List<String> workOrders = workOrder.getWorkOrders();
			logger.debug("{} workorders received ", workOrders.size());
			String schedulingComments = workOrder.getSchedulingToDoComment();
			String crewNames = workOrder.getCrewNames();
			String leadCrew = workOrder.getLeadCrew();
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
			Date scheduleDate = null;
			try {
				scheduleDate = simpleDateFormat.parse(workOrder.getScheduleDate());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String depotId = workOrder.getDepotId();
			String meterialRefs = workOrder.getMeterialReqRef();
			logger.debug("Execution Package {}", workOrder.getExctnPckgName());
			@SuppressWarnings("unchecked")
			List<ExecutionPackage> executionPackage = (List<ExecutionPackage>) getRecordsByField("exctnPckgNam",
					ExecutionPackage.class,
					(workOrder.getExctnPckgName() == null ? "PKG1" : workOrder.getExctnPckgName()));

			logger.debug("Execution Package retrieved as {}", executionPackage.get(0));

			if (workOrders != null && workOrders.size() > 0) {
				// loop on each task id
				for (String taskName : workOrders) {
					Task task = null;
					logger.debug("Checking if Task alreday exists for taskname {}", taskName);
					task = (Task) sessionFactory.getCurrentSession().get(Task.class, taskName);
					if (task == null) {
						task = new Task();
					} else {
						logger.debug("Task alreday exists for taskname {}", task.getTaskId());
					}
					task.setExecutionPackage(executionPackage.get(0));
					task.setCmts(schedulingComments);
					task.setCrewId(crewNames);
					task.setLeadCrewId(leadCrew);
					task.setSchdDt(scheduleDate);
					task.setDepotId(depotId);
					task.setMatrlReqRef(meterialRefs);
					task.setTaskId(taskName);
					logger.debug("Creating Task for  {}", taskName);
					List<ToDoItem> toDoItemList = mapOfTaskToDo.get(taskName); // get
																				// the
																				// ToDoItesm
																				// for
																				// every
																				// task
																				// name
					if (toDoItemList != null && toDoItemList.size() > 0) {
						Set<TodoAssignment> assignments = new HashSet<TodoAssignment>();
						// TODO : for now deleting the existing records until
						// composite key is added in DB for taskname and todo id
						
						int recordsDeleted = deleteToDoAssignmentsByTaskToDo(taskName);
						logger.debug("Removing {} todo records for this task  {}", recordsDeleted, taskName);
						for (ToDoItem toDoItem : toDoItemList) {

							String toDoName = toDoItem.getToDoName();
							logger.debug("Adding Todo  item  {}", toDoName);
							List<TodoTemplate> todoTemplateRecord = (List<TodoTemplate>) getRecordsByField("todoNam",
									TodoTemplate.class, toDoName);

							logger.debug("Retrieving Todo  template  {}", todoTemplateRecord.get(0).getTodoNam());

							// logger.debug("Work order for ToDo {}
							// {}",toDoName,workInTodo);
							TodoAssignment todoAssignment = new TodoAssignment();
							todoAssignment.setCmts(toDoItem.getComments());
							//todoAssignment.setExecutionPackage(executionPackage.get(0));
							todoAssignment.setReqdByDt(toDoItem.getReqdByDate());
							todoAssignment.setStat(toDoItem.getStatus());
							todoAssignment.setSuprtngDocLnk(toDoItem.getSupportingDocLink());

							//todoAssignment.setTodoTemplate(todoTemplateRecord.get(0));
							todoAssignment.getTodoAssignMentPK().setTodoId(todoTemplateRecord.get(0).getTodoId());
							todoAssignment.getTodoAssignMentPK().setTask(task);
							todoAssignment.setCrtdTs(new Timestamp(System.currentTimeMillis()));
							todoAssignment.setCrtdUsr("test user");
							todoAssignment.setLstUpdtdTs(new Timestamp(System.currentTimeMillis()));
							todoAssignment.setLstUpdtdUsr("test user");
							assignments.add(todoAssignment);
							// }
						}

						task.setTodoAssignments(assignments);
					}

					task.setCrtdTs(new Timestamp(System.currentTimeMillis()));
					task.setCrtdUsr("test user");
					task.setLstUpdtdTs(new Timestamp(System.currentTimeMillis()));
					task.setLstUpdtdUsr("test user");
					sessionFactory.getCurrentSession().saveOrUpdate(task);
				}

			}

			sessionFactory.getCurrentSession().flush();
			sessionFactory.getCurrentSession().clear();
		}

	}
	private List<?> getRecordsByField(String fieldName, Class<?> className, String value){
		Criteria criteria = sessionFactory.getCurrentSession().
				 createCriteria(className);
		criteria.add(Restrictions.eq(fieldName, value));
		criteria.setFetchSize(1);
		List<?> retValue =  (List<?>) criteria.list();
		
		return retValue;
		
	}
	
	private int deleteToDoAssignmentsByTaskToDo(String taskName){
		Query query = sessionFactory.getCurrentSession().createQuery("delete from "
				+ "TodoAssignment todoAssignMnt where "
				+ "todoAssignMnt.task.taskId = :taskId ");
		query.setString("taskId", taskName);
		int records = query.executeUpdate();
		sessionFactory.getCurrentSession().flush();
		return records;
		
	}

	@Transactional
	@Override
	public List<TodoAssignment> fetchToDosByWorkOrder(WorkOrder workOrder) {
		logger.debug("Fetching todo list from TodoAssignment table for task id # {}", workOrder.getWorkOrders().get(0));
		Task task = new Task();
		task.setTaskId(workOrder.getWorkOrders().get(0));
		
        @SuppressWarnings("unchecked")
		List<TodoAssignment> listToDo = (List<TodoAssignment>) sessionFactory.getCurrentSession()
                .createCriteria(TodoAssignment.class)
                .add(Restrictions.eq("task", task))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
        logger.debug("Fetched todo list from TodoAssignment table for task id # {} and list of todos # {}", workOrder.getWorkOrders().get(0), listToDo);
        sessionFactory.getCurrentSession().flush();
        sessionFactory.getCurrentSession().clear();
        
        return listToDo;
	}
	
	
	@Transactional
	@Override
	public TodoAssignment fetchAssignmentWorkOrderNToDo(String workOrderId, String toDoName) {
		logger.debug("Fetching todo list from TodoAssignment table for task id # {}", workOrderId);
		Task task = new Task();
		task.setTaskId(workOrderId);
		
		TodoTemplate todo = toDoNameMap.get(toDoName);
		
        @SuppressWarnings("unchecked")
        TodoAssignment todoAssignment = (TodoAssignment)sessionFactory.getCurrentSession()
                .createCriteria(TodoAssignment.class)
                .add(Restrictions.eq("task", task))
                .add(Restrictions.eq("todoId", todo.getTodoId()))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).uniqueResult();
        
        sessionFactory.getCurrentSession().flush();
        sessionFactory.getCurrentSession().clear();
        
        return todoAssignment;
	}
	
	@Override
	public String getToDoName(Long id) {
		fetchAllToDos();
		if (toDoMap != null && toDoMap.containsKey(id)) {
			return toDoMap.get(id).getTodoNam();
		}
		return null;
	}
	
	@Override
	public TodoTemplate getTodoTemplate ( String todoName) {
		fetchAllToDos();
		return  toDoNameMap.get(todoName);
	}

	@Override
	public BigDecimal getToDoId(String todoName) {
		fetchAllToDos();
		if (toDoNameMap != null && toDoNameMap.containsKey(todoName)) {
			return toDoNameMap.get(todoName).getTodoId();
		}
		return null;
	}
	
	@Override
	@Transactional
	public TodoAssignment saveToDoAssignment(TodoAssignment todoAssign) {
		logger.info("Entering method saveToDo");
		if (todoAssign != null) {

			sessionFactory.getCurrentSession().saveOrUpdate(todoAssign);
		}
		return todoAssign;
	}

}
