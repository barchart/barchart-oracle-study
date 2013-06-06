package sun.plugin.perf;

import com.sun.deploy.perf.PerfLabel;
import com.sun.deploy.perf.PerfRollup;
import java.io.PrintStream;
import java.util.HashMap;

public class PluginRollup
  implements PerfRollup
{
  private static final String JVM_INITIALIZE_JAVA = "Plug-in Java VM initialization phase";
  private static final String JVM_START_JAVA_VM = "Plug-in Java VM startup";
  private static final String JVM_RUNTIME_INIT = "invoke JavaRunTime.initEnvironment";
  private static final String JVM_CONSOLE = "Java console initialization";
  private static final String JVM_IE_JNI_REG = "register IE specific JNI methods";
  private static final String JVM_MOZILLA_JNI_REG = "register Mozilla specific JNI methods";
  private static final String ENV_VIEWER_INIT = "AppletViewer.initEnvironment";
  private static final String ENV_THREAD_GRP = "AppletViewer.initEnvironment - PluginSysUtil.getPluginThreadGroup";
  private static final String ENV_TRACE_ENV = "AppletViewer.initEnvironment - JavaRunTime.initTraceEnvironment";
  private static final String ENV_PROXY_AUTH = "AppletViewer.initEnvironment - enable proxy/web server authentication";
  private static final String ENV_PROXY_SELECT = "AppletViewer.initEnvironment - DeployProxySelector.reset";
  private static final String ENV_WIN_SYS_TRAY = "AppletViewer.initEnvironment - show update message";
  private static final String ENV_UPGRADE_CACHE = "AppletViewer.initEnvironment - upgrade cache";
  private static final String ENV_CREATE_IE_OBJ = "create browser plugin object (IE)";
  private static final String ENV_CREATE_MOZW_OBJ = "create browser plugin object (Mozilla:Windows)";
  private static final String ENV_CREATE_MOZU_OBJ = "create browser plugin object (Unix:Windows)";
  private static final String ENV_CREATE_IE_WND = "create embedded browser frame (IE)";
  private static final String ENV_CREATE_MOZW_WND = "create embedded browser frame (Mozilla:Windows)";
  private static final String ENV_CREATE_MOZU_WND = "create embedded browser frame (Mozilla:Unix)";
  private static final String ENV_CREATE_LOADER = "AppletViewer.createClassLoader";
  private static final String ENV_APPLET_INIT = "AppletViewer.initApplet";
  private static final String OVERALL_APPLET_INIT_START = "AppletViewer.initEnvironment";
  private static final String OVERALL_APPLET_INIT_END = "AppletViewer.initApplet";

  public void doRollup(PerfLabel[] paramArrayOfPerfLabel, PrintStream paramPrintStream)
  {
    if (paramArrayOfPerfLabel.length > 0)
    {
      EventSet localEventSet = new EventSet(paramArrayOfPerfLabel);
      long l1 = paramArrayOfPerfLabel[(paramArrayOfPerfLabel.length - 1)].getTime() - paramArrayOfPerfLabel[0].getTime();
      long l2 = localEventSet.getEventDelta("Plug-in Java VM initialization phase");
      long l3 = localEventSet.getEventDelta("Plug-in Java VM startup");
      long l4 = localEventSet.getEventDelta("invoke JavaRunTime.initEnvironment");
      long l5 = localEventSet.getEventDelta("Java console initialization");
      long l6 = 0L;
      Event localEvent1 = null;
      localEvent1 = localEventSet.getEvent("register IE specific JNI methods");
      if (localEvent1 != null)
        l6 = localEvent1.getDelta();
      else
        l6 = localEventSet.getEventDelta("register Mozilla specific JNI methods");
      l2 += l6;
      long l7 = localEventSet.getEventDelta("AppletViewer.initEnvironment");
      long l8 = localEventSet.getEventDelta("AppletViewer.initEnvironment - PluginSysUtil.getPluginThreadGroup");
      long l9 = localEventSet.getEventDelta("AppletViewer.initEnvironment - JavaRunTime.initTraceEnvironment");
      long l10 = localEventSet.getEventDelta("AppletViewer.initEnvironment - enable proxy/web server authentication");
      long l11 = localEventSet.getEventDelta("AppletViewer.initEnvironment - DeployProxySelector.reset");
      long l12 = localEventSet.getEventDelta("AppletViewer.initEnvironment - show update message");
      long l13 = localEventSet.getEventDelta("AppletViewer.initEnvironment - upgrade cache");
      long l14 = localEventSet.getEventDelta("AppletViewer.createClassLoader");
      long l15 = localEventSet.getEventDelta("AppletViewer.initApplet");
      long l16 = 0L;
      long l17 = 0L;
      localEvent1 = localEventSet.getEvent("create browser plugin object (IE)");
      if (localEvent1 != null)
      {
        l16 = localEvent1.getDelta();
        l17 = localEventSet.getEventDelta("create embedded browser frame (IE)");
      }
      else
      {
        localEvent1 = localEventSet.getEvent("create browser plugin object (Mozilla:Windows)");
        if (localEvent1 != null)
        {
          l16 = localEvent1.getDelta();
          l17 = localEventSet.getEventDelta("create embedded browser frame (Mozilla:Windows)");
        }
        else
        {
          l16 = localEventSet.getEventDelta("create browser plugin object (Unix:Windows)");
          l17 = localEventSet.getEventDelta("create embedded browser frame (Mozilla:Unix)");
        }
      }
      long l18 = 0L;
      localEvent1 = localEventSet.getEvent("AppletViewer.initEnvironment");
      if (localEvent1 != null)
      {
        Event localEvent2 = localEventSet.getEvent("AppletViewer.initApplet");
        if (localEvent2 != null)
          l18 = localEvent2.getEnd().getTime() - localEvent1.getStart().getTime();
      }
      paramPrintStream.println();
      paramPrintStream.println("Overall Plug-in startup time................... " + l1 + " ms");
      paramPrintStream.println("     Total time starting JVM................... " + l2 + " ms");
      paramPrintStream.println("         JVM startup........................... " + l3 + " ms");
      paramPrintStream.println("         Runtime initialization................ " + l4 + " ms");
      paramPrintStream.println("         Console initialization................ " + l5 + " ms");
      paramPrintStream.println("         Browser specific JVM initialization... " + l6 + " ms");
      paramPrintStream.println("     Total time preparing applet............... " + l18 + " ms");
      paramPrintStream.println("         Viewer initialization................. " + l7 + " ms");
      paramPrintStream.println("              get Plug-in thread group......... " + l8 + " ms");
      paramPrintStream.println("              init trace environment........... " + l9 + " ms");
      paramPrintStream.println("              enable proxy authentication...... " + l10 + " ms");
      paramPrintStream.println("              proxy selector reset............. " + l11 + " ms");
      paramPrintStream.println("              update system tray message....... " + l12 + " ms");
      paramPrintStream.println("              upgrade cache.................... " + l13 + " ms");
      paramPrintStream.println("         Create Plug-in object................. " + l16 + " ms");
      paramPrintStream.println("         Create applet window.................. " + l17 + " ms");
      paramPrintStream.println("         Create Plug-in class loader........... " + l14 + " ms");
      paramPrintStream.println("         Invoke applet.init().................. " + l15 + " ms");
    }
    else
    {
      paramPrintStream.println("Plug-in perf logging is not currently implemented for UNIX platforms.");
    }
  }

  class Event
  {
    String key;
    PerfLabel start;
    PerfLabel end;

    PerfLabel getStart()
    {
      return this.start;
    }

    PerfLabel getEnd()
    {
      return this.end;
    }

    void setEnd(PerfLabel paramPerfLabel)
    {
      this.end = paramPerfLabel;
    }

    long getDelta()
    {
      return this.end.getTime() - this.start.getTime();
    }

    Event(String paramPerfLabel, PerfLabel arg3)
    {
      this.key = paramPerfLabel;
      Object localObject;
      this.start = localObject;
      this.end = null;
    }
  }

  class EventSet
  {
    HashMap map = new HashMap();
    static final int TYPE_START = 0;
    static final int TYPE_END = 5;
    static final int AREA_START = 8;
    static final int AREA_END = 14;
    static final int CAT_START = 17;
    static final int CAT_END = 20;
    static final int KEY_START = 23;

    PluginRollup.Event getEvent(String paramString)
    {
      return (PluginRollup.Event)this.map.get(paramString);
    }

    long getEventDelta(String paramString)
    {
      PluginRollup.Event localEvent = getEvent(paramString);
      return localEvent != null ? localEvent.getDelta() : 0L;
    }

    EventSet(PerfLabel[] arg2)
    {
      Object localObject;
      for (int i = 0; i < localObject.length; i++)
      {
        long l = localObject[i].getTime();
        String str1 = localObject[i].getLabel();
        String str2 = str1.substring(0, 5).trim();
        String str3 = str1.substring(23).trim();
        if (str2.equals("START") == true)
        {
          this.map.put(str3, new PluginRollup.Event(PluginRollup.this, str3, localObject[i]));
        }
        else if (str2.equals("END") == true)
        {
          PluginRollup.Event localEvent = (PluginRollup.Event)this.map.get(str3);
          if (localEvent != null)
            localEvent.setEnd(localObject[i]);
        }
      }
    }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.perf.PluginRollup
 * JD-Core Version:    0.6.2
 */