/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Button;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.Frame;
/*     */ import java.awt.GridLayout;
/*     */ import java.awt.Image;
/*     */ import java.awt.Insets;
/*     */ import java.awt.Label;
/*     */ import java.awt.MouseInfo;
/*     */ import java.awt.Panel;
/*     */ import java.awt.Point;
/*     */ import java.awt.PointerInfo;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.Window;
/*     */ import java.awt.Window.Type;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.text.BreakIterator;
/*     */ import java.util.concurrent.ArrayBlockingQueue;
/*     */ import sun.awt.SunToolkit;
/*     */ 
/*     */ public abstract class InfoWindow extends Window
/*     */ {
/*     */   private Container container;
/*     */   private Closer closer;
/*     */ 
/*     */   protected InfoWindow(Frame paramFrame, Color paramColor)
/*     */   {
/*  47 */     super(paramFrame);
/*  48 */     setType(Window.Type.POPUP);
/*  49 */     this.container = new Container()
/*     */     {
/*     */       public Insets getInsets() {
/*  52 */         return new Insets(1, 1, 1, 1);
/*     */       }
/*     */     };
/*  55 */     setLayout(new BorderLayout());
/*  56 */     setBackground(paramColor);
/*  57 */     add(this.container, "Center");
/*  58 */     this.container.setLayout(new BorderLayout());
/*     */ 
/*  60 */     this.closer = new Closer(null);
/*     */   }
/*     */ 
/*     */   public Component add(Component paramComponent) {
/*  64 */     this.container.add(paramComponent, "Center");
/*  65 */     return paramComponent;
/*     */   }
/*     */ 
/*     */   protected void setCloser(Runnable paramRunnable, int paramInt) {
/*  69 */     this.closer.set(paramRunnable, paramInt);
/*     */   }
/*     */ 
/*     */   protected void show(Point paramPoint, int paramInt)
/*     */   {
/*  74 */     assert (SunToolkit.isDispatchThreadForAppContext(this));
/*     */ 
/*  76 */     pack();
/*     */ 
/*  78 */     Dimension localDimension1 = getSize();
/*     */ 
/*  81 */     Dimension localDimension2 = Toolkit.getDefaultToolkit().getScreenSize();
/*     */ 
/*  83 */     if ((paramPoint.x < localDimension2.width / 2) && (paramPoint.y < localDimension2.height / 2)) {
/*  84 */       setLocation(paramPoint.x + paramInt, paramPoint.y + paramInt);
/*     */     }
/*  86 */     else if ((paramPoint.x >= localDimension2.width / 2) && (paramPoint.y < localDimension2.height / 2)) {
/*  87 */       setLocation(paramPoint.x - paramInt - localDimension1.width, paramPoint.y + paramInt);
/*     */     }
/*  89 */     else if ((paramPoint.x < localDimension2.width / 2) && (paramPoint.y >= localDimension2.height / 2)) {
/*  90 */       setLocation(paramPoint.x + paramInt, paramPoint.y - paramInt - localDimension1.height);
/*     */     }
/*  92 */     else if ((paramPoint.x >= localDimension2.width / 2) && (paramPoint.y >= localDimension2.height / 2)) {
/*  93 */       setLocation(paramPoint.x - paramInt - localDimension1.width, paramPoint.y - paramInt - localDimension1.height);
/*     */     }
/*     */ 
/*  96 */     super.show();
/*  97 */     this.closer.schedule();
/*     */   }
/*     */ 
/*     */   public void hide() {
/* 101 */     this.closer.close();
/*     */   }
/*     */ 
/*     */   public static class Balloon extends InfoWindow
/*     */   {
/*     */     private final LiveArguments liveArguments;
/*     */     private final Object target;
/*     */     private static final int BALLOON_SHOW_TIME = 10000;
/*     */     private static final int BALLOON_TEXT_MAX_LENGTH = 256;
/*     */     private static final int BALLOON_WORD_LINE_MAX_LENGTH = 16;
/*     */     private static final int BALLOON_WORD_LINE_MAX_COUNT = 4;
/*     */     private static final int BALLOON_ICON_WIDTH = 32;
/*     */     private static final int BALLOON_ICON_HEIGHT = 32;
/*     */     private static final int BALLOON_TRAY_ICON_INDENT = 0;
/* 259 */     private static final Color BALLOON_CAPTION_BACKGROUND_COLOR = new Color(200, 200, 255);
/* 260 */     private static final Font BALLOON_CAPTION_FONT = new Font("Dialog", 1, 12);
/*     */ 
/* 262 */     private Panel mainPanel = new Panel();
/* 263 */     private Panel captionPanel = new Panel();
/* 264 */     private Label captionLabel = new Label("");
/* 265 */     private Button closeButton = new Button("X");
/* 266 */     private Panel textPanel = new Panel();
/* 267 */     private XTrayIconPeer.IconCanvas iconCanvas = new XTrayIconPeer.IconCanvas(32, 32);
/* 268 */     private Label[] lineLabels = new Label[4];
/* 269 */     private ActionPerformer ap = new ActionPerformer(null);
/*     */     private Image iconImage;
/*     */     private Image errorImage;
/*     */     private Image warnImage;
/*     */     private Image infoImage;
/*     */     private boolean gtkImagesLoaded;
/* 277 */     private Displayer displayer = new Displayer();
/*     */ 
/*     */     public Balloon(Frame paramFrame, Object paramObject, LiveArguments paramLiveArguments) {
/* 280 */       super(new Color(90, 80, 190));
/* 281 */       this.liveArguments = paramLiveArguments;
/* 282 */       this.target = paramObject;
/*     */ 
/* 284 */       XTrayIconPeer.suppressWarningString(this);
/*     */ 
/* 286 */       setCloser(new Runnable() {
/*     */         public void run() {
/* 288 */           if (InfoWindow.Balloon.this.textPanel != null) {
/* 289 */             InfoWindow.Balloon.this.textPanel.removeAll();
/* 290 */             InfoWindow.Balloon.this.textPanel.setSize(0, 0);
/* 291 */             InfoWindow.Balloon.this.iconCanvas.setSize(0, 0);
/* 292 */             XToolkit.awtLock();
/*     */             try {
/* 294 */               InfoWindow.Balloon.this.displayer.isDisplayed = false;
/* 295 */               XToolkit.awtLockNotifyAll();
/*     */             } finally {
/* 297 */               XToolkit.awtUnlock();
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       , 10000);
/*     */ 
/* 303 */       add(this.mainPanel);
/*     */ 
/* 305 */       this.captionLabel.setFont(BALLOON_CAPTION_FONT);
/* 306 */       this.captionLabel.addMouseListener(this.ap);
/*     */ 
/* 308 */       this.captionPanel.setLayout(new BorderLayout());
/* 309 */       this.captionPanel.add(this.captionLabel, "West");
/* 310 */       this.captionPanel.add(this.closeButton, "East");
/* 311 */       this.captionPanel.setBackground(BALLOON_CAPTION_BACKGROUND_COLOR);
/* 312 */       this.captionPanel.addMouseListener(this.ap);
/*     */ 
/* 314 */       this.closeButton.addActionListener(new ActionListener() {
/*     */         public void actionPerformed(ActionEvent paramAnonymousActionEvent) {
/* 316 */           InfoWindow.Balloon.this.hide();
/*     */         }
/*     */       });
/* 320 */       this.mainPanel.setLayout(new BorderLayout());
/* 321 */       this.mainPanel.setBackground(Color.white);
/* 322 */       this.mainPanel.add(this.captionPanel, "North");
/* 323 */       this.mainPanel.add(this.iconCanvas, "West");
/* 324 */       this.mainPanel.add(this.textPanel, "Center");
/*     */ 
/* 326 */       this.iconCanvas.addMouseListener(this.ap);
/*     */ 
/* 328 */       for (int i = 0; i < 4; i++) {
/* 329 */         this.lineLabels[i] = new Label();
/* 330 */         this.lineLabels[i].addMouseListener(this.ap);
/* 331 */         this.lineLabels[i].setBackground(Color.white);
/*     */       }
/*     */ 
/* 334 */       this.displayer.start();
/*     */     }
/*     */ 
/*     */     public void display(String paramString1, String paramString2, String paramString3) {
/* 338 */       if (!this.gtkImagesLoaded) {
/* 339 */         loadGtkImages();
/*     */       }
/* 341 */       this.displayer.display(paramString1, paramString2, paramString3);
/*     */     }
/*     */ 
/*     */     private void _display(String paramString1, String paramString2, String paramString3) {
/* 345 */       this.captionLabel.setText(paramString1);
/*     */ 
/* 347 */       BreakIterator localBreakIterator = BreakIterator.getWordInstance();
/* 348 */       if (paramString2 != null) { localBreakIterator.setText(paramString2);
/* 350 */         int i = localBreakIterator.first();
/* 351 */         int k = 0;
/*     */         int j;
/*     */         do { j = localBreakIterator.next();
/*     */ 
/* 356 */           if ((j == -1) || (paramString2.substring(i, j).length() >= 50))
/*     */           {
/* 359 */             this.lineLabels[k].setText(paramString2.substring(i, j == -1 ? localBreakIterator.last() : j));
/*     */ 
/* 361 */             this.textPanel.add(this.lineLabels[(k++)]);
/* 362 */             i = j;
/*     */           }
/* 364 */           if (k == 4) {
/* 365 */             if (j == -1) break;
/* 366 */             this.lineLabels[(k - 1)].setText(new String(this.lineLabels[(k - 1)].getText() + " ...")); break;
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 371 */         while (j != -1);
/*     */ 
/* 374 */         this.textPanel.setLayout(new GridLayout(k, 1));
/*     */       }
/*     */ 
/* 377 */       if ("ERROR".equals(paramString3))
/* 378 */         this.iconImage = this.errorImage;
/* 379 */       else if ("WARNING".equals(paramString3))
/* 380 */         this.iconImage = this.warnImage;
/* 381 */       else if ("INFO".equals(paramString3))
/* 382 */         this.iconImage = this.infoImage;
/*     */       else {
/* 384 */         this.iconImage = null;
/*     */       }
/*     */ 
/* 387 */       if (this.iconImage != null) {
/* 388 */         Dimension localDimension = this.textPanel.getSize();
/* 389 */         this.iconCanvas.setSize(32, 32 > localDimension.height ? 32 : localDimension.height);
/*     */ 
/* 391 */         this.iconCanvas.validate();
/*     */       }
/*     */ 
/* 394 */       SunToolkit.executeOnEventHandlerThread(this.target, new Runnable() {
/*     */         public void run() {
/* 396 */           if (InfoWindow.Balloon.this.liveArguments.isDisposed()) {
/* 397 */             return;
/*     */           }
/* 399 */           Point localPoint = InfoWindow.Balloon.this.getParent().getLocationOnScreen();
/* 400 */           Dimension localDimension = InfoWindow.Balloon.this.getParent().getSize();
/* 401 */           InfoWindow.Balloon.this.show(new Point(localPoint.x + localDimension.width / 2, localPoint.y + localDimension.height / 2), 0);
/*     */ 
/* 403 */           if (InfoWindow.Balloon.this.iconImage != null)
/* 404 */             InfoWindow.Balloon.this.iconCanvas.updateImage(InfoWindow.Balloon.this.iconImage);
/*     */         }
/*     */       });
/*     */     }
/*     */ 
/*     */     public void dispose()
/*     */     {
/* 411 */       this.displayer.interrupt();
/* 412 */       super.dispose();
/*     */     }
/*     */ 
/*     */     private void loadGtkImages() {
/* 416 */       if (!this.gtkImagesLoaded) {
/* 417 */         this.errorImage = ((Image)Toolkit.getDefaultToolkit().getDesktopProperty("gtk.icon.gtk-dialog-error.6.rtl"));
/*     */ 
/* 419 */         this.warnImage = ((Image)Toolkit.getDefaultToolkit().getDesktopProperty("gtk.icon.gtk-dialog-warning.6.rtl"));
/*     */ 
/* 421 */         this.infoImage = ((Image)Toolkit.getDefaultToolkit().getDesktopProperty("gtk.icon.gtk-dialog-info.6.rtl"));
/*     */ 
/* 423 */         this.gtkImagesLoaded = true;
/*     */       }
/*     */     }
/*     */     private class ActionPerformer extends MouseAdapter {
/*     */       private ActionPerformer() {
/*     */       }
/*     */ 
/* 430 */       public void mouseClicked(MouseEvent paramMouseEvent) { InfoWindow.Balloon.this.hide();
/* 431 */         if (paramMouseEvent.getButton() == 1) {
/* 432 */           ActionEvent localActionEvent = new ActionEvent(InfoWindow.Balloon.this.target, 1001, InfoWindow.Balloon.this.liveArguments.getActionCommand(), paramMouseEvent.getWhen(), paramMouseEvent.getModifiers());
/*     */ 
/* 435 */           XToolkit.postEvent(XToolkit.targetToAppContext(localActionEvent.getSource()), localActionEvent);
/*     */         } } 
/*     */     }
/*     */ 
/*     */     private class Displayer extends Thread {
/* 441 */       final int MAX_CONCURRENT_MSGS = 10;
/*     */ 
/* 443 */       ArrayBlockingQueue<InfoWindow.Balloon.Message> messageQueue = new ArrayBlockingQueue(10);
/*     */       boolean isDisplayed;
/*     */ 
/* 447 */       Displayer() { setDaemon(true); }
/*     */ 
/*     */       public void run()
/*     */       {
/*     */         while (true) {
/* 452 */           InfoWindow.Balloon.Message localMessage = null;
/*     */           try {
/* 454 */             localMessage = (InfoWindow.Balloon.Message)this.messageQueue.take();
/*     */           } catch (InterruptedException localInterruptedException1) {
/* 456 */             return;
/*     */           }
/*     */ 
/* 462 */           XToolkit.awtLock();
/*     */           try {
/* 464 */             if (this.isDisplayed)
/*     */               try
/*     */               {
/*     */               }
/*     */               catch (InterruptedException localInterruptedException2) {
/*     */                 return;
/*     */               }
/* 471 */             this.isDisplayed = true;
/*     */           } finally {
/* 473 */             XToolkit.awtUnlock();
/*     */           }
/* 475 */           InfoWindow.Balloon.this._display(localMessage.caption, localMessage.text, localMessage.messageType);
/*     */         }
/*     */       }
/*     */ 
/*     */       void display(String paramString1, String paramString2, String paramString3) {
/* 480 */         this.messageQueue.offer(new InfoWindow.Balloon.Message(paramString1, paramString2, paramString3)); }  } 
/*     */     public static abstract interface LiveArguments extends InfoWindow.LiveArguments { public abstract String getActionCommand(); } 
/*     */     private static class Message { String caption;
/*     */       String text;
/*     */       String messageType;
/*     */ 
/* 488 */       Message(String paramString1, String paramString2, String paramString3) { this.caption = paramString1;
/* 489 */         this.text = paramString2;
/* 490 */         this.messageType = paramString3;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private class Closer
/*     */     implements Runnable
/*     */   {
/*     */     Runnable action;
/*     */     int time;
/*     */ 
/*     */     private Closer()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void run()
/*     */     {
/* 109 */       doClose();
/*     */     }
/*     */ 
/*     */     void set(Runnable paramRunnable, int paramInt) {
/* 113 */       this.action = paramRunnable;
/* 114 */       this.time = paramInt;
/*     */     }
/*     */ 
/*     */     void schedule() {
/* 118 */       XToolkit.schedule(this, this.time);
/*     */     }
/*     */ 
/*     */     void close() {
/* 122 */       XToolkit.remove(this);
/* 123 */       doClose();
/*     */     }
/*     */ 
/*     */     private void doClose()
/*     */     {
/* 128 */       SunToolkit.executeOnEventHandlerThread(InfoWindow.this, new Runnable() {
/*     */         public void run() {
/* 130 */           InfoWindow.this.hide();
/* 131 */           InfoWindow.this.invalidate();
/* 132 */           if (InfoWindow.Closer.this.action != null)
/* 133 */             InfoWindow.Closer.this.action.run();
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */ 
/*     */   private static abstract interface LiveArguments
/*     */   {
/*     */     public abstract boolean isDisposed();
/*     */ 
/*     */     public abstract Rectangle getBounds();
/*     */   }
/*     */ 
/*     */   public static class Tooltip extends InfoWindow
/*     */   {
/*     */     private final Object target;
/*     */     private final LiveArguments liveArguments;
/* 159 */     private final Label textLabel = new Label("");
/* 160 */     private final Runnable starter = new Runnable() {
/*     */       public void run() {
/* 162 */         InfoWindow.Tooltip.this.display();
/*     */       }
/* 160 */     };
/*     */     private static final int TOOLTIP_SHOW_TIME = 10000;
/*     */     private static final int TOOLTIP_START_DELAY_TIME = 1000;
/*     */     private static final int TOOLTIP_MAX_LENGTH = 64;
/*     */     private static final int TOOLTIP_MOUSE_CURSOR_INDENT = 5;
/* 169 */     private static final Color TOOLTIP_BACKGROUND_COLOR = new Color(255, 255, 220);
/* 170 */     private static final Font TOOLTIP_TEXT_FONT = XWindow.getDefaultFont();
/*     */ 
/*     */     public Tooltip(Frame paramFrame, Object paramObject, LiveArguments paramLiveArguments)
/*     */     {
/* 175 */       super(Color.black);
/*     */ 
/* 177 */       this.target = paramObject;
/* 178 */       this.liveArguments = paramLiveArguments;
/*     */ 
/* 180 */       XTrayIconPeer.suppressWarningString(this);
/*     */ 
/* 182 */       setCloser(null, 10000);
/* 183 */       this.textLabel.setBackground(TOOLTIP_BACKGROUND_COLOR);
/* 184 */       this.textLabel.setFont(TOOLTIP_TEXT_FONT);
/* 185 */       add(this.textLabel);
/*     */     }
/*     */ 
/*     */     private void display()
/*     */     {
/* 193 */       SunToolkit.executeOnEventHandlerThread(this.target, new Runnable() {
/*     */         public void run() {
/* 195 */           if (InfoWindow.Tooltip.this.liveArguments.isDisposed()) {
/* 196 */             return;
/*     */           }
/*     */ 
/* 199 */           String str = InfoWindow.Tooltip.this.liveArguments.getTooltipString();
/* 200 */           if (str == null)
/* 201 */             return;
/* 202 */           if (str.length() > 64)
/* 203 */             InfoWindow.Tooltip.this.textLabel.setText(str.substring(0, 64));
/*     */           else {
/* 205 */             InfoWindow.Tooltip.this.textLabel.setText(str);
/*     */           }
/*     */ 
/* 208 */           Point localPoint = (Point)AccessController.doPrivileged(new PrivilegedAction() {
/*     */             public Object run() {
/* 210 */               if (!InfoWindow.Tooltip.this.isPointerOverTrayIcon(InfoWindow.Tooltip.this.liveArguments.getBounds())) {
/* 211 */                 return null;
/*     */               }
/* 213 */               return MouseInfo.getPointerInfo().getLocation();
/*     */             }
/*     */           });
/* 216 */           if (localPoint == null) {
/* 217 */             return;
/*     */           }
/* 219 */           InfoWindow.Tooltip.this.show(new Point(localPoint.x, localPoint.y), 5);
/*     */         }
/*     */       });
/*     */     }
/*     */ 
/*     */     public void enter() {
/* 225 */       XToolkit.schedule(this.starter, 1000L);
/*     */     }
/*     */ 
/*     */     public void exit() {
/* 229 */       XToolkit.remove(this.starter);
/* 230 */       if (isVisible())
/* 231 */         hide();
/*     */     }
/*     */ 
/*     */     private boolean isPointerOverTrayIcon(Rectangle paramRectangle)
/*     */     {
/* 236 */       Point localPoint = MouseInfo.getPointerInfo().getLocation();
/* 237 */       return (localPoint.x >= paramRectangle.x) && (localPoint.x <= paramRectangle.x + paramRectangle.width) && (localPoint.y >= paramRectangle.y) && (localPoint.y <= paramRectangle.y + paramRectangle.height);
/*     */     }
/*     */ 
/*     */     public static abstract interface LiveArguments extends InfoWindow.LiveArguments
/*     */     {
/*     */       public abstract String getTooltipString();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.InfoWindow
 * JD-Core Version:    0.6.2
 */