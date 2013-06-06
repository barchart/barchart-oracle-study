package sun.plugin2.main.server;

import com.sun.applet2.AppletParameters;
import com.sun.deploy.config.AutoUpdater;
import com.sun.deploy.config.Config;
import com.sun.deploy.config.JREInfo;
import com.sun.deploy.config.JfxRuntime;
import com.sun.deploy.config.Platform;
import com.sun.deploy.config.PluginServerConfig;
import com.sun.deploy.util.JVMParameters;
import com.sun.deploy.util.SecurityBaseline;
import com.sun.deploy.util.SystemUtils;
import com.sun.deploy.util.VersionID;
import com.sun.deploy.util.VersionString;
import com.sun.javaws.jnl.DefaultMatchJRE;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import sun.plugin2.liveconnect.RemoteJavaObject;
import sun.plugin2.message.AppletMessage;
import sun.plugin2.message.Conversation;
import sun.plugin2.util.SystemUtil;

public class JVMManager
{
  static boolean DEBUG = SystemUtil.isDebug();
  static boolean VERBOSE = SystemUtil.isVerbose();
  private static final String JPI_USER_PROFILE = "javaplugin.user.profile";
  private static JVMManager soleInstance;
  private static final int RETRY_COUNT = 2;
  private static int browserType;
  private String userJPIProfile = SystemUtil.getenv("USER_JPI_PROFILE");
  private Map activeJVMs = new HashMap();
  private int curJVMID;
  private final List javaPlatformList = new ArrayList();
  private Map javaParamMap = new IdentityHashMap();
  private int curAppletID;
  private Map appletToJVMMap = new HashMap();
  private Map appletMessageQueue = new HashMap();

  private JVMManager()
  {
    if (this.userJPIProfile != null)
      System.setProperty("javaplugin.user.profile", this.userJPIProfile);
    Config.setInstance(new PluginServerConfig());
    processJREInfo();
    Platform.get().getAutoUpdater().checkForUpdate(null);
  }

  public static void setBrowserType(int paramInt)
  {
    browserType = paramInt;
  }

  public static int getBrowserType()
  {
    return browserType;
  }

  public static synchronized JVMManager getManager()
  {
    if (soleInstance == null)
      soleInstance = new JVMManager();
    return soleInstance;
  }

  public boolean instanceExited(int paramInt)
  {
    return getJVMInstance(paramInt) == null;
  }

  public boolean appletExited(AppletID paramAppletID)
  {
    Iterator localIterator = this.activeJVMs.values().iterator();
    while (localIterator.hasNext())
    {
      JVMInstance localJVMInstance = (JVMInstance)localIterator.next();
      if (localJVMInstance.appletRunning(paramAppletID))
        return false;
    }
    return true;
  }

  public AppletID startApplet(AppletParameters paramAppletParameters, Plugin paramPlugin, long paramLong, String paramString, boolean paramBoolean)
  {
    return startApplet(paramAppletParameters, paramPlugin, paramLong, paramString, paramBoolean, (String)paramAppletParameters.get("java_version"));
  }

  public AppletID startApplet(AppletParameters paramAppletParameters, Plugin paramPlugin, long paramLong, String paramString1, boolean paramBoolean, String paramString2)
  {
    return startAppletImpl(SystemUtils.microTime(), paramAppletParameters, paramPlugin, paramLong, paramString1, paramBoolean, paramString2, false, nextAppletID(), false);
  }

  public AppletID startDummyApplet(AppletParameters paramAppletParameters, Plugin paramPlugin)
  {
    return startAppletImpl(SystemUtils.microTime(), paramAppletParameters, paramPlugin, 0L, null, false, (String)paramAppletParameters.get("java_version"), true, nextAppletID(), false);
  }

  public AppletID relaunchApplet(long paramLong1, AppletParameters paramAppletParameters, Plugin paramPlugin, long paramLong2, String paramString1, boolean paramBoolean1, String paramString2, int paramInt, boolean paramBoolean2)
  {
    if (paramBoolean2)
      processJREInfo();
    return startAppletImpl(paramLong1, paramAppletParameters, paramPlugin, paramLong2, paramString1, paramBoolean1, paramString2, false, paramInt, true);
  }

