/*     */ package sun.awt;
/*     */ 
/*     */ import java.awt.GraphicsDevice;
/*     */ import java.awt.GraphicsEnvironment;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.io.PrintStream;
/*     */ import java.net.InetAddress;
/*     */ import java.net.NetworkInterface;
/*     */ import java.net.SocketException;
/*     */ import java.net.UnknownHostException;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Enumeration;
/*     */ import sun.java2d.SunGraphicsEnvironment;
/*     */ import sun.java2d.SurfaceManagerFactory;
/*     */ import sun.java2d.UnixSurfaceManagerFactory;
/*     */ import sun.java2d.xr.XRSurfaceData;
/*     */ import sun.security.action.GetPropertyAction;
/*     */ import sun.util.logging.PlatformLogger;
/*     */ 
/*     */ public class X11GraphicsEnvironment extends SunGraphicsEnvironment
/*     */ {
/*  68 */   private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.X11GraphicsEnvironment");
/*  69 */   private static final PlatformLogger screenLog = PlatformLogger.getLogger("sun.awt.screen.X11GraphicsEnvironment");
/*     */   private static Boolean xinerState;
/*     */   private static boolean glxAvailable;
/*     */   private static boolean glxVerbose;
/*     */   private static boolean xRenderVerbose;
/*     */   private static boolean xRenderAvailable;
/*     */   private Boolean isDisplayLocal;
/*     */ 
/*     */   private static native boolean initGLX();
/*     */ 
/*     */   public static boolean isGLXAvailable()
/*     */   {
/* 152 */     return glxAvailable;
/*     */   }
/*     */ 
/*     */   public static boolean isGLXVerbose() {
/* 156 */     return glxVerbose;
/*     */   }
/*     */ 
/*     */   private static native boolean initXRender(boolean paramBoolean);
/*     */ 
/*     */   public static boolean isXRenderAvailable()
/*     */   {
/* 164 */     return xRenderAvailable;
/*     */   }
/*     */ 
/*     */   public static boolean isXRenderVerbose() {
/* 168 */     return xRenderVerbose;
/*     */   }
/*     */ 
/*     */   private static native int checkShmExt();
/*     */ 
/*     */   private static native String getDisplayString();
/*     */ 
/*     */   private static native void initDisplay(boolean paramBoolean);
/*     */ 
/*     */   protected native int getNumScreens();
/*     */ 
/*     */   protected GraphicsDevice makeScreenDevice(int paramInt)
/*     */   {
/* 195 */     return new X11GraphicsDevice(paramInt);
/*     */   }
/*     */ 
/*     */   protected native int getDefaultScreenNum();
/*     */ 
/*     */   public GraphicsDevice getDefaultScreenDevice()
/*     */   {
/* 203 */     return getScreenDevices()[getDefaultScreenNum()];
/*     */   }
/*     */ 
/*     */   public boolean isDisplayLocal() {
/* 207 */     if (this.isDisplayLocal == null) {
/* 208 */       SunToolkit.awtLock();
/*     */       try {
/* 210 */         if (this.isDisplayLocal == null)
/* 211 */           this.isDisplayLocal = Boolean.valueOf(_isDisplayLocal());
/*     */       }
/*     */       finally {
/* 214 */         SunToolkit.awtUnlock();
/*     */       }
/*     */     }
/* 217 */     return this.isDisplayLocal.booleanValue();
/*     */   }
/*     */ 
/*     */   private static boolean _isDisplayLocal() {
/* 221 */     if (isHeadless()) {
/* 222 */       return true;
/*     */     }
/*     */ 
/* 225 */     String str1 = (String)AccessController.doPrivileged(new GetPropertyAction("sun.java2d.remote"));
/*     */ 
/* 227 */     if (str1 != null) {
/* 228 */       return str1.equals("false");
/*     */     }
/*     */ 
/* 231 */     int i = checkShmExt();
/* 232 */     if (i != -1) {
/* 233 */       return i == 1;
/*     */     }
/*     */ 
/* 239 */     String str2 = getDisplayString();
/* 240 */     int j = str2.indexOf(':');
/* 241 */     String str3 = str2.substring(0, j);
/* 242 */     if (j <= 0)
/*     */     {
/* 244 */       return true;
/*     */     }
/*     */ 
/* 247 */     Boolean localBoolean = (Boolean)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Object run() {
/* 250 */         InetAddress[] arrayOfInetAddress = null;
/* 251 */         Enumeration localEnumeration1 = null;
/* 252 */         Enumeration localEnumeration2 = null;
/*     */         try {
/* 254 */           localEnumeration2 = NetworkInterface.getNetworkInterfaces();
/* 255 */           arrayOfInetAddress = InetAddress.getAllByName(this.val$hostName);
/* 256 */           if (arrayOfInetAddress == null)
/* 257 */             return Boolean.FALSE;
/*     */         }
/*     */         catch (UnknownHostException localUnknownHostException) {
/* 260 */           System.err.println("Unknown host: " + this.val$hostName);
/* 261 */           return Boolean.FALSE;
/*     */         } catch (SocketException localSocketException) {
/* 263 */           System.err.println(localSocketException.getMessage());
/* 264 */           return Boolean.FALSE;
/*     */         }
/*     */ 
/* 267 */         while (localEnumeration2.hasMoreElements()) {
/* 268 */           localEnumeration1 = ((NetworkInterface)localEnumeration2.nextElement()).getInetAddresses();
/* 269 */           while (localEnumeration1.hasMoreElements()) {
/* 270 */             for (int i = 0; i < arrayOfInetAddress.length; i++) {
/* 271 */               if (localEnumeration1.nextElement().equals(arrayOfInetAddress[i])) {
/* 272 */                 return Boolean.TRUE;
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/* 277 */         return Boolean.FALSE;
/*     */       }
/*     */     });
/* 279 */     return localBoolean.booleanValue();
/*     */   }
/*     */ 
/*     */   public String getDefaultFontFaceName()
/*     */   {
/* 291 */     return null;
/*     */   }
/*     */ 
/*     */   private static native boolean pRunningXinerama();
/*     */ 
/*     */   private static native Point getXineramaCenterPoint();
/*     */ 
/*     */   public Point getCenterPoint()
/*     */   {
/* 302 */     if (runningXinerama()) {
/* 303 */       Point localPoint = getXineramaCenterPoint();
/* 304 */       if (localPoint != null) {
/* 305 */         return localPoint;
/*     */       }
/*     */     }
/* 308 */     return super.getCenterPoint();
/*     */   }
/*     */ 
/*     */   public Rectangle getMaximumWindowBounds()
/*     */   {
/* 315 */     if (runningXinerama()) {
/* 316 */       return getXineramaWindowBounds();
/*     */     }
/* 318 */     return super.getMaximumWindowBounds();
/*     */   }
/*     */ 
/*     */   public boolean runningXinerama()
/*     */   {
/* 323 */     if (xinerState == null)
/*     */     {
/* 326 */       xinerState = Boolean.valueOf(pRunningXinerama());
/* 327 */       if (screenLog.isLoggable(400)) {
/* 328 */         screenLog.finer("Running Xinerama: " + xinerState);
/*     */       }
/*     */     }
/* 331 */     return xinerState.booleanValue();
/*     */   }
/*     */ 
/*     */   protected Rectangle getXineramaWindowBounds()
/*     */   {
/* 363 */     Point localPoint = getCenterPoint();
/*     */ 
/* 365 */     GraphicsDevice[] arrayOfGraphicsDevice = getScreenDevices();
/* 366 */     Object localObject = null;
/*     */ 
/* 389 */     Rectangle localRectangle1 = getUsableBounds(arrayOfGraphicsDevice[0]);
/*     */ 
/* 391 */     for (int i = 0; i < arrayOfGraphicsDevice.length; i++) {
/* 392 */       Rectangle localRectangle2 = getUsableBounds(arrayOfGraphicsDevice[i]);
/* 393 */       if ((localObject == null) && (localRectangle2.width / 2 + localRectangle2.x > localPoint.x - 1) && (localRectangle2.height / 2 + localRectangle2.y > localPoint.y - 1) && (localRectangle2.width / 2 + localRectangle2.x < localPoint.x + 1) && (localRectangle2.height / 2 + localRectangle2.y < localPoint.y + 1))
/*     */       {
/* 399 */         localObject = localRectangle2;
/*     */       }
/* 401 */       localRectangle1 = localRectangle1.union(localRectangle2);
/*     */     }
/*     */ 
/* 406 */     if ((localRectangle1.width / 2 + localRectangle1.x > localPoint.x - 1) && (localRectangle1.height / 2 + localRectangle1.y > localPoint.y - 1) && (localRectangle1.width / 2 + localRectangle1.x < localPoint.x + 1) && (localRectangle1.height / 2 + localRectangle1.y < localPoint.y + 1))
/*     */     {
/* 411 */       if (screenLog.isLoggable(400)) {
/* 412 */         screenLog.finer("Video Wall: center point is at center of all displays.");
/*     */       }
/* 414 */       return localRectangle1;
/*     */     }
/*     */ 
/* 418 */     if (localObject != null) {
/* 419 */       if (screenLog.isLoggable(400)) {
/* 420 */         screenLog.finer("Center point at center of a particular monitor, but not of the entire virtual display.");
/*     */       }
/*     */ 
/* 423 */       return localObject;
/*     */     }
/*     */ 
/* 427 */     if (screenLog.isLoggable(400)) {
/* 428 */       screenLog.finer("Center point is somewhere strange - return union of all bounds.");
/*     */     }
/* 430 */     return localRectangle1;
/*     */   }
/*     */ 
/*     */   public void paletteChanged()
/*     */   {
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  74 */     AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Object run() {
/*  77 */         System.loadLibrary("awt");
/*     */ 
/*  84 */         if (!GraphicsEnvironment.isHeadless())
/*     */         {
/*  86 */           boolean bool = false;
/*  87 */           String str1 = System.getProperty("sun.java2d.opengl");
/*  88 */           if (str1 != null) {
/*  89 */             if ((str1.equals("true")) || (str1.equals("t"))) {
/*  90 */               bool = true;
/*  91 */             } else if ((str1.equals("True")) || (str1.equals("T"))) {
/*  92 */               bool = true;
/*  93 */               X11GraphicsEnvironment.access$002(true);
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/*  98 */           int i = 0;
/*  99 */           String str2 = System.getProperty("sun.java2d.xrender");
/* 100 */           if (str2 != null) {
/* 101 */             if ((str2.equals("true")) || (str2.equals("t"))) {
/* 102 */               i = 1;
/* 103 */             } else if ((str2.equals("True")) || (str2.equals("T"))) {
/* 104 */               i = 1;
/* 105 */               X11GraphicsEnvironment.access$102(true);
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/* 110 */           X11GraphicsEnvironment.initDisplay(bool);
/*     */ 
/* 113 */           if (bool) {
/* 114 */             X11GraphicsEnvironment.access$302(X11GraphicsEnvironment.access$400());
/* 115 */             if ((X11GraphicsEnvironment.glxVerbose) && (!X11GraphicsEnvironment.glxAvailable)) {
/* 116 */               System.out.println("Could not enable OpenGL pipeline (GLX 1.3 not available)");
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/* 123 */           if (i != 0) {
/* 124 */             X11GraphicsEnvironment.access$502(X11GraphicsEnvironment.initXRender(X11GraphicsEnvironment.xRenderVerbose));
/* 125 */             if ((X11GraphicsEnvironment.xRenderVerbose) && (!X11GraphicsEnvironment.xRenderAvailable)) {
/* 126 */               System.out.println("Could not enable XRender pipeline");
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/* 131 */           if (X11GraphicsEnvironment.xRenderAvailable) {
/* 132 */             XRSurfaceData.initXRSurfaceData();
/*     */           }
/*     */         }
/*     */ 
/* 136 */         return null;
/*     */       }
/*     */     });
/* 141 */     SurfaceManagerFactory.setInstance(new UnixSurfaceManagerFactory());
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11GraphicsEnvironment
 * JD-Core Version:    0.6.2
 */