package sun.plugin2.ipc.windows;

import java.util.HashMap;
import java.util.Map;
import sun.plugin2.ipc.Event;
import sun.plugin2.os.windows.Windows;

public class WindowsEvent extends Event
{
  private long handle;
  private String name;

  public WindowsEvent(long paramLong, String paramString)
  {
    this.handle = paramLong;
    this.name = paramString;
  }

  public long getEventHandle()
  {
    return this.handle;
  }

  public void waitForSignal(long paramLong)
  {
    Windows.WaitForSingleObject(this.handle, (int)(paramLong == 0L ? -1L : paramLong));
  }

  public void signal()
  {
    if (this.handle != 0L)
      Windows.SetEvent(this.handle);
  }

  public Map getChildProcessParameters()
  {
    HashMap localHashMap = new HashMap();
    localHashMap.put("evt_name", this.name);
    return localHashMap;
  }

  public void dispose()
  {
    if (this.handle != 0L)
    {
      Windows.CloseHandle(this.handle);
      this.handle = 0L;
    }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.ipc.windows.WindowsEvent
 * JD-Core Version:    0.6.2
 */