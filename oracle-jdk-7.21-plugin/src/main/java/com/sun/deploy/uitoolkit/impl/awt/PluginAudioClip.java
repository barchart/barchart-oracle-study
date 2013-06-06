package com.sun.deploy.uitoolkit.impl.awt;

import java.applet.AudioClip;

class PluginAudioClip
  implements AudioClip
{
  private AudioClip clip = null;

  public PluginAudioClip(AudioClip paramAudioClip)
  {
    this.clip = paramAudioClip;
  }

  public void loop()
  {
    if (this.clip != null)
      this.clip.loop();
  }

  public void play()
  {
    if (this.clip != null)
      this.clip.play();
  }

  public void stop()
  {
    if (this.clip != null)
      this.clip.stop();
  }

  public void finalize()
  {
    stop();
    this.clip = null;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     com.sun.deploy.uitoolkit.impl.awt.PluginAudioClip
 * JD-Core Version:    0.6.2
 */