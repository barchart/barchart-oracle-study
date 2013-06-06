package com.sun.deploy.association.utility;

import com.sun.deploy.association.Action;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class GnomeAssociationUtil
{
  public static final String GNOME_VFS_MIME_KEY_DESCRIPTION = "description";
  public static final String GNOME_VFS_MIME_KEY_ICON_FILENAME = "icon_filename";
  private static boolean GNOMELoaded = GnomeVfsWrapper.openGNOMELibrary();
  private static boolean GNOMEInitializeded = GnomeVfsWrapper.initGNOMELibrary();

  public static String getMimeTypeByFileExt(String paramString)
  {
    String str1 = null;
    String[] arrayOfString1 = GnomeVfsWrapper.gnome_vfs_get_registered_mime_types();
    if (arrayOfString1 == null)
      return null;
    for (int i = 0; i < arrayOfString1.length; i++)
    {
      String str2 = arrayOfString1[i];
      String[] arrayOfString2 = GnomeVfsWrapper.gnome_vfs_mime_get_extensions_list(str2);
      if (arrayOfString2 != null)
        for (int j = 0; j < arrayOfString2.length; j++)
          if (arrayOfString2[j].equals(paramString))
          {
            str1 = arrayOfString1[i];
            break;
          }
      if (str1 != null)
        break;
    }
    return str1;
  }

  public static List getFileExtListByMimeType(String paramString)
  {
    String[] arrayOfString = GnomeVfsWrapper.gnome_vfs_mime_get_extensions_list(paramString);
    if (arrayOfString == null)
      return null;
    ArrayList localArrayList = new ArrayList();
    for (int i = 0; i < arrayOfString.length; i++)
      localArrayList.add(arrayOfString[i]);
    return localArrayList;
  }

  public static String getIconFileNameByMimeType(String paramString)
  {
    return GnomeVfsWrapper.gnome_vfs_mime_get_icon(paramString);
  }

  public static String getDescriptionByMimeType(String paramString)
  {
    return GnomeVfsWrapper.gnome_vfs_mime_get_description(paramString);
  }

  public static List getActionListByMimeType(String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    Action localAction = null;
    String[] arrayOfString = GnomeVfsWrapper.gnome_vfs_mime_get_key_list(paramString);
    if (arrayOfString != null)
    {
      str = null;
      for (int i = 0; i < arrayOfString.length; i++)
      {
        str = GnomeVfsWrapper.gnome_vfs_mime_get_value(paramString, arrayOfString[i]);
        if (str != null)
        {
          localAction = new Action(arrayOfString[i], str);
          localArrayList.add(localAction);
        }
      }
    }
    String str = GnomeVfsWrapper.gnome_vfs_mime_get_default_application_command(paramString);
    if (str != null)
      localArrayList.add(new Action("open", str));
    if (localArrayList.isEmpty())
      return null;
    return localArrayList;
  }

  public static String getMimeTypeByURL(URL paramURL)
  {
    return GnomeVfsWrapper.gnome_vfs_get_mime_type(paramURL.toString());
  }

  public static boolean isMimeTypeExist(String paramString)
  {
    boolean bool = false;
    String[] arrayOfString = GnomeVfsWrapper.gnome_vfs_get_registered_mime_types();
    if (arrayOfString == null)
      return false;
    for (int i = 0; i < arrayOfString.length; i++)
      if (paramString.equals(arrayOfString[i]))
      {
        bool = true;
        break;
      }
    return bool;
  }

  public static boolean isFileExtExist(String paramString)
  {
    return getMimeTypeByFileExt(paramString) != null;
  }

  public static String getEnv(String paramString)
  {
    return GnomeVfsWrapper.getenv(paramString);
  }

  public static boolean supportsCurrentPlatform()
  {
    if ((GNOMELoaded) && (GNOMEInitializeded))
    {
      String str = GnomeVfsWrapper.getVersion();
      return (str != null) && (compareVersion(str, "2.6") <= 0);
    }
    return false;
  }

  public static boolean isAssociationSupported()
  {
    if ((GNOMELoaded) && (GNOMEInitializeded))
    {
      String str = GnomeVfsWrapper.getVersion();
      return (str != null) && (compareVersion(str, "2.8") < 0);
    }
    return false;
  }

  public static int compareVersion(String paramString1, String paramString2)
  {
    StringTokenizer localStringTokenizer1 = new StringTokenizer(paramString1, ".");
    StringTokenizer localStringTokenizer2 = new StringTokenizer(paramString2, ".");
    int i = 0;
    while ((localStringTokenizer1.hasMoreTokens()) && (i == 0))
      if (localStringTokenizer2.hasMoreTokens())
        i = Integer.parseInt(localStringTokenizer1.nextToken()) - Integer.parseInt(localStringTokenizer2.nextToken());
      else
        i = Integer.parseInt(localStringTokenizer1.nextToken());
    while ((i == 0) && (localStringTokenizer2.hasMoreTokens()))
      i = 0 - Integer.parseInt(localStringTokenizer2.nextToken());
    return i;
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.association.utility.GnomeAssociationUtil
 * JD-Core Version:    0.6.2
 */