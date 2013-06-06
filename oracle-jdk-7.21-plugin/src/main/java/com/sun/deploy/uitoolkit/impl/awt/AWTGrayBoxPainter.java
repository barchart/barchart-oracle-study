package com.sun.deploy.uitoolkit.impl.awt;

import com.sun.applet2.Applet2Host;
import com.sun.deploy.trace.Trace;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.net.URL;
import sun.awt.AWTAccessor;
import sun.awt.AWTAccessor.ContainerAccessor;

public class AWTGrayBoxPainter
{
  private Color boxBGColor;
  private Color boxFGColor;
  private boolean boxBorder = true;
  private Image customImage;
  private URL customImageURL;
  private boolean customImageIsCentered;
  private String waitingMessage;
  private Container container;
  private ThreadGroup threadGroup;
  private MediaTracker tracker;
  private boolean stopOnError = false;
  private boolean animationReady = false;
  private boolean progressBarReady = false;
  private volatile boolean paintingSuspended = false;
  private volatile boolean paintingFinished = false;
  private AWTGrayBoxPanel m_grayboxPanel = null;
  private Applet2Host host = null;
  private int currentProgress = 0;

  public AWTGrayBoxPainter(Container paramContainer, Applet2Host paramApplet2Host)
  {
    this.container = paramContainer;
    this.host = paramApplet2Host;
  }

  public synchronized void beginPainting(ThreadGroup paramThreadGroup)
  {
    this.threadGroup = paramThreadGroup;
    this.tracker = new MediaTracker(this.container);
    loadCustomImage();
    final AWTGrayBoxPainter localAWTGrayBoxPainter = this;
    this.paintingSuspended = false;
    Thread localThread = new Thread(new Runnable()
    {
      private final AWTGrayBoxPainter val$painter;

      public void run()
      {
        try
        {
          Thread.sleep(1500L);
        }
        catch (Throwable localThrowable)
        {
        }
        finally
        {
          localAWTGrayBoxPainter.setAnimationReady();
          localAWTGrayBoxPainter.setProgressBarReady();
          if ((!AWTGrayBoxPainter.this.paintingSuspended) && (!AWTGrayBoxPainter.this.paintingFinished))
            localAWTGrayBoxPainter.repaintGrayBox();
        }
      }
    });
    localThread.setDaemon(true);
    localThread.start();
  }

  public synchronized void finishPainting()
  {
    try
    {
      if (this.m_grayboxPanel != null)
      {
        this.m_grayboxPanel.stop();
        this.container.remove(this.m_grayboxPanel);
        this.m_grayboxPanel = null;
      }
      this.paintingSuspended = false;
      this.paintingFinished = true;
    }
    catch (RuntimeException localRuntimeException)
    {
      Trace.ignoredException(localRuntimeException);
    }
  }

  public synchronized void stopOnError()
  {
    if (this.m_grayboxPanel != null)
      this.m_grayboxPanel.stopOnError();
  }

  private void loadCustomImage()
  {
    if (this.customImageURL != null)
      try
      {
        this.customImage = Toolkit.getDefaultToolkit().getImage(this.customImageURL);
        this.tracker.addImage(this.customImage, 1);
        this.tracker.waitForID(1);
        setAnimationReady();
      }
      catch (InterruptedException localInterruptedException)
      {
        localInterruptedException.printStackTrace();
      }
      catch (RuntimeException localRuntimeException)
      {
        Trace.ignoredException(localRuntimeException);
      }
  }

  public void setCustomImageURL(URL paramURL, boolean paramBoolean)
  {
    this.customImageURL = paramURL;
    this.customImageIsCentered = paramBoolean;
  }

  public void setProgress(int paramInt)
  {
    this.currentProgress = paramInt;
    repaintGrayBox();
  }

  public void setBoxBGColor(Color paramColor)
  {
    this.boxBGColor = paramColor;
  }

  public Color getBoxBGColor()
  {
    return this.boxBGColor;
  }

  public void setBoxFGColor(Color paramColor)
  {
    this.boxFGColor = paramColor;
  }

  public Color getBoxFGColor()
  {
    return this.boxFGColor;
  }

