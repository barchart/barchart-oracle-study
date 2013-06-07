/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.Toolkit;
/*     */ import java.util.Vector;
/*     */ import sun.util.logging.PlatformLogger;
/*     */ 
/*     */ public class XMenuWindow extends XBaseMenuWindow
/*     */ {
/*  45 */   private static PlatformLogger log = PlatformLogger.getLogger("sun.awt.X11.XMenuWindow");
/*     */   private XMenuPeer menuPeer;
/*     */   private static final int WINDOW_SPACING_LEFT = 2;
/*     */   private static final int WINDOW_SPACING_RIGHT = 2;
/*     */   private static final int WINDOW_SPACING_TOP = 2;
/*     */   private static final int WINDOW_SPACING_BOTTOM = 2;
/*     */   private static final int WINDOW_ITEM_INDENT = 15;
/*     */   private static final int WINDOW_ITEM_MARGIN_LEFT = 2;
/*     */   private static final int WINDOW_ITEM_MARGIN_RIGHT = 2;
/*     */   private static final int WINDOW_ITEM_MARGIN_TOP = 2;
/*     */   private static final int WINDOW_ITEM_MARGIN_BOTTOM = 2;
/*     */   private static final int WINDOW_SHORTCUT_SPACING = 10;
/*     */   private static final int CHECKMARK_SIZE = 128;
/*  70 */   private static final int[] CHECKMARK_X = { 1, 25, 56, 124, 124, 85, 64 };
/*  71 */   private static final int[] CHECKMARK_Y = { 59, 35, 67, 0, 12, 66, 123 };
/*     */ 
/*     */   XMenuWindow(XMenuPeer paramXMenuPeer)
/*     */   {
/* 163 */     if (paramXMenuPeer != null) {
/* 164 */       this.menuPeer = paramXMenuPeer;
/* 165 */       this.target = paramXMenuPeer.getContainer().target;
/*     */ 
/* 167 */       Vector localVector = null;
/* 168 */       localVector = getMenuTargetItems();
/* 169 */       reloadItems(localVector);
/*     */     }
/*     */   }
/*     */ 
/*     */   void postInit(XCreateWindowParams paramXCreateWindowParams)
/*     */   {
/* 182 */     super.postInit(paramXCreateWindowParams);
/*     */   }
/*     */ 
/*     */   protected XBaseMenuWindow getParentMenuWindow()
/*     */   {
/* 198 */     return this.menuPeer != null ? this.menuPeer.getContainer() : null;
/*     */   }
/*     */ 
/*     */   protected MappingData map()
/*     */   {
/* 207 */     if (!isCreated()) {
/* 208 */       localObject1 = new MappingData(new XMenuItemPeer[0], new Rectangle(0, 0, 0, 0), new Dimension(0, 0), 0, 0, 0);
/* 209 */       return localObject1;
/*     */     }
/* 211 */     Object localObject1 = copyItems();
/* 212 */     int i = localObject1.length;
/*     */ 
/* 214 */     Dimension localDimension1 = getCaptionSize();
/* 215 */     int j = localDimension1 != null ? localDimension1.width : 0;
/* 216 */     int k = 0;
/* 217 */     int m = 0;
/* 218 */     int n = 0;
/* 219 */     XMenuItemPeer.TextMetrics[] arrayOfTextMetrics = new XMenuItemPeer.TextMetrics[i];
/* 220 */     for (int i1 = 0; i1 < i; i1++) {
/* 221 */       Object localObject2 = localObject1[i1];
/* 222 */       arrayOfTextMetrics[i1] = localObject1[i1].getTextMetrics();
/* 223 */       Dimension localDimension2 = arrayOfTextMetrics[i1].getTextDimension();
/* 224 */       if (localDimension2 != null) {
/* 225 */         if ((localObject1[i1] instanceof XCheckboxMenuItemPeer))
/* 226 */           k = Math.max(k, localDimension2.height);
/* 227 */         else if ((localObject1[i1] instanceof XMenuPeer)) {
/* 228 */           m = Math.max(m, localDimension2.height);
/*     */         }
/* 230 */         j = Math.max(j, localDimension2.width);
/* 231 */         n = Math.max(n, arrayOfTextMetrics[i1].getShortcutWidth());
/*     */       }
/*     */     }
/*     */ 
/* 235 */     i1 = 2;
/* 236 */     int i2 = 4 + k + j;
/* 237 */     if (n > 0) {
/* 238 */       i2 += 10;
/*     */     }
/* 240 */     int i3 = i2 + n;
/* 241 */     int i4 = i3 + m + 2;
/* 242 */     int i5 = 2 + i4 + 2;
/*     */ 
/* 244 */     Rectangle localRectangle1 = null;
/* 245 */     if (localDimension1 != null) {
/* 246 */       localRectangle1 = new Rectangle(2, i1, i4, localDimension1.height);
/* 247 */       i1 += localDimension1.height;
/*     */     } else {
/* 249 */       localRectangle1 = new Rectangle(2, i1, j, 0);
/*     */     }
/*     */ 
/* 252 */     for (int i6 = 0; i6 < i; i6++) {
/* 253 */       localMappingData = localObject1[i6];
/* 254 */       XMenuItemPeer.TextMetrics localTextMetrics = arrayOfTextMetrics[i6];
/* 255 */       Dimension localDimension3 = localTextMetrics.getTextDimension();
/*     */       Object localObject3;
/* 256 */       if (localDimension3 != null) {
/* 257 */         int i7 = 2 + localDimension3.height + 2;
/* 258 */         localObject3 = new Rectangle(2, i1, i4, i7);
/* 259 */         int i8 = (i7 + localDimension3.height) / 2 - localTextMetrics.getTextBaseline();
/* 260 */         Point localPoint = new Point(4 + k, i1 + i8);
/* 261 */         i1 += i7;
/* 262 */         localMappingData.map((Rectangle)localObject3, localPoint);
/*     */       }
/*     */       else
/*     */       {
/* 266 */         Rectangle localRectangle2 = new Rectangle(2, i1, 0, 0);
/* 267 */         localObject3 = new Point(4 + k, i1);
/* 268 */         localMappingData.map(localRectangle2, (Point)localObject3);
/*     */       }
/*     */     }
/* 271 */     i6 = i1 + 2;
/* 272 */     MappingData localMappingData = new MappingData((XMenuItemPeer[])localObject1, localRectangle1, new Dimension(i5, i6), k, i2, i3);
/* 273 */     return localMappingData;
/*     */   }
/*     */ 
/*     */   protected Rectangle getSubmenuBounds(Rectangle paramRectangle, Dimension paramDimension)
/*     */   {
/* 280 */     Rectangle localRectangle1 = toGlobal(paramRectangle);
/* 281 */     Dimension localDimension = Toolkit.getDefaultToolkit().getScreenSize();
/*     */ 
/* 283 */     Rectangle localRectangle2 = fitWindowRight(localRectangle1, paramDimension, localDimension);
/* 284 */     if (localRectangle2 != null) {
/* 285 */       return localRectangle2;
/*     */     }
/* 287 */     localRectangle2 = fitWindowBelow(localRectangle1, paramDimension, localDimension);
/* 288 */     if (localRectangle2 != null) {
/* 289 */       return localRectangle2;
/*     */     }
/* 291 */     localRectangle2 = fitWindowAbove(localRectangle1, paramDimension, localDimension);
/* 292 */     if (localRectangle2 != null) {
/* 293 */       return localRectangle2;
/*     */     }
/* 295 */     localRectangle2 = fitWindowLeft(localRectangle1, paramDimension, localDimension);
/* 296 */     if (localRectangle2 != null) {
/* 297 */       return localRectangle2;
/*     */     }
/* 299 */     return fitWindowToScreen(paramDimension, localDimension);
/*     */   }
/*     */ 
/*     */   protected void updateSize()
/*     */   {
/* 307 */     resetMapping();
/* 308 */     if (isShowing())
/* 309 */       XToolkit.executeOnEventHandlerThread(this.target, new Runnable() {
/*     */         public void run() {
/* 311 */           Dimension localDimension = XMenuWindow.this.getDesiredSize();
/* 312 */           XMenuWindow.this.reshape(XMenuWindow.this.x, XMenuWindow.this.y, localDimension.width, localDimension.height);
/*     */         }
/*     */       });
/*     */   }
/*     */ 
/*     */   protected Dimension getCaptionSize()
/*     */   {
/* 331 */     return null;
/*     */   }
/*     */ 
/*     */   protected void paintCaption(Graphics paramGraphics, Rectangle paramRectangle)
/*     */   {
/*     */   }
/*     */ 
/*     */   XMenuPeer getMenuPeer()
/*     */   {
/* 352 */     return this.menuPeer;
/*     */   }
/*     */ 
/*     */   Vector getMenuTargetItems()
/*     */   {
/* 360 */     return this.menuPeer.getTargetItems();
/*     */   }
/*     */ 
/*     */   Dimension getDesiredSize()
/*     */   {
/* 367 */     MappingData localMappingData = (MappingData)getMappingData();
/* 368 */     return localMappingData.getDesiredSize();
/*     */   }
/*     */ 
/*     */   boolean isCreated()
/*     */   {
/* 375 */     return getWindow() != 0L;
/*     */   }
/*     */ 
/*     */   boolean ensureCreated()
/*     */   {
/* 382 */     if (!isCreated()) {
/* 383 */       XCreateWindowParams localXCreateWindowParams = getDelayedParams();
/* 384 */       localXCreateWindowParams.remove("delayed");
/* 385 */       localXCreateWindowParams.add("overrideRedirect", Boolean.TRUE);
/* 386 */       localXCreateWindowParams.add("target", this.target);
/* 387 */       init(localXCreateWindowParams);
/*     */     }
/* 389 */     return true;
/*     */   }
/*     */ 
/*     */   void show(Rectangle paramRectangle)
/*     */   {
/* 399 */     if (!isCreated()) {
/* 400 */       return;
/*     */     }
/* 402 */     if (log.isLoggable(400)) {
/* 403 */       log.finer("showing menu window + " + getWindow() + " at " + paramRectangle);
/*     */     }
/* 405 */     XToolkit.awtLock();
/*     */     try {
/* 407 */       reshape(paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height);
/* 408 */       xSetVisible(true);
/*     */ 
/* 411 */       toFront();
/* 412 */       selectItem(getFirstSelectableItem(), false);
/*     */     } finally {
/* 414 */       XToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   void hide()
/*     */   {
/* 422 */     selectItem(null, false);
/* 423 */     xSetVisible(false);
/*     */   }
/*     */ 
/*     */   public void paint(Graphics paramGraphics)
/*     */   {
/* 436 */     resetColors();
/*     */ 
/* 438 */     int i = getWidth();
/* 439 */     int j = getHeight();
/*     */ 
/* 441 */     flush();
/*     */ 
/* 443 */     paramGraphics.setColor(getBackgroundColor());
/* 444 */     paramGraphics.fillRect(1, 1, i - 2, j - 2);
/* 445 */     draw3DRect(paramGraphics, 0, 0, i, j, true);
/*     */ 
/* 448 */     MappingData localMappingData = (MappingData)getMappingData();
/*     */ 
/* 451 */     paintCaption(paramGraphics, localMappingData.getCaptionRect());
/*     */ 
/* 454 */     XMenuItemPeer[] arrayOfXMenuItemPeer = localMappingData.getItems();
/* 455 */     Dimension localDimension1 = localMappingData.getDesiredSize();
/* 456 */     XMenuItemPeer localXMenuItemPeer1 = getSelectedItem();
/* 457 */     for (int k = 0; k < arrayOfXMenuItemPeer.length; k++) {
/* 458 */       XMenuItemPeer localXMenuItemPeer2 = arrayOfXMenuItemPeer[k];
/* 459 */       XMenuItemPeer.TextMetrics localTextMetrics = localXMenuItemPeer2.getTextMetrics();
/* 460 */       Rectangle localRectangle = localXMenuItemPeer2.getBounds();
/* 461 */       if (localXMenuItemPeer2.isSeparator()) {
/* 462 */         draw3DRect(paramGraphics, localRectangle.x, localRectangle.y + localRectangle.height / 2, localRectangle.width, 2, false);
/*     */       }
/*     */       else {
/* 465 */         paramGraphics.setFont(localXMenuItemPeer2.getTargetFont());
/* 466 */         Point localPoint = localXMenuItemPeer2.getTextOrigin();
/* 467 */         Dimension localDimension2 = localTextMetrics.getTextDimension();
/* 468 */         if (localXMenuItemPeer2 == localXMenuItemPeer1) {
/* 469 */           paramGraphics.setColor(getSelectedColor());
/* 470 */           paramGraphics.fillRect(localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height);
/* 471 */           draw3DRect(paramGraphics, localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height, false);
/*     */         }
/* 473 */         paramGraphics.setColor(localXMenuItemPeer2.isTargetItemEnabled() ? getForegroundColor() : getDisabledColor());
/* 474 */         paramGraphics.drawString(localXMenuItemPeer2.getTargetLabel(), localPoint.x, localPoint.y);
/* 475 */         String str = localXMenuItemPeer2.getShortcutText();
/* 476 */         if (str != null)
/* 477 */           paramGraphics.drawString(str, localMappingData.getShortcutOrigin(), localPoint.y);
/*     */         int m;
/*     */         int n;
/*     */         int i1;
/*     */         int i2;
/* 479 */         if ((localXMenuItemPeer2 instanceof XMenuPeer))
/*     */         {
/* 481 */           m = localDimension2.height * 4 / 5;
/* 482 */           n = localDimension2.height * 4 / 5;
/* 483 */           i1 = localRectangle.x + localRectangle.width - m - 2 - 2;
/* 484 */           i2 = localRectangle.y + (localRectangle.height - n) / 2;
/*     */ 
/* 486 */           paramGraphics.setColor(localXMenuItemPeer2.isTargetItemEnabled() ? getDarkShadowColor() : getDisabledColor());
/* 487 */           paramGraphics.drawLine(i1, i2 + n, i1 + m, i2 + n / 2);
/* 488 */           paramGraphics.setColor(localXMenuItemPeer2.isTargetItemEnabled() ? getLightShadowColor() : getDisabledColor());
/* 489 */           paramGraphics.drawLine(i1, i2, i1 + m, i2 + n / 2);
/* 490 */           paramGraphics.drawLine(i1, i2, i1, i2 + n);
/* 491 */         } else if ((localXMenuItemPeer2 instanceof XCheckboxMenuItemPeer))
/*     */         {
/* 493 */           m = localDimension2.height * 4 / 5;
/* 494 */           n = localDimension2.height * 4 / 5;
/* 495 */           i1 = 4;
/* 496 */           i2 = localRectangle.y + (localRectangle.height - n) / 2;
/* 497 */           boolean bool = ((XCheckboxMenuItemPeer)localXMenuItemPeer2).getTargetState();
/*     */ 
/* 499 */           if (bool) {
/* 500 */             paramGraphics.setColor(getSelectedColor());
/* 501 */             paramGraphics.fillRect(i1, i2, m, n);
/* 502 */             draw3DRect(paramGraphics, i1, i2, m, n, false);
/* 503 */             int[] arrayOfInt1 = new int[CHECKMARK_X.length];
/* 504 */             int[] arrayOfInt2 = new int[CHECKMARK_X.length];
/* 505 */             for (int i3 = 0; i3 < CHECKMARK_X.length; i3++) {
/* 506 */               arrayOfInt1[i3] = (i1 + CHECKMARK_X[i3] * m / 128);
/* 507 */               arrayOfInt2[i3] = (i2 + CHECKMARK_Y[i3] * n / 128);
/*     */             }
/* 509 */             paramGraphics.setColor(localXMenuItemPeer2.isTargetItemEnabled() ? getForegroundColor() : getDisabledColor());
/* 510 */             paramGraphics.fillPolygon(arrayOfInt1, arrayOfInt2, CHECKMARK_X.length);
/*     */           } else {
/* 512 */             paramGraphics.setColor(getBackgroundColor());
/* 513 */             paramGraphics.fillRect(i1, i2, m, n);
/* 514 */             draw3DRect(paramGraphics, i1, i2, m, n, true);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 519 */     flush();
/*     */   }
/*     */ 
/*     */   static class MappingData extends XBaseMenuWindow.MappingData
/*     */   {
/*     */     private Rectangle captionRect;
/*     */     private Dimension desiredSize;
/*     */     private int leftMarkWidth;
/*     */     private int shortcutOrigin;
/*     */     private int rightMarkOrigin;
/*     */ 
/*     */     MappingData(XMenuItemPeer[] paramArrayOfXMenuItemPeer, Rectangle paramRectangle, Dimension paramDimension, int paramInt1, int paramInt2, int paramInt3)
/*     */     {
/* 110 */       super();
/* 111 */       this.captionRect = paramRectangle;
/* 112 */       this.desiredSize = paramDimension;
/* 113 */       this.leftMarkWidth = paramInt1;
/* 114 */       this.shortcutOrigin = paramInt2;
/* 115 */       this.rightMarkOrigin = paramInt3;
/*     */     }
/*     */ 
/*     */     MappingData()
/*     */     {
/* 123 */       this.desiredSize = new Dimension(0, 0);
/* 124 */       this.leftMarkWidth = 0;
/* 125 */       this.shortcutOrigin = 0;
/* 126 */       this.rightMarkOrigin = 0;
/*     */     }
/*     */ 
/*     */     public Rectangle getCaptionRect() {
/* 130 */       return this.captionRect;
/*     */     }
/*     */ 
/*     */     public Dimension getDesiredSize() {
/* 134 */       return this.desiredSize;
/*     */     }
/*     */ 
/*     */     public int getShortcutOrigin() {
/* 138 */       return this.shortcutOrigin;
/*     */     }
/*     */ 
/*     */     public int getLeftMarkWidth() {
/* 142 */       return this.leftMarkWidth;
/*     */     }
/*     */ 
/*     */     public int getRightMarkOrigin() {
/* 146 */       return this.rightMarkOrigin;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XMenuWindow
 * JD-Core Version:    0.6.2
 */