package sun.plugin;

import com.sun.deploy.config.Config;
import com.sun.deploy.perf.DeployPerfUtil;
import com.sun.deploy.perf.NativePerfHelper;
import com.sun.deploy.resources.ResourceManager;
import com.sun.deploy.trace.FileTraceListener;
import com.sun.deploy.trace.LoggerTraceListener;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.trace.TraceLevel;
import com.sun.deploy.trace.TraceListener;
import com.sun.deploy.uitoolkit.ToolkitStore;
import com.sun.deploy.uitoolkit.ui.ConsoleController14;
import com.sun.deploy.uitoolkit.ui.ConsoleTraceListener;
import com.sun.deploy.uitoolkit.ui.ConsoleWindow;
import com.sun.deploy.uitoolkit.ui.UIFactory;
import com.sun.deploy.util.DeploySysRun;
import java.io.File;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.plugin.util.PluginSysUtil;
import sun.plugin.util.UserProfile;
import sun.security.action.GetBooleanAction;
import sun.security.action.GetPropertyAction;

public class JavaRunTime
{
  private static boolean traceInit = false;
  private static ConsoleWindow console = null;
  private static ConsoleTraceListener ctl = new ConsoleTraceListener();
  private static ConsoleController14 controller = null;

  public static void initEnvironment(String paramString1, String paramString2, String paramString3)
  {
    initEnvironment(paramString1, paramString2, paramString3, true);
  }

  public static void initEnvironment(String paramString1, String paramString2, String paramString3, boolean paramBoolean)
  {
    if (DeployPerfUtil.isEnabled() == true)
      DeployPerfUtil.initialize(new NativePerfHelper());
    DeployPerfUtil.put("START - Java   - JVM - JavaRunTime.initEnvironment");
    DeployPerfUtil.put("START - Java   - JVM - JavaRunTime.initEnvironment - instantiate PluginSysUtil");
    DeploySysRun.setOverride(new PluginSysUtil());
    DeployPerfUtil.put("END   - Java   - JVM - JavaRunTime.initEnvironment - instantiate PluginSysUtil");
    DeployPerfUtil.put("START - Java   - JVM - JavaRunTime.initEnvironment - set user.home property");
    Properties localProperties = System.getProperties();
    localProperties.put("java.home", paramString1);
    if (paramString3 == null)
      localProperties.put("user.home", paramString1);
    else
      localProperties.put("user.home", paramString3);
    DeployPerfUtil.put("END   - Java   - JVM - JavaRunTime.initEnvironment - set user.home property");
    try
    {
      Class localClass = Class.forName("sun.plugin.AppletViewer");
      Method localMethod = localClass.getMethod("loadPropertiesFiles", null);
      localMethod.invoke(null, null);
    }
    catch (Throwable localThrowable)
    {
    }
    DeployPerfUtil.put("START - Java   - JVM - JavaRunTime.initEnvironment - setup trace redirect");
    if (paramBoolean)
      Trace.redirectStdioStderr();
    DeployPerfUtil.put("END   - Java   - JVM - JavaRunTime.initEnvironment - setup trace redirect");
    DeployPerfUtil.put("END   - Java   - JVM - JavaRunTime.initEnvironment");
  }

  private static synchronized ConsoleWindow getJavaConsole()
  {
    initTraceEnvironment();
    if (console == null)
    {
      console = ToolkitStore.getUI().getConsole(controller);
      ctl.setConsole(console);
      console.clear();
    }
    return console;
  }

  public static synchronized void initTraceEnvironment()
  {
    initTraceEnvironment(null);
  }

