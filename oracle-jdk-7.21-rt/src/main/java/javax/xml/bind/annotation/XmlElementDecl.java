package javax.xml.bind.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.METHOD})
public @interface XmlElementDecl
{
  public abstract Class scope();

  public abstract String namespace();

  public abstract String name();

  public abstract String substitutionHeadNamespace();

  public abstract String substitutionHeadName();

  public abstract String defaultValue();

  public static final class GLOBAL
  {
  }
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.xml.bind.annotation.XmlElementDecl
 * JD-Core Version:    0.6.2
 */