  private void maintainCurrentArchFlag(JVMParameters paramJVMParameters, JREInfo paramJREInfo)
  {
    if ((SystemUtil.getOSType() == 3) && (!paramJVMParameters.contains("-d32")) && (!paramJVMParameters.contains("-d64")))
      if (("x86_64".equals(paramJREInfo.getOSArch())) || ("amd64".equals(paramJREInfo.getOSArch())))
        paramJVMParameters.addInternalArgument("-d64");
      else
        paramJVMParameters.addInternalArgument("-d32");
  }

  VersionString getVersionStringToRunAndSetSsvVersion(String paramString, AppletParameters paramAppletParameters)
  {
    VersionString localVersionString = new VersionString(paramString);
    String str = noDash(getBestJREInfo(localVersionString).getProduct());
    if (localVersionString.contains(str))
    {
      if (SecurityBaseline.satisfiesSecurityBaseline(str))
        return new VersionString(str);
      paramAppletParameters.put("__applet_ssv_version", str);
    }
    else
    {
      paramAppletParameters.put("__applet_request_version", paramString);
    }
    return null;
  }

  private AppletID startAppletImpl(long paramLong1, AppletParameters paramAppletParameters, Plugin paramPlugin, long paramLong2, String paramString1, boolean paramBoolean1, String paramString2, boolean paramBoolean2, int paramInt, boolean paramBoolean3)
  {
    VersionString localVersionString = null;
    if (paramString2 != null)
      if (paramBoolean3)
        localVersionString = new VersionString(paramString2);
      else
        localVersionString = getVersionStringToRunAndSetSsvVersion(paramString2, paramAppletParameters);
    JVMParameters localJVMParameters = SystemUtil.extractAppletParamsToJVMParameters(paramAppletParameters, paramPlugin.getDocumentBase(), paramBoolean3);
    int i = 0;
    ClientJVMSelectionParameters localClientJVMSelectionParameters = ClientJVMSelectionParameters.extract(paramAppletParameters);
    AppletID localAppletID = new AppletID(paramInt);
    do
    {
      JVMInstance localJVMInstance = getOrCreateBestJVMInstance(paramLong1, paramBoolean3 ? localVersionString : null, localJVMParameters, localClientJVMSelectionParameters, paramBoolean3 ? localAppletID : null);
      if (null == localJVMInstance)
      {
        paramPlugin.startupStatus(3);
        return null;
      }
      if ((localVersionString != null) && (paramAppletParameters.get("__applet_ssv_version") == null) && (paramAppletParameters.get("__applet_request_version") == null))
      {
        Object localObject1 = null;
        Iterator localIterator = this.javaPlatformList.iterator();
        while (localIterator.hasNext())
        {
          JREInfo localJREInfo = (JREInfo)localIterator.next();
          if ((localVersionString.contains(localJREInfo.getProductVersion())) && ((localObject1 == null) || (localJREInfo.getProductVersion().isGreaterThan(localObject1.getProductVersion()))))
            localObject1 = localJREInfo;
        }
        if (localObject1 != null)
          paramAppletParameters.put("__applet_ssv_version", localObject1.getProductVersion().toString());
      }
      if ((!localJVMInstance.exited()) && (localJVMInstance.startApplet(paramAppletParameters, paramPlugin, paramLong2, paramString1, paramBoolean1, paramInt, paramBoolean2, paramBoolean3)))
      {
        this.appletToJVMMap.put(localAppletID, localJVMInstance);
        synchronized (this.appletMessageQueue)
        {
          if (null == this.appletMessageQueue.get(localAppletID))
            this.appletMessageQueue.put(localAppletID, new ArrayList());
        }
        if (DEBUG)
          System.out.println("JVMManager: applet launch (ID " + localAppletID + ") succeeded");
        return localAppletID;
      }
      if ((DEBUG) && (localJVMInstance.errorOccurred()))
        System.out.println("Error occurred during launch of JVM");
      i++;
    }
    while (i < 2);
    return null;
  }

