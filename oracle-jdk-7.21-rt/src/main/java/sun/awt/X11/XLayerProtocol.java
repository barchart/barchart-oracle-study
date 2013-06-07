package sun.awt.X11;

public abstract interface XLayerProtocol
{
  public static final int LAYER_NORMAL = 0;
  public static final int LAYER_ALWAYS_ON_TOP = 1;

  public abstract boolean supportsLayer(int paramInt);

  public abstract void setLayer(XWindowPeer paramXWindowPeer, int paramInt);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XLayerProtocol
 * JD-Core Version:    0.6.2
 */