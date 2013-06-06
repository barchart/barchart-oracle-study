package sun.plugin2.util;

import com.sun.applet2.AppletParameters;
import com.sun.deploy.config.Config;
import com.sun.deploy.config.JREInfo;
import com.sun.deploy.config.JfxRuntime;
import com.sun.deploy.config.Platform;
import com.sun.deploy.resources.ResourceManager;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.util.JVMParameters;
import com.sun.deploy.util.VersionID;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.misc.BASE64Decoder;
import sun.plugin2.main.server.ClientJVMSelectionParameters;
import sun.plugin2.main.server.JVMManager;
import sun.plugin2.os.windows.OSVERSIONINFOA;
import sun.plugin2.os.windows.Windows;

public class SystemUtil
{
  static boolean DEBUG = getenv("JPI_PLUGIN2_DEBUG") != null;
  static boolean VERBOSE = getenv("JPI_PLUGIN2_VERBOSE") != null;
  private static final String JAVA_EXT_DIRS = "-Djava.ext.dirs=";
  private static final String TRUSTED_DIR = File.separator + "lib" + File.separator + "trusted";
  private static String javaHome;
  private static volatile boolean getenvSupported = true;
  public static final int WINDOWS = 1;
  public static final int UNIX = 2;
  public static final int MACOSX = 3;
  private static int osType;
  private static boolean isVista;
  private static final String JPI_VM_OPTIONS = "_JPI_VM_OPTIONS";

  public static String getJavaHome()
  {
    if (javaHome == null)
      javaHome = getSystemProperty("java.home");
    return javaHome;
  }

  public static String getSystemProperty(String paramString)
  {
    return (String)AccessController.doPrivileged(new PrivilegedAction()
    {
      private final String val$property;

      public Object run()
      {
        return System.getProperty(this.val$property);
      }
    });
  }

  public static boolean debug(String paramString)
  {
    return getSystemProperty("sun.plugin2.debug." + paramString) != null;
  }

  public static String getenv(String paramString)
  {
    if (getenvSupported)
      try
      {
        return (String)AccessController.doPrivileged(new PrivilegedAction()
        {
          private final String val$variableName;

          public Object run()
          {
            return System.getenv(this.val$variableName);
          }
        });
      }
      catch (Error localError)
      {
        getenvSupported = false;
      }
    return null;
  }

  private static void computeIsVista()
  {
    OSVERSIONINFOA localOSVERSIONINFOA = OSVERSIONINFOA.create();
    localOSVERSIONINFOA.dwOSVersionInfoSize(OSVERSIONINFOA.size());
    if (Windows.GetVersionExA(localOSVERSIONINFOA))
      isVista = (localOSVERSIONINFOA.dwPlatformId() == 2) && (localOSVERSIONINFOA.dwMajorVersion() >= 6);
  }

  public static int getOSType()
  {
    if (osType == 0)
    {
      String str = getSystemProperty("os.name").toLowerCase();
      if (str.startsWith("windows"))
      {
        osType = 1;
        computeIsVista();
      }
      else if (str.indexOf("os x") != -1)
      {
        osType = 3;
      }
      else
      {
        osType = 2;
      }
    }
    return osType;
  }

  public static boolean isWindowsVista()
  {
    getOSType();
    return isVista;
  }

  public static String formatExecutableName(String paramString)
  {
    if (getOSType() == 1)
      return paramString + ".exe";
    return paramString;
  }

  public static boolean isDebug()
  {
    return DEBUG;
  }

  public static boolean isVerbose()
  {
    return VERBOSE;
  }

  public static byte[] decodeBase64(String paramString)
  {
    if (paramString == null)
      return null;
    BASE64Decoder localBASE64Decoder = new BASE64Decoder();
    try
    {
      return localBASE64Decoder.decodeBuffer(paramString);
    }
    catch (IOException localIOException)
    {
      Trace.ignoredException(localIOException);
    }
    return null;
  }

