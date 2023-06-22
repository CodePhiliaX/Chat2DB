package ai.chat2db.server.web.api.aspect;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author moji
 * @version BusinessExceptionAspect.java, v 0.1 2022年10月10日 14:44 moji Exp $
 * @date 2022/10/10
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface BusinessExceptionAspect {
}
