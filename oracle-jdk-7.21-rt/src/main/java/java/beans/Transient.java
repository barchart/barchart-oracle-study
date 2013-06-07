package java.beans;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Transient
{
  public abstract boolean value();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.Transient
 * JD-Core Version:    0.6.2
 */