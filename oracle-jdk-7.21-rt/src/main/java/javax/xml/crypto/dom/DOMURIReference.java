package javax.xml.crypto.dom;

import javax.xml.crypto.URIReference;
import org.w3c.dom.Node;

public abstract interface DOMURIReference extends URIReference
{
  public abstract Node getHere();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.xml.crypto.dom.DOMURIReference
 * JD-Core Version:    0.6.2
 */