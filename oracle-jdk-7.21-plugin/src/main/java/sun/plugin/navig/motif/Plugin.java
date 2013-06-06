package sun.plugin.navig.motif;

import java.awt.Color;
import java.awt.Toolkit;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Properties;
import sun.awt.SunToolkit;
import sun.plugin.AppletViewer;
import sun.plugin.JavaRunTime;
import sun.plugin.viewer.LifeCycleManager;
import sun.plugin.viewer.MNetscapePluginContext;
import sun.plugin.viewer.MNetscapePluginObject;

public class Plugin extends JavaRunTime
{
  private static String encoding;
  private static Hashtable panels = new Hashtable();
  private static int nextId = 0;
  private static boolean tracing;
  private static PrintStream printOut;
  private static DataOutputStream cmdOut;
  private static DataInputStream cmdIn;
  private static Worker worker;
  private static int navig_version;
  private static BufferedWriter tracefile;
  private static String messages = "";
  public static final int CmdFD = 11;
  public static final int WorkFD = 12;
  public static final int PrintFD = 13;
  public static OJIPlugin oji;
  public static final int JAVA_PLUGIN_NEW = 16384001;
  public static final int JAVA_PLUGIN_DESTROY = 16384002;
  public static final int JAVA_PLUGIN_WINDOW = 16384003;
  public static final int JAVA_PLUGIN_SHUTDOWN = 16384004;
  public static final int JAVA_PLUGIN_DOCBASE = 16384005;
  public static final int JAVA_PLUGIN_PROXY_MAPPING = 16384007;
  public static final int JAVA_PLUGIN_COOKIE = 16384008;
  public static final int JAVA_PLUGIN_JAVASCRIPT_REPLY = 16384010;
  public static final int JAVA_PLUGIN_JAVASCRIPT_END = 16384011;
  public static final int JAVA_PLUGIN_START = 16384017;
  public static final int JAVA_PLUGIN_STOP = 16384018;
  public static final int JAVA_PLUGIN_ATTACH_THREAD = 16384019;
  public static final int JAVA_PLUGIN_REQUEST_ABRUPTLY_TERMINATED = 16384020;
  public static final int JAVA_PLUGIN_GET_INSTANCE_JAVA_OBJECT = 16384021;
  public static final int JAVA_PLUGIN_PRINT = 16384022;
  public static final int JAVA_PLUGIN_CONSOLE_SHOW = 16384025;
  public static final int JAVA_PLUGIN_CONSOLE_HIDE = 16384026;
  public static final int JAVA_PLUGIN_QUERY_XEMBED = 16384027;
  public static final int JAVA_PLUGIN_OK = 16449537;
  public static final int JAVA_PLUGIN_XEMBED_TRUE = 16449538;
  public static final int JAVA_PLUGIN_XEMBED_FALSE = 16449539;
  public static String Progress;

  public static void trace(String paramString)
  {
    if (tracing)
      try
      {
        System.err.println("Child: " + paramString);
        tracefile.write(paramString);
        tracefile.newLine();
      }
      catch (IOException localIOException)
      {
        System.err.println("Could not write to trace file");
      }
  }

  public static boolean getTracingFlag()
  {
    return tracing;
  }

  public static int getNavigVersion()
  {
    return navig_version;
  }

  public static void error(String paramString)
  {
    System.err.println("                      PLUGIN ERROR ");
    System.err.println("                      ************ ");
    System.err.println(paramString);
    System.err.println("\n");
    trace(paramString);
  }

