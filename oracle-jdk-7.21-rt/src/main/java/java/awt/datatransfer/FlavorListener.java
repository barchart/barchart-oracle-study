package java.awt.datatransfer;

import java.util.EventListener;

public abstract interface FlavorListener extends EventListener
{
  public abstract void flavorsChanged(FlavorEvent paramFlavorEvent);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.awt.datatransfer.FlavorListener
 * JD-Core Version:    0.6.2
 */