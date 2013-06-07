/*      */ package sun.awt.X11;
/*      */ 
/*      */ import java.awt.AWTEvent;
/*      */ import java.awt.Color;
/*      */ import java.awt.Component;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.EventQueue;
/*      */ import java.awt.Font;
/*      */ import java.awt.FontMetrics;
/*      */ import java.awt.Graphics;
/*      */ import java.awt.Graphics2D;
/*      */ import java.awt.List;
/*      */ import java.awt.Point;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.Shape;
/*      */ import java.awt.SystemColor;
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.FocusEvent;
/*      */ import java.awt.event.ItemEvent;
/*      */ import java.awt.event.KeyEvent;
/*      */ import java.awt.event.MouseEvent;
/*      */ import java.awt.event.MouseWheelEvent;
/*      */ import java.awt.image.VolatileImage;
/*      */ import java.awt.peer.ListPeer;
/*      */ import java.util.Vector;
/*      */ import sun.awt.X11GraphicsConfig;
/*      */ import sun.util.logging.PlatformLogger;
/*      */ 
/*      */ class XListPeer extends XComponentPeer
/*      */   implements ListPeer, XScrollbarClient
/*      */ {
/*   43 */   private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.X11.XListPeer");
/*      */   public static final int MARGIN = 2;
/*      */   public static final int SPACE = 1;
/*      */   public static final int SCROLLBAR_AREA = 17;
/*      */   public static final int SCROLLBAR_WIDTH = 13;
/*      */   public static final int NONE = -1;
/*      */   public static final int WINDOW = 0;
/*      */   public static final int VERSCROLLBAR = 1;
/*      */   public static final int HORSCROLLBAR = 2;
/*      */   public static final int DEFAULT_VISIBLE_ROWS = 4;
/*      */   public static final int HORIZ_SCROLL_AMT = 10;
/*      */   private static final int PAINT_VSCROLL = 2;
/*      */   private static final int PAINT_HSCROLL = 4;
/*      */   private static final int PAINT_ITEMS = 8;
/*      */   private static final int PAINT_FOCUS = 16;
/*      */   private static final int PAINT_BACKGROUND = 32;
/*      */   private static final int PAINT_HIDEFOCUS = 64;
/*      */   private static final int PAINT_ALL = 62;
/*      */   private static final int COPY_AREA = 128;
/*      */   XVerticalScrollbar vsb;
/*      */   XHorizontalScrollbar hsb;
/*      */   ListPainter painter;
/*      */   Vector items;
/*      */   boolean multipleSelections;
/*   75 */   int active = -1;
/*      */   int[] selected;
/*      */   int fontHeight;
/*      */   int fontAscent;
/*      */   int fontLeading;
/*   87 */   int currentIndex = -1;
/*      */ 
/*   91 */   int eventIndex = -1;
/*   92 */   int eventType = -1;
/*      */   int focusIndex;
/*      */   int maxLength;
/*      */   boolean vsbVis;
/*      */   boolean hsbVis;
/*      */   int listWidth;
/*      */   int listHeight;
/*  106 */   private int firstTimeVisibleIndex = 0;
/*      */   boolean bgColorSet;
/*      */   boolean fgColorSet;
/*  117 */   boolean mouseDraggedOutHorizontally = false;
/*  118 */   boolean mouseDraggedOutVertically = false;
/*      */ 
/*  122 */   boolean isScrollBarOriginated = false;
/*      */ 
/*  126 */   boolean isMousePressed = false;
/*      */ 
/*      */   XListPeer(List paramList)
/*      */   {
/*  132 */     super(paramList);
/*      */   }
/*      */ 
/*      */   public void preInit(XCreateWindowParams paramXCreateWindowParams)
/*      */   {
/*  139 */     super.preInit(paramXCreateWindowParams);
/*      */ 
/*  142 */     this.items = new Vector();
/*  143 */     createVerScrollbar();
/*  144 */     createHorScrollbar();
/*      */ 
/*  146 */     this.painter = new ListPainter();
/*      */ 
/*  149 */     this.bgColorSet = this.target.isBackgroundSet();
/*  150 */     this.fgColorSet = this.target.isForegroundSet();
/*      */   }
/*      */ 
/*      */   public void postInit(XCreateWindowParams paramXCreateWindowParams) {
/*  154 */     super.postInit(paramXCreateWindowParams);
/*  155 */     initFontMetrics();
/*      */ 
/*  159 */     List localList = (List)this.target;
/*  160 */     int i = localList.getItemCount();
/*  161 */     for (int j = 0; j < i; j++) {
/*  162 */       this.items.addElement(localList.getItem(j));
/*      */     }
/*      */ 
/*  166 */     j = localList.getVisibleIndex();
/*  167 */     if (j >= 0)
/*      */     {
/*  170 */       this.vsb.setValues(j, 0, 0, this.items.size());
/*      */     }
/*      */ 
/*  174 */     this.maxLength = maxLength();
/*      */ 
/*  177 */     int[] arrayOfInt = localList.getSelectedIndexes();
/*  178 */     this.selected = new int[arrayOfInt.length];
/*      */ 
/*  180 */     for (int k = 0; k < arrayOfInt.length; k++) {
/*  181 */       this.selected[k] = arrayOfInt[k];
/*      */     }
/*      */ 
/*  187 */     if (arrayOfInt.length > 0) {
/*  188 */       setFocusIndex(arrayOfInt[(arrayOfInt.length - 1)]);
/*      */     }
/*      */     else {
/*  191 */       setFocusIndex(0);
/*      */     }
/*      */ 
/*  194 */     this.multipleSelections = localList.isMultipleMode();
/*      */   }
/*      */ 
/*      */   void createVerScrollbar()
/*      */   {
/*  202 */     this.vsb = new XVerticalScrollbar(this);
/*  203 */     this.vsb.setValues(0, 0, 0, 0, 1, 1);
/*      */   }
/*      */ 
/*      */   void createHorScrollbar()
/*      */   {
/*  211 */     this.hsb = new XHorizontalScrollbar(this);
/*  212 */     this.hsb.setValues(0, 0, 0, 0, 10, 10);
/*      */   }
/*      */ 
/*      */   public void add(String paramString, int paramInt)
/*      */   {
/*  217 */     addItem(paramString, paramInt);
/*      */   }
/*      */ 
/*      */   public void removeAll()
/*      */   {
/*  222 */     clear();
/*  223 */     this.maxLength = 0;
/*      */   }
/*      */ 
/*      */   public void setMultipleMode(boolean paramBoolean)
/*      */   {
/*  228 */     setMultipleSelections(paramBoolean);
/*      */   }
/*      */ 
/*      */   public Dimension getPreferredSize(int paramInt)
/*      */   {
/*  233 */     return preferredSize(paramInt);
/*      */   }
/*      */ 
/*      */   public Dimension getMinimumSize(int paramInt)
/*      */   {
/*  238 */     return minimumSize(paramInt);
/*      */   }
/*      */ 
/*      */   public Dimension minimumSize()
/*      */   {
/*  245 */     return minimumSize(4);
/*      */   }
/*      */ 
/*      */   public Dimension preferredSize(int paramInt)
/*      */   {
/*  252 */     return minimumSize(paramInt);
/*      */   }
/*      */ 
/*      */   public Dimension minimumSize(int paramInt)
/*      */   {
/*  259 */     FontMetrics localFontMetrics = getFontMetrics(getFont());
/*  260 */     initFontMetrics();
/*  261 */     return new Dimension(20 + localFontMetrics.stringWidth("0123456789abcde"), getItemHeight() * paramInt + 4);
/*      */   }
/*      */ 
/*      */   void initFontMetrics()
/*      */   {
/*  269 */     FontMetrics localFontMetrics = getFontMetrics(getFont());
/*  270 */     this.fontHeight = localFontMetrics.getHeight();
/*  271 */     this.fontAscent = localFontMetrics.getAscent();
/*  272 */     this.fontLeading = localFontMetrics.getLeading();
/*      */   }
/*      */ 
/*      */   int maxLength()
/*      */   {
/*  280 */     FontMetrics localFontMetrics = getFontMetrics(getFont());
/*  281 */     int i = 0;
/*  282 */     int j = this.items.size();
/*  283 */     for (int k = 0; k < j; k++) {
/*  284 */       int m = localFontMetrics.stringWidth((String)this.items.elementAt(k));
/*  285 */       i = Math.max(i, m);
/*      */     }
/*  287 */     return i;
/*      */   }
/*      */ 
/*      */   int getItemWidth(int paramInt)
/*      */   {
/*  294 */     FontMetrics localFontMetrics = getFontMetrics(getFont());
/*  295 */     return localFontMetrics.stringWidth((String)this.items.elementAt(paramInt));
/*      */   }
/*      */ 
/*      */   int stringLength(String paramString)
/*      */   {
/*  302 */     FontMetrics localFontMetrics = getFontMetrics(this.target.getFont());
/*  303 */     return localFontMetrics.stringWidth(paramString);
/*      */   }
/*      */ 
/*      */   public void setForeground(Color paramColor) {
/*  307 */     this.fgColorSet = true;
/*  308 */     super.setForeground(paramColor);
/*      */   }
/*      */ 
/*      */   public void setBackground(Color paramColor) {
/*  312 */     this.bgColorSet = true;
/*  313 */     super.setBackground(paramColor);
/*      */   }
/*      */ 
/*      */   private Color getListBackground(Color[] paramArrayOfColor)
/*      */   {
/*  323 */     if (this.bgColorSet) {
/*  324 */       return paramArrayOfColor[0];
/*      */     }
/*      */ 
/*  327 */     return SystemColor.text;
/*      */   }
/*      */ 
/*      */   private Color getListForeground(Color[] paramArrayOfColor)
/*      */   {
/*  335 */     if (this.fgColorSet) {
/*  336 */       return paramArrayOfColor[3];
/*      */     }
/*      */ 
/*  339 */     return SystemColor.textText;
/*      */   }
/*      */ 
/*      */   Rectangle getVScrollBarRec()
/*      */   {
/*  344 */     return new Rectangle(this.width - 13, 0, 14, this.height);
/*      */   }
/*      */ 
/*      */   Rectangle getHScrollBarRec() {
/*  348 */     return new Rectangle(0, this.height - 13, this.width, 13);
/*      */   }
/*      */ 
/*      */   int getFirstVisibleItem() {
/*  352 */     if (this.vsbVis) {
/*  353 */       return this.vsb.getValue();
/*      */     }
/*  355 */     return 0;
/*      */   }
/*      */ 
/*      */   int getLastVisibleItem()
/*      */   {
/*  360 */     if (this.vsbVis) {
/*  361 */       return Math.min(this.items.size() - 1, this.vsb.getValue() + itemsInWindow() - 1);
/*      */     }
/*  363 */     return Math.min(this.items.size() - 1, itemsInWindow() - 1);
/*      */   }
/*      */ 
/*      */   public void repaintScrollbarRequest(XScrollbar paramXScrollbar)
/*      */   {
/*  368 */     Graphics localGraphics = getGraphics();
/*  369 */     if (paramXScrollbar == this.hsb) {
/*  370 */       repaint(4);
/*      */     }
/*  372 */     else if (paramXScrollbar == this.vsb)
/*  373 */       repaint(2);
/*      */   }
/*      */ 
/*      */   public void repaint()
/*      */   {
/*  383 */     repaint(getFirstVisibleItem(), getLastVisibleItem(), 62);
/*      */   }
/*      */ 
/*      */   private void repaint(int paramInt) {
/*  387 */     repaint(getFirstVisibleItem(), getLastVisibleItem(), paramInt);
/*      */   }
/*      */ 
/*      */   private void repaint(int paramInt1, int paramInt2, int paramInt3) {
/*  391 */     repaint(paramInt1, paramInt2, paramInt3, null, null);
/*      */   }
/*      */ 
/*      */   private void repaint(int paramInt1, int paramInt2, int paramInt3, Rectangle paramRectangle, Point paramPoint)
/*      */   {
/*  413 */     Graphics localGraphics = getGraphics();
/*      */     try {
/*  415 */       this.painter.paint(localGraphics, paramInt1, paramInt2, paramInt3, paramRectangle, paramPoint);
/*      */     } finally {
/*  417 */       localGraphics.dispose();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void paint(Graphics paramGraphics) {
/*  422 */     this.painter.paint(paramGraphics, getFirstVisibleItem(), getLastVisibleItem(), 62);
/*      */   }
/*      */   public boolean isFocusable() {
/*  425 */     return true;
/*      */   }
/*      */ 
/*      */   public void focusGained(FocusEvent paramFocusEvent) {
/*  429 */     super.focusGained(paramFocusEvent);
/*  430 */     repaint(16);
/*      */   }
/*      */ 
/*      */   public void focusLost(FocusEvent paramFocusEvent) {
/*  434 */     super.focusLost(paramFocusEvent);
/*  435 */     repaint(16);
/*      */   }
/*      */ 
/*      */   public void layout()
/*      */   {
/*  446 */     assert (this.target != null);
/*      */ 
/*  455 */     int k = this.vsb.getValue();
/*  456 */     int i = itemsInWindow(false);
/*  457 */     int j = this.items.size() < i ? i : this.items.size();
/*  458 */     this.vsb.setValues(this.vsb.getValue(), i, this.vsb.getMinimum(), j);
/*      */     boolean bool;
/*  459 */     this.vsbVis = (bool = vsbIsVisible(false));
/*  460 */     this.listHeight = this.height;
/*      */ 
/*  463 */     this.listWidth = getListWidth();
/*  464 */     i = this.listWidth - 6;
/*  465 */     j = this.maxLength < i ? i : this.maxLength;
/*  466 */     this.hsb.setValues(this.hsb.getValue(), i, this.hsb.getMinimum(), j);
/*  467 */     this.hsbVis = hsbIsVisible(this.vsbVis);
/*      */ 
/*  469 */     if (this.hsbVis)
/*      */     {
/*  472 */       this.listHeight = (this.height - 17);
/*  473 */       i = itemsInWindow(true);
/*  474 */       j = this.items.size() < i ? i : this.items.size();
/*  475 */       this.vsb.setValues(k, i, this.vsb.getMinimum(), j);
/*  476 */       this.vsbVis = vsbIsVisible(true);
/*      */     }
/*      */ 
/*  482 */     if (bool != this.vsbVis) {
/*  483 */       this.listWidth = getListWidth();
/*  484 */       i = this.listWidth - 6;
/*  485 */       j = this.maxLength < i ? 0 : this.maxLength;
/*  486 */       this.hsb.setValues(this.hsb.getValue(), i, this.hsb.getMinimum(), j);
/*  487 */       this.hsbVis = hsbIsVisible(this.vsbVis);
/*      */     }
/*      */ 
/*  490 */     this.vsb.setSize(13, this.listHeight);
/*  491 */     this.hsb.setSize(this.listWidth, 13);
/*      */ 
/*  493 */     this.vsb.setBlockIncrement(itemsInWindow());
/*  494 */     this.hsb.setBlockIncrement(this.width - (6 + (this.vsbVis ? 17 : 0)));
/*      */   }
/*      */ 
/*      */   int getItemWidth() {
/*  498 */     return this.width - (4 + (this.vsbVis ? 17 : 0));
/*      */   }
/*      */ 
/*      */   int getItemHeight()
/*      */   {
/*  503 */     return this.fontHeight - this.fontLeading + 2;
/*      */   }
/*      */ 
/*      */   int getItemX() {
/*  507 */     return 3;
/*      */   }
/*      */ 
/*      */   int getItemY(int paramInt) {
/*  511 */     return index2y(paramInt);
/*      */   }
/*      */ 
/*      */   int getFocusIndex() {
/*  515 */     return this.focusIndex;
/*      */   }
/*      */ 
/*      */   void setFocusIndex(int paramInt) {
/*  519 */     this.focusIndex = paramInt;
/*      */   }
/*      */ 
/*      */   Rectangle getFocusRect()
/*      */   {
/*  529 */     Rectangle localRectangle = new Rectangle();
/*      */ 
/*  531 */     localRectangle.x = 1;
/*  532 */     localRectangle.width = (getListWidth() - 3);
/*      */ 
/*  535 */     if (isIndexDisplayed(getFocusIndex()))
/*      */     {
/*  537 */       localRectangle.y = (index2y(getFocusIndex()) - 2);
/*  538 */       localRectangle.height = (getItemHeight() + 1);
/*      */     }
/*      */     else {
/*  541 */       localRectangle.y = 1;
/*  542 */       localRectangle.height = (this.hsbVis ? this.height - 17 : this.height);
/*  543 */       localRectangle.height -= 3;
/*      */     }
/*  545 */     return localRectangle;
/*      */   }
/*      */ 
/*      */   public void handleConfigureNotifyEvent(XEvent paramXEvent) {
/*  549 */     super.handleConfigureNotifyEvent(paramXEvent);
/*      */ 
/*  552 */     this.painter.invalidate();
/*      */   }
/*  554 */   public boolean handlesWheelScrolling() { return true; }
/*      */ 
/*      */   void handleJavaMouseEvent(MouseEvent paramMouseEvent)
/*      */   {
/*  558 */     super.handleJavaMouseEvent(paramMouseEvent);
/*  559 */     int i = paramMouseEvent.getID();
/*  560 */     switch (i) {
/*      */     case 501:
/*  562 */       mousePressed(paramMouseEvent);
/*  563 */       break;
/*      */     case 502:
/*  565 */       mouseReleased(paramMouseEvent);
/*  566 */       break;
/*      */     case 506:
/*  568 */       mouseDragged(paramMouseEvent);
/*      */     }
/*      */   }
/*      */ 
/*      */   void handleJavaMouseWheelEvent(MouseWheelEvent paramMouseWheelEvent)
/*      */   {
/*  574 */     if (ListHelper.doWheelScroll(this.vsbVis ? this.vsb : null, this.hsbVis ? this.hsb : null, paramMouseWheelEvent))
/*      */     {
/*  576 */       repaint();
/*      */     }
/*      */   }
/*      */ 
/*      */   void mousePressed(MouseEvent paramMouseEvent) {
/*  581 */     if (log.isLoggable(400)) log.finer(paramMouseEvent.toString() + ", hsb " + this.hsbVis + ", vsb " + this.vsbVis);
/*  582 */     if ((isEnabled()) && (paramMouseEvent.getButton() == 1)) {
/*  583 */       if (inWindow(paramMouseEvent.getX(), paramMouseEvent.getY())) {
/*  584 */         if (log.isLoggable(500)) log.fine("Mouse press in items area");
/*  585 */         this.active = 0;
/*  586 */         int i = y2index(paramMouseEvent.getY());
/*  587 */         if (i >= 0) {
/*  588 */           if (this.multipleSelections) {
/*  589 */             if (isSelected(i))
/*      */             {
/*  591 */               deselectItem(i);
/*  592 */               this.eventIndex = i;
/*  593 */               this.eventType = 2;
/*      */             }
/*      */             else {
/*  596 */               selectItem(i);
/*  597 */               this.eventIndex = i;
/*  598 */               this.eventType = 1;
/*      */             }
/*      */ 
/*      */           }
/*      */           else
/*      */           {
/*  607 */             selectItem(i);
/*  608 */             this.eventIndex = i;
/*  609 */             this.eventType = 1;
/*      */           }
/*      */ 
/*  613 */           setFocusIndex(i);
/*  614 */           repaint(16);
/*      */         }
/*      */         else
/*      */         {
/*  618 */           this.currentIndex = -1;
/*      */         }
/*  620 */       } else if (inVerticalScrollbar(paramMouseEvent.getX(), paramMouseEvent.getY())) {
/*  621 */         if (log.isLoggable(500)) log.fine("Mouse press in vertical scrollbar");
/*  622 */         this.active = 1;
/*  623 */         this.vsb.handleMouseEvent(paramMouseEvent.getID(), paramMouseEvent.getModifiers(), paramMouseEvent.getX() - (this.width - 13), paramMouseEvent.getY());
/*      */       }
/*  627 */       else if (inHorizontalScrollbar(paramMouseEvent.getX(), paramMouseEvent.getY())) {
/*  628 */         if (log.isLoggable(500)) log.fine("Mouse press in horizontal scrollbar");
/*  629 */         this.active = 2;
/*  630 */         this.hsb.handleMouseEvent(paramMouseEvent.getID(), paramMouseEvent.getModifiers(), paramMouseEvent.getX(), paramMouseEvent.getY() - (this.height - 13));
/*      */       }
/*      */ 
/*  636 */       this.isMousePressed = true;
/*      */     }
/*      */   }
/*      */ 
/*  640 */   void mouseReleased(MouseEvent paramMouseEvent) { if ((isEnabled()) && (paramMouseEvent.getButton() == 1))
/*      */     {
/*  642 */       int i = paramMouseEvent.getClickCount();
/*  643 */       if (this.active == 1) {
/*  644 */         this.vsb.handleMouseEvent(paramMouseEvent.getID(), paramMouseEvent.getModifiers(), paramMouseEvent.getX() - (this.width - 13), paramMouseEvent.getY());
/*      */       }
/*  648 */       else if (this.active == 2) {
/*  649 */         this.hsb.handleMouseEvent(paramMouseEvent.getID(), paramMouseEvent.getModifiers(), paramMouseEvent.getX(), paramMouseEvent.getY() - (this.height - 13));
/*      */       }
/*  653 */       else if ((this.currentIndex >= 0) && (i >= 2) && (i % 2 == 0))
/*      */       {
/*  655 */         postEvent(new ActionEvent(this.target, 1001, (String)this.items.elementAt(this.currentIndex), paramMouseEvent.getWhen(), paramMouseEvent.getModifiers()));
/*      */       }
/*  660 */       else if (this.active == 0)
/*      */       {
/*  662 */         trackMouseReleasedScroll();
/*      */ 
/*  664 */         if (this.eventType == 2) {
/*  665 */           assert (this.multipleSelections) : "Shouldn't get a deselect for a single-select List";
/*      */ 
/*  667 */           deselectItem(this.eventIndex);
/*      */         }
/*  669 */         if (this.eventType != -1) {
/*  670 */           postEvent(new ItemEvent((List)this.target, 701, Integer.valueOf(this.eventIndex), this.eventType));
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  676 */       this.active = -1;
/*  677 */       this.eventIndex = -1;
/*  678 */       this.eventType = -1;
/*  679 */       this.isMousePressed = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   void mouseDragged(MouseEvent paramMouseEvent)
/*      */   {
/*  685 */     if ((isEnabled()) && ((paramMouseEvent.getModifiersEx() & 0x400) != 0))
/*      */     {
/*  687 */       if (this.active == 1) {
/*  688 */         this.vsb.handleMouseEvent(paramMouseEvent.getID(), paramMouseEvent.getModifiers(), paramMouseEvent.getX() - (this.width - 13), paramMouseEvent.getY());
/*      */       }
/*  692 */       else if (this.active == 2) {
/*  693 */         this.hsb.handleMouseEvent(paramMouseEvent.getID(), paramMouseEvent.getModifiers(), paramMouseEvent.getX(), paramMouseEvent.getY() - (this.height - 13));
/*      */       }
/*  697 */       else if (this.active == 0) {
/*  698 */         int i = y2index(paramMouseEvent.getY());
/*  699 */         if (this.multipleSelections)
/*      */         {
/*  703 */           if ((this.eventType == 2) && 
/*  704 */             (i != this.eventIndex)) {
/*  705 */             this.eventType = -1;
/*  706 */             this.eventIndex = -1;
/*      */           }
/*      */ 
/*      */         }
/*  710 */         else if (this.eventType == 1)
/*      */         {
/*  716 */           trackMouseDraggedScroll(paramMouseEvent);
/*      */ 
/*  718 */           if ((i >= 0) && (!isSelected(i))) {
/*  719 */             int j = this.eventIndex;
/*  720 */             selectItem(i);
/*  721 */             this.eventIndex = i;
/*  722 */             repaint(j, this.eventIndex, 8);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  727 */         if (i >= 0) {
/*  728 */           setFocusIndex(i);
/*  729 */           repaint(16);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   void trackMouseDraggedScroll(MouseEvent paramMouseEvent)
/*      */   {
/*  743 */     if (this.vsb.beforeThumb(paramMouseEvent.getX(), paramMouseEvent.getY()))
/*  744 */       this.vsb.setMode(2);
/*      */     else {
/*  746 */       this.vsb.setMode(1);
/*      */     }
/*      */ 
/*  749 */     if ((paramMouseEvent.getY() < 0) || (paramMouseEvent.getY() >= this.listHeight)) {
/*  750 */       if (!this.mouseDraggedOutVertically) {
/*  751 */         this.mouseDraggedOutVertically = true;
/*  752 */         this.vsb.startScrollingInstance();
/*      */       }
/*      */     }
/*  755 */     else if (this.mouseDraggedOutVertically) {
/*  756 */       this.mouseDraggedOutVertically = false;
/*  757 */       this.vsb.stopScrollingInstance();
/*      */     }
/*      */ 
/*  761 */     if (this.hsb.beforeThumb(paramMouseEvent.getX(), paramMouseEvent.getY()))
/*  762 */       this.hsb.setMode(2);
/*      */     else {
/*  764 */       this.hsb.setMode(1);
/*      */     }
/*      */ 
/*  767 */     if ((paramMouseEvent.getX() < 0) || (paramMouseEvent.getX() >= this.listWidth)) {
/*  768 */       if (!this.mouseDraggedOutHorizontally) {
/*  769 */         this.mouseDraggedOutHorizontally = true;
/*  770 */         this.hsb.startScrollingInstance();
/*      */       }
/*      */     }
/*  773 */     else if (this.mouseDraggedOutHorizontally) {
/*  774 */       this.mouseDraggedOutHorizontally = false;
/*  775 */       this.hsb.stopScrollingInstance();
/*      */     }
/*      */   }
/*      */ 
/*      */   void trackMouseReleasedScroll()
/*      */   {
/*  788 */     if (this.mouseDraggedOutVertically) {
/*  789 */       this.mouseDraggedOutVertically = false;
/*  790 */       this.vsb.stopScrollingInstance();
/*      */     }
/*      */ 
/*  793 */     if (this.mouseDraggedOutHorizontally) {
/*  794 */       this.mouseDraggedOutHorizontally = false;
/*  795 */       this.hsb.stopScrollingInstance();
/*      */     }
/*      */   }
/*      */ 
/*      */   void handleJavaKeyEvent(KeyEvent paramKeyEvent) {
/*  800 */     switch (paramKeyEvent.getID()) {
/*      */     case 401:
/*  802 */       if (!this.isMousePressed)
/*  803 */         keyPressed(paramKeyEvent);
/*      */       break;
/*      */     }
/*      */   }
/*      */ 
/*      */   void keyPressed(KeyEvent paramKeyEvent)
/*      */   {
/*  810 */     int i = paramKeyEvent.getKeyCode();
/*  811 */     if (log.isLoggable(500)) log.fine(paramKeyEvent.toString());
/*      */     int j;
/*      */     int k;
/*  812 */     switch (i) {
/*      */     case 38:
/*      */     case 224:
/*  815 */       if (getFocusIndex() > 0) {
/*  816 */         setFocusIndex(getFocusIndex() - 1);
/*  817 */         repaint(64);
/*      */ 
/*  819 */         if (!this.multipleSelections) {
/*  820 */           selectItem(getFocusIndex());
/*  821 */           postEvent(new ItemEvent((List)this.target, 701, Integer.valueOf(getFocusIndex()), 1));
/*      */         }
/*      */ 
/*  826 */         if (isItemHidden(getFocusIndex())) {
/*  827 */           makeVisible(getFocusIndex());
/*      */         }
/*      */         else
/*  830 */           repaint(16);  } break;
/*      */     case 40:
/*      */     case 225:
/*  836 */       if (getFocusIndex() < this.items.size() - 1) {
/*  837 */         setFocusIndex(getFocusIndex() + 1);
/*  838 */         repaint(64);
/*      */ 
/*  840 */         if (!this.multipleSelections) {
/*  841 */           selectItem(getFocusIndex());
/*  842 */           postEvent(new ItemEvent((List)this.target, 701, Integer.valueOf(getFocusIndex()), 1));
/*      */         }
/*      */ 
/*  847 */         if (isItemHidden(getFocusIndex())) {
/*  848 */           makeVisible(getFocusIndex());
/*      */         }
/*      */         else
/*  851 */           repaint(16);  } break;
/*      */     case 33:
/*  857 */       j = this.vsb.getValue();
/*  858 */       this.vsb.setValue(this.vsb.getValue() - this.vsb.getBlockIncrement());
/*  859 */       k = this.vsb.getValue();
/*      */ 
/*  862 */       if (j != k) {
/*  863 */         setFocusIndex(Math.max(getFocusIndex() - itemsInWindow(), 0));
/*  864 */         if (!this.multipleSelections) {
/*  865 */           selectItem(getFocusIndex());
/*  866 */           postEvent(new ItemEvent((List)this.target, 701, Integer.valueOf(getFocusIndex()), 1));
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  872 */       repaint();
/*  873 */       break;
/*      */     case 34:
/*  877 */       j = this.vsb.getValue();
/*  878 */       this.vsb.setValue(this.vsb.getValue() + this.vsb.getBlockIncrement());
/*  879 */       k = this.vsb.getValue();
/*      */ 
/*  882 */       if (j != k) {
/*  883 */         setFocusIndex(Math.min(getFocusIndex() + itemsInWindow(), this.items.size() - 1));
/*  884 */         if (!this.multipleSelections) {
/*  885 */           selectItem(getFocusIndex());
/*  886 */           postEvent(new ItemEvent((List)this.target, 701, Integer.valueOf(getFocusIndex()), 1));
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  892 */       repaint();
/*  893 */       break;
/*      */     case 37:
/*      */     case 226:
/*  897 */       if ((this.hsbVis & this.hsb.getValue() > 0)) {
/*  898 */         this.hsb.setValue(this.hsb.getValue() - 10);
/*  899 */         repaint(); } break;
/*      */     case 39:
/*      */     case 227:
/*  904 */       if (this.hsbVis) {
/*  905 */         this.hsb.setValue(this.hsb.getValue() + 10);
/*  906 */         repaint(); } break;
/*      */     case 36:
/*  912 */       if ((paramKeyEvent.isControlDown()) && (((List)this.target).getItemCount() > 0))
/*      */       {
/*  914 */         if (this.vsbVis) {
/*  915 */           this.vsb.setValue(this.vsb.getMinimum());
/*      */         }
/*  917 */         setFocusIndex(0);
/*  918 */         if (!this.multipleSelections) {
/*  919 */           selectItem(getFocusIndex());
/*  920 */           postEvent(new ItemEvent((List)this.target, 701, Integer.valueOf(getFocusIndex()), 1));
/*      */         }
/*      */ 
/*  925 */         repaint();
/*  926 */       }break;
/*      */     case 35:
/*  928 */       if ((paramKeyEvent.isControlDown()) && (((List)this.target).getItemCount() > 0))
/*      */       {
/*  930 */         if (this.vsbVis) {
/*  931 */           this.vsb.setValue(this.vsb.getMaximum());
/*      */         }
/*  933 */         setFocusIndex(this.items.size() - 1);
/*  934 */         if (!this.multipleSelections) {
/*  935 */           selectItem(getFocusIndex());
/*  936 */           postEvent(new ItemEvent((List)this.target, 701, Integer.valueOf(getFocusIndex()), 1));
/*      */         }
/*      */ 
/*  941 */         repaint();
/*  942 */       }break;
/*      */     case 32:
/*  946 */       if ((getFocusIndex() >= 0) && (((List)this.target).getItemCount() > 0))
/*      */       {
/*  950 */         boolean bool = isSelected(getFocusIndex());
/*      */ 
/*  953 */         if ((this.multipleSelections) && (bool)) {
/*  954 */           deselectItem(getFocusIndex());
/*  955 */           postEvent(new ItemEvent((List)this.target, 701, Integer.valueOf(getFocusIndex()), 2));
/*      */         }
/*  960 */         else if (!bool)
/*      */         {
/*  966 */           selectItem(getFocusIndex());
/*  967 */           postEvent(new ItemEvent((List)this.target, 701, Integer.valueOf(getFocusIndex()), 1)); }  } break;
/*      */     case 10:
/*  979 */       if (this.selected.length > 0)
/*  980 */         postEvent(new ActionEvent((List)this.target, 1001, (String)this.items.elementAt(getFocusIndex()), paramKeyEvent.getWhen(), paramKeyEvent.getModifiers()));
/*      */       break;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void notifyValue(XScrollbar paramXScrollbar, int paramInt1, int paramInt2, boolean paramBoolean)
/*      */   {
/*  996 */     if (log.isLoggable(500)) log.fine("Notify value changed on " + paramXScrollbar + " to " + paramInt2);
/*  997 */     int i = paramXScrollbar.getValue();
/*  998 */     if (paramXScrollbar == this.vsb) {
/*  999 */       scrollVertical(paramInt2 - i);
/*      */ 
/* 1002 */       int j = this.eventIndex;
/* 1003 */       int k = this.eventIndex + paramInt2 - i;
/* 1004 */       if ((this.mouseDraggedOutVertically) && (!isSelected(k))) {
/* 1005 */         selectItem(k);
/* 1006 */         this.eventIndex = k;
/* 1007 */         repaint(j, this.eventIndex, 8);
/*      */ 
/* 1011 */         setFocusIndex(k);
/* 1012 */         repaint(16);
/*      */       }
/*      */     }
/* 1015 */     else if ((XHorizontalScrollbar)paramXScrollbar == this.hsb) {
/* 1016 */       scrollHorizontal(paramInt2 - i);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void deselectAllItems()
/*      */   {
/* 1025 */     this.selected = new int[0];
/* 1026 */     repaint(8);
/*      */   }
/*      */ 
/*      */   public void setMultipleSelections(boolean paramBoolean)
/*      */   {
/* 1033 */     if (this.multipleSelections != paramBoolean) {
/* 1034 */       if (!paramBoolean) {
/* 1035 */         int i = isSelected(this.focusIndex) ? this.focusIndex : -1;
/* 1036 */         deselectAllItems();
/* 1037 */         if (i != -1) {
/* 1038 */           selectItem(i);
/*      */         }
/*      */       }
/* 1041 */       this.multipleSelections = paramBoolean;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addItem(String paramString, int paramInt)
/*      */   {
/* 1051 */     int i = this.maxLength;
/* 1052 */     boolean bool1 = this.hsbVis;
/* 1053 */     boolean bool2 = this.vsbVis;
/*      */ 
/* 1055 */     int j = 0;
/* 1056 */     if ((paramInt < 0) || (paramInt >= this.items.size())) {
/* 1057 */       paramInt = -1;
/*      */     }
/*      */ 
/* 1064 */     this.currentIndex = -1;
/*      */ 
/* 1066 */     if (paramInt == -1) {
/* 1067 */       this.items.addElement(paramString);
/* 1068 */       paramInt = 0;
/* 1069 */       j = this.items.size() - 1;
/*      */     } else {
/* 1071 */       this.items.insertElementAt(paramString, paramInt);
/* 1072 */       j = paramInt;
/* 1073 */       for (k = 0; k < this.selected.length; k++) {
/* 1074 */         if (this.selected[k] >= paramInt) {
/* 1075 */           this.selected[k] += 1;
/*      */         }
/*      */       }
/*      */     }
/* 1079 */     if (log.isLoggable(400)) log.finer("Adding item '" + paramString + "' to " + j);
/*      */ 
/* 1082 */     int k = !isItemHidden(j) ? 1 : 0;
/* 1083 */     this.maxLength = Math.max(this.maxLength, getItemWidth(j));
/* 1084 */     layout();
/*      */ 
/* 1086 */     int m = 0;
/* 1087 */     if ((this.vsbVis != bool2) || (this.hsbVis != bool1))
/*      */     {
/* 1089 */       m = 62;
/*      */     }
/*      */     else {
/* 1092 */       m = (k != 0 ? 8 : 0) | ((this.maxLength != i) || ((bool1 ^ this.hsbVis)) ? 4 : 0) | (this.vsb.needsRepaint() ? 2 : 0);
/*      */     }
/*      */ 
/* 1097 */     if (log.isLoggable(300)) log.finest("Last visible: " + getLastVisibleItem() + ", hsb changed : " + (bool1 ^ this.hsbVis) + ", items changed " + k);
/*      */ 
/* 1099 */     repaint(j, getLastVisibleItem(), m);
/*      */   }
/*      */ 
/*      */   public void delItems(int paramInt1, int paramInt2)
/*      */   {
/* 1109 */     boolean bool1 = this.hsbVis;
/* 1110 */     boolean bool2 = this.vsbVis;
/* 1111 */     int i = lastItemDisplayed();
/*      */ 
/* 1113 */     if (log.isLoggable(500)) log.fine("Deleting from " + paramInt1 + " to " + paramInt2);
/*      */ 
/* 1115 */     if (log.isLoggable(300)) log.finest("Last displayed item: " + i + ", items in window " + itemsInWindow() + ", size " + this.items.size());
/*      */ 
/* 1118 */     if (this.items.size() == 0) {
/* 1119 */       return;
/*      */     }
/*      */ 
/* 1123 */     if (paramInt1 > paramInt2) {
/* 1124 */       j = paramInt1;
/* 1125 */       paramInt1 = paramInt2;
/* 1126 */       paramInt2 = j;
/*      */     }
/*      */ 
/* 1130 */     if (paramInt1 < 0) {
/* 1131 */       paramInt1 = 0;
/*      */     }
/*      */ 
/* 1135 */     if (paramInt2 >= this.items.size()) {
/* 1136 */       paramInt2 = this.items.size() - 1;
/*      */     }
/*      */ 
/* 1146 */     int j = (paramInt1 >= getFirstVisibleItem()) && (paramInt1 <= getLastVisibleItem()) ? 1 : 0;
/*      */ 
/* 1149 */     for (int k = paramInt1; k <= paramInt2; k++) {
/* 1150 */       this.items.removeElementAt(paramInt1);
/* 1151 */       m = posInSel(k);
/* 1152 */       if (m != -1) {
/* 1153 */         int[] arrayOfInt = new int[this.selected.length - 1];
/* 1154 */         System.arraycopy(this.selected, 0, arrayOfInt, 0, m);
/* 1155 */         System.arraycopy(this.selected, m + 1, arrayOfInt, m, this.selected.length - (m + 1));
/* 1156 */         this.selected = arrayOfInt;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1162 */     k = paramInt2 - paramInt1 + 1;
/* 1163 */     for (int m = 0; m < this.selected.length; m++) {
/* 1164 */       if (this.selected[m] > paramInt2) {
/* 1165 */         this.selected[m] -= k;
/*      */       }
/*      */     }
/*      */ 
/* 1169 */     m = 2;
/*      */ 
/* 1171 */     if (getFocusIndex() > paramInt2) {
/* 1172 */       setFocusIndex(getFocusIndex() - (paramInt2 - paramInt1 + 1));
/* 1173 */       m |= 16;
/* 1174 */     } else if ((getFocusIndex() >= paramInt1) && (getFocusIndex() <= paramInt2))
/*      */     {
/* 1178 */       n = this.items.size() > 0 ? 0 : -1;
/* 1179 */       setFocusIndex(Math.max(paramInt1 - 1, n));
/* 1180 */       m |= 16;
/*      */     }
/*      */ 
/* 1183 */     if (log.isLoggable(300)) log.finest("Multiple selections: " + this.multipleSelections);
/*      */ 
/* 1186 */     if (this.vsb.getValue() >= paramInt1) {
/* 1187 */       if (this.vsb.getValue() <= paramInt2)
/* 1188 */         this.vsb.setValue(paramInt2 + 1 - k);
/*      */       else {
/* 1190 */         this.vsb.setValue(this.vsb.getValue() - k);
/*      */       }
/*      */     }
/*      */ 
/* 1194 */     int n = this.maxLength;
/* 1195 */     this.maxLength = maxLength();
/* 1196 */     if (this.maxLength != n)
/*      */     {
/* 1199 */       m |= 4;
/*      */     }
/* 1201 */     layout();
/* 1202 */     j |= (((bool2 ^ this.vsbVis)) || ((bool1 ^ this.hsbVis)) ? 1 : 0);
/* 1203 */     if (j != 0) {
/* 1204 */       m |= 62;
/*      */     }
/* 1206 */     repaint(paramInt1, i, m);
/*      */   }
/*      */ 
/*      */   public void select(int paramInt)
/*      */   {
/* 1214 */     setFocusIndex(paramInt);
/* 1215 */     repaint(16);
/* 1216 */     selectItem(paramInt);
/*      */   }
/*      */ 
/*      */   void selectItem(int paramInt)
/*      */   {
/* 1230 */     this.currentIndex = paramInt;
/*      */ 
/* 1232 */     if (isSelected(paramInt)) {
/* 1233 */       return;
/*      */     }
/* 1235 */     if (!this.multipleSelections) {
/* 1236 */       if (this.selected.length == 0) {
/* 1237 */         this.selected = new int[1];
/* 1238 */         this.selected[0] = paramInt;
/*      */       }
/*      */       else {
/* 1241 */         int i = this.selected[0];
/* 1242 */         this.selected[0] = paramInt;
/* 1243 */         if (!isItemHidden(i))
/*      */         {
/* 1245 */           repaint(i, i, 8);
/*      */         }
/*      */       }
/*      */     }
/*      */     else {
/* 1250 */       int[] arrayOfInt = new int[this.selected.length + 1];
/* 1251 */       int j = 0;
/* 1252 */       while ((j < this.selected.length) && (paramInt > this.selected[j])) {
/* 1253 */         arrayOfInt[j] = this.selected[j];
/* 1254 */         j++;
/*      */       }
/* 1256 */       arrayOfInt[j] = paramInt;
/* 1257 */       System.arraycopy(this.selected, j, arrayOfInt, j + 1, this.selected.length - j);
/* 1258 */       this.selected = arrayOfInt;
/*      */     }
/* 1260 */     if (!isItemHidden(paramInt))
/*      */     {
/* 1262 */       repaint(paramInt, paramInt, 8);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void deselect(int paramInt)
/*      */   {
/* 1271 */     deselectItem(paramInt);
/*      */   }
/*      */ 
/*      */   void deselectItem(int paramInt)
/*      */   {
/* 1279 */     if (!isSelected(paramInt)) {
/* 1280 */       return;
/*      */     }
/* 1282 */     if (!this.multipleSelections)
/*      */     {
/* 1285 */       this.selected = new int[0];
/*      */     } else {
/* 1287 */       int i = posInSel(paramInt);
/* 1288 */       int[] arrayOfInt = new int[this.selected.length - 1];
/* 1289 */       System.arraycopy(this.selected, 0, arrayOfInt, 0, i);
/* 1290 */       System.arraycopy(this.selected, i + 1, arrayOfInt, i, this.selected.length - (i + 1));
/* 1291 */       this.selected = arrayOfInt;
/*      */     }
/* 1293 */     this.currentIndex = paramInt;
/* 1294 */     if (!isItemHidden(paramInt))
/*      */     {
/* 1296 */       repaint(paramInt, paramInt, 8);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void makeVisible(int paramInt)
/*      */   {
/* 1306 */     if ((paramInt < 0) || (paramInt >= this.items.size())) {
/* 1307 */       return;
/*      */     }
/* 1309 */     if (isItemHidden(paramInt))
/*      */     {
/* 1311 */       if (paramInt < this.vsb.getValue()) {
/* 1312 */         scrollVertical(paramInt - this.vsb.getValue());
/*      */       }
/* 1315 */       else if (paramInt > lastItemDisplayed()) {
/* 1316 */         int i = paramInt - lastItemDisplayed();
/* 1317 */         scrollVertical(i);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void clear()
/*      */   {
/* 1326 */     this.selected = new int[0];
/* 1327 */     this.items = new Vector();
/* 1328 */     this.currentIndex = -1;
/*      */ 
/* 1331 */     setFocusIndex(-1);
/* 1332 */     this.vsb.setValue(0);
/* 1333 */     this.maxLength = 0;
/* 1334 */     layout();
/* 1335 */     repaint();
/*      */   }
/*      */ 
/*      */   public int[] getSelectedIndexes()
/*      */   {
/* 1342 */     return this.selected;
/*      */   }
/*      */ 
/*      */   int index2y(int paramInt)
/*      */   {
/* 1352 */     int i = getItemHeight();
/*      */ 
/* 1355 */     return 2 + (paramInt - this.vsb.getValue()) * i + 1;
/*      */   }
/*      */ 
/*      */   boolean validY(int paramInt)
/*      */   {
/* 1363 */     int i = itemsDisplayed();
/* 1364 */     int j = i * getItemHeight() + 2;
/*      */ 
/* 1366 */     if (i == itemsInWindow()) {
/* 1367 */       j += 2;
/*      */     }
/*      */ 
/* 1370 */     if ((paramInt < 0) || (paramInt >= j)) {
/* 1371 */       return false;
/*      */     }
/*      */ 
/* 1374 */     return true;
/*      */   }
/*      */ 
/*      */   int posInSel(int paramInt)
/*      */   {
/* 1382 */     for (int i = 0; i < this.selected.length; i++) {
/* 1383 */       if (paramInt == this.selected[i]) {
/* 1384 */         return i;
/*      */       }
/*      */     }
/* 1387 */     return -1;
/*      */   }
/*      */ 
/*      */   boolean isIndexDisplayed(int paramInt) {
/* 1391 */     int i = lastItemDisplayed();
/*      */ 
/* 1393 */     return (paramInt <= i) && (paramInt >= Math.max(0, i - itemsInWindow() + 1));
/*      */   }
/*      */ 
/*      */   int lastItemDisplayed()
/*      */   {
/* 1401 */     int i = itemsInWindow();
/* 1402 */     return Math.min(this.items.size() - 1, this.vsb.getValue() + i - 1);
/*      */   }
/*      */ 
/*      */   boolean isItemHidden(int paramInt)
/*      */   {
/* 1410 */     return (paramInt < this.vsb.getValue()) || (paramInt >= this.vsb.getValue() + itemsInWindow());
/*      */   }
/*      */ 
/*      */   int getListWidth()
/*      */   {
/* 1419 */     return this.vsbVis ? this.width - 17 : this.width;
/*      */   }
/*      */ 
/*      */   int itemsDisplayed()
/*      */   {
/* 1427 */     return Math.min(this.items.size() - this.vsb.getValue(), itemsInWindow());
/*      */   }
/*      */ 
/*      */   void scrollVertical(int paramInt)
/*      */   {
/* 1436 */     if (log.isLoggable(500)) log.fine("Scrolling vertically by " + paramInt);
/* 1437 */     int i = itemsInWindow();
/* 1438 */     int j = getItemHeight();
/* 1439 */     int k = paramInt * j;
/*      */ 
/* 1441 */     if (this.vsb.getValue() < -paramInt) {
/* 1442 */       paramInt = -this.vsb.getValue();
/*      */     }
/* 1444 */     this.vsb.setValue(this.vsb.getValue() + paramInt);
/*      */ 
/* 1446 */     Rectangle localRectangle = null;
/* 1447 */     Point localPoint = null;
/* 1448 */     int m = 0; int n = 0;
/* 1449 */     int i1 = 90;
/* 1450 */     if (paramInt > 0) {
/* 1451 */       if (paramInt < i) {
/* 1452 */         localRectangle = new Rectangle(2, 2 + k, this.width - 17, j * (i - paramInt - 1) - 1);
/* 1453 */         localPoint = new Point(0, -k);
/* 1454 */         i1 |= 128;
/*      */       }
/* 1456 */       m = this.vsb.getValue() + i - paramInt - 1;
/* 1457 */       n = this.vsb.getValue() + i - 1;
/*      */     }
/* 1459 */     else if (paramInt < 0) {
/* 1460 */       if (paramInt + itemsInWindow() > 0) {
/* 1461 */         localRectangle = new Rectangle(2, 2, this.width - 17, j * (i + paramInt));
/* 1462 */         localPoint = new Point(0, -k);
/* 1463 */         i1 |= 128;
/*      */       }
/* 1465 */       m = this.vsb.getValue();
/* 1466 */       n = Math.min(getLastVisibleItem(), this.vsb.getValue() + -paramInt);
/*      */     }
/* 1468 */     repaint(m, n, i1, localRectangle, localPoint);
/*      */   }
/*      */ 
/*      */   void scrollHorizontal(int paramInt)
/*      */   {
/* 1476 */     if (log.isLoggable(500)) log.fine("Scrolling horizontally by " + this.y);
/* 1477 */     int i = getListWidth();
/* 1478 */     i -= 6;
/* 1479 */     int j = this.height - 21;
/* 1480 */     this.hsb.setValue(this.hsb.getValue() + paramInt);
/*      */ 
/* 1482 */     int k = 12;
/*      */ 
/* 1484 */     Rectangle localRectangle = null;
/* 1485 */     Point localPoint = null;
/* 1486 */     if (paramInt < 0) {
/* 1487 */       localRectangle = new Rectangle(3, 2, i + paramInt, j);
/* 1488 */       localPoint = new Point(-paramInt, 0);
/* 1489 */       k |= 128;
/* 1490 */     } else if (paramInt > 0) {
/* 1491 */       localRectangle = new Rectangle(3 + paramInt, 2, i - paramInt, j);
/* 1492 */       localPoint = new Point(-paramInt, 0);
/* 1493 */       k |= 128;
/*      */     }
/* 1495 */     repaint(this.vsb.getValue(), lastItemDisplayed(), k, localRectangle, localPoint);
/*      */   }
/*      */ 
/*      */   int y2index(int paramInt)
/*      */   {
/* 1502 */     if (!validY(paramInt)) {
/* 1503 */       return -1;
/*      */     }
/*      */ 
/* 1506 */     int i = (paramInt - 2) / getItemHeight() + this.vsb.getValue();
/* 1507 */     int j = lastItemDisplayed();
/*      */ 
/* 1509 */     if (i > j) {
/* 1510 */       i = j;
/*      */     }
/*      */ 
/* 1513 */     return i;
/*      */   }
/*      */ 
/*      */   boolean isSelected(int paramInt)
/*      */   {
/* 1521 */     if ((this.eventType == 1) && (paramInt == this.eventIndex)) {
/* 1522 */       return true;
/*      */     }
/* 1524 */     for (int i = 0; i < this.selected.length; i++) {
/* 1525 */       if (this.selected[i] == paramInt) {
/* 1526 */         return true;
/*      */       }
/*      */     }
/* 1529 */     return false;
/*      */   }
/*      */ 
/*      */   int itemsInWindow(boolean paramBoolean)
/*      */   {
/*      */     int i;
/* 1538 */     if (paramBoolean)
/* 1539 */       i = this.height - 21;
/*      */     else {
/* 1541 */       i = this.height - 4;
/*      */     }
/* 1543 */     return i / getItemHeight();
/*      */   }
/*      */ 
/*      */   int itemsInWindow() {
/* 1547 */     return itemsInWindow(this.hsbVis);
/*      */   }
/*      */ 
/*      */   boolean inHorizontalScrollbar(int paramInt1, int paramInt2)
/*      */   {
/* 1554 */     int i = getListWidth();
/* 1555 */     int j = this.height - 13;
/* 1556 */     return (this.hsbVis) && (paramInt1 >= 0) && (paramInt1 <= i) && (paramInt2 > j);
/*      */   }
/*      */ 
/*      */   boolean inVerticalScrollbar(int paramInt1, int paramInt2)
/*      */   {
/* 1563 */     int i = this.width - 13;
/* 1564 */     int j = this.hsbVis ? this.height - 17 : this.height;
/* 1565 */     return (this.vsbVis) && (paramInt1 > i) && (paramInt2 >= 0) && (paramInt2 <= j);
/*      */   }
/*      */ 
/*      */   boolean inWindow(int paramInt1, int paramInt2)
/*      */   {
/* 1572 */     int i = getListWidth();
/* 1573 */     int j = this.hsbVis ? this.height - 17 : this.height;
/* 1574 */     return (paramInt1 >= 0) && (paramInt1 <= i) && (paramInt2 >= 0) && (paramInt2 <= j);
/*      */   }
/*      */ 
/*      */   boolean vsbIsVisible(boolean paramBoolean)
/*      */   {
/* 1582 */     return this.items.size() > itemsInWindow(paramBoolean);
/*      */   }
/*      */ 
/*      */   boolean hsbIsVisible(boolean paramBoolean)
/*      */   {
/* 1590 */     int i = this.width - (6 + (paramBoolean ? 17 : 0));
/* 1591 */     return this.maxLength > i;
/*      */   }
/*      */ 
/*      */   boolean prePostEvent(AWTEvent paramAWTEvent)
/*      */   {
/* 1599 */     if ((paramAWTEvent instanceof MouseEvent)) {
/* 1600 */       return prePostMouseEvent((MouseEvent)paramAWTEvent);
/*      */     }
/* 1602 */     return super.prePostEvent(paramAWTEvent);
/*      */   }
/*      */ 
/*      */   boolean prePostMouseEvent(MouseEvent paramMouseEvent)
/*      */   {
/* 1613 */     if (getToplevelXWindow().isModalBlocked()) {
/* 1614 */       return false;
/*      */     }
/*      */ 
/* 1617 */     int i = paramMouseEvent.getID();
/*      */ 
/* 1619 */     if (i != 503)
/*      */     {
/* 1622 */       if (((i == 506) || (i == 502)) && (this.isScrollBarOriginated))
/*      */       {
/* 1626 */         if (i == 502) {
/* 1627 */           this.isScrollBarOriginated = false;
/*      */         }
/* 1629 */         handleJavaMouseEventOnEDT(paramMouseEvent);
/* 1630 */         return true;
/* 1631 */       }if (((i == 501) || (i == 500)) && ((inVerticalScrollbar(paramMouseEvent.getX(), paramMouseEvent.getY())) || (inHorizontalScrollbar(paramMouseEvent.getX(), paramMouseEvent.getY()))))
/*      */       {
/* 1636 */         if (i == 501) {
/* 1637 */           this.isScrollBarOriginated = true;
/*      */         }
/* 1639 */         handleJavaMouseEventOnEDT(paramMouseEvent);
/* 1640 */         return true;
/*      */       }
/*      */     }
/* 1642 */     return false;
/*      */   }
/*      */ 
/*      */   void handleJavaMouseEventOnEDT(final MouseEvent paramMouseEvent)
/*      */   {
/* 1649 */     EventQueue.invokeLater(new Runnable() {
/*      */       public void run() {
/* 1651 */         XListPeer.this.handleJavaMouseEvent(paramMouseEvent);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void setFont(Font paramFont)
/*      */   {
/* 1662 */     super.setFont(paramFont);
/* 1663 */     initFontMetrics();
/* 1664 */     layout();
/* 1665 */     repaint();
/*      */   }
/*      */ 
/*      */   class ListPainter
/*      */   {
/*      */     VolatileImage buffer;
/*      */     Color[] colors;
/*      */     private Rectangle prevFocusRect;
/*      */ 
/*      */     ListPainter() {
/*      */     }
/*      */ 
/*      */     private Color getListForeground() {
/* 1680 */       if (XListPeer.this.fgColorSet) {
/* 1681 */         return this.colors[3];
/*      */       }
/*      */ 
/* 1684 */       return SystemColor.textText;
/*      */     }
/*      */ 
/*      */     private Color getListBackground() {
/* 1688 */       if (XListPeer.this.bgColorSet) {
/* 1689 */         return this.colors[0];
/*      */       }
/*      */ 
/* 1692 */       return SystemColor.text;
/*      */     }
/*      */ 
/*      */     private Color getDisabledColor()
/*      */     {
/* 1697 */       Color localColor1 = getListBackground();
/* 1698 */       Color localColor2 = getListForeground();
/* 1699 */       return localColor1.equals(Color.BLACK) ? localColor2.darker() : localColor1.darker();
/*      */     }
/*      */ 
/*      */     private boolean createBuffer() {
/* 1703 */       VolatileImage localVolatileImage = null;
/* 1704 */       XToolkit.awtLock();
/*      */       try {
/* 1706 */         localVolatileImage = this.buffer;
/*      */       } finally {
/* 1708 */         XToolkit.awtUnlock();
/*      */       }
/*      */ 
/* 1711 */       if (localVolatileImage == null) {
/* 1712 */         if (XListPeer.log.isLoggable(500)) XListPeer.log.fine("Creating buffer " + XListPeer.this.width + "x" + XListPeer.this.height);
/*      */ 
/* 1715 */         localVolatileImage = XListPeer.this.graphicsConfig.createCompatibleVolatileImage(XListPeer.this.width + 1, XListPeer.this.height + 1);
/*      */       }
/*      */ 
/* 1719 */       XToolkit.awtLock();
/*      */       try {
/* 1721 */         if (this.buffer == null) {
/* 1722 */           this.buffer = localVolatileImage;
/* 1723 */           return true;
/*      */         }
/*      */       } finally {
/* 1726 */         XToolkit.awtUnlock();
/*      */       }
/* 1728 */       return false;
/*      */     }
/*      */ 
/*      */     public void invalidate() {
/* 1732 */       XToolkit.awtLock();
/*      */       try {
/* 1734 */         if (this.buffer != null) {
/* 1735 */           this.buffer.flush();
/*      */         }
/* 1737 */         this.buffer = null;
/*      */       } finally {
/* 1739 */         XToolkit.awtUnlock();
/*      */       }
/*      */     }
/*      */ 
/*      */     private void paint(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3) {
/* 1744 */       paint(paramGraphics, paramInt1, paramInt2, paramInt3, null, null);
/*      */     }
/*      */ 
/*      */     private void paint(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, Rectangle paramRectangle, Point paramPoint)
/*      */     {
/* 1749 */       if (XListPeer.log.isLoggable(400)) XListPeer.log.finer("Repaint from " + paramInt1 + " to " + paramInt2 + " options " + paramInt3);
/* 1750 */       if (paramInt1 > paramInt2) {
/* 1751 */         int i = paramInt2;
/* 1752 */         paramInt2 = paramInt1;
/* 1753 */         paramInt1 = i;
/*      */       }
/* 1755 */       if (paramInt1 < 0) {
/* 1756 */         paramInt1 = 0;
/*      */       }
/* 1758 */       this.colors = XListPeer.this.getGUIcolors();
/* 1759 */       VolatileImage localVolatileImage = null;
/*      */       do {
/* 1761 */         XToolkit.awtLock();
/*      */         try {
/* 1763 */           if (createBuffer())
/*      */           {
/* 1765 */             paramInt3 = 62;
/*      */           }
/* 1767 */           localVolatileImage = this.buffer;
/*      */         } finally {
/* 1769 */           XToolkit.awtUnlock();
/*      */         }
/* 1771 */         switch (localVolatileImage.validate(XListPeer.this.getGraphicsConfiguration())) {
/*      */         case 2:
/* 1773 */           invalidate();
/* 1774 */           paramInt3 = 62;
/* 1775 */           break;
/*      */         case 1:
/* 1777 */           paramInt3 = 62;
/*      */         default:
/* 1779 */           Graphics2D localGraphics2D = localVolatileImage.createGraphics();
/*      */           try
/*      */           {
/* 1784 */             localGraphics2D.setFont(XListPeer.this.getFont());
/*      */ 
/* 1788 */             if ((paramInt3 & 0x40) != 0) {
/* 1789 */               paintFocus(localGraphics2D, 64);
/*      */             }
/*      */ 
/* 1798 */             if ((paramInt3 & 0x80) != 0) {
/* 1799 */               localGraphics2D.copyArea(paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height, paramPoint.x, paramPoint.y);
/*      */             }
/*      */ 
/* 1802 */             if ((paramInt3 & 0x20) != 0) {
/* 1803 */               paintBackground(localGraphics2D);
/*      */ 
/* 1805 */               paramInt1 = XListPeer.this.getFirstVisibleItem();
/* 1806 */               paramInt2 = XListPeer.this.getLastVisibleItem();
/*      */             }
/* 1808 */             if ((paramInt3 & 0x8) != 0) {
/* 1809 */               paintItems(localGraphics2D, paramInt1, paramInt2, paramInt3);
/*      */             }
/* 1811 */             if (((paramInt3 & 0x2) != 0) && (XListPeer.this.vsbVis)) {
/* 1812 */               localGraphics2D.setClip(XListPeer.this.getVScrollBarRec());
/* 1813 */               paintVerScrollbar(localGraphics2D, true);
/*      */             }
/* 1815 */             if (((paramInt3 & 0x4) != 0) && (XListPeer.this.hsbVis)) {
/* 1816 */               localGraphics2D.setClip(XListPeer.this.getHScrollBarRec());
/* 1817 */               paintHorScrollbar(localGraphics2D, true);
/*      */             }
/* 1819 */             if ((paramInt3 & 0x10) != 0)
/* 1820 */               paintFocus(localGraphics2D, 16);
/*      */           }
/*      */           finally {
/* 1823 */             localGraphics2D.dispose();
/*      */           }
/*      */         }
/*      */       }
/* 1825 */       while (localVolatileImage.contentsLost());
/* 1826 */       paramGraphics.drawImage(localVolatileImage, 0, 0, null);
/*      */     }
/*      */ 
/*      */     private void paintBackground(Graphics paramGraphics) {
/* 1830 */       paramGraphics.setColor(SystemColor.window);
/* 1831 */       paramGraphics.fillRect(0, 0, XListPeer.this.width, XListPeer.this.height);
/* 1832 */       paramGraphics.setColor(getListBackground());
/* 1833 */       paramGraphics.fillRect(0, 0, XListPeer.this.listWidth, XListPeer.this.listHeight);
/* 1834 */       XListPeer.this.draw3DRect(paramGraphics, XComponentPeer.getSystemColors(), 0, 0, XListPeer.this.listWidth - 1, XListPeer.this.listHeight - 1, false);
/*      */     }
/*      */ 
/*      */     private void paintItems(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3) {
/* 1838 */       if (XListPeer.log.isLoggable(400)) XListPeer.log.finer("Painting items from " + paramInt1 + " to " + paramInt2 + ", focused " + XListPeer.this.focusIndex + ", first " + XListPeer.this.getFirstVisibleItem() + ", last " + XListPeer.this.getLastVisibleItem());
/*      */ 
/* 1840 */       paramInt1 = Math.max(XListPeer.this.getFirstVisibleItem(), paramInt1);
/* 1841 */       if (paramInt1 > paramInt2) {
/* 1842 */         i = paramInt2;
/* 1843 */         paramInt2 = paramInt1;
/* 1844 */         paramInt1 = i;
/*      */       }
/* 1846 */       paramInt1 = Math.max(XListPeer.this.getFirstVisibleItem(), paramInt1);
/* 1847 */       paramInt2 = Math.min(paramInt2, XListPeer.this.items.size() - 1);
/*      */ 
/* 1849 */       if (XListPeer.log.isLoggable(400)) XListPeer.log.finer("Actually painting items from " + paramInt1 + " to " + paramInt2 + ", items in window " + XListPeer.this.itemsInWindow());
/*      */ 
/* 1851 */       for (int i = paramInt1; i <= paramInt2; i++)
/* 1852 */         paintItem(paramGraphics, i);
/*      */     }
/*      */ 
/*      */     private void paintItem(Graphics paramGraphics, int paramInt)
/*      */     {
/* 1857 */       if (XListPeer.log.isLoggable(300)) XListPeer.log.finest("Painting item " + paramInt);
/*      */ 
/* 1859 */       if (!XListPeer.this.isItemHidden(paramInt)) {
/* 1860 */         Shape localShape = paramGraphics.getClip();
/* 1861 */         int i = XListPeer.this.getItemWidth();
/* 1862 */         int j = XListPeer.this.getItemHeight();
/* 1863 */         int k = XListPeer.this.getItemY(paramInt);
/* 1864 */         int m = XListPeer.this.getItemX();
/* 1865 */         if (XListPeer.log.isLoggable(300)) XListPeer.log.finest("Setting clip " + new Rectangle(m, k, i - 2, j - 2));
/* 1866 */         paramGraphics.setClip(m, k, i - 2, j - 2);
/*      */ 
/* 1870 */         if (XListPeer.this.isSelected(paramInt)) {
/* 1871 */           if (XListPeer.log.isLoggable(300)) XListPeer.log.finest("Painted item is selected");
/* 1872 */           paramGraphics.setColor(getListForeground());
/*      */         } else {
/* 1874 */           paramGraphics.setColor(getListBackground());
/*      */         }
/* 1876 */         if (XListPeer.log.isLoggable(300)) XListPeer.log.finest("Filling " + new Rectangle(m, k, i, j));
/* 1877 */         paramGraphics.fillRect(m, k, i, j);
/*      */ 
/* 1879 */         if ((paramInt <= XListPeer.this.getLastVisibleItem()) && (paramInt < XListPeer.this.items.size())) {
/* 1880 */           if (!XListPeer.this.isEnabled())
/* 1881 */             paramGraphics.setColor(getDisabledColor());
/* 1882 */           else if (XListPeer.this.isSelected(paramInt))
/* 1883 */             paramGraphics.setColor(getListBackground());
/*      */           else {
/* 1885 */             paramGraphics.setColor(getListForeground());
/*      */           }
/* 1887 */           String str = (String)XListPeer.this.items.elementAt(paramInt);
/* 1888 */           paramGraphics.drawString(str, m - XListPeer.this.hsb.getValue(), k + XListPeer.this.fontAscent);
/*      */         }
/*      */         else {
/* 1891 */           paramGraphics.setClip(m, k, XListPeer.this.listWidth, j);
/* 1892 */           paramGraphics.setColor(getListBackground());
/* 1893 */           paramGraphics.fillRect(m, k, XListPeer.this.listWidth, j);
/*      */         }
/* 1895 */         paramGraphics.setClip(localShape);
/*      */       }
/*      */     }
/*      */ 
/*      */     void paintScrollBar(XScrollbar paramXScrollbar, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean) {
/* 1900 */       if (XListPeer.log.isLoggable(300)) XListPeer.log.finest("Painting scrollbar " + paramXScrollbar + " width " + paramInt3 + " height " + paramInt4 + ", paintAll " + paramBoolean);
/*      */ 
/* 1902 */       paramGraphics.translate(paramInt1, paramInt2);
/* 1903 */       paramXScrollbar.paint(paramGraphics, XComponentPeer.getSystemColors(), paramBoolean);
/* 1904 */       paramGraphics.translate(-paramInt1, -paramInt2);
/*      */     }
/*      */ 
/*      */     void paintHorScrollbar(Graphics paramGraphics, boolean paramBoolean)
/*      */     {
/* 1915 */       int i = XListPeer.this.getListWidth();
/* 1916 */       paintScrollBar(XListPeer.this.hsb, paramGraphics, 0, XListPeer.this.height - 13, i, 13, paramBoolean);
/*      */     }
/*      */ 
/*      */     void paintVerScrollbar(Graphics paramGraphics, boolean paramBoolean)
/*      */     {
/* 1927 */       int i = XListPeer.this.height - (XListPeer.this.hsbVis ? 15 : 0);
/* 1928 */       paintScrollBar(XListPeer.this.vsb, paramGraphics, XListPeer.this.width - 13, 0, 11, i, paramBoolean);
/*      */     }
/*      */ 
/*      */     private void paintFocus(Graphics paramGraphics, int paramInt)
/*      */     {
/* 1934 */       boolean bool = (paramInt & 0x10) != 0;
/* 1935 */       if ((bool) && (!XListPeer.this.hasFocus())) {
/* 1936 */         bool = false;
/*      */       }
/* 1938 */       if (XListPeer.log.isLoggable(500)) XListPeer.log.fine("Painting focus, focus index " + XListPeer.this.getFocusIndex() + ", focus is " + (XListPeer.this.isItemHidden(XListPeer.this.getFocusIndex()) ? "invisible" : "visible") + ", paint focus is " + bool);
/*      */ 
/* 1940 */       Shape localShape = paramGraphics.getClip();
/* 1941 */       paramGraphics.setClip(0, 0, XListPeer.this.listWidth, XListPeer.this.listHeight);
/* 1942 */       if (XListPeer.log.isLoggable(300)) XListPeer.log.finest("Setting focus clip " + new Rectangle(0, 0, XListPeer.this.listWidth, XListPeer.this.listHeight));
/* 1943 */       Rectangle localRectangle = XListPeer.this.getFocusRect();
/* 1944 */       if (this.prevFocusRect != null)
/*      */       {
/* 1946 */         if (XListPeer.log.isLoggable(300)) XListPeer.log.finest("Erasing previous focus rect " + this.prevFocusRect);
/* 1947 */         paramGraphics.setColor(getListBackground());
/* 1948 */         paramGraphics.drawRect(this.prevFocusRect.x, this.prevFocusRect.y, this.prevFocusRect.width, this.prevFocusRect.height);
/* 1949 */         this.prevFocusRect = null;
/*      */       }
/* 1951 */       if (bool)
/*      */       {
/* 1953 */         if (XListPeer.log.isLoggable(300)) XListPeer.log.finest("Painting focus rect " + localRectangle);
/* 1954 */         paramGraphics.setColor(getListForeground());
/* 1955 */         paramGraphics.drawRect(localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height);
/* 1956 */         this.prevFocusRect = localRectangle;
/*      */       }
/* 1958 */       paramGraphics.setClip(localShape);
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XListPeer
 * JD-Core Version:    0.6.2
 */