  public static void start(boolean paramBoolean)
  {
    tracing = paramBoolean;
    Color localColor = Color.black;
    loadLibrary();
    String str1 = System.getProperty("javaplugin.nodotversion");
    Progress = System.getProperty("progressON");
    if (tracing)
    {
      str2 = "plugin_java" + str1 + ".trace";
      try
      {
        String str3 = System.getProperty("user.name", "unknown");
        str2 = "/tmp/plugin_java" + str1 + "_" + str3 + ".trace";
        tracefile = new BufferedWriter(new FileWriter(str2));
      }
      catch (IOException localIOException)
      {
        error("Could not create " + str2);
      }
    }
    String str2 = getenv("JAVA_PLUGIN_AGENT");
    if (str2.indexOf("Mozilla/3") >= 0)
    {
      trace("Setting Mozilla version to 3");
      navig_version = 3;
    }
    else if (str2.indexOf("Mozilla/4") >= 0)
    {
      trace("Setting Mozilla version to 4");
      navig_version = 4;
    }
    else
    {
      trace("Setting Mozilla version to 5");
      navig_version = 5;
    }
    Plugin localPlugin = new Plugin();
    localPlugin.doit();
  }

  private static String evalString(int paramInt, String paramString)
  {
    MNetscapePluginObject localMNetscapePluginObject = (MNetscapePluginObject)panels.get(new Integer(paramInt));
    if (localMNetscapePluginObject != null)
      return localMNetscapePluginObject.evalString(paramInt, paramString);
    return null;
  }

