package com.sun.org.glassfish.gmbal;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target({java.lang.annotation.ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface InheritedAttributes
{
  public abstract InheritedAttribute[] value();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.glassfish.gmbal.InheritedAttributes
 * JD-Core Version:    0.6.2
 */