package sun.plugin2.message;

import java.util.Map;

public abstract class EventMessage extends AppletMessage
{
  public static final int NPCocoaEventDrawRect = 1;
  public static final int NPCocoaEventMouseDown = 2;
  public static final int NPCocoaEventMouseUp = 3;
  public static final int NPCocoaEventMouseMoved = 4;
  public static final int NPCocoaEventMouseEntered = 5;
  public static final int NPCocoaEventMouseExited = 6;
  public static final int NPCocoaEventMouseDragged = 7;
  public static final int NPCocoaEventKeyDown = 8;
  public static final int NPCocoaEventKeyUp = 9;
  public static final int NPCocoaEventFlagsChanged = 10;
  public static final int NPCocoaEventFocusChanged = 11;
  public static final int NPCocoaEventWindowFocusChanged = 12;
  public static final int NPCocoaEventScrollWheel = 13;
  public static final int NPCocoaEventTextInput = 14;

  public EventMessage(int paramInt, Conversation paramConversation)
  {
    super(paramInt, paramConversation);
  }

  public EventMessage(int paramInt1, Conversation paramConversation, int paramInt2)
  {
    super(paramInt1, paramConversation, paramInt2);
  }

  public abstract void flattenInto(Map paramMap);
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.EventMessage
 * JD-Core Version:    0.6.2
 */