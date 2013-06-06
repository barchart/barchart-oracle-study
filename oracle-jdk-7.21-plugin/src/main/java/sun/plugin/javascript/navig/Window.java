package sun.plugin.javascript.navig;

import java.util.HashMap;
import netscape.javascript.JSException;

public class Window extends JSObject
{
  private static HashMap methodTable = new HashMap();
  private static HashMap fieldTable = new HashMap();
  private static long varCount = 0L;

  public Window(int paramInt, String paramString)
  {
    super(paramInt, paramString);
    addObjectTable(fieldTable, methodTable);
  }

  public Window(int paramInt)
  {
    this(paramInt, "self");
  }

  public Object call(String paramString, Object[] paramArrayOfObject)
    throws JSException
  {
    Object localObject1;
    if (paramString.equals("open"))
    {
      Object[] arrayOfObject;
      if ((paramArrayOfObject == null) || (paramArrayOfObject.length == 0))
        arrayOfObject = new Object[] { "", generateVarName("__pluginwin") };
      else if (paramArrayOfObject.length == 1)
        arrayOfObject = new Object[] { paramArrayOfObject[0], generateVarName("__pluginwin") };
      else
        arrayOfObject = paramArrayOfObject;
      localObject1 = super.call(paramString, arrayOfObject);
      if (localObject1 == null)
        throw new JSException("call does not support " + toString() + "." + paramString);
      String str1 = this.context + ".open(";
      for (int j = 0; j < arrayOfObject.length; j++)
      {
        if ((arrayOfObject[j] instanceof String))
          str1 = str1 + "'" + arrayOfObject[j].toString() + "'";
        else
          str1 = str1 + arrayOfObject[j].toString();
        if (j != arrayOfObject.length - 1)
          str1 = str1 + ", ";
      }
      str1 = str1 + ")";
      return resolveObject("[object Window]", str1);
    }
    try
    {
      return super.call(paramString, paramArrayOfObject);
    }
    catch (JSException localJSException)
    {
      if (eval(this.context + "." + paramString) == null)
        throw new JSException("call does not support " + toString() + "." + paramString);
      localObject1 = this.context + "." + paramString + "(";
      if (paramArrayOfObject != null)
        for (int i = 0; i < paramArrayOfObject.length; i++)
        {
          if ((paramArrayOfObject[i] instanceof String))
            localObject1 = (String)localObject1 + "'" + paramArrayOfObject[i].toString() + "'";
          else
            localObject1 = (String)localObject1 + paramArrayOfObject[i].toString();
          if (i != paramArrayOfObject.length - 1)
            localObject1 = (String)localObject1 + ", ";
        }
      localObject1 = (String)localObject1 + ")";
      String str2 = generateVarName("__pluginVar");
      Object localObject2 = eval(str2 + "=" + (String)localObject1);
      if (localObject2 != null)
        localObject2 = resolveObject(localObject2.toString().trim(), this.context + "." + str2);
      return localObject2;
    }
  }

  public Object getMember(String paramString)
    throws JSException
  {
    if (paramString.equals("document"))
      return resolveObject("[object Document]", this.context + ".document");
    if (paramString.equals("history"))
      return resolveObject("[object History]", this.context + ".history");
    if (paramString.equals("location"))
      return resolveObject("[object Location]", this.context + ".location");
    if (paramString.equals("frames"))
      return resolveObject("[object FrameArray]", this.context + ".frames");
    if (paramString.equals("navigator"))
      return resolveObject("[object Navigator]", "navigator");
    if (paramString.equals("self"))
      return this;
    if (paramString.equals("window"))
      return this;
    if (paramString.equals("parent"))
      return resolveObject("[object Window]", this.context + ".parent");
    if (paramString.equals("top"))
      return resolveObject("[object Window]", this.context + ".top");
    if (paramString.equals("opener"))
      return resolveObject("[object Window]", this.context + ".opener");
    try
    {
      return super.getMember(paramString);
    }
    catch (JSException localJSException)
    {
      String str = generateVarName("__pluginVar");
      Object localObject = eval(str + "=" + this.context + "." + paramString);
      if (localObject != null)
        localObject = resolveObject(localObject.toString().trim(), this.context + "." + str);
      return localObject;
    }
  }

  public void setMember(String paramString, Object paramObject)
    throws JSException
  {
    try
    {
      super.setMember(paramString, paramObject);
    }
    catch (JSException localJSException)
    {
      if ((paramObject instanceof String))
        eval(this.context + "." + paramString + "='" + paramObject.toString() + "'");
      else
        eval(this.context + "." + paramString + "=" + paramObject.toString());
    }
  }

  protected static String generateVarName(String paramString)
  {
    if (paramString != null)
      return paramString + varCount++;
    return "__pluginTemp" + varCount++;
  }

  static
  {
    methodTable.put("alert", Boolean.FALSE);
    methodTable.put("blur", Boolean.FALSE);
    methodTable.put("clearTimeout", Boolean.FALSE);
    methodTable.put("close", Boolean.FALSE);
    methodTable.put("confirm", Boolean.TRUE);
    methodTable.put("focus", Boolean.FALSE);
    methodTable.put("open", Boolean.TRUE);
    methodTable.put("prompt", Boolean.TRUE);
    methodTable.put("scroll", Boolean.FALSE);
    methodTable.put("setTimeout", Boolean.TRUE);
    fieldTable.put("closed", Boolean.FALSE);
    fieldTable.put("defaultStatus", Boolean.FALSE);
    fieldTable.put("frames", Boolean.FALSE);
    fieldTable.put("history", Boolean.FALSE);
    fieldTable.put("length", Boolean.FALSE);
    fieldTable.put("location", Boolean.FALSE);
    fieldTable.put("name", Boolean.TRUE);
    fieldTable.put("navigator", Boolean.FALSE);
    fieldTable.put("opener", Boolean.TRUE);
    fieldTable.put("parent", Boolean.TRUE);
    fieldTable.put("self", Boolean.FALSE);
    fieldTable.put("status", Boolean.TRUE);
    fieldTable.put("top", Boolean.TRUE);
    fieldTable.put("window", Boolean.FALSE);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.javascript.navig.Window
 * JD-Core Version:    0.6.2
 */