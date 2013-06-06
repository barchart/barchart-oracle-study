package sun.plugin.dom;

import com.sun.java.browser.dom.DOMAccessException;
import com.sun.java.browser.dom.DOMAction;

public final class DOMService extends com.sun.java.browser.dom.DOMService
{
  public Object invokeAndWait(DOMAction paramDOMAction)
    throws DOMAccessException
  {
    return paramDOMAction.run(new DOMAccessor());
  }

  public void invokeLater(DOMAction paramDOMAction)
  {
    paramDOMAction.run(new DOMAccessor());
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.DOMService
 * JD-Core Version:    0.6.2
 */