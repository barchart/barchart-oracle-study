package java.nio.file;

import java.io.IOException;

public abstract interface Watchable
{
  public abstract WatchKey register(WatchService paramWatchService, WatchEvent.Kind<?>[] paramArrayOfKind, WatchEvent.Modifier[] paramArrayOfModifier)
    throws IOException;

  public abstract WatchKey register(WatchService paramWatchService, WatchEvent.Kind<?>[] paramArrayOfKind)
    throws IOException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.nio.file.Watchable
 * JD-Core Version:    0.6.2
 */