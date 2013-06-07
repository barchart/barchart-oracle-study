package javax.lang.model.type;

import java.util.List;

public abstract interface UnionType extends TypeMirror
{
  public abstract List<? extends TypeMirror> getAlternatives();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.lang.model.type.UnionType
 * JD-Core Version:    0.6.2
 */