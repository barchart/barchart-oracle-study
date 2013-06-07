/*      */ package sun.awt.X11;
/*      */ 
/*      */ import java.awt.AWTEvent;
/*      */ import java.awt.Color;
/*      */ import java.awt.Component;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.EventQueue;
/*      */ import java.awt.Font;
/*      */ import java.awt.Graphics;
/*      */ import java.awt.MenuItem;
/*      */ import java.awt.Point;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.SystemColor;
/*      */ import java.awt.event.KeyEvent;
/*      */ import java.awt.event.MouseEvent;
/*      */ import java.awt.event.PaintEvent;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Vector;
/*      */ import sun.java2d.SurfaceData;
/*      */ import sun.util.logging.PlatformLogger;
/*      */ 
/*      */ public abstract class XBaseMenuWindow extends XWindow
/*      */ {
/*   52 */   private static PlatformLogger log = PlatformLogger.getLogger("sun.awt.X11.XBaseMenuWindow");
/*      */   private Color backgroundColor;
/*      */   private Color foregroundColor;
/*      */   private Color lightShadowColor;
/*      */   private Color darkShadowColor;
/*      */   private Color selectedColor;
/*      */   private Color disabledColor;
/*      */   private ArrayList<XMenuItemPeer> items;
/*   73 */   private int selectedIndex = -1;
/*      */ 
/*   78 */   private XMenuPeer showingSubmenu = null;
/*      */ 
/*   92 */   private static Object menuTreeLock = new Object();
/*      */ 
/*  108 */   private XMenuPeer showingMousePressedSubmenu = null;
/*      */ 
/*  116 */   protected Point grabInputPoint = null;
/*  117 */   protected boolean hasPointerMoved = false;
/*      */   private MappingData mappingData;
/*      */ 
/*      */   XBaseMenuWindow()
/*      */   {
/*  175 */     super(new XCreateWindowParams(new Object[] { "delayed", Boolean.TRUE }));
/*      */   }
/*      */ 
/*      */   protected abstract XBaseMenuWindow getParentMenuWindow();
/*      */ 
/*      */   protected abstract MappingData map();
/*      */ 
/*      */   protected abstract Rectangle getSubmenuBounds(Rectangle paramRectangle, Dimension paramDimension);
/*      */ 
/*      */   protected abstract void updateSize();
/*      */ 
/*      */   void instantPreInit(XCreateWindowParams paramXCreateWindowParams)
/*      */   {
/*  229 */     super.instantPreInit(paramXCreateWindowParams);
/*  230 */     this.items = new ArrayList();
/*      */   }
/*      */ 
/*      */   static Object getMenuTreeLock()
/*      */   {
/*  243 */     return menuTreeLock;
/*      */   }
/*      */ 
/*      */   protected void resetMapping()
/*      */   {
/*  251 */     this.mappingData = null;
/*      */   }
/*      */ 
/*      */   void postPaintEvent()
/*      */   {
/*  258 */     if (isShowing()) {
/*  259 */       PaintEvent localPaintEvent = new PaintEvent(this.target, 800, new Rectangle(0, 0, this.width, this.height));
/*      */ 
/*  261 */       postEvent(localPaintEvent);
/*      */     }
/*      */   }
/*      */ 
/*      */   XMenuItemPeer getItem(int paramInt)
/*      */   {
/*  276 */     if (paramInt >= 0) {
/*  277 */       synchronized (getMenuTreeLock()) {
/*  278 */         if (this.items.size() > paramInt) {
/*  279 */           return (XMenuItemPeer)this.items.get(paramInt);
/*      */         }
/*      */       }
/*      */     }
/*  283 */     return null;
/*      */   }
/*      */ 
/*      */   XMenuItemPeer[] copyItems()
/*      */   {
/*  290 */     synchronized (getMenuTreeLock()) {
/*  291 */       return (XMenuItemPeer[])this.items.toArray(new XMenuItemPeer[0]);
/*      */     }
/*      */   }
/*      */ 
/*      */   XMenuItemPeer getSelectedItem()
/*      */   {
/*  300 */     synchronized (getMenuTreeLock()) {
/*  301 */       if ((this.selectedIndex >= 0) && 
/*  302 */         (this.items.size() > this.selectedIndex)) {
/*  303 */         return (XMenuItemPeer)this.items.get(this.selectedIndex);
/*      */       }
/*      */ 
/*  306 */       return null;
/*      */     }
/*      */   }
/*      */ 
/*      */   XMenuPeer getShowingSubmenu()
/*      */   {
/*  314 */     synchronized (getMenuTreeLock()) {
/*  315 */       return this.showingSubmenu;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addItem(MenuItem paramMenuItem)
/*      */   {
/*  326 */     XMenuItemPeer localXMenuItemPeer = (XMenuItemPeer)paramMenuItem.getPeer();
/*  327 */     if (localXMenuItemPeer != null) {
/*  328 */       localXMenuItemPeer.setContainer(this);
/*  329 */       synchronized (getMenuTreeLock()) {
/*  330 */         this.items.add(localXMenuItemPeer);
/*      */       }
/*      */     }
/*  333 */     else if (log.isLoggable(500)) {
/*  334 */       log.fine("WARNING: Attempt to add menu item without a peer");
/*      */     }
/*      */ 
/*  337 */     updateSize();
/*      */   }
/*      */ 
/*      */   public void delItem(int paramInt)
/*      */   {
/*  345 */     synchronized (getMenuTreeLock()) {
/*  346 */       if (this.selectedIndex == paramInt)
/*  347 */         selectItem(null, false);
/*  348 */       else if (this.selectedIndex > paramInt) {
/*  349 */         this.selectedIndex -= 1;
/*      */       }
/*  351 */       if (paramInt < this.items.size()) {
/*  352 */         this.items.remove(paramInt);
/*      */       }
/*  354 */       else if (log.isLoggable(500)) {
/*  355 */         log.fine("WARNING: Attempt to remove non-existing menu item, index : " + paramInt + ", item count : " + this.items.size());
/*      */       }
/*      */     }
/*      */ 
/*  359 */     updateSize();
/*      */   }
/*      */ 
/*      */   public void reloadItems(Vector paramVector)
/*      */   {
/*  367 */     synchronized (getMenuTreeLock()) {
/*  368 */       this.items.clear();
/*  369 */       MenuItem[] arrayOfMenuItem = (MenuItem[])paramVector.toArray(new MenuItem[0]);
/*  370 */       int i = arrayOfMenuItem.length;
/*  371 */       for (int j = 0; j < i; j++)
/*  372 */         addItem(arrayOfMenuItem[j]);
/*      */     }
/*      */   }
/*      */ 
/*      */   void selectItem(XMenuItemPeer paramXMenuItemPeer, boolean paramBoolean)
/*      */   {
/*  385 */     synchronized (getMenuTreeLock()) {
/*  386 */       XMenuPeer localXMenuPeer1 = getShowingSubmenu();
/*  387 */       int i = paramXMenuItemPeer != null ? this.items.indexOf(paramXMenuItemPeer) : -1;
/*  388 */       if (this.selectedIndex != i) {
/*  389 */         if (log.isLoggable(300)) {
/*  390 */           log.finest("Selected index changed, was : " + this.selectedIndex + ", new : " + i);
/*      */         }
/*  392 */         this.selectedIndex = i;
/*  393 */         postPaintEvent();
/*      */       }
/*  395 */       final XMenuPeer localXMenuPeer2 = (paramBoolean) && ((paramXMenuItemPeer instanceof XMenuPeer)) ? (XMenuPeer)paramXMenuItemPeer : null;
/*  396 */       if (localXMenuPeer2 != localXMenuPeer1)
/*  397 */         XToolkit.executeOnEventHandlerThread(this.target, new Runnable() {
/*      */           public void run() {
/*  399 */             XBaseMenuWindow.this.doShowSubmenu(localXMenuPeer2);
/*      */           }
/*      */         });
/*      */     }
/*      */   }
/*      */ 
/*      */   private void doShowSubmenu(XMenuPeer paramXMenuPeer)
/*      */   {
/*  414 */     Object localObject1 = paramXMenuPeer != null ? paramXMenuPeer.getMenuWindow() : null;
/*  415 */     Dimension localDimension = null;
/*  416 */     Rectangle localRectangle = null;
/*      */ 
/*  422 */     if (localObject1 != null) {
/*  423 */       localObject1.ensureCreated();
/*      */     }
/*  425 */     XToolkit.awtLock();
/*      */     try {
/*  427 */       synchronized (getMenuTreeLock()) {
/*  428 */         if (this.showingSubmenu != paramXMenuPeer) {
/*  429 */           if (log.isLoggable(400)) {
/*  430 */             log.finest("Changing showing submenu");
/*      */           }
/*  432 */           if (this.showingSubmenu != null) {
/*  433 */             XMenuWindow localXMenuWindow = this.showingSubmenu.getMenuWindow();
/*  434 */             if (localXMenuWindow != null) {
/*  435 */               localXMenuWindow.hide();
/*      */             }
/*      */           }
/*  438 */           if (paramXMenuPeer != null) {
/*  439 */             localDimension = localObject1.getDesiredSize();
/*  440 */             localRectangle = localObject1.getParentMenuWindow().getSubmenuBounds(paramXMenuPeer.getBounds(), localDimension);
/*  441 */             localObject1.show(localRectangle);
/*      */           }
/*  443 */           this.showingSubmenu = paramXMenuPeer;
/*      */         }
/*      */       }
/*      */     } finally {
/*  447 */       XToolkit.awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   final void setItemsFont(Font paramFont) {
/*  452 */     XMenuItemPeer[] arrayOfXMenuItemPeer = copyItems();
/*  453 */     int i = arrayOfXMenuItemPeer.length;
/*  454 */     for (int j = 0; j < i; j++)
/*  455 */       arrayOfXMenuItemPeer[j].setFont(paramFont);
/*      */   }
/*      */ 
/*      */   MappingData getMappingData()
/*      */   {
/*  473 */     MappingData localMappingData = this.mappingData;
/*  474 */     if (localMappingData == null) {
/*  475 */       localMappingData = map();
/*  476 */       this.mappingData = localMappingData;
/*      */     }
/*  478 */     return (MappingData)localMappingData.clone();
/*      */   }
/*      */ 
/*      */   XMenuItemPeer getItemFromPoint(Point paramPoint)
/*      */   {
/*  487 */     XMenuItemPeer[] arrayOfXMenuItemPeer = getMappingData().getItems();
/*  488 */     int i = arrayOfXMenuItemPeer.length;
/*  489 */     for (int j = 0; j < i; j++) {
/*  490 */       if (arrayOfXMenuItemPeer[j].getBounds().contains(paramPoint)) {
/*  491 */         return arrayOfXMenuItemPeer[j];
/*      */       }
/*      */     }
/*  494 */     return null;
/*      */   }
/*      */ 
/*      */   XMenuItemPeer getNextSelectableItem()
/*      */   {
/*  505 */     XMenuItemPeer[] arrayOfXMenuItemPeer = getMappingData().getItems();
/*  506 */     XMenuItemPeer localXMenuItemPeer1 = getSelectedItem();
/*  507 */     int i = arrayOfXMenuItemPeer.length;
/*      */ 
/*  509 */     int j = -1;
/*  510 */     for (int k = 0; k < i; k++) {
/*  511 */       if (arrayOfXMenuItemPeer[k] == localXMenuItemPeer1) {
/*  512 */         j = k;
/*  513 */         break;
/*      */       }
/*      */     }
/*  516 */     k = j == i - 1 ? 0 : j + 1;
/*      */ 
/*  521 */     for (int m = 0; m < i; m++) {
/*  522 */       XMenuItemPeer localXMenuItemPeer2 = arrayOfXMenuItemPeer[k];
/*  523 */       if ((!localXMenuItemPeer2.isSeparator()) && (localXMenuItemPeer2.isTargetItemEnabled())) {
/*  524 */         return localXMenuItemPeer2;
/*      */       }
/*  526 */       k++;
/*  527 */       if (k >= i) {
/*  528 */         k = 0;
/*      */       }
/*      */     }
/*      */ 
/*  532 */     return null;
/*      */   }
/*      */ 
/*      */   XMenuItemPeer getPrevSelectableItem()
/*      */   {
/*  540 */     XMenuItemPeer[] arrayOfXMenuItemPeer = getMappingData().getItems();
/*  541 */     XMenuItemPeer localXMenuItemPeer1 = getSelectedItem();
/*  542 */     int i = arrayOfXMenuItemPeer.length;
/*      */ 
/*  544 */     int j = -1;
/*  545 */     for (int k = 0; k < i; k++) {
/*  546 */       if (arrayOfXMenuItemPeer[k] == localXMenuItemPeer1) {
/*  547 */         j = k;
/*  548 */         break;
/*      */       }
/*      */     }
/*  551 */     k = j <= 0 ? i - 1 : j - 1;
/*      */ 
/*  553 */     for (int m = 0; m < i; m++) {
/*  554 */       XMenuItemPeer localXMenuItemPeer2 = arrayOfXMenuItemPeer[k];
/*  555 */       if ((!localXMenuItemPeer2.isSeparator()) && (localXMenuItemPeer2.isTargetItemEnabled())) {
/*  556 */         return localXMenuItemPeer2;
/*      */       }
/*  558 */       k--;
/*  559 */       if (k < 0) {
/*  560 */         k = i - 1;
/*      */       }
/*      */     }
/*      */ 
/*  564 */     return null;
/*      */   }
/*      */ 
/*      */   XMenuItemPeer getFirstSelectableItem()
/*      */   {
/*  572 */     XMenuItemPeer[] arrayOfXMenuItemPeer = getMappingData().getItems();
/*  573 */     int i = arrayOfXMenuItemPeer.length;
/*  574 */     for (int j = 0; j < i; j++) {
/*  575 */       XMenuItemPeer localXMenuItemPeer = arrayOfXMenuItemPeer[j];
/*  576 */       if ((!localXMenuItemPeer.isSeparator()) && (localXMenuItemPeer.isTargetItemEnabled())) {
/*  577 */         return localXMenuItemPeer;
/*      */       }
/*      */     }
/*      */ 
/*  581 */     return null;
/*      */   }
/*      */ 
/*      */   XBaseMenuWindow getShowingLeaf()
/*      */   {
/*  596 */     synchronized (getMenuTreeLock()) {
/*  597 */       Object localObject1 = this;
/*  598 */       XMenuPeer localXMenuPeer = ((XBaseMenuWindow)localObject1).getShowingSubmenu();
/*  599 */       while (localXMenuPeer != null) {
/*  600 */         localObject1 = localXMenuPeer.getMenuWindow();
/*  601 */         localXMenuPeer = ((XBaseMenuWindow)localObject1).getShowingSubmenu();
/*      */       }
/*  603 */       return localObject1;
/*      */     }
/*      */   }
/*      */ 
/*      */   XBaseMenuWindow getRootMenuWindow()
/*      */   {
/*  612 */     synchronized (getMenuTreeLock()) {
/*  613 */       Object localObject1 = this;
/*  614 */       XBaseMenuWindow localXBaseMenuWindow = ((XBaseMenuWindow)localObject1).getParentMenuWindow();
/*  615 */       while (localXBaseMenuWindow != null) {
/*  616 */         localObject1 = localXBaseMenuWindow;
/*  617 */         localXBaseMenuWindow = ((XBaseMenuWindow)localObject1).getParentMenuWindow();
/*      */       }
/*  619 */       return localObject1;
/*      */     }
/*      */   }
/*      */ 
/*      */   XBaseMenuWindow getMenuWindowFromPoint(Point paramPoint)
/*      */   {
/*  630 */     synchronized (getMenuTreeLock()) {
/*  631 */       XBaseMenuWindow localXBaseMenuWindow = getShowingLeaf();
/*  632 */       while (localXBaseMenuWindow != null) {
/*  633 */         Rectangle localRectangle = new Rectangle(localXBaseMenuWindow.toGlobal(new Point(0, 0)), localXBaseMenuWindow.getSize());
/*  634 */         if (localRectangle.contains(paramPoint)) {
/*  635 */           return localXBaseMenuWindow;
/*      */         }
/*  637 */         localXBaseMenuWindow = localXBaseMenuWindow.getParentMenuWindow();
/*      */       }
/*  639 */       return null;
/*      */     }
/*      */   }
/*      */ 
/*      */   Rectangle fitWindowBelow(Rectangle paramRectangle, Dimension paramDimension1, Dimension paramDimension2)
/*      */   {
/*  663 */     int i = paramDimension1.width;
/*  664 */     int j = paramDimension1.height;
/*      */ 
/*  668 */     int k = paramRectangle.x > 0 ? paramRectangle.x : 0;
/*  669 */     int m = paramRectangle.y + paramRectangle.height > 0 ? paramRectangle.y + paramRectangle.height : 0;
/*  670 */     if (m + j <= paramDimension2.height)
/*      */     {
/*  672 */       if (i > paramDimension2.width) {
/*  673 */         i = paramDimension2.width;
/*      */       }
/*  675 */       if (k + i > paramDimension2.width) {
/*  676 */         k = paramDimension2.width - i;
/*      */       }
/*  678 */       return new Rectangle(k, m, i, j);
/*      */     }
/*  680 */     return null;
/*      */   }
/*      */ 
/*      */   Rectangle fitWindowAbove(Rectangle paramRectangle, Dimension paramDimension1, Dimension paramDimension2)
/*      */   {
/*  692 */     int i = paramDimension1.width;
/*  693 */     int j = paramDimension1.height;
/*      */ 
/*  697 */     int k = paramRectangle.x > 0 ? paramRectangle.x : 0;
/*  698 */     int m = paramRectangle.y > paramDimension2.height ? paramDimension2.height - j : paramRectangle.y - j;
/*  699 */     if (m >= 0)
/*      */     {
/*  701 */       if (i > paramDimension2.width) {
/*  702 */         i = paramDimension2.width;
/*      */       }
/*  704 */       if (k + i > paramDimension2.width) {
/*  705 */         k = paramDimension2.width - i;
/*      */       }
/*  707 */       return new Rectangle(k, m, i, j);
/*      */     }
/*  709 */     return null;
/*      */   }
/*      */ 
/*      */   Rectangle fitWindowRight(Rectangle paramRectangle, Dimension paramDimension1, Dimension paramDimension2)
/*      */   {
/*  721 */     int i = paramDimension1.width;
/*  722 */     int j = paramDimension1.height;
/*      */ 
/*  726 */     int k = paramRectangle.x + paramRectangle.width > 0 ? paramRectangle.x + paramRectangle.width : 0;
/*  727 */     int m = paramRectangle.y > 0 ? paramRectangle.y : 0;
/*  728 */     if (k + i <= paramDimension2.width)
/*      */     {
/*  730 */       if (j > paramDimension2.height) {
/*  731 */         j = paramDimension2.height;
/*      */       }
/*  733 */       if (m + j > paramDimension2.height) {
/*  734 */         m = paramDimension2.height - j;
/*      */       }
/*  736 */       return new Rectangle(k, m, i, j);
/*      */     }
/*  738 */     return null;
/*      */   }
/*      */ 
/*      */   Rectangle fitWindowLeft(Rectangle paramRectangle, Dimension paramDimension1, Dimension paramDimension2)
/*      */   {
/*  750 */     int i = paramDimension1.width;
/*  751 */     int j = paramDimension1.height;
/*      */ 
/*  755 */     int k = paramRectangle.x < paramDimension2.width ? paramRectangle.x - i : paramDimension2.width - i;
/*  756 */     int m = paramRectangle.y > 0 ? paramRectangle.y : 0;
/*  757 */     if (k >= 0)
/*      */     {
/*  759 */       if (j > paramDimension2.height) {
/*  760 */         j = paramDimension2.height;
/*      */       }
/*  762 */       if (m + j > paramDimension2.height) {
/*  763 */         m = paramDimension2.height - j;
/*      */       }
/*  765 */       return new Rectangle(k, m, i, j);
/*      */     }
/*  767 */     return null;
/*      */   }
/*      */ 
/*      */   Rectangle fitWindowToScreen(Dimension paramDimension1, Dimension paramDimension2)
/*      */   {
/*  779 */     int i = paramDimension1.width < paramDimension2.width ? paramDimension1.width : paramDimension2.width;
/*  780 */     int j = paramDimension1.height < paramDimension2.height ? paramDimension1.height : paramDimension2.height;
/*  781 */     return new Rectangle(0, 0, i, j);
/*      */   }
/*      */ 
/*      */   void resetColors()
/*      */   {
/*  799 */     replaceColors(this.target == null ? SystemColor.window : this.target.getBackground());
/*      */   }
/*      */ 
/*      */   void replaceColors(Color paramColor)
/*      */   {
/*  809 */     if (paramColor != this.backgroundColor) {
/*  810 */       this.backgroundColor = paramColor;
/*      */ 
/*  812 */       int i = paramColor.getRed();
/*  813 */       int j = paramColor.getGreen();
/*  814 */       int k = paramColor.getBlue();
/*      */ 
/*  816 */       this.foregroundColor = new Color(MotifColorUtilities.calculateForegroundFromBackground(i, j, k));
/*  817 */       this.lightShadowColor = new Color(MotifColorUtilities.calculateTopShadowFromBackground(i, j, k));
/*  818 */       this.darkShadowColor = new Color(MotifColorUtilities.calculateBottomShadowFromBackground(i, j, k));
/*  819 */       this.selectedColor = new Color(MotifColorUtilities.calculateSelectFromBackground(i, j, k));
/*  820 */       this.disabledColor = (paramColor.equals(Color.BLACK) ? this.foregroundColor.darker() : paramColor.darker());
/*      */     }
/*      */   }
/*      */ 
/*      */   Color getBackgroundColor() {
/*  825 */     return this.backgroundColor;
/*      */   }
/*      */ 
/*      */   Color getForegroundColor() {
/*  829 */     return this.foregroundColor;
/*      */   }
/*      */ 
/*      */   Color getLightShadowColor() {
/*  833 */     return this.lightShadowColor;
/*      */   }
/*      */ 
/*      */   Color getDarkShadowColor() {
/*  837 */     return this.darkShadowColor;
/*      */   }
/*      */ 
/*      */   Color getSelectedColor() {
/*  841 */     return this.selectedColor;
/*      */   }
/*      */ 
/*      */   Color getDisabledColor() {
/*  845 */     return this.disabledColor;
/*      */   }
/*      */ 
/*      */   void draw3DRect(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean)
/*      */   {
/*  864 */     if ((paramInt3 <= 0) || (paramInt4 <= 0)) {
/*  865 */       return;
/*      */     }
/*  867 */     Color localColor = paramGraphics.getColor();
/*  868 */     paramGraphics.setColor(paramBoolean ? getLightShadowColor() : getDarkShadowColor());
/*  869 */     paramGraphics.drawLine(paramInt1, paramInt2, paramInt1, paramInt2 + paramInt4 - 1);
/*  870 */     paramGraphics.drawLine(paramInt1 + 1, paramInt2, paramInt1 + paramInt3 - 1, paramInt2);
/*  871 */     paramGraphics.setColor(paramBoolean ? getDarkShadowColor() : getLightShadowColor());
/*  872 */     paramGraphics.drawLine(paramInt1 + 1, paramInt2 + paramInt4 - 1, paramInt1 + paramInt3 - 1, paramInt2 + paramInt4 - 1);
/*  873 */     paramGraphics.drawLine(paramInt1 + paramInt3 - 1, paramInt2 + 1, paramInt1 + paramInt3 - 1, paramInt2 + paramInt4 - 1);
/*  874 */     paramGraphics.setColor(localColor);
/*      */   }
/*      */ 
/*      */   protected boolean isEventDisabled(XEvent paramXEvent)
/*      */   {
/*  887 */     switch (paramXEvent.get_type()) {
/*      */     case 2:
/*      */     case 3:
/*      */     case 4:
/*      */     case 5:
/*      */     case 6:
/*      */     case 12:
/*      */     case 13:
/*      */     case 17:
/*  896 */       return super.isEventDisabled(paramXEvent);
/*      */     case 7:
/*      */     case 8:
/*      */     case 9:
/*      */     case 10:
/*      */     case 11:
/*      */     case 14:
/*      */     case 15:
/*  898 */     case 16: } return true;
/*      */   }
/*      */ 
/*      */   public void dispose()
/*      */   {
/*  906 */     setDisposed(true);
/*  907 */     EventQueue.invokeLater(new Runnable() {
/*      */       public void run() {
/*  909 */         XBaseMenuWindow.this.doDispose();
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   protected void doDispose()
/*      */   {
/*  919 */     xSetVisible(false);
/*  920 */     SurfaceData localSurfaceData = this.surfaceData;
/*  921 */     this.surfaceData = null;
/*  922 */     if (localSurfaceData != null) {
/*  923 */       localSurfaceData.invalidate();
/*      */     }
/*  925 */     XToolkit.targetDisposedPeer(this.target, this);
/*  926 */     destroy();
/*      */   }
/*      */ 
/*      */   void postEvent(final AWTEvent paramAWTEvent)
/*      */   {
/*  936 */     EventQueue.invokeLater(new Runnable() {
/*      */       public void run() {
/*  938 */         XBaseMenuWindow.this.handleEvent(paramAWTEvent);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   protected void handleEvent(AWTEvent paramAWTEvent)
/*      */   {
/*  949 */     switch (paramAWTEvent.getID()) {
/*      */     case 800:
/*  951 */       doHandleJavaPaintEvent((PaintEvent)paramAWTEvent);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean grabInput()
/*      */   {
/*  964 */     XToolkit.awtLock();
/*      */     boolean bool;
/*      */     int i;
/*      */     int j;
/*      */     try
/*      */     {
/*  966 */       long l = XlibWrapper.RootWindow(XToolkit.getDisplay(), getScreenNumber());
/*      */ 
/*  968 */       bool = XlibWrapper.XQueryPointer(XToolkit.getDisplay(), l, XlibWrapper.larg1, XlibWrapper.larg2, XlibWrapper.larg3, XlibWrapper.larg4, XlibWrapper.larg5, XlibWrapper.larg6, XlibWrapper.larg7);
/*      */ 
/*  976 */       i = Native.getInt(XlibWrapper.larg3);
/*  977 */       j = Native.getInt(XlibWrapper.larg4);
/*  978 */       bool &= super.grabInput();
/*      */     } finally {
/*  980 */       XToolkit.awtUnlock();
/*      */     }
/*  982 */     if (bool)
/*      */     {
/*  984 */       this.grabInputPoint = new Point(i, j);
/*  985 */       this.hasPointerMoved = false;
/*      */     } else {
/*  987 */       this.grabInputPoint = null;
/*  988 */       this.hasPointerMoved = true;
/*      */     }
/*  990 */     return bool;
/*      */   }
/*      */ 
/*      */   void doHandleJavaPaintEvent(PaintEvent paramPaintEvent)
/*      */   {
/* 1002 */     Rectangle localRectangle = paramPaintEvent.getUpdateRect();
/* 1003 */     repaint(localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height);
/*      */   }
/*      */ 
/*      */   void doHandleJavaMouseEvent(MouseEvent paramMouseEvent)
/*      */   {
/* 1019 */     if ((!XToolkit.isLeftMouseButton(paramMouseEvent)) && (!XToolkit.isRightMouseButton(paramMouseEvent))) {
/* 1020 */       return;
/*      */     }
/*      */ 
/* 1023 */     XBaseWindow localXBaseWindow = XAwtState.getGrabWindow();
/*      */ 
/* 1025 */     Point localPoint = paramMouseEvent.getLocationOnScreen();
/* 1026 */     if (!this.hasPointerMoved)
/*      */     {
/* 1028 */       if ((this.grabInputPoint == null) || (Math.abs(localPoint.x - this.grabInputPoint.x) > getMouseMovementSmudge()) || (Math.abs(localPoint.y - this.grabInputPoint.y) > getMouseMovementSmudge()))
/*      */       {
/* 1031 */         this.hasPointerMoved = true;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1036 */     XBaseMenuWindow localXBaseMenuWindow1 = getMenuWindowFromPoint(localPoint);
/*      */ 
/* 1038 */     XMenuItemPeer localXMenuItemPeer = localXBaseMenuWindow1 != null ? localXBaseMenuWindow1.getItemFromPoint(localXBaseMenuWindow1.toLocal(localPoint)) : null;
/*      */ 
/* 1040 */     XBaseMenuWindow localXBaseMenuWindow2 = getShowingLeaf();
/* 1041 */     switch (paramMouseEvent.getID())
/*      */     {
/*      */     case 501:
/* 1045 */       this.showingMousePressedSubmenu = null;
/* 1046 */       if ((localXBaseWindow == this) && (localXBaseMenuWindow1 == null))
/*      */       {
/* 1049 */         ungrabInput();
/*      */       }
/*      */       else {
/* 1052 */         grabInput();
/* 1053 */         if ((localXMenuItemPeer != null) && (!localXMenuItemPeer.isSeparator()) && (localXMenuItemPeer.isTargetItemEnabled()))
/*      */         {
/* 1055 */           if (localXBaseMenuWindow1.getShowingSubmenu() == localXMenuItemPeer)
/*      */           {
/* 1059 */             this.showingMousePressedSubmenu = ((XMenuPeer)localXMenuItemPeer);
/*      */           }
/* 1061 */           localXBaseMenuWindow1.selectItem(localXMenuItemPeer, true);
/*      */         }
/* 1064 */         else if (localXBaseMenuWindow1 != null) {
/* 1065 */           localXBaseMenuWindow1.selectItem(null, false); }  } break;
/*      */     case 502:
/* 1072 */       if ((localXMenuItemPeer != null) && (!localXMenuItemPeer.isSeparator()) && (localXMenuItemPeer.isTargetItemEnabled())) {
/* 1073 */         if ((localXMenuItemPeer instanceof XMenuPeer)) {
/* 1074 */           if (this.showingMousePressedSubmenu == localXMenuItemPeer)
/*      */           {
/* 1077 */             if ((localXBaseMenuWindow1 instanceof XMenuBarPeer))
/* 1078 */               ungrabInput();
/*      */             else
/* 1080 */               localXBaseMenuWindow1.selectItem(localXMenuItemPeer, false);
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/* 1085 */           localXMenuItemPeer.action(paramMouseEvent.getWhen());
/* 1086 */           ungrabInput();
/*      */         }
/*      */ 
/*      */       }
/* 1090 */       else if ((this.hasPointerMoved) || ((localXBaseMenuWindow1 instanceof XMenuBarPeer))) {
/* 1091 */         ungrabInput();
/*      */       }
/*      */ 
/* 1094 */       this.showingMousePressedSubmenu = null;
/* 1095 */       break;
/*      */     case 506:
/* 1097 */       if (localXBaseMenuWindow1 != null)
/*      */       {
/* 1100 */         if ((localXMenuItemPeer != null) && (!localXMenuItemPeer.isSeparator()) && (localXMenuItemPeer.isTargetItemEnabled())) {
/* 1101 */           if (localXBaseWindow == this)
/* 1102 */             localXBaseMenuWindow1.selectItem(localXMenuItemPeer, true);
/*      */         }
/*      */         else {
/* 1105 */           localXBaseMenuWindow1.selectItem(null, false);
/*      */         }
/*      */ 
/*      */       }
/* 1110 */       else if (localXBaseMenuWindow2 != null)
/* 1111 */         localXBaseMenuWindow2.selectItem(null, false);
/*      */       break;
/*      */     }
/*      */   }
/*      */ 
/*      */   void doHandleJavaKeyEvent(KeyEvent paramKeyEvent)
/*      */   {
/* 1125 */     if (log.isLoggable(400)) log.finer(paramKeyEvent.toString());
/* 1126 */     if (paramKeyEvent.getID() != 401) {
/* 1127 */       return;
/*      */     }
/* 1129 */     int i = paramKeyEvent.getKeyCode();
/* 1130 */     XBaseMenuWindow localXBaseMenuWindow1 = getShowingLeaf();
/* 1131 */     XMenuItemPeer localXMenuItemPeer = localXBaseMenuWindow1.getSelectedItem();
/*      */     XBaseMenuWindow localXBaseMenuWindow2;
/* 1132 */     switch (i) {
/*      */     case 38:
/*      */     case 224:
/* 1135 */       if (!(localXBaseMenuWindow1 instanceof XMenuBarPeer))
/*      */       {
/* 1138 */         localXBaseMenuWindow1.selectItem(localXBaseMenuWindow1.getPrevSelectableItem(), false); } break;
/*      */     case 40:
/*      */     case 225:
/* 1143 */       if ((localXBaseMenuWindow1 instanceof XMenuBarPeer))
/*      */       {
/* 1145 */         selectItem(getSelectedItem(), true);
/*      */       }
/*      */       else {
/* 1148 */         localXBaseMenuWindow1.selectItem(localXBaseMenuWindow1.getNextSelectableItem(), false);
/*      */       }
/* 1150 */       break;
/*      */     case 37:
/*      */     case 226:
/* 1153 */       if ((localXBaseMenuWindow1 instanceof XMenuBarPeer))
/*      */       {
/* 1156 */         selectItem(getPrevSelectableItem(), false);
/* 1157 */       } else if ((localXBaseMenuWindow1.getParentMenuWindow() instanceof XMenuBarPeer))
/*      */       {
/* 1161 */         selectItem(getPrevSelectableItem(), true);
/*      */       }
/*      */       else
/*      */       {
/* 1165 */         localXBaseMenuWindow2 = localXBaseMenuWindow1.getParentMenuWindow();
/*      */ 
/* 1167 */         if (localXBaseMenuWindow2 != null) {
/* 1168 */           localXBaseMenuWindow2.selectItem(localXBaseMenuWindow2.getSelectedItem(), false);
/*      */         }
/*      */       }
/* 1171 */       break;
/*      */     case 39:
/*      */     case 227:
/* 1174 */       if ((localXBaseMenuWindow1 instanceof XMenuBarPeer))
/*      */       {
/* 1177 */         selectItem(getNextSelectableItem(), false);
/* 1178 */       } else if ((localXMenuItemPeer instanceof XMenuPeer))
/*      */       {
/* 1181 */         localXBaseMenuWindow1.selectItem(localXMenuItemPeer, true);
/* 1182 */       } else if ((this instanceof XMenuBarPeer))
/*      */       {
/* 1186 */         selectItem(getNextSelectableItem(), true); } break;
/*      */     case 10:
/*      */     case 32:
/* 1193 */       if ((localXMenuItemPeer instanceof XMenuPeer)) {
/* 1194 */         localXBaseMenuWindow1.selectItem(localXMenuItemPeer, true);
/* 1195 */       } else if (localXMenuItemPeer != null) {
/* 1196 */         localXMenuItemPeer.action(paramKeyEvent.getWhen());
/* 1197 */         ungrabInput(); } break;
/*      */     case 27:
/* 1207 */       if (((localXBaseMenuWindow1 instanceof XMenuBarPeer)) || ((localXBaseMenuWindow1.getParentMenuWindow() instanceof XMenuBarPeer))) {
/* 1208 */         ungrabInput();
/* 1209 */       } else if ((localXBaseMenuWindow1 instanceof XPopupMenuPeer)) {
/* 1210 */         ungrabInput();
/*      */       } else {
/* 1212 */         localXBaseMenuWindow2 = localXBaseMenuWindow1.getParentMenuWindow();
/* 1213 */         localXBaseMenuWindow2.selectItem(localXBaseMenuWindow2.getSelectedItem(), false);
/*      */       }
/* 1215 */       break;
/*      */     case 121:
/* 1219 */       ungrabInput();
/* 1220 */       break;
/*      */     }
/*      */   }
/*      */ 
/*      */   static class MappingData
/*      */     implements Cloneable
/*      */   {
/*      */     private XMenuItemPeer[] items;
/*      */ 
/*      */     MappingData(XMenuItemPeer[] paramArrayOfXMenuItemPeer)
/*      */     {
/*  145 */       this.items = paramArrayOfXMenuItemPeer;
/*      */     }
/*      */ 
/*      */     MappingData()
/*      */     {
/*  153 */       this.items = new XMenuItemPeer[0];
/*      */     }
/*      */ 
/*      */     public Object clone() {
/*      */       try {
/*  158 */         return super.clone(); } catch (CloneNotSupportedException localCloneNotSupportedException) {
/*      */       }
/*  160 */       throw new InternalError();
/*      */     }
/*      */ 
/*      */     public XMenuItemPeer[] getItems()
/*      */     {
/*  165 */       return this.items;
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XBaseMenuWindow
 * JD-Core Version:    0.6.2
 */