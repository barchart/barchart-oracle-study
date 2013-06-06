package sun.plugin2.main.client;

import com.sun.deploy.resources.ResourceManager;
import com.sun.deploy.security.CeilingPolicy;
import com.sun.deploy.security.SecureCookiePermission;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.trace.TraceLevel;
import com.sun.deploy.ui.AppInfo;
import com.sun.deploy.uitoolkit.Applet2Adapter;
import com.sun.deploy.uitoolkit.ToolkitStore;
import com.sun.deploy.uitoolkit.ui.UIFactory;
import com.sun.java.browser.plugin2.liveconnect.v1.Bridge;
import com.sun.java.browser.plugin2.liveconnect.v1.ConversionDelegate;
import com.sun.java.browser.plugin2.liveconnect.v1.InvocationDelegate;
import com.sun.java.browser.plugin2.liveconnect.v1.Result;
import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.SocketPermission;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Policy;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;
import sun.net.www.ParseUtil;
import sun.plugin.liveconnect.JavaScriptProtectionDomain;
import sun.plugin2.applet.Plugin2ClassLoader;
import sun.plugin2.applet.Plugin2Manager;
import sun.plugin2.liveconnect.ArgumentHelper;
import sun.plugin2.liveconnect.BrowserSideObject;
import sun.plugin2.liveconnect.JavaClass;
import sun.plugin2.liveconnect.RemoteJavaObject;
import sun.plugin2.message.JavaObjectOpMessage;
import sun.plugin2.message.JavaReplyMessage;
import sun.plugin2.message.JavaScriptReleaseObjectMessage;
import sun.plugin2.message.Pipe;
import sun.plugin2.util.SystemUtil;

public class LiveConnectSupport
{
  private static final boolean DEBUG = SystemUtil.getenv("JPI_PLUGIN2_DEBUG") != null;
  private static Pipe pipe;
  private static int jvmID;
  private static ReferenceQueue queue = new ReferenceQueue();
  private static volatile boolean shouldStop;
  private static Thread cleanupThread;
  private static Vector bsoRefs = new Vector();
  private static Map exportedObjectMap = new IdentityHashMap();
  private static Map objectIDMap = new HashMap();
  private static int nextObjectID;
  private static Map appletInfoMap = new HashMap();

  public static void initialize(Pipe paramPipe, int paramInt)
  {
    pipe = paramPipe;
    jvmID = paramInt;
    cleanupThread = new BrowserSideObjectCleanupThread();
    cleanupThread.start();
  }

  public static void shutdown()
  {
    shouldStop = true;
    cleanupThread.interrupt();
  }

  public static synchronized void appletStarted(int paramInt, Plugin2Manager paramPlugin2Manager)
  {
    appletInfoMap.put(new Integer(paramInt), new PerAppletInfo(paramInt, paramPlugin2Manager));
  }

  public static synchronized void appletStopped(int paramInt)
  {
    PerAppletInfo localPerAppletInfo = (PerAppletInfo)appletInfoMap.remove(new Integer(paramInt));
    if (localPerAppletInfo != null)
      localPerAppletInfo.stop();
  }

