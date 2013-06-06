package com.sun.deploy.uitoolkit.impl.text;

import com.sun.deploy.trace.Trace;
import com.sun.deploy.uitoolkit.PluginWindowFactory;
import com.sun.deploy.uitoolkit.WindowFactory;
import com.sun.deploy.uitoolkit.ui.AbstractDialog;
import com.sun.deploy.uitoolkit.ui.DelegatingPluginUIFactory;
import com.sun.deploy.uitoolkit.ui.ModalityHelper;
import sun.plugin2.applet.Plugin2Manager;
import sun.plugin2.main.client.ModalityInterface;

public class TextPluginUIFactory extends DelegatingPluginUIFactory
{
  PluginWindowFactory piWindowFactory = null;

  public TextPluginUIFactory()
  {
    super(new TextUIFactory());
  }

  public WindowFactory getWindowFactory()
  {
    if (this.piWindowFactory == null)
      this.piWindowFactory = new TextPluginWindowFactory();
    return this.piWindowFactory;
  }

  public ModalityHelper getModalityHelper()
  {
    return new TextModalityHelper();
  }

  class TextModalityHelper
    implements ModalityHelper
  {
    TextModalityHelper()
    {
    }

    public void reactivateDialog(AbstractDialog paramAbstractDialog)
    {
      Trace.println("TextModalityHelper.");
    }

    public boolean installModalityListener(ModalityInterface paramModalityInterface)
    {
      Trace.println("TextModalityHelper.");
      return false;
    }

    public void pushManagerShowingSystemDialog()
    {
      Trace.println("TextModalityHelper.");
    }

    public Plugin2Manager getManagerShowingSystemDialog()
    {
      Trace.println("TextModalityHelper.");
      return null;
    }

    public void popManagerShowingSystemDialog()
    {
      Trace.println("TextModalityHelper.");
    }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     com.sun.deploy.uitoolkit.impl.text.TextPluginUIFactory
 * JD-Core Version:    0.6.2
 */