  private void doit()
  {
    try
    {
      trace("Plugin class started");
      String str1 = getenv("JAVA_HOME");
      localObject1 = getenv("HOME");
      JavaRunTime.initEnvironment(str1, "", (String)localObject1);
      localObject2 = getenv("JAVA_PLUGIN_VERSION");
      if (localObject2 != null)
        System.getProperties().put("javaplugin.version", localObject2);
      if (getNavigVersion() >= 5.0F)
      {
        AppletViewer.initEnvironment(4099);
        encoding = new String("UTF-8");
      }
      else
      {
        AppletViewer.initEnvironment(4098);
        encoding = new String("ISO-8859-1");
      }
      trace("Initialized environment. Printing  messages.\n");
      System.out.print(messages);
      Watcher localWatcher = new Watcher(null);
      localWatcher.start();
      initializeCommunication();
      while (true)
      {
        if (!parentAlive())
        {
          trace("parent is dead. Exiting.");
          onExit();
          System.exit(4);
        }
        int i = -1;
        trace("Plugin: Reading next  code...");
        try
        {
          i = cmdIn.readInt();
        }
        catch (EOFException localEOFException)
        {
          trace("Pipe got closed, our work is done. Exiting.");
          onExit();
          System.exit(4);
        }
        catch (IOException localIOException2)
        {
          trace("Could not read next command code!");
        }
        continue;
        if (tracing)
          trace("VM Received Command >>>" + protocol_to_str(i));
        int j;
        if (i == 16384001)
        {
          j = cmdIn.readInt();
          int m = cmdIn.readInt();
          int i2 = cmdIn.readInt();
          String[] arrayOfString1 = new String[i2];
          String[] arrayOfString2 = new String[i2];
          for (int i8 = 0; i8 < i2; i8++)
          {
            arrayOfString1[i8] = readString();
            arrayOfString2[i8] = readString();
            if (tracing)
              trace("   >" + arrayOfString1[i8] + "==>" + arrayOfString2[i8]);
          }
          MNetscapePluginObject localMNetscapePluginObject4 = MNetscapePluginContext.createPluginObject(m != 0, arrayOfString1, arrayOfString2, j);
          panels.put(new Integer(j), localMNetscapePluginObject4);
          trace("Registering panel:" + j);
          replyOK();
        }
        else
        {
          MNetscapePluginObject localMNetscapePluginObject1;
          if (i == 16384017)
          {
            j = cmdIn.readInt();
            localMNetscapePluginObject1 = getPluginObject(j);
            if (localMNetscapePluginObject1 != null)
              localMNetscapePluginObject1.startPlugin();
          }
          else if (i == 16384018)
          {
            j = cmdIn.readInt();
            localMNetscapePluginObject1 = getPluginObject(j);
            if (localMNetscapePluginObject1 != null)
              localMNetscapePluginObject1.stopPlugin();
          }
          else if (i == 16384002)
          {
            j = cmdIn.readInt();
            localMNetscapePluginObject1 = getPluginObject(j);
            if (localMNetscapePluginObject1 != null)
            {
              trace("Removing panel:" + j);
              panels.remove(new Integer(j));
              try
              {
                localMNetscapePluginObject1.destroyPlugin();
              }
              catch (Exception localException)
              {
                localException.printStackTrace();
              }
            }
            replyOK();
          }
          else
          {
            int i6;
            int i7;
            if (i == 16384003)
            {
              j = cmdIn.readInt();
              int n = cmdIn.readInt();
              int i3 = cmdIn.readInt();
              i6 = cmdIn.readInt();
              i7 = cmdIn.readInt();
              int i9 = cmdIn.readInt();
              int i10 = cmdIn.readInt();
              MNetscapePluginObject localMNetscapePluginObject6 = getPluginObject(j);
              trace("Window " + n + " " + i6 + "x" + i7 + " " + i9 + "x" + i10 + " xembed=" + i3);
              if (localMNetscapePluginObject6 != null)
                localMNetscapePluginObject6.setWindow(n, i3, i6, i7, i9, i10);
              replyOK();
            }
            else if (i != 16384004)
            {
              String str3;
              if (i == 16384005)
              {
                j = cmdIn.readInt();
                str3 = readString();
                trace("DOCBASE := " + str3);
                MNetscapePluginObject localMNetscapePluginObject2 = getPluginObject(j);
                trace("Setting docbase for " + j + " to " + str3);
                if (localMNetscapePluginObject2 != null)
                  localMNetscapePluginObject2.setDocumentURL(str3);
              }
              else if (i == 16384007)
              {
                String str2 = readString();
                str3 = readString();
                trace("Plugin.java: PROXY MAPPING: \"" + str2 + "\" => \"" + str3 + "\"");
                Worker.addProxyMapping(str2, str3);
              }
              else
              {
                int k;
                if (i == 16384022)
                {
                  k = cmdIn.readInt();
                  int i1 = cmdIn.readInt() / 10;
                  int i4 = cmdIn.readInt() / 10;
                  i6 = cmdIn.readInt() / 10;
                  i7 = cmdIn.readInt() / 10;
                  trace("PRINT " + k + " x=" + i1 + " y=" + i4 + " w=" + i6 + " h=" + i7);
                  MNetscapePluginObject localMNetscapePluginObject5 = getPluginObject(k);
                  if (localMNetscapePluginObject5 != null)
                    localMNetscapePluginObject5.doPrint(i1, i4, i6, i7, printOut);
                  replyOK();
                }
                else
                {
                  Object localObject3;
                  if (i == 16384008)
                  {
                    k = cmdIn.readInt();
                    localObject3 = readString();
                    Worker.setCookieString((String)localObject3);
                  }
                  else if (i == 16384010)
                  {
                    k = cmdIn.readInt();
                    localObject3 = readString();
                    MNetscapePluginObject localMNetscapePluginObject3 = getPluginObject(k);
                    if (localMNetscapePluginObject3 != null)
                      localMNetscapePluginObject3.setJSReply((String)localObject3);
                  }
                  else if (i == 16384011)
                  {
                    k = cmdIn.readInt();
                    localObject3 = getPluginObject(k);
                    if (localObject3 != null)
                      ((MNetscapePluginObject)localObject3).finishJSReply();
                  }
                  else if (i == 16384020)
                  {
                    Worker.terminateRequestAbruptly();
                  }
                  else if (i == 16384019)
                  {
                    trace("Attach Thread ");
                    attachThread();
                  }
                  else if (i == 16384021)
                  {
                    trace("Getting java object");
                    k = cmdIn.readInt();
                    localObject3 = getPluginObject(k);
                    int i5 = 0;
                    if (localObject3 != null)
                      i5 = ((MNetscapePluginObject)localObject3).getNativeJavaObject();
                    replyOK();
                    if (i5 == 0)
                      trace("Return null Java Object");
                    cmdOut.writeInt(i5);
                    cmdOut.flush();
                  }
                  else if (i == 16384027)
                  {
                    replyOK();
                    if (SunToolkit.needsXEmbed())
                      cmdOut.writeInt(16449538);
                    else
                      cmdOut.writeInt(16449539);
                    cmdOut.flush();
                  }
                  else if (i == 16384025)
                  {
                    trace("Showing Java Console");
                    JavaRunTime.showJavaConsole(true);
                  }
                  else if (i == 16384026)
                  {
                    trace("Hiding Java Console");
                    JavaRunTime.showJavaConsole(false);
                  }
                  else
                  {
                    error("Java process: unexpected request " + Integer.toHexString(i));
                    onExit();
                    System.exit(6);
                  }
                }
              }
            }
          }
        }
      }
    }
    catch (Throwable localThrowable)
    {
      Object localObject1;
      Object localObject2;
      if (parentAlive())
      {
        error("Java process caught exception: " + localThrowable);
        localThrowable.printStackTrace();
      }
      try
      {
        localObject1 = new FileOutputStream("plugin_stack.trace");
        localObject2 = new PrintWriter((OutputStream)localObject1);
        trace("Java process caught exception: " + localThrowable.toString());
        localThrowable.printStackTrace((PrintWriter)localObject2);
        ((PrintWriter)localObject2).flush();
        ((PrintWriter)localObject2).close();
      }
      catch (IOException localIOException1)
      {
        error("Could not print the stack trace\n");
      }
      trace("Exiting. Navigator may also be dead.");
      onExit();
      System.exit(6);
    }
  }

