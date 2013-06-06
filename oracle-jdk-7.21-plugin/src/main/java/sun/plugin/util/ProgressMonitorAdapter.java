package sun.plugin.util;

import com.sun.applet2.preloader.CancelException;
import com.sun.applet2.preloader.Preloader;
import com.sun.applet2.preloader.event.DownloadEvent;
import com.sun.deploy.util.URLUtil;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import sun.net.ProgressEvent;
import sun.net.ProgressListener;

public class ProgressMonitorAdapter
  implements ProgressListener
{
  private int numJarTotal = 0;
  private int numberOfJarLoaded = 0;
  private final ArrayList progressSourceFilterList = new ArrayList();
  private URL[] jarURLs = new URL[0];
  private final HashMap downloadInProgressMap = new HashMap();
  private URL codebaseURL;
  Preloader delegate;

  public ProgressMonitorAdapter(Preloader paramPreloader)
  {
    this.delegate = paramPreloader;
  }

  public void setProgressFilter(URL paramURL, String paramString)
  {
    this.codebaseURL = paramURL;
    if (paramString != null)
    {
      ArrayList localArrayList = new ArrayList();
      URL[] arrayOfURL = new URL[0];
      StringTokenizer localStringTokenizer = new StringTokenizer(paramString, ",", false);
      while (localStringTokenizer.hasMoreTokens())
      {
        String str = localStringTokenizer.nextToken().trim();
        try
        {
          URL localURL = new URL(paramURL, str);
          if (!URLUtil.checkTargetURL(paramURL, localURL))
            throw new SecurityException("Permission denied: " + localURL);
          localArrayList.add(localURL);
        }
        catch (MalformedURLException localMalformedURLException)
        {
          localMalformedURLException.printStackTrace();
        }
      }
      arrayOfURL = new URL[localArrayList.size()];
      int i = 0;
      Iterator localIterator = localArrayList.iterator();
      while (localIterator.hasNext())
      {
        arrayOfURL[i] = ((URL)localIterator.next());
        i++;
      }
      this.jarURLs = arrayOfURL;
      this.numJarTotal = arrayOfURL.length;
    }
  }

  public void progressStart(ProgressEvent paramProgressEvent)
  {
    synchronized (this.progressSourceFilterList)
    {
      if (this.progressSourceFilterList.contains(paramProgressEvent.getSource()))
        return;
      if (this.numJarTotal > 0)
        for (int i = 0; i < this.jarURLs.length; i++)
          if (paramProgressEvent.getURL().equals(this.jarURLs[i]))
          {
            this.progressSourceFilterList.add(paramProgressEvent.getSource());
            synchronized (this.downloadInProgressMap)
            {
              this.downloadInProgressMap.put(paramProgressEvent.getURL(), paramProgressEvent);
            }
            break;
          }
      else if (paramProgressEvent.getURL().toString().startsWith(this.codebaseURL.toString()))
        this.progressSourceFilterList.add(paramProgressEvent.getSource());
    }
  }

  public void progressUpdate(ProgressEvent paramProgressEvent)
  {
    synchronized (this.progressSourceFilterList)
    {
      if (this.progressSourceFilterList.isEmpty())
        progressStart(paramProgressEvent);
      if (!this.progressSourceFilterList.contains(paramProgressEvent.getSource()))
        return;
    }
    if (this.numJarTotal > 0)
    {
      synchronized (this.downloadInProgressMap)
      {
        this.downloadInProgressMap.put(paramProgressEvent.getURL(), paramProgressEvent);
      }
      sendEvent(paramProgressEvent.getURL(), ProgressMonitor.getProgress(paramProgressEvent), ProgressMonitor.getExpected(paramProgressEvent), getCurrentProgress());
    }
  }

  public void progressFinish(ProgressEvent paramProgressEvent)
  {
    Object localObject1 = 0;
    int j = 0;
    synchronized (this.progressSourceFilterList)
    {
      if (!this.progressSourceFilterList.contains(paramProgressEvent.getSource()))
        return;
      this.progressSourceFilterList.remove(paramProgressEvent.getSource());
    }
    if (ProgressMonitor.getProgress(paramProgressEvent) == 0L)
    {
      this.downloadInProgressMap.remove(paramProgressEvent.getURL());
      return;
    }
    int i;
    if (this.numJarTotal > 0)
    {
      synchronized (this.downloadInProgressMap)
      {
        this.downloadInProgressMap.remove(paramProgressEvent.getURL());
        this.numberOfJarLoaded += 1;
        if (this.numJarTotal == this.numberOfJarLoaded)
          localObject1 = 100;
        else
          localObject1 = getCurrentProgress();
      }
    }
    else
    {
      ??? = (100 - localObject1) / 2;
      localObject1 += ???;
    }
    if (j == 0)
      sendEvent(paramProgressEvent.getURL(), ProgressMonitor.getProgress(paramProgressEvent), ProgressMonitor.getExpected(paramProgressEvent), i);
  }

  private void sendEvent(URL paramURL, long paramLong1, long paramLong2, long paramLong3)
  {
    try
    {
      this.delegate.handleEvent(new DownloadEvent(0, paramURL, null, null, (int)paramLong1, (int)paramLong2, (int)paramLong3));
    }
    catch (CancelException localCancelException)
    {
    }
  }

  private int getCurrentProgress()
  {
    if (this.numJarTotal == 0)
      return 100;
    double d1 = 100.0D / this.numJarTotal;
    double d2 = d1 * this.numberOfJarLoaded;
    synchronized (this.downloadInProgressMap)
    {
      Iterator localIterator = this.downloadInProgressMap.values().iterator();
      while (localIterator.hasNext())
      {
        ProgressEvent localProgressEvent = (ProgressEvent)localIterator.next();
        long l = ProgressMonitor.getExpected(localProgressEvent);
        if (l > 0L)
          d2 += d1 * ProgressMonitor.getProgress(localProgressEvent) / l;
        else
          d2 += d1 / 2.0D;
      }
    }
    if (d2 < 100.0D)
      return (int)d2;
    return 100;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.util.ProgressMonitorAdapter
 * JD-Core Version:    0.6.2
 */