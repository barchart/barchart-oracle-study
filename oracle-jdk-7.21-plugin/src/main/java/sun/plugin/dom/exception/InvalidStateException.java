package sun.plugin.dom.exception;

import org.w3c.dom.DOMException;

public class InvalidStateException extends DOMException
{
  public InvalidStateException(String paramString)
  {
    super((short)11, paramString);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.exception.InvalidStateException
 * JD-Core Version:    0.6.2
 */