  protected void onExit()
  {
    LifeCycleManager.destroyCachedAppletPanels();
  }

  void initializeCommunication()
  {
    trace("Opening pipes at this end\n");
    cmdIn = newInput("Command Input", 11);
    cmdOut = newOutput("Command Output", 11);
    DataInputStream localDataInputStream = newInput("Work Input", 12);
    DataOutputStream localDataOutputStream = newOutput("Work Output", 12);
    FileDescriptor localFileDescriptor = getPipe(13);
    FileOutputStream localFileOutputStream = new FileOutputStream(localFileDescriptor);
    printOut = new PrintStream(localFileOutputStream);
    Toolkit localToolkit = Toolkit.getDefaultToolkit();
    trace("Toolkit = " + localToolkit.getClass().getName());
    worker = new Worker(localDataInputStream, localDataOutputStream);
    trace("Initialized worker");
    OJIPlugin.initialize();
    try
    {
      cmdOut.write(17);
      cmdOut.flush();
      trace("Wrote initial ack on command pipe");
    }
    catch (IOException localIOException)
    {
      error("Error in writing back to the parent");
    }
    trace("Wrote the initial ack\n");
  }

  public static MNetscapePluginObject getPluginObject(int paramInt)
  {
    Integer localInteger = new Integer(paramInt);
    MNetscapePluginObject localMNetscapePluginObject = (MNetscapePluginObject)panels.get(localInteger);
    if (localMNetscapePluginObject == null)
      error("Could not find a Viewer for " + paramInt);
    else
      trace("Found a viewer for:" + paramInt);
    return localMNetscapePluginObject;
  }

  static DataInputStream newInput(String paramString, int paramInt)
  {
    trace("Creating input pipe:" + paramString + " fd = " + paramInt);
    FileDescriptor localFileDescriptor = getPipe(paramInt);
    FileInputStream localFileInputStream = new FileInputStream(localFileDescriptor);
    DataInputStream localDataInputStream = new DataInputStream(localFileInputStream);
    return localDataInputStream;
  }

