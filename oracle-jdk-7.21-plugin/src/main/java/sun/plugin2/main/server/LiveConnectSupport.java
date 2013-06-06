package sun.plugin2.main.server;

import com.sun.deploy.util.SystemUtils;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import netscape.javascript.JSException;
import sun.plugin2.liveconnect.BrowserSideObject;
import sun.plugin2.liveconnect.RemoteJavaObject;
import sun.plugin2.message.Conversation;
import sun.plugin2.util.SystemUtil;

public class LiveConnectSupport
{
  private static final boolean DEBUG = SystemUtil.getenv("JPI_PLUGIN2_DEBUG") != null;
  private static final boolean VERBOSE = SystemUtil.getenv("JPI_PLUGIN2_VERBOSE") != null;
  private static Map pluginInfoMap = new HashMap();
  private static Map currentConversationMap = new HashMap();
  private static int curResultID;
  private static Map resultIDInterestMap = new HashMap();
  private static Map results = new HashMap();

  public static synchronized void initialize(int paramInt, Plugin paramPlugin)
  {
    AppletID localAppletID = new AppletID(paramInt);
    pluginInfoMap.put(localAppletID, new PerPluginInfo(paramPlugin, paramInt));
  }

  public static synchronized void shutdown(int paramInt)
  {
    if (DEBUG)
      System.out.println("  LiveConnectSupport.shutdown(" + paramInt + ")");
    PerPluginInfo localPerPluginInfo = getInfo(paramInt);
    localPerPluginInfo.releaseAllObjects();
    AppletID localAppletID = new AppletID(paramInt);
    pluginInfoMap.remove(localAppletID);
    currentConversationMap.remove(localPerPluginInfo.getPlugin());
    localPerPluginInfo.getPlugin().notifyMainThread();
  }

  public static void registerObject(int paramInt, BrowserSideObject paramBrowserSideObject)
    throws JSException
  {
    getInfo(paramInt).registerObject(paramBrowserSideObject);
  }

  public static void releaseObject(int paramInt, BrowserSideObject paramBrowserSideObject)
  {
    try
    {
      getInfo(paramInt).releaseObject(paramBrowserSideObject);
    }
    catch (JSException localJSException)
    {
    }
  }

  public static BrowserSideObject javaScriptGetWindow(Conversation paramConversation, int paramInt)
    throws JSException
  {
    return getInfo(paramInt).javaScriptGetWindow(paramConversation);
  }

  public static Object javaScriptCall(Conversation paramConversation, int paramInt, BrowserSideObject paramBrowserSideObject, String paramString, Object[] paramArrayOfObject)
    throws JSException
  {
    Object localObject1 = null;
    PerPluginInfo localPerPluginInfo = getInfo(paramInt);
    Plugin localPlugin = localPerPluginInfo.getPlugin();
    localPlugin.incrementActiveJSCounter();
    try
    {
      localObject1 = localPerPluginInfo.javaScriptCall(paramConversation, paramBrowserSideObject, paramString, paramArrayOfObject);
    }
    finally
    {
      localPlugin.decrementActiveJSCounter();
    }
    return localObject1;
  }

  public static Object javaScriptEval(Conversation paramConversation, int paramInt, BrowserSideObject paramBrowserSideObject, String paramString)
    throws JSException
  {
    Object localObject1 = null;
    PerPluginInfo localPerPluginInfo = getInfo(paramInt);
    Plugin localPlugin = localPerPluginInfo.getPlugin();
    localPlugin.incrementActiveJSCounter();
    try
    {
      localObject1 = localPerPluginInfo.javaScriptEval(paramConversation, paramBrowserSideObject, paramString);
    }
    finally
    {
      localPlugin.decrementActiveJSCounter();
    }
    return localObject1;
  }

  public static Object javaScriptGetMember(Conversation paramConversation, int paramInt, BrowserSideObject paramBrowserSideObject, String paramString)
    throws JSException
  {
    return getInfo(paramInt).javaScriptGetMember(paramConversation, paramBrowserSideObject, paramString);
  }

