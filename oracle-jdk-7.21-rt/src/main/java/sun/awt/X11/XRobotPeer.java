/*    */ package sun.awt.X11;
/*    */ 
/*    */ import java.awt.GraphicsConfiguration;
/*    */ import java.awt.Rectangle;
/*    */ import java.awt.Toolkit;
/*    */ import java.awt.peer.RobotPeer;
/*    */ import sun.awt.AWTAccessor;
/*    */ import sun.awt.AWTAccessor.InputEventAccessor;
/*    */ import sun.awt.SunToolkit;
/*    */ import sun.awt.X11GraphicsConfig;
/*    */ 
/*    */ class XRobotPeer
/*    */   implements RobotPeer
/*    */ {
/* 37 */   private X11GraphicsConfig xgc = null;
/*    */ 
/* 42 */   static Object robotLock = new Object();
/*    */ 
/*    */   XRobotPeer(GraphicsConfiguration paramGraphicsConfiguration) {
/* 45 */     this.xgc = ((X11GraphicsConfig)paramGraphicsConfiguration);
/* 46 */     SunToolkit localSunToolkit = (SunToolkit)Toolkit.getDefaultToolkit();
/* 47 */     setup(localSunToolkit.getNumberOfButtons(), AWTAccessor.getInputEventAccessor().getButtonDownMasks());
/*    */   }
/*    */ 
/*    */   public void dispose()
/*    */   {
/*    */   }
/*    */ 
/*    */   public void mouseMove(int paramInt1, int paramInt2) {
/* 55 */     mouseMoveImpl(this.xgc, paramInt1, paramInt2);
/*    */   }
/*    */ 
/*    */   public void mousePress(int paramInt) {
/* 59 */     mousePressImpl(paramInt);
/*    */   }
/*    */ 
/*    */   public void mouseRelease(int paramInt) {
/* 63 */     mouseReleaseImpl(paramInt);
/*    */   }
/*    */ 
/*    */   public void mouseWheel(int paramInt) {
/* 67 */     mouseWheelImpl(paramInt);
/*    */   }
/*    */ 
/*    */   public void keyPress(int paramInt) {
/* 71 */     keyPressImpl(paramInt);
/*    */   }
/*    */ 
/*    */   public void keyRelease(int paramInt) {
/* 75 */     keyReleaseImpl(paramInt);
/*    */   }
/*    */ 
/*    */   public int getRGBPixel(int paramInt1, int paramInt2) {
/* 79 */     int[] arrayOfInt = new int[1];
/* 80 */     getRGBPixelsImpl(this.xgc, paramInt1, paramInt2, 1, 1, arrayOfInt);
/* 81 */     return arrayOfInt[0];
/*    */   }
/*    */ 
/*    */   public int[] getRGBPixels(Rectangle paramRectangle) {
/* 85 */     int[] arrayOfInt = new int[paramRectangle.width * paramRectangle.height];
/* 86 */     getRGBPixelsImpl(this.xgc, paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height, arrayOfInt);
/* 87 */     return arrayOfInt;
/*    */   }
/*    */ 
/*    */   private static synchronized native void setup(int paramInt, int[] paramArrayOfInt);
/*    */ 
/*    */   private static synchronized native void mouseMoveImpl(X11GraphicsConfig paramX11GraphicsConfig, int paramInt1, int paramInt2);
/*    */ 
/*    */   private static synchronized native void mousePressImpl(int paramInt);
/*    */ 
/*    */   private static synchronized native void mouseReleaseImpl(int paramInt);
/*    */ 
/*    */   private static synchronized native void mouseWheelImpl(int paramInt);
/*    */ 
/*    */   private static synchronized native void keyPressImpl(int paramInt);
/*    */ 
/*    */   private static synchronized native void keyReleaseImpl(int paramInt);
/*    */ 
/*    */   private static synchronized native void getRGBPixelsImpl(X11GraphicsConfig paramX11GraphicsConfig, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt);
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XRobotPeer
 * JD-Core Version:    0.6.2
 */