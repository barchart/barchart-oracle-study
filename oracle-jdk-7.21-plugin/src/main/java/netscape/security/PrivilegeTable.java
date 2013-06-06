package netscape.security;

import java.util.Enumeration;
import java.util.Hashtable;

public class PrivilegeTable
{
  Hashtable table = new Hashtable();

  public int size()
  {
    return this.table.size();
  }

  public boolean isEmpty()
  {
    return this.table.isEmpty();
  }

  public Enumeration keys()
  {
    return this.table.keys();
  }

  public Enumeration elements()
  {
    return this.table.elements();
  }

  public Privilege get(Object paramObject)
  {
    return (Privilege)this.table.get(paramObject);
  }

  public Privilege get(Target paramTarget)
  {
    return get(paramTarget);
  }

  public Privilege put(Object paramObject, Privilege paramPrivilege)
  {
    return (Privilege)this.table.put(paramObject, paramPrivilege);
  }

  public Privilege put(Target paramTarget, Privilege paramPrivilege)
  {
    return (Privilege)this.table.put(paramTarget, paramPrivilege);
  }

  public Privilege remove(Object paramObject)
  {
    return (Privilege)this.table.remove(paramObject);
  }

  public Privilege remove(Target paramTarget)
  {
    return (Privilege)this.table.remove(paramTarget);
  }

  public void clear()
  {
    this.table.clear();
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     netscape.security.PrivilegeTable
 * JD-Core Version:    0.6.2
 */