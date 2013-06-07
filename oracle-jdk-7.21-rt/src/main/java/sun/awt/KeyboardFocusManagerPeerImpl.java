/*     */ package sun.awt;
/*     */ 
/*     */ import java.awt.Canvas;
/*     */ import java.awt.Component;
/*     */ import java.awt.KeyboardFocusManager;
/*     */ import java.awt.Panel;
/*     */ import java.awt.Scrollbar;
/*     */ import java.awt.Window;
/*     */ import java.awt.peer.ComponentPeer;
/*     */ import java.awt.peer.KeyboardFocusManagerPeer;
/*     */ import sun.util.logging.PlatformLogger;
/*     */ 
/*     */ public abstract class KeyboardFocusManagerPeerImpl
/*     */   implements KeyboardFocusManagerPeer
/*     */ {
/*  46 */   private static final PlatformLogger focusLog = PlatformLogger.getLogger("sun.awt.focus.KeyboardFocusManagerPeerImpl");
/*     */ 
/*  48 */   private static AWTAccessor.KeyboardFocusManagerAccessor kfmAccessor = AWTAccessor.getKeyboardFocusManagerAccessor();
/*     */   public static final int SNFH_FAILURE = 0;
/*     */   public static final int SNFH_SUCCESS_HANDLED = 1;
/*     */   public static final int SNFH_SUCCESS_PROCEED = 2;
/*     */   protected KeyboardFocusManager manager;
/*     */ 
/*     */   public KeyboardFocusManagerPeerImpl(KeyboardFocusManager paramKeyboardFocusManager)
/*     */   {
/*  59 */     this.manager = paramKeyboardFocusManager;
/*     */   }
/*     */ 
/*     */   public void clearGlobalFocusOwner(Window paramWindow)
/*     */   {
/*  64 */     if (paramWindow != null) {
/*  65 */       Component localComponent = paramWindow.getFocusOwner();
/*  66 */       if (focusLog.isLoggable(500))
/*  67 */         focusLog.fine("Clearing global focus owner " + localComponent);
/*  68 */       if (localComponent != null) {
/*  69 */         CausedFocusEvent localCausedFocusEvent = new CausedFocusEvent(localComponent, 1005, false, null, CausedFocusEvent.Cause.CLEAR_GLOBAL_FOCUS_OWNER);
/*     */ 
/*  71 */         SunToolkit.postPriorityEvent(localCausedFocusEvent);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static boolean shouldFocusOnClick(Component paramComponent)
/*     */   {
/*  84 */     int i = 0;
/*     */ 
/*  91 */     if (((paramComponent instanceof Canvas)) || ((paramComponent instanceof Scrollbar)))
/*     */     {
/*  94 */       i = 1;
/*     */     }
/*  97 */     else if ((paramComponent instanceof Panel)) {
/*  98 */       i = ((Panel)paramComponent).getComponentCount() == 0 ? 1 : 0;
/*     */     }
/*     */     else
/*     */     {
/* 103 */       Object localObject = paramComponent != null ? paramComponent.getPeer() : null;
/* 104 */       i = localObject != null ? localObject.isFocusable() : 0;
/*     */     }
/* 106 */     return (i != 0) && (AWTAccessor.getComponentAccessor().canBeFocusOwner(paramComponent));
/*     */   }
/*     */ 
/*     */   public static boolean deliverFocus(Component paramComponent1, Component paramComponent2, boolean paramBoolean1, boolean paramBoolean2, long paramLong, CausedFocusEvent.Cause paramCause, Component paramComponent3)
/*     */   {
/* 121 */     if (paramComponent1 == null) {
/* 122 */       paramComponent1 = paramComponent2;
/*     */     }
/*     */ 
/* 125 */     Component localComponent = paramComponent3;
/* 126 */     if ((localComponent != null) && (localComponent.getPeer() == null)) {
/* 127 */       localComponent = null;
/*     */     }
/* 129 */     if (localComponent != null) {
/* 130 */       localCausedFocusEvent = new CausedFocusEvent(localComponent, 1005, false, paramComponent1, paramCause);
/*     */ 
/* 133 */       if (focusLog.isLoggable(400))
/* 134 */         focusLog.finer("Posting focus event: " + localCausedFocusEvent);
/* 135 */       SunToolkit.postPriorityEvent(localCausedFocusEvent);
/*     */     }
/*     */ 
/* 138 */     CausedFocusEvent localCausedFocusEvent = new CausedFocusEvent(paramComponent1, 1004, false, localComponent, paramCause);
/*     */ 
/* 141 */     if (focusLog.isLoggable(400))
/* 142 */       focusLog.finer("Posting focus event: " + localCausedFocusEvent);
/* 143 */     SunToolkit.postPriorityEvent(localCausedFocusEvent);
/* 144 */     return true;
/*     */   }
/*     */ 
/*     */   public static boolean requestFocusFor(Component paramComponent, CausedFocusEvent.Cause paramCause)
/*     */   {
/* 149 */     return AWTAccessor.getComponentAccessor().requestFocus(paramComponent, paramCause);
/*     */   }
/*     */ 
/*     */   public static int shouldNativelyFocusHeavyweight(Component paramComponent1, Component paramComponent2, boolean paramBoolean1, boolean paramBoolean2, long paramLong, CausedFocusEvent.Cause paramCause)
/*     */   {
/* 160 */     return kfmAccessor.shouldNativelyFocusHeavyweight(paramComponent1, paramComponent2, paramBoolean1, paramBoolean2, paramLong, paramCause);
/*     */   }
/*     */ 
/*     */   public static void removeLastFocusRequest(Component paramComponent)
/*     */   {
/* 165 */     kfmAccessor.removeLastFocusRequest(paramComponent);
/*     */   }
/*     */ 
/*     */   public static boolean processSynchronousLightweightTransfer(Component paramComponent1, Component paramComponent2, boolean paramBoolean1, boolean paramBoolean2, long paramLong)
/*     */   {
/* 175 */     return kfmAccessor.processSynchronousLightweightTransfer(paramComponent1, paramComponent2, paramBoolean1, paramBoolean2, paramLong);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.KeyboardFocusManagerPeerImpl
 * JD-Core Version:    0.6.2
 */