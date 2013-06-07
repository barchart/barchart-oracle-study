package org.xml.sax;

import java.io.IOException;

public abstract interface EntityResolver
{
  public abstract InputSource resolveEntity(String paramString1, String paramString2)
    throws SAXException, IOException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.xml.sax.EntityResolver
 * JD-Core Version:    0.6.2
 */