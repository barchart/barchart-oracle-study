package com.sun.deploy.uitoolkit.impl.awt;

import com.sun.applet2.Applet2Context;
import com.sun.applet2.Applet2Host;
import com.sun.applet2.preloader.CancelException;
import com.sun.applet2.preloader.Preloader;
import com.sun.applet2.preloader.event.AppletInitEvent;
import com.sun.applet2.preloader.event.DownloadEvent;
import com.sun.applet2.preloader.event.ErrorEvent;
import com.sun.applet2.preloader.event.PreloaderEvent;
import com.sun.deploy.net.JARSigningException;
import com.sun.deploy.perf.DeployPerfUtil;
import com.sun.deploy.resources.ResourceManager;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.trace.TraceLevel;
import com.sun.deploy.uitoolkit.ToolkitStore;
import com.sun.deploy.uitoolkit.impl.awt.ui.DownloadWindow;
import com.sun.deploy.uitoolkit.ui.UIFactory;
import com.sun.deploy.util.URLUtil;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import sun.plugin.JavaRunTime;
import sun.plugin2.applet2.Plugin2Host;
import sun.plugin2.main.client.DisconnectedExecutionContext;
import sun.plugin2.util.ColorUtil;
import sun.plugin2.util.ColorUtil.ColorRGB;

public class AWTDefaultPreloader extends Preloader
{
  private Preloader webstartDP = null;
  private Container parentContainer = null;
  private AWTGrayBoxPainter grayBoxPainter;
  private GrayBoxListener grayBoxListener;
  private final GrayBoxPainterStarter grayBoxPainterStarter;
  private boolean gotError = false;
  private boolean hadSwitched = false;
  private boolean offerReload = false;
  private Color bgColor = Color.white;
  private Color fgColor = Color.black;

  public AWTDefaultPreloader(Applet2Context paramApplet2Context, Container paramContainer)
  {
    super(paramApplet2Context);
    this.parentContainer = paramContainer;
    if (isDownloadDialogMode())
    {
      this.webstartDP = new DownloadWindow();
      this.grayBoxPainterStarter = null;
    }
    else
    {
      this.grayBoxPainterStarter = new GrayBoxPainterStarter();
      this.grayBoxPainterStarter.start();
      this.webstartDP = null;
    }
  }

  private boolean isDownloadDialogMode()
  {
    Applet2Host localApplet2Host = this.ctx.getHost();
    if ((localApplet2Host instanceof Plugin2Host))
    {
      Plugin2Host localPlugin2Host = (Plugin2Host)localApplet2Host;
      if ((localPlugin2Host.getAppletExecutionContext() instanceof DisconnectedExecutionContext))
        return true;
    }
    return false;
  }

  private synchronized void markGotError()
  {
    this.gotError = true;
  }

  private synchronized boolean hasErrorOccurred()
  {
    return this.gotError;
  }

  private synchronized void resetErrorStatus()
  {
    this.gotError = false;
  }

  public Object getOwner()
  {
    this.grayBoxPainterStarter.waitTillDone();
    return this.parentContainer;
  }

  public boolean handleEvent(PreloaderEvent paramPreloaderEvent)
    throws CancelException
  {
    if (this.grayBoxPainterStarter != null)
      return browserHandleEvent(paramPreloaderEvent);
    return webstartHandleEvent(paramPreloaderEvent);
  }

  private boolean webstartHandleEvent(PreloaderEvent paramPreloaderEvent)
    throws CancelException
  {
    if (paramPreloaderEvent.getType() == 5)
    {
      AppletInitEvent localAppletInitEvent = (AppletInitEvent)paramPreloaderEvent;
      int i = localAppletInitEvent.getSubtype();
      if ((i == 3) || (i == 4) || (i == 2))
        this.ctx.getHost().showApplet();
    }
    return this.webstartDP.handleEvent(paramPreloaderEvent);
  }