  static DataOutputStream newOutput(String paramString, int paramInt)
  {
    trace("Creating output pipe:" + paramString + " fd = " + paramInt);
    FileDescriptor localFileDescriptor = getPipe(paramInt);
    FileOutputStream localFileOutputStream = new FileOutputStream(localFileDescriptor);
    BufferedOutputStream localBufferedOutputStream = new BufferedOutputStream(localFileOutputStream);
    DataOutputStream localDataOutputStream = new DataOutputStream(localBufferedOutputStream);
    return localDataOutputStream;
  }

  private static String readString()
    throws IOException
  {
    int i = cmdIn.readUnsignedShort();
    byte[] arrayOfByte = new byte[i];
    for (int j = 0; j < i; j++)
      arrayOfByte[j] = cmdIn.readByte();
    String str = new String(arrayOfByte, encoding);
    trace("readString:" + str);
    return str;
  }

  private static byte[] readByteArray()
    throws IOException
  {
    int i = cmdIn.readInt();
    byte[] arrayOfByte = new byte[i];
    for (int j = 0; j < i; j++)
      arrayOfByte[j] = cmdIn.readByte();
    return arrayOfByte;
  }

  private static void replyOK()
    throws IOException
  {
    trace("Sending OK reply");
    cmdOut.writeInt(16449537);
    cmdOut.flush();
  }

  private static void loadLibrary()
  {
    String str = System.getProperty("javaplugin.lib");
    try
    {
      System.load(str);
    }
    catch (UnsatisfiedLinkError localUnsatisfiedLinkError)
    {
      System.err.println("Plugin could not load:" + str);
      System.err.println("Path is:" + System.getProperty("java.library.path"));
      System.err.println(localUnsatisfiedLinkError.toString());
    }
  }

  static boolean getTracing()
  {
    return tracing;
  }

  static native FileDescriptor getPipe(int paramInt);

  static native boolean parentAlive();

  private static native String getenv(String paramString);

  private static native void attachThread();

  public static String protocol_to_str(int paramInt)
  {
    switch (paramInt)
    {
    case 16384001:
      return "JAVA_PLUGIN_NEW";
    case 16384002:
      return "JAVA_PLUGIN_DESTROY";
    case 16384003:
      return "JAVA_PLUGIN_WINDOW";
    case 16384004:
      return "JAVA_PLUGIN_SHUTDOWN";
    case 16384005:
      return "JAVA_PLUGIN_DOCBASE";
    case 16384007:
      return "JAVA_PLUGIN_PROXY_MAPPING";
    case 16384008:
      return "JAVA_PLUGIN_COOKIE     ";
    case 16384010:
      return "JAVA_PLUGIN_JAVASCRIPT_REPLY";
    case 16384011:
      return "JAVA_PLUGIN_JAVASCRIPT_END";
    case 16384017:
      return "JAVA_PLUGIN_START";
    case 16384018:
      return "JAVA_PLUGIN_STOP";
    case 16384019:
      return "JAVA_PLUGIN_ATTACH_THRE";
    case 16449537:
      return "JAVA_PLUGIN_OK";
    case 16384022:
      return "JAVA_PLUGIN_PRINT";
    case 16384020:
      return "JAVA_PLUGIN_REQUEST_ABRUPTLY_TERMINATED";
    case 16384025:
      return "JAVA_PLUGIN_CONSOLE_SHOW";
    case 16384026:
      return "JAVA_PLUGIN_CONSOLE_HIDE";
    }
    return "Unknown code:" + paramInt;
  }

  private class Watcher extends Thread
  {
    private Watcher()
    {
    }

    public void run()
    {
      Plugin.trace(" Starting watcher\n");
      while (true)
      {
        try
        {
          Thread.sleep(30000L);
        }
        catch (InterruptedException localInterruptedException)
        {
        }
        if (!Plugin.parentAlive())
        {
          Plugin.trace(" exiting due to parent death");
          Plugin.this.onExit();
          System.exit(2);
        }
      }
    }

    Watcher(Plugin.1 arg2)
    {
      this();
    }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.navig.motif.Plugin
 * JD-Core Version:    0.6.2
 */