package sun.awt.X11;

abstract interface XDragSourceProtocolListener
{
  public abstract void handleDragReply(int paramInt);

  public abstract void handleDragReply(int paramInt1, int paramInt2, int paramInt3);

  public abstract void handleDragReply(int paramInt1, int paramInt2, int paramInt3, int paramInt4);

  public abstract void handleDragFinished();

  public abstract void handleDragFinished(boolean paramBoolean);

  public abstract void handleDragFinished(boolean paramBoolean, int paramInt);

  public abstract void handleDragFinished(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3);

  public abstract void cleanup(long paramLong);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XDragSourceProtocolListener
 * JD-Core Version:    0.6.2
 */