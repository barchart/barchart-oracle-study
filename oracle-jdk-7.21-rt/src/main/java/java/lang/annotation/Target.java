package java.lang.annotation;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE})
public @interface Target
{
  public abstract ElementType[] value();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.annotation.Target
 * JD-Core Version:    0.6.2
 */