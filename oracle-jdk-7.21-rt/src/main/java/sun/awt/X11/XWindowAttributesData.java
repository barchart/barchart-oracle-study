/*    */ package sun.awt.X11;
/*    */ 
/*    */ import java.util.List;
/*    */ 
/*    */ class XWindowAttributesData
/*    */ {
/* 30 */   static int NORMAL = 0;
/* 31 */   static int ICONIC = 1;
/* 32 */   static int MAXIMIZED = 2;
/*    */ 
/* 34 */   static int AWT_DECOR_NONE = 0;
/* 35 */   static int AWT_DECOR_ALL = 1;
/* 36 */   static int AWT_DECOR_BORDER = 2;
/* 37 */   static int AWT_DECOR_RESIZEH = 4;
/* 38 */   static int AWT_DECOR_TITLE = 8;
/* 39 */   static int AWT_DECOR_MENU = 16;
/* 40 */   static int AWT_DECOR_MINIMIZE = 32;
/* 41 */   static int AWT_DECOR_MAXIMIZE = 64;
/* 42 */   static int AWT_UNOBSCURED = 0;
/* 43 */   static int AWT_PARTIALLY_OBSCURED = 1;
/* 44 */   static int AWT_FULLY_OBSCURED = 2;
/* 45 */   static int AWT_UNKNOWN_OBSCURITY = 3;
/*    */   boolean nativeDecor;
/*    */   boolean initialFocus;
/*    */   boolean isResizable;
/*    */   int initialState;
/*    */   boolean initialResizability;
/*    */   int visibilityState;
/*    */   String title;
/*    */   List<XIconInfo> icons;
/*    */   boolean iconsInherited;
/*    */   int decorations;
/*    */   int functions;
/*    */ 
/*    */   XWindowAttributesData()
/*    */   {
/* 61 */     this.nativeDecor = false;
/* 62 */     this.initialFocus = false;
/* 63 */     this.isResizable = false;
/* 64 */     this.initialState = NORMAL;
/* 65 */     this.visibilityState = AWT_UNKNOWN_OBSCURITY;
/* 66 */     this.title = null;
/* 67 */     this.icons = null;
/* 68 */     this.iconsInherited = true;
/* 69 */     this.decorations = 0;
/* 70 */     this.functions = 0;
/* 71 */     this.initialResizability = true;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XWindowAttributesData
 * JD-Core Version:    0.6.2
 */