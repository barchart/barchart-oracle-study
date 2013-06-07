package javax.accessibility;

public abstract interface AccessibleSelection
{
  public abstract int getAccessibleSelectionCount();

  public abstract Accessible getAccessibleSelection(int paramInt);

  public abstract boolean isAccessibleChildSelected(int paramInt);

  public abstract void addAccessibleSelection(int paramInt);

  public abstract void removeAccessibleSelection(int paramInt);

  public abstract void clearAccessibleSelection();

  public abstract void selectAllAccessibleSelection();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.accessibility.AccessibleSelection
 * JD-Core Version:    0.6.2
 */