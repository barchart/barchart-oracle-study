package sun.plugin.dom;

import org.w3c.dom.DOMException;

public class DOMObjectHelper
{
  public static boolean getBooleanMember(DOMObject paramDOMObject, String paramString)
    throws DOMException
  {
    Object localObject = paramDOMObject.getMember(paramString);
    if (localObject != null)
      return new Boolean(localObject.toString()).booleanValue();
    return false;
  }

  public static boolean getBooleanMemberNoEx(DOMObject paramDOMObject, String paramString)
  {
    try
    {
      return getBooleanMember(paramDOMObject, paramString);
    }
    catch (DOMException localDOMException)
    {
    }
    return false;
  }

  public static void setBooleanMember(DOMObject paramDOMObject, String paramString, boolean paramBoolean)
    throws DOMException
  {
    paramDOMObject.setMember(paramString, paramBoolean + "");
  }

  public static void setBooleanMemberNoEx(DOMObject paramDOMObject, String paramString, boolean paramBoolean)
  {
    try
    {
      setBooleanMember(paramDOMObject, paramString, paramBoolean);
    }
    catch (DOMException localDOMException)
    {
    }
  }

  public static int getIntMember(DOMObject paramDOMObject, String paramString)
    throws DOMException
  {
    Object localObject = paramDOMObject.getMember(paramString);
    if (localObject != null)
      return new Float(localObject.toString()).intValue();
    return 0;
  }

  public static int getIntMemberNoEx(DOMObject paramDOMObject, String paramString)
  {
    try
    {
      return getIntMember(paramDOMObject, paramString);
    }
    catch (DOMException localDOMException)
    {
    }
    return 0;
  }

  public static void setIntMember(DOMObject paramDOMObject, String paramString, int paramInt)
    throws DOMException
  {
    paramDOMObject.setMember(paramString, paramInt + "");
  }

  public static void setIntMemberNoEx(DOMObject paramDOMObject, String paramString, int paramInt)
  {
    try
    {
      setIntMember(paramDOMObject, paramString, paramInt);
    }
    catch (DOMException localDOMException)
    {
    }
  }

  public static String getStringMember(DOMObject paramDOMObject, String paramString)
    throws DOMException
  {
    Object localObject = paramDOMObject.getMember(paramString);
    if (localObject != null)
      return localObject.toString();
    return null;
  }

  public static String getStringMemberNoEx(DOMObject paramDOMObject, String paramString)
  {
    try
    {
      return getStringMember(paramDOMObject, paramString);
    }
    catch (DOMException localDOMException)
    {
    }
    return null;
  }

  public static void setStringMember(DOMObject paramDOMObject, String paramString1, String paramString2)
    throws DOMException
  {
    paramDOMObject.setMember(paramString1, paramString2);
  }

  public static void setStringMemberNoEx(DOMObject paramDOMObject, String paramString1, String paramString2)
  {
    try
    {
      setStringMember(paramDOMObject, paramString1, paramString2);
    }
    catch (DOMException localDOMException)
    {
    }
  }

  public static String callStringMethod(DOMObject paramDOMObject, String paramString, Object[] paramArrayOfObject)
  {
    Object localObject = paramDOMObject.call(paramString, paramArrayOfObject);
    if (localObject != null)
      return localObject.toString();
    return null;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.DOMObjectHelper
 * JD-Core Version:    0.6.2
 */