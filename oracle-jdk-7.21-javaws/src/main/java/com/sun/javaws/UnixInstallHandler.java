package com.sun.javaws;

import com.sun.deploy.Environment;
import com.sun.deploy.association.Action;
import com.sun.deploy.association.Association;
import com.sun.deploy.association.AssociationAlreadyRegisteredException;
import com.sun.deploy.association.AssociationNotRegisteredException;
import com.sun.deploy.association.AssociationService;
import com.sun.deploy.association.RegisterFailedException;
import com.sun.deploy.association.utility.DesktopEntry;
import com.sun.deploy.config.Config;
import com.sun.deploy.config.Platform;
import com.sun.deploy.model.LocalApplicationProperties;
import com.sun.deploy.model.ResourceProvider;
import com.sun.deploy.resources.ResourceManager;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.trace.TraceLevel;
import com.sun.deploy.uitoolkit.ToolkitStore;
import com.sun.deploy.uitoolkit.ui.UIFactory;
import com.sun.javaws.jnl.InformationDesc;
import com.sun.javaws.jnl.LaunchDesc;
import com.sun.javaws.jnl.RContentDesc;
import com.sun.javaws.jnl.ShortcutDesc;
import java.io.File;
import java.net.URL;
import java.util.StringTokenizer;

public abstract class UnixInstallHandler extends LocalInstallHandler
{
  private static final String INSTALLED_DESKTOP_SHORTCUT_KEY = "installed.desktop";
  private static final String INSTALLED_START_MENU_KEY = "installed.menu";
  private static final String INSTALLED_DIRECTORY_KEY = "installed.directory";
  private static final String INSTALLED_UNINSTALL_KEY = "installed.uninstalled";
  private static final String INSTALLED_RC_KEY = "installed.rc";
  private final String nameBadChars = "\"\\/|:?*<>#";
  private final String dirBadChars = "\"|:?*<>#";

  public boolean isShortcutExists(LocalApplicationProperties paramLocalApplicationProperties)
  {
    String str1 = paramLocalApplicationProperties.get("installed.desktop");
    String str2 = paramLocalApplicationProperties.get("installed.menu");
    boolean bool1 = false;
    boolean bool2 = false;
    if (str1 != null)
      bool1 = desktopEntryExists(str1);
    if (str2 != null)
      bool2 = desktopEntryExists(str2);
    if ((str1 != null) && (str2 != null))
      return (bool1) && (bool2);
    return (bool1) || (bool2);
  }

  protected abstract boolean desktopEntryExists(String paramString);

  public boolean[] whichShortcutsExist(LocalApplicationProperties paramLocalApplicationProperties)
  {
    String str1 = paramLocalApplicationProperties.get("installed.desktop");
    String str2 = paramLocalApplicationProperties.get("installed.menu");
    boolean[] arrayOfBoolean = new boolean[2];
    arrayOfBoolean[0] = ((str1 != null) && (desktopEntryExists(str1)) ? 1 : false);
    arrayOfBoolean[1] = ((str2 != null) && (desktopEntryExists(str2)) ? 1 : false);
    return arrayOfBoolean;
  }

  public String getAssociationPrintCommand(String paramString)
  {
    return null;
  }

  public String getAssociationOpenCommand(String paramString)
  {
    return Platform.get().getSystemJavawsPath() + " -localfile " + paramString + " -open";
  }

  public void registerAssociationInternal(LaunchDesc paramLaunchDesc, LocalApplicationProperties paramLocalApplicationProperties, Association paramAssociation)
    throws AssociationAlreadyRegisteredException, RegisterFailedException
  {
    AssociationService localAssociationService = new AssociationService(paramLocalApplicationProperties);
    if (Environment.isSystemCacheMode())
      localAssociationService.registerSystemAssociation(paramAssociation);
    else
      localAssociationService.registerUserAssociation(paramAssociation);
  }

  public void unregisterAssociationInternal(LaunchDesc paramLaunchDesc, LocalApplicationProperties paramLocalApplicationProperties, Association paramAssociation)
    throws AssociationNotRegisteredException, RegisterFailedException
  {
    AssociationService localAssociationService = new AssociationService(paramLocalApplicationProperties);
    if (Environment.isSystemCacheMode())
      localAssociationService.unregisterSystemAssociation(paramAssociation);
    else
      localAssociationService.unregisterUserAssociation(paramAssociation);
  }

