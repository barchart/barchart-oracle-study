package com.sun.javaws.jnl;

import com.sun.deploy.Environment;
import com.sun.deploy.config.JREInfo;
import com.sun.deploy.config.Platform;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.trace.TraceLevel;
import com.sun.deploy.util.JVMParameters;
import com.sun.deploy.util.VersionID;
import com.sun.deploy.util.VersionString;
import java.io.File;
import java.net.URL;

public class DefaultMatchJRE
  implements MatchJREIf
{
  private static final boolean DEBUG = true;
  private JREDesc selectedJREDesc;
  private JREInfo selectedJREInfo;
  private boolean matchComplete;
  private boolean matchSecureComplete;
  private boolean matchVersion;
  private boolean matchFXVersion;
  private boolean matchJVMArgs;
  private boolean matchSecureJVMArgs;
  private long selectedMaxHeap;
  private long selectedInitHeap;
  private String selectedJVMArgString;
  private JVMParameters selectedJVMArgs;

  public DefaultMatchJRE()
  {
    reset(null);
  }

  public boolean hasBeenRun()
  {
    return null != this.selectedJVMArgs;
  }

  public void beginTraversal(LaunchDesc paramLaunchDesc)
  {
    reset(paramLaunchDesc);
    Trace.println("\tMatch: beginTraversal");
  }

  private void reset(LaunchDesc paramLaunchDesc)
  {
    this.matchComplete = false;
    this.matchSecureComplete = false;
    this.matchVersion = false;
    this.matchFXVersion = false;
    this.matchJVMArgs = false;
    this.matchSecureJVMArgs = false;
    this.selectedInitHeap = -1L;
    this.selectedJVMArgString = null;
    this.selectedJREDesc = null;
    this.selectedJREInfo = null;
    if (null == paramLaunchDesc)
    {
      this.selectedMaxHeap = -1L;
      this.selectedJVMArgs = null;
    }
    else
    {
      this.selectedMaxHeap = JVMParameters.getDefaultHeapSize();
      this.selectedJVMArgs = new JVMParameters();
    }
  }

  public JREInfo getSelectedJREInfo()
  {
    return this.selectedJREInfo;
  }

  public JREDesc getSelectedJREDesc()
  {
    return this.selectedJREDesc;
  }

  public JVMParameters getSelectedJVMParameters()
  {
    return this.selectedJVMArgs;
  }

  public String getSelectedJVMParameterString()
  {
    return this.selectedJVMArgString;
  }

  public long getSelectedInitHeapSize()
  {
    return this.selectedInitHeap;
  }

  public long getSelectedMaxHeapSize()
  {
    return this.selectedMaxHeap;
  }

  public boolean isRunningJVMSatisfying(boolean paramBoolean)
  {
    if (paramBoolean)
      return this.matchComplete;
    return this.matchSecureComplete;
  }

  public boolean isRunningJVMVersionSatisfying()
  {
    return (this.matchVersion) && (this.matchFXVersion);
  }

  public boolean isRunningJVMArgsSatisfying(boolean paramBoolean)
  {
    if (paramBoolean)
      return this.matchJVMArgs;
    return this.matchSecureJVMArgs;
  }

  public void digest(JREDesc paramJREDesc, JREInfo paramJREInfo)
  {
    Trace.println("Match: digest selected JREDesc: " + paramJREDesc + ", JREInfo: " + paramJREInfo);
    this.selectedJREDesc = paramJREDesc;
    this.selectedJREInfo = paramJREInfo;
    long l = paramJREDesc.getMaxHeap();
    if (l > this.selectedMaxHeap)
    {
      this.selectedMaxHeap = l;
      Trace.println("\tMatch: selecting maxHeap: " + l);
    }
    else
    {
      Trace.println("\tMatch: ignoring maxHeap: " + l);
    }
    l = paramJREDesc.getMinHeap();
    if (l > this.selectedInitHeap)
    {
      this.selectedInitHeap = l;
      Trace.println("\tMatch: selecting InitHeap: " + l);
    }
    else
    {
      Trace.println("\tMatch: ignoring InitHeap: " + l);
    }
    Trace.println("\tMatch: digesting vmargs: " + paramJREDesc.getVmArgs());
    this.selectedJVMArgs.parse(paramJREDesc.getVmArgs());
    Trace.println("\tMatch: digested vmargs: " + this.selectedJVMArgs);
    l = this.selectedJVMArgs.getMaxHeapSize();
    if (l > this.selectedMaxHeap)
    {
      this.selectedMaxHeap = l;
      Trace.println("\tMatch: selecting maxHeap(2): " + l);
    }
    this.selectedJVMArgs.setMaxHeapSize(JVMParameters.getDefaultHeapSize());
    Trace.println("\tMatch: JVM args after accumulation: " + this.selectedJVMArgs);
  }

  public void digest(LaunchDesc paramLaunchDesc)
  {
    ResourcesDesc localResourcesDesc = paramLaunchDesc.getResources();
    if (null != localResourcesDesc)
      this.selectedJVMArgs.addProperties(localResourcesDesc.getResourcePropertyList());
    Trace.println("\tMatch: digest LaunchDesc: " + paramLaunchDesc.getLocation());
    if (null != localResourcesDesc)
      Trace.println("\tMatch: digest properties: " + localResourcesDesc.getResourcePropertyList());
    else
      Trace.println("\tMatch: digest properties: ResourcesDesc null");
    Trace.println("\tMatch: JVM args: " + this.selectedJVMArgs);
  }

  public void endTraversal(LaunchDesc paramLaunchDesc)
  {
    Trace.println("\tMatch: endTraversal ..");
    if ((paramLaunchDesc.isApplicationDescriptor()) && (null == this.selectedJREDesc))
      throw new IllegalArgumentException("selectedJREDesc null");
    if ((this.selectedInitHeap > 0L) && (this.selectedInitHeap != JVMParameters.getDefaultHeapSize()))
      this.selectedJVMArgs.parse("-Xms" + JVMParameters.unparseMemorySpec(this.selectedInitHeap));
    this.selectedJVMArgs.setMaxHeapSize(this.selectedMaxHeap);
    this.selectedJVMArgString = this.selectedJVMArgs.getCommandLineArgumentsAsString(false);
    Trace.println("\tMatch: JVM args final: " + this.selectedJVMArgString);
    if (this.selectedJREInfo == null)
      return;
    this.matchVersion = isVersionMatch(paramLaunchDesc, this.selectedJREInfo);
    this.matchFXVersion = isFXVersionMatch(paramLaunchDesc, this.selectedJREInfo);
    if (!this.matchFXVersion)
    {
      this.selectedJREInfo = null;
      return;
    }
    JVMParameters localJVMParameters = JVMParameters.getRunningJVMParameters();
    if (localJVMParameters == null)
    {
      if (Trace.isEnabled(TraceLevel.BASIC))
        Trace.println("\t Match: Running JVM is not set: want:<" + this.selectedJVMArgs.getCommandLineArgumentsAsString(false) + ">", TraceLevel.BASIC);
      this.matchJVMArgs = false;
      this.matchSecureJVMArgs = false;
    }
    else if (localJVMParameters.satisfies(this.selectedJVMArgs))
    {
      Trace.println("\t Match: Running JVM args match: have:<" + localJVMParameters.getCommandLineArgumentsAsString(false) + ">  satisfy want:<" + this.selectedJVMArgs.getCommandLineArgumentsAsString(false) + ">");
      this.matchJVMArgs = true;
      this.matchSecureJVMArgs = true;
    }
    else if (localJVMParameters.satisfiesSecure(this.selectedJVMArgs))
    {
      Trace.println("\t Match: Running JVM args match the secure subset: have:<" + localJVMParameters.getCommandLineArgumentsAsString(false) + ">  satisfy want:<" + this.selectedJVMArgs.getCommandLineArgumentsAsString(false) + ">");
      this.matchJVMArgs = false;
      this.matchSecureJVMArgs = true;
    }
    else
    {
      Trace.println("\t Match: Running JVM args mismatch: have:<" + localJVMParameters.getCommandLineArgumentsAsString(false) + "> !satisfy want:<" + this.selectedJVMArgs.getCommandLineArgumentsAsString(false) + ">");
      this.matchJVMArgs = false;
      this.matchSecureJVMArgs = false;
    }
    this.matchComplete = ((this.matchVersion) && (this.matchJVMArgs) && (this.matchFXVersion));
    this.matchSecureComplete = ((this.matchVersion) && (this.matchSecureJVMArgs) && (this.matchFXVersion));
  }

  public static boolean isInstallJRE(JREInfo paramJREInfo)
  {
    File localFile1 = new File(Environment.getJavaHome());
    File localFile2 = new File(paramJREInfo.getPath());
    File localFile3 = localFile2.getParentFile();
    File localFile4 = new File(localFile1, "lib" + File.separator + "rt.jar");
    if (!localFile4.exists())
      return true;
    return Platform.get().samePaths(localFile1.getPath(), localFile3.getParentFile().getPath());
  }

  public static boolean isPlatformMatch(JREInfo paramJREInfo, VersionString paramVersionString)
  {
    String str = paramJREInfo.getProduct();
    int i;
    if ((str == null) || (isInstallJRE(paramJREInfo)) || (str.indexOf('-') == -1) || (str.indexOf("-rev") != -1) || (str.indexOf("-er") != -1))
      i = 1;
    else
      i = 0;
    if (new File(paramJREInfo.getPath()).exists())
      return (paramVersionString.contains(paramJREInfo.getPlatform())) && (i != 0);
    return false;
  }

  public static boolean isProductMatch(JREInfo paramJREInfo, URL paramURL, VersionString paramVersionString)
  {
    if (new File(paramJREInfo.getPath()).exists())
      return (paramJREInfo.getLocation().equals(paramURL.toString())) && (paramVersionString.contains(paramJREInfo.getProduct()));
    return false;
  }

  public boolean isVersionMatch(JREInfo paramJREInfo, VersionString paramVersionString, URL paramURL)
  {
    return paramURL == null ? isPlatformMatch(paramJREInfo, paramVersionString) : isProductMatch(paramJREInfo, paramURL, paramVersionString);
  }

  public static boolean isFXVersionMatch(String paramString, VersionString paramVersionString)
  {
    if (paramVersionString == null)
      return false;
    String str1 = paramVersionString.toString().trim();
    if (str1.endsWith("+"))
      return paramVersionString.contains(paramString);
    String str2 = str1;
    int i = str1.indexOf(".");
    if (i != -1)
      str2 = str2.substring(0, i);
    str2 = str2 + "*";
    String str3 = str1;
    if (str3.endsWith("*"))
      str3 = str3.substring(0, str3.length() - 1);
    str3 = str3 + "+";
    VersionString localVersionString1 = new VersionString(str2);
    VersionString localVersionString2 = new VersionString(str3);
    return (localVersionString1.contains(paramString)) && (localVersionString2.contains(paramString));
  }

  public boolean isFXVersionMatch(JREInfo paramJREInfo, VersionString paramVersionString)
  {
    if ((paramVersionString == null) || (paramVersionString.toString().equals("")))
      return true;
    return (paramJREInfo.getFXVersion() != null) && (isFXVersionMatch(paramJREInfo.getFXVersion().toString(), paramVersionString));
  }

  public boolean isFXVersionMatch(LaunchDesc paramLaunchDesc, JREInfo paramJREInfo)
  {
    JavaFXRuntimeDesc localJavaFXRuntimeDesc = paramLaunchDesc.getJavaFXRuntimeDescriptor();
    if (localJavaFXRuntimeDesc == null)
      return true;
    VersionString localVersionString = new VersionString(localJavaFXRuntimeDesc.getVersion());
    return isFXVersionMatch(paramJREInfo.getFXVersion().toString(), localVersionString);
  }

  public boolean isVersionMatch(LaunchDesc paramLaunchDesc, JREInfo paramJREInfo)
  {
    JREInfo localJREInfo = paramLaunchDesc.getHomeJRE();
    if (!localJREInfo.getProductVersion().match(paramJREInfo.getProductVersion()))
    {
      Trace.println("\tMatch: Running JREInfo Version mismatches: " + localJREInfo.getProductVersion() + " != " + paramJREInfo.getProductVersion());
      return false;
    }
    Trace.println("\tMatch: Running JREInfo Version    match: " + localJREInfo.getProductVersion() + " == " + paramJREInfo.getProductVersion());
    return true;
  }

  public String toString()
  {
    return "DefaultMatchJRE: \n  JREDesc:    " + getSelectedJREDesc() + "\n  JREInfo:    " + getSelectedJREInfo() + "\n  Init Heap:  " + getSelectedInitHeapSize() + "\n  Max  Heap:  " + getSelectedMaxHeapSize() + "\n  Satisfying: " + isRunningJVMSatisfying(true) + ", " + isRunningJVMSatisfying(false) + "\n  SatisfyingVersion: " + isRunningJVMVersionSatisfying() + "\n  SatisfyingJVMArgs: " + isRunningJVMArgsSatisfying(true) + ", " + isRunningJVMSatisfying(false) + "\n  SatisfyingSecure: " + isRunningJVMSatisfying(true) + "\n  Selected JVMParam: " + getSelectedJVMParameters() + "\n  Running  JVMParam: " + JVMParameters.getRunningJVMParameters();
  }
}

/* Location:           /home/user1/Temp/jvm/javaws.jar
 * Qualified Name:     com.sun.javaws.jnl.DefaultMatchJRE
 * JD-Core Version:    0.6.2
 */