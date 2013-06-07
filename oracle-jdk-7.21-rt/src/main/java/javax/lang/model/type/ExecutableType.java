package javax.lang.model.type;

import java.util.List;

public abstract interface ExecutableType extends TypeMirror
{
  public abstract List<? extends TypeVariable> getTypeVariables();

  public abstract TypeMirror getReturnType();

  public abstract List<? extends TypeMirror> getParameterTypes();

  public abstract List<? extends TypeMirror> getThrownTypes();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.lang.model.type.ExecutableType
 * JD-Core Version:    0.6.2
 */