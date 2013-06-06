package com.sun.deploy.resources;

import com.sun.deploy.trace.Trace;
import java.awt.Color;
import java.awt.Font;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class ResourceManager
{
  private static ResourceBundle rb;
  private static NumberFormat _numberFormat;
  static Class _keyEventClazz = null;

  static void reset()
  {
    rb = ResourceBundle.getBundle("com.sun.deploy.resources.Deployment");
    _numberFormat = NumberFormat.getInstance();
  }

  public static String getMessage(String paramString)
  {
    try
    {
      return removeMnemonics(rb.getString(paramString));
    }
    catch (MissingResourceException localMissingResourceException)
    {
    }
    return paramString;
  }

  public static String getFormattedMessage(String paramString, Object[] paramArrayOfObject)
  {
    try
    {
      MessageFormat localMessageFormat = new MessageFormat(getMessage(paramString));
      return localMessageFormat.format(paramArrayOfObject);
    }
    catch (MissingResourceException localMissingResourceException)
    {
    }
    return paramString;
  }

  public static String getString(String paramString)
  {
    return getMessage(paramString);
  }

  public static int getInteger(String paramString)
  {
    try
    {
      return Integer.parseInt(removeMnemonics(rb.getString(paramString)), 16);
    }
    catch (MissingResourceException localMissingResourceException)
    {
      Trace.ignoredException(localMissingResourceException);
    }
    return -1;
  }

  public static String getString(String paramString1, String paramString2)
  {
    Object[] arrayOfObject = { paramString2 };
    return applyPattern(paramString1, arrayOfObject);
  }

  public static String getString(String paramString1, String paramString2, String paramString3)
  {
    Object[] arrayOfObject = { paramString2, paramString3 };
    return applyPattern(paramString1, arrayOfObject);
  }

  public static String getString(String paramString, Long paramLong1, Long paramLong2)
  {
    Object[] arrayOfObject = { paramLong1, paramLong2 };
    return applyPattern(paramString, arrayOfObject);
  }

  public static String getString(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    Object[] arrayOfObject = { paramString2, paramString3, paramString4 };
    return applyPattern(paramString1, arrayOfObject);
  }

  public static String getString(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
  {
    Object[] arrayOfObject = { paramString2, paramString3, paramString4, paramString5 };
    return applyPattern(paramString1, arrayOfObject);
  }

  public static String getString(String paramString, int paramInt)
  {
    Object[] arrayOfObject = { new Integer(paramInt) };
    return applyPattern(paramString, arrayOfObject);
  }

  public static String getString(String paramString, int paramInt1, int paramInt2, int paramInt3)
  {
    Object[] arrayOfObject = { new Integer(paramInt1), new Integer(paramInt2), new Integer(paramInt3) };
    return applyPattern(paramString, arrayOfObject);
  }

  public static String getString(String paramString1, String paramString2, int paramInt, String paramString3)
  {
    Object[] arrayOfObject = { paramString2, new Integer(paramInt), paramString3 };
    return applyPattern(paramString1, arrayOfObject);
  }

  public static String getString(String paramString1, String paramString2, int paramInt)
  {
    Object[] arrayOfObject = { paramString2, new Integer(paramInt) };
    return applyPattern(paramString1, arrayOfObject);
  }

  public static synchronized String formatDouble(double paramDouble, int paramInt)
  {
    _numberFormat.setGroupingUsed(true);
    _numberFormat.setMaximumFractionDigits(paramInt);
    _numberFormat.setMinimumFractionDigits(paramInt);
    return _numberFormat.format(paramDouble);
  }

  public static ImageIcon getIcon(String paramString)
  {
    String str = getString(paramString);
    return new ImageIcon(ResourceManager.class.getResource(str));
  }

  public static ImageIcon[] getIcons(String paramString)
  {
    ImageIcon[] arrayOfImageIcon = new ImageIcon[4];
    String str1 = getString(paramString);
    arrayOfImageIcon[0] = new ImageIcon(ResourceManager.class.getResource(str1));
    int i = str1.lastIndexOf(".");
    String str2 = str1;
    String str3 = "";
    if (i > 0)
    {
      str2 = str1.substring(0, i);
      str3 = str1.substring(i);
    }
    arrayOfImageIcon[1] = new ImageIcon(ResourceManager.class.getResource(str2 + "-p" + str3));
    arrayOfImageIcon[2] = new ImageIcon(ResourceManager.class.getResource(str2 + "-d" + str3));
    arrayOfImageIcon[3] = new ImageIcon(ResourceManager.class.getResource(str2 + "-o" + str3));
    return arrayOfImageIcon;
  }

  private static String applyPattern(String paramString, Object[] paramArrayOfObject)
  {
    String str1 = getString(paramString);
    String str2 = MessageFormat.format(str1, paramArrayOfObject);
    return str2;
  }

  public static Color getColor(String paramString)
  {
    int i = getInteger(paramString);
    return new Color(i);
  }

  public static Font getUIFont()
  {
    return new JLabel().getFont();
  }

  public static int getMinFontSize()
  {
    int i = 0;
    try
    {
      i = ((Integer)rb.getObject("ui.min.font.size")).intValue();
    }
    catch (MissingResourceException localMissingResourceException)
    {
    }
    return i;
  }

  public static String removeMnemonics(String paramString)
  {
    int i = paramString.indexOf("&");
    int j = paramString.length();
    if ((i < 0) || (i == j - 1))
      return paramString;
    int k = paramString.indexOf("&", i + 1);
    if (k == i + 1)
    {
      if (k + 1 == j)
        return paramString.substring(0, i);
      return paramString.substring(0, i) + removeMnemonics(paramString.substring(k + 1));
    }
    if (i == 0)
      return removeMnemonics(paramString.substring(1));
    return paramString.substring(0, i) + removeMnemonics(paramString.substring(i + 1));
  }

  public static String extractMnemonic(String paramString)
  {
    if (paramString != null)
      try
      {
        int i = paramString.indexOf("&");
        int j = paramString.length();
        if ((i < 0) || (i == j - 1))
          return null;
        int k = paramString.indexOf("&", i + 1);
        if (k == i + 1)
        {
          if (k + 1 == j)
            return null;
          return extractMnemonic(paramString.substring(k + 1));
        }
        return paramString.substring(i + 1, i + 2);
      }
      catch (Exception localException)
      {
        Trace.ignored(localException);
      }
    return null;
  }

  public static int getMnemonic(String paramString)
  {
    String str1;
    try
    {
      str1 = rb.getString(paramString);
    }
    catch (MissingResourceException localMissingResourceException)
    {
      Trace.ignored(localMissingResourceException);
      return 0;
    }
    String str2 = extractMnemonic(str1);
    if ((str2 == null) || (str2.length() != 1))
      return 0;
    String str3 = "VK_" + str2.toUpperCase();
    try
    {
      if (_keyEventClazz == null)
        _keyEventClazz = Class.forName("java.awt.event.KeyEvent");
      Field localField = _keyEventClazz.getDeclaredField(str3);
      int i = localField.getInt(null);
      return i;
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      Trace.ignoredException(localClassNotFoundException);
    }
    catch (NoSuchFieldException localNoSuchFieldException)
    {
      Trace.ignoredException(localNoSuchFieldException);
    }
    catch (SecurityException localSecurityException)
    {
      Trace.ignoredException(localSecurityException);
    }
    catch (Exception localException)
    {
      Trace.ignoredException(localException);
    }
    return 0;
  }

  public static int getAcceleratorKey(String paramString)
  {
    return getMnemonic(paramString);
  }

  static
  {
    reset();
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.resources.ResourceManager
 * JD-Core Version:    0.6.2
 */