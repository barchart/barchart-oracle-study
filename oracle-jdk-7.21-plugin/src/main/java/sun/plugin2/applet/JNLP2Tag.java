package sun.plugin2.applet;

import com.sun.applet2.AppletParameters;
import com.sun.javaws.jnl.AppletDesc;
import com.sun.javaws.jnl.JavaFXAppDesc;
import java.util.Enumeration;
import java.util.Properties;
import sun.plugin2.util.ParameterNames;

public final class JNLP2Tag
{
  public static String JNLP_HREF = "jnlp_href";

  private static AppletParameters toStringMap(AppletParameters paramAppletParameters, Properties paramProperties, boolean paramBoolean1, boolean paramBoolean2)
  {
    Enumeration localEnumeration = paramProperties.propertyNames();
    while (localEnumeration.hasMoreElements())
    {
      String str1 = (String)localEnumeration.nextElement();
      String str2 = paramProperties.getProperty(str1);
      if ((str1 != null) && (str2 != null))
      {
        if (paramBoolean2)
          str1 = str1.toLowerCase();
        if ((paramBoolean1) || (paramAppletParameters.get(str1) == null))
          paramAppletParameters.put(str1, str2);
      }
    }
    return paramAppletParameters;
  }

  public static AppletParameters addJNLParams2Map(AppletParameters paramAppletParameters, AppletDesc paramAppletDesc)
  {
    if (paramAppletParameters == null)
      paramAppletParameters = new AppletParameters();
    paramAppletParameters = toStringMap(paramAppletParameters, paramAppletDesc.getParameters(), false, true);
    if (paramAppletParameters.get("code") == null)
      paramAppletParameters.put("code", paramAppletDesc.getAppletClass());
    if (paramAppletParameters.get("width") == null)
      paramAppletParameters.put("width", String.valueOf(paramAppletDesc.getWidth()));
    if (paramAppletParameters.get("height") == null)
      paramAppletParameters.put("height", String.valueOf(paramAppletDesc.getHeight()));
    return paramAppletParameters;
  }

  public static AppletParameters addJnlpJfxParams(AppletParameters paramAppletParameters, JavaFXAppDesc paramJavaFXAppDesc)
  {
    if (paramAppletParameters == null)
      paramAppletParameters = new AppletParameters();
    if (paramJavaFXAppDesc.getParameters() != null)
      paramAppletParameters = toStringMap(paramAppletParameters, paramJavaFXAppDesc.getParameters(), false, false);
    String[] arrayOfString = paramJavaFXAppDesc.getArguments();
    if ((arrayOfString != null) && (arrayOfString.length > 0))
      paramAppletParameters.put(ParameterNames.ARGUMENTS, arrayOfString);
    if (paramAppletParameters.get("code") == null)
      paramAppletParameters.put("code", paramJavaFXAppDesc.getMainClass());
    return paramAppletParameters;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.applet.JNLP2Tag
 * JD-Core Version:    0.6.2
 */