  private boolean browserHandleEvent(PreloaderEvent paramPreloaderEvent)
    throws CancelException
  {
    this.grayBoxPainterStarter.waitTillDone();
    if (hasErrorOccurred())
    {
      if (paramPreloaderEvent.getType() == 1)
        resetErrorStatus();
      return true;
    }
    switch (paramPreloaderEvent.getType())
    {
    case 3:
      DownloadEvent localDownloadEvent = (DownloadEvent)paramPreloaderEvent;
      if (this.grayBoxPainter != null)
        this.grayBoxPainter.setProgress(localDownloadEvent.getOverallPercentage());
      return true;
    case 6:
      markGotError();
      if (this.grayBoxPainter != null)
        this.grayBoxPainter.finishPainting();
      ErrorEvent localErrorEvent = (ErrorEvent)paramPreloaderEvent;
      Throwable localThrowable = localErrorEvent.getException();
      boolean bool = true;
      if ((localThrowable != null) && ((localThrowable instanceof JARSigningException)))
        bool = false;
      String str = localThrowable != null ? localThrowable.getMessage() : null;
      this.ctx.getHost().showError(str, localThrowable, bool);
      return true;
    case 7:
      this.offerReload = true;
      return true;
    case 5:
      AppletInitEvent localAppletInitEvent = (AppletInitEvent)paramPreloaderEvent;
      if (((!this.hadSwitched) && ((localAppletInitEvent.getSubtype() == 3) || (localAppletInitEvent.getSubtype() == 4))) || (localAppletInitEvent.getSubtype() == 6))
      {
        shutdownGrayBoxPainter();
        this.ctx.getHost().showApplet();
        this.hadSwitched = true;
      }
      return true;
    case 2:
      return true;
    case 4:
    }
    return false;
  }

  void handleReloadApplet()
  {
    this.ctx.getHost().reloadAppletPage();
  }

  public Color getBGColor()
  {
    this.grayBoxPainterStarter.waitTillDone();
    return this.bgColor;
  }

  public Color getFGColor()
  {
    this.grayBoxPainterStarter.waitTillDone();
    return this.fgColor;
  }

  public void shutdownGrayBoxPainter()
  {
    if (this.grayBoxPainterStarter.isAlive())
      this.grayBoxPainterStarter.interrupt();
    if (this.grayBoxPainter != null)
    {
      this.grayBoxPainter.finishPainting();
      this.grayBoxPainter = null;
    }
    if (this.grayBoxListener != null)
    {
      if (this.parentContainer != null)
        this.parentContainer.removeMouseListener(this.grayBoxListener);
      this.grayBoxListener = null;
    }
  }

  private void setupColorAndText(String paramString)
  {
    ColorUtil.ColorRGB localColorRGB = null;
    String str1 = this.ctx.getParameter("boxbgcolor");
    if (str1 != null)
    {
      localColorRGB = ColorUtil.createColorRGB("boxbgcolor", str1);
      if (localColorRGB != null)
        this.bgColor = new Color(localColorRGB.rgb);
    }
    this.grayBoxPainter.setBoxBGColor(this.bgColor);
    String str2 = this.ctx.getParameter("boxfgcolor");
    if (str2 != null)
    {
      localColorRGB = ColorUtil.createColorRGB("boxfgcolor", str2);
      if (localColorRGB != null)
        this.fgColor = new Color(localColorRGB.rgb);
    }
    this.grayBoxPainter.setBoxFGColor(this.fgColor);
    if (this.parentContainer != null)
    {
      this.parentContainer.setBackground(this.grayBoxPainter.getBoxBGColor());
      this.parentContainer.setForeground(this.grayBoxPainter.getBoxFGColor());
    }
    if (paramString != null)
      this.grayBoxPainter.setWaitingMessage(paramString);
    else
      this.grayBoxPainter.setWaitingMessage(getWaitingMessage());
  }

  public void doPaint(Graphics paramGraphics)
  {
    this.grayBoxPainterStarter.waitTillDone();
    if (this.grayBoxPainter != null)
      this.grayBoxPainter.paintGrayBox(paramGraphics);
  }

  protected static String getMessage(String paramString)
  {
    return ResourceManager.getMessage(paramString);
  }

  protected String getWaitingMessage()
  {
    if (hasErrorOccurred())
      return getMessage("failed");
    MessageFormat localMessageFormat = new MessageFormat(getMessage("loading"));
    return localMessageFormat.format(new Object[] { getHandledType() });
  }

  protected String getHandledType()
  {
    return getMessage("java_applet");
  }

