/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.awt.datatransfer.Transferable;
/*     */ import java.awt.dnd.InvalidDnDOperationException;
/*     */ import java.util.Map;
/*     */ 
/*     */ abstract class XDragSourceProtocol
/*     */ {
/*     */   private final XDragSourceProtocolListener listener;
/*  44 */   private boolean initialized = false;
/*     */ 
/*  46 */   private long targetWindow = 0L;
/*  47 */   private long targetProxyWindow = 0L;
/*  48 */   private int targetProtocolVersion = 0;
/*  49 */   private long targetWindowMask = 0L;
/*     */ 
/*     */   static long getDragSourceWindow()
/*     */   {
/*  53 */     return XWindow.getXAWTRootWindow().getWindow();
/*     */   }
/*     */ 
/*     */   protected XDragSourceProtocol(XDragSourceProtocolListener paramXDragSourceProtocolListener) {
/*  57 */     if (paramXDragSourceProtocolListener == null) {
/*  58 */       throw new NullPointerException("Null XDragSourceProtocolListener");
/*     */     }
/*  60 */     this.listener = paramXDragSourceProtocolListener;
/*     */   }
/*     */ 
/*     */   protected final XDragSourceProtocolListener getProtocolListener() {
/*  64 */     return this.listener;
/*     */   }
/*     */ 
/*     */   public abstract String getProtocolName();
/*     */ 
/*     */   public final void initializeDrag(int paramInt, Transferable paramTransferable, Map paramMap, long[] paramArrayOfLong)
/*     */     throws InvalidDnDOperationException, IllegalArgumentException, XException
/*     */   {
/*  90 */     XToolkit.awtLock();
/*     */     try {
/*     */       try {
/*  93 */         if (this.initialized) {
/*  94 */           throw new InvalidDnDOperationException("Already initialized");
/*     */         }
/*     */ 
/*  97 */         initializeDragImpl(paramInt, paramTransferable, paramMap, paramArrayOfLong);
/*     */ 
/*  99 */         this.initialized = true;
/*     */       } finally {
/* 101 */         if (!this.initialized)
/* 102 */           cleanup();
/*     */       }
/*     */     }
/*     */     finally {
/* 106 */       XToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected abstract void initializeDragImpl(int paramInt, Transferable paramTransferable, Map paramMap, long[] paramArrayOfLong)
/*     */     throws InvalidDnDOperationException, IllegalArgumentException, XException;
/*     */ 
/*     */   public void cleanup()
/*     */   {
/* 123 */     this.initialized = false;
/* 124 */     cleanupTargetInfo();
/*     */   }
/*     */ 
/*     */   public void cleanupTargetInfo()
/*     */   {
/* 133 */     this.targetWindow = 0L;
/* 134 */     this.targetProxyWindow = 0L;
/* 135 */     this.targetProtocolVersion = 0;
/*     */   }
/*     */ 
/*     */   public abstract boolean processClientMessage(XClientMessageEvent paramXClientMessageEvent)
/*     */     throws XException;
/*     */ 
/*     */   public final boolean attachTargetWindow(long paramLong1, long paramLong2)
/*     */   {
/* 148 */     assert (XToolkit.isAWTLockHeldByCurrentThread());
/*     */ 
/* 150 */     TargetWindowInfo localTargetWindowInfo = getTargetWindowInfo(paramLong1);
/* 151 */     if (localTargetWindowInfo == null) {
/* 152 */       return false;
/*     */     }
/* 154 */     this.targetWindow = paramLong1;
/* 155 */     this.targetProxyWindow = localTargetWindowInfo.getProxyWindow();
/* 156 */     this.targetProtocolVersion = localTargetWindowInfo.getProtocolVersion();
/* 157 */     return true;
/*     */   }
/*     */ 
/*     */   public abstract TargetWindowInfo getTargetWindowInfo(long paramLong);
/*     */ 
/*     */   public abstract void sendEnterMessage(long[] paramArrayOfLong, int paramInt1, int paramInt2, long paramLong);
/*     */ 
/*     */   public abstract void sendMoveMessage(int paramInt1, int paramInt2, int paramInt3, int paramInt4, long paramLong);
/*     */ 
/*     */   public abstract void sendLeaveMessage(long paramLong);
/*     */ 
/*     */   protected abstract void sendDropMessage(int paramInt1, int paramInt2, int paramInt3, int paramInt4, long paramLong);
/*     */ 
/*     */   public final void initiateDrop(int paramInt1, int paramInt2, int paramInt3, int paramInt4, long paramLong)
/*     */   {
/* 182 */     XWindowAttributes localXWindowAttributes = new XWindowAttributes();
/*     */     try {
/* 184 */       XToolkit.WITH_XERROR_HANDLER(XErrorHandler.IgnoreBadWindowHandler.getInstance());
/* 185 */       int i = XlibWrapper.XGetWindowAttributes(XToolkit.getDisplay(), this.targetWindow, localXWindowAttributes.pData);
/*     */ 
/* 188 */       XToolkit.RESTORE_XERROR_HANDLER();
/*     */ 
/* 190 */       if ((i == 0) || ((XToolkit.saved_error != null) && (XToolkit.saved_error.get_error_code() != 0)))
/*     */       {
/* 193 */         throw new XException("XGetWindowAttributes failed");
/*     */       }
/*     */ 
/* 196 */       this.targetWindowMask = localXWindowAttributes.get_your_event_mask();
/*     */     } finally {
/* 198 */       localXWindowAttributes.dispose();
/*     */     }
/*     */ 
/* 201 */     XToolkit.WITH_XERROR_HANDLER(XErrorHandler.IgnoreBadWindowHandler.getInstance());
/* 202 */     XlibWrapper.XSelectInput(XToolkit.getDisplay(), this.targetWindow, this.targetWindowMask | 0x20000);
/*     */ 
/* 206 */     XToolkit.RESTORE_XERROR_HANDLER();
/*     */ 
/* 208 */     if ((XToolkit.saved_error != null) && (XToolkit.saved_error.get_error_code() != 0))
/*     */     {
/* 210 */       throw new XException("XSelectInput failed");
/*     */     }
/*     */ 
/* 213 */     sendDropMessage(paramInt1, paramInt2, paramInt3, paramInt4, paramLong);
/*     */   }
/*     */ 
/*     */   protected final void finalizeDrop() {
/* 217 */     XToolkit.WITH_XERROR_HANDLER(XErrorHandler.IgnoreBadWindowHandler.getInstance());
/* 218 */     XlibWrapper.XSelectInput(XToolkit.getDisplay(), this.targetWindow, this.targetWindowMask);
/*     */ 
/* 220 */     XToolkit.RESTORE_XERROR_HANDLER();
/*     */   }
/*     */ 
/*     */   public abstract boolean processProxyModeEvent(XClientMessageEvent paramXClientMessageEvent, long paramLong);
/*     */ 
/*     */   protected final long getTargetWindow()
/*     */   {
/* 227 */     return this.targetWindow;
/*     */   }
/*     */ 
/*     */   protected final long getTargetProxyWindow() {
/* 231 */     if (this.targetProxyWindow != 0L) {
/* 232 */       return this.targetProxyWindow;
/*     */     }
/* 234 */     return this.targetWindow;
/*     */   }
/*     */ 
/*     */   protected final int getTargetProtocolVersion()
/*     */   {
/* 239 */     return this.targetProtocolVersion;
/*     */   }
/*     */   public static class TargetWindowInfo {
/*     */     private final long proxyWindow;
/*     */     private final int protocolVersion;
/*     */ 
/* 246 */     public TargetWindowInfo(long paramLong, int paramInt) { this.proxyWindow = paramLong;
/* 247 */       this.protocolVersion = paramInt; }
/*     */ 
/*     */     public long getProxyWindow() {
/* 250 */       return this.proxyWindow;
/*     */     }
/*     */     public int getProtocolVersion() {
/* 253 */       return this.protocolVersion;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XDragSourceProtocol
 * JD-Core Version:    0.6.2
 */