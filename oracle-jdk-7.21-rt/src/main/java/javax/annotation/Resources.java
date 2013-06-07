package javax.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.TYPE})
public @interface Resources
{
  public abstract Resource[] value();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.annotation.Resources
 * JD-Core Version:    0.6.2
 */