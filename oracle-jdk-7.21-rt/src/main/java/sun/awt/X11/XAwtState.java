/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.awt.Component;
/*     */ import java.lang.ref.WeakReference;
/*     */ 
/*     */ class XAwtState
/*     */ {
/*  43 */   private static WeakReference componentMouseEnteredRef = null;
/*     */ 
/*  80 */   private static boolean inManualGrab = false;
/*     */ 
/*  86 */   private static WeakReference grabWindowRef = null;
/*     */ 
/*     */   static void setComponentMouseEntered(Component paramComponent)
/*     */   {
/*  46 */     XToolkit.awtLock();
/*     */     try {
/*  48 */       if (paramComponent == null) {
/*  49 */         componentMouseEnteredRef = null;
/*     */       }
/*  52 */       else if (paramComponent != getComponentMouseEntered())
/*  53 */         componentMouseEnteredRef = new WeakReference(paramComponent);
/*     */     }
/*     */     finally {
/*  56 */       XToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   static Component getComponentMouseEntered() {
/*  61 */     XToolkit.awtLock();
/*     */     try
/*     */     {
/*     */       Component localComponent;
/*  63 */       if (componentMouseEnteredRef == null) {
/*  64 */         return null;
/*     */       }
/*  66 */       return (Component)componentMouseEnteredRef.get();
/*     */     } finally {
/*  68 */       XToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   static boolean isManualGrab()
/*     */   {
/*  83 */     return inManualGrab;
/*     */   }
/*     */ 
/*     */   static void setGrabWindow(XBaseWindow paramXBaseWindow)
/*     */   {
/*  93 */     setGrabWindow(paramXBaseWindow, false);
/*     */   }
/*     */ 
/*     */   static void setAutoGrabWindow(XBaseWindow paramXBaseWindow)
/*     */   {
/* 100 */     setGrabWindow(paramXBaseWindow, true);
/*     */   }
/*     */ 
/*     */   private static void setGrabWindow(XBaseWindow paramXBaseWindow, boolean paramBoolean) {
/* 104 */     XToolkit.awtLock();
/*     */     try {
/* 106 */       if ((inManualGrab) && (paramBoolean)) {
/*     */         return;
/*     */       }
/* 109 */       inManualGrab = (paramXBaseWindow != null) && (!paramBoolean);
/* 110 */       if (paramXBaseWindow == null) {
/* 111 */         grabWindowRef = null;
/*     */       }
/* 114 */       else if (paramXBaseWindow != getGrabWindow())
/* 115 */         grabWindowRef = new WeakReference(paramXBaseWindow);
/*     */     }
/*     */     finally {
/* 118 */       XToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   static XBaseWindow getGrabWindow() {
/* 123 */     XToolkit.awtLock();
/*     */     try {
/* 125 */       if (grabWindowRef == null) {
/* 126 */         return null;
/*     */       }
/* 128 */       XBaseWindow localXBaseWindow1 = (XBaseWindow)grabWindowRef.get();
/* 129 */       if ((localXBaseWindow1 != null) && (localXBaseWindow1.isDisposed())) {
/* 130 */         localXBaseWindow1 = null;
/* 131 */         grabWindowRef = null;
/* 132 */       } else if (localXBaseWindow1 == null) {
/* 133 */         grabWindowRef = null;
/*     */       }
/* 135 */       return localXBaseWindow1;
/*     */     } finally {
/* 137 */       XToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XAwtState
 * JD-Core Version:    0.6.2
 */