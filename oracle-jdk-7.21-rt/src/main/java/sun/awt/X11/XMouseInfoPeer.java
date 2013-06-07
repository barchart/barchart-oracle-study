/*    */ package sun.awt.X11;
/*    */ 
/*    */ import java.awt.GraphicsDevice;
/*    */ import java.awt.GraphicsEnvironment;
/*    */ import java.awt.Point;
/*    */ import java.awt.Window;
/*    */ import java.awt.peer.MouseInfoPeer;
/*    */ 
/*    */ public class XMouseInfoPeer
/*    */   implements MouseInfoPeer
/*    */ {
/*    */   public int fillPointWithCoords(Point paramPoint)
/*    */   {
/* 44 */     long l1 = XToolkit.getDisplay();
/* 45 */     GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
/*    */ 
/* 47 */     GraphicsDevice[] arrayOfGraphicsDevice = localGraphicsEnvironment.getScreenDevices();
/* 48 */     int i = arrayOfGraphicsDevice.length;
/*    */ 
/* 50 */     XToolkit.awtLock();
/*    */     try {
/* 52 */       for (int j = 0; j < i; j++) {
/* 53 */         long l2 = XlibWrapper.RootWindow(l1, j);
/* 54 */         boolean bool = XlibWrapper.XQueryPointer(l1, l2, XlibWrapper.larg1, XlibWrapper.larg2, XlibWrapper.larg3, XlibWrapper.larg4, XlibWrapper.larg5, XlibWrapper.larg6, XlibWrapper.larg7);
/*    */ 
/* 63 */         if (bool) {
/* 64 */           paramPoint.x = Native.getInt(XlibWrapper.larg3);
/* 65 */           paramPoint.y = Native.getInt(XlibWrapper.larg4);
/* 66 */           return j;
/*    */         }
/*    */       }
/*    */     } finally {
/* 70 */       XToolkit.awtUnlock();
/*    */     }
/*    */ 
/* 74 */     if (!$assertionsDisabled) throw new AssertionError("No pointer found in the system.");
/* 75 */     return 0;
/*    */   }
/*    */ 
/*    */   public boolean isWindowUnderMouse(Window paramWindow)
/*    */   {
/* 80 */     long l1 = XToolkit.getDisplay();
/*    */ 
/* 86 */     long l2 = ((XWindow)paramWindow.getPeer()).getContentWindow();
/* 87 */     long l3 = XlibUtil.getParentWindow(l2);
/*    */ 
/* 89 */     XToolkit.awtLock();
/*    */     try
/*    */     {
/* 92 */       boolean bool1 = XlibWrapper.XQueryPointer(l1, l3, XlibWrapper.larg1, XlibWrapper.larg8, XlibWrapper.larg3, XlibWrapper.larg4, XlibWrapper.larg5, XlibWrapper.larg6, XlibWrapper.larg7);
/*    */ 
/* 100 */       long l4 = Native.getWindow(XlibWrapper.larg8);
/* 101 */       return (l4 == l2) && (bool1);
/*    */     }
/*    */     finally
/*    */     {
/* 105 */       XToolkit.awtUnlock();
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XMouseInfoPeer
 * JD-Core Version:    0.6.2
 */