  public static Object exportObject(Object paramObject, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramObject == null)
      return paramObject;
    if ((ArgumentHelper.isPrimitiveOrString(paramObject)) && (!paramBoolean1))
      return paramObject;
    if ((paramObject instanceof MessagePassingJSObject))
      return ((MessagePassingJSObject)paramObject).getBrowserSideObject();
    return exportRemoteObject(paramObject, paramInt, paramBoolean2);
  }

  public static Object importObject(Object paramObject, int paramInt)
  {
    if (paramObject == null)
      return paramObject;
    if (ArgumentHelper.isPrimitiveOrString(paramObject))
      return paramObject;
    if ((paramObject instanceof BrowserSideObject))
    {
      BrowserSideObject localBrowserSideObject = (BrowserSideObject)paramObject;
      MessagePassingJSObject localMessagePassingJSObject = new MessagePassingJSObject(localBrowserSideObject, paramInt, pipe);
      track(localMessagePassingJSObject);
      return localMessagePassingJSObject;
    }
    if ((paramObject instanceof RemoteJavaObject))
      return importRemoteObject((RemoteJavaObject)paramObject);
    throw new IllegalArgumentException("Unsupported argument type " + paramObject.getClass().getName());
  }

  public static Object importOneWayJSObject(Object paramObject, int paramInt, Plugin2Manager paramPlugin2Manager)
  {
    if (paramObject == null)
      return paramObject;
    if (ArgumentHelper.isPrimitiveOrString(paramObject))
      return paramObject;
    if ((paramObject instanceof BrowserSideObject))
    {
      BrowserSideObject localBrowserSideObject = (BrowserSideObject)paramObject;
      MessagePassingJSObject localMessagePassingJSObject = new MessagePassingJSObject(localBrowserSideObject, paramInt, pipe, paramPlugin2Manager);
      MessagePassingOneWayJSObject localMessagePassingOneWayJSObject = new MessagePassingOneWayJSObject(localMessagePassingJSObject);
      track2(localMessagePassingOneWayJSObject);
      return localMessagePassingOneWayJSObject;
    }
    if ((paramObject instanceof RemoteJavaObject))
      return importRemoteObject((RemoteJavaObject)paramObject);
    throw new IllegalArgumentException("Unsupported argument type " + paramObject.getClass().getName());
  }

  public static void doObjectOp(JavaObjectOpMessage paramJavaObjectOpMessage)
    throws IOException
  {
    RemoteJavaObject localRemoteJavaObject = paramJavaObjectOpMessage.getObject();
    PerAppletInfo localPerAppletInfo = getInfo(localRemoteJavaObject.getAppletID());
    if (localPerAppletInfo != null)
    {
      if (paramJavaObjectOpMessage.getConversation() == null)
        localPerAppletInfo.enqueue(paramJavaObjectOpMessage);
      else
        localPerAppletInfo.doObjectOp(paramJavaObjectOpMessage);
    }
    else
      pipe.send(new JavaReplyMessage(paramJavaObjectOpMessage.getConversation(), paramJavaObjectOpMessage.getResultID(), null, false, "Applet ID " + localRemoteJavaObject.getAppletID() + " is not registered in this JVM instance"));
  }

  public static synchronized void releaseRemoteObject(RemoteJavaObject paramRemoteJavaObject)
  {
    Integer localInteger = new Integer(paramRemoteJavaObject.getObjectID());
    Object localObject = objectIDMap.remove(localInteger);
    if (localObject != null)
      exportedObjectMap.remove(localObject);
  }

  private static void track(MessagePassingJSObject paramMessagePassingJSObject)
  {
    bsoRefs.add(new BrowserSideObjectReference(paramMessagePassingJSObject, queue, paramMessagePassingJSObject.getBrowserSideObject(), paramMessagePassingJSObject.getAppletID()));
  }

  private static void track2(MessagePassingOneWayJSObject paramMessagePassingOneWayJSObject)
  {
    bsoRefs.add(new BrowserSideObjectReference(paramMessagePassingOneWayJSObject, queue, paramMessagePassingOneWayJSObject.getBrowserSideObject(), paramMessagePassingOneWayJSObject.getAppletID()));
  }

  private static synchronized RemoteJavaObject exportRemoteObject(Object paramObject, int paramInt, boolean paramBoolean)
  {
    RemoteJavaObject localRemoteJavaObject = (RemoteJavaObject)exportedObjectMap.get(paramObject);
    if ((localRemoteJavaObject != null) && (!isAppletRunning(localRemoteJavaObject.getAppletID())))
    {
      releaseRemoteObject(localRemoteJavaObject);
      localRemoteJavaObject = null;
    }
    if (localRemoteJavaObject == null)
    {
      int i = ++nextObjectID;
      localRemoteJavaObject = new RemoteJavaObject(jvmID, paramInt, i, paramBoolean);
      exportedObjectMap.put(paramObject, localRemoteJavaObject);
      objectIDMap.put(new Integer(i), paramObject);
    }
    return localRemoteJavaObject;
  }

  private static synchronized Object importRemoteObject(RemoteJavaObject paramRemoteJavaObject)
  {
    return objectIDMap.get(new Integer(paramRemoteJavaObject.getObjectID()));
  }

  private static synchronized void releaseRemoteObjects(int paramInt)
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = exportedObjectMap.values().iterator();
    while (localIterator.hasNext())
    {
      RemoteJavaObject localRemoteJavaObject = (RemoteJavaObject)localIterator.next();
      if (localRemoteJavaObject.getAppletID() == paramInt)
        localArrayList.add(localRemoteJavaObject);
    }
    localIterator = localArrayList.iterator();
    while (localIterator.hasNext())
      releaseRemoteObject((RemoteJavaObject)localIterator.next());
  }

  private static synchronized PerAppletInfo getInfo(int paramInt)
  {
    return (PerAppletInfo)appletInfoMap.get(new Integer(paramInt));
  }

  private static boolean isAppletRunning(int paramInt)
  {
    return getInfo(paramInt) != null;
  }

  public static synchronized Bridge getBridge(Object paramObject)
  {
    Iterator localIterator = appletInfoMap.values().iterator();
    while (localIterator.hasNext())
    {
      PerAppletInfo localPerAppletInfo = (PerAppletInfo)localIterator.next();
      if (localPerAppletInfo.hostsApplet(paramObject))
        return localPerAppletInfo.getBridge();
    }
    return null;
  }

  private static AccessControlContext createContext(URL paramURL)
  {
    try
    {
      ProtectionDomain[] arrayOfProtectionDomain = new ProtectionDomain[1];
      arrayOfProtectionDomain[0] = getJSProtectionDomain(paramURL);
      return new AccessControlContext(arrayOfProtectionDomain);
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    return null;
  }

  private static ProtectionDomain getJSProtectionDomain(URL paramURL)
    throws MalformedURLException
  {
    Policy localPolicy = (Policy)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        return Policy.getPolicy();
      }
    });
    CodeSource localCodeSource = new CodeSource(null, (Certificate[])null);
    PermissionCollection localPermissionCollection = localPolicy.getPermissions(localCodeSource);
    Plugin2ClassLoader.addDefaultPermissions(localPermissionCollection);
    if (paramURL != null)
    {
      String str1 = null;
      Permission localPermission;
      try
      {
        localPermission = paramURL.openConnection().getPermission();
      }
      catch (IOException localIOException)
      {
        localPermission = null;
      }
      if ((localPermission instanceof FilePermission))
      {
        str1 = localPermission.getName();
      }
      else if ((localPermission == null) && (paramURL.getProtocol().equals("file")))
      {
        str1 = paramURL.getFile().replace('/', File.separatorChar);
        str1 = ParseUtil.decode(str1);
      }
      else if ((localPermission instanceof SocketPermission))
      {
        String str2 = paramURL.getHost();
        if ((str2 == null) || (str2.equals("")))
          try
          {
            str2 = new URL(paramURL.getFile()).getHost();
          }
          catch (MalformedURLException localMalformedURLException)
          {
          }
        if ((str2 != null) && (!str2.equals("")))
        {
          localPermissionCollection.add(new SocketPermission(str2, "connect,accept"));
          localPermissionCollection.add(new SecureCookiePermission(SecureCookiePermission.getURLOriginString(paramURL)));
        }
      }
      if (str1 != null)
      {
        if (str1.endsWith(File.separator))
        {
          str1 = str1 + "-";
        }
        else
        {
          int i = str1.lastIndexOf(File.separatorChar);
          if (i != -1)
            str1 = str1.substring(0, i + 1) + "-";
        }
        localPermissionCollection.add(new FilePermission(str1, "read"));
      }
      if ("chrome".equals(paramURL.getProtocol()))
        CeilingPolicy.addTrustedPermissions(localPermissionCollection);
    }
    return new JavaScriptProtectionDomain(localPermissionCollection);
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

  private static class BridgeImpl
    implements Bridge
  {
    private volatile LiveConnectSupport.PerAppletInfo info;

    private BridgeImpl(LiveConnectSupport.PerAppletInfo paramPerAppletInfo)
    {
      this.info = paramPerAppletInfo;
    }

    public void register(InvocationDelegate paramInvocationDelegate)
    {
      getInfo().register(paramInvocationDelegate);
    }

    public void unregister(InvocationDelegate paramInvocationDelegate)
    {
      getInfo().unregister(paramInvocationDelegate);
    }

    public void register(ConversionDelegate paramConversionDelegate)
    {
      getInfo().register(paramConversionDelegate);
    }

    public void unregister(ConversionDelegate paramConversionDelegate)
    {
      getInfo().unregister(paramConversionDelegate);
    }

    public int conversionCost(Object paramObject1, Object paramObject2)
    {
      return getInfo().conversionCost(paramObject1, paramObject2);
    }

    public Object convert(Object paramObject1, Object paramObject2)
      throws Exception
    {
      return getInfo().convert(paramObject1, paramObject2);
    }

    public void stop()
    {
      this.info = null;
    }

    private LiveConnectSupport.PerAppletInfo getInfo()
    {
      LiveConnectSupport.PerAppletInfo localPerAppletInfo = this.info;
      if (localPerAppletInfo == null)
        throw new IllegalStateException("Applet has already terminated");
      return localPerAppletInfo;
    }

    BridgeImpl(LiveConnectSupport.PerAppletInfo paramPerAppletInfo, LiveConnectSupport.1 param1)
    {
      this(paramPerAppletInfo);
    }
  }

  private static class BrowserSideObjectCleanupThread extends Thread
  {
    public BrowserSideObjectCleanupThread()
    {
      super();
    }

    public void run()
    {
      while (!LiveConnectSupport.shouldStop)
        try
        {
          if (LiveConnectSupport.DEBUG)
            Trace.println("Waiting to GC browser side object ...", TraceLevel.LIVECONNECT);
          LiveConnectSupport.BrowserSideObjectReference localBrowserSideObjectReference = (LiveConnectSupport.BrowserSideObjectReference)LiveConnectSupport.queue.remove();
          if (LiveConnectSupport.DEBUG)
            Trace.println("About to GC browser side object " + localBrowserSideObjectReference.getObject().getNativeObjectReference(), TraceLevel.LIVECONNECT);
          LiveConnectSupport.bsoRefs.remove(localBrowserSideObjectReference);
          LiveConnectSupport.pipe.send(new JavaScriptReleaseObjectMessage(null, localBrowserSideObjectReference.getObject(), localBrowserSideObjectReference.getAppletId()));
        }
        catch (IOException localIOException)
        {
          return;
        }
        catch (Exception localException)
        {
          localException.printStackTrace();
        }
    }
  }

  private static class BrowserSideObjectReference extends PhantomReference
  {
    private final BrowserSideObject bso;
    private final int appletId;

    public BrowserSideObjectReference(Object paramObject, ReferenceQueue paramReferenceQueue, BrowserSideObject paramBrowserSideObject, int paramInt)
    {
      super(paramReferenceQueue);
      this.bso = paramBrowserSideObject;
      this.appletId = paramInt;
    }

    public BrowserSideObject getObject()
    {
      return this.bso;
    }

    public int getAppletId()
    {
      return this.appletId;
    }
  }

  private static class PerAppletInfo
  {
    private int appletID;
    private Plugin2Manager manager;
    private LiveConnectWorker worker;
    private LiveConnectSupport.BridgeImpl bridge;
    private boolean fetchedDocumentBase;
    private URL documentBase;
    private AccessControlContext context;
    private boolean liveconnectChecked = false;
    private boolean liveconnectPermissionGranted = false;
    private List invocationDelegates = Collections.synchronizedList(new ArrayList());
    private List conversionDelegates = Collections.synchronizedList(new ArrayList());
    private Map classes = new HashMap();
    private Set notJavaClasses = new HashSet();
    private static Map appletLiveconnectAllowedMap = Collections.synchronizedMap(new HashMap());

    public PerAppletInfo(int paramInt, Plugin2Manager paramPlugin2Manager)
    {
      this.appletID = paramInt;
      this.manager = paramPlugin2Manager;
      this.bridge = new LiveConnectSupport.BridgeImpl(this, null);
      register(new DefaultInvocationDelegate());
      register(new DefaultConversionDelegate());
      this.worker = new LiveConnectWorker(null);
      paramPlugin2Manager.startWorkerThread("Applet " + paramInt + " LiveConnect Worker Thread", this.worker);
    }

    public boolean hostsApplet(Object paramObject)
    {
      return (this.manager != null) && (this.manager.getApplet2Adapter().getLiveConnectObject() == paramObject);
    }

    public Bridge getBridge()
    {
      return this.bridge;
    }

    public void register(InvocationDelegate paramInvocationDelegate)
    {
      this.invocationDelegates.add(0, paramInvocationDelegate);
    }

    public void unregister(InvocationDelegate paramInvocationDelegate)
    {
      this.invocationDelegates.remove(paramInvocationDelegate);
    }

    public void register(ConversionDelegate paramConversionDelegate)
    {
      this.conversionDelegates.add(0, paramConversionDelegate);
    }

    public void unregister(ConversionDelegate paramConversionDelegate)
    {
      this.conversionDelegates.remove(paramConversionDelegate);
    }

    public int conversionCost(final Object paramObject1, final Object paramObject2)
    {
      final int[] arrayOfInt = new int[1];
      AccessController.doPrivileged(new PrivilegedAction()
      {
        private final Object val$object;
        private final Object val$toType;
        private final int[] val$resultBox;

        public Object run()
        {
          Iterator localIterator = LiveConnectSupport.PerAppletInfo.this.conversionDelegates.iterator();
          while (localIterator.hasNext())
          {
            ConversionDelegate localConversionDelegate = (ConversionDelegate)localIterator.next();
            int i = localConversionDelegate.conversionCost(paramObject1, paramObject2);
            if (i >= 0)
            {
              arrayOfInt[0] = i;
              return null;
            }
          }
          arrayOfInt[0] = -1;
          return null;
        }
      }
      , getContext());
      return arrayOfInt[0];
    }

    public Object convert(final Object paramObject1, final Object paramObject2)
      throws Exception
    {
      return AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        private final Object val$object;
        private final Object val$toType;

        public Object run()
          throws Exception
        {
          Object[] arrayOfObject = new Object[1];
          Iterator localIterator = LiveConnectSupport.PerAppletInfo.this.conversionDelegates.iterator();
          while (localIterator.hasNext())
          {
            ConversionDelegate localConversionDelegate = (ConversionDelegate)localIterator.next();
            if (localConversionDelegate.convert(paramObject1, paramObject2, arrayOfObject))
              return arrayOfObject[0];
          }
          throw LiveConnectSupport.PerAppletInfo.inconvertible(paramObject1, paramObject2);
        }
      }
      , getContext());
    }

    public void enqueue(JavaObjectOpMessage paramJavaObjectOpMessage)
    {
      this.worker.enqueue(paramJavaObjectOpMessage);
    }

    public void stop()
    {
      this.bridge.stop();
      this.worker.stop();
      LiveConnectSupport.releaseRemoteObjects(this.appletID);
    }

    public void doObjectOp(final JavaObjectOpMessage paramJavaObjectOpMessage)
      throws IOException
    {
      JavaReplyMessage localJavaReplyMessage = null;
      if (LiveConnectSupport.DEBUG)
        System.out.println("LiveConnectSupport: " + LiveConnectSupport.getOpName(paramJavaObjectOpMessage.getOperationKind()) + " \"" + paramJavaObjectOpMessage.getMemberName() + "\"");
      final Object localObject1;
      if (!isLiveconnectCallAllowed(paramJavaObjectOpMessage))
      {
        localObject1 = paramJavaObjectOpMessage.getObject();
        localJavaReplyMessage = new JavaReplyMessage(paramJavaObjectOpMessage.getConversation(), paramJavaObjectOpMessage.getResultID(), null, false, "Liveconnect call for Applet ID " + ((RemoteJavaObject)localObject1).getAppletID() + " is not allowed in this JVM instance");
        LiveConnectSupport.pipe.send(localJavaReplyMessage);
        return;
      }
      try
      {
        waitForAppletStartOrError();
        if (this.manager.hasErrorOccurred())
        {
          if (this.manager.getErrorMessage() != null)
            throw new RuntimeException(this.manager.getErrorMessage());
          if (this.manager.getErrorException() != null)
            throw ((IOException)new IOException().initCause(this.manager.getErrorException()));
        }
        localObject1 = LiveConnectSupport.importObject(paramJavaObjectOpMessage.getObject(), this.appletID);
        localObject2 = paramJavaObjectOpMessage.getArguments();
        if (localObject2 != null)
          for (int i = 0; i < localObject2.length; i++)
            localObject2[i] = LiveConnectSupport.importObject(localObject2[i], this.appletID);
        Result localResult = null;
        final boolean bool1 = paramJavaObjectOpMessage.getObject().isApplet();
        boolean bool2 = false;
        switch (paramJavaObjectOpMessage.getOperationKind())
        {
        case 1:
          localResult = (Result)AccessController.doPrivileged(new PrivilegedExceptionAction()
          {
            private final JavaObjectOpMessage val$msg;
            private final Object val$target;
            private final Object[] val$args;
            private final boolean val$isApplet;

            public Object run()
              throws Exception
            {
              Result[] arrayOfResult = new Result[1];
              Iterator localIterator = LiveConnectSupport.PerAppletInfo.this.invocationDelegates.iterator();
              while (localIterator.hasNext())
              {
                InvocationDelegate localInvocationDelegate = (InvocationDelegate)localIterator.next();
                if (localInvocationDelegate.invoke(paramJavaObjectOpMessage.getMemberName(), localObject1, this.val$args, false, bool1, arrayOfResult))
                  break;
              }
              return arrayOfResult[0];
            }
          }
          , getContext());
          if (localResult.value() == Void.TYPE)
          {
            bool2 = true;
            localResult = null;
          }
          break;
        case 2:
          localResult = (Result)AccessController.doPrivileged(new PrivilegedExceptionAction()
          {
            private final JavaObjectOpMessage val$msg;
            private final Object val$target;
            private final boolean val$isApplet;

            public Object run()
              throws Exception
            {
              Result[] arrayOfResult = new Result[1];
              Iterator localIterator = LiveConnectSupport.PerAppletInfo.this.invocationDelegates.iterator();
              while (localIterator.hasNext())
              {
                InvocationDelegate localInvocationDelegate = (InvocationDelegate)localIterator.next();
                if (localInvocationDelegate.getField(paramJavaObjectOpMessage.getMemberName(), localObject1, false, bool1, arrayOfResult))
                  break;
              }
              return arrayOfResult[0];
            }
          }
          , getContext());
          break;
        case 3:
          AccessController.doPrivileged(new PrivilegedExceptionAction()
          {
            private final JavaObjectOpMessage val$msg;
            private final Object val$target;
            private final Object[] val$args;
            private final boolean val$isApplet;

            public Object run()
              throws Exception
            {
              Iterator localIterator = LiveConnectSupport.PerAppletInfo.this.invocationDelegates.iterator();
              while (localIterator.hasNext())
              {
                InvocationDelegate localInvocationDelegate = (InvocationDelegate)localIterator.next();
                if (localInvocationDelegate.setField(paramJavaObjectOpMessage.getMemberName(), localObject1, this.val$args[0], false, bool1))
                  break;
              }
              return null;
            }
          }
          , getContext());
          bool2 = true;
          break;
        case 4:
          localResult = (Result)AccessController.doPrivileged(new PrivilegedExceptionAction()
          {
            private final boolean val$isApplet;
            private final JavaObjectOpMessage val$msg;
            private final Object val$target;

            public Object run()
              throws Exception
            {
              boolean[] arrayOfBoolean = new boolean[1];
              int i = 0;
              Object localObject;
              if (bool1)
              {
                localObject = paramJavaObjectOpMessage.getMemberName().toLowerCase();
                if ((((String)localObject).equals("width")) || (((String)localObject).equals("height")))
                {
                  arrayOfBoolean[0] = false;
                  i = 1;
                }
              }
              if (i == 0)
              {
                localObject = LiveConnectSupport.PerAppletInfo.this.invocationDelegates.iterator();
                while (((Iterator)localObject).hasNext())
                {
                  InvocationDelegate localInvocationDelegate = (InvocationDelegate)((Iterator)localObject).next();
                  if (localInvocationDelegate.hasField(paramJavaObjectOpMessage.getMemberName(), localObject1, false, bool1, arrayOfBoolean))
                    break;
                }
              }
              return arrayOfBoolean[0] != 0 ? new Result(Boolean.TRUE, false) : new Result(Boolean.FALSE, false);
            }
          }
          , getContext());
          break;
        case 5:
          localResult = (Result)AccessController.doPrivileged(new PrivilegedExceptionAction()
          {
            private final JavaObjectOpMessage val$msg;
            private final Object val$target;
            private final boolean val$isApplet;

            public Object run()
              throws Exception
            {
              boolean[] arrayOfBoolean = new boolean[1];
              Iterator localIterator = LiveConnectSupport.PerAppletInfo.this.invocationDelegates.iterator();
              while (localIterator.hasNext())
              {
                InvocationDelegate localInvocationDelegate = (InvocationDelegate)localIterator.next();
                if (localInvocationDelegate.hasMethod(paramJavaObjectOpMessage.getMemberName(), localObject1, false, bool1, arrayOfBoolean))
                  break;
              }
              return arrayOfBoolean[0] != 0 ? new Result(Boolean.TRUE, false) : new Result(Boolean.FALSE, false);
            }
          }
          , getContext());
          break;
        case 6:
          localResult = (Result)AccessController.doPrivileged(new PrivilegedExceptionAction()
          {
            private final JavaObjectOpMessage val$msg;
            private final Object val$target;
            private final boolean val$isApplet;

            public Object run()
              throws Exception
            {
              boolean[] arrayOfBoolean = new boolean[1];
              Iterator localIterator = LiveConnectSupport.PerAppletInfo.this.invocationDelegates.iterator();
              while (localIterator.hasNext())
              {
                InvocationDelegate localInvocationDelegate = (InvocationDelegate)localIterator.next();
                if (localInvocationDelegate.hasFieldOrMethod(paramJavaObjectOpMessage.getMemberName(), localObject1, false, bool1, arrayOfBoolean))
                  break;
              }
              return arrayOfBoolean[0] != 0 ? new Result(Boolean.TRUE, false) : new Result(Boolean.FALSE, false);
            }
          }
          , getContext());
          break;
        default:
          throw new RuntimeException("Internal error: unknown Java object operation " + paramJavaObjectOpMessage.getOperationKind());
        }
        Object localObject3 = null;
        boolean bool3 = false;
        if (localResult != null)
        {
          localObject3 = localResult.value();
          bool3 = localResult.skipUnboxing();
        }
        localJavaReplyMessage = new JavaReplyMessage(paramJavaObjectOpMessage.getConversation(), paramJavaObjectOpMessage.getResultID(), LiveConnectSupport.exportObject(localObject3, this.appletID, bool3, false), bool2, null);
        if (LiveConnectSupport.DEBUG)
          System.out.println("LiveConnectSupport: " + LiveConnectSupport.getOpName(paramJavaObjectOpMessage.getOperationKind()) + " \"" + paramJavaObjectOpMessage.getMemberName() + "\": returning result " + localObject3);
      }
      catch (Throwable localThrowable)
      {
        if (LiveConnectSupport.DEBUG)
        {
          System.out.println("Exception occurred during " + LiveConnectSupport.getOpName(paramJavaObjectOpMessage.getOperationKind()) + " " + paramJavaObjectOpMessage.getMemberName() + ":");
          localThrowable.printStackTrace();
        }
        Exception localException;
        if ((localThrowable instanceof PrivilegedActionException))
          localException = ((PrivilegedActionException)localThrowable).getException();
        if ((localException instanceof InvocationTargetException))
        {
          localObject2 = ((InvocationTargetException)localException).getTargetException();
          if ((localObject2 instanceof Exception))
            localException = (Exception)localObject2;
        }
        Object localObject2 = localException.toString();
        localJavaReplyMessage = new JavaReplyMessage(paramJavaObjectOpMessage.getConversation(), paramJavaObjectOpMessage.getResultID(), null, false, (String)localObject2);
      }
      LiveConnectSupport.pipe.send(localJavaReplyMessage);
    }

    private boolean isLiveconnectCallAllowed(JavaObjectOpMessage paramJavaObjectOpMessage)
    {
      Plugin2Manager localPlugin2Manager = this.manager;
      boolean bool1 = false;
      if (localPlugin2Manager != null)
      {
        boolean bool2 = localPlugin2Manager.isVMSecure();
        if (!bool2)
        {
          Boolean localBoolean = null;
          if (this.liveconnectChecked)
          {
            localBoolean = new Boolean(this.liveconnectPermissionGranted);
          }
          else
          {
            String str = localPlugin2Manager.getAppletUniqueKey();
            localBoolean = (Boolean)appletLiveconnectAllowedMap.get(str);
            if (localBoolean == null)
            {
              localBoolean = new Boolean(getUserPermissionForLiveconnectCall(paramJavaObjectOpMessage));
              appletLiveconnectAllowedMap.put(str, localBoolean);
              this.liveconnectChecked = true;
              this.liveconnectPermissionGranted = localBoolean.booleanValue();
            }
          }
          bool1 = localBoolean.booleanValue();
        }
        else
        {
          bool1 = true;
        }
        int i = paramJavaObjectOpMessage.getOperationKind();
        if ((!paramJavaObjectOpMessage.getObject().isApplet()) || (i == 1) || (i == 2) || (i == 3))
          try
          {
            localPlugin2Manager.checkUntrustedAccess();
          }
          catch (SecurityException localSecurityException)
          {
            this.liveconnectPermissionGranted = false;
            bool1 = false;
          }
      }
      return bool1;
    }

    private boolean getUserPermissionForLiveconnectCall(JavaObjectOpMessage paramJavaObjectOpMessage)
    {
      String str1 = ResourceManager.getString("deployment.ssv.title");
      String str2 = ResourceManager.getString("liveconnect.insecurejvm.warning");
      String str3 = ResourceManager.getString("common.continue_btn");
      String str4 = ResourceManager.getString("common.cancel_btn");
      URL localURL = getDocumentBase();
      String str5 = this.manager.getName();
      AppInfo localAppInfo = new AppInfo();
      localAppInfo.setTitle(str5);
      localAppInfo.setFrom(localURL);
      ToolkitStore.getUI();
      int i = -1;
      ToolkitStore.getUI();
      i = ToolkitStore.getUI().showMessageDialog(null, localAppInfo, 3, str1, null, str2, null, str3, str4, null);
      ToolkitStore.getUI();
      return i == 0;
    }

    private AccessControlContext getContext()
    {
      if (this.context == null)
        this.context = LiveConnectSupport.createContext(getDocumentBase());
      return this.context;
    }

    private URL getDocumentBase()
    {
      if (!this.fetchedDocumentBase)
      {
        this.documentBase = this.manager.getDocumentBase();
        this.fetchedDocumentBase = true;
      }
      return this.documentBase;
    }

    private InvocationDelegate getDelegate(Object paramObject, boolean paramBoolean)
    {
      if (paramBoolean)
        return getJavaClass((Class)paramObject);
      return getJavaClass(paramObject.getClass());
    }

    private synchronized JavaClass getJavaClass(Class paramClass)
    {
      JavaClass localJavaClass = (JavaClass)this.classes.get(paramClass);
      if (localJavaClass == null)
      {
        localJavaClass = new JavaClass(paramClass, getBridge());
        this.classes.put(paramClass, localJavaClass);
      }
      return localJavaClass;
    }

    private void waitForAppletStartOrError()
      throws IOException
    {
      if (this.manager.isForDummyApplet())
        return;
      if ((this.manager.getApplet2Adapter().isInstantiated()) || (this.manager.hasErrorOccurred()))
      {
        this.manager.waitUntilAppletStartDone();
        return;
      }
      throw new IOException("LiveConnect operation without existing applet");
    }

    private static IllegalArgumentException inconvertible(Object paramObject1, Object paramObject2)
    {
      return new IllegalArgumentException("Object " + paramObject1 + " can not be converted to " + paramObject2);
    }

    private static IllegalArgumentException inconvertible(Class paramClass1, Class paramClass2)
    {
      return inconvertible(paramClass1, paramClass2, null);
    }

    private static IllegalArgumentException inconvertible(Class paramClass1, Class paramClass2, Exception paramException)
    {
      IllegalArgumentException localIllegalArgumentException = new IllegalArgumentException("Class " + paramClass1.getName() + " can not be converted to " + paramClass2.getName());
      if (paramException != null)
        localIllegalArgumentException.initCause(paramException);
      return localIllegalArgumentException;
    }

    class DefaultConversionDelegate
      implements ConversionDelegate
    {
      private Class jsObjectClass = JSObject.class;
      private static final int TOSTRING_CONVERSION_PENALTY = 50;
      private static final int JSOBJECT_CONVERSION_PENALTY = 2500;

      DefaultConversionDelegate()
      {
      }

      public int conversionCost(Object paramObject1, Object paramObject2)
      {
        if (!(paramObject2 instanceof Class))
          return -1;
        Class localClass1 = (Class)paramObject2;
        if (paramObject1 == null)
        {
          if (localClass1.isPrimitive())
            return -1;
          return 0;
        }
        Class localClass2 = paramObject1.getClass();
        if ((localClass2 == localClass1) || ((localClass1 == this.jsObjectClass) && (localClass1.isAssignableFrom(localClass2))))
          return 0;
        if (localClass1.isAssignableFrom(localClass2))
          return conversionDistance(localClass2, localClass1);
        if ((this.jsObjectClass.isAssignableFrom(localClass2)) && (canConvert((JSObject)paramObject1, localClass1)))
          return 2500;
        if ((this.jsObjectClass.isAssignableFrom(localClass2)) || (this.jsObjectClass.isAssignableFrom(localClass1)))
          return -1;
        if (localClass1 == String.class)
          return 50;
        if (localClass1.isPrimitive())
          localClass1 = getBoxingClass(localClass1);
        if ((Number.class.isAssignableFrom(localClass1)) || (localClass1 == Character.class) || (localClass1 == Boolean.class))
        {
          if (localClass1 == localClass2)
            return 0;
          if ((localClass2 == String.class) || (Number.class.isAssignableFrom(localClass2)) || (localClass2 == Character.class) || (localClass2 == Boolean.class))
            return 1;
        }
        return -1;
      }

      public boolean convert(Object paramObject1, Object paramObject2, Object[] paramArrayOfObject)
        throws Exception
      {
        if (paramObject1 == null)
          return true;
        if (!(paramObject2 instanceof Class))
          throw LiveConnectSupport.PerAppletInfo.inconvertible(paramObject1, paramObject2);
        Class localClass1 = (Class)paramObject2;
        Class localClass2 = paramObject1.getClass();
        if (localClass1.isAssignableFrom(localClass2))
        {
          paramArrayOfObject[0] = paramObject1;
          return true;
        }
        Object localObject1;
        if (localClass1 == String.class)
        {
          if ((paramObject1 instanceof Number))
          {
            localObject1 = NumberFormat.getNumberInstance();
            try
            {
              paramArrayOfObject[0] = ((NumberFormat)localObject1).parse(paramObject1.toString()).toString();
              return true;
            }
            catch (ParseException localParseException)
            {
            }
          }
          paramArrayOfObject[0] = paramObject1.toString();
          return true;
        }
        if ((this.jsObjectClass.isAssignableFrom(localClass2)) && (localClass1.isArray()))
          try
          {
            localObject1 = (JSObject)paramObject1;
            Class localClass3 = localClass1.getComponentType();
            int i = ((Number)((JSObject)localObject1).getMember("length")).intValue();
            Object localObject2 = Array.newInstance(localClass3, i);
            Object[] arrayOfObject = new Object[1];
            for (int j = 0; j < i; j++)
            {
              Object localObject3 = null;
              try
              {
                localObject3 = ((JSObject)localObject1).getSlot(j);
              }
              catch (JSException localJSException)
              {
              }
              if (localObject3 != null)
              {
                convert(localObject3, localClass3, arrayOfObject);
                Array.set(localObject2, j, arrayOfObject[0]);
              }
            }
            paramArrayOfObject[0] = localObject2;
            return true;
          }
          catch (Exception localException)
          {
            throw LiveConnectSupport.PerAppletInfo.inconvertible(localClass2, localClass1, localException);
          }
        if ((this.jsObjectClass.isAssignableFrom(localClass2)) || (this.jsObjectClass.isAssignableFrom(localClass1)))
          throw LiveConnectSupport.PerAppletInfo.inconvertible(localClass2, localClass1);
        if ((localClass1.isPrimitive()) || (Number.class.isAssignableFrom(localClass1)) || (localClass1 == Character.class) || (localClass1 == Boolean.class))
        {
          boolean bool = paramObject1 instanceof Number;
          if ((!bool) && (!(paramObject1 instanceof String)) && (!(paramObject1 instanceof Character)) && (!(paramObject1 instanceof Boolean)))
            throw LiveConnectSupport.PerAppletInfo.inconvertible(localClass2, localClass1);
          if ((localClass1 == Boolean.TYPE) || (localClass1 == Boolean.class))
          {
            if (localClass2 == Boolean.class)
            {
              paramArrayOfObject[0] = paramObject1;
              return true;
            }
            if (bool)
            {
              double d = ((Number)paramObject1).doubleValue();
              if ((Double.isNaN(d)) || (d == 0.0D))
                paramArrayOfObject[0] = Boolean.FALSE;
              else
                paramArrayOfObject[0] = Boolean.TRUE;
              return true;
            }
            if (((String)paramObject1).length() == 0)
              paramArrayOfObject[0] = Boolean.FALSE;
            else
              paramArrayOfObject[0] = Boolean.TRUE;
            return true;
          }
          if ((localClass1 == Byte.TYPE) || (localClass1 == Byte.class))
          {
            if (localClass2 == Byte.class)
            {
              paramArrayOfObject[0] = paramObject1;
              return true;
            }
            if (bool)
              paramArrayOfObject[0] = new Byte(((Number)paramObject1).byteValue());
            else
              paramArrayOfObject[0] = Byte.valueOf((String)paramObject1);
            return true;
          }
          if ((localClass1 == Short.TYPE) || (localClass1 == Short.class))
          {
            if ((localClass2 == Short.class) || (localClass2 == Byte.class))
            {
              paramArrayOfObject[0] = paramObject1;
              return true;
            }
            if (bool)
              paramArrayOfObject[0] = new Short(((Number)paramObject1).shortValue());
            else
              paramArrayOfObject[0] = Short.valueOf((String)paramObject1);
            return true;
          }
          if ((localClass1 == Integer.TYPE) || (localClass1 == Integer.class))
          {
            if ((localClass2 == Integer.class) || (localClass2 == Character.class) || (localClass2 == Short.class) || (localClass2 == Byte.class))
            {
              paramArrayOfObject[0] = paramObject1;
              return true;
            }
            if (bool)
              paramArrayOfObject[0] = new Integer(((Number)paramObject1).intValue());
            else
              paramArrayOfObject[0] = Integer.valueOf((String)paramObject1);
            return true;
          }
          if ((localClass1 == Long.TYPE) || (localClass1 == Long.class))
          {
            if ((localClass2 == Long.class) || (localClass2 == Integer.class) || (localClass2 == Character.class) || (localClass2 == Short.class) || (localClass2 == Byte.class))
            {
              paramArrayOfObject[0] = paramObject1;
              return true;
            }
            if (bool)
              paramArrayOfObject[0] = new Long(((Number)paramObject1).longValue());
            else
              paramArrayOfObject[0] = Long.valueOf((String)paramObject1);
            return true;
          }
          if ((localClass1 == Float.TYPE) || (localClass1 == Float.class))
          {
            if ((localClass2 == Float.class) || (localClass2 == Long.class) || (localClass2 == Integer.class) || (localClass2 == Character.class) || (localClass2 == Short.class) || (localClass2 == Byte.class))
            {
              paramArrayOfObject[0] = paramObject1;
              return true;
            }
            if (bool)
              paramArrayOfObject[0] = new Float(((Number)paramObject1).floatValue());
            else
              paramArrayOfObject[0] = Float.valueOf((String)paramObject1);
            return true;
          }
          if ((localClass1 == Double.TYPE) || (localClass1 == Double.class))
          {
            if ((localClass2 == Double.class) || (localClass2 == Float.class) || (localClass2 == Long.class) || (localClass2 == Integer.class) || (localClass2 == Character.class) || (localClass2 == Short.class) || (localClass2 == Byte.class))
            {
              paramArrayOfObject[0] = paramObject1;
              return true;
            }
            if (bool)
              paramArrayOfObject[0] = new Double(((Number)paramObject1).doubleValue());
            else
              paramArrayOfObject[0] = Double.valueOf((String)paramObject1);
            return true;
          }
          if ((localClass1 == Character.TYPE) || (localClass1 == Character.class))
          {
            if (bool)
              paramArrayOfObject[0] = new Character((char)((Number)paramObject1).shortValue());
            else
              paramArrayOfObject[0] = new Character((char)Short.decode((String)paramObject1).shortValue());
            return true;
          }
        }
        throw LiveConnectSupport.PerAppletInfo.inconvertible(localClass2, localClass1);
      }

      private boolean canConvert(JSObject paramJSObject, Class paramClass)
      {
        if (paramClass == String.class)
          return true;
        if (paramClass.isArray())
          try
          {
            paramJSObject.getMember("length");
            return true;
          }
          catch (JSException localJSException)
          {
          }
        return false;
      }

      private int conversionDistance(Class paramClass1, Class paramClass2)
      {
        if ((paramClass2.isInterface()) || (paramClass2.isArray()))
          return 1;
        int i = 0;
        while ((paramClass1 != null) && (paramClass1 != paramClass2))
        {
          i++;
          paramClass1 = paramClass1.getSuperclass();
        }
        if (paramClass1 != paramClass2)
          return 1;
        return i;
      }

      private Class getBoxingClass(Class paramClass)
      {
        if (paramClass == Boolean.TYPE)
          return Boolean.class;
        if (paramClass == Byte.TYPE)
          return Byte.class;
        if (paramClass == Short.TYPE)
          return Short.class;
        if (paramClass == Character.TYPE)
          return Character.class;
        if (paramClass == Integer.TYPE)
          return Integer.class;
        if (paramClass == Long.TYPE)
          return Long.class;
        if (paramClass == Float.TYPE)
          return Float.class;
        if (paramClass == Double.TYPE)
          return Double.class;
        throw new IllegalArgumentException("Not a primitive type class");
      }
    }

    class DefaultInvocationDelegate
      implements InvocationDelegate
    {
      DefaultInvocationDelegate()
      {
      }

      public boolean invoke(String paramString, Object paramObject, Object[] paramArrayOfObject, boolean paramBoolean1, boolean paramBoolean2, Result[] paramArrayOfResult)
        throws Exception
      {
        InvocationDelegate localInvocationDelegate = LiveConnectSupport.PerAppletInfo.this.getDelegate(paramObject, paramBoolean1);
        return localInvocationDelegate.invoke(paramString, paramBoolean1 ? null : paramObject, paramArrayOfObject, false, paramBoolean2, paramArrayOfResult);
      }

      public boolean getField(String paramString, Object paramObject, boolean paramBoolean1, boolean paramBoolean2, Result[] paramArrayOfResult)
        throws Exception
      {
        InvocationDelegate localInvocationDelegate = LiveConnectSupport.PerAppletInfo.this.getDelegate(paramObject, paramBoolean1);
        return localInvocationDelegate.getField(paramString, paramBoolean1 ? null : paramObject, false, paramBoolean2, paramArrayOfResult);
      }

      public boolean setField(String paramString, Object paramObject1, Object paramObject2, boolean paramBoolean1, boolean paramBoolean2)
        throws Exception
      {
        InvocationDelegate localInvocationDelegate = LiveConnectSupport.PerAppletInfo.this.getDelegate(paramObject1, paramBoolean1);
        return localInvocationDelegate.setField(paramString, paramBoolean1 ? null : paramObject1, paramObject2, false, paramBoolean2);
      }

      public boolean hasField(String paramString, Object paramObject, boolean paramBoolean1, boolean paramBoolean2, boolean[] paramArrayOfBoolean)
      {
        InvocationDelegate localInvocationDelegate = LiveConnectSupport.PerAppletInfo.this.getDelegate(paramObject, paramBoolean1);
        return localInvocationDelegate.hasField(paramString, paramBoolean1 ? null : paramObject, false, paramBoolean2, paramArrayOfBoolean);
      }

      public boolean hasMethod(String paramString, Object paramObject, boolean paramBoolean1, boolean paramBoolean2, boolean[] paramArrayOfBoolean)
      {
        InvocationDelegate localInvocationDelegate = LiveConnectSupport.PerAppletInfo.this.getDelegate(paramObject, paramBoolean1);
        return localInvocationDelegate.hasMethod(paramString, paramBoolean1 ? null : paramObject, false, paramBoolean2, paramArrayOfBoolean);
      }

      public boolean hasFieldOrMethod(String paramString, Object paramObject, boolean paramBoolean1, boolean paramBoolean2, boolean[] paramArrayOfBoolean)
      {
        InvocationDelegate localInvocationDelegate = LiveConnectSupport.PerAppletInfo.this.getDelegate(paramObject, paramBoolean1);
        return localInvocationDelegate.hasFieldOrMethod(paramString, paramBoolean1 ? null : paramObject, false, paramBoolean2, paramArrayOfBoolean);
      }

      public Object findClass(String paramString)
      {
        try
        {
          ClassLoader localClassLoader = Thread.currentThread().getContextClassLoader();
          return Class.forName(paramString, false, localClassLoader);
        }
        catch (ClassFormatError localClassFormatError)
        {
          return null;
        }
        catch (ClassNotFoundException localClassNotFoundException)
        {
          return null;
        }
        catch (RuntimeException localRuntimeException)
        {
          throw localRuntimeException;
        }
      }

      public Object newInstance(Object paramObject, Object[] paramArrayOfObject)
        throws Exception
      {
        JavaClass localJavaClass = LiveConnectSupport.PerAppletInfo.this.getJavaClass((Class)paramObject);
        return localJavaClass.newInstance(null, paramArrayOfObject);
      }
    }

    private class LiveConnectWorker
      implements Runnable
    {
      private volatile boolean shouldStop;
      private Object lock = new Object();
      private LinkedList workQueue = new LinkedList();

      private LiveConnectWorker()
      {
      }

      public void enqueue(JavaObjectOpMessage paramJavaObjectOpMessage)
      {
        synchronized (this.lock)
        {
          this.workQueue.add(paramJavaObjectOpMessage);
          this.lock.notifyAll();
        }
      }

      public void stop()
      {
        this.shouldStop = true;
        synchronized (this.lock)
        {
          this.lock.notifyAll();
        }
      }

      public void run()
      {
        Plugin2Manager.setCurrentManagerThreadLocal(LiveConnectSupport.PerAppletInfo.this.manager);
        try
        {
          if (!this.shouldStop)
          {
            synchronized (this.lock)
            {
              while ((!this.shouldStop) && (this.workQueue.isEmpty()))
                try
                {
                  this.lock.wait();
                }
                catch (InterruptedException localInterruptedException)
                {
                  localInterruptedException.printStackTrace();
                }
            }
            while ((!this.shouldStop) && (!this.workQueue.isEmpty()))
            {
              ??? = null;
              synchronized (this.lock)
              {
                ??? = (JavaObjectOpMessage)this.workQueue.removeFirst();
              }
              LiveConnectSupport.PerAppletInfo.this.doObjectOp((JavaObjectOpMessage)???);
            }
          }
        }
        catch (IOException localIOException)
        {
          localIOException.printStackTrace();
        }
      }

      LiveConnectWorker(LiveConnectSupport.1 arg2)
      {
        this();
      }
    }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.main.client.LiveConnectSupport
 * JD-Core Version:    0.6.2
 */