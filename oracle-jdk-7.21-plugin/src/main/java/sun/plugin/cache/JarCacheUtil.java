package sun.plugin.cache;

import com.sun.deploy.model.ResourceObject;
import com.sun.deploy.resources.ResourceManager;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.trace.TraceLevel;
import com.sun.deploy.util.URLUtil;
import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

public class JarCacheUtil
{
  public static HashMap getJarsWithVersion(String paramString1, String paramString2, String paramString3)
  {
    HashMap localHashMap = new HashMap();
    StringTokenizer localStringTokenizer1;
    StringTokenizer localStringTokenizer2;
    String str1;
    String str2;
    if ((paramString1 != null) && (paramString2 != null))
    {
      localStringTokenizer1 = new StringTokenizer(paramString1, ",", false);
      int i = localStringTokenizer1.countTokens();
      localStringTokenizer2 = new StringTokenizer(paramString2, ",", false);
      int j = localStringTokenizer2.countTokens();
      if ((i != j) && (Trace.isEnabled(TraceLevel.BASIC)))
        Trace.println(ResourceManager.getMessage("cache.version_attrib_error"), TraceLevel.BASIC);
      while ((localStringTokenizer1.hasMoreTokens()) && (localStringTokenizer2.hasMoreTokens()))
      {
        str1 = localStringTokenizer1.nextToken().trim();
        str2 = localStringTokenizer2.nextToken().trim();
        localHashMap.put(str1, str2);
      }
    }
    if (paramString3 != null)
    {
      localStringTokenizer1 = new StringTokenizer(paramString3, ",", false);
      while (localStringTokenizer1.hasMoreTokens())
      {
        String str3 = localStringTokenizer1.nextToken().trim();
        localStringTokenizer2 = new StringTokenizer(str3, ";", false);
        str1 = localStringTokenizer2.nextToken().trim();
        while (localStringTokenizer2.hasMoreTokens())
        {
          str2 = localStringTokenizer2.nextToken().trim();
          if (Pattern.matches("\\p{XDigit}{1,4}\\.\\p{XDigit}{1,4}\\.\\p{XDigit}{1,4}\\.\\p{XDigit}{1,4}", str2))
            localHashMap.put(str1, str2);
        }
      }
    }
    return localHashMap;
  }

  public static synchronized void preload(URL paramURL, HashMap paramHashMap)
    throws IOException
  {
    Iterator localIterator = paramHashMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str1 = (String)localIterator.next();
      String str2 = (String)paramHashMap.get(str1);
      URL localURL1 = new URL(paramURL, str1);
      if (!URLUtil.checkTargetURL(paramURL, localURL1))
        throw new SecurityException("Permission denied: " + localURL1);
      URL localURL2 = null;
      if (str2 != null)
        localURL2 = new URL("jar:" + localURL1.toString() + "?version-id=" + str2 + "!/");
      else
        localURL2 = new URL("jar:" + localURL1.toString() + "!/");
      JarURLConnection localJarURLConnection = (JarURLConnection)localURL2.openConnection();
      localJarURLConnection.getContentLength();
    }
  }

  public static JarFile cloneJarFile(JarFile paramJarFile)
    throws IOException
  {
    if (paramJarFile == null)
      return paramJarFile;
    try
    {
      return (JarFile)AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        private final JarFile val$jarFile;

        public Object run()
          throws Exception
        {
          if (((this.val$jarFile instanceof JarFile)) && ((this.val$jarFile instanceof ResourceObject)))
            return (JarFile)((ResourceObject)this.val$jarFile).clone();
          String str = this.val$jarFile.getName();
          if (new File(str).exists())
            return new JarFile(this.val$jarFile.getName());
          return this.val$jarFile;
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw new IOException(localPrivilegedActionException.getCause().getMessage());
    }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.cache.JarCacheUtil
 * JD-Core Version:    0.6.2
 */