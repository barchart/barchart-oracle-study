package javax.lang.model.element;

public abstract interface PackageElement extends Element, QualifiedNameable
{
  public abstract Name getQualifiedName();

  public abstract Name getSimpleName();

  public abstract boolean isUnnamed();

  public abstract Element getEnclosingElement();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.lang.model.element.PackageElement
 * JD-Core Version:    0.6.2
 */