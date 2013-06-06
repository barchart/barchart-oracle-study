package com.sun.deploy.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

public class SearchPath
{
  private static ArrayList PREFERRED_BROWSERS = new ArrayList();

  public static File findOne(String paramString)
  {
    File localFile1 = null;
    PREFERRED_BROWSERS.clear();
    PREFERRED_BROWSERS.add("mozilla");
    PREFERRED_BROWSERS.add("firefox");
    PREFERRED_BROWSERS.add("netscape");
    PREFERRED_BROWSERS.add("opera");
    PREFERRED_BROWSERS.add("konqueror");
    PREFERRED_BROWSERS.add("galeon");
    Iterator localIterator = PREFERRED_BROWSERS.iterator();
    while (localIterator.hasNext())
    {
      StringTokenizer localStringTokenizer = new StringTokenizer(paramString, File.pathSeparator);
      String str = (String)localIterator.next();
      while (localStringTokenizer.hasMoreTokens())
      {
        File localFile2 = new File(localStringTokenizer.nextToken());
        File localFile3 = new File(localFile2, str);
        if (localFile3.exists())
          return localFile3;
      }
    }
    return localFile1;
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.util.SearchPath
 * JD-Core Version:    0.6.2
 */