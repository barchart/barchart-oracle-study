package sun.plugin;

import com.sun.deploy.util.URLUtil;
import java.applet.Applet;
import java.beans.Beans;
import java.beans.XMLDecoder;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.JComponent;
import javax.swing.LayoutFocusTraversalPolicy;
import sun.applet.AppletClassLoader;

public class BeansViewer extends AppletViewer
{
  public static final int APPLET_DISPOSE = 0;
  public static final int APPLET_ERROR = 7;
  private byte[] bytes = null;

  public static Applet createJavaBeanComponent(AppletViewer paramAppletViewer, AppletClassLoader paramAppletClassLoader)
    throws ClassNotFoundException, IllegalAccessException, IOException, InstantiationException, InterruptedException
  {
    String str1 = paramAppletViewer.getSerializedObject();
    String str2 = paramAppletViewer.getCode();
    if ((str2 != null) && (str1 != null))
    {
      System.err.println(AppletViewer.getMessage("bean_code_and_ser"));
      return null;
    }
    Object localObject1;
    if ((str2 == null) && (str1 == null))
    {
      localObject1 = "nocode";
      paramAppletViewer.setStatus(7);
      paramAppletViewer.showAppletStatus((String)localObject1);
      paramAppletViewer.showAppletLog((String)localObject1);
      paramAppletViewer.repaint();
    }
    Applet localApplet;
    if (str2 != null)
    {
      localObject1 = Beans.instantiate(paramAppletClassLoader, str2);
      localApplet = createApplet(localObject1);
      paramAppletViewer.setDoInit(true);
    }
    else
    {
      localObject1 = Beans.instantiate(paramAppletClassLoader, str1);
      localApplet = createApplet(localObject1);
      paramAppletViewer.setDoInit(false);
    }
    if (Thread.interrupted())
    {
      try
      {
        paramAppletViewer.setStatus(0);
        localApplet = null;
        paramAppletViewer.showAppletStatus("death");
      }
      finally
      {
        Thread.currentThread().interrupt();
      }
      return null;
    }
    return localApplet;
  }

  protected Applet createApplet(AppletClassLoader paramAppletClassLoader)
    throws ClassNotFoundException, IllegalAccessException, IOException, InstantiationException, InterruptedException
  {
    if (this.bytes == null)
      return createJavaBeanComponent(this, paramAppletClassLoader);
    ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(this.bytes);
    XMLDecoder localXMLDecoder = new XMLDecoder(localByteArrayInputStream);
    Object localObject = localXMLDecoder.readObject();
    Applet localApplet = createApplet(localObject);
    localByteArrayInputStream.close();
    setDoInit(true);
    return localApplet;
  }

  protected String getHandledType()
  {
    return "JavaBeans";
  }

  public URL getCodeBase()
  {
    if (!this.codeBaseInit)
    {
      String str = getParameter("java_codebase");
      if (str == null)
        str = getParameter("codebase");
      if (str != null)
      {
        if ((!str.equals(".")) && (!str.endsWith("/")))
          str = str + "/";
        try
        {
          this.baseURL = new URL(URLUtil.canonicalize(str));
        }
        catch (MalformedURLException localMalformedURLException)
        {
        }
      }
      this.codeBaseInit = true;
    }
    return this.baseURL;
  }

  public URL getDocumentBase()
  {
    return null;
  }

  public void setByteStream(byte[] paramArrayOfByte)
  {
    this.bytes = paramArrayOfByte;
  }

  static Applet createApplet(Object paramObject)
  {
    BeansApplet localBeansApplet = new BeansApplet(paramObject);
    if ((paramObject instanceof JComponent))
    {
      localBeansApplet.setFocusTraversalPolicy(new LayoutFocusTraversalPolicy());
      localBeansApplet.setFocusTraversalPolicyProvider(true);
    }
    return localBeansApplet;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.BeansViewer
 * JD-Core Version:    0.6.2
 */