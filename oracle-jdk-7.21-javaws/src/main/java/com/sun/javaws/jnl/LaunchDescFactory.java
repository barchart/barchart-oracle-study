package com.sun.javaws.jnl;

import com.sun.deploy.Environment;
import com.sun.deploy.cache.Cache;
import com.sun.deploy.cache.CacheEntry;
import com.sun.deploy.config.Config;
import com.sun.deploy.model.LocalApplicationProperties;
import com.sun.deploy.model.Resource;
import com.sun.deploy.model.ResourceProvider;
import com.sun.deploy.net.DownloadEngine;
import com.sun.deploy.net.FailedDownloadException;
import com.sun.deploy.net.HttpRequest;
import com.sun.deploy.net.HttpResponse;
import com.sun.deploy.net.offline.DeployOfflineManager;
import com.sun.deploy.resources.ResourceManager;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.util.URLUtil;
import com.sun.deploy.xml.XMLNode;
import com.sun.javaws.exceptions.BadFieldException;
import com.sun.javaws.exceptions.JNLParseException;
import com.sun.javaws.exceptions.MissingFieldException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.zip.GZIPInputStream;

public class LaunchDescFactory
{
  private static final boolean DEBUG = false;
  private static final int BUFFER_SIZE = 8192;
  private static URL derivedCodebase = null;
  private static URL docbase = null;

  public static void setDocBase(URL paramURL)
  {
    docbase = paramURL;
  }

  public static URL getDocBase()
  {
    return docbase;
  }

  public static URL getDerivedCodebase()
  {
    if ((docbase != null) && (derivedCodebase == null))
      try
      {
        derivedCodebase = new URL(docbase.toString().substring(0, docbase.toString().lastIndexOf("/") + 1));
      }
      catch (MalformedURLException localMalformedURLException)
      {
        Trace.ignoredException(localMalformedURLException);
      }
    return derivedCodebase;
  }

  public static LaunchDesc buildDescriptor(byte[] paramArrayOfByte, URL paramURL1, URL paramURL2, URL paramURL3)
    throws IOException, BadFieldException, MissingFieldException, JNLParseException
  {
    return XMLFormat.parse(paramArrayOfByte, paramURL1, paramURL2, paramURL3, new DefaultMatchJRE());
  }

  public static LaunchDesc buildDescriptor(byte[] paramArrayOfByte, URL paramURL1, URL paramURL2)
    throws IOException, BadFieldException, MissingFieldException, JNLParseException
  {
    return buildDescriptor(paramArrayOfByte, paramURL1, paramURL2, null);
  }

  public static LaunchDesc buildDescriptor(File paramFile, URL paramURL1, URL paramURL2, URL paramURL3)
    throws IOException, BadFieldException, MissingFieldException, JNLParseException
  {
    return buildDescriptor(readBytes(new FileInputStream(paramFile), paramFile.length()), paramURL1, paramURL2, paramURL3);
  }

  private static LaunchDesc buildDescriptor(File paramFile, LocalApplicationProperties paramLocalApplicationProperties, URL paramURL1, URL paramURL2)
    throws IOException, BadFieldException, MissingFieldException, JNLParseException
  {
    String str1 = paramLocalApplicationProperties.getDocumentBase();
    String str2 = paramLocalApplicationProperties.getCodebase();
    if (str2 != null)
      try
      {
        paramURL1 = new URL(str2);
      }
      catch (MalformedURLException localMalformedURLException1)
      {
      }
    if (str1 != null)
      try
      {
        paramURL2 = new URL(str1);
      }
      catch (MalformedURLException localMalformedURLException2)
      {
      }
    return buildDescriptor(paramFile, paramURL1, paramURL2, null);
  }

  public static LaunchDesc buildDescriptor(File paramFile)
    throws IOException, BadFieldException, MissingFieldException, JNLParseException
  {
    LocalApplicationProperties localLocalApplicationProperties = Cache.getLocalApplicationProperties(paramFile.getPath());
    if (localLocalApplicationProperties != null)
      return buildDescriptor(paramFile, localLocalApplicationProperties, null, null);
    String str1 = System.getProperty("jnlpx.origFilenameArg");
    if (str1 != null)
    {
      File localFile = new File(str1);
      URL localURL = null;
      try
      {
        String str2 = localFile.getAbsoluteFile().getParent();
        if (str2.startsWith(File.separator))
          str2 = str2.substring(1, str2.length());
        localURL = new URL("file:/" + str2 + File.separator);
      }
      catch (MalformedURLException localMalformedURLException)
      {
        Trace.ignoredException(localMalformedURLException);
      }
      if (localURL != null)
      {
        LaunchDesc localLaunchDesc = buildDescriptor(paramFile, localURL, null, null);
        derivedCodebase = localURL;
        return localLaunchDesc;
      }
    }
    return null;
  }

