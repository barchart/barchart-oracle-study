/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.awt.AWTEvent;
/*     */ import java.awt.AWTException;
/*     */ import java.awt.BufferCapabilities;
/*     */ import java.awt.BufferCapabilities.FlipContents;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.GraphicsConfiguration;
/*     */ import java.awt.Image;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.Window;
/*     */ import java.awt.event.ComponentEvent;
/*     */ import java.awt.event.FocusEvent;
/*     */ import java.awt.event.InputEvent;
/*     */ import java.awt.event.InvocationEvent;
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.awt.event.PaintEvent;
/*     */ import java.awt.image.ColorModel;
/*     */ import java.awt.image.ImageObserver;
/*     */ import java.awt.image.ImageProducer;
/*     */ import java.awt.image.VolatileImage;
/*     */ import java.awt.peer.ComponentPeer;
/*     */ import java.awt.peer.ContainerPeer;
/*     */ import sun.awt.CausedFocusEvent.Cause;
/*     */ import sun.java2d.pipe.Region;
/*     */ 
/*     */ public class XEmbedChildProxyPeer
/*     */   implements ComponentPeer, XEventDispatcher
/*     */ {
/*     */   XEmbeddingContainer container;
/*     */   XEmbedChildProxy proxy;
/*     */   long handle;
/*     */ 
/*     */   XEmbedChildProxyPeer(XEmbedChildProxy paramXEmbedChildProxy)
/*     */   {
/*  45 */     this.container = paramXEmbedChildProxy.getEmbeddingContainer();
/*  46 */     this.handle = paramXEmbedChildProxy.getHandle();
/*  47 */     this.proxy = paramXEmbedChildProxy;
/*  48 */     initDispatching();
/*     */   }
/*     */ 
/*     */   void initDispatching() {
/*  52 */     XToolkit.awtLock();
/*     */     try {
/*  54 */       XToolkit.addEventDispatcher(this.handle, this);
/*  55 */       XlibWrapper.XSelectInput(XToolkit.getDisplay(), this.handle, 4325376L);
/*     */     }
/*     */     finally
/*     */     {
/*  59 */       XToolkit.awtUnlock();
/*     */     }
/*  61 */     this.container.notifyChildEmbedded(this.handle);
/*     */   }
/*  63 */   public boolean isObscured() { return false; } 
/*  64 */   public boolean canDetermineObscurity() { return false; } 
/*     */   public void setVisible(boolean paramBoolean) {
/*  66 */     if (!paramBoolean) {
/*  67 */       XToolkit.awtLock();
/*     */       try {
/*  69 */         XlibWrapper.XUnmapWindow(XToolkit.getDisplay(), this.handle);
/*     */       }
/*     */       finally {
/*  72 */         XToolkit.awtUnlock();
/*     */       }
/*     */     } else {
/*  75 */       XToolkit.awtLock();
/*     */       try {
/*  77 */         XlibWrapper.XMapWindow(XToolkit.getDisplay(), this.handle);
/*     */       }
/*     */       finally {
/*  80 */         XToolkit.awtUnlock();
/*     */       }
/*     */     }
/*     */   }
/*     */   public void setEnabled(boolean paramBoolean) {
/*     */   }
/*     */   public void paint(Graphics paramGraphics) {
/*     */   }
/*     */   public void repaint(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {  }
/*     */ 
/*     */   public void print(Graphics paramGraphics) {  }
/*     */ 
/*  90 */   public void setBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { XToolkit.awtLock();
/*     */     try {
/*  92 */       XlibWrapper.XMoveResizeWindow(XToolkit.getDisplay(), this.handle, paramInt1, paramInt2, paramInt3, paramInt4);
/*     */     }
/*     */     finally {
/*  95 */       XToolkit.awtUnlock();
/*     */     } }
/*     */ 
/*     */   public void handleEvent(AWTEvent paramAWTEvent) {
/*  99 */     switch (paramAWTEvent.getID()) {
/*     */     case 1004:
/* 101 */       XKeyboardFocusManagerPeer.setCurrentNativeFocusOwner(this.proxy);
/* 102 */       this.container.focusGained(this.handle);
/* 103 */       break;
/*     */     case 1005:
/* 105 */       XKeyboardFocusManagerPeer.setCurrentNativeFocusOwner(null);
/* 106 */       this.container.focusLost(this.handle);
/* 107 */       break;
/*     */     case 401:
/*     */     case 402:
/* 110 */       if (!((InputEvent)paramAWTEvent).isConsumed())
/* 111 */         this.container.forwardKeyEvent(this.handle, (KeyEvent)paramAWTEvent); break;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void coalescePaintEvent(PaintEvent paramPaintEvent) {
/*     */   }
/*     */ 
/* 118 */   public Point getLocationOnScreen() { XWindowAttributes localXWindowAttributes = new XWindowAttributes();
/* 119 */     XToolkit.awtLock();
/*     */     try {
/* 121 */       XlibWrapper.XGetWindowAttributes(XToolkit.getDisplay(), this.handle, localXWindowAttributes.pData);
/* 122 */       return new Point(localXWindowAttributes.get_x(), localXWindowAttributes.get_y());
/*     */     } finally {
/* 124 */       XToolkit.awtUnlock();
/* 125 */       localXWindowAttributes.dispose();
/*     */     } }
/*     */ 
/*     */   public Dimension getPreferredSize() {
/* 129 */     XToolkit.awtLock();
/* 130 */     long l = XlibWrapper.XAllocSizeHints();
/*     */     try {
/* 132 */       XSizeHints localXSizeHints = new XSizeHints(l);
/* 133 */       XlibWrapper.XGetWMNormalHints(XToolkit.getDisplay(), this.handle, l, XlibWrapper.larg1);
/* 134 */       Dimension localDimension1 = new Dimension(localXSizeHints.get_width(), localXSizeHints.get_height());
/* 135 */       return localDimension1;
/*     */     } finally {
/* 137 */       XlibWrapper.XFree(l);
/* 138 */       XToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/* 142 */   public Dimension getMinimumSize() { XToolkit.awtLock();
/* 143 */     long l = XlibWrapper.XAllocSizeHints();
/*     */     try {
/* 145 */       XSizeHints localXSizeHints = new XSizeHints(l);
/* 146 */       XlibWrapper.XGetWMNormalHints(XToolkit.getDisplay(), this.handle, l, XlibWrapper.larg1);
/* 147 */       Dimension localDimension1 = new Dimension(localXSizeHints.get_min_width(), localXSizeHints.get_min_height());
/* 148 */       return localDimension1;
/*     */     } finally {
/* 150 */       XlibWrapper.XFree(l);
/* 151 */       XToolkit.awtUnlock();
/*     */     } } 
/*     */   public ColorModel getColorModel() {
/* 154 */     return null; } 
/* 155 */   public Toolkit getToolkit() { return Toolkit.getDefaultToolkit(); } 
/*     */   public Graphics getGraphics() {
/* 157 */     return null; } 
/* 158 */   public FontMetrics getFontMetrics(Font paramFont) { return null; } 
/*     */   public void dispose() {
/* 160 */     this.container.detachChild(this.handle);
/*     */   }
/*     */   public void setForeground(Color paramColor) {
/*     */   }
/*     */   public void setBackground(Color paramColor) {
/*     */   }
/*     */   public void setFont(Font paramFont) {  } 
/*     */   public void updateCursorImmediately() {  } 
/* 168 */   void postEvent(AWTEvent paramAWTEvent) { XToolkit.postEvent(XToolkit.targetToAppContext(this.proxy), paramAWTEvent); }
/*     */ 
/*     */ 
/*     */   boolean simulateMotifRequestFocus(Component paramComponent, boolean paramBoolean1, boolean paramBoolean2, long paramLong)
/*     */   {
/* 174 */     if (paramComponent == null) {
/* 175 */       paramComponent = this.proxy;
/*     */     }
/* 177 */     Component localComponent = XKeyboardFocusManagerPeer.getCurrentNativeFocusOwner();
/* 178 */     if ((localComponent != null) && (localComponent.getPeer() == null)) {
/* 179 */       localComponent = null;
/*     */     }
/* 181 */     FocusEvent localFocusEvent1 = new FocusEvent(paramComponent, 1004, false, localComponent);
/* 182 */     FocusEvent localFocusEvent2 = null;
/* 183 */     if (localComponent != null) {
/* 184 */       localFocusEvent2 = new FocusEvent(localComponent, 1005, false, paramComponent);
/*     */     }
/*     */ 
/* 188 */     if (localFocusEvent2 != null) {
/* 189 */       postEvent(XComponentPeer.wrapInSequenced(localFocusEvent2));
/*     */     }
/* 191 */     postEvent(XComponentPeer.wrapInSequenced(localFocusEvent1));
/*     */ 
/* 193 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean requestFocus(Component paramComponent, boolean paramBoolean1, boolean paramBoolean2, long paramLong, CausedFocusEvent.Cause paramCause)
/*     */   {
/* 202 */     int i = XKeyboardFocusManagerPeer.shouldNativelyFocusHeavyweight(this.proxy, paramComponent, paramBoolean1, false, paramLong, paramCause);
/*     */ 
/* 206 */     switch (i) {
/*     */     case 0:
/* 208 */       return false;
/*     */     case 2:
/* 221 */       Container localContainer = this.proxy.getParent();
/*     */ 
/* 223 */       while ((localContainer != null) && (!(localContainer instanceof Window))) {
/* 224 */         localContainer = localContainer.getParent();
/*     */       }
/* 226 */       if (localContainer != null) {
/* 227 */         Window localWindow = (Window)localContainer;
/*     */ 
/* 229 */         if ((!localWindow.isFocused()) && (XKeyboardFocusManagerPeer.getCurrentNativeFocusedWindow() == localWindow))
/*     */         {
/* 232 */           return true;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 241 */       return simulateMotifRequestFocus(paramComponent, paramBoolean1, paramBoolean2, paramLong);
/*     */     case 1:
/* 245 */       return true;
/*     */     }
/* 247 */     return false;
/*     */   }
/*     */   public boolean isFocusable() {
/* 250 */     return true;
/*     */   }
/*     */   public Image createImage(ImageProducer paramImageProducer) {
/* 253 */     return null; } 
/* 254 */   public Image createImage(int paramInt1, int paramInt2) { return null; } 
/* 255 */   public VolatileImage createVolatileImage(int paramInt1, int paramInt2) { return null; } 
/* 256 */   public boolean prepareImage(Image paramImage, int paramInt1, int paramInt2, ImageObserver paramImageObserver) { return false; } 
/* 257 */   public int checkImage(Image paramImage, int paramInt1, int paramInt2, ImageObserver paramImageObserver) { return 0; } 
/* 258 */   public GraphicsConfiguration getGraphicsConfiguration() { return null; } 
/* 259 */   public boolean handlesWheelScrolling() { return true; } 
/*     */   public void createBuffers(int paramInt, BufferCapabilities paramBufferCapabilities) throws AWTException {
/*     */   }
/* 262 */   public Image getBackBuffer() { return null; }
/*     */ 
/*     */   public void flip(int paramInt1, int paramInt2, int paramInt3, int paramInt4, BufferCapabilities.FlipContents paramFlipContents)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void destroyBuffers()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void layout()
/*     */   {
/*     */   }
/*     */ 
/*     */   public Dimension preferredSize() {
/* 277 */     return getPreferredSize();
/*     */   }
/*     */ 
/*     */   public Dimension minimumSize()
/*     */   {
/* 284 */     return getMinimumSize();
/*     */   }
/*     */ 
/*     */   public void show()
/*     */   {
/* 291 */     setVisible(true);
/*     */   }
/*     */ 
/*     */   public void hide()
/*     */   {
/* 298 */     setVisible(false);
/*     */   }
/*     */ 
/*     */   public void enable()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void disable()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void reshape(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/* 315 */     setBounds(paramInt1, paramInt2, paramInt3, paramInt4, 3);
/*     */   }
/*     */ 
/*     */   Window getTopLevel(Component paramComponent) {
/* 319 */     while ((paramComponent != null) && (!(paramComponent instanceof Window))) {
/* 320 */       paramComponent = paramComponent.getParent();
/*     */     }
/* 322 */     return (Window)paramComponent;
/*     */   }
/*     */ 
/*     */   void childResized() {
/* 326 */     XToolkit.postEvent(XToolkit.targetToAppContext(this.proxy), new ComponentEvent(this.proxy, 101));
/* 327 */     this.container.childResized(this.proxy);
/*     */   }
/*     */ 
/*     */   void handlePropertyNotify(XEvent paramXEvent)
/*     */   {
/* 336 */     XPropertyEvent localXPropertyEvent = paramXEvent.get_xproperty();
/* 337 */     if (localXPropertyEvent.get_atom() == 40L)
/* 338 */       childResized();
/*     */   }
/*     */ 
/*     */   void handleConfigureNotify(XEvent paramXEvent) {
/* 342 */     childResized();
/*     */   }
/*     */   public void dispatchEvent(XEvent paramXEvent) {
/* 345 */     int i = paramXEvent.get_type();
/* 346 */     switch (i) {
/*     */     case 28:
/* 348 */       handlePropertyNotify(paramXEvent);
/* 349 */       break;
/*     */     case 22:
/* 351 */       handleConfigureNotify(paramXEvent);
/*     */     }
/*     */   }
/*     */ 
/*     */   void requestXEmbedFocus()
/*     */   {
/* 357 */     postEvent(new InvocationEvent(this.proxy, new Runnable() {
/*     */       public void run() {
/* 359 */         XEmbedChildProxyPeer.this.proxy.requestFocusInWindow();
/*     */       } } ));
/*     */   }
/*     */ 
/*     */   public void reparent(ContainerPeer paramContainerPeer) {
/*     */   }
/*     */ 
/*     */   public boolean isReparentSupported() {
/* 367 */     return false;
/*     */   }
/*     */   public Rectangle getBounds() {
/* 370 */     XWindowAttributes localXWindowAttributes = new XWindowAttributes();
/* 371 */     XToolkit.awtLock();
/*     */     try {
/* 373 */       XlibWrapper.XGetWindowAttributes(XToolkit.getDisplay(), this.handle, localXWindowAttributes.pData);
/* 374 */       return new Rectangle(localXWindowAttributes.get_x(), localXWindowAttributes.get_y(), localXWindowAttributes.get_width(), localXWindowAttributes.get_height());
/*     */     } finally {
/* 376 */       XToolkit.awtUnlock();
/* 377 */       localXWindowAttributes.dispose();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setBoundsOperation(int paramInt) {
/*     */   }
/*     */ 
/*     */   public void applyShape(Region paramRegion) {
/*     */   }
/*     */ 
/*     */   public void setZOrder(ComponentPeer paramComponentPeer) {
/*     */   }
/*     */ 
/* 390 */   public boolean updateGraphicsData(GraphicsConfiguration paramGraphicsConfiguration) { return false; }
/*     */ 
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XEmbedChildProxyPeer
 * JD-Core Version:    0.6.2
 */