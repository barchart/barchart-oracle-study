package javax.management.relation;

import java.io.Serializable;
import java.util.List;

public abstract interface RelationType extends Serializable
{
  public abstract String getRelationTypeName();

  public abstract List<RoleInfo> getRoleInfos();

  public abstract RoleInfo getRoleInfo(String paramString)
    throws IllegalArgumentException, RoleInfoNotFoundException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.management.relation.RelationType
 * JD-Core Version:    0.6.2
 */