  private static String getVmArgs(JREInfo paramJREInfo)
  {
    String str = getenv("FORCED_CLIENTVM_ARGS");
    if ((DEBUG) && (str != null))
      System.out.println("JVMManager: using override from FORCED_CLIENTVM_ARGS" + str);
    if (str == null)
      str = getenv("_JPI_VM_OPTIONS");
    if (str != null)
      return str;
    return paramJREInfo.getVmArgs();
  }

  public static JVMParameters getDefaultVmArgs(JREInfo paramJREInfo)
  {
    JVMParameters localJVMParameters = new JVMParameters();
    localJVMParameters.parseTrustedOptions(getVmArgs(paramJREInfo));
    localJVMParameters.setDefault(true);
    return localJVMParameters;
  }

  public static JVMParameters extractAppletParamsToJVMParameters(AppletParameters paramAppletParameters, String paramString, boolean paramBoolean)
  {
    JVMParameters localJVMParameters = new JVMParameters();
    localJVMParameters.parseBootClassPath(JVMParameters.getPlugInDependentJars());
    localJVMParameters.addInternalArgument("-Djava.class.path=" + Config.getJREHome() + File.separator + "classes");
    localJVMParameters.setDefault(true);
    String str = (String)paramAppletParameters.get("java_arguments");
    if (str != null)
    {
      localJVMParameters.setHtmlJavaArgs(true);
      localJVMParameters.parse(str, !paramBoolean);
    }
    else
    {
      localJVMParameters.setHtmlJavaArgs(false);
    }
    if (getOSType() == 3)
      scopeJVMInstanceByHost(getURLHost(paramString), localJVMParameters);
    return localJVMParameters;
  }

  private static String getURLHost(String paramString)
  {
    if (paramString != null)
      try
      {
        URI localURI = new URI(paramString);
        return localURI.getHost();
      }
      catch (Exception localException)
      {
        Trace.ignored(localException);
      }
    return null;
  }

  public static JVMParameters prepareJVMParameter(JVMParameters paramJVMParameters1, JREInfo paramJREInfo, JVMParameters paramJVMParameters2, ClientJVMSelectionParameters paramClientJVMSelectionParameters)
  {
    JVMParameters localJVMParameters = new JVMParameters();
    VersionID localVersionID = paramClientJVMSelectionParameters.getJfxRequirement();
    int i = 0;
    JfxRuntime localJfxRuntime = null;
    if (null != localVersionID)
    {
      if ((DEBUG) && (VERBOSE))
        System.out.println("Need JavaFX version: " + localVersionID);
      if (!paramJREInfo.getProductVersion().isGreaterThanOrEqual(ClientJVMSelectionParameters.JFX_JRE_MINIMUM_VER))
      {
        if ((DEBUG) && (VERBOSE))
          System.out.println("JavaFX requires minimum JRE version: " + ClientJVMSelectionParameters.JFX_JRE_MINIMUM_VER);
        return null;
      }
      localJfxRuntime = paramJREInfo.getJfxRuntime();
      if (localJfxRuntime == null)
      {
        System.out.println("Launching in a vanilla JVM to install JavaFX and relaunch.");
      }
      else
      {
        if (!paramClientJVMSelectionParameters.useJfxToolkit())
          localJVMParameters.addInternalArgument("-Djnlp.tk=awt");
        else
          i = 1;
        localJVMParameters.addInternalArgument("-Djnlp.fx=" + localJfxRuntime.getProductVersion().toString());
      }
    }
    if ((DEBUG) && (VERBOSE))
      System.out.println("    JVMManager.createJVMInstance passing along JVM parameters from deployment.properties");
    localJVMParameters.addArguments(paramJVMParameters2);
    if ((DEBUG) && (VERBOSE))
      System.out.println("    JVMManager.createJVMInstance passing along JVM parameters from this applet instance");
    localJVMParameters.addArguments(paramJVMParameters1);
    localJVMParameters.setHtmlJavaArgs(paramJVMParameters1.isHtmlJavaArgs());
    maintainCurrentArchFlag(localJVMParameters, paramJREInfo);
    addJavaExtDirsOption(localJVMParameters);
    addXToolkitOption(localJVMParameters, paramJREInfo.getProductVersion());
    addUIElementOption(localJVMParameters);
    if (i == 0)
      localJVMParameters.addInternalArgument("-Dsun.awt.warmup=true");
    return localJVMParameters;
  }

