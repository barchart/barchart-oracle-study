/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.awt.AWTEvent;
/*     */ import java.awt.Adjustable;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Insets;
/*     */ import java.awt.Point;
/*     */ import java.awt.ScrollPane;
/*     */ import java.awt.ScrollPaneAdjustable;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.peer.ScrollPanePeer;
/*     */ import javax.swing.UIDefaults;
/*     */ import sun.awt.AWTAccessor;
/*     */ import sun.awt.AWTAccessor.ScrollPaneAdjustableAccessor;
/*     */ 
/*     */ class XScrollPanePeer extends XComponentPeer
/*     */   implements ScrollPanePeer, XScrollbarClient
/*     */ {
/*     */   public static final int MARGIN = 1;
/*  45 */   public static final int SCROLLBAR = XToolkit.getUIDefaults().getInt("ScrollBar.defaultWidth");
/*     */   public static final int SPACE = 2;
/*     */   public static final int SCROLLBAR_INSET = 2;
/*     */   public static final int VERTICAL = 1;
/*     */   public static final int HORIZONTAL = 2;
/*     */   XVerticalScrollbar vsb;
/*     */   XHorizontalScrollbar hsb;
/*     */   XWindow clip;
/*  52 */   int active = 1;
/*     */   int hsbSpace;
/*     */   int vsbSpace;
/*     */   int vval;
/*     */   int hval;
/*     */   int vmax;
/*     */   int hmax;
/*     */ 
/*     */   XScrollPanePeer(ScrollPane paramScrollPane)
/*     */   {
/*  66 */     super(paramScrollPane);
/*     */ 
/*  70 */     this.clip = null;
/*     */ 
/*  73 */     XScrollPaneContentWindow localXScrollPaneContentWindow = new XScrollPaneContentWindow(paramScrollPane, this.window);
/*  74 */     this.clip = localXScrollPaneContentWindow;
/*     */ 
/*  76 */     this.vsb = new XVerticalScrollbar(this);
/*     */ 
/*  78 */     this.hsb = new XHorizontalScrollbar(this);
/*     */ 
/*  80 */     if (paramScrollPane.getScrollbarDisplayPolicy() == 1)
/*  81 */       this.vsbSpace = (this.hsbSpace = SCROLLBAR);
/*     */     else {
/*  83 */       this.vsbSpace = (this.hsbSpace = 0);
/*     */     }
/*     */ 
/*  86 */     int i = 1;
/*  87 */     Adjustable localAdjustable1 = paramScrollPane.getVAdjustable();
/*  88 */     if (localAdjustable1 != null) {
/*  89 */       i = localAdjustable1.getUnitIncrement();
/*     */     }
/*  91 */     int j = this.height - this.hsbSpace;
/*  92 */     this.vsb.setValues(0, j, 0, j, i, Math.max(1, (int)(j * 0.9D)));
/*  93 */     this.vsb.setSize(this.vsbSpace - 2, j);
/*     */ 
/*  95 */     i = 1;
/*  96 */     Adjustable localAdjustable2 = paramScrollPane.getHAdjustable();
/*  97 */     if (localAdjustable2 != null) {
/*  98 */       i = localAdjustable2.getUnitIncrement();
/*     */     }
/* 100 */     int k = this.width - this.vsbSpace;
/* 101 */     this.hsb.setValues(0, k, 0, k, i, Math.max(1, (int)(k * 0.9D)));
/* 102 */     this.hsb.setSize(k, this.hsbSpace - 2);
/*     */ 
/* 104 */     setViewportSize();
/* 105 */     this.clip.xSetVisible(true);
/*     */   }
/*     */ 
/*     */   public long getContentWindow()
/*     */   {
/* 112 */     return this.clip == null ? this.window : this.clip.getWindow();
/*     */   }
/*     */ 
/*     */   public void setBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
/* 116 */     super.setBounds(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
/*     */ 
/* 118 */     if (this.clip == null) return;
/* 119 */     setScrollbarSpace();
/* 120 */     setViewportSize();
/* 121 */     repaint();
/*     */   }
/*     */ 
/*     */   public Insets getInsets() {
/* 125 */     return new Insets(1, 1, 1 + this.hsbSpace, 1 + this.vsbSpace);
/*     */   }
/*     */ 
/*     */   public int getHScrollbarHeight() {
/* 129 */     return SCROLLBAR;
/*     */   }
/*     */ 
/*     */   public int getVScrollbarWidth() {
/* 133 */     return SCROLLBAR;
/*     */   }
/*     */ 
/*     */   public void childResized(int paramInt1, int paramInt2) {
/* 137 */     if (setScrollbarSpace()) {
/* 138 */       setViewportSize();
/*     */     }
/* 140 */     repaint();
/*     */   }
/*     */ 
/*     */   Dimension getChildSize() {
/* 144 */     ScrollPane localScrollPane = (ScrollPane)this.target;
/* 145 */     if (localScrollPane.countComponents() > 0) {
/* 146 */       Component localComponent = localScrollPane.getComponent(0);
/* 147 */       return localComponent.size();
/*     */     }
/* 149 */     return new Dimension(0, 0);
/*     */   }
/*     */ 
/*     */   boolean setScrollbarSpace()
/*     */   {
/* 154 */     ScrollPane localScrollPane = (ScrollPane)this.target;
/* 155 */     boolean bool = false;
/* 156 */     int i = localScrollPane.getScrollbarDisplayPolicy();
/*     */ 
/* 158 */     if (i == 2) {
/* 159 */       return bool;
/*     */     }
/* 161 */     Dimension localDimension = getChildSize();
/*     */     int k;
/* 163 */     if (i == 0) {
/* 164 */       j = this.hsbSpace;
/* 165 */       k = this.vsbSpace;
/* 166 */       this.hsbSpace = (localDimension.width <= this.width - 2 ? 0 : SCROLLBAR);
/* 167 */       this.vsbSpace = (localDimension.height <= this.height - 2 ? 0 : SCROLLBAR);
/*     */ 
/* 169 */       if ((this.hsbSpace == 0) && (this.vsbSpace != 0)) {
/* 170 */         this.hsbSpace = (localDimension.width <= this.width - SCROLLBAR - 2 ? 0 : SCROLLBAR);
/*     */       }
/* 172 */       if ((this.vsbSpace == 0) && (this.hsbSpace != 0)) {
/* 173 */         this.vsbSpace = (localDimension.height <= this.height - SCROLLBAR - 2 ? 0 : SCROLLBAR);
/*     */       }
/* 175 */       if ((j != this.hsbSpace) || (k != this.vsbSpace)) {
/* 176 */         bool = true;
/*     */       }
/*     */     }
/* 179 */     if (this.vsbSpace > 0) {
/* 180 */       j = this.height - 2 - this.hsbSpace;
/* 181 */       k = Math.max(localDimension.height, j);
/* 182 */       this.vsb.setValues(this.vsb.getValue(), j, 0, k);
/* 183 */       this.vsb.setBlockIncrement((int)(this.vsb.getVisibleAmount() * 0.9D));
/* 184 */       this.vsb.setSize(this.vsbSpace - 2, this.height - this.hsbSpace);
/*     */     }
/*     */ 
/* 190 */     if (this.hsbSpace > 0) {
/* 191 */       j = this.width - 2 - this.vsbSpace;
/* 192 */       k = Math.max(localDimension.width, j);
/* 193 */       this.hsb.setValues(this.hsb.getValue(), j, 0, k);
/* 194 */       this.hsb.setBlockIncrement((int)(this.hsb.getVisibleAmount() * 0.9D));
/* 195 */       this.hsb.setSize(this.width - this.vsbSpace, this.hsbSpace - 2);
/*     */     }
/*     */ 
/* 208 */     int j = 0;
/*     */ 
/* 212 */     Point localPoint = new Point(0, 0);
/*     */ 
/* 214 */     if (((ScrollPane)this.target).getComponentCount() > 0)
/*     */     {
/* 216 */       localPoint = ((ScrollPane)this.target).getComponent(0).location();
/*     */ 
/* 218 */       if ((this.vsbSpace == 0) && (localPoint.y < 0)) {
/* 219 */         localPoint.y = 0;
/* 220 */         j = 1;
/*     */       }
/*     */ 
/* 223 */       if ((this.hsbSpace == 0) && (localPoint.x < 0)) {
/* 224 */         localPoint.x = 0;
/* 225 */         j = 1;
/*     */       }
/*     */     }
/*     */ 
/* 229 */     if (j != 0) {
/* 230 */       scroll(this.x, this.y, 3);
/*     */     }
/* 232 */     return bool;
/*     */   }
/*     */ 
/*     */   void setViewportSize() {
/* 236 */     this.clip.xSetBounds(1, 1, this.width - 2 - this.vsbSpace, this.height - 2 - this.hsbSpace);
/*     */   }
/*     */ 
/*     */   public void setUnitIncrement(Adjustable paramAdjustable, int paramInt)
/*     */   {
/* 242 */     if (paramAdjustable.getOrientation() == 1) {
/* 243 */       this.vsb.setUnitIncrement(paramInt);
/*     */     }
/*     */     else
/* 246 */       this.hsb.setUnitIncrement(paramInt);
/*     */   }
/*     */ 
/*     */   public void setValue(Adjustable paramAdjustable, int paramInt)
/*     */   {
/* 251 */     if (paramAdjustable.getOrientation() == 1) {
/* 252 */       scroll(-1, paramInt, 1);
/*     */     }
/*     */     else
/* 255 */       scroll(paramInt, -1, 2);
/*     */   }
/*     */ 
/*     */   public void setScrollPosition(int paramInt1, int paramInt2)
/*     */   {
/* 260 */     scroll(paramInt1, paramInt2, 3);
/*     */   }
/*     */ 
/*     */   void scroll(int paramInt1, int paramInt2, int paramInt3) {
/* 264 */     scroll(paramInt1, paramInt2, paramInt3, 5);
/*     */   }
/*     */ 
/*     */   void scroll(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/* 271 */     checkSecurity();
/* 272 */     ScrollPane localScrollPane = (ScrollPane)this.target;
/* 273 */     Component localComponent = getScrollChild();
/* 274 */     if (localComponent == null) {
/* 275 */       return;
/*     */     }
/*     */ 
/* 278 */     Color[] arrayOfColor = getGUIcolors();
/*     */     int i;
/*     */     int j;
/* 280 */     if (localScrollPane.getScrollbarDisplayPolicy() == 2) {
/* 281 */       i = -paramInt1;
/* 282 */       j = -paramInt2;
/*     */     } else {
/* 284 */       Point localPoint = localComponent.location();
/* 285 */       i = localPoint.x;
/* 286 */       j = localPoint.y;
/*     */       ScrollPaneAdjustable localScrollPaneAdjustable;
/*     */       Graphics localGraphics;
/* 288 */       if ((paramInt3 & 0x2) != 0) {
/* 289 */         this.hsb.setValue(Math.min(paramInt1, this.hsb.getMaximum() - this.hsb.getVisibleAmount()));
/* 290 */         localScrollPaneAdjustable = (ScrollPaneAdjustable)localScrollPane.getHAdjustable();
/* 291 */         setAdjustableValue(localScrollPaneAdjustable, this.hsb.getValue(), paramInt4);
/* 292 */         i = -this.hsb.getValue();
/* 293 */         localGraphics = getGraphics();
/*     */         try {
/* 295 */           paintHorScrollbar(localGraphics, arrayOfColor, true);
/*     */         } finally {
/* 297 */           localGraphics.dispose();
/*     */         }
/*     */       }
/* 300 */       if ((paramInt3 & 0x1) != 0) {
/* 301 */         this.vsb.setValue(Math.min(paramInt2, this.vsb.getMaximum() - this.vsb.getVisibleAmount()));
/* 302 */         localScrollPaneAdjustable = (ScrollPaneAdjustable)localScrollPane.getVAdjustable();
/* 303 */         setAdjustableValue(localScrollPaneAdjustable, this.vsb.getValue(), paramInt4);
/* 304 */         j = -this.vsb.getValue();
/* 305 */         localGraphics = getGraphics();
/*     */         try {
/* 307 */           paintVerScrollbar(localGraphics, arrayOfColor, true);
/*     */         } finally {
/* 309 */           localGraphics.dispose();
/*     */         }
/*     */       }
/*     */     }
/* 313 */     localComponent.move(i, j);
/*     */   }
/*     */ 
/*     */   void setAdjustableValue(ScrollPaneAdjustable paramScrollPaneAdjustable, int paramInt1, int paramInt2) {
/* 317 */     AWTAccessor.getScrollPaneAdjustableAccessor().setTypedValue(paramScrollPaneAdjustable, paramInt1, paramInt2);
/*     */   }
/*     */ 
/*     */   public void paint(Graphics paramGraphics) {
/* 321 */     paintComponent(paramGraphics);
/*     */   }
/*     */ 
/*     */   void paintScrollBars(Graphics paramGraphics, Color[] paramArrayOfColor)
/*     */   {
/* 326 */     if (this.vsbSpace > 0) {
/* 327 */       paintVerScrollbar(paramGraphics, paramArrayOfColor, true);
/*     */     }
/*     */ 
/* 331 */     if (this.hsbSpace > 0)
/* 332 */       paintHorScrollbar(paramGraphics, paramArrayOfColor, true);
/*     */   }
/*     */ 
/*     */   void repaintScrollBars()
/*     */   {
/* 338 */     Graphics localGraphics = getGraphics();
/* 339 */     Color[] arrayOfColor = getGUIcolors();
/* 340 */     if (localGraphics != null) {
/* 341 */       paintScrollBars(localGraphics, arrayOfColor);
/*     */     }
/* 343 */     localGraphics.dispose();
/*     */   }
/*     */ 
/*     */   public void repaintScrollbarRequest(XScrollbar paramXScrollbar) {
/* 347 */     Graphics localGraphics = getGraphics();
/* 348 */     Color[] arrayOfColor = getGUIcolors();
/* 349 */     if (localGraphics != null)
/* 350 */       if (paramXScrollbar == this.vsb) {
/* 351 */         paintVerScrollbar(localGraphics, arrayOfColor, true);
/*     */       }
/* 353 */       else if (paramXScrollbar == this.hsb)
/* 354 */         paintHorScrollbar(localGraphics, arrayOfColor, true);
/*     */   }
/*     */ 
/*     */   public void paintComponent(Graphics paramGraphics)
/*     */   {
/* 364 */     Color[] arrayOfColor = getGUIcolors();
/* 365 */     paramGraphics.setColor(arrayOfColor[0]);
/* 366 */     int i = this.height - this.hsbSpace;
/* 367 */     int j = this.width - this.vsbSpace;
/*     */ 
/* 369 */     paramGraphics.fillRect(0, 0, j, i);
/*     */ 
/* 372 */     paramGraphics.fillRect(j, i, this.vsbSpace, this.hsbSpace);
/*     */ 
/* 375 */     draw3DRect(paramGraphics, arrayOfColor, 0, 0, j - 1, i - 1, false);
/*     */ 
/* 378 */     paintScrollBars(paramGraphics, arrayOfColor);
/*     */   }
/*     */ 
/*     */   public void handleEvent(AWTEvent paramAWTEvent) {
/* 382 */     super.handleEvent(paramAWTEvent);
/*     */ 
/* 384 */     int i = paramAWTEvent.getID();
/* 385 */     switch (i) {
/*     */     case 800:
/*     */     case 801:
/* 388 */       repaintScrollBars();
/*     */     }
/*     */   }
/*     */ 
/*     */   void paintHorScrollbar(Graphics paramGraphics, Color[] paramArrayOfColor, boolean paramBoolean)
/*     */   {
/* 402 */     if (this.hsbSpace <= 0) {
/* 403 */       return;
/*     */     }
/* 405 */     Graphics localGraphics = paramGraphics.create();
/* 406 */     paramGraphics.setColor(paramArrayOfColor[0]);
/*     */ 
/* 411 */     int i = this.width - this.vsbSpace - 2;
/* 412 */     paramGraphics.fillRect(1, this.height - SCROLLBAR, i, 2);
/* 413 */     paramGraphics.fillRect(0, this.height - SCROLLBAR, 1, SCROLLBAR);
/* 414 */     paramGraphics.fillRect(1 + i, this.height - SCROLLBAR, 1, SCROLLBAR);
/*     */     try
/*     */     {
/* 417 */       localGraphics.translate(1, this.height - (SCROLLBAR - 2));
/* 418 */       this.hsb.paint(localGraphics, paramArrayOfColor, paramBoolean);
/*     */     }
/*     */     finally {
/* 421 */       localGraphics.dispose();
/*     */     }
/*     */   }
/*     */ 
/*     */   void paintVerScrollbar(Graphics paramGraphics, Color[] paramArrayOfColor, boolean paramBoolean)
/*     */   {
/* 438 */     if (this.vsbSpace <= 0) {
/* 439 */       return;
/*     */     }
/* 441 */     Graphics localGraphics = paramGraphics.create();
/* 442 */     paramGraphics.setColor(paramArrayOfColor[0]);
/*     */ 
/* 447 */     int i = this.height - this.hsbSpace - 2;
/* 448 */     paramGraphics.fillRect(this.width - SCROLLBAR, 1, 2, i);
/* 449 */     paramGraphics.fillRect(this.width - SCROLLBAR, 0, SCROLLBAR, 1);
/* 450 */     paramGraphics.fillRect(this.width - SCROLLBAR, 1 + i, SCROLLBAR, 1);
/*     */     try
/*     */     {
/* 453 */       localGraphics.translate(this.width - (SCROLLBAR - 2), 1);
/* 454 */       this.vsb.paint(localGraphics, paramArrayOfColor, paramBoolean);
/*     */     }
/*     */     finally {
/* 457 */       localGraphics.dispose();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void handleJavaMouseEvent(MouseEvent paramMouseEvent)
/*     */   {
/* 473 */     super.handleJavaMouseEvent(paramMouseEvent);
/* 474 */     int i = paramMouseEvent.getModifiers();
/* 475 */     int j = paramMouseEvent.getID();
/* 476 */     int k = paramMouseEvent.getX();
/* 477 */     int m = paramMouseEvent.getY();
/*     */ 
/* 482 */     if ((i & 0x10) == 0)
/*     */       return;
/*     */     int n;
/* 486 */     switch (j) {
/*     */     case 501:
/* 488 */       if (inVerticalScrollbar(k, m)) {
/* 489 */         this.active = 1;
/* 490 */         n = this.height - this.hsbSpace - 2;
/* 491 */         this.vsb.handleMouseEvent(j, i, k - (this.width - SCROLLBAR + 2), m - 1);
/* 492 */       } else if (inHorizontalScrollbar(k, m)) {
/* 493 */         this.active = 2;
/* 494 */         n = this.width - 2 - this.vsbSpace;
/* 495 */         this.hsb.handleMouseEvent(j, i, k - 1, m - (this.height - SCROLLBAR + 2));
/* 496 */       }break;
/*     */     case 502:
/* 503 */       if (this.active == 1)
/* 504 */         this.vsb.handleMouseEvent(j, i, k, m);
/* 505 */       else if (this.active == 2)
/* 506 */         this.hsb.handleMouseEvent(j, i, k, m); break;
/*     */     case 506:
/* 512 */       if (this.active == 1) {
/* 513 */         n = this.height - 2 - this.hsbSpace;
/* 514 */         this.vsb.handleMouseEvent(j, i, k - (this.width - SCROLLBAR + 2), m - 1);
/* 515 */       } else if (this.active == 2) {
/* 516 */         n = this.width - 2 - this.vsbSpace;
/* 517 */         this.hsb.handleMouseEvent(j, i, k - 1, m - (this.height - SCROLLBAR + 2));
/*     */       }
/*     */       break;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void notifyValue(XScrollbar paramXScrollbar, int paramInt1, int paramInt2, boolean paramBoolean)
/*     */   {
/* 527 */     if (paramXScrollbar == this.vsb)
/* 528 */       scroll(-1, paramInt2, 1, paramInt1);
/* 529 */     else if ((XHorizontalScrollbar)paramXScrollbar == this.hsb)
/* 530 */       scroll(paramInt2, -1, 2, paramInt1);
/*     */   }
/*     */ 
/*     */   boolean inVerticalScrollbar(int paramInt1, int paramInt2)
/*     */   {
/* 538 */     if (this.vsbSpace <= 0) {
/* 539 */       return false;
/*     */     }
/* 541 */     int i = this.height - 1 - this.hsbSpace;
/* 542 */     return (paramInt1 >= this.width - (SCROLLBAR - 2)) && (paramInt1 < this.width) && (paramInt2 >= 1) && (paramInt2 < i);
/*     */   }
/*     */ 
/*     */   boolean inHorizontalScrollbar(int paramInt1, int paramInt2)
/*     */   {
/* 549 */     if (this.hsbSpace <= 0) {
/* 550 */       return false;
/*     */     }
/* 552 */     int i = this.width - 1 - this.vsbSpace;
/* 553 */     return (paramInt1 >= 1) && (paramInt1 < i) && (paramInt2 >= this.height - (SCROLLBAR - 2)) && (paramInt2 < this.height);
/*     */   }
/*     */ 
/*     */   private Component getScrollChild() {
/* 557 */     ScrollPane localScrollPane = (ScrollPane)this.target;
/* 558 */     Component localComponent = null;
/*     */     try {
/* 560 */       localComponent = localScrollPane.getComponent(0);
/*     */     }
/*     */     catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException) {
/*     */     }
/* 564 */     return localComponent;
/*     */   }
/*     */ 
/*     */   public void print(Graphics paramGraphics)
/*     */   {
/* 577 */     ScrollPane localScrollPane = (ScrollPane)this.target;
/* 578 */     Dimension localDimension1 = localScrollPane.size();
/* 579 */     Color localColor1 = localScrollPane.getBackground();
/* 580 */     Color localColor2 = localScrollPane.getForeground();
/* 581 */     Point localPoint = localScrollPane.getScrollPosition();
/* 582 */     Component localComponent = getScrollChild();
/*     */     Dimension localDimension2;
/* 584 */     if (localComponent != null)
/* 585 */       localDimension2 = localComponent.size();
/*     */     else {
/* 587 */       localDimension2 = new Dimension(0, 0);
/*     */     }
/* 589 */     int i = localScrollPane.getScrollbarDisplayPolicy();
/*     */ 
/* 592 */     switch (i) {
/*     */     case 2:
/* 594 */       this.hsbSpace = (this.vsbSpace = 0);
/* 595 */       break;
/*     */     case 1:
/* 597 */       this.hsbSpace = (this.vsbSpace = SCROLLBAR);
/* 598 */       break;
/*     */     case 0:
/* 600 */       this.hsbSpace = (localDimension2.width <= localDimension1.width - 2 ? 0 : SCROLLBAR);
/* 601 */       this.vsbSpace = (localDimension2.height <= localDimension1.height - 2 ? 0 : SCROLLBAR);
/*     */ 
/* 603 */       if ((this.hsbSpace == 0) && (this.vsbSpace != 0)) {
/* 604 */         this.hsbSpace = (localDimension2.width <= localDimension1.width - SCROLLBAR - 2 ? 0 : SCROLLBAR);
/*     */       }
/* 606 */       if ((this.vsbSpace == 0) && (this.hsbSpace != 0))
/* 607 */         this.vsbSpace = (localDimension2.height <= localDimension1.height - SCROLLBAR - 2 ? 0 : SCROLLBAR);
/*     */       break;
/*     */     }
/*     */     int i4;
/*     */     int i3;
/*     */     int i2;
/*     */     int i1;
/*     */     int n;
/*     */     int m;
/*     */     int k;
/* 611 */     int j = k = m = n = i1 = i2 = i3 = i4 = 0;
/*     */ 
/* 613 */     if (this.vsbSpace > 0) {
/* 614 */       m = 0;
/* 615 */       j = localDimension1.height - 2 - this.hsbSpace;
/* 616 */       i1 = Math.max(localDimension2.height - j, 0);
/* 617 */       i3 = localPoint.y;
/*     */     }
/* 619 */     if (this.hsbSpace > 0) {
/* 620 */       n = 0;
/* 621 */       k = localDimension1.width - 2 - this.vsbSpace;
/* 622 */       i2 = Math.max(localDimension2.width - k, 0);
/* 623 */       i4 = localPoint.x;
/*     */     }
/*     */ 
/* 628 */     int i5 = localDimension1.width - this.vsbSpace;
/* 629 */     int i6 = localDimension1.height - this.hsbSpace;
/*     */ 
/* 631 */     paramGraphics.setColor(localColor1);
/* 632 */     paramGraphics.fillRect(0, 0, localDimension1.width, localDimension1.height);
/*     */     int i7;
/*     */     Graphics localGraphics;
/* 634 */     if (this.hsbSpace > 0) {
/* 635 */       i7 = localDimension1.width - this.vsbSpace;
/* 636 */       paramGraphics.fillRect(1, localDimension1.height - SCROLLBAR - 3, i7 - 1, SCROLLBAR - 3);
/* 637 */       localGraphics = paramGraphics.create();
/*     */       try {
/* 639 */         localGraphics.translate(0, localDimension1.height - (SCROLLBAR - 2));
/* 640 */         drawScrollbar(localGraphics, localColor1, SCROLLBAR - 2, i7, n, i2, i4, k, true);
/*     */       }
/*     */       finally {
/* 643 */         localGraphics.dispose();
/*     */       }
/*     */     }
/* 646 */     if (this.vsbSpace > 0) {
/* 647 */       i7 = localDimension1.height - this.hsbSpace;
/* 648 */       paramGraphics.fillRect(localDimension1.width - SCROLLBAR - 3, 1, SCROLLBAR - 3, i7 - 1);
/* 649 */       localGraphics = paramGraphics.create();
/*     */       try {
/* 651 */         localGraphics.translate(localDimension1.width - (SCROLLBAR - 2), 0);
/* 652 */         drawScrollbar(localGraphics, localColor1, SCROLLBAR - 2, i7, m, i1, i3, j, false);
/*     */       }
/*     */       finally {
/* 655 */         localGraphics.dispose();
/*     */       }
/*     */     }
/*     */ 
/* 659 */     draw3DRect(paramGraphics, localColor1, 0, 0, i5 - 1, i6 - 1, false);
/*     */ 
/* 661 */     this.target.print(paramGraphics);
/* 662 */     localScrollPane.printComponents(paramGraphics);
/*     */   }
/*     */ 
/*     */   static class XScrollPaneContentWindow extends XWindow
/*     */   {
/*     */     XScrollPaneContentWindow(ScrollPane paramScrollPane, long paramLong)
/*     */     {
/*  58 */       super(paramLong);
/*     */     }
/*     */     public String getWMName() {
/*  61 */       return "ScrollPane content";
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XScrollPanePeer
 * JD-Core Version:    0.6.2
 */