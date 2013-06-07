package java.beans;

import java.util.EventListener;

public abstract interface VetoableChangeListener extends EventListener
{
  public abstract void vetoableChange(PropertyChangeEvent paramPropertyChangeEvent)
    throws PropertyVetoException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.VetoableChangeListener
 * JD-Core Version:    0.6.2
 */