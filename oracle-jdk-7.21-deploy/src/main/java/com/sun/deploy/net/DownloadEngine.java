package com.sun.deploy.net;

import com.sun.applet2.preloader.CancelException;
import com.sun.deploy.Environment;
import com.sun.deploy.cache.Cache;
import com.sun.deploy.cache.CacheEntry;
import com.sun.deploy.cache.MemoryCache;
import com.sun.deploy.config.Config;
import com.sun.deploy.jardiff.JarDiffPatcher;
import com.sun.deploy.jardiff.Patcher;
import com.sun.deploy.jardiff.Patcher.PatchDelegate;
import com.sun.deploy.model.DownloadDelegate;
import com.sun.deploy.model.Resource;
import com.sun.deploy.resources.ResourceManager;
import com.sun.deploy.security.JarVerifier;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.trace.TraceLevel;
import com.sun.deploy.util.SystemUtils;
import com.sun.deploy.util.URLUtil;
import com.sun.deploy.util.VersionID;
import com.sun.deploy.util.VersionString;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.CodeSigner;
import java.security.PrivilegedAction;
import java.security.cert.Certificate;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.jar.JarFile;

public class DownloadEngine
{
  private static final String ARG_ARCH = "arch";
  private static final String ARG_OS = "os";
  private static final String ARG_LOCALE = "locale";
  private static final String ARG_VERSION_ID = "version-id";
  private static final String ARG_JFX_VERSION_ID = "jfx-version-id";
  public static final String ARG_CURRENT_VERSION_ID = "current-version-id";
  private static final String ARG_PLATFORM_VERSION_ID = "platform-version-id";
  private static final String ARG_KNOWN_PLATFORMS = "known-platforms";
  private static final String REPLY_JNLP_VERSION = "x-java-jnlp-version-id";
  private static final String ERROR_MIME_TYPE = "application/x-java-jnlp-error";
  private static final String JARDIFF_MIME_TYPE = "application/x-java-archive-diff";
  private static final String JNLP_MIME_TYPE = "application/x-java-jnlp-file";
  private static final int BASIC_DOWNLOAD_PROTOCOL = 1;
  private static final int VERSION_DOWNLOAD_PROTOCOL = 2;
  private static final int EXTENSION_DOWNLOAD_PROTOCOL = 3;
  private static int BUF_SIZE = 8192;
  private static final String defaultLocaleString = Locale.getDefault().toString();
  private static HttpRequest _httpRequestImpl = new BasicHttpRequest();
  private static HttpDownload _httpDownloadImpl = new HttpDownloadHelper(_httpRequestImpl);
  private static InheritableThreadLocal backgroundUpdateThreadLocal = new InheritableThreadLocal()
  {
    protected Object initialValue()
    {
      return Boolean.FALSE;
    }
  };
  private static ThreadLocal processingState = new ThreadLocal();
  private static final ThreadLocal internalUse = new ThreadLocal();
  private static Hashtable noCacheRedirectFinalURLs = new Hashtable();

  public static boolean haveDownloadInProgress()
  {
    return processingState.get() != null;
  }

  private static void setDownloadInProgress(boolean paramBoolean)
  {
    if (paramBoolean)
      processingState.set(Boolean.TRUE);
    else
      processingState.set(null);
  }

  public static boolean isBackgroundUpdateRequest()
  {
    return ((Boolean)backgroundUpdateThreadLocal.get()).booleanValue();
  }

  public static void _setBackgroundUpdateRequest(boolean paramBoolean)
  {
    backgroundUpdateThreadLocal.set(new Boolean(paramBoolean));
  }

  public static int _incrementInternalUse()
  {
    int i = getInternalUseLevel() + 1;
    SystemUtils.setThreadLocalInt(internalUse, i);
    return i;
  }

  public static void _decrementInternalUse(int paramInt)
  {
    int i = getInternalUseLevel();
    if (i != paramInt)
    {
      if (Trace.isEnabled(TraceLevel.NETWORK))
        Trace.ignored(new Exception("WARNING: unbalanced internalUse level: expect=" + paramInt + " : got=" + i));
      i = paramInt;
    }
    i--;
    i = i;
    if (i < 0)
      i = 0;
    SystemUtils.setThreadLocalInt(internalUse, i);
  }

