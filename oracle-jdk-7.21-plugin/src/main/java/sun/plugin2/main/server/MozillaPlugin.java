package sun.plugin2.main.server;

import com.sun.applet2.AppletParameters;
import com.sun.deploy.config.OSType;
import com.sun.deploy.config.Platform;
import com.sun.deploy.net.cookie.CookieUnavailableException;
import com.sun.deploy.services.ServiceManager;
import com.sun.deploy.util.SystemUtils;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import netscape.javascript.JSException;
import sun.plugin2.ipc.Event;
import sun.plugin2.ipc.IPCFactory;
import sun.plugin2.ipc.InProcEvent;
import sun.plugin2.liveconnect.BrowserSideObject;
import sun.plugin2.liveconnect.JSExceptions;
import sun.plugin2.liveconnect.RemoteJavaObject;
import sun.plugin2.util.NativeLibLoader;
import sun.plugin2.util.PluginTrace;
import sun.plugin2.util.SystemUtil;

public class MozillaPlugin extends AbstractPlugin
{
  private static final boolean DEBUG = SystemUtil.getenv("JPI_PLUGIN2_DEBUG") != null;
  private static final boolean USE_XEMBED = SystemUtil.getenv("JPI_PLUGIN2_NO_XEMBED") == null;
  private static final boolean DISABLE_406251_WORKAROUND = SystemUtil.getenv("JPI_PLUGIN2_DISABLE_406251_WORKAROUND") != null;
  private AppletParameters params = new AppletParameters();
  private long mozPluginInstance = 0L;
  private long npp = 0L;
  private String docbase = null;
  private String mimeType = null;
  private String requestedVersion = null;
  private final String _versionPattern = ";version=";
  private final String validChars = "0123456789._";
  private long hWndControlWindow = 0L;
  private String caRenderServerName = null;
  private boolean started = false;
  private AppletID appletID;
  private int status = 0;
  private boolean destroyed;
  private Timer updateTimer;
  private PlatformDependentHandler handler;
  private Long browserThreadId;
  private long javaScriptWindow;
  private static Map npObjectMap = new HashMap();
  private static final Map runnableQueueMap = new HashMap();
  private static final Map mainThreadEventMap = new HashMap();
  private boolean iCreatedMainThreadEvent = false;
  private Runnable drainer = new DrainRunnableQueue();

  public MozillaPlugin(long paramLong1, long paramLong2, String paramString1, String paramString2, long paramLong3)
  {
    if (DEBUG)
      System.out.println("MozillaPlugin.MozillaPlugin with browser thread ID: " + paramLong3);
    this.mozPluginInstance = paramLong1;
    this.npp = paramLong2;
    this.docbase = paramString1;
    this.mimeType = paramString2;
    this.requestedVersion = getRequestedVersion(paramString2);
    this.handler = getPlatformDependentHandler();
    this.browserThreadId = SystemUtils.longValueOf(paramLong3);
  }

  private native boolean isBrowserThread0(long paramLong);

  public void addParameters(String[] paramArrayOfString1, String[] paramArrayOfString2)
  {
    for (int i = 0; i < paramArrayOfString1.length; i++)
      addParameter(paramArrayOfString1[i], paramArrayOfString2[i]);
  }

  private void addParameter(String paramString1, String paramString2)
  {
    if ((paramString1 != null) && (paramString1.charAt(0) != '_') && (!paramString1.equals("PARAM")))
      this.params.put(paramString1, paramString2);
  }

  public void setWindow(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8)
  {
    if (DEBUG)
      System.out.println("MozillaPlugin.setWindow " + this + " hWndControlWindow = " + paramLong + " caRenderServerName = " + this.caRenderServerName);
    this.hWndControlWindow = paramLong;
    if (this.appletID != null)
    {
      if (DEBUG)
        System.out.println("MozillaPlugin.setWindow setting applet " + this.appletID + " size to " + paramInt3 + ", " + paramInt4);
      JVMManager.getManager().setAppletSize(this.appletID, paramInt3, paramInt4);
    }
    else
    {
      this.params.put("width", Integer.toString(paramInt3));
      this.params.put("height", Integer.toString(paramInt4));
      maybeStartApplet(false);
    }
  }

