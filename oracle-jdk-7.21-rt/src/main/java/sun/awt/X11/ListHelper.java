/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Font;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.event.MouseWheelEvent;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import sun.util.logging.PlatformLogger;
/*     */ 
/*     */ public class ListHelper
/*     */   implements XScrollbarClient
/*     */ {
/*  46 */   private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.X11.ListHelper");
/*     */ 
/*  48 */   private final int FOCUS_INSET = 1;
/*     */   private final int BORDER_WIDTH;
/*     */   private final int ITEM_MARGIN;
/*     */   private final int TEXT_SPACE;
/*     */   private final int SCROLLBAR_WIDTH;
/*     */   private List items;
/*     */   private List selected;
/*     */   private boolean multiSelect;
/*     */   private int focusedIndex;
/*     */   private int maxVisItems;
/*     */   private XVerticalScrollbar vsb;
/*     */   private boolean vsbVis;
/*     */   private XHorizontalScrollbar hsb;
/*     */   private boolean hsbVis;
/*     */   private Font font;
/*     */   private FontMetrics fm;
/*     */   private XWindow peer;
/*     */   private Color[] colors;
/*  84 */   boolean mouseDraggedOutVertically = false;
/*  85 */   private volatile boolean vsbVisibilityChanged = false;
/*     */ 
/*     */   public ListHelper(XWindow paramXWindow, Color[] paramArrayOfColor, int paramInt1, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, Font paramFont, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
/*     */   {
/* 102 */     this.peer = paramXWindow;
/* 103 */     this.colors = paramArrayOfColor;
/* 104 */     this.multiSelect = paramBoolean1;
/* 105 */     this.items = new ArrayList(paramInt1);
/* 106 */     this.selected = new ArrayList(1);
/* 107 */     this.selected.add(Integer.valueOf(-1));
/*     */ 
/* 109 */     this.maxVisItems = paramInt2;
/* 110 */     if (paramBoolean2) {
/* 111 */       this.vsb = new XVerticalScrollbar(this);
/* 112 */       this.vsb.setValues(0, 0, 0, 0, 1, paramInt2 - 1);
/*     */     }
/* 114 */     if (paramBoolean3) {
/* 115 */       this.hsb = new XHorizontalScrollbar(this);
/* 116 */       this.hsb.setValues(0, 0, 0, 0, 1, 1);
/*     */     }
/*     */ 
/* 119 */     setFont(paramFont);
/* 120 */     this.TEXT_SPACE = paramInt3;
/* 121 */     this.ITEM_MARGIN = paramInt4;
/* 122 */     this.BORDER_WIDTH = paramInt5;
/* 123 */     this.SCROLLBAR_WIDTH = paramInt6;
/*     */   }
/*     */ 
/*     */   public Component getEventSource() {
/* 127 */     return this.peer.getEventSource();
/*     */   }
/*     */ 
/*     */   public void add(String paramString)
/*     */   {
/* 135 */     this.items.add(paramString);
/* 136 */     updateScrollbars();
/*     */   }
/*     */ 
/*     */   public void add(String paramString, int paramInt) {
/* 140 */     this.items.add(paramInt, paramString);
/* 141 */     updateScrollbars();
/*     */   }
/*     */ 
/*     */   public void remove(String paramString)
/*     */   {
/* 146 */     this.items.remove(paramString);
/* 147 */     updateScrollbars();
/*     */   }
/*     */ 
/*     */   public void remove(int paramInt)
/*     */   {
/* 153 */     this.items.remove(paramInt);
/* 154 */     updateScrollbars();
/*     */   }
/*     */ 
/*     */   public void removeAll()
/*     */   {
/* 159 */     this.items.removeAll(this.items);
/* 160 */     updateScrollbars();
/*     */   }
/*     */ 
/*     */   public void setMultiSelect(boolean paramBoolean) {
/* 164 */     this.multiSelect = paramBoolean;
/*     */   }
/*     */ 
/*     */   public void select(int paramInt)
/*     */   {
/* 173 */     if (paramInt > getItemCount() - 1) {
/* 174 */       paramInt = isEmpty() ? -1 : 0;
/*     */     }
/* 176 */     if (this.multiSelect) {
/* 177 */       if (!$assertionsDisabled) throw new AssertionError("Implement ListHelper.select() for multiselect");
/*     */     }
/* 179 */     else if (getSelectedIndex() != paramInt) {
/* 180 */       this.selected.remove(0);
/* 181 */       this.selected.add(Integer.valueOf(paramInt));
/* 182 */       makeVisible(paramInt);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void deselect(int paramInt)
/*     */   {
/* 188 */     if (!$assertionsDisabled) throw new AssertionError();
/*     */   }
/*     */ 
/*     */   public int getSelectedIndex()
/*     */   {
/* 194 */     if (!this.multiSelect) {
/* 195 */       Integer localInteger = (Integer)this.selected.get(0);
/* 196 */       return localInteger.intValue();
/*     */     }
/* 198 */     return -1;
/*     */   }
/*     */   int[] getSelectedIndexes() {
/* 201 */     if (!$assertionsDisabled) throw new AssertionError(); return null;
/*     */   }
/*     */ 
/*     */   public boolean checkVsbVisibilityChangedAndReset()
/*     */   {
/* 208 */     boolean bool = this.vsbVisibilityChanged;
/* 209 */     this.vsbVisibilityChanged = false;
/* 210 */     return bool;
/*     */   }
/*     */ 
/*     */   public boolean isEmpty() {
/* 214 */     return this.items.isEmpty();
/*     */   }
/*     */ 
/*     */   public int getItemCount() {
/* 218 */     return this.items.size();
/*     */   }
/*     */ 
/*     */   public String getItem(int paramInt) {
/* 222 */     return (String)this.items.get(paramInt);
/*     */   }
/*     */ 
/*     */   public void setFocusedIndex(int paramInt)
/*     */   {
/* 230 */     this.focusedIndex = paramInt;
/*     */   }
/*     */ 
/*     */   public boolean isFocusedIndex(int paramInt) {
/* 234 */     return paramInt == this.focusedIndex;
/*     */   }
/*     */ 
/*     */   public void setFont(Font paramFont) {
/* 238 */     if (paramFont != this.font) {
/* 239 */       this.font = paramFont;
/* 240 */       this.fm = Toolkit.getDefaultToolkit().getFontMetrics(this.font);
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getMaxItemWidth()
/*     */   {
/* 249 */     int i = 0;
/* 250 */     int j = getItemCount();
/* 251 */     for (int k = 0; k < j; k++) {
/* 252 */       int m = this.fm.stringWidth(getItem(k));
/* 253 */       i = Math.max(i, m);
/*     */     }
/* 255 */     return i;
/*     */   }
/*     */ 
/*     */   int getItemHeight()
/*     */   {
/* 262 */     return this.fm.getHeight() + 2 * this.TEXT_SPACE;
/*     */   }
/*     */ 
/*     */   public int y2index(int paramInt) {
/* 266 */     if (log.isLoggable(500)) {
/* 267 */       log.fine("y=" + paramInt + ", firstIdx=" + firstDisplayedIndex() + ", itemHeight=" + getItemHeight() + ",item_margin=" + this.ITEM_MARGIN);
/*     */     }
/*     */ 
/* 271 */     int i = firstDisplayedIndex() + (paramInt - 2 * this.ITEM_MARGIN) / (getItemHeight() + 2 * this.ITEM_MARGIN);
/* 272 */     return i;
/*     */   }
/*     */ 
/*     */   public int firstDisplayedIndex()
/*     */   {
/* 281 */     if (this.vsbVis) {
/* 282 */       return this.vsb.getValue();
/*     */     }
/* 284 */     return 0;
/*     */   }
/*     */ 
/*     */   public int lastDisplayedIndex()
/*     */   {
/* 289 */     if ((this.hsbVis) && 
/* 290 */       (!$assertionsDisabled)) throw new AssertionError("Implement for horiz scroll bar");
/*     */ 
/* 293 */     return this.vsbVis ? this.vsb.getValue() + this.maxVisItems - 1 : getItemCount() - 1;
/*     */   }
/*     */ 
/*     */   public void makeVisible(int paramInt)
/*     */   {
/* 300 */     if (this.vsbVis)
/* 301 */       if (paramInt < firstDisplayedIndex()) {
/* 302 */         this.vsb.setValue(paramInt);
/*     */       }
/* 304 */       else if (paramInt > lastDisplayedIndex())
/* 305 */         this.vsb.setValue(paramInt - this.maxVisItems + 1);
/*     */   }
/*     */ 
/*     */   public void up()
/*     */   {
/* 312 */     int i = getSelectedIndex();
/* 313 */     int j = getItemCount();
/*     */ 
/* 316 */     assert (i >= 0);
/*     */     int k;
/* 318 */     if (i == 0) {
/* 319 */       k = j - 1;
/*     */     }
/*     */     else {
/* 322 */       i--; k = i;
/*     */     }
/*     */ 
/* 325 */     select(k);
/*     */   }
/*     */ 
/*     */   public void down() {
/* 329 */     int i = (getSelectedIndex() + 1) % getItemCount();
/* 330 */     select(i);
/*     */   }
/*     */ 
/*     */   public void pageUp()
/*     */   {
/* 335 */     if ((this.vsbVis) && (firstDisplayedIndex() > 0))
/* 336 */       if (this.multiSelect) {
/* 337 */         if (!$assertionsDisabled) throw new AssertionError("Implement pageUp() for multiSelect"); 
/*     */       }
/*     */       else
/*     */       {
/* 340 */         int i = getSelectedIndex() - firstDisplayedIndex();
/*     */ 
/* 342 */         int j = firstDisplayedIndex() - this.vsb.getBlockIncrement();
/* 343 */         this.vsb.setValue(j);
/* 344 */         select(firstDisplayedIndex() + i);
/*     */       }
/*     */   }
/*     */ 
/*     */   public void pageDown() {
/* 349 */     if ((this.vsbVis) && (lastDisplayedIndex() < getItemCount() - 1))
/* 350 */       if (this.multiSelect) {
/* 351 */         if (!$assertionsDisabled) throw new AssertionError("Implement pageDown() for multiSelect"); 
/*     */       }
/*     */       else
/*     */       {
/* 354 */         int i = getSelectedIndex() - firstDisplayedIndex();
/*     */ 
/* 356 */         int j = lastDisplayedIndex();
/* 357 */         this.vsb.setValue(j);
/* 358 */         select(firstDisplayedIndex() + i);
/*     */       } 
/*     */   }
/*     */   public void home() {
/*     */   }
/*     */ 
/*     */   public void end() {  }
/*     */ 
/*     */ 
/* 366 */   public boolean isVSBVisible() { return this.vsbVis; } 
/* 367 */   public boolean isHSBVisible() { return this.hsbVis; } 
/*     */   public XVerticalScrollbar getVSB() {
/* 369 */     return this.vsb; } 
/* 370 */   public XHorizontalScrollbar getHSB() { return this.hsb; }
/*     */ 
/*     */   public boolean isInVertSB(Rectangle paramRectangle, int paramInt1, int paramInt2) {
/* 373 */     if (this.vsbVis) {
/* 374 */       assert (this.vsb != null) : "Vert scrollbar is visible, yet is null?";
/* 375 */       int i = this.hsbVis ? paramRectangle.height - this.SCROLLBAR_WIDTH : paramRectangle.height;
/* 376 */       return (paramInt1 <= paramRectangle.width) && (paramInt1 >= paramRectangle.width - this.SCROLLBAR_WIDTH) && (paramInt2 >= 0) && (paramInt2 <= i);
/*     */     }
/*     */ 
/* 381 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isInHorizSB(Rectangle paramRectangle, int paramInt1, int paramInt2) {
/* 385 */     if (this.hsbVis) {
/* 386 */       assert (this.hsb != null) : "Horiz scrollbar is visible, yet is null?";
/*     */ 
/* 388 */       int i = this.vsbVis ? paramRectangle.width - this.SCROLLBAR_WIDTH : paramRectangle.width;
/* 389 */       return (paramInt1 <= i) && (paramInt1 >= 0) && (paramInt2 >= paramRectangle.height - this.SCROLLBAR_WIDTH) && (paramInt2 <= paramRectangle.height);
/*     */     }
/*     */ 
/* 394 */     return false;
/*     */   }
/*     */ 
/*     */   public void handleVSBEvent(MouseEvent paramMouseEvent, Rectangle paramRectangle, int paramInt1, int paramInt2) {
/* 398 */     int i = this.hsbVis ? paramRectangle.height - this.SCROLLBAR_WIDTH : paramRectangle.height;
/*     */ 
/* 400 */     this.vsb.handleMouseEvent(paramMouseEvent.getID(), paramMouseEvent.getModifiers(), paramInt1 - (paramRectangle.width - this.SCROLLBAR_WIDTH), paramInt2);
/*     */   }
/*     */ 
/*     */   void updateScrollbars()
/*     */   {
/* 411 */     boolean bool = this.vsbVis;
/* 412 */     this.vsbVis = ((this.vsb != null) && (this.items.size() > this.maxVisItems));
/* 413 */     if (this.vsbVis) {
/* 414 */       this.vsb.setValues(this.vsb.getValue(), getNumItemsDisplayed(), this.vsb.getMinimum(), this.items.size());
/*     */     }
/*     */ 
/* 421 */     this.vsbVisibilityChanged = (this.vsbVis != bool);
/*     */   }
/*     */ 
/*     */   public int getNumItemsDisplayed()
/*     */   {
/* 426 */     return this.items.size() > this.maxVisItems ? this.maxVisItems : this.items.size();
/*     */   }
/*     */ 
/*     */   public void repaintScrollbarRequest(XScrollbar paramXScrollbar) {
/* 430 */     Graphics localGraphics = this.peer.getGraphics();
/* 431 */     Rectangle localRectangle = this.peer.getBounds();
/* 432 */     if ((paramXScrollbar == this.vsb) && (this.vsbVis)) {
/* 433 */       paintVSB(localGraphics, XComponentPeer.getSystemColors(), localRectangle);
/*     */     }
/* 435 */     else if ((paramXScrollbar == this.hsb) && (this.hsbVis)) {
/* 436 */       paintHSB(localGraphics, XComponentPeer.getSystemColors(), localRectangle);
/*     */     }
/* 438 */     localGraphics.dispose();
/*     */   }
/*     */ 
/*     */   public void notifyValue(XScrollbar paramXScrollbar, int paramInt1, int paramInt2, boolean paramBoolean) {
/* 442 */     if (paramXScrollbar == this.vsb) {
/* 443 */       int i = this.vsb.getValue();
/* 444 */       this.vsb.setValue(paramInt2);
/* 445 */       int j = i != this.vsb.getValue() ? 1 : 0;
/*     */ 
/* 447 */       if (this.mouseDraggedOutVertically) {
/* 448 */         int k = getSelectedIndex();
/* 449 */         int m = getSelectedIndex() + paramInt2 - i;
/* 450 */         select(m);
/* 451 */         j = (j != 0) || (getSelectedIndex() != k) ? 1 : 0;
/*     */       }
/*     */ 
/* 455 */       Graphics localGraphics = this.peer.getGraphics();
/* 456 */       Rectangle localRectangle = this.peer.getBounds();
/* 457 */       int n = paramInt2;
/* 458 */       int i1 = Math.min(getItemCount() - 1, paramInt2 + this.maxVisItems);
/*     */ 
/* 460 */       if (j != 0) {
/* 461 */         paintItems(localGraphics, this.colors, localRectangle, n, i1);
/*     */       }
/* 463 */       localGraphics.dispose();
/*     */     }
/* 466 */     else if ((XHorizontalScrollbar)paramXScrollbar == this.hsb) {
/* 467 */       this.hsb.setValue(paramInt2);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void updateColors(Color[] paramArrayOfColor)
/*     */   {
/* 473 */     this.colors = paramArrayOfColor;
/*     */   }
/*     */ 
/*     */   public void paintItems(Graphics paramGraphics, Color[] paramArrayOfColor, Rectangle paramRectangle)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void paintAllItems(Graphics paramGraphics, Color[] paramArrayOfColor, Rectangle paramRectangle)
/*     */   {
/* 498 */     paintItems(paramGraphics, paramArrayOfColor, paramRectangle, firstDisplayedIndex(), lastDisplayedIndex());
/*     */   }
/*     */ 
/*     */   public void paintItems(Graphics paramGraphics, Color[] paramArrayOfColor, Rectangle paramRectangle, int paramInt1, int paramInt2)
/*     */   {
/* 506 */     this.peer.flush();
/* 507 */     int i = this.BORDER_WIDTH + this.ITEM_MARGIN;
/* 508 */     int j = paramRectangle.width - 2 * this.ITEM_MARGIN - 2 * this.BORDER_WIDTH - (this.vsbVis ? this.SCROLLBAR_WIDTH : 0);
/* 509 */     int k = getItemHeight();
/* 510 */     int m = this.BORDER_WIDTH + this.ITEM_MARGIN;
/*     */ 
/* 512 */     for (int n = paramInt1; n <= paramInt2; n++) {
/* 513 */       paintItem(paramGraphics, paramArrayOfColor, getItem(n), i, m, j, k, isItemSelected(n), isFocusedIndex(n));
/*     */ 
/* 517 */       m += k + 2 * this.ITEM_MARGIN;
/*     */     }
/*     */ 
/* 520 */     if (this.vsbVis) {
/* 521 */       paintVSB(paramGraphics, XComponentPeer.getSystemColors(), paramRectangle);
/*     */     }
/* 523 */     if (this.hsbVis) {
/* 524 */       paintHSB(paramGraphics, XComponentPeer.getSystemColors(), paramRectangle);
/*     */     }
/* 526 */     this.peer.flush();
/*     */   }
/*     */ 
/*     */   public void paintItem(Graphics paramGraphics, Color[] paramArrayOfColor, String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean1, boolean paramBoolean2)
/*     */   {
/* 545 */     if (paramBoolean1) {
/* 546 */       paramGraphics.setColor(paramArrayOfColor[3]);
/*     */     }
/*     */     else {
/* 549 */       paramGraphics.setColor(paramArrayOfColor[0]);
/*     */     }
/* 551 */     paramGraphics.fillRect(paramInt1, paramInt2, paramInt3, paramInt4);
/*     */ 
/* 553 */     if (paramBoolean2)
/*     */     {
/* 555 */       paramGraphics.setColor(Color.BLACK);
/* 556 */       paramGraphics.drawRect(paramInt1 + 1, paramInt2 + 1, paramInt3 - 2, paramInt4 - 2);
/*     */     }
/*     */ 
/* 562 */     if (paramBoolean1) {
/* 563 */       paramGraphics.setColor(paramArrayOfColor[0]);
/*     */     }
/*     */     else {
/* 566 */       paramGraphics.setColor(paramArrayOfColor[3]);
/*     */     }
/* 568 */     paramGraphics.setFont(this.font);
/*     */ 
/* 573 */     int i = this.fm.getAscent();
/* 574 */     int j = this.fm.getDescent();
/*     */ 
/* 576 */     paramGraphics.drawString(paramString, paramInt1 + this.TEXT_SPACE, paramInt2 + (paramInt4 + this.fm.getMaxAscent() - this.fm.getMaxDescent()) / 2);
/*     */   }
/*     */ 
/*     */   boolean isItemSelected(int paramInt)
/*     */   {
/* 581 */     Iterator localIterator = this.selected.iterator();
/* 582 */     while (localIterator.hasNext()) {
/* 583 */       Integer localInteger = (Integer)localIterator.next();
/* 584 */       if (localInteger.intValue() == paramInt) {
/* 585 */         return true;
/*     */       }
/*     */     }
/* 588 */     return false;
/*     */   }
/*     */ 
/*     */   public void paintVSB(Graphics paramGraphics, Color[] paramArrayOfColor, Rectangle paramRectangle) {
/* 592 */     int i = paramRectangle.height - 2 * this.BORDER_WIDTH - (this.hsbVis ? this.SCROLLBAR_WIDTH - 2 : 0);
/* 593 */     Graphics localGraphics = paramGraphics.create();
/*     */ 
/* 595 */     paramGraphics.setColor(paramArrayOfColor[0]);
/*     */     try {
/* 597 */       localGraphics.translate(paramRectangle.width - this.BORDER_WIDTH - this.SCROLLBAR_WIDTH, this.BORDER_WIDTH);
/*     */ 
/* 600 */       this.vsb.setSize(this.SCROLLBAR_WIDTH, paramRectangle.height);
/* 601 */       this.vsb.paint(localGraphics, paramArrayOfColor, true);
/*     */     } finally {
/* 603 */       localGraphics.dispose();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void paintHSB(Graphics paramGraphics, Color[] paramArrayOfColor, Rectangle paramRectangle)
/*     */   {
/*     */   }
/*     */ 
/*     */   static boolean doWheelScroll(XVerticalScrollbar paramXVerticalScrollbar, XHorizontalScrollbar paramXHorizontalScrollbar, MouseWheelEvent paramMouseWheelEvent)
/*     */   {
/* 622 */     Object localObject = null;
/*     */ 
/* 626 */     if (paramXVerticalScrollbar != null) {
/* 627 */       localObject = paramXVerticalScrollbar;
/*     */     }
/* 629 */     else if (paramXHorizontalScrollbar != null) {
/* 630 */       localObject = paramXHorizontalScrollbar;
/*     */     }
/*     */     else {
/* 633 */       return false;
/*     */     }
/*     */ 
/* 636 */     int i = paramMouseWheelEvent.getWheelRotation();
/*     */ 
/* 639 */     if (((i < 0) && (((XScrollbar)localObject).getValue() > ((XScrollbar)localObject).getMinimum())) || ((i > 0) && (((XScrollbar)localObject).getValue() < ((XScrollbar)localObject).getMaximum())) || (i != 0))
/*     */     {
/* 643 */       int j = paramMouseWheelEvent.getScrollType();
/*     */       int k;
/* 645 */       if (j == 1) {
/* 646 */         k = i * ((XScrollbar)localObject).getBlockIncrement();
/*     */       }
/*     */       else {
/* 649 */         k = paramMouseWheelEvent.getUnitsToScroll() * ((XScrollbar)localObject).getUnitIncrement();
/*     */       }
/* 651 */       ((XScrollbar)localObject).setValue(((XScrollbar)localObject).getValue() + k);
/* 652 */       return true;
/*     */     }
/* 654 */     return false;
/*     */   }
/*     */ 
/*     */   void trackMouseDraggedScroll(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/* 665 */     if (!this.mouseDraggedOutVertically) {
/* 666 */       if (this.vsb.beforeThumb(paramInt1, paramInt2))
/* 667 */         this.vsb.setMode(2);
/*     */       else {
/* 669 */         this.vsb.setMode(1);
/*     */       }
/*     */     }
/*     */ 
/* 673 */     if ((!this.mouseDraggedOutVertically) && ((paramInt2 < 0) || (paramInt2 >= paramInt4))) {
/* 674 */       this.mouseDraggedOutVertically = true;
/* 675 */       this.vsb.startScrollingInstance();
/*     */     }
/*     */ 
/* 678 */     if ((this.mouseDraggedOutVertically) && (paramInt2 >= 0) && (paramInt2 < paramInt4) && (paramInt1 >= 0) && (paramInt1 < paramInt3)) {
/* 679 */       this.mouseDraggedOutVertically = false;
/* 680 */       this.vsb.stopScrollingInstance();
/*     */     }
/*     */   }
/*     */ 
/*     */   void trackMouseReleasedScroll()
/*     */   {
/* 692 */     if (this.mouseDraggedOutVertically) {
/* 693 */       this.mouseDraggedOutVertically = false;
/* 694 */       this.vsb.stopScrollingInstance();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.ListHelper
 * JD-Core Version:    0.6.2
 */