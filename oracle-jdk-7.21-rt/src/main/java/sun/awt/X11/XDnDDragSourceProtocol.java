/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.awt.datatransfer.Transferable;
/*     */ import java.awt.dnd.InvalidDnDOperationException;
/*     */ import java.util.Map;
/*     */ import sun.misc.Unsafe;
/*     */ import sun.util.logging.PlatformLogger;
/*     */ 
/*     */ class XDnDDragSourceProtocol extends XDragSourceProtocol
/*     */ {
/*  45 */   private static final PlatformLogger logger = PlatformLogger.getLogger("sun.awt.X11.xembed.xdnd.XDnDDragSourceProtocol");
/*     */ 
/*  48 */   private static final Unsafe unsafe = XlibWrapper.unsafe;
/*     */ 
/*     */   protected XDnDDragSourceProtocol(XDragSourceProtocolListener paramXDragSourceProtocolListener) {
/*  51 */     super(paramXDragSourceProtocolListener);
/*     */   }
/*     */ 
/*     */   static XDragSourceProtocol createInstance(XDragSourceProtocolListener paramXDragSourceProtocolListener)
/*     */   {
/*  60 */     return new XDnDDragSourceProtocol(paramXDragSourceProtocolListener);
/*     */   }
/*     */ 
/*     */   public String getProtocolName() {
/*  64 */     return "XDnD";
/*     */   }
/*     */ 
/*     */   protected void initializeDragImpl(int paramInt, Transferable paramTransferable, Map paramMap, long[] paramArrayOfLong)
/*     */     throws InvalidDnDOperationException, IllegalArgumentException, XException
/*     */   {
/*  76 */     assert (XToolkit.isAWTLockHeldByCurrentThread());
/*     */ 
/*  78 */     long l1 = XDragSourceProtocol.getDragSourceWindow();
/*     */ 
/*  80 */     long l2 = Native.allocateLongArray(3);
/*  81 */     int i = 0;
/*     */     try {
/*  83 */       if ((paramInt & 0x1) != 0) {
/*  84 */         Native.putLong(l2, i, XDnDConstants.XA_XdndActionCopy.getAtom());
/*     */ 
/*  86 */         i++;
/*     */       }
/*  88 */       if ((paramInt & 0x2) != 0) {
/*  89 */         Native.putLong(l2, i, XDnDConstants.XA_XdndActionMove.getAtom());
/*     */ 
/*  91 */         i++;
/*     */       }
/*  93 */       if ((paramInt & 0x40000000) != 0) {
/*  94 */         Native.putLong(l2, i, XDnDConstants.XA_XdndActionLink.getAtom());
/*     */ 
/*  96 */         i++;
/*     */       }
/*     */ 
/*  99 */       XToolkit.WITH_XERROR_HANDLER(XErrorHandler.VerifyChangePropertyHandler.getInstance());
/* 100 */       XDnDConstants.XA_XdndActionList.setAtomData(l1, 4L, l2, i);
/*     */ 
/* 103 */       XToolkit.RESTORE_XERROR_HANDLER();
/*     */ 
/* 105 */       if ((XToolkit.saved_error != null) && (XToolkit.saved_error.get_error_code() != 0))
/*     */       {
/* 107 */         cleanup();
/* 108 */         throw new XException("Cannot write XdndActionList property");
/*     */       }
/*     */     } finally {
/* 111 */       unsafe.freeMemory(l2);
/* 112 */       l2 = 0L;
/*     */     }
/*     */ 
/* 115 */     l2 = Native.allocateLongArray(paramArrayOfLong.length);
/*     */     try
/*     */     {
/* 118 */       Native.put(l2, paramArrayOfLong);
/*     */ 
/* 120 */       XToolkit.WITH_XERROR_HANDLER(XErrorHandler.VerifyChangePropertyHandler.getInstance());
/* 121 */       XDnDConstants.XA_XdndTypeList.setAtomData(l1, 4L, l2, paramArrayOfLong.length);
/*     */ 
/* 124 */       XToolkit.RESTORE_XERROR_HANDLER();
/*     */ 
/* 126 */       if ((XToolkit.saved_error != null) && (XToolkit.saved_error.get_error_code() != 0))
/*     */       {
/* 128 */         cleanup();
/* 129 */         throw new XException("Cannot write XdndActionList property");
/*     */       }
/*     */     } finally {
/* 132 */       unsafe.freeMemory(l2);
/* 133 */       l2 = 0L;
/*     */     }
/*     */ 
/* 136 */     if (!XDnDConstants.XDnDSelection.setOwner(paramTransferable, paramMap, paramArrayOfLong, 0L))
/*     */     {
/* 138 */       cleanup();
/* 139 */       throw new InvalidDnDOperationException("Cannot acquire selection ownership");
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean processXdndStatus(XClientMessageEvent paramXClientMessageEvent) {
/* 144 */     int i = 0;
/*     */ 
/* 147 */     if (paramXClientMessageEvent.get_data(0) != getTargetWindow()) {
/* 148 */       return true;
/*     */     }
/*     */ 
/* 151 */     if ((paramXClientMessageEvent.get_data(1) & 1L) != 0L)
/*     */     {
/* 154 */       i = XDnDConstants.getJavaActionForXDnDAction(paramXClientMessageEvent.get_data(4));
/*     */     }
/*     */ 
/* 157 */     getProtocolListener().handleDragReply(i);
/*     */ 
/* 159 */     return true;
/*     */   }
/*     */ 
/*     */   private boolean processXdndFinished(XClientMessageEvent paramXClientMessageEvent)
/*     */   {
/* 164 */     if (paramXClientMessageEvent.get_data(0) != getTargetWindow()) {
/* 165 */       return true;
/*     */     }
/*     */ 
/* 168 */     if (getTargetProtocolVersion() >= 5) {
/* 169 */       boolean bool = (paramXClientMessageEvent.get_data(1) & 1L) != 0L;
/* 170 */       int i = XDnDConstants.getJavaActionForXDnDAction(paramXClientMessageEvent.get_data(2));
/* 171 */       getProtocolListener().handleDragFinished(bool, i);
/*     */     } else {
/* 173 */       getProtocolListener().handleDragFinished();
/*     */     }
/*     */ 
/* 176 */     finalizeDrop();
/*     */ 
/* 178 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean processClientMessage(XClientMessageEvent paramXClientMessageEvent) {
/* 182 */     if (paramXClientMessageEvent.get_message_type() == XDnDConstants.XA_XdndStatus.getAtom())
/* 183 */       return processXdndStatus(paramXClientMessageEvent);
/* 184 */     if (paramXClientMessageEvent.get_message_type() == XDnDConstants.XA_XdndFinished.getAtom()) {
/* 185 */       return processXdndFinished(paramXClientMessageEvent);
/*     */     }
/* 187 */     return false;
/*     */   }
/*     */ 
/*     */   public XDragSourceProtocol.TargetWindowInfo getTargetWindowInfo(long paramLong)
/*     */   {
/* 192 */     assert (XToolkit.isAWTLockHeldByCurrentThread());
/*     */ 
/* 194 */     WindowPropertyGetter localWindowPropertyGetter1 = new WindowPropertyGetter(paramLong, XDnDConstants.XA_XdndAware, 0L, 1L, false, 0L);
/*     */ 
/* 198 */     int i = localWindowPropertyGetter1.execute(XErrorHandler.IgnoreBadWindowHandler.getInstance());
/*     */ 
/* 200 */     if ((i == 0) && (localWindowPropertyGetter1.getData() != 0L) && (localWindowPropertyGetter1.getActualType() == 4L))
/*     */     {
/* 203 */       int j = (int)Native.getLong(localWindowPropertyGetter1.getData());
/*     */ 
/* 205 */       localWindowPropertyGetter1.dispose();
/*     */ 
/* 207 */       if (j >= 3) {
/* 208 */         long l = 0L;
/* 209 */         int k = j < 5 ? j : 5;
/*     */ 
/* 213 */         WindowPropertyGetter localWindowPropertyGetter2 = new WindowPropertyGetter(paramLong, XDnDConstants.XA_XdndProxy, 0L, 1L, false, 33L);
/*     */         try
/*     */         {
/* 218 */           i = localWindowPropertyGetter2.execute(XErrorHandler.IgnoreBadWindowHandler.getInstance());
/*     */ 
/* 220 */           if ((i == 0) && (localWindowPropertyGetter2.getData() != 0L) && (localWindowPropertyGetter2.getActualType() == 33L))
/*     */           {
/* 224 */             l = Native.getLong(localWindowPropertyGetter2.getData());
/*     */           }
/*     */         } finally {
/* 227 */           localWindowPropertyGetter2.dispose();
/*     */         }
/*     */ 
/* 230 */         if (l != 0L) {
/* 231 */           WindowPropertyGetter localWindowPropertyGetter3 = new WindowPropertyGetter(l, XDnDConstants.XA_XdndProxy, 0L, 1L, false, 33L);
/*     */           try
/*     */           {
/* 236 */             i = localWindowPropertyGetter3.execute(XErrorHandler.IgnoreBadWindowHandler.getInstance());
/*     */ 
/* 238 */             if ((i != 0) || (localWindowPropertyGetter3.getData() == 0L) || (localWindowPropertyGetter3.getActualType() != 33L) || (Native.getLong(localWindowPropertyGetter3.getData()) != l))
/*     */             {
/* 243 */               l = 0L;
/*     */             } else {
/* 245 */               WindowPropertyGetter localWindowPropertyGetter4 = new WindowPropertyGetter(l, XDnDConstants.XA_XdndAware, 0L, 1L, false, 0L);
/*     */               try
/*     */               {
/* 252 */                 i = localWindowPropertyGetter4.execute(XErrorHandler.IgnoreBadWindowHandler.getInstance());
/*     */ 
/* 254 */                 if ((i != 0) || (localWindowPropertyGetter4.getData() == 0L) || (localWindowPropertyGetter4.getActualType() != 4L))
/*     */                 {
/* 258 */                   l = 0L;
/*     */                 }
/*     */               } finally {
/*     */               }
/*     */             }
/*     */           }
/*     */           finally {
/* 265 */             localWindowPropertyGetter3.dispose();
/*     */           }
/*     */         }
/*     */ 
/* 269 */         return new XDragSourceProtocol.TargetWindowInfo(l, k);
/*     */       }
/*     */     } else {
/* 272 */       localWindowPropertyGetter1.dispose();
/*     */     }
/*     */ 
/* 275 */     return null;
/*     */   }
/*     */ 
/*     */   public void sendEnterMessage(long[] paramArrayOfLong, int paramInt1, int paramInt2, long paramLong)
/*     */   {
/* 280 */     assert (XToolkit.isAWTLockHeldByCurrentThread());
/* 281 */     assert (getTargetWindow() != 0L);
/* 282 */     assert (paramArrayOfLong != null);
/*     */ 
/* 284 */     XClientMessageEvent localXClientMessageEvent = new XClientMessageEvent();
/*     */     try {
/* 286 */       localXClientMessageEvent.set_type(33);
/* 287 */       localXClientMessageEvent.set_window(getTargetWindow());
/* 288 */       localXClientMessageEvent.set_format(32);
/* 289 */       localXClientMessageEvent.set_message_type(XDnDConstants.XA_XdndEnter.getAtom());
/* 290 */       localXClientMessageEvent.set_data(0, XDragSourceProtocol.getDragSourceWindow());
/* 291 */       long l = getTargetProtocolVersion() << 24;
/*     */ 
/* 293 */       l |= (paramArrayOfLong.length > 3 ? 1L : 0L);
/* 294 */       localXClientMessageEvent.set_data(1, l);
/* 295 */       localXClientMessageEvent.set_data(2, paramArrayOfLong.length > 0 ? paramArrayOfLong[0] : 0L);
/* 296 */       localXClientMessageEvent.set_data(3, paramArrayOfLong.length > 1 ? paramArrayOfLong[1] : 0L);
/* 297 */       localXClientMessageEvent.set_data(4, paramArrayOfLong.length > 2 ? paramArrayOfLong[2] : 0L);
/* 298 */       XlibWrapper.XSendEvent(XToolkit.getDisplay(), getTargetProxyWindow(), false, 0L, localXClientMessageEvent.pData);
/*     */     }
/*     */     finally
/*     */     {
/* 303 */       localXClientMessageEvent.dispose();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void sendMoveMessage(int paramInt1, int paramInt2, int paramInt3, int paramInt4, long paramLong)
/*     */   {
/* 309 */     assert (XToolkit.isAWTLockHeldByCurrentThread());
/* 310 */     assert (getTargetWindow() != 0L);
/*     */ 
/* 312 */     XClientMessageEvent localXClientMessageEvent = new XClientMessageEvent();
/*     */     try {
/* 314 */       localXClientMessageEvent.set_type(33);
/* 315 */       localXClientMessageEvent.set_window(getTargetWindow());
/* 316 */       localXClientMessageEvent.set_format(32);
/* 317 */       localXClientMessageEvent.set_message_type(XDnDConstants.XA_XdndPosition.getAtom());
/* 318 */       localXClientMessageEvent.set_data(0, XDragSourceProtocol.getDragSourceWindow());
/* 319 */       localXClientMessageEvent.set_data(1, 0L);
/* 320 */       localXClientMessageEvent.set_data(2, paramInt1 << 16 | paramInt2);
/* 321 */       localXClientMessageEvent.set_data(3, paramLong);
/* 322 */       localXClientMessageEvent.set_data(4, XDnDConstants.getXDnDActionForJavaAction(paramInt3));
/* 323 */       XlibWrapper.XSendEvent(XToolkit.getDisplay(), getTargetProxyWindow(), false, 0L, localXClientMessageEvent.pData);
/*     */     }
/*     */     finally
/*     */     {
/* 328 */       localXClientMessageEvent.dispose();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void sendLeaveMessage(long paramLong) {
/* 333 */     assert (XToolkit.isAWTLockHeldByCurrentThread());
/* 334 */     assert (getTargetWindow() != 0L);
/*     */ 
/* 336 */     XClientMessageEvent localXClientMessageEvent = new XClientMessageEvent();
/*     */     try {
/* 338 */       localXClientMessageEvent.set_type(33);
/* 339 */       localXClientMessageEvent.set_window(getTargetWindow());
/* 340 */       localXClientMessageEvent.set_format(32);
/* 341 */       localXClientMessageEvent.set_message_type(XDnDConstants.XA_XdndLeave.getAtom());
/* 342 */       localXClientMessageEvent.set_data(0, XDragSourceProtocol.getDragSourceWindow());
/* 343 */       localXClientMessageEvent.set_data(1, 0L);
/* 344 */       localXClientMessageEvent.set_data(2, 0L);
/* 345 */       localXClientMessageEvent.set_data(3, 0L);
/* 346 */       localXClientMessageEvent.set_data(4, 0L);
/* 347 */       XlibWrapper.XSendEvent(XToolkit.getDisplay(), getTargetProxyWindow(), false, 0L, localXClientMessageEvent.pData);
/*     */     }
/*     */     finally
/*     */     {
/* 352 */       localXClientMessageEvent.dispose();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void sendDropMessage(int paramInt1, int paramInt2, int paramInt3, int paramInt4, long paramLong)
/*     */   {
/* 359 */     assert (XToolkit.isAWTLockHeldByCurrentThread());
/* 360 */     assert (getTargetWindow() != 0L);
/*     */ 
/* 362 */     XClientMessageEvent localXClientMessageEvent = new XClientMessageEvent();
/*     */     try {
/* 364 */       localXClientMessageEvent.set_type(33);
/* 365 */       localXClientMessageEvent.set_window(getTargetWindow());
/* 366 */       localXClientMessageEvent.set_format(32);
/* 367 */       localXClientMessageEvent.set_message_type(XDnDConstants.XA_XdndDrop.getAtom());
/* 368 */       localXClientMessageEvent.set_data(0, XDragSourceProtocol.getDragSourceWindow());
/* 369 */       localXClientMessageEvent.set_data(1, 0L);
/* 370 */       localXClientMessageEvent.set_data(2, paramLong);
/* 371 */       localXClientMessageEvent.set_data(3, 0L);
/* 372 */       localXClientMessageEvent.set_data(4, 0L);
/* 373 */       XlibWrapper.XSendEvent(XToolkit.getDisplay(), getTargetProxyWindow(), false, 0L, localXClientMessageEvent.pData);
/*     */     }
/*     */     finally
/*     */     {
/* 378 */       localXClientMessageEvent.dispose();
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean processProxyModeEvent(XClientMessageEvent paramXClientMessageEvent, long paramLong)
/*     */   {
/* 384 */     if ((paramXClientMessageEvent.get_message_type() == XDnDConstants.XA_XdndStatus.getAtom()) || (paramXClientMessageEvent.get_message_type() == XDnDConstants.XA_XdndFinished.getAtom()))
/*     */     {
/* 387 */       if (paramXClientMessageEvent.get_message_type() == XDnDConstants.XA_XdndFinished.getAtom()) {
/* 388 */         XDragSourceContextPeer.setProxyModeSourceWindow(0L);
/*     */       }
/*     */ 
/* 394 */       if (paramXClientMessageEvent.get_window() == paramLong) {
/* 395 */         return false;
/*     */       }
/*     */ 
/* 398 */       if (logger.isLoggable(300)) {
/* 399 */         logger.finest("        sourceWindow=" + paramLong + " get_window=" + paramXClientMessageEvent.get_window() + " xclient=" + paramXClientMessageEvent);
/*     */       }
/*     */ 
/* 403 */       paramXClientMessageEvent.set_data(0, paramXClientMessageEvent.get_window());
/* 404 */       paramXClientMessageEvent.set_window(paramLong);
/*     */ 
/* 406 */       assert (XToolkit.isAWTLockHeldByCurrentThread());
/*     */ 
/* 408 */       XlibWrapper.XSendEvent(XToolkit.getDisplay(), paramLong, false, 0L, paramXClientMessageEvent.pData);
/*     */ 
/* 412 */       return true;
/*     */     }
/*     */ 
/* 415 */     return false;
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/* 421 */     cleanup();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XDnDDragSourceProtocol
 * JD-Core Version:    0.6.2
 */