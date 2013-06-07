package org.w3c.dom.events;

public abstract interface EventTarget
{
  public abstract void addEventListener(String paramString, EventListener paramEventListener, boolean paramBoolean);

  public abstract void removeEventListener(String paramString, EventListener paramEventListener, boolean paramBoolean);

  public abstract boolean dispatchEvent(Event paramEvent)
    throws EventException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.w3c.dom.events.EventTarget
 * JD-Core Version:    0.6.2
 */