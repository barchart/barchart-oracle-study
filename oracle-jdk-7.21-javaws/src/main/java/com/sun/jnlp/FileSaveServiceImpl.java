package com.sun.jnlp;

import com.sun.deploy.resources.ResourceManager;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.uitoolkit.ToolkitStore;
import com.sun.deploy.uitoolkit.ui.UIFactory;
import com.sun.deploy.util.DeploySysAction;
import com.sun.deploy.util.DeploySysRun;
import com.sun.deploy.util.Waiter;
import com.sun.deploy.util.Waiter.WaiterTask;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.jnlp.FileContents;
import javax.jnlp.FileSaveService;

public final class FileSaveServiceImpl
  implements FileSaveService
{
  static FileSaveServiceImpl _sharedInstance = null;
  private ApiDialog _apiDialog = new ApiDialog();
  private String _lastPath;

  public static synchronized FileSaveService getInstance()
  {
    if (_sharedInstance == null)
      _sharedInstance = new FileSaveServiceImpl();
    return _sharedInstance;
  }

  String getLastPath()
  {
    return this._lastPath;
  }

  void setLastPath(String paramString)
  {
    this._lastPath = paramString;
  }

  public FileContents saveAsFileDialog(String paramString, String[] paramArrayOfString, FileContents paramFileContents)
    throws IOException
  {
    return saveFileDialog(paramString, paramArrayOfString, paramFileContents.getInputStream(), paramFileContents.getName());
  }

  public FileContents saveFileDialog(String paramString1, String[] paramArrayOfString, InputStream paramInputStream, String paramString2)
    throws IOException
  {
    if (!askUser())
      return null;
    Waiter.WaiterTask local1 = new Waiter.WaiterTask()
    {
      private final String val$pathHint;
      private final String[] val$extensions;
      private final String val$filename;
      private final InputStream val$stream;

      public Object run()
        throws Exception
      {
        Object localObject = DeploySysRun.executePrivileged(new DeploySysAction()
        {
          public Object execute()
          {
            String str = FileSaveServiceImpl.1.this.val$pathHint;
            if (str == null)
              str = FileSaveServiceImpl.this.getLastPath();
            File[] arrayOfFile = ToolkitStore.getUI().showFileChooser(str, FileSaveServiceImpl.1.this.val$extensions, 9, false, FileSaveServiceImpl.1.this.val$filename);
            if (arrayOfFile[0] != null)
              try
              {
                byte[] arrayOfByte = new byte[8192];
                BufferedOutputStream localBufferedOutputStream = new BufferedOutputStream(new FileOutputStream(arrayOfFile[0]));
                BufferedInputStream localBufferedInputStream = new BufferedInputStream(FileSaveServiceImpl.1.this.val$stream);
                for (int i = localBufferedInputStream.read(arrayOfByte); i != -1; i = localBufferedInputStream.read(arrayOfByte))
                  localBufferedOutputStream.write(arrayOfByte, 0, i);
                localBufferedOutputStream.close();
                FileSaveServiceImpl.this.setLastPath(arrayOfFile[0].getPath());
                return new FileContentsImpl(arrayOfFile[0], FileSaveServiceImpl.computeMaxLength(arrayOfFile[0].length()));
              }
              catch (IOException localIOException)
              {
                Trace.ignored(localIOException);
                return localIOException;
              }
            return null;
          }
        }
        , null);
        if ((localObject instanceof IOException))
          throw ((IOException)localObject);
        return (FileContents)localObject;
      }
    };
    try
    {
      return (FileContents)Waiter.runAndWait(local1);
    }
    catch (Exception localException)
    {
      if ((localException instanceof IOException))
        throw ((IOException)localException);
      Trace.ignored(localException);
    }
    return null;
  }

  synchronized boolean askUser()
  {
    if (CheckServicePermission.hasFileAccessPermissions())
      return true;
    return this._apiDialog.askUser(ResourceManager.getString("api.file.save.title"), ResourceManager.getString("api.file.save.message"), ResourceManager.getString("api.file.save.always"));
  }

  static long computeMaxLength(long paramLong)
  {
    return paramLong * 3L;
  }
}

/* Location:           /home/user1/Temp/jvm/javaws.jar
 * Qualified Name:     com.sun.jnlp.FileSaveServiceImpl
 * JD-Core Version:    0.6.2
 */