package javax.lang.model.element;

import java.util.Map;
import javax.lang.model.type.DeclaredType;

public abstract interface AnnotationMirror
{
  public abstract DeclaredType getAnnotationType();

  public abstract Map<? extends ExecutableElement, ? extends AnnotationValue> getElementValues();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.lang.model.element.AnnotationMirror
 * JD-Core Version:    0.6.2
 */