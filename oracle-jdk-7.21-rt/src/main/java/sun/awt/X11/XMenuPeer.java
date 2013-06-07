/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.awt.Font;
/*     */ import java.awt.Menu;
/*     */ import java.awt.MenuItem;
/*     */ import java.awt.peer.MenuPeer;
/*     */ import java.util.Vector;
/*     */ import sun.awt.AWTAccessor;
/*     */ import sun.awt.AWTAccessor.MenuAccessor;
/*     */ import sun.util.logging.PlatformLogger;
/*     */ 
/*     */ public class XMenuPeer extends XMenuItemPeer
/*     */   implements MenuPeer
/*     */ {
/*  41 */   private static PlatformLogger log = PlatformLogger.getLogger("sun.awt.X11.XMenuPeer");
/*     */   XMenuWindow menuWindow;
/*     */ 
/*     */   XMenuPeer(Menu paramMenu)
/*     */   {
/*  54 */     super(paramMenu);
/*     */   }
/*     */ 
/*     */   void setContainer(XBaseMenuWindow paramXBaseMenuWindow)
/*     */   {
/*  63 */     super.setContainer(paramXBaseMenuWindow);
/*  64 */     this.menuWindow = new XMenuWindow(this);
/*     */   }
/*     */ 
/*     */   public void dispose()
/*     */   {
/*  82 */     if (this.menuWindow != null) {
/*  83 */       this.menuWindow.dispose();
/*     */     }
/*  85 */     super.dispose();
/*     */   }
/*     */ 
/*     */   public void setFont(Font paramFont)
/*     */   {
/*  95 */     resetTextMetrics();
/*     */ 
/*  97 */     XMenuWindow localXMenuWindow = getMenuWindow();
/*  98 */     if (localXMenuWindow != null) {
/*  99 */       localXMenuWindow.setItemsFont(paramFont);
/*     */     }
/*     */ 
/* 102 */     repaintIfShowing();
/*     */   }
/*     */ 
/*     */   public void addSeparator()
/*     */   {
/* 114 */     if (log.isLoggable(400)) log.finer("addSeparator is not implemented"); 
/*     */   }
/*     */ 
/*     */   public void addItem(MenuItem paramMenuItem)
/*     */   {
/* 118 */     XMenuWindow localXMenuWindow = getMenuWindow();
/* 119 */     if (localXMenuWindow != null) {
/* 120 */       localXMenuWindow.addItem(paramMenuItem);
/*     */     }
/* 122 */     else if (log.isLoggable(500))
/* 123 */       log.fine("Attempt to use XMenuWindowPeer without window");
/*     */   }
/*     */ 
/*     */   public void delItem(int paramInt)
/*     */   {
/* 129 */     XMenuWindow localXMenuWindow = getMenuWindow();
/* 130 */     if (localXMenuWindow != null) {
/* 131 */       localXMenuWindow.delItem(paramInt);
/*     */     }
/* 133 */     else if (log.isLoggable(500))
/* 134 */       log.fine("Attempt to use XMenuWindowPeer without window");
/*     */   }
/*     */ 
/*     */   Vector getTargetItems()
/*     */   {
/* 145 */     return AWTAccessor.getMenuAccessor().getItems((Menu)getTarget());
/*     */   }
/*     */ 
/*     */   boolean isSeparator()
/*     */   {
/* 154 */     return false;
/*     */   }
/*     */ 
/*     */   String getShortcutText()
/*     */   {
/* 160 */     return null;
/*     */   }
/*     */ 
/*     */   XMenuWindow getMenuWindow()
/*     */   {
/* 175 */     return this.menuWindow;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XMenuPeer
 * JD-Core Version:    0.6.2
 */