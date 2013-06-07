/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.Robot;
/*     */ import java.util.LinkedList;
/*     */ import sun.misc.Unsafe;
/*     */ import sun.util.logging.PlatformLogger;
/*     */ 
/*     */ public class XEmbedServerTester
/*     */   implements XEventDispatcher
/*     */ {
/*  40 */   private static final PlatformLogger xembedLog = PlatformLogger.getLogger("sun.awt.X11.xembed.XEmbedServerTester");
/*  41 */   private final Object EVENT_LOCK = new Object();
/*     */   static final int SYSTEM_EVENT_MASK = 32768;
/*     */   int my_version;
/*     */   int server_version;
/*  44 */   XEmbedHelper xembed = new XEmbedHelper();
/*     */   boolean focused;
/*     */   int focusedKind;
/*     */   int focusedServerComponent;
/*     */   boolean reparent;
/*     */   long parent;
/*     */   boolean windowActive;
/*     */   boolean xembedActive;
/*     */   XBaseWindow window;
/*  53 */   volatile int eventWaited = -1; volatile int eventReceived = -1;
/*     */   int mapped;
/*     */   int accel_key;
/*     */   int accel_keysym;
/*     */   int accel_mods;
/*  56 */   static Rectangle initialBounds = new Rectangle(0, 0, 100, 100);
/*     */   Robot robot;
/*     */   Rectangle[] serverBounds;
/*     */   private static final int SERVER_BOUNDS = 0;
/*     */   private static final int OTHER_FRAME = 1;
/*     */   private static final int SERVER_FOCUS = 2;
/*     */   private static final int SERVER_MODAL = 3;
/*     */   private static final int MODAL_CLOSE = 4;
/*  61 */   LinkedList<Integer> events = new LinkedList();
/*     */ 
/*     */   private XEmbedServerTester(Rectangle[] paramArrayOfRectangle, long paramLong) {
/*  64 */     this.parent = paramLong;
/*  65 */     this.focusedKind = -1;
/*  66 */     this.focusedServerComponent = -1;
/*  67 */     this.reparent = false;
/*  68 */     this.windowActive = false;
/*  69 */     this.xembedActive = false;
/*  70 */     this.my_version = 0;
/*  71 */     this.mapped = 1;
/*  72 */     this.serverBounds = paramArrayOfRectangle;
/*  73 */     if (paramArrayOfRectangle.length < 5) {
/*  74 */       throw new IllegalArgumentException("There must be at least five areas: server-activation, server-deactivation, server-focus, server-modal show, modal-close");
/*     */     }
/*     */     try
/*     */     {
/*  78 */       this.robot = new Robot();
/*  79 */       this.robot.setAutoDelay(100);
/*     */     } catch (Exception localException) {
/*  81 */       throw new RuntimeException("Can't create robot");
/*     */     }
/*  83 */     initAccel();
/*  84 */     xembedLog.finer("XEmbed client(tester), embedder window: " + Long.toHexString(paramLong));
/*     */   }
/*     */ 
/*     */   public static XEmbedServerTester getTester(Rectangle[] paramArrayOfRectangle, long paramLong) {
/*  88 */     return new XEmbedServerTester(paramArrayOfRectangle, paramLong);
/*     */   }
/*     */ 
/*     */   private void dumpReceivedEvents() {
/*  92 */     xembedLog.finer("Events received so far:");
/*  93 */     int i = 0;
/*  94 */     for (Integer localInteger : this.events) {
/*  95 */       xembedLog.finer(i++ + ":" + XEmbedHelper.msgidToString(localInteger.intValue()));
/*     */     }
/*  97 */     xembedLog.finer("End of event dump");
/*     */   }
/*     */ 
/*     */   public void test1_1() {
/* 101 */     int i = embedCompletely();
/* 102 */     waitWindowActivated(i);
/* 103 */     requestFocus();
/* 104 */     deactivateServer();
/* 105 */     i = activateServer(getEventPos());
/* 106 */     waitFocusGained(i);
/* 107 */     checkFocusGained(0);
/*     */   }
/*     */ 
/*     */   public void test1_2() {
/* 111 */     int i = embedCompletely();
/* 112 */     waitWindowActivated(i);
/* 113 */     requestFocus();
/* 114 */     checkFocusGained(0);
/*     */   }
/*     */ 
/*     */   public void test1_3() {
/* 118 */     embedCompletely();
/* 119 */     deactivateServer();
/* 120 */     requestFocusNoWait();
/* 121 */     checkNotFocused();
/*     */   }
/*     */ 
/*     */   public void test1_4() {
/* 125 */     embedCompletely();
/* 126 */     deactivateServer();
/* 127 */     requestFocusNoWait();
/* 128 */     checkNotFocused();
/* 129 */     int i = getEventPos();
/* 130 */     activateServer(i);
/* 131 */     waitFocusGained(i);
/* 132 */     checkFocusGained(0);
/*     */   }
/*     */ 
/*     */   public void test1_5() {
/* 136 */     int i = embedCompletely();
/* 137 */     waitWindowActivated(i);
/* 138 */     checkWindowActivated();
/*     */   }
/*     */ 
/*     */   public void test1_6() {
/* 142 */     int i = embedCompletely();
/* 143 */     waitWindowActivated(i);
/* 144 */     requestFocus();
/* 145 */     i = deactivateServer();
/* 146 */     checkFocused();
/*     */   }
/*     */ 
/*     */   public void test1_7() {
/* 150 */     int i = embedCompletely();
/* 151 */     waitWindowActivated(i);
/* 152 */     requestFocus();
/* 153 */     focusServer();
/* 154 */     checkFocusLost();
/*     */   }
/*     */ 
/*     */   public void test2_5() {
/* 158 */     int i = embedCompletely();
/* 159 */     waitWindowActivated(i);
/* 160 */     requestFocus();
/* 161 */     focusServerNext();
/* 162 */     checkFocusedServerNext();
/* 163 */     checkFocusLost();
/*     */   }
/*     */ 
/*     */   public void test2_6() {
/* 167 */     int i = embedCompletely();
/* 168 */     waitWindowActivated(i);
/* 169 */     requestFocus();
/* 170 */     focusServerPrev();
/* 171 */     checkFocusedServerPrev();
/* 172 */     checkFocusLost();
/*     */   }
/*     */ 
/*     */   public void test3_1() {
/* 176 */     this.reparent = false;
/* 177 */     embedCompletely();
/*     */   }
/*     */ 
/*     */   public void test3_3() {
/* 181 */     this.reparent = true;
/* 182 */     embedCompletely();
/*     */   }
/*     */ 
/*     */   public void test3_4() {
/* 186 */     this.my_version = 10;
/* 187 */     embedCompletely();
/* 188 */     if (this.server_version != 0)
/* 189 */       throw new RuntimeException("Version " + this.server_version + " is not minimal");
/*     */   }
/*     */ 
/*     */   public void test3_5()
/*     */   {
/* 194 */     embedCompletely();
/*     */ 
/* 196 */     this.window.destroy();
/*     */ 
/* 202 */     sleep(1000);
/*     */   }
/*     */ 
/*     */   public void test3_6() {
/* 206 */     embedCompletely();
/*     */ 
/* 208 */     sleep(1000);
/* 209 */     XToolkit.awtLock();
/*     */     try {
/* 211 */       XlibWrapper.XUnmapWindow(XToolkit.getDisplay(), this.window.getWindow());
/* 212 */       XlibWrapper.XReparentWindow(XToolkit.getDisplay(), this.window.getWindow(), XToolkit.getDefaultRootWindow(), 0, 0);
/*     */     } finally {
/* 214 */       XToolkit.awtUnlock();
/*     */     }
/*     */ 
/* 217 */     int i = getEventPos();
/*     */ 
/* 219 */     activateServerNoWait(i);
/*     */ 
/* 221 */     sleep(1000);
/* 222 */     if (checkEventList(i, 1) != -1)
/* 223 */       throw new RuntimeException("Focus was been given to the client after XEmbed has ended");
/*     */   }
/*     */ 
/*     */   public void test4_1()
/*     */   {
/* 228 */     this.mapped = 1;
/* 229 */     int i = getEventPos();
/* 230 */     embedCompletely();
/* 231 */     sleep(1000);
/* 232 */     checkMapped();
/*     */   }
/*     */ 
/*     */   public void test4_2() {
/* 236 */     this.mapped = 0;
/* 237 */     embedCompletely();
/* 238 */     sleep(1000);
/*     */ 
/* 240 */     int i = getEventPos();
/* 241 */     this.mapped = 1;
/* 242 */     updateEmbedInfo();
/* 243 */     sleep(1000);
/* 244 */     checkMapped();
/*     */   }
/*     */ 
/*     */   public void test4_3() {
/* 248 */     int i = getEventPos();
/* 249 */     this.mapped = 1;
/* 250 */     embedCompletely();
/*     */ 
/* 252 */     i = getEventPos();
/* 253 */     this.mapped = 0;
/* 254 */     updateEmbedInfo();
/* 255 */     sleep(1000);
/* 256 */     checkNotMapped();
/*     */   }
/*     */ 
/*     */   public void test4_4() {
/* 260 */     this.mapped = 0;
/* 261 */     embedCompletely();
/* 262 */     sleep(1000);
/* 263 */     if (XlibUtil.getWindowMapState(this.window.getWindow()) != 0)
/* 264 */       throw new RuntimeException("Client has been mapped");
/*     */   }
/*     */ 
/*     */   public void test6_1_1()
/*     */   {
/* 269 */     embedCompletely();
/* 270 */     registerAccelerator();
/* 271 */     focusServer();
/* 272 */     int i = pressAccelKey();
/* 273 */     waitForEvent(i, 14);
/*     */   }
/*     */ 
/*     */   public void test6_1_2() {
/* 277 */     embedCompletely();
/* 278 */     registerAccelerator();
/* 279 */     focusServer();
/* 280 */     deactivateServer();
/* 281 */     int i = pressAccelKey();
/* 282 */     sleep(1000);
/* 283 */     if (checkEventList(i, 14) != -1)
/* 284 */       throw new RuntimeException("Accelerator has been activated in inactive embedder");
/*     */   }
/*     */ 
/*     */   public void test6_1_3()
/*     */   {
/* 289 */     embedCompletely();
/* 290 */     registerAccelerator();
/* 291 */     focusServer();
/* 292 */     deactivateServer();
/* 293 */     unregisterAccelerator();
/* 294 */     int i = pressAccelKey();
/* 295 */     sleep(1000);
/* 296 */     if (checkEventList(i, 14) != -1)
/* 297 */       throw new RuntimeException("Accelerator has been activated after unregistering");
/*     */   }
/*     */ 
/*     */   public void test6_1_4()
/*     */   {
/* 302 */     embedCompletely();
/* 303 */     registerAccelerator();
/* 304 */     requestFocus();
/* 305 */     int i = pressAccelKey();
/* 306 */     sleep(1000);
/* 307 */     if (checkEventList(i, 14) != -1)
/* 308 */       throw new RuntimeException("Accelerator has been activated in focused client");
/*     */   }
/*     */ 
/*     */   public void test6_2_1() {
/* 312 */     embedCompletely();
/* 313 */     grabKey();
/* 314 */     focusServer();
/* 315 */     int i = pressAccelKey();
/* 316 */     waitSystemEvent(i, 2);
/*     */   }
/*     */ 
/*     */   public void test6_2_2() {
/* 320 */     embedCompletely();
/* 321 */     grabKey();
/* 322 */     focusServer();
/* 323 */     deactivateServer();
/* 324 */     int i = pressAccelKey();
/* 325 */     sleep(1000);
/* 326 */     if (checkEventList(i, 32770) != -1)
/* 327 */       throw new RuntimeException("Accelerator has been activated in inactive embedder");
/*     */   }
/*     */ 
/*     */   public void test6_2_3()
/*     */   {
/* 332 */     embedCompletely();
/* 333 */     grabKey();
/* 334 */     focusServer();
/* 335 */     deactivateServer();
/* 336 */     ungrabKey();
/* 337 */     int i = pressAccelKey();
/* 338 */     sleep(1000);
/* 339 */     if (checkEventList(i, 32770) != -1)
/* 340 */       throw new RuntimeException("Accelerator has been activated after unregistering");
/*     */   }
/*     */ 
/*     */   public void test6_2_4()
/*     */   {
/* 345 */     embedCompletely();
/* 346 */     grabKey();
/* 347 */     requestFocus();
/* 348 */     int i = pressAccelKey();
/* 349 */     sleep(1000);
/* 350 */     int j = checkEventList(i, 32770);
/* 351 */     if (j != -1) {
/* 352 */       j = checkEventList(j + 1, 32770);
/* 353 */       if (j != -1)
/* 354 */         throw new RuntimeException("Accelerator has been activated in focused client");
/*     */     }
/*     */   }
/*     */ 
/*     */   public void test7_1()
/*     */   {
/* 360 */     embedCompletely();
/* 361 */     int i = showModalDialog();
/* 362 */     waitForEvent(i, 10);
/*     */   }
/*     */ 
/*     */   public void test7_2() {
/* 366 */     embedCompletely();
/* 367 */     int i = showModalDialog();
/* 368 */     waitForEvent(i, 10);
/* 369 */     i = hideModalDialog();
/* 370 */     waitForEvent(i, 11);
/*     */   }
/*     */ 
/*     */   public void test9_1() {
/* 374 */     embedCompletely();
/* 375 */     requestFocus();
/* 376 */     int i = pressAccelKey();
/* 377 */     waitForEvent(i, 32770);
/*     */   }
/*     */ 
/*     */   private int embed() {
/* 381 */     int i = getEventPos();
/* 382 */     XToolkit.awtLock();
/*     */     try {
/* 384 */       XCreateWindowParams localXCreateWindowParams = new XCreateWindowParams(new Object[] { "parent window", Long.valueOf(this.reparent ? XToolkit.getDefaultRootWindow() : this.parent), "bounds", initialBounds, "embedded", Boolean.TRUE, "visible", Boolean.valueOf(this.mapped == 1 ? 1 : false), "event mask", Long.valueOf(720897L) });
/*     */ 
/* 392 */       this.window = new XBaseWindow(localXCreateWindowParams);
/*     */ 
/* 394 */       xembedLog.finer("Created tester window: " + this.window);
/*     */ 
/* 396 */       XToolkit.addEventDispatcher(this.window.getWindow(), this);
/* 397 */       updateEmbedInfo();
/* 398 */       if (this.reparent) {
/* 399 */         xembedLog.finer("Reparenting to embedder");
/* 400 */         XlibWrapper.XReparentWindow(XToolkit.getDisplay(), this.window.getWindow(), this.parent, 0, 0);
/*     */       }
/*     */     } finally {
/* 403 */       XToolkit.awtUnlock();
/*     */     }
/* 405 */     return i;
/*     */   }
/*     */ 
/*     */   private void updateEmbedInfo() {
/* 409 */     long[] arrayOfLong = { this.my_version, this.mapped };
/* 410 */     long l = Native.card32ToData(arrayOfLong);
/*     */     try {
/* 412 */       XEmbedHelper.XEmbedInfo.setAtomData(this.window.getWindow(), l, arrayOfLong.length);
/*     */     } finally {
/* 414 */       XEmbedHelper.unsafe.freeMemory(l);
/*     */     }
/*     */   }
/*     */ 
/*     */   private int getEventPos() {
/* 419 */     synchronized (this.EVENT_LOCK) {
/* 420 */       return this.events.size();
/*     */     }
/*     */   }
/*     */ 
/*     */   private int embedCompletely() {
/* 425 */     xembedLog.fine("Embedding completely");
/* 426 */     int i = getEventPos();
/* 427 */     embed();
/* 428 */     waitEmbeddedNotify(i);
/* 429 */     return i;
/*     */   }
/*     */   private int requestFocus() {
/* 432 */     xembedLog.fine("Requesting focus");
/* 433 */     int i = getEventPos();
/* 434 */     sendMessage(3);
/* 435 */     waitFocusGained(i);
/* 436 */     return i;
/*     */   }
/*     */   private int requestFocusNoWait() {
/* 439 */     xembedLog.fine("Requesting focus without wait");
/* 440 */     int i = getEventPos();
/* 441 */     sendMessage(3);
/* 442 */     return i;
/*     */   }
/*     */   private int activateServer(int paramInt) {
/* 445 */     int i = activateServerNoWait(paramInt);
/* 446 */     waitWindowActivated(i);
/* 447 */     return i;
/*     */   }
/*     */   private int activateServerNoWait(int paramInt) {
/* 450 */     xembedLog.fine("Activating server");
/* 451 */     int i = getEventPos();
/* 452 */     if (checkEventList(paramInt, 1) != -1) {
/* 453 */       xembedLog.fine("Activation already received");
/* 454 */       return i;
/*     */     }
/* 456 */     Point localPoint = this.serverBounds[0].getLocation();
/*     */     Point tmp44_43 = localPoint; tmp44_43.x = ((int)(tmp44_43.x + this.serverBounds[0].getWidth() / 2.0D));
/* 458 */     localPoint.y += 5;
/* 459 */     this.robot.mouseMove(localPoint.x, localPoint.y);
/* 460 */     this.robot.mousePress(16);
/* 461 */     this.robot.mouseRelease(16);
/* 462 */     return i;
/*     */   }
/*     */   private int deactivateServer() {
/* 465 */     xembedLog.fine("Deactivating server");
/* 466 */     int i = getEventPos();
/* 467 */     Point localPoint = this.serverBounds[1].getLocation();
/*     */     Point tmp24_23 = localPoint; tmp24_23.x = ((int)(tmp24_23.x + this.serverBounds[1].getWidth() / 2.0D));
/*     */     Point tmp48_47 = localPoint; tmp48_47.y = ((int)(tmp48_47.y + this.serverBounds[1].getHeight() / 2.0D));
/* 470 */     this.robot.mouseMove(localPoint.x, localPoint.y);
/* 471 */     this.robot.mousePress(16);
/* 472 */     this.robot.delay(50);
/* 473 */     this.robot.mouseRelease(16);
/* 474 */     waitWindowDeactivated(i);
/* 475 */     return i;
/*     */   }
/*     */   private int focusServer() {
/* 478 */     xembedLog.fine("Focusing server");
/* 479 */     boolean bool = this.focused;
/* 480 */     int i = getEventPos();
/* 481 */     Point localPoint = this.serverBounds[2].getLocation();
/* 482 */     localPoint.x += 5;
/* 483 */     localPoint.y += 5;
/* 484 */     this.robot.mouseMove(localPoint.x, localPoint.y);
/* 485 */     this.robot.mousePress(16);
/* 486 */     this.robot.delay(50);
/* 487 */     this.robot.mouseRelease(16);
/* 488 */     if (bool) {
/* 489 */       waitFocusLost(i);
/*     */     }
/* 491 */     return i;
/*     */   }
/*     */   private int focusServerNext() {
/* 494 */     xembedLog.fine("Focusing next server component");
/* 495 */     int i = getEventPos();
/* 496 */     sendMessage(6);
/* 497 */     waitFocusLost(i);
/* 498 */     return i;
/*     */   }
/*     */   private int focusServerPrev() {
/* 501 */     xembedLog.fine("Focusing previous server component");
/* 502 */     int i = getEventPos();
/* 503 */     sendMessage(7);
/* 504 */     waitFocusLost(i);
/* 505 */     return i;
/*     */   }
/*     */ 
/*     */   private void waitEmbeddedNotify(int paramInt) {
/* 509 */     waitForEvent(paramInt, 0);
/*     */   }
/*     */   private void waitFocusGained(int paramInt) {
/* 512 */     waitForEvent(paramInt, 4);
/*     */   }
/*     */   private void waitFocusLost(int paramInt) {
/* 515 */     waitForEvent(paramInt, 5);
/*     */   }
/*     */   private void waitWindowActivated(int paramInt) {
/* 518 */     waitForEvent(paramInt, 1);
/*     */   }
/*     */   private void waitWindowDeactivated(int paramInt) {
/* 521 */     waitForEvent(paramInt, 2);
/*     */   }
/*     */ 
/*     */   private void waitSystemEvent(int paramInt1, int paramInt2) {
/* 525 */     waitForEvent(paramInt1, paramInt2 | 0x8000);
/*     */   }
/*     */ 
/*     */   private void waitForEvent(int paramInt1, int paramInt2) {
/* 529 */     synchronized (this.EVENT_LOCK)
/*     */     {
/* 531 */       if (checkEventList(paramInt1, paramInt2) != -1) {
/* 532 */         xembedLog.finer("The event " + XEmbedHelper.msgidToString(paramInt2) + " has already been received");
/* 533 */         return;
/*     */       }
/*     */ 
/* 536 */       if (this.eventReceived == paramInt2)
/*     */       {
/* 538 */         xembedLog.finer("Already received " + XEmbedHelper.msgidToString(paramInt2));
/* 539 */         return;
/*     */       }
/* 541 */       this.eventReceived = -1;
/* 542 */       this.eventWaited = paramInt2;
/* 543 */       xembedLog.finer("Waiting for " + XEmbedHelper.msgidToString(paramInt2) + " starting from " + paramInt1);
/*     */       try {
/* 545 */         this.EVENT_LOCK.wait(3000L);
/*     */       } catch (InterruptedException localInterruptedException) {
/* 547 */         xembedLog.warning("Event wait interrupted", localInterruptedException);
/*     */       }
/* 549 */       this.eventWaited = -1;
/* 550 */       if (checkEventList(paramInt1, paramInt2) == -1) {
/* 551 */         dumpReceivedEvents();
/* 552 */         throw new RuntimeException("Didn't receive event " + XEmbedHelper.msgidToString(paramInt2) + " but recevied " + XEmbedHelper.msgidToString(this.eventReceived));
/*     */       }
/* 554 */       xembedLog.finer("Successfully recevied " + XEmbedHelper.msgidToString(paramInt2));
/*     */     }
/*     */   }
/*     */ 
/*     */   private int checkEventList(int paramInt1, int paramInt2)
/*     */   {
/* 562 */     if (paramInt1 == -1) {
/* 563 */       return -1;
/*     */     }
/* 565 */     synchronized (this.EVENT_LOCK) {
/* 566 */       for (int i = paramInt1; i < this.events.size(); i++) {
/* 567 */         if (((Integer)this.events.get(i)).intValue() == paramInt2) {
/* 568 */           return i;
/*     */         }
/*     */       }
/* 571 */       return -1;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void checkFocusedServerNext() {
/* 576 */     if (this.focusedServerComponent != 0)
/* 577 */       throw new RuntimeException("Wrong focused server component, should be 0, but it is " + this.focusedServerComponent);
/*     */   }
/*     */ 
/*     */   private void checkFocusedServerPrev() {
/* 581 */     if (this.focusedServerComponent != 2)
/* 582 */       throw new RuntimeException("Wrong focused server component, should be 2, but it is " + this.focusedServerComponent);
/*     */   }
/*     */ 
/*     */   private void checkFocusGained(int paramInt) {
/* 586 */     if (!this.focused) {
/* 587 */       throw new RuntimeException("Didn't receive FOCUS_GAINED");
/*     */     }
/* 589 */     if (this.focusedKind != paramInt)
/* 590 */       throw new RuntimeException("Kinds don't match, required: " + paramInt + ", current: " + this.focusedKind);
/*     */   }
/*     */ 
/*     */   private void checkNotFocused() {
/* 594 */     if (this.focused)
/* 595 */       throw new RuntimeException("Focused");
/*     */   }
/*     */ 
/*     */   private void checkFocused() {
/* 599 */     if (!this.focused)
/* 600 */       throw new RuntimeException("Not Focused");
/*     */   }
/*     */ 
/*     */   private void checkFocusLost()
/*     */   {
/* 605 */     checkNotFocused();
/* 606 */     if (this.focusedKind != 5)
/* 607 */       throw new RuntimeException("Didn't receive FOCUS_LOST");
/*     */   }
/*     */ 
/*     */   private void checkWindowActivated() {
/* 611 */     if (!this.windowActive)
/* 612 */       throw new RuntimeException("Window is not active");
/*     */   }
/*     */ 
/*     */   private void checkMapped() {
/* 616 */     if (XlibUtil.getWindowMapState(this.window.getWindow()) == 0)
/* 617 */       throw new RuntimeException("Client is not mapped");
/*     */   }
/*     */ 
/*     */   private void checkNotMapped() {
/* 621 */     if (XlibUtil.getWindowMapState(this.window.getWindow()) != 0)
/* 622 */       throw new RuntimeException("Client is mapped");
/*     */   }
/*     */ 
/*     */   private void sendMessage(int paramInt)
/*     */   {
/* 627 */     this.xembed.sendMessage(this.parent, paramInt);
/*     */   }
/*     */   private void sendMessage(int paramInt1, int paramInt2, long paramLong1, long paramLong2) {
/* 630 */     this.xembed.sendMessage(this.parent, paramInt1, paramInt2, paramLong1, paramLong2);
/*     */   }
/*     */ 
/*     */   public void dispatchEvent(XEvent paramXEvent) {
/* 634 */     if (paramXEvent.get_type() == 33) {
/* 635 */       XClientMessageEvent localXClientMessageEvent = paramXEvent.get_xclient();
/* 636 */       if (localXClientMessageEvent.get_message_type() == XEmbedHelper.XEmbed.getAtom()) {
/* 637 */         if (xembedLog.isLoggable(500)) xembedLog.fine("Embedded message: " + XEmbedHelper.msgidToString((int)localXClientMessageEvent.get_data(1)));
/* 638 */         switch ((int)localXClientMessageEvent.get_data(1)) {
/*     */         case 0:
/* 640 */           this.xembedActive = true;
/* 641 */           this.server_version = ((int)localXClientMessageEvent.get_data(3));
/* 642 */           break;
/*     */         case 1:
/* 644 */           this.windowActive = true;
/* 645 */           break;
/*     */         case 2:
/* 647 */           this.windowActive = false;
/* 648 */           break;
/*     */         case 4:
/* 650 */           this.focused = true;
/* 651 */           this.focusedKind = ((int)localXClientMessageEvent.get_data(2));
/* 652 */           break;
/*     */         case 5:
/* 654 */           this.focused = false;
/* 655 */           this.focusedKind = 5;
/* 656 */           this.focusedServerComponent = ((int)localXClientMessageEvent.get_data(2));
/*     */         case 3:
/*     */         }
/* 659 */         synchronized (this.EVENT_LOCK) {
/* 660 */           this.events.add(Integer.valueOf((int)localXClientMessageEvent.get_data(1)));
/*     */ 
/* 662 */           xembedLog.finer("Tester is waiting for " + XEmbedHelper.msgidToString(this.eventWaited));
/* 663 */           if ((int)localXClientMessageEvent.get_data(1) == this.eventWaited) {
/* 664 */             this.eventReceived = ((int)localXClientMessageEvent.get_data(1));
/* 665 */             xembedLog.finer("Notifying waiting object for event " + System.identityHashCode(this.EVENT_LOCK));
/* 666 */             this.EVENT_LOCK.notifyAll();
/*     */           }
/*     */         }
/*     */       }
/*     */     } else {
/* 671 */       synchronized (this.EVENT_LOCK) {
/* 672 */         int i = paramXEvent.get_type() | 0x8000;
/* 673 */         this.events.add(Integer.valueOf(i));
/*     */ 
/* 675 */         xembedLog.finer("Tester is waiting for " + XEmbedHelper.msgidToString(this.eventWaited) + ", but we received " + paramXEvent + "(" + XEmbedHelper.msgidToString(i) + ")");
/* 676 */         if (i == this.eventWaited) {
/* 677 */           this.eventReceived = i;
/* 678 */           xembedLog.finer("Notifying waiting object" + System.identityHashCode(this.EVENT_LOCK));
/* 679 */           this.EVENT_LOCK.notifyAll();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void sleep(int paramInt) {
/*     */     try {
/* 687 */       Thread.sleep(paramInt);
/*     */     } catch (Exception localException) {
/*     */     }
/*     */   }
/*     */ 
/*     */   private void registerAccelerator() {
/* 693 */     sendMessage(12, 1, this.accel_keysym, this.accel_mods);
/*     */   }
/*     */ 
/*     */   private void unregisterAccelerator() {
/* 697 */     sendMessage(13, 1, 0L, 0L);
/*     */   }
/*     */ 
/*     */   private int pressAccelKey() {
/* 701 */     int i = getEventPos();
/* 702 */     this.robot.keyPress(this.accel_key);
/* 703 */     this.robot.keyRelease(this.accel_key);
/* 704 */     return i;
/*     */   }
/*     */ 
/*     */   private void initAccel() {
/* 708 */     this.accel_key = 65;
/* 709 */     this.accel_keysym = XWindow.getKeySymForAWTKeyCode(this.accel_key);
/* 710 */     this.accel_mods = 0;
/*     */   }
/*     */ 
/*     */   private void grabKey() {
/* 714 */     sendMessage(108, 0, this.accel_keysym, this.accel_mods);
/*     */   }
/*     */   private void ungrabKey() {
/* 717 */     sendMessage(109, 0, this.accel_keysym, this.accel_mods);
/*     */   }
/*     */   private int showModalDialog() {
/* 720 */     xembedLog.fine("Showing modal dialog");
/* 721 */     int i = getEventPos();
/* 722 */     Point localPoint = this.serverBounds[3].getLocation();
/* 723 */     localPoint.x += 5;
/* 724 */     localPoint.y += 5;
/* 725 */     this.robot.mouseMove(localPoint.x, localPoint.y);
/* 726 */     this.robot.mousePress(16);
/* 727 */     this.robot.delay(50);
/* 728 */     this.robot.mouseRelease(16);
/* 729 */     return i;
/*     */   }
/*     */   private int hideModalDialog() {
/* 732 */     xembedLog.fine("Hide modal dialog");
/* 733 */     int i = getEventPos();
/*     */ 
/* 741 */     this.robot.keyPress(32);
/* 742 */     this.robot.keyRelease(32);
/* 743 */     return i;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XEmbedServerTester
 * JD-Core Version:    0.6.2
 */