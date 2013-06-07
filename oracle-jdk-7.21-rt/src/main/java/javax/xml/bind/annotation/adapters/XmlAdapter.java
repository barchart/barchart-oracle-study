package javax.xml.bind.annotation.adapters;

public abstract class XmlAdapter<ValueType, BoundType>
{
  public abstract BoundType unmarshal(ValueType paramValueType)
    throws Exception;

  public abstract ValueType marshal(BoundType paramBoundType)
    throws Exception;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.xml.bind.annotation.adapters.XmlAdapter
 * JD-Core Version:    0.6.2
 */