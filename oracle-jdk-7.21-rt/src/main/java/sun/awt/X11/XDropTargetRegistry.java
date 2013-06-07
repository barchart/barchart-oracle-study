/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.awt.Point;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import sun.util.logging.PlatformLogger;
/*     */ 
/*     */ final class XDropTargetRegistry
/*     */ {
/*  45 */   private static final PlatformLogger logger = PlatformLogger.getLogger("sun.awt.X11.xembed.xdnd.XDropTargetRegistry");
/*     */   private static final long DELAYED_REGISTRATION_PERIOD = 200L;
/*  50 */   private static final XDropTargetRegistry theInstance = new XDropTargetRegistry();
/*     */ 
/*  53 */   private final HashMap<Long, Runnable> delayedRegistrationMap = new HashMap();
/*     */ 
/* 196 */   private final HashMap<Long, EmbeddedDropSiteEntry> embeddedDropSiteRegistry = new HashMap();
/*     */   private static final boolean XEMBED_PROTOCOLS = true;
/*     */   private static final boolean NON_XEMBED_PROTOCOLS = false;
/*     */ 
/*     */   static XDropTargetRegistry getRegistry()
/*     */   {
/*  59 */     return theInstance;
/*     */   }
/*     */ 
/*     */   private long getToplevelWindow(long paramLong)
/*     */   {
/*  67 */     XBaseWindow localXBaseWindow = XToolkit.windowToXWindow(paramLong);
/*  68 */     if (localXBaseWindow != null) {
/*  69 */       XWindowPeer localXWindowPeer = localXBaseWindow.getToplevelXWindow();
/*  70 */       if ((localXWindowPeer != null) && (!(localXWindowPeer instanceof XEmbeddedFramePeer))) {
/*  71 */         return localXWindowPeer.getWindow();
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*     */     do
/*     */     {
/*  78 */       if (XlibUtil.isTrueToplevelWindow(paramLong)) {
/*  79 */         return paramLong;
/*     */       }
/*     */ 
/*  82 */       paramLong = XlibUtil.getParentWindow(paramLong);
/*     */     }
/*  84 */     while (paramLong != 0L);
/*     */ 
/*  86 */     return paramLong;
/*     */   }
/*     */ 
/*     */   static final long getDnDProxyWindow() {
/*  90 */     return XWindow.getXAWTRootWindow().getWindow();
/*     */   }
/*     */ 
/*     */   private EmbeddedDropSiteEntry registerEmbedderDropSite(long paramLong)
/*     */   {
/* 200 */     assert (XToolkit.isAWTLockHeldByCurrentThread());
/*     */ 
/* 202 */     Iterator localIterator = XDragAndDropProtocols.getDropTargetProtocols();
/*     */ 
/* 205 */     Object localObject1 = new ArrayList();
/*     */ 
/* 207 */     while (localIterator.hasNext()) {
/* 208 */       XDropTargetProtocol localXDropTargetProtocol = (XDropTargetProtocol)localIterator.next();
/*     */ 
/* 210 */       if (localXDropTargetProtocol.isProtocolSupported(paramLong)) {
/* 211 */         ((List)localObject1).add(localXDropTargetProtocol);
/*     */       }
/*     */     }
/*     */ 
/* 215 */     localObject1 = Collections.unmodifiableList((List)localObject1);
/*     */ 
/* 219 */     XlibWrapper.XGrabServer(XToolkit.getDisplay());
/*     */     try {
/* 221 */       long l1 = 0L;
/* 222 */       long l2 = 0L;
/* 223 */       XWindowAttributes localXWindowAttributes = new XWindowAttributes();
/*     */       try {
/* 225 */         XToolkit.WITH_XERROR_HANDLER(XErrorHandler.IgnoreBadWindowHandler.getInstance());
/* 226 */         int i = XlibWrapper.XGetWindowAttributes(XToolkit.getDisplay(), paramLong, localXWindowAttributes.pData);
/*     */ 
/* 228 */         XToolkit.RESTORE_XERROR_HANDLER();
/*     */ 
/* 230 */         if ((i == 0) || ((XToolkit.saved_error != null) && (XToolkit.saved_error.get_error_code() != 0)))
/*     */         {
/* 233 */           throw new XException("XGetWindowAttributes failed");
/*     */         }
/*     */ 
/* 236 */         l2 = localXWindowAttributes.get_your_event_mask();
/* 237 */         l1 = localXWindowAttributes.get_root();
/*     */       } finally {
/* 239 */         localXWindowAttributes.dispose();
/*     */       }
/*     */ 
/* 242 */       if ((l2 & 0x400000) == 0L) {
/* 243 */         XToolkit.WITH_XERROR_HANDLER(XErrorHandler.IgnoreBadWindowHandler.getInstance());
/* 244 */         XlibWrapper.XSelectInput(XToolkit.getDisplay(), paramLong, l2 | 0x400000);
/*     */ 
/* 246 */         XToolkit.RESTORE_XERROR_HANDLER();
/*     */ 
/* 248 */         if ((XToolkit.saved_error != null) && (XToolkit.saved_error.get_error_code() != 0))
/*     */         {
/* 250 */           throw new XException("XSelectInput failed");
/*     */         }
/*     */       }
/*     */ 
/* 254 */       return new EmbeddedDropSiteEntry(l1, l2, (List)localObject1);
/*     */     } finally {
/* 256 */       XlibWrapper.XUngrabServer(XToolkit.getDisplay());
/*     */     }
/*     */   }
/*     */ 
/*     */   private void registerProtocols(long paramLong, boolean paramBoolean, List<XDropTargetProtocol> paramList)
/*     */   {
/* 265 */     Iterator localIterator = null;
/*     */ 
/* 280 */     if (!paramList.isEmpty())
/* 281 */       localIterator = paramList.iterator();
/*     */     else {
/* 283 */       localIterator = XDragAndDropProtocols.getDropTargetProtocols();
/*     */     }
/*     */ 
/* 289 */     XlibWrapper.XGrabServer(XToolkit.getDisplay());
/*     */     try {
/* 291 */       while (localIterator.hasNext()) {
/* 292 */         XDropTargetProtocol localXDropTargetProtocol = (XDropTargetProtocol)localIterator.next();
/*     */ 
/* 294 */         if ((paramBoolean == true) == localXDropTargetProtocol.isXEmbedSupported())
/*     */         {
/* 296 */           localXDropTargetProtocol.registerEmbedderDropSite(paramLong);
/*     */         }
/*     */       }
/*     */     } finally {
/* 300 */       XlibWrapper.XUngrabServer(XToolkit.getDisplay());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void updateEmbedderDropSite(long paramLong) {
/* 305 */     XBaseWindow localXBaseWindow = XToolkit.windowToXWindow(paramLong);
/*     */ 
/* 307 */     if (localXBaseWindow != null) {
/* 308 */       return;
/*     */     }
/*     */ 
/* 311 */     assert (XToolkit.isAWTLockHeldByCurrentThread());
/*     */ 
/* 313 */     Iterator localIterator = XDragAndDropProtocols.getDropTargetProtocols();
/*     */ 
/* 316 */     Object localObject1 = new ArrayList();
/*     */ 
/* 318 */     while (localIterator.hasNext()) {
/* 319 */       localObject2 = (XDropTargetProtocol)localIterator.next();
/*     */ 
/* 321 */       if (((XDropTargetProtocol)localObject2).isProtocolSupported(paramLong)) {
/* 322 */         ((List)localObject1).add(localObject2);
/*     */       }
/*     */     }
/*     */ 
/* 326 */     localObject1 = Collections.unmodifiableList((List)localObject1);
/*     */ 
/* 328 */     Object localObject2 = Long.valueOf(paramLong);
/* 329 */     int i = 0;
/* 330 */     synchronized (this) {
/* 331 */       EmbeddedDropSiteEntry localEmbeddedDropSiteEntry = (EmbeddedDropSiteEntry)this.embeddedDropSiteRegistry.get(localObject2);
/*     */ 
/* 333 */       if (localEmbeddedDropSiteEntry == null) {
/* 334 */         return;
/*     */       }
/* 336 */       localEmbeddedDropSiteEntry.setSupportedProtocols((List)localObject1);
/* 337 */       i = !localEmbeddedDropSiteEntry.hasNonXEmbedClientSites() ? 1 : 0;
/*     */     }
/*     */ 
/* 353 */     if (!((List)localObject1).isEmpty())
/* 354 */       localIterator = ((List)localObject1).iterator();
/*     */     else {
/* 356 */       localIterator = XDragAndDropProtocols.getDropTargetProtocols();
/*     */     }
/*     */ 
/* 362 */     XlibWrapper.XGrabServer(XToolkit.getDisplay());
/*     */     try {
/* 364 */       while (localIterator.hasNext()) {
/* 365 */         ??? = (XDropTargetProtocol)localIterator.next();
/*     */ 
/* 367 */         if ((i == 0) || (!((XDropTargetProtocol)???).isXEmbedSupported()))
/* 368 */           ((XDropTargetProtocol)???).registerEmbedderDropSite(paramLong);
/*     */       }
/*     */     }
/*     */     finally {
/* 372 */       XlibWrapper.XUngrabServer(XToolkit.getDisplay());
/*     */     }
/*     */   }
/*     */ 
/*     */   private void unregisterEmbedderDropSite(long paramLong, EmbeddedDropSiteEntry paramEmbeddedDropSiteEntry)
/*     */   {
/* 378 */     assert (XToolkit.isAWTLockHeldByCurrentThread());
/*     */ 
/* 380 */     Iterator localIterator = XDragAndDropProtocols.getDropTargetProtocols();
/*     */ 
/* 385 */     XlibWrapper.XGrabServer(XToolkit.getDisplay());
/*     */     try {
/* 387 */       while (localIterator.hasNext()) {
/* 388 */         XDropTargetProtocol localXDropTargetProtocol = (XDropTargetProtocol)localIterator.next();
/*     */ 
/* 390 */         localXDropTargetProtocol.unregisterEmbedderDropSite(paramLong);
/*     */       }
/*     */ 
/* 393 */       long l = paramEmbeddedDropSiteEntry.getEventMask();
/*     */ 
/* 396 */       if ((l & 0x400000) == 0L) {
/* 397 */         XToolkit.WITH_XERROR_HANDLER(XErrorHandler.IgnoreBadWindowHandler.getInstance());
/* 398 */         XlibWrapper.XSelectInput(XToolkit.getDisplay(), paramLong, l);
/*     */ 
/* 400 */         XToolkit.RESTORE_XERROR_HANDLER();
/*     */ 
/* 402 */         if ((XToolkit.saved_error != null) && (XToolkit.saved_error.get_error_code() != 0))
/*     */         {
/* 404 */           throw new XException("XSelectInput failed");
/*     */         }
/*     */       }
/*     */     } finally {
/* 408 */       XlibWrapper.XUngrabServer(XToolkit.getDisplay());
/*     */     }
/*     */   }
/*     */ 
/*     */   private void registerEmbeddedDropSite(long paramLong1, long paramLong2) {
/* 413 */     XBaseWindow localXBaseWindow = XToolkit.windowToXWindow(paramLong2);
/* 414 */     boolean bool = ((localXBaseWindow instanceof XEmbeddedFramePeer)) && (((XEmbeddedFramePeer)localXBaseWindow).isXEmbedActive());
/*     */ 
/* 418 */     XEmbedCanvasPeer localXEmbedCanvasPeer = null;
/*     */ 
/* 420 */     Object localObject1 = XToolkit.windowToXWindow(paramLong1);
/* 421 */     if (localObject1 != null) {
/* 422 */       if ((localObject1 instanceof XEmbedCanvasPeer))
/* 423 */         localXEmbedCanvasPeer = (XEmbedCanvasPeer)localObject1;
/*     */       else {
/* 425 */         throw new UnsupportedOperationException();
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 430 */     localObject1 = Long.valueOf(paramLong1);
/* 431 */     EmbeddedDropSiteEntry localEmbeddedDropSiteEntry = null;
/* 432 */     synchronized (this) {
/* 433 */       localEmbeddedDropSiteEntry = (EmbeddedDropSiteEntry)this.embeddedDropSiteRegistry.get(localObject1);
/*     */ 
/* 435 */       if (localEmbeddedDropSiteEntry == null) {
/* 436 */         if (localXEmbedCanvasPeer != null)
/*     */         {
/* 439 */           localXEmbedCanvasPeer.setXEmbedDropTarget();
/*     */ 
/* 441 */           localEmbeddedDropSiteEntry = new EmbeddedDropSiteEntry(0L, 0L, Collections.emptyList());
/*     */         }
/*     */         else
/*     */         {
/* 448 */           localEmbeddedDropSiteEntry = registerEmbedderDropSite(paramLong1);
/*     */ 
/* 453 */           registerProtocols(paramLong1, false, localEmbeddedDropSiteEntry.getSupportedProtocols());
/*     */         }
/*     */ 
/* 456 */         this.embeddedDropSiteRegistry.put(localObject1, localEmbeddedDropSiteEntry);
/*     */       }
/*     */     }
/*     */ 
/* 460 */     assert (localEmbeddedDropSiteEntry != null);
/*     */ 
/* 462 */     synchronized (localEmbeddedDropSiteEntry)
/*     */     {
/* 464 */       if (localXEmbedCanvasPeer == null) {
/* 465 */         if (!bool)
/*     */         {
/* 472 */           registerProtocols(paramLong1, true, localEmbeddedDropSiteEntry.getSupportedProtocols());
/*     */         }
/*     */         else {
/* 475 */           Iterator localIterator = XDragAndDropProtocols.getDropTargetProtocols();
/*     */ 
/* 480 */           while (localIterator.hasNext()) {
/* 481 */             XDropTargetProtocol localXDropTargetProtocol = (XDropTargetProtocol)localIterator.next();
/*     */ 
/* 483 */             if (localXDropTargetProtocol.isXEmbedSupported()) {
/* 484 */               localXDropTargetProtocol.registerEmbedderDropSite(paramLong2);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 490 */       localEmbeddedDropSiteEntry.addSite(paramLong2, bool);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void unregisterEmbeddedDropSite(long paramLong1, long paramLong2) {
/* 495 */     Long localLong = Long.valueOf(paramLong1);
/* 496 */     EmbeddedDropSiteEntry localEmbeddedDropSiteEntry = null;
/* 497 */     synchronized (this) {
/* 498 */       localEmbeddedDropSiteEntry = (EmbeddedDropSiteEntry)this.embeddedDropSiteRegistry.get(localLong);
/*     */ 
/* 500 */       if (localEmbeddedDropSiteEntry == null) {
/* 501 */         return;
/*     */       }
/* 503 */       localEmbeddedDropSiteEntry.removeSite(paramLong2);
/* 504 */       if (!localEmbeddedDropSiteEntry.hasSites()) {
/* 505 */         this.embeddedDropSiteRegistry.remove(localLong);
/*     */ 
/* 507 */         XBaseWindow localXBaseWindow = XToolkit.windowToXWindow(paramLong1);
/* 508 */         if (localXBaseWindow != null) {
/* 509 */           if ((localXBaseWindow instanceof XEmbedCanvasPeer)) {
/* 510 */             XEmbedCanvasPeer localXEmbedCanvasPeer = (XEmbedCanvasPeer)localXBaseWindow;
/*     */ 
/* 512 */             localXEmbedCanvasPeer.removeXEmbedDropTarget();
/*     */           } else {
/* 514 */             throw new UnsupportedOperationException();
/*     */           }
/*     */         }
/* 517 */         else unregisterEmbedderDropSite(paramLong1, localEmbeddedDropSiteEntry);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public long getEmbeddedDropSite(long paramLong, int paramInt1, int paramInt2)
/*     */   {
/* 528 */     Long localLong = Long.valueOf(paramLong);
/* 529 */     EmbeddedDropSiteEntry localEmbeddedDropSiteEntry = (EmbeddedDropSiteEntry)this.embeddedDropSiteRegistry.get(localLong);
/*     */ 
/* 531 */     if (localEmbeddedDropSiteEntry == null) {
/* 532 */       return 0L;
/*     */     }
/* 534 */     return localEmbeddedDropSiteEntry.getSite(paramInt1, paramInt2);
/*     */   }
/*     */ 
/*     */   public void registerDropSite(long paramLong)
/*     */   {
/* 541 */     assert (XToolkit.isAWTLockHeldByCurrentThread());
/*     */ 
/* 543 */     if (paramLong == 0L) {
/* 544 */       throw new IllegalArgumentException();
/*     */     }
/*     */ 
/* 547 */     XDropTargetEventProcessor.activate();
/*     */ 
/* 549 */     long l = getToplevelWindow(paramLong);
/*     */ 
/* 559 */     if (l == 0L) {
/* 560 */       addDelayedRegistrationEntry(paramLong);
/* 561 */       return;
/*     */     }
/*     */ 
/* 564 */     if (l == paramLong) {
/* 565 */       Iterator localIterator = XDragAndDropProtocols.getDropTargetProtocols();
/*     */ 
/* 568 */       while (localIterator.hasNext()) {
/* 569 */         XDropTargetProtocol localXDropTargetProtocol = (XDropTargetProtocol)localIterator.next();
/*     */ 
/* 571 */         localXDropTargetProtocol.registerDropTarget(l);
/*     */       }
/*     */     } else {
/* 574 */       registerEmbeddedDropSite(l, paramLong);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void unregisterDropSite(long paramLong)
/*     */   {
/* 582 */     assert (XToolkit.isAWTLockHeldByCurrentThread());
/*     */ 
/* 584 */     if (paramLong == 0L) {
/* 585 */       throw new IllegalArgumentException();
/*     */     }
/*     */ 
/* 588 */     long l = getToplevelWindow(paramLong);
/*     */ 
/* 590 */     if (l == paramLong) {
/* 591 */       Iterator localIterator = XDragAndDropProtocols.getDropTargetProtocols();
/*     */ 
/* 594 */       removeDelayedRegistrationEntry(paramLong);
/*     */ 
/* 596 */       while (localIterator.hasNext()) {
/* 597 */         XDropTargetProtocol localXDropTargetProtocol = (XDropTargetProtocol)localIterator.next();
/* 598 */         localXDropTargetProtocol.unregisterDropTarget(paramLong);
/*     */       }
/*     */     } else {
/* 601 */       unregisterEmbeddedDropSite(l, paramLong);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void registerXEmbedClient(long paramLong1, long paramLong2)
/*     */   {
/* 610 */     XDragSourceProtocol localXDragSourceProtocol = XDragAndDropProtocols.getDragSourceProtocol("XDnD");
/*     */ 
/* 612 */     XDragSourceProtocol.TargetWindowInfo localTargetWindowInfo = localXDragSourceProtocol.getTargetWindowInfo(paramLong2);
/*     */ 
/* 614 */     if ((localTargetWindowInfo != null) && (localTargetWindowInfo.getProtocolVersion() >= 3))
/*     */     {
/* 617 */       if (logger.isLoggable(500)) {
/* 618 */         logger.fine("        XEmbed drop site will be registered for " + Long.toHexString(paramLong2));
/*     */       }
/* 620 */       registerEmbeddedDropSite(paramLong1, paramLong2);
/*     */ 
/* 622 */       Iterator localIterator = XDragAndDropProtocols.getDropTargetProtocols();
/*     */ 
/* 625 */       while (localIterator.hasNext()) {
/* 626 */         XDropTargetProtocol localXDropTargetProtocol = (XDropTargetProtocol)localIterator.next();
/*     */ 
/* 628 */         localXDropTargetProtocol.registerEmbeddedDropSite(paramLong2);
/*     */       }
/*     */ 
/* 631 */       if (logger.isLoggable(500))
/* 632 */         logger.fine("        XEmbed drop site has been registered for " + Long.toHexString(paramLong2));
/*     */     }
/*     */   }
/*     */ 
/*     */   public void unregisterXEmbedClient(long paramLong1, long paramLong2)
/*     */   {
/* 638 */     if (logger.isLoggable(500)) {
/* 639 */       logger.fine("        XEmbed drop site will be unregistered for " + Long.toHexString(paramLong2));
/*     */     }
/* 641 */     Iterator localIterator = XDragAndDropProtocols.getDropTargetProtocols();
/*     */ 
/* 644 */     while (localIterator.hasNext()) {
/* 645 */       XDropTargetProtocol localXDropTargetProtocol = (XDropTargetProtocol)localIterator.next();
/*     */ 
/* 647 */       localXDropTargetProtocol.unregisterEmbeddedDropSite(paramLong2);
/*     */     }
/*     */ 
/* 650 */     unregisterEmbeddedDropSite(paramLong1, paramLong2);
/*     */ 
/* 652 */     if (logger.isLoggable(500))
/* 653 */       logger.fine("        XEmbed drop site has beed unregistered for " + Long.toHexString(paramLong2));
/*     */   }
/*     */ 
/*     */   private void addDelayedRegistrationEntry(final long paramLong)
/*     */   {
/* 660 */     Long localLong = Long.valueOf(paramLong);
/* 661 */     Runnable local1 = new Runnable() {
/*     */       public void run() {
/* 663 */         XDropTargetRegistry.this.removeDelayedRegistrationEntry(paramLong);
/* 664 */         XDropTargetRegistry.this.registerDropSite(paramLong);
/*     */       }
/*     */     };
/* 668 */     XToolkit.awtLock();
/*     */     try {
/* 670 */       removeDelayedRegistrationEntry(paramLong);
/* 671 */       this.delayedRegistrationMap.put(localLong, local1);
/* 672 */       XToolkit.schedule(local1, 200L);
/*     */     } finally {
/* 674 */       XToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void removeDelayedRegistrationEntry(long paramLong) {
/* 679 */     Long localLong = Long.valueOf(paramLong);
/*     */ 
/* 681 */     XToolkit.awtLock();
/*     */     try {
/* 683 */       Runnable localRunnable = (Runnable)this.delayedRegistrationMap.remove(localLong);
/* 684 */       if (localRunnable != null)
/* 685 */         XToolkit.remove(localRunnable);
/*     */     }
/*     */     finally {
/* 688 */       XToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class EmbeddedDropSiteEntry
/*     */   {
/*     */     private final long root;
/*     */     private final long event_mask;
/*     */     private List<XDropTargetProtocol> supportedProtocols;
/*  97 */     private final HashSet<Long> nonXEmbedClientSites = new HashSet();
/*  98 */     private final List<Long> sites = new ArrayList();
/*     */ 
/*     */     public EmbeddedDropSiteEntry(long paramLong1, long paramLong2, List<XDropTargetProtocol> paramList)
/*     */     {
/* 102 */       if (paramList == null) {
/* 103 */         throw new NullPointerException("Null supportedProtocols");
/*     */       }
/* 105 */       this.root = paramLong1;
/* 106 */       this.event_mask = paramLong2;
/* 107 */       this.supportedProtocols = paramList;
/*     */     }
/*     */ 
/*     */     public long getRoot() {
/* 111 */       return this.root;
/*     */     }
/*     */     public long getEventMask() {
/* 114 */       return this.event_mask;
/*     */     }
/*     */     public boolean hasNonXEmbedClientSites() {
/* 117 */       return !this.nonXEmbedClientSites.isEmpty();
/*     */     }
/*     */     public synchronized void addSite(long paramLong, boolean paramBoolean) {
/* 120 */       Long localLong = Long.valueOf(paramLong);
/* 121 */       if (!this.sites.contains(localLong)) {
/* 122 */         this.sites.add(localLong);
/*     */       }
/* 124 */       if (!paramBoolean)
/* 125 */         this.nonXEmbedClientSites.add(localLong);
/*     */     }
/*     */ 
/*     */     public synchronized void removeSite(long paramLong) {
/* 129 */       Long localLong = Long.valueOf(paramLong);
/* 130 */       this.sites.remove(localLong);
/* 131 */       this.nonXEmbedClientSites.remove(localLong);
/*     */     }
/*     */     public void setSupportedProtocols(List<XDropTargetProtocol> paramList) {
/* 134 */       this.supportedProtocols = paramList;
/*     */     }
/*     */     public List<XDropTargetProtocol> getSupportedProtocols() {
/* 137 */       return this.supportedProtocols;
/*     */     }
/*     */     public boolean hasSites() {
/* 140 */       return !this.sites.isEmpty();
/*     */     }
/*     */     public long[] getSites() {
/* 143 */       long[] arrayOfLong = new long[this.sites.size()];
/* 144 */       Iterator localIterator = this.sites.iterator();
/* 145 */       int i = 0;
/* 146 */       while (localIterator.hasNext()) {
/* 147 */         Long localLong = (Long)localIterator.next();
/* 148 */         arrayOfLong[(i++)] = localLong.longValue();
/*     */       }
/* 150 */       return arrayOfLong;
/*     */     }
/*     */     public long getSite(int paramInt1, int paramInt2) {
/* 153 */       assert (XToolkit.isAWTLockHeldByCurrentThread());
/*     */ 
/* 155 */       Iterator localIterator = this.sites.iterator();
/* 156 */       while (localIterator.hasNext()) {
/* 157 */         Long localLong = (Long)localIterator.next();
/* 158 */         long l1 = localLong.longValue();
/*     */ 
/* 160 */         Point localPoint = XBaseWindow.toOtherWindow(getRoot(), l1, paramInt1, paramInt2);
/*     */ 
/* 162 */         if (localPoint != null)
/*     */         {
/* 166 */           int i = localPoint.x;
/* 167 */           int j = localPoint.y;
/* 168 */           if ((i >= 0) && (j >= 0)) {
/* 169 */             XWindowAttributes localXWindowAttributes = new XWindowAttributes();
/*     */             try {
/* 171 */               XToolkit.WITH_XERROR_HANDLER(XErrorHandler.IgnoreBadWindowHandler.getInstance());
/* 172 */               int k = XlibWrapper.XGetWindowAttributes(XToolkit.getDisplay(), l1, localXWindowAttributes.pData);
/*     */ 
/* 174 */               XToolkit.RESTORE_XERROR_HANDLER();
/*     */ 
/* 176 */               if ((k == 0) || ((XToolkit.saved_error != null) && (XToolkit.saved_error.get_error_code() != 0)))
/*     */               {
/* 188 */                 localXWindowAttributes.dispose();
/*     */               }
/* 182 */               else if ((localXWindowAttributes.get_map_state() != 0) && (i < localXWindowAttributes.get_width()) && (j < localXWindowAttributes.get_height()))
/*     */               {
/* 185 */                 return l1;
/*     */               }
/*     */             } finally {
/* 188 */               localXWindowAttributes.dispose();
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 192 */       return 0L;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XDropTargetRegistry
 * JD-Core Version:    0.6.2
 */