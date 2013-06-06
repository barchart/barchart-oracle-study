package com.sun.deploy.panel;

import com.sun.deploy.config.Config;

public class TextFieldProperty extends BasicProperty
{
  public TextFieldProperty(String paramString1, String paramString2)
  {
    super(paramString1, paramString2);
    String str = Config.getStringProperty(paramString1);
    if (str != null)
      setValue(str);
  }

  public boolean isSelected()
  {
    return false;
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.panel.TextFieldProperty
 * JD-Core Version:    0.6.2
 */