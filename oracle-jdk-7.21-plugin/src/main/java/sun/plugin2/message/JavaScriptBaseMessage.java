package sun.plugin2.message;

import java.io.IOException;
import sun.plugin2.liveconnect.BrowserSideObject;

public abstract class JavaScriptBaseMessage extends AppletMessage
{
  private BrowserSideObject object;

  protected JavaScriptBaseMessage(int paramInt, Conversation paramConversation)
  {
    super(paramInt, paramConversation);
  }

  protected JavaScriptBaseMessage(int paramInt1, Conversation paramConversation, BrowserSideObject paramBrowserSideObject, int paramInt2)
  {
    super(paramInt1, paramConversation, paramInt2);
    this.object = paramBrowserSideObject;
  }

  public void writeFields(Serializer paramSerializer)
    throws IOException
  {
    super.writeFields(paramSerializer);
    BrowserSideObject.write(paramSerializer, this.object);
  }

  public void readFields(Serializer paramSerializer)
    throws IOException
  {
    super.readFields(paramSerializer);
    this.object = BrowserSideObject.read(paramSerializer);
  }

  public BrowserSideObject getObject()
  {
    return this.object;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.JavaScriptBaseMessage
 * JD-Core Version:    0.6.2
 */