package sun.plugin2.applet;

import com.sun.deploy.uitoolkit.Applet2Adapter;

public class Applet2Status
{
  private Applet2Adapter adapter;
  private boolean errorOccurred;
  private String errorMessage;
  private Throwable errorException;

  public Applet2Status(Applet2Adapter paramApplet2Adapter, boolean paramBoolean, String paramString, Throwable paramThrowable)
  {
    this.adapter = paramApplet2Adapter;
    this.errorOccurred = paramBoolean;
    this.errorMessage = paramString;
    this.errorException = paramThrowable;
  }

  public Applet2Adapter getAdapter()
  {
    return this.adapter;
  }

  public boolean isInErrorState()
  {
    return this.errorOccurred;
  }

  public String getErrorMessage()
  {
    return this.errorMessage;
  }

  public Throwable getErrorException()
  {
    return this.errorException;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.applet.Applet2Status
 * JD-Core Version:    0.6.2
 */