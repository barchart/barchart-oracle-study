package java.awt.event;

import java.util.EventListener;

public abstract interface InputMethodListener extends EventListener
{
  public abstract void inputMethodTextChanged(InputMethodEvent paramInputMethodEvent);

  public abstract void caretPositionChanged(InputMethodEvent paramInputMethodEvent);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.awt.event.InputMethodListener
 * JD-Core Version:    0.6.2
 */