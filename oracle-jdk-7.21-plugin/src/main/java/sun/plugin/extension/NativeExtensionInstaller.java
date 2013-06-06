package sun.plugin.extension;

import com.sun.deploy.resources.ResourceManager;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.uitoolkit.ToolkitStore;
import com.sun.deploy.uitoolkit.ui.UIFactory;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class NativeExtensionInstaller
  implements ExtensionInstaller
{
  public boolean install(String paramString1, String paramString2, String paramString3)
    throws IOException, InterruptedException
  {
    Trace.msgExtPrintln("optpkg.install.native.launch");
    String str1 = null;
    try
    {
      JarFile localJarFile = new JarFile(paramString2);
      Manifest localManifest = localJarFile.getManifest();
      Attributes localAttributes = localManifest.getMainAttributes();
      String str2 = localAttributes.getValue(Attributes.Name.EXTENSION_INSTALLATION);
      if (str2 != null)
        str2 = str2.trim();
      InputStream localInputStream = localJarFile.getInputStream(localJarFile.getEntry(str2));
      BufferedInputStream localBufferedInputStream = new BufferedInputStream(localInputStream);
      str1 = ExtensionUtils.getTempDir() + File.separator + str2;
      FileOutputStream localFileOutputStream = new FileOutputStream(str1);
      BufferedOutputStream localBufferedOutputStream = new BufferedOutputStream(localFileOutputStream);
      ExtensionUtils.copy(localBufferedInputStream, localBufferedOutputStream);
      localBufferedInputStream.close();
      localInputStream.close();
      localBufferedOutputStream.close();
      localFileOutputStream.close();
      String str3 = System.getProperty("os.name");
      String str4 = System.getProperty("java.home");
      if (str3.indexOf("Windows") == -1)
      {
        localProcess1 = Runtime.getRuntime().exec("chmod 755 " + str1);
        localProcess1.waitFor();
        try
        {
          File localFile1 = new File(str4);
          Process localProcess2 = Runtime.getRuntime().exec(str1, null, localFile1);
          int i = localProcess2.waitFor();
          if (i != 0)
          {
            Trace.msgExtPrintln("optpkg.install.native.launch.fail.0", new Object[] { str1 });
            boolean bool3 = false;
            return bool3;
          }
        }
        catch (SecurityException localSecurityException)
        {
          Trace.msgExtPrintln("optpkg.install.native.launch.fail.1", new Object[] { str4 });
          Trace.securityPrintException(localSecurityException);
        }
        finally
        {
          if (str1 != null)
          {
            File localFile2 = new File(str1);
            if (localFile2.exists())
              localFile2.delete();
          }
        }
        boolean bool2 = false;
        return bool2;
      }
      Process localProcess1 = Runtime.getRuntime().exec(str1);
      ToolkitStore.getUI();
      ToolkitStore.getUI().showMessageDialog(null, null, 1, ResourceManager.getMessage("optpkg.installer.launch.caption"), null, ResourceManager.getMessage("optpkg.installer.launch.wait"), null, null, null, null);
      boolean bool1 = true;
      return bool1;
    }
    finally
    {
      if (str1 != null)
      {
        File localFile3 = new File(str1);
        if (localFile3.exists())
          localFile3.delete();
      }
    }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.extension.NativeExtensionInstaller
 * JD-Core Version:    0.6.2
 */