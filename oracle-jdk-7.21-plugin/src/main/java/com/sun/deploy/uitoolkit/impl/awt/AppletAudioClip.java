package com.sun.deploy.uitoolkit.impl.awt;

import java.applet.AudioClip;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

class AppletAudioClip
  implements AudioClip
{
  private static Constructor acConstructor = null;
  private URL url = null;
  private AudioClip audioClip = null;
  boolean DEBUG = false;

  AppletAudioClip(URL paramURL)
  {
    this.url = paramURL;
    try
    {
      InputStream localInputStream = paramURL.openStream();
      createAppletAudioClip(localInputStream);
    }
    catch (IOException localIOException)
    {
      if (this.DEBUG)
        System.err.println("IOException creating AppletAudioClip" + localIOException);
    }
  }

  AppletAudioClip(byte[] paramArrayOfByte)
  {
    try
    {
      ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(paramArrayOfByte);
      createAppletAudioClip(localByteArrayInputStream);
    }
    catch (IOException localIOException)
    {
      if (this.DEBUG)
        System.err.println("IOException creating AppletAudioClip " + localIOException);
    }
  }

  void createAppletAudioClip(InputStream paramInputStream)
    throws IOException
  {
    if (acConstructor == null)
    {
      if (this.DEBUG)
        System.out.println("Initializing AudioClip constructor.");
      try
      {
        acConstructor = (Constructor)AccessController.doPrivileged(new PrivilegedExceptionAction()
        {
          public Object run()
            throws NoSuchMethodException, SecurityException, ClassNotFoundException
          {
            Class localClass = null;
            try
            {
              localClass = Class.forName("com.sun.media.sound.JavaSoundAudioClip", true, ClassLoader.getSystemClassLoader());
              if (AppletAudioClip.this.DEBUG)
                System.out.println("Loaded JavaSoundAudioClip");
            }
            catch (ClassNotFoundException localClassNotFoundException)
            {
              localClass = Class.forName("sun.audio.SunAudioClip", true, null);
              if (AppletAudioClip.this.DEBUG)
                System.out.println("Loaded SunAudioClip");
            }
            Class[] arrayOfClass = new Class[1];
            arrayOfClass[0] = Class.forName("java.io.InputStream");
            return localClass.getConstructor(arrayOfClass);
          }
        });
      }
      catch (PrivilegedActionException localPrivilegedActionException)
      {
        if (this.DEBUG)
          System.out.println("Got a PrivilegedActionException: " + localPrivilegedActionException.getException());
        throw new IOException("Failed to get AudioClip constructor: " + localPrivilegedActionException.getException());
      }
    }
    try
    {
      Object[] arrayOfObject = { paramInputStream };
      this.audioClip = ((AudioClip)acConstructor.newInstance(arrayOfObject));
    }
    catch (Exception localException)
    {
      throw new IOException("Failed to construct the AudioClip: " + localException);
    }
  }

  public synchronized void play()
  {
    if (this.audioClip != null)
      this.audioClip.play();
  }

  public synchronized void loop()
  {
    if (this.audioClip != null)
      this.audioClip.loop();
  }

  public synchronized void stop()
  {
    if (this.audioClip != null)
      this.audioClip.stop();
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     com.sun.deploy.uitoolkit.impl.awt.AppletAudioClip
 * JD-Core Version:    0.6.2
 */