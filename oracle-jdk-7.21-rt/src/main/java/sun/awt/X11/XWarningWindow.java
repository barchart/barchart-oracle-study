/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Insets;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.SystemColor;
/*     */ import java.awt.Window;
/*     */ import java.awt.geom.Point2D;
/*     */ import java.lang.ref.WeakReference;
/*     */ import sun.awt.AWTAccessor;
/*     */ import sun.awt.AWTAccessor.WindowAccessor;
/*     */ import sun.awt.SunToolkit;
/*     */ 
/*     */ class XWarningWindow extends XWindow
/*     */ {
/*     */   private static final int SHOWING_DELAY = 330;
/*     */   private static final int HIDING_DELAY = 2000;
/*     */   private final Window ownerWindow;
/*     */   private WeakReference<XWindowPeer> ownerPeer;
/*     */   private long parentWindow;
/*     */   private static final String OWNER = "OWNER";
/*     */   private InfoWindow.Tooltip tooltip;
/*  50 */   private volatile int currentIcon = 0;
/*     */ 
/*  58 */   private int currentSize = -1;
/*     */   private static XIconInfo[][] icons;
/* 361 */   private final Runnable hidingTask = new Runnable() {
/*     */     public void run() {
/* 363 */       XWarningWindow.this.xSetVisible(false);
/*     */     }
/* 361 */   };
/*     */ 
/* 367 */   private final Runnable showingTask = new Runnable() {
/*     */     public void run() {
/* 369 */       if (!XWarningWindow.this.isVisible()) {
/* 370 */         XWarningWindow.this.xSetVisible(true);
/* 371 */         XWarningWindow.this.updateIconSize();
/* 372 */         XWindowPeer localXWindowPeer = (XWindowPeer)XWarningWindow.this.ownerPeer.get();
/* 373 */         if (localXWindowPeer != null) {
/* 374 */           localXWindowPeer.repositionSecurityWarning();
/*     */         }
/*     */       }
/* 377 */       XWarningWindow.this.repaint();
/* 378 */       if (XWarningWindow.this.currentIcon > 0) {
/* 379 */         XWarningWindow.access$310(XWarningWindow.this);
/* 380 */         XToolkit.schedule(XWarningWindow.this.showingTask, 330L);
/*     */       }
/*     */     }
/* 367 */   };
/*     */ 
/*     */   private static XIconInfo getSecurityIconInfo(int paramInt1, int paramInt2)
/*     */   {
/*  61 */     synchronized (XWarningWindow.class) {
/*  62 */       if (icons == null) {
/*  63 */         icons = new XIconInfo[4][3];
/*  64 */         if (XlibWrapper.dataModel == 32) {
/*  65 */           icons[0][0] = new XIconInfo(XAWTIcon32_security_icon_bw16_png.security_icon_bw16_png);
/*  66 */           icons[0][1] = new XIconInfo(XAWTIcon32_security_icon_interim16_png.security_icon_interim16_png);
/*  67 */           icons[0][2] = new XIconInfo(XAWTIcon32_security_icon_yellow16_png.security_icon_yellow16_png);
/*  68 */           icons[1][0] = new XIconInfo(XAWTIcon32_security_icon_bw24_png.security_icon_bw24_png);
/*  69 */           icons[1][1] = new XIconInfo(XAWTIcon32_security_icon_interim24_png.security_icon_interim24_png);
/*  70 */           icons[1][2] = new XIconInfo(XAWTIcon32_security_icon_yellow24_png.security_icon_yellow24_png);
/*  71 */           icons[2][0] = new XIconInfo(XAWTIcon32_security_icon_bw32_png.security_icon_bw32_png);
/*  72 */           icons[2][1] = new XIconInfo(XAWTIcon32_security_icon_interim32_png.security_icon_interim32_png);
/*  73 */           icons[2][2] = new XIconInfo(XAWTIcon32_security_icon_yellow32_png.security_icon_yellow32_png);
/*  74 */           icons[3][0] = new XIconInfo(XAWTIcon32_security_icon_bw48_png.security_icon_bw48_png);
/*  75 */           icons[3][1] = new XIconInfo(XAWTIcon32_security_icon_interim48_png.security_icon_interim48_png);
/*  76 */           icons[3][2] = new XIconInfo(XAWTIcon32_security_icon_yellow48_png.security_icon_yellow48_png);
/*     */         } else {
/*  78 */           icons[0][0] = new XIconInfo(XAWTIcon64_security_icon_bw16_png.security_icon_bw16_png);
/*  79 */           icons[0][1] = new XIconInfo(XAWTIcon64_security_icon_interim16_png.security_icon_interim16_png);
/*  80 */           icons[0][2] = new XIconInfo(XAWTIcon64_security_icon_yellow16_png.security_icon_yellow16_png);
/*  81 */           icons[1][0] = new XIconInfo(XAWTIcon64_security_icon_bw24_png.security_icon_bw24_png);
/*  82 */           icons[1][1] = new XIconInfo(XAWTIcon64_security_icon_interim24_png.security_icon_interim24_png);
/*  83 */           icons[1][2] = new XIconInfo(XAWTIcon64_security_icon_yellow24_png.security_icon_yellow24_png);
/*  84 */           icons[2][0] = new XIconInfo(XAWTIcon64_security_icon_bw32_png.security_icon_bw32_png);
/*  85 */           icons[2][1] = new XIconInfo(XAWTIcon64_security_icon_interim32_png.security_icon_interim32_png);
/*  86 */           icons[2][2] = new XIconInfo(XAWTIcon64_security_icon_yellow32_png.security_icon_yellow32_png);
/*  87 */           icons[3][0] = new XIconInfo(XAWTIcon64_security_icon_bw48_png.security_icon_bw48_png);
/*  88 */           icons[3][1] = new XIconInfo(XAWTIcon64_security_icon_interim48_png.security_icon_interim48_png);
/*  89 */           icons[3][2] = new XIconInfo(XAWTIcon64_security_icon_yellow48_png.security_icon_yellow48_png);
/*     */         }
/*     */       }
/*     */     }
/*  93 */     int i = paramInt1 % icons.length;
/*  94 */     return icons[i][(paramInt2 % icons[i].length)];
/*     */   }
/*     */ 
/*     */   private void updateIconSize() {
/*  98 */     int i = -1;
/*     */     Object localObject1;
/* 100 */     if (this.ownerWindow != null) {
/* 101 */       localObject1 = this.ownerWindow.getInsets();
/* 102 */       int j = Math.max(((Insets)localObject1).top, Math.max(((Insets)localObject1).bottom, Math.max(((Insets)localObject1).left, ((Insets)localObject1).right)));
/*     */ 
/* 104 */       if (j < 24)
/* 105 */         i = 0;
/* 106 */       else if (j < 32)
/* 107 */         i = 1;
/* 108 */       else if (j < 48)
/* 109 */         i = 2;
/*     */       else {
/* 111 */         i = 3;
/*     */       }
/*     */     }
/*     */ 
/* 115 */     if (i == -1) {
/* 116 */       i = 0;
/*     */     }
/*     */ 
/* 124 */     XToolkit.awtLock();
/*     */     try {
/* 126 */       if (i != this.currentSize) {
/* 127 */         this.currentSize = i;
/* 128 */         localObject1 = getSecurityIconInfo(this.currentSize, 0);
/* 129 */         XlibWrapper.SetBitmapShape(XToolkit.getDisplay(), getWindow(), ((XIconInfo)localObject1).getWidth(), ((XIconInfo)localObject1).getHeight(), ((XIconInfo)localObject1).getIntData());
/*     */ 
/* 131 */         AWTAccessor.getWindowAccessor().setSecurityWarningSize(this.ownerWindow, ((XIconInfo)localObject1).getWidth(), ((XIconInfo)localObject1).getHeight());
/*     */       }
/*     */     }
/*     */     finally {
/* 135 */       XToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   private XIconInfo getSecurityIconInfo() {
/* 140 */     updateIconSize();
/* 141 */     return getSecurityIconInfo(this.currentSize, this.currentIcon);
/*     */   }
/*     */ 
/*     */   XWarningWindow(Window paramWindow, long paramLong, XWindowPeer paramXWindowPeer) {
/* 145 */     super(new XCreateWindowParams(new Object[] { "target", paramWindow, "OWNER", Long.valueOf(paramLong) }));
/*     */ 
/* 149 */     this.ownerWindow = paramWindow;
/* 150 */     this.parentWindow = paramLong;
/* 151 */     this.tooltip = new InfoWindow.Tooltip(null, getTarget(), new InfoWindow.Tooltip.LiveArguments()
/*     */     {
/*     */       public boolean isDisposed() {
/* 154 */         return XWarningWindow.this.isDisposed();
/*     */       }
/*     */       public Rectangle getBounds() {
/* 157 */         return XWarningWindow.this.getBounds();
/*     */       }
/*     */       public String getTooltipString() {
/* 160 */         return XWarningWindow.this.ownerWindow.getWarningString();
/*     */       }
/*     */     });
/* 163 */     this.ownerPeer = new WeakReference(paramXWindowPeer);
/*     */   }
/*     */ 
/*     */   private void requestNoTaskbar() {
/* 167 */     XNETProtocol localXNETProtocol = XWM.getWM().getNETProtocol();
/* 168 */     if (localXNETProtocol != null)
/* 169 */       localXNETProtocol.requestState(this, localXNETProtocol.XA_NET_WM_STATE_SKIP_TASKBAR, true);
/*     */   }
/*     */ 
/*     */   void postInit(XCreateWindowParams paramXCreateWindowParams)
/*     */   {
/* 175 */     super.postInit(paramXCreateWindowParams);
/* 176 */     XToolkit.awtLock();
/*     */     try {
/* 178 */       XWM.setMotifDecor(this, false, 0, 0);
/* 179 */       XWM.setOLDecor(this, false, 0);
/*     */ 
/* 181 */       long l = ((Long)paramXCreateWindowParams.get("OWNER")).longValue();
/* 182 */       XlibWrapper.XSetTransientFor(XToolkit.getDisplay(), getWindow(), l);
/*     */ 
/* 185 */       XWMHints localXWMHints = getWMHints();
/* 186 */       localXWMHints.set_flags(localXWMHints.get_flags() | 1L | 0x2);
/* 187 */       localXWMHints.set_input(false);
/* 188 */       localXWMHints.set_initial_state(1);
/* 189 */       XlibWrapper.XSetWMHints(XToolkit.getDisplay(), getWindow(), localXWMHints.pData);
/*     */ 
/* 191 */       initWMProtocols();
/* 192 */       requestNoTaskbar();
/*     */     } finally {
/* 194 */       XToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void reposition(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/* 202 */     Point2D localPoint2D = AWTAccessor.getWindowAccessor().calculateSecurityWarningPosition(this.ownerWindow, paramInt1, paramInt2, paramInt3, paramInt4);
/*     */ 
/* 205 */     reshape((int)localPoint2D.getX(), (int)localPoint2D.getY(), getWidth(), getHeight());
/*     */   }
/*     */ 
/*     */   protected String getWMName() {
/* 209 */     return "Warning window";
/*     */   }
/*     */ 
/*     */   public Graphics getGraphics() {
/* 213 */     if ((this.surfaceData == null) || (this.ownerWindow == null)) return null;
/* 214 */     return getGraphics(this.surfaceData, getColor(), getBackground(), getFont());
/*     */   }
/*     */ 
/*     */   void paint(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/* 220 */     paramGraphics.drawImage(getSecurityIconInfo().getImage(), 0, 0, null);
/*     */   }
/*     */ 
/*     */   String getWarningString() {
/* 224 */     return this.ownerWindow.getWarningString();
/*     */   }
/*     */ 
/*     */   int getWidth() {
/* 228 */     return getSecurityIconInfo().getWidth();
/*     */   }
/*     */ 
/*     */   int getHeight() {
/* 232 */     return getSecurityIconInfo().getHeight();
/*     */   }
/*     */ 
/*     */   Color getBackground() {
/* 236 */     return SystemColor.window;
/*     */   }
/*     */   Color getColor() {
/* 239 */     return Color.black;
/*     */   }
/*     */   Font getFont() {
/* 242 */     return this.ownerWindow.getFont();
/*     */   }
/*     */   public void repaint() {
/* 245 */     Rectangle localRectangle = getBounds();
/* 246 */     Graphics localGraphics = getGraphics();
/*     */     try {
/* 248 */       paint(localGraphics, 0, 0, localRectangle.width, localRectangle.height);
/*     */     } finally {
/* 250 */       localGraphics.dispose();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void handleExposeEvent(XEvent paramXEvent)
/*     */   {
/* 256 */     super.handleExposeEvent(paramXEvent);
/*     */ 
/* 258 */     XExposeEvent localXExposeEvent = paramXEvent.get_xexpose();
/* 259 */     final int i = localXExposeEvent.get_x();
/* 260 */     final int j = localXExposeEvent.get_y();
/* 261 */     final int k = localXExposeEvent.get_width();
/* 262 */     final int m = localXExposeEvent.get_height();
/* 263 */     SunToolkit.executeOnEventHandlerThread(this.target, new Runnable()
/*     */     {
/*     */       public void run() {
/* 266 */         Graphics localGraphics = XWarningWindow.this.getGraphics();
/*     */         try {
/* 268 */           XWarningWindow.this.paint(localGraphics, i, j, k, m);
/*     */         } finally {
/* 270 */           localGraphics.dispose();
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   protected boolean isEventDisabled(XEvent paramXEvent)
/*     */   {
/* 278 */     return true;
/*     */   }
/*     */ 
/*     */   private void withdraw()
/*     */   {
/* 284 */     XEvent localXEvent = new XEvent();
/*     */     try {
/* 287 */       XToolkit.awtLock();
/*     */       long l;
/*     */       try { l = XlibWrapper.RootWindow(XToolkit.getDisplay(), getScreenNumber());
/*     */       } finally
/*     */       {
/* 292 */         XToolkit.awtUnlock();
/*     */       }
/*     */ 
/* 295 */       localXEvent.set_type(18);
/*     */ 
/* 297 */       XUnmapEvent localXUnmapEvent = localXEvent.get_xunmap();
/*     */ 
/* 299 */       localXUnmapEvent.set_event(l);
/* 300 */       localXUnmapEvent.set_window(getWindow());
/* 301 */       localXUnmapEvent.set_from_configure(false);
/*     */ 
/* 303 */       XToolkit.awtLock();
/*     */       try {
/* 305 */         XlibWrapper.XSendEvent(XToolkit.getDisplay(), l, false, 1572864L, localXEvent.pData);
/*     */       }
/*     */       finally
/*     */       {
/* 312 */         XToolkit.awtUnlock();
/*     */       }
/*     */     } finally {
/* 315 */       localXEvent.dispose();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void stateChanged(long paramLong, int paramInt1, int paramInt2)
/*     */   {
/* 321 */     if (paramInt2 == 3) {
/* 322 */       super.xSetVisible(false);
/* 323 */       withdraw();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void setMouseAbove(boolean paramBoolean)
/*     */   {
/* 329 */     super.setMouseAbove(paramBoolean);
/* 330 */     XWindowPeer localXWindowPeer = (XWindowPeer)this.ownerPeer.get();
/* 331 */     if (localXWindowPeer != null)
/* 332 */       localXWindowPeer.updateSecurityWarningVisibility();
/*     */   }
/*     */ 
/*     */   protected void enterNotify(long paramLong)
/*     */   {
/* 338 */     super.enterNotify(paramLong);
/* 339 */     if (paramLong == getWindow())
/* 340 */       this.tooltip.enter();
/*     */   }
/*     */ 
/*     */   protected void leaveNotify(long paramLong)
/*     */   {
/* 346 */     super.leaveNotify(paramLong);
/* 347 */     if (paramLong == getWindow())
/* 348 */       this.tooltip.exit();
/*     */   }
/*     */ 
/*     */   public void xSetVisible(boolean paramBoolean)
/*     */   {
/* 354 */     super.xSetVisible(paramBoolean);
/*     */ 
/* 358 */     requestNoTaskbar();
/*     */   }
/*     */ 
/*     */   public void setSecurityWarningVisible(boolean paramBoolean1, boolean paramBoolean2)
/*     */   {
/* 386 */     if (paramBoolean1) {
/* 387 */       XToolkit.remove(this.hidingTask);
/* 388 */       XToolkit.remove(this.showingTask);
/* 389 */       if (isVisible())
/* 390 */         this.currentIcon = 0;
/*     */       else {
/* 392 */         this.currentIcon = 3;
/*     */       }
/* 394 */       if (paramBoolean2)
/* 395 */         XToolkit.schedule(this.showingTask, 1L);
/*     */       else
/* 397 */         this.showingTask.run();
/*     */     }
/*     */     else {
/* 400 */       XToolkit.remove(this.showingTask);
/* 401 */       XToolkit.remove(this.hidingTask);
/* 402 */       if (!isVisible()) {
/* 403 */         return;
/*     */       }
/* 405 */       if (paramBoolean2)
/* 406 */         XToolkit.schedule(this.hidingTask, 2000L);
/*     */       else
/* 408 */         this.hidingTask.run();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XWarningWindow
 * JD-Core Version:    0.6.2
 */