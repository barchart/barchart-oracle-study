package javax.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.METHOD})
public @interface PreDestroy
{
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.annotation.PreDestroy
 * JD-Core Version:    0.6.2
 */