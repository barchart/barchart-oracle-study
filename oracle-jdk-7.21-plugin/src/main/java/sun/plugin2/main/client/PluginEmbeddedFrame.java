package sun.plugin2.main.client;

import com.sun.deploy.ui.DeployEmbeddedFrameIf;
import com.sun.deploy.uitoolkit.impl.awt.ui.AWTDialog;
import java.applet.Applet;
import java.awt.Component;
import java.awt.Dialog;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Stack;
import sun.awt.X11.XEmbeddedFrame;
import sun.plugin.util.UIUtil;
import sun.plugin2.message.EventMessage;
import sun.plugin2.message.Pipe;
import sun.print.PSPrinterJob.PluginPrinter;

public class PluginEmbeddedFrame extends XEmbeddedFrame
  implements DeployEmbeddedFrameIf
{
  private ModalityInterface modality;
  Stack dialogStack = new Stack();
  Component baseComponent = null;

  public PluginEmbeddedFrame(long paramLong, String paramString, boolean paramBoolean, ModalityInterface paramModalityInterface, Pipe paramPipe, int paramInt)
  {
    super(paramLong, paramBoolean);
    this.modality = paramModalityInterface;
  }

  public void addNotify()
  {
    UIUtil.disableBackgroundErase(this);
    super.addNotify();
  }

  public void notifyModalBlocked(Dialog paramDialog, boolean paramBoolean)
  {
    if (this.modality != null)
      if (paramBoolean)
        this.modality.modalityPushed(AWTDialog.getAWTDialog(paramDialog));
      else
        this.modality.modalityPopped(AWTDialog.getAWTDialog(paramDialog));
  }

  public byte[] printPlugin(Applet paramApplet, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    PrintStream localPrintStream = new PrintStream(localByteArrayOutputStream);
    if (paramApplet != null)
    {
      PSPrinterJob.PluginPrinter localPluginPrinter = new PSPrinterJob.PluginPrinter(paramApplet, localPrintStream, paramInt1, paramInt2, paramInt3, paramInt4);
      try
      {
        localPluginPrinter.printAll();
      }
      catch (Throwable localThrowable)
      {
        localThrowable.printStackTrace();
      }
    }
    return localByteArrayOutputStream.toByteArray();
  }

  public void synthesizeEvent(EventMessage paramEventMessage)
  {
  }

  protected void addImpl(Component paramComponent, Object paramObject, int paramInt)
  {
    synchronized (this.dialogStack)
    {
      this.baseComponent = paramComponent;
      if (this.dialogStack.empty())
        super.addImpl(paramComponent, paramObject, paramInt);
    }
  }

  public void removeAll()
  {
    synchronized (this.dialogStack)
    {
      this.baseComponent = null;
      if (this.dialogStack.empty())
        super.removeAll();
    }
  }

  public void remove(int paramInt)
  {
    if (paramInt == 0)
      removeAll();
  }

  public void push(Component paramComponent)
  {
    synchronized (this.dialogStack)
    {
      this.dialogStack.push(paramComponent);
      super.removeAll();
      super.addImpl(paramComponent, "Center", 0);
    }
  }

  public Component pop()
  {
    synchronized (this.dialogStack)
    {
      Component localComponent1 = this.dialogStack.empty() ? null : (Component)this.dialogStack.pop();
      Component localComponent2 = this.dialogStack.empty() ? null : (Component)this.dialogStack.peek();
      super.removeAll();
      if (localComponent2 != null)
        super.addImpl(localComponent2, "Center", 0);
      else if (this.baseComponent != null)
        super.addImpl(this.baseComponent, "Center", 0);
      return localComponent1;
    }
  }

  public int getLayerID()
  {
    return -1;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.main.client.PluginEmbeddedFrame
 * JD-Core Version:    0.6.2
 */