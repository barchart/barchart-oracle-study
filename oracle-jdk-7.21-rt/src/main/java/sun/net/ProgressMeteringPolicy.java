package sun.net;

import java.net.URL;

public abstract interface ProgressMeteringPolicy
{
  public abstract boolean shouldMeterInput(URL paramURL, String paramString);

  public abstract int getProgressUpdateThreshold();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.net.ProgressMeteringPolicy
 * JD-Core Version:    0.6.2
 */