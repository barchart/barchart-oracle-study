package sun.plugin;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Component;

public class BeansApplet extends Applet
{
  Object bean;
  Component c;

  BeansApplet(Object paramObject)
  {
    this.bean = paramObject;
    if ((this.bean instanceof Component))
      this.c = ((Component)paramObject);
  }

  public void init()
  {
    if (this.c != null)
      setLayout(new BorderLayout());
  }

  public void start()
  {
    if (this.c != null)
      add(this.c);
  }

  public void stop()
  {
    if (this.c != null)
      remove(this.c);
  }

  public void destroy()
  {
    this.c = null;
    this.bean = null;
  }

  public Object getBean()
  {
    return this.bean;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.BeansApplet
 * JD-Core Version:    0.6.2
 */