  public boolean print(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (DEBUG)
      System.out.println("MozillaPlugin.printApplet " + this + " hdc = " + paramLong + ", x = " + paramInt1 + ", y = " + paramInt2 + ", width = " + paramInt3 + ", height = " + paramInt4);
    if (this.appletID != null)
    {
      if (DEBUG)
        System.out.println("MozillaPlugin.print() applet " + this.appletID);
      return JVMManager.getManager().printApplet(this.appletID, paramLong, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    return false;
  }

  public void destroy()
  {
    if (DEBUG)
      System.out.println("MozillaPlugin.destroy");
    stopApplet();
    if (this.javaScriptWindow != 0L)
    {
      npnReleaseObject(this.javaScriptWindow);
      this.javaScriptWindow = 0L;
    }
    this.destroyed = true;
    disposeMainThreadEvent();
  }

  public boolean isDestroyed()
  {
    return this.destroyed;
  }

  private boolean isDummyApplet()
  {
    if (SystemUtil.getOSType() == 3)
      return this.params.size() == 0;
    return this.hWndControlWindow == 0L;
  }

  private boolean parentContainerReady()
  {
    if (SystemUtil.getOSType() == 3)
      return this.caRenderServerName != null;
    return this.hWndControlWindow != 0L;
  }

  private void maybeStartApplet(boolean paramBoolean)
  {
    if (!this.started)
      if (((paramBoolean) || (parentContainerReady())) && (this.params != null))
      {
        if (DEBUG)
          System.out.println("  Attempting to start " + (paramBoolean ? "dummy " : "") + "applet");
        this.started = true;
        if ((this.params.get("java_version") == null) && (this.requestedVersion != null))
          this.params.put("java_version", this.requestedVersion);
        if (!paramBoolean)
        {
          this.appletID = JVMManager.getManager().startApplet(this.params, this, this.hWndControlWindow, this.caRenderServerName, USE_XEMBED);
        }
        else
        {
          if (this.docbase == null)
            this.docbase = "file:///";
          this.appletID = JVMManager.getManager().startDummyApplet(this.params, this);
        }
        appletStarted(this.appletID, this.handler);
        if (OSType.isMac())
        {
          TimerTask local1 = new TimerTask()
          {
            public void run()
            {
              MozillaPlugin.this.updateWindowLocation();
            }
          };
          this.updateTimer = new Timer();
          this.updateTimer.schedule(local1, 0L, 16L);
        }
        if (DEBUG)
          System.out.println("  Received applet ID " + this.appletID);
      }
      else if (DEBUG)
      {
        System.out.println("  Skipped starting applet: hWndControlWindow = " + this.hWndControlWindow + ", params = " + this.params + ", caRenderServerName = " + this.caRenderServerName);
      }
  }

  private void stopApplet()
  {
    if (this.appletID == null)
      return;
    if (DEBUG)
      System.out.println("  Stopping applet ID " + this.appletID);
    int i = 1;
    Object localObject1 = appletStopMark.get();
    if (localObject1 != null)
      i = 0;
    else
      appletStopMark.set(this);
    if (this.updateTimer != null)
      this.updateTimer.cancel();
    JVMManager.getManager().sendStopApplet(this.appletID);
    try
    {
      long l1 = System.currentTimeMillis();
      int j = 0;
      long l2 = l1 + 1100L;
      while ((j == 0) && (!JVMManager.getManager().appletExited(this.appletID)) && (!JVMManager.getManager().receivedStopAcknowledgment(this.appletID)))
      {
        long l3 = System.currentTimeMillis();
        if (l2 - l3 > 0L)
          if (i != 0)
          {
            this.handler.waitForSignal(l2 - l3);
          }
          else
          {
            l2 = -900L;
            Thread.yield();
          }
        if (!JVMManager.getManager().receivedStopAcknowledgment(this.appletID))
        {
          l3 = System.currentTimeMillis();
          j = l3 >= l2 ? 1 : 0;
        }
      }
    }
    finally
    {
    }
    ret;
  }

  public void invokeLater(Runnable paramRunnable)
  {
    getRunnableQueue(this.browserThreadId).add(new RunnableWrapper(paramRunnable, this));
    invokeLater0(this.npp, this.drainer);
    notifyMainThread();
  }

  public void notifyMainThread()
  {
    getMainThreadEvent(this.browserThreadId).signal();
  }

  private boolean isBrowserThread()
  {
    return isBrowserThread0(this.npp);
  }

  public String getDocumentBase()
  {
    return this.docbase;
  }

  public void showStatus(String paramString)
  {
    if ((this.npp != 0L) && (paramString != null))
      showStatus0(this.npp, paramString);
  }

  public void startupStatus(int paramInt)
  {
    if (paramInt == 0)
      return;
    this.status = paramInt;
    setAppletStatus(this.mozPluginInstance, paramInt);
  }

  public String getCookie(URL paramURL)
    throws CookieUnavailableException
  {
    try
    {
      return getCookie0(this.npp, paramURL.toString());
    }
    catch (Exception localException)
    {
      throw ((CookieUnavailableException)new CookieUnavailableException().initCause(localException));
    }
  }

  public void setCookie(URL paramURL, String paramString)
    throws CookieUnavailableException
  {
    try
    {
      setCookie0(this.npp, paramURL.toString(), paramString);
    }
    catch (Exception localException)
    {
      throw ((CookieUnavailableException)new CookieUnavailableException().initCause(localException));
    }
  }

  public PasswordAuthentication getAuthentication(String paramString1, String paramString2, int paramInt, String paramString3, String paramString4, URL paramURL, boolean paramBoolean)
  {
    return getPAFromCharArray(getAuthentication0(this.npp, paramString1, paramString2, paramInt, paramString3, paramString4));
  }

  private static PasswordAuthentication getPAFromCharArray(char[] paramArrayOfChar)
  {
    if (paramArrayOfChar == null)
      return null;
    for (int i = 0; (i < paramArrayOfChar.length) && (':' != paramArrayOfChar[i]); i++);
    PasswordAuthentication localPasswordAuthentication = null;
    if (i < paramArrayOfChar.length)
    {
      String str = new String(paramArrayOfChar, 0, i);
      char[] arrayOfChar = extractArray(paramArrayOfChar, i + 1);
      localPasswordAuthentication = new PasswordAuthentication(str, arrayOfChar);
      resetArray(arrayOfChar);
    }
    resetArray(paramArrayOfChar);
    return localPasswordAuthentication;
  }

  private static void resetArray(char[] paramArrayOfChar)
  {
    Arrays.fill(paramArrayOfChar, ' ');
  }

  private static char[] extractArray(char[] paramArrayOfChar, int paramInt)
  {
    char[] arrayOfChar = new char[paramArrayOfChar.length - paramInt];
    for (int i = 0; i < arrayOfChar.length; i++)
      arrayOfChar[i] = paramArrayOfChar[(i + paramInt)];
    return arrayOfChar;
  }

  public String getProxy(String paramString)
  {
    return getProxy0(this.npp, paramString);
  }

  private void useCARendering(String paramString)
  {
    this.caRenderServerName = paramString;
    this.hWndControlWindow = -1L;
    if (DEBUG)
      System.out.println("MozillaPlugin.useCARendering " + this + " hWndControlWindow = " + this.hWndControlWindow + " serverName = " + this.caRenderServerName);
  }

  private void moveWindowTo(double paramDouble1, double paramDouble2)
  {
    JVMManager.getManager().sendOverlayWindowMove(this.appletID, paramDouble1, paramDouble2);
  }

  private void sendFocusEvent(boolean paramBoolean)
  {
    JVMManager.getManager().sendGotFocus(this.appletID, paramBoolean);
  }

  private void sendWindowFocusEvent(boolean paramBoolean)
  {
    JVMManager.getManager().sendWindowActivation(this.appletID, paramBoolean);
  }

  private void sendKeyEvent(int paramInt1, int paramInt2, String paramString1, String paramString2, boolean paramBoolean1, int paramInt3, boolean paramBoolean2)
  {
    JVMManager.getManager().sendKeyEvent(this.appletID, paramInt1, paramInt2, paramString1, paramString2, paramBoolean1, paramInt3, paramBoolean2);
  }

  private void sendMouseEvent(int paramInt1, int paramInt2, double paramDouble1, double paramDouble2, int paramInt3, int paramInt4)
  {
    JVMManager.getManager().sendMouseEvent(this.appletID, paramInt1, paramInt2, paramDouble1, paramDouble2, paramInt3, paramInt4);
  }

  private void sendScrollEvent(double paramDouble1, double paramDouble2, int paramInt, double paramDouble3, double paramDouble4, double paramDouble5)
  {
    JVMManager.getManager().sendScrollEvent(this.appletID, paramDouble1, paramDouble2, paramInt, paramDouble3, paramDouble4, paramDouble5);
  }

  private void sendTextEvent(String paramString)
  {
    JVMManager.getManager().sendTextEvent(this.appletID, paramString);
  }

  void updateWindowLocation()
  {
    nativeUpdateWindowLocation(this.npp);
  }

  public void hostRemoteCAContext(int paramInt)
  {
    nativeHostRemoteCAContext(this.npp, this.mozPluginInstance, paramInt);
  }

  private native void nativeHostRemoteCAContext(long paramLong1, long paramLong2, int paramInt);

  private native void invokeLater0(long paramLong, Runnable paramRunnable);

  private native void showStatus0(long paramLong, String paramString);

  private native void nativeUpdateWindowLocation(long paramLong);

  private native String getProxy0(long paramLong, String paramString);

  private native String getCookie0(long paramLong, String paramString)
    throws RuntimeException;

  private native void setCookie0(long paramLong, String paramString1, String paramString2)
    throws RuntimeException;

  private native char[] getAuthentication0(long paramLong, String paramString1, String paramString2, int paramInt, String paramString3, String paramString4);

  private void checkValidity(BrowserSideObject paramBrowserSideObject)
  {
    if (isDestroyed())
    {
      JSException localJSException = new JSException("Plugin instance was already destroyed! Should not reach here!");
      if (DEBUG)
        localJSException.printStackTrace();
      throw localJSException;
    }
    if (paramBrowserSideObject == null)
      return;
    checkValidOrigin(paramBrowserSideObject);
  }

  private void checkValidOrigin(BrowserSideObject paramBrowserSideObject)
    throws JSException
  {
    Object localObject1 = null;
    try
    {
      localObject1 = javaScriptGetMemberImpl(paramBrowserSideObject.getNativeObjectReference(), npnGetStringIdentifier("document"), "document", 0);
    }
    catch (JSException localJSException)
    {
      if (DEBUG)
        localJSException.printStackTrace();
      return;
    }
    if ((localObject1 instanceof BrowserSideObject))
    {
      BrowserSideObject localBrowserSideObject = (BrowserSideObject)localObject1;
      Object localObject2 = javaScriptGetMemberImpl(localBrowserSideObject.getNativeObjectReference(), npnGetStringIdentifier("baseURI"), "baseURI", 0);
      try
      {
        String str1 = new URL(localObject2.toString()).getHost();
        String str2 = new URL(this.docbase.toString()).getHost();
        if (!str2.equalsIgnoreCase(str1))
          throw new JSException("baseURI and docbase host DO NOT match: " + str1 + " " + str2);
      }
      catch (MalformedURLException localMalformedURLException)
      {
        if (DEBUG)
          localMalformedURLException.printStackTrace();
      }
    }
  }

  protected BrowserSideObject javaScriptGetWindowInternal(boolean paramBoolean)
  {
    if (this.javaScriptWindow == 0L)
    {
      if (this.npp == 0L)
        return null;
      this.javaScriptWindow = javaScriptGetWindow0(this.npp);
    }
    if (this.javaScriptWindow == 0L)
      return null;
    return newBrowserSideObject(this.javaScriptWindow, paramBoolean);
  }

  public BrowserSideObject javaScriptGetWindow()
  {
    checkValidity(null);
    return javaScriptGetWindowInternal(true);
  }

  private native long javaScriptGetWindow0(long paramLong);

  public void javaScriptRetainObject(BrowserSideObject paramBrowserSideObject)
  {
    checkValidity(null);
    npnRetainObject(paramBrowserSideObject.getNativeObjectReference());
  }

  public void javaScriptReleaseObject(BrowserSideObject paramBrowserSideObject)
  {
    checkValidity(null);
    npnReleaseObject(paramBrowserSideObject.getNativeObjectReference());
  }

  public Object javaScriptCall(BrowserSideObject paramBrowserSideObject, String paramString, Object[] paramArrayOfObject)
    throws JSException
  {
    checkValidity(paramBrowserSideObject);
    if (this.npp == 0L)
      return null;
    long l1 = 0L;
    long l2 = 0L;
    try
    {
      if ((paramArrayOfObject != null) && (paramArrayOfObject.length > 0))
      {
        l1 = allocateVariantArray(paramArrayOfObject.length);
        for (int i = 0; i < paramArrayOfObject.length; i++)
          objectToVariantArrayElement(paramArrayOfObject[i], l1, i);
      }
      long l3 = npnGetStringIdentifier(paramString);
      if (!npnHasMethod(this.npp, paramBrowserSideObject.getNativeObjectReference(), l3))
        throw JSExceptions.noSuchMethod(paramString);
      l2 = allocateVariantArray(1);
      boolean bool = npnInvoke(this.npp, paramBrowserSideObject.getNativeObjectReference(), l3, l1, paramArrayOfObject == null ? 0 : paramArrayOfObject.length, l2);
      if (!bool)
      {
        if (!npnHasMethod(this.npp, paramBrowserSideObject.getNativeObjectReference(), l3))
          throw JSExceptions.noSuchMethod(paramString);
        throw new JSException("JavaScript error while calling \"" + paramString + "\"");
      }
      Object localObject1 = variantArrayElementToObject(l2, 0);
      return localObject1;
    }
    finally
    {
      if (l1 != 0L)
        freeVariantArray(l1, paramArrayOfObject.length);
      if (l2 != 0L)
        freeVariantArray(l2, 1);
    }
  }

  public Object javaScriptEval(BrowserSideObject paramBrowserSideObject, String paramString)
    throws JSException
  {
    checkValidity(paramBrowserSideObject);
    long l = allocateVariantArray(1);
    try
    {
      boolean bool = npnEvaluate(this.npp, paramBrowserSideObject.getNativeObjectReference(), paramString, l);
      if (!bool)
        throw new JSException("JavaScript error evaluating code \"" + paramString + "\"");
      Object localObject1 = variantArrayElementToObject(l, 0);
      return localObject1;
    }
    finally
    {
      if (l != 0L)
        freeVariantArray(l, 1);
    }
  }

  public Object javaScriptGetMember(BrowserSideObject paramBrowserSideObject, String paramString)
    throws JSException
  {
    checkValidity(paramBrowserSideObject);
    return javaScriptGetMemberImpl(paramBrowserSideObject.getNativeObjectReference(), npnGetStringIdentifier(paramString), paramString, 0);
  }

  public void javaScriptSetMember(BrowserSideObject paramBrowserSideObject, String paramString, Object paramObject)
    throws JSException
  {
    checkValidity(paramBrowserSideObject);
    javaScriptSetMemberImpl(paramBrowserSideObject.getNativeObjectReference(), npnGetStringIdentifier(paramString), paramObject, paramString, 0);
  }

  public void javaScriptRemoveMember(BrowserSideObject paramBrowserSideObject, String paramString)
    throws JSException
  {
    checkValidity(paramBrowserSideObject);
    if (this.npp == 0L)
      return;
    if (!npnRemoveProperty(this.npp, paramBrowserSideObject.getNativeObjectReference(), npnGetStringIdentifier(paramString)))
      throw new JSException("JavaScript error removing member \"" + paramString + "\"");
  }

  public Object javaScriptGetSlot(BrowserSideObject paramBrowserSideObject, int paramInt)
    throws JSException
  {
    checkValidity(paramBrowserSideObject);
    return javaScriptGetMemberImpl(paramBrowserSideObject.getNativeObjectReference(), npnGetIntIdentifier(paramInt), null, paramInt);
  }

  public void javaScriptSetSlot(BrowserSideObject paramBrowserSideObject, int paramInt, Object paramObject)
    throws JSException
  {
    checkValidity(paramBrowserSideObject);
    javaScriptSetMemberImpl(paramBrowserSideObject.getNativeObjectReference(), npnGetIntIdentifier(paramInt), paramObject, null, paramInt);
  }

  public String javaScriptToString(BrowserSideObject paramBrowserSideObject)
  {
    Object localObject = null;
    String str = null;
    try
    {
      checkValidity(paramBrowserSideObject);
      localObject = javaScriptCall(paramBrowserSideObject, "toString", null);
    }
    catch (JSException localJSException)
    {
      localObject = "[object JSObject]";
    }
    if (localObject != null)
      str = localObject.toString();
    return str;
  }

  private Object javaScriptGetMemberImpl(long paramLong1, long paramLong2, String paramString, int paramInt)
    throws JSException
  {
    if (this.npp == 0L)
      return null;
    long l = allocateVariantArray(1);
    try
    {
      boolean bool = npnHasProperty(this.npp, paramLong1, paramLong2);
      if (!bool)
      {
        if (paramString != null)
          throw JSExceptions.noSuchProperty(paramString);
        throw JSExceptions.noSuchSlot(paramInt);
      }
      bool = npnGetProperty(this.npp, paramLong1, paramLong2, l);
      if (!bool)
      {
        if (paramString != null)
          throw new JSException("JavaScript error while getting property \"" + paramString + "\"");
        throw new JSException("JavaScript error while getting index " + paramInt);
      }
      Object localObject1 = variantArrayElementToObject(l, 0);
      return localObject1;
    }
    finally
    {
      if (l != 0L)
        freeVariantArray(l, 1);
    }
  }

  private void javaScriptSetMemberImpl(long paramLong1, long paramLong2, Object paramObject, String paramString, int paramInt)
    throws JSException
  {
    if (this.npp == 0L)
      return;
    long l = allocateVariantArray(1);
    try
    {
      objectToVariantArrayElement(paramObject, l, 0);
      boolean bool = npnSetProperty(this.npp, paramLong1, paramLong2, l);
      if (!bool)
      {
        if (paramString != null)
          throw new JSException("JavaScript error while setting property \"" + paramString + "\"");
        throw new JSException("JavaScript error while setting index " + paramInt);
      }
    }
    finally
    {
      if (l != 0L)
        freeVariantArray(l, 1);
    }
  }

  private static native long npnGetStringIdentifier(String paramString);

  private static native long npnGetIntIdentifier(int paramInt);

  private static native boolean npnIdentifierIsString(long paramLong);

  private static native String npnUTF8FromIdentifier(long paramLong);

  private static native int npnIntFromIdentifier(long paramLong);

  private static native void npnRetainObject(long paramLong);

  private static native void npnReleaseObject(long paramLong);

  private static native boolean npnInvoke(long paramLong1, long paramLong2, long paramLong3, long paramLong4, int paramInt, long paramLong5);

  private static native boolean npnEvaluate(long paramLong1, long paramLong2, String paramString, long paramLong3);

  private static native boolean npnGetProperty(long paramLong1, long paramLong2, long paramLong3, long paramLong4);

  private static native boolean npnSetProperty(long paramLong1, long paramLong2, long paramLong3, long paramLong4);

  private static native boolean npnRemoveProperty(long paramLong1, long paramLong2, long paramLong3);

  private static native boolean npnHasProperty(long paramLong1, long paramLong2, long paramLong3);

  private static native boolean npnHasMethod(long paramLong1, long paramLong2, long paramLong3);

  private static native void npnSetException(long paramLong, String paramString);

  protected boolean scriptingObjectArgumentListsAreReversed()
  {
    return false;
  }

  protected native long allocateVariantArray(int paramInt);

  protected native void freeVariantArray(long paramLong, int paramInt);

  protected native void setVariantArrayElement0(long paramLong, int paramInt, boolean paramBoolean);

  protected native void setVariantArrayElement0(long paramLong, int paramInt, byte paramByte);

  protected native void setVariantArrayElement0(long paramLong, int paramInt, char paramChar);

  protected native void setVariantArrayElement0(long paramLong, int paramInt, short paramShort);

  protected native void setVariantArrayElement0(long paramLong, int paramInt1, int paramInt2);

  protected native void setVariantArrayElement0(long paramLong1, int paramInt, long paramLong2);

  protected native void setVariantArrayElement0(long paramLong, int paramInt, float paramFloat);

  protected native void setVariantArrayElement0(long paramLong, int paramInt, double paramDouble);

  protected native void setVariantArrayElement0(long paramLong, int paramInt, String paramString);

  protected native void setVariantArrayElementToScriptingObject0(long paramLong1, int paramInt, long paramLong2);

  protected native void setVariantArrayElementToVoid0(long paramLong, int paramInt);

  protected void setVariantArrayElement(long paramLong, int paramInt, boolean paramBoolean)
  {
    setVariantArrayElement0(paramLong, paramInt, paramBoolean);
  }

  protected void setVariantArrayElement(long paramLong, int paramInt, byte paramByte)
  {
    if (DISABLE_406251_WORKAROUND)
      setVariantArrayElement0(paramLong, paramInt, paramByte);
    else
      setVariantArrayElement(paramLong, paramInt, paramByte);
  }

  protected void setVariantArrayElement(long paramLong, int paramInt, char paramChar)
  {
    setVariantArrayElement0(paramLong, paramInt, paramChar);
  }

  protected void setVariantArrayElement(long paramLong, int paramInt, short paramShort)
  {
    if (DISABLE_406251_WORKAROUND)
      setVariantArrayElement0(paramLong, paramInt, paramShort);
    else
      setVariantArrayElement(paramLong, paramInt, paramShort);
  }

  protected void setVariantArrayElement(long paramLong, int paramInt1, int paramInt2)
  {
    if (DISABLE_406251_WORKAROUND)
      setVariantArrayElement0(paramLong, paramInt1, paramInt2);
    else if ((paramInt2 & 0xC0000000) != 0)
      setVariantArrayElement(paramLong, paramInt1, paramInt2);
    else
      setVariantArrayElement0(paramLong, paramInt1, paramInt2);
  }

  protected void setVariantArrayElement(long paramLong1, int paramInt, long paramLong2)
  {
    setVariantArrayElement0(paramLong1, paramInt, paramLong2);
  }

  protected void setVariantArrayElement(long paramLong, int paramInt, float paramFloat)
  {
    setVariantArrayElement0(paramLong, paramInt, paramFloat);
  }

  protected void setVariantArrayElement(long paramLong, int paramInt, double paramDouble)
  {
    setVariantArrayElement0(paramLong, paramInt, paramDouble);
  }

  protected void setVariantArrayElement(long paramLong, int paramInt, String paramString)
  {
    setVariantArrayElement0(paramLong, paramInt, paramString);
  }

  protected void setVariantArrayElementToScriptingObject(long paramLong1, int paramInt, long paramLong2)
  {
    setVariantArrayElementToScriptingObject0(paramLong1, paramInt, paramLong2);
  }

  protected void setVariantArrayElementToVoid(long paramLong, int paramInt)
  {
    setVariantArrayElementToVoid0(paramLong, paramInt);
  }

  protected Object variantArrayElementToObject(long paramLong, int paramInt)
  {
    return variantArrayElementToObject0(this.mozPluginInstance, paramLong, paramInt);
  }

  protected static native Object variantArrayElementToObject0(long paramLong1, long paramLong2, int paramInt);

  private static native void setAppletStatus(long paramLong, int paramInt);

  private static native long hookupApplet(long paramLong, RemoteJavaObject paramRemoteJavaObject, int paramInt);

  private static native long allocateNPObject(long paramLong, RemoteJavaObject paramRemoteJavaObject);

  protected long lookupScriptingObject(RemoteJavaObject paramRemoteJavaObject, boolean paramBoolean)
  {
    synchronized (npObjectMap)
    {
      Long localLong = (Long)npObjectMap.get(paramRemoteJavaObject);
      if (localLong != null)
        return localLong.longValue();
      long l = 0L;
      if (paramBoolean)
        l = hookupApplet(this.mozPluginInstance, paramRemoteJavaObject, this.status);
      else
        l = allocateNPObject(this.mozPluginInstance, paramRemoteJavaObject);
      if (l != 0L)
      {
        localLong = new Long(l);
        npObjectMap.put(paramRemoteJavaObject, localLong);
        return localLong.longValue();
      }
      return 0L;
    }
  }

  protected Object wrapOrUnwrapScriptingObject(long paramLong)
  {
    return newBrowserSideObject(paramLong);
  }

  protected String identifierToString(long paramLong)
  {
    if (npnIdentifierIsString(paramLong))
      return npnUTF8FromIdentifier(paramLong);
    return Integer.toString(npnIntFromIdentifier(paramLong));
  }

  protected void fillInExceptionInfo(long paramLong, String paramString)
  {
    if (paramLong == 0L)
    {
      if (DEBUG)
        System.out.println("MozillaPlugin: JavaScript error: " + paramString);
    }
    else
      npnSetException(paramLong, paramString);
  }

  protected void fillInExceptionInfo(long paramLong, Exception paramException)
  {
    if (paramLong == 0L)
    {
      if (DEBUG)
        paramException.printStackTrace();
    }
    else
      fillInExceptionInfo(paramLong, paramException.getMessage());
  }

  protected void releaseRemoteJavaObject(RemoteJavaObject paramRemoteJavaObject)
  {
    super.releaseRemoteJavaObject(paramRemoteJavaObject);
    synchronized (npObjectMap)
    {
      npObjectMap.remove(paramRemoteJavaObject);
    }
  }

  protected long getScriptingObjectForApplet(long paramLong)
  {
    if (isDummyApplet())
    {
      maybeStartApplet(true);
      return 0L;
    }
    return super.getScriptingObjectForApplet(paramLong);
  }

  public void waitForSignalWithModalBlocking()
  {
    if (DEBUG)
      System.out.println("MozillaPlugin entering waitForSignalWithModalBlocking for " + this.appletID);
    this.handler.waitForSignalWithModalBlocking(this.hWndControlWindow, this.appletID.getID());
    if (DEBUG)
      System.out.println("MozillaPlugin exiting waitForSignalWithModalBlocking for " + this.appletID);
  }

  private static synchronized List getRunnableQueue(Long paramLong)
  {
    List localList = (List)runnableQueueMap.get(paramLong);
    if (localList == null)
    {
      localList = Collections.synchronizedList(new LinkedList());
      runnableQueueMap.put(paramLong, localList);
    }
    return localList;
  }

  private Event getMainThreadEvent(Long paramLong)
  {
    synchronized (mainThreadEventMap)
    {
      Event localEvent = (Event)mainThreadEventMap.get(paramLong);
      if (localEvent == null)
      {
        localEvent = this.handler.createEvent();
        mainThreadEventMap.put(paramLong, localEvent);
      }
      return localEvent;
    }
  }

  private void disposeMainThreadEvent()
  {
    if (this.iCreatedMainThreadEvent)
    {
      synchronized (mainThreadEventMap)
      {
        Event localEvent = (Event)mainThreadEventMap.remove(this.browserThreadId);
        if (localEvent != null)
          localEvent.dispose();
      }
      this.iCreatedMainThreadEvent = false;
    }
  }

  private void drainRunnableQueue()
  {
    List localList = getRunnableQueue(this.browserThreadId);
    while (!localList.isEmpty())
    {
      RunnableWrapper localRunnableWrapper = (RunnableWrapper)localList.remove(0);
      if (!localRunnableWrapper.getPlugin().isDestroyed())
        localRunnableWrapper.run();
    }
  }

  private PlatformDependentHandler getPlatformDependentHandler()
  {
    if (SystemUtil.getOSType() == 1)
      return new WindowsHandler();
    if ((SystemUtil.getOSType() == 2) || (SystemUtil.getOSType() == 3))
      return new UnixHandler();
    throw new RuntimeException("Need to port platform-specific portion of MozillaPlugin to your platform");
  }

  private String getRequestedVersion(String paramString)
  {
    if ((paramString != null) && (paramString.indexOf(";version=") != -1))
    {
      String[] arrayOfString = paramString.split(";version=");
      if ((arrayOfString.length == 2) && (isValidVersionString(arrayOfString[1])))
        return arrayOfString[1] + "+";
    }
    return null;
  }

  private boolean isValidVersionString(String paramString)
  {
    if (paramString.length() < 3)
      return false;
    for (int i = 0; i < paramString.length(); i++)
    {
      int j = paramString.charAt(i);
      if ("0123456789._".indexOf(j) == -1)
        return false;
    }
    return paramString.compareTo("1.4") >= 0;
  }

  static
  {
    Platform.get().loadDeployNativeLib();
    NativeLibLoader.load(new String[] { "npjp2" });
    PluginTrace.init();
    int i = SystemUtil.getOSType();
    if ((i == 1) || (i == 2) || (i == 3))
      ServiceManager.setService(new MozillaBrowserService());
    else
      throw new RuntimeException("Must port BrowserService portion of MozillaPlugin to your platform");
    JVMManager.setBrowserType(3);
  }

  class DrainRunnableQueue
    implements Runnable
  {
    DrainRunnableQueue()
    {
    }

    public void run()
    {
      MozillaPlugin.this.drainRunnableQueue();
    }
  }

  static abstract class PlatformDependentHandler extends ResultHandler
  {
    public abstract Event createEvent();

    public abstract void waitForSignalWithModalBlocking(long paramLong, int paramInt);
  }

  static class RunnableWrapper
    implements Runnable
  {
    private Runnable runnable;
    private MozillaPlugin plugin;

    public RunnableWrapper(Runnable paramRunnable, MozillaPlugin paramMozillaPlugin)
    {
      this.runnable = paramRunnable;
      this.plugin = paramMozillaPlugin;
    }

    public void run()
    {
      this.runnable.run();
    }

    public MozillaPlugin getPlugin()
    {
      return this.plugin;
    }
  }

  class UnixHandler extends MozillaPlugin.PlatformDependentHandler
  {
    UnixHandler()
    {
    }

    public Event createEvent()
    {
      return new InProcEvent();
    }

    public void waitForSignal()
    {
      waitForSignal(0L);
    }

    public void waitForSignal(long paramLong)
    {
      MozillaPlugin.this.getMainThreadEvent(MozillaPlugin.this.browserThreadId).waitForSignal(paramLong);
      if (MozillaPlugin.this.isBrowserThread())
        MozillaPlugin.this.drainRunnableQueue();
    }

    public void waitForSignalWithModalBlocking(long paramLong, int paramInt)
    {
      waitForSignal();
    }
  }

  class WindowsHandler extends MozillaPlugin.PlatformDependentHandler
  {
    WindowsHandler()
    {
    }

    public Event createEvent()
    {
      return IPCFactory.getFactory().createEvent(null);
    }

    public void waitForSignal()
    {
      waitForSignal(-1L);
    }

    public void waitForSignal(long paramLong)
    {
      WindowsHelper.runMessagePump(MozillaPlugin.this.getMainThreadEvent(MozillaPlugin.this.browserThreadId), paramLong, ModalitySupport.appletShouldBlockBrowser(MozillaPlugin.this.appletID));
      if (MozillaPlugin.this.isBrowserThread())
        MozillaPlugin.this.drainRunnableQueue();
    }

    public void waitForSignalWithModalBlocking(long paramLong, int paramInt)
    {
      boolean bool = WindowsHelper.registerModalDialogHooks(paramLong, paramInt);
      waitForSignal();
      if (bool)
        WindowsHelper.unregisterModalDialogHooks(paramLong);
    }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.main.server.MozillaPlugin
 * JD-Core Version:    0.6.2
 */