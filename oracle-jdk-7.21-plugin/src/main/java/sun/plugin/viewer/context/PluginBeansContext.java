package sun.plugin.viewer.context;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import netscape.javascript.JSObject;
import sun.applet.AppletPanel;

public class PluginBeansContext
  implements PluginAppletContext
{
  private PluginAppletContext ac = null;

  public void setPluginAppletContext(PluginAppletContext paramPluginAppletContext)
  {
    this.ac = paramPluginAppletContext;
  }

  public PluginAppletContext getPluginAppletContext()
  {
    return this.ac;
  }

  public AudioClip getAudioClip(URL paramURL)
  {
    return this.ac.getAudioClip(paramURL);
  }

  public Image getImage(URL paramURL)
  {
    return this.ac.getImage(paramURL);
  }

  public Applet getApplet(String paramString)
  {
    return null;
  }

  public Enumeration getApplets()
  {
    return null;
  }

  public void showDocument(URL paramURL)
  {
    this.ac.showDocument(paramURL);
  }

  public void showDocument(URL paramURL, String paramString)
  {
    this.ac.showDocument(paramURL, paramString);
  }

  public void showStatus(String paramString)
  {
    this.ac.showStatus(paramString);
  }

  public void setStream(String paramString, InputStream paramInputStream)
    throws IOException
  {
    this.ac.setStream(paramString, paramInputStream);
  }

  public InputStream getStream(String paramString)
  {
    return this.ac.getStream(paramString);
  }

  public Iterator getStreamKeys()
  {
    return this.ac.getStreamKeys();
  }

  public JSObject getJSObject()
  {
    return this.ac.getJSObject();
  }

  public JSObject getOneWayJSObject()
  {
    return null;
  }

  public void addAppletPanelInContext(AppletPanel paramAppletPanel)
  {
    this.ac.addAppletPanelInContext(paramAppletPanel);
  }

  public void removeAppletPanelFromContext(AppletPanel paramAppletPanel)
  {
    this.ac.removeAppletPanelFromContext(paramAppletPanel);
  }

  public void setAppletContextHandle(int paramInt)
  {
    this.ac.setAppletContextHandle(paramInt);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.viewer.context.PluginBeansContext
 * JD-Core Version:    0.6.2
 */