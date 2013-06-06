package sun.plugin.javascript.navig5;

import com.sun.deploy.trace.Trace;
import java.security.AccessControlContext;
import java.security.AccessControlException;
import java.security.AllPermission;
import netscape.javascript.JSException;
import sun.plugin.viewer.context.NetscapeAppletContext;

public class JSObject extends sun.plugin.javascript.JSObject
{
  private int nativeJSObject = 0;
  private int jsThreadID = 0;
  private int handle = 0;
  private NetscapeAppletContext nac = null;
  private boolean released = false;
  public static final int JSOBJECT_GETWINDOW = 1;
  public static final int JSOBJECT_GETMEMBER = 2;
  public static final int JSOBJECT_GETSLOT = 3;
  public static final int JSOBJECT_SETMEMBER = 4;
  public static final int JSOBJECT_SETSLOT = 5;
  public static final int JSOBJECT_REMOVEMEMBER = 6;
  public static final int JSOBJECT_CALL = 7;
  public static final int JSOBJECT_EVAL = 8;
  public static final int JSOBJECT_TOSTRING = 9;
  public static final int JSOBJECT_FINALIZE = 10;

  public JSObject(int paramInt)
  {
    this.handle = paramInt;
    this.jsThreadID = JSGetThreadID(paramInt);
    this.nativeJSObject = JSGetNativeJSObject();
  }

  public JSObject(int paramInt1, int paramInt2)
  {
    this.jsThreadID = paramInt1;
    this.nativeJSObject = paramInt2;
  }

  public void setNetscapeAppletContext(NetscapeAppletContext paramNetscapeAppletContext)
  {
    this.nac = paramNetscapeAppletContext;
    this.handle = paramNetscapeAppletContext.getAppletContextHandle();
    paramNetscapeAppletContext.addJSObjectToExportedList(this);
  }

  public synchronized void cleanup()
  {
    if (!this.released)
    {
      JSObjectCleanup(this.jsThreadID, this.handle, this.nativeJSObject);
      this.released = true;
    }
  }

  private Object invoke(int paramInt, String paramString, Object[] paramArrayOfObject)
    throws JSException
  {
    if (this.released)
      throw new JSException("Native DOM Object has been released");
    SecurityContext localSecurityContext = SecurityContext.getCurrentSecurityContext();
    boolean bool = false;
    try
    {
      AccessControlContext localAccessControlContext = localSecurityContext.getAccessControlContext();
      localAccessControlContext.checkPermission(new AllPermission());
      bool = true;
    }
    catch (AccessControlException localAccessControlException)
    {
    }
    Trace.msgLiveConnectPrintln("jsobject.invoke.url.permission", new Object[] { localSecurityContext.getURL(), String.valueOf(bool) });
    Object localObject = JSObjectInvoke(paramInt, this.jsThreadID, this.handle, this.nativeJSObject, localSecurityContext.getURL(), paramString, paramArrayOfObject, bool);
    if ((localObject != null) && ((localObject instanceof JSObject)))
      ((JSObject)localObject).setNetscapeAppletContext(this.nac);
    return localObject;
  }

  public Object call(String paramString, Object[] paramArrayOfObject)
    throws JSException
  {
    Trace.msgLiveConnectPrintln("jsobject.call", new Object[] { paramString });
    return invoke(7, paramString, paramArrayOfObject);
  }

  public Object eval(String paramString)
    throws JSException
  {
    Trace.msgLiveConnectPrintln("jsobject.eval", new Object[] { paramString });
    return invoke(8, paramString, null);
  }

  public Object getMember(String paramString)
    throws JSException
  {
    Trace.msgLiveConnectPrintln("jsobject.getMember", new Object[] { paramString });
    return invoke(2, paramString, null);
  }

  public void setMember(String paramString, Object paramObject)
    throws JSException
  {
    Trace.msgLiveConnectPrintln("jsobject.setMember", new Object[] { paramString });
    Object[] arrayOfObject = new Object[1];
    arrayOfObject[0] = paramObject;
    invoke(4, paramString, arrayOfObject);
  }

  public void removeMember(String paramString)
    throws JSException
  {
    Trace.msgLiveConnectPrintln("jsobject.removeMember", new Object[] { paramString });
    invoke(6, paramString, null);
  }

  public Object getSlot(int paramInt)
    throws JSException
  {
    Trace.msgLiveConnectPrintln("jsobject.getSlot", new Object[] { String.valueOf(paramInt) });
    Object[] arrayOfObject = new Object[1];
    arrayOfObject[0] = new Integer(paramInt);
    return invoke(3, null, arrayOfObject);
  }

  public void setSlot(int paramInt, Object paramObject)
    throws JSException
  {
    Trace.msgLiveConnectPrintln("jsobject.setSlot", new Object[] { String.valueOf(paramInt) });
    Object[] arrayOfObject = new Object[2];
    arrayOfObject[0] = new Integer(paramInt);
    arrayOfObject[1] = paramObject;
    invoke(5, null, arrayOfObject);
  }

  public String toString()
  {
    Object localObject = invoke(9, null, null);
    if (localObject != null)
      return localObject.toString();
    return null;
  }

  public void finalize()
  {
    cleanup();
  }

  private int JSGetNativeJSObject()
  {
    Object localObject = invoke(1, null, null);
    if ((localObject != null) && ((localObject instanceof Integer)))
      return ((Integer)localObject).intValue();
    throw new JSException("Native Window is destroyed");
  }

  private native Object JSObjectInvoke(int paramInt1, int paramInt2, int paramInt3, int paramInt4, String paramString1, String paramString2, Object[] paramArrayOfObject, boolean paramBoolean);

  private native void JSObjectCleanup(int paramInt1, int paramInt2, int paramInt3);

  private static native int JSGetThreadID(int paramInt);
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.javascript.navig5.JSObject
 * JD-Core Version:    0.6.2
 */