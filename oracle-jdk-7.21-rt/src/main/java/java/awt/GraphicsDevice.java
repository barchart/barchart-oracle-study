/*     */ package java.awt;
/*     */ 
/*     */ import java.awt.image.ColorModel;
/*     */ import sun.awt.AppContext;
/*     */ import sun.awt.SunToolkit;
/*     */ 
/*     */ public abstract class GraphicsDevice
/*     */ {
/*     */   private Window fullScreenWindow;
/*     */   private AppContext fullScreenAppContext;
/*  85 */   private final Object fsAppContextLock = new Object();
/*     */   private Rectangle windowedModeBounds;
/*     */   public static final int TYPE_RASTER_SCREEN = 0;
/*     */   public static final int TYPE_PRINTER = 1;
/*     */   public static final int TYPE_IMAGE_BUFFER = 2;
/*     */ 
/*     */   public abstract int getType();
/*     */ 
/*     */   public abstract String getIDstring();
/*     */ 
/*     */   public abstract GraphicsConfiguration[] getConfigurations();
/*     */ 
/*     */   public abstract GraphicsConfiguration getDefaultConfiguration();
/*     */ 
/*     */   public GraphicsConfiguration getBestConfiguration(GraphicsConfigTemplate paramGraphicsConfigTemplate)
/*     */   {
/* 206 */     GraphicsConfiguration[] arrayOfGraphicsConfiguration = getConfigurations();
/* 207 */     return paramGraphicsConfigTemplate.getBestConfiguration(arrayOfGraphicsConfiguration);
/*     */   }
/*     */ 
/*     */   public boolean isFullScreenSupported()
/*     */   {
/* 224 */     return false;
/*     */   }
/*     */ 
/*     */   public void setFullScreenWindow(Window paramWindow)
/*     */   {
/* 286 */     if (paramWindow != null) {
/* 287 */       if (paramWindow.getShape() != null) {
/* 288 */         paramWindow.setShape(null);
/*     */       }
/* 290 */       if (paramWindow.getOpacity() < 1.0F) {
/* 291 */         paramWindow.setOpacity(1.0F);
/*     */       }
/* 293 */       if (!paramWindow.isOpaque()) {
/* 294 */         Color localColor = paramWindow.getBackground();
/* 295 */         localColor = new Color(localColor.getRed(), localColor.getGreen(), localColor.getBlue(), 255);
/*     */ 
/* 297 */         paramWindow.setBackground(localColor);
/*     */       }
/*     */     }
/* 300 */     if ((this.fullScreenWindow != null) && (this.windowedModeBounds != null))
/*     */     {
/* 303 */       if (this.windowedModeBounds.width == 0) this.windowedModeBounds.width = 1;
/* 304 */       if (this.windowedModeBounds.height == 0) this.windowedModeBounds.height = 1;
/* 305 */       this.fullScreenWindow.setBounds(this.windowedModeBounds);
/*     */     }
/*     */ 
/* 308 */     synchronized (this.fsAppContextLock)
/*     */     {
/* 310 */       if (paramWindow == null)
/* 311 */         this.fullScreenAppContext = null;
/*     */       else {
/* 313 */         this.fullScreenAppContext = AppContext.getAppContext();
/*     */       }
/* 315 */       this.fullScreenWindow = paramWindow;
/*     */     }
/* 317 */     if (this.fullScreenWindow != null) {
/* 318 */       this.windowedModeBounds = this.fullScreenWindow.getBounds();
/*     */ 
/* 322 */       ??? = getDefaultConfiguration().getBounds();
/* 323 */       this.fullScreenWindow.setBounds(((Rectangle)???).x, ((Rectangle)???).y, ((Rectangle)???).width, ((Rectangle)???).height);
/*     */ 
/* 325 */       this.fullScreenWindow.setVisible(true);
/* 326 */       this.fullScreenWindow.toFront();
/*     */     }
/*     */   }
/*     */ 
/*     */   public Window getFullScreenWindow()
/*     */   {
/* 340 */     Window localWindow = null;
/* 341 */     synchronized (this.fsAppContextLock)
/*     */     {
/* 344 */       if (this.fullScreenAppContext == AppContext.getAppContext()) {
/* 345 */         localWindow = this.fullScreenWindow;
/*     */       }
/*     */     }
/* 348 */     return localWindow;
/*     */   }
/*     */ 
/*     */   public boolean isDisplayChangeSupported()
/*     */   {
/* 366 */     return false;
/*     */   }
/*     */ 
/*     */   public void setDisplayMode(DisplayMode paramDisplayMode)
/*     */   {
/* 421 */     throw new UnsupportedOperationException("Cannot change display mode");
/*     */   }
/*     */ 
/*     */   public DisplayMode getDisplayMode()
/*     */   {
/* 437 */     GraphicsConfiguration localGraphicsConfiguration = getDefaultConfiguration();
/* 438 */     Rectangle localRectangle = localGraphicsConfiguration.getBounds();
/* 439 */     ColorModel localColorModel = localGraphicsConfiguration.getColorModel();
/* 440 */     return new DisplayMode(localRectangle.width, localRectangle.height, localColorModel.getPixelSize(), 0);
/*     */   }
/*     */ 
/*     */   public DisplayMode[] getDisplayModes()
/*     */   {
/* 455 */     return new DisplayMode[] { getDisplayMode() };
/*     */   }
/*     */ 
/*     */   public int getAvailableAcceleratedMemory()
/*     */   {
/* 487 */     return -1;
/*     */   }
/*     */ 
/*     */   public boolean isWindowTranslucencySupported(WindowTranslucency paramWindowTranslucency)
/*     */   {
/* 500 */     switch (1.$SwitchMap$java$awt$GraphicsDevice$WindowTranslucency[paramWindowTranslucency.ordinal()]) {
/*     */     case 1:
/* 502 */       return isWindowShapingSupported();
/*     */     case 2:
/* 504 */       return isWindowOpacitySupported();
/*     */     case 3:
/* 506 */       return isWindowPerpixelTranslucencySupported();
/*     */     }
/* 508 */     return false;
/*     */   }
/*     */ 
/*     */   static boolean isWindowShapingSupported()
/*     */   {
/* 519 */     Toolkit localToolkit = Toolkit.getDefaultToolkit();
/* 520 */     if (!(localToolkit instanceof SunToolkit)) {
/* 521 */       return false;
/*     */     }
/* 523 */     return ((SunToolkit)localToolkit).isWindowShapingSupported();
/*     */   }
/*     */ 
/*     */   static boolean isWindowOpacitySupported()
/*     */   {
/* 534 */     Toolkit localToolkit = Toolkit.getDefaultToolkit();
/* 535 */     if (!(localToolkit instanceof SunToolkit)) {
/* 536 */       return false;
/*     */     }
/* 538 */     return ((SunToolkit)localToolkit).isWindowOpacitySupported();
/*     */   }
/*     */ 
/*     */   boolean isWindowPerpixelTranslucencySupported()
/*     */   {
/* 550 */     Toolkit localToolkit = Toolkit.getDefaultToolkit();
/* 551 */     if (!(localToolkit instanceof SunToolkit)) {
/* 552 */       return false;
/*     */     }
/* 554 */     if (!((SunToolkit)localToolkit).isWindowTranslucencySupported()) {
/* 555 */       return false;
/*     */     }
/*     */ 
/* 559 */     return getTranslucencyCapableGC() != null;
/*     */   }
/*     */ 
/*     */   GraphicsConfiguration getTranslucencyCapableGC()
/*     */   {
/* 566 */     GraphicsConfiguration localGraphicsConfiguration = getDefaultConfiguration();
/* 567 */     if (localGraphicsConfiguration.isTranslucencyCapable()) {
/* 568 */       return localGraphicsConfiguration;
/*     */     }
/*     */ 
/* 572 */     GraphicsConfiguration[] arrayOfGraphicsConfiguration = getConfigurations();
/* 573 */     for (int i = 0; i < arrayOfGraphicsConfiguration.length; i++) {
/* 574 */       if (arrayOfGraphicsConfiguration[i].isTranslucencyCapable()) {
/* 575 */         return arrayOfGraphicsConfiguration[i];
/*     */       }
/*     */     }
/*     */ 
/* 579 */     return null;
/*     */   }
/*     */ 
/*     */   public static enum WindowTranslucency
/*     */   {
/* 129 */     PERPIXEL_TRANSPARENT, 
/*     */ 
/* 135 */     TRANSLUCENT, 
/*     */ 
/* 141 */     PERPIXEL_TRANSLUCENT;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.awt.GraphicsDevice
 * JD-Core Version:    0.6.2
 */