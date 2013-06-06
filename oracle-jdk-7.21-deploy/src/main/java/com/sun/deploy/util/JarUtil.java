package com.sun.deploy.util;

import java.util.Locale;

public class JarUtil
{
  private static final String META_FILE_DIR = "META-INF/";

  public static boolean isSigningRelated(String paramString)
  {
    paramString = paramString.toUpperCase(Locale.ENGLISH);
    if (!paramString.startsWith("META-INF/"))
      return false;
    paramString = paramString.substring(9);
    if (paramString.indexOf('/') != -1)
      return false;
    return (paramString.endsWith(".DSA")) || (paramString.endsWith(".RSA")) || (paramString.endsWith(".SF")) || (paramString.endsWith(".EC")) || (paramString.startsWith("SIG-")) || (paramString.equals("MANIFEST.MF"));
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.util.JarUtil
 * JD-Core Version:    0.6.2
 */