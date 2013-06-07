package javax.sql;

import java.util.EventListener;

public abstract interface StatementEventListener extends EventListener
{
  public abstract void statementClosed(StatementEvent paramStatementEvent);

  public abstract void statementErrorOccurred(StatementEvent paramStatementEvent);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.sql.StatementEventListener
 * JD-Core Version:    0.6.2
 */