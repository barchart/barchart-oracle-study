package sun.plugin2.main.server;

import sun.plugin2.ipc.Event;
import sun.plugin2.ipc.windows.WindowsEvent;

public class WindowsHelper
{
  private static final ThreadLocal installedHooks = new ThreadLocal();

  public static void runMessagePump(Event paramEvent, long paramLong, boolean paramBoolean)
  {
    long l = ((WindowsEvent)paramEvent).getEventHandle();
    if (l != 0L)
      runMessagePump0(l, paramLong, paramBoolean);
  }

  private static native void runMessagePump0(long paramLong1, long paramLong2, boolean paramBoolean);

  public static boolean registerModalDialogHooks(long paramLong, int paramInt)
  {
    if (installedHooks.get() != null)
      return false;
    long l1 = installModalFilterHook(paramLong, paramInt);
    long l2 = installMouseHook(paramLong, paramInt);
    Hooks localHooks = new Hooks(l1, l2);
    installedHooks.set(localHooks);
    return true;
  }

  public static void unregisterModalDialogHooks(long paramLong)
  {
    Hooks localHooks = (Hooks)installedHooks.get();
    if (localHooks != null)
    {
      installedHooks.set(null);
      uninstallHook(localHooks.modalFilterHook, paramLong);
      uninstallHook(localHooks.mouseHook, paramLong);
    }
  }

  private static native long installModalFilterHook(long paramLong, int paramInt);

  private static native long installMouseHook(long paramLong, int paramInt);

  private static native long uninstallHook(long paramLong1, long paramLong2);

  public static void reactivateCurrentModalDialog()
  {
    Integer localInteger = ModalitySupport.getAppletBlockingBrowser();
    if (localInteger != null)
      JVMManager.getManager().sendWindowActivation(new AppletID(localInteger.intValue()), true);
  }

  static class Hooks
  {
    long modalFilterHook;
    long mouseHook;

    Hooks(long paramLong1, long paramLong2)
    {
      this.modalFilterHook = paramLong1;
      this.mouseHook = paramLong2;
    }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.main.server.WindowsHelper
 * JD-Core Version:    0.6.2
 */