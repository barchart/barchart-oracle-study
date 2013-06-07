/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.awt.AWTException;
/*     */ import java.awt.Canvas;
/*     */ import java.awt.Dialog.ModalExclusionType;
/*     */ import java.awt.EventQueue;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.GraphicsConfiguration;
/*     */ import java.awt.Image;
/*     */ import java.awt.Point;
/*     */ import java.awt.PopupMenu;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.SystemTray;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.TrayIcon;
/*     */ import java.awt.Window;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.event.MouseListener;
/*     */ import java.awt.event.MouseMotionListener;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.ImageObserver;
/*     */ import java.awt.peer.TrayIconPeer;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import sun.awt.AWTAccessor;
/*     */ import sun.awt.AWTAccessor.WindowAccessor;
/*     */ import sun.awt.SunToolkit;
/*     */ import sun.util.logging.PlatformLogger;
/*     */ 
/*     */ public class XTrayIconPeer
/*     */   implements TrayIconPeer, InfoWindow.Balloon.LiveArguments, InfoWindow.Tooltip.LiveArguments
/*     */ {
/*  44 */   private static final PlatformLogger ctrLog = PlatformLogger.getLogger("sun.awt.X11.XTrayIconPeer.centering");
/*     */   TrayIcon target;
/*     */   TrayIconEventProxy eventProxy;
/*     */   XTrayIconEmbeddedFrame eframe;
/*     */   TrayIconCanvas canvas;
/*     */   InfoWindow.Balloon balloon;
/*     */   InfoWindow.Tooltip tooltip;
/*     */   PopupMenu popup;
/*     */   String tooltipString;
/*     */   boolean isTrayIconDisplayed;
/*     */   long eframeParentID;
/*     */   final XEventDispatcher parentXED;
/*     */   final XEventDispatcher eframeXED;
/*  58 */   static final XEventDispatcher dummyXED = new XEventDispatcher() { public void dispatchEvent(XEvent paramAnonymousXEvent) {  }  } ;
/*     */   volatile boolean isDisposed;
/*     */   boolean isParentWindowLocated;
/*     */   int old_x;
/*     */   int old_y;
/*     */   int ex_width;
/*     */   int ex_height;
/*     */   static final int TRAY_ICON_WIDTH = 24;
/*     */   static final int TRAY_ICON_HEIGHT = 24;
/*     */ 
/*  74 */   XTrayIconPeer(TrayIcon paramTrayIcon) throws AWTException { this.target = paramTrayIcon;
/*     */ 
/*  76 */     this.eventProxy = new TrayIconEventProxy(this);
/*     */ 
/*  78 */     this.canvas = new TrayIconCanvas(paramTrayIcon, 24, 24);
/*     */ 
/*  80 */     this.eframe = new XTrayIconEmbeddedFrame();
/*     */ 
/*  82 */     this.eframe.setSize(24, 24);
/*  83 */     this.eframe.add(this.canvas);
/*     */ 
/*  88 */     AccessController.doPrivileged(new PrivilegedAction() {
/*     */       public Object run() {
/*  90 */         XTrayIconPeer.this.eframe.setModalExclusionType(Dialog.ModalExclusionType.TOOLKIT_EXCLUDE);
/*  91 */         return null;
/*     */       }
/*     */     });
/*  96 */     if (XWM.getWMID() != 11) {
/*  97 */       this.parentXED = dummyXED;
/*     */     }
/*     */     else {
/* 100 */       this.parentXED = new XEventDispatcher()
/*     */       {
/*     */         public void dispatchEvent(XEvent paramAnonymousXEvent) {
/* 103 */           if ((XTrayIconPeer.this.isDisposed()) || (paramAnonymousXEvent.get_type() != 22)) {
/* 104 */             return;
/*     */           }
/*     */ 
/* 107 */           XConfigureEvent localXConfigureEvent = paramAnonymousXEvent.get_xconfigure();
/*     */ 
/* 109 */           XTrayIconPeer.ctrLog.fine("ConfigureNotify on parent of {0}: {1}x{2}+{3}+{4} (old: {5}+{6})", new Object[] { XTrayIconPeer.this, Integer.valueOf(localXConfigureEvent.get_width()), Integer.valueOf(localXConfigureEvent.get_height()), Integer.valueOf(localXConfigureEvent.get_x()), Integer.valueOf(localXConfigureEvent.get_y()), Integer.valueOf(XTrayIconPeer.this.old_x), Integer.valueOf(XTrayIconPeer.this.old_y) });
/*     */ 
/* 126 */           if ((localXConfigureEvent.get_height() != 24) && (localXConfigureEvent.get_width() != 24))
/*     */           {
/* 131 */             XTrayIconPeer.ctrLog.fine("ConfigureNotify on parent of {0}. Skipping as intermediate resizing.", new Object[] { XTrayIconPeer.this });
/*     */ 
/* 133 */             return;
/*     */           }
/* 135 */           if (localXConfigureEvent.get_height() > 24)
/*     */           {
/* 137 */             XTrayIconPeer.ctrLog.fine("ConfigureNotify on parent of {0}. Centering by \"Y\".", new Object[] { XTrayIconPeer.this });
/*     */ 
/* 140 */             XlibWrapper.XMoveResizeWindow(XToolkit.getDisplay(), XTrayIconPeer.this.eframeParentID, localXConfigureEvent.get_x(), localXConfigureEvent.get_y() + localXConfigureEvent.get_height() / 2 - 12, 24, 24);
/*     */ 
/* 145 */             XTrayIconPeer.this.ex_height = localXConfigureEvent.get_height();
/* 146 */             XTrayIconPeer.this.ex_width = 0;
/*     */           }
/* 148 */           else if (localXConfigureEvent.get_width() > 24)
/*     */           {
/* 150 */             XTrayIconPeer.ctrLog.fine("ConfigureNotify on parent of {0}. Centering by \"X\".", new Object[] { XTrayIconPeer.this });
/*     */ 
/* 153 */             XlibWrapper.XMoveResizeWindow(XToolkit.getDisplay(), XTrayIconPeer.this.eframeParentID, localXConfigureEvent.get_x() + localXConfigureEvent.get_width() / 2 - 12, localXConfigureEvent.get_y(), 24, 24);
/*     */ 
/* 158 */             XTrayIconPeer.this.ex_width = localXConfigureEvent.get_width();
/* 159 */             XTrayIconPeer.this.ex_height = 0;
/*     */           }
/* 161 */           else if ((XTrayIconPeer.this.isParentWindowLocated) && (localXConfigureEvent.get_x() != XTrayIconPeer.this.old_x) && (localXConfigureEvent.get_y() != XTrayIconPeer.this.old_y))
/*     */           {
/* 166 */             if (XTrayIconPeer.this.ex_height != 0)
/*     */             {
/* 168 */               XTrayIconPeer.ctrLog.fine("ConfigureNotify on parent of {0}. Move detected. Centering by \"Y\".", new Object[] { XTrayIconPeer.this });
/*     */ 
/* 171 */               XlibWrapper.XMoveWindow(XToolkit.getDisplay(), XTrayIconPeer.this.eframeParentID, localXConfigureEvent.get_x(), localXConfigureEvent.get_y() + XTrayIconPeer.this.ex_height / 2 - 12);
/*     */             }
/* 175 */             else if (XTrayIconPeer.this.ex_width != 0)
/*     */             {
/* 177 */               XTrayIconPeer.ctrLog.fine("ConfigureNotify on parent of {0}. Move detected. Centering by \"X\".", new Object[] { XTrayIconPeer.this });
/*     */ 
/* 180 */               XlibWrapper.XMoveWindow(XToolkit.getDisplay(), XTrayIconPeer.this.eframeParentID, localXConfigureEvent.get_x() + XTrayIconPeer.this.ex_width / 2 - 12, localXConfigureEvent.get_y());
/*     */             }
/*     */             else
/*     */             {
/* 184 */               XTrayIconPeer.ctrLog.fine("ConfigureNotify on parent of {0}. Move detected. Skipping.", new Object[] { XTrayIconPeer.this });
/*     */             }
/*     */           }
/*     */ 
/* 188 */           XTrayIconPeer.this.old_x = localXConfigureEvent.get_x();
/* 189 */           XTrayIconPeer.this.old_y = localXConfigureEvent.get_y();
/* 190 */           XTrayIconPeer.this.isParentWindowLocated = true;
/*     */         }
/*     */       };
/*     */     }
/* 194 */     this.eframeXED = new XEventDispatcher()
/*     */     {
/* 196 */       XTrayIconPeer xtiPeer = XTrayIconPeer.this;
/*     */ 
/*     */       public void dispatchEvent(XEvent paramAnonymousXEvent) {
/* 199 */         if ((XTrayIconPeer.this.isDisposed()) || (paramAnonymousXEvent.get_type() != 21)) {
/* 200 */           return;
/*     */         }
/*     */ 
/* 203 */         XReparentEvent localXReparentEvent = paramAnonymousXEvent.get_xreparent();
/* 204 */         XTrayIconPeer.this.eframeParentID = localXReparentEvent.get_parent();
/*     */ 
/* 206 */         if (XTrayIconPeer.this.eframeParentID == XToolkit.getDefaultRootWindow())
/*     */         {
/* 208 */           if (XTrayIconPeer.this.isTrayIconDisplayed) {
/* 209 */             SunToolkit.executeOnEventHandlerThread(this.xtiPeer.target, new Runnable() {
/*     */               public void run() {
/* 211 */                 SystemTray.getSystemTray().remove(XTrayIconPeer.4.this.xtiPeer.target);
/*     */               }
/*     */             });
/*     */           }
/* 215 */           return;
/*     */         }
/*     */ 
/* 218 */         if (!XTrayIconPeer.this.isTrayIconDisplayed) {
/* 219 */           XTrayIconPeer.this.addXED(XTrayIconPeer.this.eframeParentID, XTrayIconPeer.this.parentXED, 131072L);
/*     */ 
/* 221 */           XTrayIconPeer.this.isTrayIconDisplayed = true;
/* 222 */           XToolkit.awtLockNotifyAll();
/*     */         }
/*     */       }
/*     */     };
/* 227 */     addXED(getWindow(), this.eframeXED, 131072L);
/*     */ 
/* 229 */     XSystemTrayPeer.getPeerInstance().addTrayIcon(this);
/*     */ 
/* 232 */     long l1 = System.currentTimeMillis();
/* 233 */     long l2 = XToolkit.getTrayIconDisplayTimeout();
/* 234 */     XToolkit.awtLock();
/*     */     try {
/* 236 */       while (!this.isTrayIconDisplayed) {
/*     */         try {
/* 238 */           XToolkit.awtLockWait(l2);
/*     */         } catch (InterruptedException localInterruptedException) {
/* 240 */           break;
/*     */         }
/* 242 */         if (System.currentTimeMillis() - l1 > l2)
/* 243 */           break;
/*     */       }
/*     */     }
/*     */     finally {
/* 247 */       XToolkit.awtUnlock();
/*     */     }
/*     */ 
/* 251 */     if ((!this.isTrayIconDisplayed) || (this.eframeParentID == 0L) || (this.eframeParentID == XToolkit.getDefaultRootWindow()))
/*     */     {
/* 254 */       throw new AWTException("TrayIcon couldn't be displayed.");
/*     */     }
/*     */ 
/* 257 */     this.eframe.setVisible(true);
/* 258 */     updateImage();
/*     */ 
/* 260 */     this.balloon = new InfoWindow.Balloon(this.eframe, paramTrayIcon, this);
/* 261 */     this.tooltip = new InfoWindow.Tooltip(this.eframe, paramTrayIcon, this);
/*     */ 
/* 263 */     addListeners(); }
/*     */ 
/*     */   public void dispose()
/*     */   {
/* 267 */     if (SunToolkit.isDispatchThreadForAppContext(this.target))
/* 268 */       disposeOnEDT();
/*     */     else
/*     */       try {
/* 271 */         SunToolkit.executeOnEDTAndWait(this.target, new Runnable() {
/*     */           public void run() {
/* 273 */             XTrayIconPeer.this.disposeOnEDT();
/*     */           }
/*     */         });
/*     */       }
/*     */       catch (InterruptedException localInterruptedException)
/*     */       {
/*     */       }
/*     */       catch (InvocationTargetException localInvocationTargetException) {
/*     */       }
/*     */   }
/*     */ 
/*     */   private void disposeOnEDT() {
/* 285 */     XToolkit.awtLock();
/* 286 */     this.isDisposed = true;
/* 287 */     XToolkit.awtUnlock();
/*     */ 
/* 289 */     removeXED(getWindow(), this.eframeXED);
/* 290 */     removeXED(this.eframeParentID, this.parentXED);
/* 291 */     this.eframe.realDispose();
/* 292 */     this.balloon.dispose();
/* 293 */     this.isTrayIconDisplayed = false;
/* 294 */     XToolkit.targetDisposedPeer(this.target, this);
/*     */   }
/*     */ 
/*     */   public static void suppressWarningString(Window paramWindow) {
/* 298 */     AWTAccessor.getWindowAccessor().setTrayIconWindow(paramWindow, true);
/*     */   }
/*     */ 
/*     */   public void setToolTip(String paramString) {
/* 302 */     this.tooltipString = paramString;
/*     */   }
/*     */ 
/*     */   public String getTooltipString() {
/* 306 */     return this.tooltipString;
/*     */   }
/*     */ 
/*     */   public void updateImage() {
/* 310 */     Runnable local6 = new Runnable() {
/*     */       public void run() {
/* 312 */         XTrayIconPeer.this.canvas.updateImage(XTrayIconPeer.this.target.getImage());
/*     */       }
/*     */     };
/* 316 */     if (!SunToolkit.isDispatchThreadForAppContext(this.target))
/* 317 */       SunToolkit.executeOnEventHandlerThread(this.target, local6);
/*     */     else
/* 319 */       local6.run();
/*     */   }
/*     */ 
/*     */   public void displayMessage(String paramString1, String paramString2, String paramString3)
/*     */   {
/* 324 */     Point localPoint = getLocationOnScreen();
/* 325 */     Rectangle localRectangle = this.eframe.getGraphicsConfiguration().getBounds();
/*     */ 
/* 328 */     if ((localPoint.x >= localRectangle.x) && (localPoint.x < localRectangle.x + localRectangle.width) && (localPoint.y >= localRectangle.y) && (localPoint.y < localRectangle.y + localRectangle.height))
/*     */     {
/* 331 */       this.balloon.display(paramString1, paramString2, paramString3);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void showPopupMenu(int paramInt1, int paramInt2)
/*     */   {
/* 337 */     if (isDisposed()) {
/* 338 */       return;
/*     */     }
/* 340 */     assert (SunToolkit.isDispatchThreadForAppContext(this.target));
/*     */ 
/* 342 */     PopupMenu localPopupMenu = this.target.getPopupMenu();
/* 343 */     if (this.popup != localPopupMenu) {
/* 344 */       if (this.popup != null) {
/* 345 */         this.eframe.remove(this.popup);
/*     */       }
/* 347 */       if (localPopupMenu != null) {
/* 348 */         this.eframe.add(localPopupMenu);
/*     */       }
/* 350 */       this.popup = localPopupMenu;
/*     */     }
/*     */ 
/* 353 */     if (this.popup != null) {
/* 354 */       Point localPoint = ((XBaseWindow)this.eframe.getPeer()).toLocal(new Point(paramInt1, paramInt2));
/* 355 */       this.popup.show(this.eframe, localPoint.x, localPoint.y);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void addXED(long paramLong1, XEventDispatcher paramXEventDispatcher, long paramLong2)
/*     */   {
/* 365 */     if (paramLong1 == 0L) {
/* 366 */       return;
/*     */     }
/* 368 */     XToolkit.awtLock();
/*     */     try {
/* 370 */       XlibWrapper.XSelectInput(XToolkit.getDisplay(), paramLong1, paramLong2);
/*     */     } finally {
/* 372 */       XToolkit.awtUnlock();
/*     */     }
/* 374 */     XToolkit.addEventDispatcher(paramLong1, paramXEventDispatcher);
/*     */   }
/*     */ 
/*     */   private void removeXED(long paramLong, XEventDispatcher paramXEventDispatcher) {
/* 378 */     if (paramLong == 0L) {
/* 379 */       return;
/*     */     }
/* 381 */     XToolkit.awtLock();
/*     */     try {
/* 383 */       XToolkit.removeEventDispatcher(paramLong, paramXEventDispatcher);
/*     */     } finally {
/* 385 */       XToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   private Point getLocationOnScreen()
/*     */   {
/* 391 */     return this.eframe.getLocationOnScreen();
/*     */   }
/*     */ 
/*     */   public Rectangle getBounds() {
/* 395 */     Point localPoint = getLocationOnScreen();
/* 396 */     return new Rectangle(localPoint.x, localPoint.y, localPoint.x + 24, localPoint.y + 24);
/*     */   }
/*     */ 
/*     */   void addListeners() {
/* 400 */     this.canvas.addMouseListener(this.eventProxy);
/* 401 */     this.canvas.addMouseMotionListener(this.eventProxy);
/*     */   }
/*     */ 
/*     */   long getWindow() {
/* 405 */     return ((XEmbeddedFramePeer)this.eframe.getPeer()).getWindow();
/*     */   }
/*     */ 
/*     */   public boolean isDisposed() {
/* 409 */     return this.isDisposed;
/*     */   }
/*     */ 
/*     */   public String getActionCommand() {
/* 413 */     return this.target.getActionCommand();
/*     */   }
/*     */ 
/*     */   public static class IconCanvas extends Canvas
/*     */   {
/*     */     volatile Image image;
/*     */     IconObserver observer;
/*     */     int width;
/*     */     int height;
/*     */     int curW;
/*     */     int curH;
/*     */ 
/*     */     IconCanvas(int paramInt1, int paramInt2)
/*     */     {
/* 544 */       this.width = (this.curW = paramInt1);
/* 545 */       this.height = (this.curH = paramInt2);
/*     */     }
/*     */ 
/*     */     public void updateImage(Image paramImage)
/*     */     {
/* 550 */       this.image = paramImage;
/* 551 */       if (this.observer == null) {
/* 552 */         this.observer = new IconObserver();
/*     */       }
/* 554 */       repaintImage(true);
/*     */     }
/*     */ 
/*     */     protected void repaintImage(boolean paramBoolean)
/*     */     {
/* 559 */       Graphics localGraphics = getGraphics();
/* 560 */       if (localGraphics != null)
/*     */         try {
/* 562 */           if (isVisible())
/* 563 */             if (paramBoolean)
/* 564 */               update(localGraphics);
/*     */             else
/* 566 */               paint(localGraphics);
/*     */         }
/*     */         finally
/*     */         {
/* 570 */           localGraphics.dispose();
/*     */         }
/*     */     }
/*     */ 
/*     */     public void paint(Graphics paramGraphics)
/*     */     {
/* 577 */       if ((paramGraphics != null) && (this.curW > 0) && (this.curH > 0)) {
/* 578 */         BufferedImage localBufferedImage = new BufferedImage(this.curW, this.curH, 2);
/* 579 */         Graphics2D localGraphics2D = localBufferedImage.createGraphics();
/* 580 */         if (localGraphics2D != null)
/*     */           try {
/* 582 */             localGraphics2D.setColor(getBackground());
/* 583 */             localGraphics2D.fillRect(0, 0, this.curW, this.curH);
/* 584 */             localGraphics2D.drawImage(this.image, 0, 0, this.curW, this.curH, this.observer);
/* 585 */             localGraphics2D.dispose();
/*     */ 
/* 587 */             paramGraphics.drawImage(localBufferedImage, 0, 0, this.curW, this.curH, null);
/*     */           } finally {
/* 589 */             localGraphics2D.dispose();
/*     */           } 
/*     */       }
/*     */     }
/*     */ 
/*     */     class IconObserver implements ImageObserver {
/*     */       IconObserver() {  }
/*     */ 
/*     */ 
/* 597 */       public boolean imageUpdate(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { if ((paramImage != XTrayIconPeer.IconCanvas.this.image) || (!XTrayIconPeer.IconCanvas.this.isVisible()))
/*     */         {
/* 600 */           return false;
/*     */         }
/* 602 */         if ((paramInt1 & 0x33) != 0)
/*     */         {
/* 605 */           SunToolkit.executeOnEventHandlerThread(XTrayIconPeer.IconCanvas.this, new Runnable() {
/*     */             public void run() {
/* 607 */               XTrayIconPeer.IconCanvas.this.repaintImage(false);
/*     */             }
/*     */           });
/*     */         }
/* 611 */         return (paramInt1 & 0x20) == 0;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   static class TrayIconCanvas extends XTrayIconPeer.IconCanvas
/*     */   {
/*     */     TrayIcon target;
/*     */     boolean autosize;
/*     */ 
/*     */     TrayIconCanvas(TrayIcon paramTrayIcon, int paramInt1, int paramInt2)
/*     */     {
/* 521 */       super(paramInt2);
/* 522 */       this.target = paramTrayIcon;
/*     */     }
/*     */ 
/*     */     protected void repaintImage(boolean paramBoolean)
/*     */     {
/* 527 */       boolean bool = this.autosize;
/* 528 */       this.autosize = this.target.isImageAutoSize();
/*     */ 
/* 530 */       this.curW = (this.autosize ? this.width : this.image.getWidth(this.observer));
/* 531 */       this.curH = (this.autosize ? this.height : this.image.getHeight(this.observer));
/*     */ 
/* 533 */       super.repaintImage((paramBoolean) || (bool != this.autosize));
/*     */     }
/*     */   }
/*     */ 
/*     */   static class TrayIconEventProxy
/*     */     implements MouseListener, MouseMotionListener
/*     */   {
/*     */     XTrayIconPeer xtiPeer;
/*     */ 
/*     */     TrayIconEventProxy(XTrayIconPeer paramXTrayIconPeer)
/*     */     {
/* 420 */       this.xtiPeer = paramXTrayIconPeer;
/*     */     }
/*     */ 
/*     */     public void handleEvent(MouseEvent paramMouseEvent)
/*     */     {
/* 425 */       if (paramMouseEvent.getID() == 506) {
/* 426 */         return;
/*     */       }
/*     */ 
/* 430 */       if (this.xtiPeer.isDisposed()) {
/* 431 */         return;
/*     */       }
/* 433 */       Point localPoint = XBaseWindow.toOtherWindow(this.xtiPeer.getWindow(), XToolkit.getDefaultRootWindow(), paramMouseEvent.getX(), paramMouseEvent.getY());
/*     */ 
/* 437 */       if (paramMouseEvent.isPopupTrigger()) {
/* 438 */         this.xtiPeer.showPopupMenu(localPoint.x, localPoint.y);
/*     */       }
/*     */ 
/* 441 */       paramMouseEvent.translatePoint(localPoint.x - paramMouseEvent.getX(), localPoint.y - paramMouseEvent.getY());
/*     */ 
/* 447 */       paramMouseEvent.setSource(this.xtiPeer.target);
/* 448 */       Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(paramMouseEvent);
/*     */     }
/*     */     public void mouseClicked(MouseEvent paramMouseEvent) {
/* 451 */       if (((paramMouseEvent.getClickCount() > 1) || (this.xtiPeer.balloon.isVisible())) && (paramMouseEvent.getButton() == 1))
/*     */       {
/* 454 */         ActionEvent localActionEvent = new ActionEvent(this.xtiPeer.target, 1001, this.xtiPeer.target.getActionCommand(), paramMouseEvent.getWhen(), paramMouseEvent.getModifiers());
/*     */ 
/* 457 */         XToolkit.postEvent(XToolkit.targetToAppContext(localActionEvent.getSource()), localActionEvent);
/*     */       }
/* 459 */       if (this.xtiPeer.balloon.isVisible()) {
/* 460 */         this.xtiPeer.balloon.hide();
/*     */       }
/* 462 */       handleEvent(paramMouseEvent);
/*     */     }
/*     */     public void mouseEntered(MouseEvent paramMouseEvent) {
/* 465 */       this.xtiPeer.tooltip.enter();
/* 466 */       handleEvent(paramMouseEvent);
/*     */     }
/*     */     public void mouseExited(MouseEvent paramMouseEvent) {
/* 469 */       this.xtiPeer.tooltip.exit();
/* 470 */       handleEvent(paramMouseEvent);
/*     */     }
/*     */     public void mousePressed(MouseEvent paramMouseEvent) {
/* 473 */       handleEvent(paramMouseEvent);
/*     */     }
/*     */     public void mouseReleased(MouseEvent paramMouseEvent) {
/* 476 */       handleEvent(paramMouseEvent);
/*     */     }
/*     */     public void mouseDragged(MouseEvent paramMouseEvent) {
/* 479 */       handleEvent(paramMouseEvent);
/*     */     }
/*     */     public void mouseMoved(MouseEvent paramMouseEvent) {
/* 482 */       handleEvent(paramMouseEvent);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class XTrayIconEmbeddedFrame extends XEmbeddedFrame
/*     */   {
/*     */     public XTrayIconEmbeddedFrame()
/*     */     {
/* 492 */       super(true, true);
/*     */     }
/*     */ 
/*     */     public boolean isUndecorated() {
/* 496 */       return true;
/*     */     }
/*     */ 
/*     */     public boolean isResizable() {
/* 500 */       return false;
/*     */     }
/*     */ 
/*     */     public void dispose()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void realDispose() {
/* 508 */       super.dispose();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XTrayIconPeer
 * JD-Core Version:    0.6.2
 */