package sun.plugin.navig.motif;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;

public class Worker
{
  private static DataInputStream data_in;
  private static DataOutputStream data_out;
  private static Hashtable proxmap = new Hashtable();
  private static String cookieString = null;
  public static final int JAVA_PLUGIN_SHOW_STATUS = 16121857;
  public static final int JAVA_PLUGIN_SHOW_DOCUMENT = 16121858;
  public static final int JAVA_PLUGIN_FIND_PROXY = 16121859;
  public static final int JAVA_PLUGIN_FIND_COOKIE = 16121860;
  public static final int JAVA_PLUGIN_JAVASCRIPT_REQUEST = 16121862;
  public static final int JAVA_PLUGIN_SET_COOKIE = 16121865;
  public static final int JAVA_PLUGIN_STATUS_CHANGE = 16121866;
  private static String requestName;
  private static final int REQUEST_IDLE = 1;
  private static final int REQUEST_IN_PROGRESS = 2;
  private static final int RESPONSE_IS_READY = 3;
  private static final int REQUEST_ABRUPTLY_TERMINATED = 4;
  private static int requestStatus = 1;

  Worker(DataInputStream paramDataInputStream, DataOutputStream paramDataOutputStream)
  {
    data_out = paramDataOutputStream;
    data_in = paramDataInputStream;
  }

  static synchronized void terminateRequestAbruptly()
  {
    terminateAbruptly();
  }

  private static synchronized void pushRequest(String paramString)
    throws IOException
  {
    data_out.flush();
    Plugin.trace("Worker pushRequest:" + paramString);
  }

  private static synchronized void writeString(String paramString)
    throws IOException
  {
    data_out.writeInt(paramString.length());
    for (int i = 0; i < paramString.length(); i++)
    {
      int j = paramString.charAt(i);
      data_out.writeByte((byte)j);
    }
  }

  private static synchronized void writeByteArr(byte[] paramArrayOfByte)
    throws IOException
  {
    data_out.writeInt(paramArrayOfByte.length);
    for (int i = 0; i < paramArrayOfByte.length; i++)
      data_out.writeByte(paramArrayOfByte[i]);
  }

  public static synchronized void showDocument(int paramInt, URL paramURL, String paramString)
  {
    try
    {
      data_out.writeInt(16121858);
      data_out.writeShort(paramInt);
      writeString("" + paramURL);
      writeString(paramString);
      pushRequest("showDocument");
    }
    catch (IOException localIOException)
    {
      Plugin.error("java process showDocument: write to parent failed\nException: " + localIOException.toString());
    }
  }

  public static synchronized void showStatus(int paramInt, String paramString)
  {
    try
    {
      if (!Plugin.parentAlive())
        return;
      if (paramString == null)
        paramString = "";
      data_out.writeInt(16121857);
      data_out.writeShort(paramInt);
      writeString(paramString);
      pushRequest("showstatus:" + paramString);
    }
    catch (IOException localIOException)
    {
      Plugin.error("java process show status: write to parent failed\nException: " + localIOException.toString());
    }
  }

  public static synchronized void notifyStatusChange(int paramInt1, int paramInt2)
  {
    try
    {
      data_out.writeInt(16121866);
      data_out.writeShort(paramInt1);
      data_out.writeShort(paramInt2);
      pushRequest("notify status change:" + paramInt2);
    }
    catch (IOException localIOException)
    {
      Plugin.error("java process fails to notify the browser about the applet status change");
    }
  }

  public static synchronized String getProxySettings(String paramString)
  {
    try
    {
      URL localURL = new URL(paramString);
      String str1 = localURL.getProtocol() + ":" + localURL.getHost() + ":" + localURL.getPort();
      Plugin.trace("getProxySettings. Using key:" + str1);
      String str2 = (String)proxmap.get(str1);
      if (str2 != null)
      {
        Plugin.trace("Retrieving cached proxy:" + str2);
        return str2;
      }
      enterRequest("Proxy");
      data_out.writeInt(16121859);
      data_out.writeShort(-1);
      writeString(paramString);
      writeString(localURL.getHost());
      pushRequest("FindProxy");
      waitForResponse("Proxy");
      str2 = (String)proxmap.get(str1);
      clearRequest();
      return str2;
    }
    catch (MalformedURLException localMalformedURLException)
    {
      System.err.println("Bad URL in getProxySettings: " + paramString);
    }
    catch (IOException localIOException)
    {
      System.err.println("getProxySettings: IO error on plugin");
    }
    clearRequest();
    return null;
  }

