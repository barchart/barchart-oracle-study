package com.sun.deploy.xdg;

import com.sun.deploy.Environment;
import com.sun.deploy.association.Association;
import java.io.File;
import java.util.Iterator;
import java.util.List;

public class Associations
{
  private static Associations instance;

  static synchronized Associations getInstance()
  {
    if (instance == null)
      instance = new Associations();
    return instance;
  }

  public static String[] getMimeBasePaths(int paramInt)
  {
    BaseDir localBaseDir = BaseDir.getInstance();
    if (paramInt == 1)
    {
      localObject1 = localBaseDir.getUserDataDir();
      localObject2 = (String)localObject1 + File.separatorChar + "mime";
      return new String[] { localObject2 };
    }
    Object localObject1 = localBaseDir.getSystemDataDirs();
    Object localObject2 = new String[localObject1.length];
    for (int i = 0; i < localObject1.length; i++)
      localObject2[i] = (localObject1[i] + File.separatorChar + "mime");
    return localObject2;
  }

  public static String[] getMimeBasePaths()
  {
    int i = Environment.isSystemCacheMode() ? 2 : 1;
    return getMimeBasePaths(i);
  }

  public static String[] getAppBasePaths(int paramInt)
  {
    BaseDir localBaseDir = BaseDir.getInstance();
    if (paramInt == 1)
    {
      localObject1 = localBaseDir.getUserDataDir();
      localObject2 = (String)localObject1 + File.separatorChar + "applications";
      return new String[] { localObject2 };
    }
    Object localObject1 = localBaseDir.getSystemDataDirs();
    Object localObject2 = new String[localObject1.length];
    for (int i = 0; i < localObject1.length; i++)
      localObject2[i] = (localObject1[i] + File.separatorChar + "applications");
    return localObject2;
  }

  public static String[] getAppBasePaths()
  {
    int i = Environment.isSystemCacheMode() ? 2 : 1;
    return getAppBasePaths(i);
  }

  private boolean isAssociationExists(String paramString, Association paramAssociation)
  {
    String str1 = paramString + File.separatorChar + "globs2";
    MimeGlob2File localMimeGlob2File = new MimeGlob2File(str1);
    boolean bool = true;
    Iterator localIterator = paramAssociation.getFileExtList().iterator();
    while ((localIterator.hasNext()) && (bool))
    {
      String str2 = (String)localIterator.next();
      if (!localMimeGlob2File.mapsFileExtToMimetype("*" + str2, paramAssociation.getMimeType()))
        bool = false;
    }
    return bool;
  }

  boolean isAssociationExist(Association paramAssociation, int paramInt)
  {
    String[] arrayOfString = getMimeBasePaths(paramInt);
    boolean bool = false;
    for (int i = 0; (!bool) && (i < arrayOfString.length); i++)
      if (isAssociationExists(arrayOfString[i], paramAssociation))
        bool = true;
    return bool;
  }

  public static File getMimeTypeFile(Association paramAssociation, int paramInt)
  {
    BaseDir localBaseDir = BaseDir.getInstance();
    String str1 = null;
    if (paramInt == 2)
      str1 = localBaseDir.getSystemDataDir();
    else
      str1 = localBaseDir.getUserDataDir();
    String str2 = str1 + File.separatorChar + "mime" + File.separatorChar + "packages";
    new File(str2).mkdirs();
    String str3 = paramAssociation.getMimeType();
    str3 = str3.replace('/', '_');
    String str4 = "oracle-" + paramAssociation.getName() + "_" + str3 + ".xml";
    return new File(str2 + File.separatorChar + str4);
  }

  public static File getDesktopEntryFile(Association paramAssociation, int paramInt)
  {
    BaseDir localBaseDir = BaseDir.getInstance();
    String str1 = null;
    if (paramInt == 2)
      str1 = localBaseDir.getSystemDataDir();
    else
      str1 = localBaseDir.getUserDataDir();
    String str2 = str1 + File.separatorChar + "applications";
    new File(str2).mkdirs();
    String str3 = paramAssociation.getMimeType();
    str3 = str3.replace('/', '_');
    String str4 = "oracle-" + paramAssociation.getName() + "_" + str3 + ".desktop";
    return new File(str2 + File.separatorChar + str4);
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.xdg.Associations
 * JD-Core Version:    0.6.2
 */