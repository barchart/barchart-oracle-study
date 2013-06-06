package sun.plugin.viewer.frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import sun.awt.X11.XEmbeddedFrame;

public class XNetscapeEmbeddedFrame extends XEmbeddedFrame
  implements WindowListener
{
  public void windowActivated(WindowEvent paramWindowEvent)
  {
  }

  public void windowClosed(WindowEvent paramWindowEvent)
  {
  }

  public void windowClosing(WindowEvent paramWindowEvent)
  {
    removeWindowListener(this);
    removeAll();
    dispose();
  }

  public void windowDeactivated(WindowEvent paramWindowEvent)
  {
  }

  public void windowDeiconified(WindowEvent paramWindowEvent)
  {
  }

  public void windowIconified(WindowEvent paramWindowEvent)
  {
  }

  public void windowOpened(WindowEvent paramWindowEvent)
  {
  }

  public XNetscapeEmbeddedFrame()
  {
    init();
  }

  public XNetscapeEmbeddedFrame(int paramInt)
  {
    super(paramInt);
    init();
  }

  public XNetscapeEmbeddedFrame(long paramLong, boolean paramBoolean)
  {
    super(paramLong, paramBoolean);
    init();
  }

  private void init()
  {
    setLayout(new BorderLayout());
    setBackground(Color.white);
    addWindowListener(this);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.viewer.frame.XNetscapeEmbeddedFrame
 * JD-Core Version:    0.6.2
 */