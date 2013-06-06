package sun.plugin2.main.server;

import com.sun.deploy.net.cookie.CookieUnavailableException;
import java.net.PasswordAuthentication;
import java.net.URL;
import netscape.javascript.JSException;
import sun.plugin2.liveconnect.BrowserSideObject;

public abstract interface Plugin
{
  public abstract void invokeLater(Runnable paramRunnable);

  public abstract void notifyMainThread();

  public abstract String getDocumentBase();

  public abstract void showDocument(String paramString1, String paramString2);

  public abstract void showStatus(String paramString);

  public abstract String getCookie(URL paramURL)
    throws CookieUnavailableException;

  public abstract void setCookie(URL paramURL, String paramString)
    throws CookieUnavailableException;

  public abstract PasswordAuthentication getAuthentication(String paramString1, String paramString2, int paramInt, String paramString3, String paramString4, URL paramURL, boolean paramBoolean);

  public abstract void hostRemoteCAContext(int paramInt);

  public abstract BrowserSideObject javaScriptGetWindow();

  public abstract void javaScriptRetainObject(BrowserSideObject paramBrowserSideObject);

  public abstract void javaScriptReleaseObject(BrowserSideObject paramBrowserSideObject);

  public abstract Object javaScriptCall(BrowserSideObject paramBrowserSideObject, String paramString, Object[] paramArrayOfObject)
    throws JSException;

  public abstract Object javaScriptEval(BrowserSideObject paramBrowserSideObject, String paramString)
    throws JSException;

  public abstract Object javaScriptGetMember(BrowserSideObject paramBrowserSideObject, String paramString)
    throws JSException;

  public abstract void javaScriptSetMember(BrowserSideObject paramBrowserSideObject, String paramString, Object paramObject)
    throws JSException;

  public abstract void javaScriptRemoveMember(BrowserSideObject paramBrowserSideObject, String paramString)
    throws JSException;

  public abstract Object javaScriptGetSlot(BrowserSideObject paramBrowserSideObject, int paramInt)
    throws JSException;

  public abstract void javaScriptSetSlot(BrowserSideObject paramBrowserSideObject, int paramInt, Object paramObject)
    throws JSException;

  public abstract String javaScriptToString(BrowserSideObject paramBrowserSideObject);

  public abstract int getActiveJSCounter();

  public abstract void incrementActiveJSCounter();

  public abstract void decrementActiveJSCounter();

  public abstract void startupStatus(int paramInt);

  public abstract void waitForSignalWithModalBlocking();
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.main.server.Plugin
 * JD-Core Version:    0.6.2
 */