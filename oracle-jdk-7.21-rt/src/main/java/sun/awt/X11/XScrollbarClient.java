package sun.awt.X11;

import java.awt.Component;

abstract interface XScrollbarClient
{
  public abstract void notifyValue(XScrollbar paramXScrollbar, int paramInt1, int paramInt2, boolean paramBoolean);

  public abstract Component getEventSource();

  public abstract void repaintScrollbarRequest(XScrollbar paramXScrollbar);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XScrollbarClient
 * JD-Core Version:    0.6.2
 */