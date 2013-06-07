package javax.xml.ws.handler.soap;

import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.ws.handler.Handler;

public abstract interface SOAPHandler<T extends SOAPMessageContext> extends Handler<T>
{
  public abstract Set<QName> getHeaders();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.xml.ws.handler.soap.SOAPHandler
 * JD-Core Version:    0.6.2
 */