  public static boolean isInternalUse()
  {
    return getInternalUseLevel() > 0;
  }

  private static int getInternalUseLevel()
  {
    return SystemUtils.getThreadLocalInt(internalUse);
  }

  public static boolean isNativeContentType(int paramInt)
  {
    return hasBit(paramInt, 16);
  }

  public static boolean isJarContentType(int paramInt)
  {
    return hasBit(paramInt, 256);
  }

  public static boolean isPackContentType(int paramInt)
  {
    return hasBit(paramInt, 4096);
  }

  private static boolean isVersionContentType(int paramInt)
  {
    return hasBit(paramInt, 65536);
  }

  private static boolean hasBit(int paramInt1, int paramInt2)
  {
    return (paramInt1 & paramInt2) == paramInt2;
  }

  static boolean isPack200Supported()
  {
    return Config.isJavaVersionAtLeast15();
  }

  static boolean isZipFile(String paramString)
  {
    return paramString.toLowerCase().endsWith(".zip");
  }

  public static boolean isAlwaysCached(String paramString)
  {
    String str = paramString.toLowerCase();
    return (str.endsWith(".jar")) || (str.endsWith(".jarjar")) || (str.endsWith(".zip"));
  }

  public static URL getNonCacheRedirectFinalURL(URL paramURL)
  {
    return (URL)noCacheRedirectFinalURLs.get(paramURL.toString());
  }

  public static URL getKnownRedirectFinalURL(URL paramURL)
  {
    URL localURL = null;
    Object localObject = MemoryCache.getLoadedResource(paramURL.toString());
    if ((localObject instanceof CacheEntry))
    {
      CacheEntry localCacheEntry = (CacheEntry)localObject;
      try
      {
        localURL = new URL(localCacheEntry.getURL());
      }
      catch (MalformedURLException localMalformedURLException)
      {
        Trace.ignored(localMalformedURLException);
      }
    }
    else
    {
      localURL = getNonCacheRedirectFinalURL(paramURL);
    }
    if (localURL == null)
      localURL = paramURL;
    return localURL;
  }

  public static void clearNoCacheJarFileList()
  {
    noCacheRedirectFinalURLs.clear();
  }

  private static Resource downloadResourceToTempDir(URL paramURL1, String paramString, URL paramURL2, HttpResponse paramHttpResponse, HttpDownloadListener paramHttpDownloadListener, int paramInt, DownloadDelegate paramDownloadDelegate)
    throws IOException
  {
    URL localURL = HttpUtils.getFinalRedirectedURL(paramHttpResponse);
    if ((localURL != null) && (!URLUtil.sameURLs(localURL, paramURL2)))
      noCacheRedirectFinalURLs.put(paramURL1.toString(), localURL);
    boolean bool1 = isJarContentType(paramInt);
    String str1 = bool1 ? "jar_cache" : "tmp_cache";
    File localFile = File.createTempFile(str1, null);
    localFile.deleteOnExit();
    String str2 = paramHttpResponse.getContentEncoding();
    if (isPackContentType(paramInt))
      str2 = "pack200-gzip";
    boolean bool2 = isInternalUse();
    try
    {
      BufferedInputStream localBufferedInputStream = paramHttpResponse.getInputStream();
      MessageHeader localMessageHeader = getHttpDownloadImpl().download(paramHttpResponse.getContentLength(), paramHttpResponse.getRequest(), localBufferedInputStream, str2, localFile, paramHttpDownloadListener, paramInt, bool2);
      localMessageHeader = MessageHeader.merge(paramHttpResponse.getHeaders(), localMessageHeader);
      if (!bool1)
        bool1 = HttpUtils.isJarFile(paramURL1.toString(), localMessageHeader.getHeaders());
      Resource localResource = createTemporaryResource(paramURL1, paramString, paramURL2, bool1, paramHttpResponse, localFile, localMessageHeader, paramDownloadDelegate);
      return localResource;
    }
    catch (CanceledDownloadException localCanceledDownloadException)
    {
      throw new IOException(localCanceledDownloadException.getLocalizedMessage());
    }
    finally
    {
      paramHttpResponse.disconnect();
    }
  }

