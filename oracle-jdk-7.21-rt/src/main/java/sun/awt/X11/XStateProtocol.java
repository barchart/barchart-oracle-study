package sun.awt.X11;

public abstract interface XStateProtocol
{
  public abstract boolean supportsState(int paramInt);

  public abstract void setState(XWindowPeer paramXWindowPeer, int paramInt);

  public abstract int getState(XWindowPeer paramXWindowPeer);

  public abstract boolean isStateChange(XPropertyEvent paramXPropertyEvent);

  public abstract void unshadeKludge(XWindowPeer paramXWindowPeer);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XStateProtocol
 * JD-Core Version:    0.6.2
 */