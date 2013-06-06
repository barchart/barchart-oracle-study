package sun.plugin2.applet;

import com.sun.deploy.config.Config;
import java.util.ArrayList;
import java.util.List;

public class Applet2ThreadGroup extends ThreadGroup
{
  public Applet2ThreadGroup(String paramString)
  {
    this(Thread.currentThread().getThreadGroup(), paramString);
  }

  public Applet2ThreadGroup(ThreadGroup paramThreadGroup, String paramString)
  {
    super(paramThreadGroup, paramString);
    setMaxPriority(4);
  }

  public static int getAppletThreadCount(int paramInt)
  {
    if (paramInt == 0)
      return 0;
    Thread[] arrayOfThread = new Thread[paramInt];
    int i = 0;
    List localList = getAppletThreadGroups();
    for (int j = 0; j < localList.size(); j++)
    {
      Applet2ThreadGroup localApplet2ThreadGroup = (Applet2ThreadGroup)localList.get(j);
      i += localApplet2ThreadGroup.enumerate(arrayOfThread);
      if (i >= paramInt)
      {
        i = paramInt;
        break;
      }
    }
    return i;
  }

  public static int getAppletThreadCount()
  {
    return getAppletThreadCount(Config.getMaxAppletThreadCount());
  }

  private static ThreadGroup getRootThreadGroup()
  {
    for (ThreadGroup localThreadGroup = Thread.currentThread().getThreadGroup(); localThreadGroup.getParent() != null; localThreadGroup = localThreadGroup.getParent());
    return localThreadGroup;
  }

  public static List getAppletThreadGroups()
  {
    ThreadGroup localThreadGroup = getRootThreadGroup();
    int i = localThreadGroup.activeGroupCount() + 5;
    if (i < 20)
      i = 20;
    ThreadGroup[] arrayOfThreadGroup = new ThreadGroup[i];
    int j = 0;
    while ((j = localThreadGroup.enumerate(arrayOfThreadGroup, true)) == i)
    {
      i *= 2;
      arrayOfThreadGroup = new ThreadGroup[i];
    }
    ArrayList localArrayList = new ArrayList(j);
    for (int k = 0; k < j; k++)
      if ((arrayOfThreadGroup[k] instanceof Applet2ThreadGroup))
        localArrayList.add(arrayOfThreadGroup[k]);
    return localArrayList;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.applet.Applet2ThreadGroup
 * JD-Core Version:    0.6.2
 */