  private static void scopeJVMInstanceByHost(String paramString, JVMParameters paramJVMParameters)
  {
    String str1;
    if (paramString != null)
    {
      paramJVMParameters.addInternalArgument("-Djava.applet.host=" + paramString);
      str1 = ResourceManager.getFormattedMessage("applet.host.app.title", new String[] { paramString });
    }
    else
    {
      str1 = ResourceManager.getMessage("applet.host.app.title.nohost");
    }
    String str2 = "-Xdock:name=" + str1;
    Charset localCharset = Charset.forName("UTF-8");
    CharsetDecoder localCharsetDecoder = localCharset.newDecoder();
    CharsetEncoder localCharsetEncoder = localCharset.newEncoder();
    String str3;
    try
    {
      ByteBuffer localByteBuffer = localCharsetEncoder.encode(CharBuffer.wrap(str2));
      CharBuffer localCharBuffer = localCharsetDecoder.decode(localByteBuffer);
      str3 = localCharBuffer.toString();
    }
    catch (CharacterCodingException localCharacterCodingException)
    {
      str3 = "-Xdock:name=Java Applet - " + paramString;
    }
    paramJVMParameters.addInternalArgument(str3);
    paramJVMParameters.addInternalArgument("-Xdock:icon=" + Platform.get().getDefaultIconPath());
  }

  private static void maintainCurrentArchFlag(JVMParameters paramJVMParameters, JREInfo paramJREInfo)
  {
    if ((getOSType() == 3) && (!paramJVMParameters.contains("-d32")) && (!paramJVMParameters.contains("-d64")))
      if (("x86_64".equals(paramJREInfo.getOSArch())) || ("amd64".equals(paramJREInfo.getOSArch())))
        paramJVMParameters.addInternalArgument("-d64");
      else
        paramJVMParameters.addInternalArgument("-d32");
  }

  private static void addXToolkitOption(JVMParameters paramJVMParameters, VersionID paramVersionID)
  {
    String str = System.getProperty("os.name").toLowerCase();
    if ((str.startsWith("sunos")) && (new VersionID("1.5*").match(paramVersionID)))
      paramJVMParameters.addInternalArgument("-Dawt.toolkit=sun.awt.X11.XToolkit");
  }

  private static void addUIElementOption(JVMParameters paramJVMParameters)
  {
    if (getOSType() == 3)
      paramJVMParameters.addInternalArgument("-Dapple.awt.UIElement=true");
  }

  private static void addJavaExtDirsOption(JVMParameters paramJVMParameters)
  {
    String str = getJavaExtDirsProp(paramJVMParameters);
    if (null != str)
      paramJVMParameters.addInternalArgument(str);
  }

  private static String getJavaExtDirsProp(JVMParameters paramJVMParameters)
  {
    if (paramJVMParameters.containsPrefix("-Djava.ext.dirs="))
      return null;
    StringBuffer localStringBuffer = new StringBuffer("-Djava.ext.dirs=");
    localStringBuffer.append(Config.getJREHome() + File.separator + "lib" + File.separator + "ext");
    String str1 = localStringBuffer.toString();
    int i = JVMManager.getBrowserType() == 3 ? 1 : 0;
    if (i != 0)
    {
      str2 = Platform.get().getBrowserHomePath() + File.separator + "jss";
      localFile = new File(str2);
      if (localFile.exists())
      {
        localStringBuffer.append(File.pathSeparator);
        localStringBuffer.append(str2);
      }
    }
    String str2 = Config.getSystemHome() + TRUSTED_DIR;
    File localFile = new File(str2);
    if ((getOSType() == 1) && (localFile.exists()))
    {
      localStringBuffer.append(File.pathSeparator);
      localStringBuffer.append(str2);
    }
    String str3 = localStringBuffer.toString();
    if (str3.equals(str1))
      return null;
    return str3;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.util.SystemUtil
 * JD-Core Version:    0.6.2
 */