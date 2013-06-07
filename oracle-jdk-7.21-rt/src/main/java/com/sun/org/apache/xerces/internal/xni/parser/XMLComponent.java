package com.sun.org.apache.xerces.internal.xni.parser;

public abstract interface XMLComponent
{
  public abstract void reset(XMLComponentManager paramXMLComponentManager)
    throws XMLConfigurationException;

  public abstract String[] getRecognizedFeatures();

  public abstract void setFeature(String paramString, boolean paramBoolean)
    throws XMLConfigurationException;

  public abstract String[] getRecognizedProperties();

  public abstract void setProperty(String paramString, Object paramObject)
    throws XMLConfigurationException;

  public abstract Boolean getFeatureDefault(String paramString);

  public abstract Object getPropertyDefault(String paramString);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.xni.parser.XMLComponent
 * JD-Core Version:    0.6.2
 */