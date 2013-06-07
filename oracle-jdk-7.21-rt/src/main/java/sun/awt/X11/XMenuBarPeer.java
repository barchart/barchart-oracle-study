/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.awt.AWTEvent;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.Frame;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Menu;
/*     */ import java.awt.MenuBar;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.peer.MenuBarPeer;
/*     */ import java.util.Vector;
/*     */ import sun.awt.AWTAccessor;
/*     */ import sun.awt.AWTAccessor.MenuBarAccessor;
/*     */ import sun.util.logging.PlatformLogger;
/*     */ 
/*     */ public class XMenuBarPeer extends XBaseMenuWindow
/*     */   implements MenuBarPeer
/*     */ {
/*  43 */   private static PlatformLogger log = PlatformLogger.getLogger("sun.awt.X11.XMenuBarPeer");
/*     */   private XFramePeer framePeer;
/*     */   private MenuBar menuBarTarget;
/*  54 */   private XMenuPeer helpMenu = null;
/*     */   private static final int BAR_SPACING_TOP = 3;
/*     */   private static final int BAR_SPACING_BOTTOM = 3;
/*     */   private static final int BAR_SPACING_LEFT = 3;
/*     */   private static final int BAR_SPACING_RIGHT = 3;
/*     */   private static final int BAR_ITEM_SPACING = 2;
/*     */   private static final int BAR_ITEM_MARGIN_LEFT = 10;
/*     */   private static final int BAR_ITEM_MARGIN_RIGHT = 10;
/*     */   private static final int BAR_ITEM_MARGIN_TOP = 2;
/*     */   private static final int BAR_ITEM_MARGIN_BOTTOM = 2;
/*     */   static final int W_DIFF = 12;
/*     */   static final int H_DIFF = 23;
/*     */ 
/*     */   XMenuBarPeer(MenuBar paramMenuBar)
/*     */   {
/* 106 */     this.menuBarTarget = paramMenuBar;
/*     */   }
/*     */ 
/*     */   public void setFont(Font paramFont)
/*     */   {
/* 119 */     resetMapping();
/* 120 */     setItemsFont(paramFont);
/* 121 */     postPaintEvent();
/*     */   }
/*     */ 
/*     */   public void addMenu(Menu paramMenu)
/*     */   {
/* 151 */     addItem(paramMenu);
/* 152 */     postPaintEvent();
/*     */   }
/*     */ 
/*     */   public void delMenu(int paramInt) {
/* 156 */     synchronized (getMenuTreeLock()) {
/* 157 */       XMenuItemPeer localXMenuItemPeer = getItem(paramInt);
/* 158 */       if ((localXMenuItemPeer != null) && (localXMenuItemPeer == this.helpMenu)) {
/* 159 */         this.helpMenu = null;
/*     */       }
/* 161 */       delItem(paramInt);
/*     */     }
/* 163 */     postPaintEvent();
/*     */   }
/*     */ 
/*     */   public void addHelpMenu(Menu paramMenu) {
/* 167 */     XMenuPeer localXMenuPeer = (XMenuPeer)paramMenu.getPeer();
/* 168 */     synchronized (getMenuTreeLock()) {
/* 169 */       this.helpMenu = localXMenuPeer;
/*     */     }
/* 171 */     postPaintEvent();
/*     */   }
/*     */ 
/*     */   public void init(Frame paramFrame)
/*     */   {
/* 183 */     this.target = paramFrame;
/* 184 */     this.framePeer = ((XFramePeer)paramFrame.getPeer());
/* 185 */     XCreateWindowParams localXCreateWindowParams = getDelayedParams();
/* 186 */     localXCreateWindowParams.remove("delayed");
/* 187 */     localXCreateWindowParams.add("parent window", this.framePeer.getShell());
/* 188 */     localXCreateWindowParams.add("target", paramFrame);
/* 189 */     init(localXCreateWindowParams);
/*     */   }
/*     */ 
/*     */   void postInit(XCreateWindowParams paramXCreateWindowParams)
/*     */   {
/* 196 */     super.postInit(paramXCreateWindowParams);
/*     */ 
/* 198 */     Vector localVector = AWTAccessor.getMenuBarAccessor().getMenus(this.menuBarTarget);
/*     */ 
/* 200 */     Menu localMenu = AWTAccessor.getMenuBarAccessor().getHelpMenu(this.menuBarTarget);
/*     */ 
/* 202 */     reloadItems(localVector);
/* 203 */     if (localMenu != null) {
/* 204 */       addHelpMenu(localMenu);
/*     */     }
/* 206 */     xSetVisible(true);
/* 207 */     toFront();
/*     */   }
/*     */ 
/*     */   protected XBaseMenuWindow getParentMenuWindow()
/*     */   {
/* 221 */     return null;
/*     */   }
/*     */ 
/*     */   protected MappingData map()
/*     */   {
/* 228 */     XMenuItemPeer[] arrayOfXMenuItemPeer1 = copyItems();
/* 229 */     int i = arrayOfXMenuItemPeer1.length;
/* 230 */     XMenuPeer localXMenuPeer = this.helpMenu;
/* 231 */     int j = -1;
/*     */ 
/* 233 */     if (localXMenuPeer != null)
/*     */     {
/* 235 */       for (k = 0; k < i; k++) {
/* 236 */         if (arrayOfXMenuItemPeer1[k] == localXMenuPeer) {
/* 237 */           j = k;
/* 238 */           break;
/*     */         }
/*     */       }
/* 241 */       if ((j != -1) && (j != i - 1)) {
/* 242 */         System.arraycopy(arrayOfXMenuItemPeer1, j + 1, arrayOfXMenuItemPeer1, j, i - 1 - j);
/* 243 */         arrayOfXMenuItemPeer1[(i - 1)] = localXMenuPeer;
/*     */       }
/*     */     }
/*     */ 
/* 247 */     int k = 0;
/* 248 */     XMenuItemPeer.TextMetrics[] arrayOfTextMetrics = new XMenuItemPeer.TextMetrics[i];
/* 249 */     for (int m = 0; m < i; m++) {
/* 250 */       arrayOfTextMetrics[m] = arrayOfXMenuItemPeer1[m].getTextMetrics();
/* 251 */       Dimension localDimension1 = arrayOfTextMetrics[m].getTextDimension();
/* 252 */       if (localDimension1 != null) {
/* 253 */         k = Math.max(k, localDimension1.height);
/*     */       }
/*     */     }
/*     */ 
/* 257 */     m = 0;
/* 258 */     int n = 2 + k + 2;
/* 259 */     int i1 = i;
/* 260 */     for (int i2 = 0; i2 < i; i2++) {
/* 261 */       localObject1 = arrayOfXMenuItemPeer1[i2];
/* 262 */       XMenuItemPeer.TextMetrics localTextMetrics = arrayOfTextMetrics[i2];
/* 263 */       Dimension localDimension2 = localTextMetrics.getTextDimension();
/*     */       Object localObject2;
/* 264 */       if (localDimension2 != null) {
/* 265 */         int i3 = 10 + localDimension2.width + 10;
/*     */ 
/* 269 */         if ((m + i3 > this.width) && (i2 > 0)) {
/* 270 */           i1 = i2;
/* 271 */           break;
/*     */         }
/*     */ 
/* 274 */         if ((i2 == i - 1) && (j != -1)) {
/* 275 */           m = Math.max(m, this.width - i3 - 3);
/*     */         }
/* 277 */         localObject2 = new Rectangle(m, 3, i3, n);
/*     */ 
/* 279 */         int i4 = (k + localDimension2.height) / 2 - localTextMetrics.getTextBaseline();
/* 280 */         Point localPoint = new Point(m + 10, 5 + i4);
/* 281 */         m += i3 + 2;
/* 282 */         ((XMenuItemPeer)localObject1).map((Rectangle)localObject2, localPoint);
/*     */       } else {
/* 284 */         Rectangle localRectangle = new Rectangle(m, 3, 0, 0);
/* 285 */         localObject2 = new Point(m + 10, 5);
/*     */       }
/*     */     }
/* 288 */     XMenuItemPeer[] arrayOfXMenuItemPeer2 = new XMenuItemPeer[i1];
/* 289 */     System.arraycopy(arrayOfXMenuItemPeer1, 0, arrayOfXMenuItemPeer2, 0, i1);
/* 290 */     Object localObject1 = new MappingData(arrayOfXMenuItemPeer2, 3 + n + 3);
/* 291 */     return localObject1;
/*     */   }
/*     */ 
/*     */   protected Rectangle getSubmenuBounds(Rectangle paramRectangle, Dimension paramDimension)
/*     */   {
/* 298 */     Rectangle localRectangle1 = toGlobal(paramRectangle);
/* 299 */     Dimension localDimension = Toolkit.getDefaultToolkit().getScreenSize();
/*     */ 
/* 301 */     Rectangle localRectangle2 = fitWindowBelow(localRectangle1, paramDimension, localDimension);
/* 302 */     if (localRectangle2 != null) {
/* 303 */       return localRectangle2;
/*     */     }
/* 305 */     localRectangle2 = fitWindowAbove(localRectangle1, paramDimension, localDimension);
/* 306 */     if (localRectangle2 != null) {
/* 307 */       return localRectangle2;
/*     */     }
/* 309 */     localRectangle2 = fitWindowRight(localRectangle1, paramDimension, localDimension);
/* 310 */     if (localRectangle2 != null) {
/* 311 */       return localRectangle2;
/*     */     }
/* 313 */     localRectangle2 = fitWindowLeft(localRectangle1, paramDimension, localDimension);
/* 314 */     if (localRectangle2 != null) {
/* 315 */       return localRectangle2;
/*     */     }
/* 317 */     return fitWindowToScreen(paramDimension, localDimension);
/*     */   }
/*     */ 
/*     */   protected void updateSize()
/*     */   {
/* 326 */     resetMapping();
/* 327 */     if (this.framePeer != null)
/* 328 */       this.framePeer.reshapeMenubarPeer();
/*     */   }
/*     */ 
/*     */   int getDesiredHeight()
/*     */   {
/* 342 */     MappingData localMappingData = (MappingData)getMappingData();
/* 343 */     return localMappingData.getDesiredHeight();
/*     */   }
/*     */ 
/*     */   boolean isFramePeerEnabled()
/*     */   {
/* 352 */     if (this.framePeer != null) {
/* 353 */       return this.framePeer.isEnabled();
/*     */     }
/* 355 */     return false;
/*     */   }
/*     */ 
/*     */   protected void doDispose()
/*     */   {
/* 368 */     super.doDispose();
/* 369 */     XToolkit.targetDisposedPeer(this.menuBarTarget, this);
/*     */   }
/*     */ 
/*     */   public void reshape(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/* 383 */     if ((paramInt3 != this.width) || (paramInt4 != this.height)) {
/* 384 */       resetMapping();
/*     */     }
/* 386 */     super.reshape(paramInt1, paramInt2, paramInt3, paramInt4);
/*     */   }
/*     */ 
/*     */   void ungrabInputImpl()
/*     */   {
/* 394 */     selectItem(null, false);
/* 395 */     super.ungrabInputImpl();
/* 396 */     postPaintEvent();
/*     */   }
/*     */ 
/*     */   public void paint(Graphics paramGraphics)
/*     */   {
/* 405 */     resetColors();
/*     */ 
/* 407 */     int i = getWidth();
/* 408 */     int j = getHeight();
/*     */ 
/* 410 */     flush();
/*     */ 
/* 412 */     paramGraphics.setColor(getBackgroundColor());
/* 413 */     paramGraphics.fillRect(1, 1, i - 2, j - 2);
/*     */ 
/* 415 */     draw3DRect(paramGraphics, 0, 0, i, j, true);
/*     */ 
/* 418 */     MappingData localMappingData = (MappingData)getMappingData();
/* 419 */     XMenuItemPeer[] arrayOfXMenuItemPeer = localMappingData.getItems();
/* 420 */     XMenuItemPeer localXMenuItemPeer1 = getSelectedItem();
/* 421 */     for (int k = 0; k < arrayOfXMenuItemPeer.length; k++) {
/* 422 */       XMenuItemPeer localXMenuItemPeer2 = arrayOfXMenuItemPeer[k];
/*     */ 
/* 424 */       paramGraphics.setFont(localXMenuItemPeer2.getTargetFont());
/* 425 */       Rectangle localRectangle = localXMenuItemPeer2.getBounds();
/* 426 */       Point localPoint = localXMenuItemPeer2.getTextOrigin();
/* 427 */       if (localXMenuItemPeer2 == localXMenuItemPeer1) {
/* 428 */         paramGraphics.setColor(getSelectedColor());
/* 429 */         paramGraphics.fillRect(localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height);
/* 430 */         draw3DRect(paramGraphics, localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height, false);
/*     */       }
/* 432 */       if ((isFramePeerEnabled()) && (localXMenuItemPeer2.isTargetItemEnabled()))
/* 433 */         paramGraphics.setColor(getForegroundColor());
/*     */       else {
/* 435 */         paramGraphics.setColor(getDisabledColor());
/*     */       }
/* 437 */       paramGraphics.drawString(localXMenuItemPeer2.getTargetLabel(), localPoint.x, localPoint.y);
/*     */     }
/* 439 */     flush();
/*     */   }
/*     */ 
/*     */   void print(Graphics paramGraphics)
/*     */   {
/*     */   }
/*     */ 
/*     */   protected void handleEvent(AWTEvent paramAWTEvent)
/*     */   {
/* 457 */     if ((this.framePeer != null) && (paramAWTEvent.getID() != 800))
/*     */     {
/* 460 */       if (this.framePeer.isModalBlocked()) {
/* 461 */         return;
/*     */       }
/*     */     }
/* 464 */     switch (paramAWTEvent.getID())
/*     */     {
/*     */     case 500:
/*     */     case 501:
/*     */     case 502:
/*     */     case 503:
/*     */     case 504:
/*     */     case 505:
/*     */     case 506:
/* 474 */       if (isFramePeerEnabled())
/* 475 */         doHandleJavaMouseEvent((MouseEvent)paramAWTEvent); break;
/*     */     case 401:
/*     */     case 402:
/* 482 */       if (isFramePeerEnabled())
/* 483 */         doHandleJavaKeyEvent((KeyEvent)paramAWTEvent); break;
/*     */     default:
/* 487 */       super.handleEvent(paramAWTEvent);
/*     */     }
/*     */   }
/*     */ 
/*     */   void handleF10KeyPress(KeyEvent paramKeyEvent)
/*     */   {
/* 505 */     int i = paramKeyEvent.getModifiers();
/* 506 */     if (((i & 0x8) != 0) || ((i & 0x1) != 0) || ((i & 0x2) != 0))
/*     */     {
/* 509 */       return;
/*     */     }
/* 511 */     grabInput();
/* 512 */     selectItem(getFirstSelectableItem(), true);
/*     */   }
/*     */ 
/*     */   public void handleKeyPress(XEvent paramXEvent)
/*     */   {
/* 521 */     XKeyEvent localXKeyEvent = paramXEvent.get_xkey();
/* 522 */     if (log.isLoggable(500)) log.fine(localXKeyEvent.toString());
/* 523 */     if (isEventDisabled(paramXEvent)) {
/* 524 */       return;
/*     */     }
/* 526 */     Component localComponent = getEventSource();
/*     */ 
/* 529 */     handleKeyPress(localXKeyEvent);
/*     */   }
/*     */ 
/*     */   static class MappingData extends XBaseMenuWindow.MappingData
/*     */   {
/*     */     int desiredHeight;
/*     */ 
/*     */     MappingData(XMenuItemPeer[] paramArrayOfXMenuItemPeer, int paramInt)
/*     */     {
/*  83 */       super();
/*  84 */       this.desiredHeight = paramInt;
/*     */     }
/*     */ 
/*     */     MappingData()
/*     */     {
/*  92 */       this.desiredHeight = 0;
/*     */     }
/*     */ 
/*     */     public int getDesiredHeight() {
/*  96 */       return this.desiredHeight;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XMenuBarPeer
 * JD-Core Version:    0.6.2
 */