package sun.plugin2.ipc;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import sun.plugin2.util.SystemUtil;

public abstract class IPCFactory
{
  public static final int KB = 1024;
  public static final int PIPE_BUF_SZ = 4096;
  private static IPCFactory instance;

  public static IPCFactory getFactory()
  {
    if (instance == null)
      try
      {
        String str;
        switch (SystemUtil.getOSType())
        {
        case 1:
          str = "sun.plugin2.ipc.windows.WindowsIPCFactory";
          break;
        case 2:
        case 3:
          str = "sun.plugin2.ipc.unix.UnixIPCFactory";
          break;
        default:
          throw new RuntimeException("Unknown OS type from SystemUtil.getOSType()");
        }
        instance = (IPCFactory)Class.forName(str).newInstance();
      }
      catch (Exception localException)
      {
        if ((localException instanceof RuntimeException))
          throw ((RuntimeException)localException);
        throw new RuntimeException(localException);
      }
    return instance;
  }

  public abstract Event createEvent(Map paramMap);

  public abstract NamedPipe createNamedPipe(Map paramMap);

  public static String mapToString(Map paramMap)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    int i = 1;
    Iterator localIterator = paramMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      if (i != 0)
        i = 0;
      else
        localStringBuffer.append(",");
      localStringBuffer.append(str);
      localStringBuffer.append("=");
      localStringBuffer.append((String)paramMap.get(str));
    }
    return localStringBuffer.toString();
  }

  public static Map stringToMap(String paramString)
  {
    HashMap localHashMap = new HashMap();
    String[] arrayOfString = paramString.split(",");
    for (int i = 0; i < arrayOfString.length; i++)
    {
      String str = arrayOfString[i];
      int j = str.indexOf("=");
      localHashMap.put(str.substring(0, j), str.substring(j + 1, str.length()));
    }
    return localHashMap;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.ipc.IPCFactory
 * JD-Core Version:    0.6.2
 */