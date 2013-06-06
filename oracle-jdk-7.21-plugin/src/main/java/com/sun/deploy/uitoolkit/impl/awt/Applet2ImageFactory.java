package com.sun.deploy.uitoolkit.impl.awt;

import com.sun.deploy.trace.Trace;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.plugin2.applet.Applet2ClassLoader;

public final class Applet2ImageFactory
{
  public static Image createImage(URL paramURL)
  {
    Image localImage = (Image)AccessController.doPrivileged(new PrivilegedAction()
    {
      private final URL val$url;

      public Object run()
      {
        try
        {
          Thread localThread = Thread.currentThread();
          ClassLoader localClassLoader = localThread.getContextClassLoader();
          if ((localClassLoader != null) && ((localClassLoader instanceof Applet2ClassLoader)))
          {
            localObject = (Applet2ClassLoader)localClassLoader;
            String str1 = ((Applet2ClassLoader)localObject).getBaseURL().toString();
            String str2 = this.val$url.toString();
            int i = str2.indexOf(str1);
            if (i == 0)
            {
              String str3;
              if (str2.length() > str1.length())
              {
                if (str2.charAt(str1.length()) == '/')
                  str3 = str2.substring(str1.length() + 1);
                else
                  str3 = str2.substring(str1.length());
              }
              else
                return null;
              InputStream localInputStream = ((Applet2ClassLoader)localObject).getResourceAsStreamFromJar(str3);
              if (localInputStream != null)
                return Applet2ImageFactory.getImage(localInputStream);
            }
          }
          Object localObject = Toolkit.getDefaultToolkit().createImage(this.val$url);
          return localObject;
        }
        catch (FileNotFoundException localFileNotFoundException)
        {
          return Toolkit.getDefaultToolkit().createImage(new byte[0]);
        }
        catch (Exception localException)
        {
          Trace.ignoredException(localException);
        }
        return null;
      }
    });
    return localImage;
  }

  private static Image getImage(InputStream paramInputStream)
    throws Exception
  {
    BufferedInputStream localBufferedInputStream = new BufferedInputStream(paramInputStream);
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    byte[] arrayOfByte1 = new byte[8192];
    int i = 0;
    while ((i = localBufferedInputStream.read(arrayOfByte1, 0, 8192)) != -1)
      localByteArrayOutputStream.write(arrayOfByte1, 0, i);
    localBufferedInputStream.close();
    byte[] arrayOfByte2 = localByteArrayOutputStream.toByteArray();
    if ((arrayOfByte2 != null) && (arrayOfByte2.length > 0))
      return Toolkit.getDefaultToolkit().createImage(arrayOfByte2);
    return null;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     com.sun.deploy.uitoolkit.impl.awt.Applet2ImageFactory
 * JD-Core Version:    0.6.2
 */