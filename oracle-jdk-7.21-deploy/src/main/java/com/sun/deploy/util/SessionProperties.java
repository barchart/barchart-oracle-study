package com.sun.deploy.util;

import com.sun.deploy.trace.Trace;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;

public class SessionProperties extends Properties
  implements SessionState.Client
{
  private String _filename;

  public SessionProperties(String paramString)
  {
    this._filename = paramString;
  }

  public void importState(File paramFile)
  {
    try
    {
      AccessController.doPrivileged(new PrivilegedAction()
      {
        private final File val$folder;

        public Object run()
        {
          try
          {
            File localFile = new File(this.val$folder, SessionProperties.this._filename);
            if (localFile.exists())
            {
              FileInputStream localFileInputStream = new FileInputStream(localFile);
              try
              {
                SessionProperties.this.load(localFileInputStream);
              }
              finally
              {
                if (localFileInputStream != null)
                  localFileInputStream.close();
              }
            }
          }
          catch (Exception localException)
          {
            Trace.ignored(localException);
          }
          return null;
        }
      });
    }
    catch (Exception localException)
    {
      Trace.ignored(localException);
    }
  }

  public void exportState(File paramFile)
  {
    try
    {
      AccessController.doPrivileged(new PrivilegedAction()
      {
        private final File val$folder;

        public Object run()
        {
          try
          {
            File localFile = new File(this.val$folder, SessionProperties.this._filename);
            this.val$folder.mkdirs();
            FileOutputStream localFileOutputStream = new FileOutputStream(localFile);
            if (localFileOutputStream != null)
              try
              {
                SessionProperties.this.store(localFileOutputStream, "Session Data");
              }
              finally
              {
                if (localFileOutputStream != null)
                {
                  localFileOutputStream.flush();
                  localFileOutputStream.close();
                }
              }
          }
          catch (Exception localException)
          {
            Trace.ignored(localException);
          }
          return null;
        }
      });
    }
    catch (Exception localException)
    {
      Trace.ignored(localException);
    }
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.util.SessionProperties
 * JD-Core Version:    0.6.2
 */