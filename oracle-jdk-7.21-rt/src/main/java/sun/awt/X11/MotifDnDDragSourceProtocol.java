/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.awt.datatransfer.Transferable;
/*     */ import java.awt.dnd.InvalidDnDOperationException;
/*     */ import java.util.Map;
/*     */ import sun.misc.Unsafe;
/*     */ 
/*     */ class MotifDnDDragSourceProtocol extends XDragSourceProtocol
/*     */   implements XEventDispatcher
/*     */ {
/*  45 */   private static final Unsafe unsafe = XlibWrapper.unsafe;
/*     */ 
/*  47 */   private long targetEnterServerTime = 0L;
/*     */ 
/*     */   protected MotifDnDDragSourceProtocol(XDragSourceProtocolListener paramXDragSourceProtocolListener) {
/*  50 */     super(paramXDragSourceProtocolListener);
/*  51 */     XToolkit.addEventDispatcher(XWindow.getXAWTRootWindow().getWindow(), this);
/*     */   }
/*     */ 
/*     */   static XDragSourceProtocol createInstance(XDragSourceProtocolListener paramXDragSourceProtocolListener)
/*     */   {
/*  60 */     return new MotifDnDDragSourceProtocol(paramXDragSourceProtocolListener);
/*     */   }
/*     */ 
/*     */   public String getProtocolName() {
/*  64 */     return "MotifDnD";
/*     */   }
/*     */ 
/*     */   protected void initializeDragImpl(int paramInt, Transferable paramTransferable, Map paramMap, long[] paramArrayOfLong)
/*     */     throws InvalidDnDOperationException, IllegalArgumentException, XException
/*     */   {
/*  72 */     long l = XDragSourceProtocol.getDragSourceWindow();
/*     */     try
/*     */     {
/*  76 */       int i = MotifDnDConstants.getIndexForTargetList(paramArrayOfLong);
/*     */ 
/*  78 */       MotifDnDConstants.writeDragInitiatorInfoStruct(l, i);
/*     */     } catch (XException localXException) {
/*  80 */       cleanup();
/*  81 */       throw localXException;
/*     */     } catch (InvalidDnDOperationException localInvalidDnDOperationException) {
/*  83 */       cleanup();
/*  84 */       throw localInvalidDnDOperationException;
/*     */     }
/*     */ 
/*  87 */     if (!MotifDnDConstants.MotifDnDSelection.setOwner(paramTransferable, paramMap, paramArrayOfLong, 0L))
/*     */     {
/*  90 */       cleanup();
/*  91 */       throw new InvalidDnDOperationException("Cannot acquire selection ownership");
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean processClientMessage(XClientMessageEvent paramXClientMessageEvent)
/*     */   {
/* 101 */     if (paramXClientMessageEvent.get_message_type() != MotifDnDConstants.XA_MOTIF_DRAG_AND_DROP_MESSAGE.getAtom())
/*     */     {
/* 103 */       return false;
/*     */     }
/*     */ 
/* 106 */     long l1 = paramXClientMessageEvent.get_data();
/* 107 */     int i = (byte)(unsafe.getByte(l1) & 0x7F);
/*     */ 
/* 109 */     int j = (byte)(unsafe.getByte(l1) & 0xFFFFFF80);
/*     */ 
/* 111 */     int k = unsafe.getByte(l1 + 1L);
/* 112 */     int m = k != MotifDnDConstants.getByteOrderByte() ? 1 : 0;
/* 113 */     int n = 0;
/* 114 */     int i1 = 0;
/* 115 */     int i2 = 0;
/*     */ 
/* 118 */     if (j != -128) {
/* 119 */       return false;
/*     */     }
/*     */ 
/* 122 */     switch (i) {
/*     */     case 2:
/*     */     case 3:
/*     */     case 4:
/*     */     case 8:
/* 127 */       break;
/*     */     case 5:
/*     */     case 6:
/*     */     case 7:
/*     */     default:
/* 130 */       return false;
/*     */     }
/*     */ 
/* 133 */     int i3 = unsafe.getInt(l1 + 4L);
/* 134 */     if (m != 0) {
/* 135 */       i3 = MotifDnDConstants.Swapper.swap(i3);
/*     */     }
/* 137 */     long l2 = i3 & 0xFFFFFFFF;
/*     */ 
/* 141 */     if ((this.targetEnterServerTime == 0L) || (l2 < this.targetEnterServerTime))
/*     */     {
/* 143 */       return true;
/*     */     }
/*     */ 
/* 146 */     if (i != 4) {
/* 147 */       short s1 = unsafe.getShort(l1 + 2L);
/* 148 */       if (m != 0) {
/* 149 */         s1 = MotifDnDConstants.Swapper.swap(s1);
/*     */       }
/*     */ 
/* 152 */       int i4 = (byte)((s1 & 0xF0) >> 4);
/*     */ 
/* 154 */       int i5 = (byte)((s1 & 0xF) >> 0);
/*     */ 
/* 157 */       if (i4 == 3)
/* 158 */         n = MotifDnDConstants.getJavaActionsForMotifActions(i5);
/*     */       else {
/* 160 */         n = 0;
/*     */       }
/*     */ 
/* 163 */       short s2 = unsafe.getShort(l1 + 8L);
/* 164 */       short s3 = unsafe.getShort(l1 + 10L);
/* 165 */       if (m != 0) {
/* 166 */         s2 = MotifDnDConstants.Swapper.swap(s2);
/* 167 */         s3 = MotifDnDConstants.Swapper.swap(s3);
/*     */       }
/* 169 */       i1 = s2;
/* 170 */       i2 = s3;
/*     */     }
/*     */ 
/* 173 */     getProtocolListener().handleDragReply(n, i1, i2);
/*     */ 
/* 175 */     return true;
/*     */   }
/*     */ 
/*     */   public XDragSourceProtocol.TargetWindowInfo getTargetWindowInfo(long paramLong) {
/* 179 */     assert (XToolkit.isAWTLockHeldByCurrentThread());
/*     */ 
/* 181 */     WindowPropertyGetter localWindowPropertyGetter = new WindowPropertyGetter(paramLong, MotifDnDConstants.XA_MOTIF_DRAG_RECEIVER_INFO, 0L, 65535L, false, 0L);
/*     */     try
/*     */     {
/* 188 */       int i = localWindowPropertyGetter.execute(XErrorHandler.IgnoreBadWindowHandler.getInstance());
/*     */ 
/* 204 */       if ((i == 0) && (localWindowPropertyGetter.getData() != 0L) && (localWindowPropertyGetter.getActualType() != 0L) && (localWindowPropertyGetter.getActualFormat() == 8) && (localWindowPropertyGetter.getNumberOfItems() >= 16))
/*     */       {
/* 209 */         long l = localWindowPropertyGetter.getData();
/* 210 */         int j = unsafe.getByte(l);
/* 211 */         int k = unsafe.getByte(l + 2L);
/*     */         XDragSourceProtocol.TargetWindowInfo localTargetWindowInfo2;
/* 212 */         switch (k) {
/*     */         case 2:
/*     */         case 4:
/*     */         case 5:
/*     */         case 6:
/* 217 */           int m = unsafe.getInt(l + 4L);
/* 218 */           if (j != MotifDnDConstants.getByteOrderByte()) {
/* 219 */             m = MotifDnDConstants.Swapper.swap(m);
/*     */           }
/*     */ 
/* 222 */           int n = unsafe.getByte(l + 1L);
/*     */ 
/* 224 */           return new XDragSourceProtocol.TargetWindowInfo(m, n);
/*     */         case 3:
/*     */         }
/* 227 */         return null;
/*     */       }
/*     */ 
/* 230 */       return null;
/*     */     }
/*     */     finally {
/* 233 */       localWindowPropertyGetter.dispose();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void sendEnterMessage(long[] paramArrayOfLong, int paramInt1, int paramInt2, long paramLong)
/*     */   {
/* 239 */     assert (XToolkit.isAWTLockHeldByCurrentThread());
/* 240 */     assert (getTargetWindow() != 0L);
/* 241 */     assert (paramArrayOfLong != null);
/*     */ 
/* 243 */     this.targetEnterServerTime = paramLong;
/*     */ 
/* 245 */     XClientMessageEvent localXClientMessageEvent = new XClientMessageEvent();
/*     */     try {
/* 247 */       localXClientMessageEvent.set_type(33);
/* 248 */       localXClientMessageEvent.set_window(getTargetWindow());
/* 249 */       localXClientMessageEvent.set_format(8);
/* 250 */       localXClientMessageEvent.set_message_type(MotifDnDConstants.XA_MOTIF_DRAG_AND_DROP_MESSAGE.getAtom());
/*     */ 
/* 252 */       long l = localXClientMessageEvent.get_data();
/* 253 */       int i = MotifDnDConstants.getMotifActionsForJavaActions(paramInt1) << 0 | MotifDnDConstants.getMotifActionsForJavaActions(paramInt2) << 8;
/*     */ 
/* 259 */       unsafe.putByte(l, (byte)0);
/*     */ 
/* 262 */       unsafe.putByte(l + 1L, MotifDnDConstants.getByteOrderByte());
/*     */ 
/* 264 */       unsafe.putShort(l + 2L, (short)i);
/* 265 */       unsafe.putInt(l + 4L, (int)paramLong);
/* 266 */       unsafe.putInt(l + 8L, (int)XDragSourceProtocol.getDragSourceWindow());
/* 267 */       unsafe.putInt(l + 12L, (int)MotifDnDConstants.XA_MOTIF_ATOM_0.getAtom());
/*     */ 
/* 269 */       XlibWrapper.XSendEvent(XToolkit.getDisplay(), getTargetProxyWindow(), false, 0L, localXClientMessageEvent.pData);
/*     */     }
/*     */     finally
/*     */     {
/* 274 */       localXClientMessageEvent.dispose();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void sendMoveMessage(int paramInt1, int paramInt2, int paramInt3, int paramInt4, long paramLong)
/*     */   {
/* 280 */     assert (XToolkit.isAWTLockHeldByCurrentThread());
/* 281 */     assert (getTargetWindow() != 0L);
/*     */ 
/* 283 */     XClientMessageEvent localXClientMessageEvent = new XClientMessageEvent();
/*     */     try {
/* 285 */       localXClientMessageEvent.set_type(33);
/* 286 */       localXClientMessageEvent.set_window(getTargetWindow());
/* 287 */       localXClientMessageEvent.set_format(8);
/* 288 */       localXClientMessageEvent.set_message_type(MotifDnDConstants.XA_MOTIF_DRAG_AND_DROP_MESSAGE.getAtom());
/*     */ 
/* 290 */       long l = localXClientMessageEvent.get_data();
/* 291 */       int i = MotifDnDConstants.getMotifActionsForJavaActions(paramInt3) << 0 | MotifDnDConstants.getMotifActionsForJavaActions(paramInt4) << 8;
/*     */ 
/* 297 */       unsafe.putByte(l, (byte)2);
/*     */ 
/* 300 */       unsafe.putByte(l + 1L, MotifDnDConstants.getByteOrderByte());
/*     */ 
/* 302 */       unsafe.putShort(l + 2L, (short)i);
/* 303 */       unsafe.putInt(l + 4L, (int)paramLong);
/* 304 */       unsafe.putShort(l + 8L, (short)paramInt1);
/* 305 */       unsafe.putShort(l + 10L, (short)paramInt2);
/*     */ 
/* 307 */       XlibWrapper.XSendEvent(XToolkit.getDisplay(), getTargetProxyWindow(), false, 0L, localXClientMessageEvent.pData);
/*     */     }
/*     */     finally
/*     */     {
/* 312 */       localXClientMessageEvent.dispose();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void sendLeaveMessage(long paramLong) {
/* 317 */     assert (XToolkit.isAWTLockHeldByCurrentThread());
/* 318 */     assert (getTargetWindow() != 0L);
/*     */ 
/* 320 */     XClientMessageEvent localXClientMessageEvent = new XClientMessageEvent();
/*     */     try {
/* 322 */       localXClientMessageEvent.set_type(33);
/* 323 */       localXClientMessageEvent.set_window(getTargetWindow());
/* 324 */       localXClientMessageEvent.set_format(8);
/* 325 */       localXClientMessageEvent.set_message_type(MotifDnDConstants.XA_MOTIF_DRAG_AND_DROP_MESSAGE.getAtom());
/*     */ 
/* 327 */       long l = localXClientMessageEvent.get_data();
/*     */ 
/* 329 */       unsafe.putByte(l, (byte)1);
/*     */ 
/* 332 */       unsafe.putByte(l + 1L, MotifDnDConstants.getByteOrderByte());
/*     */ 
/* 334 */       unsafe.putShort(l + 2L, (short)0);
/* 335 */       unsafe.putInt(l + 4L, (int)paramLong);
/* 336 */       unsafe.putInt(l + 8L, (int)XDragSourceProtocol.getDragSourceWindow());
/*     */ 
/* 338 */       XlibWrapper.XSendEvent(XToolkit.getDisplay(), getTargetProxyWindow(), false, 0L, localXClientMessageEvent.pData);
/*     */     }
/*     */     finally
/*     */     {
/* 343 */       localXClientMessageEvent.dispose();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void sendDropMessage(int paramInt1, int paramInt2, int paramInt3, int paramInt4, long paramLong)
/*     */   {
/* 350 */     assert (XToolkit.isAWTLockHeldByCurrentThread());
/* 351 */     assert (getTargetWindow() != 0L);
/*     */ 
/* 356 */     sendLeaveMessage(paramLong);
/*     */ 
/* 358 */     XClientMessageEvent localXClientMessageEvent = new XClientMessageEvent();
/*     */     try {
/* 360 */       localXClientMessageEvent.set_type(33);
/* 361 */       localXClientMessageEvent.set_window(getTargetWindow());
/* 362 */       localXClientMessageEvent.set_format(8);
/* 363 */       localXClientMessageEvent.set_message_type(MotifDnDConstants.XA_MOTIF_DRAG_AND_DROP_MESSAGE.getAtom());
/*     */ 
/* 365 */       long l = localXClientMessageEvent.get_data();
/* 366 */       int i = MotifDnDConstants.getMotifActionsForJavaActions(paramInt3) << 0 | MotifDnDConstants.getMotifActionsForJavaActions(paramInt4) << 8;
/*     */ 
/* 372 */       unsafe.putByte(l, (byte)5);
/*     */ 
/* 375 */       unsafe.putByte(l + 1L, MotifDnDConstants.getByteOrderByte());
/*     */ 
/* 377 */       unsafe.putShort(l + 2L, (short)i);
/* 378 */       unsafe.putInt(l + 4L, (int)paramLong);
/* 379 */       unsafe.putShort(l + 8L, (short)paramInt1);
/* 380 */       unsafe.putShort(l + 10L, (short)paramInt2);
/* 381 */       unsafe.putInt(l + 12L, (int)MotifDnDConstants.XA_MOTIF_ATOM_0.getAtom());
/* 382 */       unsafe.putInt(l + 16L, (int)XDragSourceProtocol.getDragSourceWindow());
/*     */ 
/* 384 */       XlibWrapper.XSendEvent(XToolkit.getDisplay(), getTargetProxyWindow(), false, 0L, localXClientMessageEvent.pData);
/*     */     }
/*     */     finally
/*     */     {
/* 389 */       localXClientMessageEvent.dispose();
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean processProxyModeEvent(XClientMessageEvent paramXClientMessageEvent, long paramLong)
/*     */   {
/* 396 */     return false;
/*     */   }
/*     */ 
/*     */   public void cleanupTargetInfo() {
/* 400 */     super.cleanupTargetInfo();
/* 401 */     this.targetEnterServerTime = 0L;
/*     */   }
/*     */ 
/*     */   public void dispatchEvent(XEvent paramXEvent) {
/* 405 */     switch (paramXEvent.get_type()) {
/*     */     case 30:
/* 407 */       XSelectionRequestEvent localXSelectionRequestEvent = paramXEvent.get_xselectionrequest();
/* 408 */       long l1 = localXSelectionRequestEvent.get_selection();
/*     */ 
/* 410 */       if (l1 == MotifDnDConstants.XA_MOTIF_ATOM_0.getAtom()) {
/* 411 */         long l2 = localXSelectionRequestEvent.get_target();
/* 412 */         if (l2 == MotifDnDConstants.XA_XmTRANSFER_SUCCESS.getAtom())
/* 413 */           getProtocolListener().handleDragFinished(true);
/* 414 */         else if (l2 == MotifDnDConstants.XA_XmTRANSFER_FAILURE.getAtom())
/* 415 */           getProtocolListener().handleDragFinished(false);
/*     */       }
/*     */       break;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.MotifDnDDragSourceProtocol
 * JD-Core Version:    0.6.2
 */