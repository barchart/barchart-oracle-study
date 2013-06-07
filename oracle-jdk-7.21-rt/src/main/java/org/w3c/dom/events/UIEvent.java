package org.w3c.dom.events;

import org.w3c.dom.views.AbstractView;

public abstract interface UIEvent extends Event
{
  public abstract AbstractView getView();

  public abstract int getDetail();

  public abstract void initUIEvent(String paramString, boolean paramBoolean1, boolean paramBoolean2, AbstractView paramAbstractView, int paramInt);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.w3c.dom.events.UIEvent
 * JD-Core Version:    0.6.2
 */