  private static Resource createTemporaryResource(URL paramURL1, String paramString, URL paramURL2, boolean paramBoolean, HttpResponse paramHttpResponse, File paramFile, MessageHeader paramMessageHeader, DownloadDelegate paramDownloadDelegate)
    throws IOException
  {
    if ((paramURL1 == null) || (paramFile == null))
      return null;
    Map localMap = paramMessageHeader != null ? paramMessageHeader.getHeaders() : Collections.EMPTY_MAP;
    JarFile localJarFile = paramBoolean ? JarVerifier.getValidatedJarFile(paramFile, paramURL1, paramURL2, paramString, paramDownloadDelegate) : null;
    long l = System.currentTimeMillis();
    return new Resource()
    {
      private final URL val$url;
      private final String val$version;
      private final HttpResponse val$response;
      private final Map val$headers;
      private final File val$tmpFile;
      private final JarFile val$jarFile;
      private final long val$timestamp;
      private final boolean val$isJar;

      public String getURL()
      {
        return this.val$url.toString();
      }

      public String getVersion()
      {
        return this.val$version;
      }

      public long getLastModified()
      {
        return this.val$response == null ? 0L : this.val$response.getLastModified();
      }

      public long getExpirationDate()
      {
        return this.val$response == null ? 0L : this.val$response.getExpiration();
      }

      public int getContentLength()
      {
        return this.val$response == null ? 0 : this.val$response.getContentLength();
      }

      public Map getHeaders()
      {
        return this.val$headers;
      }

      public long getSize()
      {
        Long localLong = (Long)AccessController.doPrivileged(new PrivilegedAction()
        {
          public Object run()
          {
            return SystemUtils.longValueOf(DownloadEngine.2.this.val$tmpFile.length());
          }
        });
        return localLong.longValue();
      }

      public File getDataFile()
      {
        return this.val$tmpFile;
      }

      public int getState()
      {
        return 1;
      }

      public JarFile getJarFile()
      {
        return this.val$jarFile;
      }

      public long getValidationTimestamp()
      {
        return this.val$timestamp;
      }

      public boolean isKnownToBeSigned()
      {
        return false;
      }

      public String getResourceFilename()
      {
        return this.val$tmpFile.getPath();
      }

      public boolean isJNLPFile()
      {
        return false;
      }

      public boolean isJarFile()
      {
        return this.val$isJar;
      }

      public void updateValidationResults(boolean paramAnonymousBoolean1, Map paramAnonymousMap, long paramAnonymousLong1, long paramAnonymousLong2, boolean paramAnonymousBoolean2)
      {
      }

      public CodeSigner[] getCodeSigners()
      {
        return new CodeSigner[0];
      }

      public Certificate[] getCertificates()
      {
        return new Certificate[0];
      }

      public Map getCachedTrustedEntries()
      {
        return Collections.EMPTY_MAP;
      }

      public byte getClassesVerificationStatus()
      {
        return 0;
      }
    };
  }

  public static HttpRequest getHttpRequestImpl()
  {
    return _httpRequestImpl;
  }

  public static HttpDownload getHttpDownloadImpl()
  {
    return _httpDownloadImpl;
  }

