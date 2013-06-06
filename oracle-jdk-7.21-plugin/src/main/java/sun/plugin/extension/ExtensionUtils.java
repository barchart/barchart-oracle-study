package sun.plugin.extension;

import com.sun.deploy.trace.Trace;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.plugin.util.UserProfile;
import sun.security.action.GetPropertyAction;

public class ExtensionUtils
{
  private static final String _tempDir;

  static String getTempDir()
  {
    return _tempDir;
  }

  static String extractJarFileName(String paramString)
  {
    int j = paramString.indexOf('#');
    int i;
    if (j == -1)
    {
      i = paramString.lastIndexOf('/');
      if (i < paramString.lastIndexOf('\\'))
        i = paramString.lastIndexOf('\\');
    }
    else
    {
      i = paramString.lastIndexOf('/', j);
      if (i < paramString.lastIndexOf('\\', j));
      i = paramString.lastIndexOf('\\', j);
    }
    if (i == -1)
      return null;
    return paramString.substring(i + 1);
  }

  static void copy(InputStream paramInputStream, OutputStream paramOutputStream)
    throws IOException
  {
    byte[] arrayOfByte = new byte[10240];
    int i;
    do
    {
      i = paramInputStream.read(arrayOfByte);
      if (i != -1)
        paramOutputStream.write(arrayOfByte, 0, i);
    }
    while (i != -1);
    paramInputStream.close();
    paramOutputStream.close();
  }

  static String makePlatformDependent(String paramString)
  {
    String str = makePlatformDependentOsName0(makePlatformDependentOsName1(paramString));
    str = makePlatformDependentName("os.arch", str);
    str = makePlatformDependentName("os.version", str);
    return str;
  }

  static String makePlatformDependentOsName0(String paramString)
  {
    String str1 = "$(os-name)$";
    int i = paramString.indexOf(str1);
    String str2 = paramString;
    if (i != -1)
    {
      String str3 = System.getProperty("os.name");
      String str4 = str3.replace(' ', '-');
      str2 = paramString.substring(0, i) + str4 + paramString.substring(i + str1.length(), paramString.length());
    }
    return str2;
  }

  static String makePlatformDependentOsName1(String paramString)
  {
    String str1 = "$(os.name)$";
    int i = paramString.indexOf(str1);
    String str2 = paramString;
    if (i != -1)
    {
      String str3 = System.getProperty("os.name");
      String str4 = str3.replace(' ', '-');
      str2 = paramString.substring(0, i) + str4 + paramString.substring(i + str1.length(), paramString.length());
    }
    return str2;
  }

  static String makePlatformDependentName(String paramString1, String paramString2)
  {
    String str1 = "$(" + paramString1 + ")$";
    int i = paramString2.indexOf(str1);
    String str2 = paramString2;
    if (i != -1)
    {
      String str3 = System.getProperty(paramString1);
      String str4 = str3.replace(' ', '-');
      str2 = paramString2.substring(0, i) + str4 + paramString2.substring(i + str1.length(), paramString2.length());
    }
    return str2;
  }

  static
  {
    String str1 = (String)AccessController.doPrivileged(new GetPropertyAction("user.home"));
    String str2 = (String)AccessController.doPrivileged(new GetPropertyAction("javaplugin.version"));
    _tempDir = UserProfile.getTempDirectory();
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        try
        {
          File localFile = new File(ExtensionUtils._tempDir);
          localFile.mkdirs();
        }
        catch (Throwable localThrowable)
        {
          Trace.extPrintException(localThrowable);
        }
        return null;
      }
    });
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.extension.ExtensionUtils
 * JD-Core Version:    0.6.2
 */