  public static void javaScriptSetMember(Conversation paramConversation, int paramInt, BrowserSideObject paramBrowserSideObject, String paramString, Object paramObject)
    throws JSException
  {
    getInfo(paramInt).javaScriptSetMember(paramConversation, paramBrowserSideObject, paramString, paramObject);
  }

  public static void javaScriptRemoveMember(Conversation paramConversation, int paramInt, BrowserSideObject paramBrowserSideObject, String paramString)
    throws JSException
  {
    getInfo(paramInt).javaScriptRemoveMember(paramConversation, paramBrowserSideObject, paramString);
  }

  public static Object javaScriptGetSlot(Conversation paramConversation, int paramInt1, BrowserSideObject paramBrowserSideObject, int paramInt2)
    throws JSException
  {
    return getInfo(paramInt1).javaScriptGetSlot(paramConversation, paramBrowserSideObject, paramInt2);
  }

  public static void javaScriptSetSlot(Conversation paramConversation, int paramInt1, BrowserSideObject paramBrowserSideObject, int paramInt2, Object paramObject)
    throws JSException
  {
    getInfo(paramInt1).javaScriptSetSlot(paramConversation, paramBrowserSideObject, paramInt2, paramObject);
  }

  public static String javaScriptToString(Conversation paramConversation, int paramInt, BrowserSideObject paramBrowserSideObject)
  {
    return getInfo(paramInt).javaScriptToString(paramConversation, paramBrowserSideObject);
  }

  public static ResultID sendGetApplet(Plugin paramPlugin, AppletID paramAppletID)
    throws IOException
  {
    int i = nextResultID();
    ResultID localResultID = new ResultID(i);
    synchronized (LiveConnectSupport.class)
    {
      resultIDInterestMap.put(localResultID, paramPlugin);
    }
    resendGetApplet(paramAppletID, localResultID);
    return localResultID;
  }

  public static void resendGetApplet(AppletID paramAppletID, ResultID paramResultID)
    throws IOException
  {
    JVMManager.getManager().sendGetApplet(paramAppletID, paramResultID.getID());
  }

  public static ResultID sendGetNameSpace(Plugin paramPlugin, AppletID paramAppletID, String paramString)
    throws IOException
  {
    int i = nextResultID();
    ResultID localResultID = new ResultID(i);
    synchronized (LiveConnectSupport.class)
    {
      resultIDInterestMap.put(localResultID, paramPlugin);
    }
    JVMManager.getManager().sendGetNameSpace(paramAppletID, paramString, i);
    return localResultID;
  }

  public static ResultID sendRemoteJavaObjectOp(Plugin paramPlugin, RemoteJavaObject paramRemoteJavaObject, String paramString, int paramInt, Object[] paramArrayOfObject)
    throws IOException, JSException
  {
    int i = paramRemoteJavaObject.getJVMID();
    if (paramArrayOfObject != null)
      for (int j = 0; j < paramArrayOfObject.length; j++)
      {
        Object localObject1 = paramArrayOfObject[j];
        if ((localObject1 != null) && ((localObject1 instanceof RemoteJavaObject)))
        {
          localObject2 = (RemoteJavaObject)localObject1;
          if (((RemoteJavaObject)localObject2).getJVMID() != i)
            throw new JSException("Can not pass objects between JVMs (arg " + j + " JVM ID = " + ((RemoteJavaObject)localObject2).getJVMID() + ", required " + i + ")");
        }
      }
    Conversation localConversation = getCurrentConversation(paramPlugin);
    int k = nextResultID();
    Object localObject2 = new ResultID(k);
    synchronized (LiveConnectSupport.class)
    {
      resultIDInterestMap.put(localObject2, paramPlugin);
    }
    if (DEBUG)
      System.out.println("LiveConnectSupport.sendRemoteJavaObjectOp: " + getOpName(paramInt) + " \"" + paramString + "\"");
    JVMManager.getManager().sendRemoteJavaObjectOp(localConversation, paramRemoteJavaObject, paramString, paramInt, paramArrayOfObject, k);
    return localObject2;
  }

