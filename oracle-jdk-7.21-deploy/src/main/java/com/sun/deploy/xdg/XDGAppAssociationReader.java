package com.sun.deploy.xdg;

import com.sun.deploy.Environment;
import com.sun.deploy.association.Action;
import com.sun.deploy.association.utility.AppAssociationReader;
import com.sun.deploy.model.LocalApplicationProperties;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.xml.BadTokenException;
import com.sun.deploy.xml.XMLNode;
import com.sun.deploy.xml.XMLParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class XDGAppAssociationReader
  implements AppAssociationReader
{
  private static final String ACTION_COMMAND = "command";

  public XDGAppAssociationReader(LocalApplicationProperties paramLocalApplicationProperties)
  {
  }

  private String getDescriptionByMimeType(String paramString1, String paramString2)
  {
    String str1 = null;
    String str2 = paramString1 + File.separatorChar + paramString2 + ".xml";
    if (new File(str2).exists())
      try
      {
        FileInputStream localFileInputStream = new FileInputStream(str2);
        InputStreamReader localInputStreamReader = new InputStreamReader(localFileInputStream);
        BufferedReader localBufferedReader = new BufferedReader(localInputStreamReader);
        StringBuffer localStringBuffer = new StringBuffer();
        for (String str3 = localBufferedReader.readLine(); str3 != null; str3 = localBufferedReader.readLine())
          localStringBuffer.append(str3);
        localBufferedReader.close();
        XMLParser localXMLParser = new XMLParser(localStringBuffer.toString());
        XMLNode localXMLNode1 = localXMLParser.parse();
        for (XMLNode localXMLNode2 = localXMLNode1.getNested(); !localXMLNode2.getName().equals("comment"); localXMLNode2 = localXMLNode2.getNext());
        str1 = localXMLNode2.getNested().getName();
      }
      catch (IOException localIOException)
      {
        Trace.ignored(localIOException);
        localIOException.printStackTrace();
      }
      catch (BadTokenException localBadTokenException)
      {
        Trace.ignored(localBadTokenException);
      }
    return str1;
  }

  public String getDescriptionByMimeType(String paramString)
  {
    String str = null;
    String[] arrayOfString = Associations.getMimeBasePaths();
    for (int i = 0; (i < arrayOfString.length) && (str == null); i++)
      str = getDescriptionByMimeType(arrayOfString[i], paramString);
    return str;
  }

  public boolean isMimeTypeExist(String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    String[] arrayOfString = Associations.getMimeBasePaths();
    for (int i = 0; i < arrayOfString.length; i++)
      getFileExtListByMimeType(localArrayList, arrayOfString[i], paramString);
    return localArrayList.size() != 0;
  }

  public String getDescriptionByFileExt(String paramString)
  {
    String str1 = getMimeTypeByFileExt(paramString);
    String str2;
    if (str1 != null)
      str2 = getDescriptionByMimeType(str1);
    else
      str2 = null;
    return str2;
  }

  public String getMimeTypeByURL(URL paramURL)
  {
    throw new UnsupportedOperationException("Unexpected call");
  }

  private void getFileExtListByMimeType(List paramList, String paramString1, String paramString2)
  {
    String str = paramString1 + File.separatorChar + "globs2";
    MimeGlob2File localMimeGlob2File = new MimeGlob2File(str);
    localMimeGlob2File.getFileExtListByMimeType(paramList, paramString2);
  }

  public List getFileExtListByMimeType(String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    String[] arrayOfString = Associations.getMimeBasePaths();
    for (int i = 0; i < arrayOfString.length; i++)
      getFileExtListByMimeType(localArrayList, arrayOfString[i], paramString);
    return localArrayList;
  }

  private String getMimeTypeByFileExt(String paramString1, String paramString2)
  {
    String str = paramString1 + File.separatorChar + "globs2";
    MimeGlob2File localMimeGlob2File = new MimeGlob2File(str);
    return localMimeGlob2File.getMimeTypeByFileExt(paramString2);
  }

  public String getMimeTypeByFileExt(String paramString)
  {
    String str = null;
    String[] arrayOfString = Associations.getMimeBasePaths();
    for (int i = 0; (i < arrayOfString.length) && (str == null); i++)
      str = getMimeTypeByFileExt(arrayOfString[i], paramString);
    return str;
  }

  public String getIconFileNameByMimeType(String paramString)
  {
    return Environment.getJavaHome() + File.separator + "lib" + File.separator + "deploy" + File.separator + "java-icon.ico";
  }

  public String getIconFileNameByFileExt(String paramString)
  {
    return Environment.getJavaHome() + File.separator + "lib" + File.separator + "deploy" + File.separator + "java-icon.ico";
  }

  public List getActionListByFileExt(String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(new Action("open", "command"));
    return localArrayList;
  }

  public List getActionListByMimeType(String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(new Action("open", "command"));
    return localArrayList;
  }

  public boolean isFileExtExist(String paramString)
  {
    String str = getMimeTypeByFileExt(paramString);
    return str != null;
  }

  public List getAssociations()
  {
    throw new RuntimeException("Unexpected call");
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.xdg.XDGAppAssociationReader
 * JD-Core Version:    0.6.2
 */