package com.sun.javaws.xdg;

import com.sun.deploy.Environment;
import com.sun.deploy.association.Association;
import com.sun.deploy.association.AssociationAlreadyRegisteredException;
import com.sun.deploy.association.AssociationNotRegisteredException;
import com.sun.deploy.association.AssociationService;
import com.sun.deploy.association.RegisterFailedException;
import com.sun.deploy.association.utility.DesktopEntry;
import com.sun.deploy.config.Config;
import com.sun.deploy.model.LocalApplicationProperties;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.trace.TraceLevel;
import com.sun.deploy.xdg.Associations;
import com.sun.deploy.xdg.BaseDir;
import com.sun.javaws.UnixInstallHandler;
import com.sun.javaws.jnl.InformationDesc;
import com.sun.javaws.jnl.LaunchDesc;
import com.sun.javaws.jnl.ShortcutDesc;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class XDGInstallHandler extends UnixInstallHandler
{
  private static final String ACTION_COMMAND = "command";

  public boolean isLocalInstallSupported()
  {
    return true;
  }

  public boolean isAssociationSupported()
  {
    return true;
  }

  protected boolean desktopEntryExists(String paramString)
  {
    return new File(paramString).exists();
  }

  protected String writeDesktopEntry(DesktopEntry paramDesktopEntry, String paramString, Association paramAssociation, int paramInt)
  {
    File localFile = null;
    String str;
    if (paramAssociation != null)
    {
      localFile = Associations.getDesktopEntryFile(paramAssociation, paramInt);
    }
    else
    {
      UserDirs localUserDirs = UserDirs.getInstance();
      str = localUserDirs.getDesktopDir();
      localFile = new File(str, paramString);
    }
    boolean bool = writeDesktopFile(paramDesktopEntry, localFile, true);
    if (bool)
      str = localFile.getAbsolutePath();
    else
      str = null;
    return "desktop:" + paramString;
  }

  private boolean writeDesktopFile(DesktopEntry paramDesktopEntry, File paramFile, boolean paramBoolean)
  {
    Trace.println("Writing desktop file to: " + paramFile, TraceLevel.UI);
    boolean bool;
    try
    {
      FileOutputStream localFileOutputStream = new FileOutputStream(paramFile);
      OutputStreamWriter localOutputStreamWriter = new OutputStreamWriter(localFileOutputStream);
      BufferedWriter localBufferedWriter = new BufferedWriter(localOutputStreamWriter);
      localBufferedWriter.write(paramDesktopEntry.toString());
      localBufferedWriter.close();
      if ((paramBoolean) && (Config.isJavaVersionAtLeast16()))
        paramFile.setExecutable(true);
      bool = true;
    }
    catch (IOException localIOException)
    {
      Trace.ignored(localIOException);
      bool = false;
    }
    return bool;
  }

  protected String writeMenuEntry(DesktopEntry paramDesktopEntry, String paramString1, String paramString2)
  {
    String str1 = getAppsDir();
    new File(str1).mkdirs();
    File localFile = new File(str1, paramString2);
    boolean bool = writeDesktopFile(paramDesktopEntry, localFile, false);
    String str2;
    if (bool)
    {
      MenuDatabase localMenuDatabase = MenuDatabase.getInstance();
      Menu localMenu = localMenuDatabase.addMenu(paramString1);
      if (paramString1 == null)
        paramString1 = "";
      localMenu.addEntry(paramString2);
      try
      {
        localMenuDatabase.save();
      }
      catch (IOException localIOException)
      {
        Trace.ignored(localIOException);
        str2 = null;
      }
      str2 = "menu:" + paramString1 + ":" + paramString2;
    }
    else
    {
      str2 = null;
    }
    return str2;
  }

  protected boolean removeDesktopPath(String paramString)
  {
    int i = paramString.indexOf(':');
    String str1 = paramString.substring(0, i);
    int j = 0;
    String str2;
    Object localObject1;
    String str3;
    Object localObject2;
    if (str1.equals("desktop"))
    {
      str2 = paramString.substring(i + 1, paramString.length());
      localObject1 = UserDirs.getInstance();
      str3 = ((UserDirs)localObject1).getDesktopDir();
      localObject2 = new File(str3, str2);
      ((File)localObject2).delete();
      j = 1;
    }
    else if (str1.equals("menu"))
    {
      str2 = paramString.substring(i + 1, paramString.length());
      i = str2.indexOf(':');
      localObject1 = str2.substring(0, i);
      str3 = str2.substring(i + 1, str2.length());
      localObject2 = getAppsDir();
      File localFile = new File((String)localObject2, str3);
      localFile.delete();
      MenuDatabase localMenuDatabase = MenuDatabase.getInstance();
      Menu localMenu = localMenuDatabase.getMenu((String)localObject1);
      if (localMenu != null)
        localMenu.removeEntry(str3);
      try
      {
        localMenuDatabase.save();
        j = 1;
      }
      catch (IOException localIOException)
      {
        Trace.ignored(localIOException);
        j = 0;
      }
    }
    else
    {
      j = 0;
    }
    return true;
  }

  protected boolean removeDirectory(String paramString)
  {
    return true;
  }

  private String getAppsDir()
  {
    BaseDir localBaseDir = BaseDir.getInstance();
    String str = localBaseDir.getUserDataDir() + File.separatorChar + "applications";
    return str;
  }

  protected String getMenuEntryDirPath(LaunchDesc paramLaunchDesc, boolean paramBoolean)
  {
    InformationDesc localInformationDesc = paramLaunchDesc.getInformation();
    ShortcutDesc localShortcutDesc = localInformationDesc.getShortcut();
    String str = null;
    if (localShortcutDesc != null)
      str = dirFilter(localShortcutDesc.getSubmenu());
    return str;
  }

  public String getAssociationPrintCommand(String paramString)
  {
    return "command";
  }

  public String getAssociationOpenCommand(String paramString)
  {
    return "command";
  }

  public void registerAssociationInternal(LaunchDesc paramLaunchDesc, LocalApplicationProperties paramLocalApplicationProperties, Association paramAssociation)
    throws AssociationAlreadyRegisteredException, RegisterFailedException
  {
    AssociationService localAssociationService = new AssociationService(paramLocalApplicationProperties);
    int i = Environment.isSystemCacheMode() ? 2 : 1;
    if (!createShortcuts(paramLaunchDesc, paramLocalApplicationProperties, new boolean[] { false, false }, paramAssociation, i))
      throw new RegisterFailedException();
    super.registerAssociationInternal(paramLaunchDesc, paramLocalApplicationProperties, paramAssociation);
  }

  public void unregisterAssociationInternal(LaunchDesc paramLaunchDesc, LocalApplicationProperties paramLocalApplicationProperties, Association paramAssociation)
    throws AssociationNotRegisteredException, RegisterFailedException
  {
    int i = Environment.isSystemCacheMode() ? 2 : 1;
    File localFile = Associations.getDesktopEntryFile(paramAssociation, i);
    if (!localFile.exists())
      Trace.print("desktop entry file doesn't exist, path == " + localFile.getAbsolutePath(), TraceLevel.TEMP);
    else
      localFile.delete();
    super.unregisterAssociationInternal(paramLaunchDesc, paramLocalApplicationProperties, paramAssociation);
  }
}

/* Location:           /home/user1/Temp/jvm/javaws.jar
 * Qualified Name:     com.sun.javaws.xdg.XDGInstallHandler
 * JD-Core Version:    0.6.2
 */