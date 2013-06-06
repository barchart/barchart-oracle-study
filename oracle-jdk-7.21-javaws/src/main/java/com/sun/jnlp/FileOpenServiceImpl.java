package com.sun.jnlp;

import com.sun.deploy.resources.ResourceManager;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.uitoolkit.ToolkitStore;
import com.sun.deploy.uitoolkit.ui.UIFactory;
import com.sun.deploy.util.DeploySysAction;
import com.sun.deploy.util.DeploySysRun;
import com.sun.deploy.util.Waiter;
import com.sun.deploy.util.Waiter.WaiterTask;
import java.io.File;
import java.io.IOException;
import javax.jnlp.FileContents;
import javax.jnlp.FileOpenService;

public final class FileOpenServiceImpl
  implements FileOpenService
{
  static FileOpenServiceImpl _sharedInstance = null;
  static FileSaveServiceImpl _fileSaveServiceImpl;
  private ApiDialog _apiDialog;

  private FileOpenServiceImpl(FileSaveServiceImpl paramFileSaveServiceImpl)
  {
    _fileSaveServiceImpl = paramFileSaveServiceImpl;
    this._apiDialog = new ApiDialog();
  }

  public static synchronized FileOpenService getInstance()
  {
    if (_sharedInstance == null)
      _sharedInstance = new FileOpenServiceImpl((FileSaveServiceImpl)FileSaveServiceImpl.getInstance());
    return _sharedInstance;
  }

  public FileContents openFileDialog(String paramString, String[] paramArrayOfString)
    throws IOException
  {
    if (!askUser())
      return null;
    Waiter.WaiterTask local1 = new Waiter.WaiterTask()
    {
      private final String val$pathHint;
      private final String[] val$extensions;

      public Object run()
        throws Exception
      {
        return (FileContents)DeploySysRun.executePrivileged(new DeploySysAction()
        {
          public Object execute()
          {
            String str = FileOpenServiceImpl.1.this.val$pathHint;
            if (str == null)
              str = FileOpenServiceImpl._fileSaveServiceImpl.getLastPath();
            File[] arrayOfFile = ToolkitStore.getUI().showFileChooser(str, FileOpenServiceImpl.1.this.val$extensions, 8, false, null);
            if (arrayOfFile[0] != null)
              try
              {
                FileOpenServiceImpl._fileSaveServiceImpl.setLastPath(arrayOfFile[0].getPath());
                return new FileContentsImpl(arrayOfFile[0], FileSaveServiceImpl.computeMaxLength(arrayOfFile[0].length()));
              }
              catch (IOException localIOException)
              {
              }
            return null;
          }
        }
        , null);
      }
    };
    try
    {
      return (FileContents)Waiter.runAndWait(local1);
    }
    catch (Exception localException)
    {
      Trace.ignored(localException);
    }
    return null;
  }

  public FileContents[] openMultiFileDialog(String paramString, String[] paramArrayOfString)
    throws IOException
  {
    if (!askUser())
      return null;
    Waiter.WaiterTask local2 = new Waiter.WaiterTask()
    {
      private final String val$pathHint;
      private final String[] val$extentions;

      public Object run()
        throws Exception
      {
        return (FileContents[])DeploySysRun.executePrivileged(new DeploySysAction()
        {
          public Object execute()
          {
            String str = FileOpenServiceImpl.2.this.val$pathHint;
            if (str == null)
              str = FileOpenServiceImpl._fileSaveServiceImpl.getLastPath();
            File[] arrayOfFile = ToolkitStore.getUI().showFileChooser(str, FileOpenServiceImpl.2.this.val$extentions, 8, true, null);
            if ((arrayOfFile != null) && (arrayOfFile.length > 0))
            {
              FileContents[] arrayOfFileContents = new FileContents[arrayOfFile.length];
              for (int i = 0; i < arrayOfFile.length; i++)
                try
                {
                  arrayOfFileContents[i] = new FileContentsImpl(arrayOfFile[i], FileSaveServiceImpl.computeMaxLength(arrayOfFile[i].length()));
                  FileOpenServiceImpl._fileSaveServiceImpl.setLastPath(arrayOfFile[i].getPath());
                }
                catch (IOException localIOException)
                {
                }
              return arrayOfFileContents;
            }
            return null;
          }
        }
        , null);
      }
    };
    try
    {
      return (FileContents[])Waiter.runAndWait(local2);
    }
    catch (Exception localException)
    {
      Trace.ignored(localException);
    }
    return null;
  }

  synchronized boolean askUser()
  {
    if (CheckServicePermission.hasFileAccessPermissions())
      return true;
    return this._apiDialog.askUser(ResourceManager.getString("api.file.open.title"), ResourceManager.getString("api.file.open.message"), ResourceManager.getString("api.file.open.always"));
  }
}

/* Location:           /home/user1/Temp/jvm/javaws.jar
 * Qualified Name:     com.sun.jnlp.FileOpenServiceImpl
 * JD-Core Version:    0.6.2
 */