package javax.lang.model.element;

import java.util.List;

public abstract interface Parameterizable extends Element
{
  public abstract List<? extends TypeParameterElement> getTypeParameters();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.lang.model.element.Parameterizable
 * JD-Core Version:    0.6.2
 */