package sun.plugin2.main.server;

import com.sun.deploy.net.cookie.CookieUnavailableException;
import sun.plugin2.message.CookieOpMessage;
import sun.plugin2.message.CookieReplyMessage;

public class CookieSupport
{
  public static CookieReplyMessage getCookieReply(Plugin paramPlugin, CookieOpMessage paramCookieOpMessage)
  {
    try
    {
      switch (paramCookieOpMessage.getOperationKind())
      {
      case 1:
        return new CookieReplyMessage(paramCookieOpMessage.getConversation(), paramPlugin.getCookie(paramCookieOpMessage.getURL()), null);
      case 2:
        paramPlugin.setCookie(paramCookieOpMessage.getURL(), paramCookieOpMessage.getCookie());
        return new CookieReplyMessage(paramCookieOpMessage.getConversation(), null, null);
      }
      return new CookieReplyMessage(paramCookieOpMessage.getConversation(), null, "Error: unknown cookie operation kind " + paramCookieOpMessage.getOperationKind());
    }
    catch (CookieUnavailableException localCookieUnavailableException)
    {
      return new CookieReplyMessage(paramCookieOpMessage.getConversation(), null, localCookieUnavailableException.toString());
    }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.main.server.CookieSupport
 * JD-Core Version:    0.6.2
 */