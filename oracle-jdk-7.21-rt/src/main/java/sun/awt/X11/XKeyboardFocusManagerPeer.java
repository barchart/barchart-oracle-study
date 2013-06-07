/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.awt.Component;
/*     */ import java.awt.KeyboardFocusManager;
/*     */ import java.awt.Window;
/*     */ import sun.awt.CausedFocusEvent.Cause;
/*     */ import sun.awt.KeyboardFocusManagerPeerImpl;
/*     */ import sun.util.logging.PlatformLogger;
/*     */ 
/*     */ public class XKeyboardFocusManagerPeer extends KeyboardFocusManagerPeerImpl
/*     */ {
/*  46 */   private static final PlatformLogger focusLog = PlatformLogger.getLogger("sun.awt.X11.focus.XKeyboardFocusManagerPeer");
/*     */ 
/*  48 */   private static Object lock = new Object() {  } ;
/*     */   private static Component currentFocusOwner;
/*     */   private static Window currentFocusedWindow;
/*     */ 
/*  53 */   XKeyboardFocusManagerPeer(KeyboardFocusManager paramKeyboardFocusManager) { super(paramKeyboardFocusManager); }
/*     */ 
/*     */ 
/*     */   public void setCurrentFocusOwner(Component paramComponent)
/*     */   {
/*  58 */     setCurrentNativeFocusOwner(paramComponent);
/*     */   }
/*     */ 
/*     */   public Component getCurrentFocusOwner()
/*     */   {
/*  63 */     return getCurrentNativeFocusOwner();
/*     */   }
/*     */ 
/*     */   public Window getCurrentFocusedWindow()
/*     */   {
/*  68 */     return getCurrentNativeFocusedWindow();
/*     */   }
/*     */ 
/*     */   public static void setCurrentNativeFocusOwner(Component paramComponent) {
/*  72 */     synchronized (lock) {
/*  73 */       currentFocusOwner = paramComponent;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static Component getCurrentNativeFocusOwner() {
/*  78 */     synchronized (lock) {
/*  79 */       return currentFocusOwner;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void setCurrentNativeFocusedWindow(Window paramWindow) {
/*  84 */     if (focusLog.isLoggable(400)) focusLog.finer("Setting current native focused window " + paramWindow);
/*  85 */     XWindowPeer localXWindowPeer1 = null; XWindowPeer localXWindowPeer2 = null;
/*     */ 
/*  87 */     synchronized (lock) {
/*  88 */       if (currentFocusedWindow != null) {
/*  89 */         localXWindowPeer1 = (XWindowPeer)currentFocusedWindow.getPeer();
/*     */       }
/*     */ 
/*  92 */       currentFocusedWindow = paramWindow;
/*     */ 
/*  94 */       if (currentFocusedWindow != null) {
/*  95 */         localXWindowPeer2 = (XWindowPeer)currentFocusedWindow.getPeer();
/*     */       }
/*     */     }
/*     */ 
/*  99 */     if (localXWindowPeer1 != null) {
/* 100 */       localXWindowPeer1.updateSecurityWarningVisibility();
/*     */     }
/* 102 */     if (localXWindowPeer2 != null)
/* 103 */       localXWindowPeer2.updateSecurityWarningVisibility();
/*     */   }
/*     */ 
/*     */   public static Window getCurrentNativeFocusedWindow()
/*     */   {
/* 108 */     synchronized (lock) {
/* 109 */       return currentFocusedWindow;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static boolean deliverFocus(Component paramComponent1, Component paramComponent2, boolean paramBoolean1, boolean paramBoolean2, long paramLong, CausedFocusEvent.Cause paramCause)
/*     */   {
/* 121 */     return KeyboardFocusManagerPeerImpl.deliverFocus(paramComponent1, paramComponent2, paramBoolean1, paramBoolean2, paramLong, paramCause, getCurrentNativeFocusOwner());
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XKeyboardFocusManagerPeer
 * JD-Core Version:    0.6.2
 */