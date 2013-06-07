/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.MenuItem;
/*     */ import java.awt.MenuShortcut;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.peer.MenuItemPeer;
/*     */ import sun.awt.AWTAccessor;
/*     */ import sun.awt.AWTAccessor.MenuComponentAccessor;
/*     */ import sun.awt.AWTAccessor.MenuItemAccessor;
/*     */ 
/*     */ public class XMenuItemPeer
/*     */   implements MenuItemPeer
/*     */ {
/*     */   private XBaseMenuWindow container;
/*     */   private MenuItem target;
/*     */   private Rectangle bounds;
/*     */   private Point textOrigin;
/*     */   private static final int SEPARATOR_WIDTH = 20;
/*     */   private static final int SEPARATOR_HEIGHT = 5;
/*     */   private TextMetrics textMetrics;
/*     */ 
/*     */   XMenuItemPeer(MenuItem paramMenuItem)
/*     */   {
/* 136 */     this.target = paramMenuItem;
/*     */   }
/*     */ 
/*     */   public void dispose()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setFont(Font paramFont)
/*     */   {
/* 153 */     resetTextMetrics();
/* 154 */     repaintIfShowing();
/*     */   }
/*     */ 
/*     */   public void setLabel(String paramString)
/*     */   {
/* 160 */     resetTextMetrics();
/* 161 */     repaintIfShowing();
/*     */   }
/*     */ 
/*     */   public void setEnabled(boolean paramBoolean) {
/* 165 */     repaintIfShowing();
/*     */   }
/*     */ 
/*     */   public void enable()
/*     */   {
/* 173 */     setEnabled(true);
/*     */   }
/*     */ 
/*     */   public void disable()
/*     */   {
/* 181 */     setEnabled(false);
/*     */   }
/*     */ 
/*     */   MenuItem getTarget()
/*     */   {
/* 191 */     return this.target;
/*     */   }
/*     */ 
/*     */   Font getTargetFont() {
/* 195 */     if (this.target == null) {
/* 196 */       return XWindow.getDefaultFont();
/*     */     }
/* 198 */     return AWTAccessor.getMenuComponentAccessor().getFont_NoClientCode(this.target);
/*     */   }
/*     */ 
/*     */   String getTargetLabel() {
/* 202 */     if (this.target == null) {
/* 203 */       return "";
/*     */     }
/* 205 */     String str = AWTAccessor.getMenuItemAccessor().getLabel(this.target);
/* 206 */     return str == null ? "" : str;
/*     */   }
/*     */ 
/*     */   boolean isTargetEnabled() {
/* 210 */     if (this.target == null) {
/* 211 */       return false;
/*     */     }
/* 213 */     return AWTAccessor.getMenuItemAccessor().isEnabled(this.target);
/*     */   }
/*     */ 
/*     */   boolean isTargetItemEnabled()
/*     */   {
/* 222 */     if (this.target == null) {
/* 223 */       return false;
/*     */     }
/* 225 */     return AWTAccessor.getMenuItemAccessor().isItemEnabled(this.target);
/*     */   }
/*     */ 
/*     */   String getTargetActionCommand() {
/* 229 */     if (this.target == null) {
/* 230 */       return "";
/*     */     }
/* 232 */     return AWTAccessor.getMenuItemAccessor().getActionCommandImpl(this.target);
/*     */   }
/*     */ 
/*     */   MenuShortcut getTargetShortcut() {
/* 236 */     if (this.target == null) {
/* 237 */       return null;
/*     */     }
/* 239 */     return AWTAccessor.getMenuItemAccessor().getShortcut(this.target);
/*     */   }
/*     */ 
/*     */   String getShortcutText()
/*     */   {
/* 244 */     if (this.container == null) {
/* 245 */       return null;
/*     */     }
/* 247 */     if ((this.container.getRootMenuWindow() instanceof XPopupMenuPeer)) {
/* 248 */       return null;
/*     */     }
/* 250 */     MenuShortcut localMenuShortcut = getTargetShortcut();
/*     */ 
/* 252 */     return localMenuShortcut == null ? null : localMenuShortcut.toString();
/*     */   }
/*     */ 
/*     */   void setContainer(XBaseMenuWindow paramXBaseMenuWindow)
/*     */   {
/* 269 */     synchronized (XBaseMenuWindow.getMenuTreeLock()) {
/* 270 */       this.container = paramXBaseMenuWindow;
/*     */     }
/*     */   }
/*     */ 
/*     */   XBaseMenuWindow getContainer()
/*     */   {
/* 278 */     return this.container;
/*     */   }
/*     */ 
/*     */   boolean isSeparator()
/*     */   {
/* 292 */     boolean bool = getTargetLabel().equals("-");
/* 293 */     return bool;
/*     */   }
/*     */ 
/*     */   boolean isContainerShowing()
/*     */   {
/* 306 */     if (this.container == null) {
/* 307 */       return false;
/*     */     }
/* 309 */     return this.container.isShowing();
/*     */   }
/*     */ 
/*     */   void repaintIfShowing()
/*     */   {
/* 316 */     if (isContainerShowing())
/* 317 */       this.container.postPaintEvent();
/*     */   }
/*     */ 
/*     */   void action(long paramLong)
/*     */   {
/* 327 */     if ((!isSeparator()) && (isTargetItemEnabled()))
/* 328 */       XWindow.postEventStatic(new ActionEvent(this.target, 1001, getTargetActionCommand(), paramLong, 0));
/*     */   }
/*     */ 
/*     */   TextMetrics getTextMetrics()
/*     */   {
/* 346 */     TextMetrics localTextMetrics = this.textMetrics;
/* 347 */     if (localTextMetrics == null) {
/* 348 */       localTextMetrics = calcTextMetrics();
/* 349 */       this.textMetrics = localTextMetrics;
/*     */     }
/* 351 */     return localTextMetrics;
/*     */   }
/*     */ 
/*     */   TextMetrics calcTextMetrics()
/*     */   {
/* 397 */     if (this.container == null) {
/* 398 */       return null;
/*     */     }
/* 400 */     if (isSeparator()) {
/* 401 */       return new TextMetrics(new Dimension(20, 5), 0, 0);
/*     */     }
/* 403 */     Graphics localGraphics = this.container.getGraphics();
/* 404 */     if (localGraphics == null)
/* 405 */       return null;
/*     */     try
/*     */     {
/* 408 */       localGraphics.setFont(getTargetFont());
/* 409 */       FontMetrics localFontMetrics = localGraphics.getFontMetrics();
/* 410 */       String str1 = getTargetLabel();
/* 411 */       int i = localFontMetrics.stringWidth(str1);
/* 412 */       int j = localFontMetrics.getHeight();
/* 413 */       Dimension localDimension = new Dimension(i, j);
/* 414 */       int k = localFontMetrics.getHeight() - localFontMetrics.getAscent();
/* 415 */       String str2 = getShortcutText();
/* 416 */       int m = str2 == null ? 0 : localFontMetrics.stringWidth(str2);
/* 417 */       return new TextMetrics(localDimension, m, k);
/*     */     } finally {
/* 419 */       localGraphics.dispose();
/*     */     }
/*     */   }
/*     */ 
/*     */   void resetTextMetrics() {
/* 424 */     this.textMetrics = null;
/* 425 */     if (this.container != null)
/* 426 */       this.container.updateSize();
/*     */   }
/*     */ 
/*     */   void map(Rectangle paramRectangle, Point paramPoint)
/*     */   {
/* 443 */     this.bounds = paramRectangle;
/* 444 */     this.textOrigin = paramPoint;
/*     */   }
/*     */ 
/*     */   Rectangle getBounds()
/*     */   {
/* 451 */     return this.bounds;
/*     */   }
/*     */ 
/*     */   Point getTextOrigin()
/*     */   {
/* 458 */     return this.textOrigin;
/*     */   }
/*     */ 
/*     */   static class TextMetrics
/*     */     implements Cloneable
/*     */   {
/*     */     private Dimension textDimension;
/*     */     private int shortcutWidth;
/*     */     private int textBaseline;
/*     */ 
/*     */     TextMetrics(Dimension paramDimension, int paramInt1, int paramInt2)
/*     */     {
/* 104 */       this.textDimension = paramDimension;
/* 105 */       this.shortcutWidth = paramInt1;
/* 106 */       this.textBaseline = paramInt2;
/*     */     }
/*     */ 
/*     */     public Object clone() {
/*     */       try {
/* 111 */         return super.clone(); } catch (CloneNotSupportedException localCloneNotSupportedException) {
/*     */       }
/* 113 */       throw new InternalError();
/*     */     }
/*     */ 
/*     */     Dimension getTextDimension()
/*     */     {
/* 118 */       return this.textDimension;
/*     */     }
/*     */ 
/*     */     int getShortcutWidth() {
/* 122 */       return this.shortcutWidth;
/*     */     }
/*     */ 
/*     */     int getTextBaseline() {
/* 126 */       return this.textBaseline;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XMenuItemPeer
 * JD-Core Version:    0.6.2
 */