  public boolean hasAssociation(LaunchDesc paramLaunchDesc, LocalApplicationProperties paramLocalApplicationProperties, Association paramAssociation)
  {
    AssociationService localAssociationService = new AssociationService(paramLocalApplicationProperties);
    return localAssociationService.hasAssociation(paramAssociation);
  }

  protected boolean createShortcuts(LaunchDesc paramLaunchDesc, LocalApplicationProperties paramLocalApplicationProperties, boolean[] paramArrayOfBoolean)
  {
    return createShortcuts(paramLaunchDesc, paramLocalApplicationProperties, paramArrayOfBoolean, null, -1);
  }

  private static String getJnlpLocation(LaunchDesc paramLaunchDesc)
  {
    File localFile = ResourceProvider.get().getCachedJNLPFile(paramLaunchDesc.getCanonicalHome(), null);
    String str;
    if (localFile != null)
      str = localFile.getAbsolutePath();
    else
      str = paramLaunchDesc.getLocation().toString();
    return str;
  }

  protected boolean createShortcuts(LaunchDesc paramLaunchDesc, LocalApplicationProperties paramLocalApplicationProperties, boolean[] paramArrayOfBoolean, Association paramAssociation, int paramInt)
  {
    Trace.println("createShortcuts called in UnixInstallHandler", TraceLevel.BASIC);
    ShortcutDesc localShortcutDesc = paramLaunchDesc.getInformation().getShortcut();
    boolean bool1 = localShortcutDesc == null ? true : localShortcutDesc.getDesktop();
    boolean bool2 = localShortcutDesc == null ? true : localShortcutDesc.getMenu();
    int i = paramAssociation != null ? 1 : 0;
    boolean bool3 = false;
    boolean bool4 = true;
    if (Environment.isSystemCacheMode())
    {
      bool1 = false;
      bool3 = true;
    }
    if (paramArrayOfBoolean != null)
    {
      bool1 = (bool1) && (paramArrayOfBoolean[0] != 0);
      bool2 = (bool2) && (paramArrayOfBoolean[1] != 0);
    }
    if ((isShortcutExists(paramLocalApplicationProperties)) && (!shouldInstallOverExisting(paramLaunchDesc)) && (i == 0))
    {
      Trace.println("Skip creating shortcut as it exists", TraceLevel.UI);
      return false;
    }
    if ((bool2) || (bool1) || (i != 0))
    {
      String str = getJnlpLocation(paramLaunchDesc);
      Object localObject;
      if (((bool1) || (i != 0)) && (bool4))
      {
        localObject = createDesktopShortcut(paramLaunchDesc, str, paramAssociation, paramInt);
        if (localObject != null)
          paramLocalApplicationProperties.put("installed.desktop", (String)localObject);
        if (localObject == null)
        {
          bool4 = false;
          Trace.println("Skip creating shortcut - can not find desktop location", TraceLevel.UI);
        }
      }
      if ((bool2) && (bool4))
      {
        localObject = createStartMenuShortcut(paramLaunchDesc, str, bool3);
        if (localObject[0] != null)
        {
          paramLocalApplicationProperties.put("installed.menu", localObject[0]);
          if (localObject[1] != null)
            paramLocalApplicationProperties.put("installed.directory", localObject[1]);
          if (localObject[2] != null)
            paramLocalApplicationProperties.put("installed.uninstalled", localObject[2]);
          if (localObject[3] != null)
            paramLocalApplicationProperties.put("installed.rc", localObject[3]);
        }
        else
        {
          bool4 = false;
          removeShortcuts(paramLaunchDesc, paramLocalApplicationProperties, bool1);
        }
      }
      if (bool4)
      {
        paramLocalApplicationProperties.setShortcutInstalled(true);
        save(paramLocalApplicationProperties);
      }
      else
      {
        installFailed(paramLaunchDesc);
      }
    }
    return bool4;
  }

  protected void registerWithInstallPanel(LaunchDesc paramLaunchDesc, LocalApplicationProperties paramLocalApplicationProperties)
  {
  }

  protected void removeFromInstallPanel(LaunchDesc paramLaunchDesc, LocalApplicationProperties paramLocalApplicationProperties, boolean paramBoolean)
  {
  }

