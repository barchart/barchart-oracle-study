package javax.jws;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.METHOD})
public @interface WebMethod
{
  public abstract String operationName();

  public abstract String action();

  public abstract boolean exclude();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.jws.WebMethod
 * JD-Core Version:    0.6.2
 */