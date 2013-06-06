package sun.plugin2.main.client;

import com.sun.applet2.AppletParameters;
import com.sun.deploy.net.cookie.CookieUnavailableException;
import com.sun.deploy.net.offline.OfflineHandler;
import com.sun.deploy.net.proxy.BrowserProxyConfig;
import com.sun.deploy.net.proxy.ProxyHandler;
import com.sun.deploy.security.BrowserAuthenticator;
import com.sun.deploy.security.CertStore;
import com.sun.deploy.security.CredentialManager;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.util.URLUtil;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;
import sun.plugin2.applet.Applet2ExecutionContext;
import sun.plugin2.applet.Plugin2Manager;
import sun.plugin2.message.Conversation;
import sun.plugin2.message.CookieOpMessage;
import sun.plugin2.message.CookieReplyMessage;
import sun.plugin2.message.CustomSecurityManagerAckMessage;
import sun.plugin2.message.CustomSecurityManagerRequestMessage;
import sun.plugin2.message.GetAuthenticationMessage;
import sun.plugin2.message.GetAuthenticationReplyMessage;
import sun.plugin2.message.GetProxyMessage;
import sun.plugin2.message.JavaScriptGetWindowMessage;
import sun.plugin2.message.JavaScriptReplyMessage;
import sun.plugin2.message.Pipe;
import sun.plugin2.message.ProxyReplyMessage;
import sun.plugin2.message.ShowDocumentMessage;
import sun.plugin2.message.ShowStatusMessage;

