/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.awt.Component;
/*     */ import java.awt.Cursor;
/*     */ import java.awt.Window;
/*     */ import java.awt.datatransfer.Transferable;
/*     */ import java.awt.dnd.DragGestureEvent;
/*     */ import java.awt.dnd.DragSourceContext;
/*     */ import java.awt.dnd.InvalidDnDOperationException;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import sun.awt.AWTAccessor;
/*     */ import sun.awt.AWTAccessor.ComponentAccessor;
/*     */ import sun.awt.dnd.SunDragSourceContextPeer;
/*     */ import sun.awt.dnd.SunDropTargetContextPeer;
/*     */ import sun.util.logging.PlatformLogger;
/*     */ 
/*     */ public final class XDragSourceContextPeer extends SunDragSourceContextPeer
/*     */   implements XDragSourceProtocolListener
/*     */ {
/*  55 */   private static final PlatformLogger logger = PlatformLogger.getLogger("sun.awt.X11.xembed.xdnd.XDragSourceContextPeer");
/*     */   private static final int ROOT_EVENT_MASK = 8195;
/*     */   private static final int GRAB_EVENT_MASK = 8204;
/*  66 */   private long rootEventMask = 0L;
/*  67 */   private boolean dndInProgress = false;
/*  68 */   private boolean dragInProgress = false;
/*  69 */   private long dragRootWindow = 0L;
/*     */ 
/*  72 */   private XDragSourceProtocol dragProtocol = null;
/*     */ 
/*  74 */   private int targetAction = 0;
/*     */ 
/*  76 */   private int sourceActions = 0;
/*     */ 
/*  79 */   private int sourceAction = 0;
/*     */ 
/*  82 */   private long[] sourceFormats = null;
/*     */ 
/*  84 */   private long targetRootSubwindow = 0L;
/*     */ 
/*  86 */   private int xRoot = 0;
/*  87 */   private int yRoot = 0;
/*     */ 
/*  89 */   private int eventState = 0;
/*     */ 
/*  92 */   private long proxyModeSourceWindow = 0L;
/*     */ 
/*  95 */   private static final XDragSourceContextPeer theInstance = new XDragSourceContextPeer(null);
/*     */ 
/*     */   private XDragSourceContextPeer(DragGestureEvent paramDragGestureEvent)
/*     */   {
/*  99 */     super(paramDragGestureEvent);
/*     */   }
/*     */ 
/*     */   static XDragSourceProtocolListener getXDragSourceProtocolListener() {
/* 103 */     return theInstance;
/*     */   }
/*     */ 
/*     */   static XDragSourceContextPeer createDragSourceContextPeer(DragGestureEvent paramDragGestureEvent) throws InvalidDnDOperationException
/*     */   {
/* 108 */     theInstance.setTrigger(paramDragGestureEvent);
/* 109 */     return theInstance;
/*     */   }
/*     */ 
/*     */   protected void startDrag(Transferable paramTransferable, long[] paramArrayOfLong, Map paramMap)
/*     */   {
/* 114 */     Component localComponent = getTrigger().getComponent();
/* 115 */     Object localObject1 = null;
/* 116 */     XWindowPeer localXWindowPeer = null;
/*     */ 
/* 118 */     for (localObject1 = localComponent; (localObject1 != null) && (!(localObject1 instanceof Window)); ) {
/* 119 */       localObject1 = AWTAccessor.getComponentAccessor().getParent((Component)localObject1);
/*     */     }
/* 121 */     if ((localObject1 instanceof Window)) {
/* 122 */       localXWindowPeer = (XWindowPeer)((Component)localObject1).getPeer();
/*     */     }
/*     */ 
/* 125 */     if (localXWindowPeer == null) {
/* 126 */       throw new InvalidDnDOperationException("Cannot find top-level for the drag source component");
/*     */     }
/*     */ 
/* 130 */     long l1 = 0L;
/* 131 */     long l2 = 0L;
/* 132 */     long l3 = 0L;
/* 133 */     long l4 = 0L;
/*     */ 
/* 137 */     Cursor localCursor = getCursor();
/* 138 */     if (localCursor != null) {
/* 139 */       l1 = XGlobalCursorManager.getCursor(localCursor);
/*     */     }
/*     */ 
/* 143 */     XToolkit.awtLock();
/*     */     try {
/* 145 */       if (this.proxyModeSourceWindow != 0L) {
/* 146 */         throw new InvalidDnDOperationException("Proxy drag in progress");
/*     */       }
/* 148 */       if (this.dndInProgress) {
/* 149 */         throw new InvalidDnDOperationException("Drag in progress");
/*     */       }
/*     */ 
/* 154 */       long l5 = XlibWrapper.XScreenNumberOfScreen(localXWindowPeer.getScreen());
/* 155 */       l2 = XlibWrapper.RootWindow(XToolkit.getDisplay(), l5);
/*     */ 
/* 158 */       l3 = XWindow.getXAWTRootWindow().getWindow();
/*     */ 
/* 160 */       l4 = XToolkit.getCurrentServerTime();
/*     */ 
/* 162 */       int i = getDragSourceContext().getSourceActions();
/*     */ 
/* 164 */       Iterator localIterator = XDragAndDropProtocols.getDragSourceProtocols();
/* 165 */       while (localIterator.hasNext()) {
/* 166 */         XDragSourceProtocol localXDragSourceProtocol = (XDragSourceProtocol)localIterator.next();
/*     */         try {
/* 168 */           localXDragSourceProtocol.initializeDrag(i, paramTransferable, paramMap, paramArrayOfLong);
/*     */         }
/*     */         catch (XException localXException) {
/* 171 */           throw ((InvalidDnDOperationException)new InvalidDnDOperationException().initCause(localXException));
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 179 */       XWindowAttributes localXWindowAttributes = new XWindowAttributes();
/*     */       try {
/* 181 */         j = XlibWrapper.XGetWindowAttributes(XToolkit.getDisplay(), l2, localXWindowAttributes.pData);
/*     */ 
/* 184 */         if (j == 0) {
/* 185 */           throw new InvalidDnDOperationException("XGetWindowAttributes failed");
/*     */         }
/*     */ 
/* 188 */         this.rootEventMask = localXWindowAttributes.get_your_event_mask();
/*     */ 
/* 190 */         XlibWrapper.XSelectInput(XToolkit.getDisplay(), l2, this.rootEventMask | 0x2003);
/*     */       }
/*     */       finally {
/* 193 */         localXWindowAttributes.dispose();
/*     */       }
/*     */ 
/* 196 */       XBaseWindow.ungrabInput();
/*     */ 
/* 198 */       int j = XlibWrapper.XGrabPointer(XToolkit.getDisplay(), l2, 0, 8204, 1, 1, 0L, l1, l4);
/*     */ 
/* 204 */       if (j != 0) { cleanup(l4);
/* 206 */         throwGrabFailureException("Cannot grab pointer", j);
/*     */         return;
/*     */       }
/* 210 */       j = XlibWrapper.XGrabKeyboard(XToolkit.getDisplay(), l2, 0, 1, 1, l4);
/*     */ 
/* 216 */       if (j != 0)
/*     */       {
/* 217 */         cleanup(l4);
/* 218 */         throwGrabFailureException("Cannot grab keyboard", j);
/*     */         return;
/*     */       }
/* 224 */       this.dndInProgress = true;
/* 225 */       this.dragInProgress = true;
/* 226 */       this.dragRootWindow = l2;
/* 227 */       this.sourceActions = i;
/* 228 */       this.sourceFormats = paramArrayOfLong;
/*     */     } finally {
/* 230 */       XToolkit.awtUnlock();
/*     */     }
/*     */ 
/* 234 */     setNativeContext(0L);
/*     */ 
/* 236 */     SunDropTargetContextPeer.setCurrentJVMLocalSourceTransferable(paramTransferable);
/*     */   }
/*     */ 
/*     */   public long getProxyModeSourceWindow() {
/* 240 */     return this.proxyModeSourceWindow;
/*     */   }
/*     */ 
/*     */   private void setProxyModeSourceWindowImpl(long paramLong) {
/* 244 */     this.proxyModeSourceWindow = paramLong;
/*     */   }
/*     */ 
/*     */   public static void setProxyModeSourceWindow(long paramLong) {
/* 248 */     theInstance.setProxyModeSourceWindowImpl(paramLong);
/*     */   }
/*     */ 
/*     */   public void setCursor(Cursor paramCursor)
/*     */     throws InvalidDnDOperationException
/*     */   {
/* 256 */     XToolkit.awtLock();
/*     */     try {
/* 258 */       super.setCursor(paramCursor);
/*     */     } finally {
/* 260 */       XToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void setNativeCursor(long paramLong, Cursor paramCursor, int paramInt) {
/* 265 */     assert (XToolkit.isAWTLockHeldByCurrentThread());
/*     */ 
/* 267 */     if (paramCursor == null) {
/* 268 */       return;
/*     */     }
/*     */ 
/* 271 */     long l = XGlobalCursorManager.getCursor(paramCursor);
/*     */ 
/* 273 */     if (l == 0L) {
/* 274 */       return;
/*     */     }
/*     */ 
/* 277 */     XlibWrapper.XChangeActivePointerGrab(XToolkit.getDisplay(), 8204, l, 0L);
/*     */   }
/*     */ 
/*     */   protected boolean needsBogusExitBeforeDrop()
/*     */   {
/* 284 */     return false;
/*     */   }
/*     */ 
/*     */   private void throwGrabFailureException(String paramString, int paramInt) throws InvalidDnDOperationException
/*     */   {
/* 289 */     String str = "";
/* 290 */     switch (paramInt) { case 3:
/* 291 */       str = "not viewable"; break;
/*     */     case 1:
/* 292 */       str = "already grabbed"; break;
/*     */     case 2:
/* 293 */       str = "invalid time"; break;
/*     */     case 4:
/* 294 */       str = "grab frozen"; break;
/*     */     default:
/* 295 */       str = "unknown failure";
/*     */     }
/* 297 */     throw new InvalidDnDOperationException(paramString + ": " + str);
/*     */   }
/*     */ 
/*     */   public void cleanup(long paramLong)
/*     */   {
/* 304 */     if (this.dndInProgress) {
/* 305 */       if (this.dragProtocol != null) {
/* 306 */         this.dragProtocol.sendLeaveMessage(paramLong);
/*     */       }
/*     */ 
/* 309 */       if (this.targetAction != 0) {
/* 310 */         dragExit(this.xRoot, this.yRoot);
/*     */       }
/*     */ 
/* 313 */       dragDropFinished(false, 0, this.xRoot, this.yRoot);
/*     */     }
/*     */ 
/* 316 */     Iterator localIterator = XDragAndDropProtocols.getDragSourceProtocols();
/* 317 */     while (localIterator.hasNext()) {
/* 318 */       XDragSourceProtocol localXDragSourceProtocol = (XDragSourceProtocol)localIterator.next();
/*     */       try {
/* 320 */         localXDragSourceProtocol.cleanup();
/*     */       }
/*     */       catch (XException localXException)
/*     */       {
/*     */       }
/*     */     }
/* 326 */     this.dndInProgress = false;
/* 327 */     this.dragInProgress = false;
/* 328 */     this.dragRootWindow = 0L;
/* 329 */     this.sourceFormats = null;
/* 330 */     this.sourceActions = 0;
/* 331 */     this.sourceAction = 0;
/* 332 */     this.eventState = 0;
/* 333 */     this.xRoot = 0;
/* 334 */     this.yRoot = 0;
/*     */ 
/* 336 */     cleanupTargetInfo();
/*     */ 
/* 338 */     removeDnDGrab(paramLong);
/*     */   }
/*     */ 
/*     */   private void cleanupTargetInfo()
/*     */   {
/* 345 */     this.targetAction = 0;
/* 346 */     this.dragProtocol = null;
/* 347 */     this.targetRootSubwindow = 0L;
/*     */   }
/*     */ 
/*     */   private void removeDnDGrab(long paramLong) {
/* 351 */     assert (XToolkit.isAWTLockHeldByCurrentThread());
/*     */ 
/* 353 */     XlibWrapper.XUngrabPointer(XToolkit.getDisplay(), paramLong);
/* 354 */     XlibWrapper.XUngrabKeyboard(XToolkit.getDisplay(), paramLong);
/*     */ 
/* 357 */     if (((this.rootEventMask | 0x2003) != this.rootEventMask) && (this.dragRootWindow != 0L))
/*     */     {
/* 360 */       XlibWrapper.XSelectInput(XToolkit.getDisplay(), this.dragRootWindow, this.rootEventMask);
/*     */     }
/*     */ 
/* 365 */     this.rootEventMask = 0L;
/* 366 */     this.dragRootWindow = 0L;
/*     */   }
/*     */ 
/*     */   private boolean processClientMessage(XClientMessageEvent paramXClientMessageEvent) {
/* 370 */     if (this.dragProtocol != null) {
/* 371 */       return this.dragProtocol.processClientMessage(paramXClientMessageEvent);
/*     */     }
/* 373 */     return false;
/*     */   }
/*     */ 
/*     */   private boolean updateSourceAction(int paramInt)
/*     */   {
/* 382 */     int i = SunDragSourceContextPeer.convertModifiersToDropAction(XWindow.getModifiers(paramInt, 0, 0), this.sourceActions);
/*     */ 
/* 384 */     if (this.sourceAction == i) {
/* 385 */       return false;
/*     */     }
/* 387 */     this.sourceAction = i;
/* 388 */     return true;
/*     */   }
/*     */ 
/*     */   private static long findClientWindow(long paramLong)
/*     */   {
/* 395 */     if (XlibUtil.isTrueToplevelWindow(paramLong)) {
/* 396 */       return paramLong;
/*     */     }
/*     */ 
/* 399 */     Set localSet = XlibUtil.getChildWindows(paramLong);
/* 400 */     for (Long localLong : localSet) {
/* 401 */       long l = findClientWindow(localLong.longValue());
/* 402 */       if (l != 0L) {
/* 403 */         return l;
/*     */       }
/*     */     }
/*     */ 
/* 407 */     return 0L;
/*     */   }
/*     */ 
/*     */   private void doUpdateTargetWindow(long paramLong1, long paramLong2) {
/* 411 */     long l1 = 0L;
/* 412 */     long l2 = 0L;
/* 413 */     Object localObject = null;
/* 414 */     int i = 0;
/*     */ 
/* 416 */     if (paramLong1 != 0L) {
/* 417 */       l1 = findClientWindow(paramLong1);
/*     */     }
/*     */ 
/* 420 */     if (l1 != 0L) {
/* 421 */       Iterator localIterator = XDragAndDropProtocols.getDragSourceProtocols();
/* 422 */       while (localIterator.hasNext()) {
/* 423 */         XDragSourceProtocol localXDragSourceProtocol = (XDragSourceProtocol)localIterator.next();
/* 424 */         if (localXDragSourceProtocol.attachTargetWindow(l1, paramLong2)) {
/* 425 */           localObject = localXDragSourceProtocol;
/* 426 */           break;
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 432 */     this.dragProtocol = localObject;
/* 433 */     this.targetAction = 0;
/* 434 */     this.targetRootSubwindow = paramLong1;
/*     */   }
/*     */ 
/*     */   private void updateTargetWindow(XMotionEvent paramXMotionEvent) {
/* 438 */     assert (XToolkit.isAWTLockHeldByCurrentThread());
/*     */ 
/* 440 */     int i = paramXMotionEvent.get_x_root();
/* 441 */     int j = paramXMotionEvent.get_y_root();
/* 442 */     long l1 = paramXMotionEvent.get_time();
/* 443 */     long l2 = paramXMotionEvent.get_subwindow();
/*     */ 
/* 449 */     if (paramXMotionEvent.get_window() != paramXMotionEvent.get_root()) {
/* 450 */       XlibWrapper.XQueryPointer(XToolkit.getDisplay(), paramXMotionEvent.get_root(), XlibWrapper.larg1, XlibWrapper.larg2, XlibWrapper.larg3, XlibWrapper.larg4, XlibWrapper.larg5, XlibWrapper.larg6, XlibWrapper.larg7);
/*     */ 
/* 459 */       l2 = Native.getLong(XlibWrapper.larg2);
/*     */     }
/*     */ 
/* 462 */     if (this.targetRootSubwindow != l2) {
/* 463 */       if (this.dragProtocol != null) {
/* 464 */         this.dragProtocol.sendLeaveMessage(l1);
/*     */ 
/* 472 */         if (this.targetAction != 0) {
/* 473 */           dragExit(i, j);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 478 */       doUpdateTargetWindow(l2, l1);
/*     */ 
/* 480 */       if (this.dragProtocol != null)
/* 481 */         this.dragProtocol.sendEnterMessage(this.sourceFormats, this.sourceAction, this.sourceActions, l1);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void processMouseMove(XMotionEvent paramXMotionEvent)
/*     */   {
/* 494 */     if (!this.dragInProgress) {
/* 495 */       return;
/*     */     }
/* 497 */     if ((this.xRoot != paramXMotionEvent.get_x_root()) || (this.yRoot != paramXMotionEvent.get_y_root())) {
/* 498 */       this.xRoot = paramXMotionEvent.get_x_root();
/* 499 */       this.yRoot = paramXMotionEvent.get_y_root();
/*     */ 
/* 501 */       postDragSourceDragEvent(this.targetAction, XWindow.getModifiers(paramXMotionEvent.get_state(), 0, 0), this.xRoot, this.yRoot, 6);
/*     */     }
/*     */ 
/* 506 */     if (this.eventState != paramXMotionEvent.get_state()) {
/* 507 */       if ((updateSourceAction(paramXMotionEvent.get_state())) && (this.dragProtocol != null)) {
/* 508 */         postDragSourceDragEvent(this.targetAction, XWindow.getModifiers(paramXMotionEvent.get_state(), 0, 0), this.xRoot, this.yRoot, 3);
/*     */       }
/*     */ 
/* 512 */       this.eventState = paramXMotionEvent.get_state();
/*     */     }
/*     */ 
/* 515 */     updateTargetWindow(paramXMotionEvent);
/*     */ 
/* 517 */     if (this.dragProtocol != null)
/* 518 */       this.dragProtocol.sendMoveMessage(paramXMotionEvent.get_x_root(), paramXMotionEvent.get_y_root(), this.sourceAction, this.sourceActions, paramXMotionEvent.get_time());
/*     */   }
/*     */ 
/*     */   private void processDrop(XButtonEvent paramXButtonEvent)
/*     */   {
/*     */     try
/*     */     {
/* 527 */       this.dragProtocol.initiateDrop(paramXButtonEvent.get_x_root(), paramXButtonEvent.get_y_root(), this.sourceAction, this.sourceActions, paramXButtonEvent.get_time());
/*     */     }
/*     */     catch (XException localXException)
/*     */     {
/* 532 */       cleanup(paramXButtonEvent.get_time());
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean processProxyModeEvent(XEvent paramXEvent) {
/* 537 */     if (getProxyModeSourceWindow() == 0L) {
/* 538 */       return false;
/*     */     }
/*     */ 
/* 541 */     if (paramXEvent.get_type() != 33) {
/* 542 */       return false;
/*     */     }
/*     */ 
/* 545 */     if (logger.isLoggable(300)) {
/* 546 */       logger.finest("        proxyModeSourceWindow=" + getProxyModeSourceWindow() + " ev=" + paramXEvent);
/*     */     }
/*     */ 
/* 551 */     XClientMessageEvent localXClientMessageEvent = paramXEvent.get_xclient();
/*     */ 
/* 553 */     Iterator localIterator = XDragAndDropProtocols.getDragSourceProtocols();
/* 554 */     while (localIterator.hasNext()) {
/* 555 */       XDragSourceProtocol localXDragSourceProtocol = (XDragSourceProtocol)localIterator.next();
/*     */ 
/* 557 */       if (localXDragSourceProtocol.processProxyModeEvent(localXClientMessageEvent, getProxyModeSourceWindow()))
/*     */       {
/* 559 */         return true;
/*     */       }
/*     */     }
/*     */ 
/* 563 */     return false;
/*     */   }
/*     */ 
/*     */   private boolean doProcessEvent(XEvent paramXEvent)
/*     */   {
/* 572 */     assert (XToolkit.isAWTLockHeldByCurrentThread());
/*     */ 
/* 574 */     if (processProxyModeEvent(paramXEvent)) {
/* 575 */       return true;
/*     */     }
/*     */ 
/* 578 */     if (!this.dndInProgress)
/* 579 */       return false;
/*     */     Object localObject1;
/* 582 */     switch (paramXEvent.get_type()) {
/*     */     case 33:
/* 584 */       localObject1 = paramXEvent.get_xclient();
/* 585 */       return processClientMessage((XClientMessageEvent)localObject1);
/*     */     case 17:
/* 588 */       localObject1 = paramXEvent.get_xdestroywindow();
/*     */ 
/* 591 */       if ((!this.dragInProgress) && (this.dragProtocol != null) && (((XDestroyWindowEvent)localObject1).get_window() == this.dragProtocol.getTargetWindow()))
/*     */       {
/* 594 */         cleanup(0L);
/* 595 */         return true;
/*     */       }
/*     */ 
/* 598 */       return false;
/*     */     }
/*     */ 
/* 602 */     if (!this.dragInProgress) {
/* 603 */       return false;
/*     */     }
/*     */ 
/* 607 */     switch (paramXEvent.get_type()) {
/*     */     case 2:
/*     */     case 3:
/* 610 */       localObject1 = paramXEvent.get_xkey();
/* 611 */       long l = XlibWrapper.XKeycodeToKeysym(XToolkit.getDisplay(), ((XKeyEvent)localObject1).get_keycode(), 0);
/*     */ 
/* 613 */       switch ((int)l) {
/*     */       case 65307:
/* 615 */         if (paramXEvent.get_type() == 3)
/* 616 */           cleanup(((XKeyEvent)localObject1).get_time()); break;
/*     */       case 65505:
/*     */       case 65506:
/*     */       case 65507:
/*     */       case 65508:
/* 624 */         XlibWrapper.XQueryPointer(XToolkit.getDisplay(), ((XKeyEvent)localObject1).get_root(), XlibWrapper.larg1, XlibWrapper.larg2, XlibWrapper.larg3, XlibWrapper.larg4, XlibWrapper.larg5, XlibWrapper.larg6, XlibWrapper.larg7);
/*     */ 
/* 633 */         XMotionEvent localXMotionEvent2 = new XMotionEvent();
/*     */         try {
/* 635 */           localXMotionEvent2.set_type(6);
/* 636 */           localXMotionEvent2.set_serial(((XKeyEvent)localObject1).get_serial());
/* 637 */           localXMotionEvent2.set_send_event(((XKeyEvent)localObject1).get_send_event());
/* 638 */           localXMotionEvent2.set_display(((XKeyEvent)localObject1).get_display());
/* 639 */           localXMotionEvent2.set_window(((XKeyEvent)localObject1).get_window());
/* 640 */           localXMotionEvent2.set_root(((XKeyEvent)localObject1).get_root());
/* 641 */           localXMotionEvent2.set_subwindow(((XKeyEvent)localObject1).get_subwindow());
/* 642 */           localXMotionEvent2.set_time(((XKeyEvent)localObject1).get_time());
/* 643 */           localXMotionEvent2.set_x(((XKeyEvent)localObject1).get_x());
/* 644 */           localXMotionEvent2.set_y(((XKeyEvent)localObject1).get_y());
/* 645 */           localXMotionEvent2.set_x_root(((XKeyEvent)localObject1).get_x_root());
/* 646 */           localXMotionEvent2.set_y_root(((XKeyEvent)localObject1).get_y_root());
/* 647 */           localXMotionEvent2.set_state((int)Native.getLong(XlibWrapper.larg7));
/*     */ 
/* 650 */           localXMotionEvent2.set_same_screen(((XKeyEvent)localObject1).get_same_screen());
/*     */ 
/* 653 */           processMouseMove(localXMotionEvent2);
/*     */         } finally {
/* 655 */           localXMotionEvent2.dispose();
/*     */         }
/* 657 */         break;
/*     */       }
/*     */ 
/* 660 */       return true;
/*     */     case 4:
/* 663 */       return true;
/*     */     case 6:
/* 665 */       processMouseMove(paramXEvent.get_xmotion());
/* 666 */       return true;
/*     */     case 5:
/* 668 */       localObject1 = paramXEvent.get_xbutton();
/*     */ 
/* 674 */       if (((XButtonEvent)localObject1).get_button() > 20) {
/* 675 */         return true;
/*     */       }
/*     */ 
/* 683 */       XMotionEvent localXMotionEvent1 = new XMotionEvent();
/*     */       try {
/* 685 */         localXMotionEvent1.set_type(6);
/* 686 */         localXMotionEvent1.set_serial(((XButtonEvent)localObject1).get_serial());
/* 687 */         localXMotionEvent1.set_send_event(((XButtonEvent)localObject1).get_send_event());
/* 688 */         localXMotionEvent1.set_display(((XButtonEvent)localObject1).get_display());
/* 689 */         localXMotionEvent1.set_window(((XButtonEvent)localObject1).get_window());
/* 690 */         localXMotionEvent1.set_root(((XButtonEvent)localObject1).get_root());
/* 691 */         localXMotionEvent1.set_subwindow(((XButtonEvent)localObject1).get_subwindow());
/* 692 */         localXMotionEvent1.set_time(((XButtonEvent)localObject1).get_time());
/* 693 */         localXMotionEvent1.set_x(((XButtonEvent)localObject1).get_x());
/* 694 */         localXMotionEvent1.set_y(((XButtonEvent)localObject1).get_y());
/* 695 */         localXMotionEvent1.set_x_root(((XButtonEvent)localObject1).get_x_root());
/* 696 */         localXMotionEvent1.set_y_root(((XButtonEvent)localObject1).get_y_root());
/* 697 */         localXMotionEvent1.set_state(((XButtonEvent)localObject1).get_state());
/*     */ 
/* 700 */         localXMotionEvent1.set_same_screen(((XButtonEvent)localObject1).get_same_screen());
/*     */ 
/* 703 */         processMouseMove(localXMotionEvent1);
/*     */       } finally {
/* 705 */         localXMotionEvent1.dispose();
/*     */       }
/* 707 */       if ((((XButtonEvent)localObject1).get_button() == XConstants.buttons[0]) || (((XButtonEvent)localObject1).get_button() == XConstants.buttons[1]))
/*     */       {
/* 712 */         removeDnDGrab(((XButtonEvent)localObject1).get_time());
/* 713 */         this.dragInProgress = false;
/* 714 */         if ((this.dragProtocol != null) && (this.targetAction != 0))
/*     */         {
/* 721 */           processDrop((XButtonEvent)localObject1);
/*     */         }
/* 723 */         else cleanup(((XButtonEvent)localObject1).get_time());
/*     */       }
/*     */ 
/* 726 */       return true;
/*     */     }
/*     */ 
/* 730 */     return false;
/*     */   }
/*     */ 
/*     */   static boolean processEvent(XEvent paramXEvent) {
/* 734 */     XToolkit.awtLock();
/*     */     try
/*     */     {
/* 737 */       return theInstance.doProcessEvent(paramXEvent);
/*     */     } catch (XException localXException) {
/* 739 */       localXException.printStackTrace();
/* 740 */       return false;
/*     */     }
/*     */     finally {
/* 743 */       XToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void handleDragReply(int paramInt)
/*     */   {
/* 752 */     handleDragReply(paramInt, this.xRoot, this.yRoot);
/*     */   }
/*     */ 
/*     */   public void handleDragReply(int paramInt1, int paramInt2, int paramInt3)
/*     */   {
/* 758 */     handleDragReply(paramInt1, this.xRoot, this.yRoot, XWindow.getModifiers(this.eventState, 0, 0));
/*     */   }
/*     */ 
/*     */   public void handleDragReply(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/* 762 */     if ((paramInt1 == 0) && (this.targetAction != 0))
/*     */     {
/* 764 */       dragExit(paramInt2, paramInt3);
/* 765 */     } else if (paramInt1 != 0) {
/* 766 */       int i = 0;
/*     */ 
/* 768 */       if (this.targetAction == 0)
/* 769 */         i = 1;
/*     */       else {
/* 771 */         i = 2;
/*     */       }
/*     */ 
/* 775 */       postDragSourceDragEvent(paramInt1, paramInt4, paramInt2, paramInt3, i);
/*     */     }
/*     */ 
/* 778 */     this.targetAction = paramInt1;
/*     */   }
/*     */ 
/*     */   public void handleDragFinished()
/*     */   {
/* 783 */     handleDragFinished(true);
/*     */   }
/*     */ 
/*     */   public void handleDragFinished(boolean paramBoolean)
/*     */   {
/* 789 */     handleDragFinished(true, this.targetAction);
/*     */   }
/*     */ 
/*     */   public void handleDragFinished(boolean paramBoolean, int paramInt)
/*     */   {
/* 795 */     handleDragFinished(paramBoolean, paramInt, this.xRoot, this.yRoot);
/*     */   }
/*     */ 
/*     */   public void handleDragFinished(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3) {
/* 799 */     dragDropFinished(paramBoolean, paramInt1, paramInt2, paramInt3);
/*     */ 
/* 801 */     this.dndInProgress = false;
/* 802 */     cleanup(0L);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XDragSourceContextPeer
 * JD-Core Version:    0.6.2
 */