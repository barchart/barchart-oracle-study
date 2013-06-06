package sun.plugin2.util;

import com.sun.deploy.trace.Trace;
import java.awt.Color;
import java.util.StringTokenizer;

public class ColorUtil
{
  public static Color createColor(String paramString1, String paramString2)
  {
    if ((paramString2 != null) && (paramString2.indexOf(",") != -1))
    {
      StringTokenizer localStringTokenizer = new StringTokenizer(paramString2, ",");
      if (localStringTokenizer.countTokens() == 3)
      {
        int i = 0;
        int j = 0;
        int k = 0;
        int m = 0;
        while (localStringTokenizer.hasMoreTokens())
        {
          String str = (String)localStringTokenizer.nextElement();
          switch (i)
          {
          case 0:
            if (!str.trim().equals(""))
              j = new Integer(str.trim()).intValue();
            break;
          case 1:
            if (!str.trim().equals(""))
              k = new Integer(str.trim()).intValue();
            break;
          case 2:
            if (!str.trim().equals(""))
              m = new Integer(str.trim()).intValue();
            break;
          }
          i++;
        }
        return new Color(j, k, m);
      }
      Trace.msgPrintln("applet_viewer.color_tag", new Object[] { paramString1 });
      return null;
    }
    if (paramString2 != null)
      try
      {
        return Color.decode(paramString2);
      }
      catch (NumberFormatException localNumberFormatException)
      {
        if (paramString2.equalsIgnoreCase("red"))
          return Color.red;
        if (paramString2.equalsIgnoreCase("yellow"))
          return Color.yellow;
        if (paramString2.equalsIgnoreCase("black"))
          return Color.black;
        if (paramString2.equalsIgnoreCase("blue"))
          return Color.blue;
        if ((paramString2.equalsIgnoreCase("cyan")) || (paramString2.equalsIgnoreCase("aqua")))
          return Color.cyan;
        if (paramString2.equalsIgnoreCase("darkGray"))
          return Color.darkGray;
        if (paramString2.equalsIgnoreCase("gray"))
          return Color.gray;
        if ((paramString2.equalsIgnoreCase("lightGray")) || (paramString2.equalsIgnoreCase("silver")))
          return Color.lightGray;
        if ((paramString2.equalsIgnoreCase("green")) || (paramString2.equalsIgnoreCase("lime")))
          return Color.green;
        if ((paramString2.equalsIgnoreCase("magenta")) || (paramString2.equalsIgnoreCase("fuchsia")))
          return Color.magenta;
        if (paramString2.equalsIgnoreCase("orange"))
          return Color.orange;
        if (paramString2.equalsIgnoreCase("pink"))
          return Color.pink;
        if (paramString2.equalsIgnoreCase("white"))
          return Color.white;
        if (paramString2.equalsIgnoreCase("maroon"))
          return new Color(128, 0, 0);
        if (paramString2.equalsIgnoreCase("purple"))
          return new Color(128, 0, 128);
        if (paramString2.equalsIgnoreCase("navy"))
          return new Color(0, 0, 128);
        if (paramString2.equalsIgnoreCase("teal"))
          return new Color(0, 128, 128);
        if (paramString2.equalsIgnoreCase("olive"))
          return new Color(128, 128, 0);
      }
    return null;
  }

  public static ColorRGB createColorRGB(String paramString1, String paramString2)
  {
    Color localColor = createColor(paramString1, paramString2);
    if (localColor != null)
      return new ColorRGB(localColor.getRGB());
    return null;
  }

  public static class ColorRGB
  {
    public int rgb = 0;

    public ColorRGB(int paramInt)
    {
      this.rgb = paramInt;
    }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.util.ColorUtil
 * JD-Core Version:    0.6.2
 */