  public static File applyPatch(File paramFile1, File paramFile2, URL paramURL, String paramString1, DownloadDelegate paramDownloadDelegate, String paramString2)
    throws FailedDownloadException
  {
    JarDiffPatcher localJarDiffPatcher = new JarDiffPatcher();
    File localFile = new File(paramString2);
    FileOutputStream localFileOutputStream = null;
    int i = 0;
    try
    {
      localFileOutputStream = new FileOutputStream(localFile);
      Patcher.PatchDelegate local3 = null;
      if (paramDownloadDelegate != null)
      {
        paramDownloadDelegate.patching(paramURL, paramString1, 0);
        local3 = new Patcher.PatchDelegate()
        {
          private final DownloadDelegate val$delegate;
          private final URL val$location;
          private final String val$newVersion;

          public void patching(int paramAnonymousInt)
            throws CancelException
          {
            this.val$delegate.patching(this.val$location, this.val$newVersion, paramAnonymousInt);
          }
        };
      }
      try
      {
        localJarDiffPatcher.applyPatch(local3, paramFile1.getPath(), paramFile2.getPath(), localFileOutputStream);
      }
      catch (IOException localIOException2)
      {
        throw new FailedDownloadException(paramURL, paramString1, localIOException2);
      }
      i = 1;
    }
    catch (IOException localIOException1)
    {
      throw new FailedDownloadException(paramURL, paramString1, localIOException1);
    }
    finally
    {
      try
      {
        if (localFileOutputStream != null)
          localFileOutputStream.close();
      }
      catch (IOException localIOException3)
      {
        Trace.ignoredException(localIOException3);
      }
      if (i == 0)
        localFile.delete();
      paramFile2.delete();
      if ((paramDownloadDelegate != null) && (i == 0))
        try
        {
          paramDownloadDelegate.downloadFailed(paramURL, paramString1);
        }
        catch (CancelException localCancelException)
        {
          throw new FailedDownloadException(paramURL, paramString1, localCancelException);
        }
    }
    return localFile;
  }

  public static boolean isJnlpURL(URL paramURL)
  {
    try
    {
      HttpResponse localHttpResponse = getHttpRequestImpl().doHeadRequest(paramURL);
      return "application/x-java-jnlp-file".equals(localHttpResponse.getContentType());
    }
    catch (IOException localIOException)
    {
    }
    return false;
  }

  private static String getVersionJarPath(String paramString1, String paramString2)
  {
    String str1 = paramString1.substring(paramString1.lastIndexOf("/") + 1);
    paramString1 = paramString1.substring(0, paramString1.lastIndexOf("/") + 1);
    String str2 = str1;
    String str3 = null;
    if (str1.lastIndexOf(".") != -1)
    {
      str3 = str1.substring(str1.lastIndexOf(".") + 1);
      str1 = str1.substring(0, str1.lastIndexOf("."));
    }
    StringBuffer localStringBuffer = new StringBuffer(str1);
    if (paramString2 != null)
    {
      localStringBuffer.append("__V");
      localStringBuffer.append(paramString2);
    }
    if (str3 != null)
    {
      localStringBuffer.append(".");
      localStringBuffer.append(str3);
    }
    paramString1 = paramString1 + localStringBuffer.toString();
    return paramString1;
  }

  private static URL getEmbeddedVersionUrl(URL paramURL, String paramString)
  {
    if ((paramString == null) || (paramString.indexOf("*") != -1) || (paramString.indexOf("+") != -1))
      return paramURL;
    URL localURL = null;
    String str1 = paramURL.getProtocol();
    String str2 = paramURL.getHost();
    int i = paramURL.getPort();
    String str3 = paramURL.getPath();
    str3 = getVersionJarPath(str3, paramString);
    try
    {
      localURL = new URL(str1, str2, i, str3);
    }
    catch (MalformedURLException localMalformedURLException)
    {
      Trace.ignoredException(localMalformedURLException);
    }
    return localURL;
  }

