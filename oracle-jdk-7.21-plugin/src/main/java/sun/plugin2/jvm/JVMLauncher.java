package sun.plugin2.jvm;

import com.sun.deploy.Environment;
import com.sun.deploy.config.Config;
import com.sun.deploy.config.OSType;
import com.sun.deploy.perf.DefaultPerfHelper;
import com.sun.deploy.perf.DeployPerfUtil;
import com.sun.deploy.util.JVMParameters;
import com.sun.deploy.util.SystemUtils;
import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import sun.plugin2.util.SystemUtil;

public class JVMLauncher
  implements ProcessLauncher
{
  private static final boolean DEBUG = SystemUtil.getenv("JPI_PLUGIN2_DEBUG") != null;
  private long jvmLaunchTime;
  private long appletLaunchTime;
  private String javaHome;
  private JVMParameters params;
  private Process process;
  private volatile boolean exited;
  private int exitCode = -1;
  private Exception error;
  private List listeners = new ArrayList();

  public JVMLauncher(long paramLong, String paramString, JVMParameters paramJVMParameters)
  {
    this.appletLaunchTime = paramLong;
    this.javaHome = paramString;
    this.params = paramJVMParameters;
  }

  public synchronized void addJVMEventListener(JVMEventListener paramJVMEventListener)
  {
    this.listeners.add(paramJVMEventListener);
  }

  public void start()
  {
    this.jvmLaunchTime = SystemUtils.microTime();
    if (DEBUG)
    {
      System.out.println("JVMLauncher.start: launcher params:");
      List localList1 = this.params.getCommandLineArguments(true, false);
      Iterator localIterator1 = localList1.iterator();
      while (localIterator1.hasNext())
        System.out.println("\t<" + (String)localIterator1.next() + ">");
    }
    if (DEBUG)
      System.out.println("JVMLauncher.start(): now - user.startApplet(): " + (this.jvmLaunchTime - this.appletLaunchTime) + " us");
    if (DeployPerfUtil.isEnabled())
      DeployPerfUtil.initialize(new DefaultPerfHelper(this.appletLaunchTime));
    long l = DeployPerfUtil.put(0L, "JVMLauncher.start() - BEGIN");
    String str1 = "-D__applet_launched=" + this.appletLaunchTime;
    String str2 = "-D__jvm_launched=" + this.jvmLaunchTime;
    int i = Config.getMaxCommandLineLength();
    String str3 = findJava();
    if (str3 == null)
      throw new RuntimeException("Unable to locate the java launcher in java.home \"" + this.javaHome + "\"");
    this.exited = false;
    this.error = null;
    List localList2;
    int k;
    if ((SystemUtil.isWindowsVista()) && (!isRemote()))
    {
      String str4 = findPlugin2VistaLauncher();
      if (str4 == null)
        throw new RuntimeException("Unable to locate the Java Plug-In's custom launcher for Windows Vista in java.home \"" + System.getProperty("java.home") + "\"");
      i -= str4.length() + 1;
      i -= this.javaHome.length() + 1;
      i -= str2.length() + 1;
      i -= str1.length() + 1;
      localList2 = this.params.getCommandLineArguments(false, true, true, true, true, i);
      localList2.add(0, str4);
      localList2.add(1, str2);
      localList2.add(2, str1);
    }
    else
    {
      i -= str3.length() + 1;
      i -= str2.length() + 1;
      i -= str1.length() + 1;
      localList2 = this.params.getCommandLineArguments(false, true, true, true, false, i);
      String str5 = null;
      Iterator localIterator2 = localList2.iterator();
      int j = 0;
      k = localList2.size();
      while (j < k)
      {
        str5 = (String)localIterator2.next();
        if (!JVMParameters.isJVMCommandLineArgument(str5))
          break;
        j++;
      }
      if (j == k)
      {
        this.exited = true;
        this.error = new RuntimeException("Invalid arguments: no main class found");
        fireJVMExited();
        return;
      }
      if ((str5 != null) && (!str5.equals("sun.plugin2.main.client.PluginMain")))
      {
        this.exited = true;
        this.error = new RuntimeException("Invalid arguments: PluginMain main class not found");
        fireJVMExited();
        return;
      }
      localList2.add(0, str3);
      localList2.add(1, str2);
      localList2.add(2, str1);
    }
    DeployPerfUtil.put("JVMLauncher.start() - post param parsing");
    Object localObject2;
    if (DEBUG)
    {
      localObject1 = null;
      k = 0;
      localObject2 = localList2.iterator();
      int m = 0;
      int n = localList2.size();
      while (m < n)
      {
        localObject1 = (String)((Iterator)localObject2).next();
        k += ((String)localObject1).length() + 1;
        System.out.println("JVMLauncher.processArg[" + m + "]: " + (String)localObject1);
        m++;
      }
      System.out.println("JVMLauncher.processArgs total len: " + k + ", custArgsMaxLen: " + i);
    }
    DeployPerfUtil.put("JVMLauncher.start() - pre ProcessBuilder cstr");
    Object localObject1 = new ProcessBuilder(localList2);
    DeployPerfUtil.put("JVMLauncher.start() - post ProcessBuilder cstr");
    Map localMap = ((ProcessBuilder)localObject1).environment();
    DeployPerfUtil.put("JVMLauncher.start() - post ProcessBuilder env mapping");
    String str6 = (String)localMap.get("LD_LIBRARY_PATH");
    if (str6 != null)
    {
      String[] arrayOfString = str6.split(File.pathSeparator);
      localObject2 = Config.getJREHome();
      for (int i1 = 0; i1 < arrayOfString.length; i1++)
        if (arrayOfString[i1].startsWith((String)localObject2))
          arrayOfString[i1] = null;
      String str7 = null;
      for (int i2 = 0; i2 < arrayOfString.length; i2++)
        if (arrayOfString[i2] != null)
          if (str7 == null)
            str7 = arrayOfString[i2];
          else
            str7 = str7 + File.pathSeparator + arrayOfString[i2];
      localMap.put("LD_LIBRARY_PATH", str7);
    }
    if (OSType.isMac())
      localMap.put("LC_CTYPE", "UTF-8");
    DeployPerfUtil.put("JVMLauncher.start() - post ProcessBuilder env LD_LIBRARY_PATH");
    localMap.remove("CLASSPATH");
    DeployPerfUtil.put("JVMLauncher.start() - post ProcessBuilder env cleanup ");
    try
    {
      DeployPerfUtil.put("JVMLauncher.start() - pre process start ");
      this.process = ((ProcessBuilder)localObject1).start();
      DeployPerfUtil.put("JVMLauncher.start() - post process start ");
      afterStart();
    }
    catch (Exception localException)
    {
      this.exited = true;
      this.error = localException;
    }
    DeployPerfUtil.put(l, "JVMLauncher.start() - END");
  }

  protected void afterStart()
  {
    System.out.println("JVMLauncher.afterStart(): starting JVM process watcher");
    new Thread(new JVMWatcher()).start();
  }

  public JVMParameters getParameters()
  {
    return this.params;
  }

  public void addParameter(String paramString)
  {
    this.params.addArgument(paramString);
  }

  public void clearUserArguments()
  {
    this.params.clearUserArguments();
  }

  public boolean exited()
  {
    return this.exited;
  }

  public int getExitCode()
  {
    return this.exitCode;
  }

  public Exception getErrorDuringStartup()
  {
    return this.error;
  }

  public void destroy()
  {
    this.process.destroy();
  }

  public InputStream getInputStream()
  {
    if (this.process != null)
      return this.process.getInputStream();
    return null;
  }

  public InputStream getErrorStream()
  {
    if (this.process != null)
      return this.process.getErrorStream();
    return null;
  }

  public long getJVMLaunchTime()
  {
    return this.jvmLaunchTime;
  }

  public long getAppletLaunchTime()
  {
    return this.appletLaunchTime;
  }

  private String findJava()
  {
    String str1 = SystemUtil.formatExecutableName("java");
    String str2 = this.javaHome + File.separator + "bin" + File.separator + str1;
    if (new File(str2).exists())
      return str2;
    str2 = this.javaHome + File.separator + "jre" + File.separator + "bin" + File.separator + str1;
    if (new File(str2).exists())
      return str2;
    return null;
  }

  private String findPlugin2VistaLauncher()
  {
    String str1 = "jp2launcher.exe";
    String str2 = Environment.getDeploymentHomePath();
    if (!str2.endsWith(File.separator))
      str2 = str2 + File.separator;
    str2 = str2 + "bin" + File.separator + str1;
    if (new File(str2).exists())
      return str2;
    return null;
  }

  private synchronized List copyListeners()
  {
    return (List)((ArrayList)this.listeners).clone();
  }

  private void fireJVMExited()
  {
    Iterator localIterator = copyListeners().iterator();
    while (localIterator.hasNext())
    {
      JVMEventListener localJVMEventListener = (JVMEventListener)localIterator.next();
      localJVMEventListener.jvmExited(this);
    }
  }

  protected boolean isRemote()
  {
    return false;
  }

  class JVMWatcher
    implements Runnable
  {
    JVMWatcher()
    {
    }

    public void run()
    {
      int i = 0;
      while (i == 0)
        try
        {
          JVMLauncher.this.exitCode = JVMLauncher.this.process.waitFor();
          i = 1;
        }
        catch (InterruptedException localInterruptedException)
        {
          localInterruptedException.printStackTrace();
        }
      JVMLauncher.this.exited = true;
      JVMLauncher.this.fireJVMExited();
    }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.jvm.JVMLauncher
 * JD-Core Version:    0.6.2
 */