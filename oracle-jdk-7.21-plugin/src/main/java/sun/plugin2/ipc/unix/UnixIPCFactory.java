package sun.plugin2.ipc.unix;

import java.util.Map;
import sun.plugin2.ipc.Event;
import sun.plugin2.ipc.IPCFactory;
import sun.plugin2.ipc.NamedPipe;

public class UnixIPCFactory extends IPCFactory
{
  public NamedPipe createNamedPipe(Map paramMap)
  {
    String str;
    if ((paramMap == null) || (paramMap.get("write_pipe_name") == null))
      str = null;
    else
      str = (String)paramMap.get("write_pipe_name");
    return new DomainSocketNamedPipe(str);
  }

  public Event createEvent(Map paramMap)
  {
    throw new UnsupportedOperationException("not supported on Unix");
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.ipc.unix.UnixIPCFactory
 * JD-Core Version:    0.6.2
 */