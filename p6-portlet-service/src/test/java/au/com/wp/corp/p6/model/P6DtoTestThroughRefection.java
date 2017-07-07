/**
 * 
 */
package au.com.wp.corp.p6.model;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialClob;

import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.reflections.ReflectionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import au.com.wp.corp.p6.dto.ActivitySearchRequest;
import au.com.wp.corp.p6.dto.Crew;
import au.com.wp.corp.p6.dto.Depot;
import au.com.wp.corp.p6.dto.ErrorResponse;
import au.com.wp.corp.p6.dto.ExecutionPackageCreateRequest;
import au.com.wp.corp.p6.dto.ExecutionPackageDTO;
import au.com.wp.corp.p6.dto.MaterialRequisitionDTO;
import au.com.wp.corp.p6.dto.MaterialRequisitionRequest;
import au.com.wp.corp.p6.dto.MetadataDTO;
import au.com.wp.corp.p6.dto.ResourceDTO;
import au.com.wp.corp.p6.dto.ResourceSearchRequest;
import au.com.wp.corp.p6.dto.TaskDTO;
import au.com.wp.corp.p6.dto.ToDoAssignment;
import au.com.wp.corp.p6.dto.ToDoItem;
import au.com.wp.corp.p6.dto.UDFCreateRequest;
import au.com.wp.corp.p6.dto.UserTokenRequest;
import au.com.wp.corp.p6.dto.ViewToDoStatus;
import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.dto.WorkOrderSearchRequest;
import au.com.wp.corp.p6.test.config.AppConfig;

