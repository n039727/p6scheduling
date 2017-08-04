/**
 * 
 */
package au.com.wp.corp.p6.wsclient.cleint.qualifier;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.beans.factory.annotation.Qualifier;

@Documented
/**
 * @author N039126
 *
 */
@Retention(RUNTIME)
@Target({ TYPE, FIELD, METHOD, PARAMETER })
@Qualifier
public @interface CreateActivity {

}
