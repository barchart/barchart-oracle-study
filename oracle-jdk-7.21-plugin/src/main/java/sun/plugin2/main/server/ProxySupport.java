package sun.plugin2.main.server;

import com.sun.deploy.net.proxy.DynamicProxyManager;
import sun.plugin2.message.GetProxyMessage;
import sun.plugin2.message.ProxyReplyMessage;

public class ProxySupport
{
  private static final ThreadLocal currentPlugin = new ThreadLocal();

  protected static void setCurrentPlugin(Plugin paramPlugin)
  {
    currentPlugin.set(paramPlugin);
  }

  protected static Plugin getCurrentPlugin()
  {
    return (Plugin)currentPlugin.get();
  }

  public static ProxyReplyMessage getProxyReply(Plugin paramPlugin, GetProxyMessage paramGetProxyMessage)
  {
    setCurrentPlugin(paramPlugin);
    try
    {
      ProxyReplyMessage localProxyReplyMessage = new ProxyReplyMessage(paramGetProxyMessage.getConversation(), DynamicProxyManager.getProxyList(paramGetProxyMessage.getURL(), paramGetProxyMessage.isSocketURI()));
      return localProxyReplyMessage;
    }
    finally
    {
      setCurrentPlugin(null);
    }
  }

  static
  {
    DynamicProxyManager.reset();
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.main.server.ProxySupport
 * JD-Core Version:    0.6.2
 */