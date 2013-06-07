/*     */ package javax.swing;
/*     */ 
/*     */ import java.awt.Component;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Frame;
/*     */ import java.awt.GraphicsConfiguration;
/*     */ import java.awt.Insets;
/*     */ import java.awt.KeyboardFocusManager;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.Window;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.FocusAdapter;
/*     */ import java.awt.event.FocusEvent;
/*     */ import java.awt.event.FocusListener;
/*     */ import java.awt.event.KeyAdapter;
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.awt.event.KeyListener;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.event.MouseMotionAdapter;
/*     */ import java.awt.event.MouseMotionListener;
/*     */ 
/*     */ public class ToolTipManager extends MouseAdapter
/*     */   implements MouseMotionListener
/*     */ {
/*     */   Timer enterTimer;
/*     */   Timer exitTimer;
/*     */   Timer insideTimer;
/*     */   String toolTipText;
/*     */   Point preferredLocation;
/*     */   JComponent insideComponent;
/*     */   MouseEvent mouseEvent;
/*     */   boolean showImmediately;
/*  60 */   private static final Object TOOL_TIP_MANAGER_KEY = new Object();
/*     */   transient Popup tipWindow;
/*     */   private Window window;
/*     */   JToolTip tip;
/*  68 */   private Rectangle popupRect = null;
/*  69 */   private Rectangle popupFrameRect = null;
/*     */ 
/*  71 */   boolean enabled = true;
/*  72 */   private boolean tipShowing = false;
/*     */ 
/*  74 */   private FocusListener focusChangeListener = null;
/*  75 */   private MouseMotionListener moveBeforeEnterListener = null;
/*  76 */   private KeyListener accessibilityKeyListener = null;
/*     */   private KeyStroke postTip;
/*     */   private KeyStroke hideTip;
/*  82 */   protected boolean lightWeightPopupEnabled = true;
/*  83 */   protected boolean heavyWeightPopupEnabled = false;
/*     */ 
/*     */   ToolTipManager() {
/*  86 */     this.enterTimer = new Timer(750, new insideTimerAction());
/*  87 */     this.enterTimer.setRepeats(false);
/*  88 */     this.exitTimer = new Timer(500, new outsideTimerAction());
/*  89 */     this.exitTimer.setRepeats(false);
/*  90 */     this.insideTimer = new Timer(4000, new stillInsideTimerAction());
/*  91 */     this.insideTimer.setRepeats(false);
/*     */ 
/*  93 */     this.moveBeforeEnterListener = new MoveBeforeEnterListener(null);
/*  94 */     this.accessibilityKeyListener = new AccessibilityKeyListener(null);
/*     */ 
/*  96 */     this.postTip = KeyStroke.getKeyStroke(112, 2);
/*  97 */     this.hideTip = KeyStroke.getKeyStroke(27, 0);
/*     */   }
/*     */ 
/*     */   public void setEnabled(boolean paramBoolean)
/*     */   {
/* 106 */     this.enabled = paramBoolean;
/* 107 */     if (!paramBoolean)
/* 108 */       hideTipWindow();
/*     */   }
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/* 118 */     return this.enabled;
/*     */   }
/*     */ 
/*     */   public void setLightWeightPopupEnabled(boolean paramBoolean)
/*     */   {
/* 132 */     this.lightWeightPopupEnabled = paramBoolean;
/*     */   }
/*     */ 
/*     */   public boolean isLightWeightPopupEnabled()
/*     */   {
/* 143 */     return this.lightWeightPopupEnabled;
/*     */   }
/*     */ 
/*     */   public void setInitialDelay(int paramInt)
/*     */   {
/* 156 */     this.enterTimer.setInitialDelay(paramInt);
/*     */   }
/*     */ 
/*     */   public int getInitialDelay()
/*     */   {
/* 167 */     return this.enterTimer.getInitialDelay();
/*     */   }
/*     */ 
/*     */   public void setDismissDelay(int paramInt)
/*     */   {
/* 178 */     this.insideTimer.setInitialDelay(paramInt);
/*     */   }
/*     */ 
/*     */   public int getDismissDelay()
/*     */   {
/* 189 */     return this.insideTimer.getInitialDelay();
/*     */   }
/*     */ 
/*     */   public void setReshowDelay(int paramInt)
/*     */   {
/* 207 */     this.exitTimer.setInitialDelay(paramInt);
/*     */   }
/*     */ 
/*     */   public int getReshowDelay()
/*     */   {
/* 217 */     return this.exitTimer.getInitialDelay();
/*     */   }
/*     */ 
/*     */   void showTipWindow() {
/* 221 */     if ((this.insideComponent == null) || (!this.insideComponent.isShowing()))
/* 222 */       return;
/* 223 */     String str = UIManager.getString("ToolTipManager.enableToolTipMode");
/*     */     Object localObject;
/* 224 */     if ("activeApplication".equals(str)) {
/* 225 */       localObject = KeyboardFocusManager.getCurrentKeyboardFocusManager();
/*     */ 
/* 227 */       if (((KeyboardFocusManager)localObject).getFocusedWindow() == null) {
/* 228 */         return;
/*     */       }
/*     */     }
/* 231 */     if (this.enabled)
/*     */     {
/* 233 */       Point localPoint1 = this.insideComponent.getLocationOnScreen();
/* 234 */       Point localPoint2 = new Point();
/*     */ 
/* 236 */       GraphicsConfiguration localGraphicsConfiguration = this.insideComponent.getGraphicsConfiguration();
/* 237 */       Rectangle localRectangle = localGraphicsConfiguration.getBounds();
/* 238 */       Insets localInsets = Toolkit.getDefaultToolkit().getScreenInsets(localGraphicsConfiguration);
/*     */ 
/* 241 */       localRectangle.x += localInsets.left;
/* 242 */       localRectangle.y += localInsets.top;
/* 243 */       localRectangle.width -= localInsets.left + localInsets.right;
/* 244 */       localRectangle.height -= localInsets.top + localInsets.bottom;
/* 245 */       boolean bool = SwingUtilities.isLeftToRight(this.insideComponent);
/*     */ 
/* 249 */       hideTipWindow();
/*     */ 
/* 251 */       this.tip = this.insideComponent.createToolTip();
/* 252 */       this.tip.setTipText(this.toolTipText);
/* 253 */       localObject = this.tip.getPreferredSize();
/*     */ 
/* 255 */       if (this.preferredLocation != null) {
/* 256 */         localPoint1.x += this.preferredLocation.x;
/* 257 */         localPoint1.y += this.preferredLocation.y;
/* 258 */         if (!bool)
/* 259 */           localPoint2.x -= ((Dimension)localObject).width;
/*     */       }
/*     */       else {
/* 262 */         localPoint1.x += this.mouseEvent.getX();
/* 263 */         localPoint2.y = (localPoint1.y + this.mouseEvent.getY() + 20);
/* 264 */         if ((!bool) && 
/* 265 */           (localPoint2.x - ((Dimension)localObject).width >= 0)) {
/* 266 */           localPoint2.x -= ((Dimension)localObject).width;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 273 */       if (this.popupRect == null) {
/* 274 */         this.popupRect = new Rectangle();
/*     */       }
/* 276 */       this.popupRect.setBounds(localPoint2.x, localPoint2.y, ((Dimension)localObject).width, ((Dimension)localObject).height);
/*     */ 
/* 280 */       if (localPoint2.x < localRectangle.x) {
/* 281 */         localPoint2.x = localRectangle.x;
/*     */       }
/* 283 */       else if (localPoint2.x - localRectangle.x + ((Dimension)localObject).width > localRectangle.width) {
/* 284 */         localPoint2.x = (localRectangle.x + Math.max(0, localRectangle.width - ((Dimension)localObject).width));
/*     */       }
/*     */ 
/* 287 */       if (localPoint2.y < localRectangle.y) {
/* 288 */         localPoint2.y = localRectangle.y;
/*     */       }
/* 290 */       else if (localPoint2.y - localRectangle.y + ((Dimension)localObject).height > localRectangle.height) {
/* 291 */         localPoint2.y = (localRectangle.y + Math.max(0, localRectangle.height - ((Dimension)localObject).height));
/*     */       }
/*     */ 
/* 294 */       PopupFactory localPopupFactory = PopupFactory.getSharedInstance();
/*     */ 
/* 296 */       if (this.lightWeightPopupEnabled) {
/* 297 */         int i = getPopupFitHeight(this.popupRect, this.insideComponent);
/* 298 */         int j = getPopupFitWidth(this.popupRect, this.insideComponent);
/* 299 */         if ((j > 0) || (i > 0))
/* 300 */           localPopupFactory.setPopupType(1);
/*     */         else
/* 302 */           localPopupFactory.setPopupType(0);
/*     */       }
/*     */       else
/*     */       {
/* 306 */         localPopupFactory.setPopupType(1);
/*     */       }
/* 308 */       this.tipWindow = localPopupFactory.getPopup(this.insideComponent, this.tip, localPoint2.x, localPoint2.y);
/*     */ 
/* 311 */       localPopupFactory.setPopupType(0);
/*     */ 
/* 313 */       this.tipWindow.show();
/*     */ 
/* 315 */       Window localWindow = SwingUtilities.windowForComponent(this.insideComponent);
/*     */ 
/* 318 */       this.window = SwingUtilities.windowForComponent(this.tip);
/* 319 */       if ((this.window != null) && (this.window != localWindow)) {
/* 320 */         this.window.addMouseListener(this);
/*     */       }
/*     */       else {
/* 323 */         this.window = null;
/*     */       }
/*     */ 
/* 326 */       this.insideTimer.start();
/* 327 */       this.tipShowing = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   void hideTipWindow() {
/* 332 */     if (this.tipWindow != null) {
/* 333 */       if (this.window != null) {
/* 334 */         this.window.removeMouseListener(this);
/* 335 */         this.window = null;
/*     */       }
/* 337 */       this.tipWindow.hide();
/* 338 */       this.tipWindow = null;
/* 339 */       this.tipShowing = false;
/* 340 */       this.tip = null;
/* 341 */       this.insideTimer.stop();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static ToolTipManager sharedInstance()
/*     */   {
/* 351 */     Object localObject = SwingUtilities.appContextGet(TOOL_TIP_MANAGER_KEY);
/* 352 */     if ((localObject instanceof ToolTipManager)) {
/* 353 */       return (ToolTipManager)localObject;
/*     */     }
/* 355 */     ToolTipManager localToolTipManager = new ToolTipManager();
/* 356 */     SwingUtilities.appContextPut(TOOL_TIP_MANAGER_KEY, localToolTipManager);
/* 357 */     return localToolTipManager;
/*     */   }
/*     */ 
/*     */   public void registerComponent(JComponent paramJComponent)
/*     */   {
/* 374 */     paramJComponent.removeMouseListener(this);
/* 375 */     paramJComponent.addMouseListener(this);
/* 376 */     paramJComponent.removeMouseMotionListener(this.moveBeforeEnterListener);
/* 377 */     paramJComponent.addMouseMotionListener(this.moveBeforeEnterListener);
/* 378 */     paramJComponent.removeKeyListener(this.accessibilityKeyListener);
/* 379 */     paramJComponent.addKeyListener(this.accessibilityKeyListener);
/*     */   }
/*     */ 
/*     */   public void unregisterComponent(JComponent paramJComponent)
/*     */   {
/* 388 */     paramJComponent.removeMouseListener(this);
/* 389 */     paramJComponent.removeMouseMotionListener(this.moveBeforeEnterListener);
/* 390 */     paramJComponent.removeKeyListener(this.accessibilityKeyListener);
/*     */   }
/*     */ 
/*     */   public void mouseEntered(MouseEvent paramMouseEvent)
/*     */   {
/* 401 */     initiateToolTip(paramMouseEvent);
/*     */   }
/*     */ 
/*     */   private void initiateToolTip(MouseEvent paramMouseEvent) {
/* 405 */     if (paramMouseEvent.getSource() == this.window) {
/* 406 */       return;
/*     */     }
/* 408 */     JComponent localJComponent = (JComponent)paramMouseEvent.getSource();
/* 409 */     localJComponent.removeMouseMotionListener(this.moveBeforeEnterListener);
/*     */ 
/* 411 */     this.exitTimer.stop();
/*     */ 
/* 413 */     Point localPoint1 = paramMouseEvent.getPoint();
/*     */ 
/* 415 */     if ((localPoint1.x < 0) || (localPoint1.x >= localJComponent.getWidth()) || (localPoint1.y < 0) || (localPoint1.y >= localJComponent.getHeight()))
/*     */     {
/* 419 */       return;
/*     */     }
/*     */ 
/* 422 */     if (this.insideComponent != null) {
/* 423 */       this.enterTimer.stop();
/*     */     }
/*     */ 
/* 428 */     localJComponent.removeMouseMotionListener(this);
/* 429 */     localJComponent.addMouseMotionListener(this);
/*     */ 
/* 431 */     int i = this.insideComponent == localJComponent ? 1 : 0;
/*     */ 
/* 433 */     this.insideComponent = localJComponent;
/* 434 */     if (this.tipWindow != null) {
/* 435 */       this.mouseEvent = paramMouseEvent;
/* 436 */       if (this.showImmediately) {
/* 437 */         String str = localJComponent.getToolTipText(paramMouseEvent);
/* 438 */         Point localPoint2 = localJComponent.getToolTipLocation(paramMouseEvent);
/*     */ 
/* 440 */         int j = localPoint2 == null ? 1 : this.preferredLocation != null ? this.preferredLocation.equals(localPoint2) : 0;
/*     */ 
/* 444 */         if ((i == 0) || (!this.toolTipText.equals(str)) || (j == 0))
/*     */         {
/* 446 */           this.toolTipText = str;
/* 447 */           this.preferredLocation = localPoint2;
/* 448 */           showTipWindow();
/*     */         }
/*     */       } else {
/* 451 */         this.enterTimer.start();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void mouseExited(MouseEvent paramMouseEvent)
/*     */   {
/* 464 */     int i = 1;
/*     */     Object localObject;
/*     */     Point localPoint1;
/* 465 */     if ((this.insideComponent != null) || (
/* 468 */       (this.window != null) && (paramMouseEvent.getSource() == this.window) && (this.insideComponent != null)))
/*     */     {
/* 471 */       localObject = this.insideComponent.getTopLevelAncestor();
/*     */ 
/* 473 */       if (localObject != null) {
/* 474 */         localPoint1 = paramMouseEvent.getPoint();
/* 475 */         SwingUtilities.convertPointToScreen(localPoint1, this.window);
/*     */ 
/* 477 */         localPoint1.x -= ((Container)localObject).getX();
/* 478 */         localPoint1.y -= ((Container)localObject).getY();
/*     */ 
/* 480 */         localPoint1 = SwingUtilities.convertPoint(null, localPoint1, this.insideComponent);
/* 481 */         if ((localPoint1.x >= 0) && (localPoint1.x < this.insideComponent.getWidth()) && (localPoint1.y >= 0) && (localPoint1.y < this.insideComponent.getHeight()))
/*     */         {
/* 483 */           i = 0;
/*     */         }
/* 485 */         else i = 1;
/*     */       }
/*     */     }
/* 488 */     else if ((paramMouseEvent.getSource() == this.insideComponent) && (this.tipWindow != null)) {
/* 489 */       localObject = SwingUtilities.getWindowAncestor(this.insideComponent);
/* 490 */       if (localObject != null) {
/* 491 */         localPoint1 = SwingUtilities.convertPoint(this.insideComponent, paramMouseEvent.getPoint(), (Component)localObject);
/*     */ 
/* 494 */         Rectangle localRectangle = this.insideComponent.getTopLevelAncestor().getBounds();
/* 495 */         localPoint1.x += localRectangle.x;
/* 496 */         localPoint1.y += localRectangle.y;
/*     */ 
/* 498 */         Point localPoint2 = new Point(0, 0);
/* 499 */         SwingUtilities.convertPointToScreen(localPoint2, this.tip);
/* 500 */         localRectangle.x = localPoint2.x;
/* 501 */         localRectangle.y = localPoint2.y;
/* 502 */         localRectangle.width = this.tip.getWidth();
/* 503 */         localRectangle.height = this.tip.getHeight();
/*     */ 
/* 505 */         if ((localPoint1.x >= localRectangle.x) && (localPoint1.x < localRectangle.x + localRectangle.width) && (localPoint1.y >= localRectangle.y) && (localPoint1.y < localRectangle.y + localRectangle.height))
/*     */         {
/* 507 */           i = 0;
/*     */         }
/* 509 */         else i = 1;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 514 */     if (i != 0) {
/* 515 */       this.enterTimer.stop();
/* 516 */       if (this.insideComponent != null) {
/* 517 */         this.insideComponent.removeMouseMotionListener(this);
/*     */       }
/* 519 */       this.insideComponent = null;
/* 520 */       this.toolTipText = null;
/* 521 */       this.mouseEvent = null;
/* 522 */       hideTipWindow();
/* 523 */       this.exitTimer.restart();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void mousePressed(MouseEvent paramMouseEvent)
/*     */   {
/* 535 */     hideTipWindow();
/* 536 */     this.enterTimer.stop();
/* 537 */     this.showImmediately = false;
/* 538 */     this.insideComponent = null;
/* 539 */     this.mouseEvent = null;
/*     */   }
/*     */ 
/*     */   public void mouseDragged(MouseEvent paramMouseEvent)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void mouseMoved(MouseEvent paramMouseEvent)
/*     */   {
/* 560 */     if (this.tipShowing) {
/* 561 */       checkForTipChange(paramMouseEvent);
/*     */     }
/* 563 */     else if (this.showImmediately) {
/* 564 */       JComponent localJComponent = (JComponent)paramMouseEvent.getSource();
/* 565 */       this.toolTipText = localJComponent.getToolTipText(paramMouseEvent);
/* 566 */       if (this.toolTipText != null) {
/* 567 */         this.preferredLocation = localJComponent.getToolTipLocation(paramMouseEvent);
/* 568 */         this.mouseEvent = paramMouseEvent;
/* 569 */         this.insideComponent = localJComponent;
/* 570 */         this.exitTimer.stop();
/* 571 */         showTipWindow();
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 576 */       this.insideComponent = ((JComponent)paramMouseEvent.getSource());
/* 577 */       this.mouseEvent = paramMouseEvent;
/* 578 */       this.toolTipText = null;
/* 579 */       this.enterTimer.restart();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void checkForTipChange(MouseEvent paramMouseEvent)
/*     */   {
/* 588 */     JComponent localJComponent = (JComponent)paramMouseEvent.getSource();
/* 589 */     String str = localJComponent.getToolTipText(paramMouseEvent);
/* 590 */     Point localPoint = localJComponent.getToolTipLocation(paramMouseEvent);
/*     */ 
/* 592 */     if ((str != null) || (localPoint != null)) {
/* 593 */       this.mouseEvent = paramMouseEvent;
/* 594 */       if (((str != null) && (str.equals(this.toolTipText))) || ((str == null) && (((localPoint != null) && (localPoint.equals(this.preferredLocation))) || (localPoint == null))))
/*     */       {
/* 597 */         if (this.tipWindow != null)
/* 598 */           this.insideTimer.restart();
/*     */         else
/* 600 */           this.enterTimer.restart();
/*     */       }
/*     */       else {
/* 603 */         this.toolTipText = str;
/* 604 */         this.preferredLocation = localPoint;
/* 605 */         if (this.showImmediately) {
/* 606 */           hideTipWindow();
/* 607 */           showTipWindow();
/* 608 */           this.exitTimer.stop();
/*     */         } else {
/* 610 */           this.enterTimer.restart();
/*     */         }
/*     */       }
/*     */     } else {
/* 614 */       this.toolTipText = null;
/* 615 */       this.preferredLocation = null;
/* 616 */       this.mouseEvent = null;
/* 617 */       this.insideComponent = null;
/* 618 */       hideTipWindow();
/* 619 */       this.enterTimer.stop();
/* 620 */       this.exitTimer.restart();
/*     */     }
/*     */   }
/*     */ 
/*     */   static Frame frameForComponent(Component paramComponent)
/*     */   {
/* 679 */     while (!(paramComponent instanceof Frame)) {
/* 680 */       paramComponent = paramComponent.getParent();
/*     */     }
/* 682 */     return (Frame)paramComponent;
/*     */   }
/*     */ 
/*     */   private FocusListener createFocusChangeListener() {
/* 686 */     return new FocusAdapter() {
/*     */       public void focusLost(FocusEvent paramAnonymousFocusEvent) {
/* 688 */         ToolTipManager.this.hideTipWindow();
/* 689 */         ToolTipManager.this.insideComponent = null;
/* 690 */         JComponent localJComponent = (JComponent)paramAnonymousFocusEvent.getSource();
/* 691 */         localJComponent.removeFocusListener(ToolTipManager.this.focusChangeListener);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   private int getPopupFitWidth(Rectangle paramRectangle, Component paramComponent)
/*     */   {
/* 700 */     if (paramComponent != null)
/*     */     {
/* 702 */       for (Container localContainer = paramComponent.getParent(); localContainer != null; localContainer = localContainer.getParent())
/*     */       {
/* 704 */         if (((localContainer instanceof JFrame)) || ((localContainer instanceof JDialog)) || ((localContainer instanceof JWindow)))
/*     */         {
/* 706 */           return getWidthAdjust(localContainer.getBounds(), paramRectangle);
/* 707 */         }if (((localContainer instanceof JApplet)) || ((localContainer instanceof JInternalFrame))) {
/* 708 */           if (this.popupFrameRect == null) {
/* 709 */             this.popupFrameRect = new Rectangle();
/*     */           }
/* 711 */           Point localPoint = localContainer.getLocationOnScreen();
/* 712 */           this.popupFrameRect.setBounds(localPoint.x, localPoint.y, localContainer.getBounds().width, localContainer.getBounds().height);
/*     */ 
/* 715 */           return getWidthAdjust(this.popupFrameRect, paramRectangle);
/*     */         }
/*     */       }
/*     */     }
/* 719 */     return 0;
/*     */   }
/*     */ 
/*     */   private int getPopupFitHeight(Rectangle paramRectangle, Component paramComponent)
/*     */   {
/* 725 */     if (paramComponent != null)
/*     */     {
/* 727 */       for (Container localContainer = paramComponent.getParent(); localContainer != null; localContainer = localContainer.getParent()) {
/* 728 */         if (((localContainer instanceof JFrame)) || ((localContainer instanceof JDialog)) || ((localContainer instanceof JWindow)))
/*     */         {
/* 730 */           return getHeightAdjust(localContainer.getBounds(), paramRectangle);
/* 731 */         }if (((localContainer instanceof JApplet)) || ((localContainer instanceof JInternalFrame))) {
/* 732 */           if (this.popupFrameRect == null) {
/* 733 */             this.popupFrameRect = new Rectangle();
/*     */           }
/* 735 */           Point localPoint = localContainer.getLocationOnScreen();
/* 736 */           this.popupFrameRect.setBounds(localPoint.x, localPoint.y, localContainer.getBounds().width, localContainer.getBounds().height);
/*     */ 
/* 739 */           return getHeightAdjust(this.popupFrameRect, paramRectangle);
/*     */         }
/*     */       }
/*     */     }
/* 743 */     return 0;
/*     */   }
/*     */ 
/*     */   private int getHeightAdjust(Rectangle paramRectangle1, Rectangle paramRectangle2) {
/* 747 */     if ((paramRectangle2.y >= paramRectangle1.y) && (paramRectangle2.y + paramRectangle2.height <= paramRectangle1.y + paramRectangle1.height)) {
/* 748 */       return 0;
/*     */     }
/* 750 */     return paramRectangle2.y + paramRectangle2.height - (paramRectangle1.y + paramRectangle1.height) + 5;
/*     */   }
/*     */ 
/*     */   private int getWidthAdjust(Rectangle paramRectangle1, Rectangle paramRectangle2)
/*     */   {
/* 759 */     if ((paramRectangle2.x >= paramRectangle1.x) && (paramRectangle2.x + paramRectangle2.width <= paramRectangle1.x + paramRectangle1.width)) {
/* 760 */       return 0;
/*     */     }
/*     */ 
/* 763 */     return paramRectangle2.x + paramRectangle2.width - (paramRectangle1.x + paramRectangle1.width) + 5;
/*     */   }
/*     */ 
/*     */   private void show(JComponent paramJComponent)
/*     */   {
/* 772 */     if (this.tipWindow != null) {
/* 773 */       hideTipWindow();
/* 774 */       this.insideComponent = null;
/*     */     }
/*     */     else {
/* 777 */       hideTipWindow();
/* 778 */       this.enterTimer.stop();
/* 779 */       this.exitTimer.stop();
/* 780 */       this.insideTimer.stop();
/* 781 */       this.insideComponent = paramJComponent;
/* 782 */       if (this.insideComponent != null) {
/* 783 */         this.toolTipText = this.insideComponent.getToolTipText();
/* 784 */         this.preferredLocation = new Point(10, this.insideComponent.getHeight() + 10);
/*     */ 
/* 786 */         showTipWindow();
/*     */ 
/* 788 */         if (this.focusChangeListener == null) {
/* 789 */           this.focusChangeListener = createFocusChangeListener();
/*     */         }
/* 791 */         this.insideComponent.addFocusListener(this.focusChangeListener);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void hide(JComponent paramJComponent) {
/* 797 */     hideTipWindow();
/* 798 */     paramJComponent.removeFocusListener(this.focusChangeListener);
/* 799 */     this.preferredLocation = null;
/* 800 */     this.insideComponent = null;
/*     */   }
/*     */ 
/*     */   private class AccessibilityKeyListener extends KeyAdapter
/*     */   {
/*     */     private AccessibilityKeyListener()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void keyPressed(KeyEvent paramKeyEvent)
/*     */     {
/* 812 */       if (!paramKeyEvent.isConsumed()) {
/* 813 */         JComponent localJComponent = (JComponent)paramKeyEvent.getComponent();
/* 814 */         KeyStroke localKeyStroke = KeyStroke.getKeyStrokeForEvent(paramKeyEvent);
/* 815 */         if (ToolTipManager.this.hideTip.equals(localKeyStroke)) {
/* 816 */           if (ToolTipManager.this.tipWindow != null) {
/* 817 */             ToolTipManager.this.hide(localJComponent);
/* 818 */             paramKeyEvent.consume();
/*     */           }
/* 820 */         } else if (ToolTipManager.this.postTip.equals(localKeyStroke))
/*     */         {
/* 822 */           ToolTipManager.this.show(localJComponent);
/* 823 */           paramKeyEvent.consume();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private class MoveBeforeEnterListener extends MouseMotionAdapter
/*     */   {
/*     */     private MoveBeforeEnterListener()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void mouseMoved(MouseEvent paramMouseEvent)
/*     */     {
/* 674 */       ToolTipManager.this.initiateToolTip(paramMouseEvent);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected class insideTimerAction
/*     */     implements ActionListener
/*     */   {
/*     */     protected insideTimerAction()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void actionPerformed(ActionEvent paramActionEvent)
/*     */     {
/* 626 */       if ((ToolTipManager.this.insideComponent != null) && (ToolTipManager.this.insideComponent.isShowing()))
/*     */       {
/* 628 */         if ((ToolTipManager.this.toolTipText == null) && (ToolTipManager.this.mouseEvent != null)) {
/* 629 */           ToolTipManager.this.toolTipText = ToolTipManager.this.insideComponent.getToolTipText(ToolTipManager.this.mouseEvent);
/* 630 */           ToolTipManager.this.preferredLocation = ToolTipManager.this.insideComponent.getToolTipLocation(ToolTipManager.this.mouseEvent);
/*     */         }
/*     */ 
/* 633 */         if (ToolTipManager.this.toolTipText != null) {
/* 634 */           ToolTipManager.this.showImmediately = true;
/* 635 */           ToolTipManager.this.showTipWindow();
/*     */         }
/*     */         else {
/* 638 */           ToolTipManager.this.insideComponent = null;
/* 639 */           ToolTipManager.this.toolTipText = null;
/* 640 */           ToolTipManager.this.preferredLocation = null;
/* 641 */           ToolTipManager.this.mouseEvent = null;
/* 642 */           ToolTipManager.this.hideTipWindow();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected class outsideTimerAction implements ActionListener { protected outsideTimerAction() {  }
/*     */ 
/*     */ 
/* 650 */     public void actionPerformed(ActionEvent paramActionEvent) { ToolTipManager.this.showImmediately = false; }  } 
/*     */   protected class stillInsideTimerAction implements ActionListener {
/*     */     protected stillInsideTimerAction() {
/*     */     }
/*     */ 
/*     */     public void actionPerformed(ActionEvent paramActionEvent) {
/* 656 */       ToolTipManager.this.hideTipWindow();
/* 657 */       ToolTipManager.this.enterTimer.stop();
/* 658 */       ToolTipManager.this.showImmediately = false;
/* 659 */       ToolTipManager.this.insideComponent = null;
/* 660 */       ToolTipManager.this.mouseEvent = null;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.ToolTipManager
 * JD-Core Version:    0.6.2
 */