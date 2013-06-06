package com.sun.deploy.security;

import com.sun.deploy.config.Config;
import com.sun.deploy.resources.ResourceManager;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.trace.TraceLevel;
import java.io.File;
import java.io.FileInputStream;
import java.security.AccessController;
import java.security.MessageDigest;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Properties;

public class BlacklistedCerts
{
  private static Properties props = null;
  private static String algorithm = null;
  private static final String ALGORITHM_KEY = "Algorithm";
  private static final File blacklistCertsFile = new File(Config.getDynamicBlacklistCertsFile());

  public static void check(X509Certificate paramX509Certificate)
    throws CertificateException
  {
    if (Config.getBooleanProperty("deployment.security.blacklist.check"))
    {
      if (props == null)
        load();
      if (algorithm == null)
        return;
      String str1 = getCertificateFingerPrint(algorithm, paramX509Certificate);
      if (props.containsKey(str1))
      {
        String str2 = ResourceManager.getMessage("blacklisted.certificate");
        Trace.println(str2, TraceLevel.SECURITY);
        throw new CertificateException(str2);
      }
    }
  }

  private static void load()
  {
    props = new Properties();
    if (blacklistCertsFile.exists())
    {
      Trace.println("Loading blacklisted.certs file: " + blacklistCertsFile, TraceLevel.SECURITY);
      try
      {
        AccessController.doPrivileged(new PrivilegedExceptionAction()
        {
          public Object run()
            throws Exception
          {
            FileInputStream localFileInputStream = null;
            try
            {
              localFileInputStream = new FileInputStream(BlacklistedCerts.blacklistCertsFile);
              BlacklistedCerts.props.load(localFileInputStream);
            }
            catch (Exception localException1)
            {
              Trace.ignored(localException1);
            }
            finally
            {
              if (localFileInputStream != null)
                try
                {
                  localFileInputStream.close();
                }
                catch (Exception localException2)
                {
                  Trace.ignored(localException2);
                }
            }
            return null;
          }
        });
      }
      catch (PrivilegedActionException localPrivilegedActionException)
      {
        localPrivilegedActionException.printStackTrace();
      }
      algorithm = props.getProperty("Algorithm");
      if (algorithm == null)
        Trace.println("blacklisted.certs file contains no Algorithm property.", TraceLevel.SECURITY);
    }
    else
    {
      Trace.println("No blacklisted.certs file", TraceLevel.SECURITY);
    }
  }

  private static void byte2hex(byte paramByte, StringBuffer paramStringBuffer)
  {
    char[] arrayOfChar = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
    int i = (paramByte & 0xF0) >> 4;
    int j = paramByte & 0xF;
    paramStringBuffer.append(arrayOfChar[i]);
    paramStringBuffer.append(arrayOfChar[j]);
  }

  private static String getCertificateFingerPrint(String paramString, X509Certificate paramX509Certificate)
  {
    String str = "";
    try
    {
      byte[] arrayOfByte1 = paramX509Certificate.getEncoded();
      MessageDigest localMessageDigest = MessageDigest.getInstance(paramString);
      byte[] arrayOfByte2 = localMessageDigest.digest(arrayOfByte1);
      StringBuffer localStringBuffer = new StringBuffer();
      for (int i = 0; i < arrayOfByte2.length; i++)
        byte2hex(arrayOfByte2[i], localStringBuffer);
      str = localStringBuffer.toString();
      Trace.println(algorithm + " finger print: " + str, TraceLevel.SECURITY);
    }
    catch (Exception localException)
    {
      Trace.ignored(localException);
    }
    return str;
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.security.BlacklistedCerts
 * JD-Core Version:    0.6.2
 */