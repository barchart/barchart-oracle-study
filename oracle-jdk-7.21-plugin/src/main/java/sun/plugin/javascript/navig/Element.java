package sun.plugin.javascript.navig;

import java.util.HashMap;
import netscape.javascript.JSException;

class Element extends JSObject
{
  private static HashMap methodTable = new HashMap();
  private static HashMap fieldTable = new HashMap();
  private Form form;

  Element(int paramInt, String paramString, Form paramForm)
  {
    super(paramInt, paramString);
    this.form = paramForm;
    addObjectTable(fieldTable, methodTable);
  }

  public Object getMember(String paramString)
    throws JSException
  {
    if (paramString.equals("form"))
      return this.form;
    if (paramString.equals("options"))
      return resolveObject("[object OptionArray]", this.context + ".options");
    Object localObject = super.getMember(paramString);
    if ((localObject == null) && (paramString.equals("value")))
      localObject = "";
    return localObject;
  }

  static
  {
    methodTable.put("blur", Boolean.FALSE);
    methodTable.put("click", Boolean.FALSE);
    methodTable.put("focus", Boolean.FALSE);
    methodTable.put("select", Boolean.FALSE);
    fieldTable.put("checked", Boolean.TRUE);
    fieldTable.put("defaultChecked", Boolean.FALSE);
    fieldTable.put("defaultValue", Boolean.FALSE);
    fieldTable.put("form", Boolean.FALSE);
    fieldTable.put("length", Boolean.FALSE);
    fieldTable.put("name", Boolean.FALSE);
    fieldTable.put("options", Boolean.FALSE);
    fieldTable.put("selectedIndex", Boolean.FALSE);
    fieldTable.put("type", Boolean.FALSE);
    fieldTable.put("value", Boolean.TRUE);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.javascript.navig.Element
 * JD-Core Version:    0.6.2
 */