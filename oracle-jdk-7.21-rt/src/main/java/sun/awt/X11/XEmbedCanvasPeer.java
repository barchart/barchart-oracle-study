/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.awt.AWTEvent;
/*     */ import java.awt.AWTKeyStroke;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.KeyEventPostProcessor;
/*     */ import java.awt.KeyboardFocusManager;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.Window;
/*     */ import java.awt.dnd.DropTarget;
/*     */ import java.awt.dnd.DropTargetListener;
/*     */ import java.awt.event.ComponentEvent;
/*     */ import java.awt.event.FocusEvent;
/*     */ import java.awt.event.InputEvent;
/*     */ import java.awt.event.InvocationEvent;
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.awt.event.WindowEvent;
/*     */ import java.awt.event.WindowFocusListener;
/*     */ import java.security.AccessController;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.TooManyListenersException;
/*     */ import sun.awt.AWTAccessor;
/*     */ import sun.awt.AWTAccessor.AWTEventAccessor;
/*     */ import sun.awt.AppContext;
/*     */ import sun.awt.CausedFocusEvent;
/*     */ import sun.awt.CausedFocusEvent.Cause;
/*     */ import sun.awt.ModalityEvent;
/*     */ import sun.awt.ModalityListener;
/*     */ import sun.awt.SunToolkit;
/*     */ import sun.awt.WindowIDProvider;
/*     */ import sun.misc.Unsafe;
/*     */ import sun.security.action.GetBooleanAction;
/*     */ import sun.util.logging.PlatformLogger;
/*     */ 
/*     */ public class XEmbedCanvasPeer extends XCanvasPeer
/*     */   implements WindowFocusListener, KeyEventPostProcessor, ModalityListener, WindowIDProvider
/*     */ {
/*  42 */   private static final PlatformLogger xembedLog = PlatformLogger.getLogger("sun.awt.X11.xembed.XEmbedCanvasPeer");
/*     */   boolean applicationActive;
/*  45 */   XEmbedServer xembed = new XEmbedServer();
/*  46 */   Map<Long, AWTKeyStroke> accelerators = new HashMap();
/*  47 */   Map<AWTKeyStroke, Long> accel_lookup = new HashMap();
/*  48 */   Set<GrabbedKey> grabbed_keys = new HashSet();
/*  49 */   Object ACCEL_LOCK = this.accelerators;
/*  50 */   Object GRAB_LOCK = this.grabbed_keys;
/*     */ 
/*     */   XEmbedCanvasPeer() {
/*     */   }
/*     */   XEmbedCanvasPeer(XCreateWindowParams paramXCreateWindowParams) {
/*  55 */     super(paramXCreateWindowParams);
/*     */   }
/*     */ 
/*     */   XEmbedCanvasPeer(Component paramComponent) {
/*  59 */     super(paramComponent);
/*     */   }
/*     */ 
/*     */   protected void postInit(XCreateWindowParams paramXCreateWindowParams) {
/*  63 */     super.postInit(paramXCreateWindowParams);
/*     */ 
/*  65 */     installActivateListener();
/*  66 */     installAcceleratorListener();
/*  67 */     installModalityListener();
/*     */ 
/*  71 */     this.target.setFocusTraversalKeysEnabled(false);
/*     */   }
/*     */ 
/*     */   protected void preInit(XCreateWindowParams paramXCreateWindowParams) {
/*  75 */     super.preInit(paramXCreateWindowParams);
/*     */ 
/*  77 */     paramXCreateWindowParams.put("event mask", Long.valueOf(2793599L));
/*     */   }
/*     */ 
/*     */   void installModalityListener()
/*     */   {
/*  86 */     ((SunToolkit)Toolkit.getDefaultToolkit()).addModalityListener(this);
/*     */   }
/*     */ 
/*     */   void deinstallModalityListener() {
/*  90 */     ((SunToolkit)Toolkit.getDefaultToolkit()).removeModalityListener(this);
/*     */   }
/*     */ 
/*     */   void installAcceleratorListener() {
/*  94 */     KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventPostProcessor(this);
/*     */   }
/*     */ 
/*     */   void deinstallAcceleratorListener() {
/*  98 */     KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventPostProcessor(this);
/*     */   }
/*     */ 
/*     */   void installActivateListener()
/*     */   {
/* 103 */     Window localWindow = getTopLevel(this.target);
/* 104 */     if (localWindow != null) {
/* 105 */       localWindow.addWindowFocusListener(this);
/* 106 */       this.applicationActive = localWindow.isFocused();
/*     */     }
/*     */   }
/*     */ 
/*     */   void deinstallActivateListener() {
/* 111 */     Window localWindow = getTopLevel(this.target);
/* 112 */     if (localWindow != null)
/* 113 */       localWindow.removeWindowFocusListener(this);
/*     */   }
/*     */ 
/*     */   boolean isXEmbedActive()
/*     */   {
/* 118 */     return this.xembed.handle != 0L;
/*     */   }
/*     */ 
/*     */   boolean isApplicationActive() {
/* 122 */     return this.applicationActive;
/*     */   }
/*     */ 
/*     */   void initDispatching() {
/* 126 */     if (xembedLog.isLoggable(500)) xembedLog.fine("Init embedding for " + Long.toHexString(this.xembed.handle));
/* 127 */     XToolkit.awtLock();
/*     */     try {
/* 129 */       XToolkit.addEventDispatcher(this.xembed.handle, this.xembed);
/* 130 */       XlibWrapper.XSelectInput(XToolkit.getDisplay(), this.xembed.handle, 4325376L);
/*     */ 
/* 133 */       XDropTargetRegistry.getRegistry().registerXEmbedClient(getWindow(), this.xembed.handle);
/*     */     } finally {
/* 135 */       XToolkit.awtUnlock();
/*     */     }
/* 137 */     this.xembed.processXEmbedInfo();
/*     */ 
/* 139 */     notifyChildEmbedded();
/*     */   }
/*     */ 
/*     */   void endDispatching() {
/* 143 */     xembedLog.fine("End dispatching for " + Long.toHexString(this.xembed.handle));
/* 144 */     XToolkit.awtLock();
/*     */     try {
/* 146 */       XDropTargetRegistry.getRegistry().unregisterXEmbedClient(getWindow(), this.xembed.handle);
/*     */ 
/* 148 */       XToolkit.removeEventDispatcher(this.xembed.handle, this.xembed);
/*     */     } finally {
/* 150 */       XToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   void embedChild(long paramLong) {
/* 155 */     if (this.xembed.handle != 0L) {
/* 156 */       detachChild();
/*     */     }
/* 158 */     this.xembed.handle = paramLong;
/* 159 */     initDispatching();
/*     */   }
/*     */ 
/*     */   void childDestroyed() {
/* 163 */     xembedLog.fine("Child " + Long.toHexString(this.xembed.handle) + " has self-destroyed.");
/* 164 */     endDispatching();
/* 165 */     this.xembed.handle = 0L;
/*     */   }
/*     */ 
/*     */   public void handleEvent(AWTEvent paramAWTEvent) {
/* 169 */     super.handleEvent(paramAWTEvent);
/* 170 */     if (isXEmbedActive())
/* 171 */       switch (paramAWTEvent.getID()) {
/*     */       case 1004:
/* 173 */         canvasFocusGained((FocusEvent)paramAWTEvent);
/* 174 */         break;
/*     */       case 1005:
/* 176 */         canvasFocusLost((FocusEvent)paramAWTEvent);
/* 177 */         break;
/*     */       case 401:
/*     */       case 402:
/* 180 */         if (!((InputEvent)paramAWTEvent).isConsumed())
/* 181 */           forwardKeyEvent((KeyEvent)paramAWTEvent);
/*     */         break;
/*     */       }
/*     */   }
/*     */ 
/*     */   public void dispatchEvent(XEvent paramXEvent)
/*     */   {
/* 189 */     super.dispatchEvent(paramXEvent);
/* 190 */     switch (paramXEvent.get_type()) {
/*     */     case 16:
/* 192 */       XCreateWindowEvent localXCreateWindowEvent = paramXEvent.get_xcreatewindow();
/* 193 */       if (xembedLog.isLoggable(300)) {
/* 194 */         xembedLog.finest("Message on embedder: " + localXCreateWindowEvent);
/*     */       }
/* 196 */       if (xembedLog.isLoggable(400)) {
/* 197 */         xembedLog.finer("Create notify for parent " + Long.toHexString(localXCreateWindowEvent.get_parent()) + ", window " + Long.toHexString(localXCreateWindowEvent.get_window()));
/*     */       }
/*     */ 
/* 200 */       embedChild(localXCreateWindowEvent.get_window());
/* 201 */       break;
/*     */     case 17:
/* 203 */       XDestroyWindowEvent localXDestroyWindowEvent = paramXEvent.get_xdestroywindow();
/* 204 */       if (xembedLog.isLoggable(300)) {
/* 205 */         xembedLog.finest("Message on embedder: " + localXDestroyWindowEvent);
/*     */       }
/* 207 */       if (xembedLog.isLoggable(400)) {
/* 208 */         xembedLog.finer("Destroy notify for parent: " + localXDestroyWindowEvent);
/*     */       }
/* 210 */       childDestroyed();
/* 211 */       break;
/*     */     case 21:
/* 213 */       XReparentEvent localXReparentEvent = paramXEvent.get_xreparent();
/* 214 */       if (xembedLog.isLoggable(300)) {
/* 215 */         xembedLog.finest("Message on embedder: " + localXReparentEvent);
/*     */       }
/* 217 */       if (xembedLog.isLoggable(400)) {
/* 218 */         xembedLog.finer("Reparent notify for parent " + Long.toHexString(localXReparentEvent.get_parent()) + ", window " + Long.toHexString(localXReparentEvent.get_window()) + ", event " + Long.toHexString(localXReparentEvent.get_event()));
/*     */       }
/*     */ 
/* 222 */       if (localXReparentEvent.get_parent() == getWindow())
/*     */       {
/* 224 */         embedChild(localXReparentEvent.get_window());
/*     */       }
/*     */       else
/* 227 */         childDestroyed();
/*     */       break;
/*     */     }
/*     */   }
/*     */ 
/*     */   public Dimension getPreferredSize()
/*     */   {
/* 234 */     if (isXEmbedActive()) {
/* 235 */       XToolkit.awtLock();
/*     */       try {
/* 237 */         long l = XlibWrapper.XAllocSizeHints();
/* 238 */         XSizeHints localXSizeHints = new XSizeHints(l);
/* 239 */         XlibWrapper.XGetWMNormalHints(XToolkit.getDisplay(), this.xembed.handle, l, XlibWrapper.larg1);
/* 240 */         Dimension localDimension1 = new Dimension(localXSizeHints.get_width(), localXSizeHints.get_height());
/* 241 */         XlibWrapper.XFree(l);
/* 242 */         return localDimension1;
/*     */       } finally {
/* 244 */         XToolkit.awtUnlock();
/*     */       }
/*     */     }
/* 247 */     return super.getPreferredSize();
/*     */   }
/*     */ 
/*     */   public Dimension getMinimumSize() {
/* 251 */     if (isXEmbedActive()) {
/* 252 */       XToolkit.awtLock();
/*     */       try {
/* 254 */         long l = XlibWrapper.XAllocSizeHints();
/* 255 */         XSizeHints localXSizeHints = new XSizeHints(l);
/* 256 */         XlibWrapper.XGetWMNormalHints(XToolkit.getDisplay(), this.xembed.handle, l, XlibWrapper.larg1);
/* 257 */         Dimension localDimension1 = new Dimension(localXSizeHints.get_min_width(), localXSizeHints.get_min_height());
/* 258 */         XlibWrapper.XFree(l);
/* 259 */         return localDimension1;
/*     */       } finally {
/* 261 */         XToolkit.awtUnlock();
/*     */       }
/*     */     }
/* 264 */     return super.getMinimumSize();
/*     */   }
/*     */ 
/*     */   public void dispose() {
/* 268 */     if (isXEmbedActive()) {
/* 269 */       detachChild();
/*     */     }
/* 271 */     deinstallActivateListener();
/* 272 */     deinstallModalityListener();
/* 273 */     deinstallAcceleratorListener();
/*     */ 
/* 278 */     super.dispose();
/*     */   }
/*     */ 
/*     */   public boolean isFocusable()
/*     */   {
/* 283 */     return true;
/*     */   }
/*     */ 
/*     */   Window getTopLevel(Component paramComponent) {
/* 287 */     while ((paramComponent != null) && (!(paramComponent instanceof Window))) {
/* 288 */       paramComponent = paramComponent.getParent();
/*     */     }
/* 290 */     return (Window)paramComponent;
/*     */   }
/*     */ 
/*     */   Rectangle getClientBounds() {
/* 294 */     XToolkit.awtLock();
/*     */     try {
/* 296 */       XWindowAttributes localXWindowAttributes = new XWindowAttributes();
/*     */       try {
/* 298 */         XToolkit.WITH_XERROR_HANDLER(XErrorHandler.IgnoreBadWindowHandler.getInstance());
/* 299 */         int i = XlibWrapper.XGetWindowAttributes(XToolkit.getDisplay(), this.xembed.handle, localXWindowAttributes.pData);
/*     */ 
/* 302 */         XToolkit.RESTORE_XERROR_HANDLER();
/*     */ 
/* 304 */         if ((i == 0) || ((XToolkit.saved_error != null) && (XToolkit.saved_error.get_error_code() != 0)))
/*     */         {
/* 307 */           localRectangle = null;
/*     */ 
/* 312 */           localXWindowAttributes.dispose();
/*     */ 
/* 315 */           return localRectangle;
/*     */         }
/* 310 */         Rectangle localRectangle = new Rectangle(localXWindowAttributes.get_x(), localXWindowAttributes.get_y(), localXWindowAttributes.get_width(), localXWindowAttributes.get_height());
/*     */ 
/* 312 */         localXWindowAttributes.dispose();
/*     */ 
/* 315 */         return localRectangle;
/*     */       }
/*     */       finally
/*     */       {
/* 312 */         localXWindowAttributes.dispose();
/*     */       }
/*     */     } finally {
/* 315 */       XToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   void childResized() {
/* 320 */     if (xembedLog.isLoggable(400)) {
/* 321 */       Rectangle localRectangle = getClientBounds();
/* 322 */       xembedLog.finer("Child resized: " + localRectangle);
/*     */     }
/*     */ 
/* 330 */     XToolkit.postEvent(XToolkit.targetToAppContext(this.target), new ComponentEvent(this.target, 101));
/*     */   }
/*     */ 
/*     */   void focusNext() {
/* 334 */     if (isXEmbedActive()) {
/* 335 */       xembedLog.fine("Requesting focus for the next component after embedder");
/* 336 */       postEvent(new InvocationEvent(this.target, new Runnable() {
/*     */         public void run() {
/* 338 */           KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent(XEmbedCanvasPeer.this.target);
/*     */         } } ));
/*     */     }
/*     */     else {
/* 342 */       xembedLog.fine("XEmbed is not active - denying focus next");
/*     */     }
/*     */   }
/*     */ 
/*     */   void focusPrev() {
/* 347 */     if (isXEmbedActive()) {
/* 348 */       xembedLog.fine("Requesting focus for the next component after embedder");
/* 349 */       postEvent(new InvocationEvent(this.target, new Runnable() {
/*     */         public void run() {
/* 351 */           KeyboardFocusManager.getCurrentKeyboardFocusManager().focusPreviousComponent(XEmbedCanvasPeer.this.target);
/*     */         } } ));
/*     */     }
/*     */     else {
/* 355 */       xembedLog.fine("XEmbed is not active - denying focus prev");
/*     */     }
/*     */   }
/*     */ 
/*     */   void requestXEmbedFocus() {
/* 360 */     if (isXEmbedActive()) {
/* 361 */       xembedLog.fine("Requesting focus for client");
/* 362 */       postEvent(new InvocationEvent(this.target, new Runnable() {
/*     */         public void run() {
/* 364 */           XEmbedCanvasPeer.this.target.requestFocus();
/*     */         } } ));
/*     */     }
/*     */     else {
/* 368 */       xembedLog.fine("XEmbed is not active - denying request focus");
/*     */     }
/*     */   }
/*     */ 
/*     */   void notifyChildEmbedded() {
/* 373 */     this.xembed.sendMessage(this.xembed.handle, 0, getWindow(), Math.min(this.xembed.version, 0L), 0L);
/* 374 */     if (isApplicationActive()) {
/* 375 */       xembedLog.fine("Sending WINDOW_ACTIVATE during initialization");
/* 376 */       this.xembed.sendMessage(this.xembed.handle, 1);
/* 377 */       if (hasFocus()) {
/* 378 */         xembedLog.fine("Sending FOCUS_GAINED during initialization");
/* 379 */         this.xembed.sendMessage(this.xembed.handle, 4, 0L, 0L, 0L);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   void detachChild() {
/* 385 */     if (xembedLog.isLoggable(500)) xembedLog.fine("Detaching child " + Long.toHexString(this.xembed.handle));
/*     */ 
/* 394 */     XToolkit.awtLock();
/*     */     try {
/* 396 */       XlibWrapper.XUnmapWindow(XToolkit.getDisplay(), this.xembed.handle);
/* 397 */       XlibWrapper.XReparentWindow(XToolkit.getDisplay(), this.xembed.handle, XToolkit.getDefaultRootWindow(), 0, 0);
/*     */     } finally {
/* 399 */       XToolkit.awtUnlock();
/*     */     }
/* 401 */     endDispatching();
/* 402 */     this.xembed.handle = 0L;
/*     */   }
/*     */ 
/*     */   public void windowGainedFocus(WindowEvent paramWindowEvent) {
/* 406 */     this.applicationActive = true;
/* 407 */     if (isXEmbedActive()) {
/* 408 */       xembedLog.fine("Sending WINDOW_ACTIVATE");
/* 409 */       this.xembed.sendMessage(this.xembed.handle, 1);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void windowLostFocus(WindowEvent paramWindowEvent) {
/* 414 */     this.applicationActive = false;
/* 415 */     if (isXEmbedActive()) {
/* 416 */       xembedLog.fine("Sending WINDOW_DEACTIVATE");
/* 417 */       this.xembed.sendMessage(this.xembed.handle, 2);
/*     */     }
/*     */   }
/*     */ 
/*     */   void canvasFocusGained(FocusEvent paramFocusEvent) {
/* 422 */     if (isXEmbedActive()) {
/* 423 */       xembedLog.fine("Forwarding FOCUS_GAINED");
/* 424 */       int i = 0;
/* 425 */       if ((paramFocusEvent instanceof CausedFocusEvent)) {
/* 426 */         CausedFocusEvent localCausedFocusEvent = (CausedFocusEvent)paramFocusEvent;
/* 427 */         if (localCausedFocusEvent.getCause() == CausedFocusEvent.Cause.TRAVERSAL_FORWARD)
/* 428 */           i = 1;
/* 429 */         else if (localCausedFocusEvent.getCause() == CausedFocusEvent.Cause.TRAVERSAL_BACKWARD) {
/* 430 */           i = 2;
/*     */         }
/*     */       }
/* 433 */       this.xembed.sendMessage(this.xembed.handle, 4, i, 0L, 0L);
/*     */     }
/*     */   }
/*     */ 
/*     */   void canvasFocusLost(FocusEvent paramFocusEvent) {
/* 438 */     if ((isXEmbedActive()) && (!paramFocusEvent.isTemporary())) {
/* 439 */       xembedLog.fine("Forwarding FOCUS_LOST");
/* 440 */       int i = 0;
/* 441 */       if (((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.awt.xembed.testing"))).booleanValue()) {
/* 442 */         Component localComponent = paramFocusEvent.getOppositeComponent();
/*     */         try {
/* 444 */           i = Integer.parseInt(localComponent.getName());
/*     */         } catch (NumberFormatException localNumberFormatException) {
/*     */         }
/*     */       }
/* 448 */       this.xembed.sendMessage(this.xembed.handle, 5, i, 0L, 0L);
/*     */     }
/*     */   }
/*     */ 
/*     */   static byte[] getBData(KeyEvent paramKeyEvent) {
/* 453 */     return AWTAccessor.getAWTEventAccessor().getBData(paramKeyEvent);
/*     */   }
/*     */ 
/*     */   void forwardKeyEvent(KeyEvent paramKeyEvent) {
/* 457 */     xembedLog.fine("Try to forward key event");
/* 458 */     byte[] arrayOfByte = getBData(paramKeyEvent);
/* 459 */     long l = Native.toData(arrayOfByte);
/* 460 */     if (l == 0L)
/* 461 */       return;
/*     */     try
/*     */     {
/* 464 */       XKeyEvent localXKeyEvent = new XKeyEvent(l);
/* 465 */       localXKeyEvent.set_window(this.xembed.handle);
/* 466 */       if (xembedLog.isLoggable(500)) xembedLog.fine("Forwarding native key event: " + localXKeyEvent);
/* 467 */       XToolkit.awtLock();
/*     */       try {
/* 469 */         XlibWrapper.XSendEvent(XToolkit.getDisplay(), this.xembed.handle, false, 0L, l);
/*     */       } finally {
/* 471 */         XToolkit.awtUnlock();
/*     */       }
/*     */     } finally {
/* 474 */       XlibWrapper.unsafe.freeMemory(l);
/*     */     }
/*     */   }
/*     */ 
/*     */   void grabKey(final long paramLong1, long paramLong2)
/*     */   {
/* 494 */     postEvent(new InvocationEvent(this.target, new Runnable() {
/*     */       public void run() {
/* 496 */         XEmbedCanvasPeer.GrabbedKey localGrabbedKey = new XEmbedCanvasPeer.GrabbedKey(paramLong1, this.val$modifiers);
/* 497 */         if (XEmbedCanvasPeer.xembedLog.isLoggable(500)) XEmbedCanvasPeer.xembedLog.fine("Grabbing key: " + localGrabbedKey);
/* 498 */         synchronized (XEmbedCanvasPeer.this.GRAB_LOCK) {
/* 499 */           XEmbedCanvasPeer.this.grabbed_keys.add(localGrabbedKey);
/*     */         }
/*     */       }
/*     */     }));
/*     */   }
/*     */ 
/*     */   void ungrabKey(final long paramLong1, long paramLong2) {
/* 506 */     postEvent(new InvocationEvent(this.target, new Runnable() {
/*     */       public void run() {
/* 508 */         XEmbedCanvasPeer.GrabbedKey localGrabbedKey = new XEmbedCanvasPeer.GrabbedKey(paramLong1, this.val$modifiers);
/* 509 */         if (XEmbedCanvasPeer.xembedLog.isLoggable(500)) XEmbedCanvasPeer.xembedLog.fine("UnGrabbing key: " + localGrabbedKey);
/* 510 */         synchronized (XEmbedCanvasPeer.this.GRAB_LOCK) {
/* 511 */           XEmbedCanvasPeer.this.grabbed_keys.remove(localGrabbedKey);
/*     */         }
/*     */       }
/*     */     }));
/*     */   }
/*     */ 
/*     */   void registerAccelerator(final long paramLong1, final long paramLong2, long paramLong3) {
/* 518 */     postEvent(new InvocationEvent(this.target, new Runnable() {
/*     */       public void run() {
/* 520 */         AWTKeyStroke localAWTKeyStroke = XEmbedCanvasPeer.this.xembed.getKeyStrokeForKeySym(paramLong2, paramLong1);
/* 521 */         if (localAWTKeyStroke != null) {
/* 522 */           if (XEmbedCanvasPeer.xembedLog.isLoggable(500)) XEmbedCanvasPeer.xembedLog.fine("Registering accelerator " + this.val$accel_id + " for " + localAWTKeyStroke);
/* 523 */           synchronized (XEmbedCanvasPeer.this.ACCEL_LOCK) {
/* 524 */             XEmbedCanvasPeer.this.accelerators.put(Long.valueOf(this.val$accel_id), localAWTKeyStroke);
/* 525 */             XEmbedCanvasPeer.this.accel_lookup.put(localAWTKeyStroke, Long.valueOf(this.val$accel_id));
/*     */           }
/*     */         }
/* 528 */         XEmbedCanvasPeer.this.propogateRegisterAccelerator(localAWTKeyStroke);
/*     */       }
/*     */     }));
/*     */   }
/*     */ 
/*     */   void unregisterAccelerator(final long paramLong) {
/* 534 */     postEvent(new InvocationEvent(this.target, new Runnable() {
/*     */       public void run() {
/* 536 */         AWTKeyStroke localAWTKeyStroke = null;
/* 537 */         synchronized (XEmbedCanvasPeer.this.ACCEL_LOCK) {
/* 538 */           localAWTKeyStroke = (AWTKeyStroke)XEmbedCanvasPeer.this.accelerators.get(Long.valueOf(paramLong));
/* 539 */           if (localAWTKeyStroke != null) {
/* 540 */             if (XEmbedCanvasPeer.xembedLog.isLoggable(500)) XEmbedCanvasPeer.xembedLog.fine("Unregistering accelerator: " + paramLong);
/* 541 */             XEmbedCanvasPeer.this.accelerators.remove(Long.valueOf(paramLong));
/* 542 */             XEmbedCanvasPeer.this.accel_lookup.remove(localAWTKeyStroke);
/*     */           }
/*     */         }
/* 545 */         XEmbedCanvasPeer.this.propogateUnRegisterAccelerator(localAWTKeyStroke);
/*     */       }
/*     */     }));
/*     */   }
/*     */ 
/*     */   void propogateRegisterAccelerator(AWTKeyStroke paramAWTKeyStroke)
/*     */   {
/* 553 */     XWindowPeer localXWindowPeer = getToplevelXWindow();
/* 554 */     if ((localXWindowPeer != null) && ((localXWindowPeer instanceof XEmbeddedFramePeer))) {
/* 555 */       XEmbeddedFramePeer localXEmbeddedFramePeer = (XEmbeddedFramePeer)localXWindowPeer;
/* 556 */       localXEmbeddedFramePeer.registerAccelerator(paramAWTKeyStroke);
/*     */     }
/*     */   }
/*     */ 
/*     */   void propogateUnRegisterAccelerator(AWTKeyStroke paramAWTKeyStroke)
/*     */   {
/* 563 */     XWindowPeer localXWindowPeer = getToplevelXWindow();
/* 564 */     if ((localXWindowPeer != null) && ((localXWindowPeer instanceof XEmbeddedFramePeer))) {
/* 565 */       XEmbeddedFramePeer localXEmbeddedFramePeer = (XEmbeddedFramePeer)localXWindowPeer;
/* 566 */       localXEmbeddedFramePeer.unregisterAccelerator(paramAWTKeyStroke);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean postProcessKeyEvent(KeyEvent paramKeyEvent)
/*     */   {
/* 579 */     XWindowPeer localXWindowPeer = getToplevelXWindow();
/* 580 */     if ((localXWindowPeer == null) || (!((Window)localXWindowPeer.getTarget()).isFocused()) || (this.target.isFocusOwner())) {
/* 581 */       return false;
/*     */     }
/*     */ 
/* 584 */     boolean bool1 = false;
/*     */ 
/* 586 */     if (xembedLog.isLoggable(400)) xembedLog.finer("Post-processing event " + paramKeyEvent);
/*     */ 
/* 589 */     AWTKeyStroke localAWTKeyStroke = AWTKeyStroke.getAWTKeyStrokeForEvent(paramKeyEvent);
/* 590 */     long l = 0L;
/* 591 */     boolean bool2 = false;
/* 592 */     synchronized (this.ACCEL_LOCK) {
/* 593 */       bool2 = this.accel_lookup.containsKey(localAWTKeyStroke);
/* 594 */       if (bool2) {
/* 595 */         l = ((Long)this.accel_lookup.get(localAWTKeyStroke)).longValue();
/*     */       }
/*     */     }
/* 598 */     if (bool2) {
/* 599 */       if (xembedLog.isLoggable(500)) xembedLog.fine("Activating accelerator " + l);
/* 600 */       this.xembed.sendMessage(this.xembed.handle, 14, l, 0L, 0L);
/* 601 */       bool1 = true;
/*     */     }
/*     */ 
/* 605 */     bool2 = false;
/* 606 */     ??? = new GrabbedKey(paramKeyEvent);
/* 607 */     synchronized (this.GRAB_LOCK) {
/* 608 */       bool2 = this.grabbed_keys.contains(???);
/*     */     }
/* 610 */     if (bool2) {
/* 611 */       if (xembedLog.isLoggable(500)) xembedLog.fine("Forwarding grabbed key " + paramKeyEvent);
/* 612 */       forwardKeyEvent(paramKeyEvent);
/* 613 */       bool1 = true;
/*     */     }
/*     */ 
/* 616 */     return bool1;
/*     */   }
/*     */ 
/*     */   public void modalityPushed(ModalityEvent paramModalityEvent) {
/* 620 */     this.xembed.sendMessage(this.xembed.handle, 10);
/*     */   }
/*     */ 
/*     */   public void modalityPopped(ModalityEvent paramModalityEvent) {
/* 624 */     this.xembed.sendMessage(this.xembed.handle, 11);
/*     */   }
/*     */ 
/*     */   public void handleClientMessage(XEvent paramXEvent) {
/* 628 */     super.handleClientMessage(paramXEvent);
/* 629 */     XClientMessageEvent localXClientMessageEvent = paramXEvent.get_xclient();
/* 630 */     if (xembedLog.isLoggable(400)) xembedLog.finer("Client message to embedder: " + localXClientMessageEvent);
/* 631 */     if ((localXClientMessageEvent.get_message_type() == XEmbedServer.XEmbed.getAtom()) && 
/* 632 */       (xembedLog.isLoggable(500))) xembedLog.fine(XEmbedServer.XEmbedMessageToString(localXClientMessageEvent));
/*     */ 
/* 634 */     if (isXEmbedActive()) {
/* 635 */       switch ((int)localXClientMessageEvent.get_data(1)) {
/*     */       case 3:
/* 637 */         requestXEmbedFocus();
/* 638 */         break;
/*     */       case 6:
/* 640 */         focusNext();
/* 641 */         break;
/*     */       case 7:
/* 643 */         focusPrev();
/* 644 */         break;
/*     */       case 12:
/* 646 */         registerAccelerator(localXClientMessageEvent.get_data(2), localXClientMessageEvent.get_data(3), localXClientMessageEvent.get_data(4));
/* 647 */         break;
/*     */       case 13:
/* 649 */         unregisterAccelerator(localXClientMessageEvent.get_data(2));
/* 650 */         break;
/*     */       case 108:
/* 652 */         grabKey(localXClientMessageEvent.get_data(3), localXClientMessageEvent.get_data(4));
/* 653 */         break;
/*     */       case 109:
/* 655 */         ungrabKey(localXClientMessageEvent.get_data(3), localXClientMessageEvent.get_data(4));
/*     */       }
/*     */     }
/*     */     else
/* 659 */       xembedLog.finer("But XEmbed is not Active!");
/*     */   }
/*     */ 
/*     */   public void setXEmbedDropTarget()
/*     */   {
/* 677 */     Runnable local8 = new Runnable() {
/*     */       public void run() {
/* 679 */         XEmbedCanvasPeer.this.target.setDropTarget(new XEmbedCanvasPeer.XEmbedDropTarget(null));
/*     */       }
/*     */     };
/* 682 */     SunToolkit.executeOnEventHandlerThread(this.target, local8);
/*     */   }
/*     */ 
/*     */   public void removeXEmbedDropTarget()
/*     */   {
/* 687 */     Runnable local9 = new Runnable() {
/*     */       public void run() {
/* 689 */         if ((XEmbedCanvasPeer.this.target.getDropTarget() instanceof XEmbedCanvasPeer.XEmbedDropTarget))
/* 690 */           XEmbedCanvasPeer.this.target.setDropTarget(null);
/*     */       }
/*     */     };
/* 694 */     SunToolkit.executeOnEventHandlerThread(this.target, local9);
/*     */   }
/*     */ 
/*     */   public boolean processXEmbedDnDEvent(long paramLong, int paramInt) {
/* 698 */     if (xembedLog.isLoggable(300)) {
/* 699 */       xembedLog.finest("     Drop target=" + this.target.getDropTarget());
/*     */     }
/* 701 */     if ((this.target.getDropTarget() instanceof XEmbedDropTarget)) {
/* 702 */       AppContext localAppContext = XToolkit.targetToAppContext(getTarget());
/* 703 */       XDropTargetContextPeer localXDropTargetContextPeer = XDropTargetContextPeer.getPeer(localAppContext);
/*     */ 
/* 705 */       localXDropTargetContextPeer.forwardEventToEmbedded(this.xembed.handle, paramLong, paramInt);
/* 706 */       return true;
/*     */     }
/* 708 */     return false;
/*     */   }
/*     */ 
/*     */   static class GrabbedKey
/*     */   {
/*     */     long keysym;
/*     */     long modifiers;
/*     */ 
/*     */     GrabbedKey(long paramLong1, long paramLong2)
/*     */     {
/* 809 */       this.keysym = paramLong1;
/* 810 */       this.modifiers = paramLong2;
/*     */     }
/*     */ 
/*     */     GrabbedKey(KeyEvent paramKeyEvent) {
/* 814 */       init(paramKeyEvent);
/*     */     }
/*     */ 
/*     */     private void init(KeyEvent paramKeyEvent) {
/* 818 */       byte[] arrayOfByte = XEmbedCanvasPeer.getBData(paramKeyEvent);
/* 819 */       long l = Native.toData(arrayOfByte);
/* 820 */       if (l == 0L)
/* 821 */         return;
/*     */       try
/*     */       {
/* 824 */         XToolkit.awtLock();
/*     */         try {
/* 826 */           this.keysym = XWindow.getKeySymForAWTKeyCode(paramKeyEvent.getKeyCode());
/*     */         } finally {
/* 828 */           XToolkit.awtUnlock();
/*     */         }
/* 830 */         XKeyEvent localXKeyEvent = new XKeyEvent(l);
/*     */ 
/* 833 */         this.modifiers = (localXKeyEvent.get_state() & 0x7);
/* 834 */         if (XEmbedCanvasPeer.xembedLog.isLoggable(300)) XEmbedCanvasPeer.xembedLog.finest("Mapped " + paramKeyEvent + " to " + this); 
/*     */       }
/* 836 */       finally { XlibWrapper.unsafe.freeMemory(l); }
/*     */     }
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 841 */       return (int)this.keysym & 0xFFFFFFFF;
/*     */     }
/*     */ 
/*     */     public boolean equals(Object paramObject) {
/* 845 */       if (!(paramObject instanceof GrabbedKey)) {
/* 846 */         return false;
/*     */       }
/* 848 */       GrabbedKey localGrabbedKey = (GrabbedKey)paramObject;
/* 849 */       return (this.keysym == localGrabbedKey.keysym) && (this.modifiers == localGrabbedKey.modifiers);
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 853 */       return "Key combination[keysym=" + this.keysym + ", mods=" + this.modifiers + "]";
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class XEmbedDropTarget extends DropTarget
/*     */   {
/*     */     public void addDropTargetListener(DropTargetListener paramDropTargetListener)
/*     */       throws TooManyListenersException
/*     */     {
/* 671 */       throw new TooManyListenersException();
/*     */     }
/*     */   }
/*     */ 
/*     */   class XEmbedServer extends XEmbedHelper
/*     */     implements XEventDispatcher
/*     */   {
/*     */     long handle;
/*     */     long version;
/*     */     long flags;
/*     */ 
/*     */     XEmbedServer()
/*     */     {
/*     */     }
/*     */ 
/*     */     boolean processXEmbedInfo()
/*     */     {
/* 718 */       long l = Native.allocateLongArray(2);
/*     */       try {
/* 720 */         if (!XEmbedInfo.getAtomData(this.handle, l, 2))
/*     */         {
/* 725 */           XEmbedCanvasPeer.xembedLog.finer("Unable to get XEMBED_INFO atom data");
/* 726 */           return false;
/*     */         }
/* 728 */         this.version = Native.getCard32(l, 0);
/* 729 */         this.flags = Native.getCard32(l, 1);
/* 730 */         boolean bool1 = (this.flags & 1L) != 0L;
/* 731 */         boolean bool2 = XlibUtil.getWindowMapState(this.handle) != 0;
/* 732 */         if (bool1 != bool2) {
/* 733 */           if (XEmbedCanvasPeer.xembedLog.isLoggable(400))
/* 734 */             XEmbedCanvasPeer.xembedLog.fine("Mapping state of the client has changed, old state: " + bool2 + ", new state: " + bool1);
/* 735 */           if (bool1) {
/* 736 */             XToolkit.awtLock();
/*     */             try {
/* 738 */               XlibWrapper.XMapWindow(XToolkit.getDisplay(), this.handle);
/*     */             } finally {
/* 740 */               XToolkit.awtUnlock();
/*     */             }
/*     */           } else {
/* 743 */             XToolkit.awtLock();
/*     */             try {
/* 745 */               XlibWrapper.XUnmapWindow(XToolkit.getDisplay(), this.handle);
/*     */             } finally {
/* 747 */               XToolkit.awtUnlock();
/*     */             }
/*     */           }
/*     */         } else {
/* 751 */           XEmbedCanvasPeer.xembedLog.finer("Mapping state didn't change, mapped: " + bool2);
/*     */         }
/* 753 */         return true;
/*     */       } finally {
/* 755 */         XlibWrapper.unsafe.freeMemory(l);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void handlePropertyNotify(XEvent paramXEvent) {
/* 760 */       if (XEmbedCanvasPeer.this.isXEmbedActive()) {
/* 761 */         XPropertyEvent localXPropertyEvent = paramXEvent.get_xproperty();
/* 762 */         if (XEmbedCanvasPeer.xembedLog.isLoggable(400)) XEmbedCanvasPeer.xembedLog.finer("Property change on client: " + localXPropertyEvent);
/* 763 */         if (localXPropertyEvent.get_atom() == 40L) {
/* 764 */           XEmbedCanvasPeer.this.childResized();
/* 765 */         } else if (localXPropertyEvent.get_atom() == XEmbedInfo.getAtom()) {
/* 766 */           processXEmbedInfo();
/* 767 */         } else if (localXPropertyEvent.get_atom() == XDnDConstants.XA_XdndAware.getAtom())
/*     */         {
/* 769 */           XDropTargetRegistry.getRegistry().unregisterXEmbedClient(XEmbedCanvasPeer.this.getWindow(), XEmbedCanvasPeer.this.xembed.handle);
/*     */ 
/* 771 */           if (localXPropertyEvent.get_state() == 0)
/* 772 */             XDropTargetRegistry.getRegistry().registerXEmbedClient(XEmbedCanvasPeer.this.getWindow(), XEmbedCanvasPeer.this.xembed.handle);
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 777 */         XEmbedCanvasPeer.xembedLog.finer("XEmbed is not active");
/*     */       }
/*     */     }
/*     */ 
/* 781 */     void handleConfigureNotify(XEvent paramXEvent) { if (XEmbedCanvasPeer.this.isXEmbedActive()) {
/* 782 */         XConfigureEvent localXConfigureEvent = paramXEvent.get_xconfigure();
/* 783 */         if (XEmbedCanvasPeer.xembedLog.isLoggable(400)) XEmbedCanvasPeer.xembedLog.finer("Bounds change on client: " + localXConfigureEvent);
/* 784 */         if (paramXEvent.get_xany().get_window() == this.handle)
/* 785 */           XEmbedCanvasPeer.this.childResized();
/*     */       } }
/*     */ 
/*     */     public void dispatchEvent(XEvent paramXEvent)
/*     */     {
/* 790 */       int i = paramXEvent.get_type();
/* 791 */       switch (i) {
/*     */       case 28:
/* 793 */         handlePropertyNotify(paramXEvent);
/* 794 */         break;
/*     */       case 22:
/* 796 */         handleConfigureNotify(paramXEvent);
/* 797 */         break;
/*     */       case 33:
/* 799 */         XEmbedCanvasPeer.this.handleClientMessage(paramXEvent);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XEmbedCanvasPeer
 * JD-Core Version:    0.6.2
 */