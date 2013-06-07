package java.awt.peer;

public abstract interface ScrollbarPeer extends ComponentPeer
{
  public abstract void setValues(int paramInt1, int paramInt2, int paramInt3, int paramInt4);

  public abstract void setLineIncrement(int paramInt);

  public abstract void setPageIncrement(int paramInt);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.awt.peer.ScrollbarPeer
 * JD-Core Version:    0.6.2
 */