  static Resource actionDownload(Resource paramResource, URL paramURL1, URL paramURL2, String paramString, DownloadDelegate paramDownloadDelegate, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
    throws IOException
  {
    int i = 0;
    int j = -1;
    String str1 = null;
    if ((paramResource != null) && (paramString != null))
    {
      if ((paramResource.getVersion() != null) && (new VersionString(paramString).contains(new VersionID(paramResource.getVersion()))))
        return paramResource;
      if (Environment.isJavaPlugin())
      {
        long l1 = paramResource.getExpirationDate();
        if ((l1 != 0L) && (new Date().after(new Date(l1))))
          paramResource = null;
      }
    }
    if (paramResource != null)
      str1 = paramResource.getVersion();
    try
    {
      setDownloadInProgress(true);
      HttpRequest localHttpRequest = getHttpRequestImpl();
      HttpResponse localHttpResponse = null;
      long l2 = 0L;
      if (paramResource != null)
        l2 = paramResource.getLastModified();
      URL localURL = paramURL2;
      if (isVersionContentType(paramInt))
        localURL = getEmbeddedVersionUrl(localURL, paramString);
      if (isPackContentType(paramInt))
        localURL = URLUtil.getPack200URL(localURL, false);
      try
      {
        try
        {
          localHttpResponse = localHttpRequest.doGetRequestEX(localURL, l2);
        }
        catch (FileNotFoundException localFileNotFoundException1)
        {
          if (paramURL2.toString().equals(localURL.toString()))
            throw localFileNotFoundException1;
          localHttpResponse = localHttpRequest.doGetRequestEX(paramURL2, l2);
          i = 1;
          j = paramInt;
          if ((isPackContentType(paramInt)) && ((isNativeContentType(paramInt)) || (isJarContentType(paramInt))))
            j &= -4097;
          if (isVersionContentType(paramInt))
            j &= -65537;
        }
      }
      catch (FailedDownloadException localFailedDownloadException)
      {
        throw localFailedDownloadException;
      }
      catch (FileNotFoundException localFileNotFoundException2)
      {
        throw localFileNotFoundException2;
      }
      catch (IOException localIOException)
      {
        Trace.ignoredException(localIOException);
        localHttpResponse = localHttpRequest.doGetRequest(paramURL2, false);
      }
      int k = localHttpResponse.getStatusCode();
      if (k == 404)
        throw new FailedDownloadException(paramURL1, paramString, new IOException("HTTP response 404"));
      if (k == 304)
      {
        localHttpResponse.disconnect();
        Resource localResource = paramResource;
        return localResource;
      }
      int m = localHttpResponse.getContentLength();
      long l3 = localHttpResponse.getLastModified();
      long l4 = localHttpResponse.getExpiration();
      boolean bool1 = HttpUtils.isJNLPFile(paramURL1.toString(), localHttpResponse.getHeaders());
      if ((!bool1) && (Environment.isImportMode()) && (Environment.getImportModeExpiration() != null) && (l4 != 0L))
      {
        localObject1 = new Date(l4);
        if (((Date)localObject1).before(Environment.getImportModeExpiration()))
        {
          localHttpResponse.disconnect();
          localObject2 = null;
          return localObject2;
        }
      }
      else if ((!bool1) && (Environment.isImportMode()) && (Environment.getImportModeTimestamp() != null) && (l3 != 0L))
      {
        localObject1 = new Date(l3);
        if (((Date)localObject1).before(Environment.getImportModeTimestamp()))
        {
          localHttpResponse.disconnect();
          localObject2 = null;
          return localObject2;
        }
      }
      Object localObject1 = localHttpResponse.getResponseHeader("x-java-jnlp-version-id");
      if ((paramString != null) && (localObject1 == null) && (Environment.getImportModeCodebaseOverride() != null) && (new VersionID(paramString).isSimpleVersion()))
        localObject1 = paramString;
      if (paramResource != null)
      {
        if ((localObject1 != null) && (new VersionString((String)localObject1).contains(str1)))
        {
          localHttpResponse.disconnect();
          localObject2 = paramResource;
          return localObject2;
        }
        if ((m == paramResource.getContentLength()) && (l3 == l2) && (str1 == null))
        {
          localHttpResponse.disconnect();
          localObject2 = paramResource;
          return localObject2;
        }
      }
      if (paramDownloadDelegate != null)
        paramDownloadDelegate.setTotalSize(m);
      if (localObject1 == null)
        if (!Environment.isJavaPlugin())
        {
          if (!isVersionContentType(i != 0 ? j : paramInt));
        }
        else
          localObject1 = paramString;
      Object localObject2 = localObject1;
      String str2 = localHttpResponse.getContentType();
      boolean bool2 = (str2 != null) && (str2.equalsIgnoreCase("application/x-java-archive-diff"));
      if (Trace.isEnabled(TraceLevel.NETWORK))
        Trace.println(ResourceManager.getString("downloadEngine.serverResponse", String.valueOf(m), new Date(l3).toString(), (String)localObject1, str2), TraceLevel.NETWORK);
      if ((str2 != null) && (str2.equalsIgnoreCase("application/x-java-jnlp-error")))
      {
        localObject3 = localHttpResponse.getInputStream();
        BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader((InputStream)localObject3));
        localObject4 = localBufferedReader.readLine();
        localBufferedReader.close();
        throw new FailedDownloadException(paramURL2, paramString, new IOException("Error returned: " + (String)localObject4));
      }
      if ((localObject1 == null) && (paramString != null))
        if (!isVersionContentType(i != 0 ? j : paramInt))
          throw new FailedDownloadException(paramURL1, paramString, new IOException("missing version response from server"));
      if ((localObject1 != null) && (paramString != null) && (!paramBoolean1))
      {
        if (!new VersionString(paramString).contains((String)localObject1))
          throw new FailedDownloadException(paramURL1, paramString, new IOException("bad version response from server:" + (String)localObject1));
        localObject3 = new VersionID((String)localObject1);
        if (!((VersionID)localObject3).isSimpleVersion())
          throw new FailedDownloadException(paramURL1, paramString, new IOException("bad version response from server:" + (String)localObject1));
      }
      Object localObject3 = paramDownloadDelegate == null ? null : new HttpDownloadListener()
      {
        private final DownloadDelegate val$dd;
        private final URL val$href;
        private final String val$responseVersion;

        public boolean downloadProgress(int paramAnonymousInt1, int paramAnonymousInt2)
          throws CancelException
        {
          this.val$dd.downloading(this.val$href, this.val$responseVersion, paramAnonymousInt1, paramAnonymousInt2, false);
          return true;
        }
      };
      if ((bool1) && (!Cache.isCacheEnabled()))
        paramBoolean2 = false;
      int n = i != 0 ? j : paramInt;
      if (paramBoolean2)
      {
        if (HttpUtils.isResourceCacheable(paramURL2.toString(), localHttpResponse.getHeaders(), false))
        {
          localObject4 = Cache.downloadResourceToCache(paramURL1, (String)localObject1, localHttpResponse, (HttpDownloadListener)localObject3, paramDownloadDelegate, paramURL2, bool2, n);
          return localObject4;
        }
        localObject4 = downloadResourceToTempDir(paramURL1, (String)localObject1, paramURL2, localHttpResponse, (HttpDownloadListener)localObject3, n, paramDownloadDelegate);
        return localObject4;
      }
      Object localObject4 = Cache.downloadResourceToTempFile(paramURL1, (String)localObject1, localHttpResponse, (HttpDownloadListener)localObject3, paramDownloadDelegate, paramURL2, bool2, i != 0 ? j : paramInt);
      ((CacheEntry)localObject4).setBusy(CacheEntry.BUSY_TRUE);
      ((CacheEntry)localObject4).setIncomplete(CacheEntry.INCOMPLETE_TRUE);
      ((CacheEntry)localObject4).updateIndexHeaderOnDisk();
      Object localObject5 = localObject4;
      return localObject5;
    }
    catch (Exception localException)
    {
      if ((localException instanceof JARSigningException))
        throw ((JARSigningException)localException);
      if ((localException instanceof FailedDownloadException))
        throw ((FailedDownloadException)localException);
      Trace.ignored(localException);
      throw new FailedDownloadException(paramURL2, paramString, localException);
    }
    finally
    {
      setDownloadInProgress(false);
    }
  }

