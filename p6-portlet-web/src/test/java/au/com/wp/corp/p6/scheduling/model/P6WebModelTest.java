/**
 * 
 */
package au.com.wp.corp.p6.scheduling.model;

import java.lang.reflect.Field;
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

import au.com.wp.corp.p6.scheduling.dto.UserAuthorizationDTO;
import au.com.wp.corp.p6.scheduling.dto.UserDetails;
import au.com.wp.corp.p6.test.config.AppConfig;

@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { AppConfig.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class P6WebModelTest {

	@Test
	public void testDTOs() {
		Assert.assertEquals(true, (boolean) checkDTO(FunctionAccess.class, new FunctionAccess()));
		Assert.assertEquals(true, (boolean) checkDTO(GroupRoleMapping.class, new GroupRoleMapping()));
		Assert.assertEquals(true, (boolean) checkDTO(PortalFunction.class, new PortalFunction()));
		Assert.assertEquals(true, (boolean) checkDTO(UserDetails.class, new UserDetails()));
		Assert.assertEquals(true, (boolean) checkDTO(UserAuthorizationDTO.class, new UserAuthorizationDTO()));
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

			Set<Field> fields = ReflectionUtils.getAllFields(type, ReflectionUtils.withModifier(Modifier.PRIVATE));
			Field[] fieldArray = new Field[fields.size()];
			fields.toArray(fieldArray);

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
				} else if (method.getParameterTypes()[0].getName().equals("boolean")) {
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
			String toStringTest = obj.toString();

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
			Set<Field> fields = ReflectionUtils.getAllFields(type, ReflectionUtils.withModifier(Modifier.PRIVATE));
			Field[] fieldArray = new Field[fields.size()];
			fields.toArray(fieldArray);

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
