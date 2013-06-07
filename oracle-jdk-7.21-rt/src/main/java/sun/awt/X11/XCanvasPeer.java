/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.GraphicsConfiguration;
/*     */ import java.awt.GraphicsDevice;
/*     */ import java.awt.peer.CanvasPeer;
/*     */ import sun.awt.SunToolkit;
/*     */ import sun.awt.X11GraphicsConfig;
/*     */ import sun.awt.X11GraphicsDevice;
/*     */ 
/*     */ class XCanvasPeer extends XComponentPeer
/*     */   implements CanvasPeer
/*     */ {
/*     */   private boolean eraseBackgroundDisabled;
/*     */ 
/*     */   XCanvasPeer()
/*     */   {
/*     */   }
/*     */ 
/*     */   XCanvasPeer(XCreateWindowParams paramXCreateWindowParams)
/*     */   {
/*  42 */     super(paramXCreateWindowParams);
/*     */   }
/*     */ 
/*     */   XCanvasPeer(Component paramComponent) {
/*  46 */     super(paramComponent);
/*     */   }
/*     */ 
/*     */   void preInit(XCreateWindowParams paramXCreateWindowParams) {
/*  50 */     super.preInit(paramXCreateWindowParams);
/*  51 */     if (SunToolkit.getSunAwtNoerasebackground())
/*  52 */       disableBackgroundErase();
/*     */   }
/*     */ 
/*     */   public GraphicsConfiguration getAppropriateGraphicsConfiguration(GraphicsConfiguration paramGraphicsConfiguration)
/*     */   {
/*  62 */     if ((this.graphicsConfig == null) || (paramGraphicsConfiguration == null)) {
/*  63 */       return paramGraphicsConfiguration;
/*     */     }
/*     */ 
/*  67 */     int i = ((X11GraphicsDevice)paramGraphicsConfiguration.getDevice()).getScreen();
/*     */ 
/*  71 */     int j = this.graphicsConfig.getVisual();
/*     */ 
/*  73 */     X11GraphicsDevice localX11GraphicsDevice = (X11GraphicsDevice)java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[i];
/*     */ 
/*  77 */     for (int k = 0; k < localX11GraphicsDevice.getNumConfigs(i); k++) {
/*  78 */       if (j == localX11GraphicsDevice.getConfigVisualId(k, i))
/*     */       {
/*  80 */         this.graphicsConfig = ((X11GraphicsConfig)localX11GraphicsDevice.getConfigurations()[k]);
/*  81 */         break;
/*     */       }
/*     */     }
/*     */ 
/*  85 */     if (this.graphicsConfig == null) {
/*  86 */       this.graphicsConfig = ((X11GraphicsConfig)java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[i].getDefaultConfiguration());
/*     */     }
/*     */ 
/*  92 */     return this.graphicsConfig;
/*     */   }
/*     */ 
/*     */   protected boolean shouldFocusOnClick()
/*     */   {
/*  97 */     return true;
/*     */   }
/*     */ 
/*     */   public void disableBackgroundErase() {
/* 101 */     this.eraseBackgroundDisabled = true;
/*     */   }
/*     */   protected boolean doEraseBackground() {
/* 104 */     return !this.eraseBackgroundDisabled;
/*     */   }
/*     */   public void setBackground(Color paramColor) {
/* 107 */     int i = 0;
/* 108 */     if ((getPeerBackground() == null) || (!getPeerBackground().equals(paramColor)))
/*     */     {
/* 110 */       i = 1;
/*     */     }
/* 112 */     super.setBackground(paramColor);
/* 113 */     if (i != 0)
/* 114 */       this.target.repaint();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XCanvasPeer
 * JD-Core Version:    0.6.2
 */