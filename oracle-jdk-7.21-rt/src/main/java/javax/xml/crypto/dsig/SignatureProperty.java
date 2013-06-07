package javax.xml.crypto.dsig;

import java.util.List;
import javax.xml.crypto.XMLStructure;

public abstract interface SignatureProperty extends XMLStructure
{
  public abstract String getTarget();

  public abstract String getId();

  public abstract List getContent();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.xml.crypto.dsig.SignatureProperty
 * JD-Core Version:    0.6.2
 */