  public void setAppletSize(AppletID paramAppletID, int paramInt1, int paramInt2)
  {
    JVMInstance localJVMInstance = getJVMInstance(paramAppletID);
    if (localJVMInstance == null)
      return;
    int[] arrayOfInt = { paramInt1, paramInt2 };
    localJVMInstance.setAppletSize(paramAppletID.getID(), paramInt1, paramInt2);
  }

  public void sendStopApplet(AppletID paramAppletID)
  {
    JVMInstance localJVMInstance = getJVMInstance(paramAppletID);
    if (localJVMInstance == null)
      return;
    localJVMInstance.sendStopApplet(paramAppletID.getID());
  }

  public boolean receivedStopAcknowledgment(AppletID paramAppletID)
  {
    JVMInstance localJVMInstance = getJVMInstance(paramAppletID);
    if (localJVMInstance == null)
      return false;
    return localJVMInstance.receivedStopAcknowledgment(paramAppletID.getID());
  }

  public void recycleAppletID(AppletID paramAppletID)
  {
    JVMInstance localJVMInstance = removeJVMInstance(paramAppletID);
    if (localJVMInstance != null)
      localJVMInstance.recycleAppletID(paramAppletID.getID());
    removeAppletMessageQueue(paramAppletID);
  }

  public void sendGetApplet(AppletID paramAppletID, int paramInt)
    throws IOException
  {
    JVMInstance localJVMInstance = getJVMInstance(paramAppletID);
    if (localJVMInstance == null)
      throw new IOException("No active JVM instance for applet ID " + paramAppletID);
    localJVMInstance.sendGetApplet(paramAppletID.getID(), paramInt);
  }

  public void sendGetNameSpace(AppletID paramAppletID, String paramString, int paramInt)
    throws IOException
  {
    JVMInstance localJVMInstance = getJVMInstance(paramAppletID);
    if (localJVMInstance == null)
      throw new IOException("No active JVM instance for applet ID " + paramAppletID);
    localJVMInstance.sendGetNameSpace(paramAppletID.getID(), paramString, paramInt);
  }

  public void sendRemoteJavaObjectOp(Conversation paramConversation, RemoteJavaObject paramRemoteJavaObject, String paramString, int paramInt1, Object[] paramArrayOfObject, int paramInt2)
    throws IOException
  {
    JVMInstance localJVMInstance = getJVMInstance(new AppletID(paramRemoteJavaObject.getAppletID()));
    if (localJVMInstance == null)
      throw new IOException("No active JVM instance for applet ID " + paramRemoteJavaObject.getAppletID() + ", JVM ID " + paramRemoteJavaObject.getJVMID());
    localJVMInstance.sendRemoteJavaObjectOp(paramConversation, paramRemoteJavaObject, paramString, paramInt1, paramArrayOfObject, paramInt2);
  }

  public void releaseRemoteJavaObject(RemoteJavaObject paramRemoteJavaObject)
  {
    JVMInstance localJVMInstance = getJVMInstance(paramRemoteJavaObject.getJVMID());
    if (localJVMInstance == null)
      return;
    localJVMInstance.releaseRemoteJavaObject(paramRemoteJavaObject.getObjectID());
  }

  public void sendWindowActivation(AppletID paramAppletID, boolean paramBoolean)
  {
    JVMInstance localJVMInstance = getJVMInstance(paramAppletID);
    if (localJVMInstance == null)
      return;
    localJVMInstance.synthesizeWindowActivation(paramAppletID.getID(), paramBoolean);
  }

  public void sendGotFocus(AppletID paramAppletID, boolean paramBoolean)
  {
    JVMInstance localJVMInstance = getJVMInstance(paramAppletID);
    if (localJVMInstance == null)
      return;
    localJVMInstance.sendGotFocus(paramAppletID.getID(), paramBoolean);
  }

