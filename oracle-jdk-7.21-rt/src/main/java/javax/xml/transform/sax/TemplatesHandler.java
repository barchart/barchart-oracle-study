package javax.xml.transform.sax;

import javax.xml.transform.Templates;
import org.xml.sax.ContentHandler;

public abstract interface TemplatesHandler extends ContentHandler
{
  public abstract Templates getTemplates();

  public abstract void setSystemId(String paramString);

  public abstract String getSystemId();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.xml.transform.sax.TemplatesHandler
 * JD-Core Version:    0.6.2
 */