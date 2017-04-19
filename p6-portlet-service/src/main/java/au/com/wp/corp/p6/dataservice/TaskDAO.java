package au.com.wp.corp.p6.dataservice;

import java.util.List;

import au.com.wp.corp.p6.dto.ExecutionPackageDTO;
import au.com.wp.corp.p6.model.ExecutionPackage;
import au.com.wp.corp.p6.model.Task;

public interface TaskDAO {
	
	List<Task> listTasks();
	List<ExecutionPackage> listExecutionPackages();
	ExecutionPackageDTO saveExecutionPackage(ExecutionPackageDTO executionPackageDTO);
}
