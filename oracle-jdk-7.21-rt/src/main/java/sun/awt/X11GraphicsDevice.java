/*     */ package sun.awt;
/*     */ 
/*     */ import java.awt.AWTPermission;
/*     */ import java.awt.DisplayMode;
/*     */ import java.awt.GraphicsConfiguration;
/*     */ import java.awt.GraphicsDevice;
/*     */ import java.awt.GraphicsEnvironment;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.Window;
/*     */ import java.io.PrintStream;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import sun.java2d.loops.SurfaceType;
/*     */ import sun.java2d.opengl.GLXGraphicsConfig;
/*     */ import sun.java2d.xr.XRGraphicsConfig;
/*     */ 
/*     */ public class X11GraphicsDevice extends GraphicsDevice
/*     */   implements DisplayChangedListener
/*     */ {
/*     */   int screen;
/*  57 */   HashMap x11ProxyKeyMap = new HashMap();
/*     */   private static AWTPermission fullScreenExclusivePermission;
/*     */   private static Boolean xrandrExtSupported;
/*  61 */   private final Object configLock = new Object();
/*  62 */   private SunDisplayChanger topLevels = new SunDisplayChanger();
/*     */   private DisplayMode origDisplayMode;
/*     */   private boolean shutdownHookRegistered;
/*     */   GraphicsConfiguration[] configs;
/*     */   GraphicsConfiguration defaultConfig;
/*     */   HashSet doubleBufferVisuals;
/*     */ 
/*     */   public X11GraphicsDevice(int paramInt)
/*     */   {
/*  67 */     this.screen = paramInt;
/*     */   }
/*     */ 
/*     */   private static native void initIDs();
/*     */ 
/*     */   public int getScreen()
/*     */   {
/*  86 */     return this.screen;
/*     */   }
/*     */ 
/*     */   public Object getProxyKeyFor(SurfaceType paramSurfaceType) {
/*  90 */     synchronized (this.x11ProxyKeyMap) {
/*  91 */       Object localObject1 = this.x11ProxyKeyMap.get(paramSurfaceType);
/*  92 */       if (localObject1 == null) {
/*  93 */         localObject1 = new Object();
/*  94 */         this.x11ProxyKeyMap.put(paramSurfaceType, localObject1);
/*     */       }
/*  96 */       return localObject1;
/*     */     }
/*     */   }
/*     */ 
/*     */   public native long getDisplay();
/*     */ 
/*     */   public int getType()
/*     */   {
/* 114 */     return 0;
/*     */   }
/*     */ 
/*     */   public String getIDstring()
/*     */   {
/* 122 */     return ":0." + this.screen;
/*     */   }
/*     */ 
/*     */   public GraphicsConfiguration[] getConfigurations()
/*     */   {
/* 135 */     if (this.configs == null) {
/* 136 */       synchronized (this.configLock) {
/* 137 */         makeConfigurations();
/*     */       }
/*     */     }
/* 140 */     return (GraphicsConfiguration[])this.configs.clone();
/*     */   }
/*     */ 
/*     */   private void makeConfigurations() {
/* 144 */     if (this.configs == null) {
/* 145 */       int i = 1;
/* 146 */       int j = getNumConfigs(this.screen);
/* 147 */       GraphicsConfiguration[] arrayOfGraphicsConfiguration = new GraphicsConfiguration[j];
/* 148 */       if (this.defaultConfig == null) {
/* 149 */         arrayOfGraphicsConfiguration[0] = getDefaultConfiguration();
/*     */       }
/*     */       else {
/* 152 */         arrayOfGraphicsConfiguration[0] = this.defaultConfig;
/*     */       }
/*     */ 
/* 155 */       boolean bool1 = X11GraphicsEnvironment.isGLXAvailable();
/* 156 */       boolean bool2 = X11GraphicsEnvironment.isXRenderAvailable();
/*     */ 
/* 158 */       boolean bool3 = isDBESupported();
/* 159 */       if ((bool3) && (this.doubleBufferVisuals == null)) {
/* 160 */         this.doubleBufferVisuals = new HashSet();
/* 161 */         getDoubleBufferVisuals(this.screen);
/*     */       }
/* 163 */       for (; i < j; i++) {
/* 164 */         int k = getConfigVisualId(i, this.screen);
/* 165 */         int m = getConfigDepth(i, this.screen);
/* 166 */         if (bool1) {
/* 167 */           arrayOfGraphicsConfiguration[i] = GLXGraphicsConfig.getConfig(this, k);
/*     */         }
/* 169 */         if (arrayOfGraphicsConfiguration[i] == null) {
/* 170 */           boolean bool4 = (bool3) && (this.doubleBufferVisuals.contains(Integer.valueOf(k)));
/*     */ 
/* 174 */           if (bool2) {
/* 175 */             arrayOfGraphicsConfiguration[i] = XRGraphicsConfig.getConfig(this, k, m, getConfigColormap(i, this.screen), bool4);
/*     */           }
/*     */           else {
/* 178 */             arrayOfGraphicsConfiguration[i] = X11GraphicsConfig.getConfig(this, k, m, getConfigColormap(i, this.screen), bool4);
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 184 */       this.configs = arrayOfGraphicsConfiguration;
/*     */     }
/*     */   }
/*     */ 
/*     */   public native int getNumConfigs(int paramInt);
/*     */ 
/*     */   public native int getConfigVisualId(int paramInt1, int paramInt2);
/*     */ 
/*     */   public native int getConfigDepth(int paramInt1, int paramInt2);
/*     */ 
/*     */   public native int getConfigColormap(int paramInt1, int paramInt2);
/*     */ 
/*     */   public static native boolean isDBESupported();
/*     */ 
/*     */   private void addDoubleBufferVisual(int paramInt)
/*     */   {
/* 213 */     this.doubleBufferVisuals.add(Integer.valueOf(paramInt));
/*     */   }
/*     */ 
/*     */   private native void getDoubleBufferVisuals(int paramInt);
/*     */ 
/*     */   public GraphicsConfiguration getDefaultConfiguration()
/*     */   {
/* 223 */     if (this.defaultConfig == null) {
/* 224 */       synchronized (this.configLock) {
/* 225 */         makeDefaultConfiguration();
/*     */       }
/*     */     }
/* 228 */     return this.defaultConfig;
/*     */   }
/*     */ 
/*     */   private void makeDefaultConfiguration() {
/* 232 */     if (this.defaultConfig == null) {
/* 233 */       int i = getConfigVisualId(0, this.screen);
/* 234 */       if (X11GraphicsEnvironment.isGLXAvailable()) {
/* 235 */         this.defaultConfig = GLXGraphicsConfig.getConfig(this, i);
/* 236 */         if (X11GraphicsEnvironment.isGLXVerbose()) {
/* 237 */           if (this.defaultConfig != null)
/* 238 */             System.out.print("OpenGL pipeline enabled");
/*     */           else {
/* 240 */             System.out.print("Could not enable OpenGL pipeline");
/*     */           }
/* 242 */           System.out.println(" for default config on screen " + this.screen);
/*     */         }
/*     */       }
/*     */ 
/* 246 */       if (this.defaultConfig == null) {
/* 247 */         int j = getConfigDepth(0, this.screen);
/* 248 */         boolean bool = false;
/* 249 */         if ((isDBESupported()) && (this.doubleBufferVisuals == null)) {
/* 250 */           this.doubleBufferVisuals = new HashSet();
/* 251 */           getDoubleBufferVisuals(this.screen);
/* 252 */           bool = this.doubleBufferVisuals.contains(Integer.valueOf(i));
/*     */         }
/*     */ 
/* 256 */         if (X11GraphicsEnvironment.isXRenderAvailable()) {
/* 257 */           if (X11GraphicsEnvironment.isXRenderVerbose()) {
/* 258 */             System.out.println("XRender pipeline enabled");
/*     */           }
/* 260 */           this.defaultConfig = XRGraphicsConfig.getConfig(this, i, j, getConfigColormap(0, this.screen), bool);
/*     */         }
/*     */         else
/*     */         {
/* 264 */           this.defaultConfig = X11GraphicsConfig.getConfig(this, i, j, getConfigColormap(0, this.screen), bool);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static native void enterFullScreenExclusive(long paramLong);
/*     */ 
/*     */   private static native void exitFullScreenExclusive(long paramLong);
/*     */ 
/*     */   private static native boolean initXrandrExtension();
/*     */ 
/*     */   private static native DisplayMode getCurrentDisplayMode(int paramInt);
/*     */ 
/*     */   private static native void enumDisplayModes(int paramInt, ArrayList<DisplayMode> paramArrayList);
/*     */ 
/*     */   private static native void configDisplayMode(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
/*     */ 
/*     */   private static native void resetNativeData(int paramInt);
/*     */ 
/*     */   private static synchronized boolean isXrandrExtensionSupported()
/*     */   {
/* 290 */     if (xrandrExtSupported == null) {
/* 291 */       xrandrExtSupported = Boolean.valueOf(initXrandrExtension());
/*     */     }
/*     */ 
/* 294 */     return xrandrExtSupported.booleanValue();
/*     */   }
/*     */ 
/*     */   public boolean isFullScreenSupported()
/*     */   {
/* 303 */     boolean bool = (this.screen == 0) && (isXrandrExtensionSupported());
/* 304 */     if (bool) {
/* 305 */       SecurityManager localSecurityManager = System.getSecurityManager();
/* 306 */       if (localSecurityManager != null) {
/* 307 */         if (fullScreenExclusivePermission == null) {
/* 308 */           fullScreenExclusivePermission = new AWTPermission("fullScreenExclusive");
/*     */         }
/*     */         try
/*     */         {
/* 312 */           localSecurityManager.checkPermission(fullScreenExclusivePermission);
/*     */         } catch (SecurityException localSecurityException) {
/* 314 */           return false;
/*     */         }
/*     */       }
/*     */     }
/* 318 */     return bool;
/*     */   }
/*     */ 
/*     */   public boolean isDisplayChangeSupported()
/*     */   {
/* 323 */     return (isFullScreenSupported()) && (getFullScreenWindow() != null);
/*     */   }
/*     */ 
/*     */   private static void enterFullScreenExclusive(Window paramWindow) {
/* 327 */     X11ComponentPeer localX11ComponentPeer = (X11ComponentPeer)paramWindow.getPeer();
/* 328 */     if (localX11ComponentPeer != null) {
/* 329 */       enterFullScreenExclusive(localX11ComponentPeer.getContentWindow());
/* 330 */       localX11ComponentPeer.setFullScreenExclusiveModeState(true);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void exitFullScreenExclusive(Window paramWindow) {
/* 335 */     X11ComponentPeer localX11ComponentPeer = (X11ComponentPeer)paramWindow.getPeer();
/* 336 */     if (localX11ComponentPeer != null) {
/* 337 */       localX11ComponentPeer.setFullScreenExclusiveModeState(false);
/* 338 */       exitFullScreenExclusive(localX11ComponentPeer.getContentWindow());
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void setFullScreenWindow(Window paramWindow)
/*     */   {
/* 344 */     Window localWindow = getFullScreenWindow();
/* 345 */     if (paramWindow == localWindow) {
/* 346 */       return;
/*     */     }
/*     */ 
/* 349 */     boolean bool = isFullScreenSupported();
/* 350 */     if ((bool) && (localWindow != null))
/*     */     {
/* 352 */       exitFullScreenExclusive(localWindow);
/* 353 */       setDisplayMode(this.origDisplayMode);
/*     */     }
/*     */ 
/* 356 */     super.setFullScreenWindow(paramWindow);
/*     */ 
/* 358 */     if ((bool) && (paramWindow != null))
/*     */     {
/* 360 */       if (this.origDisplayMode == null) {
/* 361 */         this.origDisplayMode = getDisplayMode();
/*     */       }
/*     */ 
/* 365 */       enterFullScreenExclusive(paramWindow);
/*     */     }
/*     */   }
/*     */ 
/*     */   private DisplayMode getDefaultDisplayMode() {
/* 370 */     GraphicsConfiguration localGraphicsConfiguration = getDefaultConfiguration();
/* 371 */     Rectangle localRectangle = localGraphicsConfiguration.getBounds();
/* 372 */     return new DisplayMode(localRectangle.width, localRectangle.height, -1, 0);
/*     */   }
/*     */ 
/*     */   public synchronized DisplayMode getDisplayMode()
/*     */   {
/* 379 */     if (isFullScreenSupported()) {
/* 380 */       return getCurrentDisplayMode(this.screen);
/*     */     }
/* 382 */     if (this.origDisplayMode == null) {
/* 383 */       this.origDisplayMode = getDefaultDisplayMode();
/*     */     }
/* 385 */     return this.origDisplayMode;
/*     */   }
/*     */ 
/*     */   public synchronized DisplayMode[] getDisplayModes()
/*     */   {
/* 391 */     if (!isFullScreenSupported()) {
/* 392 */       return super.getDisplayModes();
/*     */     }
/* 394 */     ArrayList localArrayList = new ArrayList();
/* 395 */     enumDisplayModes(this.screen, localArrayList);
/* 396 */     DisplayMode[] arrayOfDisplayMode = new DisplayMode[localArrayList.size()];
/* 397 */     return (DisplayMode[])localArrayList.toArray(arrayOfDisplayMode);
/*     */   }
/*     */ 
/*     */   public synchronized void setDisplayMode(DisplayMode paramDisplayMode)
/*     */   {
/* 402 */     if (!isDisplayChangeSupported()) {
/* 403 */       super.setDisplayMode(paramDisplayMode);
/* 404 */       return;
/*     */     }
/* 406 */     Window localWindow = getFullScreenWindow();
/* 407 */     if (localWindow == null) {
/* 408 */       throw new IllegalStateException("Must be in fullscreen mode in order to set display mode");
/*     */     }
/*     */ 
/* 411 */     if (getDisplayMode().equals(paramDisplayMode)) {
/* 412 */       return;
/*     */     }
/* 414 */     if ((paramDisplayMode == null) || ((paramDisplayMode = getMatchingDisplayMode(paramDisplayMode)) == null))
/*     */     {
/* 417 */       throw new IllegalArgumentException("Invalid display mode");
/*     */     }
/*     */ 
/* 420 */     if (!this.shutdownHookRegistered)
/*     */     {
/* 425 */       this.shutdownHookRegistered = true;
/* 426 */       PrivilegedAction local1 = new PrivilegedAction() {
/*     */         public Void run() {
/* 428 */           Object localObject = Thread.currentThread().getThreadGroup();
/* 429 */           ThreadGroup localThreadGroup = ((ThreadGroup)localObject).getParent();
/* 430 */           while (localThreadGroup != null) {
/* 431 */             localObject = localThreadGroup;
/* 432 */             localThreadGroup = ((ThreadGroup)localObject).getParent();
/*     */           }
/* 434 */           Runnable local1 = new Runnable() {
/*     */             public void run() {
/* 436 */               Window localWindow = X11GraphicsDevice.this.getFullScreenWindow();
/* 437 */               if (localWindow != null) {
/* 438 */                 X11GraphicsDevice.exitFullScreenExclusive(localWindow);
/* 439 */                 X11GraphicsDevice.this.setDisplayMode(X11GraphicsDevice.this.origDisplayMode);
/*     */               }
/*     */             }
/*     */           };
/* 443 */           Thread localThread = new Thread((ThreadGroup)localObject, local1, "Display-Change-Shutdown-Thread-" + X11GraphicsDevice.this.screen);
/* 444 */           localThread.setContextClassLoader(null);
/* 445 */           Runtime.getRuntime().addShutdownHook(localThread);
/* 446 */           return null;
/*     */         }
/*     */       };
/* 449 */       AccessController.doPrivileged(local1);
/*     */     }
/*     */ 
/* 453 */     configDisplayMode(this.screen, paramDisplayMode.getWidth(), paramDisplayMode.getHeight(), paramDisplayMode.getRefreshRate());
/*     */ 
/* 458 */     localWindow.setBounds(0, 0, paramDisplayMode.getWidth(), paramDisplayMode.getHeight());
/*     */ 
/* 463 */     ((X11GraphicsEnvironment)GraphicsEnvironment.getLocalGraphicsEnvironment()).displayChanged();
/*     */   }
/*     */ 
/*     */   private synchronized DisplayMode getMatchingDisplayMode(DisplayMode paramDisplayMode)
/*     */   {
/* 468 */     if (!isDisplayChangeSupported()) {
/* 469 */       return null;
/*     */     }
/* 471 */     DisplayMode[] arrayOfDisplayMode1 = getDisplayModes();
/* 472 */     for (DisplayMode localDisplayMode : arrayOfDisplayMode1) {
/* 473 */       if ((paramDisplayMode.equals(localDisplayMode)) || ((paramDisplayMode.getRefreshRate() == 0) && (paramDisplayMode.getWidth() == localDisplayMode.getWidth()) && (paramDisplayMode.getHeight() == localDisplayMode.getHeight()) && (paramDisplayMode.getBitDepth() == localDisplayMode.getBitDepth())))
/*     */       {
/* 479 */         return localDisplayMode;
/*     */       }
/*     */     }
/* 482 */     return null;
/*     */   }
/*     */ 
/*     */   public synchronized void displayChanged()
/*     */   {
/* 491 */     this.defaultConfig = null;
/* 492 */     this.configs = null;
/* 493 */     this.doubleBufferVisuals = null;
/*     */ 
/* 497 */     resetNativeData(this.screen);
/*     */ 
/* 500 */     this.topLevels.notifyListeners();
/*     */   }
/*     */ 
/*     */   public void paletteChanged()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void addDisplayChangedListener(DisplayChangedListener paramDisplayChangedListener)
/*     */   {
/* 516 */     this.topLevels.add(paramDisplayChangedListener);
/*     */   }
/*     */ 
/*     */   public void removeDisplayChangedListener(DisplayChangedListener paramDisplayChangedListener)
/*     */   {
/* 523 */     this.topLevels.remove(paramDisplayChangedListener);
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 527 */     return "X11GraphicsDevice[screen=" + this.screen + "]";
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  77 */     if (!GraphicsEnvironment.isHeadless())
/*  78 */       initIDs();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11GraphicsDevice
 * JD-Core Version:    0.6.2
 */