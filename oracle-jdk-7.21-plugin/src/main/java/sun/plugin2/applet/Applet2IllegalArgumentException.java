package sun.plugin2.applet;

public class Applet2IllegalArgumentException extends IllegalArgumentException
{
  private String key = null;
  private static Applet2MessageHandler amh = new Applet2MessageHandler("appletillegalargumentexception");

  public Applet2IllegalArgumentException(String paramString)
  {
    super(paramString);
    this.key = paramString;
  }

  public String getLocalizedMessage()
  {
    return amh.getMessage(this.key);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.applet.Applet2IllegalArgumentException
 * JD-Core Version:    0.6.2
 */