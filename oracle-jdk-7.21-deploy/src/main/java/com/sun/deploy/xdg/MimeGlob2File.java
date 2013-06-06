package com.sun.deploy.xdg;

import com.sun.deploy.trace.Trace;
import com.sun.deploy.trace.TraceLevel;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

class MimeGlob2File
{
  private String filePath;
  private Map fileExtToMime;

  MimeGlob2File(String paramString)
  {
    this.filePath = paramString;
  }

  private void initIfNecessary()
  {
    if (this.fileExtToMime == null)
      this.fileExtToMime = new HashMap();
    File localFile = new File(this.filePath);
    if (localFile.exists())
      try
      {
        FileInputStream localFileInputStream = new FileInputStream(localFile);
        InputStreamReader localInputStreamReader = new InputStreamReader(localFileInputStream);
        BufferedReader localBufferedReader = new BufferedReader(localInputStreamReader);
        for (String str = localBufferedReader.readLine(); str != null; str = localBufferedReader.readLine())
          parseLine(str);
      }
      catch (IOException localIOException)
      {
        Trace.ignored(localIOException);
      }
    else
      Trace.println("globs2 doesn't exist, path == " + this.filePath, TraceLevel.TEMP);
  }

  private void parseLine(String paramString)
  {
    if (paramString.startsWith("#"))
      return;
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, ":");
    String str1 = localStringTokenizer.nextToken();
    String str2 = localStringTokenizer.nextToken();
    String str3 = localStringTokenizer.nextToken();
    this.fileExtToMime.put(str3, str2);
  }

  boolean mapsFileExtToMimetype(String paramString1, String paramString2)
  {
    initIfNecessary();
    String str = (String)this.fileExtToMime.get(paramString1);
    boolean bool = false;
    if (str != null)
      bool = str.equals(paramString2);
    return bool;
  }

  void getFileExtListByMimeType(List paramList, String paramString)
  {
    initIfNecessary();
    Set localSet = this.fileExtToMime.keySet();
    Iterator localIterator = localSet.iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      if (mapsFileExtToMimetype(str, paramString))
      {
        if (str.startsWith("*."))
          str = str.substring(2, str.length());
        paramList.add(str);
      }
    }
  }

  String getMimeTypeByFileExt(String paramString)
  {
    initIfNecessary();
    return (String)this.fileExtToMime.get(paramString);
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.xdg.MimeGlob2File
 * JD-Core Version:    0.6.2
 */