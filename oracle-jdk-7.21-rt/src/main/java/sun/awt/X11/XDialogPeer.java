/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.awt.Dialog;
/*     */ import java.awt.Dialog.ModalityType;
/*     */ import java.awt.Window;
/*     */ import java.awt.peer.DialogPeer;
/*     */ import java.util.List;
/*     */ import java.util.Vector;
/*     */ import sun.awt.AWTAccessor;
/*     */ import sun.awt.AWTAccessor.ComponentAccessor;
/*     */ 
/*     */ class XDialogPeer extends XDecoratedPeer
/*     */   implements DialogPeer
/*     */ {
/*     */   private Boolean undecorated;
/*     */ 
/*     */   XDialogPeer(Dialog paramDialog)
/*     */   {
/*  40 */     super(paramDialog);
/*     */   }
/*     */ 
/*     */   public void preInit(XCreateWindowParams paramXCreateWindowParams) {
/*  44 */     super.preInit(paramXCreateWindowParams);
/*     */ 
/*  46 */     Dialog localDialog = (Dialog)this.target;
/*  47 */     this.undecorated = Boolean.valueOf(localDialog.isUndecorated());
/*  48 */     this.winAttr.nativeDecor = (!localDialog.isUndecorated());
/*  49 */     if (this.winAttr.nativeDecor)
/*  50 */       this.winAttr.decorations = XWindowAttributesData.AWT_DECOR_ALL;
/*     */     else {
/*  52 */       this.winAttr.decorations = XWindowAttributesData.AWT_DECOR_NONE;
/*     */     }
/*  54 */     this.winAttr.functions = 1;
/*  55 */     this.winAttr.isResizable = true;
/*  56 */     this.winAttr.initialResizability = localDialog.isResizable();
/*  57 */     this.winAttr.title = localDialog.getTitle();
/*  58 */     this.winAttr.initialState = XWindowAttributesData.NORMAL;
/*     */   }
/*     */ 
/*     */   public void setVisible(boolean paramBoolean) {
/*  62 */     XToolkit.awtLock();
/*     */     try {
/*  64 */       Dialog localDialog = (Dialog)this.target;
/*  65 */       if (paramBoolean) {
/*  66 */         if ((localDialog.getModalityType() != Dialog.ModalityType.MODELESS) && 
/*  67 */           (!isModalBlocked()))
/*  68 */           XBaseWindow.ungrabInput();
/*     */       }
/*     */       else
/*     */       {
/*  72 */         restoreTransientFor(this);
/*  73 */         this.prevTransientFor = null;
/*  74 */         this.nextTransientFor = null;
/*     */       }
/*     */     } finally {
/*  77 */       XToolkit.awtUnlock();
/*     */     }
/*     */ 
/*  80 */     super.setVisible(paramBoolean);
/*     */   }
/*     */ 
/*     */   boolean isTargetUndecorated()
/*     */   {
/*  85 */     if (this.undecorated != null) {
/*  86 */       return this.undecorated.booleanValue();
/*     */     }
/*  88 */     return ((Dialog)this.target).isUndecorated();
/*     */   }
/*     */ 
/*     */   int getDecorations()
/*     */   {
/*  93 */     int i = super.getDecorations();
/*     */ 
/*  95 */     if ((i & 0x1) != 0)
/*  96 */       i |= 96;
/*     */     else {
/*  98 */       i &= -97;
/*     */     }
/* 100 */     return i;
/*     */   }
/*     */ 
/*     */   int getFunctions() {
/* 104 */     int i = super.getFunctions();
/*     */ 
/* 106 */     if ((i & 0x1) != 0)
/* 107 */       i |= 24;
/*     */     else {
/* 109 */       i &= -25;
/*     */     }
/* 111 */     return i;
/*     */   }
/*     */ 
/*     */   public void blockWindows(List<Window> paramList) {
/* 115 */     Vector localVector = null;
/* 116 */     XToolkit.awtLock();
/*     */     try {
/* 118 */       localVector = XWindowPeer.collectJavaToplevels();
/* 119 */       for (Window localWindow : paramList) {
/* 120 */         XWindowPeer localXWindowPeer = (XWindowPeer)AWTAccessor.getComponentAccessor().getPeer(localWindow);
/* 121 */         if (localXWindowPeer != null)
/* 122 */           localXWindowPeer.setModalBlocked((Dialog)this.target, true, localVector);
/*     */       }
/*     */     }
/*     */     finally {
/* 126 */       XToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   boolean isFocusedWindowModalBlocker()
/*     */   {
/* 138 */     Window localWindow = XKeyboardFocusManagerPeer.getCurrentNativeFocusedWindow();
/* 139 */     XWindowPeer localXWindowPeer = null;
/*     */ 
/* 141 */     if (localWindow != null) {
/* 142 */       localXWindowPeer = (XWindowPeer)AWTAccessor.getComponentAccessor().getPeer(localWindow);
/*     */     }
/*     */     else
/*     */     {
/* 149 */       localXWindowPeer = getNativeFocusedWindowPeer();
/*     */     }
/* 151 */     synchronized (getStateLock()) {
/* 152 */       if ((localXWindowPeer != null) && (localXWindowPeer.modalBlocker == this.target)) {
/* 153 */         return true;
/*     */       }
/*     */     }
/* 156 */     return super.isFocusedWindowModalBlocker();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XDialogPeer
 * JD-Core Version:    0.6.2
 */