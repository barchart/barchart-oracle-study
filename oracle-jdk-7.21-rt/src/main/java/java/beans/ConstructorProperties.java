package java.beans;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target({java.lang.annotation.ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConstructorProperties
{
  public abstract String[] value();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.ConstructorProperties
 * JD-Core Version:    0.6.2
 */