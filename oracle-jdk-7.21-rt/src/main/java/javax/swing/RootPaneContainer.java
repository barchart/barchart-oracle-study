package javax.swing;

import java.awt.Component;
import java.awt.Container;

public abstract interface RootPaneContainer
{
  public abstract JRootPane getRootPane();

  public abstract void setContentPane(Container paramContainer);

  public abstract Container getContentPane();

  public abstract void setLayeredPane(JLayeredPane paramJLayeredPane);

  public abstract JLayeredPane getLayeredPane();

  public abstract void setGlassPane(Component paramComponent);

  public abstract Component getGlassPane();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.RootPaneContainer
 * JD-Core Version:    0.6.2
 */