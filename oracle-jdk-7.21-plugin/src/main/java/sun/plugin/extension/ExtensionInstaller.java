package sun.plugin.extension;

import java.io.IOException;
import sun.misc.ExtensionInstallationException;

public abstract interface ExtensionInstaller
{
  public abstract boolean install(String paramString1, String paramString2, String paramString3)
    throws ExtensionInstallationException, IOException, InterruptedException;
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.extension.ExtensionInstaller
 * JD-Core Version:    0.6.2
 */