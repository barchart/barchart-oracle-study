/*     */ package sun.awt.dnd;
/*     */ 
/*     */ import java.awt.Component;
/*     */ import java.awt.Point;
/*     */ import java.awt.datatransfer.DataFlavor;
/*     */ import java.awt.datatransfer.Transferable;
/*     */ import java.awt.datatransfer.UnsupportedFlavorException;
/*     */ import java.awt.dnd.DropTarget;
/*     */ import java.awt.dnd.DropTargetContext;
/*     */ import java.awt.dnd.DropTargetDragEvent;
/*     */ import java.awt.dnd.DropTargetDropEvent;
/*     */ import java.awt.dnd.DropTargetEvent;
/*     */ import java.awt.dnd.DropTargetListener;
/*     */ import java.awt.dnd.InvalidDnDOperationException;
/*     */ import java.awt.dnd.peer.DropTargetContextPeer;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import sun.awt.AppContext;
/*     */ import sun.awt.SunToolkit;
/*     */ import sun.awt.datatransfer.DataTransferer;
/*     */ import sun.awt.datatransfer.ToolkitThreadBlockedHandler;
/*     */ import sun.util.logging.PlatformLogger;
/*     */ 
/*     */ public abstract class SunDropTargetContextPeer
/*     */   implements DropTargetContextPeer, Transferable
/*     */ {
/*     */   public static final boolean DISPATCH_SYNC = true;
/*     */   private DropTarget currentDT;
/*     */   private DropTargetContext currentDTC;
/*     */   private long[] currentT;
/*     */   private int currentA;
/*     */   private int currentSA;
/*     */   private int currentDA;
/*     */   private int previousDA;
/*     */   private long nativeDragContext;
/*     */   private Transferable local;
/*  92 */   private boolean dragRejected = false;
/*     */ 
/*  94 */   protected int dropStatus = 0;
/*  95 */   protected boolean dropComplete = false;
/*     */ 
/* 100 */   boolean dropInProcess = false;
/*     */ 
/* 106 */   protected static final Object _globalLock = new Object();
/*     */ 
/* 108 */   private static final PlatformLogger dndLog = PlatformLogger.getLogger("sun.awt.dnd.SunDropTargetContextPeer");
/*     */ 
/* 114 */   protected static Transferable currentJVMLocalSourceTransferable = null;
/*     */   protected static final int STATUS_NONE = 0;
/*     */   protected static final int STATUS_WAIT = 1;
/*     */   protected static final int STATUS_ACCEPT = 2;
/*     */   protected static final int STATUS_REJECT = -1;
/*     */ 
/*     */   public static void setCurrentJVMLocalSourceTransferable(Transferable paramTransferable)
/*     */     throws InvalidDnDOperationException
/*     */   {
/* 117 */     synchronized (_globalLock) {
/* 118 */       if ((paramTransferable != null) && (currentJVMLocalSourceTransferable != null)) {
/* 119 */         throw new InvalidDnDOperationException();
/*     */       }
/* 121 */       currentJVMLocalSourceTransferable = paramTransferable;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static Transferable getJVMLocalSourceTransferable()
/*     */   {
/* 131 */     return currentJVMLocalSourceTransferable;
/*     */   }
/*     */ 
/*     */   public DropTarget getDropTarget()
/*     */   {
/* 155 */     return this.currentDT;
/*     */   }
/*     */ 
/*     */   public synchronized void setTargetActions(int paramInt)
/*     */   {
/* 162 */     this.currentA = (paramInt & 0x40000003);
/*     */   }
/*     */ 
/*     */   public int getTargetActions()
/*     */   {
/* 171 */     return this.currentA;
/*     */   }
/*     */ 
/*     */   public Transferable getTransferable()
/*     */   {
/* 179 */     return this;
/*     */   }
/*     */ 
/*     */   public DataFlavor[] getTransferDataFlavors()
/*     */   {
/* 189 */     Transferable localTransferable = this.local;
/*     */ 
/* 191 */     if (localTransferable != null) {
/* 192 */       return localTransferable.getTransferDataFlavors();
/*     */     }
/* 194 */     return DataTransferer.getInstance().getFlavorsForFormatsAsArray(this.currentT, DataTransferer.adaptFlavorMap(this.currentDT.getFlavorMap()));
/*     */   }
/*     */ 
/*     */   public boolean isDataFlavorSupported(DataFlavor paramDataFlavor)
/*     */   {
/* 205 */     Transferable localTransferable = this.local;
/*     */ 
/* 207 */     if (localTransferable != null) {
/* 208 */       return localTransferable.isDataFlavorSupported(paramDataFlavor);
/*     */     }
/* 210 */     return DataTransferer.getInstance().getFlavorsForFormats(this.currentT, DataTransferer.adaptFlavorMap(this.currentDT.getFlavorMap())).containsKey(paramDataFlavor);
/*     */   }
/*     */ 
/*     */   public Object getTransferData(DataFlavor paramDataFlavor)
/*     */     throws UnsupportedFlavorException, IOException, InvalidDnDOperationException
/*     */   {
/* 226 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*     */     try {
/* 228 */       if ((!this.dropInProcess) && (localSecurityManager != null))
/* 229 */         localSecurityManager.checkSystemClipboardAccess();
/*     */     }
/*     */     catch (Exception localException) {
/* 232 */       localObject1 = Thread.currentThread();
/* 233 */       ((Thread)localObject1).getUncaughtExceptionHandler().uncaughtException((Thread)localObject1, localException);
/* 234 */       return null;
/*     */     }
/*     */ 
/* 237 */     Long localLong = null;
/* 238 */     Object localObject1 = this.local;
/*     */ 
/* 240 */     if (localObject1 != null) {
/* 241 */       return ((Transferable)localObject1).getTransferData(paramDataFlavor);
/*     */     }
/*     */ 
/* 244 */     if ((this.dropStatus != 2) || (this.dropComplete)) {
/* 245 */       throw new InvalidDnDOperationException("No drop current");
/*     */     }
/*     */ 
/* 248 */     Map localMap = DataTransferer.getInstance().getFlavorsForFormats(this.currentT, DataTransferer.adaptFlavorMap(this.currentDT.getFlavorMap()));
/*     */ 
/* 252 */     localLong = (Long)localMap.get(paramDataFlavor);
/* 253 */     if (localLong == null) {
/* 254 */       throw new UnsupportedFlavorException(paramDataFlavor);
/*     */     }
/*     */ 
/* 257 */     if ((paramDataFlavor.isRepresentationClassRemote()) && (this.currentDA != 1073741824))
/*     */     {
/* 259 */       throw new InvalidDnDOperationException("only ACTION_LINK is permissable for transfer of java.rmi.Remote objects");
/*     */     }
/*     */ 
/* 262 */     long l = localLong.longValue();
/* 263 */     Object localObject2 = getNativeData(l);
/*     */ 
/* 265 */     if ((localObject2 instanceof byte[]))
/*     */       try {
/* 267 */         return DataTransferer.getInstance().translateBytes((byte[])localObject2, paramDataFlavor, l, this);
/*     */       }
/*     */       catch (IOException localIOException1) {
/* 270 */         throw new InvalidDnDOperationException(localIOException1.getMessage());
/*     */       }
/* 272 */     if ((localObject2 instanceof InputStream)) {
/*     */       try {
/* 274 */         return DataTransferer.getInstance().translateStream((InputStream)localObject2, paramDataFlavor, l, this);
/*     */       }
/*     */       catch (IOException localIOException2) {
/* 277 */         throw new InvalidDnDOperationException(localIOException2.getMessage());
/*     */       }
/*     */     }
/* 280 */     throw new IOException("no native data was transfered");
/*     */   }
/*     */ 
/*     */   protected abstract Object getNativeData(long paramLong)
/*     */     throws IOException;
/*     */ 
/*     */   public boolean isTransferableJVMLocal()
/*     */   {
/* 291 */     return (this.local != null) || (getJVMLocalSourceTransferable() != null);
/*     */   }
/*     */ 
/*     */   private int handleEnterMessage(Component paramComponent, int paramInt1, int paramInt2, int paramInt3, int paramInt4, long[] paramArrayOfLong, long paramLong)
/*     */   {
/* 299 */     return postDropTargetEvent(paramComponent, paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfLong, paramLong, 504, true);
/*     */   }
/*     */ 
/*     */   protected void processEnterMessage(SunDropTargetEvent paramSunDropTargetEvent)
/*     */   {
/* 310 */     Component localComponent = (Component)paramSunDropTargetEvent.getSource();
/* 311 */     DropTarget localDropTarget = localComponent.getDropTarget();
/* 312 */     Point localPoint = paramSunDropTargetEvent.getPoint();
/*     */ 
/* 314 */     this.local = getJVMLocalSourceTransferable();
/*     */ 
/* 316 */     if (this.currentDTC != null) {
/* 317 */       this.currentDTC.removeNotify();
/* 318 */       this.currentDTC = null;
/*     */     }
/*     */ 
/* 321 */     if ((localComponent.isShowing()) && (localDropTarget != null) && (localDropTarget.isActive())) {
/* 322 */       this.currentDT = localDropTarget;
/* 323 */       this.currentDTC = this.currentDT.getDropTargetContext();
/*     */ 
/* 325 */       this.currentDTC.addNotify(this);
/*     */ 
/* 327 */       this.currentA = localDropTarget.getDefaultActions();
/*     */       try
/*     */       {
/* 330 */         localDropTarget.dragEnter(new DropTargetDragEvent(this.currentDTC, localPoint, this.currentDA, this.currentSA));
/*     */       }
/*     */       catch (Exception localException)
/*     */       {
/* 335 */         localException.printStackTrace();
/* 336 */         this.currentDA = 0;
/*     */       }
/*     */     } else {
/* 339 */       this.currentDT = null;
/* 340 */       this.currentDTC = null;
/* 341 */       this.currentDA = 0;
/* 342 */       this.currentSA = 0;
/* 343 */       this.currentA = 0;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void handleExitMessage(Component paramComponent, long paramLong)
/*     */   {
/* 358 */     postDropTargetEvent(paramComponent, 0, 0, 0, 0, null, paramLong, 505, true);
/*     */   }
/*     */ 
/*     */   protected void processExitMessage(SunDropTargetEvent paramSunDropTargetEvent)
/*     */   {
/* 369 */     Component localComponent = (Component)paramSunDropTargetEvent.getSource();
/* 370 */     DropTarget localDropTarget = localComponent.getDropTarget();
/* 371 */     DropTargetContext localDropTargetContext = null;
/*     */ 
/* 373 */     if (localDropTarget == null) {
/* 374 */       this.currentDT = null;
/* 375 */       this.currentT = null;
/*     */ 
/* 377 */       if (this.currentDTC != null) {
/* 378 */         this.currentDTC.removeNotify();
/*     */       }
/*     */ 
/* 381 */       this.currentDTC = null;
/*     */ 
/* 383 */       return;
/*     */     }
/*     */ 
/* 386 */     if (localDropTarget != this.currentDT)
/*     */     {
/* 388 */       if (this.currentDTC != null) {
/* 389 */         this.currentDTC.removeNotify();
/*     */       }
/*     */ 
/* 392 */       this.currentDT = localDropTarget;
/* 393 */       this.currentDTC = localDropTarget.getDropTargetContext();
/*     */ 
/* 395 */       this.currentDTC.addNotify(this);
/*     */     }
/*     */ 
/* 398 */     localDropTargetContext = this.currentDTC;
/*     */ 
/* 400 */     if (localDropTarget.isActive()) try {
/* 401 */         localDropTarget.dragExit(new DropTargetEvent(localDropTargetContext));
/*     */       } catch (Exception localException) {
/* 403 */         localException.printStackTrace();
/*     */       } finally {
/* 405 */         this.currentA = 0;
/* 406 */         this.currentSA = 0;
/* 407 */         this.currentDA = 0;
/* 408 */         this.currentDT = null;
/* 409 */         this.currentT = null;
/*     */ 
/* 411 */         this.currentDTC.removeNotify();
/* 412 */         this.currentDTC = null;
/*     */ 
/* 414 */         this.local = null;
/*     */ 
/* 416 */         this.dragRejected = false;
/*     */       }
/*     */   }
/*     */ 
/*     */   private int handleMotionMessage(Component paramComponent, int paramInt1, int paramInt2, int paramInt3, int paramInt4, long[] paramArrayOfLong, long paramLong)
/*     */   {
/* 425 */     return postDropTargetEvent(paramComponent, paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfLong, paramLong, 506, true);
/*     */   }
/*     */ 
/*     */   protected void processMotionMessage(SunDropTargetEvent paramSunDropTargetEvent, boolean paramBoolean)
/*     */   {
/* 437 */     Component localComponent = (Component)paramSunDropTargetEvent.getSource();
/* 438 */     Point localPoint = paramSunDropTargetEvent.getPoint();
/* 439 */     int i = paramSunDropTargetEvent.getID();
/* 440 */     DropTarget localDropTarget1 = localComponent.getDropTarget();
/* 441 */     DropTargetContext localDropTargetContext = null;
/*     */ 
/* 443 */     if ((localComponent.isShowing()) && (localDropTarget1 != null) && (localDropTarget1.isActive())) {
/* 444 */       if (this.currentDT != localDropTarget1) {
/* 445 */         if (this.currentDTC != null) {
/* 446 */           this.currentDTC.removeNotify();
/*     */         }
/*     */ 
/* 449 */         this.currentDT = localDropTarget1;
/* 450 */         this.currentDTC = null;
/*     */       }
/*     */ 
/* 453 */       localDropTargetContext = this.currentDT.getDropTargetContext();
/* 454 */       if (localDropTargetContext != this.currentDTC) {
/* 455 */         if (this.currentDTC != null) {
/* 456 */           this.currentDTC.removeNotify();
/*     */         }
/*     */ 
/* 459 */         this.currentDTC = localDropTargetContext;
/* 460 */         this.currentDTC.addNotify(this);
/*     */       }
/*     */ 
/* 463 */       this.currentA = this.currentDT.getDefaultActions();
/*     */       try
/*     */       {
/* 466 */         DropTargetDragEvent localDropTargetDragEvent = new DropTargetDragEvent(localDropTargetContext, localPoint, this.currentDA, this.currentSA);
/*     */ 
/* 470 */         DropTarget localDropTarget2 = localDropTarget1;
/* 471 */         if (paramBoolean)
/* 472 */           localDropTarget2.dropActionChanged(localDropTargetDragEvent);
/*     */         else {
/* 474 */           localDropTarget2.dragOver(localDropTargetDragEvent);
/*     */         }
/*     */ 
/* 477 */         if (this.dragRejected)
/* 478 */           this.currentDA = 0;
/*     */       }
/*     */       catch (Exception localException) {
/* 481 */         localException.printStackTrace();
/* 482 */         this.currentDA = 0;
/*     */       }
/*     */     } else {
/* 485 */       this.currentDA = 0;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void handleDropMessage(Component paramComponent, int paramInt1, int paramInt2, int paramInt3, int paramInt4, long[] paramArrayOfLong, long paramLong)
/*     */   {
/* 498 */     postDropTargetEvent(paramComponent, paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfLong, paramLong, 502, false);
/*     */   }
/*     */ 
/*     */   protected void processDropMessage(SunDropTargetEvent paramSunDropTargetEvent)
/*     */   {
/* 509 */     Component localComponent = (Component)paramSunDropTargetEvent.getSource();
/* 510 */     Point localPoint = paramSunDropTargetEvent.getPoint();
/* 511 */     DropTarget localDropTarget = localComponent.getDropTarget();
/*     */ 
/* 513 */     this.dropStatus = 1;
/* 514 */     this.dropComplete = false;
/*     */ 
/* 516 */     if ((localComponent.isShowing()) && (localDropTarget != null) && (localDropTarget.isActive())) {
/* 517 */       DropTargetContext localDropTargetContext = localDropTarget.getDropTargetContext();
/*     */ 
/* 519 */       this.currentDT = localDropTarget;
/*     */ 
/* 521 */       if (this.currentDTC != null) {
/* 522 */         this.currentDTC.removeNotify();
/*     */       }
/*     */ 
/* 525 */       this.currentDTC = localDropTargetContext;
/* 526 */       this.currentDTC.addNotify(this);
/* 527 */       this.currentA = localDropTarget.getDefaultActions();
/*     */ 
/* 529 */       synchronized (_globalLock) {
/* 530 */         if ((this.local = getJVMLocalSourceTransferable()) != null) {
/* 531 */           setCurrentJVMLocalSourceTransferable(null);
/*     */         }
/*     */       }
/* 534 */       this.dropInProcess = true;
/*     */       try
/*     */       {
/* 537 */         localDropTarget.drop(new DropTargetDropEvent(localDropTargetContext, localPoint, this.currentDA, this.currentSA, this.local != null));
/*     */       }
/*     */       finally
/*     */       {
/* 543 */         if (this.dropStatus == 1)
/* 544 */           rejectDrop();
/* 545 */         else if (!this.dropComplete) {
/* 546 */           dropComplete(false);
/*     */         }
/* 548 */         this.dropInProcess = false;
/*     */       }
/*     */     } else {
/* 551 */       rejectDrop();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected int postDropTargetEvent(Component paramComponent, int paramInt1, int paramInt2, int paramInt3, int paramInt4, long[] paramArrayOfLong, long paramLong, int paramInt5, boolean paramBoolean)
/*     */   {
/* 563 */     AppContext localAppContext = SunToolkit.targetToAppContext(paramComponent);
/*     */ 
/* 565 */     EventDispatcher localEventDispatcher = new EventDispatcher(this, paramInt3, paramInt4, paramArrayOfLong, paramLong, paramBoolean);
/*     */ 
/* 569 */     SunDropTargetEvent localSunDropTargetEvent = new SunDropTargetEvent(paramComponent, paramInt5, paramInt1, paramInt2, localEventDispatcher);
/*     */ 
/* 572 */     if (paramBoolean == true) {
/* 573 */       DataTransferer.getInstance().getToolkitThreadBlockedHandler().lock();
/*     */     }
/*     */ 
/* 577 */     SunToolkit.postEvent(localAppContext, localSunDropTargetEvent);
/*     */ 
/* 579 */     eventPosted(localSunDropTargetEvent);
/*     */ 
/* 581 */     if (paramBoolean == true) {
/* 582 */       while (!localEventDispatcher.isDone()) {
/* 583 */         DataTransferer.getInstance().getToolkitThreadBlockedHandler().enter();
/*     */       }
/*     */ 
/* 586 */       DataTransferer.getInstance().getToolkitThreadBlockedHandler().unlock();
/*     */ 
/* 589 */       return localEventDispatcher.getReturnValue();
/*     */     }
/* 591 */     return 0;
/*     */   }
/*     */ 
/*     */   public synchronized void acceptDrag(int paramInt)
/*     */   {
/* 600 */     if (this.currentDT == null) {
/* 601 */       throw new InvalidDnDOperationException("No Drag pending");
/*     */     }
/* 603 */     this.currentDA = mapOperation(paramInt);
/* 604 */     if (this.currentDA != 0)
/* 605 */       this.dragRejected = false;
/*     */   }
/*     */ 
/*     */   public synchronized void rejectDrag()
/*     */   {
/* 614 */     if (this.currentDT == null) {
/* 615 */       throw new InvalidDnDOperationException("No Drag pending");
/*     */     }
/* 617 */     this.currentDA = 0;
/* 618 */     this.dragRejected = true;
/*     */   }
/*     */ 
/*     */   public synchronized void acceptDrop(int paramInt)
/*     */   {
/* 626 */     if (paramInt == 0) {
/* 627 */       throw new IllegalArgumentException("invalid acceptDrop() action");
/*     */     }
/* 629 */     if (this.dropStatus != 1) {
/* 630 */       throw new InvalidDnDOperationException("invalid acceptDrop()");
/*     */     }
/*     */ 
/* 633 */     this.currentDA = (this.currentA = mapOperation(paramInt & this.currentSA));
/*     */ 
/* 635 */     this.dropStatus = 2;
/* 636 */     this.dropComplete = false;
/*     */   }
/*     */ 
/*     */   public synchronized void rejectDrop()
/*     */   {
/* 644 */     if (this.dropStatus != 1) {
/* 645 */       throw new InvalidDnDOperationException("invalid rejectDrop()");
/*     */     }
/* 647 */     this.dropStatus = -1;
/*     */ 
/* 654 */     this.currentDA = 0;
/* 655 */     dropComplete(false);
/*     */   }
/*     */ 
/*     */   private int mapOperation(int paramInt)
/*     */   {
/* 663 */     int[] arrayOfInt = { 2, 1, 1073741824 };
/*     */ 
/* 668 */     int i = 0;
/*     */ 
/* 670 */     for (int j = 0; j < arrayOfInt.length; j++) {
/* 671 */       if ((paramInt & arrayOfInt[j]) == arrayOfInt[j]) {
/* 672 */         i = arrayOfInt[j];
/* 673 */         break;
/*     */       }
/*     */     }
/*     */ 
/* 677 */     return i;
/*     */   }
/*     */ 
/*     */   public synchronized void dropComplete(boolean paramBoolean)
/*     */   {
/* 685 */     if (this.dropStatus == 0) {
/* 686 */       throw new InvalidDnDOperationException("No Drop pending");
/*     */     }
/*     */ 
/* 689 */     if (this.currentDTC != null) this.currentDTC.removeNotify();
/*     */ 
/* 691 */     this.currentDT = null;
/* 692 */     this.currentDTC = null;
/* 693 */     this.currentT = null;
/* 694 */     this.currentA = 0;
/*     */ 
/* 696 */     synchronized (_globalLock) {
/* 697 */       currentJVMLocalSourceTransferable = null;
/*     */     }
/*     */ 
/* 700 */     this.dropStatus = 0;
/* 701 */     this.dropComplete = true;
/*     */     try
/*     */     {
/* 704 */       doDropDone(paramBoolean, this.currentDA, this.local != null);
/*     */     } finally {
/* 706 */       this.currentDA = 0;
/*     */ 
/* 709 */       this.nativeDragContext = 0L;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected abstract void doDropDone(boolean paramBoolean1, int paramInt, boolean paramBoolean2);
/*     */ 
/*     */   protected synchronized long getNativeDragContext()
/*     */   {
/* 717 */     return this.nativeDragContext;
/*     */   }
/*     */ 
/*     */   protected void eventPosted(SunDropTargetEvent paramSunDropTargetEvent)
/*     */   {
/*     */   }
/*     */ 
/*     */   protected void eventProcessed(SunDropTargetEvent paramSunDropTargetEvent, int paramInt, boolean paramBoolean)
/*     */   {
/*     */   }
/*     */ 
/*     */   protected static class EventDispatcher
/*     */   {
/*     */     private final SunDropTargetContextPeer peer;
/*     */     private final int dropAction;
/*     */     private final int actions;
/*     */     private final long[] formats;
/*     */     private long nativeCtxt;
/*     */     private final boolean dispatchType;
/* 735 */     private boolean dispatcherDone = false;
/*     */ 
/* 738 */     private int returnValue = 0;
/*     */ 
/* 740 */     private final HashSet eventSet = new HashSet(3);
/*     */ 
/* 742 */     static final ToolkitThreadBlockedHandler handler = DataTransferer.getInstance().getToolkitThreadBlockedHandler();
/*     */ 
/*     */     EventDispatcher(SunDropTargetContextPeer paramSunDropTargetContextPeer, int paramInt1, int paramInt2, long[] paramArrayOfLong, long paramLong, boolean paramBoolean)
/*     */     {
/* 752 */       this.peer = paramSunDropTargetContextPeer;
/* 753 */       this.nativeCtxt = paramLong;
/* 754 */       this.dropAction = paramInt1;
/* 755 */       this.actions = paramInt2;
/* 756 */       this.formats = (null == paramArrayOfLong ? null : Arrays.copyOf(paramArrayOfLong, paramArrayOfLong.length));
/*     */ 
/* 758 */       this.dispatchType = paramBoolean;
/*     */     }
/*     */ 
/*     */     void dispatchEvent(SunDropTargetEvent paramSunDropTargetEvent) {
/* 762 */       int i = paramSunDropTargetEvent.getID();
/*     */ 
/* 764 */       switch (i) {
/*     */       case 504:
/* 766 */         dispatchEnterEvent(paramSunDropTargetEvent);
/* 767 */         break;
/*     */       case 506:
/* 769 */         dispatchMotionEvent(paramSunDropTargetEvent);
/* 770 */         break;
/*     */       case 505:
/* 772 */         dispatchExitEvent(paramSunDropTargetEvent);
/* 773 */         break;
/*     */       case 502:
/* 775 */         dispatchDropEvent(paramSunDropTargetEvent);
/* 776 */         break;
/*     */       case 503:
/*     */       default:
/* 778 */         throw new InvalidDnDOperationException();
/*     */       }
/*     */     }
/*     */ 
/*     */     private void dispatchEnterEvent(SunDropTargetEvent paramSunDropTargetEvent) {
/* 783 */       synchronized (this.peer)
/*     */       {
/* 786 */         this.peer.previousDA = this.dropAction;
/*     */ 
/* 789 */         this.peer.nativeDragContext = this.nativeCtxt;
/* 790 */         this.peer.currentT = this.formats;
/* 791 */         this.peer.currentSA = this.actions;
/* 792 */         this.peer.currentDA = this.dropAction;
/*     */ 
/* 794 */         this.peer.dropStatus = 2;
/* 795 */         this.peer.dropComplete = false;
/*     */         try
/*     */         {
/* 798 */           this.peer.processEnterMessage(paramSunDropTargetEvent);
/*     */         } finally {
/* 800 */           this.peer.dropStatus = 0;
/*     */         }
/*     */ 
/* 803 */         setReturnValue(this.peer.currentDA);
/*     */       }
/*     */     }
/*     */ 
/*     */     private void dispatchMotionEvent(SunDropTargetEvent paramSunDropTargetEvent) {
/* 808 */       synchronized (this.peer)
/*     */       {
/* 810 */         boolean bool = this.peer.previousDA != this.dropAction;
/* 811 */         this.peer.previousDA = this.dropAction;
/*     */ 
/* 814 */         this.peer.nativeDragContext = this.nativeCtxt;
/* 815 */         this.peer.currentT = this.formats;
/* 816 */         this.peer.currentSA = this.actions;
/* 817 */         this.peer.currentDA = this.dropAction;
/*     */ 
/* 819 */         this.peer.dropStatus = 2;
/* 820 */         this.peer.dropComplete = false;
/*     */         try
/*     */         {
/* 823 */           this.peer.processMotionMessage(paramSunDropTargetEvent, bool);
/*     */         } finally {
/* 825 */           this.peer.dropStatus = 0;
/*     */         }
/*     */ 
/* 828 */         setReturnValue(this.peer.currentDA);
/*     */       }
/*     */     }
/*     */ 
/*     */     private void dispatchExitEvent(SunDropTargetEvent paramSunDropTargetEvent) {
/* 833 */       synchronized (this.peer)
/*     */       {
/* 836 */         this.peer.nativeDragContext = this.nativeCtxt;
/*     */ 
/* 838 */         this.peer.processExitMessage(paramSunDropTargetEvent);
/*     */       }
/*     */     }
/*     */ 
/*     */     private void dispatchDropEvent(SunDropTargetEvent paramSunDropTargetEvent) {
/* 843 */       synchronized (this.peer)
/*     */       {
/* 846 */         this.peer.nativeDragContext = this.nativeCtxt;
/* 847 */         this.peer.currentT = this.formats;
/* 848 */         this.peer.currentSA = this.actions;
/* 849 */         this.peer.currentDA = this.dropAction;
/*     */ 
/* 851 */         this.peer.processDropMessage(paramSunDropTargetEvent);
/*     */       }
/*     */     }
/*     */ 
/*     */     void setReturnValue(int paramInt) {
/* 856 */       this.returnValue = paramInt;
/*     */     }
/*     */ 
/*     */     int getReturnValue() {
/* 860 */       return this.returnValue;
/*     */     }
/*     */ 
/*     */     boolean isDone() {
/* 864 */       return this.eventSet.isEmpty();
/*     */     }
/*     */ 
/*     */     void registerEvent(SunDropTargetEvent paramSunDropTargetEvent) {
/* 868 */       handler.lock();
/* 869 */       if ((!this.eventSet.add(paramSunDropTargetEvent)) && (SunDropTargetContextPeer.dndLog.isLoggable(500))) {
/* 870 */         SunDropTargetContextPeer.dndLog.fine("Event is already registered: " + paramSunDropTargetEvent);
/*     */       }
/* 872 */       handler.unlock();
/*     */     }
/*     */ 
/*     */     void unregisterEvent(SunDropTargetEvent paramSunDropTargetEvent) {
/* 876 */       handler.lock();
/*     */       try {
/* 878 */         if (!this.eventSet.remove(paramSunDropTargetEvent))
/*     */         {
/*     */           return;
/*     */         }
/* 882 */         if (this.eventSet.isEmpty()) {
/* 883 */           if ((!this.dispatcherDone) && (this.dispatchType == true)) {
/* 884 */             handler.exit();
/*     */           }
/* 886 */           this.dispatcherDone = true;
/*     */         }
/*     */       } finally {
/* 889 */         handler.unlock();
/*     */       }
/*     */       try
/*     */       {
/* 893 */         this.peer.eventProcessed(paramSunDropTargetEvent, this.returnValue, this.dispatcherDone);
/*     */       }
/*     */       finally
/*     */       {
/* 899 */         if (this.dispatcherDone) {
/* 900 */           this.nativeCtxt = 0L;
/*     */ 
/* 902 */           this.peer.nativeDragContext = 0L;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     public void unregisterAllEvents()
/*     */     {
/* 909 */       Object[] arrayOfObject = null;
/* 910 */       handler.lock();
/*     */       try {
/* 912 */         arrayOfObject = this.eventSet.toArray();
/*     */       } finally {
/* 914 */         handler.unlock();
/*     */       }
/*     */ 
/* 917 */       if (arrayOfObject != null)
/* 918 */         for (int i = 0; i < arrayOfObject.length; i++)
/* 919 */           unregisterEvent((SunDropTargetEvent)arrayOfObject[i]);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.dnd.SunDropTargetContextPeer
 * JD-Core Version:    0.6.2
 */