  public static synchronized void recordResult(ResultID paramResultID, Object paramObject)
  {
    results.put(paramResultID, paramObject);
    Plugin localPlugin = (Plugin)resultIDInterestMap.remove(paramResultID);
    if ((DEBUG) && (localPlugin == null))
      System.out.println("*** WARNING: no plugin was waiting for result " + paramResultID);
    if (localPlugin != null)
      localPlugin.notifyMainThread();
  }

  public static synchronized boolean resultAvailable(ResultID paramResultID)
  {
    return results.containsKey(paramResultID);
  }

  public static synchronized Object getResult(ResultID paramResultID)
    throws RuntimeException
  {
    if (!resultAvailable(paramResultID))
      throw new RuntimeException("Result has not yet been produced for " + paramResultID);
    Object localObject = results.remove(paramResultID);
    if ((localObject != null) && ((localObject instanceof RuntimeException)))
    {
      if (DEBUG)
      {
        System.out.println("LiveConnectSupport: exception thrown during JavaScript -> Java call:");
        ((RuntimeException)localObject).printStackTrace();
      }
      throw ((RuntimeException)localObject);
    }
    if (DEBUG)
      System.out.println("LiveConnectSupport: result " + paramResultID + " = " + localObject);
    return localObject;
  }

  private static synchronized PerPluginInfo getInfo(int paramInt)
    throws JSException
  {
    PerPluginInfo localPerPluginInfo = (PerPluginInfo)pluginInfoMap.get(new AppletID(paramInt));
    if (localPerPluginInfo == null)
      throw new JSException("Plugin instance for applet ID " + paramInt + " was already released");
    return localPerPluginInfo;
  }

  private static synchronized Conversation replaceCurrentConversation(Plugin paramPlugin, Conversation paramConversation)
  {
    return (Conversation)currentConversationMap.put(paramPlugin, paramConversation);
  }

  private static synchronized Conversation getCurrentConversation(Plugin paramPlugin)
  {
    return (Conversation)currentConversationMap.get(paramPlugin);
  }

  private static synchronized int nextResultID()
  {
    return ++curResultID;
  }

  private static String getOpName(int paramInt)
  {
    switch (paramInt)
    {
    case 1:
      return "CALL_METHOD";
    case 2:
      return "GET_FIELD";
    case 3:
      return "SET_FIELD";
    case 4:
      return "HAS_FIELD";
    case 5:
      return "HAS_METHOD";
    case 6:
      return "HAS_FIELD_OR_METHOD";
    }
    throw new IllegalArgumentException("Invalid operation kind " + paramInt);
  }

  static class PerPluginInfo
  {
    private Plugin plugin;
    private int appletID;
    private Map registeredBrowserSideObjects = new HashMap();

    public PerPluginInfo(Plugin paramPlugin, int paramInt)
    {
      this.plugin = paramPlugin;
      this.appletID = paramInt;
    }

    public Plugin getPlugin()
    {
      return this.plugin;
    }

    public synchronized void registerObject(BrowserSideObject paramBrowserSideObject)
    {
      Integer localInteger = (Integer)this.registeredBrowserSideObjects.get(paramBrowserSideObject);
      if (localInteger == null)
      {
        this.plugin.javaScriptRetainObject(paramBrowserSideObject);
        if ((LiveConnectSupport.DEBUG) && (LiveConnectSupport.VERBOSE))
          System.out.println("  LiveConnectSupport: retained " + paramBrowserSideObject + " for applet " + this.appletID);
        localInteger = SystemUtils.integerValueOf(1);
      }
      else
      {
        localInteger = SystemUtils.integerValueOf(localInteger.intValue() + 1);
      }
      this.registeredBrowserSideObjects.put(paramBrowserSideObject, localInteger);
    }

