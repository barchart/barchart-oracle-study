package javax.xml.bind;

import java.io.IOException;
import javax.xml.transform.Result;

public abstract class SchemaOutputResolver
{
  public abstract Result createOutput(String paramString1, String paramString2)
    throws IOException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.xml.bind.SchemaOutputResolver
 * JD-Core Version:    0.6.2
 */