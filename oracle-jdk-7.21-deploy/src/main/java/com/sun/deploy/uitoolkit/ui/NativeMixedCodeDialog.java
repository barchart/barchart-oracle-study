package com.sun.deploy.uitoolkit.ui;

import com.sun.deploy.Environment;
import com.sun.deploy.config.OSType;
import com.sun.deploy.config.Platform;
import com.sun.deploy.resources.ResourceManager;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.trace.TraceLevel;
import com.sun.deploy.trace.TraceListener;
import java.io.PrintStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.security.action.GetPropertyAction;

public class NativeMixedCodeDialog
{
  public static final int ERROR = -1;
  public static final int OK = 0;
  public static final int CANCEL = 1;
  private static Boolean jnlpNativeMixedCodeDialog = null;

  public static final void main(String[] paramArrayOfString)
  {
    int i = -1;
    try
    {
      TraceListener local1 = new TraceListener()
      {
        public void flush()
        {
          System.err.flush();
        }

        public void print(String paramAnonymousString)
        {
          System.err.print(paramAnonymousString);
        }
      };
      Trace.addTraceListener(local1);
      Trace.println("NativeMixedCodeDialog.main enter");
      Platform.get();
      String str1 = ResourceManager.getString("security.dialog.nativemixcode.title");
      String str2 = ResourceManager.getString("security.dialog.nativemixcode.masthead");
      String str3 = ResourceManager.getString("security.dialog.nativemixcode.message");
      String str4 = ResourceManager.getString("security.dialog.nativemixcode.info");
      String str5 = ResourceManager.getString("security.dialog.nativemixcode.blockBtnStr");
      String str6 = ResourceManager.getString("security.dialog.nativemixcode.dontBlockBtnStr");
      String str7 = ResourceManager.getString("security.dialog.nativemixcode.helpBtnStr");
      String str8 = ResourceManager.getString("security.dialog.nativemixcode.closeBtnStr");
      String str9 = ResourceManager.getString("security.dialog.nativemixcode.helpTitle");
      String str10 = ResourceManager.getString("security.dialog.nativemixcode.helpMessage");
      String str11 = ResourceManager.getString("security.dialog.nativemixcode.appLabelStr");
      String str12 = paramArrayOfString[0];
      if (isSupported())
      {
        i = showImmediately(str1, str2, str3, str4, str5, str6, str7, str8, str9, str10, str11, str12);
        Trace.println("NativeMixedCodeDialog.show(...) returns " + i);
      }
      else
      {
        Trace.println("NativeMixedCodeDialog isn't supported");
      }
      Trace.println("NativeMixedCodeDialog.main exit");
      Trace.ensureMessageQueueProcessingStarted();
    }
    catch (Throwable localThrowable)
    {
      Trace.ignored(localThrowable);
      i = -1;
    }
    System.exit(i);
  }

  private static native int _show(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, String paramString8, String paramString9, String paramString10, String paramString11, String paramString12);

  private static native void _activateCurrentProcess();

  private static native boolean _isMainToolkitThread();

  public static int show(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, String paramString8, String paramString9, String paramString10, String paramString11, String paramString12)
  {
    if (_isMainToolkitThread())
      return showImmediately(paramString1, paramString2, paramString3, paramString4, paramString5, paramString6, paramString7, paramString8, paramString9, paramString10, paramString11, paramString12);
    String[] arrayOfString = { Environment.getJavaHome() + "/bin/java", "-cp", Environment.getJavaHome() + "/lib/deploy.jar", "com.sun.deploy.uitoolkit.ui.NativeMixedCodeDialog", paramString12 };
    Trace.println("NativeMixedCodeDialog executes the command in a separate process:", TraceLevel.UI);
    for (int i = 0; i < arrayOfString.length; i++)
      Trace.println("    args[" + i + "]=" + arrayOfString[i], TraceLevel.UI);
    i = -1;
    try
    {
      Process localProcess = Runtime.getRuntime().exec(arrayOfString);
      i = localProcess.waitFor();
      _activateCurrentProcess();
    }
    catch (Throwable localThrowable)
    {
      Trace.ignored(localThrowable);
      i = -1;
    }
    return i;
  }

  private static int showImmediately(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, String paramString8, String paramString9, String paramString10, String paramString11, String paramString12)
  {
    if ((paramString1 == null) || (paramString2 == null) || (paramString3 == null) || (paramString4 == null) || (paramString5 == null) || (paramString6 == null) || (paramString7 == null) || (paramString8 == null) || (paramString9 == null) || (paramString10 == null) || (paramString11 == null) || (paramString12 == null))
    {
      Trace.println("parameters shouldn't be null", TraceLevel.UI);
      return -1;
    }
    PrivilegedAction local2 = new PrivilegedAction()
    {
      private final String val$title;
      private final String val$masthead;
      private final String val$message;
      private final String val$info;
      private final String val$blockBtnStr;
      private final String val$dontBlockBtnStr;
      private final String val$helpBtnStr;
      private final String val$closeBtnStr;
      private final String val$helpTitle;
      private final String val$helpMessage;
      private final String val$appLabelStr;
      private final String val$appTitle;

      public Object run()
      {
        int i = NativeMixedCodeDialog._show(this.val$title, this.val$masthead, this.val$message, this.val$info, this.val$blockBtnStr, this.val$dontBlockBtnStr, this.val$helpBtnStr, this.val$closeBtnStr, this.val$helpTitle, this.val$helpMessage, this.val$appLabelStr, this.val$appTitle);
        return new Integer(i);
      }
    };
    try
    {
      return ((Integer)AccessController.doPrivileged(local2)).intValue();
    }
    catch (Throwable localThrowable)
    {
      Trace.ignored(localThrowable);
    }
    return -1;
  }

  public static boolean isSupported()
  {
    return ((!OSType.isUnix()) || (Platform.get().isGTKAvailable(2, 14, 0))) && (getNativeMixedCodeDialogProperty());
  }

  private static boolean getNativeMixedCodeDialogProperty()
  {
    if (jnlpNativeMixedCodeDialog == null)
    {
      GetPropertyAction localGetPropertyAction = new GetPropertyAction("jnlp.nativeMixedCodeDialog", "true");
      String str = (String)AccessController.doPrivileged(localGetPropertyAction);
      if (str.equals("false"))
        jnlpNativeMixedCodeDialog = Boolean.FALSE;
      else
        jnlpNativeMixedCodeDialog = Boolean.TRUE;
    }
    return jnlpNativeMixedCodeDialog.booleanValue();
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.uitoolkit.ui.NativeMixedCodeDialog
 * JD-Core Version:    0.6.2
 */