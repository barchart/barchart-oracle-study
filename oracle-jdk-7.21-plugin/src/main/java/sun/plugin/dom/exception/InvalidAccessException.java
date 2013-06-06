package sun.plugin.dom.exception;

import org.w3c.dom.DOMException;

public class InvalidAccessException extends DOMException
{
  public InvalidAccessException(String paramString)
  {
    super((short)15, paramString);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.exception.InvalidAccessException
 * JD-Core Version:    0.6.2
 */