  public String getDefaultIconPath()
  {
    return Environment.getJavaHome() + File.separator + "lib" + File.separator + "deploy" + File.separator + "java-icon.ico";
  }

  private String getIcon(LaunchDesc paramLaunchDesc, boolean paramBoolean)
  {
    String str = IconUtil.getIconPath(paramLaunchDesc, paramBoolean);
    if (str == null)
      str = getDefaultIconPath();
    return str;
  }

  private String getRCIcon(RContentDesc paramRContentDesc, LaunchDesc paramLaunchDesc)
  {
    URL localURL = paramRContentDesc.getIcon();
    String str = null;
    if (localURL != null)
      str = IconUtil.getIconPath(localURL, null);
    if (str == null)
      str = getIcon(paramLaunchDesc, false);
    return str;
  }

  private String[] createStartMenuShortcut(LaunchDesc paramLaunchDesc, String paramString, boolean paramBoolean)
  {
    InformationDesc localInformationDesc = paramLaunchDesc.getInformation();
    ShortcutDesc localShortcutDesc = localInformationDesc.getShortcut();
    String[] arrayOfString = new String[5];
    String str1 = nameFilter(localInformationDesc.getTitle());
    String str2 = getIcon(paramLaunchDesc, false);
    int i = (!localInformationDesc.supportsOfflineOperation()) || (localShortcutDesc == null) || (localShortcutDesc.getOnline()) ? 1 : 0;
    String str3 = i != 0 ? "" : "-offline ";
    String str4 = paramLaunchDesc.getLocation() != null ? paramLaunchDesc.getLocation().toString() : null;
    String str5 = "-J-Djnlp.application.href=" + str4 + " ";
    String str6 = "-localfile " + str3 + str5;
    String str7 = getMenuEntryDirPath(paramLaunchDesc, paramBoolean);
    arrayOfString[0] = createDesktopFile(paramLaunchDesc, str1, str2, str7, paramString, str6, false, null, -1);
    arrayOfString[1] = str7;
    if (addUninstallShortcut())
      arrayOfString[2] = createDesktopFile(paramLaunchDesc, ResourceManager.getString("install.startMenuUninstallShortcutName", str1), str2, str7, paramString, "-uninstall", false, null, -1);
    Trace.println("directoryFileName: " + arrayOfString[1], TraceLevel.BASIC);
    Trace.println("desktopFileName: " + arrayOfString[0], TraceLevel.BASIC);
    RContentDesc[] arrayOfRContentDesc = localInformationDesc.getRelatedContent();
    if (arrayOfRContentDesc != null)
    {
      StringBuffer localStringBuffer = new StringBuffer(512 * arrayOfRContentDesc.length);
      for (int j = 0; j < arrayOfRContentDesc.length; j++)
      {
        URL localURL = arrayOfRContentDesc[j].getHref();
        if ((localURL == null) || (!localURL.toString().endsWith(".jnlp")))
        {
          String str8 = createRCDesktopFile(arrayOfRContentDesc[j], getRCIcon(arrayOfRContentDesc[j], paramLaunchDesc), str7);
          if (str8 != null)
          {
            localStringBuffer.append(str8);
            localStringBuffer.append(";");
          }
        }
      }
      arrayOfString[3] = localStringBuffer.toString();
    }
    return arrayOfString;
  }

  private String getFolderName(LaunchDesc paramLaunchDesc)
  {
    String str = null;
    if (paramLaunchDesc.getInformation().getShortcut() != null)
      str = paramLaunchDesc.getInformation().getShortcut().getSubmenu();
    if (str == null)
      str = nameFilter(paramLaunchDesc.getInformation().getTitle());
    str = str.replace('<', '-');
    str = str.replace('>', '-');
    return str;
  }

  protected abstract String writeDesktopEntry(DesktopEntry paramDesktopEntry, String paramString, Association paramAssociation, int paramInt);

