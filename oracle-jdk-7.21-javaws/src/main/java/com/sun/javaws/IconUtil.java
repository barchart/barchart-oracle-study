package com.sun.javaws;

import com.sun.deploy.config.Platform;
import com.sun.deploy.model.Resource;
import com.sun.deploy.model.ResourceProvider;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.util.IconEncoder;
import com.sun.javaws.jnl.IconDesc;
import com.sun.javaws.jnl.InformationDesc;
import com.sun.javaws.jnl.LaunchDesc;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class IconUtil
{
  public static String getIconPath(LaunchDesc paramLaunchDesc, boolean paramBoolean)
  {
    return getIconPath(paramLaunchDesc);
  }

  public static String getIconPath(LaunchDesc paramLaunchDesc)
  {
    ArrayList localArrayList = new ArrayList();
    int[] arrayOfInt1 = Platform.get().getIconSizes();
    Integer localInteger1 = new Integer(Platform.get().getSystemShortcutIconSize(true));
    Integer localInteger2 = new Integer(Platform.get().getSystemShortcutIconSize(false));
    for (Integer localInteger3 = 0; localInteger3 < arrayOfInt1.length; localInteger3++)
    {
      localInteger4 = new Integer(arrayOfInt1[localInteger3]);
      if (!localArrayList.contains(localInteger4))
        localArrayList.add(localInteger4);
    }
    if (!localArrayList.contains(localInteger1))
      localArrayList.add(localInteger1);
    if (!localArrayList.contains(localInteger2))
      localArrayList.add(localInteger2);
    localInteger3 = ((Integer)localArrayList.get(0)).intValue();
    Integer localInteger4 = localInteger3;
    Integer localInteger5 = localInteger3;
    int j;
    int i;
    for (int k = 0; k < localArrayList.size(); k++)
    {
      localInteger3 = ((Integer)localArrayList.get(k)).intValue();
      if (localInteger3 > localInteger5)
        j = localInteger3;
      if (localInteger3 < localInteger4)
        i = localInteger3;
    }
    Iterator localIterator = localArrayList.iterator();
    IconDesc[] arrayOfIconDesc = new IconDesc[localArrayList.size()];
    int[] arrayOfInt2 = new int[localArrayList.size()];
    int m = 0;
    while (localIterator.hasNext())
    {
      int n = ((Integer)localIterator.next()).intValue();
      if (n < i)
        n = i;
      if (n > j)
        n = j;
      localObject2 = paramLaunchDesc.getInformation().getIconLocation(n, 5);
      if (localObject2 == null)
        localObject2 = paramLaunchDesc.getInformation().getIconLocation(n, 0);
      if (localObject2 != null)
      {
        i1 = 0;
        for (int i2 = 0; (i2 < m) && (i1 == 0); i2++)
          if (((IconDesc)localObject2).equals(arrayOfIconDesc[i2]))
            i1 = 1;
        if (i1 == 0)
        {
          arrayOfIconDesc[m] = localObject2;
          i2 = ((IconDesc)localObject2).getWidth();
          int i3 = ((IconDesc)localObject2).getHeight();
          if ((i2 == i3) && (i2 >= i) && (i2 <= j))
            arrayOfInt2[m] = i2;
          else
            arrayOfInt2[m] = n;
          m++;
        }
      }
    }
    Object localObject1 = null;
    Object localObject2 = new File[localArrayList.size()];
    for (int i1 = 0; i1 < m; i1++)
    {
      String str1 = null;
      Object localObject3 = null;
      try
      {
        Resource localResource = ResourceProvider.get().getResource(arrayOfIconDesc[i1].getLocation(), arrayOfIconDesc[i1].getVersion());
        Object localObject4 = localResource != null ? localResource.getDataFile() : null;
        if (localObject4 != null)
        {
          if (Platform.get().isPlatformIconType(arrayOfIconDesc[i1].getLocation().toString()))
          {
            str1 = localObject4.toString();
            localObject3 = localObject4;
          }
          else
          {
            String str2 = Platform.get().getPlatformIconType();
            str1 = localObject4.getPath() + "." + str2;
            localObject3 = new File(str1);
          }
          if (((File)localObject3).exists())
          {
            localObject1 = str1;
            return localObject1;
          }
          if (localObject1 == null)
            localObject1 = str1;
          localObject2[i1] = localObject4;
        }
      }
      catch (IOException localIOException)
      {
        Trace.ignored(localIOException);
      }
    }
    if ((localObject1 != null) && (m > 0))
    {
      IconEncoder localIconEncoder = Platform.get().getIconEncoder();
      localIconEncoder.convert((File[])localObject2, arrayOfInt2, m, localObject1);
    }
    if ((localObject1 != null) && (new File(localObject1).exists()))
      return localObject1;
    return null;
  }

  public static String getIconPath(URL paramURL, String paramString)
  {
    String str = null;
    File localFile2 = null;
    Object localObject;
    try
    {
      Resource localResource = ResourceProvider.get().getResource(paramURL, paramString);
      if (localResource != null)
        localFile2 = localResource.getDataFile();
      if (localFile2 != null)
      {
        File localFile1;
        if (Platform.get().isPlatformIconType(paramURL.toString()))
        {
          str = localFile2.toString();
          localFile1 = localFile2;
        }
        else
        {
          localObject = Platform.get().getPlatformIconType();
          str = localFile2.getPath() + "." + (String)localObject;
          localFile1 = new File(str);
        }
        if (localFile1.exists())
          return str;
      }
    }
    catch (IOException localIOException)
    {
      Trace.ignored(localIOException);
    }
    if (str != null)
    {
      File[] arrayOfFile = { localFile2 };
      localObject = new int[] { 32 };
      int i = 1;
      IconEncoder localIconEncoder = Platform.get().getIconEncoder();
      localIconEncoder.convert(arrayOfFile, (int[])localObject, i, str);
    }
    if ((str != null) && (new File(str).exists()))
      return str;
    return null;
  }
}

/* Location:           /home/user1/Temp/jvm/javaws.jar
 * Qualified Name:     com.sun.javaws.IconUtil
 * JD-Core Version:    0.6.2
 */