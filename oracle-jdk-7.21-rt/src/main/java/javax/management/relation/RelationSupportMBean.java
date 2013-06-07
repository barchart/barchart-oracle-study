package javax.management.relation;

public abstract interface RelationSupportMBean extends Relation
{
  public abstract Boolean isInRelationService();

  public abstract void setRelationServiceManagementFlag(Boolean paramBoolean)
    throws IllegalArgumentException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.management.relation.RelationSupportMBean
 * JD-Core Version:    0.6.2
 */