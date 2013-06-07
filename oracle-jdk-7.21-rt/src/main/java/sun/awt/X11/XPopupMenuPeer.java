/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.awt.AWTEvent;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Event;
/*     */ import java.awt.Font;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Point;
/*     */ import java.awt.PopupMenu;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.peer.PopupMenuPeer;
/*     */ import java.util.Vector;
/*     */ import sun.awt.AWTAccessor;
/*     */ import sun.awt.AWTAccessor.MenuAccessor;
/*     */ import sun.awt.AWTAccessor.MenuComponentAccessor;
/*     */ import sun.awt.AWTAccessor.MenuItemAccessor;
/*     */ import sun.util.logging.PlatformLogger;
/*     */ 
/*     */ public class XPopupMenuPeer extends XMenuWindow
/*     */   implements PopupMenuPeer
/*     */ {
/*  42 */   private static PlatformLogger log = PlatformLogger.getLogger("sun.awt.X11.XBaseMenuWindow");
/*     */   private XComponentPeer componentPeer;
/*     */   private PopupMenu popupMenuTarget;
/*  56 */   private XMenuPeer showingMousePressedSubmenu = null;
/*     */   private static final int CAPTION_MARGIN_TOP = 4;
/*     */   private static final int CAPTION_SEPARATOR_HEIGHT = 6;
/*     */ 
/*     */   XPopupMenuPeer(PopupMenu paramPopupMenu)
/*     */   {
/*  70 */     super(null);
/*  71 */     this.popupMenuTarget = paramPopupMenu;
/*     */   }
/*     */ 
/*     */   public void setFont(Font paramFont)
/*     */   {
/*  83 */     resetMapping();
/*  84 */     setItemsFont(paramFont);
/*  85 */     postPaintEvent();
/*     */   }
/*     */ 
/*     */   public void setLabel(String paramString)
/*     */   {
/*  92 */     resetMapping();
/*  93 */     postPaintEvent();
/*     */   }
/*     */ 
/*     */   public void setEnabled(boolean paramBoolean)
/*     */   {
/*  98 */     postPaintEvent();
/*     */   }
/*     */ 
/*     */   public void enable()
/*     */   {
/* 106 */     setEnabled(true);
/*     */   }
/*     */ 
/*     */   public void disable()
/*     */   {
/* 114 */     setEnabled(false);
/*     */   }
/*     */ 
/*     */   public void addSeparator()
/*     */   {
/* 126 */     if (log.isLoggable(400)) log.finer("addSeparator is not implemented");
/*     */   }
/*     */ 
/*     */   public void show(Event paramEvent)
/*     */   {
/* 133 */     this.target = ((Component)paramEvent.target);
/*     */ 
/* 135 */     Vector localVector = getMenuTargetItems();
/* 136 */     if (localVector != null) {
/* 137 */       reloadItems(localVector);
/*     */ 
/* 139 */       Point localPoint1 = this.target.getLocationOnScreen();
/* 140 */       Point localPoint2 = new Point(localPoint1.x + paramEvent.x, localPoint1.y + paramEvent.y);
/*     */ 
/* 143 */       if (!ensureCreated()) {
/* 144 */         return;
/*     */       }
/* 146 */       Dimension localDimension = getDesiredSize();
/*     */ 
/* 149 */       Rectangle localRectangle = getWindowBounds(localPoint2, localDimension);
/* 150 */       reshape(localRectangle);
/* 151 */       xSetVisible(true);
/* 152 */       toFront();
/* 153 */       selectItem(null, false);
/* 154 */       grabInput();
/*     */     }
/*     */   }
/*     */ 
/*     */   Font getTargetFont()
/*     */   {
/* 166 */     if (this.popupMenuTarget == null) {
/* 167 */       return XWindow.getDefaultFont();
/*     */     }
/* 169 */     return AWTAccessor.getMenuComponentAccessor().getFont_NoClientCode(this.popupMenuTarget);
/*     */   }
/*     */ 
/*     */   String getTargetLabel()
/*     */   {
/* 175 */     if (this.target == null) {
/* 176 */       return "";
/*     */     }
/* 178 */     return AWTAccessor.getMenuItemAccessor().getLabel(this.popupMenuTarget);
/*     */   }
/*     */ 
/*     */   boolean isTargetEnabled()
/*     */   {
/* 183 */     if (this.popupMenuTarget == null) {
/* 184 */       return false;
/*     */     }
/* 186 */     return AWTAccessor.getMenuItemAccessor().isEnabled(this.popupMenuTarget);
/*     */   }
/*     */ 
/*     */   Vector getMenuTargetItems() {
/* 190 */     if (this.popupMenuTarget == null) {
/* 191 */       return null;
/*     */     }
/* 193 */     return AWTAccessor.getMenuAccessor().getItems(this.popupMenuTarget);
/*     */   }
/*     */ 
/*     */   protected Rectangle getWindowBounds(Point paramPoint, Dimension paramDimension)
/*     */   {
/* 215 */     Rectangle localRectangle1 = new Rectangle(paramPoint.x, paramPoint.y, 0, 0);
/* 216 */     Dimension localDimension = Toolkit.getDefaultToolkit().getScreenSize();
/*     */ 
/* 218 */     Rectangle localRectangle2 = fitWindowRight(localRectangle1, paramDimension, localDimension);
/* 219 */     if (localRectangle2 != null) {
/* 220 */       return localRectangle2;
/*     */     }
/* 222 */     localRectangle2 = fitWindowLeft(localRectangle1, paramDimension, localDimension);
/* 223 */     if (localRectangle2 != null) {
/* 224 */       return localRectangle2;
/*     */     }
/* 226 */     localRectangle2 = fitWindowBelow(localRectangle1, paramDimension, localDimension);
/* 227 */     if (localRectangle2 != null) {
/* 228 */       return localRectangle2;
/*     */     }
/* 230 */     localRectangle2 = fitWindowAbove(localRectangle1, paramDimension, localDimension);
/* 231 */     if (localRectangle2 != null) {
/* 232 */       return localRectangle2;
/*     */     }
/* 234 */     return fitWindowToScreen(paramDimension, localDimension);
/*     */   }
/*     */ 
/*     */   protected Dimension getCaptionSize()
/*     */   {
/* 248 */     String str1 = getTargetLabel();
/* 249 */     if (str1.equals("")) {
/* 250 */       return null;
/*     */     }
/* 252 */     Graphics localGraphics = getGraphics();
/* 253 */     if (localGraphics == null)
/* 254 */       return null;
/*     */     try
/*     */     {
/* 257 */       localGraphics.setFont(getTargetFont());
/* 258 */       FontMetrics localFontMetrics = localGraphics.getFontMetrics();
/* 259 */       String str2 = getTargetLabel();
/* 260 */       int i = localFontMetrics.stringWidth(str2);
/* 261 */       int j = 4 + localFontMetrics.getHeight() + 6;
/* 262 */       Dimension localDimension1 = new Dimension(i, j);
/* 263 */       return localDimension1;
/*     */     } finally {
/* 265 */       localGraphics.dispose();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void paintCaption(Graphics paramGraphics, Rectangle paramRectangle)
/*     */   {
/* 275 */     String str1 = getTargetLabel();
/* 276 */     if (str1.equals("")) {
/* 277 */       return;
/*     */     }
/* 279 */     paramGraphics.setFont(getTargetFont());
/* 280 */     FontMetrics localFontMetrics = paramGraphics.getFontMetrics();
/* 281 */     String str2 = getTargetLabel();
/* 282 */     int i = localFontMetrics.stringWidth(str2);
/* 283 */     int j = paramRectangle.x + (paramRectangle.width - i) / 2;
/* 284 */     int k = paramRectangle.y + 4 + localFontMetrics.getAscent();
/* 285 */     int m = paramRectangle.y + paramRectangle.height - 3;
/* 286 */     paramGraphics.setColor(isTargetEnabled() ? getForegroundColor() : getDisabledColor());
/* 287 */     paramGraphics.drawString(str1, j, k);
/* 288 */     draw3DRect(paramGraphics, paramRectangle.x, m, paramRectangle.width, 2, false);
/*     */   }
/*     */ 
/*     */   protected void doDispose()
/*     */   {
/* 297 */     super.doDispose();
/* 298 */     XToolkit.targetDisposedPeer(this.popupMenuTarget, this);
/*     */   }
/*     */ 
/*     */   protected void handleEvent(AWTEvent paramAWTEvent) {
/* 302 */     switch (paramAWTEvent.getID()) {
/*     */     case 500:
/*     */     case 501:
/*     */     case 502:
/*     */     case 503:
/*     */     case 504:
/*     */     case 505:
/*     */     case 506:
/* 310 */       doHandleJavaMouseEvent((MouseEvent)paramAWTEvent);
/* 311 */       break;
/*     */     case 401:
/*     */     case 402:
/* 314 */       doHandleJavaKeyEvent((KeyEvent)paramAWTEvent);
/* 315 */       break;
/*     */     default:
/* 317 */       super.handleEvent(paramAWTEvent);
/*     */     }
/*     */   }
/*     */ 
/*     */   void ungrabInputImpl()
/*     */   {
/* 328 */     hide();
/*     */   }
/*     */ 
/*     */   public void handleKeyPress(XEvent paramXEvent)
/*     */   {
/* 343 */     XKeyEvent localXKeyEvent = paramXEvent.get_xkey();
/* 344 */     if (log.isLoggable(500)) {
/* 345 */       log.fine(localXKeyEvent.toString());
/*     */     }
/* 347 */     if (isEventDisabled(paramXEvent)) {
/* 348 */       return;
/*     */     }
/* 350 */     Component localComponent = getEventSource();
/* 351 */     handleKeyPress(localXKeyEvent);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XPopupMenuPeer
 * JD-Core Version:    0.6.2
 */