package com.sun.deploy.uitoolkit.impl.awt;

import java.applet.AudioClip;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.plugin2.applet.Applet2ClassLoader;

public final class Applet2AudioClipFactory
{
  public static AudioClip createAudioClip(URL paramURL)
  {
    AudioClip localAudioClip = (AudioClip)AccessController.doPrivileged(new PrivilegedAction()
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
            Applet2ClassLoader localApplet2ClassLoader = (Applet2ClassLoader)localClassLoader;
            String str1 = localApplet2ClassLoader.getBaseURL().toString();
            String str2 = this.val$url.toString();
            int i = str2.indexOf(str1);
            if (i == 0)
            {
              String str3;
              if (str2.charAt(str1.length()) == '/')
                str3 = str2.substring(str1.length() + 1);
              else
                str3 = str2.substring(str1.length());
              InputStream localInputStream = localApplet2ClassLoader.getResourceAsStream(str3);
              if (localInputStream != null)
              {
                BufferedInputStream localBufferedInputStream = new BufferedInputStream(localInputStream);
                ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
                byte[] arrayOfByte1 = new byte[8192];
                int j = 0;
                while ((j = localBufferedInputStream.read(arrayOfByte1, 0, 8192)) != -1)
                  localByteArrayOutputStream.write(arrayOfByte1, 0, j);
                localBufferedInputStream.close();
                byte[] arrayOfByte2 = localByteArrayOutputStream.toByteArray();
                if ((arrayOfByte2 != null) && (arrayOfByte2.length > 0))
                  return new AppletAudioClip(arrayOfByte2);
              }
            }
          }
          return new AppletAudioClip(this.val$url);
        }
        catch (Exception localException)
        {
          localException.printStackTrace();
        }
        return null;
      }
    });
    if (localAudioClip != null)
      return new PluginAudioClip(localAudioClip);
    return null;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     com.sun.deploy.uitoolkit.impl.awt.Applet2AudioClipFactory
 * JD-Core Version:    0.6.2
 */