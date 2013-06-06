package sun.plugin2.message;

import sun.plugin2.liveconnect.BrowserSideObject;

public class JavaScriptToStringMessage extends JavaScriptBaseMessage
{
  public static final int ID = 26;

  public JavaScriptToStringMessage(Conversation paramConversation)
  {
    super(26, paramConversation);
  }

  public JavaScriptToStringMessage(Conversation paramConversation, BrowserSideObject paramBrowserSideObject, int paramInt)
  {
    super(26, paramConversation, paramBrowserSideObject, paramInt);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.JavaScriptToStringMessage
 * JD-Core Version:    0.6.2
 */