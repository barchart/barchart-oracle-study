package com.sun.deploy.uitoolkit.impl.awt;

import com.sun.applet2.Applet2Host;
import com.sun.deploy.cache.MemoryCache;
import com.sun.deploy.resources.ResourceManager;
import com.sun.deploy.security.TrustDecider;
import com.sun.deploy.ui.AppInfo;
import com.sun.deploy.uitoolkit.ToolkitStore;
import com.sun.deploy.uitoolkit.ui.UIFactory;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;
import sun.plugin.JavaRunTime;

public class AWTErrorPanel extends JPanel
  implements MouseListener, ActionListener
{
  private Image errorImage = null;
  private static final String ERROR_IMAGE_FILE = "com/sun/deploy/uitoolkit/impl/awt/graybox_error.png";
  private static final Color ERROR_BORDER = new Color(204, 204, 204);
  private Color bg_color;
  private Color fg_color;
  private PopupMenu popup;
  private MenuItem open_console;
  private MenuItem about_java;
  private MenuItem reload;
  private boolean offerReload = false;
  private Applet2Host host = null;
  private String title = null;
  private String message = null;

  public AWTErrorPanel(Color paramColor1, Color paramColor2, Applet2Host paramApplet2Host, boolean paramBoolean)
  {
    this.host = paramApplet2Host;
    setBackground(paramColor1);
    this.bg_color = paramColor1;
    this.fg_color = paramColor2;
    setToolTipText(ResourceManager.getMessage("applet.error.message"));
    addMouseListener(this);
    this.offerReload = paramBoolean;
    if (paramBoolean)
      TrustDecider.resetDenyStore();
  }

  public void setMessage(String paramString1, String paramString2)
  {
    this.title = paramString1;
    this.message = paramString2;
  }

  private String getMasthead()
  {
    return (this.title == null) || (this.title.length() == 0) ? ResourceManager.getMessage("applet.error.generic.masthead") : this.title;
  }

  private String getMessage()
  {
    return (this.message == null) || (this.message.length() == 0) ? ResourceManager.getMessage("applet.error.generic.body") : this.message;
  }

  private synchronized Image getErrorImage()
  {
    if (this.errorImage == null)
    {
      Toolkit localToolkit = Toolkit.getDefaultToolkit();
      this.errorImage = localToolkit.createImage(ClassLoader.getSystemResource("com/sun/deploy/uitoolkit/impl/awt/graybox_error.png"));
      MediaTracker localMediaTracker = new MediaTracker(this);
      localMediaTracker.addImage(this.errorImage, 0);
      try
      {
        localMediaTracker.waitForID(0);
      }
      catch (InterruptedException localInterruptedException)
      {
      }
    }
    return this.errorImage;
  }

  protected void paintComponent(Graphics paramGraphics)
  {
    super.paintComponent(paramGraphics);
    Graphics2D localGraphics2D = (Graphics2D)paramGraphics.create();
    drawBorder(localGraphics2D, getSize());
    if ((getWidth() > 24) && (getHeight() > 24))
    {
      int i = 4;
      int j = 5;
      localGraphics2D.drawImage(getErrorImage(), i, j, this.bg_color, null);
      drawMessage(localGraphics2D, ResourceManager.getMessage("applet.error.message"), getErrorImage().getWidth(null) + i + 7, getErrorImage().getHeight(null) + j - 4);
    }
    localGraphics2D.dispose();
  }

  private void drawBorder(Graphics paramGraphics, Dimension paramDimension)
  {
    Color localColor1 = ERROR_BORDER;
    Color localColor2 = paramGraphics.getColor();
    paramGraphics.setColor(localColor1);
    paramGraphics.drawRect(0, 0, paramDimension.width - 1, paramDimension.height - 1);
    paramGraphics.setColor(localColor2);
  }

  private void drawMessage(Graphics2D paramGraphics2D, String paramString, int paramInt1, int paramInt2)
  {
    Dimension localDimension = getSize();
    FontMetrics localFontMetrics = paramGraphics2D.getFontMetrics();
    Rectangle2D localRectangle2D = localFontMetrics.getStringBounds(paramString, paramGraphics2D);
    int i = 1;
    if (localRectangle2D.getWidth() + paramInt1 > localDimension.getWidth())
    {
      i = 0;
      while (i == 0)
      {
        int j = paramString.lastIndexOf(" ");
        if (j == -1)
          break;
        paramString = paramString.substring(0, j);
        paramString = paramString + "...";
        localRectangle2D = localFontMetrics.getStringBounds(paramString, paramGraphics2D);
        if (localRectangle2D.getWidth() + paramInt1 < localDimension.getWidth())
          i = 1;
      }
      if (i == 0)
      {
        paramString = "...";
        localRectangle2D = localFontMetrics.getStringBounds(paramString, paramGraphics2D);
        if (localRectangle2D.getWidth() + paramInt1 < localDimension.getWidth())
          i = 1;
      }
    }
    if (i != 0)
    {
      Color localColor = paramGraphics2D.getColor();
      paramGraphics2D.setColor(this.fg_color);
      paramGraphics2D.drawString(paramString, paramInt1, paramInt2);
      paramGraphics2D.setColor(localColor);
    }
  }

  private PopupMenu getPopupMenu()
  {
    if (this.popup == null)
    {
      this.popup = new PopupMenu();
      this.open_console = new MenuItem(ResourceManager.getMessage("dialogfactory.menu.open_console"));
      this.popup.add(this.open_console);
      this.popup.add("-");
      this.about_java = new MenuItem(ResourceManager.getMessage("dialogfactory.menu.about_java"));
      this.popup.add(this.about_java);
      if (this.offerReload)
      {
        this.reload = new MenuItem("Reload applet");
        this.popup.add("-");
        this.popup.add(this.reload);
        this.reload.addActionListener(this);
      }
      this.open_console.addActionListener(this);
      this.about_java.addActionListener(this);
      add(this.popup);
    }
    return this.popup;
  }

  public void mouseEntered(MouseEvent paramMouseEvent)
  {
  }

  public void mouseExited(MouseEvent paramMouseEvent)
  {
  }

  public void mousePressed(MouseEvent paramMouseEvent)
  {
    if (paramMouseEvent.isPopupTrigger())
      getPopupMenu().show(paramMouseEvent.getComponent(), paramMouseEvent.getX(), paramMouseEvent.getY());
    else if (paramMouseEvent.getButton() == 1)
      onLeftMouseClick();
  }

  public void mouseReleased(MouseEvent paramMouseEvent)
  {
    if (paramMouseEvent.isPopupTrigger())
      getPopupMenu().show(paramMouseEvent.getComponent(), paramMouseEvent.getX(), paramMouseEvent.getY());
    else if (paramMouseEvent.getButton() == 1)
      onLeftMouseClick();
  }

  public void mouseClicked(MouseEvent paramMouseEvent)
  {
  }

  private void onLeftMouseClick()
  {
    int i = -1;
    if (this.offerReload)
    {
      ToolkitStore.getUI();
      i = ToolkitStore.getUI().showMessageDialog(null, new AppInfo(), 0, null, getMasthead(), getMessage(), null, "applet.error.details.btn", "applet.error.ignore.btn", "applet.error.reload.btn");
    }
    else
    {
      ToolkitStore.getUI();
      i = ToolkitStore.getUI().showMessageDialog(null, new AppInfo(), 0, null, getMasthead(), getMessage(), null, "applet.error.details.btn", "applet.error.ignore.btn", null);
    }
    ToolkitStore.getUI();
    if (i == 0)
    {
      JavaRunTime.showJavaConsole(true);
    }
    else
    {
      ToolkitStore.getUI();
      if (i != 1)
      {
        ToolkitStore.getUI();
        if (i == 3)
          reloadApplet();
      }
    }
  }

  public void actionPerformed(ActionEvent paramActionEvent)
  {
    if (paramActionEvent.getSource() == this.open_console)
      JavaRunTime.showJavaConsole(true);
    else if (paramActionEvent.getSource() == this.about_java)
      ToolkitStore.getUI().showAboutJavaDialog();
    else if (paramActionEvent.getSource() == this.reload)
      reloadApplet();
  }

  private void reloadApplet()
  {
    if (this.host != null)
    {
      MemoryCache.clearLoadedResources();
      this.host.reloadAppletPage();
    }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     com.sun.deploy.uitoolkit.impl.awt.AWTErrorPanel
 * JD-Core Version:    0.6.2
 */