package com.sun.deploy.util;

import com.sun.deploy.trace.Trace;
import com.sun.deploy.trace.TraceLevel;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class SessionState
{
  private static File sessionDataFolder;
  private static List sessionObjects = new LinkedList();

  public static synchronized void register(Client paramClient)
  {
    sessionObjects.add(paramClient);
    if (sessionDataFolder != null)
      paramClient.importState(sessionDataFolder);
  }

  public static synchronized File save()
  {
    File localFile = null;
    try
    {
      localFile = File.createTempFile("session", "");
      localFile.delete();
      localFile.mkdirs();
      Trace.println("Saving session state to " + localFile.getAbsolutePath(), TraceLevel.BASIC);
      Iterator localIterator = sessionObjects.iterator();
      while (localIterator.hasNext())
      {
        Client localClient = (Client)localIterator.next();
        localClient.exportState(localFile);
      }
    }
    catch (IOException localIOException)
    {
      Trace.ignored(localIOException);
    }
    return localFile;
  }

  public static synchronized void init(String paramString)
  {
    if (paramString != null)
    {
      sessionDataFolder = new File(paramString);
      Trace.println("Session state location: " + sessionDataFolder.getAbsolutePath(), TraceLevel.BASIC);
    }
    Runtime.getRuntime().addShutdownHook(new Thread()
    {
      public void run()
      {
        try
        {
          if (SessionState.sessionDataFolder != null)
            SystemUtils.deleteRecursive(SessionState.sessionDataFolder);
        }
        catch (FileNotFoundException localFileNotFoundException)
        {
        }
      }
    });
  }

  public static abstract interface Client
  {
    public abstract void importState(File paramFile);

    public abstract void exportState(File paramFile);
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.util.SessionState
 * JD-Core Version:    0.6.2
 */