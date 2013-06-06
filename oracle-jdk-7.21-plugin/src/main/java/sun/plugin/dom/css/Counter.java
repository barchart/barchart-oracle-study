package sun.plugin.dom.css;

import sun.plugin.dom.DOMObject;

public final class Counter
  implements org.w3c.dom.css.Counter
{
  private DOMObject obj;

  public Counter(DOMObject paramDOMObject)
  {
    this.obj = paramDOMObject;
  }

  public String getIdentifier()
  {
    return (String)this.obj.getMember("identifier");
  }

  public String getListStyle()
  {
    return (String)this.obj.getMember("listStyle");
  }

  public String getSeparator()
  {
    return (String)this.obj.getMember("separator");
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.css.Counter
 * JD-Core Version:    0.6.2
 */