/*    */ package sun.awt.motif;
/*    */ 
/*    */ import java.awt.Image;
/*    */ 
/*    */ class MWindowAttributes
/*    */ {
/* 29 */   static int NORMAL = 0;
/* 30 */   static int ICONIC = 1;
/* 31 */   static int MAXIMIZED = 2;
/*    */ 
/* 33 */   static int AWT_DECOR_NONE = 0;
/* 34 */   static int AWT_DECOR_ALL = 1;
/* 35 */   static int AWT_DECOR_BORDER = 2;
/* 36 */   static int AWT_DECOR_RESIZEH = 4;
/* 37 */   static int AWT_DECOR_TITLE = 8;
/* 38 */   static int AWT_DECOR_MENU = 16;
/* 39 */   static int AWT_DECOR_MINIMIZE = 32;
/* 40 */   static int AWT_DECOR_MAXIMIZE = 64;
/* 41 */   static int AWT_UNOBSCURED = 0;
/* 42 */   static int AWT_PARTIALLY_OBSCURED = 1;
/* 43 */   static int AWT_FULLY_OBSCURED = 2;
/* 44 */   static int AWT_UNKNOWN_OBSCURITY = 3;
/*    */   boolean nativeDecor;
/*    */   boolean initialFocus;
/*    */   boolean isResizable;
/*    */   int initialState;
/*    */   int visibilityState;
/*    */   String title;
/*    */   Image icon;
/*    */   int decorations;
/*    */ 
/*    */   private static native void initIDs();
/*    */ 
/*    */   MWindowAttributes()
/*    */   {
/* 63 */     this.nativeDecor = false;
/* 64 */     this.initialFocus = false;
/* 65 */     this.isResizable = false;
/* 66 */     this.initialState = NORMAL;
/* 67 */     this.visibilityState = AWT_UNKNOWN_OBSCURITY;
/* 68 */     this.title = null;
/* 69 */     this.icon = null;
/* 70 */     this.decorations = 0;
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/* 59 */     initIDs();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.motif.MWindowAttributes
 * JD-Core Version:    0.6.2
 */