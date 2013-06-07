package java.lang.annotation;

public abstract interface Annotation
{
  public abstract boolean equals(Object paramObject);

  public abstract int hashCode();

  public abstract String toString();

  public abstract Class<? extends Annotation> annotationType();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.annotation.Annotation
 * JD-Core Version:    0.6.2
 */