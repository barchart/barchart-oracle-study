package com.sun.javaws.jnl;

import com.sun.deploy.xml.XMLAttribute;
import com.sun.deploy.xml.XMLAttributeBuilder;
import com.sun.deploy.xml.XMLNode;
import com.sun.deploy.xml.XMLNodeBuilder;
import com.sun.deploy.xml.XMLable;
import com.sun.javaws.HtmlOptions;
import java.util.Enumeration;
import java.util.Properties;

public class JavaFXAppDesc
  implements XMLable
{
  private String _mainClass;
  private String _preloaderClass;
  private final Properties parameters;
  private final Properties jnlpParameters;
  private final String[] arguments;

  public JavaFXAppDesc(String paramString1, String paramString2, String[] paramArrayOfString, Properties paramProperties)
  {
    this._mainClass = paramString1;
    this._preloaderClass = paramString2;
    this.arguments = paramArrayOfString;
    this.jnlpParameters = paramProperties;
    this.parameters = new Properties();
    if (paramProperties != null)
      this.parameters.putAll(paramProperties);
    if (HtmlOptions.get() != null)
    {
      Properties localProperties = HtmlOptions.get().getParameters();
      if (localProperties != null)
        this.parameters.putAll(localProperties);
    }
  }

  public String getMainClass()
  {
    return this._mainClass;
  }

  public String getPreloaderClass()
  {
    return this._preloaderClass;
  }

  public Properties getParameters()
  {
    return this.parameters;
  }

  public String[] getArguments()
  {
    return this.arguments;
  }

  public XMLNode asXML()
  {
    XMLAttributeBuilder localXMLAttributeBuilder = new XMLAttributeBuilder();
    localXMLAttributeBuilder.add("main-class", this._mainClass);
    localXMLAttributeBuilder.add("preloader-class", this._preloaderClass);
    XMLNodeBuilder localXMLNodeBuilder = new XMLNodeBuilder("javafx-desc", localXMLAttributeBuilder.getAttributeList());
    if (this.parameters != null)
    {
      Enumeration localEnumeration = this.parameters.keys();
      while (localEnumeration.hasMoreElements())
      {
        String str1 = (String)localEnumeration.nextElement();
        String str2 = this.parameters.getProperty(str1);
        localXMLNodeBuilder.add(new XMLNode("param", new XMLAttribute("name", str1, new XMLAttribute("value", str2)), null, null));
      }
    }
    if (this.arguments != null)
      for (int i = 0; i < this.arguments.length; i++)
        localXMLNodeBuilder.add(new XMLNode("argument", null, new XMLNode(this.arguments[i]), null));
    return localXMLNodeBuilder.getNode();
  }
}

/* Location:           /home/user1/Temp/jvm/javaws.jar
 * Qualified Name:     com.sun.javaws.jnl.JavaFXAppDesc
 * JD-Core Version:    0.6.2
 */