package java.lang.reflect;

public abstract interface TypeVariable<D extends GenericDeclaration> extends Type
{
  public abstract Type[] getBounds();

  public abstract D getGenericDeclaration();

  public abstract String getName();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.reflect.TypeVariable
 * JD-Core Version:    0.6.2
 */