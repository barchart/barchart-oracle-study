/*      */ package sun.awt.X11;
/*      */ 
/*      */ import java.awt.Point;
/*      */ import java.io.IOException;
/*      */ import sun.misc.Unsafe;
/*      */ 
/*      */ class MotifDnDDropTargetProtocol extends XDropTargetProtocol
/*      */ {
/*   44 */   private static final Unsafe unsafe = XlibWrapper.unsafe;
/*      */ 
/*   46 */   private long sourceWindow = 0L;
/*   47 */   private long sourceWindowMask = 0L;
/*   48 */   private int sourceProtocolVersion = 0;
/*   49 */   private int sourceActions = 0;
/*   50 */   private long[] sourceFormats = null;
/*   51 */   private long sourceAtom = 0L;
/*   52 */   private int userAction = 0;
/*   53 */   private int sourceX = 0;
/*   54 */   private int sourceY = 0;
/*   55 */   private XWindow targetXWindow = null;
/*   56 */   private boolean topLevelLeavePostponed = false;
/*      */ 
/*      */   protected MotifDnDDropTargetProtocol(XDropTargetProtocolListener paramXDropTargetProtocolListener) {
/*   59 */     super(paramXDropTargetProtocolListener);
/*      */   }
/*      */ 
/*      */   static XDropTargetProtocol createInstance(XDropTargetProtocolListener paramXDropTargetProtocolListener)
/*      */   {
/*   68 */     return new MotifDnDDropTargetProtocol(paramXDropTargetProtocolListener);
/*      */   }
/*      */ 
/*      */   public String getProtocolName() {
/*   72 */     return "MotifDnD";
/*      */   }
/*      */ 
/*      */   public void registerDropTarget(long paramLong) {
/*   76 */     assert (XToolkit.isAWTLockHeldByCurrentThread());
/*      */ 
/*   78 */     MotifDnDConstants.writeDragReceiverInfoStruct(paramLong);
/*      */   }
/*      */ 
/*      */   public void unregisterDropTarget(long paramLong) {
/*   82 */     assert (XToolkit.isAWTLockHeldByCurrentThread());
/*      */ 
/*   84 */     MotifDnDConstants.XA_MOTIF_ATOM_0.DeleteProperty(paramLong);
/*      */   }
/*      */ 
/*      */   public void registerEmbedderDropSite(long paramLong) {
/*   88 */     assert (XToolkit.isAWTLockHeldByCurrentThread());
/*      */ 
/*   90 */     boolean bool = false;
/*   91 */     int i = 0;
/*   92 */     long l1 = 0L;
/*   93 */     long l2 = XDropTargetRegistry.getDnDProxyWindow();
/*   94 */     int j = 0;
/*   95 */     long l3 = 0L;
/*   96 */     int k = 16;
/*      */ 
/*   98 */     WindowPropertyGetter localWindowPropertyGetter = new WindowPropertyGetter(paramLong, MotifDnDConstants.XA_MOTIF_DRAG_RECEIVER_INFO, 0L, 65535L, false, 0L);
/*      */     try
/*      */     {
/*  105 */       j = localWindowPropertyGetter.execute(XErrorHandler.IgnoreBadWindowHandler.getInstance());
/*      */ 
/*  121 */       if ((j == 0) && (localWindowPropertyGetter.getData() != 0L) && (localWindowPropertyGetter.getActualType() != 0L) && (localWindowPropertyGetter.getActualFormat() == 8) && (localWindowPropertyGetter.getNumberOfItems() >= 16))
/*      */       {
/*  126 */         bool = true;
/*  127 */         l3 = localWindowPropertyGetter.getData();
/*  128 */         k = localWindowPropertyGetter.getNumberOfItems();
/*      */ 
/*  130 */         int m = unsafe.getByte(l3);
/*      */ 
/*  133 */         int n = unsafe.getInt(l3 + 4L);
/*  134 */         if (m != MotifDnDConstants.getByteOrderByte()) {
/*  135 */           n = MotifDnDConstants.Swapper.swap(n);
/*      */         }
/*  137 */         l1 = n;
/*      */ 
/*  140 */         if (l1 == l2)
/*      */         {
/*      */           return;
/*      */         }
/*      */ 
/*  146 */         n = (int)l2;
/*  147 */         if (m != MotifDnDConstants.getByteOrderByte()) {
/*  148 */           n = MotifDnDConstants.Swapper.swap(n);
/*      */         }
/*  150 */         unsafe.putInt(l3 + 4L, n);
/*      */       }
/*      */       else {
/*  153 */         l3 = unsafe.allocateMemory(k);
/*      */ 
/*  155 */         unsafe.putByte(l3, MotifDnDConstants.getByteOrderByte());
/*  156 */         unsafe.putByte(l3 + 1L, (byte)0);
/*  157 */         unsafe.putByte(l3 + 2L, (byte)5);
/*  158 */         unsafe.putByte(l3 + 3L, (byte)0);
/*  159 */         unsafe.putInt(l3 + 4L, (int)l2);
/*  160 */         unsafe.putShort(l3 + 8L, (short)0);
/*  161 */         unsafe.putShort(l3 + 10L, (short)0);
/*  162 */         unsafe.putInt(l3 + 12L, k);
/*      */       }
/*      */ 
/*  165 */       XToolkit.WITH_XERROR_HANDLER(XErrorHandler.VerifyChangePropertyHandler.getInstance());
/*  166 */       XlibWrapper.XChangeProperty(XToolkit.getDisplay(), paramLong, MotifDnDConstants.XA_MOTIF_DRAG_RECEIVER_INFO.getAtom(), MotifDnDConstants.XA_MOTIF_DRAG_RECEIVER_INFO.getAtom(), 8, 0, l3, k);
/*      */ 
/*  171 */       XToolkit.RESTORE_XERROR_HANDLER();
/*      */ 
/*  173 */       if ((XToolkit.saved_error != null) && (XToolkit.saved_error.get_error_code() != 0))
/*      */       {
/*  175 */         throw new XException("Cannot write Motif receiver info property");
/*      */       }
/*      */     } finally {
/*  178 */       if (!bool) {
/*  179 */         unsafe.freeMemory(l3);
/*  180 */         l3 = 0L;
/*      */       }
/*  182 */       localWindowPropertyGetter.dispose();
/*      */     }
/*      */ 
/*  185 */     putEmbedderRegistryEntry(paramLong, bool, i, l1);
/*      */   }
/*      */ 
/*      */   public void unregisterEmbedderDropSite(long paramLong) {
/*  189 */     assert (XToolkit.isAWTLockHeldByCurrentThread());
/*      */ 
/*  191 */     XDropTargetProtocol.EmbedderRegistryEntry localEmbedderRegistryEntry = getEmbedderRegistryEntry(paramLong);
/*      */ 
/*  193 */     if (localEmbedderRegistryEntry == null) {
/*  194 */       return;
/*      */     }
/*      */ 
/*  197 */     if (localEmbedderRegistryEntry.isOverriden()) {
/*  198 */       int i = 0;
/*      */ 
/*  200 */       WindowPropertyGetter localWindowPropertyGetter = new WindowPropertyGetter(paramLong, MotifDnDConstants.XA_MOTIF_DRAG_RECEIVER_INFO, 0L, 65535L, false, 0L);
/*      */       try
/*      */       {
/*  207 */         i = localWindowPropertyGetter.execute(XErrorHandler.IgnoreBadWindowHandler.getInstance());
/*      */ 
/*  223 */         if ((i == 0) && (localWindowPropertyGetter.getData() != 0L) && (localWindowPropertyGetter.getActualType() != 0L) && (localWindowPropertyGetter.getActualFormat() == 8) && (localWindowPropertyGetter.getNumberOfItems() >= 16))
/*      */         {
/*  228 */           int j = 16;
/*  229 */           long l = localWindowPropertyGetter.getData();
/*  230 */           int k = unsafe.getByte(l);
/*      */ 
/*  232 */           int m = (int)localEmbedderRegistryEntry.getProxy();
/*  233 */           if (MotifDnDConstants.getByteOrderByte() != k) {
/*  234 */             m = MotifDnDConstants.Swapper.swap(m);
/*      */           }
/*      */ 
/*  237 */           unsafe.putInt(l + 4L, m);
/*      */ 
/*  239 */           XToolkit.WITH_XERROR_HANDLER(XErrorHandler.VerifyChangePropertyHandler.getInstance());
/*  240 */           XlibWrapper.XChangeProperty(XToolkit.getDisplay(), paramLong, MotifDnDConstants.XA_MOTIF_DRAG_RECEIVER_INFO.getAtom(), MotifDnDConstants.XA_MOTIF_DRAG_RECEIVER_INFO.getAtom(), 8, 0, l, j);
/*      */ 
/*  245 */           XToolkit.RESTORE_XERROR_HANDLER();
/*      */ 
/*  247 */           if ((XToolkit.saved_error != null) && (XToolkit.saved_error.get_error_code() != 0))
/*      */           {
/*  249 */             throw new XException("Cannot write Motif receiver info property");
/*      */           }
/*      */         }
/*      */       } finally {
/*  253 */         localWindowPropertyGetter.dispose();
/*      */       }
/*      */     } else {
/*  256 */       MotifDnDConstants.XA_MOTIF_DRAG_RECEIVER_INFO.DeleteProperty(paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void registerEmbeddedDropSite(long paramLong)
/*      */   {
/*  265 */     assert (XToolkit.isAWTLockHeldByCurrentThread());
/*      */ 
/*  267 */     boolean bool = false;
/*  268 */     int i = 0;
/*  269 */     long l1 = 0L;
/*  270 */     int j = 0;
/*      */ 
/*  272 */     WindowPropertyGetter localWindowPropertyGetter = new WindowPropertyGetter(paramLong, MotifDnDConstants.XA_MOTIF_DRAG_RECEIVER_INFO, 0L, 65535L, false, 0L);
/*      */     try
/*      */     {
/*  279 */       j = localWindowPropertyGetter.execute(XErrorHandler.IgnoreBadWindowHandler.getInstance());
/*      */ 
/*  295 */       if ((j == 0) && (localWindowPropertyGetter.getData() != 0L) && (localWindowPropertyGetter.getActualType() != 0L) && (localWindowPropertyGetter.getActualFormat() == 8) && (localWindowPropertyGetter.getNumberOfItems() >= 16))
/*      */       {
/*  300 */         bool = true;
/*  301 */         long l2 = localWindowPropertyGetter.getData();
/*      */ 
/*  303 */         int k = unsafe.getByte(l2);
/*      */ 
/*  306 */         int m = unsafe.getInt(l2 + 4L);
/*  307 */         if (k != MotifDnDConstants.getByteOrderByte()) {
/*  308 */           m = MotifDnDConstants.Swapper.swap(m);
/*      */         }
/*  310 */         l1 = m;
/*      */       }
/*      */     }
/*      */     finally {
/*  314 */       localWindowPropertyGetter.dispose();
/*      */     }
/*      */ 
/*  317 */     putEmbedderRegistryEntry(paramLong, bool, i, l1);
/*      */   }
/*      */ 
/*      */   public boolean isProtocolSupported(long paramLong) {
/*  321 */     WindowPropertyGetter localWindowPropertyGetter = new WindowPropertyGetter(paramLong, MotifDnDConstants.XA_MOTIF_DRAG_RECEIVER_INFO, 0L, 65535L, false, 0L);
/*      */     try
/*      */     {
/*  328 */       int i = localWindowPropertyGetter.execute(XErrorHandler.IgnoreBadWindowHandler.getInstance());
/*      */       boolean bool;
/*  330 */       if ((i == 0) && (localWindowPropertyGetter.getData() != 0L) && (localWindowPropertyGetter.getActualType() != 0L) && (localWindowPropertyGetter.getActualFormat() == 8) && (localWindowPropertyGetter.getNumberOfItems() >= 16))
/*      */       {
/*  334 */         return true;
/*      */       }
/*  336 */       return false;
/*      */     }
/*      */     finally {
/*  339 */       localWindowPropertyGetter.dispose();
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean processTopLevelEnter(XClientMessageEvent paramXClientMessageEvent) {
/*  344 */     assert (XToolkit.isAWTLockHeldByCurrentThread());
/*      */ 
/*  346 */     if ((this.targetXWindow != null) || (this.sourceWindow != 0L)) {
/*  347 */       return false;
/*      */     }
/*      */ 
/*  350 */     if ((!(XToolkit.windowToXWindow(paramXClientMessageEvent.get_window()) instanceof XWindow)) && (getEmbedderRegistryEntry(paramXClientMessageEvent.get_window()) == null))
/*      */     {
/*  352 */       return false;
/*  355 */     }
/*      */ long l1 = 0L;
/*  356 */     long l2 = 0L;
/*  357 */     int i = 0;
/*  358 */     long l3 = 0L;
/*  359 */     long[] arrayOfLong = null;
/*      */ 
/*  362 */     long l4 = paramXClientMessageEvent.get_data();
/*  363 */     byte b1 = unsafe.getByte(l4 + 1L);
/*  364 */     l1 = MotifDnDConstants.Swapper.getInt(l4 + 8L, b1);
/*  365 */     l3 = MotifDnDConstants.Swapper.getInt(l4 + 12L, b1);
/*      */ 
/*  370 */     Object localObject1 = new WindowPropertyGetter(l1, XAtom.get(l3), 0L, 65535L, false, MotifDnDConstants.XA_MOTIF_DRAG_INITIATOR_INFO.getAtom());
/*      */     int j;
/*      */     try {
/*  378 */       j = ((WindowPropertyGetter)localObject1).execute(XErrorHandler.IgnoreBadWindowHandler.getInstance());
/*      */ 
/*  380 */       if ((j == 0) && (((WindowPropertyGetter)localObject1).getData() != 0L) && (((WindowPropertyGetter)localObject1).getActualType() == MotifDnDConstants.XA_MOTIF_DRAG_INITIATOR_INFO.getAtom()) && (((WindowPropertyGetter)localObject1).getActualFormat() == 8) && (((WindowPropertyGetter)localObject1).getNumberOfItems() == 8))
/*      */       {
/*  387 */         long l5 = ((WindowPropertyGetter)localObject1).getData();
/*  388 */         byte b2 = unsafe.getByte(l5);
/*      */ 
/*  390 */         i = unsafe.getByte(l5 + 1L);
/*      */ 
/*  392 */         if (i != 0)
/*      */         {
/*  394 */           return false;
/*      */         }
/*      */ 
/*  397 */         int k = MotifDnDConstants.Swapper.getShort(l5 + 2L, b2);
/*      */ 
/*  400 */         arrayOfLong = MotifDnDConstants.getTargetListForIndex(k);
/*      */       } else {
/*  402 */         arrayOfLong = new long[0];
/*      */       }
/*      */     } finally {
/*  405 */       ((WindowPropertyGetter)localObject1).dispose();
/*      */     }
/*      */ 
/*  413 */     localObject1 = new XWindowAttributes();
/*      */     try {
/*  415 */       XToolkit.WITH_XERROR_HANDLER(XErrorHandler.IgnoreBadWindowHandler.getInstance());
/*  416 */       j = XlibWrapper.XGetWindowAttributes(XToolkit.getDisplay(), l1, ((XWindowAttributes)localObject1).pData);
/*      */ 
/*  419 */       XToolkit.RESTORE_XERROR_HANDLER();
/*      */ 
/*  421 */       if ((j == 0) || ((XToolkit.saved_error != null) && (XToolkit.saved_error.get_error_code() != 0)))
/*      */       {
/*  424 */         throw new XException("XGetWindowAttributes failed");
/*      */       }
/*      */ 
/*  427 */       l2 = ((XWindowAttributes)localObject1).get_your_event_mask();
/*      */     } finally {
/*  429 */       ((XWindowAttributes)localObject1).dispose();
/*      */     }
/*      */ 
/*  432 */     XToolkit.WITH_XERROR_HANDLER(XErrorHandler.IgnoreBadWindowHandler.getInstance());
/*  433 */     XlibWrapper.XSelectInput(XToolkit.getDisplay(), l1, l2 | 0x20000);
/*      */ 
/*  437 */     XToolkit.RESTORE_XERROR_HANDLER();
/*      */ 
/*  439 */     if ((XToolkit.saved_error != null) && (XToolkit.saved_error.get_error_code() != 0))
/*      */     {
/*  441 */       throw new XException("XSelectInput failed");
/*      */     }
/*      */ 
/*  444 */     this.sourceWindow = l1;
/*  445 */     this.sourceWindowMask = l2;
/*  446 */     this.sourceProtocolVersion = i;
/*      */ 
/*  451 */     this.sourceActions = 0;
/*  452 */     this.sourceFormats = arrayOfLong;
/*  453 */     this.sourceAtom = l3;
/*      */ 
/*  455 */     return true;
/*      */   }
/*      */ 
/*      */   private boolean processDragMotion(XClientMessageEvent paramXClientMessageEvent) {
/*  459 */     long l1 = paramXClientMessageEvent.get_data();
/*  460 */     byte b = unsafe.getByte(l1 + 1L);
/*  461 */     int i = (byte)(unsafe.getByte(l1) & 0x7F);
/*      */ 
/*  463 */     int j = 0;
/*  464 */     int k = 0;
/*      */ 
/*  466 */     int m = MotifDnDConstants.Swapper.getShort(l1 + 2L, b);
/*      */ 
/*  468 */     int n = (m & 0xF) >> 0;
/*      */ 
/*  470 */     int i1 = (m & 0xF00) >> 8;
/*      */ 
/*  473 */     int i2 = MotifDnDConstants.getJavaActionsForMotifActions(n);
/*  474 */     int i3 = MotifDnDConstants.getJavaActionsForMotifActions(i1);
/*      */ 
/*  479 */     int i4 = (int)this.sourceWindow;
/*  480 */     if (b != MotifDnDConstants.getByteOrderByte()) {
/*  481 */       i4 = MotifDnDConstants.Swapper.swap(i4);
/*      */     }
/*  483 */     unsafe.putInt(l1 + 12L, i4);
/*      */ 
/*  486 */     XWindow localXWindow = null;
/*      */ 
/*  488 */     XBaseWindow localXBaseWindow1 = XToolkit.windowToXWindow(paramXClientMessageEvent.get_window());
/*  489 */     if ((localXBaseWindow1 instanceof XWindow)) {
/*  490 */       localXWindow = (XWindow)localXBaseWindow1;
/*      */     }
/*      */ 
/*  494 */     if (i == 8)
/*      */     {
/*  497 */       j = this.sourceX;
/*  498 */       k = this.sourceY;
/*      */ 
/*  500 */       if (localXWindow == null)
/*  501 */         localXWindow = this.targetXWindow;
/*      */     }
/*      */     else {
/*  504 */       j = MotifDnDConstants.Swapper.getShort(l1 + 8L, b);
/*  505 */       k = MotifDnDConstants.Swapper.getShort(l1 + 10L, b);
/*      */ 
/*  507 */       if (localXWindow == null) {
/*  508 */         long l2 = XDropTargetRegistry.getRegistry().getEmbeddedDropSite(paramXClientMessageEvent.get_window(), j, k);
/*      */ 
/*  512 */         if (l2 != 0L) {
/*  513 */           XBaseWindow localXBaseWindow2 = XToolkit.windowToXWindow(l2);
/*  514 */           if ((localXBaseWindow2 instanceof XWindow)) {
/*  515 */             localXWindow = (XWindow)localXBaseWindow2;
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*  520 */       if (localXWindow != null) {
/*  521 */         Point localPoint = localXWindow.toLocal(j, k);
/*  522 */         j = localPoint.x;
/*  523 */         k = localPoint.y;
/*      */       }
/*      */     }
/*      */ 
/*  527 */     if (localXWindow == null) {
/*  528 */       if (this.targetXWindow != null) {
/*  529 */         notifyProtocolListener(this.targetXWindow, j, k, 0, i3, paramXClientMessageEvent, 505);
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*  534 */       int i5 = 0;
/*      */ 
/*  536 */       if (this.targetXWindow == null)
/*  537 */         i5 = 504;
/*      */       else {
/*  539 */         i5 = 506;
/*      */       }
/*      */ 
/*  542 */       notifyProtocolListener(localXWindow, j, k, i2, i3, paramXClientMessageEvent, i5);
/*      */     }
/*      */ 
/*  546 */     this.sourceActions = i3;
/*  547 */     this.userAction = i2;
/*  548 */     this.sourceX = j;
/*  549 */     this.sourceY = k;
/*  550 */     this.targetXWindow = localXWindow;
/*      */ 
/*  552 */     return true;
/*      */   }
/*      */ 
/*      */   private boolean processTopLevelLeave(XClientMessageEvent paramXClientMessageEvent) {
/*  556 */     assert (XToolkit.isAWTLockHeldByCurrentThread());
/*      */ 
/*  558 */     long l1 = paramXClientMessageEvent.get_data();
/*  559 */     byte b = unsafe.getByte(l1 + 1L);
/*      */ 
/*  561 */     long l2 = MotifDnDConstants.Swapper.getInt(l1 + 8L, b);
/*      */ 
/*  564 */     if (l2 != this.sourceWindow) {
/*  565 */       return false;
/*      */     }
/*      */ 
/*  574 */     this.topLevelLeavePostponed = true;
/*      */     long l3;
/*  584 */     if (getEmbedderRegistryEntry(paramXClientMessageEvent.get_window()) != null)
/*  585 */       l3 = XDropTargetRegistry.getDnDProxyWindow();
/*      */     else {
/*  587 */       l3 = paramXClientMessageEvent.get_window();
/*      */     }
/*      */ 
/*  590 */     XClientMessageEvent localXClientMessageEvent = new XClientMessageEvent();
/*      */     try
/*      */     {
/*  593 */       localXClientMessageEvent.set_type(33);
/*  594 */       localXClientMessageEvent.set_window(paramXClientMessageEvent.get_window());
/*  595 */       localXClientMessageEvent.set_format(32);
/*  596 */       localXClientMessageEvent.set_message_type(0L);
/*  597 */       localXClientMessageEvent.set_data(0, 0L);
/*  598 */       localXClientMessageEvent.set_data(1, 0L);
/*  599 */       localXClientMessageEvent.set_data(2, 0L);
/*  600 */       localXClientMessageEvent.set_data(3, 0L);
/*  601 */       localXClientMessageEvent.set_data(4, 0L);
/*  602 */       XlibWrapper.XSendEvent(XToolkit.getDisplay(), l3, false, 0L, localXClientMessageEvent.pData);
/*      */     }
/*      */     finally
/*      */     {
/*  606 */       localXClientMessageEvent.dispose();
/*      */     }
/*      */ 
/*  609 */     return true;
/*      */   }
/*      */ 
/*      */   private boolean processDropStart(XClientMessageEvent paramXClientMessageEvent) {
/*  613 */     long l1 = paramXClientMessageEvent.get_data();
/*  614 */     byte b = unsafe.getByte(l1 + 1L);
/*      */ 
/*  616 */     long l2 = MotifDnDConstants.Swapper.getInt(l1 + 16L, b);
/*      */ 
/*  620 */     if (l2 != this.sourceWindow) {
/*  621 */       return false;
/*      */     }
/*      */ 
/*  624 */     long l3 = MotifDnDConstants.Swapper.getInt(l1 + 12L, b);
/*      */ 
/*  627 */     int i = MotifDnDConstants.Swapper.getShort(l1 + 2L, b);
/*      */ 
/*  630 */     int j = (i & 0xF) >> 0;
/*      */ 
/*  632 */     int k = (i & 0xF00) >> 8;
/*      */ 
/*  635 */     int m = MotifDnDConstants.getJavaActionsForMotifActions(j);
/*  636 */     int n = MotifDnDConstants.getJavaActionsForMotifActions(k);
/*      */ 
/*  638 */     int i1 = MotifDnDConstants.Swapper.getShort(l1 + 8L, b);
/*  639 */     int i2 = MotifDnDConstants.Swapper.getShort(l1 + 10L, b);
/*      */ 
/*  641 */     XWindow localXWindow = null;
/*      */ 
/*  643 */     XBaseWindow localXBaseWindow1 = XToolkit.windowToXWindow(paramXClientMessageEvent.get_window());
/*  644 */     if ((localXBaseWindow1 instanceof XWindow)) {
/*  645 */       localXWindow = (XWindow)localXBaseWindow1;
/*      */     }
/*      */ 
/*  649 */     if (localXWindow == null) {
/*  650 */       long l4 = XDropTargetRegistry.getRegistry().getEmbeddedDropSite(paramXClientMessageEvent.get_window(), i1, i2);
/*      */ 
/*  654 */       if (l4 != 0L) {
/*  655 */         XBaseWindow localXBaseWindow2 = XToolkit.windowToXWindow(l4);
/*  656 */         if ((localXBaseWindow2 instanceof XWindow)) {
/*  657 */           localXWindow = (XWindow)localXBaseWindow2;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  662 */     if (localXWindow != null) {
/*  663 */       Point localPoint = localXWindow.toLocal(i1, i2);
/*  664 */       i1 = localPoint.x;
/*  665 */       i2 = localPoint.y;
/*      */     }
/*      */ 
/*  668 */     if (localXWindow != null) {
/*  669 */       notifyProtocolListener(localXWindow, i1, i2, m, n, paramXClientMessageEvent, 502);
/*      */     }
/*  671 */     else if (this.targetXWindow != null) {
/*  672 */       notifyProtocolListener(this.targetXWindow, i1, i2, 0, n, paramXClientMessageEvent, 505);
/*      */     }
/*      */ 
/*  677 */     return true;
/*      */   }
/*      */ 
/*      */   public int getMessageType(XClientMessageEvent paramXClientMessageEvent) {
/*  681 */     if (paramXClientMessageEvent.get_message_type() != MotifDnDConstants.XA_MOTIF_DRAG_AND_DROP_MESSAGE.getAtom())
/*      */     {
/*  684 */       return 0;
/*      */     }
/*      */ 
/*  687 */     long l = paramXClientMessageEvent.get_data();
/*  688 */     int i = (byte)(unsafe.getByte(l) & 0x7F);
/*      */ 
/*  691 */     switch (i) {
/*      */     case 0:
/*  693 */       return 1;
/*      */     case 2:
/*      */     case 8:
/*  696 */       return 2;
/*      */     case 1:
/*  698 */       return 3;
/*      */     case 5:
/*  700 */       return 4;
/*      */     case 3:
/*      */     case 4:
/*      */     case 6:
/*  702 */     case 7: } return 0;
/*      */   }
/*      */ 
/*      */   protected boolean processClientMessageImpl(XClientMessageEvent paramXClientMessageEvent)
/*      */   {
/*  707 */     if (paramXClientMessageEvent.get_message_type() != MotifDnDConstants.XA_MOTIF_DRAG_AND_DROP_MESSAGE.getAtom())
/*      */     {
/*  709 */       if (this.topLevelLeavePostponed) {
/*  710 */         this.topLevelLeavePostponed = false;
/*  711 */         cleanup();
/*      */       }
/*      */ 
/*  714 */       return false;
/*      */     }
/*      */ 
/*  717 */     long l = paramXClientMessageEvent.get_data();
/*  718 */     int i = (byte)(unsafe.getByte(l) & 0x7F);
/*      */ 
/*  720 */     int j = (byte)(unsafe.getByte(l) & 0xFFFFFF80);
/*      */ 
/*  723 */     if (this.topLevelLeavePostponed) {
/*  724 */       this.topLevelLeavePostponed = false;
/*  725 */       if (i != 5) {
/*  726 */         cleanup();
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  731 */     if (j != 0) {
/*  732 */       return false;
/*      */     }
/*      */ 
/*  735 */     switch (i) {
/*      */     case 0:
/*  737 */       return processTopLevelEnter(paramXClientMessageEvent);
/*      */     case 2:
/*      */     case 8:
/*  740 */       return processDragMotion(paramXClientMessageEvent);
/*      */     case 1:
/*  742 */       return processTopLevelLeave(paramXClientMessageEvent);
/*      */     case 5:
/*  744 */       return processDropStart(paramXClientMessageEvent);
/*      */     case 3:
/*      */     case 4:
/*      */     case 6:
/*  746 */     case 7: } return false;
/*      */   }
/*      */ 
/*      */   protected void sendEnterMessageToToplevel(long paramLong, XClientMessageEvent paramXClientMessageEvent)
/*      */   {
/*  756 */     throw new Error("UNIMPLEMENTED");
/*      */   }
/*      */ 
/*      */   protected void sendLeaveMessageToToplevel(long paramLong, XClientMessageEvent paramXClientMessageEvent)
/*      */   {
/*  761 */     throw new Error("UNIMPLEMENTED");
/*      */   }
/*      */ 
/*      */   public boolean forwardEventToEmbedded(long paramLong1, long paramLong2, int paramInt)
/*      */   {
/*  767 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isXEmbedSupported() {
/*  771 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean sendResponse(long paramLong, int paramInt1, int paramInt2) {
/*  775 */     XClientMessageEvent localXClientMessageEvent1 = new XClientMessageEvent(paramLong);
/*  776 */     if (localXClientMessageEvent1.get_message_type() != MotifDnDConstants.XA_MOTIF_DRAG_AND_DROP_MESSAGE.getAtom())
/*      */     {
/*  778 */       return false;
/*      */     }
/*      */ 
/*  781 */     long l1 = localXClientMessageEvent1.get_data();
/*  782 */     int i = (byte)(unsafe.getByte(l1) & 0x7F);
/*      */ 
/*  784 */     int j = (byte)(unsafe.getByte(l1) & 0xFFFFFF80);
/*      */ 
/*  786 */     byte b = unsafe.getByte(l1 + 1L);
/*  787 */     int k = 0;
/*      */ 
/*  790 */     if (j != 0) {
/*  791 */       return false;
/*      */     }
/*      */ 
/*  794 */     switch (i)
/*      */     {
/*      */     case 0:
/*      */     case 1:
/*  798 */       return false;
/*      */     case 2:
/*  800 */       switch (paramInt1) {
/*      */       case 504:
/*  802 */         k = 3;
/*  803 */         break;
/*      */       case 506:
/*  805 */         k = 2;
/*  806 */         break;
/*      */       case 505:
/*  808 */         k = 4;
/*      */       }
/*      */ 
/*  811 */       break;
/*      */     case 5:
/*      */     case 8:
/*  814 */       k = i;
/*  815 */       break;
/*      */     case 3:
/*      */     case 4:
/*      */     case 6:
/*      */     case 7:
/*      */     default:
/*  818 */       if (!$assertionsDisabled) throw new AssertionError();
/*      */       break;
/*      */     }
/*  821 */     XClientMessageEvent localXClientMessageEvent2 = new XClientMessageEvent();
/*      */     try
/*      */     {
/*  824 */       localXClientMessageEvent2.set_type(33);
/*  825 */       localXClientMessageEvent2.set_window(MotifDnDConstants.Swapper.getInt(l1 + 12L, b));
/*  826 */       localXClientMessageEvent2.set_format(8);
/*  827 */       localXClientMessageEvent2.set_message_type(MotifDnDConstants.XA_MOTIF_DRAG_AND_DROP_MESSAGE.getAtom());
/*      */ 
/*  829 */       long l2 = localXClientMessageEvent2.get_data();
/*      */ 
/*  831 */       unsafe.putByte(l2, (byte)(k | 0xFFFFFF80));
/*      */ 
/*  833 */       unsafe.putByte(l2 + 1L, MotifDnDConstants.getByteOrderByte());
/*      */ 
/*  835 */       int m = 0;
/*      */       short s1;
/*  837 */       if (k != 4) {
/*  838 */         n = MotifDnDConstants.Swapper.getShort(l1 + 2L, b);
/*      */ 
/*  840 */         s1 = paramInt2 == 0 ? 2 : 3;
/*      */ 
/*  845 */         m = n & 0xFFFFFFF0 & 0xFFFFFF0F;
/*      */ 
/*  849 */         m |= MotifDnDConstants.getMotifActionsForJavaActions(paramInt2) << 0;
/*      */ 
/*  852 */         m |= s1 << 4;
/*      */       }
/*      */       else {
/*  855 */         m = 0;
/*      */       }
/*      */ 
/*  858 */       unsafe.putShort(l2 + 2L, (short)m);
/*      */ 
/*  861 */       int n = MotifDnDConstants.Swapper.getInt(l1 + 4L, b);
/*  862 */       unsafe.putInt(l2 + 4L, n);
/*      */ 
/*  865 */       if (k != 4) {
/*  866 */         s1 = MotifDnDConstants.Swapper.getShort(l1 + 8L, b);
/*      */ 
/*  868 */         short s2 = MotifDnDConstants.Swapper.getShort(l1 + 10L, b);
/*      */ 
/*  870 */         unsafe.putShort(l2 + 8L, s1);
/*  871 */         unsafe.putShort(l2 + 10L, s2);
/*      */       } else {
/*  873 */         unsafe.putShort(l2 + 8L, (short)0);
/*  874 */         unsafe.putShort(l2 + 10L, (short)0);
/*      */       }
/*      */ 
/*  877 */       XToolkit.awtLock();
/*      */       try {
/*  879 */         XlibWrapper.XSendEvent(XToolkit.getDisplay(), localXClientMessageEvent2.get_window(), false, 0L, localXClientMessageEvent2.pData);
/*      */       }
/*      */       finally
/*      */       {
/*  884 */         XToolkit.awtUnlock();
/*      */       }
/*      */     } finally {
/*  887 */       localXClientMessageEvent2.dispose();
/*      */     }
/*      */ 
/*  890 */     return true;
/*      */   }
/*      */ 
/*      */   public Object getData(long paramLong1, long paramLong2) throws IllegalArgumentException, IOException
/*      */   {
/*  895 */     XClientMessageEvent localXClientMessageEvent = new XClientMessageEvent(paramLong1);
/*      */ 
/*  897 */     if (localXClientMessageEvent.get_message_type() != MotifDnDConstants.XA_MOTIF_DRAG_AND_DROP_MESSAGE.getAtom())
/*      */     {
/*  899 */       throw new IllegalArgumentException();
/*      */     }
/*      */ 
/*  902 */     long l1 = localXClientMessageEvent.get_data();
/*  903 */     int i = (byte)(unsafe.getByte(l1) & 0x7F);
/*      */ 
/*  905 */     int j = (byte)(unsafe.getByte(l1) & 0xFFFFFF80);
/*      */ 
/*  907 */     byte b = unsafe.getByte(l1 + 1L);
/*      */ 
/*  909 */     if (j != 0) {
/*  910 */       throw new IOException("Cannot get data: corrupted context");
/*      */     }
/*      */ 
/*  913 */     long l2 = 0L;
/*      */ 
/*  915 */     switch (i) {
/*      */     case 2:
/*      */     case 8:
/*  918 */       l2 = this.sourceAtom;
/*  919 */       break;
/*      */     case 5:
/*  921 */       l2 = MotifDnDConstants.Swapper.getInt(l1 + 12L, b);
/*  922 */       break;
/*      */     default:
/*  924 */       throw new IOException("Cannot get data: invalid message reason");
/*      */     }
/*      */ 
/*  927 */     if (l2 == 0L) {
/*  928 */       throw new IOException("Cannot get data: drag source property atom unavailable");
/*      */     }
/*      */ 
/*  931 */     long l3 = MotifDnDConstants.Swapper.getInt(l1 + 4L, b) & 0xFFFFFFFF;
/*      */ 
/*  934 */     XAtom localXAtom = XAtom.get(l2);
/*      */ 
/*  936 */     XSelection localXSelection = XSelection.getSelection(localXAtom);
/*  937 */     if (localXSelection == null) {
/*  938 */       localXSelection = new XSelection(localXAtom);
/*      */     }
/*      */ 
/*  941 */     return localXSelection.getData(paramLong2, l3);
/*      */   }
/*      */ 
/*      */   public boolean sendDropDone(long paramLong, boolean paramBoolean, int paramInt) {
/*  945 */     XClientMessageEvent localXClientMessageEvent = new XClientMessageEvent(paramLong);
/*      */ 
/*  947 */     if (localXClientMessageEvent.get_message_type() != MotifDnDConstants.XA_MOTIF_DRAG_AND_DROP_MESSAGE.getAtom())
/*      */     {
/*  949 */       return false;
/*      */     }
/*      */ 
/*  952 */     long l1 = localXClientMessageEvent.get_data();
/*  953 */     int i = (byte)(unsafe.getByte(l1) & 0x7F);
/*      */ 
/*  955 */     int j = (byte)(unsafe.getByte(l1) & 0xFFFFFF80);
/*      */ 
/*  957 */     byte b = unsafe.getByte(l1 + 1L);
/*      */ 
/*  959 */     if (j != 0) {
/*  960 */       return false;
/*      */     }
/*      */ 
/*  963 */     if (i != 5) {
/*  964 */       return false;
/*      */     }
/*      */ 
/*  967 */     long l2 = MotifDnDConstants.Swapper.getInt(l1 + 4L, b) & 0xFFFFFFFF;
/*      */ 
/*  970 */     long l3 = MotifDnDConstants.Swapper.getInt(l1 + 12L, b);
/*      */ 
/*  972 */     long l4 = 0L;
/*      */ 
/*  974 */     if (paramBoolean)
/*  975 */       l4 = MotifDnDConstants.XA_XmTRANSFER_SUCCESS.getAtom();
/*      */     else {
/*  977 */       l4 = MotifDnDConstants.XA_XmTRANSFER_FAILURE.getAtom();
/*      */     }
/*      */ 
/*  980 */     XToolkit.awtLock();
/*      */     try {
/*  982 */       XlibWrapper.XConvertSelection(XToolkit.getDisplay(), l3, l4, MotifDnDConstants.XA_MOTIF_ATOM_0.getAtom(), XWindow.getXAWTRootWindow().getWindow(), l2);
/*      */ 
/*  993 */       XlibWrapper.XFlush(XToolkit.getDisplay());
/*      */     } finally {
/*  995 */       XToolkit.awtUnlock();
/*      */     }
/*      */ 
/*  999 */     this.targetXWindow = null;
/*      */ 
/* 1003 */     cleanup();
/* 1004 */     return true;
/*      */   }
/*      */ 
/*      */   public final long getSourceWindow() {
/* 1008 */     return this.sourceWindow;
/*      */   }
/*      */ 
/*      */   public void cleanup()
/*      */   {
/* 1016 */     XDropTargetEventProcessor.reset();
/*      */ 
/* 1018 */     if (this.targetXWindow != null) {
/* 1019 */       notifyProtocolListener(this.targetXWindow, 0, 0, 0, this.sourceActions, null, 505);
/*      */     }
/*      */ 
/* 1024 */     if (this.sourceWindow != 0L) {
/* 1025 */       XToolkit.awtLock();
/*      */       try {
/* 1027 */         XToolkit.WITH_XERROR_HANDLER(XErrorHandler.IgnoreBadWindowHandler.getInstance());
/* 1028 */         XlibWrapper.XSelectInput(XToolkit.getDisplay(), this.sourceWindow, this.sourceWindowMask);
/*      */ 
/* 1030 */         XToolkit.RESTORE_XERROR_HANDLER();
/*      */       } finally {
/* 1032 */         XToolkit.awtUnlock();
/*      */       }
/*      */     }
/*      */ 
/* 1036 */     this.sourceWindow = 0L;
/* 1037 */     this.sourceWindowMask = 0L;
/* 1038 */     this.sourceProtocolVersion = 0;
/* 1039 */     this.sourceActions = 0;
/* 1040 */     this.sourceFormats = null;
/* 1041 */     this.sourceAtom = 0L;
/* 1042 */     this.userAction = 0;
/* 1043 */     this.sourceX = 0;
/* 1044 */     this.sourceY = 0;
/* 1045 */     this.targetXWindow = null;
/* 1046 */     this.topLevelLeavePostponed = false;
/*      */   }
/*      */ 
/*      */   public boolean isDragOverComponent() {
/* 1050 */     return this.targetXWindow != null;
/*      */   }
/*      */ 
/*      */   private void notifyProtocolListener(XWindow paramXWindow, int paramInt1, int paramInt2, int paramInt3, int paramInt4, XClientMessageEvent paramXClientMessageEvent, int paramInt5)
/*      */   {
/* 1057 */     long l = 0L;
/*      */ 
/* 1062 */     if (paramXClientMessageEvent != null) {
/* 1063 */       int i = XClientMessageEvent.getSize();
/*      */ 
/* 1065 */       l = unsafe.allocateMemory(i + 4 * Native.getLongSize());
/*      */ 
/* 1067 */       unsafe.copyMemory(paramXClientMessageEvent.pData, l, i);
/*      */     }
/*      */ 
/* 1070 */     getProtocolListener().handleDropTargetNotification(paramXWindow, paramInt1, paramInt2, paramInt3, paramInt4, this.sourceFormats, l, paramInt5);
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.MotifDnDDropTargetProtocol
 * JD-Core Version:    0.6.2
 */