  private static void addURLArgument(StringBuffer paramStringBuffer, String paramString1, String paramString2)
  {
    try
    {
      paramStringBuffer.append(URLEncoder.encode(paramString1, "UTF-8"));
      paramStringBuffer.append('=');
      paramStringBuffer.append(URLEncoder.encode(paramString2, "UTF-8"));
      paramStringBuffer.append('&');
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      Trace.ignoredException(localUnsupportedEncodingException);
    }
  }

  public static URL getRequestURL(URL paramURL, String paramString1, String paramString2, boolean paramBoolean, String paramString3)
  {
    int i = paramString1 == null ? 1 : 2;
    return getRequestURL(paramURL, paramString1, paramString2, paramBoolean, paramString3, i);
  }

  private static URL getRequestURL(URL paramURL, String paramString1, String paramString2, boolean paramBoolean, String paramString3, int paramInt, String paramString4)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    if ((paramString2 == null) && (paramString1 != null))
      paramString2 = Cache.getCacheEntryVersion(paramURL);
    if ((paramString1 != null) && (paramInt == 2))
    {
      addURLArgument(localStringBuffer, "version-id", paramString1);
      if (paramString2 != null)
        addURLArgument(localStringBuffer, "current-version-id", paramString2);
    }
    if ((paramString1 != null) && (paramInt == 3))
    {
      if (paramBoolean)
        addURLArgument(localStringBuffer, "platform-version-id", paramString1);
      else
        addURLArgument(localStringBuffer, "version-id", paramString1);
      addURLArgument(localStringBuffer, "arch", Config.getOSArch());
      addURLArgument(localStringBuffer, "os", Config.getOSName());
      addURLArgument(localStringBuffer, "locale", defaultLocaleString);
      if (paramString3 != null)
        addURLArgument(localStringBuffer, "known-platforms", paramString3);
      if (paramString4 != null)
        addURLArgument(localStringBuffer, "jfx-version-id", paramString4);
    }
    if (localStringBuffer.length() > 0)
      localStringBuffer.setLength(localStringBuffer.length() - 1);
    if (localStringBuffer.length() > 0)
      localStringBuffer.insert(0, '?');
    try
    {
      return new URL(paramURL.getProtocol(), paramURL.getHost(), paramURL.getPort(), paramURL.getFile() + localStringBuffer);
    }
    catch (MalformedURLException localMalformedURLException)
    {
      Trace.ignoredException(localMalformedURLException);
    }
    return null;
  }

  public static URL getRequestURL(URL paramURL, String paramString1, String paramString2, boolean paramBoolean, String paramString3, int paramInt)
  {
    return getRequestURL(paramURL, paramString1, paramString2, paramBoolean, paramString3, paramInt, null);
  }

  public static Resource downloadResource(Resource paramResource, URL paramURL, String paramString1, DownloadDelegate paramDownloadDelegate, boolean paramBoolean1, String paramString2, int paramInt, boolean paramBoolean2)
    throws IOException
  {
    int i = 1;
    if (paramString2 != null)
      i = 3;
    else if (paramString1 != null)
      i = 2;
    URL localURL = getRequestURL(paramURL, paramString1, null, paramBoolean1, paramString2, i);
    return actionDownload(paramResource, paramURL, localURL, paramString1, paramDownloadDelegate, paramInt, paramBoolean1, paramBoolean2);
  }

  public static String getAvailableVersion(URL paramURL, String paramString1, boolean paramBoolean, String paramString2)
  {
    return getAvailableVersion(paramURL, paramString1, paramBoolean, paramString2, null);
  }

  public static String getAvailableVersion(URL paramURL, String paramString1, boolean paramBoolean, String paramString2, String paramString3)
  {
    int i = paramString2 != null ? 3 : 2;
    URL localURL = getRequestURL(paramURL, paramString1, null, paramBoolean, paramString2, i, paramString3);
    HttpRequest localHttpRequest = getHttpRequestImpl();
    String str = null;
    try
    {
      HttpResponse localHttpResponse = localHttpRequest.doGetRequest(localURL);
      if (localHttpResponse != null)
      {
        str = localHttpResponse.getResponseHeader("x-java-jnlp-version-id");
        localHttpResponse.disconnect();
      }
    }
    catch (Throwable localThrowable)
    {
      Trace.ignored(localThrowable);
    }
    return str;
  }

  static boolean isJarHeaderValid(byte[] paramArrayOfByte)
  {
    return get32(paramArrayOfByte, 0) == 67324752L;
  }

  private static int get16(byte[] paramArrayOfByte, int paramInt)
  {
    return paramArrayOfByte[paramInt] & 0xFF | (paramArrayOfByte[(paramInt + 1)] & 0xFF) << 8;
  }

  private static long get32(byte[] paramArrayOfByte, int paramInt)
  {
    return get16(paramArrayOfByte, paramInt) | get16(paramArrayOfByte, paramInt + 2) << 16;
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.net.DownloadEngine
 * JD-Core Version:    0.6.2
 */