package sun.plugin.util;

import com.sun.deploy.config.Config;
import com.sun.deploy.config.DefaultConfig;
import com.sun.deploy.config.PluginClientConfig;
import com.sun.deploy.model.ResourceProvider;
import com.sun.deploy.trace.Trace;
import java.io.File;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;

public class UserProfile
{
  public static String getPropertyFile()
  {
    return Config.getUserHome() + File.separator + Config.getPropertiesFilename();
  }

  public static String getLogDirectory()
  {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("javaplugin.outputfiles.path"));
    if ((str == null) || (str.trim().equals("")))
      str = Config.getLogDirectory();
    return str;
  }

  public static String getTempDirectory()
  {
    return ResourceProvider.get().getCacheDir().getPath() + File.separator + "tmp";
  }

  public static String getSecurityDirectory()
  {
    return Config.getUserHome() + File.separator + "security";
  }

  static
  {
    try
    {
      if ((Config.get() instanceof DefaultConfig))
        Config.setInstance(new PluginClientConfig());
      new File(Config.getUserHome()).mkdirs();
      new File(Config.getSystemHome()).mkdirs();
      new File(getLogDirectory()).mkdirs();
      new File(getSecurityDirectory()).mkdirs();
      new File(Config.getUserExtensionDirectory()).mkdirs();
      new File(getTempDirectory()).mkdirs();
    }
    catch (Throwable localThrowable)
    {
      Trace.ignored(localThrowable);
    }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.util.UserProfile
 * JD-Core Version:    0.6.2
 */