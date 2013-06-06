package sun.plugin2.main.server;

import java.io.IOException;
import java.io.PrintStream;
import netscape.javascript.JSException;
import sun.plugin2.liveconnect.BrowserSideObject;
import sun.plugin2.liveconnect.RemoteJavaObject;
import sun.plugin2.util.PluginTrace;
import sun.plugin2.util.SystemUtil;

public abstract class AbstractPlugin
  implements Plugin
{
  protected static final boolean DEBUG = SystemUtil.getenv("JPI_PLUGIN2_DEBUG") != null;
  protected static final boolean VERBOSE = SystemUtil.getenv("JPI_PLUGIN2_VERBOSE") != null;
  private AppletID appletID;
  private ResultHandler resultHandler;
  private int activeJScripts = 0;
  protected static final ThreadLocal appletStopMark = new ThreadLocal();
  protected PluginTrace ptrace = new PluginTrace();
  protected static final long STOP_ACK_DELAY = 1100L;
  protected static final long STOP_ACK_DELAY_REDUCTION = 900L;

  public PluginTrace getTrace()
  {
    return this.ptrace;
  }

  protected void appletStarted(AppletID paramAppletID, ResultHandler paramResultHandler)
  {
    this.appletID = paramAppletID;
    this.resultHandler = paramResultHandler;
  }

  protected void appletStopped()
  {
    startupStatus(4);
    this.appletID = null;
    this.resultHandler = null;
  }

  public int getActiveJSCounter()
  {
    return this.activeJScripts;
  }

  public void incrementActiveJSCounter()
  {
    this.activeJScripts += 1;
  }

  public void decrementActiveJSCounter()
  {
    this.activeJScripts -= 1;
  }

  protected abstract boolean scriptingObjectArgumentListsAreReversed();

  protected abstract long allocateVariantArray(int paramInt);

  protected abstract void freeVariantArray(long paramLong, int paramInt);

  protected abstract void setVariantArrayElement(long paramLong, int paramInt, boolean paramBoolean);

  protected abstract void setVariantArrayElement(long paramLong, int paramInt, byte paramByte);

  protected abstract void setVariantArrayElement(long paramLong, int paramInt, char paramChar);

  protected abstract void setVariantArrayElement(long paramLong, int paramInt, short paramShort);

  protected abstract void setVariantArrayElement(long paramLong, int paramInt1, int paramInt2);

  protected abstract void setVariantArrayElement(long paramLong1, int paramInt, long paramLong2);

  protected abstract void setVariantArrayElement(long paramLong, int paramInt, float paramFloat);

  protected abstract void setVariantArrayElement(long paramLong, int paramInt, double paramDouble);

  protected abstract void setVariantArrayElement(long paramLong, int paramInt, String paramString);

  protected abstract void setVariantArrayElementToScriptingObject(long paramLong1, int paramInt, long paramLong2);

  protected abstract void setVariantArrayElementToVoid(long paramLong, int paramInt);

  protected abstract Object variantArrayElementToObject(long paramLong, int paramInt);

  protected abstract long lookupScriptingObject(RemoteJavaObject paramRemoteJavaObject, boolean paramBoolean);

  protected abstract Object wrapOrUnwrapScriptingObject(long paramLong);

  protected abstract String identifierToString(long paramLong);

  protected abstract void fillInExceptionInfo(long paramLong, String paramString);

  protected abstract void fillInExceptionInfo(long paramLong, Exception paramException);

  private void setVariantArrayElement(long paramLong, int paramInt, BrowserSideObject paramBrowserSideObject)
  {
    setVariantArrayElementToScriptingObject(paramLong, paramInt, paramBrowserSideObject.getNativeObjectReference());
  }

  private void setVariantArrayElement(long paramLong, int paramInt, RemoteJavaObject paramRemoteJavaObject)
  {
    long l = lookupScriptingObject(paramRemoteJavaObject, false);
    setVariantArrayElementToScriptingObject(paramLong, paramInt, l);
  }

  protected void objectToVariantArrayElement(Object paramObject, long paramLong, int paramInt)
  {
    if (paramLong != 0L)
      if (paramObject != null)
      {
        if ((paramObject instanceof Boolean))
          setVariantArrayElement(paramLong, paramInt, ((Boolean)paramObject).booleanValue());
        else if ((paramObject instanceof Byte))
          setVariantArrayElement(paramLong, paramInt, ((Byte)paramObject).byteValue());
        else if ((paramObject instanceof Character))
          setVariantArrayElement(paramLong, paramInt, ((Character)paramObject).charValue());
        else if ((paramObject instanceof Short))
          setVariantArrayElement(paramLong, paramInt, ((Short)paramObject).shortValue());
        else if ((paramObject instanceof Integer))
          setVariantArrayElement(paramLong, paramInt, ((Integer)paramObject).intValue());
        else if ((paramObject instanceof Long))
          setVariantArrayElement(paramLong, paramInt, ((Long)paramObject).longValue());
        else if ((paramObject instanceof Float))
          setVariantArrayElement(paramLong, paramInt, ((Float)paramObject).floatValue());
        else if ((paramObject instanceof Double))
          setVariantArrayElement(paramLong, paramInt, ((Double)paramObject).doubleValue());
        else if ((paramObject instanceof String))
          setVariantArrayElement(paramLong, paramInt, (String)paramObject);
        else if ((paramObject instanceof BrowserSideObject))
          setVariantArrayElement(paramLong, paramInt, (BrowserSideObject)paramObject);
        else if ((paramObject instanceof RemoteJavaObject))
          setVariantArrayElement(paramLong, paramInt, (RemoteJavaObject)paramObject);
        else if (paramObject == Void.TYPE)
          setVariantArrayElementToVoid(paramLong, paramInt);
        else
          throw new JSException("Inconvertible argument type to LiveConnect: " + paramObject.getClass().getName());
      }
      else
        setVariantArrayElementToScriptingObject(paramLong, paramInt, 0L);
  }

  protected BrowserSideObject newBrowserSideObject(long paramLong)
  {
    return newBrowserSideObject(paramLong, true);
  }

  protected BrowserSideObject newBrowserSideObject(long paramLong, boolean paramBoolean)
  {
    if ((paramBoolean) && (this.appletID == null))
      return null;
    BrowserSideObject localBrowserSideObject = new BrowserSideObject(paramLong);
    if (paramBoolean)
      LiveConnectSupport.registerObject(this.appletID.getID(), localBrowserSideObject);
    return localBrowserSideObject;
  }

  protected long getScriptingObjectForApplet(long paramLong)
  {
    long l = 0L;
    String str = null;
    if (this.appletID == null)
    {
      fillInExceptionInfo(paramLong, "Applet is not running");
      if (DEBUG)
        str = " because applet is not running";
    }
    else
    {
      try
      {
        ResultID localResultID = LiveConnectSupport.sendGetApplet(this, this.appletID);
        if (DEBUG)
          System.out.println("AbstractPlugin.getScriptingObjectForApplet starting to wait for result ID " + localResultID.getID());
        this.resultHandler.waitForResult(localResultID, this.appletID);
        if (DEBUG)
          System.out.println("AbstractPlugin.getScriptingObjectForApplet ending wait for result ID " + localResultID.getID());
        notifyMainThread();
        if (LiveConnectSupport.resultAvailable(localResultID))
          try
          {
            l = lookupScriptingObject((RemoteJavaObject)LiveConnectSupport.getResult(localResultID), true);
          }
          catch (RuntimeException localRuntimeException)
          {
            fillInExceptionInfo(paramLong, localRuntimeException);
          }
        else
          fillInExceptionInfo(paramLong, "Target applet or JVM process exited abruptly");
      }
      catch (IOException localIOException)
      {
        fillInExceptionInfo(paramLong, "Target JVM seems to have already exited");
      }
    }
    if (DEBUG)
      System.out.println("AbstractPlugin.getScriptingObjectForApplet(" + this.appletID + ") returning 0x" + Long.toHexString(l) + (str != null ? str : ""));
    return l;
  }

  protected Object getJavaNameSpace(String paramString)
  {
    if (this.appletID == null)
      return null;
    try
    {
      ResultID localResultID = LiveConnectSupport.sendGetNameSpace(this, this.appletID, paramString);
      if (DEBUG)
        System.out.println("AbstractPlugin.getJavaNameSpace starting to wait for result ID " + localResultID.getID());
      this.resultHandler.waitForResult(localResultID, this.appletID);
      if (DEBUG)
        System.out.println("AbstractPlugin.getJavaNameSpace ending wait for result ID " + localResultID.getID());
      notifyMainThread();
      if (LiveConnectSupport.resultAvailable(localResultID))
        try
        {
          return LiveConnectSupport.getResult(localResultID);
        }
        catch (RuntimeException localRuntimeException)
        {
          if (DEBUG)
          {
            System.out.println("AbstractPlugin.getJavaNameSpace: exception occurred during fetch of namespace \"" + paramString + "\"");
            localRuntimeException.printStackTrace();
          }
        }
      else if (DEBUG)
        System.out.println("AbstractPlugin.getJavaNameSpace: target applet or JVM process exited abruptly");
    }
    catch (IOException localIOException)
    {
      if (DEBUG)
        System.out.println("AbstractPlugin.getJavaNameSpace: target JVM process seems to have already exited");
    }
    return null;
  }

  protected boolean javaObjectInvoke(RemoteJavaObject paramRemoteJavaObject, boolean paramBoolean, long paramLong1, long paramLong2, int paramInt, long paramLong3, long paramLong4)
  {
    return doJavaObjectOp(paramRemoteJavaObject, paramBoolean, 1, identifierToString(paramLong1), paramLong1, paramLong2, paramInt, paramLong3, paramLong4);
  }

  protected boolean javaObjectInvokeConstructor(RemoteJavaObject paramRemoteJavaObject, boolean paramBoolean, long paramLong1, int paramInt, long paramLong2, long paramLong3)
  {
    return doJavaObjectOp(paramRemoteJavaObject, paramBoolean, 1, "<init>", -1L, paramLong1, paramInt, paramLong2, paramLong3);
  }

  protected boolean javaObjectGetField(RemoteJavaObject paramRemoteJavaObject, boolean paramBoolean, long paramLong1, long paramLong2, long paramLong3)
  {
    return doJavaObjectOp(paramRemoteJavaObject, paramBoolean, 2, identifierToString(paramLong1), paramLong1, 0L, 0, paramLong2, paramLong3);
  }

  protected boolean javaObjectSetField(RemoteJavaObject paramRemoteJavaObject, boolean paramBoolean, long paramLong1, long paramLong2, long paramLong3)
  {
    return doJavaObjectOp(paramRemoteJavaObject, paramBoolean, 3, identifierToString(paramLong1), paramLong1, paramLong2, 1, 0L, paramLong3);
  }

  protected boolean javaObjectHasField(RemoteJavaObject paramRemoteJavaObject, long paramLong)
  {
    return doJavaObjectOp(paramRemoteJavaObject, false, 4, identifierToString(paramLong), paramLong, 0L, 0, 0L, 0L);
  }

  protected boolean javaObjectHasMethod(RemoteJavaObject paramRemoteJavaObject, long paramLong)
  {
    return doJavaObjectOp(paramRemoteJavaObject, false, 5, identifierToString(paramLong), paramLong, 0L, 0, 0L, 0L);
  }

  protected boolean javaObjectHasFieldOrMethod(RemoteJavaObject paramRemoteJavaObject, long paramLong)
  {
    return doJavaObjectOp(paramRemoteJavaObject, false, 6, identifierToString(paramLong), paramLong, 0L, 0, 0L, 0L);
  }

  protected boolean doJavaObjectOp(RemoteJavaObject paramRemoteJavaObject, boolean paramBoolean, int paramInt1, String paramString, long paramLong1, long paramLong2, int paramInt2, long paramLong3, long paramLong4)
  {
    if (paramString == null)
    {
      localObject1 = "Invalid (null) name -- bad field or method identifier " + paramLong1;
      if (DEBUG)
        System.out.println("AbstractPlugin.doJavaObjectOp: " + (String)localObject1);
      fillInExceptionInfo(paramLong4, (String)localObject1);
      return false;
    }
    if (paramRemoteJavaObject == null)
    {
      localObject1 = "Invalid (null) object";
      if (DEBUG)
        System.out.println("AbstractPlugin.doJavaObjectOp: " + (String)localObject1);
      fillInExceptionInfo(paramLong4, (String)localObject1);
      return false;
    }
    Object localObject1 = null;
    if ((paramLong2 != 0L) && (paramInt2 > 0))
    {
      localObject1 = new Object[paramInt2];
      int i;
      if (scriptingObjectArgumentListsAreReversed())
        for (i = 0; i < paramInt2; i++)
          localObject1[i] = variantArrayElementToObject(paramLong2, paramInt2 - i - 1);
      else
        for (i = 0; i < paramInt2; i++)
          localObject1[i] = variantArrayElementToObject(paramLong2, i);
    }
    Object localObject2 = null;
    try
    {
      ResultID localResultID = LiveConnectSupport.sendRemoteJavaObjectOp(this, paramRemoteJavaObject, paramString, paramInt1, (Object[])localObject1);
      if (DEBUG)
        System.out.println("AbstractPlugin.doJavaObjectOp starting to wait for result ID " + localResultID.getID());
      this.resultHandler.waitForResult(localResultID, paramRemoteJavaObject.getJVMID(), new AppletID(paramRemoteJavaObject.getAppletID()));
      if (DEBUG)
        System.out.println("AbstractPlugin.doJavaObjectOp ending wait for result ID " + localResultID.getID());
      notifyMainThread();
      if (LiveConnectSupport.resultAvailable(localResultID))
        try
        {
          Object localObject3 = LiveConnectSupport.getResult(localResultID);
          if ((paramInt1 == 4) || (paramInt1 == 5) || (paramInt1 == 6))
          {
            if (localObject3 != null)
              return ((Boolean)localObject3).booleanValue();
            return false;
          }
          objectToVariantArrayElement(localObject3, paramLong3, 0);
          return true;
        }
        catch (RuntimeException localRuntimeException)
        {
          if (DEBUG)
            localRuntimeException.printStackTrace();
          fillInExceptionInfo(paramLong4, localRuntimeException.getMessage());
        }
      else
        fillInExceptionInfo(paramLong4, "Target applet or JVM process exited abruptly");
    }
    catch (IOException localIOException)
    {
      if (DEBUG)
        localIOException.printStackTrace();
      fillInExceptionInfo(paramLong4, "Target JVM seems to have already exited");
    }
    catch (JSException localJSException)
    {
      if (DEBUG)
        localJSException.printStackTrace();
      fillInExceptionInfo(paramLong4, localJSException.toString());
    }
    return false;
  }

  private void javaObjectRemoveField(RemoteJavaObject paramRemoteJavaObject, long paramLong1, long paramLong2)
  {
    fillInExceptionInfo(paramLong2, "Removal of Java fields (\"" + identifierToString(paramLong1) + "\") not supported");
  }

  protected abstract BrowserSideObject javaScriptGetWindowInternal(boolean paramBoolean);

  private String escape(String paramString)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = 0; i < paramString.length(); i++)
    {
      char c = paramString.charAt(i);
      if ((c == '\'') || (c == '"') || (c == '\\'))
        localStringBuffer.append("\\");
      localStringBuffer.append(c);
    }
    return localStringBuffer.toString();
  }

  protected String prepareURL(String paramString)
  {
    return escape(paramString);
  }

  protected String prepareTarget(String paramString)
  {
    if ((paramString == null) || (paramString.length() == 0))
      paramString = "_blank";
    return escape(paramString);
  }

  public void showDocument(String paramString1, String paramString2)
  {
    BrowserSideObject localBrowserSideObject = javaScriptGetWindowInternal(false);
    if (localBrowserSideObject != null)
      javaScriptEval(localBrowserSideObject, "window.open('" + prepareURL(paramString1) + "','" + prepareTarget(paramString2) + "').focus();");
  }

  public void showStatus(String paramString)
  {
    BrowserSideObject localBrowserSideObject = javaScriptGetWindowInternal(false);
    if ((paramString != null) && (localBrowserSideObject != null))
      javaScriptEval(localBrowserSideObject, "window.top.status='" + escape(paramString) + "';");
  }

  protected void releaseRemoteJavaObject(RemoteJavaObject paramRemoteJavaObject)
  {
    JVMManager.getManager().releaseRemoteJavaObject(paramRemoteJavaObject);
  }

  private Boolean newBoolean(boolean paramBoolean)
  {
    return paramBoolean ? Boolean.TRUE : Boolean.FALSE;
  }

  private Byte newByte(byte paramByte)
  {
    return new Byte(paramByte);
  }

  private Character newCharacter(char paramChar)
  {
    return new Character(paramChar);
  }

  private Short newShort(short paramShort)
  {
    return new Short(paramShort);
  }

  private Integer newInteger(int paramInt)
  {
    return new Integer(paramInt);
  }

  private Long newLong(long paramLong)
  {
    return new Long(paramLong);
  }

  private Float newFloat(float paramFloat)
  {
    return new Float(paramFloat);
  }

  private Double newDouble(double paramDouble)
  {
    return new Double(paramDouble);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.main.server.AbstractPlugin
 * JD-Core Version:    0.6.2
 */