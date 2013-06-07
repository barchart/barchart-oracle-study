package java.awt.event;

import java.util.EventListener;

public abstract interface WindowListener extends EventListener
{
  public abstract void windowOpened(WindowEvent paramWindowEvent);

  public abstract void windowClosing(WindowEvent paramWindowEvent);

  public abstract void windowClosed(WindowEvent paramWindowEvent);

  public abstract void windowIconified(WindowEvent paramWindowEvent);

  public abstract void windowDeiconified(WindowEvent paramWindowEvent);

  public abstract void windowActivated(WindowEvent paramWindowEvent);

  public abstract void windowDeactivated(WindowEvent paramWindowEvent);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.awt.event.WindowListener
 * JD-Core Version:    0.6.2
 */