public class MessagePassingExecutionContext
  implements Applet2ExecutionContext
{
  private AppletParameters params;
  private Pipe pipe;
  private int appletID;
  private String documentBase;
  private BrowserAuthenticator authenticator;
  protected static long pid = 0L;

  public MessagePassingExecutionContext(AppletParameters paramAppletParameters, Pipe paramPipe, int paramInt, String paramString)
  {
    this.params = paramAppletParameters;
    this.pipe = paramPipe;
    this.appletID = paramInt;
    this.documentBase = paramString;
  }

  public AppletParameters getAppletParameters()
  {
    return this.params;
  }

  public void setAppletParameters(AppletParameters paramAppletParameters)
  {
    this.params = paramAppletParameters;
  }

  public String getDocumentBase(Plugin2Manager paramPlugin2Manager)
  {
    if (this.documentBase == null)
      try
      {
        JSObject localJSObject1 = getJSObject(paramPlugin2Manager);
        if (localJSObject1 != null)
        {
          JSObject localJSObject2 = (JSObject)localJSObject1.getMember("document");
          String str1 = null;
          try
          {
            str1 = (String)localJSObject2.getMember("URL");
          }
          catch (JSException localJSException)
          {
            str1 = (String)localJSObject2.getMember("documentURI");
          }
          if (str1 == null)
            throw new Exception("Can not get DocumentBase");
          String str2 = URLUtil.canonicalize(str1);
          this.documentBase = URLUtil.canonicalizeDocumentBaseURL(str2);
          try
          {
            this.documentBase = new URL(this.documentBase).toString();
          }
          catch (MalformedURLException localMalformedURLException)
          {
          }
        }
      }
      catch (Exception localException)
      {
        Trace.ignored(localException);
      }
    return this.documentBase;
  }

  public List getProxyList(URL paramURL, boolean paramBoolean)
  {
    Conversation localConversation = this.pipe.beginConversation();
    try
    {
      GetProxyMessage localGetProxyMessage = new GetProxyMessage(localConversation, this.appletID, paramURL, paramBoolean);
      this.pipe.send(localGetProxyMessage);
      ProxyReplyMessage localProxyReplyMessage = (ProxyReplyMessage)this.pipe.receive(0L, localConversation);
      if (localProxyReplyMessage != null)
      {
        List localList = localProxyReplyMessage.getProxyList();
        return localList;
      }
    }
    catch (Throwable localThrowable)
    {
      Trace.ignored(localThrowable);
    }
    finally
    {
      this.pipe.endConversation(localConversation);
    }
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(Proxy.NO_PROXY);
    return localArrayList;
  }

  public void setCookie(URL paramURL, String paramString)
    throws CookieUnavailableException
  {
    doCookieOp(false, paramURL, paramString);
  }

  public String getCookie(URL paramURL)
    throws CookieUnavailableException
  {
    return doCookieOp(true, paramURL, null);
  }

  private String doCookieOp(boolean paramBoolean, URL paramURL, String paramString)
    throws CookieUnavailableException
  {
    Conversation localConversation = this.pipe.beginConversation();
    try
    {
      CookieOpMessage localCookieOpMessage = new CookieOpMessage(localConversation, this.appletID, paramBoolean ? 1 : 2, paramURL, paramString);
      this.pipe.send(localCookieOpMessage);
      CookieReplyMessage localCookieReplyMessage = (CookieReplyMessage)this.pipe.receive(0L, localConversation);
      if (localCookieReplyMessage.getExceptionMessage() != null)
        throw new CookieUnavailableException(localCookieReplyMessage.getExceptionMessage());
      String str = localCookieReplyMessage.getCookie();
      return str;
    }
    catch (InterruptedException localInterruptedException)
    {
      Trace.ignored(localInterruptedException);
      throw new CookieUnavailableException("Pipe operation produced InterruptedException");
    }
    catch (IOException localIOException)
    {
      Trace.ignored(localIOException);
      throw new CookieUnavailableException("Pipe operation produced IOException");
    }
    finally
    {
      this.pipe.endConversation(localConversation);
    }
  }

  public CertStore getBrowserSigningRootCertStore()
  {
    return ServiceDelegate.get().getBrowserSigningRootCertStore();
  }

  public CertStore getBrowserSSLRootCertStore()
  {
    return ServiceDelegate.get().getBrowserSSLRootCertStore();
  }

  public CertStore getBrowserTrustedCertStore()
  {
    return ServiceDelegate.get().getBrowserTrustedCertStore();
  }

  public KeyStore getBrowserClientAuthKeyStore()
  {
    return ServiceDelegate.get().getBrowserClientAuthKeyStore();
  }

  public CredentialManager getCredentialManager()
  {
    return ServiceDelegate.get().getCredentialManager();
  }

  public SecureRandom getSecureRandom()
  {
    return ServiceDelegate.get().getSecureRandom();
  }

  public boolean isIExplorer()
  {
    return ServiceDelegate.get().isIExplorer();
  }

  public boolean isNetscape()
  {
    return ServiceDelegate.get().isNetscape();
  }

  public OfflineHandler getOfflineHandler()
  {
    return ServiceDelegate.get().getOfflineHandler();
  }

  public BrowserAuthenticator getBrowserAuthenticator()
  {
    if (this.authenticator == null)
      this.authenticator = new BrowserAuthenticatorImpl();
    return this.authenticator;
  }

  public float getBrowserVersion()
  {
    return 1.0F;
  }

  public boolean isConsoleIconifiedOnClose()
  {
    return false;
  }

  public boolean installBrowserEventListener()
  {
    return false;
  }

  public String mapBrowserElement(String paramString)
  {
    return paramString;
  }

  public void showDocument(URL paramURL)
  {
    showDocument(paramURL, "_self");
  }

  public static void setBrowserPID(long paramLong)
  {
    pid = paramLong;
  }

  protected static void AllowSetForegroundWindow()
  {
    if (pid != 0L)
      ServiceDelegate.get().AllowSetForegroundWindow(pid);
  }

  public void showDocument(URL paramURL, String paramString)
  {
    URL localURL;
    try
    {
      localURL = new URL(this.documentBase);
    }
    catch (Exception localException)
    {
      Trace.ignored(localException);
      return;
    }
    if (!URLUtil.checkTargetURL(localURL, paramURL))
      throw new SecurityException("showDocument url permission denied");
    Conversation localConversation = this.pipe.beginConversation();
    try
    {
      AllowSetForegroundWindow();
      ShowDocumentMessage localShowDocumentMessage = new ShowDocumentMessage(localConversation, this.appletID, paramURL.toString(), paramString);
      this.pipe.send(localShowDocumentMessage);
    }
    catch (IOException localIOException)
    {
      Trace.ignored(localIOException);
    }
    finally
    {
      this.pipe.endConversation(localConversation);
    }
  }

  public void showStatus(String paramString)
  {
    Conversation localConversation = this.pipe.beginConversation();
    try
    {
      ShowStatusMessage localShowStatusMessage = new ShowStatusMessage(localConversation, this.appletID, paramString);
      this.pipe.send(localShowStatusMessage);
    }
    catch (IOException localIOException)
    {
      Trace.ignored(localIOException);
    }
    finally
    {
      this.pipe.endConversation(localConversation);
    }
  }

  public JSObject getJSObject(Plugin2Manager paramPlugin2Manager)
    throws JSException
  {
    paramPlugin2Manager.stopWaitingForAppletStart();
    Conversation localConversation = this.pipe.beginConversation();
    try
    {
      JavaScriptGetWindowMessage localJavaScriptGetWindowMessage = new JavaScriptGetWindowMessage(localConversation, this.appletID);
      this.pipe.send(localJavaScriptGetWindowMessage);
      JavaScriptReplyMessage localJavaScriptReplyMessage = (JavaScriptReplyMessage)this.pipe.receive(0L, localConversation);
      if (localJavaScriptReplyMessage.getExceptionMessage() != null)
        throw new JSException(localJavaScriptReplyMessage.getExceptionMessage());
      JSObject localJSObject = (JSObject)LiveConnectSupport.importObject(localJavaScriptReplyMessage.getResult(), this.appletID);
      return localJSObject;
    }
    catch (InterruptedException localInterruptedException)
    {
      throw ((JSException)new JSException().initCause(localInterruptedException));
    }
    catch (IOException localIOException)
    {
      throw ((JSException)new JSException().initCause(localIOException));
    }
    finally
    {
      this.pipe.endConversation(localConversation);
    }
  }

  public JSObject getOneWayJSObject(Plugin2Manager paramPlugin2Manager)
    throws JSException
  {
    Conversation localConversation = this.pipe.beginConversation();
    try
    {
      JavaScriptGetWindowMessage localJavaScriptGetWindowMessage = new JavaScriptGetWindowMessage(localConversation, this.appletID);
      this.pipe.send(localJavaScriptGetWindowMessage);
      JavaScriptReplyMessage localJavaScriptReplyMessage = (JavaScriptReplyMessage)this.pipe.receive(0L, localConversation);
      if (localJavaScriptReplyMessage.getExceptionMessage() != null)
        throw new JSException(localJavaScriptReplyMessage.getExceptionMessage());
      JSObject localJSObject = (JSObject)LiveConnectSupport.importOneWayJSObject(localJavaScriptReplyMessage.getResult(), this.appletID, paramPlugin2Manager);
      return localJSObject;
    }
    catch (InterruptedException localInterruptedException)
    {
      throw ((JSException)new JSException().initCause(localInterruptedException));
    }
    catch (IOException localIOException)
    {
      throw ((JSException)new JSException().initCause(localIOException));
    }
    finally
    {
      this.pipe.endConversation(localConversation);
    }
  }

  public BrowserProxyConfig getProxyConfig()
  {
    return null;
  }

  public ProxyHandler getSystemProxyHandler()
  {
    return null;
  }

  public ProxyHandler getAutoProxyHandler()
  {
    return null;
  }

  public ProxyHandler getBrowserProxyHandler()
  {
    return null;
  }

  public boolean requestCustomSecurityManager()
  {
    Conversation localConversation = this.pipe.beginConversation();
    try
    {
      CustomSecurityManagerRequestMessage localCustomSecurityManagerRequestMessage = new CustomSecurityManagerRequestMessage(localConversation, this.appletID);
      this.pipe.send(localCustomSecurityManagerRequestMessage);
      CustomSecurityManagerAckMessage localCustomSecurityManagerAckMessage = (CustomSecurityManagerAckMessage)this.pipe.receive(1000L, localConversation);
      return localCustomSecurityManagerAckMessage.isAllowed();
    }
    catch (InterruptedException localInterruptedException)
    {
      localInterruptedException.printStackTrace();
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
    return false;
  }

  class BrowserAuthenticatorImpl
    implements BrowserAuthenticator
  {
    BrowserAuthenticatorImpl()
    {
    }

    public PasswordAuthentication getAuthentication(String paramString1, String paramString2, int paramInt, String paramString3, String paramString4, URL paramURL, boolean paramBoolean)
    {
      Conversation localConversation = MessagePassingExecutionContext.this.pipe.beginConversation();
      try
      {
        GetAuthenticationMessage localGetAuthenticationMessage = new GetAuthenticationMessage(localConversation, MessagePassingExecutionContext.this.appletID, paramString1, paramString2, paramInt, paramString3, paramString4, paramURL, paramBoolean);
        MessagePassingExecutionContext.this.pipe.send(localGetAuthenticationMessage);
        GetAuthenticationReplyMessage localGetAuthenticationReplyMessage = (GetAuthenticationReplyMessage)MessagePassingExecutionContext.this.pipe.receive(0L, localConversation);
        if (localGetAuthenticationReplyMessage.getErrorMessage() != null)
          throw new RuntimeException(localGetAuthenticationReplyMessage.getErrorMessage());
        PasswordAuthentication localPasswordAuthentication = localGetAuthenticationReplyMessage.getAuthentication();
        return localPasswordAuthentication;
      }
      catch (InterruptedException localInterruptedException)
      {
        throw new RuntimeException(localInterruptedException);
      }
      catch (IOException localIOException)
      {
        throw new RuntimeException(localIOException);
      }
      finally
      {
        MessagePassingExecutionContext.this.pipe.endConversation(localConversation);
      }
    }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.main.client.MessagePassingExecutionContext
 * JD-Core Version:    0.6.2
 */