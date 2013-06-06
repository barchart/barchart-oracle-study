package sun.plugin2.main.client;

import com.sun.deploy.trace.Trace;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import sun.plugin2.applet.Applet2ExecutionContext;
import sun.plugin2.applet.Applet2Manager;

public class PluginProxySelector extends ProxySelector
{
  private static HashMap proxyCache = new HashMap();

  public static void initialize()
  {
    proxyCache.clear();
    ProxySelector.setDefault(new PluginProxySelector());
  }

  public List select(URI paramURI)
  {
    if (paramURI == null)
      throw new IllegalArgumentException();
    List localList1 = null;
    try
    {
      String str1 = paramURI.getScheme();
      boolean bool = (str1.equalsIgnoreCase("socket")) || (str1.equalsIgnoreCase("serversocket"));
      URL localURL = getURLFromURI(paramURI, bool);
      String str2 = buildProxyKey(localURL);
      synchronized (this)
      {
        List localList2 = (List)proxyCache.get(str2);
        if (localList2 == null)
        {
          Applet2ExecutionContext localApplet2ExecutionContext = Applet2Manager.getCurrentAppletExecutionContext();
          localList2 = localApplet2ExecutionContext.getProxyList(localURL, bool);
          proxyCache.put(str2, localList2);
        }
        localList1 = (List)((ArrayList)localList2).clone();
      }
      if (localList1.size() > 0)
        Trace.msgNetPrintln("net.proxy.connect", new Object[] { localURL, localList1.get(0) });
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
    }
    return localList1;
  }

  public void connectFailed(URI paramURI, SocketAddress paramSocketAddress, IOException paramIOException)
  {
    if ((paramURI == null) || (paramSocketAddress == null) || (paramIOException == null))
      throw new IllegalArgumentException("Arguments can't be null.");
    Trace.msgNetPrintln("net.proxy.connectionFailure", new Object[] { paramURI.toString() + ", " + paramSocketAddress.toString() + paramIOException.toString() });
    try
    {
      removeProxyFromCache(paramURI.toURL(), paramSocketAddress.toString());
    }
    catch (Exception localException)
    {
      Trace.securityPrintException(localException);
    }
  }

  private static URL getURLFromURI(URI paramURI, boolean paramBoolean)
  {
    if (paramURI == null)
      return null;
    Object localObject1 = paramURI.getHost();
    int i = paramURI.getPort();
    if (localObject1 == null)
    {
      localObject2 = paramURI.getAuthority();
      if (localObject2 != null)
      {
        int j = ((String)localObject2).indexOf('@');
        if (j >= 0)
          localObject2 = ((String)localObject2).substring(j + 1);
        j = ((String)localObject2).lastIndexOf(':');
        if (j >= 0)
        {
          try
          {
            i = Integer.parseInt(((String)localObject2).substring(j + 1));
          }
          catch (NumberFormatException localNumberFormatException)
          {
            i = -1;
          }
          localObject2 = ((String)localObject2).substring(0, j);
        }
        localObject1 = localObject2;
      }
    }
    Object localObject2 = null;
    try
    {
      if (paramBoolean)
      {
        if (i == -1)
          localObject2 = new URL("http://" + (String)localObject1 + "/");
        else
          localObject2 = new URL("http://" + (String)localObject1 + ":" + i + "/");
      }
      else
        localObject2 = paramURI.toURL();
    }
    catch (MalformedURLException localMalformedURLException)
    {
      localMalformedURLException.printStackTrace();
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      localIllegalArgumentException.printStackTrace();
    }
    return localObject2;
  }

  private static String buildProxyKey(URL paramURL)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append(paramURL.getProtocol());
    localStringBuffer.append(paramURL.getHost());
    localStringBuffer.append(paramURL.getPort());
    return localStringBuffer.toString();
  }

  private synchronized void removeProxyFromCache(URL paramURL, String paramString)
  {
    String str = buildProxyKey(paramURL);
    if (proxyCache.containsKey(str))
    {
      List localList = (List)proxyCache.get(str);
      ArrayList localArrayList = new ArrayList();
      ListIterator localListIterator = localList.listIterator();
      while (localListIterator.hasNext())
      {
        Proxy localProxy = (Proxy)localListIterator.next();
        InetSocketAddress localInetSocketAddress = (InetSocketAddress)localProxy.address();
        if ((localInetSocketAddress != null) && (paramString.contains(localInetSocketAddress.getHostName())))
          localArrayList.add(localProxy);
      }
      localListIterator = localArrayList.listIterator();
      while (localListIterator.hasNext())
        localList.remove(localListIterator.next());
      if (localList.size() == 0)
        proxyCache.remove(str);
    }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.main.client.PluginProxySelector
 * JD-Core Version:    0.6.2
 */