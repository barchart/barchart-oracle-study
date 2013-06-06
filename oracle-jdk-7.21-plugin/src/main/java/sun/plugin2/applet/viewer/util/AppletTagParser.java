package sun.plugin2.applet.viewer.util;

import com.sun.applet2.AppletParameters;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLEditorKit.ParserCallback;

public class AppletTagParser extends HTMLEditorKit.ParserCallback
{
  private AppletParameters parameters = new AppletParameters();
  private boolean foundApplet;

  private static String toStringHelper(Object paramObject)
  {
    if (paramObject == null)
      return null;
    return paramObject.toString();
  }

  public void handleStartTag(HTML.Tag paramTag, MutableAttributeSet paramMutableAttributeSet, int paramInt)
  {
    if ((paramTag == HTML.Tag.APPLET) && (!this.foundApplet))
    {
      this.foundApplet = true;
      extractAttributes(paramMutableAttributeSet, this.parameters);
    }
  }

  public void handleSimpleTag(HTML.Tag paramTag, MutableAttributeSet paramMutableAttributeSet, int paramInt)
  {
    if (paramTag == HTML.Tag.PARAM)
    {
      HashMap localHashMap = new HashMap();
      extractAttributes(paramMutableAttributeSet, localHashMap);
      String str1 = (String)localHashMap.get("name");
      String str2 = (String)localHashMap.get("value");
      if ((str1 != null) && (str2 != null))
        this.parameters.put(str1, str2);
    }
  }

  private void extractAttributes(MutableAttributeSet paramMutableAttributeSet, Map paramMap)
  {
    Enumeration localEnumeration = paramMutableAttributeSet.getAttributeNames();
    while (localEnumeration.hasMoreElements())
    {
      Object localObject = localEnumeration.nextElement();
      String str1 = toStringHelper(localObject);
      String str2 = toStringHelper(paramMutableAttributeSet.getAttribute(localObject));
      if ((str1 != null) && (str2 != null))
        paramMap.put(str1, str2);
    }
  }

  public boolean foundApplet()
  {
    return this.foundApplet;
  }

  public AppletParameters getParameters()
  {
    return this.parameters;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.applet.viewer.util.AppletTagParser
 * JD-Core Version:    0.6.2
 */