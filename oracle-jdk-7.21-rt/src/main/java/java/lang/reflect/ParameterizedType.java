package java.lang.reflect;

public abstract interface ParameterizedType extends Type
{
  public abstract Type[] getActualTypeArguments();

  public abstract Type getRawType();

  public abstract Type getOwnerType();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.reflect.ParameterizedType
 * JD-Core Version:    0.6.2
 */