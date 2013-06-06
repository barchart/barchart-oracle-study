package sun.plugin2.main.client;

import java.security.AccessController;
import java.security.PrivilegedAction;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;
import sun.plugin2.applet.Plugin2Manager;
import sun.plugin2.liveconnect.ArgumentHelper;
import sun.plugin2.liveconnect.BrowserSideObject;
import sun.plugin2.util.SystemUtil;

public class MessagePassingOneWayJSObject extends JSObject
{
  private MessagePassingJSObject mpjso;
  private Plugin2Manager manager;
  private static final boolean DEBUG = AccessController.doPrivileged(new PrivilegedAction()
  {
    public Object run()
    {
      return SystemUtil.getenv("JPI_PLUGIN2_DEBUG");
    }
  }) != null;

  public MessagePassingOneWayJSObject(MessagePassingJSObject paramMessagePassingJSObject)
  {
    this.mpjso = paramMessagePassingJSObject;
    this.manager = paramMessagePassingJSObject.getManager();
  }

  public BrowserSideObject getBrowserSideObject()
  {
    return this.mpjso.getBrowserSideObject();
  }

  public int getAppletID()
  {
    return this.mpjso.getAppletID();
  }

  public Object call(String paramString, Object[] paramArrayOfObject)
    throws JSException
  {
    if (this.manager != null)
      this.manager.increaseJava2JSCounter();
    Object localObject = this.mpjso.call(paramString, paramArrayOfObject);
    if (localObject == null)
      return null;
    if (ArgumentHelper.isPrimitiveOrString(localObject))
      return localObject;
    return new MessagePassingOneWayJSObject((MessagePassingJSObject)localObject);
  }

  public Object eval(String paramString)
    throws JSException
  {
    if (this.manager != null)
      this.manager.increaseJava2JSCounter();
    Object localObject = this.mpjso.eval(paramString);
    if (localObject == null)
      return null;
    if (ArgumentHelper.isPrimitiveOrString(localObject))
      return localObject;
    return new MessagePassingOneWayJSObject((MessagePassingJSObject)localObject);
  }

  public Object getMember(String paramString)
    throws JSException
  {
    if (this.manager != null)
      this.manager.increaseJava2JSCounter();
    Object localObject = this.mpjso.getMember(paramString);
    if (localObject == null)
      return null;
    if (ArgumentHelper.isPrimitiveOrString(localObject))
      return localObject;
    return new MessagePassingOneWayJSObject((MessagePassingJSObject)localObject);
  }

  public void setMember(String paramString, Object paramObject)
    throws JSException
  {
    if (this.manager != null)
      this.manager.increaseJava2JSCounter();
    this.mpjso.setMember(paramString, paramObject);
  }

  public void removeMember(String paramString)
    throws JSException
  {
    if (this.manager != null)
      this.manager.increaseJava2JSCounter();
    this.mpjso.removeMember(paramString);
  }

  public Object getSlot(int paramInt)
    throws JSException
  {
    if (this.manager != null)
      this.manager.increaseJava2JSCounter();
    Object localObject = this.mpjso.getSlot(paramInt);
    if (localObject == null)
      return null;
    if (ArgumentHelper.isPrimitiveOrString(localObject))
      return localObject;
    return new MessagePassingOneWayJSObject((MessagePassingJSObject)localObject);
  }

  public void setSlot(int paramInt, Object paramObject)
    throws JSException
  {
    if (this.manager != null)
      this.manager.increaseJava2JSCounter();
    this.mpjso.setSlot(paramInt, paramObject);
  }

  public String toString()
  {
    if (this.manager != null)
      this.manager.increaseJava2JSCounter();
    return this.mpjso.toString();
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.main.client.MessagePassingOneWayJSObject
 * JD-Core Version:    0.6.2
 */