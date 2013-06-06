package com.sun.deploy.uitoolkit.impl.awt;

import com.sun.deploy.trace.Trace;
import java.awt.AlphaComposite;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import sun.plugin.util.UIUtil;

public class AWTAnimationPanel2 extends Canvas
  implements Runnable
{
  private static final int ANIMATION_CYCLE_TIME = 2000;
  private static final int ZIPPY_PULSE_TIME = 300;
  private static final long[] stateStops = { 0L, 300L, 99999999L, 750L, 950L, 300L, 1000L };
  private static final String JAVA_LOGO_IMAGE = "JavaCupLogo-161.png";
  private static final String JAVA_COM_IMAGE = "javacom300.png";
  private Color bgColor;
  private Color fgColor = Color.BLACK;
  private Image javaLogoImage;
  private Image javaComImage;
  private boolean preloadedAll = false;
  private boolean errorDuringPreloading = false;
  private Image backbuffer = null;
  private float loadingProgress;
  private float spinnerProgress;
  private static final int NUM_SPINNER_STOPS = 16;
  private static final int SPINNER_R = 84;
  private static final int SPINNER_G = 130;
  private static final int SPINNER_B = 161;
  private long startTime;
  private long initialStartTime;
  private int currentState = 1;
  private float stateProgress;
  private boolean showLogoAndText;
  private float zippyProgress;
  private float zippyStartProgress;
  private long zippyStartTime;
  private boolean fadeAway;
  private boolean animationThreadRunning = false;

  public AWTAnimationPanel2()
  {
    UIUtil.disableBackgroundErase(this);
    setBoxBGColor(Color.WHITE);
  }

  public void setBoxBGColor(Color paramColor)
  {
    setBackground(paramColor);
    this.bgColor = paramColor;
  }

  public void setBoxFGColor(Color paramColor)
  {
    this.fgColor = paramColor;
  }

  public void startAnimation()
  {
    synchronized (this)
    {
      if (this.animationThreadRunning)
        return;
      this.animationThreadRunning = true;
    }
    ??? = new Thread(this);
    ((Thread)???).setDaemon(true);
    ((Thread)???).start();
  }

  public void stopAnimation()
  {
    synchronized (this)
    {
      this.animationThreadRunning = false;
    }
  }

  public float getProgressValue()
  {
    return this.loadingProgress;
  }

  public void setProgressValue(float paramFloat)
  {
    this.zippyStartTime = 0L;
    this.zippyStartProgress = this.zippyProgress;
    this.loadingProgress = paramFloat;
  }

  public void fadeAway()
  {
    if ((this.currentState == 2) && (!this.fadeAway))
    {
      setProgressValue(1.0F);
      this.fadeAway = true;
    }
  }

  private Image loadImage(Toolkit paramToolkit, String paramString)
  {
    Image localImage = null;
    try
    {
      localImage = paramToolkit.createImage(getClass().getResource(paramString));
    }
    catch (Throwable localThrowable)
    {
      Trace.println("AWTAnimationPanel.loadImage, cannot create image, caught: " + localThrowable);
      Trace.printException(localThrowable);
    }
    return localImage;
  }

  private void preloadResources()
  {
    if ((this.preloadedAll) || (this.errorDuringPreloading))
      return;
    Toolkit localToolkit = Toolkit.getDefaultToolkit();
    MediaTracker localMediaTracker = new MediaTracker(this);
    this.javaLogoImage = loadImage(localToolkit, "JavaCupLogo-161.png");
    localMediaTracker.addImage(this.javaLogoImage, 0);
    this.javaComImage = loadImage(localToolkit, "javacom300.png");
    localMediaTracker.addImage(this.javaComImage, 1);
    try
    {
      localMediaTracker.waitForAll();
    }
    catch (InterruptedException localInterruptedException)
    {
      this.errorDuringPreloading = true;
      return;
    }
    this.preloadedAll = true;
  }

  private void allocateBackBuffer(int paramInt1, int paramInt2)
  {
    if ((this.backbuffer != null) && ((this.backbuffer.getWidth(null) != paramInt1) || (this.backbuffer.getHeight(null) != paramInt2)))
    {
      this.backbuffer.flush();
      this.backbuffer = null;
    }
    if (this.backbuffer == null)
    {
      this.backbuffer = createImage(paramInt1, paramInt2);
      if (this.backbuffer == null)
        this.backbuffer = new BufferedImage(paramInt1, paramInt2, 2);
    }
  }

  private Dimension getImageBoundsWithinSize(int paramInt1, int paramInt2, int paramInt3, float paramFloat)
  {
    float f = paramFloat * paramInt3 / (float)Math.sqrt(paramInt1 * paramInt1 + paramInt2 * paramInt2);
    return new Dimension((int)(paramInt1 * f), (int)(paramInt2 * f));
  }

  private static float bias(float paramFloat1, float paramFloat2)
  {
    return paramFloat2 + paramFloat1 * (1.0F - paramFloat2);
  }

  private void renderSpinner(Graphics2D paramGraphics2D, int paramInt1, int paramInt2, int paramInt3)
  {
    paramGraphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    int i = (int)Math.max(1.0F, 0.04F * paramInt3);
    int j = (int)Math.max(1.0F, 0.05F * paramInt3);
    int k = (int)Math.max(1.0F, 0.35F * paramInt3);
    int m = paramInt1 / 2;
    int n = paramInt2 / 2;
    AffineTransform localAffineTransform = paramGraphics2D.getTransform();
    paramGraphics2D.translate(m, n - 0.05F * paramInt3);
    if (this.showLogoAndText)
    {
      Dimension localDimension = getImageBoundsWithinSize(this.javaLogoImage.getWidth(null), this.javaLogoImage.getHeight(null), paramInt3, 0.6F);
      paramGraphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
      paramGraphics2D.drawImage(this.javaLogoImage, -localDimension.width / 2, -localDimension.height / 2, localDimension.width, localDimension.height, null);
    }
    double d = 0.3926990816987241D;
    int i1 = this.showLogoAndText ? 1 : 5;
    int[] arrayOfInt1 = { -j, j, i, -i };
    int[] arrayOfInt2 = { -j, -j, i1 * i, i1 * i };
    paramGraphics2D.rotate(-d / 2.0D);
    paramGraphics2D.translate(0, -k);
    float f1 = 0.1F;
    float f2 = this.spinnerProgress;
    float f3 = 0.0625F;
    for (int i2 = 0; i2 < 16; i2++)
    {
      paramGraphics2D.translate(0, k);
      paramGraphics2D.rotate(-d);
      paramGraphics2D.translate(0, -k);
      float f4 = i2 * f3 + f2;
      float f5 = f4 + f3;
      int i3 = 0;
      while (f4 > 1.0F)
        f4 -= 1.0F;
      while (f5 > 1.0F)
        f5 -= 1.0F;
      if (f4 > f5)
        i3 = 1;
      if (i3 == 0)
      {
        GradientPaint localGradientPaint1 = new GradientPaint(-j, 0.0F, new Color(84, 130, 161, (int)(255.0F * bias(1.0F - f4, f1))), j, 0.0F, new Color(84, 130, 161, (int)(255.0F * bias(1.0F - f5, f1))));
        paramGraphics2D.setPaint(localGradientPaint1);
        paramGraphics2D.fillPolygon(arrayOfInt1, arrayOfInt2, arrayOfInt1.length);
      }
      else
      {
        float f6 = f5 / f3;
        int i4 = (int)(f6 * 2.0F * j);
        int i5 = (int)(f6 * 2.0F * i);
        GradientPaint localGradientPaint2 = new GradientPaint(-j + i4, 0.0F, new Color(84, 130, 161, (int)(255.0F * bias(0.0F, f1))), j, 0.0F, new Color(84, 130, 161, (int)(255.0F * bias(f5, f1))));
        paramGraphics2D.setPaint(localGradientPaint2);
        paramGraphics2D.fillPolygon(new int[] { -j + i4, j, i, -i + i5 }, arrayOfInt2, arrayOfInt2.length);
        localGradientPaint2 = new GradientPaint(-j, 0.0F, new Color(84, 130, 161, (int)(255.0F * bias(f4, f1))), -j + i4, 0.0F, new Color(84, 130, 161, (int)(255.0F * bias(1.0F, f1))));
        paramGraphics2D.setPaint(localGradientPaint2);
        paramGraphics2D.fillPolygon(new int[] { -j, -j + i4, -i + i5, -i }, arrayOfInt2, arrayOfInt2.length);
      }
    }
    paramGraphics2D.setTransform(localAffineTransform);
  }

  private void renderProgress(Graphics2D paramGraphics2D, int paramInt1, int paramInt2, int paramInt3)
  {
    int i = (int)(paramInt3 * 0.7F);
    int j = (int)Math.max(4.0F, paramInt3 * 0.05F);
    j = Math.min(8, j);
    int k = (paramInt1 - i) / 2;
    int m = (int)((paramInt2 + 0.9F * paramInt3) / 2.0F) - j;
    paramGraphics2D.setColor(this.fgColor);
    paramGraphics2D.drawRect(k, m, i, j);
    int n = (int)(i * this.zippyProgress);
    paramGraphics2D.fillRect(k + 2, m + 2, n - 3, j - 3);
  }

  private void renderJavaCom(Graphics2D paramGraphics2D, int paramInt1, int paramInt2, int paramInt3)
  {
    if (this.showLogoAndText)
    {
      int i = paramInt1 / 2;
      int j = paramInt2 / 2;
      AffineTransform localAffineTransform = paramGraphics2D.getTransform();
      paramGraphics2D.translate(i, j - 0.05F * paramInt3);
      Dimension localDimension = getImageBoundsWithinSize(this.javaComImage.getWidth(null), this.javaComImage.getHeight(null), paramInt3, 0.6F);
      paramGraphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
      paramGraphics2D.drawImage(this.javaComImage, -localDimension.width / 2, -localDimension.height / 2, localDimension.width, localDimension.height, null);
    }
  }

  private void renderState1(Graphics2D paramGraphics2D, int paramInt1, int paramInt2, int paramInt3)
  {
    Composite localComposite = paramGraphics2D.getComposite();
    paramGraphics2D.setComposite(AlphaComposite.getInstance(3, this.stateProgress));
    renderSpinner(paramGraphics2D, paramInt1, paramInt2, paramInt3);
    renderProgress(paramGraphics2D, paramInt1, paramInt2, paramInt3);
  }

  private void renderState2(Graphics2D paramGraphics2D, int paramInt1, int paramInt2, int paramInt3)
  {
    renderSpinner(paramGraphics2D, paramInt1, paramInt2, paramInt3);
    renderProgress(paramGraphics2D, paramInt1, paramInt2, paramInt3);
  }

  private void renderState3(Graphics2D paramGraphics2D, int paramInt1, int paramInt2, int paramInt3)
  {
    Composite localComposite = paramGraphics2D.getComposite();
    paramGraphics2D.setComposite(AlphaComposite.getInstance(3, 1.0F - this.stateProgress));
    renderSpinner(paramGraphics2D, paramInt1, paramInt2, paramInt3);
    renderProgress(paramGraphics2D, paramInt1, paramInt2, paramInt3);
    paramGraphics2D.setComposite(AlphaComposite.getInstance(3, this.stateProgress));
    renderJavaCom(paramGraphics2D, paramInt1, paramInt2, paramInt3);
    paramGraphics2D.setComposite(localComposite);
  }

  private void renderState4(Graphics2D paramGraphics2D, int paramInt1, int paramInt2, int paramInt3)
  {
    if (this.showLogoAndText)
      renderJavaCom(paramGraphics2D, paramInt1, paramInt2, paramInt3);
    else
      this.currentState += 1;
  }

  private void renderState5(Graphics2D paramGraphics2D, int paramInt1, int paramInt2, int paramInt3)
  {
    if (this.showLogoAndText)
    {
      Composite localComposite = paramGraphics2D.getComposite();
      paramGraphics2D.setComposite(AlphaComposite.getInstance(3, 1.0F - this.stateProgress));
      renderJavaCom(paramGraphics2D, paramInt1, paramInt2, paramInt3);
      paramGraphics2D.setComposite(localComposite);
    }
    else
    {
      this.currentState += 1;
    }
  }

  public void update(Graphics paramGraphics)
  {
    paint(paramGraphics);
  }

  public void paint(Graphics paramGraphics)
  {
    if (!this.animationThreadRunning)
      try
      {
        doPaint(paramGraphics);
      }
      catch (Exception localException)
      {
      }
  }

  public void doPaint(Graphics paramGraphics)
  {
    int i = getWidth();
    int j = getHeight();
    int k = getBoxSize(i, j);
    if (k <= 0)
    {
      paramGraphics.setColor(this.bgColor);
      paramGraphics.fillRect(0, 0, i, j);
      return;
    }
    preloadResources();
    if (!this.preloadedAll)
      return;
    allocateBackBuffer(i, j);
    if (k < 50)
      this.showLogoAndText = false;
    else
      this.showLogoAndText = true;
    Graphics2D localGraphics2D = (Graphics2D)this.backbuffer.getGraphics();
    localGraphics2D.setColor(this.bgColor);
    localGraphics2D.fillRect(0, 0, i, j);
    switch (this.currentState)
    {
    case 1:
      renderState1(localGraphics2D, i, j, k);
      break;
    case 2:
      renderState2(localGraphics2D, i, j, k);
      break;
    case 3:
      renderState3(localGraphics2D, i, j, k);
      break;
    case 4:
      renderState4(localGraphics2D, i, j, k);
      break;
    case 5:
      renderState5(localGraphics2D, i, j, k);
      break;
    }
    localGraphics2D.dispose();
    paramGraphics.drawImage(this.backbuffer, 0, 0, null);
  }

  private static int getBoxSize(int paramInt1, int paramInt2)
  {
    int i = paramInt1 > paramInt2 ? paramInt2 : paramInt1;
    if (i < 25)
      return 0;
    if (i < 200)
      return (int)(0.75F * i);
    if (i < 300)
      return 150;
    if (i > 600)
      return 300;
    return i / 2;
  }

  private static float convertToNonLinear(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    float f1 = 1.0F / (1.0F - paramFloat2 / 2.0F - paramFloat3 / 2.0F);
    float f2;
    if (paramFloat1 < paramFloat2)
    {
      f2 = f1 * (paramFloat1 / paramFloat2) / 2.0F;
      paramFloat1 *= f2;
    }
    else if (paramFloat1 > 1.0F - paramFloat3)
    {
      f2 = paramFloat1 - (1.0F - paramFloat3);
      float f3 = f2 / paramFloat3;
      paramFloat1 = f1 * (1.0F - paramFloat2 / 2.0F - paramFloat3 + f2 * (2.0F - f3) / 2.0F);
    }
    else
    {
      paramFloat1 = f1 * (paramFloat1 - paramFloat2 / 2.0F);
    }
    return paramFloat1;
  }

  public void run()
  {
    while (true)
    {
      try
      {
        Thread.sleep(20L);
      }
      catch (Exception localException)
      {
      }
      repaint();
      synchronized (this)
      {
        if (!this.animationThreadRunning)
          break;
      }
      if (this.startTime == 0L)
        this.startTime = System.currentTimeMillis();
      long l1 = System.currentTimeMillis();
      if (isShowing())
        try
        {
          doPaint(getGraphics());
        }
        catch (RuntimeException localRuntimeException)
        {
          Trace.ignoredException(localRuntimeException);
        }
      long l2 = l1 - this.startTime;
      if (this.initialStartTime == 0L)
        this.initialStartTime = this.startTime;
      long l3 = l1 - this.initialStartTime;
      if (this.currentState < stateStops.length)
        if (l2 >= stateStops[this.currentState])
        {
          this.startTime = l1;
          this.stateProgress = 0.0F;
          l2 = 0L;
          this.currentState += 1;
        }
        else
        {
          this.stateProgress = ((float)l2 / (float)stateStops[this.currentState]);
        }
      if (this.currentState < 6)
      {
        this.spinnerProgress = ((float)(l3 % 2000L) / 2000.0F);
        if (this.zippyStartTime == 0L)
          this.zippyStartTime = l1;
        long l4 = l1 - this.zippyStartTime;
        if (l4 > 300L)
        {
          this.zippyProgress = this.loadingProgress;
        }
        else
        {
          float f = (float)l4 / 300.0F;
          f = convertToNonLinear(f, 0.5F, 0.1F);
          this.zippyProgress = (this.zippyStartProgress + (this.loadingProgress - this.zippyStartProgress) * f);
        }
        if ((this.fadeAway) && (this.zippyProgress >= 1.0F))
        {
          this.fadeAway = false;
          this.loadingProgress = (this.zippyProgress = 1.0F);
          this.startTime = l1;
          this.currentState += 1;
        }
      }
      else
      {
        synchronized (this)
        {
          this.animationThreadRunning = false;
        }
      }
    }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     com.sun.deploy.uitoolkit.impl.awt.AWTAnimationPanel2
 * JD-Core Version:    0.6.2
 */