  public static LaunchDesc buildDescriptor(URL paramURL1, URL paramURL2)
    throws IOException, BadFieldException, MissingFieldException, JNLParseException
  {
    int i = ResourceProvider.get().incrementInternalUse();
    try
    {
      LaunchDesc localLaunchDesc = _buildDescriptor(paramURL1, paramURL2);
      return localLaunchDesc;
    }
    finally
    {
      ResourceProvider.get().decrementInternalUse(i);
    }
  }

  private static LaunchDesc _buildDescriptor(URL paramURL1, URL paramURL2)
    throws IOException, BadFieldException, MissingFieldException, JNLParseException
  {
    Resource localResource = null;
    Object localObject1 = null;
    File localFile;
    try
    {
      localFile = ResourceProvider.get().getResource(paramURL1, null).getDataFile();
    }
    catch (IOException localIOException)
    {
      if (((localIOException instanceof UnknownHostException)) || ((localIOException instanceof FailedDownloadException)) || ((localIOException instanceof ConnectException)) || ((localIOException instanceof SocketException)))
      {
        Trace.ignoredException(localIOException);
        localObject1 = localIOException;
        localFile = ResourceProvider.get().getCachedJNLPFile(paramURL1, null);
        if ((localFile == null) && (DeployOfflineManager.isForcedOffline()))
          throw localIOException;
      }
      else
      {
        throw localIOException;
      }
    }
    URL localURL = URLUtil.asPathURL(URLUtil.getBase(paramURL1));
    if ((localFile != null) && (localFile.exists()))
    {
      if (Environment.getImportModeCodebaseOverride() != null)
        localURL = new URL(Environment.getImportModeCodebaseOverride());
      localObject2 = buildDescriptor(localFile, localURL, paramURL2, paramURL1);
      if ((localObject2 != null) && (((LaunchDesc)localObject2).getLaunchType() == 5))
        ResourceProvider.get().markRetired(localResource, false);
      if ((localObject1 != null) && (!((LaunchDesc)localObject2).getInformation().supportsOfflineOperation()))
        throw localObject1;
      derivedCodebase = localURL;
      return localObject2;
    }
    Object localObject2 = DownloadEngine.getHttpRequestImpl();
    HttpResponse localHttpResponse = ((HttpRequest)localObject2).doGetRequest(paramURL1);
    Object localObject3 = localHttpResponse.getInputStream();
    int i = localHttpResponse.getContentLength();
    String str = localHttpResponse.getContentEncoding();
    if ((str != null) && (str.indexOf("gzip") >= 0))
      localObject3 = new GZIPInputStream((InputStream)localObject3, 8192);
    LaunchDesc localLaunchDesc = buildDescriptor(readBytes((InputStream)localObject3, i), localURL, paramURL2);
    ((InputStream)localObject3).close();
    return localLaunchDesc;
  }

  private static LaunchDesc buildDescriptorFromCache(URL paramURL1, URL paramURL2)
    throws BadFieldException, MissingFieldException, JNLParseException
  {
    try
    {
      File localFile = ResourceProvider.get().getCachedJNLPFile(paramURL1, null);
      if (localFile != null)
      {
        URL localURL = URLUtil.asPathURL(URLUtil.getBase(paramURL1));
        return buildDescriptor(localFile, localURL, paramURL2, paramURL1);
      }
    }
    catch (IOException localIOException)
    {
    }
    return null;
  }

