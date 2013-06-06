package sun.plugin.javascript.navig4;

import java.util.HashMap;
import netscape.javascript.JSException;

public class Document extends sun.plugin.javascript.navig.Document
{
  private static HashMap methodTable = new HashMap();
  private static HashMap fieldTable = new HashMap();

  protected Document(int paramInt, String paramString)
  {
    super(paramInt, paramString);
    addObjectTable(fieldTable, methodTable);
  }

  public Object getMember(String paramString)
    throws JSException
  {
    if (paramString.equals("layers"))
      return resolveObject("[object LayerArray]", this.context + ".layers");
    return super.getMember(paramString);
  }

  static
  {
    methodTable.put("getSelection", Boolean.TRUE);
    fieldTable.put("layers", Boolean.FALSE);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.javascript.navig4.Document
 * JD-Core Version:    0.6.2
 */