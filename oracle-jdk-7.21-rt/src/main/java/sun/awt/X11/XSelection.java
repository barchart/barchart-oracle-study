/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.awt.datatransfer.Transferable;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Map;
/*     */ import sun.awt.AppContext;
/*     */ import sun.awt.SunToolkit;
/*     */ import sun.awt.UNIXToolkit;
/*     */ import sun.awt.datatransfer.DataTransferer;
/*     */ import sun.misc.Unsafe;
/*     */ 
/*     */ public final class XSelection
/*     */ {
/*  48 */   private static final Hashtable<XAtom, XSelection> table = new Hashtable();
/*     */ 
/*  50 */   private static final Object lock = new Object();
/*     */ 
/*  52 */   private static final XAtom selectionPropertyAtom = XAtom.get("XAWT_SELECTION");
/*     */   public static final long MAX_LENGTH = 1000000L;
/*     */   public static final int MAX_PROPERTY_SIZE;
/*     */   private static final XEventDispatcher incrementalTransferHandler;
/*     */   private static WindowPropertyGetter propertyGetter;
/*     */   private final XAtom selectionAtom;
/*  88 */   private Transferable contents = null;
/*     */ 
/*  90 */   private Map formatMap = null;
/*     */ 
/*  92 */   private long[] formats = null;
/*     */ 
/*  94 */   private AppContext appContext = null;
/*     */   private static long lastRequestServerTime;
/*  99 */   private long ownershipTime = 0L;
/*     */   private boolean isOwner;
/* 102 */   private OwnershipListener ownershipListener = null;
/* 103 */   private final Object stateLock = new Object();
/*     */ 
/*     */   static XSelection getSelection(XAtom paramXAtom)
/*     */   {
/* 115 */     return (XSelection)table.get(paramXAtom);
/*     */   }
/*     */ 
/*     */   public XSelection(XAtom paramXAtom)
/*     */   {
/* 126 */     if (paramXAtom == null) {
/* 127 */       throw new NullPointerException("Null atom");
/*     */     }
/* 129 */     this.selectionAtom = paramXAtom;
/* 130 */     table.put(this.selectionAtom, this);
/*     */   }
/*     */ 
/*     */   public XAtom getSelectionAtom() {
/* 134 */     return this.selectionAtom;
/*     */   }
/*     */ 
/*     */   public synchronized boolean setOwner(Transferable paramTransferable, Map paramMap, long[] paramArrayOfLong, long paramLong)
/*     */   {
/* 140 */     long l1 = XWindow.getXAWTRootWindow().getWindow();
/* 141 */     long l2 = this.selectionAtom.getAtom();
/*     */ 
/* 144 */     if (paramLong == 0L) {
/* 145 */       paramLong = XToolkit.getCurrentServerTime();
/*     */     }
/*     */ 
/* 148 */     this.contents = paramTransferable;
/* 149 */     this.formatMap = paramMap;
/* 150 */     this.formats = paramArrayOfLong;
/* 151 */     this.appContext = AppContext.getAppContext();
/* 152 */     this.ownershipTime = paramLong;
/*     */ 
/* 154 */     XToolkit.awtLock();
/*     */     try {
/* 156 */       XlibWrapper.XSetSelectionOwner(XToolkit.getDisplay(), l2, l1, paramLong);
/*     */       boolean bool;
/* 158 */       if (XlibWrapper.XGetSelectionOwner(XToolkit.getDisplay(), l2) != l1)
/*     */       {
/* 161 */         reset();
/* 162 */         return false;
/*     */       }
/* 164 */       setOwnerProp(true);
/* 165 */       return true;
/*     */     } finally {
/* 167 */       XToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void waitForSelectionNotify(WindowPropertyGetter paramWindowPropertyGetter)
/*     */     throws InterruptedException
/*     */   {
/* 175 */     long l = System.currentTimeMillis();
/* 176 */     XToolkit.awtLock();
/*     */     try {
/*     */       do {
/* 179 */         DataTransferer.getInstance().processDataConversionRequests();
/* 180 */         XToolkit.awtLockWait(250L);
/* 181 */         if (propertyGetter != paramWindowPropertyGetter) break;  } while (System.currentTimeMillis() < l + UNIXToolkit.getDatatransferTimeout());
/*     */     } finally {
/* 183 */       XToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   public long[] getTargets(long paramLong)
/*     */   {
/* 192 */     if (XToolkit.isToolkitThread()) {
/* 193 */       throw new Error("UNIMPLEMENTED");
/*     */     }
/*     */ 
/* 196 */     long[] arrayOfLong1 = null;
/*     */ 
/* 198 */     synchronized (lock) {
/* 199 */       WindowPropertyGetter localWindowPropertyGetter = new WindowPropertyGetter(XWindow.getXAWTRootWindow().getWindow(), selectionPropertyAtom, 0L, 1000000L, true, 0L);
/*     */       try
/*     */       {
/* 205 */         XToolkit.awtLock();
/*     */         try {
/* 207 */           propertyGetter = localWindowPropertyGetter;
/* 208 */           lastRequestServerTime = paramLong;
/*     */ 
/* 210 */           XlibWrapper.XConvertSelection(XToolkit.getDisplay(), getSelectionAtom().getAtom(), XDataTransferer.TARGETS_ATOM.getAtom(), selectionPropertyAtom.getAtom(), XWindow.getXAWTRootWindow().getWindow(), paramLong);
/*     */           try
/*     */           {
/* 220 */             waitForSelectionNotify(localWindowPropertyGetter);
/*     */           } catch (InterruptedException localInterruptedException) {
/* 222 */             long[] arrayOfLong2 = new long[0];
/*     */ 
/* 224 */             propertyGetter = null;
/*     */ 
/* 227 */             XToolkit.awtUnlock();
/*     */ 
/* 231 */             localWindowPropertyGetter.dispose(); return arrayOfLong2;
/*     */           }
/*     */           finally
/*     */           {
/* 224 */             propertyGetter = null;
/*     */           }
/*     */         } finally {
/* 227 */           XToolkit.awtUnlock();
/*     */         }
/* 229 */         arrayOfLong1 = getFormats(localWindowPropertyGetter);
/*     */       } finally {
/* 231 */         localWindowPropertyGetter.dispose();
/*     */       }
/*     */     }
/* 234 */     return arrayOfLong1;
/*     */   }
/*     */ 
/*     */   static long[] getFormats(WindowPropertyGetter paramWindowPropertyGetter) {
/* 238 */     long[] arrayOfLong = null;
/*     */ 
/* 240 */     if ((paramWindowPropertyGetter.isExecuted()) && (!paramWindowPropertyGetter.isDisposed()) && ((paramWindowPropertyGetter.getActualType() == 4L) || (paramWindowPropertyGetter.getActualType() == XDataTransferer.TARGETS_ATOM.getAtom())) && (paramWindowPropertyGetter.getActualFormat() == 32))
/*     */     {
/* 247 */       int i = paramWindowPropertyGetter.getNumberOfItems();
/* 248 */       if (i > 0) {
/* 249 */         long l = paramWindowPropertyGetter.getData();
/* 250 */         arrayOfLong = new long[i];
/* 251 */         for (int j = 0; j < i; j++) {
/* 252 */           arrayOfLong[j] = Native.getLong(l + j * XAtom.getAtomSize());
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 258 */     return arrayOfLong != null ? arrayOfLong : new long[0];
/*     */   }
/*     */ 
/*     */   public byte[] getData(long paramLong1, long paramLong2)
/*     */     throws IOException
/*     */   {
/* 266 */     if (XToolkit.isToolkitThread()) {
/* 267 */       throw new Error("UNIMPLEMENTED");
/*     */     }
/*     */ 
/* 270 */     byte[] arrayOfByte1 = null;
/*     */ 
/* 272 */     synchronized (lock) {
/* 273 */       WindowPropertyGetter localWindowPropertyGetter1 = new WindowPropertyGetter(XWindow.getXAWTRootWindow().getWindow(), selectionPropertyAtom, 0L, 1000000L, false, 0L);
/*     */       try
/*     */       {
/* 280 */         XToolkit.awtLock();
/*     */         try {
/* 282 */           propertyGetter = localWindowPropertyGetter1;
/* 283 */           lastRequestServerTime = paramLong2;
/*     */ 
/* 285 */           XlibWrapper.XConvertSelection(XToolkit.getDisplay(), getSelectionAtom().getAtom(), paramLong1, selectionPropertyAtom.getAtom(), XWindow.getXAWTRootWindow().getWindow(), paramLong2);
/*     */           try
/*     */           {
/* 295 */             waitForSelectionNotify(localWindowPropertyGetter1);
/*     */           } catch (InterruptedException localInterruptedException1) {
/* 297 */             byte[] arrayOfByte2 = new byte[0];
/*     */ 
/* 299 */             propertyGetter = null;
/*     */ 
/* 302 */             XToolkit.awtUnlock();
/*     */ 
/* 429 */             localWindowPropertyGetter1.dispose(); return arrayOfByte2;
/*     */           }
/*     */           finally
/*     */           {
/* 299 */             propertyGetter = null;
/*     */           }
/*     */         } finally {
/* 302 */           XToolkit.awtUnlock();
/*     */         }
/*     */ 
/* 305 */         validateDataGetter(localWindowPropertyGetter1);
/*     */         int i;
/*     */         long l1;
/*     */         int j;
/* 308 */         if (localWindowPropertyGetter1.getActualType() == XDataTransferer.INCR_ATOM.getAtom())
/*     */         {
/* 311 */           if (localWindowPropertyGetter1.getActualFormat() != 32) {
/* 312 */             throw new IOException("Unsupported INCR format: " + localWindowPropertyGetter1.getActualFormat());
/*     */           }
/*     */ 
/* 316 */           i = localWindowPropertyGetter1.getNumberOfItems();
/*     */ 
/* 318 */           if (i <= 0) {
/* 319 */             throw new IOException("INCR data is missed.");
/*     */           }
/*     */ 
/* 322 */           l1 = localWindowPropertyGetter1.getData();
/*     */ 
/* 324 */           j = 0;
/*     */ 
/* 328 */           long l2 = Native.getLong(l1, i - 1);
/*     */ 
/* 330 */           if (l2 <= 0L) {
/* 331 */             byte[] arrayOfByte3 = new byte[0];
/*     */ 
/* 429 */             localWindowPropertyGetter1.dispose(); return arrayOfByte3;
/*     */           }
/* 334 */           if (l2 > 2147483647L) {
/* 335 */             throw new IOException("Can't handle large data block: " + l2 + " bytes");
/*     */           }
/*     */ 
/* 339 */           j = (int)l2;
/*     */ 
/* 342 */           localWindowPropertyGetter1.dispose();
/*     */ 
/* 344 */           ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(j);
/*     */           while (true)
/*     */           {
/* 347 */             WindowPropertyGetter localWindowPropertyGetter2 = new WindowPropertyGetter(XWindow.getXAWTRootWindow().getWindow(), selectionPropertyAtom, 0L, 1000000L, false, 0L);
/*     */             try
/*     */             {
/* 354 */               XToolkit.awtLock();
/* 355 */               XToolkit.addEventDispatcher(XWindow.getXAWTRootWindow().getWindow(), incrementalTransferHandler);
/*     */ 
/* 358 */               propertyGetter = localWindowPropertyGetter2;
/*     */               try
/*     */               {
/* 361 */                 XlibWrapper.XDeleteProperty(XToolkit.getDisplay(), XWindow.getXAWTRootWindow().getWindow(), selectionPropertyAtom.getAtom());
/*     */ 
/* 368 */                 waitForSelectionNotify(localWindowPropertyGetter2);
/*     */ 
/* 372 */                 propertyGetter = null;
/* 373 */                 XToolkit.removeEventDispatcher(XWindow.getXAWTRootWindow().getWindow(), incrementalTransferHandler);
/*     */ 
/* 375 */                 XToolkit.awtUnlock();
/*     */               }
/*     */               catch (InterruptedException localInterruptedException2)
/*     */               {
/* 372 */                 propertyGetter = null; } finally { propertyGetter = null;
/* 373 */                 XToolkit.removeEventDispatcher(XWindow.getXAWTRootWindow().getWindow(), incrementalTransferHandler);
/*     */ 
/* 375 */                 XToolkit.awtUnlock();
/*     */               }
/*     */ 
/* 385 */               i = localWindowPropertyGetter2.getNumberOfItems();
/*     */ 
/* 387 */               if (i == 0)
/*     */               {
/* 401 */                 localWindowPropertyGetter2.dispose(); break;
/*     */               }
/* 391 */               if (i > 0) {
/* 392 */                 l1 = localWindowPropertyGetter2.getData();
/* 393 */                 for (int k = 0; k < i; k++) {
/* 394 */                   localByteArrayOutputStream.write(Native.getByte(l1 + k));
/*     */                 }
/*     */               }
/*     */ 
/* 398 */               arrayOfByte1 = localByteArrayOutputStream.toByteArray();
/*     */             }
/*     */             finally {
/*     */             }
/*     */           }
/*     */         }
/*     */         else {
/* 405 */           XToolkit.awtLock();
/*     */           try {
/* 407 */             XlibWrapper.XDeleteProperty(XToolkit.getDisplay(), XWindow.getXAWTRootWindow().getWindow(), selectionPropertyAtom.getAtom());
/*     */           }
/*     */           finally
/*     */           {
/* 411 */             XToolkit.awtUnlock();
/*     */           }
/*     */ 
/* 414 */           if (localWindowPropertyGetter1.getActualFormat() != 8) {
/* 415 */             throw new IOException("Unsupported data format: " + localWindowPropertyGetter1.getActualFormat());
/*     */           }
/*     */ 
/* 419 */           i = localWindowPropertyGetter1.getNumberOfItems();
/* 420 */           if (i > 0) {
/* 421 */             arrayOfByte1 = new byte[i];
/* 422 */             l1 = localWindowPropertyGetter1.getData();
/* 423 */             for (j = 0; j < i; j++)
/* 424 */               arrayOfByte1[j] = Native.getByte(l1 + j);
/*     */           }
/*     */         }
/*     */       }
/*     */       finally {
/* 429 */         localWindowPropertyGetter1.dispose();
/*     */       }
/*     */     }
/*     */ 
/* 433 */     return arrayOfByte1 != null ? arrayOfByte1 : new byte[0];
/*     */   }
/*     */ 
/*     */   void validateDataGetter(WindowPropertyGetter paramWindowPropertyGetter)
/*     */     throws IOException
/*     */   {
/* 443 */     if (paramWindowPropertyGetter.isDisposed()) {
/* 444 */       throw new IOException("Owner failed to convert data");
/*     */     }
/*     */ 
/* 448 */     if (!paramWindowPropertyGetter.isExecuted())
/* 449 */       throw new IOException("Owner timed out");
/*     */   }
/*     */ 
/*     */   boolean isOwner()
/*     */   {
/* 455 */     return this.isOwner;
/*     */   }
/*     */ 
/*     */   private void setOwnerProp(boolean paramBoolean)
/*     */   {
/* 460 */     this.isOwner = paramBoolean;
/* 461 */     fireOwnershipChanges(this.isOwner);
/*     */   }
/*     */ 
/*     */   private void lostOwnership() {
/* 465 */     setOwnerProp(false);
/*     */   }
/*     */ 
/*     */   public synchronized void reset() {
/* 469 */     this.contents = null;
/* 470 */     this.formatMap = null;
/* 471 */     this.formats = null;
/* 472 */     this.appContext = null;
/* 473 */     this.ownershipTime = 0L;
/*     */   }
/*     */ 
/*     */   private boolean convertAndStore(long paramLong1, long paramLong2, long paramLong3)
/*     */   {
/* 480 */     int i = 8;
/* 481 */     byte[] arrayOfByte = null;
/* 482 */     long l = 0L;
/* 483 */     int j = 0;
/*     */     try
/*     */     {
/* 486 */       SunToolkit.insertTargetMapping(this, this.appContext);
/*     */ 
/* 488 */       arrayOfByte = DataTransferer.getInstance().convertData(this, this.contents, paramLong2, this.formatMap, XToolkit.isToolkitThread());
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/* 494 */       return false;
/*     */     }
/*     */ 
/* 497 */     if (arrayOfByte == null) {
/* 498 */       return false;
/*     */     }
/*     */ 
/* 501 */     j = arrayOfByte.length;
/*     */     try
/*     */     {
/* 504 */       if (j > 0) {
/* 505 */         if (j <= MAX_PROPERTY_SIZE) {
/* 506 */           l = Native.toData(arrayOfByte);
/*     */         }
/*     */         else {
/* 509 */           new IncrementalDataProvider(paramLong1, paramLong3, paramLong2, 8, arrayOfByte);
/*     */ 
/* 512 */           l = XlibWrapper.unsafe.allocateMemory(XAtom.getAtomSize());
/*     */ 
/* 515 */           Native.putLong(l, j);
/*     */ 
/* 517 */           paramLong2 = XDataTransferer.INCR_ATOM.getAtom();
/* 518 */           i = 32;
/* 519 */           j = 1;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 524 */       XToolkit.awtLock();
/*     */       try {
/* 526 */         XlibWrapper.XChangeProperty(XToolkit.getDisplay(), paramLong1, paramLong3, paramLong2, i, 0, l, j);
/*     */       }
/*     */       finally
/*     */       {
/* 531 */         XToolkit.awtUnlock();
/*     */       }
/*     */ 
/* 534 */       if (l != 0L) {
/* 535 */         XlibWrapper.unsafe.freeMemory(l);
/* 536 */         l = 0L;
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/* 534 */       if (l != 0L) {
/* 535 */         XlibWrapper.unsafe.freeMemory(l);
/* 536 */         l = 0L;
/*     */       }
/*     */     }
/*     */ 
/* 540 */     return true;
/*     */   }
/*     */ 
/*     */   private void handleSelectionRequest(XSelectionRequestEvent paramXSelectionRequestEvent) {
/* 544 */     long l1 = paramXSelectionRequestEvent.get_property();
/* 545 */     long l2 = paramXSelectionRequestEvent.get_requestor();
/* 546 */     long l3 = paramXSelectionRequestEvent.get_time();
/* 547 */     long l4 = paramXSelectionRequestEvent.get_target();
/* 548 */     boolean bool = false;
/*     */ 
/* 550 */     if ((this.ownershipTime != 0L) && ((l3 == 0L) || (l3 >= this.ownershipTime)))
/*     */     {
/* 554 */       if (l4 == XDataTransferer.MULTIPLE_ATOM.getAtom()) {
/* 555 */         bool = handleMultipleRequest(l2, l1);
/*     */       }
/*     */       else {
/* 558 */         if (l1 == 0L) {
/* 559 */           l1 = l4;
/*     */         }
/*     */ 
/* 562 */         if (l4 == XDataTransferer.TARGETS_ATOM.getAtom())
/* 563 */           bool = handleTargetsRequest(l1, l2);
/*     */         else {
/* 565 */           bool = convertAndStore(l2, l4, l1);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 570 */     if (!bool)
/*     */     {
/* 572 */       l1 = 0L;
/*     */     }
/*     */ 
/* 575 */     XSelectionEvent localXSelectionEvent = new XSelectionEvent();
/*     */     try {
/* 577 */       localXSelectionEvent.set_type(31);
/* 578 */       localXSelectionEvent.set_send_event(true);
/* 579 */       localXSelectionEvent.set_requestor(l2);
/* 580 */       localXSelectionEvent.set_selection(this.selectionAtom.getAtom());
/* 581 */       localXSelectionEvent.set_target(l4);
/* 582 */       localXSelectionEvent.set_property(l1);
/* 583 */       localXSelectionEvent.set_time(l3);
/*     */ 
/* 585 */       XToolkit.awtLock();
/*     */       try {
/* 587 */         XlibWrapper.XSendEvent(XToolkit.getDisplay(), l2, false, 0L, localXSelectionEvent.pData);
/*     */       }
/*     */       finally {
/* 590 */         XToolkit.awtUnlock();
/*     */       }
/*     */     } finally {
/* 593 */       localXSelectionEvent.dispose();
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean handleMultipleRequest(long paramLong1, long paramLong2) {
/* 598 */     if (0L == paramLong2)
/*     */     {
/* 600 */       return false;
/*     */     }
/*     */ 
/* 603 */     boolean bool = false;
/*     */ 
/* 606 */     WindowPropertyGetter localWindowPropertyGetter = new WindowPropertyGetter(paramLong1, XAtom.get(paramLong2), 0L, 1000000L, false, 0L);
/*     */     try
/*     */     {
/* 611 */       localWindowPropertyGetter.execute();
/*     */ 
/* 613 */       if ((localWindowPropertyGetter.getActualFormat() == 32) && (localWindowPropertyGetter.getNumberOfItems() % 2 == 0)) {
/* 614 */         long l1 = localWindowPropertyGetter.getNumberOfItems() / 2;
/* 615 */         long l2 = localWindowPropertyGetter.getData();
/* 616 */         int i = 0;
/*     */ 
/* 618 */         for (int j = 0; j < l1; j++) {
/* 619 */           long l3 = Native.getLong(l2, 2 * j);
/* 620 */           long l4 = Native.getLong(l2, 2 * j + 1);
/*     */ 
/* 622 */           if (!convertAndStore(paramLong1, l3, l4))
/*     */           {
/* 625 */             Native.putLong(l2, 2 * j, 0L);
/* 626 */             i = 1;
/*     */           }
/*     */         }
/* 629 */         if (i != 0) {
/* 630 */           XToolkit.awtLock();
/*     */           try {
/* 632 */             XlibWrapper.XChangeProperty(XToolkit.getDisplay(), paramLong1, paramLong2, localWindowPropertyGetter.getActualType(), localWindowPropertyGetter.getActualFormat(), 0, localWindowPropertyGetter.getData(), localWindowPropertyGetter.getNumberOfItems());
/*     */           }
/*     */           finally
/*     */           {
/* 641 */             XToolkit.awtUnlock();
/*     */           }
/*     */         }
/* 644 */         bool = true;
/*     */       }
/*     */     } finally {
/* 647 */       localWindowPropertyGetter.dispose();
/*     */     }
/*     */ 
/* 650 */     return bool;
/*     */   }
/*     */ 
/*     */   private boolean handleTargetsRequest(long paramLong1, long paramLong2)
/*     */     throws IllegalStateException
/*     */   {
/* 656 */     boolean bool = false;
/*     */ 
/* 658 */     long[] arrayOfLong = this.formats;
/*     */ 
/* 660 */     if (arrayOfLong == null) {
/* 661 */       throw new IllegalStateException("Not an owner.");
/*     */     }
/*     */ 
/* 664 */     long l = 0L;
/*     */     try
/*     */     {
/* 667 */       int i = arrayOfLong.length;
/*     */ 
/* 670 */       if (i > 0) {
/* 671 */         l = Native.allocateLongArray(i);
/* 672 */         Native.put(l, arrayOfLong);
/*     */       }
/*     */ 
/* 675 */       bool = true;
/*     */ 
/* 677 */       XToolkit.awtLock();
/*     */       try {
/* 679 */         XlibWrapper.XChangeProperty(XToolkit.getDisplay(), paramLong2, paramLong1, 4L, 32, 0, l, i);
/*     */       }
/*     */       finally
/*     */       {
/* 684 */         XToolkit.awtUnlock();
/*     */       }
/*     */ 
/* 687 */       if (l != 0L) {
/* 688 */         XlibWrapper.unsafe.freeMemory(l);
/* 689 */         l = 0L;
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/* 687 */       if (l != 0L) {
/* 688 */         XlibWrapper.unsafe.freeMemory(l);
/* 689 */         l = 0L;
/*     */       }
/*     */     }
/* 692 */     return bool;
/*     */   }
/*     */ 
/*     */   private void fireOwnershipChanges(boolean paramBoolean) {
/* 696 */     OwnershipListener localOwnershipListener = null;
/* 697 */     synchronized (this.stateLock) {
/* 698 */       localOwnershipListener = this.ownershipListener;
/*     */     }
/* 700 */     if (null != localOwnershipListener)
/* 701 */       localOwnershipListener.ownershipChanged(paramBoolean);
/*     */   }
/*     */ 
/*     */   void registerOwershipListener(OwnershipListener paramOwnershipListener)
/*     */   {
/* 706 */     synchronized (this.stateLock) {
/* 707 */       this.ownershipListener = paramOwnershipListener;
/*     */     }
/*     */   }
/*     */ 
/*     */   void unregisterOwnershipListener() {
/* 712 */     synchronized (this.stateLock) {
/* 713 */       this.ownershipListener = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  61 */     XToolkit.awtLock();
/*     */     try {
/*  63 */       MAX_PROPERTY_SIZE = (int)(XlibWrapper.XMaxRequestSize(XToolkit.getDisplay()) * 4L - 100L);
/*     */     }
/*     */     finally {
/*  66 */       XToolkit.awtUnlock();
/*     */     }
/*     */ 
/*  71 */     incrementalTransferHandler = new IncrementalTransferHandler(null);
/*     */ 
/*  74 */     propertyGetter = null;
/*     */ 
/* 106 */     XToolkit.addEventDispatcher(XWindow.getXAWTRootWindow().getWindow(), new SelectionEventHandler(null));
/*     */   }
/*     */ 
/*     */   private static class IncrementalDataProvider
/*     */     implements XEventDispatcher
/*     */   {
/*     */     private final long requestor;
/*     */     private final long property;
/*     */     private final long target;
/*     */     private final int format;
/*     */     private final byte[] data;
/* 778 */     private int offset = 0;
/*     */ 
/*     */     public IncrementalDataProvider(long paramLong1, long paramLong2, long paramLong3, int paramInt, byte[] paramArrayOfByte)
/*     */     {
/* 783 */       if (paramInt != 8) {
/* 784 */         throw new IllegalArgumentException("Unsupported format: " + paramInt);
/*     */       }
/*     */ 
/* 787 */       this.requestor = paramLong1;
/* 788 */       this.property = paramLong2;
/* 789 */       this.target = paramLong3;
/* 790 */       this.format = paramInt;
/* 791 */       this.data = paramArrayOfByte;
/*     */ 
/* 793 */       XWindowAttributes localXWindowAttributes = new XWindowAttributes();
/*     */       try {
/* 795 */         XToolkit.awtLock();
/*     */         try {
/* 797 */           XlibWrapper.XGetWindowAttributes(XToolkit.getDisplay(), paramLong1, localXWindowAttributes.pData);
/*     */ 
/* 799 */           XlibWrapper.XSelectInput(XToolkit.getDisplay(), paramLong1, localXWindowAttributes.get_your_event_mask() | 0x400000);
/*     */         }
/*     */         finally
/*     */         {
/* 803 */           XToolkit.awtUnlock();
/*     */         }
/*     */       } finally {
/* 806 */         localXWindowAttributes.dispose();
/*     */       }
/* 808 */       XToolkit.addEventDispatcher(paramLong1, this);
/*     */     }
/*     */ 
/*     */     public void dispatchEvent(XEvent paramXEvent) {
/* 812 */       switch (paramXEvent.get_type()) {
/*     */       case 28:
/* 814 */         XPropertyEvent localXPropertyEvent = paramXEvent.get_xproperty();
/* 815 */         if ((localXPropertyEvent.get_window() == this.requestor) && (localXPropertyEvent.get_state() == 1) && (localXPropertyEvent.get_atom() == this.property))
/*     */         {
/* 819 */           int i = this.data.length - this.offset;
/* 820 */           long l = 0L;
/* 821 */           if (i > XSelection.MAX_PROPERTY_SIZE) {
/* 822 */             i = XSelection.MAX_PROPERTY_SIZE;
/*     */           }
/*     */ 
/* 825 */           if (i > 0) {
/* 826 */             l = XlibWrapper.unsafe.allocateMemory(i);
/* 827 */             for (int j = 0; j < i; j++)
/* 828 */               Native.putByte(l + j, this.data[(this.offset + j)]);
/*     */           }
/*     */           else {
/* 831 */             assert (i == 0);
/*     */ 
/* 834 */             XToolkit.removeEventDispatcher(this.requestor, this);
/*     */           }
/*     */ 
/* 837 */           XToolkit.awtLock();
/*     */           try {
/* 839 */             XlibWrapper.XChangeProperty(XToolkit.getDisplay(), this.requestor, this.property, this.target, this.format, 0, l, i);
/*     */           }
/*     */           finally
/*     */           {
/* 845 */             XToolkit.awtUnlock();
/*     */           }
/* 847 */           if (l != 0L) {
/* 848 */             XlibWrapper.unsafe.freeMemory(l);
/* 849 */             l = 0L;
/*     */           }
/*     */ 
/* 852 */           this.offset += i;
/*     */         }break;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class IncrementalTransferHandler implements XEventDispatcher {
/*     */     public void dispatchEvent(XEvent paramXEvent) {
/* 860 */       switch (paramXEvent.get_type()) {
/*     */       case 28:
/* 862 */         XPropertyEvent localXPropertyEvent = paramXEvent.get_xproperty();
/* 863 */         if ((localXPropertyEvent.get_state() == 0) && (localXPropertyEvent.get_atom() == XSelection.selectionPropertyAtom.getAtom()))
/*     */         {
/* 865 */           XToolkit.awtLock();
/*     */           try {
/* 867 */             if (XSelection.propertyGetter != null) {
/* 868 */               XSelection.propertyGetter.execute();
/* 869 */               XSelection.access$202(null);
/*     */             }
/* 871 */             XToolkit.awtLockNotifyAll();
/*     */           } finally {
/* 873 */             XToolkit.awtUnlock();
/*     */           }
/*     */         }
/*     */         break;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class SelectionEventHandler
/*     */     implements XEventDispatcher
/*     */   {
/*     */     public void dispatchEvent(XEvent paramXEvent)
/*     */     {
/*     */       Object localObject1;
/*     */       long l;
/*     */       XSelection localXSelection;
/* 719 */       switch (paramXEvent.get_type()) {
/*     */       case 31:
/* 721 */         XToolkit.awtLock();
/*     */         try {
/* 723 */           localObject1 = paramXEvent.get_xselection();
/*     */ 
/* 725 */           if ((XSelection.propertyGetter != null) && (((XSelectionEvent)localObject1).get_time() == XSelection.lastRequestServerTime))
/*     */           {
/* 727 */             if (((XSelectionEvent)localObject1).get_property() == XSelection.selectionPropertyAtom.getAtom()) {
/* 728 */               XSelection.propertyGetter.execute();
/* 729 */               XSelection.access$202(null);
/* 730 */             } else if (((XSelectionEvent)localObject1).get_property() == 0L) {
/* 731 */               XSelection.propertyGetter.dispose();
/* 732 */               XSelection.access$202(null);
/*     */             }
/*     */           }
/* 735 */           XToolkit.awtLockNotifyAll();
/*     */         } finally {
/* 737 */           XToolkit.awtUnlock();
/*     */         }
/* 739 */         break;
/*     */       case 30:
/* 742 */         localObject1 = paramXEvent.get_xselectionrequest();
/* 743 */         l = ((XSelectionRequestEvent)localObject1).get_selection();
/* 744 */         localXSelection = XSelection.getSelection(XAtom.get(l));
/*     */ 
/* 746 */         if (localXSelection != null)
/* 747 */           localXSelection.handleSelectionRequest((XSelectionRequestEvent)localObject1); break;
/*     */       case 29:
/* 752 */         localObject1 = paramXEvent.get_xselectionclear();
/* 753 */         l = ((XSelectionClearEvent)localObject1).get_selection();
/* 754 */         localXSelection = XSelection.getSelection(XAtom.get(l));
/*     */ 
/* 756 */         if (localXSelection != null) {
/* 757 */           localXSelection.lostOwnership();
/*     */         }
/*     */ 
/* 760 */         XToolkit.awtLock();
/*     */         try {
/* 762 */           XToolkit.awtLockNotifyAll();
/*     */         } finally {
/* 764 */           XToolkit.awtUnlock();
/*     */         }
/* 766 */         break;
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XSelection
 * JD-Core Version:    0.6.2
 */