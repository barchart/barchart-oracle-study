/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.awt.AWTKeyStroke;
/*     */ import java.awt.Dialog;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import sun.awt.EmbeddedFrame;
/*     */ import sun.awt.SunToolkit;
/*     */ import sun.util.logging.PlatformLogger;
/*     */ 
/*     */ public class XEmbeddedFramePeer extends XFramePeer
/*     */ {
/*  42 */   private static final PlatformLogger xembedLog = PlatformLogger.getLogger("sun.awt.X11.xembed.XEmbeddedFramePeer");
/*     */   LinkedList<AWTKeyStroke> strokes;
/*     */   XEmbedClientHelper embedder;
/*     */ 
/*     */   public XEmbeddedFramePeer(EmbeddedFrame paramEmbeddedFrame)
/*     */   {
/*  51 */     super(new XCreateWindowParams(new Object[] { "target", paramEmbeddedFrame, "visible", Boolean.TRUE, "embedded", Boolean.TRUE }));
/*     */   }
/*     */ 
/*     */   public void preInit(XCreateWindowParams paramXCreateWindowParams)
/*     */   {
/*  58 */     super.preInit(paramXCreateWindowParams);
/*  59 */     this.strokes = new LinkedList();
/*  60 */     if (supportsXEmbed())
/*  61 */       this.embedder = new XEmbedClientHelper();
/*     */   }
/*     */ 
/*     */   void postInit(XCreateWindowParams paramXCreateWindowParams) {
/*  65 */     super.postInit(paramXCreateWindowParams);
/*  66 */     if (this.embedder != null)
/*     */     {
/*  68 */       this.embedder.setClient(this);
/*     */ 
/*  70 */       this.embedder.install();
/*  71 */     } else if (getParentWindowHandle() != 0L) {
/*  72 */       XToolkit.awtLock();
/*     */       try {
/*  74 */         XlibWrapper.XReparentWindow(XToolkit.getDisplay(), getWindow(), getParentWindowHandle(), 0, 0);
/*     */       }
/*     */       finally
/*     */       {
/*  79 */         XToolkit.awtUnlock();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void dispose()
/*     */   {
/*  86 */     if (this.embedder != null)
/*     */     {
/*  88 */       this.embedder.setClient(null);
/*     */     }
/*  90 */     super.dispose();
/*     */   }
/*     */ 
/*     */   public void updateMinimumSize() {
/*     */   }
/*     */ 
/*     */   protected String getWMName() {
/*  97 */     return "JavaEmbeddedFrame";
/*     */   }
/*     */ 
/*     */   final long getParentWindowHandle() {
/* 101 */     return ((XEmbeddedFrame)this.target).handle;
/*     */   }
/*     */ 
/*     */   boolean supportsXEmbed() {
/* 105 */     return ((EmbeddedFrame)this.target).supportsXEmbed();
/*     */   }
/*     */ 
/*     */   public boolean requestWindowFocus(long paramLong, boolean paramBoolean)
/*     */   {
/* 110 */     if ((this.embedder != null) && (this.embedder.isActive())) {
/* 111 */       xembedLog.fine("Requesting focus from embedding host");
/* 112 */       return this.embedder.requestFocus();
/*     */     }
/* 114 */     xembedLog.fine("Requesting focus from X");
/* 115 */     return super.requestWindowFocus(paramLong, paramBoolean);
/*     */   }
/*     */ 
/*     */   protected void requestInitialFocus()
/*     */   {
/* 120 */     if ((this.embedder != null) && (supportsXEmbed()))
/* 121 */       this.embedder.requestFocus();
/*     */     else
/* 123 */       super.requestInitialFocus();
/*     */   }
/*     */ 
/*     */   protected boolean isEventDisabled(XEvent paramXEvent)
/*     */   {
/* 128 */     if ((this.embedder != null) && (this.embedder.isActive())) {
/* 129 */       switch (paramXEvent.get_type()) {
/*     */       case 9:
/*     */       case 10:
/* 132 */         return true;
/*     */       }
/*     */     }
/* 135 */     return super.isEventDisabled(paramXEvent);
/*     */   }
/*     */ 
/*     */   public void handleConfigureNotifyEvent(XEvent paramXEvent)
/*     */   {
/* 140 */     assert (SunToolkit.isAWTLockHeldByCurrentThread());
/* 141 */     XConfigureEvent localXConfigureEvent = paramXEvent.get_xconfigure();
/* 142 */     if (xembedLog.isLoggable(500)) {
/* 143 */       xembedLog.fine(localXConfigureEvent.toString());
/*     */     }
/*     */ 
/* 149 */     checkIfOnNewScreen(toGlobal(new Rectangle(localXConfigureEvent.get_x(), localXConfigureEvent.get_y(), localXConfigureEvent.get_width(), localXConfigureEvent.get_height())));
/*     */ 
/* 154 */     Rectangle localRectangle = getBounds();
/*     */ 
/* 156 */     synchronized (getStateLock()) {
/* 157 */       this.x = localXConfigureEvent.get_x();
/* 158 */       this.y = localXConfigureEvent.get_y();
/* 159 */       this.width = localXConfigureEvent.get_width();
/* 160 */       this.height = localXConfigureEvent.get_height();
/*     */ 
/* 162 */       this.dimensions.setClientSize(this.width, this.height);
/* 163 */       this.dimensions.setLocation(this.x, this.y);
/*     */     }
/*     */ 
/* 166 */     if (!getLocation().equals(localRectangle.getLocation())) {
/* 167 */       handleMoved(this.dimensions);
/*     */     }
/* 169 */     reconfigureContentWindow(this.dimensions);
/*     */   }
/*     */ 
/*     */   protected void traverseOutForward() {
/* 173 */     if ((this.embedder != null) && (this.embedder.isActive()) && 
/* 174 */       (this.embedder.isApplicationActive())) {
/* 175 */       xembedLog.fine("Traversing out Forward");
/* 176 */       this.embedder.traverseOutForward();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void traverseOutBackward()
/*     */   {
/* 182 */     if ((this.embedder != null) && (this.embedder.isActive()) && 
/* 183 */       (this.embedder.isApplicationActive())) {
/* 184 */       xembedLog.fine("Traversing out Backward");
/* 185 */       this.embedder.traverseOutBackward();
/*     */     }
/*     */   }
/*     */ 
/*     */   public Point getLocationOnScreen()
/*     */   {
/* 192 */     XToolkit.awtLock();
/*     */     try {
/* 194 */       return toGlobal(0, 0);
/*     */     } finally {
/* 196 */       XToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   public Rectangle getBounds()
/*     */   {
/* 202 */     return new Rectangle(this.x, this.y, this.width, this.height);
/*     */   }
/*     */ 
/*     */   public void setBoundsPrivate(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/* 206 */     setBounds(paramInt1, paramInt2, paramInt3, paramInt4, 16387);
/*     */   }
/*     */ 
/*     */   public Rectangle getBoundsPrivate() {
/* 210 */     int i = 0; int j = 0;
/* 211 */     int k = 0; int m = 0;
/* 212 */     XWindowAttributes localXWindowAttributes = new XWindowAttributes();
/*     */ 
/* 214 */     XToolkit.awtLock();
/*     */     try {
/* 216 */       XlibWrapper.XGetWindowAttributes(XToolkit.getDisplay(), getWindow(), localXWindowAttributes.pData);
/*     */ 
/* 218 */       i = localXWindowAttributes.get_x();
/* 219 */       j = localXWindowAttributes.get_y();
/* 220 */       k = localXWindowAttributes.get_width();
/* 221 */       m = localXWindowAttributes.get_height();
/*     */     } finally {
/* 223 */       XToolkit.awtUnlock();
/*     */     }
/* 225 */     localXWindowAttributes.dispose();
/*     */ 
/* 227 */     return new Rectangle(i, j, k, m);
/*     */   }
/*     */   void registerAccelerator(AWTKeyStroke paramAWTKeyStroke) {
/* 230 */     if (paramAWTKeyStroke == null) return;
/* 231 */     this.strokes.add(paramAWTKeyStroke);
/* 232 */     if ((this.embedder != null) && (this.embedder.isActive()))
/* 233 */       this.embedder.registerAccelerator(paramAWTKeyStroke, this.strokes.size() - 1);
/*     */   }
/*     */ 
/*     */   void unregisterAccelerator(AWTKeyStroke paramAWTKeyStroke)
/*     */   {
/* 238 */     if (paramAWTKeyStroke == null) return;
/* 239 */     if ((this.embedder != null) && (this.embedder.isActive())) {
/* 240 */       int i = this.strokes.indexOf(paramAWTKeyStroke);
/* 241 */       this.embedder.unregisterAccelerator(i);
/*     */     }
/*     */   }
/*     */ 
/*     */   void notifyStarted()
/*     */   {
/* 247 */     if ((this.embedder != null) && (this.embedder.isActive())) {
/* 248 */       int i = 0;
/* 249 */       Iterator localIterator = this.strokes.iterator();
/* 250 */       while (localIterator.hasNext()) {
/* 251 */         this.embedder.registerAccelerator((AWTKeyStroke)localIterator.next(), i++);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 257 */     updateDropTarget();
/*     */   }
/*     */   void notifyStopped() {
/* 260 */     if ((this.embedder != null) && (this.embedder.isActive()))
/* 261 */       for (int i = this.strokes.size() - 1; i >= 0; i--)
/* 262 */         this.embedder.unregisterAccelerator(i);
/*     */   }
/*     */ 
/*     */   long getFocusTargetWindow()
/*     */   {
/* 268 */     return getWindow();
/*     */   }
/*     */ 
/*     */   boolean isXEmbedActive() {
/* 272 */     return (this.embedder != null) && (this.embedder.isActive());
/*     */   }
/*     */ 
/*     */   public int getAbsoluteX()
/*     */   {
/* 277 */     Point localPoint = XlibUtil.translateCoordinates(getWindow(), XToolkit.getDefaultRootWindow(), new Point(0, 0));
/*     */ 
/* 280 */     return localPoint != null ? localPoint.x : 0;
/*     */   }
/*     */ 
/*     */   public int getAbsoluteY()
/*     */   {
/* 285 */     Point localPoint = XlibUtil.translateCoordinates(getWindow(), XToolkit.getDefaultRootWindow(), new Point(0, 0));
/*     */ 
/* 288 */     return localPoint != null ? localPoint.y : 0;
/*     */   }
/*     */ 
/*     */   public int getWidth() {
/* 292 */     return this.width;
/*     */   }
/*     */   public int getHeight() {
/* 295 */     return this.height;
/*     */   }
/*     */ 
/*     */   public Dimension getSize() {
/* 299 */     return new Dimension(this.width, this.height);
/*     */   }
/*     */ 
/*     */   public void setModalBlocked(Dialog paramDialog, boolean paramBoolean)
/*     */   {
/* 305 */     super.setModalBlocked(paramDialog, paramBoolean);
/*     */ 
/* 307 */     EmbeddedFrame localEmbeddedFrame = (EmbeddedFrame)this.target;
/* 308 */     localEmbeddedFrame.notifyModalBlocked(paramDialog, paramBoolean);
/*     */   }
/*     */ 
/*     */   public void synthesizeFocusInOut(boolean paramBoolean) {
/* 312 */     XFocusChangeEvent localXFocusChangeEvent = new XFocusChangeEvent();
/*     */ 
/* 314 */     XToolkit.awtLock();
/*     */     try {
/* 316 */       localXFocusChangeEvent.set_type(paramBoolean ? 9 : 10);
/* 317 */       localXFocusChangeEvent.set_window(getFocusProxy().getWindow());
/* 318 */       localXFocusChangeEvent.set_mode(0);
/* 319 */       XlibWrapper.XSendEvent(XToolkit.getDisplay(), getFocusProxy().getWindow(), false, 0L, localXFocusChangeEvent.pData);
/*     */     }
/*     */     finally {
/* 322 */       XToolkit.awtUnlock();
/* 323 */       localXFocusChangeEvent.dispose();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XEmbeddedFramePeer
 * JD-Core Version:    0.6.2
 */