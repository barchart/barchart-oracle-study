package sun.plugin2.main.server;

import com.sun.deploy.config.Platform;
import com.sun.deploy.util.JVMParameters;
import com.sun.deploy.util.VersionID;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Map;
import sun.plugin2.util.SystemUtil;

public class ClientJVMSelectionParameters
{
  public static final VersionID DEFAULT_FX_VERSION = new VersionID("2.0+");
  public static final VersionID JFX_JRE_MINIMUM_VER = new VersionID("1.6.0_10");
  private static boolean DEBUG = SystemUtil.isDebug();
  private static boolean VERBOSE = SystemUtil.isVerbose();
  private boolean separateJVM;
  private long availableHeapSize = -1L;
  private VersionID needFx = null;
  private boolean useJfxToolkit = false;

  public static ClientJVMSelectionParameters getDefault()
  {
    return new ClientJVMSelectionParameters();
  }

  public void setAvailableHeapSize(long paramLong)
  {
    this.availableHeapSize = paramLong;
  }

  public void setSeparateJVM(boolean paramBoolean)
  {
    this.separateJVM = paramBoolean;
  }

  public void setJfxRequirement(String paramString)
  {
    if (paramString == null)
    {
      this.needFx = null;
    }
    else
    {
      paramString = paramString.trim();
      this.needFx = (paramString.length() == 0 ? DEFAULT_FX_VERSION : new VersionID(paramString));
    }
  }

  public VersionID getJfxRequirement()
  {
    return this.needFx;
  }

  public boolean useJfxToolkit()
  {
    return this.useJfxToolkit;
  }

  public static ClientJVMSelectionParameters extract(Map paramMap)
  {
    ClientJVMSelectionParameters localClientJVMSelectionParameters = new ClientJVMSelectionParameters();
    localClientJVMSelectionParameters.separateJVM = extractBoolean("separate_jvm", paramMap);
    localClientJVMSelectionParameters.availableHeapSize = -1L;
    String str = (String)paramMap.get("java_required_heap");
    if (str != null)
      try
      {
        localClientJVMSelectionParameters.availableHeapSize = JVMParameters.parseMemorySpec(str);
      }
      catch (Exception localException)
      {
        if (DEBUG)
        {
          System.out.println("WARNING: no JVM heap size restriction will be applieddue to invalid 'java_required_heap' value: " + str);
          System.out.println(", exception: " + localException.getLocalizedMessage());
        }
      }
    localClientJVMSelectionParameters.setJfxRequirement((String)paramMap.get("javafx_version"));
    if (localClientJVMSelectionParameters.getJfxRequirement() != null)
    {
      str = (String)paramMap.get("__ui_tk");
      if (str == null)
      {
        localClientJVMSelectionParameters.useJfxToolkit = true;
      }
      else
      {
        str = str.trim();
        localClientJVMSelectionParameters.useJfxToolkit = str.equalsIgnoreCase("jfx");
      }
    }
    return localClientJVMSelectionParameters;
  }

  public long getAvailableHeapSize()
  {
    return this.availableHeapSize;
  }

  public boolean isSeparateJVM()
  {
    return this.separateJVM;
  }

  private static boolean extractBoolean(String paramString, Map paramMap)
  {
    boolean bool = false;
    String str = (String)paramMap.get(paramString);
    if (str != null)
      bool = Boolean.valueOf(str).booleanValue();
    return bool;
  }

  public boolean match(JVMInstance paramJVMInstance)
  {
    boolean bool = false;
    if ((this.availableHeapSize < 0L) || (paramJVMInstance.getAvailableHeapSize() < 0L))
      bool = true;
    if ((!bool) && (this.availableHeapSize <= paramJVMInstance.getAvailableHeapSize()))
      bool = true;
    VersionID localVersionID = null;
    localVersionID = paramJVMInstance.getJfxAvailability();
    if (bool)
      if (this.needFx == null)
      {
        bool = null == localVersionID;
      }
      else
      {
        bool = paramJVMInstance.getProductVersion().isGreaterThanOrEqual(JFX_JRE_MINIMUM_VER);
        if (bool)
          bool = localVersionID == null ? Platform.get().getInstalledJfxRuntimes().isEmpty() : this.needFx.match(localVersionID);
      }
    if (bool)
      bool = paramJVMInstance.isJfxToolkit() == this.useJfxToolkit;
    if ((DEBUG) && (VERBOSE))
    {
      String str1 = "ClientJVMSelectionParameters matched=" + bool;
      str1 = str1 + " required availableHeapSize=" + this.availableHeapSize + "\n";
      String str2 = this.needFx == null ? "none" : this.needFx.toString();
      str1 = str1 + "  require FX version: " + str2 + "\n";
      str2 = localVersionID == null ? "none" : localVersionID.toString();
      str1 = str1 + "  detected FX version: " + str2 + "\n";
      str1 = str1 + "  need " + (this.useJfxToolkit ? "FX" : "AWT") + " toolkit\n";
      str1 = str1 + "  has " + (paramJVMInstance.isJfxToolkit() ? "FX" : "AWT") + " toolkit\n";
      System.out.println(str1);
    }
    return bool;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.main.server.ClientJVMSelectionParameters
 * JD-Core Version:    0.6.2
 */