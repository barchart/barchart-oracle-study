package javax.jws.soap;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.TYPE})
@Deprecated
public @interface SOAPMessageHandlers
{
  public abstract SOAPMessageHandler[] value();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.jws.soap.SOAPMessageHandlers
 * JD-Core Version:    0.6.2
 */