package sun.awt.X11;

public abstract interface XModalityProtocol
{
  public abstract boolean setModal(XDialogPeer paramXDialogPeer, boolean paramBoolean);

  public abstract boolean isBlocked(XDialogPeer paramXDialogPeer, XWindowPeer paramXWindowPeer);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XModalityProtocol
 * JD-Core Version:    0.6.2
 */