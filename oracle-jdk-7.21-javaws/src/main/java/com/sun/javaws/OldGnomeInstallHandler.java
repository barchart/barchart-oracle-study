package com.sun.javaws;

import com.sun.deploy.association.Association;
import com.sun.deploy.association.utility.DesktopEntry;
import com.sun.deploy.association.utility.DesktopEntryFile;
import com.sun.deploy.association.utility.GnomeAssociationUtil;
import com.sun.deploy.association.utility.GnomeVfsWrapper;
import com.sun.deploy.config.Platform;
import com.sun.deploy.trace.Trace;
import com.sun.javaws.jnl.InformationDesc;
import com.sun.javaws.jnl.LaunchDesc;
import com.sun.javaws.jnl.ShortcutDesc;
import java.io.File;
import java.io.IOException;

public class OldGnomeInstallHandler extends UnixInstallHandler
{
  public boolean isLocalInstallSupported()
  {
    return Platform.get().isLocalInstallSupported();
  }

  public boolean isAssociationSupported()
  {
    return GnomeAssociationUtil.isAssociationSupported();
  }

  protected boolean desktopEntryExists(String paramString)
  {
    return new DesktopEntryFile(paramString).exists();
  }

  protected boolean removeDesktopPath(String paramString)
  {
    DesktopEntryFile localDesktopEntryFile = new DesktopEntryFile(paramString);
    if (localDesktopEntryFile.exists())
      return localDesktopEntryFile.delete();
    return false;
  }

  protected boolean removeDirectory(String paramString)
  {
    return new DesktopEntryFile(paramString).deleteToNonEmptyParent();
  }

  protected String writeDesktopEntry(DesktopEntry paramDesktopEntry, String paramString, Association paramAssociation, int paramInt)
  {
    String str = "file://" + getGnomeDesktopPath() + File.separator + paramString;
    try
    {
      new DesktopEntryFile(str).writeEntry(paramDesktopEntry);
    }
    catch (IOException localIOException)
    {
      Trace.ignoredException(localIOException);
      return null;
    }
    return str;
  }

  private String getGnomeDesktopPath()
  {
    String str = GnomeVfsWrapper.getVersion();
    int i = (str != null) && (GnomeAssociationUtil.compareVersion(str, "2.6") < 0) ? 1 : 0;
    return i != 0 ? getGnomePre26DesktopPath() : getGnome26DesktopPath();
  }

  private String getGnomePre26DesktopPath()
  {
    return System.getProperty("user.home") + File.separator + ".gnome-desktop";
  }

  private String getGnome26DesktopPath()
  {
    return System.getProperty("user.home") + File.separator + "Desktop";
  }

  protected String writeMenuEntry(DesktopEntry paramDesktopEntry, String paramString1, String paramString2)
  {
    String str = paramString1 + File.separator + paramString2;
    try
    {
      new DesktopEntryFile(str).writeEntry(paramDesktopEntry);
    }
    catch (IOException localIOException)
    {
      Trace.ignoredException(localIOException);
      return null;
    }
    return str;
  }

  protected String getMenuEntryDirPath(LaunchDesc paramLaunchDesc, boolean paramBoolean)
  {
    InformationDesc localInformationDesc = paramLaunchDesc.getInformation();
    ShortcutDesc localShortcutDesc = localInformationDesc.getShortcut();
    String str1 = null;
    if (localShortcutDesc != null)
      str1 = dirFilter(localShortcutDesc.getSubmenu());
    if (str1 == null)
      str1 = dirFilter(localInformationDesc.getTitle());
    if (paramBoolean)
    {
      if (str1.startsWith("applications://"))
      {
        String str2 = "applications://";
        str1 = "applications-all-users://" + str1.substring(str1.indexOf(str2), str2.length());
      }
      else
      {
        str1 = "applications-all-users://" + File.separator + str1;
      }
    }
    else if (!str1.startsWith("applications://"))
      str1 = "applications://" + File.separator + str1;
    return str1;
  }
}

/* Location:           /home/user1/Temp/jvm/javaws.jar
 * Qualified Name:     com.sun.javaws.OldGnomeInstallHandler
 * JD-Core Version:    0.6.2
 */