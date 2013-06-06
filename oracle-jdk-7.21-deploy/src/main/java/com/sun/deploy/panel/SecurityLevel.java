package com.sun.deploy.panel;

import com.sun.deploy.resources.ResourceManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SecurityLevel
{
  private static final List SLIDER_LIST = new ArrayList();
  private static final Map LEVEL_MAP = new HashMap();
  private final String configKey;
  private final String name;
  private final int sliderSetting;
  private final String description;
  public static final SecurityLevel Medium = createLevel("MEDIUM", 0);
  public static final SecurityLevel High = createLevel("HIGH", 1);
  public static final SecurityLevel VeryHigh = createLevel("VERY_HIGH", 2);

  private static SecurityLevel createLevel(String paramString, int paramInt)
  {
    SecurityLevel localSecurityLevel = new SecurityLevel(paramString, paramInt);
    SLIDER_LIST.add(paramInt, localSecurityLevel);
    LEVEL_MAP.put(paramString, localSecurityLevel);
    return localSecurityLevel;
  }

  private SecurityLevel(String paramString, int paramInt)
  {
    this.configKey = paramString;
    String str = "deployment.security.slider." + paramString;
    this.description = ResourceManager.getMessage(str + ".description");
    this.name = ResourceManager.getMessage(str);
    this.sliderSetting = paramInt;
  }

  public String getName()
  {
    return this.name;
  }

  public String getConfigKey()
  {
    return this.configKey;
  }

  public String getDescription()
  {
    return this.description;
  }

  public int getSliderSetting()
  {
    return this.sliderSetting;
  }

  public static SecurityLevel getSliderSetting(int paramInt)
  {
    return (SecurityLevel)SLIDER_LIST.get(paramInt);
  }

  public static SecurityLevel getLevel(String paramString)
  {
    if (LEVEL_MAP.get(paramString) != null)
      return (SecurityLevel)LEVEL_MAP.get(paramString);
    return High;
  }

  public static List values()
  {
    return Collections.unmodifiableList(SLIDER_LIST);
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.panel.SecurityLevel
 * JD-Core Version:    0.6.2
 */