  private String createDesktopShortcut(LaunchDesc paramLaunchDesc, String paramString, Association paramAssociation, int paramInt)
  {
    InformationDesc localInformationDesc = paramLaunchDesc.getInformation();
    ShortcutDesc localShortcutDesc = localInformationDesc.getShortcut();
    String str1 = nameFilter(localInformationDesc.getTitle());
    String str2 = getIcon(paramLaunchDesc, true);
    int i = (!localInformationDesc.supportsOfflineOperation()) || (localShortcutDesc == null) || (localShortcutDesc.getOnline()) ? 1 : 0;
    String str3 = i != 0 ? "" : "-offline ";
    Trace.println("iconPath: " + str2, TraceLevel.TEMP);
    String str4 = paramLaunchDesc.getLocation() != null ? paramLaunchDesc.getLocation().toString() : null;
    String str5 = "-J-Djnlp.application.href=" + str4 + " ";
    String str6 = "-localfile " + str3 + str5;
    return createDesktopFile(paramLaunchDesc, str1, str2, null, paramString, str6, true, paramAssociation, paramInt);
  }

  protected abstract String getMenuEntryDirPath(LaunchDesc paramLaunchDesc, boolean paramBoolean);

  private String getRCCommand(URL paramURL)
  {
    File localFile = CacheUtil.getCachedFileNative(paramURL);
    Object localObject = "";
    if (paramURL.toString().endsWith(".jnlp"))
      return Environment.getJavawsCommand() + " " + paramURL.toString();
    String str1;
    if (localFile != null)
    {
      str1 = localFile.getAbsolutePath();
      String str2 = str1.substring(str1.lastIndexOf("."), str1.length());
      if ((isAssociationSupported()) && (!str2.equals(".html")))
      {
        AssociationService localAssociationService = new AssociationService(null);
        Association localAssociation = localAssociationService.getFileExtensionAssociation(str2);
        if (localAssociation != null)
        {
          Action localAction = localAssociation.getActionByVerb("open");
          if (localAction != null)
          {
            String str3 = localAction.getCommand();
            StringTokenizer localStringTokenizer = new StringTokenizer(str3);
            if (localStringTokenizer.hasMoreTokens())
              str3 = localStringTokenizer.nextToken();
            localObject = str3;
          }
        }
      }
      if ("".equals(localObject))
        localObject = Config.getStringProperty("deployment.browser.path");
    }
    else
    {
      str1 = paramURL.toString();
      localObject = Config.getStringProperty("deployment.browser.path");
    }
    return (String)localObject + " " + str1;
  }

  private String createRCDesktopFile(RContentDesc paramRContentDesc, String paramString1, String paramString2)
  {
    URL localURL = paramRContentDesc.getHref();
    String str = nameFilter(paramRContentDesc.getTitle());
    DesktopEntry localDesktopEntry = new DesktopEntry();
    localDesktopEntry.setType("Application");
    localDesktopEntry.setExec(getRCCommand(localURL));
    localDesktopEntry.setIcon(paramString1);
    localDesktopEntry.setTerminal(false);
    localDesktopEntry.setName(str);
    localDesktopEntry.setComment(paramRContentDesc.getDescription());
    localDesktopEntry.setCategories("Applications;" + str);
    return writeMenuEntry(localDesktopEntry, paramString2, uniqDesktopFileName(str));
  }

  protected abstract String writeMenuEntry(DesktopEntry paramDesktopEntry, String paramString1, String paramString2);

  private String createDesktopFile(LaunchDesc paramLaunchDesc, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, boolean paramBoolean, Association paramAssociation, int paramInt)
  {
    InformationDesc localInformationDesc = paramLaunchDesc.getInformation();
    String str1 = getFolderName(paramLaunchDesc);
    String str2 = localInformationDesc.getDescription(0);
    String str3 = localInformationDesc.getDescription(3);
    String str4 = str3 == null ? str2 : str3;
    if (paramString5 == null)
      paramString5 = "";
    else if ((paramString5.length() > 0) && (!paramString5.endsWith(" ")))
      paramString5 = paramString5 + " ";
    DesktopEntry localDesktopEntry = new DesktopEntry();
    localDesktopEntry.setEncoding("UTF-8");
    localDesktopEntry.setType("Application");
    String str5;
    if (paramAssociation != null)
      str5 = Environment.getJavawsCommand() + " -open %U " + paramString5 + paramString4;
    else
      str5 = Environment.getJavawsCommand() + " " + paramString5 + paramString4;
    localDesktopEntry.setExec(str5);
    localDesktopEntry.setIcon(paramString2);
    localDesktopEntry.setTerminal(false);
    localDesktopEntry.setName(paramString1);
    localDesktopEntry.setComment(str4);
    localDesktopEntry.setCategories("Applications;" + str1);
    if (paramAssociation != null)
      localDesktopEntry.set("MimeType", paramAssociation.getMimeType());
    String str7 = uniqDesktopFileName(paramString1);
    String str6;
    if (paramBoolean)
      str6 = writeDesktopEntry(localDesktopEntry, str7, paramAssociation, paramInt);
    else
      str6 = writeMenuEntry(localDesktopEntry, paramString3, str7);
    return str6;
  }

