package sun.awt.X11;

public abstract interface XMSelectionListener
{
  public abstract void ownerChanged(int paramInt, XMSelection paramXMSelection, long paramLong1, long paramLong2, long paramLong3);

  public abstract void ownerDeath(int paramInt, XMSelection paramXMSelection, long paramLong);

  public abstract void selectionChanged(int paramInt, XMSelection paramXMSelection, long paramLong, XPropertyEvent paramXPropertyEvent);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XMSelectionListener
 * JD-Core Version:    0.6.2
 */