  public void sendOverlayWindowMove(AppletID paramAppletID, double paramDouble1, double paramDouble2)
  {
    JVMInstance localJVMInstance = getJVMInstance(paramAppletID);
    if (localJVMInstance == null)
      return;
    localJVMInstance.sendOverlayWindowMove(paramAppletID.getID(), paramDouble1, paramDouble2);
  }

  public void sendMouseEvent(AppletID paramAppletID, int paramInt1, int paramInt2, double paramDouble1, double paramDouble2, int paramInt3, int paramInt4)
  {
    JVMInstance localJVMInstance = getJVMInstance(paramAppletID);
    if (localJVMInstance == null)
      return;
    localJVMInstance.sendMouseEvent(paramAppletID.getID(), paramInt1, paramInt2, paramDouble1, paramDouble2, paramInt3, paramInt4);
  }

  public void sendKeyEvent(AppletID paramAppletID, int paramInt1, int paramInt2, String paramString1, String paramString2, boolean paramBoolean1, int paramInt3, boolean paramBoolean2)
  {
    JVMInstance localJVMInstance = getJVMInstance(paramAppletID);
    if (localJVMInstance == null)
      return;
    localJVMInstance.sendKeyEvent(paramAppletID.getID(), paramInt1, paramInt2, paramString1, paramString2, paramBoolean1, paramInt3, paramBoolean2);
  }

  public void sendScrollEvent(AppletID paramAppletID, double paramDouble1, double paramDouble2, int paramInt, double paramDouble3, double paramDouble4, double paramDouble5)
  {
    JVMInstance localJVMInstance = getJVMInstance(paramAppletID);
    if (localJVMInstance == null)
      return;
    localJVMInstance.sendScrollEvent(paramAppletID.getID(), paramDouble1, paramDouble2, paramInt, paramDouble3, paramDouble4, paramDouble5);
  }

  public void sendTextEvent(AppletID paramAppletID, String paramString)
  {
    JVMInstance localJVMInstance = getJVMInstance(paramAppletID);
    if (localJVMInstance == null)
      return;
    localJVMInstance.sendTextEvent(paramAppletID.getID(), paramString);
  }

  public boolean printApplet(AppletID paramAppletID, long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    JVMInstance localJVMInstance = getJVMInstance(paramAppletID);
    if (localJVMInstance == null)
      return false;
    return localJVMInstance.printApplet(paramAppletID.getID(), paramLong, paramInt1, paramInt2, paramInt3, paramInt4);
  }

  public boolean isMoreRecentJVMAvailable(JREInfo paramJREInfo)
  {
    Iterator localIterator = this.javaPlatformList.iterator();
    while (localIterator.hasNext())
    {
      JREInfo localJREInfo = (JREInfo)localIterator.next();
      if (DEBUG)
        System.out.println("isMoreRecentJVMAvailable considering " + localJREInfo.getProductVersion() + " JVM for relaunch");
      if (localJREInfo.getProductVersion().isGreaterThan(paramJREInfo.getProductVersion()))
      {
        if (DEBUG)
          System.out.println("  isMoreRecentJVMAvailable (chosen)");
        return true;
      }
    }
    return false;
  }

  protected boolean spoolAppletMessage(AppletMessage paramAppletMessage)
  {
    synchronized (this.appletMessageQueue)
    {
      List localList = (List)this.appletMessageQueue.get(new AppletID(paramAppletMessage.getAppletID()));
      if (localList != null)
      {
        if ((DEBUG) && (VERBOSE))
          System.out.println("Spool AppletMessage: " + paramAppletMessage);
        localList.add(paramAppletMessage);
        return true;
      }
    }
    return false;
  }

