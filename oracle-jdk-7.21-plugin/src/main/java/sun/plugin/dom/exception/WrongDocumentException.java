package sun.plugin.dom.exception;

import org.w3c.dom.DOMException;

public class WrongDocumentException extends DOMException
{
  public WrongDocumentException(String paramString)
  {
    super((short)4, paramString);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.exception.WrongDocumentException
 * JD-Core Version:    0.6.2
 */