package java.awt.peer;

import java.awt.Dimension;

public abstract interface TextFieldPeer extends TextComponentPeer
{
  public abstract void setEchoChar(char paramChar);

  public abstract Dimension getPreferredSize(int paramInt);

  public abstract Dimension getMinimumSize(int paramInt);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.awt.peer.TextFieldPeer
 * JD-Core Version:    0.6.2
 */