package com.sun.deploy.uitoolkit.impl.awt;

import com.sun.applet2.Applet2Host;
import com.sun.deploy.trace.Trace;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.ImageObserver;
import sun.plugin.util.UIUtil;

class AWTGrayBoxPanel extends Panel
  implements ComponentListener, ImageObserver
{
  private static final Color LOADING_BORDER = new Color(153, 153, 153);
  private Color backgroundColor;
  private Color foregroundColor;
  private AWTAnimationPanel2 m_panel = null;
  private Container m_parent;
  private int m_maxValue;
  private boolean m_boxBorder = true;
  private Image m_image = null;
  private boolean m_imageIsCentered = false;
  private boolean m_animateImage = true;
  long startTimeMillis;
  int endSequenceMillis = 2000;
  private Image backBufferImage;
  private int backBufferImageWidth;
  private int backBufferImageHeight;

  public AWTGrayBoxPanel(Container paramContainer)
  {
    this(paramContainer, Color.WHITE);
  }

  public AWTGrayBoxPanel(Container paramContainer, Color paramColor)
  {
    this(paramContainer, paramColor, Color.BLACK);
  }

  public AWTGrayBoxPanel(Container paramContainer, Color paramColor1, Color paramColor2)
  {
    this(paramContainer, paramColor1, paramColor2, null);
  }

  public AWTGrayBoxPanel(Container paramContainer, Color paramColor1, Color paramColor2, Applet2Host paramApplet2Host)
  {
    this.m_parent = paramContainer;
    setBgColor(paramColor1);
    setFgColor(paramColor2);
    setLayout(new BorderLayout());
    UIUtil.disableBackgroundErase(this);
  }

  public void setCustomImage(Image paramImage, boolean paramBoolean)
  {
    setImage(paramImage, paramBoolean);
  }

  public void setBgColor(Color paramColor)
  {
    this.backgroundColor = paramColor;
    setBackground(paramColor);
    if (this.m_panel != null)
      this.m_panel.setBoxBGColor(paramColor);
  }

  public void setFgColor(Color paramColor)
  {
    this.foregroundColor = paramColor;
    setForeground(paramColor);
    if (this.m_panel != null)
      this.m_panel.setBoxFGColor(paramColor);
  }

  public void setBoxBorder(boolean paramBoolean)
  {
    this.m_boxBorder = paramBoolean;
  }

  public void stopOnError()
  {
    if (this.m_panel != null)
    {
      this.m_panel.stopAnimation();
      removeAll();
      this.m_panel = null;
      validate();
    }
  }

  public void setMaxProgressValue(int paramInt)
  {
    this.m_maxValue = paramInt;
  }

  public void progress(int paramInt)
  {
    long l = (this.m_maxValue - paramInt) * (System.currentTimeMillis() - this.startTimeMillis) / (paramInt + 1);
    if (this.m_panel != null)
    {
      float f = paramInt / this.m_maxValue;
      if (l > this.endSequenceMillis)
        this.m_panel.setProgressValue(f);
      else
        this.m_panel.fadeAway();
    }
    else
    {
      repaint();
    }
  }

  public void start()
  {
    if ((this.m_panel == null) && (this.m_image == null))
    {
      this.m_panel = new AWTAnimationPanel2();
      this.m_panel.setCursor(new Cursor(12));
      add(this.m_panel, "Center");
      this.m_panel.setBoxBGColor(this.backgroundColor);
      this.m_panel.setBoxFGColor(this.foregroundColor);
    }
    this.startTimeMillis = System.currentTimeMillis();
    if (this.m_panel != null)
    {
      this.m_panel.startAnimation();
    }
    else if (this.m_image != null)
    {
      this.m_animateImage = true;
      repaint();
    }
    this.m_parent.addComponentListener(this);
  }

  public void stop()
  {
    if (this.m_panel != null)
      this.m_panel.stopAnimation();
    this.m_animateImage = false;
    this.m_parent.removeComponentListener(this);
  }

  public void update(Graphics paramGraphics)
  {
    paint(paramGraphics);
  }

  public void paint(Graphics paramGraphics)
  {
    try
    {
      Dimension localDimension = this.m_parent.getSize();
      if (this.m_panel != null)
      {
        this.m_panel.repaint();
      }
      else
      {
        int j;
        if ((this.backBufferImage == null) || (this.backBufferImageWidth != localDimension.width) || (this.backBufferImageHeight != localDimension.height))
        {
          if (this.backBufferImage != null)
            this.backBufferImage.flush();
          int i = Math.max(1, localDimension.width);
          j = Math.max(1, localDimension.height);
          this.backBufferImage = createImage(i, j);
          this.backBufferImageWidth = i;
          this.backBufferImageHeight = j;
        }
        Graphics localGraphics = this.backBufferImage.getGraphics();
        localGraphics.setColor(this.backgroundColor);
        localGraphics.fillRect(0, 0, this.backBufferImageWidth, this.backBufferImageHeight);
        if (this.m_image != null)
        {
          j = this.m_image.getWidth(this);
          int k = this.m_image.getHeight(this);
          if ((this.m_imageIsCentered) && (j >= 0) && (k >= 0))
            drawImage(localGraphics, this.m_image, (this.backBufferImageWidth - j) / 2, (this.backBufferImageHeight - k) / 2);
          else if ((localDimension.width > 24) && (localDimension.height > 24))
            drawImage(localGraphics, this.m_image, 1, 1);
          else
            drawImage(localGraphics, this.m_image, 0, 0);
        }
        paramGraphics.drawImage(this.backBufferImage, 0, 0, null);
      }
      if ((this.m_boxBorder) && (localDimension.width > 24) && (localDimension.height > 24))
        drawBorder(paramGraphics, localDimension);
    }
    catch (Exception localException)
    {
      Trace.ignoredException(localException);
    }
  }

  public boolean imageUpdate(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    if ((paramInt1 & 0x10) != 0)
    {
      if (this.m_animateImage)
        repaint();
      return this.m_animateImage;
    }
    if ((paramInt1 & 0x20) != 0)
      repaint();
    return true;
  }

  private void drawImage(Graphics paramGraphics, Image paramImage, int paramInt1, int paramInt2)
  {
    paramGraphics.drawImage(paramImage, paramInt1, paramInt2, this.backgroundColor, this);
  }

  private void drawBorder(Graphics paramGraphics, Dimension paramDimension)
  {
    Color localColor1 = LOADING_BORDER;
    Color localColor2 = paramGraphics.getColor();
    paramGraphics.setColor(localColor1);
    paramGraphics.drawRect(0, 0, paramDimension.width - 1, paramDimension.height - 1);
    paramGraphics.setColor(localColor2);
  }

  private synchronized void setImage(Image paramImage, boolean paramBoolean)
  {
    if (this.m_panel != null)
    {
      this.m_panel.stopAnimation();
      removeAll();
      this.m_panel = null;
    }
    this.m_image = paramImage;
    this.m_imageIsCentered = paramBoolean;
    repaint();
  }

  public void componentResized(ComponentEvent paramComponentEvent)
  {
    Dimension localDimension = this.m_parent.getSize();
    setSize(localDimension);
    if (this.m_panel != null)
      this.m_panel.setSize(localDimension);
  }

  public void componentShown(ComponentEvent paramComponentEvent)
  {
  }

  public void componentMoved(ComponentEvent paramComponentEvent)
  {
  }

  public void componentHidden(ComponentEvent paramComponentEvent)
  {
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     com.sun.deploy.uitoolkit.impl.awt.AWTGrayBoxPanel
 * JD-Core Version:    0.6.2
 */