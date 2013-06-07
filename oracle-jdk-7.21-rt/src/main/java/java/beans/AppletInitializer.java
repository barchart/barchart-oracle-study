package java.beans;

import java.applet.Applet;
import java.beans.beancontext.BeanContext;

public abstract interface AppletInitializer
{
  public abstract void initialize(Applet paramApplet, BeanContext paramBeanContext);

  public abstract void activate(Applet paramApplet);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.AppletInitializer
 * JD-Core Version:    0.6.2
 */