@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { AppConfig.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class P6DtoTestThroughRefection {

	@Test
	public void testDTOs() {
		Assert.assertEquals(true, (boolean) checkDTO(ActivitySearchRequest.class, new ActivitySearchRequest()));
		Assert.assertEquals(true, (boolean) checkDTO(Crew.class, new Crew()));
		Assert.assertEquals(true, (boolean) checkDTO(Depot.class, new Depot()));
		Assert.assertEquals(true, (boolean) checkDTO(ErrorResponse.class, new ErrorResponse()));
		Assert.assertEquals(true, (boolean) checkDTO(ExecutionPackageCreateRequest.class, new ExecutionPackageCreateRequest()));
		Assert.assertEquals(true, (boolean) checkDTO(ExecutionPackageDTO.class, new ExecutionPackageDTO()));
		Assert.assertEquals(true, (boolean) checkDTO(MaterialRequisitionDTO.class, new MaterialRequisitionDTO()));
		Assert.assertEquals(true, (boolean) checkDTO(MaterialRequisitionRequest.class, new MaterialRequisitionRequest()));
		Assert.assertEquals(true, (boolean) checkDTO(MetadataDTO.class, new MetadataDTO()));
		Assert.assertEquals(true, (boolean) checkDTO(ResourceDTO.class, new ResourceDTO()));
		Assert.assertEquals(true, (boolean) checkDTO(ResourceSearchRequest.class, new ResourceSearchRequest()));
		Assert.assertEquals(true, (boolean) checkDTO(TaskDTO.class, new TaskDTO()));
		Assert.assertEquals(true, (boolean) checkDTO(ToDoAssignment.class, new ToDoAssignment()));
		Assert.assertEquals(true, (boolean) checkDTO(ToDoItem.class, new ToDoItem()));
		Assert.assertEquals(true, (boolean) checkDTO(UDFCreateRequest.class, new UDFCreateRequest()));
		Assert.assertEquals(true, (boolean) checkDTO(UserTokenRequest.class, new UserTokenRequest()));
		Assert.assertEquals(true, (boolean) checkDTO(ViewToDoStatus.class, new ViewToDoStatus()));
		Assert.assertEquals(true, (boolean) checkDTO(WorkOrder.class, new WorkOrder()));
		Assert.assertEquals(true, (boolean) checkDTO(WorkOrderSearchRequest.class, new WorkOrderSearchRequest()));
	}
	public Boolean checkDTO(Class type, Object obj) {

		Boolean isOk = true;
		String s = null;
		try {
			Set<Method> setterMethods = ReflectionUtils.getAllMethods(type,
					ReflectionUtils.withModifier(Modifier.PUBLIC), ReflectionUtils.withPrefix("set"));
			Set<Method> getterMethods = new HashSet<Method>();
			getterMethods.addAll(ReflectionUtils.getAllMethods(type, ReflectionUtils.withModifier(Modifier.PUBLIC),
					ReflectionUtils.withPrefix("get")));
			getterMethods.addAll(ReflectionUtils.getAllMethods(type, ReflectionUtils.withModifier(Modifier.PUBLIC),
					ReflectionUtils.withPrefix("is")));

			for (Method method : setterMethods) {
				if (method.getParameterTypes()[0].getName().equals("long")
						|| method.getParameterTypes()[0].getName().equals("java.lang.Long")) {
					method.invoke(obj, 0l);
				} else if (method.getParameterTypes()[0].getName().equals("java.sql.Date")) {
					Date dt = new Date((new java.util.Date()).getTime());
					method.invoke(obj, dt);
				} else if (method.getParameterTypes()[0].getName().equals("java.lang.Integer")
						|| method.getParameterTypes()[0].getName().equals("int")) {
					method.invoke(obj, 0);
				} else if (method.getParameterTypes()[0].getName().equals("java.sql.Clob")) {
					Clob clob = new SerialClob("".toCharArray());
					method.invoke(obj, clob);
				} else if (method.getParameterTypes()[0].getName().equals("java.math.BigDecimal")) {
					BigDecimal bigDec = new BigDecimal(0);
					method.invoke(obj, bigDec);
				} else if (method.getParameterTypes()[0].getName().equals("java.sql.Timestamp")) {
					Timestamp ts = Timestamp.valueOf("2016-01-01 01:01:01");
					method.invoke(obj, ts);
				} else if (method.getParameterTypes()[0].getName().equals("short")) {
					short ts = 0;
					method.invoke(obj, ts);
				} else if (method.getParameterTypes()[0].getName().equals("byte")) {
					byte[] ts = "test".getBytes();
					method.invoke(obj, ts[0]);
				} else if (method.getParameterTypes()[0].getName().equals("float")) {
					float ts = 3.4f;
					method.invoke(obj, ts);
				} else if (method.getParameterTypes()[0].getName().equals("java.util.List")) {
					List<? extends Object> ts = new ArrayList<Object>();
					method.invoke(obj, ts);
				}  else if (method.getParameterTypes()[0].getName().equals("java.util.Map")) {
					Map<String,? extends Object> ts = new HashMap<String,Object>();
					method.invoke(obj, ts);
				} else if (method.getParameterTypes()[0].getName().equals("java.sql.Blob")) {
					Blob blob = new SerialBlob("abc".getBytes());
					method.invoke(obj, blob);
				} else if (method.getParameterTypes()[0].getName().equals("javax.xml.datatype.XMLGregorianCalendar")) {
					// do nothing
				} else if (method.getParameterTypes()[0].getName().equals("java.lang.InstantiationException")) {
					// do nothing
				} else if (method.getParameterTypes()[0].getName().equals("[B")) {
					byte[] bytes = "abc".getBytes();
					method.invoke(obj, bytes);
				} else if (method.getParameterTypes()[0].getName().equals("boolean")|| method.getParameterTypes()[0].getName().equals("java.lang.Boolean")) {
					boolean bl = false;
					method.invoke(obj, bl);
				} else if (method.getParameterTypes()[0].getName().equals("org.springframework.http.HttpStatus")) {
					HttpStatus bl = HttpStatus.OK;
					method.invoke(obj, bl);
				} else {
					s = method.getParameterTypes()[0].getName();
					method.invoke(obj, method.getParameterTypes()[0].newInstance());
				}
			}
			for (Method method : getterMethods) {
				method.invoke(obj);
			}

		} catch (Exception e) {
			return false;
		}

		if ((type.getClasses() != null) && (type.getClasses().length > 0)) {
			for (Class clazz : type.getClasses()) {
				try {
					checkDTO(clazz, clazz.newInstance());
				} catch (Exception e) {
					return false;
				}
			}
		}
		return isOk;
	}

	public Boolean checkObjectFactory(Class type, ObjectFactory obj) {
		Boolean isOk = true;
		String s = null;
		try {
			Set<Method> setterMethods = ReflectionUtils.getAllMethods(type,
					ReflectionUtils.withModifier(Modifier.PUBLIC), ReflectionUtils.withPrefix(""));
		

			for (Method method : setterMethods) {
				method.invoke(obj);
			}

			String toStringTest = obj.toString();

		} catch (Exception e) {
			System.out.println(s);
			e.printStackTrace();
			isOk = false;
		}

		return isOk;
	}
}
