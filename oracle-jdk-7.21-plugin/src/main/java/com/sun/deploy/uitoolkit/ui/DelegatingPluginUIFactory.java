package com.sun.deploy.uitoolkit.ui;

import com.sun.deploy.security.CredentialInfo;
import com.sun.deploy.ui.AppInfo;
import java.io.File;
import java.net.URL;
import java.security.cert.Certificate;
import java.util.TreeMap;
import java.util.Vector;

public abstract class DelegatingPluginUIFactory extends PluginUIFactory
{
  final UIFactory factory;

  private DelegatingPluginUIFactory()
  {
    this.factory = null;
  }

  public DelegatingPluginUIFactory(UIFactory paramUIFactory)
  {
    this.factory = paramUIFactory;
  }

  public int showMessageDialog(Object paramObject, AppInfo paramAppInfo, int paramInt, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7)
  {
    return this.factory.showMessageDialog(paramObject, paramAppInfo, paramInt, paramString1, paramString2, paramString3, paramString4, paramString5, paramString6, paramString7);
  }

  public int showMessageDialog(Object paramObject, AppInfo paramAppInfo, int paramInt1, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, URL paramURL, String paramString8, int paramInt2)
  {
    return this.factory.showMessageDialog(paramObject, paramAppInfo, paramInt1, paramString1, paramString2, paramString3, paramString4, paramString5, paramString6, paramString7, paramURL, paramString8, paramInt2);
  }

  public void showExceptionDialog(Object paramObject, AppInfo paramAppInfo, Throwable paramThrowable, String paramString1, String paramString2, String paramString3, Certificate[] paramArrayOfCertificate)
  {
    this.factory.showExceptionDialog(paramObject, paramAppInfo, paramThrowable, paramString1, paramString2, paramString3, paramArrayOfCertificate);
  }

  public CredentialInfo showPasswordDialog(Object paramObject, String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2, CredentialInfo paramCredentialInfo, boolean paramBoolean3, String paramString3)
  {
    return this.factory.showPasswordDialog(paramObject, paramString1, paramString2, paramBoolean1, paramBoolean2, paramCredentialInfo, paramBoolean3, paramString3);
  }

  public int showSecurityDialog(AppInfo paramAppInfo, String paramString1, String paramString2, String paramString3, URL paramURL, boolean paramBoolean1, boolean paramBoolean2, String paramString4, String paramString5, String[] paramArrayOfString1, String[] paramArrayOfString2, boolean paramBoolean3, Certificate[] paramArrayOfCertificate, int paramInt1, int paramInt2, boolean paramBoolean4)
  {
    return this.factory.showSecurityDialog(paramAppInfo, paramString1, paramString2, paramString3, paramURL, paramBoolean1, paramBoolean2, paramString4, paramString5, paramArrayOfString1, paramArrayOfString2, paramBoolean3, paramArrayOfCertificate, paramInt1, paramInt2, paramBoolean4);
  }

  public int showSecurityDialog(AppInfo paramAppInfo, String paramString1, String paramString2, String paramString3, URL paramURL, boolean paramBoolean1, boolean paramBoolean2, String paramString4, String paramString5, String[] paramArrayOfString1, String[] paramArrayOfString2, boolean paramBoolean3, Certificate[] paramArrayOfCertificate, int paramInt1, int paramInt2, boolean paramBoolean4, boolean paramBoolean5)
  {
    return this.factory.showSecurityDialog(paramAppInfo, paramString1, paramString2, paramString3, paramURL, paramBoolean1, paramBoolean2, paramString4, paramString5, paramArrayOfString1, paramArrayOfString2, paramBoolean3, paramArrayOfCertificate, paramInt1, paramInt2, paramBoolean4, paramBoolean5);
  }

  public int showSandboxSecurityDialog(AppInfo paramAppInfo, String paramString1, String paramString2, String paramString3, URL paramURL, boolean paramBoolean1, boolean paramBoolean2, String paramString4, String paramString5, String[] paramArrayOfString1, String[] paramArrayOfString2, boolean paramBoolean3, Certificate[] paramArrayOfCertificate, int paramInt1, int paramInt2, boolean paramBoolean4)
  {
    return this.factory.showSandboxSecurityDialog(paramAppInfo, paramString1, paramString2, paramString3, paramURL, paramBoolean1, paramBoolean2, paramString4, paramString5, paramArrayOfString1, paramArrayOfString2, paramBoolean3, paramArrayOfCertificate, paramInt1, paramInt2, paramBoolean4);
  }

  public void showAboutJavaDialog()
  {
    this.factory.showAboutJavaDialog();
  }

  public int showListDialog(Object paramObject, String paramString1, String paramString2, String paramString3, boolean paramBoolean, Vector paramVector, TreeMap paramTreeMap)
  {
    return this.factory.showListDialog(paramObject, paramString1, paramString2, paramString3, paramBoolean, paramVector, paramTreeMap);
  }

  public int showUpdateCheckDialog()
  {
    return this.factory.showUpdateCheckDialog();
  }

  public ConsoleWindow getConsole(ConsoleController paramConsoleController)
  {
    return this.factory.getConsole(paramConsoleController);
  }

  public void setDialogHook(DialogHook paramDialogHook)
  {
    this.factory.setDialogHook(paramDialogHook);
  }

  public int showSSVDialog(Object paramObject, AppInfo paramAppInfo, String paramString1, String paramString2, String paramString3, String paramString4, URL paramURL, String paramString5, String paramString6, String paramString7, String paramString8, String paramString9)
  {
    return this.factory.showSSVDialog(paramObject, paramAppInfo, paramString1, paramString2, paramString3, paramString4, paramURL, paramString5, paramString6, paramString7, paramString8, paramString9);
  }

  public File[] showFileChooser(String paramString1, String[] paramArrayOfString, int paramInt, boolean paramBoolean, String paramString2)
  {
    return this.factory.showFileChooser(paramString1, paramArrayOfString, paramInt, paramBoolean, paramString2);
  }

  public int showSSV3Dialog(Object paramObject, AppInfo paramAppInfo, int paramInt, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, String paramString8, String paramString9, String paramString10, String paramString11, URL paramURL)
  {
    return this.factory.showSSV3Dialog(paramObject, paramAppInfo, paramInt, paramString1, paramString2, paramString3, paramString4, paramString5, paramString6, paramString7, paramString8, paramString9, paramString10, paramString11, paramURL);
  }

  public int showPublisherInfo(Object paramObject, AppInfo paramAppInfo, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6)
  {
    return this.factory.showPublisherInfo(paramObject, paramAppInfo, paramString1, paramString2, paramString3, paramString4, paramString5, paramString6);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     com.sun.deploy.uitoolkit.ui.DelegatingPluginUIFactory
 * JD-Core Version:    0.6.2
 */