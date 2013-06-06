package com.sun.deploy.net;

import com.sun.deploy.cache.Cache;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.trace.TraceLevel;
import com.sun.deploy.util.URLUtil;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class HttpUtils
{
  private static final String CONNECTION_HEADER = "Connection";
  public static final String LOCATION_HEADER = "Location";
  private static final String CONNECTION_KEEP_ALIVE = "Keep-Alive";
  private static final String PROTOCOL_VERSION_1_1 = "HTTP/1.1";
  private static final HashSet isNotCacheable = new HashSet();

  public static HttpURLConnection followRedirects(URLConnection paramURLConnection)
    throws IOException
  {
    int j = 0;
    InputStream localInputStream = null;
    int i;
    do
    {
      if ((paramURLConnection instanceof HttpURLConnection))
        ((HttpURLConnection)paramURLConnection).setInstanceFollowRedirects(false);
      localInputStream = paramURLConnection.getInputStream();
      i = 0;
      if ((paramURLConnection instanceof HttpURLConnection))
      {
        HttpURLConnection localHttpURLConnection = (HttpURLConnection)paramURLConnection;
        int k = localHttpURLConnection.getResponseCode();
        if (isRedirect(k))
        {
          URL localURL1 = localHttpURLConnection.getURL();
          String str = localHttpURLConnection.getHeaderField("Location");
          URL localURL2 = null;
          if (str != null)
            localURL2 = new URL(localURL1, str);
          cleanupConnection(localHttpURLConnection);
          if ((localURL2 == null) || (!localURL1.getProtocol().equals(localURL2.getProtocol())) || (j >= 5))
            throw new SecurityException("illegal URL redirect");
          i = 1;
          paramURLConnection = localURL2.openConnection();
          j++;
        }
      }
    }
    while (i != 0);
    if (!(paramURLConnection instanceof HttpURLConnection))
      throw new IOException(paramURLConnection.getURL() + " redirected to non-http URL");
    return (HttpURLConnection)paramURLConnection;
  }

  public static URL removeQueryStringFromURL(URL paramURL)
  {
    URL localURL = paramURL;
    if (localURL != null)
    {
      String str = localURL.toString();
      int i = str.lastIndexOf('?');
      if (i != -1)
        try
        {
          localURL = new URL(str.substring(0, i));
        }
        catch (MalformedURLException localMalformedURLException)
        {
          Trace.ignoredException(localMalformedURLException);
        }
    }
    return localURL;
  }

  public static URL removeVersionQueriesFromURL(URL paramURL)
  {
    if (paramURL != null)
    {
      String str1 = paramURL.toString();
      int i = str1.lastIndexOf('?');
      if (i > -1)
      {
        StringBuffer localStringBuffer = new StringBuffer(str1.substring(0, i + 1));
        String[] arrayOfString1 = str1.substring(i + 1).split("&");
        int j = 0;
        for (int k = 0; k < arrayOfString1.length; k++)
        {
          String str2 = arrayOfString1[k];
          String[] arrayOfString2 = str2.split("=");
          if ((arrayOfString2.length <= 0) || ((!"version-id".equals(arrayOfString2[0])) && (!"current-version-id".equals(arrayOfString2[0]))))
          {
            if (j != 0)
              localStringBuffer.append('&');
            else
              j = 1;
            localStringBuffer.append(str2);
          }
        }
        try
        {
          return new URL(localStringBuffer.toString());
        }
        catch (MalformedURLException localMalformedURLException)
        {
          Trace.ignoredException(localMalformedURLException);
        }
      }
    }
    return paramURL;
  }

  public static boolean sameURLsIgnoreVersionQueries(URL paramURL1, URL paramURL2)
  {
    paramURL1 = removeVersionQueriesFromURL(paramURL1);
    paramURL2 = removeVersionQueriesFromURL(paramURL2);
    return URLUtil.sameURLs(paramURL1, paramURL2);
  }

  public static void cleanupConnection(URLConnection paramURLConnection)
  {
    if ((paramURLConnection == null) || (!(paramURLConnection instanceof HttpURLConnection)))
      return;
    try
    {
      HttpURLConnection localHttpURLConnection = (HttpURLConnection)paramURLConnection;
      String str1 = localHttpURLConnection.getHeaderField(null);
      String str2 = localHttpURLConnection.getHeaderField("Connection");
      if (((str2 != null) && (str2.equalsIgnoreCase("Keep-Alive"))) || ((str1 != null) && (str1.startsWith("HTTP/1.1")) && (str2 == null)))
      {
        int i = localHttpURLConnection.getResponseCode();
        if (i < 400)
        {
          InputStream localInputStream = localHttpURLConnection.getInputStream();
          if (localInputStream != null)
          {
            byte[] arrayOfByte = new byte[8192];
            while (localInputStream.read(arrayOfByte) > 0);
            localInputStream.close();
          }
        }
      }
    }
    catch (IOException localIOException)
    {
    }
  }

  public static boolean isRedirect(int paramInt)
  {
    return (paramInt >= 300) && (paramInt <= 305) && (paramInt != 304);
  }

  public static URL getFinalRedirectedURL(HttpResponse paramHttpResponse)
  {
    if ((paramHttpResponse instanceof BasicHttpResponse))
      return ((BasicHttpResponse)paramHttpResponse).getFinalURL();
    return null;
  }

  static boolean hasContentEncoding(Map paramMap, String paramString)
  {
    paramString = paramString.toLowerCase(Locale.US);
    Set localSet = getEncodingTokens(paramMap, false);
    return localSet.contains(paramString);
  }

  public static boolean matchEncoding(String paramString, Map paramMap1, Map paramMap2)
  {
    paramString = paramString == null ? null : paramString.toLowerCase(Locale.US);
    Set localSet1 = getEncodingTokens(paramMap1, true);
    Set localSet2 = getEncodingTokens(paramMap2, false);
    if ((paramString != null) && (!localSet1.contains(paramString)))
      return true;
    if (paramString != null)
      return localSet2.contains(paramString);
    Iterator localIterator = localSet1.iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      if (("*".equals(str)) || (localSet2.contains(str)))
        return true;
    }
    return false;
  }

  private static Set getEncodingTokens(Map paramMap, boolean paramBoolean)
  {
    List localList = (List)paramMap.get(paramBoolean ? "accept-encoding" : "content-encoding");
    HashSet localHashSet = new HashSet();
    if (localList != null)
    {
      Iterator localIterator = localList.iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        String[] arrayOfString = str.split(",");
        for (int i = 0; i < arrayOfString.length; i++)
          localHashSet.add(arrayOfString[i].trim().toLowerCase(Locale.US));
      }
    }
    return localHashSet;
  }

  public static boolean hasGzipOrPack200Encoding(URLConnection paramURLConnection)
  {
    String str = paramURLConnection.getRequestProperty("accept-encoding");
    if (str != null)
      str = str.toLowerCase(Locale.US);
    return (str != null) && ((str.indexOf("gzip") > -1) || (str.indexOf("pack200-gzip") > -1));
  }

  public static boolean refusesIdentityEncodings(Map paramMap)
  {
    Set localSet = getEncodingTokens(paramMap, true);
    return (localSet.contains("identity;q=0")) || ((localSet.contains("*;q=0")) && (!localSet.contains("identity")));
  }

  public static boolean refusesIdentityEncodings(URLConnection paramURLConnection)
  {
    String str = paramURLConnection.getRequestProperty("accept-encoding");
    if (str == null)
      return false;
    return (str.indexOf("identity;q=0") > -1) || ((str.indexOf("*;q=0") > -1) && (str.indexOf("identity") == -1));
  }

  public static boolean hasGzipOrPack200Encoding(Map paramMap)
  {
    return (hasContentEncoding(paramMap, "gzip")) || (hasContentEncoding(paramMap, "pack200-gzip"));
  }

  public static boolean hasGzipEncoding(String paramString)
  {
    return (paramString != null) && (paramString.toLowerCase(Locale.US).indexOf("gzip") > -1);
  }

  static String removeGzipEncoding(String paramString)
  {
    if (paramString == null)
      return null;
    String[] arrayOfString = paramString.split(",");
    StringBuffer localStringBuffer = new StringBuffer();
    int i = 0;
    for (int j = 0; j < arrayOfString.length; j++)
      if (!arrayOfString[j].trim().toLowerCase(Locale.US).equals("gzip"))
      {
        if (i != 0)
          localStringBuffer.append(", ");
        else
          i = 1;
        localStringBuffer.append(arrayOfString[j].trim());
      }
    return localStringBuffer.length() > 0 ? localStringBuffer.toString() : null;
  }

  public static long getEffectiveExpiration(long paramLong, MessageHeader paramMessageHeader)
  {
    Map localMap = paramMessageHeader.getHeaders();
    Object localObject = localMap.get("cache-control");
    Long localLong = null;
    if ((localObject instanceof List))
    {
      List localList = (List)localObject;
      for (int i = 0; i < localList.size(); i++)
        if ((localList.get(i) instanceof String))
        {
          String[] arrayOfString = ((String)localList.get(i)).split("=");
          if ((arrayOfString != null) && (arrayOfString.length == 2))
          {
            String str1 = arrayOfString[0];
            String str2 = arrayOfString[1];
            if ("max-age".equalsIgnoreCase(str1))
              try
              {
                localLong = Long.valueOf(str2);
              }
              catch (Exception localException)
              {
              }
          }
        }
    }
    if (localLong != null)
      return System.currentTimeMillis() + localLong.longValue() * 1000L;
    return paramLong;
  }

  public static long getEffectiveExpiration(HttpResponse paramHttpResponse)
  {
    return getEffectiveExpiration(paramHttpResponse.getExpiration(), paramHttpResponse.getHeaders());
  }

  public static boolean isResourceCacheable(String paramString, MessageHeader paramMessageHeader, boolean paramBoolean)
  {
    if (!Cache.isCacheEnabled())
      return false;
    Map localMap = paramMessageHeader.getHeaders();
    if (localMap.get("content-range") != null)
      return false;
    Object localObject1 = localMap.get("cache-control");
    if ((localObject1 instanceof List))
    {
      List localList = (List)localObject1;
      for (int i = 0; i < localList.size(); i++)
        if ((localList.get(i) instanceof String))
        {
          String str = (String)localList.get(i);
          if (str.toLowerCase(Locale.US).indexOf("no-store") > -1)
            return false;
        }
    }
    if ((paramBoolean) && (!hasValueFor(localMap, "last-modified")) && (!hasValueFor(localMap, "expires")) && (localObject1 == null))
    {
      synchronized (isNotCacheable)
      {
        isNotCacheable.add(paramString);
        Trace.println(paramString + " is not cacheable.", TraceLevel.CACHE);
      }
      return false;
    }
    return true;
  }

  public static boolean resourceNotCached(String paramString)
  {
    synchronized (isNotCacheable)
    {
      return isNotCacheable.contains(paramString);
    }
  }

  private static boolean hasValueFor(Map paramMap, String paramString)
  {
    Object localObject = paramMap.get(paramString);
    if ((localObject instanceof List))
    {
      List localList = (List)localObject;
      if (localList.size() > 0)
        return true;
    }
    else if ((localObject instanceof String))
    {
      return true;
    }
    return false;
  }

  private static boolean hasMimeType(Map paramMap, String paramString)
  {
    Object localObject = paramMap.get("content-type");
    if (!(localObject instanceof List))
      return false;
    List localList = (List)localObject;
    return localList.contains(paramString);
  }

  private static boolean hasRequestType(Map paramMap, String paramString)
  {
    Object localObject = paramMap.get("deploy-request-content-type");
    if (!(localObject instanceof List))
      return false;
    List localList = (List)localObject;
    return localList.contains(paramString);
  }

  public static boolean isJarFile(String paramString, Map paramMap)
  {
    String str = paramString;
    if (hasRequestType(paramMap, "application/x-java-archive"))
      return true;
    int i;
    if ((i = str.indexOf(";")) != -1)
      str = str.substring(0, i);
    if ((i = str.indexOf("?")) != -1)
      str = str.substring(0, i);
    return (str.toLowerCase().endsWith(".jar")) || (str.toLowerCase().endsWith(".jarjar")) || (hasMimeType(paramMap, "application/x-java-archive")) || (hasMimeType(paramMap, "application/java-archive")) || (hasMimeType(paramMap, "application/x-java-archive-diff"));
  }

  public static boolean isJNLPFile(String paramString, MessageHeader paramMessageHeader)
  {
    Map localMap = paramMessageHeader == null ? Collections.EMPTY_MAP : paramMessageHeader.getHeaders();
    return isJNLPFile(paramString, localMap);
  }

  public static boolean isJNLPFile(String paramString, Map paramMap)
  {
    if ((hasRequestType(paramMap, "application/x-java-jnlp-file")) || (hasMimeType(paramMap, "application/x-java-jnlp-file")))
      return true;
    String str = paramString;
    int i;
    if ((i = str.indexOf(";")) != -1)
      str = str.substring(0, i);
    if ((i = str.indexOf("?")) != -1)
      str = str.substring(0, i);
    return (str.toLowerCase(Locale.US).endsWith(".jnlp")) || (str.toLowerCase(Locale.US).endsWith(".jarjnlp"));
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.net.HttpUtils
 * JD-Core Version:    0.6.2
 */