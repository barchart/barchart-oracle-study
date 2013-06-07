package java.lang.reflect;

public abstract interface WildcardType extends Type
{
  public abstract Type[] getUpperBounds();

  public abstract Type[] getLowerBounds();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.reflect.WildcardType
 * JD-Core Version:    0.6.2
 */