  public static LaunchDesc buildDescriptorFromCache(String paramString, URL paramURL1, URL paramURL2)
    throws BadFieldException, MissingFieldException, JNLParseException
  {
    URL localURL = null;
    LaunchDesc localLaunchDesc = null;
    try
    {
      File localFile = new File(paramString);
      if (localFile.isFile())
        localLaunchDesc = buildDescriptor(localFile, paramURL1, paramURL2, null);
      if (localLaunchDesc != null)
        return localLaunchDesc;
    }
    catch (Exception localException1)
    {
    }
    try
    {
      localURL = new URL(paramString);
      if (paramString.endsWith(".jarjnlp"))
        localLaunchDesc = buildNoHrefDescriptorFromCache(localURL, paramURL1, paramURL2);
      else
        localLaunchDesc = buildDescriptorFromCache(localURL, paramURL2);
      if (localLaunchDesc != null)
        return localLaunchDesc;
    }
    catch (Exception localException2)
    {
    }
    if (paramURL1 != null)
      try
      {
        localURL = new URL(paramURL1, paramString);
        localLaunchDesc = buildDescriptorFromCache(localURL, paramURL2);
        if (localLaunchDesc != null)
          return localLaunchDesc;
      }
      catch (Exception localException3)
      {
      }
    if ((paramURL1 == null) && (paramURL2 != null))
      try
      {
        localURL = new URL(URLUtil.getBase(paramURL2), paramString);
        localLaunchDesc = buildDescriptorFromCache(localURL, paramURL2);
        if (localLaunchDesc != null)
          return localLaunchDesc;
      }
      catch (Exception localException4)
      {
      }
    return null;
  }

  private static LaunchDesc buildNoHrefDescriptorFromCache(URL paramURL1, URL paramURL2, URL paramURL3)
    throws BadFieldException, MissingFieldException, JNLParseException
  {
    try
    {
      if (paramURL2 == null)
        paramURL2 = URLUtil.asPathURL(URLUtil.getBase(paramURL1));
      CacheEntry localCacheEntry = (CacheEntry)ResourceProvider.get().getCachedResource(paramURL1, null);
      if (localCacheEntry == null)
        return null;
      LocalApplicationProperties localLocalApplicationProperties = Cache.getLocalApplicationProperties(localCacheEntry);
      if (localLocalApplicationProperties != null)
        return buildDescriptor(localCacheEntry.getDataFile(), localLocalApplicationProperties, paramURL2, paramURL3);
      return buildDescriptor(localCacheEntry.getDataFile(), paramURL2, paramURL3, paramURL1);
    }
    catch (IOException localIOException)
    {
    }
    return null;
  }

  public static LaunchDesc buildDescriptor(String paramString, URL paramURL1, URL paramURL2, boolean paramBoolean)
    throws BadFieldException, MissingFieldException, JNLParseException
  {
    URL localURL = null;
    try
    {
      localURL = new URL(paramString);
    }
    catch (Exception localException1)
    {
      localURL = null;
    }
    if (localURL != null)
      try
      {
        LaunchDesc localLaunchDesc1 = buildDescriptor(localURL, paramURL2);
        if (paramBoolean)
          System.out.println("   JNLP Ref (absolute): " + localURL.toString());
        return localLaunchDesc1;
      }
      catch (BadFieldException localBadFieldException1)
      {
        throw localBadFieldException1;
      }
      catch (MissingFieldException localMissingFieldException1)
      {
        throw localMissingFieldException1;
      }
      catch (JNLParseException localJNLParseException1)
      {
        throw localJNLParseException1;
      }
      catch (Exception localException2)
      {
        if (paramBoolean)
        {
          System.out.println(localException2);
          localException2.printStackTrace();
        }
        localURL = null;
      }
    if (paramURL1 != null)
    {
      try
      {
        localURL = new URL(paramURL1, paramString);
      }
      catch (Exception localException3)
      {
        localURL = null;
      }
      if (localURL != null)
        try
        {
          LaunchDesc localLaunchDesc2 = buildDescriptor(localURL, paramURL2);
          if (paramBoolean)
            System.out.println("   JNLP Ref (codebase + ref): " + localURL.toString());
          return localLaunchDesc2;
        }
        catch (BadFieldException localBadFieldException2)
        {
          throw localBadFieldException2;
        }
        catch (MissingFieldException localMissingFieldException2)
        {
          throw localMissingFieldException2;
        }
        catch (JNLParseException localJNLParseException2)
        {
          throw localJNLParseException2;
        }
        catch (Exception localException4)
        {
          if (paramBoolean)
          {
            System.out.println(localException4);
            localException4.printStackTrace();
          }
          localURL = null;
        }
    }
    if ((paramURL1 == null) && (paramURL2 != null))
    {
      try
      {
        localURL = new URL(URLUtil.getBase(paramURL2), paramString);
      }
      catch (Exception localException5)
      {
        localURL = null;
      }
      if (localURL != null)
        try
        {
          LaunchDesc localLaunchDesc3 = buildDescriptor(localURL, paramURL2);
          if (paramBoolean)
            System.out.println("   JNLP Ref (documentbase + ref): " + localURL.toString());
          return localLaunchDesc3;
        }
        catch (BadFieldException localBadFieldException3)
        {
          throw localBadFieldException3;
        }
        catch (MissingFieldException localMissingFieldException3)
        {
          throw localMissingFieldException3;
        }
        catch (JNLParseException localJNLParseException3)
        {
          throw localJNLParseException3;
        }
        catch (Exception localException6)
        {
          if (paramBoolean)
          {
            System.out.println(localException6);
            localException6.printStackTrace();
          }
          localURL = null;
        }
    }
    if (paramBoolean)
      System.out.println("   JNLP Ref (...): NULL !");
    return null;
  }

