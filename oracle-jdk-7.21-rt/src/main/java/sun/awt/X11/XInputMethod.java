/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.awt.AWTException;
/*     */ import java.awt.Component;
/*     */ import java.awt.Container;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.im.spi.InputMethodContext;
/*     */ import java.awt.peer.ComponentPeer;
/*     */ import sun.awt.X11InputMethod;
/*     */ import sun.util.logging.PlatformLogger;
/*     */ 
/*     */ public class XInputMethod extends X11InputMethod
/*     */ {
/*  44 */   private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.X11.XInputMethod");
/*     */ 
/*  74 */   private static volatile long xicFocus = 0L;
/*     */ 
/*     */   public XInputMethod()
/*     */     throws AWTException
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setInputMethodContext(InputMethodContext paramInputMethodContext)
/*     */   {
/*  51 */     paramInputMethodContext.enableClientWindowNotification(this, true);
/*     */   }
/*     */ 
/*     */   public void notifyClientWindowChange(Rectangle paramRectangle) {
/*  55 */     XComponentPeer localXComponentPeer = (XComponentPeer)getPeer(this.clientComponentWindow);
/*  56 */     if (localXComponentPeer != null)
/*  57 */       adjustStatusWindow(localXComponentPeer.getContentWindow());
/*     */   }
/*     */ 
/*     */   protected boolean openXIM()
/*     */   {
/*  62 */     return openXIMNative(XToolkit.getDisplay());
/*     */   }
/*     */ 
/*     */   protected boolean createXIC() {
/*  66 */     XComponentPeer localXComponentPeer = (XComponentPeer)getPeer(this.clientComponentWindow);
/*  67 */     if (localXComponentPeer == null) {
/*  68 */       return false;
/*     */     }
/*  70 */     return createXICNative(localXComponentPeer.getContentWindow());
/*     */   }
/*     */ 
/*     */   protected void setXICFocus(ComponentPeer paramComponentPeer, boolean paramBoolean1, boolean paramBoolean2)
/*     */   {
/*  78 */     if (paramComponentPeer == null) {
/*  79 */       return;
/*     */     }
/*  81 */     xicFocus = ((XComponentPeer)paramComponentPeer).getContentWindow();
/*  82 */     setXICFocusNative(((XComponentPeer)paramComponentPeer).getContentWindow(), paramBoolean1, paramBoolean2);
/*     */   }
/*     */ 
/*     */   public static long getXICFocus()
/*     */   {
/*  88 */     return xicFocus;
/*     */   }
/*     */ 
/*     */   protected Container getParent(Component paramComponent)
/*     */   {
/*  95 */     return paramComponent.getParent();
/*     */   }
/*     */ 
/*     */   protected ComponentPeer getPeer(Component paramComponent)
/*     */   {
/* 105 */     if (log.isLoggable(500)) log.fine("Client is " + paramComponent);
/* 106 */     XComponentPeer localXComponentPeer = (XComponentPeer)XToolkit.targetToPeer(paramComponent);
/* 107 */     while ((paramComponent != null) && (localXComponentPeer == null)) {
/* 108 */       paramComponent = getParent(paramComponent);
/* 109 */       localXComponentPeer = (XComponentPeer)XToolkit.targetToPeer(paramComponent);
/*     */     }
/* 111 */     log.fine("Peer is {0}, client is {1}", new Object[] { localXComponentPeer, paramComponent });
/*     */ 
/* 113 */     if (localXComponentPeer != null) {
/* 114 */       return localXComponentPeer;
/*     */     }
/* 116 */     return null;
/*     */   }
/*     */ 
/*     */   protected synchronized void disposeImpl()
/*     */   {
/* 124 */     super.disposeImpl();
/* 125 */     this.clientComponentWindow = null;
/*     */   }
/*     */ 
/*     */   protected void awtLock() {
/* 129 */     XToolkit.awtLock();
/*     */   }
/*     */ 
/*     */   protected void awtUnlock() {
/* 133 */     XToolkit.awtUnlock();
/*     */   }
/*     */ 
/*     */   long getCurrentParentWindow() {
/* 137 */     return ((XWindow)this.clientComponentWindow.getPeer()).getContentWindow();
/*     */   }
/*     */ 
/*     */   private native boolean openXIMNative(long paramLong);
/*     */ 
/*     */   private native boolean createXICNative(long paramLong);
/*     */ 
/*     */   private native void setXICFocusNative(long paramLong, boolean paramBoolean1, boolean paramBoolean2);
/*     */ 
/*     */   private native void adjustStatusWindow(long paramLong);
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XInputMethod
 * JD-Core Version:    0.6.2
 */