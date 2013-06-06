package sun.plugin.javascript.navig;

import java.util.HashMap;
import netscape.javascript.JSException;

class Form extends JSObject
{
  private static HashMap methodTable = new HashMap();
  private static HashMap fieldTable = new HashMap();

  Form(int paramInt, String paramString)
  {
    super(paramInt, paramString);
    addObjectTable(fieldTable, methodTable);
  }

  public Object getMember(String paramString)
    throws JSException
  {
    if (paramString.equals("elements"))
      return resolveObject("[object ElementArray]", this.context + ".elements", this);
    try
    {
      return super.getMember(paramString);
    }
    catch (JSException localJSException)
    {
      String str1 = evalScript(this.instance, "javascript: typeof(" + this.context + "." + paramString + ")");
      if ((str1 != null) && (str1.equalsIgnoreCase("object")))
      {
        String str2 = evalScript(this.instance, "javascript:" + this.context + "." + paramString + ".constructor.name");
        if ((str2.equalsIgnoreCase("Input")) || (str2.equalsIgnoreCase("HTMLInputElement")))
          return resolveObject("[object Element]", this.context + "." + paramString);
        str2 = evalScript(this.instance, "javascript:" + this.context + "." + paramString + "[0].constructor.name");
        if ((str2.equalsIgnoreCase("Input")) || (str2.equalsIgnoreCase("HTMLInputElement")))
          return resolveObject("[object ElementArray]", this.context + "." + paramString);
        throw localJSException;
      }
      throw localJSException;
    }
  }

  static
  {
    methodTable.put("reset", Boolean.FALSE);
    methodTable.put("submit", Boolean.FALSE);
    fieldTable.put("action", Boolean.TRUE);
    fieldTable.put("elements", Boolean.FALSE);
    fieldTable.put("encoding", Boolean.TRUE);
    fieldTable.put("method", Boolean.TRUE);
    fieldTable.put("target", Boolean.TRUE);
    fieldTable.put("name", Boolean.TRUE);
    fieldTable.put("length", Boolean.FALSE);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.javascript.navig.Form
 * JD-Core Version:    0.6.2
 */