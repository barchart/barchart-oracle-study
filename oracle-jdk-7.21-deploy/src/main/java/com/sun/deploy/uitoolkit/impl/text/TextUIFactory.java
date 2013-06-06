package com.sun.deploy.uitoolkit.impl.text;

import com.sun.deploy.security.CredentialInfo;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.ui.AppInfo;
import com.sun.deploy.uitoolkit.ui.ConsoleController;
import com.sun.deploy.uitoolkit.ui.ConsoleWindow;
import com.sun.deploy.uitoolkit.ui.DialogHook;
import com.sun.deploy.uitoolkit.ui.UIFactory;
import java.io.File;
import java.net.URL;
import java.security.cert.Certificate;
import java.util.TreeMap;
import java.util.Vector;

public class TextUIFactory extends UIFactory
{
  ConsoleWindow cw = new TextConsoleWindow();

  public int showMessageDialog(Object paramObject, AppInfo paramAppInfo, int paramInt, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7)
  {
    Trace.println("TextUIFactory showMessageDialog()");
    Trace.println("TITLE: " + paramString1);
    Trace.println("MESSAGE: " + paramString3);
    if (paramString4 != null)
      Trace.println("DETAIL: " + paramString4);
    return 0;
  }

  public void showExceptionDialog(Object paramObject, AppInfo paramAppInfo, Throwable paramThrowable, String paramString1, String paramString2, String paramString3, Certificate[] paramArrayOfCertificate)
  {
    Trace.println("TextUIFactory showExceptionDialog()");
  }

  public CredentialInfo showPasswordDialog(Object paramObject, String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2, CredentialInfo paramCredentialInfo, boolean paramBoolean3, String paramString3)
  {
    Trace.println("TextUIFactory showPasswordDialog()");
    return null;
  }

  public int showSecurityDialog(AppInfo paramAppInfo, String paramString1, String paramString2, String paramString3, URL paramURL, boolean paramBoolean1, boolean paramBoolean2, String paramString4, String paramString5, String[] paramArrayOfString1, String[] paramArrayOfString2, boolean paramBoolean3, Certificate[] paramArrayOfCertificate, int paramInt1, int paramInt2, boolean paramBoolean4, boolean paramBoolean5)
  {
    return showSecurityDialog(paramAppInfo, paramString1, paramString2, paramString3, paramURL, paramBoolean1, paramBoolean2, paramString4, paramString5, paramArrayOfString1, paramArrayOfString2, paramBoolean3, paramArrayOfCertificate, paramInt1, paramInt2, paramBoolean4);
  }

  public int showSecurityDialog(AppInfo paramAppInfo, String paramString1, String paramString2, String paramString3, URL paramURL, boolean paramBoolean1, boolean paramBoolean2, String paramString4, String paramString5, String[] paramArrayOfString1, String[] paramArrayOfString2, boolean paramBoolean3, Certificate[] paramArrayOfCertificate, int paramInt1, int paramInt2, boolean paramBoolean4)
  {
    Trace.println("TextUIFactory showSecurityDialog()");
    return 0;
  }

  public int showSandboxSecurityDialog(AppInfo paramAppInfo, String paramString1, String paramString2, String paramString3, URL paramURL, boolean paramBoolean1, boolean paramBoolean2, String paramString4, String paramString5, String[] paramArrayOfString1, String[] paramArrayOfString2, boolean paramBoolean3, Certificate[] paramArrayOfCertificate, int paramInt1, int paramInt2, boolean paramBoolean4)
  {
    Trace.println("TextUIFactory showSecurityDialog()");
    return 0;
  }

  public void showAboutJavaDialog()
  {
    Trace.println("TextUIFactory showAboutJavaDialog()");
  }

  public int showListDialog(Object paramObject, String paramString1, String paramString2, String paramString3, boolean paramBoolean, Vector paramVector, TreeMap paramTreeMap)
  {
    Trace.println("TextUIToolkit showListDialog()");
    return 0;
  }

  public int showUpdateCheckDialog()
  {
    Trace.println("TextUIToolkit showUpdateCheckDialog()");
    return 0;
  }

  public ConsoleWindow getConsole(ConsoleController paramConsoleController)
  {
    Trace.println("TextUIToolkit getConsole()");
    return this.cw;
  }

  public void setDialogHook(DialogHook paramDialogHook)
  {
    Trace.println("TextUIToolkit setDialogHook()" + paramDialogHook);
  }

  public File[] showFileChooser(String paramString1, String[] paramArrayOfString, int paramInt, boolean paramBoolean, String paramString2)
  {
    Trace.println("TextUIToolkit showFileChooser showMultiple:" + paramBoolean);
    Trace.println("initDir:" + paramString1 + " option:" + paramInt);
    return null;
  }

  public int showSSV3Dialog(Object paramObject, AppInfo paramAppInfo, int paramInt, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, String paramString8, String paramString9, String paramString10, String paramString11, URL paramURL)
  {
    Trace.println("TextUIToolkit showSSV3Dialog()");
    return 0;
  }

  public int showPublisherInfo(Object paramObject, AppInfo paramAppInfo, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6)
  {
    Trace.println("TextUIToolkit showPublisherInfo()");
    return 0;
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.uitoolkit.impl.text.TextUIFactory
 * JD-Core Version:    0.6.2
 */