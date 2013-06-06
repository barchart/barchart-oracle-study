package com.sun.javaws;

import com.sun.deploy.config.Platform;
import com.sun.deploy.trace.Trace;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;

public class HtmlOptions
{
  private static final String DOCBASE_KEY = "docbase";
  private static final String JNLPHREF_KEY = "jnlphref";
  private static final String EMBEDDED_KEY = "embedded";
  private static final String APPARG_KEY = "apparg";
  private static final String VMARG_KEY = "vmarg";
  private static final String PNAME_KEY = "name";
  private static final String PVALUE_KEY = "value";
  private String _embedded_jnlp = null;
  private String[] _htmlApplicationArgs = null;
  private String[] _htmlVmArgs = null;
  private Properties _htmlParameters;
  private URL _docbase = null;
  private String _jnlphref = null;
  private Properties rawProperties = null;
  static HtmlOptions theInstance = null;

  private HtmlOptions()
  {
  }

  private HtmlOptions(InputStream paramInputStream)
    throws MalformedURLException, IOException
  {
    importProperties(paramInputStream);
  }

  public static synchronized HtmlOptions get()
  {
    return theInstance;
  }

  public static synchronized HtmlOptions create(InputStream paramInputStream)
    throws MalformedURLException, IOException
  {
    theInstance = new HtmlOptions(paramInputStream);
    return theInstance;
  }

  public void export(OutputStream paramOutputStream)
    throws IOException
  {
    if (this.rawProperties != null)
    {
      OutputStreamWriter localOutputStreamWriter = new OutputStreamWriter(paramOutputStream);
      this.rawProperties.store(localOutputStreamWriter, null);
      localOutputStreamWriter.close();
    }
  }

  public synchronized void init(String paramString)
  {
    theInstance = new HtmlOptions();
  }

  private byte[] readFully(InputStream paramInputStream)
    throws IOException
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    int i = 0;
    byte[] arrayOfByte = new byte[16384];
    while ((i = paramInputStream.read(arrayOfByte)) != -1)
      localByteArrayOutputStream.write(arrayOfByte, 0, i);
    paramInputStream.close();
    return localByteArrayOutputStream.toByteArray();
  }

  private void importProperties(InputStream paramInputStream)
    throws MalformedURLException, IOException
  {
    byte[] arrayOfByte = readFully(paramInputStream);
    this.rawProperties = load(arrayOfByte, Platform.get().getPlatformNativeEncoding());
    if (this.rawProperties.getProperty("docbase") == null)
      this.rawProperties = load(arrayOfByte, "UTF-8");
    String str1 = this.rawProperties.getProperty("docbase");
    this._jnlphref = this.rawProperties.getProperty("jnlphref");
    String str2 = this.rawProperties.getProperty("embedded");
    this._htmlApplicationArgs = extractList("apparg");
    this._htmlVmArgs = extractList("vmarg");
    this._htmlParameters = extractParameters();
    if ((str1 != null) && (str1.length() > 0))
      this._docbase = new URL(str1);
    if ((str2 != null) && (str2.length() > 0))
      this._embedded_jnlp = str2;
  }

  private Properties extractParameters()
  {
    Properties localProperties = new Properties();
    for (int i = 0; ; i++)
    {
      String str1 = "name." + i;
      if (!this.rawProperties.containsKey(str1))
        break;
      String str2 = "value." + i;
      localProperties.setProperty(this.rawProperties.getProperty(str1), this.rawProperties.getProperty(str2));
    }
    return localProperties.isEmpty() ? null : localProperties;
  }

  String[] extractList(String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    for (int i = 0; ; i++)
    {
      String str1 = paramString + "." + i;
      String str2 = this.rawProperties.getProperty(str1);
      if (str2 == null)
        break;
      localArrayList.add(str2);
    }
    if ((localArrayList.isEmpty()) && (this.rawProperties.getProperty(paramString + ".length") == null))
      return null;
    return (String[])localArrayList.toArray(new String[0]);
  }

  private Properties load(byte[] paramArrayOfByte, String paramString)
  {
    ByteArrayInputStream localByteArrayInputStream = null;
    InputStreamReader localInputStreamReader = null;
    try
    {
      localByteArrayInputStream = new ByteArrayInputStream(paramArrayOfByte);
      localInputStreamReader = new InputStreamReader(localByteArrayInputStream, paramString);
      Properties localProperties1 = new Properties();
      localProperties1.load(localInputStreamReader);
      localProperties2 = localProperties1;
      return localProperties2;
    }
    catch (Exception localException)
    {
      Properties localProperties2 = new Properties();
      return localProperties2;
    }
    finally
    {
      if (localInputStreamReader != null)
        try
        {
          localInputStreamReader.close();
        }
        catch (IOException localIOException5)
        {
          Trace.ignoredException(localIOException5);
        }
      else if (localByteArrayInputStream != null)
        try
        {
          localByteArrayInputStream.close();
        }
        catch (IOException localIOException6)
        {
          Trace.ignoredException(localIOException6);
        }
    }
  }

  public String getEmbedded()
  {
    return this._embedded_jnlp;
  }

  public String[] getHtmlApplicationArgs()
  {
    return this._htmlApplicationArgs;
  }

  public String[] getHtmlVmArgs()
  {
    return this._htmlVmArgs;
  }

  public Properties getParameters()
  {
    return this._htmlParameters;
  }

  public String getAbsoluteHref(URL paramURL)
    throws MalformedURLException
  {
    if ((this._jnlphref != null) && (this._jnlphref.length() > 0))
      try
      {
        return new URL(this._jnlphref).toString();
      }
      catch (MalformedURLException localMalformedURLException)
      {
        if (paramURL != null)
        {
          boolean bool = paramURL.toString().endsWith("/");
          return new URL(paramURL.toString() + (bool ? "" : "/") + this._jnlphref).toString();
        }
      }
    return null;
  }

  public URL getDocBase()
  {
    return this._docbase;
  }
}

/* Location:           /home/user1/Temp/jvm/javaws.jar
 * Qualified Name:     com.sun.javaws.HtmlOptions
 * JD-Core Version:    0.6.2
 */