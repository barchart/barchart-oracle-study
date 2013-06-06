package sun.plugin2.main.client;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;
import sun.plugin2.applet.Plugin2Manager;
import sun.plugin2.liveconnect.BrowserSideObject;
import sun.plugin2.message.Conversation;
import sun.plugin2.message.JavaObjectOpMessage;
import sun.plugin2.message.JavaScriptCallMessage;
import sun.plugin2.message.JavaScriptEvalMessage;
import sun.plugin2.message.JavaScriptMemberOpMessage;
import sun.plugin2.message.JavaScriptReplyMessage;
import sun.plugin2.message.JavaScriptSlotOpMessage;
import sun.plugin2.message.JavaScriptToStringMessage;
import sun.plugin2.message.Message;
import sun.plugin2.message.Pipe;
import sun.plugin2.util.SystemUtil;

public class MessagePassingJSObject extends JSObject
{
  static final IllegalArgumentException BAD_METHOD_NAME_EXCEPTION = new IllegalArgumentException("Method name should not be null.");
  private BrowserSideObject object;
  private int appletID;
  private Pipe pipe;
  private Plugin2Manager manager;
  private static final boolean DEBUG = AccessController.doPrivileged(new PrivilegedAction()
  {
    public Object run()
    {
      return SystemUtil.getenv("JPI_PLUGIN2_DEBUG");
    }
  }) != null;

  public MessagePassingJSObject(BrowserSideObject paramBrowserSideObject, int paramInt, Pipe paramPipe)
  {
    this.object = paramBrowserSideObject;
    this.appletID = paramInt;
    this.pipe = paramPipe;
    this.manager = null;
  }

  public MessagePassingJSObject(BrowserSideObject paramBrowserSideObject, int paramInt, Pipe paramPipe, Plugin2Manager paramPlugin2Manager)
  {
    this.object = paramBrowserSideObject;
    this.appletID = paramInt;
    this.pipe = paramPipe;
    this.manager = paramPlugin2Manager;
  }

  public BrowserSideObject getBrowserSideObject()
  {
    return this.object;
  }

  public int getAppletID()
  {
    return this.appletID;
  }

  public Plugin2Manager getManager()
  {
    return this.manager;
  }

  public Object call(String paramString, Object[] paramArrayOfObject)
    throws JSException
  {
    if (isInvalidJSMethodName(paramString))
      throw BAD_METHOD_NAME_EXCEPTION;
    if (this.manager != null)
      this.manager.decreaseJava2JSCounter();
    if (("eval".equals(paramString)) && (concatenatable(paramArrayOfObject)))
      return eval(concat(paramArrayOfObject));
    Conversation localConversation = this.pipe.beginConversation();
    try
    {
      MessagePassingExecutionContext.AllowSetForegroundWindow();
      JavaScriptCallMessage localJavaScriptCallMessage = new JavaScriptCallMessage(localConversation, this.object, this.appletID, paramString, convert(paramArrayOfObject));
      this.pipe.send(localJavaScriptCallMessage);
      Object localObject1 = waitForReply(localConversation);
      return localObject1;
    }
    catch (IOException localIOException)
    {
      throw newJSException(localIOException);
    }
    finally
    {
      this.pipe.endConversation(localConversation);
    }
  }

  public Object eval(String paramString)
    throws JSException
  {
    if ((paramString == null) || (paramString.length() == 0))
      return null;
    if (this.manager != null)
      this.manager.decreaseJava2JSCounter();
    Conversation localConversation = this.pipe.beginConversation();
    try
    {
      MessagePassingExecutionContext.AllowSetForegroundWindow();
      JavaScriptEvalMessage localJavaScriptEvalMessage = new JavaScriptEvalMessage(localConversation, this.object, this.appletID, paramString);
      this.pipe.send(localJavaScriptEvalMessage);
      Object localObject1 = waitForReply(localConversation);
      return localObject1;
    }
    catch (IOException localIOException)
    {
      throw newJSException(localIOException);
    }
    finally
    {
      this.pipe.endConversation(localConversation);
    }
  }

  public Object getMember(String paramString)
    throws JSException
  {
    if (isInvalidJSMethodName(paramString))
      throw BAD_METHOD_NAME_EXCEPTION;
    if (this.manager != null)
      this.manager.decreaseJava2JSCounter();
    return doMemberOp(paramString, 1, null);
  }

  public void setMember(String paramString, Object paramObject)
    throws JSException
  {
    if (isInvalidJSMethodName(paramString))
      throw BAD_METHOD_NAME_EXCEPTION;
    if (this.manager != null)
      this.manager.decreaseJava2JSCounter();
    doMemberOp(paramString, 2, paramObject);
  }

  public void removeMember(String paramString)
    throws JSException
  {
    if (isInvalidJSMethodName(paramString))
      throw BAD_METHOD_NAME_EXCEPTION;
    if (this.manager != null)
      this.manager.decreaseJava2JSCounter();
    doMemberOp(paramString, 3, null);
  }

  public Object getSlot(int paramInt)
    throws JSException
  {
    if (this.manager != null)
      this.manager.decreaseJava2JSCounter();
    return doSlotOp(paramInt, 1, null);
  }