  protected void drainAppletMessages(AppletID paramAppletID)
  {
    JVMInstance localJVMInstance = getJVMInstance(paramAppletID);
    if ((localJVMInstance == null) && (DEBUG))
    {
      System.out.println("JVMManager.drainAppletMessages: no JVM instance for applet ID " + paramAppletID);
      return;
    }
    synchronized (this.appletMessageQueue)
    {
      List localList = (List)this.appletMessageQueue.remove(paramAppletID);
      if (localList != null)
      {
        Iterator localIterator = localList.iterator();
        while (localIterator.hasNext())
          try
          {
            AppletMessage localAppletMessage = (AppletMessage)localIterator.next();
            if ((DEBUG) && (VERBOSE))
              System.out.println("Drain AppletMessage: " + localAppletMessage);
            localJVMInstance.sendMessageDirect(localAppletMessage);
          }
          catch (IOException localIOException)
          {
            localIOException.printStackTrace();
          }
      }
    }
  }

  private void removeAppletMessageQueue(AppletID paramAppletID)
  {
    synchronized (this.appletMessageQueue)
    {
      this.appletMessageQueue.remove(paramAppletID);
    }
  }

  protected synchronized int getJVMIDForApplet(AppletID paramAppletID)
  {
    JVMInstance localJVMInstance = (JVMInstance)this.appletToJVMMap.get(paramAppletID);
    return null != localJVMInstance ? localJVMInstance.getID() : -1;
  }

  synchronized JVMInstance getJVMInstance(AppletID paramAppletID)
  {
    return (JVMInstance)this.appletToJVMMap.get(paramAppletID);
  }

  private synchronized JVMInstance getJVMInstance(int paramInt)
  {
    return (JVMInstance)this.activeJVMs.get(new Integer(paramInt));
  }

  private synchronized JVMInstance removeJVMInstance(AppletID paramAppletID)
  {
    return (JVMInstance)this.appletToJVMMap.remove(paramAppletID);
  }

  private synchronized int nextJVMID()
  {
    while (true)
    {
      int i = ++this.curJVMID;
      if (this.activeJVMs.get(new Integer(i)) == null)
        return i;
    }
  }

  private synchronized void processJREInfo()
  {
    Config.get().refreshIfNeeded();
    this.javaPlatformList.clear();
    if ((DEBUG) && (VERBOSE))
    {
      System.out.println("JREInfos (1)");
      JREInfo.printJREs();
    }
    Vector localVector = Platform.get().getInstalledJREList();
    if (localVector != null)
      Config.get().storeInstalledJREs(localVector);
    if ((DEBUG) && (VERBOSE))
    {
      System.out.println("JREInfos (2)");
      JREInfo.printJREs();
    }
    this.javaPlatformList.addAll(Arrays.asList(JREInfo.getAll()));
    filterJavaPlatformList(this.javaPlatformList);
    Collections.sort(this.javaPlatformList, new Comparator()
    {
      public int compare(Object paramAnonymousObject1, Object paramAnonymousObject2)
      {
        VersionID localVersionID1 = ((JREInfo)paramAnonymousObject1).getProductVersion();
        VersionID localVersionID2 = ((JREInfo)paramAnonymousObject2).getProductVersion();
        return -localVersionID1.compareTo(localVersionID2);
      }

      public boolean equals(Object paramAnonymousObject)
      {
        return false;
      }
    });
    filterDisabledJREs(this.javaPlatformList);
    updateJavaParamMap();
  }

  private void updateJavaParamMap()
  {
    Iterator localIterator = this.javaPlatformList.iterator();
    while (localIterator.hasNext())
    {
      JREInfo localJREInfo = (JREInfo)localIterator.next();
      JVMParameters localJVMParameters = SystemUtil.getDefaultVmArgs(localJREInfo);
      this.javaParamMap.put(localJREInfo, localJVMParameters);
    }
  }

  JREInfo getBestJREInfo(VersionString paramVersionString)
  {
    return getBestJREInfo(paramVersionString, null);
  }

