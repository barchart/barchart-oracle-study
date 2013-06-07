package java.awt.peer;

import java.awt.Window;
import java.util.List;

public abstract interface DialogPeer extends WindowPeer
{
  public abstract void setTitle(String paramString);

  public abstract void setResizable(boolean paramBoolean);

  public abstract void blockWindows(List<Window> paramList);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.awt.peer.DialogPeer
 * JD-Core Version:    0.6.2
 */