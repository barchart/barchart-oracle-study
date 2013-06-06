package sun.plugin.net.proxy;

import com.sun.deploy.net.proxy.AbstractAutoProxyHandler;
import com.sun.deploy.net.proxy.ProxyInfo;
import com.sun.deploy.net.proxy.ProxyUnavailableException;
import com.sun.deploy.services.ServiceManager;
import com.sun.deploy.trace.Trace;
import java.net.URL;
import netscape.javascript.JSObject;
import sun.applet.AppletPanel;
import sun.plugin.services.BrowserService;
import sun.plugin.viewer.AppletPanelCache;
import sun.plugin.viewer.context.PluginAppletContext;

public final class PluginAutoProxyHandler extends AbstractAutoProxyHandler
{
  protected String getBrowserSpecificAutoProxy()
  {
    BrowserService localBrowserService = (BrowserService)ServiceManager.getService();
    if (localBrowserService.isIExplorer())
      return super.getBrowserSpecificAutoProxy();
    return "function isInNet(ipaddr, pattern, maskstr) {\n    var ipPattern = /^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$/;\n    var test = ipaddr.match(ipPattern);\n    if (test == null) {\n        ipaddr = dnsResolve(ipaddr);\n        if (ipaddr == null)\n            return false;\n    } else if ((test[1] > 255) || (test[2] > 255) || \n               (test[3] > 255) || (test[4] > 255) ) {\n        return false;\n    }\n    var host = convert_addr(ipaddr);\n    var pat  = convert_addr(pattern);\n    var mask = convert_addr(maskstr);\n    return ((host & mask) == (pat & mask));\n    \n}\nfunction dnsResolve(host){\nif (typeof host != 'string' || dnsDomainLevels(host) != 3) return ''; \nfor (var i=0; i < host.length; i++) \nif ((host.charAt(i) < '0' || host.charAt(i) > '9') && host.charAt(i) != '.') return ''; \nreturn host; }";
  }

  public ProxyInfo[] getProxyInfo(URL paramURL)
    throws ProxyUnavailableException
  {
    // Byte code:
    //   0: invokestatic 110	sun/plugin/viewer/AppletPanelCache:hasValidInstance	()Z
    //   3: ifne +13 -> 16
    //   6: new 50	com/sun/deploy/net/proxy/ProxyUnavailableException
    //   9: dup
    //   10: ldc 4
    //   12: invokespecial 98	com/sun/deploy/net/proxy/ProxyUnavailableException:<init>	(Ljava/lang/String;)V
    //   15: athrow
    //   16: invokestatic 111	sun/plugin/viewer/AppletPanelCache:getAppletPanels	()[Ljava/lang/Object;
    //   19: astore_2
    //   20: aload_2
    //   21: iconst_0
    //   22: aaload
    //   23: checkcast 58	sun/applet/AppletPanel
    //   26: astore_3
    //   27: aload_3
    //   28: invokevirtual 108	sun/applet/AppletPanel:getAppletContext	()Ljava/applet/AppletContext;
    //   31: checkcast 62	sun/plugin/viewer/context/PluginAppletContext
    //   34: astore 4
    //   36: aconst_null
    //   37: astore 5
    //   39: aload 4
    //   41: invokeinterface 113 1 0
    //   46: astore 6
    //   48: new 54	java/lang/StringBuffer
    //   51: dup
    //   52: invokespecial 101	java/lang/StringBuffer:<init>	()V
    //   55: astore 7
    //   57: aload 7
    //   59: aload_0
    //   60: getfield 94	sun/plugin/net/proxy/PluginAutoProxyHandler:autoProxyScript	Ljava/lang/StringBuffer;
    //   63: invokevirtual 105	java/lang/StringBuffer:append	(Ljava/lang/StringBuffer;)Ljava/lang/StringBuffer;
    //   66: pop
    //   67: aload 7
    //   69: ldc 3
    //   71: invokevirtual 104	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   74: pop
    //   75: aload 7
    //   77: aload_1
    //   78: invokevirtual 103	java/lang/StringBuffer:append	(Ljava/lang/Object;)Ljava/lang/StringBuffer;
    //   81: pop
    //   82: aload 7
    //   84: ldc 2
    //   86: invokevirtual 104	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   89: pop
    //   90: aload 7
    //   92: aload_1
    //   93: invokevirtual 106	java/net/URL:getHost	()Ljava/lang/String;
    //   96: invokevirtual 104	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   99: pop
    //   100: aload 7
    //   102: ldc 1
    //   104: invokevirtual 104	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   107: pop
    //   108: aload 6
    //   110: aload 7
    //   112: invokevirtual 102	java/lang/StringBuffer:toString	()Ljava/lang/String;
    //   115: invokevirtual 107	netscape/javascript/JSObject:eval	(Ljava/lang/String;)Ljava/lang/Object;
    //   118: checkcast 53	java/lang/String
    //   121: astore 5
    //   123: aload_0
    //   124: aload 5
    //   126: invokevirtual 109	sun/plugin/net/proxy/PluginAutoProxyHandler:extractAutoProxySetting	(Ljava/lang/String;)[Lcom/sun/deploy/net/proxy/ProxyInfo;
    //   129: areturn
    //   130: astore 6
    //   132: ldc 6
    //   134: invokestatic 100	com/sun/deploy/trace/Trace:msgNetPrintln	(Ljava/lang/String;)V
    //   137: iconst_1
    //   138: anewarray 49	com/sun/deploy/net/proxy/ProxyInfo
    //   141: dup
    //   142: iconst_0
    //   143: new 49	com/sun/deploy/net/proxy/ProxyInfo
    //   146: dup
    //   147: aconst_null
    //   148: invokespecial 97	com/sun/deploy/net/proxy/ProxyInfo:<init>	(Ljava/lang/String;)V
    //   151: aastore
    //   152: areturn
    //
    // Exception table:
    //   from	to	target	type
    //   39	129	130	java/lang/Throwable
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.net.proxy.PluginAutoProxyHandler
 * JD-Core Version:    0.6.2
 */