  synchronized JREInfo getBestJREInfo(VersionString paramVersionString1, VersionString paramVersionString2)
  {
    int i = 0;
    if ((paramVersionString1 == null) && (this.javaPlatformList.size() > 0))
      return (JREInfo)this.javaPlatformList.get(0);
    while (true)
    {
      if (i != 0)
      {
        Config.get().refreshIfNeeded();
        processJREInfo();
      }
      Iterator localIterator = this.javaPlatformList.iterator();
      while (localIterator.hasNext())
      {
        JREInfo localJREInfo = (JREInfo)localIterator.next();
        if (paramVersionString1.contains(localJREInfo.getProductVersion()))
          if ((paramVersionString2 != null) && (!paramVersionString2.toString().equals("")))
          {
            if ((localJREInfo.getFXVersion() != null) && (DefaultMatchJRE.isFXVersionMatch(localJREInfo.getFXVersion().toString(), paramVersionString2)))
              return localJREInfo;
          }
          else
            return localJREInfo;
      }
      if (i != 0)
        break;
      i = 1;
    }
    return (JREInfo)this.javaPlatformList.get(0);
  }

  synchronized JVMInstance getOrCreateBestJVMInstance(long paramLong, VersionString paramVersionString, JVMParameters paramJVMParameters, ClientJVMSelectionParameters paramClientJVMSelectionParameters, AppletID paramAppletID)
  {
    JVMInstance localJVMInstance = null;
    Object localObject;
    if (!paramClientJVMSelectionParameters.isSeparateJVM())
    {
      localJVMInstance = getBestJVMInstance(paramVersionString, paramJVMParameters, paramClientJVMSelectionParameters);
      if (localJVMInstance != null)
      {
        if (DEBUG)
        {
          System.out.println("JVMManager reusing JVMInstance for product version " + localJVMInstance.getProductVersion());
          System.out.println("\t Set AppletLaunchTime: " + paramLong);
        }
        localJVMInstance.setAppletLaunchTime(paramLong);
        return localJVMInstance;
      }
      localObject = getBestJREInfo(paramVersionString);
      localJVMInstance = getBestJVMInstance(new VersionString(((JREInfo)localObject).getProductVersion()), paramJVMParameters, paramClientJVMSelectionParameters);
      if (localJVMInstance != null)
      {
        if (DEBUG)
        {
          System.out.println("JVMManager reusing JVMInstance for product version " + localJVMInstance.getProductVersion());
          System.out.println("\t Set AppletLaunchTime: " + paramLong);
        }
        localJVMInstance.setAppletLaunchTime(paramLong);
        return localJVMInstance;
      }
    }
    localJVMInstance = createJVMInstance(paramLong, paramVersionString, paramJVMParameters, paramClientJVMSelectionParameters, paramAppletID);
    if (localJVMInstance == null)
    {
      if (DEBUG)
        System.out.print("Cannot find a suitable JRE.");
      return null;
    }
    if (DEBUG)
    {
      System.out.println("JVMManager starting JVMInstance for product version " + localJVMInstance.getProductVersion());
      localObject = localJVMInstance.getParameters().getCommandLineArguments(SystemUtil.isWindowsVista(), false);
      if (((List)localObject).size() > 0)
      {
        System.out.println("  Command-line arguments: ");
        for (int i = 0; i < ((List)localObject).size(); i++)
          System.out.println("    Argument " + i + ": " + ((List)localObject).get(i));
      }
    }
    try
    {
      localJVMInstance.start();
      return localJVMInstance;
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
      throw new RuntimeException(localIOException);
    }
    catch (RuntimeException localRuntimeException)
    {
      localRuntimeException.printStackTrace();
      throw localRuntimeException;
    }
    catch (Error localError)
    {
      localError.printStackTrace();
      throw localError;
    }
  }

