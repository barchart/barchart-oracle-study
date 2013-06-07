package java.awt.peer;

import java.awt.Insets;

public abstract interface ContainerPeer extends ComponentPeer
{
  public abstract Insets getInsets();

  public abstract void beginValidate();

  public abstract void endValidate();

  public abstract void beginLayout();

  public abstract void endLayout();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.awt.peer.ContainerPeer
 * JD-Core Version:    0.6.2
 */