package javax.sql.rowset.spi;

import java.io.Reader;
import java.sql.SQLException;
import javax.sql.RowSetReader;
import javax.sql.rowset.WebRowSet;

public abstract interface XmlReader extends RowSetReader
{
  public abstract void readXML(WebRowSet paramWebRowSet, Reader paramReader)
    throws SQLException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.sql.rowset.spi.XmlReader
 * JD-Core Version:    0.6.2
 */