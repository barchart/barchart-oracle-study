package java.beans;

import java.util.EventListener;

public abstract interface PropertyChangeListener extends EventListener
{
  public abstract void propertyChange(PropertyChangeEvent paramPropertyChangeEvent);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.PropertyChangeListener
 * JD-Core Version:    0.6.2
 */