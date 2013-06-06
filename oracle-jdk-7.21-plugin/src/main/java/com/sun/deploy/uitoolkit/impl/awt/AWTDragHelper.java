package com.sun.deploy.uitoolkit.impl.awt;

import com.sun.deploy.trace.Trace;
import com.sun.deploy.trace.TraceLevel;
import com.sun.deploy.uitoolkit.DragContext;
import com.sun.deploy.uitoolkit.DragHelper;
import com.sun.deploy.uitoolkit.DragListener;
import com.sun.deploy.uitoolkit.Window;
import java.applet.Applet;
import java.awt.AWTEvent;
import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JFrame;
import sun.awt.SunToolkit;
import sun.plugin2.util.SystemUtil;

public class AWTDragHelper
  implements DragHelper
{
  private static final boolean DEBUG = SystemUtil.getenv("JPI_PLUGIN2_DEBUG") != null;
  private static final boolean isOSX = System.getProperty("os.name").indexOf("OS X") != -1;
  private static AWTDragHelper soleInstance = new AWTDragHelper();
  private boolean initialized;
  private Method isSystemGeneratedMethod;
  private List perAppletInfoList = new ArrayList();
  private volatile boolean dragging;
  private Image javaLogoImage;

  public static AWTDragHelper getInstance()
  {
    return soleInstance;
  }

  public synchronized void register(DragContext paramDragContext, DragListener paramDragListener)
  {
    if (!initialize())
      return;
    this.perAppletInfoList.add(new PerAppletInfo(paramDragContext, paramDragListener));
  }

  public void makeDisconnected(DragContext paramDragContext, Window paramWindow)
  {
    Iterator localIterator = this.perAppletInfoList.iterator();
    while (localIterator.hasNext())
    {
      PerAppletInfo localPerAppletInfo = (PerAppletInfo)localIterator.next();
      if (localPerAppletInfo.getDragContext() == paramDragContext)
      {
        localPerAppletInfo.makeDisconnected((Frame)paramWindow.getWindowObject());
        return;
      }
    }
  }

  public synchronized void restore(DragContext paramDragContext)
  {
    Iterator localIterator = this.perAppletInfoList.iterator();
    while (localIterator.hasNext())
    {
      PerAppletInfo localPerAppletInfo = (PerAppletInfo)localIterator.next();
      if (localPerAppletInfo.getDragContext() == paramDragContext)
      {
        localPerAppletInfo.restore();
        return;
      }
    }
  }

  public synchronized void unregister(DragContext paramDragContext)
  {
    Iterator localIterator = this.perAppletInfoList.iterator();
    while (localIterator.hasNext())
    {
      PerAppletInfo localPerAppletInfo = (PerAppletInfo)localIterator.next();
      if (localPerAppletInfo.getDragContext() == paramDragContext)
      {
        assert (!localPerAppletInfo.iAmDragging());
        if (localPerAppletInfo.iAmDragging())
        {
          if (DEBUG)
            Trace.print("Dragging applet interrupted.", TraceLevel.UI);
          setSomeoneDragging(false);
        }
        localIterator.remove();
        return;
      }
    }
  }

  private boolean initialize()
  {
    if (!this.initialized)
    {
      this.initialized = true;
      AccessController.doPrivileged(new PrivilegedAction()
      {
        public Object run()
        {
          try
          {
            Class localClass = SunToolkit.class;
            AWTDragHelper.this.isSystemGeneratedMethod = localClass.getMethod("isSystemGenerated", new Class[] { AWTEvent.class });
            AWTDragHelper.this.isSystemGeneratedMethod.setAccessible(true);
          }
          catch (Exception localException)
          {
            if (!AWTDragHelper.isOSX)
              localException.printStackTrace();
          }
          Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener()
          {
            public void eventDispatched(AWTEvent paramAnonymous2AWTEvent)
            {
              AWTDragHelper.this.dispatchEvent(paramAnonymous2AWTEvent);
            }
          }
          , 48L);
          return null;
        }
      });
    }
    return (this.isSystemGeneratedMethod != null) || (isOSX);
  }

  private void dispatchEvent(AWTEvent paramAWTEvent)
  {
    if ((paramAWTEvent instanceof InputEvent))
    {
      InputEvent localInputEvent = (InputEvent)paramAWTEvent;
      if ((isSystemGenerated(paramAWTEvent)) && ((this.dragging) || (couldBeDragStartEvent(localInputEvent))))
        synchronized (this)
        {
          Iterator localIterator = this.perAppletInfoList.iterator();
          while (localIterator.hasNext())
          {
            PerAppletInfo localPerAppletInfo = (PerAppletInfo)localIterator.next();
            if (localPerAppletInfo.dispatchEvent(localInputEvent))
              return;
          }
        }
    }
  }

  private boolean isSystemGenerated(AWTEvent paramAWTEvent)
  {
    if (this.isSystemGeneratedMethod == null)
    {
      if (!isOSX)
        return false;
      return !((InputEvent)paramAWTEvent).isConsumed();
    }
    try
    {
      return ((Boolean)this.isSystemGeneratedMethod.invoke(null, new Object[] { paramAWTEvent })).booleanValue();
    }
    catch (Exception localException)
    {
    }
    return false;
  }

  private boolean isSomeoneDragging()
  {
    return this.dragging;
  }

  private void setSomeoneDragging(boolean paramBoolean)
  {
    this.dragging = paramBoolean;
  }

  private static boolean couldBeDragStartEvent(AWTEvent paramAWTEvent)
  {
    int i = paramAWTEvent.getID();
    return (i == 501) || (i == 506);
  }

  private static Point getCurrentMouseLocation()
  {
    return (Point)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        return MouseInfo.getPointerInfo().getLocation();
      }
    });
  }

  private static Dimension getDragSize(Component paramComponent)
  {
    Object localObject1 = paramComponent;
    Object localObject2 = null;
    while (localObject1 != null)
    {
      Dimension localDimension = ((Component)localObject1).getSize();
      if ((localObject2 == null) || (localDimension.width < localObject2.width) || (localDimension.height < localObject2.height))
        localObject2 = localDimension;
      localObject1 = ((Component)localObject1).getParent();
    }
    return localObject2;
  }

  private Image getJavaLogoImage(final Component paramComponent)
  {
    if (this.javaLogoImage == null)
      this.javaLogoImage = ((Image)AccessController.doPrivileged(new PrivilegedAction()
      {
        private final Component val$c;

        public Object run()
        {
          URL localURL = ClassLoader.getSystemResource("com/sun/deploy/uitoolkit/impl/awt/JavaCupLogo-161.png");
          Image localImage = Toolkit.getDefaultToolkit().getImage(localURL);
          MediaTracker localMediaTracker = new MediaTracker(paramComponent);
          localMediaTracker.addImage(localImage, 0);
          try
          {
            localMediaTracker.waitForID(0);
          }
          catch (InterruptedException localInterruptedException)
          {
          }
          return localImage;
        }
      }));
    return this.javaLogoImage;
  }

  private Canvas getJavaLogoCanvas(Dimension paramDimension)
  {
    Canvas local4 = new Canvas()
    {
      public void paint(Graphics paramAnonymousGraphics)
      {
        Image localImage = AWTDragHelper.this.getJavaLogoImage(this);
        if (localImage != null)
        {
          Rectangle localRectangle = AWTDragHelper.getCenteredImageBoundsWithinContainer(new Dimension(localImage.getWidth(this), localImage.getHeight(this)), getParent().getSize());
          Graphics2D localGraphics2D = (Graphics2D)paramAnonymousGraphics;
          localGraphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
          localGraphics2D.drawImage(localImage, localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height, this);
        }
      }
    };
    local4.setSize(paramDimension);
    return local4;
  }

  private static Rectangle getCenteredImageBoundsWithinContainer(Dimension paramDimension1, Dimension paramDimension2)
  {
    float f1 = paramDimension1.width / paramDimension1.height;
    float f2 = paramDimension2.width / paramDimension2.height;
    int i;
    if (f2 < f1)
    {
      if (paramDimension1.width > paramDimension2.width)
      {
        i = (int)(paramDimension2.width / f1);
        return new Rectangle(0, (paramDimension2.height - i) / 2, paramDimension2.width, i);
      }
    }
    else if (paramDimension1.height > paramDimension2.height)
    {
      i = (int)(paramDimension2.height * f1);
      return new Rectangle((paramDimension2.width - i) / 2, 0, i, paramDimension2.height);
    }
    return new Rectangle((paramDimension2.width - paramDimension1.width) / 2, (paramDimension2.height - paramDimension1.height) / 2, paramDimension1.width, paramDimension1.height);
  }

  private static boolean isSubclass(Class paramClass, String paramString)
  {
    if (paramClass == null)
      return false;
    if (paramClass.getName().equals(paramString))
      return true;
    return isSubclass(paramClass.getSuperclass(), paramString);
  }

  public class DraggedAppletFrame extends Frame
  {
    DraggedAppletFrame()
    {
    }
  }

  public class DraggedAppletJFrame extends JFrame
  {
    DraggedAppletJFrame()
    {
    }
  }

  private class PerAppletInfo
  {
    private DragContext ctx;
    private Frame frame;
    private Point dragOffset;
    private Point upperLeft;
    private DragListener listener;
    private boolean notificationsSent;
    private Frame closeButtonFrame;
    private final Point closeButtonOffset = new Point(5, -5);
    private ActionListener closeListener;
    private boolean closing;
    private Dimension dragSize;
    private static final int CLOSE_BUTTON_SIZE = 10;
    private static final int CLOSE_BUTTON_OFFSET = 5;
    private boolean initializedDragStartMethod;
    private Method dragStartMethod;

    PerAppletInfo(DragContext paramDragListener, DragListener arg3)
    {
      this.ctx = paramDragListener;
      Object localObject;
      this.listener = localObject;
    }

    public DragContext getDragContext()
    {
      return this.ctx;
    }

    public boolean dispatchEvent(AWTEvent paramAWTEvent)
    {
      MouseEvent localMouseEvent = (MouseEvent)paramAWTEvent;
      if ((AWTDragHelper.this.isSomeoneDragging()) && (!iAmDragging()))
        return false;
      Object localObject = localMouseEvent.getComponent();
      Applet localApplet = ((AWTAppletAdapter)this.ctx.getApplet2Adapter()).getApplet();
      if (localApplet == null)
        return false;
      if (this.ctx.getModalityLevel() != 0)
        return false;
      if (iAmDragging())
      {
        dispatchEventImpl(localApplet, localMouseEvent, this.ctx.isSignedApplet());
        return true;
      }
      int i = 0;
      while (localObject != null)
      {
        if (localObject == localApplet)
        {
          i = 1;
          break;
        }
        localObject = ((Component)localObject).getParent();
      }
      if (i == 0)
        return false;
      if (isDragStartEvent(localApplet, localMouseEvent))
      {
        dispatchEventImpl(localApplet, localMouseEvent, this.ctx.isSignedApplet());
        return true;
      }
      return false;
    }

    public void restore()
    {
      Container localContainer = (Container)this.ctx.getParentContainer().getWindowObject();
      Applet localApplet = ((AWTAppletAdapter)this.ctx.getApplet2Adapter()).getApplet();
      if ((localApplet != null) && (localContainer != null))
      {
        if ((localContainer instanceof JFrame))
          ((JFrame)localContainer).getContentPane().removeAll();
        else
          localContainer.removeAll();
        localApplet.resize(this.dragSize.width, this.dragSize.height);
        localContainer.add(localApplet);
        sendAppletRestored(localApplet);
        this.notificationsSent = false;
        this.closing = false;
        localApplet.repaint();
      }
    }

    private boolean iAmDragging()
    {
      return this.dragOffset != null;
    }

    private void setFrameTitle(Frame paramFrame)
    {
      String str = this.ctx.getDraggedTitle();
      int i = str.lastIndexOf(".");
      if (i > 0)
        str = str.substring(i + 1);
      paramFrame.setTitle(str);
    }

    private void setupCloseListener(Applet paramApplet, boolean paramBoolean)
    {
      this.closeListener = new CloseListener();
      if (!sendSetAppletCloseListener(paramApplet, this.closeListener))
      {
        this.closeButtonFrame = createCloseButton(this.closeListener, paramBoolean);
        refreshCloseButtonFrame();
        this.closeButtonFrame.setVisible(true);
      }
    }

    private void setupWindow(Frame paramFrame)
    {
      paramFrame.addWindowListener(new WindowAdapter()
      {
        private long lastActivate;

        public void windowActivated(WindowEvent paramAnonymousWindowEvent)
        {
          long l = System.currentTimeMillis();
          if (l - this.lastActivate > 200L)
          {
            if (AWTDragHelper.PerAppletInfo.this.closeButtonFrame != null)
              AWTDragHelper.PerAppletInfo.this.closeButtonFrame.toFront();
            this.lastActivate = l;
          }
        }

        public void windowClosing(WindowEvent paramAnonymousWindowEvent)
        {
          if (AWTDragHelper.PerAppletInfo.this.closeListener != null)
            AWTDragHelper.PerAppletInfo.this.closeListener.actionPerformed(null);
        }

        public void windowIconified(WindowEvent paramAnonymousWindowEvent)
        {
          if (AWTDragHelper.PerAppletInfo.this.closeButtonFrame != null)
            AWTDragHelper.PerAppletInfo.this.closeButtonFrame.setVisible(false);
        }

        public void windowDeiconified(WindowEvent paramAnonymousWindowEvent)
        {
          if (AWTDragHelper.PerAppletInfo.this.closeButtonFrame != null)
          {
            AWTDragHelper.PerAppletInfo.this.closeButtonFrame.setVisible(true);
            AWTDragHelper.PerAppletInfo.this.refreshCloseButtonFrame();
          }
        }
      });
      GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
      if (localGraphicsEnvironment != null)
      {
        GraphicsDevice localGraphicsDevice = localGraphicsEnvironment.getDefaultScreenDevice();
        if (localGraphicsDevice != null)
        {
          DisplayMode localDisplayMode = localGraphicsDevice.getDisplayMode();
          if (localDisplayMode != null)
          {
            int i = 30;
            paramFrame.setMaximizedBounds(new Rectangle(0, 0, localDisplayMode.getWidth() - i, localDisplayMode.getHeight() - i));
          }
        }
      }
    }

    private void makeDisconnected(Frame paramFrame)
    {
      this.frame = paramFrame;
      setupWindow(paramFrame);
      setFrameTitle(paramFrame);
      this.upperLeft = paramFrame.getLocation();
      Applet localApplet = ((AWTAppletAdapter)this.ctx.getApplet2Adapter()).getApplet();
      sendDragStarted(localApplet);
      sendDragFinished(localApplet);
      this.notificationsSent = true;
      setupCloseListener(localApplet, this.ctx.isSignedApplet());
    }

    private void refreshCloseButtonFrame()
    {
      if ((this.closeButtonFrame != null) && (this.frame != null))
      {
        Point localPoint = this.frame.getLocationOnScreen();
        Dimension localDimension = Toolkit.getDefaultToolkit().getScreenSize();
        int i = (int)localDimension.getWidth();
        int j = localPoint.x + this.frame.getWidth() + this.closeButtonOffset.x;
        if (j > i)
          j = i - 10;
        int k = localPoint.y + this.closeButtonOffset.y;
        if (k < 0)
          k = 10;
        this.closeButtonFrame.setLocation(j, k);
      }
    }

    private void dispatchEventImpl(Applet paramApplet, MouseEvent paramMouseEvent, boolean paramBoolean)
    {
      if (!iAmDragging())
      {
        AWTDragHelper.this.setSomeoneDragging(true);
        this.upperLeft = paramApplet.getLocationOnScreen();
        Point localPoint1 = AWTDragHelper.access$1100();
        this.dragOffset = new Point(localPoint1.x - this.upperLeft.x, localPoint1.y - this.upperLeft.y);
        this.dragSize = AWTDragHelper.getDragSize(paramApplet);
        if (this.frame == null)
        {
          if (AWTDragHelper.isSubclass(paramApplet.getClass(), "javax.swing.JApplet"))
          {
            if (paramBoolean)
              this.frame = new JFrame();
            else
              this.frame = new AWTDragHelper.DraggedAppletJFrame(AWTDragHelper.this);
          }
          else if (paramBoolean)
            this.frame = new Frame();
          else
            this.frame = new AWTDragHelper.DraggedAppletFrame(AWTDragHelper.this);
          this.ctx.setDraggedApplet();
          boolean bool = this.ctx.getUndecorated();
          this.frame.setUndecorated(bool);
          setupWindow(this.frame);
          setFrameTitle(this.frame);
          final Container localContainer = (Container)this.ctx.getParentContainer().getWindowObject();
          if (localContainer != null)
          {
            localContainer.remove(paramApplet);
            OldPluginAWTUtil.invokeLater(localContainer, new Runnable()
            {
              private final Container val$currentParent;

              public void run()
              {
                Canvas localCanvas = AWTDragHelper.this.getJavaLogoCanvas(AWTDragHelper.PerAppletInfo.this.dragSize);
                localContainer.add(localCanvas);
              }
            });
          }
          this.frame.add(paramApplet, "Center");
          paramApplet.setLocation(0, 0);
          this.frame.setSize(this.dragSize);
          this.frame.setResizable(false);
        }
        if (!this.notificationsSent)
        {
          sendDragStarted(paramApplet);
          this.listener.appletDraggingToDesktop(this.ctx);
        }
        this.frame.setLocation(this.upperLeft);
        this.frame.setVisible(true);
        if ((AWTDragHelper.isOSX) && (!this.notificationsSent))
          AccessController.doPrivileged(new PrivilegedAction()
          {
            public Object run()
            {
              try
              {
                Robot localRobot = new Robot();
                localRobot.mouseRelease(16);
                localRobot.mousePress(16);
              }
              catch (Exception localException)
              {
                localException.printStackTrace();
              }
              return null;
            }
          });
      }
      else
      {
        int i = paramMouseEvent.getID();
        if ((i == 506) || (i == 505))
        {
          if ((this.frame != null) && (this.dragOffset != null))
          {
            try
            {
              Point localPoint2 = AWTDragHelper.access$1100();
              this.upperLeft.x = (localPoint2.x - this.dragOffset.x);
              this.upperLeft.y = (localPoint2.y - this.dragOffset.y);
            }
            catch (Throwable localThrowable)
            {
              if (i != 505)
              {
                int j = paramMouseEvent.getX() - this.dragOffset.x;
                int k = paramMouseEvent.getY() - this.dragOffset.y;
                this.upperLeft.x += j;
                this.upperLeft.y += k;
              }
            }
            this.frame.setLocation(this.upperLeft);
            this.frame.toFront();
            if (this.closeButtonFrame != null)
            {
              refreshCloseButtonFrame();
              this.closeButtonFrame.toFront();
            }
          }
        }
        else if (((i == 502) && (paramMouseEvent.getButton() == 1)) || (i == 503))
        {
          this.dragOffset = null;
          if (!this.notificationsSent)
          {
            sendDragFinished(paramApplet);
            this.listener.appletDroppedOntoDesktop(this.ctx);
            this.notificationsSent = true;
            setupCloseListener(paramApplet, paramBoolean);
          }
          AWTDragHelper.this.setSomeoneDragging(false);
        }
      }
      paramMouseEvent.consume();
    }

    private Frame createCloseButton(final ActionListener paramActionListener, boolean paramBoolean)
    {
      Canvas local4 = new Canvas()
      {
        public void paint(Graphics paramAnonymousGraphics)
        {
          Graphics2D localGraphics2D = (Graphics2D)paramAnonymousGraphics;
          int i = (int)Math.max(1.0D, 0.025D * Math.min(getWidth(), getHeight()));
          int j = getWidth() / 2;
          int k = getHeight() / 2;
          localGraphics2D.setColor(Color.WHITE);
          localGraphics2D.fillRect(0, 0, i, getHeight());
          localGraphics2D.fillRect(0, 0, getWidth(), i);
          localGraphics2D.fillRect(0, getHeight() - i, getWidth(), i);
          localGraphics2D.fillRect(getWidth() - i, 0, i, getHeight());
          localGraphics2D.setStroke(new BasicStroke(i, 0, 2));
          float f = 0.15F;
          localGraphics2D.drawLine((int)(f * getWidth()), (int)(f * getHeight()), (int)((1.0F - f) * getWidth()), (int)((1.0F - f) * getHeight()));
          localGraphics2D.drawLine((int)((1.0F - f) * getWidth()), (int)(f * getHeight()), (int)(f * getWidth()), (int)((1.0F - f) * getHeight()));
        }
      };
      local4.setBackground(Color.BLACK);
      local4.addMouseListener(new MouseAdapter()
      {
        private final ActionListener val$closeListener;

        public void mouseClicked(MouseEvent paramAnonymousMouseEvent)
        {
          paramActionListener.actionPerformed(null);
          if (AWTDragHelper.PerAppletInfo.this.closeButtonFrame != null)
          {
            AWTDragHelper.PerAppletInfo.this.closeButtonFrame.setVisible(false);
            AWTDragHelper.PerAppletInfo.this.closeButtonFrame.dispose();
            AWTDragHelper.PerAppletInfo.this.closeButtonFrame = null;
          }
        }
      });
      Frame localFrame = new Frame();
      localFrame.setUndecorated(true);
      localFrame.setResizable(false);
      localFrame.setFocusableWindowState(false);
      localFrame.add(local4);
      localFrame.setSize(10, 10);
      return localFrame;
    }

    private boolean isDragStartEvent(Applet paramApplet, MouseEvent paramMouseEvent)
    {
      if (!this.initializedDragStartMethod)
      {
        this.initializedDragStartMethod = true;
        try
        {
          this.dragStartMethod = paramApplet.getClass().getMethod("isAppletDragStart", new Class[] { MouseEvent.class });
        }
        catch (Throwable localThrowable1)
        {
        }
      }
      if (this.dragStartMethod != null)
        try
        {
          Boolean localBoolean = (Boolean)this.dragStartMethod.invoke(paramApplet, new Object[] { paramMouseEvent });
          return localBoolean.booleanValue();
        }
        catch (Throwable localThrowable2)
        {
          this.dragStartMethod = null;
        }
      int i = paramMouseEvent.getModifiersEx();
      return (paramMouseEvent.getButton() == 1) && ((i == 1536) || ((!AWTDragHelper.isOSX) && (i == 1280)));
    }

    private void sendDragStarted(Applet paramApplet)
    {
      if (AWTDragHelper.DEBUG)
        System.out.println("DragHelper sending appletDragStarted for applet ID " + this.ctx.getAppletId());
      try
      {
        Method localMethod = paramApplet.getClass().getMethod("appletDragStarted", null);
        localMethod.invoke(paramApplet, null);
      }
      catch (Throwable localThrowable)
      {
      }
    }

    private void sendDragFinished(Applet paramApplet)
    {
      if (AWTDragHelper.DEBUG)
        System.out.println("DragHelper sending appletDragFinished for applet ID " + this.ctx.getAppletId());
      try
      {
        Method localMethod = paramApplet.getClass().getMethod("appletDragFinished", null);
        localMethod.invoke(paramApplet, null);
      }
      catch (Throwable localThrowable)
      {
      }
    }

    private void sendAppletRestored(Applet paramApplet)
    {
      if (AWTDragHelper.DEBUG)
        System.out.println("DragHelper sending appletRestored for applet ID " + this.ctx.getAppletId());
      try
      {
        Method localMethod = paramApplet.getClass().getMethod("appletRestored", null);
        localMethod.invoke(paramApplet, null);
      }
      catch (Throwable localThrowable)
      {
      }
    }

    private boolean sendSetAppletCloseListener(Applet paramApplet, ActionListener paramActionListener)
    {
      if (AWTDragHelper.DEBUG)
        System.out.println("DragHelper sending setAppletCloseListener for applet ID " + this.ctx.getAppletId());
      try
      {
        Method localMethod = paramApplet.getClass().getMethod("setAppletCloseListener", new Class[] { ActionListener.class });
        localMethod.invoke(paramApplet, new Object[] { paramActionListener });
        return true;
      }
      catch (Throwable localThrowable)
      {
      }
      return false;
    }

    class CloseListener
      implements ActionListener
    {
      CloseListener()
      {
      }

      public void actionPerformed(ActionEvent paramActionEvent)
      {
        if (AWTDragHelper.PerAppletInfo.this.closing)
          return;
        AWTDragHelper.PerAppletInfo.this.closing = true;
        if (AWTDragHelper.PerAppletInfo.this.listener != null)
          AWTDragHelper.PerAppletInfo.this.listener.appletExternalWindowClosed(AWTDragHelper.PerAppletInfo.this.ctx);
        if (AWTDragHelper.PerAppletInfo.this.ctx.isDisconnected())
          AWTDragHelper.this.unregister(AWTDragHelper.PerAppletInfo.this.ctx);
        AWTDragHelper.PerAppletInfo.this.frame.setVisible(false);
        AWTDragHelper.PerAppletInfo.this.frame.dispose();
        AWTDragHelper.PerAppletInfo.this.frame = null;
        if (AWTDragHelper.PerAppletInfo.this.closeButtonFrame != null)
        {
          AWTDragHelper.PerAppletInfo.this.closeButtonFrame.setVisible(false);
          AWTDragHelper.PerAppletInfo.this.closeButtonFrame.dispose();
          AWTDragHelper.PerAppletInfo.this.closeButtonFrame = null;
        }
      }
    }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     com.sun.deploy.uitoolkit.impl.awt.AWTDragHelper
 * JD-Core Version:    0.6.2
 */