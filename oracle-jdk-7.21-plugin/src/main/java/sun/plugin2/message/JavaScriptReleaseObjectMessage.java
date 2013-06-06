package sun.plugin2.message;

import sun.plugin2.liveconnect.BrowserSideObject;

public class JavaScriptReleaseObjectMessage extends JavaScriptBaseMessage
{
  public static final int ID = 28;
  private BrowserSideObject obj;
  private int appletID;

  public JavaScriptReleaseObjectMessage(Conversation paramConversation)
  {
    super(28, paramConversation);
  }

  public JavaScriptReleaseObjectMessage(Conversation paramConversation, BrowserSideObject paramBrowserSideObject, int paramInt)
  {
    super(28, paramConversation, paramBrowserSideObject, paramInt);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.JavaScriptReleaseObjectMessage
 * JD-Core Version:    0.6.2
 */