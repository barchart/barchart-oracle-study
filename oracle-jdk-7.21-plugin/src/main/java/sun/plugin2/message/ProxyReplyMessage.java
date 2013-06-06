package sun.plugin2.message;

import java.io.IOException;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import sun.plugin2.message.helper.ProxyHelper;

public class ProxyReplyMessage extends PluginMessage
{
  public static final int ID = 42;
  private List proxyList;
  private int listSize;

  public ProxyReplyMessage(Conversation paramConversation)
  {
    super(42, paramConversation);
  }

  public ProxyReplyMessage(Conversation paramConversation, List paramList)
  {
    this(paramConversation);
    if (paramList == null)
    {
      this.listSize = 1;
      this.proxyList = new ArrayList();
      this.proxyList.add(Proxy.NO_PROXY);
    }
    else
    {
      this.proxyList = paramList;
      this.listSize = paramList.size();
    }
  }

  public List getProxyList()
  {
    return this.proxyList;
  }

  public void writeFields(Serializer paramSerializer)
    throws IOException
  {
    paramSerializer.writeInt(this.listSize);
    Iterator localIterator = this.proxyList.iterator();
    while (localIterator.hasNext())
    {
      Proxy localProxy = (Proxy)localIterator.next();
      ProxyHelper.write(paramSerializer, localProxy);
    }
  }

  public void readFields(Serializer paramSerializer)
    throws IOException
  {
    this.listSize = paramSerializer.readInt();
    this.proxyList = new ArrayList(this.listSize);
    for (int i = 0; i < this.listSize; i++)
      this.proxyList.add(ProxyHelper.read(paramSerializer));
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.ProxyReplyMessage
 * JD-Core Version:    0.6.2
 */