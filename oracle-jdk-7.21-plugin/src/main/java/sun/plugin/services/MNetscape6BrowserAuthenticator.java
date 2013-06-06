package sun.plugin.services;

import com.sun.deploy.security.AbstractBrowserAuthenticator;
import java.net.PasswordAuthentication;
import java.net.URL;

public final class MNetscape6BrowserAuthenticator extends AbstractBrowserAuthenticator
{
  public PasswordAuthentication getAuthentication(String paramString1, String paramString2, int paramInt, String paramString3, String paramString4, URL paramURL, boolean paramBoolean)
  {
    Object[] arrayOfObject = new Object[5];
    arrayOfObject[0] = paramString1;
    arrayOfObject[1] = paramString2;
    arrayOfObject[2] = String.valueOf(paramInt);
    arrayOfObject[3] = paramString3;
    arrayOfObject[4] = paramString4;
    return getPAFromCharArray(getBrowserAuthentication(arrayOfObject));
  }

  private native char[] getBrowserAuthentication(Object[] paramArrayOfObject);
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.services.MNetscape6BrowserAuthenticator
 * JD-Core Version:    0.6.2
 */