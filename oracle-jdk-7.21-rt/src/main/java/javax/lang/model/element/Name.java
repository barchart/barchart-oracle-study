package javax.lang.model.element;

public abstract interface Name extends CharSequence
{
  public abstract boolean equals(Object paramObject);

  public abstract int hashCode();

  public abstract boolean contentEquals(CharSequence paramCharSequence);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.lang.model.element.Name
 * JD-Core Version:    0.6.2
 */