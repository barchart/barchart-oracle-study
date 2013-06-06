package sun.plugin2.liveconnect;

import netscape.javascript.JSException;

public class JSExceptions
{
  public static JSException noSuchMethod(String paramString)
  {
    return new JSException("No such method \"" + paramString + "\" on JavaScript object");
  }

  public static JSException noSuchProperty(String paramString)
  {
    return new JSException("No such property \"" + paramString + "\" on JavaScript object");
  }

  public static JSException noSuchSlot(int paramInt)
  {
    return new JSException("No such slot " + paramInt + " on JavaScript object");
  }

  public static JSException canNotRemoveMember(String paramString)
  {
    return new JSException("Member \"" + paramString + "\" does not exist, or exists and can not be removed");
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.liveconnect.JSExceptions
 * JD-Core Version:    0.6.2
 */