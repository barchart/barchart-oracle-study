/*      */ package sun.awt.X11;
/*      */ 
/*      */ import java.awt.AWTEvent;
/*      */ import java.awt.Choice;
/*      */ import java.awt.Color;
/*      */ import java.awt.Component;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.EventQueue;
/*      */ import java.awt.Font;
/*      */ import java.awt.FontMetrics;
/*      */ import java.awt.Graphics;
/*      */ import java.awt.Insets;
/*      */ import java.awt.Point;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.Toolkit;
/*      */ import java.awt.event.FocusEvent;
/*      */ import java.awt.event.ItemEvent;
/*      */ import java.awt.event.KeyEvent;
/*      */ import java.awt.event.MouseEvent;
/*      */ import java.awt.event.MouseWheelEvent;
/*      */ import java.awt.peer.ChoicePeer;
/*      */ import sun.util.logging.PlatformLogger;
/*      */ 
/*      */ public class XChoicePeer extends XComponentPeer
/*      */   implements ChoicePeer, ToplevelStateListener
/*      */ {
/*   46 */   private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.X11.XChoicePeer");
/*      */   private static final int MAX_UNFURLED_ITEMS = 10;
/*      */   public static final int TEXT_SPACE = 1;
/*      */   public static final int BORDER_WIDTH = 1;
/*      */   public static final int ITEM_MARGIN = 1;
/*      */   public static final int SCROLLBAR_WIDTH = 15;
/*   60 */   private static final Insets focusInsets = new Insets(0, 0, 0, 0);
/*      */   static final int WIDGET_OFFSET = 18;
/*      */   static final int TEXT_XPAD = 8;
/*      */   static final int TEXT_YPAD = 6;
/*   72 */   static final Color focusColor = Color.black;
/*      */ 
/*   78 */   private boolean unfurled = false;
/*      */ 
/*   80 */   private boolean dragging = false;
/*      */ 
/*   84 */   private boolean mouseInSB = false;
/*      */ 
/*   87 */   private boolean firstPress = false;
/*      */ 
/*   99 */   private boolean wasDragged = false;
/*      */   private ListHelper helper;
/*      */   private UnfurledChoice unfurledChoice;
/*  108 */   private boolean drawSelectedItem = true;
/*      */   private Component alignUnder;
/*  117 */   private int dragStartIdx = -1;
/*      */   private XChoicePeerListener choiceListener;
/*      */ 
/*      */   XChoicePeer(Choice paramChoice)
/*      */   {
/*  124 */     super(paramChoice);
/*      */   }
/*      */ 
/*      */   void preInit(XCreateWindowParams paramXCreateWindowParams) {
/*  128 */     super.preInit(paramXCreateWindowParams);
/*  129 */     Choice localChoice = (Choice)this.target;
/*  130 */     int i = localChoice.getItemCount();
/*  131 */     this.unfurledChoice = new UnfurledChoice(localChoice);
/*  132 */     getToplevelXWindow().addToplevelStateListener(this);
/*  133 */     this.helper = new ListHelper(this.unfurledChoice, getGUIcolors(), i, false, true, false, localChoice.getFont(), 10, 1, 1, 1, 15);
/*      */   }
/*      */ 
/*      */   void postInit(XCreateWindowParams paramXCreateWindowParams)
/*      */   {
/*  148 */     super.postInit(paramXCreateWindowParams);
/*  149 */     Choice localChoice = (Choice)this.target;
/*  150 */     int i = localChoice.getItemCount();
/*      */ 
/*  153 */     for (int j = 0; j < i; j++) {
/*  154 */       this.helper.add(localChoice.getItem(j));
/*      */     }
/*  156 */     if (!this.helper.isEmpty()) {
/*  157 */       this.helper.select(localChoice.getSelectedIndex());
/*  158 */       this.helper.setFocusedIndex(localChoice.getSelectedIndex());
/*      */     }
/*  160 */     this.helper.updateColors(getGUIcolors());
/*  161 */     updateMotifColors(getPeerBackground());
/*      */   }
/*      */   public boolean isFocusable() {
/*  164 */     return true;
/*      */   }
/*      */ 
/*      */   public void setBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
/*      */   {
/*  170 */     int i = this.x;
/*  171 */     int j = this.y;
/*  172 */     int k = this.width;
/*  173 */     int m = this.height;
/*  174 */     super.setBounds(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
/*  175 */     if ((this.unfurled) && ((i != this.x) || (j != this.y) || (k != this.width) || (m != this.height)))
/*  176 */       hidePopdownMenu();
/*      */   }
/*      */ 
/*      */   public void focusGained(FocusEvent paramFocusEvent)
/*      */   {
/*  182 */     super.focusGained(paramFocusEvent);
/*  183 */     repaint();
/*      */   }
/*      */ 
/*      */   public void setEnabled(boolean paramBoolean)
/*      */   {
/*  192 */     super.setEnabled(paramBoolean);
/*  193 */     this.helper.updateColors(getGUIcolors());
/*  194 */     if ((!paramBoolean) && (this.unfurled))
/*  195 */       hidePopdownMenu();
/*      */   }
/*      */ 
/*      */   public void focusLost(FocusEvent paramFocusEvent)
/*      */   {
/*  201 */     super.focusLost(paramFocusEvent);
/*  202 */     repaint();
/*      */   }
/*      */ 
/*      */   void ungrabInputImpl() {
/*  206 */     if (this.unfurled) {
/*  207 */       this.unfurled = false;
/*  208 */       this.dragging = false;
/*  209 */       this.mouseInSB = false;
/*  210 */       this.unfurledChoice.setVisible(false);
/*      */     }
/*      */ 
/*  213 */     super.ungrabInputImpl();
/*      */   }
/*      */ 
/*      */   void handleJavaKeyEvent(KeyEvent paramKeyEvent) {
/*  217 */     if (paramKeyEvent.getID() == 401)
/*  218 */       keyPressed(paramKeyEvent);
/*      */   }
/*      */ 
/*      */   public void keyPressed(KeyEvent paramKeyEvent)
/*      */   {
/*      */     int i;
/*      */     int j;
/*  223 */     switch (paramKeyEvent.getKeyCode())
/*      */     {
/*      */     case 40:
/*      */     case 225:
/*  227 */       if (this.helper.getItemCount() > 1) {
/*  228 */         this.helper.down();
/*  229 */         i = this.helper.getSelectedIndex();
/*      */ 
/*  231 */         ((Choice)this.target).select(i);
/*  232 */         postEvent(new ItemEvent((Choice)this.target, 701, ((Choice)this.target).getItem(i), 1));
/*      */ 
/*  236 */         repaint();
/*  237 */       }break;
/*      */     case 38:
/*      */     case 224:
/*  242 */       if (this.helper.getItemCount() > 1) {
/*  243 */         this.helper.up();
/*  244 */         i = this.helper.getSelectedIndex();
/*      */ 
/*  246 */         ((Choice)this.target).select(i);
/*  247 */         postEvent(new ItemEvent((Choice)this.target, 701, ((Choice)this.target).getItem(i), 1));
/*      */ 
/*  251 */         repaint();
/*  252 */       }break;
/*      */     case 34:
/*  256 */       if ((this.unfurled) && (!this.dragging)) {
/*  257 */         i = this.helper.getSelectedIndex();
/*  258 */         this.helper.pageDown();
/*  259 */         j = this.helper.getSelectedIndex();
/*  260 */         if (i != j) {
/*  261 */           ((Choice)this.target).select(j);
/*  262 */           postEvent(new ItemEvent((Choice)this.target, 701, ((Choice)this.target).getItem(j), 1));
/*      */ 
/*  266 */           repaint();
/*      */         }
/*      */       }
/*  268 */       break;
/*      */     case 33:
/*  271 */       if ((this.unfurled) && (!this.dragging)) {
/*  272 */         i = this.helper.getSelectedIndex();
/*  273 */         this.helper.pageUp();
/*  274 */         j = this.helper.getSelectedIndex();
/*  275 */         if (i != j) {
/*  276 */           ((Choice)this.target).select(j);
/*  277 */           postEvent(new ItemEvent((Choice)this.target, 701, ((Choice)this.target).getItem(j), 1));
/*      */ 
/*  281 */           repaint();
/*      */         }
/*      */       }
/*  283 */       break;
/*      */     case 10:
/*      */     case 27:
/*  287 */       if (this.unfurled) {
/*  288 */         if (this.dragging) {
/*  289 */           if (paramKeyEvent.getKeyCode() == 27)
/*      */           {
/*  293 */             this.helper.select(this.dragStartIdx);
/*      */           } else {
/*  295 */             i = this.helper.getSelectedIndex();
/*  296 */             ((Choice)this.target).select(i);
/*  297 */             postEvent(new ItemEvent((Choice)this.target, 701, ((Choice)this.target).getItem(i), 1));
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  303 */         hidePopdownMenu();
/*  304 */         this.dragging = false;
/*  305 */         this.wasDragged = false;
/*  306 */         this.mouseInSB = false;
/*      */ 
/*  309 */         if (this.choiceListener != null)
/*  310 */           this.choiceListener.unfurledChoiceClosing();  } break;
/*      */     default:
/*  315 */       if (this.unfurled)
/*  316 */         Toolkit.getDefaultToolkit().beep();
/*      */       break;
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean handlesWheelScrolling() {
/*  322 */     return true;
/*      */   }
/*      */   void handleJavaMouseWheelEvent(MouseWheelEvent paramMouseWheelEvent) {
/*  325 */     if ((this.unfurled) && (this.helper.isVSBVisible()) && 
/*  326 */       (ListHelper.doWheelScroll(this.helper.getVSB(), null, paramMouseWheelEvent)))
/*  327 */       repaint();
/*      */   }
/*      */ 
/*      */   void handleJavaMouseEvent(MouseEvent paramMouseEvent)
/*      */   {
/*  333 */     super.handleJavaMouseEvent(paramMouseEvent);
/*  334 */     int i = paramMouseEvent.getID();
/*  335 */     switch (i) {
/*      */     case 501:
/*  337 */       mousePressed(paramMouseEvent);
/*  338 */       break;
/*      */     case 502:
/*  340 */       mouseReleased(paramMouseEvent);
/*  341 */       break;
/*      */     case 506:
/*  343 */       mouseDragged(paramMouseEvent);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void mousePressed(MouseEvent paramMouseEvent)
/*      */   {
/*  354 */     if (paramMouseEvent.getButton() == 1) {
/*  355 */       this.dragStartIdx = this.helper.getSelectedIndex();
/*  356 */       if (this.unfurled)
/*      */       {
/*  358 */         if ((!isMouseEventInChoice(paramMouseEvent)) && (!this.unfurledChoice.isMouseEventInside(paramMouseEvent)))
/*      */         {
/*  361 */           hidePopdownMenu();
/*      */         }
/*      */ 
/*  365 */         this.unfurledChoice.trackMouse(paramMouseEvent);
/*      */       }
/*      */       else
/*      */       {
/*  369 */         grabInput();
/*  370 */         this.unfurledChoice.toFront();
/*  371 */         this.firstPress = true;
/*  372 */         this.wasDragged = false;
/*  373 */         this.unfurled = true;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   void hidePopdownMenu()
/*      */   {
/*  382 */     ungrabInput();
/*  383 */     this.unfurledChoice.setVisible(false);
/*  384 */     this.unfurled = false;
/*      */   }
/*      */ 
/*      */   public void mouseReleased(MouseEvent paramMouseEvent) {
/*  388 */     if (this.unfurled) {
/*  389 */       if (this.mouseInSB) {
/*  390 */         this.unfurledChoice.trackMouse(paramMouseEvent);
/*      */       }
/*      */       else
/*      */       {
/*  403 */         boolean bool1 = this.unfurledChoice.isMouseEventInside(paramMouseEvent);
/*  404 */         boolean bool2 = this.unfurledChoice.isMouseInListArea(paramMouseEvent);
/*      */ 
/*  409 */         if ((!this.helper.isEmpty()) && (!bool2) && (this.dragging))
/*      */         {
/*  411 */           ((Choice)this.target).select(this.dragStartIdx);
/*      */         }
/*      */ 
/*  416 */         if ((!this.firstPress) && (bool2)) {
/*  417 */           hidePopdownMenu();
/*      */         }
/*      */ 
/*  421 */         if ((!this.firstPress) && (!bool1)) {
/*  422 */           hidePopdownMenu();
/*      */         }
/*      */ 
/*  426 */         if ((this.firstPress) && (this.dragging)) {
/*  427 */           hidePopdownMenu();
/*      */         }
/*      */ 
/*  433 */         if ((!this.firstPress) && (!bool2) && (bool1) && (this.dragging))
/*      */         {
/*  436 */           hidePopdownMenu();
/*      */         }
/*      */ 
/*  439 */         if (!this.helper.isEmpty())
/*      */         {
/*  442 */           if (this.unfurledChoice.isMouseInListArea(paramMouseEvent)) {
/*  443 */             int i = this.helper.getSelectedIndex();
/*  444 */             if (i >= 0)
/*      */             {
/*  447 */               if (i != this.dragStartIdx) {
/*  448 */                 ((Choice)this.target).select(i);
/*      */               }
/*      */ 
/*  452 */               if ((this.wasDragged) && (paramMouseEvent.getButton() != 1)) {
/*  453 */                 ((Choice)this.target).select(this.dragStartIdx);
/*      */               }
/*      */ 
/*  459 */               if ((paramMouseEvent.getButton() == 1) && ((!this.firstPress) || (this.wasDragged)))
/*      */               {
/*  462 */                 postEvent(new ItemEvent((Choice)this.target, 701, ((Choice)this.target).getItem(i), 1));
/*      */               }
/*      */ 
/*  469 */               if (this.choiceListener != null) {
/*  470 */                 this.choiceListener.unfurledChoiceClosing();
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/*  476 */         this.unfurledChoice.trackMouse(paramMouseEvent);
/*      */       }
/*      */     }
/*      */ 
/*  480 */     this.dragging = false;
/*  481 */     this.wasDragged = false;
/*  482 */     this.firstPress = false;
/*  483 */     this.dragStartIdx = -1;
/*      */   }
/*      */ 
/*      */   public void mouseDragged(MouseEvent paramMouseEvent)
/*      */   {
/*  494 */     if (paramMouseEvent.getModifiers() == 16) {
/*  495 */       this.dragging = true;
/*  496 */       this.wasDragged = true;
/*  497 */       this.unfurledChoice.trackMouse(paramMouseEvent);
/*      */     }
/*      */   }
/*      */ 
/*      */   public Dimension getMinimumSize()
/*      */   {
/*  504 */     FontMetrics localFontMetrics = getFontMetrics(this.target.getFont());
/*  505 */     Choice localChoice = (Choice)this.target;
/*  506 */     int i = 0;
/*  507 */     for (int j = localChoice.countItems(); j-- > 0; ) {
/*  508 */       i = Math.max(localFontMetrics.stringWidth(localChoice.getItem(j)), i);
/*      */     }
/*  510 */     return new Dimension(i + 8 + 18, localFontMetrics.getMaxAscent() + localFontMetrics.getMaxDescent() + 6);
/*      */   }
/*      */ 
/*      */   public void layout()
/*      */   {
/*      */   }
/*      */ 
/*      */   public void paint(Graphics paramGraphics)
/*      */   {
/*  554 */     flush();
/*  555 */     Dimension localDimension = getPeerSize();
/*      */ 
/*  558 */     paramGraphics.setColor(getPeerBackground());
/*  559 */     paramGraphics.fillRect(0, 0, this.width, this.height);
/*      */ 
/*  561 */     drawMotif3DRect(paramGraphics, 1, 1, this.width - 2, this.height - 2, false);
/*  562 */     drawMotif3DRect(paramGraphics, this.width - 18, this.height / 2 - 3, 12, 6, false);
/*      */ 
/*  564 */     if ((!this.helper.isEmpty()) && (this.helper.getSelectedIndex() != -1)) {
/*  565 */       paramGraphics.setFont(getPeerFont());
/*  566 */       FontMetrics localFontMetrics = paramGraphics.getFontMetrics();
/*  567 */       String str = this.helper.getItem(this.helper.getSelectedIndex());
/*  568 */       if ((str != null) && (this.drawSelectedItem)) {
/*  569 */         paramGraphics.setClip(1, 1, this.width - 18 - 2, this.height);
/*  570 */         if (isEnabled()) {
/*  571 */           paramGraphics.setColor(getPeerForeground());
/*  572 */           paramGraphics.drawString(str, 5, (this.height + localFontMetrics.getMaxAscent() - localFontMetrics.getMaxDescent()) / 2);
/*      */         }
/*      */         else {
/*  575 */           paramGraphics.setColor(getPeerBackground().brighter());
/*  576 */           paramGraphics.drawString(str, 5, (this.height + localFontMetrics.getMaxAscent() - localFontMetrics.getMaxDescent()) / 2);
/*  577 */           paramGraphics.setColor(getPeerBackground().darker());
/*  578 */           paramGraphics.drawString(str, 4, (this.height + localFontMetrics.getMaxAscent() - localFontMetrics.getMaxDescent()) / 2 - 1);
/*      */         }
/*  580 */         paramGraphics.setClip(0, 0, this.width, this.height);
/*      */       }
/*      */     }
/*  583 */     if (hasFocus()) {
/*  584 */       paintFocus(paramGraphics, focusInsets.left, focusInsets.top, localDimension.width - (focusInsets.left + focusInsets.right) - 1, localDimension.height - (focusInsets.top + focusInsets.bottom) - 1);
/*      */     }
/*  586 */     if (this.unfurled) {
/*  587 */       this.unfurledChoice.repaint();
/*      */     }
/*  589 */     flush();
/*      */   }
/*      */ 
/*      */   protected void paintFocus(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  594 */     paramGraphics.setColor(focusColor);
/*  595 */     paramGraphics.drawRect(paramInt1, paramInt2, paramInt3, paramInt4);
/*      */   }
/*      */ 
/*      */   public void select(int paramInt)
/*      */   {
/*  605 */     this.helper.select(paramInt);
/*  606 */     this.helper.setFocusedIndex(paramInt);
/*  607 */     repaint();
/*      */   }
/*      */ 
/*      */   public void add(String paramString, int paramInt) {
/*  611 */     this.helper.add(paramString, paramInt);
/*  612 */     repaint();
/*      */   }
/*      */ 
/*      */   public void remove(int paramInt) {
/*  616 */     int i = paramInt == this.helper.getSelectedIndex() ? 1 : 0;
/*  617 */     int j = (paramInt >= this.helper.firstDisplayedIndex()) && (paramInt <= this.helper.lastDisplayedIndex()) ? 1 : 0;
/*  618 */     this.helper.remove(paramInt);
/*  619 */     if (i != 0) {
/*  620 */       if (this.helper.isEmpty()) {
/*  621 */         this.helper.select(-1);
/*      */       }
/*      */       else {
/*  624 */         this.helper.select(0);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  636 */     if (!this.unfurled)
/*      */     {
/*  641 */       if (this.helper.isEmpty()) {
/*  642 */         repaint();
/*      */       }
/*  644 */       return;
/*      */     }
/*      */ 
/*  651 */     if (j != 0) {
/*  652 */       Rectangle localRectangle = this.unfurledChoice.placeOnScreen();
/*  653 */       this.unfurledChoice.reshape(localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height);
/*  654 */       return;
/*      */     }
/*      */ 
/*  662 */     if ((j != 0) || (i != 0))
/*  663 */       repaint();
/*      */   }
/*      */ 
/*      */   public void removeAll()
/*      */   {
/*  668 */     this.helper.removeAll();
/*  669 */     this.helper.select(-1);
/*      */ 
/*  675 */     Rectangle localRectangle = this.unfurledChoice.placeOnScreen();
/*  676 */     this.unfurledChoice.reshape(localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height);
/*  677 */     repaint();
/*      */   }
/*      */ 
/*      */   public void addItem(String paramString, int paramInt)
/*      */   {
/*  684 */     add(paramString, paramInt);
/*      */   }
/*      */ 
/*      */   public void setFont(Font paramFont) {
/*  688 */     super.setFont(paramFont);
/*  689 */     this.helper.setFont(this.font);
/*      */   }
/*      */ 
/*      */   public void setForeground(Color paramColor) {
/*  693 */     super.setForeground(paramColor);
/*  694 */     this.helper.updateColors(getGUIcolors());
/*      */   }
/*      */ 
/*      */   public void setBackground(Color paramColor) {
/*  698 */     super.setBackground(paramColor);
/*  699 */     this.unfurledChoice.setBackground(paramColor);
/*  700 */     this.helper.updateColors(getGUIcolors());
/*  701 */     updateMotifColors(paramColor);
/*      */   }
/*      */ 
/*      */   public void setDrawSelectedItem(boolean paramBoolean) {
/*  705 */     this.drawSelectedItem = paramBoolean;
/*      */   }
/*      */ 
/*      */   public void setAlignUnder(Component paramComponent) {
/*  709 */     this.alignUnder = paramComponent;
/*      */   }
/*      */ 
/*      */   public void addXChoicePeerListener(XChoicePeerListener paramXChoicePeerListener)
/*      */   {
/*  714 */     this.choiceListener = paramXChoicePeerListener;
/*      */   }
/*      */ 
/*      */   public void removeXChoicePeerListener()
/*      */   {
/*  719 */     this.choiceListener = null;
/*      */   }
/*      */ 
/*      */   public boolean isUnfurled() {
/*  723 */     return this.unfurled;
/*      */   }
/*      */ 
/*      */   public void stateChangedICCCM(int paramInt1, int paramInt2)
/*      */   {
/*  731 */     if ((this.unfurled) && (paramInt1 != paramInt2))
/*  732 */       hidePopdownMenu();
/*      */   }
/*      */ 
/*      */   public void stateChangedJava(int paramInt1, int paramInt2)
/*      */   {
/*  738 */     if ((this.unfurled) && (paramInt1 != paramInt2))
/*  739 */       hidePopdownMenu();
/*      */   }
/*      */ 
/*      */   public void dispose()
/*      */   {
/* 1005 */     if (this.unfurledChoice != null) {
/* 1006 */       this.unfurledChoice.destroy();
/*      */     }
/* 1008 */     super.dispose();
/*      */   }
/*      */ 
/*      */   boolean prePostEvent(final AWTEvent paramAWTEvent)
/*      */   {
/* 1022 */     if (this.unfurled)
/*      */     {
/* 1024 */       if ((paramAWTEvent instanceof MouseWheelEvent)) {
/* 1025 */         return super.prePostEvent(paramAWTEvent);
/*      */       }
/*      */ 
/* 1028 */       if ((paramAWTEvent instanceof KeyEvent))
/*      */       {
/* 1030 */         EventQueue.invokeLater(new Runnable() {
/*      */           public void run() {
/* 1032 */             if ((XChoicePeer.this.target.isFocusable()) && (XChoicePeer.this.getParentTopLevel().isFocusableWindow()))
/*      */             {
/* 1035 */               XChoicePeer.this.handleJavaKeyEvent((KeyEvent)paramAWTEvent);
/*      */             }
/*      */           }
/*      */         });
/* 1039 */         return true;
/*      */       }
/* 1041 */       if ((paramAWTEvent instanceof MouseEvent))
/*      */       {
/* 1047 */         MouseEvent localMouseEvent = (MouseEvent)paramAWTEvent;
/* 1048 */         int i = paramAWTEvent.getID();
/*      */ 
/* 1051 */         if ((this.unfurledChoice.isMouseEventInside(localMouseEvent)) || ((!this.firstPress) && (i == 506)))
/*      */         {
/* 1054 */           return handleMouseEventByChoice(localMouseEvent);
/*      */         }
/*      */ 
/* 1058 */         if (i == 503) {
/* 1059 */           return handleMouseEventByChoice(localMouseEvent);
/*      */         }
/*      */ 
/* 1062 */         if ((!this.firstPress) && (!isMouseEventInChoice(localMouseEvent)) && (!this.unfurledChoice.isMouseEventInside(localMouseEvent)) && ((i == 501) || (i == 502) || (i == 500)))
/*      */         {
/* 1069 */           return handleMouseEventByChoice(localMouseEvent);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1074 */     return super.prePostEvent(paramAWTEvent);
/*      */   }
/*      */ 
/*      */   public boolean handleMouseEventByChoice(final MouseEvent paramMouseEvent)
/*      */   {
/* 1080 */     EventQueue.invokeLater(new Runnable() {
/*      */       public void run() {
/* 1082 */         XChoicePeer.this.handleJavaMouseEvent(paramMouseEvent);
/*      */       }
/*      */     });
/* 1085 */     return true;
/*      */   }
/*      */ 
/*      */   private boolean isMouseEventInChoice(MouseEvent paramMouseEvent)
/*      */   {
/* 1093 */     int i = paramMouseEvent.getX();
/* 1094 */     int j = paramMouseEvent.getY();
/* 1095 */     Rectangle localRectangle = getBounds();
/*      */ 
/* 1097 */     if ((i < 0) || (i > localRectangle.width) || (j < 0) || (j > localRectangle.height))
/*      */     {
/* 1100 */       return false;
/*      */     }
/* 1102 */     return true;
/*      */   }
/*      */ 
/*      */   class UnfurledChoice extends XWindow
/*      */   {
/*      */     public UnfurledChoice(Component arg2)
/*      */     {
/*  756 */       super();
/*      */     }
/*      */ 
/*      */     public void preInit(XCreateWindowParams paramXCreateWindowParams)
/*      */     {
/*  764 */       paramXCreateWindowParams.delete("parent window");
/*  765 */       super.preInit(paramXCreateWindowParams);
/*      */ 
/*  767 */       paramXCreateWindowParams.remove("bounds");
/*  768 */       paramXCreateWindowParams.add("overrideRedirect", Boolean.TRUE);
/*      */     }
/*      */ 
/*      */     Rectangle placeOnScreen()
/*      */     {
/*      */       int i;
/*  779 */       if (XChoicePeer.this.helper.isEmpty()) {
/*  780 */         i = 1;
/*      */       }
/*      */       else {
/*  783 */         int j = XChoicePeer.this.helper.getItemCount();
/*  784 */         i = Math.min(10, j);
/*      */       }
/*  786 */       Point localPoint = XChoicePeer.this.toGlobal(0, 0);
/*  787 */       Dimension localDimension = Toolkit.getDefaultToolkit().getScreenSize();
/*      */ 
/*  789 */       if (XChoicePeer.this.alignUnder != null) {
/*  790 */         Rectangle localRectangle1 = XChoicePeer.this.getBounds();
/*  791 */         localRectangle1.setLocation(0, 0);
/*  792 */         localRectangle1 = XChoicePeer.this.toGlobal(localRectangle1);
/*  793 */         Rectangle localRectangle2 = new Rectangle(XChoicePeer.this.alignUnder.getLocationOnScreen(), XChoicePeer.this.alignUnder.getSize());
/*  794 */         Rectangle localRectangle3 = localRectangle1.union(localRectangle2);
/*      */ 
/*  796 */         this.width = localRectangle3.width;
/*  797 */         this.x = localRectangle3.x;
/*  798 */         this.y = (localRectangle3.y + localRectangle3.height);
/*  799 */         this.height = (2 + i * (XChoicePeer.this.helper.getItemHeight() + 2));
/*      */       }
/*      */       else {
/*  802 */         this.x = localPoint.x;
/*  803 */         this.y = (localPoint.y + XChoicePeer.this.height);
/*  804 */         this.width = Math.max(XChoicePeer.this.width, XChoicePeer.this.helper.getMaxItemWidth() + 6 + (XChoicePeer.this.helper.isVSBVisible() ? 15 : 0));
/*      */ 
/*  806 */         this.height = (2 + i * (XChoicePeer.this.helper.getItemHeight() + 2));
/*      */       }
/*      */ 
/*  810 */       if (this.x < 0) {
/*  811 */         this.x = 0;
/*      */       }
/*  813 */       else if (this.x + this.width > localDimension.width) {
/*  814 */         this.x = (localDimension.width - this.width);
/*      */       }
/*      */ 
/*  817 */       if (this.y < 0) {
/*  818 */         this.y = 0;
/*      */       }
/*  820 */       else if (this.y + this.height > localDimension.height) {
/*  821 */         this.y = (localDimension.height - this.height);
/*      */       }
/*  823 */       return new Rectangle(this.x, this.y, this.width, this.height);
/*      */     }
/*      */ 
/*      */     public void toFront()
/*      */     {
/*  828 */       if (XChoicePeer.this.choiceListener != null) {
/*  829 */         XChoicePeer.this.choiceListener.unfurledChoiceOpening(XChoicePeer.this.helper);
/*      */       }
/*  831 */       Rectangle localRectangle = placeOnScreen();
/*  832 */       reshape(localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height);
/*  833 */       super.toFront();
/*  834 */       setVisible(true);
/*      */     }
/*      */ 
/*      */     public void trackMouse(MouseEvent paramMouseEvent)
/*      */     {
/*  845 */       Point localPoint = toLocalCoords(paramMouseEvent);
/*      */ 
/*  850 */       switch (paramMouseEvent.getID())
/*      */       {
/*      */       case 501:
/*  855 */         if (XChoicePeer.this.helper.isInVertSB(getBounds(), localPoint.x, localPoint.y)) {
/*  856 */           XChoicePeer.this.mouseInSB = true;
/*  857 */           XChoicePeer.this.helper.handleVSBEvent(paramMouseEvent, getBounds(), localPoint.x, localPoint.y);
/*      */         }
/*      */         else {
/*  860 */           trackSelection(localPoint.x, localPoint.y);
/*      */         }
/*  862 */         break;
/*      */       case 502:
/*  864 */         if (XChoicePeer.this.mouseInSB) {
/*  865 */           XChoicePeer.this.mouseInSB = false;
/*  866 */           XChoicePeer.this.helper.handleVSBEvent(paramMouseEvent, getBounds(), localPoint.x, localPoint.y);
/*      */         }
/*      */         else {
/*  869 */           XChoicePeer.this.helper.trackMouseReleasedScroll();
/*      */         }
/*      */ 
/*  876 */         break;
/*      */       case 506:
/*  878 */         if (XChoicePeer.this.mouseInSB) {
/*  879 */           XChoicePeer.this.helper.handleVSBEvent(paramMouseEvent, getBounds(), localPoint.x, localPoint.y);
/*      */         }
/*      */         else
/*      */         {
/*  883 */           XChoicePeer.this.helper.trackMouseDraggedScroll(localPoint.x, localPoint.y, this.width, this.height);
/*  884 */           trackSelection(localPoint.x, localPoint.y);
/*      */         }
/*      */         break;
/*      */       }
/*      */     }
/*      */ 
/*      */     private void trackSelection(int paramInt1, int paramInt2) {
/*  891 */       if ((!XChoicePeer.this.helper.isEmpty()) && 
/*  892 */         (paramInt1 > 0) && (paramInt1 < this.width) && (paramInt2 > 0) && (paramInt2 < this.height))
/*      */       {
/*  894 */         int i = XChoicePeer.this.helper.y2index(paramInt2);
/*  895 */         if (XChoicePeer.log.isLoggable(500)) {
/*  896 */           XChoicePeer.log.fine("transX=" + paramInt1 + ", transY=" + paramInt2 + ",width=" + this.width + ", height=" + this.height + ", newIdx=" + i + " on " + this.target);
/*      */         }
/*      */ 
/*  900 */         if ((i >= 0) && (i < XChoicePeer.this.helper.getItemCount()) && (i != XChoicePeer.this.helper.getSelectedIndex()))
/*      */         {
/*  903 */           XChoicePeer.this.helper.select(i);
/*  904 */           XChoicePeer.this.unfurledChoice.repaint();
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     public void paintBackground()
/*      */     {
/*  916 */       Graphics localGraphics = getGraphics();
/*  917 */       localGraphics.setColor(XChoicePeer.this.getPeerBackground());
/*  918 */       localGraphics.fillRect(0, 0, this.width, this.height);
/*      */     }
/*      */ 
/*      */     public void repaint()
/*      */     {
/*  926 */       if (!isVisible()) {
/*  927 */         return;
/*      */       }
/*  929 */       if (XChoicePeer.this.helper.checkVsbVisibilityChangedAndReset()) {
/*  930 */         paintBackground();
/*      */       }
/*  932 */       super.repaint();
/*      */     }
/*      */ 
/*      */     public void paint(Graphics paramGraphics)
/*      */     {
/*  937 */       Choice localChoice = (Choice)this.target;
/*  938 */       Color[] arrayOfColor = XChoicePeer.this.getGUIcolors();
/*  939 */       XChoicePeer.this.draw3DRect(paramGraphics, XComponentPeer.getSystemColors(), 0, 0, this.width - 1, this.height - 1, true);
/*  940 */       XChoicePeer.this.draw3DRect(paramGraphics, XComponentPeer.getSystemColors(), 1, 1, this.width - 3, this.height - 3, true);
/*      */ 
/*  942 */       XChoicePeer.this.helper.paintAllItems(paramGraphics, arrayOfColor, getBounds());
/*      */     }
/*      */ 
/*      */     public void setVisible(boolean paramBoolean)
/*      */     {
/*  948 */       xSetVisible(paramBoolean);
/*      */ 
/*  950 */       if ((!paramBoolean) && (XChoicePeer.this.alignUnder != null))
/*  951 */         XChoicePeer.this.alignUnder.requestFocusInWindow();
/*      */     }
/*      */ 
/*      */     private Point toLocalCoords(MouseEvent paramMouseEvent)
/*      */     {
/*  961 */       Point localPoint = paramMouseEvent.getLocationOnScreen();
/*      */ 
/*  963 */       localPoint.x -= this.x;
/*  964 */       localPoint.y -= this.y;
/*  965 */       return localPoint;
/*      */     }
/*      */ 
/*      */     private boolean isMouseEventInside(MouseEvent paramMouseEvent)
/*      */     {
/*  972 */       Point localPoint = toLocalCoords(paramMouseEvent);
/*  973 */       if ((localPoint.x > 0) && (localPoint.x < this.width) && (localPoint.y > 0) && (localPoint.y < this.height))
/*      */       {
/*  975 */         return true;
/*      */       }
/*  977 */       return false;
/*      */     }
/*      */ 
/*      */     private boolean isMouseInListArea(MouseEvent paramMouseEvent)
/*      */     {
/*  985 */       if (isMouseEventInside(paramMouseEvent)) {
/*  986 */         Point localPoint = toLocalCoords(paramMouseEvent);
/*  987 */         Rectangle localRectangle = getBounds();
/*  988 */         if (!XChoicePeer.this.helper.isInVertSB(localRectangle, localPoint.x, localPoint.y)) {
/*  989 */           return true;
/*      */         }
/*      */       }
/*  992 */       return false;
/*      */     }
/*      */ 
/*      */     public void handleConfigureNotifyEvent(XEvent paramXEvent)
/*      */     {
/*      */     }
/*      */ 
/*      */     public void handleMapNotifyEvent(XEvent paramXEvent)
/*      */     {
/*      */     }
/*      */ 
/*      */     public void handleUnmapNotifyEvent(XEvent paramXEvent)
/*      */     {
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XChoicePeer
 * JD-Core Version:    0.6.2
 */