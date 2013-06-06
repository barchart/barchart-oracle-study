package sun.plugin.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

class DOMAccessor
  implements com.sun.java.browser.dom.DOMAccessor
{
  com.sun.java.browser.dom.DOMServiceProvider provider = new DOMServiceProvider();

  public Document getDocument(Object paramObject)
    throws DOMException
  {
    try
    {
      return this.provider.getDocument(paramObject);
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    return null;
  }

  public DOMImplementation getDOMImplementation()
  {
    return this.provider.getDOMImplementation();
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.DOMAccessor
 * JD-Core Version:    0.6.2
 */