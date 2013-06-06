package sun.plugin.javascript.navig;

import java.util.HashMap;
import netscape.javascript.JSException;

public class Document extends JSObject
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
    if (paramString.equals("links"))
      return resolveObject("[object LinkArray]", this.context + ".links");
    if (paramString.equals("anchors"))
      return resolveObject("[object AnchorArray]", this.context + ".anchors");
    if (paramString.equals("forms"))
      return resolveObject("[object FormArray]", this.context + ".forms");
    if (paramString.equals("images"))
      return resolveObject("[object ImageArray]", this.context + ".images");
    if (paramString.equals("applets"))
      return resolveObject("[object AppletArray]", this.context + ".applets");
    if (paramString.equals("embeds"))
      return resolveObject("[object EmbedArray]", this.context + ".embeds");
    if (paramString.equals("location"))
      return resolveObject("[object Location]", this.context + ".location");
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
        if ((str2.equalsIgnoreCase("Form")) || (str2.equalsIgnoreCase("HTMLFormElement")))
          return resolveObject("[object Form]", this.context + "." + paramString);
        throw localJSException;
      }
      throw localJSException;
    }
  }

  static
  {
    methodTable.put("clear", Boolean.FALSE);
    methodTable.put("close", Boolean.FALSE);
    methodTable.put("open", Boolean.FALSE);
    methodTable.put("write", Boolean.FALSE);
    methodTable.put("writeln", Boolean.FALSE);
    fieldTable.put("alinkColor", Boolean.TRUE);
    fieldTable.put("anchors", Boolean.FALSE);
    fieldTable.put("applets", Boolean.FALSE);
    fieldTable.put("bgColor", Boolean.TRUE);
    fieldTable.put("cookie", Boolean.TRUE);
    fieldTable.put("domain", Boolean.TRUE);
    fieldTable.put("embeds", Boolean.FALSE);
    fieldTable.put("fgColor", Boolean.TRUE);
    fieldTable.put("forms", Boolean.FALSE);
    fieldTable.put("images", Boolean.FALSE);
    fieldTable.put("lastModified", Boolean.FALSE);
    fieldTable.put("linkColor", Boolean.TRUE);
    fieldTable.put("links", Boolean.FALSE);
    fieldTable.put("location", Boolean.TRUE);
    fieldTable.put("plugins", Boolean.FALSE);
    fieldTable.put("referrer", Boolean.FALSE);
    fieldTable.put("title", Boolean.FALSE);
    fieldTable.put("URL", Boolean.FALSE);
    fieldTable.put("vlinkColor", Boolean.TRUE);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.javascript.navig.Document
 * JD-Core Version:    0.6.2
 */