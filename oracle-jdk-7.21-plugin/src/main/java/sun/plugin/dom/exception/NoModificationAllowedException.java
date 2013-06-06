package sun.plugin.dom.exception;

import org.w3c.dom.DOMException;

public class NoModificationAllowedException extends DOMException
{
  public NoModificationAllowedException(String paramString)
  {
    super((short)7, paramString);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.exception.NoModificationAllowedException
 * JD-Core Version:    0.6.2
 */