  private class GrayBoxListener
    implements MouseListener, ActionListener
  {
    private PopupMenu popup;
    private MenuItem open_console;
    private MenuItem about_java;
    private String msg = null;
    private Container parent;

    GrayBoxListener(Container paramString, String arg3)
    {
      Object localObject;
      this.msg = localObject;
      this.parent = paramString;
    }

    private PopupMenu getPopupMenu()
    {
      if (this.popup == null)
      {
        Font localFont1 = this.parent.getFont();
        Font localFont2 = localFont1.deriveFont(11.0F);
        this.popup = new PopupMenu();
        this.open_console = new MenuItem(ResourceManager.getMessage("dialogfactory.menu.open_console"));
        this.open_console.setFont(localFont2);
        this.about_java = new MenuItem(ResourceManager.getMessage("dialogfactory.menu.about_java"));
        this.about_java.setFont(localFont2);
        this.open_console.addActionListener(this);
        this.about_java.addActionListener(this);
        this.popup.add(this.open_console);
        this.popup.add("-");
        this.popup.add(this.about_java);
        this.parent.add(this.popup);
      }
      return this.popup;
    }

    public void mouseEntered(MouseEvent paramMouseEvent)
    {
      Trace.msgPrintln(this.msg != null ? this.msg : AWTDefaultPreloader.this.getWaitingMessage(), null, TraceLevel.BASIC);
    }

    public void mouseExited(MouseEvent paramMouseEvent)
    {
    }

    public void mousePressed(MouseEvent paramMouseEvent)
    {
      if ((paramMouseEvent.isPopupTrigger()) && (AWTDefaultPreloader.this.hasErrorOccurred()))
        getPopupMenu().show(paramMouseEvent.getComponent(), paramMouseEvent.getX(), paramMouseEvent.getY());
    }

    public void mouseReleased(MouseEvent paramMouseEvent)
    {
      if ((paramMouseEvent.isPopupTrigger()) && (AWTDefaultPreloader.this.hasErrorOccurred()))
        getPopupMenu().show(paramMouseEvent.getComponent(), paramMouseEvent.getX(), paramMouseEvent.getY());
    }

    public void mouseClicked(MouseEvent paramMouseEvent)
    {
    }

    public void actionPerformed(ActionEvent paramActionEvent)
    {
      if (paramActionEvent.getSource() == this.open_console)
        JavaRunTime.showJavaConsole(true);
      else if (paramActionEvent.getSource() == this.about_java)
        ToolkitStore.getUI().showAboutJavaDialog();
    }
  }

  private class GrayBoxPainterStarter extends Thread
  {
    private int state = 0;

    public GrayBoxPainterStarter()
    {
      setDaemon(true);
    }

    public void waitTillDone()
    {
      synchronized (this)
      {
        while (this.state == 0)
          try
          {
            wait();
          }
          catch (InterruptedException localInterruptedException)
          {
          }
      }
    }

    public void run()
    {
      int i = 0;
      try
      {
        long l = DeployPerfUtil.put(0L, "Plugin2Manager.setupGrayBoxPainter() - BEGIN");
        Trace.println("GrayBox: parent = " + AWTDefaultPreloader.this.parentContainer, TraceLevel.PRELOADER);
        if (AWTDefaultPreloader.this.parentContainer != null)
        {
          AWTDefaultPreloader.this.grayBoxPainter = new AWTGrayBoxPainter(AWTDefaultPreloader.this.parentContainer, AWTDefaultPreloader.this.ctx.getHost());
          String str1 = AWTDefaultPreloader.this.ctx.getParameter("image");
          if (str1 != null)
            try
            {
              URL localURL1 = AWTDefaultPreloader.this.ctx.getCodeBase();
              URL localURL2 = new URL(localURL1, str1);
              if (!URLUtil.checkTargetURL(localURL1, localURL2))
                throw new SecurityException("Permission denied: " + localURL2);
              boolean bool = Boolean.valueOf(AWTDefaultPreloader.this.ctx.getParameter("centerimage")).booleanValue();
              AWTDefaultPreloader.this.grayBoxPainter.setBoxBorder(AWTDefaultPreloader.this.ctx.getParameter("boxborder"));
              AWTDefaultPreloader.this.grayBoxPainter.setCustomImageURL(localURL2, bool);
            }
            catch (MalformedURLException localMalformedURLException)
            {
              localMalformedURLException.printStackTrace();
            }
          String str2 = AWTDefaultPreloader.this.ctx.getParameter("boxmessage");
          AWTDefaultPreloader.this.setupColorAndText(str2);
          AWTDefaultPreloader.this.grayBoxPainter.beginPainting(Thread.currentThread().getThreadGroup());
          AWTDefaultPreloader.this.grayBoxListener = new AWTDefaultPreloader.GrayBoxListener(AWTDefaultPreloader.this, AWTDefaultPreloader.this.parentContainer, str2);
          AWTDefaultPreloader.this.parentContainer.addMouseListener(AWTDefaultPreloader.this.grayBoxListener);
        }
        DeployPerfUtil.put(l, "Plugin2Manager.setupGrayBoxPainter() - END");
        i = 1;
      }
      finally
      {
        synchronized (this)
        {
          this.state = (i != 0 ? 1 : -1);
          notifyAll();
        }
      }
    }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     com.sun.deploy.uitoolkit.impl.awt.AWTDefaultPreloader
 * JD-Core Version:    0.6.2
 */