  synchronized JVMInstance getBestJVMInstance(VersionString paramVersionString, JVMParameters paramJVMParameters, ClientJVMSelectionParameters paramClientJVMSelectionParameters)
  {
    if (paramClientJVMSelectionParameters.isSeparateJVM())
      return null;
    ArrayList localArrayList = new ArrayList();
    Object localObject1 = this.activeJVMs.values().iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (JVMInstance)((Iterator)localObject1).next();
      if (((JVMInstance)localObject2).exited())
        localArrayList.add(new Integer(((JVMInstance)localObject2).getID()));
    }
    localObject1 = localArrayList.iterator();
    while (((Iterator)localObject1).hasNext())
      this.activeJVMs.remove(((Iterator)localObject1).next());
    if (DEBUG)
    {
      System.out.println("Seeking suitable JRE for version IDs: " + paramVersionString);
      System.out.println("and JVMParameters: " + paramJVMParameters);
      System.out.println(this.activeJVMs.values().size() + " active JVM(s)");
    }
    localObject1 = null;
    Object localObject2 = this.activeJVMs.values().iterator();
    while (((Iterator)localObject2).hasNext())
    {
      localObject3 = (JVMInstance)((Iterator)localObject2).next();
      if (DEBUG)
        System.out.println("Considering for reuse: " + localObject3);
      if (((JVMInstance)localObject3).isExclusive())
      {
        if (DEBUG)
          System.out.println("\tRejected by exclusive instance");
      }
      else if (!((JVMInstance)localObject3).isHealthy())
      {
        if (DEBUG)
          System.out.println("\tRejected by unhealthy: " + ((JVMInstance)localObject3).getHealthData());
      }
      else if (!paramClientJVMSelectionParameters.match((JVMInstance)localObject3))
      {
        if (DEBUG)
          System.out.println("\tRejected by targetJVMSelectionPreferences: " + paramClientJVMSelectionParameters);
      }
      else if ((paramVersionString != null) && (!paramVersionString.contains(((JVMInstance)localObject3).getProductVersion())))
      {
        if (DEBUG)
          System.out.println("\tRejected by unmatch version: " + ((JVMInstance)localObject3).getProductVersion());
      }
      else if (!((JVMInstance)localObject3).getParameters().satisfies(paramJVMParameters))
      {
        if (DEBUG)
          System.out.println("\tRejected by unsatisfied parameters: " + ((JVMInstance)localObject3).getParameters());
      }
      else if ((localObject1 != null) && (!((JVMInstance)localObject3).getProductVersion().isGreaterThan(((JVMInstance)localObject1).getProductVersion())))
      {
        if (DEBUG)
          System.out.println("\tRejected, not later than current best: " + ((JVMInstance)localObject1).getProductVersion());
      }
      else
      {
        localObject1 = localObject3;
        if (DEBUG)
          System.out.println("  Selected: " + ((JVMInstance)localObject1).getProductVersion());
      }
    }
    if (DEBUG)
      System.out.println("Selecting a matched JREInfo...");
    localObject2 = null;
    Object localObject3 = this.javaPlatformList.iterator();
    while (((Iterator)localObject3).hasNext())
    {
      JREInfo localJREInfo = (JREInfo)((Iterator)localObject3).next();
      if (DEBUG)
        System.out.println("Considering " + localJREInfo.getProductVersion() + " JVM for launch");
      if (((paramVersionString == null) || (paramVersionString.contains(localJREInfo.getProductVersion()))) && ((localObject2 == null) || (localJREInfo.getProductVersion().isGreaterThan(((JREInfo)localObject2).getProductVersion()))))
      {
        localObject2 = localJREInfo;
        if (DEBUG)
          System.out.println("  (chosen)");
      }
      else if (DEBUG)
      {
        System.out.println("  (rejected)");
      }
    }
    if (localObject1 != null)
    {
      if (localObject2 == null)
        throw new InternalError("Should not find a running JVM instance but no matching JRE platform");
      if (((JVMInstance)localObject1).getProductVersion().isGreaterThanOrEqual(((JREInfo)localObject2).getProductVersion()))
      {
        if (DEBUG)
          System.out.println("Reusing JVM instance with product version " + ((JVMInstance)localObject1).getProductVersion() + "; best available product version " + ((JREInfo)localObject2).getProductVersion());
        return localObject1;
      }
      if (DEBUG)
        System.out.println("NOT reusing JVM instance with product version " + ((JVMInstance)localObject1).getProductVersion() + "; best available product version " + ((JREInfo)localObject2).getProductVersion());
    }
    else if (DEBUG)
    {
      System.out.println("No suitable JVM instance to reuse");
    }
    return null;
  }

  JVMInstance createJVMInstance(long paramLong, VersionString paramVersionString, JVMParameters paramJVMParameters, ClientJVMSelectionParameters paramClientJVMSelectionParameters, AppletID paramAppletID)
  {
    JREInfo localJREInfo = getBestJREInfo(paramVersionString, new VersionString(paramClientJVMSelectionParameters.getJfxRequirement()));
    if (localJREInfo == null)
      return null;
    JVMParameters localJVMParameters = SystemUtil.prepareJVMParameter(paramJVMParameters, localJREInfo, (JVMParameters)this.javaParamMap.get(localJREInfo), paramClientJVMSelectionParameters);
    if (this.userJPIProfile != null)
      localJVMParameters.addInternalArgument("-Djavaplugin.user.profile=" + this.userJPIProfile);
    if (DEBUG)
      System.out.println("JVMManager creating JVMInstance for product version " + localJREInfo.getProductVersion());
    int i = nextJVMID();
    JVMInstance localJVMInstance = new JVMInstance(paramLong, i, localJREInfo, localJVMParameters, paramClientJVMSelectionParameters.isSeparateJVM(), paramAppletID);
    boolean bool = false;
    JfxRuntime localJfxRuntime = null;
    if (paramClientJVMSelectionParameters.getJfxRequirement() != null)
    {
      localJfxRuntime = localJREInfo.getJfxRuntime();
      bool = (localJfxRuntime != null) && (paramClientJVMSelectionParameters.useJfxToolkit());
    }
    localJVMInstance.setJfxSupport(localJfxRuntime);
    localJVMInstance.setUseJfxToolkit(bool);
    synchronized (this)
    {
      this.activeJVMs.put(new Integer(i), localJVMInstance);
    }
    return localJVMInstance;
  }

  private synchronized int nextAppletID()
  {
    return ++this.curAppletID;
  }

  private static void filterJavaPlatformList(List paramList)
  {
    VersionID localVersionID1 = new VersionID("1.4+");
    VersionID localVersionID2 = new VersionID("1.5+");
    int i = SystemUtil.getOSType() == 2 ? 1 : 0;
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      JREInfo localJREInfo = (JREInfo)localIterator.next();
      if ((!localVersionID1.match(localJREInfo.getProductVersion())) || ((i != 0) && (!localVersionID2.match(localJREInfo.getProductVersion()))))
      {
        localIterator.remove();
      }
      else if (!localJREInfo.isOsInfoMatch(Config.getOSName(), Config.getOSArch()))
      {
        localIterator.remove();
      }
      else
      {
        File localFile = new File(localJREInfo.getPath());
        if (!localFile.exists())
          localIterator.remove();
      }
    }
  }

  private static void filterDisabledJREs(List paramList)
  {
    String str = SystemUtil.getJavaHome();
    Object localObject = null;
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      JREInfo localJREInfo = (JREInfo)localIterator.next();
      if (localJREInfo.getJREPath().startsWith(str))
        localObject = localJREInfo;
      if (!localJREInfo.isEnabled())
        localIterator.remove();
    }
    if ((paramList.isEmpty()) && (localObject != null))
      paramList.add(localObject);
  }

  void setJavaPlatformListForTest(List paramList)
  {
    this.javaPlatformList.clear();
    this.javaPlatformList.addAll(paramList);
    updateJavaParamMap();
  }

  static void resetInstanceForTest()
  {
    soleInstance = null;
  }

  private static String noDash(String paramString)
  {
    int i = paramString.indexOf("-");
    if (i != -1)
      return paramString.substring(0, i);
    return paramString;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.main.server.JVMManager
 * JD-Core Version:    0.6.2
 */