    public synchronized void releaseObject(final BrowserSideObject paramBrowserSideObject)
    {
      Integer localInteger = (Integer)this.registeredBrowserSideObjects.remove(paramBrowserSideObject);
      if (localInteger == null)
      {
        if (LiveConnectSupport.DEBUG)
          System.out.println("!!! WARNING: LiveConnectSupport.releaseObject called for already released / untracked object 0x" + Long.toHexString(paramBrowserSideObject.getNativeObjectReference()));
        return;
      }
      if (localInteger.intValue() < 1)
        throw new IllegalStateException("Reference count should not be " + localInteger.intValue());
      if (localInteger.intValue() == 1)
      {
        if (LiveConnectSupport.DEBUG)
          System.out.println("  LiveConnectSupport: schedule release " + paramBrowserSideObject + " for applet " + this.appletID);
        this.plugin.invokeLater(new Runnable()
        {
          private final BrowserSideObject val$object;

          public void run()
          {
            LiveConnectSupport.PerPluginInfo.this.plugin.javaScriptReleaseObject(paramBrowserSideObject);
          }
        });
      }
      else
      {
        localInteger = SystemUtils.integerValueOf(localInteger.intValue() - 1);
        this.registeredBrowserSideObjects.put(paramBrowserSideObject, localInteger);
      }
    }

    public synchronized void releaseAllObjects()
    {
      Iterator localIterator = this.registeredBrowserSideObjects.keySet().iterator();
      while (localIterator.hasNext())
      {
        BrowserSideObject localBrowserSideObject = (BrowserSideObject)localIterator.next();
        this.plugin.javaScriptReleaseObject(localBrowserSideObject);
        if ((LiveConnectSupport.DEBUG) && (LiveConnectSupport.VERBOSE))
          System.out.println("  LiveConnectSupport: released " + localBrowserSideObject + " for applet " + this.appletID);
      }
      this.registeredBrowserSideObjects.clear();
    }

    public BrowserSideObject javaScriptGetWindow(Conversation paramConversation)
    {
      return this.plugin.javaScriptGetWindow();
    }

    public Object javaScriptCall(Conversation paramConversation, BrowserSideObject paramBrowserSideObject, String paramString, Object[] paramArrayOfObject)
      throws JSException
    {
      validateObject(paramBrowserSideObject);
      validateObjectArray(paramArrayOfObject);
      if (LiveConnectSupport.DEBUG)
      {
        System.out.println("LiveConnectSupport.PerPluginInfo.javaScriptCall:");
        System.out.println("  methodName: " + paramString);
        System.out.print("  args: ");
        if (paramArrayOfObject == null)
        {
          System.out.println("null");
        }
        else
        {
          System.out.print("[");
          for (int i = 0; i < paramArrayOfObject.length; i++)
          {
            if (i > 0)
              System.out.print(", ");
            System.out.print(paramArrayOfObject[i]);
          }
          System.out.println("]");
        }
      }
      Conversation localConversation = LiveConnectSupport.replaceCurrentConversation(this.plugin, paramConversation);
      try
      {
        Object localObject1 = this.plugin.javaScriptCall(paramBrowserSideObject, paramString, paramArrayOfObject);
        if (LiveConnectSupport.DEBUG)
          System.out.println("  result of call to \"" + paramString + "\": " + localObject1);
        Object localObject2 = localObject1;
        return localObject2;
      }
      finally
      {
        LiveConnectSupport.replaceCurrentConversation(this.plugin, localConversation);
      }
    }

    public Object javaScriptEval(Conversation paramConversation, BrowserSideObject paramBrowserSideObject, String paramString)
      throws JSException
    {
      validateObject(paramBrowserSideObject);
      Conversation localConversation = LiveConnectSupport.replaceCurrentConversation(this.plugin, paramConversation);
      try
      {
        Object localObject1 = this.plugin.javaScriptEval(paramBrowserSideObject, paramString);
        return localObject1;
      }
      finally
      {
        LiveConnectSupport.replaceCurrentConversation(this.plugin, localConversation);
      }
    }