  static synchronized void addProxyMapping(String paramString1, String paramString2)
  {
    try
    {
      URL localURL = new URL(paramString1);
      String str = localURL.getProtocol() + ":" + localURL.getHost() + ":" + localURL.getPort();
      proxmap.put(str, paramString2);
      responseIsReady("Proxy");
    }
    catch (MalformedURLException localMalformedURLException)
    {
      System.err.println("Bad URL in getting proxy: " + paramString1);
    }
  }

  public static synchronized void sendJSRequest(int paramInt, String paramString)
  {
    try
    {
      data_out.writeInt(16121862);
      data_out.writeShort((short)paramInt);
      data_out.writeShort(0);
      writeString(paramString);
      pushRequest("JS Request");
    }
    catch (IOException localIOException)
    {
      System.err.println("sendJSRequest: io error in Plugin " + localIOException);
    }
  }

  public static synchronized String findCookieForURL(String paramString)
  {
    Plugin.trace("Worker.findCookieForURL: " + paramString);
    try
    {
      enterRequest("Cookie");
      cookieString = null;
      data_out.writeInt(16121860);
      data_out.writeShort(-1);
      writeString(paramString);
      pushRequest("FindCookie");
      waitForResponse("Cookie");
      String str = cookieString;
      clearRequest();
      Plugin.trace(" Got cookie string:" + str);
      return str;
    }
    catch (IOException localIOException)
    {
      System.err.println("IOException in findCookieURL");
      System.err.println("Bad termination of cookie request!");
      clearRequest();
    }
    return null;
  }

  public static synchronized String setCookieForURL(String paramString1, String paramString2)
  {
    Plugin.trace("Worker.setCookieForURL: " + paramString1 + "=" + paramString2);
    try
    {
      data_out.writeInt(16121865);
      data_out.writeShort(-1);
      writeString("" + paramString1);
      writeString("" + paramString2);
      pushRequest("setCookie");
    }
    catch (IOException localIOException)
    {
      Plugin.error("java process setCookie: write to parent failed\nException: " + localIOException.toString());
    }
    return null;
  }

  static synchronized void setCookieString(String paramString)
  {
    cookieString = paramString;
    responseIsReady("Cookie");
  }

  static synchronized void enterRequest(String paramString)
  {
    try
    {
      while (requestStatus != 1)
        Worker.class.wait();
      requestName = paramString;
      requestStatus = 2;
      Plugin.trace("Entering request for:" + paramString);
    }
    catch (InterruptedException localInterruptedException)
    {
      Plugin.trace("Request was interrupted when entering");
    }
  }

  static synchronized boolean waitForResponse(String paramString)
  {
    Plugin.trace("Waiting for response: " + paramString);
    try
    {
      while (requestStatus != 3)
      {
        Worker.class.wait();
        Plugin.trace("Woke up in request for:" + paramString);
        if (requestStatus == 4)
        {
          clearRequest();
          return false;
        }
      }
      Plugin.trace("Got response for request:" + paramString);
      return true;
    }
    catch (InterruptedException localInterruptedException)
    {
      Plugin.trace("Request was interrupted before response");
    }
    return false;
  }

  static synchronized void responseIsReady(String paramString)
  {
    Plugin.trace("Response is ready:" + paramString);
    requestStatus = 3;
    Worker.class.notifyAll();
  }

  static synchronized void terminateAbruptly()
  {
    Plugin.trace("Request was abruptly terminated");
    if (requestStatus != 1)
      requestStatus = 4;
    Worker.class.notifyAll();
  }

  static synchronized void clearRequest()
  {
    Plugin.trace("Request was cleared");
    requestStatus = 1;
    Worker.class.notifyAll();
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.navig.motif.Worker
 * JD-Core Version:    0.6.2
 */