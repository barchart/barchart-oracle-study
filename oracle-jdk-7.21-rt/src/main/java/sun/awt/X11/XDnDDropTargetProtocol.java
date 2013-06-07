/*      */ package sun.awt.X11;
/*      */ 
/*      */ import java.awt.Point;
/*      */ import java.io.IOException;
/*      */ import sun.misc.Unsafe;
/*      */ import sun.util.logging.PlatformLogger;
/*      */ 
/*      */ class XDnDDropTargetProtocol extends XDropTargetProtocol
/*      */ {
/*   46 */   private static final PlatformLogger logger = PlatformLogger.getLogger("sun.awt.X11.xembed.xdnd.XDnDDropTargetProtocol");
/*      */ 
/*   49 */   private static final Unsafe unsafe = XlibWrapper.unsafe;
/*      */ 
/*   51 */   private long sourceWindow = 0L;
/*   52 */   private long sourceWindowMask = 0L;
/*   53 */   private int sourceProtocolVersion = 0;
/*   54 */   private int sourceActions = 0;
/*   55 */   private long[] sourceFormats = null;
/*   56 */   private boolean trackSourceActions = false;
/*   57 */   private int userAction = 0;
/*   58 */   private int sourceX = 0;
/*   59 */   private int sourceY = 0;
/*   60 */   private XWindow targetXWindow = null;
/*      */ 
/*   63 */   private long prevCtxt = 0L;
/*   64 */   private boolean overXEmbedClient = false;
/*      */ 
/*      */   protected XDnDDropTargetProtocol(XDropTargetProtocolListener paramXDropTargetProtocolListener) {
/*   67 */     super(paramXDropTargetProtocolListener);
/*      */   }
/*      */ 
/*      */   static XDropTargetProtocol createInstance(XDropTargetProtocolListener paramXDropTargetProtocolListener)
/*      */   {
/*   76 */     return new XDnDDropTargetProtocol(paramXDropTargetProtocolListener);
/*      */   }
/*      */ 
/*      */   public String getProtocolName() {
/*   80 */     return "XDnD";
/*      */   }
/*      */ 
/*      */   public void registerDropTarget(long paramLong) {
/*   84 */     assert (XToolkit.isAWTLockHeldByCurrentThread());
/*      */ 
/*   86 */     long l = Native.allocateLongArray(1);
/*      */     try
/*      */     {
/*   89 */       Native.putLong(l, 0, 5L);
/*      */ 
/*   91 */       XToolkit.WITH_XERROR_HANDLER(XErrorHandler.VerifyChangePropertyHandler.getInstance());
/*   92 */       XDnDConstants.XA_XdndAware.setAtomData(paramLong, 4L, l, 1);
/*   93 */       XToolkit.RESTORE_XERROR_HANDLER();
/*      */ 
/*   95 */       if ((XToolkit.saved_error != null) && (XToolkit.saved_error.get_error_code() != 0))
/*      */       {
/*   97 */         throw new XException("Cannot write XdndAware property");
/*      */       }
/*      */     } finally {
/*  100 */       unsafe.freeMemory(l);
/*  101 */       l = 0L;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void unregisterDropTarget(long paramLong) {
/*  106 */     assert (XToolkit.isAWTLockHeldByCurrentThread());
/*      */ 
/*  108 */     XDnDConstants.XA_XdndAware.DeleteProperty(paramLong);
/*      */   }
/*      */ 
/*      */   public void registerEmbedderDropSite(long paramLong) {
/*  112 */     assert (XToolkit.isAWTLockHeldByCurrentThread());
/*      */ 
/*  114 */     boolean bool = false;
/*  115 */     int i = 0;
/*  116 */     long l1 = 0L;
/*  117 */     long l2 = XDropTargetRegistry.getDnDProxyWindow();
/*  118 */     int j = 0;
/*      */ 
/*  120 */     WindowPropertyGetter localWindowPropertyGetter1 = new WindowPropertyGetter(paramLong, XDnDConstants.XA_XdndAware, 0L, 1L, false, 0L);
/*      */     try
/*      */     {
/*  125 */       j = localWindowPropertyGetter1.execute(XErrorHandler.IgnoreBadWindowHandler.getInstance());
/*      */ 
/*  127 */       if ((j == 0) && (localWindowPropertyGetter1.getData() != 0L) && (localWindowPropertyGetter1.getActualType() == 4L))
/*      */       {
/*  130 */         bool = true;
/*  131 */         i = (int)Native.getLong(localWindowPropertyGetter1.getData());
/*      */       }
/*      */     } finally {
/*  134 */       localWindowPropertyGetter1.dispose();
/*      */     }
/*      */ 
/*  138 */     if ((bool) && (i >= 4)) {
/*  139 */       WindowPropertyGetter localWindowPropertyGetter2 = new WindowPropertyGetter(paramLong, XDnDConstants.XA_XdndProxy, 0L, 1L, false, 33L);
/*      */       try
/*      */       {
/*  144 */         j = localWindowPropertyGetter2.execute(XErrorHandler.IgnoreBadWindowHandler.getInstance());
/*      */ 
/*  146 */         if ((j == 0) && (localWindowPropertyGetter2.getData() != 0L) && (localWindowPropertyGetter2.getActualType() == 33L))
/*      */         {
/*  150 */           l1 = Native.getLong(localWindowPropertyGetter2.getData());
/*      */         }
/*      */       } finally {
/*  153 */         localWindowPropertyGetter2.dispose();
/*      */       }
/*      */ 
/*  156 */       if (l1 != 0L) {
/*  157 */         WindowPropertyGetter localWindowPropertyGetter3 = new WindowPropertyGetter(l1, XDnDConstants.XA_XdndProxy, 0L, 1L, false, 33L);
/*      */         try
/*      */         {
/*  162 */           j = localWindowPropertyGetter3.execute(XErrorHandler.IgnoreBadWindowHandler.getInstance());
/*      */ 
/*  164 */           if ((j != 0) || (localWindowPropertyGetter3.getData() == 0L) || (localWindowPropertyGetter3.getActualType() != 33L) || (Native.getLong(localWindowPropertyGetter3.getData()) != l1))
/*      */           {
/*  169 */             l1 = 0L;
/*      */           } else {
/*  171 */             WindowPropertyGetter localWindowPropertyGetter4 = new WindowPropertyGetter(l1, XDnDConstants.XA_XdndAware, 0L, 1L, false, 0L);
/*      */             try
/*      */             {
/*  178 */               j = localWindowPropertyGetter4.execute(XErrorHandler.IgnoreBadWindowHandler.getInstance());
/*      */ 
/*  180 */               if ((j != 0) || (localWindowPropertyGetter4.getData() == 0L) || (localWindowPropertyGetter4.getActualType() != 4L))
/*      */               {
/*  184 */                 l1 = 0L;
/*      */               }
/*      */             } finally {
/*      */             }
/*      */           }
/*      */         }
/*      */         finally {
/*  191 */           localWindowPropertyGetter3.dispose();
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  196 */     if (l1 == l2)
/*      */     {
/*  198 */       return;
/*      */     }
/*      */ 
/*  201 */     long l3 = Native.allocateLongArray(1);
/*      */     try
/*      */     {
/*  204 */       Native.putLong(l3, 0, 5L);
/*      */ 
/*  208 */       XToolkit.WITH_XERROR_HANDLER(XErrorHandler.VerifyChangePropertyHandler.getInstance());
/*  209 */       XDnDConstants.XA_XdndAware.setAtomData(l2, 4L, l3, 1);
/*      */ 
/*  211 */       XToolkit.RESTORE_XERROR_HANDLER();
/*      */ 
/*  213 */       if ((XToolkit.saved_error != null) && (XToolkit.saved_error.get_error_code() != 0))
/*      */       {
/*  216 */         throw new XException("Cannot write XdndAware property");
/*      */       }
/*      */ 
/*  219 */       Native.putLong(l3, 0, l2);
/*      */ 
/*  222 */       XToolkit.WITH_XERROR_HANDLER(XErrorHandler.VerifyChangePropertyHandler.getInstance());
/*  223 */       XDnDConstants.XA_XdndProxy.setAtomData(l2, 33L, l3, 1);
/*      */ 
/*  225 */       XToolkit.RESTORE_XERROR_HANDLER();
/*      */ 
/*  227 */       if ((XToolkit.saved_error != null) && (XToolkit.saved_error.get_error_code() != 0))
/*      */       {
/*  230 */         throw new XException("Cannot write XdndProxy property");
/*      */       }
/*      */ 
/*  233 */       Native.putLong(l3, 0, 5L);
/*      */ 
/*  235 */       XToolkit.WITH_XERROR_HANDLER(XErrorHandler.VerifyChangePropertyHandler.getInstance());
/*  236 */       XDnDConstants.XA_XdndAware.setAtomData(paramLong, 4L, l3, 1);
/*      */ 
/*  238 */       XToolkit.RESTORE_XERROR_HANDLER();
/*      */ 
/*  240 */       if ((XToolkit.saved_error != null) && (XToolkit.saved_error.get_error_code() != 0))
/*      */       {
/*  243 */         throw new XException("Cannot write XdndAware property");
/*      */       }
/*      */ 
/*  246 */       Native.putLong(l3, 0, l2);
/*      */ 
/*  248 */       XToolkit.WITH_XERROR_HANDLER(XErrorHandler.VerifyChangePropertyHandler.getInstance());
/*  249 */       XDnDConstants.XA_XdndProxy.setAtomData(paramLong, 33L, l3, 1);
/*      */ 
/*  251 */       XToolkit.RESTORE_XERROR_HANDLER();
/*      */ 
/*  253 */       if ((XToolkit.saved_error != null) && (XToolkit.saved_error.get_error_code() != 0))
/*      */       {
/*  256 */         throw new XException("Cannot write XdndProxy property");
/*      */       }
/*      */     } finally {
/*  259 */       unsafe.freeMemory(l3);
/*  260 */       l3 = 0L;
/*      */     }
/*      */ 
/*  263 */     putEmbedderRegistryEntry(paramLong, bool, i, l1);
/*      */   }
/*      */ 
/*      */   public void unregisterEmbedderDropSite(long paramLong) {
/*  267 */     assert (XToolkit.isAWTLockHeldByCurrentThread());
/*      */ 
/*  269 */     XDropTargetProtocol.EmbedderRegistryEntry localEmbedderRegistryEntry = getEmbedderRegistryEntry(paramLong);
/*      */ 
/*  271 */     if (localEmbedderRegistryEntry == null) {
/*  272 */       return;
/*      */     }
/*      */ 
/*  275 */     if (localEmbedderRegistryEntry.isOverriden()) {
/*  276 */       long l = Native.allocateLongArray(1);
/*      */       try
/*      */       {
/*  279 */         Native.putLong(l, 0, localEmbedderRegistryEntry.getVersion());
/*      */ 
/*  281 */         XToolkit.WITH_XERROR_HANDLER(XErrorHandler.VerifyChangePropertyHandler.getInstance());
/*  282 */         XDnDConstants.XA_XdndAware.setAtomData(paramLong, 4L, l, 1);
/*      */ 
/*  284 */         XToolkit.RESTORE_XERROR_HANDLER();
/*      */ 
/*  286 */         if ((XToolkit.saved_error != null) && (XToolkit.saved_error.get_error_code() != 0))
/*      */         {
/*  289 */           throw new XException("Cannot write XdndAware property");
/*      */         }
/*      */ 
/*  292 */         Native.putLong(l, 0, (int)localEmbedderRegistryEntry.getProxy());
/*      */ 
/*  294 */         XToolkit.WITH_XERROR_HANDLER(XErrorHandler.VerifyChangePropertyHandler.getInstance());
/*  295 */         XDnDConstants.XA_XdndProxy.setAtomData(paramLong, 33L, l, 1);
/*      */ 
/*  297 */         XToolkit.RESTORE_XERROR_HANDLER();
/*      */ 
/*  299 */         if ((XToolkit.saved_error != null) && (XToolkit.saved_error.get_error_code() != 0))
/*      */         {
/*  302 */           throw new XException("Cannot write XdndProxy property");
/*      */         }
/*      */       } finally {
/*  305 */         unsafe.freeMemory(l);
/*  306 */         l = 0L;
/*      */       }
/*      */     } else {
/*  309 */       XDnDConstants.XA_XdndAware.DeleteProperty(paramLong);
/*  310 */       XDnDConstants.XA_XdndProxy.DeleteProperty(paramLong);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void registerEmbeddedDropSite(long paramLong)
/*      */   {
/*  319 */     assert (XToolkit.isAWTLockHeldByCurrentThread());
/*      */ 
/*  321 */     boolean bool = false;
/*  322 */     int i = 0;
/*  323 */     long l1 = 0L;
/*  324 */     long l2 = XDropTargetRegistry.getDnDProxyWindow();
/*  325 */     int j = 0;
/*      */ 
/*  327 */     WindowPropertyGetter localWindowPropertyGetter1 = new WindowPropertyGetter(paramLong, XDnDConstants.XA_XdndAware, 0L, 1L, false, 0L);
/*      */     try
/*      */     {
/*  332 */       j = localWindowPropertyGetter1.execute(XErrorHandler.IgnoreBadWindowHandler.getInstance());
/*      */ 
/*  334 */       if ((j == 0) && (localWindowPropertyGetter1.getData() != 0L) && (localWindowPropertyGetter1.getActualType() == 4L))
/*      */       {
/*  337 */         bool = true;
/*  338 */         i = (int)Native.getLong(localWindowPropertyGetter1.getData());
/*      */       }
/*      */     } finally {
/*  341 */       localWindowPropertyGetter1.dispose();
/*      */     }
/*      */ 
/*  345 */     if ((bool) && (i >= 4)) {
/*  346 */       WindowPropertyGetter localWindowPropertyGetter2 = new WindowPropertyGetter(paramLong, XDnDConstants.XA_XdndProxy, 0L, 1L, false, 33L);
/*      */       try
/*      */       {
/*  351 */         j = localWindowPropertyGetter2.execute(XErrorHandler.IgnoreBadWindowHandler.getInstance());
/*      */ 
/*  353 */         if ((j == 0) && (localWindowPropertyGetter2.getData() != 0L) && (localWindowPropertyGetter2.getActualType() == 33L))
/*      */         {
/*  357 */           l1 = Native.getLong(localWindowPropertyGetter2.getData());
/*      */         }
/*      */       } finally {
/*  360 */         localWindowPropertyGetter2.dispose();
/*      */       }
/*      */ 
/*  363 */       if (l1 != 0L) {
/*  364 */         WindowPropertyGetter localWindowPropertyGetter3 = new WindowPropertyGetter(l1, XDnDConstants.XA_XdndProxy, 0L, 1L, false, 33L);
/*      */         try
/*      */         {
/*  369 */           j = localWindowPropertyGetter3.execute(XErrorHandler.IgnoreBadWindowHandler.getInstance());
/*      */ 
/*  371 */           if ((j != 0) || (localWindowPropertyGetter3.getData() == 0L) || (localWindowPropertyGetter3.getActualType() != 33L) || (Native.getLong(localWindowPropertyGetter3.getData()) != l1))
/*      */           {
/*  376 */             l1 = 0L;
/*      */           } else {
/*  378 */             WindowPropertyGetter localWindowPropertyGetter4 = new WindowPropertyGetter(l1, XDnDConstants.XA_XdndAware, 0L, 1L, false, 0L);
/*      */             try
/*      */             {
/*  385 */               j = localWindowPropertyGetter4.execute(XErrorHandler.IgnoreBadWindowHandler.getInstance());
/*      */ 
/*  387 */               if ((j != 0) || (localWindowPropertyGetter4.getData() == 0L) || (localWindowPropertyGetter4.getActualType() != 4L))
/*      */               {
/*  391 */                 l1 = 0L;
/*      */               }
/*      */             } finally {
/*      */             }
/*      */           }
/*      */         }
/*      */         finally {
/*  398 */           localWindowPropertyGetter3.dispose();
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  403 */     putEmbedderRegistryEntry(paramLong, bool, i, l1);
/*      */   }
/*      */ 
/*      */   public boolean isProtocolSupported(long paramLong) {
/*  407 */     assert (XToolkit.isAWTLockHeldByCurrentThread());
/*      */ 
/*  409 */     WindowPropertyGetter localWindowPropertyGetter = new WindowPropertyGetter(paramLong, XDnDConstants.XA_XdndAware, 0L, 1L, false, 0L);
/*      */     try
/*      */     {
/*  414 */       int i = localWindowPropertyGetter.execute(XErrorHandler.IgnoreBadWindowHandler.getInstance());
/*      */       boolean bool;
/*  416 */       if ((i == 0) && (localWindowPropertyGetter.getData() != 0L) && (localWindowPropertyGetter.getActualType() == 4L))
/*      */       {
/*  419 */         return true;
/*      */       }
/*  421 */       return false;
/*      */     }
/*      */     finally {
/*  424 */       localWindowPropertyGetter.dispose();
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean processXdndEnter(XClientMessageEvent paramXClientMessageEvent) {
/*  429 */     long l1 = 0L;
/*  430 */     long l2 = 0L;
/*  431 */     int i = 0;
/*  432 */     int j = 0;
/*  433 */     boolean bool = true;
/*  434 */     long[] arrayOfLong1 = null;
/*      */ 
/*  436 */     if (getSourceWindow() != 0L) {
/*  437 */       return false;
/*      */     }
/*      */ 
/*  440 */     if ((!(XToolkit.windowToXWindow(paramXClientMessageEvent.get_window()) instanceof XWindow)) && (getEmbedderRegistryEntry(paramXClientMessageEvent.get_window()) == null))
/*      */     {
/*  442 */       return false;
/*      */     }
/*      */ 
/*  445 */     if (paramXClientMessageEvent.get_message_type() != XDnDConstants.XA_XdndEnter.getAtom()) {
/*  446 */       return false;
/*      */     }
/*      */ 
/*  449 */     i = (int)((paramXClientMessageEvent.get_data(1) & 0xFF000000) >> 24);
/*      */ 
/*  454 */     if (i < 3) {
/*  455 */       return false;
/*      */     }
/*      */ 
/*  459 */     if (i > 5) {
/*  460 */       return false;
/*      */     }
/*      */ 
/*  463 */     l1 = paramXClientMessageEvent.get_data(0);
/*      */     WindowPropertyGetter localWindowPropertyGetter;
/*  466 */     if (i < 2)
/*      */     {
/*  468 */       j = 1;
/*      */     } else {
/*  470 */       localWindowPropertyGetter = new WindowPropertyGetter(l1, XDnDConstants.XA_XdndActionList, 0L, 65535L, false, 4L);
/*      */       try
/*      */       {
/*  476 */         localWindowPropertyGetter.execute(XErrorHandler.IgnoreBadWindowHandler.getInstance());
/*      */ 
/*  478 */         if ((localWindowPropertyGetter.getActualType() == 4L) && (localWindowPropertyGetter.getActualFormat() == 32))
/*      */         {
/*  480 */           long l3 = localWindowPropertyGetter.getData();
/*      */ 
/*  482 */           for (int i1 = 0; i1 < localWindowPropertyGetter.getNumberOfItems(); i1++) {
/*  483 */             j |= XDnDConstants.getJavaActionForXDnDAction(Native.getLong(l3, i1));
/*      */           }
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*  492 */           j = 1;
/*  493 */           bool = true;
/*      */         }
/*      */       } finally {
/*  496 */         localWindowPropertyGetter.dispose();
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  501 */     if ((paramXClientMessageEvent.get_data(1) & 1L) != 0L) {
/*  502 */       localWindowPropertyGetter = new WindowPropertyGetter(l1, XDnDConstants.XA_XdndTypeList, 0L, 65535L, false, 4L);
/*      */       try
/*      */       {
/*  508 */         localWindowPropertyGetter.execute(XErrorHandler.IgnoreBadWindowHandler.getInstance());
/*      */ 
/*  510 */         if ((localWindowPropertyGetter.getActualType() == 4L) && (localWindowPropertyGetter.getActualFormat() == 32))
/*      */         {
/*  512 */           arrayOfLong1 = Native.toLongs(localWindowPropertyGetter.getData(), localWindowPropertyGetter.getNumberOfItems());
/*      */         }
/*      */         else
/*  515 */           arrayOfLong1 = new long[0];
/*      */       }
/*      */       finally {
/*  518 */         localWindowPropertyGetter.dispose();
/*      */       }
/*      */     } else {
/*  521 */       int k = 0;
/*  522 */       long[] arrayOfLong2 = new long[3];
/*      */ 
/*  524 */       for (int n = 0; n < 3; n++)
/*      */       {
/*      */         long l4;
/*  526 */         if ((l4 = paramXClientMessageEvent.get_data(2 + n)) != 0L) {
/*  527 */           arrayOfLong2[(k++)] = l4;
/*      */         }
/*      */       }
/*      */ 
/*  531 */       arrayOfLong1 = new long[k];
/*      */ 
/*  533 */       System.arraycopy(arrayOfLong2, 0, arrayOfLong1, 0, k);
/*      */     }
/*      */ 
/*  536 */     assert (XToolkit.isAWTLockHeldByCurrentThread());
/*      */ 
/*  542 */     XWindowAttributes localXWindowAttributes = new XWindowAttributes();
/*      */     try {
/*  544 */       XToolkit.WITH_XERROR_HANDLER(XErrorHandler.IgnoreBadWindowHandler.getInstance());
/*  545 */       int m = XlibWrapper.XGetWindowAttributes(XToolkit.getDisplay(), l1, localXWindowAttributes.pData);
/*      */ 
/*  548 */       XToolkit.RESTORE_XERROR_HANDLER();
/*      */ 
/*  550 */       if ((m == 0) || ((XToolkit.saved_error != null) && (XToolkit.saved_error.get_error_code() != 0)))
/*      */       {
/*  553 */         throw new XException("XGetWindowAttributes failed");
/*      */       }
/*      */ 
/*  556 */       l2 = localXWindowAttributes.get_your_event_mask();
/*      */     } finally {
/*  558 */       localXWindowAttributes.dispose();
/*      */     }
/*      */ 
/*  561 */     XToolkit.WITH_XERROR_HANDLER(XErrorHandler.IgnoreBadWindowHandler.getInstance());
/*  562 */     XlibWrapper.XSelectInput(XToolkit.getDisplay(), l1, l2 | 0x20000);
/*      */ 
/*  566 */     XToolkit.RESTORE_XERROR_HANDLER();
/*      */ 
/*  568 */     if ((XToolkit.saved_error != null) && (XToolkit.saved_error.get_error_code() != 0))
/*      */     {
/*  570 */       throw new XException("XSelectInput failed");
/*      */     }
/*      */ 
/*  573 */     this.sourceWindow = l1;
/*  574 */     this.sourceWindowMask = l2;
/*  575 */     this.sourceProtocolVersion = i;
/*  576 */     this.sourceActions = j;
/*  577 */     this.sourceFormats = arrayOfLong1;
/*  578 */     this.trackSourceActions = bool;
/*      */ 
/*  580 */     return true;
/*      */   }
/*      */ 
/*      */   private boolean processXdndPosition(XClientMessageEvent paramXClientMessageEvent) {
/*  584 */     long l1 = 0L;
/*  585 */     long l2 = 0L;
/*  586 */     int i = 0;
/*  587 */     int j = 0;
/*  588 */     int k = 0;
/*      */ 
/*  591 */     if (this.sourceWindow != paramXClientMessageEvent.get_data(0)) {
/*  592 */       return false;
/*      */     }
/*      */ 
/*  595 */     XWindow localXWindow = null;
/*      */ 
/*  597 */     XBaseWindow localXBaseWindow1 = XToolkit.windowToXWindow(paramXClientMessageEvent.get_window());
/*  598 */     if ((localXBaseWindow1 instanceof XWindow)) {
/*  599 */       localXWindow = (XWindow)localXBaseWindow1;
/*      */     }
/*      */ 
/*  603 */     j = (int)(paramXClientMessageEvent.get_data(2) >> 16);
/*  604 */     k = (int)(paramXClientMessageEvent.get_data(2) & 0xFFFF);
/*      */ 
/*  606 */     if (localXWindow == null) {
/*  607 */       long l3 = XDropTargetRegistry.getRegistry().getEmbeddedDropSite(paramXClientMessageEvent.get_window(), j, k);
/*      */ 
/*  611 */       if (l3 != 0L) {
/*  612 */         XBaseWindow localXBaseWindow2 = XToolkit.windowToXWindow(l3);
/*  613 */         if ((localXBaseWindow2 instanceof XWindow)) {
/*  614 */           localXWindow = (XWindow)localXBaseWindow2;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  619 */     if (localXWindow != null)
/*      */     {
/*  622 */       Point localPoint = localXWindow.toLocal(j, k);
/*  623 */       j = localPoint.x;
/*  624 */       k = localPoint.y;
/*      */     }
/*      */ 
/*  628 */     if (this.sourceProtocolVersion > 0) {
/*  629 */       l1 = paramXClientMessageEvent.get_data(3);
/*      */     }
/*      */ 
/*  633 */     if (this.sourceProtocolVersion > 1) {
/*  634 */       l2 = paramXClientMessageEvent.get_data(4);
/*      */     }
/*      */     else {
/*  637 */       l2 = XDnDConstants.XA_XdndActionCopy.getAtom();
/*      */     }
/*      */ 
/*  640 */     i = XDnDConstants.getJavaActionForXDnDAction(l2);
/*      */ 
/*  642 */     if (this.trackSourceActions) {
/*  643 */       this.sourceActions |= i;
/*      */     }
/*      */ 
/*  646 */     if (localXWindow == null) {
/*  647 */       if (this.targetXWindow != null) {
/*  648 */         notifyProtocolListener(this.targetXWindow, j, k, 0, paramXClientMessageEvent, 505);
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*  653 */       int m = 0;
/*      */ 
/*  655 */       if (this.targetXWindow == null)
/*  656 */         m = 504;
/*      */       else {
/*  658 */         m = 506;
/*      */       }
/*      */ 
/*  661 */       notifyProtocolListener(localXWindow, j, k, i, paramXClientMessageEvent, m);
/*      */     }
/*      */ 
/*  665 */     this.userAction = i;
/*  666 */     this.sourceX = j;
/*  667 */     this.sourceY = k;
/*  668 */     this.targetXWindow = localXWindow;
/*      */ 
/*  670 */     return true;
/*      */   }
/*      */ 
/*      */   private boolean processXdndLeave(XClientMessageEvent paramXClientMessageEvent)
/*      */   {
/*  675 */     if (this.sourceWindow != paramXClientMessageEvent.get_data(0)) {
/*  676 */       return false;
/*      */     }
/*      */ 
/*  679 */     cleanup();
/*      */ 
/*  681 */     return true;
/*      */   }
/*      */ 
/*      */   private boolean processXdndDrop(XClientMessageEvent paramXClientMessageEvent)
/*      */   {
/*  686 */     if (this.sourceWindow != paramXClientMessageEvent.get_data(0)) {
/*  687 */       return false;
/*      */     }
/*      */ 
/*  690 */     if (this.targetXWindow != null) {
/*  691 */       notifyProtocolListener(this.targetXWindow, this.sourceX, this.sourceY, this.userAction, paramXClientMessageEvent, 502);
/*      */     }
/*      */ 
/*  695 */     return true;
/*      */   }
/*      */ 
/*      */   public int getMessageType(XClientMessageEvent paramXClientMessageEvent) {
/*  699 */     long l = paramXClientMessageEvent.get_message_type();
/*      */ 
/*  701 */     if (l == XDnDConstants.XA_XdndEnter.getAtom())
/*  702 */       return 1;
/*  703 */     if (l == XDnDConstants.XA_XdndPosition.getAtom())
/*  704 */       return 2;
/*  705 */     if (l == XDnDConstants.XA_XdndLeave.getAtom())
/*  706 */       return 3;
/*  707 */     if (l == XDnDConstants.XA_XdndDrop.getAtom()) {
/*  708 */       return 4;
/*      */     }
/*  710 */     return 0;
/*      */   }
/*      */ 
/*      */   protected boolean processClientMessageImpl(XClientMessageEvent paramXClientMessageEvent)
/*      */   {
/*  715 */     long l = paramXClientMessageEvent.get_message_type();
/*      */ 
/*  717 */     if (l == XDnDConstants.XA_XdndEnter.getAtom())
/*  718 */       return processXdndEnter(paramXClientMessageEvent);
/*  719 */     if (l == XDnDConstants.XA_XdndPosition.getAtom())
/*  720 */       return processXdndPosition(paramXClientMessageEvent);
/*  721 */     if (l == XDnDConstants.XA_XdndLeave.getAtom())
/*  722 */       return processXdndLeave(paramXClientMessageEvent);
/*  723 */     if (l == XDnDConstants.XA_XdndDrop.getAtom()) {
/*  724 */       return processXdndDrop(paramXClientMessageEvent);
/*      */     }
/*  726 */     return false;
/*      */   }
/*      */ 
/*      */   protected void sendEnterMessageToToplevel(long paramLong, XClientMessageEvent paramXClientMessageEvent)
/*      */   {
/*  733 */     long l1 = this.sourceProtocolVersion << 24;
/*  734 */     if ((this.sourceFormats != null) && (this.sourceFormats.length > 3)) {
/*  735 */       l1 |= 1L;
/*      */     }
/*  737 */     long l2 = this.sourceFormats.length > 0 ? this.sourceFormats[0] : 0L;
/*  738 */     long l3 = this.sourceFormats.length > 1 ? this.sourceFormats[1] : 0L;
/*  739 */     long l4 = this.sourceFormats.length > 2 ? this.sourceFormats[2] : 0L;
/*  740 */     sendEnterMessageToToplevelImpl(paramLong, paramXClientMessageEvent.get_data(0), l1, l2, l3, l4);
/*      */   }
/*      */ 
/*      */   private void sendEnterMessageToToplevelImpl(long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6)
/*      */   {
/*  749 */     XClientMessageEvent localXClientMessageEvent = new XClientMessageEvent();
/*      */     try {
/*  751 */       localXClientMessageEvent.set_type(33);
/*  752 */       localXClientMessageEvent.set_window(paramLong1);
/*  753 */       localXClientMessageEvent.set_format(32);
/*  754 */       localXClientMessageEvent.set_message_type(XDnDConstants.XA_XdndEnter.getAtom());
/*      */ 
/*  756 */       localXClientMessageEvent.set_data(0, paramLong2);
/*  757 */       localXClientMessageEvent.set_data(1, paramLong3);
/*  758 */       localXClientMessageEvent.set_data(2, paramLong4);
/*  759 */       localXClientMessageEvent.set_data(3, paramLong5);
/*  760 */       localXClientMessageEvent.set_data(4, paramLong6);
/*      */ 
/*  762 */       forwardClientMessageToToplevel(paramLong1, localXClientMessageEvent);
/*      */     } finally {
/*  764 */       localXClientMessageEvent.dispose();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void sendLeaveMessageToToplevel(long paramLong, XClientMessageEvent paramXClientMessageEvent)
/*      */   {
/*  770 */     sendLeaveMessageToToplevelImpl(paramLong, paramXClientMessageEvent.get_data(0));
/*      */   }
/*      */ 
/*      */   protected void sendLeaveMessageToToplevelImpl(long paramLong1, long paramLong2)
/*      */   {
/*  775 */     XClientMessageEvent localXClientMessageEvent = new XClientMessageEvent();
/*      */     try {
/*  777 */       localXClientMessageEvent.set_type(33);
/*  778 */       localXClientMessageEvent.set_window(paramLong1);
/*  779 */       localXClientMessageEvent.set_format(32);
/*  780 */       localXClientMessageEvent.set_message_type(XDnDConstants.XA_XdndLeave.getAtom());
/*      */ 
/*  782 */       localXClientMessageEvent.set_data(0, paramLong2);
/*      */ 
/*  784 */       localXClientMessageEvent.set_data(1, 0L);
/*      */ 
/*  786 */       forwardClientMessageToToplevel(paramLong1, localXClientMessageEvent);
/*      */     } finally {
/*  788 */       localXClientMessageEvent.dispose();
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean sendResponse(long paramLong, int paramInt1, int paramInt2) {
/*  793 */     XClientMessageEvent localXClientMessageEvent1 = new XClientMessageEvent(paramLong);
/*      */ 
/*  795 */     if (localXClientMessageEvent1.get_message_type() != XDnDConstants.XA_XdndPosition.getAtom())
/*      */     {
/*  798 */       return false;
/*      */     }
/*      */ 
/*  801 */     if (paramInt1 == 505) {
/*  802 */       paramInt2 = 0;
/*      */     }
/*      */ 
/*  805 */     XClientMessageEvent localXClientMessageEvent2 = new XClientMessageEvent();
/*      */     try {
/*  807 */       localXClientMessageEvent2.set_type(33);
/*  808 */       localXClientMessageEvent2.set_window(localXClientMessageEvent1.get_data(0));
/*  809 */       localXClientMessageEvent2.set_format(32);
/*  810 */       localXClientMessageEvent2.set_message_type(XDnDConstants.XA_XdndStatus.getAtom());
/*      */ 
/*  812 */       localXClientMessageEvent2.set_data(0, localXClientMessageEvent1.get_window());
/*      */ 
/*  814 */       long l = 0L;
/*  815 */       if (paramInt2 != 0) {
/*  816 */         l |= 1L;
/*      */       }
/*  818 */       localXClientMessageEvent2.set_data(1, l);
/*      */ 
/*  820 */       localXClientMessageEvent2.set_data(2, 0L);
/*  821 */       localXClientMessageEvent2.set_data(3, 0L);
/*      */ 
/*  823 */       localXClientMessageEvent2.set_data(4, XDnDConstants.getXDnDActionForJavaAction(paramInt2));
/*      */ 
/*  825 */       XToolkit.awtLock();
/*      */       try {
/*  827 */         XlibWrapper.XSendEvent(XToolkit.getDisplay(), localXClientMessageEvent1.get_data(0), false, 0L, localXClientMessageEvent2.pData);
/*      */       }
/*      */       finally
/*      */       {
/*  832 */         XToolkit.awtUnlock();
/*      */       }
/*      */     } finally {
/*  835 */       localXClientMessageEvent2.dispose();
/*      */     }
/*      */ 
/*  838 */     return true;
/*      */   }
/*      */ 
/*      */   public Object getData(long paramLong1, long paramLong2) throws IllegalArgumentException, IOException
/*      */   {
/*  843 */     XClientMessageEvent localXClientMessageEvent = new XClientMessageEvent(paramLong1);
/*  844 */     long l1 = localXClientMessageEvent.get_message_type();
/*  845 */     long l2 = 0L;
/*      */ 
/*  849 */     if (l1 == XDnDConstants.XA_XdndPosition.getAtom())
/*      */     {
/*  851 */       l2 = localXClientMessageEvent.get_data(3) & 0xFFFFFFFF;
/*  852 */     } else if (l1 == XDnDConstants.XA_XdndDrop.getAtom())
/*      */     {
/*  854 */       l2 = localXClientMessageEvent.get_data(2) & 0xFFFFFFFF;
/*      */     }
/*  856 */     else throw new IllegalArgumentException();
/*      */ 
/*  859 */     return XDnDConstants.XDnDSelection.getData(paramLong2, l2);
/*      */   }
/*      */ 
/*      */   public boolean sendDropDone(long paramLong, boolean paramBoolean, int paramInt) {
/*  863 */     XClientMessageEvent localXClientMessageEvent1 = new XClientMessageEvent(paramLong);
/*      */ 
/*  865 */     if (localXClientMessageEvent1.get_message_type() != XDnDConstants.XA_XdndDrop.getAtom())
/*      */     {
/*  867 */       return false;
/*      */     }
/*      */ 
/*  874 */     if ((paramInt == 2) && (paramBoolean))
/*      */     {
/*  876 */       long l1 = localXClientMessageEvent1.get_data(2);
/*  877 */       long l2 = XDnDConstants.XDnDSelection.getSelectionAtom().getAtom();
/*      */ 
/*  880 */       XToolkit.awtLock();
/*      */       try {
/*  882 */         XlibWrapper.XConvertSelection(XToolkit.getDisplay(), l2, XAtom.get("DELETE").getAtom(), XAtom.get("XAWT_SELECTION").getAtom(), XWindow.getXAWTRootWindow().getWindow(), l1);
/*      */       }
/*      */       finally
/*      */       {
/*  889 */         XToolkit.awtUnlock();
/*      */       }
/*      */     }
/*      */ 
/*  893 */     XClientMessageEvent localXClientMessageEvent2 = new XClientMessageEvent();
/*      */     try {
/*  895 */       localXClientMessageEvent2.set_type(33);
/*  896 */       localXClientMessageEvent2.set_window(localXClientMessageEvent1.get_data(0));
/*  897 */       localXClientMessageEvent2.set_format(32);
/*  898 */       localXClientMessageEvent2.set_message_type(XDnDConstants.XA_XdndFinished.getAtom());
/*  899 */       localXClientMessageEvent2.set_data(0, localXClientMessageEvent1.get_window());
/*  900 */       localXClientMessageEvent2.set_data(1, 0L);
/*      */ 
/*  902 */       localXClientMessageEvent2.set_data(2, 0L);
/*  903 */       if (this.sourceProtocolVersion >= 5) {
/*  904 */         if (paramBoolean) {
/*  905 */           localXClientMessageEvent2.set_data(1, 1L);
/*      */         }
/*      */ 
/*  908 */         localXClientMessageEvent2.set_data(2, XDnDConstants.getXDnDActionForJavaAction(paramInt));
/*      */       }
/*  910 */       localXClientMessageEvent2.set_data(3, 0L);
/*  911 */       localXClientMessageEvent2.set_data(4, 0L);
/*      */ 
/*  913 */       XToolkit.awtLock();
/*      */       try {
/*  915 */         XlibWrapper.XSendEvent(XToolkit.getDisplay(), localXClientMessageEvent1.get_data(0), false, 0L, localXClientMessageEvent2.pData);
/*      */       }
/*      */       finally
/*      */       {
/*  920 */         XToolkit.awtUnlock();
/*      */       }
/*      */     } finally {
/*  923 */       localXClientMessageEvent2.dispose();
/*      */     }
/*      */ 
/*  930 */     XToolkit.awtLock();
/*      */     try {
/*  932 */       XlibWrapper.XFlush(XToolkit.getDisplay());
/*      */     } finally {
/*  934 */       XToolkit.awtUnlock();
/*      */     }
/*      */ 
/*  938 */     this.targetXWindow = null;
/*      */ 
/*  942 */     cleanup();
/*  943 */     return true;
/*      */   }
/*      */ 
/*      */   public final long getSourceWindow() {
/*  947 */     return this.sourceWindow;
/*      */   }
/*      */ 
/*      */   public void cleanup()
/*      */   {
/*  955 */     XDropTargetEventProcessor.reset();
/*      */ 
/*  957 */     if (this.targetXWindow != null) {
/*  958 */       notifyProtocolListener(this.targetXWindow, 0, 0, 0, null, 505);
/*      */     }
/*      */ 
/*  963 */     if (this.sourceWindow != 0L) {
/*  964 */       XToolkit.awtLock();
/*      */       try {
/*  966 */         XToolkit.WITH_XERROR_HANDLER(XErrorHandler.IgnoreBadWindowHandler.getInstance());
/*  967 */         XlibWrapper.XSelectInput(XToolkit.getDisplay(), this.sourceWindow, this.sourceWindowMask);
/*      */ 
/*  969 */         XToolkit.RESTORE_XERROR_HANDLER();
/*      */       } finally {
/*  971 */         XToolkit.awtUnlock();
/*      */       }
/*      */     }
/*      */ 
/*  975 */     this.sourceWindow = 0L;
/*  976 */     this.sourceWindowMask = 0L;
/*  977 */     this.sourceProtocolVersion = 0;
/*  978 */     this.sourceActions = 0;
/*  979 */     this.sourceFormats = null;
/*  980 */     this.trackSourceActions = false;
/*  981 */     this.userAction = 0;
/*  982 */     this.sourceX = 0;
/*  983 */     this.sourceY = 0;
/*  984 */     this.targetXWindow = null;
/*      */   }
/*      */ 
/*      */   public boolean isDragOverComponent() {
/*  988 */     return this.targetXWindow != null;
/*      */   }
/*      */ 
/*      */   public void adjustEventForForwarding(XClientMessageEvent paramXClientMessageEvent, XDropTargetProtocol.EmbedderRegistryEntry paramEmbedderRegistryEntry)
/*      */   {
/*  994 */     int i = paramEmbedderRegistryEntry.getVersion();
/*  995 */     if (paramXClientMessageEvent.get_message_type() == XDnDConstants.XA_XdndEnter.getAtom()) {
/*  996 */       int j = this.sourceProtocolVersion < i ? this.sourceProtocolVersion : i;
/*      */ 
/*  998 */       long l = j << 24;
/*  999 */       if ((this.sourceFormats != null) && (this.sourceFormats.length > 3)) {
/* 1000 */         l |= 1L;
/*      */       }
/* 1002 */       if (logger.isLoggable(300)) {
/* 1003 */         logger.finest("          entryVersion=" + i + " sourceProtocolVersion=" + this.sourceProtocolVersion + " sourceFormats.length=" + (this.sourceFormats != null ? this.sourceFormats.length : 0));
/*      */       }
/*      */ 
/* 1010 */       paramXClientMessageEvent.set_data(1, l);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void notifyProtocolListener(XWindow paramXWindow, int paramInt1, int paramInt2, int paramInt3, XClientMessageEvent paramXClientMessageEvent, int paramInt4)
/*      */   {
/* 1018 */     long l1 = 0L;
/*      */ 
/* 1023 */     if (paramXClientMessageEvent != null) {
/* 1024 */       new XClientMessageEvent(l1); int i = XClientMessageEvent.getSize();
/*      */ 
/* 1026 */       l1 = unsafe.allocateMemory(i + 4 * Native.getLongSize());
/*      */ 
/* 1028 */       unsafe.copyMemory(paramXClientMessageEvent.pData, l1, i);
/*      */ 
/* 1030 */       long l2 = this.sourceProtocolVersion << 24;
/* 1031 */       if ((this.sourceFormats != null) && (this.sourceFormats.length > 3)) {
/* 1032 */         l2 |= 1L;
/*      */       }
/*      */ 
/* 1035 */       Native.putLong(l1 + i, l2);
/* 1036 */       Native.putLong(l1 + i + Native.getLongSize(), this.sourceFormats.length > 0 ? this.sourceFormats[0] : 0L);
/*      */ 
/* 1038 */       Native.putLong(l1 + i + 2 * Native.getLongSize(), this.sourceFormats.length > 1 ? this.sourceFormats[1] : 0L);
/*      */ 
/* 1040 */       Native.putLong(l1 + i + 3 * Native.getLongSize(), this.sourceFormats.length > 2 ? this.sourceFormats[2] : 0L);
/*      */     }
/*      */ 
/* 1044 */     getProtocolListener().handleDropTargetNotification(paramXWindow, paramInt1, paramInt2, paramInt3, this.sourceActions, this.sourceFormats, l1, paramInt4);
/*      */   }
/*      */ 
/*      */   public boolean forwardEventToEmbedded(long paramLong1, long paramLong2, int paramInt)
/*      */   {
/* 1061 */     if (logger.isLoggable(300)) {
/* 1062 */       logger.finest("        ctxt=" + paramLong2 + " type=" + (paramLong2 != 0L ? getMessageType(new XClientMessageEvent(paramLong2)) : 0) + " prevCtxt=" + this.prevCtxt + " prevType=" + (this.prevCtxt != 0L ? getMessageType(new XClientMessageEvent(this.prevCtxt)) : 0));
/*      */     }
/*      */ 
/* 1071 */     if (((paramLong2 == 0L) || (getMessageType(new XClientMessageEvent(paramLong2)) == 0)) && ((this.prevCtxt == 0L) || (getMessageType(new XClientMessageEvent(this.prevCtxt)) == 0)))
/*      */     {
/* 1075 */       return false;
/*      */     }
/*      */ 
/* 1079 */     int i = XClientMessageEvent.getSize();
/*      */     XClientMessageEvent localXClientMessageEvent1;
/* 1081 */     if (paramLong2 != 0L) {
/* 1082 */       localXClientMessageEvent1 = new XClientMessageEvent(paramLong2);
/* 1083 */       if (!this.overXEmbedClient) {
/* 1084 */         long l1 = Native.getLong(paramLong2 + i);
/* 1085 */         long l2 = Native.getLong(paramLong2 + i + Native.getLongSize());
/* 1086 */         long l3 = Native.getLong(paramLong2 + i + 2 * Native.getLongSize());
/* 1087 */         long l4 = Native.getLong(paramLong2 + i + 3 * Native.getLongSize());
/*      */ 
/* 1089 */         if (logger.isLoggable(300)) {
/* 1090 */           logger.finest("         1  embedded=" + paramLong1 + " source=" + localXClientMessageEvent1.get_data(0) + " data1=" + l1 + " data2=" + l2 + " data3=" + l3 + " data4=" + l4);
/*      */         }
/*      */ 
/* 1100 */         if ((l1 & 1L) != 0L) {
/* 1101 */           WindowPropertyGetter localWindowPropertyGetter = new WindowPropertyGetter(localXClientMessageEvent1.get_data(0), XDnDConstants.XA_XdndTypeList, 0L, 65535L, false, 4L);
/*      */           try
/*      */           {
/* 1107 */             localWindowPropertyGetter.execute(XErrorHandler.IgnoreBadWindowHandler.getInstance());
/*      */ 
/* 1109 */             if ((localWindowPropertyGetter.getActualType() == 4L) && (localWindowPropertyGetter.getActualFormat() == 32))
/*      */             {
/* 1112 */               XToolkit.awtLock();
/*      */               try {
/* 1114 */                 XToolkit.WITH_XERROR_HANDLER(XErrorHandler.VerifyChangePropertyHandler.getInstance());
/* 1115 */                 XDnDConstants.XA_XdndTypeList.setAtomData(localXClientMessageEvent1.get_window(), 4L, localWindowPropertyGetter.getData(), localWindowPropertyGetter.getNumberOfItems());
/*      */ 
/* 1119 */                 XToolkit.RESTORE_XERROR_HANDLER();
/*      */ 
/* 1121 */                 if ((XToolkit.saved_error != null) && (XToolkit.saved_error.get_error_code() != 0))
/*      */                 {
/* 1123 */                   if (logger.isLoggable(900))
/* 1124 */                     logger.warning("Cannot set XdndTypeList on the proxy window");
/*      */                 }
/*      */               }
/*      */               finally {
/* 1128 */                 XToolkit.awtUnlock();
/*      */               }
/*      */             }
/* 1131 */             else if (logger.isLoggable(900)) {
/* 1132 */               logger.warning("Cannot read XdndTypeList from the source window");
/*      */             }
/*      */           }
/*      */           finally {
/* 1136 */             localWindowPropertyGetter.dispose();
/*      */           }
/*      */         }
/* 1139 */         XDragSourceContextPeer.setProxyModeSourceWindow(localXClientMessageEvent1.get_data(0));
/*      */ 
/* 1141 */         sendEnterMessageToToplevelImpl(paramLong1, localXClientMessageEvent1.get_window(), l1, l2, l3, l4);
/*      */ 
/* 1143 */         this.overXEmbedClient = true;
/*      */       }
/*      */ 
/* 1146 */       if (logger.isLoggable(300)) {
/* 1147 */         logger.finest("         2  embedded=" + paramLong1 + " xclient=" + localXClientMessageEvent1);
/*      */       }
/*      */ 
/* 1155 */       XClientMessageEvent localXClientMessageEvent2 = new XClientMessageEvent();
/* 1156 */       unsafe.copyMemory(localXClientMessageEvent1.pData, localXClientMessageEvent2.pData, XClientMessageEvent.getSize());
/*      */ 
/* 1158 */       localXClientMessageEvent2.set_data(0, localXClientMessageEvent1.get_window());
/*      */ 
/* 1160 */       forwardClientMessageToToplevel(paramLong1, localXClientMessageEvent2);
/*      */     }
/*      */ 
/* 1164 */     if ((paramInt == 505) && 
/* 1165 */       (this.overXEmbedClient)) {
/* 1166 */       if ((paramLong2 != 0L) || (this.prevCtxt != 0L))
/*      */       {
/* 1168 */         localXClientMessageEvent1 = paramLong2 != 0L ? new XClientMessageEvent(paramLong2) : new XClientMessageEvent(this.prevCtxt);
/*      */ 
/* 1171 */         sendLeaveMessageToToplevelImpl(paramLong1, localXClientMessageEvent1.get_window());
/*      */       }
/* 1173 */       this.overXEmbedClient = false;
/*      */ 
/* 1184 */       XDragSourceContextPeer.setProxyModeSourceWindow(0L);
/*      */     }
/*      */ 
/* 1188 */     if (paramInt == 502) {
/* 1189 */       this.overXEmbedClient = false;
/* 1190 */       cleanup();
/*      */     }
/*      */ 
/* 1193 */     if (this.prevCtxt != 0L) {
/* 1194 */       unsafe.freeMemory(this.prevCtxt);
/* 1195 */       this.prevCtxt = 0L;
/*      */     }
/*      */ 
/* 1198 */     if ((paramLong2 != 0L) && (this.overXEmbedClient)) {
/* 1199 */       this.prevCtxt = unsafe.allocateMemory(i + 4 * Native.getLongSize());
/*      */ 
/* 1201 */       unsafe.copyMemory(paramLong2, this.prevCtxt, i + 4 * Native.getLongSize());
/*      */     }
/*      */ 
/* 1204 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean isXEmbedSupported() {
/* 1208 */     return true;
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XDnDDropTargetProtocol
 * JD-Core Version:    0.6.2
 */