  public void setSlot(int paramInt, Object paramObject)
    throws JSException
  {
    if (this.manager != null)
      this.manager.decreaseJava2JSCounter();
    doSlotOp(paramInt, 2, paramObject);
  }

  public String toString()
  {
    if (this.manager != null)
      this.manager.decreaseJava2JSCounter();
    Conversation localConversation = this.pipe.beginConversation();
    try
    {
      JavaScriptToStringMessage localJavaScriptToStringMessage = new JavaScriptToStringMessage(localConversation, this.object, this.appletID);
      this.pipe.send(localJavaScriptToStringMessage);
      String str = (String)waitForReply(localConversation);
      return str;
    }
    catch (IOException localIOException)
    {
      throw newJSException(localIOException);
    }
    finally
    {
      this.pipe.endConversation(localConversation);
    }
  }

  private Object waitForReply(Conversation paramConversation)
    throws JSException
  {
    try
    {
      while (true)
      {
        Message localMessage = this.pipe.receive(0L, paramConversation);
        switch (localMessage.getID())
        {
        case 27:
          JavaScriptReplyMessage localJavaScriptReplyMessage = (JavaScriptReplyMessage)localMessage;
          if (localJavaScriptReplyMessage.getExceptionMessage() != null)
            throw newJSException(localJavaScriptReplyMessage.getExceptionMessage());
          return LiveConnectSupport.importObject(localJavaScriptReplyMessage.getResult(), this.appletID);
        case 33:
          try
          {
            LiveConnectSupport.doObjectOp((JavaObjectOpMessage)localMessage);
          }
          catch (IOException localIOException2)
          {
            throw newJSException(localIOException2);
          }
        default:
          throw newJSException("Unexpected reply message ID " + localMessage.getID() + " from web browser");
        }
      }
    }
    catch (IOException localIOException1)
    {
      throw newJSException(localIOException1);
    }
    catch (InterruptedException localInterruptedException)
    {
      throw newJSException(localInterruptedException);
    }
  }

  private Object[] convert(Object[] paramArrayOfObject)
  {
    if (paramArrayOfObject == null)
      return null;
    Object[] arrayOfObject = new Object[paramArrayOfObject.length];
    for (int i = 0; i < paramArrayOfObject.length; i++)
      arrayOfObject[i] = LiveConnectSupport.exportObject(paramArrayOfObject[i], this.appletID, false, false);
    return arrayOfObject;
  }

  private Object doMemberOp(String paramString, int paramInt, Object paramObject)
    throws JSException
  {
    Conversation localConversation = this.pipe.beginConversation();
    try
    {
      JavaScriptMemberOpMessage localJavaScriptMemberOpMessage = new JavaScriptMemberOpMessage(localConversation, this.object, this.appletID, paramString, paramInt, LiveConnectSupport.exportObject(paramObject, this.appletID, false, false));
      this.pipe.send(localJavaScriptMemberOpMessage);
      Object localObject1 = waitForReply(localConversation);
      return localObject1;
    }
    catch (IOException localIOException)
    {
      throw newJSException(localIOException);
    }
    finally
    {
      this.pipe.endConversation(localConversation);
    }
  }

  private Object doSlotOp(int paramInt1, int paramInt2, Object paramObject)
    throws JSException
  {
    Conversation localConversation = this.pipe.beginConversation();
    try
    {
      JavaScriptSlotOpMessage localJavaScriptSlotOpMessage = new JavaScriptSlotOpMessage(localConversation, this.object, this.appletID, paramInt1, paramInt2, LiveConnectSupport.exportObject(paramObject, this.appletID, false, false));
      this.pipe.send(localJavaScriptSlotOpMessage);
      Object localObject1 = waitForReply(localConversation);
      return localObject1;
    }
    catch (IOException localIOException)
    {
      throw newJSException(localIOException);
    }
    finally
    {
      this.pipe.endConversation(localConversation);
    }
  }

  private JSException newJSException(Exception paramException)
  {
    JSException localJSException = (JSException)new JSException().initCause(paramException);
    if (DEBUG)
      localJSException.printStackTrace();
    return localJSException;
  }

  private JSException newJSException(String paramString)
  {
    JSException localJSException = new JSException(paramString);
    if (DEBUG)
      localJSException.printStackTrace();
    return localJSException;
  }

  private boolean concatenatable(Object[] paramArrayOfObject)
  {
    if (paramArrayOfObject == null)
      return true;
    for (int i = 0; i < paramArrayOfObject.length; i++)
      if (!(paramArrayOfObject[i] instanceof String))
        return false;
    return true;
  }

  private String concat(Object[] paramArrayOfObject)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    if (paramArrayOfObject != null)
      for (int i = 0; i < paramArrayOfObject.length; i++)
        if (paramArrayOfObject[i] != null)
        {
          localStringBuffer.append(paramArrayOfObject[i]);
          if (i < paramArrayOfObject.length - 1)
            localStringBuffer.append(" ");
        }
    return localStringBuffer.toString();
  }

  private boolean isInvalidJSMethodName(String paramString)
  {
    return (paramString == null) || (paramString.replaceAll("\\s", "").isEmpty());
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.main.client.MessagePassingJSObject
 * JD-Core Version:    0.6.2
 */