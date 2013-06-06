package sun.plugin2.ipc.windows;

import java.util.Map;
import sun.plugin2.ipc.Event;
import sun.plugin2.ipc.IPCFactory;
import sun.plugin2.ipc.NamedPipe;
import sun.plugin2.os.windows.SECURITY_ATTRIBUTES;
import sun.plugin2.os.windows.Windows;

public class WindowsIPCFactory extends IPCFactory
{
  private static final String PIPE_NAME_PREFIX = "\\\\.\\pipe\\";
  private static final int TIMEOUT = 5000;
  private SECURITY_ATTRIBUTES securityAttributes = SECURITY_ATTRIBUTES.create();
  private static int currentHandleID;

  public WindowsIPCFactory()
  {
    this.securityAttributes.nLength(SECURITY_ATTRIBUTES.size());
    this.securityAttributes.bInheritHandle(1);
  }

  public Event createEvent(Map paramMap)
  {
    String str = null;
    long l = 0L;
    if ((paramMap == null) || (paramMap.get("evt_name") == null))
    {
      str = nextHandleName("evt");
      l = Windows.CreateEventA(this.securityAttributes, false, false, str);
      if (l == 0L)
        throw new RuntimeException("Error creating Event object");
    }
    else
    {
      str = (String)paramMap.get("evt_name");
      l = Windows.OpenEventA(2031619, true, str);
      if (l == 0L)
        throw new RuntimeException("Error opening Event object \"" + str + "\"");
    }
    return new WindowsEvent(l, str);
  }

  public NamedPipe createNamedPipe(Map paramMap)
  {
    String str1 = null;
    String str2 = null;
    long l1 = 0L;
    long l2 = 0L;
    boolean bool = false;
    if ((paramMap == null) || (paramMap.get("write_pipe_name") == null) || (paramMap.get("read_pipe_name") == null))
    {
      str1 = nextHandleName("pipe");
      l1 = Windows.CreateNamedPipeA("\\\\.\\pipe\\" + str1, 2, 0, 255, 4096, 4096, 5000, null);
      if (l1 == 0L)
        throw new RuntimeException("Error creating named pipe for writing");
      str2 = nextHandleName("pipe");
      l2 = Windows.CreateNamedPipeA("\\\\.\\pipe\\" + str2, 1, 0, 255, 4096, 4096, 5000, null);
      if (l2 == 0L)
        throw new RuntimeException("Error creating named pipe for reading");
      bool = true;
    }
    else
    {
      str1 = (String)paramMap.get("write_pipe_name");
      l1 = Windows.CreateFileA("\\\\.\\pipe\\" + str1, 1073741824, 0, null, 3, 128, 0L);
      if (l1 == 0L)
        throw new RuntimeException("Error opening named pipe \"\\\\.\\pipe\\" + str1 + "\"  for writing");
      str2 = (String)paramMap.get("read_pipe_name");
      l2 = Windows.CreateFileA("\\\\.\\pipe\\" + str2, -2147483648, 0, null, 3, 128, 0L);
      if (l2 == 0L)
        throw new RuntimeException("Error opening named pipe \"\\\\.\\pipe\\" + str2 + "\" for reading");
    }
    return new WindowsNamedPipe(l1, l2, str1, str2, bool);
  }

  private String nextHandleName(String paramString)
  {
    return "jpi2_pid" + Windows.GetCurrentProcessId() + "_" + paramString + nextHandleID();
  }

  private static synchronized int nextHandleID()
  {
    return ++currentHandleID;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.ipc.windows.WindowsIPCFactory
 * JD-Core Version:    0.6.2
 */