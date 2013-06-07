/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Frame;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Insets;
/*     */ import java.awt.MenuBar;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.peer.FramePeer;
/*     */ import sun.awt.AWTAccessor;
/*     */ import sun.awt.AWTAccessor.FrameAccessor;
/*     */ import sun.util.logging.PlatformLogger;
/*     */ 
/*     */ class XFramePeer extends XDecoratedPeer
/*     */   implements FramePeer
/*     */ {
/*  41 */   private static PlatformLogger log = PlatformLogger.getLogger("sun.awt.X11.XFramePeer");
/*  42 */   private static PlatformLogger stateLog = PlatformLogger.getLogger("sun.awt.X11.states");
/*  43 */   private static PlatformLogger insLog = PlatformLogger.getLogger("sun.awt.X11.insets.XFramePeer");
/*     */   XMenuBarPeer menubarPeer;
/*     */   MenuBar menubar;
/*     */   int state;
/*     */   private Boolean undecorated;
/*     */   private static final int MENUBAR_HEIGHT_IF_NO_MENUBAR = 0;
/*  51 */   private int lastAppliedMenubarHeight = 0;
/*     */   static final int CROSSHAIR_INSET = 5;
/*     */   static final int BUTTON_Y = 6;
/*     */   static final int BUTTON_W = 17;
/*     */   static final int BUTTON_H = 17;
/*     */   static final int SYS_MENU_X = 6;
/*     */   static final int SYS_MENU_CONTAINED_X = 11;
/*     */   static final int SYS_MENU_CONTAINED_Y = 13;
/*     */   static final int SYS_MENU_CONTAINED_W = 8;
/*     */   static final int SYS_MENU_CONTAINED_H = 3;
/*     */   static final int MAXIMIZE_X_DIFF = 22;
/*     */   static final int MAXIMIZE_CONTAINED_X_DIFF = 17;
/*     */   static final int MAXIMIZE_CONTAINED_Y = 11;
/*     */   static final int MAXIMIZE_CONTAINED_W = 8;
/*     */   static final int MAXIMIZE_CONTAINED_H = 8;
/*     */   static final int MINIMIZE_X_DIFF = 39;
/*     */   static final int MINIMIZE_CONTAINED_X_DIFF = 32;
/*     */   static final int MINIMIZE_CONTAINED_Y = 13;
/*     */   static final int MINIMIZE_CONTAINED_W = 3;
/*     */   static final int MINIMIZE_CONTAINED_H = 3;
/*     */   static final int TITLE_X = 23;
/*     */   static final int TITLE_W_DIFF = 60;
/*     */   static final int TITLE_MID_Y = 14;
/*     */   static final int MENUBAR_X = 6;
/*     */   static final int MENUBAR_Y = 23;
/*     */   static final int HORIZ_RESIZE_INSET = 22;
/*     */   static final int VERT_RESIZE_INSET = 22;
/*     */ 
/*     */   XFramePeer(Frame paramFrame)
/*     */   {
/*  54 */     super(paramFrame);
/*     */   }
/*     */ 
/*     */   XFramePeer(XCreateWindowParams paramXCreateWindowParams) {
/*  58 */     super(paramXCreateWindowParams);
/*     */   }
/*     */ 
/*     */   void preInit(XCreateWindowParams paramXCreateWindowParams) {
/*  62 */     super.preInit(paramXCreateWindowParams);
/*  63 */     Frame localFrame = (Frame)this.target;
/*     */ 
/*  65 */     this.winAttr.initialState = localFrame.getExtendedState();
/*  66 */     this.state = 0;
/*  67 */     this.undecorated = Boolean.valueOf(localFrame.isUndecorated());
/*  68 */     this.winAttr.nativeDecor = (!localFrame.isUndecorated());
/*  69 */     if (this.winAttr.nativeDecor)
/*  70 */       this.winAttr.decorations = XWindowAttributesData.AWT_DECOR_ALL;
/*     */     else {
/*  72 */       this.winAttr.decorations = XWindowAttributesData.AWT_DECOR_NONE;
/*     */     }
/*  74 */     this.winAttr.functions = 1;
/*  75 */     this.winAttr.isResizable = true;
/*  76 */     this.winAttr.title = localFrame.getTitle();
/*  77 */     this.winAttr.initialResizability = localFrame.isResizable();
/*  78 */     if (log.isLoggable(500))
/*  79 */       log.fine("Frame''s initial attributes: decor {0}, resizable {1}, undecorated {2}, initial state {3}", new Object[] { Integer.valueOf(this.winAttr.decorations), Boolean.valueOf(this.winAttr.initialResizability), Boolean.valueOf(!this.winAttr.nativeDecor ? 1 : false), Integer.valueOf(this.winAttr.initialState) });
/*     */   }
/*     */ 
/*     */   void postInit(XCreateWindowParams paramXCreateWindowParams)
/*     */   {
/*  86 */     super.postInit(paramXCreateWindowParams);
/*  87 */     setupState(true);
/*     */   }
/*     */ 
/*     */   boolean isTargetUndecorated()
/*     */   {
/*  92 */     if (this.undecorated != null) {
/*  93 */       return this.undecorated.booleanValue();
/*     */     }
/*  95 */     return ((Frame)this.target).isUndecorated();
/*     */   }
/*     */ 
/*     */   void setupState(boolean paramBoolean)
/*     */   {
/* 100 */     if (paramBoolean) {
/* 101 */       this.state = this.winAttr.initialState;
/*     */     }
/* 103 */     if ((this.state & 0x1) != 0)
/* 104 */       setInitialState(3);
/*     */     else {
/* 106 */       setInitialState(1);
/*     */     }
/* 108 */     setExtendedState(this.state);
/*     */   }
/*     */ 
/*     */   public void setMenuBar(MenuBar paramMenuBar)
/*     */   {
/* 113 */     XToolkit.awtLock();
/*     */     try {
/* 115 */       synchronized (getStateLock()) {
/* 116 */         if (paramMenuBar == this.menubar) return;
/* 117 */         if (paramMenuBar == null) {
/* 118 */           if (this.menubar != null) {
/* 119 */             this.menubarPeer.xSetVisible(false);
/* 120 */             this.menubar = null;
/* 121 */             this.menubarPeer.dispose();
/* 122 */             this.menubarPeer = null;
/*     */           }
/*     */         } else {
/* 125 */           this.menubar = paramMenuBar;
/* 126 */           this.menubarPeer = ((XMenuBarPeer)paramMenuBar.getPeer());
/* 127 */           if (this.menubarPeer != null)
/* 128 */             this.menubarPeer.init((Frame)this.target);
/*     */         }
/*     */       }
/*     */     }
/*     */     finally {
/* 133 */       XToolkit.awtUnlock();
/*     */     }
/*     */ 
/* 136 */     reshapeMenubarPeer();
/*     */   }
/*     */ 
/*     */   XMenuBarPeer getMenubarPeer() {
/* 140 */     return this.menubarPeer;
/*     */   }
/*     */ 
/*     */   int getMenuBarHeight() {
/* 144 */     if (this.menubarPeer != null) {
/* 145 */       return this.menubarPeer.getDesiredHeight();
/*     */     }
/* 147 */     return 0;
/*     */   }
/*     */ 
/*     */   void updateChildrenSizes()
/*     */   {
/* 152 */     super.updateChildrenSizes();
/* 153 */     int i = getMenuBarHeight();
/*     */ 
/* 159 */     XToolkit.awtLock();
/*     */     try {
/* 161 */       synchronized (getStateLock()) {
/* 162 */         int j = this.dimensions.getClientSize().width;
/* 163 */         if (this.menubarPeer != null)
/* 164 */           this.menubarPeer.reshape(0, 0, j, i);
/*     */       }
/*     */     }
/*     */     finally {
/* 168 */       XToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   final void reshapeMenubarPeer()
/*     */   {
/* 178 */     XToolkit.executeOnEventHandlerThread(this.target, new Runnable()
/*     */     {
/*     */       public void run()
/*     */       {
/* 182 */         XFramePeer.this.updateChildrenSizes();
/* 183 */         int i = 0;
/*     */ 
/* 185 */         int j = XFramePeer.this.getMenuBarHeight();
/*     */ 
/* 191 */         synchronized (XFramePeer.this.getStateLock()) {
/* 192 */           if (j != XFramePeer.this.lastAppliedMenubarHeight) {
/* 193 */             XFramePeer.this.lastAppliedMenubarHeight = j;
/* 194 */             i = 1;
/*     */           }
/*     */         }
/* 197 */         if (i != 0)
/*     */         {
/* 203 */           XFramePeer.this.target.invalidate();
/* 204 */           XFramePeer.this.target.validate();
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public void setMaximizedBounds(Rectangle paramRectangle)
/*     */   {
/* 212 */     if (insLog.isLoggable(500)) insLog.fine("Setting maximized bounds to " + paramRectangle);
/* 213 */     if (paramRectangle == null) return;
/* 214 */     this.maxBounds = new Rectangle(paramRectangle);
/* 215 */     XToolkit.awtLock();
/*     */     try {
/* 217 */       XSizeHints localXSizeHints = getHints();
/* 218 */       localXSizeHints.set_flags(localXSizeHints.get_flags() | 0x20);
/* 219 */       if (paramRectangle.width != 2147483647)
/* 220 */         localXSizeHints.set_max_width(paramRectangle.width);
/*     */       else {
/* 222 */         localXSizeHints.set_max_width((int)XlibWrapper.DisplayWidth(XToolkit.getDisplay(), XlibWrapper.DefaultScreen(XToolkit.getDisplay())));
/*     */       }
/* 224 */       if (paramRectangle.height != 2147483647)
/* 225 */         localXSizeHints.set_max_height(paramRectangle.height);
/*     */       else {
/* 227 */         localXSizeHints.set_max_height((int)XlibWrapper.DisplayHeight(XToolkit.getDisplay(), XlibWrapper.DefaultScreen(XToolkit.getDisplay())));
/*     */       }
/* 229 */       if (insLog.isLoggable(400)) insLog.finer("Setting hints, flags " + XlibWrapper.hintsToString(localXSizeHints.get_flags()));
/* 230 */       XlibWrapper.XSetWMNormalHints(XToolkit.getDisplay(), this.window, localXSizeHints.pData);
/*     */     } finally {
/* 232 */       XToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getState() {
/* 237 */     synchronized (getStateLock()) {
/* 238 */       return this.state;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setState(int paramInt) {
/* 243 */     synchronized (getStateLock()) {
/* 244 */       if (!isShowing()) {
/* 245 */         stateLog.finer("Frame is not showing");
/* 246 */         this.state = paramInt;
/* 247 */         return;
/*     */       }
/*     */     }
/* 250 */     changeState(paramInt);
/*     */   }
/*     */ 
/*     */   void changeState(int paramInt) {
/* 254 */     int i = this.state ^ paramInt;
/* 255 */     int j = i & 0x1;
/* 256 */     boolean bool = (paramInt & 0x1) != 0;
/* 257 */     stateLog.finer("Changing state, old state {0}, new state {1}(iconic {2})", new Object[] { Integer.valueOf(this.state), Integer.valueOf(paramInt), Boolean.valueOf(bool) });
/*     */ 
/* 259 */     if ((j != 0) && (bool)) {
/* 260 */       if (stateLog.isLoggable(400)) stateLog.finer("Iconifying shell " + getShell() + ", this " + this + ", screen " + getScreenNumber());
/* 261 */       XToolkit.awtLock();
/*     */       try {
/* 263 */         int k = XlibWrapper.XIconifyWindow(XToolkit.getDisplay(), getShell(), getScreenNumber());
/* 264 */         if (stateLog.isLoggable(400)) stateLog.finer("XIconifyWindow returned " + k); 
/*     */       }
/*     */       finally
/*     */       {
/* 267 */         XToolkit.awtUnlock();
/*     */       }
/*     */     }
/* 270 */     if ((i & 0xFFFFFFFE) != 0) {
/* 271 */       setExtendedState(paramInt);
/*     */     }
/* 273 */     if ((j != 0) && (!bool)) {
/* 274 */       if (stateLog.isLoggable(400)) stateLog.finer("DeIconifying " + this);
/* 275 */       xSetVisible(true);
/*     */     }
/*     */   }
/*     */ 
/*     */   void setExtendedState(int paramInt) {
/* 280 */     XWM.getWM().setExtendedState(this, paramInt);
/*     */   }
/*     */ 
/*     */   public void handlePropertyNotify(XEvent paramXEvent) {
/* 284 */     super.handlePropertyNotify(paramXEvent);
/* 285 */     XPropertyEvent localXPropertyEvent = paramXEvent.get_xproperty();
/*     */ 
/* 287 */     log.finer("Property change {0}", new Object[] { localXPropertyEvent });
/*     */ 
/* 292 */     if (!XWM.getWM().isStateChange(this, localXPropertyEvent)) {
/* 293 */       stateLog.finer("either not a state atom or state has not been changed");
/* 294 */       return;
/*     */     }
/*     */ 
/* 297 */     int i = XWM.getWM().getState(this);
/* 298 */     int j = this.state ^ i;
/* 299 */     if (j == 0) {
/* 300 */       stateLog.finer("State is the same: " + this.state);
/* 301 */       return;
/*     */     }
/*     */ 
/* 304 */     int k = this.state;
/* 305 */     this.state = i;
/*     */ 
/* 308 */     AWTAccessor.getFrameAccessor().setExtendedState((Frame)this.target, this.state);
/*     */ 
/* 310 */     if ((j & 0x1) != 0) {
/* 311 */       if ((this.state & 0x1) != 0) {
/* 312 */         stateLog.finer("Iconified");
/* 313 */         handleIconify();
/*     */       } else {
/* 315 */         stateLog.finer("DeIconified");
/* 316 */         this.content.purgeIconifiedExposeEvents();
/* 317 */         handleDeiconify();
/*     */       }
/*     */     }
/* 320 */     handleStateChange(k, this.state);
/*     */   }
/*     */ 
/*     */   public void handleStateChange(int paramInt1, int paramInt2)
/*     */   {
/* 326 */     super.handleStateChange(paramInt1, paramInt2);
/* 327 */     for (ToplevelStateListener localToplevelStateListener : this.toplevelStateListeners)
/* 328 */       localToplevelStateListener.stateChangedJava(paramInt1, paramInt2);
/*     */   }
/*     */ 
/*     */   public void setVisible(boolean paramBoolean)
/*     */   {
/* 333 */     if (paramBoolean) {
/* 334 */       setupState(false);
/*     */     }
/* 336 */     else if ((this.state & 0x6) != 0) {
/* 337 */       XWM.getWM().setExtendedState(this, this.state & 0xFFFFFFF9);
/*     */     }
/*     */ 
/* 340 */     super.setVisible(paramBoolean);
/* 341 */     if ((paramBoolean) && (this.maxBounds != null))
/* 342 */       setMaximizedBounds(this.maxBounds);
/*     */   }
/*     */ 
/*     */   void setInitialState(int paramInt)
/*     */   {
/* 347 */     XToolkit.awtLock();
/*     */     try {
/* 349 */       XWMHints localXWMHints = getWMHints();
/* 350 */       localXWMHints.set_flags(0x2 | localXWMHints.get_flags());
/* 351 */       localXWMHints.set_initial_state(paramInt);
/* 352 */       if (stateLog.isLoggable(500)) stateLog.fine("Setting initial WM state on " + this + " to " + paramInt);
/* 353 */       XlibWrapper.XSetWMHints(XToolkit.getDisplay(), getWindow(), localXWMHints.pData);
/*     */     }
/*     */     finally {
/* 356 */       XToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void dispose() {
/* 361 */     if (this.menubarPeer != null) {
/* 362 */       this.menubarPeer.dispose();
/*     */     }
/* 364 */     super.dispose();
/*     */   }
/*     */ 
/*     */   boolean isMaximized() {
/* 368 */     return (this.state & 0x6) != 0;
/*     */   }
/*     */ 
/*     */   public void print(Graphics paramGraphics)
/*     */   {
/* 416 */     super.print(paramGraphics);
/*     */ 
/* 418 */     Frame localFrame = (Frame)this.target;
/* 419 */     Insets localInsets = localFrame.getInsets();
/* 420 */     Dimension localDimension = localFrame.getSize();
/*     */ 
/* 422 */     Color localColor1 = localFrame.getBackground();
/* 423 */     Color localColor2 = localFrame.getForeground();
/* 424 */     Color localColor3 = localColor1.brighter();
/* 425 */     Color localColor4 = localColor1.darker();
/*     */ 
/* 431 */     if (hasDecorations(XWindowAttributesData.AWT_DECOR_BORDER))
/*     */     {
/* 436 */       if (localColor3.equals(Color.white)) {
/* 437 */         paramGraphics.setColor(new Color(230, 230, 230));
/*     */       }
/*     */       else {
/* 440 */         paramGraphics.setColor(localColor3);
/*     */       }
/* 442 */       paramGraphics.drawLine(0, 0, localDimension.width, 0);
/* 443 */       paramGraphics.drawLine(0, 1, localDimension.width - 1, 1);
/*     */ 
/* 452 */       paramGraphics.drawLine(0, 0, 0, localDimension.height);
/* 453 */       paramGraphics.drawLine(1, 0, 1, localDimension.height - 1);
/*     */ 
/* 456 */       paramGraphics.setColor(localColor3);
/* 457 */       paramGraphics.drawLine(6, localDimension.height - 5, localDimension.width - 5, localDimension.height - 5);
/*     */ 
/* 463 */       paramGraphics.drawLine(localDimension.width - 5, 6, localDimension.width - 5, localDimension.height - 5);
/*     */ 
/* 468 */       paramGraphics.setColor(localColor4);
/* 469 */       paramGraphics.drawLine(1, localDimension.height, localDimension.width, localDimension.height);
/* 470 */       paramGraphics.drawLine(2, localDimension.height - 1, localDimension.width, localDimension.height - 1);
/*     */ 
/* 474 */       paramGraphics.drawLine(localDimension.width, 1, localDimension.width, localDimension.height);
/* 475 */       paramGraphics.drawLine(localDimension.width - 1, 2, localDimension.width - 1, localDimension.height);
/*     */ 
/* 479 */       paramGraphics.drawLine(5, 5, localDimension.width - 5, 5);
/*     */ 
/* 484 */       paramGraphics.drawLine(5, 5, 5, localDimension.height - 5);
/*     */     }
/*     */     Object localObject2;
/*     */     Object localObject3;
/* 488 */     if (hasDecorations(XWindowAttributesData.AWT_DECOR_TITLE))
/*     */     {
/* 490 */       if (hasDecorations(XWindowAttributesData.AWT_DECOR_MENU))
/*     */       {
/* 493 */         paramGraphics.setColor(localColor1);
/* 494 */         paramGraphics.fill3DRect(6, 6, 17, 17, true);
/* 495 */         paramGraphics.fill3DRect(11, 13, 8, 3, true);
/*     */       }
/*     */ 
/* 501 */       paramGraphics.fill3DRect(23, 6, localDimension.width - 60, 17, true);
/*     */ 
/* 504 */       if (hasDecorations(XWindowAttributesData.AWT_DECOR_MINIMIZE))
/*     */       {
/* 508 */         paramGraphics.fill3DRect(localDimension.width - 39, 6, 17, 17, true);
/*     */ 
/* 510 */         paramGraphics.fill3DRect(localDimension.width - 32, 13, 3, 3, true);
/*     */       }
/*     */ 
/* 515 */       if (hasDecorations(XWindowAttributesData.AWT_DECOR_MAXIMIZE))
/*     */       {
/* 519 */         paramGraphics.fill3DRect(localDimension.width - 22, 6, 17, 17, true);
/*     */ 
/* 521 */         paramGraphics.fill3DRect(localDimension.width - 17, 11, 8, 8, true);
/*     */       }
/*     */ 
/* 527 */       paramGraphics.setColor(localColor2);
/* 528 */       localObject1 = new Font("SansSerif", 0, 10);
/* 529 */       paramGraphics.setFont((Font)localObject1);
/* 530 */       localObject2 = paramGraphics.getFontMetrics();
/* 531 */       localObject3 = localFrame.getTitle();
/* 532 */       paramGraphics.drawString((String)localObject3, (46 + localDimension.width - 60) / 2 - ((FontMetrics)localObject2).stringWidth((String)localObject3) / 2, 14 + ((FontMetrics)localObject2).getMaxDescent());
/*     */     }
/*     */ 
/* 538 */     if ((localFrame.isResizable()) && (hasDecorations(XWindowAttributesData.AWT_DECOR_RESIZEH)))
/*     */     {
/* 544 */       paramGraphics.setColor(localColor4);
/* 545 */       paramGraphics.drawLine(1, 22, 5, 22);
/*     */ 
/* 549 */       paramGraphics.drawLine(22, 1, 22, 5);
/*     */ 
/* 552 */       paramGraphics.drawLine(localDimension.width - 5 + 1, 22, localDimension.width, 22);
/*     */ 
/* 556 */       paramGraphics.drawLine(localDimension.width - 22 - 1, 2, localDimension.width - 22 - 1, 6);
/*     */ 
/* 560 */       paramGraphics.drawLine(1, localDimension.height - 22 - 1, 5, localDimension.height - 22 - 1);
/*     */ 
/* 564 */       paramGraphics.drawLine(22, localDimension.height - 5 + 1, 22, localDimension.height);
/*     */ 
/* 568 */       paramGraphics.drawLine(localDimension.width - 5 + 1, localDimension.height - 22 - 1, localDimension.width, localDimension.height - 22 - 1);
/*     */ 
/* 573 */       paramGraphics.drawLine(localDimension.width - 22 - 1, localDimension.height - 5 + 1, localDimension.width - 22 - 1, localDimension.height);
/*     */ 
/* 578 */       paramGraphics.setColor(localColor3);
/* 579 */       paramGraphics.drawLine(2, 23, 5, 23);
/*     */ 
/* 583 */       paramGraphics.drawLine(23, 2, 23, 5);
/*     */ 
/* 587 */       paramGraphics.drawLine(localDimension.width - 5 + 1, 23, localDimension.width - 1, 23);
/*     */ 
/* 592 */       paramGraphics.drawLine(localDimension.width - 22, 2, localDimension.width - 22, 5);
/*     */ 
/* 596 */       paramGraphics.drawLine(2, localDimension.height - 22, 5, localDimension.height - 22);
/*     */ 
/* 600 */       paramGraphics.drawLine(23, localDimension.height - 5 + 1, 23, localDimension.height - 1);
/*     */ 
/* 605 */       paramGraphics.drawLine(localDimension.width - 5 + 1, localDimension.height - 22, localDimension.width - 1, localDimension.height - 22);
/*     */ 
/* 610 */       paramGraphics.drawLine(localDimension.width - 22, localDimension.height - 5 + 1, localDimension.width - 22, localDimension.height - 1);
/*     */     }
/*     */ 
/* 615 */     Object localObject1 = this.menubarPeer;
/* 616 */     if (localObject1 != null) {
/* 617 */       localObject2 = getInsets();
/* 618 */       localObject3 = paramGraphics.create();
/* 619 */       int i = 0;
/* 620 */       int j = 0;
/* 621 */       if (hasDecorations(XWindowAttributesData.AWT_DECOR_BORDER)) {
/* 622 */         i += 6;
/* 623 */         j += 6;
/*     */       }
/* 625 */       if (hasDecorations(XWindowAttributesData.AWT_DECOR_TITLE))
/* 626 */         j += 17;
/*     */       try
/*     */       {
/* 629 */         ((Graphics)localObject3).translate(i, j);
/* 630 */         ((XMenuBarPeer)localObject1).print((Graphics)localObject3);
/*     */       } finally {
/* 632 */         ((Graphics)localObject3).dispose();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setBoundsPrivate(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/* 638 */     setBounds(paramInt1, paramInt2, paramInt3, paramInt4, 3);
/*     */   }
/*     */ 
/*     */   public Rectangle getBoundsPrivate() {
/* 642 */     return getBounds();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XFramePeer
 * JD-Core Version:    0.6.2
 */