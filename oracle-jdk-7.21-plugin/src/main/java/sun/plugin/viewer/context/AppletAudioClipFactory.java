package sun.plugin.viewer.context;

import java.applet.AudioClip;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.plugin.security.PluginClassLoader;

final class AppletAudioClipFactory
{
  static AudioClip createAudioClip(URL paramURL)
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
          if ((localClassLoader != null) && ((localClassLoader instanceof PluginClassLoader)))
          {
            PluginClassLoader localPluginClassLoader = (PluginClassLoader)localClassLoader;
            String str1 = localPluginClassLoader.getBaseURL().toString();
            String str2 = this.val$url.toString();
            int i = str2.indexOf(str1);
            if (i == 0)
            {
              String str3;
              if (str2.charAt(str1.length()) == '/')
                str3 = str2.substring(str1.length() + 1);
              else
                str3 = str2.substring(str1.length());
              InputStream localInputStream = localPluginClassLoader.getResourceAsStream(str3);
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
 * Qualified Name:     sun.plugin.viewer.context.AppletAudioClipFactory
 * JD-Core Version:    0.6.2
 */