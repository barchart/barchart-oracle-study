package sun.awt;

import java.awt.AWTException;
import java.awt.Window;
import java.awt.im.spi.InputMethodDescriptor;
import java.util.Locale;
import sun.awt.im.InputContext;

public abstract interface InputMethodSupport
{
  public abstract InputMethodDescriptor getInputMethodAdapterDescriptor()
    throws AWTException;

  public abstract Window createInputMethodWindow(String paramString, InputContext paramInputContext);

  public abstract boolean enableInputMethodsForTextComponent();

  public abstract Locale getDefaultKeyboardLocale();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.InputMethodSupport
 * JD-Core Version:    0.6.2
 */