    public Object javaScriptGetMember(Conversation paramConversation, BrowserSideObject paramBrowserSideObject, String paramString)
      throws JSException
    {
      validateObject(paramBrowserSideObject);
      Conversation localConversation = LiveConnectSupport.replaceCurrentConversation(this.plugin, paramConversation);
      try
      {
        Object localObject1 = this.plugin.javaScriptGetMember(paramBrowserSideObject, paramString);
        return localObject1;
      }
      finally
      {
        LiveConnectSupport.replaceCurrentConversation(this.plugin, localConversation);
      }
    }

    public void javaScriptSetMember(Conversation paramConversation, BrowserSideObject paramBrowserSideObject, String paramString, Object paramObject)
      throws JSException
    {
      validateObject(paramBrowserSideObject);
      validateObject(paramObject);
      Conversation localConversation = LiveConnectSupport.replaceCurrentConversation(this.plugin, paramConversation);
      try
      {
        this.plugin.javaScriptSetMember(paramBrowserSideObject, paramString, paramObject);
      }
      finally
      {
        LiveConnectSupport.replaceCurrentConversation(this.plugin, localConversation);
      }
    }

    public void javaScriptRemoveMember(Conversation paramConversation, BrowserSideObject paramBrowserSideObject, String paramString)
      throws JSException
    {
      validateObject(paramBrowserSideObject);
      Conversation localConversation = LiveConnectSupport.replaceCurrentConversation(this.plugin, paramConversation);
      try
      {
        this.plugin.javaScriptRemoveMember(paramBrowserSideObject, paramString);
      }
      finally
      {
        LiveConnectSupport.replaceCurrentConversation(this.plugin, localConversation);
      }
    }

    public Object javaScriptGetSlot(Conversation paramConversation, BrowserSideObject paramBrowserSideObject, int paramInt)
      throws JSException
    {
      validateObject(paramBrowserSideObject);
      Conversation localConversation = LiveConnectSupport.replaceCurrentConversation(this.plugin, paramConversation);
      try
      {
        Object localObject1 = this.plugin.javaScriptGetSlot(paramBrowserSideObject, paramInt);
        return localObject1;
      }
      finally
      {
        LiveConnectSupport.replaceCurrentConversation(this.plugin, localConversation);
      }
    }

    public void javaScriptSetSlot(Conversation paramConversation, BrowserSideObject paramBrowserSideObject, int paramInt, Object paramObject)
      throws JSException
    {
      validateObject(paramBrowserSideObject);
      validateObject(paramObject);
      Conversation localConversation = LiveConnectSupport.replaceCurrentConversation(this.plugin, paramConversation);
      try
      {
        this.plugin.javaScriptSetSlot(paramBrowserSideObject, paramInt, paramObject);
      }
      finally
      {
        LiveConnectSupport.replaceCurrentConversation(this.plugin, localConversation);
      }
    }

    public String javaScriptToString(Conversation paramConversation, BrowserSideObject paramBrowserSideObject)
    {
      validateObject(paramBrowserSideObject);
      Conversation localConversation = LiveConnectSupport.replaceCurrentConversation(this.plugin, paramConversation);
      try
      {
        String str = this.plugin.javaScriptToString(paramBrowserSideObject);
        return str;
      }
      finally
      {
        LiveConnectSupport.replaceCurrentConversation(this.plugin, localConversation);
      }
    }

    private void validateObjectArray(Object[] paramArrayOfObject)
      throws JSException
    {
      if (paramArrayOfObject != null)
        for (int i = 0; i < paramArrayOfObject.length; i++)
          validateObject(paramArrayOfObject[i]);
    }

    private void validateObject(Object paramObject)
      throws JSException
    {
      if (paramObject == null)
        return;
      if (((paramObject instanceof BrowserSideObject)) && (!this.registeredBrowserSideObjects.containsKey(paramObject)))
        throw new JSException("Attempt to reference unknown or already-released JavaScript object; may not pass JSObjects between applets directly");
    }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.main.server.LiveConnectSupport
 * JD-Core Version:    0.6.2
 */