  private String uniqDesktopFileName(String paramString)
  {
    return "jws_app_shortcut_" + System.currentTimeMillis() + ".desktop";
  }

  private void installFailed(LaunchDesc paramLaunchDesc)
  {
    Runnable local1 = new Runnable()
    {
      private final LaunchDesc val$desc;

      public void run()
      {
        ToolkitStore.getUI();
        ToolkitStore.getUI().showMessageDialog(null, null, 0, ResourceManager.getString("install.installFailedTitle"), ResourceManager.getString("install.installFailed"), UnixInstallHandler.this.nameFilter(this.val$desc.getInformation().getTitle()), null, null, null, null);
      }
    };
    invokeRunnable(local1);
  }

  public boolean removePathShortcut(String paramString)
  {
    return removeDesktopPath(paramString);
  }

  protected abstract boolean removeDesktopPath(String paramString);

  protected abstract boolean removeDirectory(String paramString);

  protected boolean removeShortcuts(LaunchDesc paramLaunchDesc, LocalApplicationProperties paramLocalApplicationProperties, boolean paramBoolean)
  {
    int i = 0;
    if (Environment.isSystemCacheMode())
    {
      paramBoolean = false;
      i = 1;
    }
    Trace.println("uninstall called in UnixInstallHandler", TraceLevel.BASIC);
    if (paramBoolean)
    {
      str1 = paramLocalApplicationProperties.get("installed.desktop");
      if (str1 != null)
      {
        removeDesktopPath(str1);
        paramLocalApplicationProperties.put("installed.desktop", null);
      }
    }
    String str1 = paramLocalApplicationProperties.get("installed.menu");
    if (str1 != null)
    {
      removeDesktopPath(str1);
      paramLocalApplicationProperties.put("installed.menu", null);
    }
    str1 = paramLocalApplicationProperties.get("installed.uninstalled");
    if (str1 != null)
    {
      removeDesktopPath(str1);
      paramLocalApplicationProperties.put("installed.uninstalled", null);
    }
    str1 = paramLocalApplicationProperties.get("installed.rc");
    if (str1 != null)
    {
      localObject = new StringTokenizer(str1, ";");
      while (((StringTokenizer)localObject).hasMoreElements())
      {
        String str2 = ((StringTokenizer)localObject).nextToken();
        if ((str2 != null) && (str2.trim().length() != 0))
          removeDesktopPath(str2);
      }
      paramLocalApplicationProperties.put("installed.rc", null);
    }
    Object localObject = paramLocalApplicationProperties.get("installed.directory");
    if (localObject != null)
    {
      removeDirectory((String)localObject);
      paramLocalApplicationProperties.put("installed.directory", null);
    }
    paramLocalApplicationProperties.setShortcutInstalled(false);
    save(paramLocalApplicationProperties);
    return true;
  }

  protected boolean isAssociationFileExtSupported(String paramString)
  {
    return true;
  }

  private String nameFilter(String paramString)
  {
    return Filter(paramString, "\"\\/|:?*<>#", '-');
  }

  protected String dirFilter(String paramString)
  {
    String str = Filter(paramString, "\"|:?*<>#", '-');
    return Filter(str, "/\\", File.separatorChar);
  }

  private String Filter(String paramString1, String paramString2, char paramChar)
  {
    if (paramString1 == null)
      return null;
    return checkTitleString(paramString1, paramString2, paramChar);
  }
}

/* Location:           /home/user1/Temp/jvm/javaws.jar
 * Qualified Name:     com.sun.javaws.UnixInstallHandler
 * JD-Core Version:    0.6.2
 */