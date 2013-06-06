package com.sun.deploy.uitoolkit.impl.awt;

import com.sun.deploy.uitoolkit.impl.awt.ui.UIFactoryImpl;
import com.sun.deploy.uitoolkit.ui.DelegatingPluginUIFactory;
import com.sun.deploy.uitoolkit.ui.ModalityHelper;

public class AWTPluginUIFactory extends DelegatingPluginUIFactory
{
  AWTPluginUIFactory()
  {
    super(new UIFactoryImpl());
  }

  public ModalityHelper getModalityHelper()
  {
    return new AWTModalityHelper();
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     com.sun.deploy.uitoolkit.impl.awt.AWTPluginUIFactory
 * JD-Core Version:    0.6.2
 */