package sun.reflect.generics.scope;

import java.lang.reflect.TypeVariable;

public abstract interface Scope
{
  public abstract TypeVariable<?> lookup(String paramString);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.reflect.generics.scope.Scope
 * JD-Core Version:    0.6.2
 */