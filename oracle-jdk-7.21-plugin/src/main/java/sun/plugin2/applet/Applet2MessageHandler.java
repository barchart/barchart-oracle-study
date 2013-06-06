package sun.plugin2.applet;

import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Applet2MessageHandler
{
  private static ResourceBundle rb;
  private String baseKey = null;

  Applet2MessageHandler(String paramString)
  {
    this.baseKey = paramString;
  }

  String getMessage(String paramString)
  {
    return rb.getString(getQualifiedKey(paramString));
  }

  String getMessage(String paramString, Object paramObject)
  {
    String str = rb.getString(getQualifiedKey(paramString));
    MessageFormat localMessageFormat = new MessageFormat(str);
    Object[] arrayOfObject = new Object[1];
    if (paramObject == null)
      paramObject = "null";
    arrayOfObject[0] = paramObject;
    return localMessageFormat.format(arrayOfObject);
  }

  String getMessage(String paramString, Object paramObject1, Object paramObject2)
  {
    String str = rb.getString(getQualifiedKey(paramString));
    MessageFormat localMessageFormat = new MessageFormat(str);
    Object[] arrayOfObject = new Object[2];
    if (paramObject1 == null)
      paramObject1 = "null";
    if (paramObject2 == null)
      paramObject2 = "null";
    arrayOfObject[0] = paramObject1;
    arrayOfObject[1] = paramObject2;
    return localMessageFormat.format(arrayOfObject);
  }

  String getMessage(String paramString, Object paramObject1, Object paramObject2, Object paramObject3)
  {
    String str = rb.getString(getQualifiedKey(paramString));
    MessageFormat localMessageFormat = new MessageFormat(str);
    Object[] arrayOfObject = new Object[3];
    if (paramObject1 == null)
      paramObject1 = "null";
    if (paramObject2 == null)
      paramObject2 = "null";
    if (paramObject3 == null)
      paramObject3 = "null";
    arrayOfObject[0] = paramObject1;
    arrayOfObject[1] = paramObject2;
    arrayOfObject[2] = paramObject3;
    return localMessageFormat.format(arrayOfObject);
  }

  String getMessage(String paramString, Object[] paramArrayOfObject)
  {
    String str = rb.getString(getQualifiedKey(paramString));
    MessageFormat localMessageFormat = new MessageFormat(str);
    return localMessageFormat.format(paramArrayOfObject);
  }

  String getQualifiedKey(String paramString)
  {
    return this.baseKey + "." + paramString;
  }

  static
  {
    try
    {
      rb = ResourceBundle.getBundle("sun.applet.resources.MsgAppletViewer");
    }
    catch (MissingResourceException localMissingResourceException)
    {
      System.out.println(localMissingResourceException.getMessage());
      System.exit(1);
    }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.applet.Applet2MessageHandler
 * JD-Core Version:    0.6.2
 */