  public static synchronized void initTraceEnvironment(ConsoleController14 paramConsoleController14)
  {
    if (traceInit)
      return;
    traceInit = true;
    Trace.clearTraceListeners();
    File localFile1 = new File(UserProfile.getLogDirectory());
    if (!localFile1.exists())
      localFile1.mkdirs();
    if (paramConsoleController14 != null)
      controller = paramConsoleController14;
    else
      try
      {
        controller = (ConsoleController14)Class.forName("sun.plugin.util.PluginConsoleController").newInstance();
      }
      catch (Throwable localThrowable)
      {
      }
    Trace.addTraceListener(ctl);
    boolean bool1 = Config.getBooleanProperty("javaplugin.trace");
    boolean bool2 = Config.getBooleanProperty("deployment.trace");
    String str1 = null;
    if (bool1)
      str1 = Config.getStringProperty("javaplugin.trace.option");
    else if (bool2)
      str1 = Config.getStringProperty("deployment.trace.level");
    boolean bool3;
    String str2;
    Object localObject;
    if ((bool1) || (bool2))
      try
      {
        if ((str1 == null) || (str1.equals("")))
        {
          Trace.setEnabled(TraceLevel.BASIC, true);
          Trace.setEnabled(TraceLevel.NETWORK, true);
          Trace.setEnabled(TraceLevel.CACHE, true);
          Trace.setEnabled(TraceLevel.TEMP, true);
          Trace.setEnabled(TraceLevel.SECURITY, true);
          Trace.setEnabled(TraceLevel.EXTENSIONS, true);
          Trace.setEnabled(TraceLevel.LIVECONNECT, true);
        }
        else
        {
          Trace.setInitialTraceLevel(str1);
        }
        File localFile2 = null;
        bool3 = false;
        str2 = Config.getStringProperty("deployment.javapi.trace.filename");
        if ((str2 != null) && (str2.trim().length() != 0))
        {
          localFile2 = new File(str2);
          localObject = localFile2.getParentFile();
          if (localObject != null)
            ((File)localObject).mkdirs();
          localFile2.createNewFile();
          if (localFile2.exists())
            bool3 = true;
          else
            localFile2 = null;
        }
        if (localFile2 == null)
        {
          localObject = (Boolean)AccessController.doPrivileged(new GetBooleanAction("javaplugin.outputfiles.overwrite"));
          if (Boolean.TRUE.equals(localObject))
          {
            StringBuffer localStringBuffer = new StringBuffer();
            localStringBuffer.append(localFile1);
            localStringBuffer.append(File.separator);
            localStringBuffer.append("plugin");
            String str3 = (String)AccessController.doPrivileged(new GetPropertyAction("javaplugin.nodotversion"));
            localStringBuffer.append(str3);
            localStringBuffer.append(".trace");
            localFile2 = new File(localStringBuffer.toString());
          }
        }
        localObject = FileTraceListener.getOrCreateSharedInstance(localFile2, localFile1, "plugin", ".trace", false, bool3);
        Trace.addTraceListener((TraceListener)localObject);
      }
      catch (Exception localException1)
      {
        Trace.println("can not write to trace file", TraceLevel.BASIC);
        Trace.ignored(localException1);
      }
    if (Config.getBooleanProperty("deployment.log"))
      try
      {
        File localFile3 = null;
        bool3 = false;
        str2 = Config.getStringProperty("deployment.javapi.log.filename");
        if ((str2 != null) && (str2.trim().length() != 0))
        {
          localFile3 = new File(str2);
          localObject = localFile3.getParentFile();
          if (localObject != null)
            ((File)localObject).mkdirs();
          localFile3.createNewFile();
          if (localFile3.exists())
            bool3 = true;
          else
            localFile3 = null;
        }
        localObject = LoggerTraceListener.getOrCreateSharedInstance("sun.plugin", localFile3, localFile1, "plugin", ".log", bool3);
        ((LoggerTraceListener)localObject).getLogger().setLevel(Level.ALL);
        controller.setLogger(((LoggerTraceListener)localObject).getLogger());
        Trace.addTraceListener((TraceListener)localObject);
      }
      catch (Exception localException2)
      {
        Trace.println("can not write to log file", TraceLevel.BASIC);
        Trace.ignored(localException2);
      }
  }

  public static boolean isJavaConsoleVisible()
  {
    if (console == null)
      return false;
    return console.isVisible();
  }

  public static void showJavaConsole(boolean paramBoolean)
  {
    ConsoleWindow localConsoleWindow = getJavaConsole();
    if (localConsoleWindow != null)
      localConsoleWindow.setVisible(paramBoolean);
  }

  public static void showOldPluginWarning()
  {
    ConsoleWindow localConsoleWindow = getJavaConsole();
    if (localConsoleWindow != null)
      localConsoleWindow.append(ResourceManager.getMessage("console.show.oldplugin.warning"));
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.JavaRunTime
 * JD-Core Version:    0.6.2
 */