  public static LaunchDesc buildDescriptor(String paramString)
    throws IOException, BadFieldException, MissingFieldException, JNLParseException
  {
    FileInputStream localFileInputStream = null;
    int i = -1;
    try
    {
      URL localURL1 = new URL(paramString);
      localObject = null;
      try
      {
        localObject = buildDescriptorFromCache(localURL1, null);
      }
      catch (Exception localException)
      {
      }
      if (localObject != null)
        return localObject;
      return buildDescriptor(localURL1, null);
    }
    catch (MalformedURLException localMalformedURLException1)
    {
      Object localObject = new File(paramString);
      if ((!((File)localObject).exists()) && (!Config.isJavaVersionAtLeast14()) && (localMalformedURLException1.getMessage().indexOf("https") != -1))
        throw new BadFieldException(ResourceManager.getString("launch.error.badfield.download.https"), "<jnlp>", "https");
      localFileInputStream = new FileInputStream(paramString);
      long l = ((File)localObject).length();
      if (l > 1048576L)
        throw new IOException("File too large");
      i = (int)l;
      if (Environment.isImportMode())
      {
        String str = ((File)localObject).getParent();
        if ((Environment.getImportModeCodebaseOverride() == null) && (str != null))
          try
          {
            URL localURL2 = new URL("file", null, URLUtil.encodePath(str));
            Environment.setImportModeCodebaseOverride(localURL2.toString());
          }
          catch (MalformedURLException localMalformedURLException2)
          {
            Trace.ignoredException(localMalformedURLException2);
          }
      }
    }
    return buildDescriptor(readBytes(localFileInputStream, i), null, null);
  }

  public static LaunchDesc buildInternalLaunchDesc(XMLNode paramXMLNode, String paramString)
  {
    return new LaunchDesc("0.1", null, null, null, null, 1, null, null, null, 5, null, null, null, null, null, paramString == null ? paramXMLNode.getName() : paramString, paramXMLNode, new DefaultMatchJRE());
  }

  public static byte[] readBytes(InputStream paramInputStream, long paramLong)
    throws IOException
  {
    if (paramLong > 1048576L)
      throw new IOException("File too large");
    BufferedInputStream localBufferedInputStream = null;
    if ((paramInputStream instanceof BufferedInputStream))
      localBufferedInputStream = (BufferedInputStream)paramInputStream;
    else
      localBufferedInputStream = new BufferedInputStream(paramInputStream);
    if (paramLong <= 0L)
      paramLong = 10240L;
    Object localObject = new byte[(int)paramLong];
    int j = 0;
    byte[] arrayOfByte;
    for (int i = localBufferedInputStream.read((byte[])localObject, j, localObject.length - j); i != -1; i = localBufferedInputStream.read((byte[])localObject, j, localObject.length - j))
    {
      j += i;
      if (localObject.length == j)
      {
        arrayOfByte = new byte[localObject.length * 2];
        System.arraycopy(localObject, 0, arrayOfByte, 0, localObject.length);
        localObject = arrayOfByte;
      }
    }
    localBufferedInputStream.close();
    paramInputStream.close();
    if (j != localObject.length)
    {
      arrayOfByte = new byte[j];
      System.arraycopy(localObject, 0, arrayOfByte, 0, j);
      localObject = arrayOfByte;
    }
    return localObject;
  }

  public static LaunchDesc tryUpdateDescriptor(LaunchDesc paramLaunchDesc)
  {
    URL localURL = paramLaunchDesc.getSourceURL();
    try
    {
      if (localURL != null)
        return buildDescriptor(localURL, null);
    }
    catch (Exception localException)
    {
      Trace.ignored(localException);
    }
    return paramLaunchDesc;
  }
}

/* Location:           /home/user1/Temp/jvm/javaws.jar
 * Qualified Name:     com.sun.javaws.jnl.LaunchDescFactory
 * JD-Core Version:    0.6.2
 */