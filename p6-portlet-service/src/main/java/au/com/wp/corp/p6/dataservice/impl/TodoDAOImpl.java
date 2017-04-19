package au.com.wp.corp.p6.dataservice.impl;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Criteria;
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

	@Transactional
	public List<TodoTemplate> fetchAllToDos() {

		logger.debug("sessionfactory initialized ====="+sessionFactory);
	        @SuppressWarnings("unchecked")
			List<TodoTemplate> listToDo = (List<TodoTemplate>) sessionFactory.getCurrentSession()
	                .createCriteria(TodoTemplate.class)
	                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	 
	        return listToDo;

	}
	
	@Transactional
	public WorkOrder saveToDos(WorkOrder workOrder) {
		logger.info("Entering method saveToDos");
        if(workOrder != null){
        
        	List<String> workOrders = workOrder.getWorkOrders();
        	if(workOrders != null && workOrders.size() > 0){
        		logger.debug("Work Orders size {}",workOrders.size());
        		for (String taskName : workOrders) {
        			Task task = new Task();
        			 Criteria criteriaExecutionPkg = sessionFactory.getCurrentSession().
							 createCriteria(ExecutionPackage.class);
        			 logger.debug("Execution Package {}",workOrder.getExecutionPackage());
					 criteriaExecutionPkg.add(Restrictions.eq("exctnPckgNam", workOrder.getExecutionPackage()));
					 List<ExecutionPackage> executionPackage =  (List<ExecutionPackage>) criteriaExecutionPkg.list();
					 logger.debug("Execution Package retrieved as {}",executionPackage.get(0));
					 task.setExecutionPackage(executionPackage.get(0));
        			task.setCmts(workOrder.getSchedulingToDoComment());
        			task.setCrewId(workOrder.getCrewNames());
        			task.setLeadCrewId(workOrder.getLeadCrew());
        			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-mm-yyyy");
        			Date scheduleDate = null;
					try {
						scheduleDate = simpleDateFormat.parse(workOrder.getScheduleDate());
						task.setSchdDt(scheduleDate);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        			
        			task.setDepotId(workOrder.getDepotId());
        			task.setMatrlReqRef(workOrder.getMeterialReqRef());
        			task.setTaskId(taskName);
        			 logger.debug("Creating Task for  {}",taskName);
        			List<ToDoItem> toDoItems = workOrder.getToDoItems();
        			List<TodoAssignment> assignments = new ArrayList<TodoAssignment>();
        			logger.debug("Todo items  {}",toDoItems);
        			if(toDoItems != null && toDoItems.size() > 0){
        				for (ToDoItem toDoItem : toDoItems) {
        					String toDoName = toDoItem.getTodoName();
        					logger.debug("Adding Todo  item  {}",toDoName);
        					List<String> workOrdersInTodo = toDoItem.getWorkOrders();
        					//if(workOrdersInTodo.contains(taskName)){
        					
        						for (String workInTodo : workOrdersInTodo) {
        							if(taskName.equals(workInTodo)){
        								logger.debug("{} is attached with {}",taskName,toDoName);
	        							TodoAssignment todoAssignment = new TodoAssignment();
	            						todoAssignment.setCmts(toDoItem.getComments());
	            						todoAssignment.setExecutionPackage(executionPackage.get(0));
	            						todoAssignment.setReqdByDt(toDoItem.getReqdByDate());
	            						todoAssignment.setStat(toDoItem.getStatus());
	            						todoAssignment.setSuprtngDocLnk(toDoItem.getSupportingDocLink());
	            						
	            						 Criteria criteriaToDo = sessionFactory.getCurrentSession().
	            								 createCriteria(TodoTemplate.class);
	            						 criteriaToDo.add(Restrictions.eqOrIsNull("todoNam", toDoName));
	            						 List<TodoTemplate> todoTemplateRecord =  (List<TodoTemplate>) criteriaToDo.list();
	            						 logger.debug("Retrieving Todo  template  {}",todoTemplateRecord.get(0));
	            						todoAssignment.setTodoTemplate(todoTemplateRecord.get(0));
	            						todoAssignment.setTodoId(todoTemplateRecord.get(0).getTodoId());
	            						todoAssignment.setTask(task);
	            						todoAssignment.setCrtdTs(new Timestamp(System.currentTimeMillis()));
	            						todoAssignment.setCrtdUsr("test user");
	            						todoAssignment.setLstUpdtdTs(new Timestamp(System.currentTimeMillis()));
	            						todoAssignment.setLstUpdtdUsr("test user");
	            						assignments.add(todoAssignment);
        							}
								}
        						
        					//}
						}
        				task.setTodoAssignments(assignments);
        				task.setCrtdTs(new Timestamp(System.currentTimeMillis()));
        				task.setCrtdUsr("test user");
        				task.setLstUpdtdTs(new Timestamp(System.currentTimeMillis()));
        				task.setLstUpdtdUsr("test user");
        				sessionFactory.getCurrentSession().saveOrUpdate(task);
        			}
				}
        	}
        	sessionFactory.getCurrentSession().flush();
        	sessionFactory.getCurrentSession().clear();
        }
		
        return workOrder;
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

}
