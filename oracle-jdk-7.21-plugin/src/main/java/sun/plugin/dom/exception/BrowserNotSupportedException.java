package sun.plugin.dom.exception;

import org.w3c.dom.DOMException;

public class BrowserNotSupportedException extends DOMException
{
  public BrowserNotSupportedException(String paramString)
  {
    super((short)9, paramString);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.exception.BrowserNotSupportedException
 * JD-Core Version:    0.6.2
 */