  public void setBoxBorder(String paramString)
  {
    if (paramString == null)
      this.boxBorder = true;
    else
      this.boxBorder = Boolean.valueOf(paramString).booleanValue();
    if (this.m_grayboxPanel != null)
      this.m_grayboxPanel.setBoxBorder(this.boxBorder);
  }

  public void setWaitingMessage(String paramString)
  {
    this.waitingMessage = paramString;
  }

  public synchronized void suspendPainting()
  {
    try
    {
      if (!this.paintingSuspended)
      {
        if (this.m_grayboxPanel != null)
        {
          this.m_grayboxPanel.stop();
          this.container.remove(this.m_grayboxPanel);
        }
        this.paintingSuspended = true;
        EventQueue.invokeLater(new Runnable()
        {
          public void run()
          {
            AWTGrayBoxPainter.this.validateContainer();
          }
        });
      }
    }
    catch (RuntimeException localRuntimeException)
    {
      Trace.ignoredException(localRuntimeException);
    }
  }

  public synchronized void resumePainting()
  {
    try
    {
      if (this.paintingSuspended)
      {
        if (this.m_grayboxPanel != null)
        {
          this.m_grayboxPanel.start();
          this.container.add(this.m_grayboxPanel, "Center");
        }
        this.paintingSuspended = false;
        EventQueue.invokeLater(new Runnable()
        {
          public void run()
          {
            AWTGrayBoxPainter.this.validateContainer();
          }
        });
      }
    }
    catch (RuntimeException localRuntimeException)
    {
      Trace.ignoredException(localRuntimeException);
    }
  }

  public void setAnimationReady()
  {
    this.animationReady = true;
  }

  public void setProgressBarReady()
  {
    this.progressBarReady = true;
  }

  private void repaintGrayBox()
  {
    try
    {
      paintGrayBox(this.container.getGraphics());
    }
    catch (RuntimeException localRuntimeException)
    {
      Trace.ignoredException(localRuntimeException);
    }
  }

  private void validateContainer()
  {
    try
    {
      AWTAccessor.getContainerAccessor().validateUnconditionally(this.container);
    }
    catch (Throwable localThrowable)
    {
      this.container.validate();
    }
  }

  private synchronized AWTGrayBoxPanel getGrayBoxPanel()
  {
    if (this.m_grayboxPanel == null)
    {
      this.m_grayboxPanel = new AWTGrayBoxPanel(this.container, this.boxBGColor, this.boxFGColor, this.host);
      if (!this.paintingSuspended)
      {
        this.container.add(this.m_grayboxPanel, "Center");
        this.m_grayboxPanel.setBoxBorder(this.boxBorder);
        if (this.customImage == null)
        {
          this.m_grayboxPanel.setMaxProgressValue(100);
          this.m_grayboxPanel.start();
        }
        else
        {
          this.m_grayboxPanel.setCustomImage(this.customImage, this.customImageIsCentered);
        }
        validateContainer();
      }
    }
    return this.m_grayboxPanel;
  }

  synchronized void paintGrayBox(Graphics paramGraphics)
  {
    try
    {
      if ((this.paintingSuspended) || (this.paintingFinished))
        return;
      if (paramGraphics == null)
      {
        localObject = getGrayBoxPanel();
        return;
      }
      Object localObject = this.container.getSize();
      if ((!this.animationReady) && (!this.stopOnError))
      {
        if ((((Dimension)localObject).width > 0) && (((Dimension)localObject).height > 0))
        {
          paramGraphics.setColor(this.boxBGColor);
          paramGraphics.fillRect(0, 0, ((Dimension)localObject).width, ((Dimension)localObject).height);
        }
        return;
      }
      if ((((Dimension)localObject).width > 0) && (((Dimension)localObject).height > 0))
      {
        AWTGrayBoxPanel localAWTGrayBoxPanel = getGrayBoxPanel();
        localAWTGrayBoxPanel.progress(this.currentProgress);
      }
    }
    catch (RuntimeException localRuntimeException)
    {
      Trace.ignoredException(localRuntimeException);
    }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     com.sun.deploy.uitoolkit.impl.awt.AWTGrayBoxPainter
 * JD-Core Version:    0.6.2
 */