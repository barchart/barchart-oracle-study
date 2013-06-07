/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.nio.ByteOrder;
/*     */ import java.util.Arrays;
/*     */ import sun.misc.Unsafe;
/*     */ 
/*     */ class MotifDnDConstants
/*     */ {
/*  46 */   private static final Unsafe unsafe = XlibWrapper.unsafe;
/*  47 */   static final XAtom XA_MOTIF_ATOM_0 = XAtom.get("_MOTIF_ATOM_0");
/*  48 */   static final XAtom XA_MOTIF_DRAG_WINDOW = XAtom.get("_MOTIF_DRAG_WINDOW");
/*  49 */   static final XAtom XA_MOTIF_DRAG_TARGETS = XAtom.get("_MOTIF_DRAG_TARGETS");
/*  50 */   static final XAtom XA_MOTIF_DRAG_INITIATOR_INFO = XAtom.get("_MOTIF_DRAG_INITIATOR_INFO");
/*     */ 
/*  52 */   static final XAtom XA_MOTIF_DRAG_RECEIVER_INFO = XAtom.get("_MOTIF_DRAG_RECEIVER_INFO");
/*     */ 
/*  54 */   static final XAtom XA_MOTIF_DRAG_AND_DROP_MESSAGE = XAtom.get("_MOTIF_DRAG_AND_DROP_MESSAGE");
/*     */ 
/*  56 */   static final XAtom XA_XmTRANSFER_SUCCESS = XAtom.get("XmTRANSFER_SUCCESS");
/*     */ 
/*  58 */   static final XAtom XA_XmTRANSFER_FAILURE = XAtom.get("XmTRANSFER_FAILURE");
/*     */ 
/*  60 */   static final XSelection MotifDnDSelection = new XSelection(XA_MOTIF_ATOM_0);
/*     */   public static final byte MOTIF_DND_PROTOCOL_VERSION = 0;
/*     */   public static final int MOTIF_PREFER_PREREGISTER_STYLE = 2;
/*     */   public static final int MOTIF_PREFER_DYNAMIC_STYLE = 4;
/*     */   public static final int MOTIF_DYNAMIC_STYLE = 5;
/*     */   public static final int MOTIF_PREFER_RECEIVER_STYLE = 6;
/*     */   public static final int MOTIF_INITIATOR_INFO_SIZE = 8;
/*     */   public static final int MOTIF_RECEIVER_INFO_SIZE = 16;
/*     */   public static final byte MOTIF_MESSAGE_REASON_MASK = 127;
/*     */   public static final byte MOTIF_MESSAGE_SENDER_MASK = -128;
/*     */   public static final byte MOTIF_MESSAGE_FROM_RECEIVER = -128;
/*     */   public static final byte MOTIF_MESSAGE_FROM_INITIATOR = 0;
/*     */   public static final int MOTIF_DND_ACTION_MASK = 15;
/*     */   public static final int MOTIF_DND_ACTION_SHIFT = 0;
/*     */   public static final int MOTIF_DND_STATUS_MASK = 240;
/*     */   public static final int MOTIF_DND_STATUS_SHIFT = 4;
/*     */   public static final int MOTIF_DND_ACTIONS_MASK = 3840;
/*     */   public static final int MOTIF_DND_ACTIONS_SHIFT = 8;
/*     */   public static final byte TOP_LEVEL_ENTER = 0;
/*     */   public static final byte TOP_LEVEL_LEAVE = 1;
/*     */   public static final byte DRAG_MOTION = 2;
/*     */   public static final byte DROP_SITE_ENTER = 3;
/*     */   public static final byte DROP_SITE_LEAVE = 4;
/*     */   public static final byte DROP_START = 5;
/*     */   public static final byte DROP_FINISH = 6;
/*     */   public static final byte DRAG_DROP_FINISH = 7;
/*     */   public static final byte OPERATION_CHANGED = 8;
/*     */   public static final int MOTIF_DND_NOOP = 0;
/*     */   public static final int MOTIF_DND_MOVE = 1;
/*     */   public static final int MOTIF_DND_COPY = 2;
/*     */   public static final int MOTIF_DND_LINK = 4;
/*     */   public static final byte MOTIF_NO_DROP_SITE = 1;
/*     */   public static final byte MOTIF_INVALID_DROP_SITE = 2;
/*     */   public static final byte MOTIF_VALID_DROP_SITE = 3;
/*     */ 
/*     */   private static long readMotifWindow()
/*     */     throws XException
/*     */   {
/* 111 */     long l1 = XlibWrapper.DefaultScreen(XToolkit.getDisplay());
/* 112 */     long l2 = XlibWrapper.RootWindow(XToolkit.getDisplay(), l1);
/*     */ 
/* 115 */     long l3 = 0L;
/*     */ 
/* 117 */     WindowPropertyGetter localWindowPropertyGetter = new WindowPropertyGetter(l2, XA_MOTIF_DRAG_WINDOW, 0L, 1L, false, 0L);
/*     */     try
/*     */     {
/* 123 */       int i = localWindowPropertyGetter.execute(XErrorHandler.IgnoreBadWindowHandler.getInstance());
/*     */       long l4;
/* 125 */       if ((i == 0) && (localWindowPropertyGetter.getData() != 0L) && (localWindowPropertyGetter.getActualType() == 33L) && (localWindowPropertyGetter.getActualFormat() == 32) && (localWindowPropertyGetter.getNumberOfItems() == 1))
/*     */       {
/* 130 */         l4 = localWindowPropertyGetter.getData();
/*     */ 
/* 132 */         l3 = Native.getLong(l4);
/*     */       }
/*     */ 
/* 135 */       return l3;
/*     */     } finally {
/* 137 */       localWindowPropertyGetter.dispose();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static long createMotifWindow() throws XException {
/* 142 */     assert (XToolkit.isAWTLockHeldByCurrentThread());
/*     */ 
/* 144 */     long l1 = XlibWrapper.DefaultScreen(XToolkit.getDisplay());
/*     */ 
/* 146 */     long l2 = XlibWrapper.RootWindow(XToolkit.getDisplay(), l1);
/*     */ 
/* 149 */     long l3 = 0L;
/*     */ 
/* 151 */     long l4 = XlibWrapper.XDisplayString(XToolkit.getDisplay());
/*     */ 
/* 153 */     if (l4 == 0L) {
/* 154 */       throw new XException("XDisplayString returns NULL");
/*     */     }
/*     */ 
/* 157 */     long l5 = XlibWrapper.XOpenDisplay(l4);
/*     */ 
/* 159 */     if (l5 == 0L) {
/* 160 */       throw new XException("XOpenDisplay returns NULL");
/*     */     }
/*     */ 
/* 163 */     XlibWrapper.XGrabServer(l5);
/*     */     try
/*     */     {
/* 166 */       XlibWrapper.XSetCloseDownMode(l5, 1);
/*     */ 
/* 168 */       XSetWindowAttributes localXSetWindowAttributes = new XSetWindowAttributes();
/*     */       try
/*     */       {
/* 171 */         localXSetWindowAttributes.set_override_redirect(true);
/* 172 */         localXSetWindowAttributes.set_event_mask(4194304L);
/*     */ 
/* 174 */         l3 = XlibWrapper.XCreateWindow(l5, l2, -10, -10, 1, 1, 0, 0, 2L, 0L, 2560L, localXSetWindowAttributes.pData);
/*     */ 
/* 182 */         if (l3 == 0L) {
/* 183 */           throw new XException("XCreateWindow returns NULL");
/*     */         }
/*     */ 
/* 186 */         XlibWrapper.XMapWindow(l5, l3);
/*     */ 
/* 188 */         long l6 = Native.allocateLongArray(1);
/*     */         try
/*     */         {
/* 191 */           Native.putLong(l6, l3);
/*     */ 
/* 193 */           XToolkit.WITH_XERROR_HANDLER(XErrorHandler.VerifyChangePropertyHandler.getInstance());
/* 194 */           XlibWrapper.XChangeProperty(XToolkit.getDisplay(), l2, XA_MOTIF_DRAG_WINDOW.getAtom(), 33L, 32, 0, l6, 1);
/*     */ 
/* 201 */           XToolkit.RESTORE_XERROR_HANDLER();
/*     */ 
/* 203 */           if ((XToolkit.saved_error != null) && (XToolkit.saved_error.get_error_code() != 0))
/*     */           {
/* 205 */             throw new XException("Cannot write motif drag window handle.");
/*     */           }
/*     */ 
/* 208 */           long l7 = l3;
/*     */ 
/* 210 */           unsafe.freeMemory(l6);
/*     */ 
/* 213 */           localXSetWindowAttributes.dispose();
/*     */ 
/* 217 */           return l7;
/*     */         }
/*     */         finally
/*     */         {
/* 210 */           unsafe.freeMemory(l6);
/*     */         }
/*     */       } finally {
/* 213 */         localXSetWindowAttributes.dispose();
/*     */       }
/*     */     } finally {
/* 216 */       XlibWrapper.XUngrabServer(l5);
/* 217 */       XlibWrapper.XCloseDisplay(l5);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static long getMotifWindow()
/*     */     throws XException
/*     */   {
/* 227 */     long l = readMotifWindow();
/* 228 */     if (l == 0L) {
/* 229 */       l = createMotifWindow();
/*     */     }
/* 231 */     return l;
/*     */   }
/*     */ 
/*     */   private static long[][] getTargetListTable(long paramLong)
/*     */     throws XException
/*     */   {
/* 277 */     WindowPropertyGetter localWindowPropertyGetter = new WindowPropertyGetter(paramLong, XA_MOTIF_DRAG_TARGETS, 0L, 100000L, false, XA_MOTIF_DRAG_TARGETS.getAtom());
/*     */     try
/*     */     {
/* 283 */       int i = localWindowPropertyGetter.execute(XErrorHandler.IgnoreBadWindowHandler.getInstance());
/*     */ 
/* 285 */       if ((i != 0) || (localWindowPropertyGetter.getActualType() != XA_MOTIF_DRAG_TARGETS.getAtom()) || (localWindowPropertyGetter.getData() == 0L))
/*     */       {
/* 289 */         return (long[][])null;
/*     */       }
/*     */ 
/* 292 */       long l1 = localWindowPropertyGetter.getData();
/*     */ 
/* 294 */       if (unsafe.getByte(l1 + 1L) != 0) {
/* 295 */         return (long[][])null;
/*     */       }
/*     */ 
/* 298 */       int j = unsafe.getByte(l1 + 0L) != getByteOrderByte() ? 1 : 0;
/*     */ 
/* 300 */       short s1 = unsafe.getShort(l1 + 2L);
/*     */ 
/* 302 */       if (j != 0) {
/* 303 */         s1 = Swapper.swap(s1);
/*     */       }
/*     */ 
/* 306 */       long[][] arrayOfLong3 = new long[s1][];
/* 307 */       ByteOrder localByteOrder = ByteOrder.nativeOrder();
/* 308 */       if (j != 0) {
/* 309 */         localByteOrder = localByteOrder == ByteOrder.LITTLE_ENDIAN ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
/*     */       }
/*     */ 
/* 313 */       long l2 = l1 + 8L;
/* 314 */       for (short s2 = 0; s2 < s1; s2 = (short)(s2 + 1)) {
/* 315 */         short s3 = unsafe.getShort(l2);
/* 316 */         l2 += 2L;
/* 317 */         if (j != 0) {
/* 318 */           s3 = Swapper.swap(s3);
/*     */         }
/*     */ 
/* 321 */         arrayOfLong3[s2] = new long[s3];
/*     */ 
/* 323 */         for (short s4 = 0; s4 < s3; s4 = (short)(s4 + 1))
/*     */         {
/* 326 */           int k = 0;
/*     */           int m;
/* 327 */           if (localByteOrder == ByteOrder.LITTLE_ENDIAN) {
/* 328 */             for (m = 0; m < 4; m++) {
/* 329 */               k |= unsafe.getByte(l2 + m) << 8 * m & 255 << 8 * m;
/*     */             }
/*     */           }
/*     */           else {
/* 333 */             for (m = 0; m < 4; m++) {
/* 334 */               k |= unsafe.getByte(l2 + m) << 8 * (3 - m) & 255 << 8 * (3 - m);
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/* 340 */           arrayOfLong3[s2][s4] = k;
/* 341 */           l2 += 4L;
/*     */         }
/*     */       }
/* 344 */       return arrayOfLong3;
/*     */     } finally {
/* 346 */       localWindowPropertyGetter.dispose();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void putTargetListTable(long paramLong, long[][] paramArrayOfLong) throws XException
/*     */   {
/* 352 */     assert (XToolkit.isAWTLockHeldByCurrentThread());
/*     */ 
/* 354 */     int i = 8;
/*     */ 
/* 356 */     for (int j = 0; j < paramArrayOfLong.length; j++) {
/* 357 */       i += paramArrayOfLong[j].length * 4 + 2;
/*     */     }
/*     */ 
/* 360 */     long l1 = unsafe.allocateMemory(i);
/*     */     try
/*     */     {
/* 364 */       unsafe.putByte(l1 + 0L, getByteOrderByte());
/*     */ 
/* 366 */       unsafe.putByte(l1 + 1L, (byte)0);
/*     */ 
/* 368 */       unsafe.putShort(l1 + 2L, (short)paramArrayOfLong.length);
/*     */ 
/* 370 */       unsafe.putInt(l1 + 4L, i);
/*     */ 
/* 372 */       long l2 = l1 + 8L;
/*     */ 
/* 374 */       for (int k = 0; k < paramArrayOfLong.length; k++) {
/* 375 */         unsafe.putShort(l2, (short)paramArrayOfLong[k].length);
/* 376 */         l2 += 2L;
/*     */ 
/* 378 */         for (int m = 0; m < paramArrayOfLong[k].length; m++) {
/* 379 */           int n = (int)paramArrayOfLong[k][m];
/*     */           int i1;
/*     */           byte b;
/* 382 */           if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN)
/* 383 */             for (i1 = 0; i1 < 4; i1++) {
/* 384 */               b = (byte)((n & 255 << 8 * i1) >> 8 * i1);
/* 385 */               unsafe.putByte(l2 + i1, b);
/*     */             }
/*     */           else {
/* 388 */             for (i1 = 0; i1 < 4; i1++) {
/* 389 */               b = (byte)((n & 255 << 8 * i1) >> 8 * i1);
/* 390 */               unsafe.putByte(l2 + (3 - i1), b);
/*     */             }
/*     */           }
/* 393 */           l2 += 4L;
/*     */         }
/*     */       }
/*     */ 
/* 397 */       XToolkit.WITH_XERROR_HANDLER(XErrorHandler.VerifyChangePropertyHandler.getInstance());
/* 398 */       XlibWrapper.XChangeProperty(XToolkit.getDisplay(), paramLong, XA_MOTIF_DRAG_TARGETS.getAtom(), XA_MOTIF_DRAG_TARGETS.getAtom(), 8, 0, l1, i);
/*     */ 
/* 405 */       XToolkit.RESTORE_XERROR_HANDLER();
/*     */ 
/* 407 */       if ((XToolkit.saved_error != null) && (XToolkit.saved_error.get_error_code() != 0))
/*     */       {
/* 411 */         paramLong = createMotifWindow();
/*     */ 
/* 413 */         XToolkit.WITH_XERROR_HANDLER(XErrorHandler.VerifyChangePropertyHandler.getInstance());
/* 414 */         XlibWrapper.XChangeProperty(XToolkit.getDisplay(), paramLong, XA_MOTIF_DRAG_TARGETS.getAtom(), XA_MOTIF_DRAG_TARGETS.getAtom(), 8, 0, l1, i);
/*     */ 
/* 421 */         XToolkit.RESTORE_XERROR_HANDLER();
/*     */ 
/* 423 */         if ((XToolkit.saved_error != null) && (XToolkit.saved_error.get_error_code() != 0))
/*     */         {
/* 425 */           throw new XException("Cannot write motif drag targets property.");
/*     */         }
/*     */       }
/*     */     } finally {
/* 429 */       unsafe.freeMemory(l1);
/*     */     }
/*     */   }
/*     */ 
/*     */   static int getIndexForTargetList(long[] paramArrayOfLong) throws XException {
/* 434 */     assert (XToolkit.isAWTLockHeldByCurrentThread());
/*     */ 
/* 436 */     if (paramArrayOfLong.length > 0)
/*     */     {
/* 438 */       paramArrayOfLong = (long[])paramArrayOfLong.clone();
/*     */ 
/* 440 */       Arrays.sort(paramArrayOfLong);
/*     */     }
/*     */ 
/* 451 */     long l = getMotifWindow();
/*     */ 
/* 453 */     XlibWrapper.XGrabServer(XToolkit.getDisplay());
/*     */     try
/*     */     {
/* 456 */       Object localObject1 = getTargetListTable(l);
/*     */ 
/* 458 */       if (localObject1 != null) {
/* 459 */         for (int i = 0; i < localObject1.length; i++) {
/* 460 */           j = 1;
/*     */           int k;
/* 461 */           if (localObject1[i].length == paramArrayOfLong.length) {
/* 462 */             for (k = 0; k < localObject1[i].length; k++)
/* 463 */               if (localObject1[i][k] != paramArrayOfLong[k]) {
/* 464 */                 j = 0;
/* 465 */                 break;
/*     */               }
/*     */           }
/*     */           else {
/* 469 */             j = 0;
/*     */           }
/*     */ 
/* 472 */           if (j != 0) {
/* 473 */             XlibWrapper.XUngrabServer(XToolkit.getDisplay());
/* 474 */             return i;
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 481 */         localObject1 = new long[2][];
/* 482 */         localObject1[0] = { 0L };
/* 483 */         localObject1[1] = { 31L };
/*     */       }
/*     */ 
/* 487 */       long[][] arrayOfLong = new long[localObject1.length + 1][];
/*     */ 
/* 490 */       for (int j = 0; j < localObject1.length; j++) {
/* 491 */         arrayOfLong[j] = localObject1[j];
/*     */       }
/*     */ 
/* 495 */       arrayOfLong[(arrayOfLong.length - 1)] = paramArrayOfLong;
/*     */ 
/* 497 */       putTargetListTable(l, arrayOfLong);
/*     */ 
/* 499 */       return arrayOfLong.length - 1;
/*     */     } finally {
/* 501 */       XlibWrapper.XUngrabServer(XToolkit.getDisplay());
/*     */     }
/*     */   }
/*     */ 
/*     */   static long[] getTargetListForIndex(int paramInt) {
/* 506 */     long l = getMotifWindow();
/* 507 */     long[][] arrayOfLong = getTargetListTable(l);
/*     */ 
/* 509 */     if ((paramInt < 0) || (paramInt >= arrayOfLong.length)) {
/* 510 */       return new long[0];
/*     */     }
/* 512 */     return arrayOfLong[paramInt];
/*     */   }
/*     */ 
/*     */   static byte getByteOrderByte()
/*     */   {
/* 518 */     return ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN ? 108 : 66;
/*     */   }
/*     */ 
/*     */   static void writeDragInitiatorInfoStruct(long paramLong, int paramInt) throws XException
/*     */   {
/* 523 */     assert (XToolkit.isAWTLockHeldByCurrentThread());
/*     */ 
/* 525 */     long l = unsafe.allocateMemory(8L);
/*     */     try
/*     */     {
/* 529 */       unsafe.putByte(l, getByteOrderByte());
/*     */ 
/* 531 */       unsafe.putByte(l + 1L, (byte)0);
/*     */ 
/* 533 */       unsafe.putShort(l + 2L, (short)paramInt);
/*     */ 
/* 535 */       unsafe.putInt(l + 4L, (int)XA_MOTIF_ATOM_0.getAtom());
/*     */ 
/* 537 */       XToolkit.WITH_XERROR_HANDLER(XErrorHandler.VerifyChangePropertyHandler.getInstance());
/* 538 */       XlibWrapper.XChangeProperty(XToolkit.getDisplay(), paramLong, XA_MOTIF_ATOM_0.getAtom(), XA_MOTIF_DRAG_INITIATOR_INFO.getAtom(), 8, 0, l, 8);
/*     */ 
/* 543 */       XToolkit.RESTORE_XERROR_HANDLER();
/*     */ 
/* 545 */       if ((XToolkit.saved_error != null) && (XToolkit.saved_error.get_error_code() != 0))
/*     */       {
/* 547 */         throw new XException("Cannot write drag initiator info");
/*     */       }
/*     */     } finally {
/* 550 */       unsafe.freeMemory(l);
/*     */     }
/*     */   }
/*     */ 
/*     */   static void writeDragReceiverInfoStruct(long paramLong) throws XException {
/* 555 */     assert (XToolkit.isAWTLockHeldByCurrentThread());
/*     */ 
/* 557 */     int i = 16;
/* 558 */     long l = unsafe.allocateMemory(i);
/*     */     try
/*     */     {
/* 561 */       unsafe.putByte(l, getByteOrderByte());
/* 562 */       unsafe.putByte(l + 1L, (byte)0);
/* 563 */       unsafe.putByte(l + 2L, (byte)5);
/* 564 */       unsafe.putByte(l + 3L, (byte)0);
/* 565 */       unsafe.putInt(l + 4L, (int)paramLong);
/* 566 */       unsafe.putShort(l + 8L, (short)0);
/* 567 */       unsafe.putShort(l + 10L, (short)0);
/* 568 */       unsafe.putInt(l + 12L, i);
/*     */ 
/* 570 */       XToolkit.WITH_XERROR_HANDLER(XErrorHandler.VerifyChangePropertyHandler.getInstance());
/* 571 */       XlibWrapper.XChangeProperty(XToolkit.getDisplay(), paramLong, XA_MOTIF_DRAG_RECEIVER_INFO.getAtom(), XA_MOTIF_DRAG_RECEIVER_INFO.getAtom(), 8, 0, l, i);
/*     */ 
/* 576 */       XToolkit.RESTORE_XERROR_HANDLER();
/*     */ 
/* 578 */       if ((XToolkit.saved_error != null) && (XToolkit.saved_error.get_error_code() != 0))
/*     */       {
/* 580 */         throw new XException("Cannot write Motif receiver info property");
/*     */       }
/*     */     } finally {
/* 583 */       unsafe.freeMemory(l);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static int getMotifActionsForJavaActions(int paramInt) {
/* 588 */     int i = 0;
/*     */ 
/* 590 */     if ((paramInt & 0x2) != 0) {
/* 591 */       i |= 1;
/*     */     }
/* 593 */     if ((paramInt & 0x1) != 0) {
/* 594 */       i |= 2;
/*     */     }
/* 596 */     if ((paramInt & 0x40000000) != 0) {
/* 597 */       i |= 4;
/*     */     }
/*     */ 
/* 600 */     return i;
/*     */   }
/*     */ 
/*     */   public static int getJavaActionsForMotifActions(int paramInt) {
/* 604 */     int i = 0;
/*     */ 
/* 606 */     if ((paramInt & 0x1) != 0) {
/* 607 */       i |= 2;
/*     */     }
/* 609 */     if ((paramInt & 0x2) != 0) {
/* 610 */       i |= 1;
/*     */     }
/* 612 */     if ((paramInt & 0x4) != 0) {
/* 613 */       i |= 1073741824;
/*     */     }
/*     */ 
/* 616 */     return i;
/*     */   }
/*     */ 
/*     */   public static final class Swapper
/*     */   {
/*     */     public static short swap(short paramShort)
/*     */     {
/* 239 */       return (short)((paramShort & 0xFF00) >>> 8 | (paramShort & 0xFF) << 8);
/*     */     }
/*     */     public static int swap(int paramInt) {
/* 242 */       return (paramInt & 0xFF000000) >>> 24 | (paramInt & 0xFF0000) >>> 8 | (paramInt & 0xFF00) << 8 | (paramInt & 0xFF) << 24;
/*     */     }
/*     */ 
/*     */     public static short getShort(long paramLong, byte paramByte)
/*     */     {
/* 247 */       short s = MotifDnDConstants.unsafe.getShort(paramLong);
/* 248 */       if (paramByte != MotifDnDConstants.getByteOrderByte()) {
/* 249 */         return swap(s);
/*     */       }
/* 251 */       return s;
/*     */     }
/*     */ 
/*     */     public static int getInt(long paramLong, byte paramByte) {
/* 255 */       int i = MotifDnDConstants.unsafe.getInt(paramLong);
/* 256 */       if (paramByte != MotifDnDConstants.getByteOrderByte()) {
/* 257 */         return swap(i);
/*     */       }
/* 259 */       return i;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.MotifDnDConstants
 * JD-Core Version:    0.6.2
 */