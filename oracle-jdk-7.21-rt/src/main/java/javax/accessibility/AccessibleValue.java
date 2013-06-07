package javax.accessibility;

public abstract interface AccessibleValue
{
  public abstract Number getCurrentAccessibleValue();

  public abstract boolean setCurrentAccessibleValue(Number paramNumber);

  public abstract Number getMinimumAccessibleValue();

  public abstract Number getMaximumAccessibleValue();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.accessibility.AccessibleValue
 * JD-Core Version:    0.6.2
 */