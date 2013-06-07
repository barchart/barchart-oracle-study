/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Point;
/*     */ import java.awt.Polygon;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.image.BufferedImage;
/*     */ import sun.awt.SunToolkit;
/*     */ import sun.awt.X11GraphicsConfig;
/*     */ import sun.util.logging.PlatformLogger;
/*     */ 
/*     */ abstract class XScrollbar
/*     */ {
/*  40 */   private static PlatformLogger log = PlatformLogger.getLogger("sun.awt.X11.XScrollbar");
/*     */ 
/*  45 */   private static XScrollRepeater scroller = new XScrollRepeater(null);
/*     */ 
/*  51 */   private XScrollRepeater i_scroller = new XScrollRepeater(null);
/*     */   private static final int MIN_THUMB_H = 5;
/*     */   private static final int ARROW_IND = 1;
/*     */   XScrollbarClient sb;
/*     */   private int val;
/*     */   private int min;
/*     */   private int max;
/*     */   private int vis;
/*     */   private int line;
/*     */   private int page;
/*  68 */   private boolean needsRepaint = true;
/*  69 */   private boolean pressed = false;
/*  70 */   private boolean dragging = false;
/*     */   Polygon firstArrow;
/*     */   Polygon secondArrow;
/*     */   int width;
/*     */   int height;
/*     */   int barWidth;
/*     */   int barLength;
/*     */   int arrowArea;
/*     */   int alignment;
/*     */   public static final int ALIGNMENT_VERTICAL = 1;
/*     */   public static final int ALIGNMENT_HORIZONTAL = 2;
/*     */   int mode;
/*     */   Point thumbOffset;
/*     */   private Rectangle prevThumb;
/*     */ 
/*     */   public XScrollbar(int paramInt, XScrollbarClient paramXScrollbarClient)
/*     */   {
/*  89 */     this.sb = paramXScrollbarClient;
/*  90 */     this.alignment = paramInt;
/*     */   }
/*     */ 
/*     */   public boolean needsRepaint() {
/*  94 */     return this.needsRepaint;
/*     */   }
/*     */ 
/*     */   void notifyValue(int paramInt) {
/*  98 */     notifyValue(paramInt, false);
/*     */   }
/*     */ 
/*     */   void notifyValue(int paramInt, final boolean paramBoolean) {
/* 102 */     if (paramInt < this.min)
/* 103 */       paramInt = this.min;
/* 104 */     else if (paramInt > this.max - this.vis) {
/* 105 */       paramInt = this.max - this.vis;
/*     */     }
/* 107 */     final int i = paramInt;
/* 108 */     final int j = this.mode;
/* 109 */     if ((this.sb != null) && ((i != this.val) || (!this.pressed)))
/* 110 */       SunToolkit.executeOnEventHandlerThread(this.sb.getEventSource(), new Runnable() {
/*     */         public void run() {
/* 112 */           XScrollbar.this.sb.notifyValue(XScrollbar.this, j, i, paramBoolean);
/*     */         }
/*     */       });
/*     */   }
/*     */ 
/*     */   protected abstract void rebuildArrows();
/*     */ 
/*     */   public void setSize(int paramInt1, int paramInt2)
/*     */   {
/* 121 */     if (log.isLoggable(400)) log.finer("Setting scroll bar " + this + " size to " + paramInt1 + "x" + paramInt2);
/* 122 */     this.width = paramInt1;
/* 123 */     this.height = paramInt2;
/*     */   }
/*     */ 
/*     */   protected Polygon createArrowShape(boolean paramBoolean1, boolean paramBoolean2)
/*     */   {
/* 130 */     Polygon localPolygon = new Polygon();
/*     */     int i;
/*     */     int j;
/*     */     int k;
/* 133 */     if (paramBoolean1) {
/* 134 */       i = this.width / 2 - getArrowWidth() / 2;
/* 135 */       j = paramBoolean2 ? 1 : this.barLength - 1;
/* 136 */       k = paramBoolean2 ? getArrowWidth() : this.barLength - getArrowWidth() - 1;
/* 137 */       localPolygon.addPoint(i + getArrowWidth() / 2, j);
/* 138 */       localPolygon.addPoint(i + getArrowWidth(), k);
/* 139 */       localPolygon.addPoint(i, k);
/* 140 */       localPolygon.addPoint(i + getArrowWidth() / 2, j);
/*     */     } else {
/* 142 */       i = this.height / 2 - getArrowWidth() / 2;
/* 143 */       j = paramBoolean2 ? 1 : this.barLength - 1;
/* 144 */       k = paramBoolean2 ? getArrowWidth() : this.barLength - getArrowWidth() - 1;
/* 145 */       localPolygon.addPoint(j, i + getArrowWidth() / 2);
/* 146 */       localPolygon.addPoint(k, i + getArrowWidth());
/* 147 */       localPolygon.addPoint(k, i);
/* 148 */       localPolygon.addPoint(j, i + getArrowWidth() / 2);
/*     */     }
/* 150 */     return localPolygon;
/*     */   }
/*     */ 
/*     */   protected abstract Rectangle getThumbArea();
/*     */ 
/*     */   void paint(Graphics paramGraphics, Color[] paramArrayOfColor, boolean paramBoolean)
/*     */   {
/* 167 */     if (log.isLoggable(400)) log.finer("Painting scrollbar " + this);
/*     */ 
/* 169 */     int i = 0;
/* 170 */     Graphics2D localGraphics2D = null;
/* 171 */     BufferedImage localBufferedImage = null;
/*     */     Object localObject1;
/* 172 */     if (!(paramGraphics instanceof Graphics2D))
/*     */     {
/* 177 */       localObject1 = (X11GraphicsConfig)this.sb.getEventSource().getGraphicsConfiguration();
/* 178 */       localBufferedImage = ((X11GraphicsConfig)localObject1).createCompatibleImage(this.width, this.height);
/* 179 */       localGraphics2D = localBufferedImage.createGraphics();
/* 180 */       i = 1;
/*     */     } else {
/* 182 */       localGraphics2D = (Graphics2D)paramGraphics;
/*     */     }
/*     */     try {
/* 185 */       localObject1 = calculateThumbRect();
/*     */ 
/* 191 */       this.prevThumb = ((Rectangle)localObject1);
/*     */ 
/* 194 */       Color localColor1 = paramArrayOfColor[0];
/* 195 */       Color localColor2 = new Color(MotifColorUtilities.calculateSelectFromBackground(localColor1.getRed(), localColor1.getGreen(), localColor1.getBlue()));
/* 196 */       Color localColor3 = new Color(MotifColorUtilities.calculateBottomShadowFromBackground(localColor1.getRed(), localColor1.getGreen(), localColor1.getBlue()));
/* 197 */       Color localColor4 = new Color(MotifColorUtilities.calculateTopShadowFromBackground(localColor1.getRed(), localColor1.getGreen(), localColor1.getBlue()));
/*     */ 
/* 199 */       XToolkit.awtLock();
/*     */       try {
/* 201 */         XlibWrapper.XFlush(XToolkit.getDisplay());
/*     */       } finally {
/* 203 */         XToolkit.awtUnlock();
/*     */       }
/*     */ 
/* 206 */       if (paramBoolean)
/*     */       {
/* 208 */         localGraphics2D.setColor(localColor2);
/* 209 */         if (this.alignment == 2) {
/* 210 */           localGraphics2D.fillRect(0, 0, ((Rectangle)localObject1).x, this.height);
/* 211 */           localGraphics2D.fillRect(((Rectangle)localObject1).x + ((Rectangle)localObject1).width, 0, this.width - (((Rectangle)localObject1).x + ((Rectangle)localObject1).width), this.height);
/*     */         } else {
/* 213 */           localGraphics2D.fillRect(0, 0, this.width, ((Rectangle)localObject1).y);
/* 214 */           localGraphics2D.fillRect(0, ((Rectangle)localObject1).y + ((Rectangle)localObject1).height, this.width, this.height - (((Rectangle)localObject1).y + ((Rectangle)localObject1).height));
/*     */         }
/*     */ 
/* 220 */         localGraphics2D.setColor(localColor3);
/* 221 */         localGraphics2D.drawLine(0, 0, this.width - 1, 0);
/* 222 */         localGraphics2D.drawLine(0, 0, 0, this.height - 1);
/*     */ 
/* 224 */         localGraphics2D.setColor(localColor4);
/* 225 */         localGraphics2D.drawLine(1, this.height - 1, this.width - 1, this.height - 1);
/* 226 */         localGraphics2D.drawLine(this.width - 1, 1, this.width - 1, this.height - 1);
/*     */       }
/*     */       else {
/* 229 */         localGraphics2D.setColor(localColor2);
/* 230 */         Rectangle localRectangle = getThumbArea();
/* 231 */         localGraphics2D.fill(localRectangle);
/*     */       }
/*     */ 
/* 234 */       if (paramBoolean)
/*     */       {
/* 236 */         paintArrows(localGraphics2D, paramArrayOfColor[0], localColor3, localColor4);
/*     */       }
/*     */ 
/* 241 */       localGraphics2D.setColor(paramArrayOfColor[0]);
/* 242 */       localGraphics2D.fillRect(((Rectangle)localObject1).x, ((Rectangle)localObject1).y, ((Rectangle)localObject1).width, ((Rectangle)localObject1).height);
/*     */ 
/* 244 */       localGraphics2D.setColor(localColor4);
/* 245 */       localGraphics2D.drawLine(((Rectangle)localObject1).x, ((Rectangle)localObject1).y, ((Rectangle)localObject1).x + ((Rectangle)localObject1).width, ((Rectangle)localObject1).y);
/*     */ 
/* 247 */       localGraphics2D.drawLine(((Rectangle)localObject1).x, ((Rectangle)localObject1).y, ((Rectangle)localObject1).x, ((Rectangle)localObject1).y + ((Rectangle)localObject1).height);
/*     */ 
/* 250 */       localGraphics2D.setColor(localColor3);
/* 251 */       localGraphics2D.drawLine(((Rectangle)localObject1).x + 1, ((Rectangle)localObject1).y + ((Rectangle)localObject1).height, ((Rectangle)localObject1).x + ((Rectangle)localObject1).width, ((Rectangle)localObject1).y + ((Rectangle)localObject1).height);
/*     */ 
/* 255 */       localGraphics2D.drawLine(((Rectangle)localObject1).x + ((Rectangle)localObject1).width, ((Rectangle)localObject1).y + 1, ((Rectangle)localObject1).x + ((Rectangle)localObject1).width, ((Rectangle)localObject1).y + ((Rectangle)localObject1).height);
/*     */     }
/*     */     finally
/*     */     {
/* 260 */       if (i != 0) {
/* 261 */         localGraphics2D.dispose();
/*     */       }
/*     */     }
/* 264 */     if (i != 0) {
/* 265 */       paramGraphics.drawImage(localBufferedImage, 0, 0, null);
/*     */     }
/* 267 */     XToolkit.awtLock();
/*     */     try {
/* 269 */       XlibWrapper.XFlush(XToolkit.getDisplay());
/*     */     } finally {
/* 271 */       XToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   void paintArrows(Graphics2D paramGraphics2D, Color paramColor1, Color paramColor2, Color paramColor3)
/*     */   {
/* 277 */     paramGraphics2D.setColor(paramColor1);
/*     */ 
/* 280 */     if ((this.pressed) && (this.mode == 2)) {
/* 281 */       paramGraphics2D.fill(this.firstArrow);
/* 282 */       paramGraphics2D.setColor(paramColor3);
/* 283 */       paramGraphics2D.drawLine(this.firstArrow.xpoints[0], this.firstArrow.ypoints[0], this.firstArrow.xpoints[1], this.firstArrow.ypoints[1]);
/*     */ 
/* 285 */       paramGraphics2D.drawLine(this.firstArrow.xpoints[1], this.firstArrow.ypoints[1], this.firstArrow.xpoints[2], this.firstArrow.ypoints[2]);
/*     */ 
/* 287 */       paramGraphics2D.setColor(paramColor2);
/* 288 */       paramGraphics2D.drawLine(this.firstArrow.xpoints[2], this.firstArrow.ypoints[2], this.firstArrow.xpoints[0], this.firstArrow.ypoints[0]);
/*     */     }
/*     */     else
/*     */     {
/* 293 */       paramGraphics2D.fill(this.firstArrow);
/* 294 */       paramGraphics2D.setColor(paramColor2);
/* 295 */       paramGraphics2D.drawLine(this.firstArrow.xpoints[0], this.firstArrow.ypoints[0], this.firstArrow.xpoints[1], this.firstArrow.ypoints[1]);
/*     */ 
/* 297 */       paramGraphics2D.drawLine(this.firstArrow.xpoints[1], this.firstArrow.ypoints[1], this.firstArrow.xpoints[2], this.firstArrow.ypoints[2]);
/*     */ 
/* 299 */       paramGraphics2D.setColor(paramColor3);
/* 300 */       paramGraphics2D.drawLine(this.firstArrow.xpoints[2], this.firstArrow.ypoints[2], this.firstArrow.xpoints[0], this.firstArrow.ypoints[0]);
/*     */     }
/*     */ 
/* 305 */     paramGraphics2D.setColor(paramColor1);
/*     */ 
/* 307 */     if ((this.pressed) && (this.mode == 1)) {
/* 308 */       paramGraphics2D.fill(this.secondArrow);
/* 309 */       paramGraphics2D.setColor(paramColor3);
/* 310 */       paramGraphics2D.drawLine(this.secondArrow.xpoints[0], this.secondArrow.ypoints[0], this.secondArrow.xpoints[1], this.secondArrow.ypoints[1]);
/*     */ 
/* 312 */       paramGraphics2D.setColor(paramColor2);
/* 313 */       paramGraphics2D.drawLine(this.secondArrow.xpoints[1], this.secondArrow.ypoints[1], this.secondArrow.xpoints[2], this.secondArrow.ypoints[2]);
/*     */ 
/* 315 */       paramGraphics2D.drawLine(this.secondArrow.xpoints[2], this.secondArrow.ypoints[2], this.secondArrow.xpoints[0], this.secondArrow.ypoints[0]);
/*     */     }
/*     */     else
/*     */     {
/* 320 */       paramGraphics2D.fill(this.secondArrow);
/* 321 */       paramGraphics2D.setColor(paramColor2);
/* 322 */       paramGraphics2D.drawLine(this.secondArrow.xpoints[0], this.secondArrow.ypoints[0], this.secondArrow.xpoints[1], this.secondArrow.ypoints[1]);
/*     */ 
/* 324 */       paramGraphics2D.setColor(paramColor3);
/* 325 */       paramGraphics2D.drawLine(this.secondArrow.xpoints[1], this.secondArrow.ypoints[1], this.secondArrow.xpoints[2], this.secondArrow.ypoints[2]);
/*     */ 
/* 327 */       paramGraphics2D.drawLine(this.secondArrow.xpoints[2], this.secondArrow.ypoints[2], this.secondArrow.xpoints[0], this.secondArrow.ypoints[0]);
/*     */     }
/*     */   }
/*     */ 
/*     */   void startScrolling()
/*     */   {
/* 338 */     log.finer("Start scrolling on " + this);
/*     */ 
/* 340 */     scroll();
/*     */ 
/* 343 */     if (scroller == null)
/*     */     {
/* 346 */       scroller = new XScrollRepeater(this);
/*     */     }
/* 348 */     else scroller.setScrollbar(this);
/*     */ 
/* 350 */     scroller.start();
/*     */   }
/*     */ 
/*     */   void startScrollingInstance()
/*     */   {
/* 358 */     log.finer("Start scrolling on " + this);
/*     */ 
/* 360 */     scroll();
/*     */ 
/* 362 */     this.i_scroller.setScrollbar(this);
/* 363 */     this.i_scroller.start();
/*     */   }
/*     */ 
/*     */   void stopScrollingInstance()
/*     */   {
/* 371 */     log.finer("Stop scrolling on " + this);
/*     */ 
/* 373 */     this.i_scroller.stop();
/*     */   }
/*     */ 
/*     */   public void setMode(int paramInt)
/*     */   {
/* 381 */     this.mode = paramInt;
/*     */   }
/*     */ 
/*     */   void scroll()
/*     */   {
/* 389 */     switch (this.mode) {
/*     */     case 2:
/* 391 */       notifyValue(this.val - this.line);
/* 392 */       return;
/*     */     case 1:
/* 395 */       notifyValue(this.val + this.line);
/* 396 */       return;
/*     */     case 3:
/* 399 */       notifyValue(this.val - this.page);
/* 400 */       return;
/*     */     case 4:
/* 403 */       notifyValue(this.val + this.page);
/* 404 */       return;
/*     */     }
/*     */   }
/*     */ 
/*     */   boolean isInArrow(int paramInt1, int paramInt2)
/*     */   {
/* 412 */     int i = this.alignment == 2 ? paramInt1 : paramInt2;
/* 413 */     int j = getArrowAreaWidth();
/*     */ 
/* 415 */     if ((i < j) || (i > this.barLength - j + 1)) {
/* 416 */       return true;
/*     */     }
/* 418 */     return false;
/*     */   }
/*     */ 
/*     */   boolean isInThumb(int paramInt1, int paramInt2)
/*     */   {
/* 428 */     Rectangle localRectangle = calculateThumbRect();
/*     */ 
/* 433 */     localRectangle.x -= 1;
/* 434 */     localRectangle.width += 3;
/* 435 */     localRectangle.height += 1;
/* 436 */     return localRectangle.contains(paramInt1, paramInt2);
/*     */   }
/*     */ 
/*     */   abstract boolean beforeThumb(int paramInt1, int paramInt2);
/*     */ 
/*     */   public void handleMouseEvent(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/* 453 */     if ((paramInt2 & 0x10) == 0)
/*     */       return;
/*     */     Object localObject;
/* 457 */     if (log.isLoggable(400))
/*     */     {
/* 459 */       switch (paramInt1) {
/*     */       case 501:
/* 461 */         localObject = "press";
/* 462 */         break;
/*     */       case 502:
/* 464 */         localObject = "release";
/* 465 */         break;
/*     */       case 506:
/* 467 */         localObject = "drag";
/* 468 */         break;
/*     */       default:
/* 470 */         localObject = "other";
/*     */       }
/* 472 */       log.finer("Mouse " + (String)localObject + " event in scroll bar " + this + "x = " + paramInt3 + ", y = " + paramInt4 + ", on arrow: " + isInArrow(paramInt3, paramInt4) + ", on thumb: " + isInThumb(paramInt3, paramInt4) + ", before thumb: " + beforeThumb(paramInt3, paramInt4) + ", thumb rect" + calculateThumbRect());
/*     */     }
/*     */ 
/* 478 */     switch (paramInt1) {
/*     */     case 501:
/* 480 */       if (isInArrow(paramInt3, paramInt4)) {
/* 481 */         this.pressed = true;
/* 482 */         if (beforeThumb(paramInt3, paramInt4))
/* 483 */           this.mode = 2;
/*     */         else {
/* 485 */           this.mode = 1;
/*     */         }
/* 487 */         this.sb.repaintScrollbarRequest(this);
/* 488 */         startScrolling();
/*     */       }
/*     */       else
/*     */       {
/* 492 */         if (isInThumb(paramInt3, paramInt4)) {
/* 493 */           this.mode = 5;
/*     */         } else {
/* 495 */           if (beforeThumb(paramInt3, paramInt4))
/* 496 */             this.mode = 3;
/*     */           else {
/* 498 */             this.mode = 4;
/*     */           }
/* 500 */           startScrolling();
/*     */         }
/* 502 */         localObject = calculateThumbRect();
/* 503 */         this.thumbOffset = new Point(paramInt3 - ((Rectangle)localObject).x, paramInt4 - ((Rectangle)localObject).y);
/* 504 */       }break;
/*     */     case 502:
/* 507 */       this.pressed = false;
/* 508 */       this.sb.repaintScrollbarRequest(this);
/* 509 */       scroller.stop();
/* 510 */       if (this.dragging) {
/* 511 */         handleTrackEvent(paramInt3, paramInt4, false);
/* 512 */         this.dragging = false; } break;
/*     */     case 506:
/* 517 */       this.dragging = true;
/* 518 */       handleTrackEvent(paramInt3, paramInt4, true);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void handleTrackEvent(int paramInt1, int paramInt2, boolean paramBoolean) {
/* 523 */     if (this.mode == 5)
/* 524 */       notifyValue(calculateCursorOffset(paramInt1, paramInt2), paramBoolean);
/*     */   }
/*     */ 
/*     */   private int calculateCursorOffset(int paramInt1, int paramInt2)
/*     */   {
/* 529 */     if (this.alignment == 2) {
/* 530 */       if (this.dragging) {
/* 531 */         return Math.max(0, (int)((paramInt1 - (this.thumbOffset.x + getArrowAreaWidth())) / getScaleFactor())) + this.min;
/*     */       }
/* 533 */       return Math.max(0, (int)((paramInt1 - getArrowAreaWidth()) / getScaleFactor())) + this.min;
/*     */     }
/* 535 */     if (this.dragging) {
/* 536 */       return Math.max(0, (int)((paramInt2 - (this.thumbOffset.y + getArrowAreaWidth())) / getScaleFactor())) + this.min;
/*     */     }
/* 538 */     return Math.max(0, (int)((paramInt2 - getArrowAreaWidth()) / getScaleFactor())) + this.min;
/*     */   }
/*     */ 
/*     */   synchronized void setValues(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/* 571 */     if (paramInt4 <= paramInt3) {
/* 572 */       paramInt4 = paramInt3 + 1;
/*     */     }
/* 574 */     if (paramInt2 > paramInt4 - paramInt3) {
/* 575 */       paramInt2 = paramInt4 - paramInt3;
/*     */     }
/* 577 */     if (paramInt2 < 1) {
/* 578 */       paramInt2 = 1;
/*     */     }
/* 580 */     if (paramInt1 < paramInt3) {
/* 581 */       paramInt1 = paramInt3;
/*     */     }
/* 583 */     if (paramInt1 > paramInt4 - paramInt2) {
/* 584 */       paramInt1 = paramInt4 - paramInt2;
/*     */     }
/*     */ 
/* 587 */     this.val = paramInt1;
/* 588 */     this.vis = paramInt2;
/* 589 */     this.min = paramInt3;
/* 590 */     this.max = paramInt4;
/*     */   }
/*     */ 
/*     */   synchronized void setValues(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
/*     */   {
/* 608 */     setValues(paramInt1, paramInt2, paramInt3, paramInt4);
/* 609 */     setUnitIncrement(paramInt5);
/* 610 */     setBlockIncrement(paramInt6);
/*     */   }
/*     */ 
/*     */   int getValue()
/*     */   {
/* 619 */     return this.val;
/*     */   }
/*     */ 
/*     */   synchronized void setValue(int paramInt)
/*     */   {
/* 634 */     setValues(paramInt, this.vis, this.min, this.max);
/*     */   }
/*     */ 
/*     */   int getMinimum()
/*     */   {
/* 643 */     return this.min;
/*     */   }
/*     */ 
/*     */   synchronized void setMinimum(int paramInt)
/*     */   {
/* 654 */     setValues(this.val, this.vis, paramInt, this.max);
/*     */   }
/*     */ 
/*     */   int getMaximum()
/*     */   {
/* 663 */     return this.max;
/*     */   }
/*     */ 
/*     */   synchronized void setMaximum(int paramInt)
/*     */   {
/* 674 */     setValues(this.val, this.vis, this.min, paramInt);
/*     */   }
/*     */ 
/*     */   int getVisibleAmount()
/*     */   {
/* 681 */     return this.vis;
/*     */   }
/*     */ 
/*     */   synchronized void setVisibleAmount(int paramInt)
/*     */   {
/* 690 */     setValues(this.val, paramInt, this.min, this.max);
/*     */   }
/*     */ 
/*     */   synchronized void setUnitIncrement(int paramInt)
/*     */   {
/* 700 */     this.line = paramInt;
/*     */   }
/*     */ 
/*     */   int getUnitIncrement()
/*     */   {
/* 707 */     return this.line;
/*     */   }
/*     */ 
/*     */   synchronized void setBlockIncrement(int paramInt)
/*     */   {
/* 717 */     this.page = paramInt;
/*     */   }
/*     */ 
/*     */   int getBlockIncrement()
/*     */   {
/* 724 */     return this.page;
/*     */   }
/*     */ 
/*     */   int getArrowWidth()
/*     */   {
/* 731 */     return getArrowAreaWidth() - 2;
/*     */   }
/*     */ 
/*     */   int getArrowAreaWidth()
/*     */   {
/* 738 */     return this.arrowArea;
/*     */   }
/*     */ 
/*     */   void calculateArrowWidth() {
/* 742 */     if (this.barLength < 2 * this.barWidth + 5 + 2) {
/* 743 */       this.arrowArea = ((this.barLength - 5 + 2) / 2 - 1);
/*     */     }
/*     */     else
/* 746 */       this.arrowArea = (this.barWidth - 1);
/*     */   }
/*     */ 
/*     */   private double getScaleFactor()
/*     */   {
/* 755 */     double d = (this.barLength - 2 * getArrowAreaWidth()) / Math.max(1, this.max - this.min);
/* 756 */     return d;
/*     */   }
/*     */ 
/*     */   protected Rectangle calculateThumbRect()
/*     */   {
/* 773 */     int k = 0;
/* 774 */     int m = getArrowAreaWidth();
/* 775 */     Rectangle localRectangle = new Rectangle(0, 0, 0, 0);
/*     */ 
/* 777 */     float f2 = this.barLength - 2 * m - 1;
/*     */     int i;
/*     */     int j;
/* 779 */     if (this.alignment == 2) {
/* 780 */       i = 5;
/* 781 */       j = this.height - 3;
/*     */     }
/*     */     else {
/* 784 */       i = this.width - 3;
/* 785 */       j = 5;
/*     */     }
/*     */ 
/* 790 */     float f1 = this.max - this.min;
/*     */ 
/* 793 */     float f3 = f2 / f1;
/*     */ 
/* 797 */     float f4 = this.vis * f3;
/*     */     int n;
/*     */     int i1;
/* 799 */     if (this.alignment == 2)
/*     */     {
/* 801 */       n = (int)(f4 + 0.5D);
/* 802 */       i1 = i;
/* 803 */       if (n > i1) {
/* 804 */         localRectangle.width = n;
/*     */       }
/*     */       else {
/* 807 */         localRectangle.width = i1;
/* 808 */         k = i1;
/*     */       }
/* 810 */       localRectangle.height = j;
/*     */     }
/*     */     else {
/* 813 */       localRectangle.width = i;
/*     */ 
/* 816 */       n = (int)(f4 + 0.5D);
/* 817 */       i1 = j;
/* 818 */       if (n > i1) {
/* 819 */         localRectangle.height = n;
/*     */       }
/*     */       else {
/* 822 */         localRectangle.height = i1;
/* 823 */         k = i1;
/*     */       }
/*     */     }
/*     */ 
/* 827 */     if (k != 0) {
/* 828 */       f2 -= k;
/* 829 */       f1 -= this.vis;
/* 830 */       f3 = f2 / f1;
/*     */     }
/*     */ 
/* 833 */     if (this.alignment == 2) {
/* 834 */       localRectangle.x = ((int)((this.val - this.min) * f3 + 0.5D) + m);
/*     */ 
/* 837 */       localRectangle.y = 1;
/*     */     }
/*     */     else
/*     */     {
/* 841 */       localRectangle.x = 1;
/* 842 */       localRectangle.y = ((int)((this.val - this.min) * f3 + 0.5D) + m);
/*     */     }
/*     */ 
/* 850 */     return localRectangle;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 854 */     return getClass() + "[" + this.width + "x" + this.height + "," + this.barWidth + "x" + this.barLength + "]";
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XScrollbar
 * JD-Core Version:    0.6.2
 */